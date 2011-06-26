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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import de.unidue.inf.is.ezdl.dlbackend.agent.Agent;
import de.unidue.inf.is.ezdl.dlbackend.data.StoredDocument;
import de.unidue.inf.is.ezdl.dlbackend.data.StoredDocumentList;
import de.unidue.inf.is.ezdl.dlbackend.wrappers.SourceInfo;
import de.unidue.inf.is.ezdl.dlcore.cache.Cache;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.Document;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.PersonList;
import de.unidue.inf.is.ezdl.dlcore.data.query.DefaultQuery;
import de.unidue.inf.is.ezdl.dlcore.data.query.DocumentQuery;
import de.unidue.inf.is.ezdl.dlcore.data.query.Query;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeBool;
import de.unidue.inf.is.ezdl.dlcore.query.Filter;
import de.unidue.inf.is.ezdl.dlcore.query.QueryConverter;
import de.unidue.inf.is.ezdl.dlcore.query.YearRangeConverter;
import de.unidue.inf.is.ezdl.dlcore.query.YearRangeConverter.YearRange;
import de.unidue.inf.is.ezdl.dlcore.utils.StringUtils;
import de.unidue.inf.is.ezdl.dlwrapper.toolkit.ToolkitAPI;
import de.unidue.inf.is.ezdl.dlwrapper.toolkit.ToolkitFaultException;



/**
 * Toolkit-based Wrapper for generic libraries.
 * <p>
 * {@link AbstractBasicToolkitWrapper} implements both conjunction-based search
 * (via {@link AbstractWrapper#processConjunction(Conjunction)}) and query-base
 * search (via {@link AbstractWrapper#processQuery(DocumentQuery)}).
 * <p>
 * It can be used when the remote DL has a search form, a result page and
 * possibly a details page. The algorithm implemented here takes a wrapper
 * description in the form of a config file for {@link ToolkitAPI}, executes a
 * search and returns the results.
 * <p>
 * Only few methods have to be implemented in order to get this class working.
 * <p>
 * The parser relies on a certain form of the wrapper configuration. It uses
 * predefined toolkit properties to pass arguments:
 * <ul>
 * <li>{@link #TOOLKIT_PROP_URL} contains the search URL base</li>
 * <li>{@link #TOOLKIT_PROP_QUERY} contains the query</li>
 * <li>{@link #TOOLKIT_PROP_YEAR_RANGE} contains a URL parameter to be passed to
 * restrict a search to the year range in the query</li>
 * <li>{@link #TOOLKIT_PROP_MAX_RESULTS} contains the maximum number of results
 * that should be on the result page of the DL</li>
 * </ul>
 * <p>
 * AbstractBasicToolkitWrapper does not make any assumption about the names of
 * the information items parsed off the DL result page. This is entirely the
 * responsibility of the implementor. I.e. if the wrapper config parses the
 * title of a document into an item "doctitl", then the implementation of
 * {@link #createDocumentFromMap(Map)} has to use this string to retrieve the
 * document title.
 * <p>
 * There are a few predefine tags, nevertheless:
 * <ul>
 * <li>{@link #HT_KEY_NEXTPAGE} contains the link to the next result page. This
 * is used internally in this class to automatically process all result pages up
 * to a given limit.</li>
 * <li>{@link #HT_KEY_DETAILS} contains the link to the details page, used to
 * download the details, should they be requested by the user and, thus, the
 * search agent.</li>
 * </ul>
 * This abstract class expects some configuration options to be set in the
 * properties that the WrapperMapper read upon start:
 * <ul>
 * <li>{@link #MAX_ERRORS_KEY} configures the maximum number of errors that the
 * wrapper may tolerate parsing any given query before it halts itself using
 * {@link #halt()}. The default is {@link #MAX_ERRORS_DEFAULT}</li>
 * <li>{@link #MAX_FETCH_PAGES_KEY} configures the maximum number of pages the
 * wrapper will retrieve from the remote DL in order to process a query. The
 * default is {@link #MAX_FETCH_PAGES_DEFAULT}.</li>
 * <li>{@link #MAX_FETCH_RESULTS_KEY} configures the maximum number of results
 * to get per page. The default is {@link #MAX_FETCH_RESULTS_DEFAULT}.
 * </ul>
 */
public abstract class AbstractBasicToolkitWrapper extends AbstractToolkitWrapper {

