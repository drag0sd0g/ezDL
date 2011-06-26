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

package de.unidue.inf.is.ezdl.dlservices.repository.store;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import de.unidue.inf.is.ezdl.dlbackend.ServiceNames;
import de.unidue.inf.is.ezdl.dlbackend.agent.Agent;
import de.unidue.inf.is.ezdl.dlbackend.data.StoredDocument;
import de.unidue.inf.is.ezdl.dlbackend.data.StoredDocumentList;
import de.unidue.inf.is.ezdl.dlbackend.message.Message;
import de.unidue.inf.is.ezdl.dlbackend.message.content.DocumentDetailsFillAsk;
import de.unidue.inf.is.ezdl.dlbackend.wrappers.SourceInfo;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.Document;
import de.unidue.inf.is.ezdl.dlcore.misc.EzDLException;
import de.unidue.inf.is.ezdl.dlservices.repository.store.repositories.DocumentRepository;



/**
 * The DocumentStore encapsulates handling documents. It can be used to store
 * and retrieve document for objects given by object ID or by example
 * (half-filled document object).
 * <p>
 * For reasons of performance improvement, the inserting of newly arriving
 * documents using {@link #addDocument(String, StoredDocument)} is cached using
 * a queue.
 * 
 * @author mjordan, tbeckers
 */
public class DocumentStore {

    /**
     * Helper class for handling and waiting on document detail answers.
     */
    private static class Waiter {

        private CountDownLatch countDownLatch;
        private StoredDocumentList results;


        public Waiter(CountDownLatch countDownLatch, StoredDocumentList results) {
            this.countDownLatch = countDownLatch;
            this.results = results;
        }


        public CountDownLatch getCountDownLatch() {
            return countDownLatch;
        }


        public StoredDocumentList getResults() {
            return results;
        }
    }


    private Logger logger = Logger.getLogger(DocumentStore.class);

    /**
     * Reference to the repository where the data is really stored.
     */
    private DocumentRepository repository;
    /**
     * The agent this document store is working for.
     */
    private Agent agent;
    /**
     * The outstanding detail requests of this store.
     */
    private Map<String, Waiter> outstandingDetailRequests;
    /**
     * A cache for wrapper information.
     */
    private final WrapperCache wrapperCache;
    /**
     * The strategy that decides when to try to retrieve details for an
     * incomplete document.
     */
    private DetailRetrievalDecision decisionStrategy;
    /**
     * The thread that inserts documents concurrently into the repository.
     */
    private DocumentInserter inserter;
    /**
     * The thread executor.
     */
    private ExecutorService executor;


    /**
     * Creates a new DocumentStore.
     * 
     * @param agent
     *            the agent the store works for
     * @param repository
     *            the repository where the store puts its data.
     * @param decisionStrategy
     *            the strategy for deciding which documents to retrieve details
     *            for.
     */
    public DocumentStore(Agent agent, DocumentRepository repository, DetailRetrievalDecision decisionStrategy) {
        this.agent = agent;
        this.repository = repository;
        this.outstandingDetailRequests = new ConcurrentHashMap<String, Waiter>();
        this.wrapperCache = getNewWrapperCache();
        this.decisionStrategy = decisionStrategy;

        this.executor = Executors.newCachedThreadPool();
        this.inserter = new DocumentInserter(repository);
        this.executor.execute(inserter);
    }


    /**
     * Returns a new instance of the wrapper cache.
     * <p>
     * This method is just there to be overridden in a unit test.
     * 
     * @return the reference to the new {@link WrapperCache}
     */
    protected WrapperCache getNewWrapperCache() {
        return new WrapperCache(agent);
    }


    /**
     * Retrieves document for a single document.
     * 
     * @param oid
     *            the object ID of the document
     * @param full
     *            true, if the data returned may be incomplete or true, if the
     *            data should first be completed before the method returns.
     * @param timeoutMs
     *            The timeout in milliseconds
     * @return the documents
     */
    public DocumentStoreResult getDocument(String oid, boolean full, int timeoutMs) {
        return getDocuments(Arrays.asList(oid), full, timeoutMs);
    }


    /**
     * Retrieves documents for multiple OIDs.
     * 
     * @param oids
     *            the collection of object ID's of the documents
     * @param sendCompleted
     *            true, if the data returned may be incomplete or true, if the
     *            data should first be completed before the method returns.
     * @param timeoutMs
     *            The timeout in milliseconds
     * @return the documents
     */
    public DocumentStoreResult getDocuments(Collection<String> oids, boolean sendCompleted, int timeoutMs) {
        StoredDocumentList documentList = tryToGetDocuments(oids, timeoutMs);

        if (sendCompleted) {
            return completeIncompleteDocuments(documentList, oids, timeoutMs);
        }
        else {
            return new DocumentStoreResult(documentList, false);
        }
    }


