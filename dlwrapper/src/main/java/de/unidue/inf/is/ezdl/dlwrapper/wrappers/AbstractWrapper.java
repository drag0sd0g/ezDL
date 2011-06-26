/*
 * Copyright 2009-2011 Universität Duisburg-Essen, Working Group
 * "Information Engineering"
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.unidue.inf.is.ezdl.dlwrapper.wrappers;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;

import de.unidue.inf.is.ezdl.dlbackend.agent.Agent;
import de.unidue.inf.is.ezdl.dlbackend.data.StoredDocument;
import de.unidue.inf.is.ezdl.dlbackend.data.StoredDocumentList;
import de.unidue.inf.is.ezdl.dlbackend.data.StoredDocumentList.StoredDocumentFilter;
import de.unidue.inf.is.ezdl.dlbackend.wrappers.SourceID;
import de.unidue.inf.is.ezdl.dlbackend.wrappers.SourceInfo;
import de.unidue.inf.is.ezdl.dlcore.cache.Cache;
import de.unidue.inf.is.ezdl.dlcore.data.query.DocumentQuery;
import de.unidue.inf.is.ezdl.dlcore.data.query.Query;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeBool;
import de.unidue.inf.is.ezdl.dlcore.data.wrappers.WrapperInfo;
import de.unidue.inf.is.ezdl.dlcore.utils.IOUtils;
import de.unidue.inf.is.ezdl.dlcore.utils.PropertiesUtils;
import de.unidue.inf.is.ezdl.dlwrapper.Wrapper;



/**
 * An abstract wrapper implementation that has the general logic of how wrappers
 * process queries but nothing more.
 */
public abstract class AbstractWrapper implements Wrapper {

    /**
     * The maximum number of parser errors before the wrapper halts itself.
     */
    private static final int MAX_PARSER_ERRORS = 5;

    /**
     * The default value for proposed minimum timeout for the wrapper in seconds
     * in case this value is not given in the properties.
     * 
     * @see WrapperInfo#setProposedMinimumTimeoutSec(int)
     */
    private static final int DEFAULT_TIMEOUT_SEC = 20;

    /**
     * The logger.
     */
    private Logger logger = Logger.getLogger(Wrapper.class);


    /**
     * Defines possible modes how a concrete wrapper operates.
     */
    protected static enum WrapperMode {
        /**
         * WHOLE_QUERY means that the wrapper processes the query in one piece
         * (e.g. by transforming it to a Lucene query and running it against a
         * Lucene index). This kind of wrappers has to implement
         * {@link AbstractWrapper#processQuery(MetadataQuery)} with real logic.
         */
        WHOLE_QUERY,
        /**
         * CONJUNCTIONS means that the wrapper processes each conjunction of the
         * query (transformed to disjunctive normal form) individually. Wrappers
         * of this kind have to implement
         * {@link AbstractWrapper#processConjunction(Conjunction)} with real
         * logic.
         */
        CONJUNCTIONS
    }


    /**
     * Wrapper implementations can throw this exception to signal that the
     * wrapper and its agents have to be halted.
     * 
     * @author mj
     */
    public static class WrapperEmergencyException extends RuntimeException {

        private static final long serialVersionUID = -8272975183990747843L;

    }


    /**
     * Wrapper implementations can throw this exception to signal that the
     * wrapper encountered a temporary problem and an empty result should be
     * returned.
     * 
     * @author mj
     */
    public static class WrapperTemporaryException extends RuntimeException {

        private static final long serialVersionUID = 4914886620474886406L;

    }


    /**
     * Calling agent instance.
     */
    private Agent agent;
    /**
     * Cache for results.
     */
    private Cache cache;
    /**
     * If true, signals that the wrapper should abort the ongoing process.
     */
    private boolean cancelProcessing;
    /**
     * The number of parser errors encountered. If this count exceeds
     * {@link #MAX_PARSER_ERRORS}, the wrapper shuts itself down.
     */
    private int parserErrorCount = 0;
    /**
     * Net connection
     */
    private Net net;