    /**
     * Properties key to define the maximum tolerable number of errors per query
     * before the wrapper shuts itself down.
     */
    public static final String MAX_ERRORS_KEY = "maxErrors";
    /**
     * The default maximum number of errors per query session that the wrapper
     * accepts before halting itself unless indicated otherwise in the
     * properties.
     */
    private static final String MAX_ERRORS_DEFAULT = "3";
    /**
     * Properties key to define the number of pages to fetch by default.
     */
    public static final String MAX_FETCH_PAGES_KEY = "fetchMaxPages";
    /**
     * The default maximum number of pages that are requested per query unless
     * indicated otherwise in the properties.
     */
    private static final String MAX_FETCH_PAGES_DEFAULT = "3";
    /**
     * Properties key to define the number of results per page to fetch by
     * default.
     */
    public static final String MAX_FETCH_RESULTS_KEY = "fetchMaxResults";
    /**
     * The default maximum number of results that are requested per page unless
     * indicated otherwise in the properties.
     */
    private static final String MAX_FETCH_RESULTS_DEFAULT = "100";
    /**
     * The toolkit property name used to pass the search URL to the
     * {@link ToolkitAPI}.
     */
    private static final String TOOLKIT_PROP_URL = "url";
    /**
     * The toolkit property name used to pass the search query to the
     * {@link ToolkitAPI}.
     */
    private static final String TOOLKIT_PROP_QUERY = "query";
    /**
     * The toolkit property name used to pass the maximum number of results per
     * page to the {@link ToolkitAPI}.
     */
    private static final String TOOLKIT_PROP_MAX_RESULTS = "maxResults";
    /**
     * The toolkit property name used to pass the year range URL parameter to
     * the {@link ToolkitAPI}.
     */
    private static final String TOOLKIT_PROP_YEAR_RANGE = "yearRange";
    /**
     * The key used by the {@link ToolkitAPI} to return the URL of the next
     * page, if a DL returns results on multiple pages.
     */
    protected static final String HT_KEY_NEXTPAGE = "nextpage";
    /**
     * The key used by the {@link ToolkitAPI} to return the link to the details
     * page so it can be used to scrape the details.
     */
    protected static final String HT_KEY_DETAILS = "details";
    /**
     * A strategy that converts a query to a year range that is contained in
     * that query.
     */
    private static final YearRangeConverter yearRangeConverter = new YearRangeConverter();
    /**
     * Maximum number of errors to tolerate in both the wrapper and the toolkit.
     */
    private int maxErrorCounter;
    /**
     * The maximum number of pages to fetch per query.
     */
    private int maxFetchPages;
    /**
     * The maximum number of results to fetch per query.
     */
    private int maxFetchResults;
    /**
     * The strategy used to convert the incoming ezDL query into one that the
     * ACM DL understands.
     */
    private QueryConverter conversionStrategy = newQueryConverter();


    /**
     * The default constructor initializes the two ToolkitAPI references with
     * real ToolkitAPI objects.
     */
    public AbstractBasicToolkitWrapper() {
        this(new ToolkitAPI());
    }


    /**
     * The constructor that takes references to ToolkitAPI objects. Used for
     * testing.
     * 
     * @param api
     *            the toolkit object used for Document queries
     */
    public AbstractBasicToolkitWrapper(ToolkitAPI api) {
        super(api);
    }


    @Override
    protected abstract WrapperMode getWrapperMode();


    /**
     * The file name, relative to the directory "wrapperconfigs", that
     * configures the {@link ToolkitAPI} for a document query.
     * 
     * @return the file name
     */
    protected abstract String getTookitConfigFileQuery();


    /**
     * The file name, relative to the directory "wrapperconfigs", that
     * configures the {@link ToolkitAPI} for a detail query.
     * 
     * @return the file name
     */
    protected abstract String getTookitConfigFileDetails();


    /**
     * The URL of the search page. This will be passed to the {@link ToolkitAPI}
     * in the property key {@link #TOOLKIT_PROP_URL}.
     * 
     * @return
     */
    protected abstract String initialSearchUrl();


    /**
     * Factory method that returns a query converter suitable for the
     * implementor.
     * 
     * @return a ready-to use {@link QueryConverter} object
     */
    protected abstract QueryConverter newQueryConverter();


    @Override
    public void init(Agent agent, Cache cache) {
        super.init(agent, cache);
        Properties props = agent.getProperties();
        maxErrorCounter = Integer.parseInt(props.getProperty(MAX_ERRORS_KEY, MAX_ERRORS_DEFAULT));
        setMaxErrors(maxErrorCounter);
        maxFetchPages = Integer.parseInt(props.getProperty(MAX_FETCH_PAGES_KEY, MAX_FETCH_PAGES_DEFAULT));
        maxFetchResults = Integer.parseInt(props.getProperty(MAX_FETCH_RESULTS_KEY, MAX_FETCH_RESULTS_DEFAULT));
    }


