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

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.unidue.inf.is.ezdl.dlbackend.AbstractBackendTestBase;
import de.unidue.inf.is.ezdl.dlbackend.BackendDocumentFactory;
import de.unidue.inf.is.ezdl.dlbackend.agent.Agent;
import de.unidue.inf.is.ezdl.dlbackend.data.StoredDocument;
import de.unidue.inf.is.ezdl.dlbackend.data.StoredDocumentList;
import de.unidue.inf.is.ezdl.dlbackend.message.Message;
import de.unidue.inf.is.ezdl.dlbackend.message.content.DocumentDetailsFillAsk;
import de.unidue.inf.is.ezdl.dlbackend.mock.MockAgent;
import de.unidue.inf.is.ezdl.dlcore.data.OIDFactory;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.TextDocument;
import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;
import de.unidue.inf.is.ezdl.dlcore.misc.EzDLException;
import de.unidue.inf.is.ezdl.dlservices.repository.store.repositories.DocumentRepository;
import de.unidue.inf.is.ezdl.dlservices.repository.store.repositories.MapRepository;



public class DocumentStoreTest extends AbstractBackendTestBase {

    private static final String FULL_ABSTRACT_CONTENT = "abstract";
    private TestableDocumentStore documentStore;
    private MockAgent mockAgent;


    class TestableDocumentStore extends DocumentStore {

        public TestableDocumentStore(Agent agent, DocumentRepository repository) {
            super(agent, repository, new SimpleDetailRetrievalDecision());
        }


        @Override
        protected WrapperCache getNewWrapperCache() {
            return null;
        }


        @Override
        protected List<String> getRelatedWrappers(String service) {
            return super.getRelatedWrappers(service);
        }
    }


    @Before
    public void setup() {
        mockAgent = new MockAgent() {

            @Override
            public String findAgent(String service) throws EzDLException {
                return "dummy";
            }
        };
        documentStore = new TestableDocumentStore(mockAgent, new MapRepository());
    }


    @Test
    public void testAddGetDocument() {
        StoredDocument storedDocument = createNewStoredFullDocument();
        documentStore.addDocument(storedDocument.getOid(), storedDocument);

        Assert.assertEquals(storedDocument, documentStore.getDocument(storedDocument.getOid(), false, 1000).getFirst());
        Assert.assertEquals(0, mockAgent.getMessagesSent().size());
    }


    @Test
    public void testAddGetDocumentFullWithTimeout() {
        StoredDocument storedDocument = createNewStoredNonFullDocument(calcCurrentYear());
        documentStore.addDocument(storedDocument.getOid(), storedDocument);

        Assert.assertEquals(storedDocument, documentStore.getDocument(storedDocument.getOid(), true, 1000).getFirst());

        StoredDocumentList list = new StoredDocumentList();
        list.add(storedDocument);
        DocumentDetailsFillAsk ask = new DocumentDetailsFillAsk(list);
        final List<Message> messagesSent = mockAgent.getMessagesSent();
        Assert.assertNotNull(messagesSent);
        Assert.assertFalse(messagesSent.size() == 0);
        Assert.assertEquals(ask, messagesSent.get(0).getContent());
    }


    @Test
    public void testAddDocumentDetails() {
        StoredDocument document = createNewStoredNonFullDocument(calcCurrentYear());
        documentStore.addDocument(document.getOid(), document);
        new Thread() {

            @Override
            public void run() {
                DocumentStoreTest.this.sleep(500);
                StoredDocumentList list = new StoredDocumentList();
                list.add(createNewStoredFullDocument());
                documentStore.addDocumentDetailsAnswer("reqid", list);
            };
        }.start();
        StoredDocument fullStoredDocument = documentStore.getDocument(document.getOid(), true, 2000).getFirst();

        final TextDocument recDoc = (TextDocument) fullStoredDocument.getDocument();
        Assert.assertTrue(FULL_ABSTRACT_CONTENT.equals(recDoc.getAbstract()));
    }


    public StoredDocument createNewStoredFullDocument() {
        StoredDocument storedDocument = createNewStoredNonFullDocument(calcCurrentYear());
        storedDocument.getDocument().setFieldValue(Field.ABSTRACT, FULL_ABSTRACT_CONTENT);
        return storedDocument;
    }


    public StoredDocument createNewStoredNonFullDocument(int year) {
        StoredDocument storedDocument = BackendDocumentFactory.createStoredDocument(null, "title", year, "author1",
                        "author2");
        String oid = OIDFactory.calcOid(storedDocument.getDocument());
        storedDocument.getDocument().setOid(oid);
        return storedDocument;
    }


    private int calcCurrentYear() {
        final Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        final int currentYear = c.get(Calendar.YEAR);
        return currentYear;
    }

}