    /**
     * Constructor.
     */
    public AbstractWrapper() {
        super();
        this.net = new Internet();
    }


    /**
     * Constructor.
     */
    public AbstractWrapper(Net net) {
        super();
        this.net = net;
    }


    @Override
    public void init(Agent agent, Cache cache) {
        this.agent = agent;
        this.cache = cache;
    }


    protected final String wrapperName() {
        return agent.agentName().toLowerCase();
    }


    /**
     * {@inheritDoc}
     * <p>
     * This default implementation reads the wrapper information from the
     * properties object.
     * <p>
     * The properties keys are as follows:
     * </p>
     * <ul>
     * <li>info.remotename - the name of the remote resource that is wrapped -
     * e.g. "ACM DL"</li>
     * <li>info.category - the ID of the category</li>
     * <li>info.category.[locale] - the category name translated for the given
     * locale - e.g. "de"</li>
     * <li>info.description.[locale] - the description translated for the given
     * locale - e.g. "en"</li>
     * <li>info.minimumTimeoutS - the proposed minimum timeout in seconds that
     * users should allow this wrapper to return results. If not given,
     * {@link #DEFAULT_TIMEOUT_SEC} is assumed.</li>
     * </ul>
     * 
     * @return a WrapperInfo object or null if not enough data was found to
     *         initialize it
     */
    @Override
    public WrapperInfo getWrapperInfo() {
        final Properties props = agent.getProperties();

        WrapperInfo wrapperInfo = new WrapperInfo();

        wrapperInfo.setId(getSourceID().getDL());
        wrapperInfo.setCategoryId(props.getProperty("info.category"));
        wrapperInfo.setRemoteName(props.getProperty("info.remotename"));
        wrapperInfo.setSmallIconData(readIcon("16"));
        wrapperInfo.setLargeIconData(readIcon("22"));

        Map<Locale, String> categoryTranslations = readPrefixMap("info.category.");
        wrapperInfo.setCategory(categoryTranslations);

        Map<Locale, String> descriptionTranslations = readPrefixMap("info.description.");
        wrapperInfo.setDescription(descriptionTranslations);

        int timeoutSInt = PropertiesUtils.getIntProperty(props, "info.minimumTimeoutS", DEFAULT_TIMEOUT_SEC);
        wrapperInfo.setProposedMinimumTimeoutSec(timeoutSInt);

        if (wrapperInfo.isValid()) {
            return wrapperInfo;
        }
        else {
            return null;
        }
    }


    private byte[] readIcon(String suffix) {
        byte[] data = null;
        String name = "/icons/" + wrapperName() + "_icon_" + suffix + ".png";
        getLogger().debug("Reading icon " + name);
        URL iconUrl = AbstractWrapper.class.getResource(name);
        if (iconUrl != null) {
            URI uri = null;
            try {
                InputStream bs = AbstractWrapper.class.getResourceAsStream(name);
                data = IOUtils.readBinary(bs);
                getLogger().debug("Image data is " + data.length + " bytes");
            }
            catch (IOException e) {
                getLogger().error("Error reading resource " + name, e);
            }
            catch (Exception e) {
                getLogger().error("Unexpected exception caught: trying to read " + name + " from " + iconUrl + //
                                " (" + uri + ") resulted in " + e.getMessage(), e);
            }
        }
        return data;
    }


    private Map<Locale, String> readPrefixMap(String prefix) {
        Map<Locale, String> map = new HashMap<Locale, String>();
        Properties p = agent.getProperties();
        final Set<String> propNames = p.stringPropertyNames();
        for (String s : propNames) {
            if (s.startsWith(prefix)) {
                String localeId = s.substring(prefix.length());
                map.put(new Locale(localeId), p.getProperty(s));
            }
        }
        return map;
    }


