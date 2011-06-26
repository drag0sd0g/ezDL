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

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import de.unidue.inf.is.ezdl.dlbackend.RequestHandlerTestBase;
import de.unidue.inf.is.ezdl.dlbackend.data.StoredDocument;
import de.unidue.inf.is.ezdl.dlbackend.data.StoredDocumentList;
import de.unidue.inf.is.ezdl.dlbackend.message.Message;
import de.unidue.inf.is.ezdl.dlbackend.message.content.DocumentQueryStoredTell;
import de.unidue.inf.is.ezdl.dlbackend.mock.MockAgent;
import de.unidue.inf.is.ezdl.dlbackend.wrappers.SourceID;
import de.unidue.inf.is.ezdl.dlbackend.wrappers.SourceInfo;
import de.unidue.inf.is.ezdl.dlcore.cache.Cache;
import de.unidue.inf.is.ezdl.dlcore.cache.MapCache;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.Document;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.Person;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.PersonList;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.TextDocument;
import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;
import de.unidue.inf.is.ezdl.dlcore.data.fields.Sorting;
import de.unidue.inf.is.ezdl.dlcore.data.query.DefaultQuery;
import de.unidue.inf.is.ezdl.dlcore.data.query.DocumentQuery;
import de.unidue.inf.is.ezdl.dlcore.data.query.ResultConfiguration;
import de.unidue.inf.is.ezdl.dlcore.data.query.ResultDocument;
import de.unidue.inf.is.ezdl.dlcore.data.query.ResultDocumentList;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeCompare;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeCompare.Predicate;
import de.unidue.inf.is.ezdl.dlcore.message.content.DocumentQueryAsk;
import de.unidue.inf.is.ezdl.dlcore.message.content.DocumentQueryInfoNotify;
import de.unidue.inf.is.ezdl.dlcore.message.content.DocumentQueryResultTell;
import de.unidue.inf.is.ezdl.dlcore.misc.EzDLException;



public class DocumentQueryHandlerTest extends RequestHandlerTestBase {

    private static final int RUNTIME_MS = 200000;
    private static final String AGENT_NAME = "MockSA";
    private static final String REPOSITORY_NAME = "MockRA";
    private static final String REQID = "reqid";
    private static final String CLIENT = "client";

    private DocumentQueryHandler handler;

    private MockAgent mockAgent;
    private Cache cache = new MapCache();


    /**
     * There to make DocumentQueryHandler testable.
     */
    class TestableDocumentQueryHandler extends DocumentQueryHandler {

        @Override
        protected int getMinWaitingTimeMs() {
            return RUNTIME_MS;
        }


        @Override
        Cache getCache() {
            return cache;
        }


        @Override
        protected String getRepositoryName() {
            return REPOSITORY_NAME;
        }
    }


    @Before
    public void setup() {
        mockAgent = new MockAgent(AGENT_NAME) {

            @Override
            public String findAgent(String service) throws EzDLException {
                return service.replace("/wrapper/dl/", "");
            }
        };
        handler = new TestableDocumentQueryHandler();
    }


    @Test
    public void testWorkEmptyMessage() {
        Message message = new Message();

        DocumentQueryHandler handler = new TestableDocumentQueryHandler();
        handler.init(message.getRequestId(), mockAgent);

        handler.work(message);

        Assert.assertEquals("Message dropped, no answer sent", 0, mockAgent.getMessagesSent().size());
    }


    /**
     * Checks if the timeout in the message sent to the wrappers complies with
     * the minimum setting in the DocumentQueryHandler.
     */
    @Test
    public void testWorkMinimumTimeForwarded() {
        checkWorkMinimumTimeForwarded("max duration forwarded", 20, RUNTIME_MS);
        checkWorkMinimumTimeForwarded("max duration forwarded", RUNTIME_MS, RUNTIME_MS);
        checkWorkMinimumTimeForwarded("max duration forwarded", RUNTIME_MS + 200, RUNTIME_MS + 200);
    }