    @Override
    protected final StoredDocumentList process(QueryNodeBool conjunction) {
        getLogger().debug("processConjunctionNew() " + conjunction);

        final Query query = new DefaultQuery(conjunction);
        final Filter fl = new Filter(conjunction);

        final StoredDocumentList result = process(query, fl);
        return result;
    }


    @Override
    protected final StoredDocumentList process(DocumentQuery docQuery) {
        getLogger().debug("processQuery() " + docQuery);

        final Query query = docQuery.getQuery();
        final Filter fl = new Filter(docQuery);

        final StoredDocumentList result = process(query, fl);
        return result;
    }


    private StoredDocumentList process(Query query, Filter fl) {
        getLogger().debug("process() " + query);

        initToolkit(getTookitConfigFileQuery(), maxErrorCounter);

        StoredDocumentList result = new StoredDocumentList();

        try {

            final String queryString = conversionStrategy.convert(query);

            if (queryString != null) {

                final YearRange range = yearRangeConverter.convertYearRange(query.getTree());
                final String yearRangeParameter = getYearRangeParameter(range, new Date());

                getLogger().debug("Using query : " + queryString + " and year range " + yearRangeParameter);

                Map<String, Object> toolkitProps = calcToolkitProps(queryString, yearRangeParameter);

                scrapePages(result, toolkitProps, fl);
            }
        }
        catch (ToolkitFaultException e) {
            logError("Parsing trouble: ", e);
        }

        getLogger().debug("Processing of " + query + " done. Got " + getErrorCounter() + " errors.");

        return result;
    }


    /**
     * Converts a given year range into a URL parameter, using a "now" date.
     * 
     * @param yearRange
     *            the year range to convert
     * @param now
     *            now, for reference purposes
     * @return null if no year range clause can or must be determined or a
     *         String that can be passed to the {@link ToolkitAPI} to add to the
     *         query. The parameter used for this is
     *         {@link #TOOLKIT_PROP_YEAR_RANGE}.
     */
    protected abstract String getYearRangeParameter(YearRange yearRange, Date now);


    protected Map<String, Object> calcToolkitProps(String query, String yearRange) {
        Map<String, Object> toolkitProps = new HashMap<String, Object>();

        final String initialUrl = initialSearchUrl();
        toolkitProps.put(TOOLKIT_PROP_URL, initialUrl);
        toolkitProps.put(TOOLKIT_PROP_QUERY, query);
        toolkitProps.put(TOOLKIT_PROP_MAX_RESULTS, Integer.toString(maxFetchResults));

        if (yearRange != null) {
            toolkitProps.put(TOOLKIT_PROP_YEAR_RANGE, yearRange);
        }
        return toolkitProps;
    }


    /**
     * Collects document scraped from pages in result. How to crawl is passed in
     * toolkitProps.
     * 
     * @param result
     *            the list that takes the result items
     * @param toolkitProps
     *            the configuration for the {@link ToolkitAPI}
     * @param filter
     *            the filter to be used
     */
    @SuppressWarnings("rawtypes")
    protected void scrapePages(StoredDocumentList result, Map<String, Object> toolkitProps, Filter filter) {
        int page = 0;
        URL nextPage;

        do {
            page++;
            nextPage = null;
            getLogger().debug("Scraping page " + page + " of max. " + maxFetchPages);

            List followData = toolkitExecuteList(toolkitProps);

            if (followData != null) {
                nextPage = processFollowData(result, followData, filter);
                betweenPages(toolkitProps, page, nextPage);
            }
        }
        while (!isHalted() && (page < maxFetchPages) /*
                                                      * && (result.size() <
                                                      * maxFetchResults)
                                                      */
                        && urlValid(nextPage));
    }


    /**
     * Performs actions between two pages being processed.
     * <p>
     * This default implementation replaces the URL with the one of the next
     * page.
     * 
     * @param toolkitProps
     * @param page
     * @param nextPage
     */
    protected void betweenPages(Map<String, Object> toolkitProps, int page, URL nextPage) {
        if (urlValid(nextPage)) {
            toolkitProps.put(TOOLKIT_PROP_URL, nextPage.toString());
        }
        getLogger().debug("Parsing errors so far: " + getErrorCounter());
    }


    protected boolean urlValid(URL nextPage) {
        return (nextPage != null) && !StringUtils.isEmpty(nextPage.toString());
    }