    /**
     * Returns the details info that this wrapper previously stored in the
     * document object.
     * 
     * @param document
     *            the document to get detail info from
     * @return the detail information or null if the wrapper has not stored any
     *         detail information in the document
     */
    protected final String getDetailInfo(StoredDocument document) {
        SourceInfo ownSI = getOwnSourceInfo(document);
        if (ownSI != null) {
            return ownSI.getDetailsInfo();
        }
        return null;
    }


    /**
     * Returns the source info that this wrapper added earlier to the given
     * document.
     * 
     * @param document
     *            the document to find the source info in
     * @return the source info object or null if no suitable source info can be
     *         found
     */
    private SourceInfo getOwnSourceInfo(StoredDocument document) {
        SourceID ownId = getSourceID();
        for (SourceInfo source : document.getSources()) {
            if ((source != null) && (source.getSourceID().equals(ownId))) {
                return source;
            }
        }
        return null;
    }


    /**
     * Sets this wrapper's detail time stamp of the given {@link StoredDocument}
     * to "now".
     * 
     * @param stored
     *            the object to update
     */
    protected void setDetailTimestampToCurrent(StoredDocument stored) {
        SourceInfo si = getOwnSourceInfo(stored);
        if (si != null) {
            si.setDetailTimestamp(new Date());
        }
    }


    /**
     * The mode in which the wrapper operates. The default is
     * {@link WrapperMode#WHOLE_QUERY}.
     * 
     * @return the operation mode of the wrapper
     */
    protected WrapperMode getWrapperMode() {
        return WrapperMode.WHOLE_QUERY;
    }


    /**
     * {@inheritDoc}
     * <p>
     * This method transforms the document query to DNF and processes the query
     * against the cache or against the method that the wrapper implements.
     */
    @Override
    public final StoredDocumentList askDocument(DocumentQuery documentQuery, boolean usingCache) {
        StoredDocumentList results = null;

        documentQuery.getQuery().asDNF();

        if (usingCache && (cache != null)) {
            results = (StoredDocumentList) cache.get(documentQuery.hashCode());
        }

        if (results == null) {
            results = retrieveMetadata(documentQuery);
        }

        if ((results != null) && (cache != null)) {
            cache.put(documentQuery.hashCode(), results);
            cache.flush();
        }

        return results;
    }


    /**
     * Processes the new document query by asking remove Digital Library
     * services.
     * 
     * @param query
     *            the query to process.
     * @return the retrieved document list, which might be empty but never null
     */
    private StoredDocumentList retrieveMetadata(DocumentQuery query) {
        StoredDocumentList documentList = null;

        try {
            documentList = getResultsByWrapperMode(query);
            removeInvalidDocuments(documentList);
        }
        catch (WrapperTemporaryException e) {
            logger.error("Temporary exception caught. Returning empty result.", e);
            documentList = new StoredDocumentList();
        }
        catch (WrapperEmergencyException e) {
            logger.error("Emergency exception caught. Shutting down.", e);
            halt();
            agent.halt();
            documentList = new StoredDocumentList();
        }

        return documentList;
    }


    /**
     * Removes invalid documents from the given list.
     * <p>
     * The validity is determined in two steps. First, the validity of the
     * source infos in the documents is determined. Documents with invalid
     * source infos are not valid. Then, the method
     * {@link #documentIsValid(StoredDocument)} is called to check the validity
     * from the viewpoint of the wrapper.
     * 
     * @param documentList
     *            the list to filter
     * @see SourceInfo#isValid(SourceInfo)
     */
    private void removeInvalidDocuments(StoredDocumentList documentList) {
        documentList.filter(new StoredDocumentFilter() {

            @Override
            public boolean isValid(StoredDocument stored) {
                for (SourceInfo source : stored.getSources()) {
                    if (!SourceInfo.isValid(source)) {
                        return false;
                    }
                }
                if (!documentIsValid(stored)) {
                    return false;
                }
                return true;
            }
        });
    }


    /**
     * Checks if the given document is valid or not.
     * <p>
     * This method is used in
     * {@link #removeInvalidDocuments(StoredDocumentList)} to remove invalid
     * documents before they are sent to the client.
     * 
     * @param stored
     *            the document to check
     * @return true, if the document is valid. Else false.
     */
    protected abstract boolean documentIsValid(StoredDocument stored);