    public void checkWorkMinimumTimeForwarded(String label, int timeMs, int expected) {
        Message message = new Message();
        DocumentQuery query = new DocumentQuery(new DefaultQuery(
                        new QueryNodeCompare(Field.TITLE, Predicate.EQ, "test")), Arrays.asList("dummy1"));
        DocumentQueryAsk content = new DocumentQueryAsk(query, new ResultConfiguration());
        content.setMaxDurationMs(timeMs);
        message.setContent(content);

        DocumentQueryHandler handler = new TestableDocumentQueryHandler();
        handler.init(message.getRequestId(), mockAgent);

        handler.work(message);

        Assert.assertEquals("2 Messages sent", 2, mockAgent.getMessagesSent().size());
        List<Message> msgSent = mockAgent.getMessagesSent();
        for (Message m : msgSent) {
            if (m.getContent() instanceof DocumentQueryAsk) {
                DocumentQueryAsk ask = (DocumentQueryAsk) m.getContent();
                Assert.assertEquals(label, expected, ask.getMaxDurationMs());
            }
        }

        mockAgent.clearMessagesSent();
    }


    @Test
    public void testWorkASKDocument1Wrapper() {
        checkMessageDelivery(handler, "Dummy1");

        Assert.assertEquals("client ID", CLIENT, handler.getSenderOfInitialMessage());
    }


    @Test
    public void testWorkASKDocument2Wrappers() {
        checkMessageDelivery(handler, "Dummy1", "Dummy2");

        Assert.assertEquals("client ID", CLIENT, handler.getSenderOfInitialMessage());
    }


    /**
     * Tests the situation that the query is forwarded to 1 wrapper.
     */
    @Test
    public void testWorkASKTELL1WrapperDocument() {
        Message messageTELL1 = getDocumentStoredTell("Dummy1", "SA", "author1");
        checkWorkASKTELL1WrapperDocument(messageTELL1);
    }


    /**
     * Tests the situation that the query is forwarded to 1 wrapper but a
     * document in the results has a null list of authors.
     */
    @Test
    public void testWorkASKTELL1WrapperDocumentAuthorsNull() {
        Message messageTELL1 = getDocumentStoredTellDefective("Dummy1", "SA", "author1");
        checkWorkASKTELL1WrapperDocument(messageTELL1);
    }


    /**
     * A query for wrappers "dummy1" and "dummy2", both of which answered the
     * query earlier and, thus, have their result sets cached in the SA. This
     * means the request can be completed in one round and thus 1
     * {@link DocumentQueryInfoNotify} is sent, 1 result set to the client and 1
     * result set to the repository plus 2 notifications for the in-cache result
     * lists.
     * <p>
     * Wrappers asked: dummy1, dummy2 <br>
     * Results cached: dummy1, dummy2
     */
    @Test
    public void testCachingScheme1() {
        String[] dls = {
                        "dummy1", "dummy2"
        };
        Message message = getDocumentAsk(dls);

        handler = new TestableDocumentQueryHandler();
        preloadCache(message, "dummy1");
        preloadCache(message, "dummy2");

        checkAskHandlingAndForwarding(handler, message, 5);
    }


    /**
     * A query for wrappers "dummy1" and "dummy2", that only dummy1 had answered
     * earlier. So 1 {@link DocumentQueryInfoNotify} message is sent and one
     * message to the missing wrapper dummy2 plus 1 notification for the
     * in-cache result list.
     * <p>
     * Wrappers asked: dummy1, dummy2 <br>
     * Results cached: dummy1
     */
    @Test
    public void testCachingScheme2() {
        String[] dls = {
                        "dummy1", "dummy2"
        };
        Message message = getDocumentAsk(dls);

        handler = new TestableDocumentQueryHandler();
        preloadCache(message, "dummy1");

        checkAskHandlingAndForwarding(handler, message, 3, "dummy2");
    }


    /**
     * A query for wrappers "dummy1" and "dummy2", that none had answered
     * earlier. So 1 {@link DocumentQueryInfoNotify} message is sent and 1
     * message each to the missing wrappers dummy1 and dummy2.
     * <p>
     * Wrappers asked: dummy1, dummy2 <br>
     * Results cached: (none)
     */
    @Test
    public void testCachingScheme3() {
        String[] dls = {
                        "dummy1", "dummy2"
        };
        Message message = getDocumentAsk(dls);

        handler = new TestableDocumentQueryHandler();

        checkAskHandlingAndForwarding(handler, message, 3, "dummy1", "dummy2");
    }


    private void preloadCache(Message message, String wrapper) {
        final String key = handler.calculateKey(wrapper, ((DocumentQueryAsk) message.getContent()).getQuery());
        cache.put(key, new StoredDocumentList());
    }


