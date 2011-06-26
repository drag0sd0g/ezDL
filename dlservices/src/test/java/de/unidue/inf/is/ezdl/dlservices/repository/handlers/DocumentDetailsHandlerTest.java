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

package de.unidue.inf.is.ezdl.dlservices.repository.handlers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.unidue.inf.is.ezdl.dlbackend.BackendDocumentFactory;
import de.unidue.inf.is.ezdl.dlbackend.RequestHandlerTestBase;
import de.unidue.inf.is.ezdl.dlbackend.agent.Agent;
import de.unidue.inf.is.ezdl.dlbackend.data.StoredDocument;
import de.unidue.inf.is.ezdl.dlbackend.data.StoredDocumentList;
import de.unidue.inf.is.ezdl.dlbackend.message.Message;
import de.unidue.inf.is.ezdl.dlbackend.mock.MockAgent;
import de.unidue.inf.is.ezdl.dlcore.DocumentFactory;
import de.unidue.inf.is.ezdl.dlcore.data.query.ResultDocumentList;
import de.unidue.inf.is.ezdl.dlcore.message.content.DocumentDetailsAsk;
import de.unidue.inf.is.ezdl.dlcore.message.content.DocumentDetailsTell;
import de.unidue.inf.is.ezdl.dlservices.repository.store.DocumentStore;
import de.unidue.inf.is.ezdl.dlservices.repository.store.DocumentStoreResult;
import de.unidue.inf.is.ezdl.dlservices.repository.store.SimpleDetailRetrievalDecision;
import de.unidue.inf.is.ezdl.dlservices.repository.store.WrapperCache;
import de.unidue.inf.is.ezdl.dlservices.repository.store.repositories.DocumentRepository;
import de.unidue.inf.is.ezdl.dlservices.repository.store.repositories.MapRepository;



/**
 * @author mjordan
 */
public class DocumentDetailsHandlerTest extends RequestHandlerTestBase {

    private static final String AGENT_NAME = "MockDA";

    private static final boolean TIMEOUT = false;

    private MockAgent mockAgent;
    private MockStore store;
    private DocumentRepository repo;


    private class MockStore extends DocumentStore {

        public MockStore(Agent agent, DocumentRepository repository) {
            super(agent, repository, new SimpleDetailRetrievalDecision());
        }


        @Override
        protected WrapperCache getNewWrapperCache() {
            return null;
        }


        @Override
        protected List<String> getRelatedWrappers(String service) {
            return new ArrayList<String>();
        }


        @Override
        public DocumentStoreResult getDocuments(Collection<String> oids, boolean full, int timeoutMs) {
            StoredDocumentList documentList = repo.getDocuments(oids);
            return new DocumentStoreResult(documentList, TIMEOUT);
        }

    }


    /**
     * There to make DocumentQueryHandler testable.
     */
    private class TestableDocumentHandler extends DocumentDetailsAskHandler {

        @Override
        DocumentStore getStore() {
            return store;
        }
    }


    @Before
    public void setup() {
        mockAgent = new MockAgent(AGENT_NAME);
        repo = new MapRepository();
        store = new MockStore(mockAgent, repo);

    }


    @Test
    public void testEmptyRepo() {
        TestableDocumentHandler handler = new TestableDocumentHandler();

        handler.init("reqid", mockAgent);

        List<String> oids = new ArrayList<String>();
        oids.add("1");
        DocumentDetailsAsk ask = new DocumentDetailsAsk(oids);
        Message message = new Message("client", "repo", ask, "reqid");

        handler.addMessageToQueue(message);
        handler.work(message);

        ResultDocumentList document = new ResultDocumentList();
        DocumentDetailsTell tell = new DocumentDetailsTell(document, TIMEOUT);
        Message expected = message.tell(tell);

        new MessageSentWaiter(mockAgent, expected).assertGetsOkay("tell sent");
    }


    @Test
    public void testFilledRepo() {
        fillRepo("1", "abstract1", "title1", 2001, "author1");
        fillRepo("2", "abstract2", "title2", 2002, "author1", "author2");
        fillRepo("3", "abstract3", "title3", 2003, "author1", "author2", "author3");
        TestableDocumentHandler handler = new TestableDocumentHandler();

        handler.init("reqid", mockAgent);

        List<String> oids = new ArrayList<String>();
        oids.add("1");
        DocumentDetailsAsk ask = new DocumentDetailsAsk(oids);
        Message message = new Message("client", "repo", ask, "reqid");

        handler.addMessageToQueue(message);
        handler.work(message);

        ResultDocumentList documents = new ResultDocumentList();
        documents.add(DocumentFactory.createResultDocument("abstract1", "title1", 2001, "author1"));
        DocumentDetailsTell tell = new DocumentDetailsTell(documents, TIMEOUT);
        Message expected = message.tell(tell);

        getLogger().debug(expected);
        new MessageSentWaiter(mockAgent, expected).assertGetsOkay("tell sent");
    }


    private void fillRepo(String oid, String abst, String title, int year, String... authors) {
        StoredDocument document = BackendDocumentFactory.createStoredDocument(abst, title, year, authors);
        repo.addDocument(oid, document);
    }

}
