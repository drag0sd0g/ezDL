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

package de.unidue.inf.is.ezdl.dlservices.search.handlers;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import de.unidue.inf.is.ezdl.dlbackend.ServiceNames;
import de.unidue.inf.is.ezdl.dlbackend.agent.Agent;
import de.unidue.inf.is.ezdl.dlbackend.agent.MessageForwarder;
import de.unidue.inf.is.ezdl.dlbackend.agent.MessageForwarderClient;
import de.unidue.inf.is.ezdl.dlbackend.agent.StartedBy;
import de.unidue.inf.is.ezdl.dlbackend.agent.handlers.AbstractRequestHandler;
import de.unidue.inf.is.ezdl.dlbackend.data.DocumentListConverter;
import de.unidue.inf.is.ezdl.dlbackend.data.StoredDocument;
import de.unidue.inf.is.ezdl.dlbackend.data.StoredDocumentList;
import de.unidue.inf.is.ezdl.dlbackend.data.StoredDocumentList.StoredDocumentFilter;
import de.unidue.inf.is.ezdl.dlbackend.message.Message;
import de.unidue.inf.is.ezdl.dlbackend.message.content.DocumentQueryStoredTell;
import de.unidue.inf.is.ezdl.dlbackend.wrappers.SourceID;
import de.unidue.inf.is.ezdl.dlbackend.wrappers.SourceInfo;
import de.unidue.inf.is.ezdl.dlcore.cache.Cache;
import de.unidue.inf.is.ezdl.dlcore.data.OIDFactory;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.Document;
import de.unidue.inf.is.ezdl.dlcore.data.query.DocumentQuery;
import de.unidue.inf.is.ezdl.dlcore.data.query.ResultConfiguration;
import de.unidue.inf.is.ezdl.dlcore.data.query.ResultDocument;
import de.unidue.inf.is.ezdl.dlcore.data.query.ResultDocumentList;
import de.unidue.inf.is.ezdl.dlcore.message.content.DocumentQueryAsk;
import de.unidue.inf.is.ezdl.dlcore.message.content.DocumentQueryInfoNotify;
import de.unidue.inf.is.ezdl.dlcore.message.content.DocumentQueryResultTell;
import de.unidue.inf.is.ezdl.dlcore.message.content.MessageContent;
import de.unidue.inf.is.ezdl.dlcore.misc.EzDLException;
import de.unidue.inf.is.ezdl.dlservices.search.SearchAgent;
import de.unidue.inf.is.ezdl.dlservices.search.handlers.ranking.LuceneRanker;
import de.unidue.inf.is.ezdl.dlservices.search.handlers.ranking.Ranker;



/**
 * Processes {@link DocumentQueryAsk} messages by forwarding the messages to
 * some wrappers, collecting the answers and sending them back to the client.
 * <p>
 * The DocumentQueryHandler is also the entity that assigns object IDs (OID's)
 * to {@link Document} objects.
 * <p>
 * The process is like this:
 * <ol>
 * <li>The cache contains results that single wrappers sent for earlier queries.
 * The first step is to try all wrappers in the {@link DocumentQuery} if they
 * already have results for the given query in the cache.</li>
 * <li>The remaining wrappers are asked for results.</li>
 * <li>The results are cached per wrapper.</li>
 * <li>All result lists are merged and sent to the client.</li>
 * </ol>
 * 
 * @author mjordan
 */
@StartedBy(DocumentQueryAsk.class)
public class DocumentQueryHandler extends AbstractRequestHandler implements MessageForwarderClient {

    /**
     * The minimum waiting time in milliseconds to set for outgoing messages to
     * wrappers.
     */
    private static final int MIN_WAITING_TIME_MS = 40000;
    /**
     * The logger.
     */
    private Logger logger = Logger.getLogger(DocumentQueryAsk.class);
    /**
     * RequestHelper deals with collecting information from multiple messages.
     */
    private StoredDocumentList resultList = new StoredDocumentList();
    /**
     * The initially received message
     */
    private boolean askHandled = false;
    /**
     * How the results are to be delivered.
     */
    private ResultConfiguration resultConfig;
    /**
     * The query that is being processed.
     */
    private DocumentQuery documentQuery;
    /**
     * Used for forwarding the message to the wrappers and waiting for the
     * results.
     */
    private MessageForwarder forwarder;
    /**
     * Ranker for the search results.
     */
    private Ranker ranker;
    /**
     * Contains a, lazily evaluated, representation of the query to use as the
     * cache key.
     */
    private String keyQueryPart;


    /**
     * Constructor.
     */
    public DocumentQueryHandler() {
    }


    @Override
    public void init(String requestId, Agent agent) {
        super.init(requestId, agent);
        forwarder = new MessageForwarder(this, getRequestId());
        ranker = new LuceneRanker();
    }