    private void checkWorkASKTELL1WrapperDocument(Message messageTELL1) {
        checkMessageDelivery(handler, "Dummy1");

        Assert.assertEquals("client ID", CLIENT, handler.getSenderOfInitialMessage());

        /*
         * Time passes by as the messages travel through the net and finally
         * reach their destinations.
         */

        mockAgent.clearMessagesSent();

        /*
         * Now the first Dummy wrapper replies with some data
         */

        new HandlerRunner(handler, messageTELL1);

        /*
         * The query is processed and 3 messages are sent: - the notify message
         * with the number of results - the result message to the client - the
         * CC to the repository
         */

        new MessageSentCountWaiter(mockAgent, 3).assertGetsOkay("n # msg 1");
        Assert.assertEquals("m # msg 2", 3, mockAgent.getMessagesSent().size());

        ResultConfiguration resConf = new ResultConfiguration(0, 10, Arrays.asList(Field.AUTHOR), new Sorting());

        Message tellDocumentList = getDocumentResultTell(AGENT_NAME, "client", resConf, messageTELL1);
        Assert.assertTrue("Tell answer sent", mockAgent.getMessagesSent().contains(tellDocumentList));

        Message tellRANotification = getDocumentStoredTell(AGENT_NAME, REPOSITORY_NAME, messageTELL1);
        Assert.assertTrue("Repository notification sent", mockAgent.getMessagesSent().contains(tellRANotification));

        Assert.assertEquals("client ID", CLIENT, handler.getSenderOfInitialMessage());
    }


    /**
     * Tests the situation that the query is forwarded to 2 wrappers.
     */
    @Test
    public void testWorkASKTELL2WrappersDocument() {
        inner_testWorkASKTELL2WrappersDocument();
    }


    private Message inner_testWorkASKTELL2WrappersDocument() {
        checkMessageDelivery(handler, "Dummy1", "Dummy2");

        /*
         * Time passes by as the messages travel through the net and finally
         * reach their destinations.
         */

        mockAgent.clearMessagesSent();

        /*
         * Now the first Dummy wrapper replies with some data
         */

        Message messageTELL1 = getDocumentStoredTell("Dummy1", "SA", "author1");

        new HandlerRunner(handler, messageTELL1);

        new MessageSentCountWaiter(mockAgent, 1).assertGetsOkay("# msg");

        /*
         * Now the second Dummy wrapper replies with some data
         */

        Message messageTELL2 = getDocumentStoredTell("Dummy2", "SA", "author2");

        new HandlerRunner(handler, messageTELL2);
        new MessageSentCountWaiter(mockAgent, 4).assertGetsOkay("n # msg");

        ResultConfiguration resConf = new ResultConfiguration(0, 10, Arrays.asList(Field.AUTHOR), new Sorting());

        Message tellDocumentList = getDocumentResultTell(AGENT_NAME, "client", resConf, messageTELL1, messageTELL2);
        Assert.assertTrue("Tell answer sent", mockAgent.getMessagesSent().contains(tellDocumentList));

        Message tellRANotification = getDocumentStoredTell(AGENT_NAME, REPOSITORY_NAME, messageTELL1, messageTELL2);
        Assert.assertTrue("Repository notification sent", mockAgent.getMessagesSent().contains(tellRANotification));

        Assert.assertEquals("client ID", CLIENT, handler.getSenderOfInitialMessage());

        return messageTELL2;
    }


    /**
     * Test with changed result config. The result config also defines only to
     * send the last result in the list.
     */
    @Test
    public void testWorkASKTELL2WrappersDocumentResultConfig() {
        checkMessageDeliveryResultConf(handler, "Dummy1", "Dummy2");

        /*
         * Time passes by as the messages travel through the net and finally
         * reach their destinations.
         */

        mockAgent.clearMessagesSent();

        /*
         * Now the first Dummy wrapper replies with some data
         */

        Message messageTELL1 = getDocumentStoredTell("Dummy1", "SA", "author1");
        new HandlerRunner(handler, messageTELL1);

        new MessageSentCountWaiter(mockAgent, 1).assertGetsOkay("# msg");

        /*
         * Now the second Dummy wrapper replies with some data
         */

        Message messageTELL2 = getDocumentStoredTell("Dummy2", "SA", "author2");

        new HandlerRunner(handler, messageTELL2);
        new MessageSentCountWaiter(mockAgent, 3).assertGetsOkay("# msg");

        ResultConfiguration resConf = new ResultConfiguration(0, 10, Arrays.asList(Field.AUTHOR), new Sorting());

        Message tellDocumentList = getDocumentResultTell(AGENT_NAME, "client", resConf, messageTELL2);
        Assert.assertTrue("Tell answer sent", mockAgent.getMessagesSent().contains(tellDocumentList));

        Assert.assertEquals("client ID", CLIENT, handler.getSenderOfInitialMessage());

        Message tellRANotification = getDocumentStoredTell(AGENT_NAME, REPOSITORY_NAME, messageTELL1, messageTELL2);
        new MessageSentWaiter(mockAgent, tellRANotification).assertGetsOkay("Repository notification sent");
    }