    /**
     * Collects documents from the followData passed and drops those documents
     * that don't match the filter.
     * 
     * @param result
     *            the result list that takes the documents
     * @param followData
     *            the data to take the documents from
     * @param filter
     *            the filter to identify desired documents
     * @return
     */
    @SuppressWarnings({
                    "rawtypes", "unchecked"
    })
    protected URL processFollowData(StoredDocumentList result, List followData, Filter filter) {
        URL nextPage = null;
        final Iterator iter = followData.iterator();
        while (!isHalted() && iter.hasNext()) {
            Map ht = (Map) iter.next();
            /*
             * Not logging an error here because sometimes only few results are
             * returned and then the wrapper should not shut down. If the web
             * page layout is wildly different and that is the reason for the
             * page number parsing problem, we should have other parsing errors
             * as well and those should be more significant than this one.
             */
            nextPage = (URL) ht.get(HT_KEY_NEXTPAGE);

            final String sourceLink = (String) ht.get(HT_KEY_DETAILS);
            final Document md = createDocumentFromMap(ht);
            processDocument(result, filter, md, sourceLink);
        }
        return nextPage;
    }


    private void processDocument(StoredDocumentList result, Filter fl, Document md, String source) {

        if (md != null) {
            if (isDocumentValid(md)) {
                if (fl.check(md)) {
                    StoredDocument stored = new StoredDocument(md);
                    stored.addSource(new SourceInfo(getSourceID(), source));
                    result.add(stored);
                }
                else {
                    getLogger().debug("Dropped md because it failed the filter check: " + md);
                }
            }
            else {
                logError("processDocument(): md was not null but also not valid");
            }
        }
        else {
            logError("processDocument(): md was null");
        }
    }


    protected boolean isDocumentValid(Document md) {
        boolean isValid = true;
        PersonList al = md.getAuthorList();
        if (al == null || al.size() == 0) {
            isValid = false;
        }

        String title = md.getTitle();
        if (title == null || title.isEmpty()) {
            isValid = false;
        }

        if (md.getYear() == Document.YEAR_INVALID) {
            isValid = false;
        }

        getLogger().debug("md valid: " + isValid + " for " + md);

        return isValid;
    }


    /**
     * Takes a map with data extracted from a result page and converts it into
     * an ezDL {@link Document}.
     * 
     * @param ht
     *            the extracted data
     * @return the document or null if an error occurred
     */
    protected abstract Document createDocumentFromMap(Map<String, Object> ht);


    @Override
    public void askDetails(StoredDocumentList incomplete) {
        getLogger().debug("askDetails() " + incomplete);
        for (StoredDocument stored : incomplete) {
            boolean detailsOk = askDetails(stored);
            if (detailsOk) {
                setDetailTimestampToCurrent(stored);
            }
        }
    }


    private final boolean askDetails(StoredDocument document) {
        Document d = null;
        getLogger().debug("askDetails: starting ...");
        String details = getDetailInfo(document);

        if (details == null) {
            getLogger().warn("No details information for " + document);
            return false;
        }

        getLogger().debug("Details encoded: " + details);
        String sDetails = StringUtils.utf8Decode(details);
        getLogger().debug("Details decoded: " + sDetails);
        try {
            URL u = documentDetailsUrl(sDetails);
            if (u != null) {
                d = fetchDetails(u);
                if (d != null) {
                    document.getDocument().merge(d);
                }
            }
        }
        catch (MalformedURLException e) {
            logError("askDetails: Could not fetch details ... for url\n" + sDetails);
            return false;
        }
        getLogger().debug("askDetails: end ...");
        return true;
    }


    /**
     * Returns the URL of the details page for a document calculated from a
     * piece of (wrapper-depending) details information.
     * 
     * @param detailInfo
     *            the piece of information that the wrapper uses to construct or
     *            look up the details page URL
     * @return the URL of the details page or null if no such page can be found.
     *         Returning null here basically says
     *         "don't retrieve details for this document".
     * @throws MalformedURLException
     */
    protected abstract URL documentDetailsUrl(String detailInfo) throws MalformedURLException;


    @SuppressWarnings("unchecked")
    private final Document fetchDetails(URL detailsURL) {
        getLogger().debug("Getting details from URL " + detailsURL);
        Map<String, Object> toolkitProps = new HashMap<String, Object>();
        toolkitProps.put(TOOLKIT_PROP_URL, detailsURL);

        initToolkit(getTookitConfigFileDetails(), maxErrorCounter);
        Map<String, Object> result = toolkitExecuteMap(toolkitProps);

        if (result != null) {
            return createDocumentFromMap(result);
        }
        else {
            getLogger().warn("Toolkit returned null for execute wrapper config.");
            return null;
        }
    }

}