    @Override
    protected boolean work(Message message) {
        boolean handled = true;

        MessageContent content = message.getContent();

        if ((content instanceof DocumentQueryAsk)) {
            handle(message, (DocumentQueryAsk) content);
        }
        else if (content instanceof DocumentQueryStoredTell) {
            handle(message, (DocumentQueryStoredTell) content);
        }
        else {
            handled = false;
        }

        return handled;
    }


    /**
     * Handles an incoming {@link DocumentQueryAsk} message content.
     * <p>
     * Sends a {@link DocumentQueryInfoNotify} message to the client, signaling
     * that the query is being processed. Then, forwards the query to the
     * wrappers given in the message.
     * 
     * @param message
     *            the message
     * @param content
     *            the content of the message
     */
    private void handle(Message message, DocumentQueryAsk content) {
        if (askHandled) {
            getLogger().warn("Got two DocumentQueryAsk messages with requestId " + message.getRequestId());
            return;
        }
        askHandled = true;

        resultConfig = content.getResultConfig();
        documentQuery = content.getQuery();

        getLogger().debug("Wrappers before cache lookup: " + documentQuery.getDLList());

        List<String> dlsNotToAskAgain = collectResultsFromCacheSendingNotifies(documentQuery);
        documentQuery.removeFromDLList(dlsNotToAskAgain);

        getLogger().debug("Results from cache: " + resultList);
        getLogger().debug("Wrappers after cache lookup: " + documentQuery.getDLList());

        if (documentQuery.getDLList().isEmpty()) {
            getLogger().debug("No more wrappers to ask - sending cached results");
            notifyUser(message, resultList.size());
            endAndSendResultList();
            getLogger().debug("Sent cached results");
        }
        else {
            getLogger().debug("Starting real (TM) search using remote wrappers");
            notifyUser(message, 0);
            forwardQueryToWrappers(content);
        }
    }


    /**
     * Sends a "search just started" notification to the user.
     * 
     * @param message
     *            the message to reply to
     */
    private void notifyUser(Message message, int count) {
        DocumentQueryInfoNotify searchStarted = new DocumentQueryInfoNotify(getRequestId(), count);
        send(message.tell(searchStarted));
    }


    /**
     * Handles an incoming {@link DocumentQueryStoredTell} from a wrapper.
     * <p>
     * Keeps track of the result. If there are still wrappers that have not
     * answered, yet, sends an info message to the client indicating the
     * progress. Else, sends the results.
     * 
     * @param message
     *            the message
     * @param content
     *            its content
     */
    private void handle(Message message, DocumentQueryStoredTell content) {
        getLogger().debug("Handling " + content);

        addResultList(content);

        String wrapperName = message.getFrom();
        forwarder.noteReceived(wrapperName);

        final StoredDocumentList results = content.getResults();
        if (results != null) {
            getLogger().debug("Sending info notify");
            sendInfoNotify(wrapperName, results.size());
            getLogger().debug("Sent info notify");

            cacheWrapperAnswer(wrapperName, results);
        }

        if (!forwarder.isWaiting()) {
            endAndSendResultList();
        }
    }


    /**
     * Forwards the query to the wrappers given in the query.
     * 
     * @param content
     *            the message content containing the query
     */
    private void forwardQueryToWrappers(DocumentQueryAsk content) {
        DocumentQuery dQuery = content.getQuery();

        List<String> dlList = dQuery.getDLList();
        List<String> agentList = new ArrayList<String>();
        for (String dl : dlList) {
            try {
                agentList.add(wrapperNameForDL(dl));
            }
            catch (EzDLException e) {
                logger.error(e.getMessage(), e);
            }
        }

        int timeoutMs = content.getMaxDurationMs();
        int minTimeMs = getMinWaitingTimeMs();
        if (timeoutMs < minTimeMs) {
            timeoutMs = minTimeMs;
            getLogger().debug("Capping timeout at " + timeoutMs + "ms");
            content.setMaxDurationMs(timeoutMs);
        }
        forwarder.forwardMessageToAgents(agentList, content, timeoutMs);
    }


    /**
     * Notifies the client that a wrapper has delivered results.
     * 
     * @param results
     *            the number of results
     * @param wrapperName
     *            the wrapper that delivered the content
     */
    private void sendInfoNotify(String wrapperName, int results) {
        String reqId = getRequestId();
        int total = resultList.size();
        DocumentQueryInfoNotify notify = new DocumentQueryInfoNotify(reqId, total, wrapperName, results);
        sendReply(notify);
        getLogger().info("Sent " + notify);
    }