    /**
     * Tests a subsequent Ask message.
     * <p>
     * That means, a first query response conversation has taken place. Now,
     * e.g. because the user flipped to the next page, more results of the same
     * query are requested.
     */
    @Test
    public void testSecondAsk() {
        Message origTELL = inner_testWorkASKTELL2WrappersDocument();

        mockAgent.clearMessagesSent();

        List<String> wrapperList = new LinkedList<String>();
        wrapperList.add("Dummy1");
        wrapperList.add("Dummy2");
        DocumentQuery query = new DocumentQuery(new DefaultQuery(new QueryNodeCompare(Field.AUTHOR, Predicate.EQ,
                        "fuhr")), wrapperList);

        List<Field> fields = new LinkedList<Field>();
        fields.add(Field.AUTHOR);
        Sorting sorting = new Sorting();
        ResultConfiguration resConf = new ResultConfiguration(1, 1, fields, sorting);

        Message message = new Message();
        message.setFrom(CLIENT);
        message.setTo(mockAgent.agentName());
        DocumentQueryAsk content = new DocumentQueryAsk(query, resConf);
        content.setMaxDurationMs(RUNTIME_MS);
        message.setContent(content);
        message.setRequestId(REQID);

        handler = new TestableDocumentQueryHandler();
        handler.init(message.getRequestId(), mockAgent);
        handler.addMessageToQueue(message);
        new HandlerRunner(handler, message);

        /*
         * Get a message that looks like the one we're expecting
         */
        ResultConfiguration resConf2 = new ResultConfiguration(0, 0, Arrays.asList(Field.AUTHOR), new Sorting());
        Message tell = getDocumentResultTell(mockAgent.agentName(), "SA", resConf2, origTELL);
        Message expected = new Message(mockAgent.agentName(), CLIENT, tell.getContent(), REQID);

        new MessageSentWaiter(mockAgent, expected).assertGetsOkay("result sent");
    }


    /**
     * Test a query to multiple wrappers some of which don't answer. We test if
     * the sources and misses lists are okay.
     */
    @Test
    public void testWorkASKTELL2WrappersOnlyOneAnswers1a() {
        /*
         * All answer.
         */
        checkWorkASKTELL2WrappersOnlyOneAnswers("Dummy1");
    }


    /**
     * Test a query to multiple wrappers some of which don't answer. We test if
     * the sources and misses lists are okay.
     */
    @Test
    public void testWorkASKTELL2WrappersOnlyOneAnswers1b() {
        /*
         * All answer.
         */
        checkWorkASKTELL2WrappersOnlyOneAnswers("Dummy1", "Dummy2");
    }


    /**
     * Test a query to multiple wrappers some of which don't answer. We test if
     * the sources and misses lists are okay.
     */
    @Test
    public void testWorkASKTELL2WrappersOnlyOneAnswers1c() {
        /*
         * All answer.
         */
        checkWorkASKTELL2WrappersOnlyOneAnswers("Dummy1", "Dummy2", "Dummy3");
    }


    /**
     * Test a query to multiple wrappers some of which don't answer. We test if
     * the sources and misses lists are okay.
     */
    @Test
    public void testWorkASKTELL2WrappersOnlyOneAnswers2a() {
        /*
         * 1 does not answer.
         */
        checkWorkASKTELL2WrappersOnlyOneAnswers("Dummy1NOT");
    }


    /**
     * Test a query to multiple wrappers some of which don't answer. We test if
     * the sources and misses lists are okay.
     */
    @Test
    public void testWorkASKTELL2WrappersOnlyOneAnswers2b() {
        /*
         * 1 does not answer.
         */
        checkWorkASKTELL2WrappersOnlyOneAnswers("Dummy1NOT", "Dummy2");
    }


    /**
     * Test a query to multiple wrappers some of which don't answer. We test if
     * the sources and misses lists are okay.
     */
    @Test
    public void testWorkASKTELL2WrappersOnlyOneAnswers2c() {
        /*
         * 1 does not answer.
         */
        checkWorkASKTELL2WrappersOnlyOneAnswers("Dummy1NOT", "Dummy2", "Dummy3");
    }