    /**
     * Retrieves results in a way that depends on the mode of the wrapper.
     * 
     * @param query
     *            the query to process
     * @return whatever {@link #processQuery(MetadataQuery)} or
     *         {@link #processConjunctions(MetadataQuery)} return
     * @throws IllegalArgumentException
     *             if the wrapper's mode is not supported by this method
     */
    private StoredDocumentList getResultsByWrapperMode(DocumentQuery query) {
        StoredDocumentList documentList;
        switch (getWrapperMode()) {
            case WHOLE_QUERY: {
                documentList = process(query);
                break;
            }
            case CONJUNCTIONS: {
                documentList = processConjunctions(query);
                break;
            }
            default: {
                throw new IllegalArgumentException("The wrapper states to implement mode " + getWrapperMode()
                                + " but that mode is unknown.");
            }
        }
        return documentList;
    }


    /**
     * Runs {@link #processConjunction(Conjunction)} on each conjunction in the
     * query.
     * 
     * @param documentQuery
     *            a query in disjunctive normal form.
     * @return the result list, which is never null
     */
    private StoredDocumentList processConjunctions(DocumentQuery documentQuery) {
        logger.debug("Process conjunctions running");

        final StoredDocumentList results = new StoredDocumentList();
        final Query query = documentQuery.getQuery();

        final List<QueryNodeBool> conjunctions = query.asConjunctionList();

        final int maxI = Math.min(conjunctions.size(), getMaxConjunctionCount());
        for (int i = 0; (i < maxI); i++) {
            final QueryNodeBool conjunction = conjunctions.get(i);
            final StoredDocumentList docs = process(conjunction);
            if (docs != null) {
                results.merge(docs);
            }
        }
        return results;
    }


    /**
     * Returns the maximum number of conjunctions a wrapper should handle if
     * wrapper runs in mode {@link WrapperMode#CONJUNCTIONS} .
     * 
     * @return the maximum number of conjunctions a wrapper should handle
     */
    protected int getMaxConjunctionCount() {
        return Integer.MAX_VALUE;
    }


    /**
     * Processes a whole document query.
     * <p>
     * Wrappers that operate in {@link WrapperMode#WHOLE_QUERY} have to
     * implement this method with actual real logic. Others can just return
     * null.
     * 
     * @param query
     *            the query to process
     */
    protected abstract StoredDocumentList process(DocumentQuery query);


    /**
     * Processes a single conjunction of a document query.
     * <p>
     * Wrappers that operate in {@link WrapperMode#CONJUNCTIONS} have to
     * implement this method with actual real logic. Others can just return
     * null.
     * 
     * @param conjunction
     */
    protected abstract StoredDocumentList process(QueryNodeBool conjunction);


    /**
     * Returns the name of the agent that runs the wrapper.
     * 
     * @return the agent's name
     */
    protected final String getAgentName() {
        if (agent != null) {
            return agent.agentName();
        }
        return "Dummy";
    }


    @Override
    public void halt() {
        cancelProcessing = true;
    }


    @Override
    public boolean isHalted() {
        return cancelProcessing;
    }


    /**
     * Returns the logger.
     * 
     * @return the logger
     */
    protected Logger getLogger() {
        return logger;
    }


    /**
     * Returns the agent's properties.
     * 
     * @return the properties
     */
    protected Properties getProperties() {
        return agent.getProperties();
    }


    /**
     * Can be called to keep track of parser errors. If this method is called
     * more than {@link #MAX_PARSER_ERRORS} times, the agent that runs the
     * wrapper is halted.
     */
    protected void logParserError() {
        parserErrorCount++;
        if (parserErrorCount > MAX_PARSER_ERRORS) {
            logger.error("Error count exceeded " + MAX_PARSER_ERRORS + " so I shut myself down.");
            agent.halt();
        }
    }


    protected final Net getNet() {
        return net;
    }

}