    /**
     * Collects and merges document information from multiple messages.
     * 
     * @param content
     *            the content to merge into the already collected document
     *            information
     */
    private void addResultList(DocumentQueryStoredTell content) {
        StoredDocumentList dl = content.getResults();
        assignObjectIdsAndValidateSources(dl);
        StoredDocumentList resultList = this.resultList;
        resultList.merge(dl);
    }


    /**
     * Assigns object IDs for incoming documents.
     * 
     * @param dl
     */
    private void assignObjectIdsAndValidateSources(StoredDocumentList dl) {
        dl.filter(new StoredDocumentFilter() {

            @Override
            public boolean isValid(StoredDocument stored) {
                Document d = stored.getDocument();
                String oid = OIDFactory.calcOid(d);
                final boolean sourcesValid = validateSources(stored);
                if ((oid == null) || !sourcesValid) {
                    return false;
                }
                else {
                    d.setOid(oid);
                    return true;
                }
            }
        });
    }


    /**
     * Checks that the source infos in a document are okay.
     * 
     * @param stored
     *            the document
     * @return true, if the sources are okay (i.e. mainly not containing null
     *         references), else false.
     */
    private boolean validateSources(StoredDocument stored) {
        for (SourceInfo source : stored.getSources()) {
            if (!SourceInfo.isValid(source)) {
                return false;
            }
        }
        return true;
    }


    /**
     * Sends the results that were collected and kills the request handler.
     */
    protected void endAndSendResultList() {
        getLogger().debug("endAndSendResultList() entered");
        getLogger().debug("results from collector: " + resultList);
        if (!isHalted() || isSendPartialResults()) {
            getLogger().debug("Sending results and ending request");
            /*
             * The order is: first reply to the user, because she is waiting for
             * the results. Then do the less time-critical things like caching
             * and notifying the repository.
             */
            sendResultsFiltered(resultList, resultConfig);
            recordMissingWrappers(resultList);
            notifyRepository(resultList);
            getLogger().debug("Ending request");
            if (!isHalted()) {
                halt();
            }
        }
        getLogger().debug("endAndSendResultList() finished");
    }


    /**
     * Creates {@link SourceInfo} objects for all wrappers that were asked but
     * failed to answer. Sets these misses sets to all documents in the results.
     * 
     * @param results
     *            the results to record misses for
     */
    private void recordMissingWrappers(StoredDocumentList results) {
        List<String> outstandingWrappers = forwarder.stillWaitedFor();

        if (outstandingWrappers.size() != 0) {
            final Date now = new Date();
            Set<SourceInfo> misses = new HashSet<SourceInfo>();

            for (String wrapperName : outstandingWrappers) {
                SourceInfo miss = new SourceInfo(new SourceID(wrapperName, ""), "", now);
                misses.add(miss);
            }

            for (StoredDocument stored : results) {
                stored.setMisses(misses);
            }
        }
    }


    /**
     * Writes documents to the cache so that subsequent requests can use the
     * cached data.
     * 
     * @param wrapperName
     *            the wrapper that sent the data
     * @param data
     *            the documents
     */
    private void cacheWrapperAnswer(String wrapperName, StoredDocumentList documents) {
        getLogger().debug("cacheResult() entered");
        Cache cache = getCache();

        final String key = calculateKey(wrapperName);
        boolean notYetCached = (cache.get(key) == null);
        if (notYetCached) {
            cache.put(key, documents);
            getLogger().debug("Put to cache: " + documents);
        }

        getLogger().debug("cacheResult() finished");
    }


    private String calculateKey(String wrapperName) {
        return calculateKey(wrapperName, documentQuery);
    }


    String calculateKey(String dl, DocumentQuery query) {
        final String normQuery = calculateKeyQueryPart(query);
        final String key = dl + "\n" + normQuery;
        return key;
    }


    private String calculateKeyQueryPart(DocumentQuery query) {
        if (keyQueryPart == null) {
            keyQueryPart = calculateKeyQueryPartUncached(query);
        }
        return keyQueryPart;
    }


    String calculateKeyQueryPartUncached(DocumentQuery query) {
        return query.getQuery().asDNF().toString();
    }


    /**
     * Gets results from the cache, if there are any, and sends notifications to
     * the client.
     * 
     * @return the list of wrappers whose result were already in the cache and
     *         don't need to be asked again
     */
    private List<String> collectResultsFromCacheSendingNotifies(DocumentQuery query) {
        final List<String> dlList = query.getDLList();
        final List<String> dlsNotToAskAgain = new ArrayList<String>(dlList.size());
        for (String dl : dlList) {
            StoredDocumentList stuff = collectResultsForDL(dl);
            if (stuff != null) {
                sendInfoNotify(dl, stuff.size());
                resultList.merge(stuff);
                /*
                 * We have results for dl so no need to send a request again.
                 */
                dlsNotToAskAgain.add(dl);
            }
        }

        return dlsNotToAskAgain;
    }