    private StoredDocumentList tryToGetDocuments(Collection<String> oids, int timeoutMs) {
        int documentsToGet = oids.size();

        StoredDocumentList documentList = inserter.getDocuments(oids);
        documentsToGet -= documentList.size();

        long startTimestamp = System.currentTimeMillis();
        while ((documentsToGet >= 1) && (System.currentTimeMillis() - startTimestamp < timeoutMs)) {
            final StoredDocumentList documents = repository.getDocuments(oids);
            documentsToGet -= documents.size();
            documentList.addAll(documents);

            if (documentsToGet >= 1) {
                try {
                    Thread.sleep(500);
                }
                catch (InterruptedException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }

        return documentList;
    }


    /**
     * Takes a list of documents that may or may not be completed and returns
     * them all completed.
     * 
     * @param documentList
     *            the input list of documents some of which might be incomplete
     * @param oids
     *            the OIDs of the input documents
     * @param timeoutMs
     *            the maximum processing time
     * @return the list of all documents, completed.
     */
    private DocumentStoreResult completeIncompleteDocuments(StoredDocumentList documentList, Collection<String> oids,
                    int timeoutMs) {
        StoredDocumentList documentsToBeCompleted = collectDocumentsToBeCompleted(documentList);

        if (documentsToBeCompleted.isEmpty()) {
            return new DocumentStoreResult(documentList, false);
        }
        else {
            return completeDocuments(documentsToBeCompleted, oids, timeoutMs);
        }
    }


    /**
     * Takes a list of documents and object IDs and returns them completed.
     * <p>
     * This method sends messages to some wrapper agents and waits for their
     * results. This might take a while so there is the timeout to let the
     * method return after some time regardless of the answer status of the
     * wrappers.
     * 
     * @param documents
     *            the documents to complete
     * @param oids
     *            the OIDs of these documents
     * @param timeoutMs
     *            the timeout to wait at most
     * @return the completed list of documents
     */
    private DocumentStoreResult completeDocuments(StoredDocumentList documents, Collection<String> oids, int timeoutMs) {
        DocumentDetailsFillAsk ask = new DocumentDetailsFillAsk(documents);
        Set<String> wrapperAgents = wrappersToAsk(documents);

        String requestId = agent.getNextRequestID();

        CountDownLatch countDownLatch = new CountDownLatch(wrapperAgents.size());
        Waiter waiter = new Waiter(countDownLatch, documents);
        outstandingDetailRequests.put(requestId, waiter);

        sendMessagesToWrappers(ask, wrapperAgents, requestId);

        try {
            boolean timeOutOccured = !countDownLatch.await(timeoutMs, TimeUnit.MILLISECONDS);
            if (timeOutOccured) {
                logger.error("timeout during detail retrieval");
            }
            outstandingDetailRequests.remove(requestId);
            logger.info("Finished Wrapper Search");
            return new DocumentStoreResult(waiter.getResults(), timeOutOccured);
        }
        catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
        return new DocumentStoreResult(tryToGetDocuments(oids, 50), false);
    }


    /**
     * Takes a document list and returns those documents that are incomplete.
     * 
     * @param documentList
     *            the documents
     * @return the incomplete documents from the input list
     */
    private StoredDocumentList collectDocumentsToBeCompleted(StoredDocumentList documentList) {
        StoredDocumentList incompleteDocuments = new StoredDocumentList();
        for (StoredDocument document : documentList) {
            if (decisionStrategy.detailRetrievalSensible(document)) {
                incompleteDocuments.add(document);
                logger.debug("Added " + document + " to completion list");
            }
        }
        return incompleteDocuments;
    }


    private void sendMessagesToWrappers(DocumentDetailsFillAsk ask, Set<String> wrapperAgents, String requestId) {
        for (String wrapperAgent : wrapperAgents) {
            Message msg = new Message(agent.agentName(), wrapperAgent, ask, requestId);
            agent.send(msg);
        }
    }


    private Set<String> wrappersToAsk(StoredDocumentList incompleteDocuments) {
        Set<String> wrapperAgents = new HashSet<String>();
        for (String service : getServicesFromDocuments(incompleteDocuments)) {
            try {
                final String agentName = agent.findAgent(service);
                if (agentName != null) {
                    wrapperAgents.add(agentName);
                }
            }
            catch (EzDLException e) {
                logger.error(e.getMessage(), e);
            }

        }
        return wrapperAgents;
    }


    /**
     * Adds a {@link StoredDocumentList} that contains {@link Document}s with
     * details.
     * 
     * @param requestID
     *            The request id this answer belongs to.
     * @param docs
     *            The {@link StoredDocumentList} with details
     */
    public void addDocumentDetailsAnswer(String requestID, StoredDocumentList docs) {
        for (StoredDocument doc : docs) {
            logger.debug("adding " + doc);
            inserter.addDetail(doc);
        }
        Waiter waiter = outstandingDetailRequests.get(requestID);
        if (waiter != null) {
            waiter.getResults().merge(docs);
            waiter.getCountDownLatch().countDown();
        }
    }


    private Set<String> getServicesFromDocuments(StoredDocumentList documentList) {
        Set<String> services = new HashSet<String>();

        for (StoredDocument d : documentList) {
            for (SourceInfo source : d.getSources()) {
                String service = ServiceNames.getServiceNameForDL(source.getSourceID().getDL());
                services.add(service);
                services.addAll(getRelatedWrappers(service));
            }
            for (SourceInfo missSource : d.getMisses()) {
                services.remove(ServiceNames.getServiceNameForDL(missSource.getSourceID().getDL().toLowerCase()));
            }
        }
        logger.info("Asked Wrapper: " + services);
        return services;
    }


    protected List<String> getRelatedWrappers(String service) {
        if (wrapperCache == null) {
            return new ArrayList<String>();
        }
        return wrapperCache.filteredCategoryWrapper(service);
    }


    /**
     * Adds the document to the underlying repository.
     */
    public void addDocument(String oid, StoredDocument document) {
        inserter.addResultItem(document);
    }


    /**
     * Returns information about the DocumentStore.
     * 
     * @return an object containing the information
     */
    public DocumentStoreInfo getInfo() {
        int totalDocs = repository.getRepositorySize();
        DocumentStoreInfo info = new DocumentStoreInfo(totalDocs);
        return info;
    }


    public void halt() {
        inserter.halt();
        executor.shutdown();
    }

}