    /**
     * Test a query to multiple wrappers some of which don't answer. We test if
     * the sources and misses lists are okay.
     */
    @Test
    public void testWorkASKTELL2WrappersOnlyOneAnswers3a() {
        /*
         * 2 don't answer.
         */
        checkWorkASKTELL2WrappersOnlyOneAnswers("Dummy1NOT", "Dummy2NOT");
    }


    /**
     * Test a query to multiple wrappers some of which don't answer. We test if
     * the sources and misses lists are okay.
     */
    @Test
    public void testWorkASKTELL2WrappersOnlyOneAnswers3b() {
        /*
         * 2 don't answer.
         */
        checkWorkASKTELL2WrappersOnlyOneAnswers("Dummy1NOT", "Dummy2", "Dummy3NOT");
    }


    /**
     * Test a query to multiple wrappers some of which don't answer. We test if
     * the sources and misses lists are okay.
     */
    @Test
    public void testWorkASKTELL2WrappersOnlyOneAnswers3c() {
        /*
         * 2 don't answer.
         */
        checkWorkASKTELL2WrappersOnlyOneAnswers("Dummy1NOT", "Dummy2NOT", "Dummy3");
    }


    /**
     * Test a query to multiple wrappers some of which don't answer. We test if
     * the sources and misses lists are okay.
     */
    @Test
    public void testWorkASKTELL2WrappersOnlyOneAnswers4a() {
        /*
         * All don't answer.
         */
        checkWorkASKTELL2WrappersOnlyOneAnswers("Dummy1NOT", "Dummy2NOT");
    }


    /**
     * Test a query to multiple wrappers some of which don't answer. We test if
     * the sources and misses lists are okay.
     */
    @Test
    public void testWorkASKTELL2WrappersOnlyOneAnswers4b() {
        /*
         * All don't answer.
         */
        checkWorkASKTELL2WrappersOnlyOneAnswers("Dummy1NOT", "Dummy2NOT", "Dummy3NOT");
    }


    /**
     * Test a query to multiple wrappers some of which don't answer. We test if
     * the sources and misses lists are okay.
     */
    @Test
    public void testWorkASKTELL2WrappersOnlyOneAnswers4c() {
        /*
         * All don't answer.
         */
        checkWorkASKTELL2WrappersOnlyOneAnswers("Dummy1NOT", "Dummy2NOT", "Dummy3NOT", "Dummy4NOT");
    }


    public void checkWorkASKTELL2WrappersOnlyOneAnswers(String... wrappers) {
        mockAgent.clearMessagesSent();
        handler = new TestableDocumentQueryHandler();
        checkMessageDelivery(handler, wrappers);

        Set<String> expectedToMiss = new HashSet<String>();
        for (String wrapper : wrappers) {
            if (wrapper.endsWith("NOT")) {
                expectedToMiss.add(wrapper);
            }
        }

        /*
         * Time passes by as the messages travel through the net and finally
         * reach their destinations.
         */

        mockAgent.clearMessagesSent();

        /*
         * Now the Dummy wrappers reply with some data, except for those whose
         * names are not in "expectedToMiss".
         */

        int msgHandled = 0;
        int msgIdx = 0;
        // Message[] messages = new Message[wrappers.length];
        List<Message> messages = new LinkedList<Message>();
        for (String wrapper : wrappers) {
            msgIdx++;
            Message msg = getDocumentStoredTell(wrapper, "SA", "author" + msgIdx);

            if (!expectedToMiss.contains(wrapper)) {
                messages.add(msg);
                msgHandled++;
                new HandlerRunner(handler, msg);

                new MessageSentCountWaiter(mockAgent, msgHandled).assertGetsOkay("# msg " + msgHandled);

            }
        }

        if (expectedToMiss.size() != 0) {
            // Some wrappers do not answer so we trigger a timeout.
            handler.timeout();
        }

        new MessageSentCountWaiter(mockAgent, msgHandled).assertGetsOkay("# msg " + msgHandled + 1);

        /*
         * Now we check the results.
         */

        // The right TELL is sent to the client

        ResultConfiguration resConf = new ResultConfiguration(Arrays.asList(Field.AUTHOR));
        Message tellDocumentList = getDocumentResultTell(AGENT_NAME, "client", resConf,
                        messages.toArray(new Message[0]));
        new MessageSentWaiter(mockAgent, tellDocumentList).assertGetsOkay("Tell answer sent");

        Message raMessage = null;
        for (Message sent : mockAgent.getMessagesSent()) {
            if (sent.getTo().equals(REPOSITORY_NAME)) {
                raMessage = sent;
                break;
            }
        }
        Assert.assertTrue("Repository notification sent", raMessage != null);

        /*
         * Check if sources and misses are set correctly.
         */
        for (StoredDocument stored : ((DocumentQueryStoredTell) raMessage.getContent()).getResults()) {
            for (SourceInfo si : stored.getMisses()) {
                Assert.assertTrue("Misses contain " + si.getSourceID(),
                                expectedToMiss.contains(si.getSourceID().getDL()));
            }
            for (SourceInfo si : stored.getSources()) {
                Assert.assertFalse("Sources don't contain " + si.getSourceID(),
                                expectedToMiss.contains(si.getSourceID().getDL()));
            }
        }

    }