    private StoredDocumentList collectResultsForDL(String dl) {
        StoredDocumentList stuff = null;
        try {
            final String key = calculateKey(wrapperNameForDL(dl));
            stuff = (StoredDocumentList) getCache().get(key);
        }
        catch (EzDLException e) {
            stuff = new StoredDocumentList();
        }
        return stuff;
    }


    /**
     * Gets the cache.
     * <p>
     * Basically only there to be overwritten in a unit test.
     * 
     * @return the cache
     */
    Cache getCache() {
        SearchAgent da = (SearchAgent) getAgent();
        return da.getCache();
    }


    /**
     * Sends the result message to the agent that requested the information
     * obeying the result configuration submitted with the query.
     * <p>
     * The parameter resultConfig is there merely to indicate that the method
     * uses the config to filter the results before sending.
     * 
     * @param results
     *            the results to send
     * @param resultConfig
     *            the ResultConfiguration that determines how the results are to
     *            be filtered before sending
     */
    private void sendResultsFiltered(StoredDocumentList results, ResultConfiguration resultConfig) {
        getLogger().debug("sendResult() entered: " + results);

        ResultDocumentList resultList = DocumentListConverter.toResultDocumentList(results);
        rank(resultList);
        DocumentListFilter filter = new DocumentListFilter(resultConfig);
        ResultDocumentList listToSend = filter.process(resultList);

        DocumentQueryResultTell content = new DocumentQueryResultTell(listToSend, resultList.size());
        getLogger().debug("Sending filtered results " + content);
        sendReply(content);
        getLogger().debug("sendResult() finished");
    }


    /**
     * Ranks the given list.
     * <p>
     * If the ranking fails, all RSVs are set to 0.0.
     * 
     * @param resultList
     *            the result list to rank
     */
    private void rank(ResultDocumentList resultList) {
        try {
            ranker.rank(resultList, documentQuery);
        }
        catch (Exception e) {
            getLogger().error("Ranking didn't work. Setting all to 0.0", e);
            for (ResultDocument result : resultList) {
                result.setRsv(0.0);
            }
        }
    }


    /**
     * Returns the name of the sender of the initial message.
     * <p>
     * For testing purposes.
     * 
     * @return the sender of the message
     */
    String getSenderOfInitialMessage() {
        return getInitialMessage().getFrom();
    }


    /**
     * Sends message content to the party that sent the initial
     * {@link DocumentQueryAsk}.
     * 
     * @param content
     *            the content to send
     */
    protected void sendReply(MessageContent content) {
        getLogger().debug("Sending reply");
        final Message initialMessage = getInitialMessage();
        send(initialMessage.tell(content));
        getLogger().debug("Sent reply");
    }


    /**
     * Sends message content to the party that sent the initial
     * {@link DocumentQueryAsk}.
     * 
     * @param content
     *            the content to send
     */
    protected void notifyRepository(StoredDocumentList results) {
        final String repositoryName = getRepositoryName();
        if (repositoryName != null) {
            MessageContent content = new DocumentQueryStoredTell(results);
            getLogger().debug("Sending results to repository");
            String rid = getAgent().getNextRequestID();
            Message message = new Message(getAgent().agentName(), repositoryName, content, rid);
            send(message);
            getLogger().debug("Sent results to repository");
        }
    }


    @Override
    public void halt() {
        getLogger().debug("halt() called");
        if (!isHalted()) {
            forwarder.halt();
            super.halt();

            if (isSendPartialResults()) {
                endAndSendResultList();
            }
        }
        getLogger().debug("halt() finished");
    }


    @Override
    public void timeout() {
        endAndSendResultList();
    }


    @Override
    public String getNextRequestID() {
        return getAgent().getNextRequestID();
    }


    @Override
    public String agentName() {
        return getAgent().agentName();
    }


    /**
     * Returns the name of the repository. Basically just there to be
     * overwritten in a mock class for testing.
     * 
     * @return the name of the repository.
     */
    protected String getRepositoryName() {
        String repositoryAgentName = null;

        try {
            repositoryAgentName = getAgent().findAgent("/service/repository");
        }
        catch (EzDLException e) {
            getLogger().warn("Could not resolve repository agent name. So search results can not be stored.", e);
        }
        return repositoryAgentName;
    }


    /**
     * Returns the minimum waiting time. Basically just there to be overwritten
     * in a mock class for testing.
     * 
     * @return the minimum waiting time in milliseconds
     */
    protected int getMinWaitingTimeMs() {
        return MIN_WAITING_TIME_MS;
    }


    private String wrapperNameForDL(String dl) throws EzDLException {
        return getAgent().findAgent(ServiceNames.getServiceNameForDL(dl));
    }

}