    /**
     * Test a query to multiple wrappers some of which don't answer. We test if
     * the results are returned if a "show now" is sent.
     */
    @Test
    public void testWorkASKTELL2WrappersOnlyOneAnswersThenShowNow1() {
        checkWorkASKTELL2WrappersOnlyOneAnswersThenShowNow("Dummy1", "Dummy2NOT");
    }


    @Test
    public void testWorkASKTELL2WrappersOnlyOneAnswersThenShowNow2() {
        checkWorkASKTELL2WrappersOnlyOneAnswersThenShowNow("Dummy1", "Dummy2NOT", "Dummy3");
    }


    @Test
    public void testWorkASKTELL2WrappersOnlyOneAnswersThenShowNow3() {
        checkWorkASKTELL2WrappersOnlyOneAnswersThenShowNow("Dummy1", "Dummy2NOT", "Dummy3NOT");
    }


    public void checkWorkASKTELL2WrappersOnlyOneAnswersThenShowNow(String... wrappers) {
        mockAgent.clearMessagesSent();
        handler = new TestableDocumentQueryHandler();
        checkMessageDelivery(handler, wrappers);

        Set<String> expectedToMiss = new HashSet<String>();
        for (String wrapper : wrappers) {
            if (wrapper.endsWith("NOT")) {
                expectedToMiss.add(wrapper);
            }
        }

        /*
         * Time passes by as the messages travel through the net and finally
         * reach their destinations.
         */

        mockAgent.clearMessagesSent();

        /*
         * Now the Dummy wrappers reply with some data, except for those whose
         * names are not in "expectedToMiss".
         */

        int msgHandled = 0;
        int msgIdx = 0;
        // Message[] messages = new Message[wrappers.length];
        List<Message> messages = new LinkedList<Message>();
        for (String wrapper : wrappers) {
            msgIdx++;
            Message msg = getDocumentStoredTell(wrapper, "SA", "author" + msgIdx);

            if (!expectedToMiss.contains(wrapper)) {
                messages.add(msg);
                msgHandled++;
                new HandlerRunner(handler, msg);

                new MessageSentCountWaiter(mockAgent, msgHandled).assertGetsOkay("# msg " + msgHandled);

            }
        }

        if (expectedToMiss.size() != 0) {
            // Some wrappers do not answer so we trigger a "show now"
            handler.setSendPartialResults(true);
            handler.halt();
        }

        new MessageSentCountWaiter(mockAgent, msgHandled).assertGetsOkay("# msg " + msgHandled + 1);

        /*
         * Now we check the results.
         */

        // The right TELL is sent to the client

        ResultConfiguration resConf = new ResultConfiguration(Arrays.asList(Field.AUTHOR));
        Message tellDocumentList = getDocumentResultTell(AGENT_NAME, CLIENT, resConf, messages.toArray(new Message[0]));
        new MessageSentWaiter(mockAgent, tellDocumentList).assertGetsOkay("Tell answer sent");

        Message raMessage = null;
        Message clientNotifyMessage = null;
        Message clientResultsMessage = null;
        for (Message sent : mockAgent.getMessagesSent()) {
            if (sent.getTo().equals(REPOSITORY_NAME)) {
                raMessage = sent;
            }
            else if (sent.getTo().equals(CLIENT) && (sent.getContent() instanceof DocumentQueryResultTell)) {
                clientResultsMessage = sent;
            }
            else if (sent.getTo().equals(CLIENT) && (sent.getContent() instanceof DocumentQueryInfoNotify)) {
                clientNotifyMessage = sent;
            }
        }
        Assert.assertTrue("Repository notification sent", raMessage != null);
        Assert.assertTrue("Client notification sent", clientNotifyMessage != null);
        Assert.assertTrue("Client results sent", (clientResultsMessage != null)
                        && (((DocumentQueryResultTell) clientResultsMessage.getContent()).getTotalDocCount() != 0));

        /*
         * Check if sources and misses are set correctly.
         */
        for (StoredDocument stored : ((DocumentQueryStoredTell) raMessage.getContent()).getResults()) {
            for (SourceInfo si : stored.getMisses()) {
                Assert.assertTrue("Misses contain " + si.getSourceID(),
                                expectedToMiss.contains(si.getSourceID().getDL()));
            }
            for (SourceInfo si : stored.getSources()) {
                Assert.assertFalse("Sources don't contain " + si.getSourceID(),
                                expectedToMiss.contains(si.getSourceID().getDL()));
            }
        }

    }


    private Message getDocumentAsk(String... wrapperNames) {
        DocumentQuery query = buildQuery(wrapperNames);

        List<Field> fields = new LinkedList<Field>();
        fields.add(Field.AUTHOR);
        ResultConfiguration resConf = new ResultConfiguration(fields);

        Message message = buildMessage(query, resConf);
        return message;
    }


    private Message getDocumentAskResConf(String... wrapperNames) {
        DocumentQuery query = buildQuery(wrapperNames);

        List<Field> fields = new LinkedList<Field>();
        fields.add(Field.AUTHOR);
        Sorting sorting = new Sorting();
        ResultConfiguration resConf = new ResultConfiguration(1, 1, fields, sorting);

        Message message = buildMessage(query, resConf);
        return message;
    }


    private DocumentQuery buildQuery(String... wrapperNames) {
        List<String> wrapperList = new LinkedList<String>();
        for (String wrapper : wrapperNames) {
            wrapperList.add(wrapper);
        }
        DocumentQuery query = new DocumentQuery(new DefaultQuery(new QueryNodeCompare(Field.AUTHOR, Predicate.EQ,
                        "fuhr")), wrapperList);
        return query;
    }


    private Message buildMessage(DocumentQuery query, ResultConfiguration resConf) {
        Message message = new Message();
        message.setFrom(CLIENT);
        message.setTo(mockAgent.agentName());
        DocumentQueryAsk content = new DocumentQueryAsk(query, resConf);
        content.setMaxDurationMs(RUNTIME_MS);
        message.setContent(content);
        message.setRequestId(REQID);
        return message;
    }


    private Message getDocumentStoredTell(String from, String to, String... authorNames) {
        Message message = new Message();
        message.setFrom(from);
        message.setTo(to);
        StoredDocumentList data = new StoredDocumentList();

        for (String authorName : authorNames) {
            Document document = new TextDocument();
            Person author = new Person(authorName);
            PersonList authors = new PersonList();
            authors.add(author);
            document.setAuthorList(authors);
            document.setTitle(UUID.randomUUID().toString());
            document.setYear(2000);

            StoredDocument result = new StoredDocument(document);
            result.addSource(new SourceInfo(new SourceID(from, "web"), "details"));
            data.add(result);
        }

        DocumentQueryStoredTell content = new DocumentQueryStoredTell(data);
        message.setContent(content);
        message.setRequestId(REQID);
        return message;
    }


    private Message getDocumentStoredTellDefective(String from, String to, String... authorNames) {
        Message message = new Message();
        message.setFrom(from);
        message.setTo(to);
        StoredDocumentList data = new StoredDocumentList();

        for (@SuppressWarnings("unused") String authorName : authorNames) {
            Document document = new TextDocument();
            document.setTitle("title");
            document.setYear(2000);

            StoredDocument result = new StoredDocument(document);
            result.addSource(new SourceInfo(new SourceID(from, "web"), "details"));
            data.add(result);
        }

        DocumentQueryStoredTell content = new DocumentQueryStoredTell(data);
        message.setContent(content);
        message.setRequestId(REQID);
        return message;
    }


    private Message getDocumentResultTell(String from, String to, ResultConfiguration resultConfig, Message... msgs) {
        Message message = new Message();
        message.setFrom(from);
        message.setTo(to);
        ResultDocumentList data = new ResultDocumentList();

        DocumentListFilter f = new DocumentListFilter(resultConfig);

        for (Message orig : msgs) {
            StoredDocumentList list = ((DocumentQueryStoredTell) orig.getContent()).getResults();

            for (StoredDocument stored : list) {

                ResultDocument result = new ResultDocument(stored.getDocument());

                result.setRsv(0.0);
                for (SourceInfo s : stored.getSources()) {
                    result.addSource(s.getSourceID().getDL());
                }
                data.add(result);
            }
        }

        data = f.process(data);

        DocumentQueryResultTell content = new DocumentQueryResultTell(data);
        message.setContent(content);
        message.setRequestId(REQID);
        return message;
    }


    private Message getDocumentStoredTell(String from, String to, Message... msgs) {
        Message message = new Message();
        message.setFrom(from);
        message.setTo(to);
        StoredDocumentList data = new StoredDocumentList();

        for (Message orig : msgs) {
            StoredDocumentList list = ((DocumentQueryStoredTell) orig.getContent()).getResults();
            data.merge(list);
        }
        DocumentQueryStoredTell content = new DocumentQueryStoredTell(data);
        message.setContent(content);
        message.setRequestId(REQID);
        return message;
    }


    /**
     * Checks if the ACK message plus the MedadataQueryAsk messages to the given
     * wrappers are sent.
     * 
     * @param handler
     *            the handler to check
     * @param dls
     *            the DLs
     */
    private void checkMessageDelivery(DocumentQueryHandler handler, String... dls) {
        Message message = getDocumentAsk(dls);
        checkAskHandlingAndForwarding(handler, message, 1 + dls.length, dls);
    }


    private void checkMessageDeliveryResultConf(DocumentQueryHandler handler, String... wrappers) {
        Message message = getDocumentAskResConf(wrappers);
        checkAskHandlingAndForwarding(handler, message, 1 + wrappers.length, wrappers);
    }


    /**
     * Checks if the handler correctly handles the original query message.
     * <p>
     * That would mean:
     * <ul>
     * <li>Forwarding the query to the given wrappers</li>
     * <li>Notifying the client that the search has been started</li>
     * </ul>
     * 
     * @param handler
     *            the handler to check
     * @param message
     *            the original query message
     * @param messagesSent
     *            the number of messages sent in the first run before any
     *            wrapper answers
     * @param dls
     *            the wrappers that have been asked
     */
    private void checkAskHandlingAndForwarding(DocumentQueryHandler handler, Message message, int messagesSent,
                    String... dls) {
        handler.init(message.getRequestId(), mockAgent);
        handler.addMessageToQueue(message);

        new HandlerRunner(handler, message);

        new MessageSentCountWaiter(mockAgent, messagesSent).assertGetsAndStaysOkay("1 # msg sent");
        Assert.assertEquals("2 # msg sent", messagesSent, mockAgent.getMessagesSent().size());

        // Check ACK

        DocumentQueryInfoNotify notify = new DocumentQueryInfoNotify(message.getRequestId(), 0);
        Message ackMsg = new Message(mockAgent.agentName(), "client", notify, "reqid");
        new MessageSentWaiter(mockAgent, ackMsg).assertGetsOkay("ACK sent");
        Assert.assertTrue("ACK sent", mockAgent.getMessagesSent().contains(ackMsg));

        // Check forwarded ASKs

        ResultConfiguration resConf = new ResultConfiguration();
        DocumentQuery query = new DocumentQuery(new DefaultQuery(new QueryNodeCompare(Field.AUTHOR, Predicate.EQ,
                        "fuhr")), Arrays.asList(dls));

        DocumentQueryAsk mqAsk = new DocumentQueryAsk(query, resConf);
        mqAsk.setMaxDurationMs(RUNTIME_MS);
        for (String dl : dls) {
            Message askWrapper = new Message(mockAgent.agentName(), dl, mqAsk, "reqid");
            new MessageReceivedWaiter(mockAgent, askWrapper).assertGetsOkay("ASK " + dl);
            Assert.assertTrue("ASK " + dl, mockAgent.getMessagesSent().contains(askWrapper));
        }
    }


    class HandlerRunner implements Runnable {

        private DocumentQueryHandler handler;

        private Message message;


        public HandlerRunner(DocumentQueryHandler handler, Message message) {
            this.handler = handler;
            this.message = message;
            Executor executor = Executors.newSingleThreadExecutor();
            executor.execute(this);
        }


        @Override
        public void run() {
            handler.work(message);
        }
    }

}
