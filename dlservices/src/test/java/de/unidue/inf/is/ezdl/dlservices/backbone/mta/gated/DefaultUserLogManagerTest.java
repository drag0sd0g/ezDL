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

package de.unidue.inf.is.ezdl.dlservices.backbone.mta.gated;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import de.unidue.inf.is.ezdl.dlbackend.AbstractBackendTestBase;
import de.unidue.inf.is.ezdl.dlbackend.message.Message;
import de.unidue.inf.is.ezdl.dlcore.DocumentFactory;
import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;
import de.unidue.inf.is.ezdl.dlcore.data.query.DefaultQuery;
import de.unidue.inf.is.ezdl.dlcore.data.query.DocumentQuery;
import de.unidue.inf.is.ezdl.dlcore.data.query.ResultConfiguration;
import de.unidue.inf.is.ezdl.dlcore.data.query.ResultDocument;
import de.unidue.inf.is.ezdl.dlcore.data.query.ResultDocumentList;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeBool;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeBool.NodeType;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeCompare;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeCompare.Predicate;
import de.unidue.inf.is.ezdl.dlcore.data.wrappers.FrontendWrapperInfo;
import de.unidue.inf.is.ezdl.dlcore.log.SessionType;
import de.unidue.inf.is.ezdl.dlcore.message.content.AvailableWrappersAsk;
import de.unidue.inf.is.ezdl.dlcore.message.content.AvailableWrappersTell;
import de.unidue.inf.is.ezdl.dlcore.message.content.CancelSearchNotify;
import de.unidue.inf.is.ezdl.dlcore.message.content.DocumentDetailsAsk;
import de.unidue.inf.is.ezdl.dlcore.message.content.DocumentQueryAsk;
import de.unidue.inf.is.ezdl.dlcore.message.content.DocumentQueryResultTell;
import de.unidue.inf.is.ezdl.dlcore.message.content.LoginTell;
import de.unidue.inf.is.ezdl.dlcore.message.content.MessageContent;
import de.unidue.inf.is.ezdl.dlcore.message.content.TextMessageNotify;
import de.unidue.inf.is.ezdl.dlcore.message.content.TextMessageNotify.Priority;
import de.unidue.inf.is.ezdl.dlcore.message.content.UserLogNotify;
import de.unidue.inf.is.ezdl.dlcore.security.Privilege;
import de.unidue.inf.is.ezdl.dlservices.backbone.mta.gated.userlog.DefaultUserLogManager;
import de.unidue.inf.is.ezdl.dlservices.backbone.mta.gated.userlog.UserLogManager;



/**
 * Tests the {@link TextMessageManager}.
 * 
 * @author mjordan
 */
public class DefaultUserLogManagerTest extends AbstractBackendTestBase {

    private static final String USERLOGAGENTNAME = "userlogagent";

    private MockGatedMTA mta;
    private UserLogManager manager;


    private class Param {

        String key;
        String value;


        Param(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }


    private LoginTell userInfo;
    private ConnectionInfo connectionInfo;


    @Before
    public void setup() {
        mta = new MockGatedMTA();
        mta.init("mta", new Properties());
        manager = new DefaultUserLogManager(mta, USERLOGAGENTNAME);
        userInfo = new LoginTell("patient0", "first", "last", "sessionid1", Collections.<Privilege> emptySet());
        connectionInfo = new ConnectionInfo("connectionid1", userInfo, SessionType.STANDARD);
    }


    @Test
    public void testLogLogin() {
        checkLogLogin("1", "login1", "session", "cid", new Param("connection", "cid"), new Param("login", "login1"));
        checkLogLogin("2", "login2", "session2", "cid2", new Param("connection", "cid2"), new Param("login", "login2"));
    }


    @Test
    public void testLogLogout() {
        checkLogLogout("1", "login1", "session", "cid");
        checkLogLogout("2", "login2", "session2", "cid2");
    }


    @Test
    public void testLogDocumentQueryAsk() {
        checkLogDocumentQueryAsk("1", "Fuhr");
        checkLogDocumentQueryAsk("2", "Fuhr", "Kriewel");
        checkLogDocumentQueryAsk("3", "Fuhr", "Kriewel", "Belkin");
    }


    @Test
    public void testLogCancelSearchNotify() {
        checkLogCancelSearchNotify("1", "a");
        checkLogCancelSearchNotify("2",
                        "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
        checkLogCancelSearchNotify("3", "c");
    }


    @Test
    public void testLogDocumentQueryResultTell() {
        checkLogDocumentQueryResultTell("1", Arrays.asList("1"), new Param("item", "1"), new Param("rsv", "1.0"));
        checkLogDocumentQueryResultTell("2", Arrays.asList("1", "2"), new Param("item", "1"), new Param("rsv", "1.0"),
                        new Param("item", "2"), new Param("rsv", "0.5"));
        checkLogDocumentQueryResultTell("3", Arrays.asList("1", "2", "4"), new Param("item", "1"), new Param("rsv",
                        "1.0"), new Param("item", "2"), new Param("rsv", "0.5"), new Param("item", "4"), new Param(
                        "rsv", "0.25"));
    }


    @Test
    public void testLogDocumentQueryResultTellParameterOrder() {
        checkLogDocumentQueryResultTellOrder("1 item", Arrays.asList("1"));
        checkLogDocumentQueryResultTellOrder("3 items sorted", Arrays.asList("1", "2", "4"));
        checkLogDocumentQueryResultTellOrder("3 items unsorted", Arrays.asList("1", "5", "4"));
        checkLogDocumentQueryResultTellOrder("many items unsorted", Arrays.asList("1", "2", "4", "2", "6", "9", "99",
                        "65", "0", "11", "43", "81", "21", "22", "27", "26"));
    }


    private void checkLogDocumentQueryResultTellOrder(String label, List<String> oids) {
        final double factor = 0.01;
        mta.clear();
        ResultDocumentList results = new ResultDocumentList();
        for (String oid : oids) {
            ResultDocument doc = DocumentFactory.createResultDocument("abstract", "title", 2000, "a");
            doc.setOid(oid);
            doc.setRsv(factor * Double.parseDouble(oid));
            results.add(doc);
        }
        DocumentQueryResultTell contentIn = new DocumentQueryResultTell(results);

        manager.logMessage(connectionInfo.getConnectionId(), userInfo, contentIn);
        List<Message> msgSent = mta.getSentMessages();

        Assert.assertEquals(label + ": msg count", 1, msgSent.size());

        Message msg = msgSent.get(0);
        Assert.assertTrue(label + ": msg content type", msg.getContent() instanceof UserLogNotify);

        UserLogNotify notify = (UserLogNotify) msg.getContent();
        List<String> items = notify.getParameters("item");
        List<String> rsvs = notify.getParameters("rsv");

        Iterator<String> oidsI = oids.iterator();
        Iterator<String> itemsI = items.iterator();
        Iterator<String> rsvsI = rsvs.iterator();

        while (oidsI.hasNext() && itemsI.hasNext() && rsvsI.hasNext()) {
            String oidsItem = oidsI.next();
            String itemsItem = itemsI.next();
            String rsvsItem = rsvsI.next();
            Assert.assertEquals(label + ": oids order okay", oidsItem, itemsItem);
            Assert.assertEquals(label + ": rsvs order okay", String.valueOf(factor * Double.parseDouble(oidsItem)),
                            rsvsItem);
        }
    }


    @Test
    public void testLogAvailableWrappersAsk() {
        checkLogAvailableWrappersAsk("1");
    }


    @Test
    public void testLogAvailableWrappersTell() {
        checkLogAvailableWrappersTell("1", Arrays.asList("id1"), new Param("wrapper", "id1"));
        checkLogAvailableWrappersTell("2", Arrays.asList("id1", "id2"), new Param("wrapper", "id1"), new Param(
                        "wrapper", "id2"));
    }


    @Test
    public void testLogDocumentDetailsAsk() {
        checkLogDocumentDetailsAsk("1", Arrays.asList("id1"), new Param("oid", "id1"));
        checkLogDocumentDetailsAsk("2", Arrays.asList("id1", "id2"), new Param("oid", "id1"), new Param("oid", "id2"));
        checkLogDocumentDetailsAsk("3", Arrays.asList("id1", "id2", "id3"), new Param("oid", "id1"), new Param("oid",
                        "id2"), new Param("oid", "id3"));
    }


    @Test
    public void testLogTextMessageNotify() {
        checkLogTextMessageNotify("1");
    }


    @Test
    public void testSequenceNumbers() {
        final int MAX = 100;
        final int tests = 5;
        long clientTime = 0;
        long backendTime = 0;
        for (int i = 0; (i < MAX); i++) {
            switch (i % tests) {
                case 0: {
                    checkLogTextMessageNotify("1");
                    break;
                }
                case 1: {
                    checkLogAvailableWrappersAsk("1");
                    break;
                }
                case 2: {
                    checkLogAvailableWrappersTell("1", Arrays.asList("id1"), new Param("wrapper", "id1"));
                    break;
                }
                case 3: {
                    checkLogDocumentDetailsAsk("1", Arrays.asList("id1"), new Param("oid", "id1"));
                    break;
                }
                case 4: {
                    checkLogDocumentQueryAsk("1", "Fuhr");
                    break;
                }
                default: {

                }
            }
            UserLogNotify notify = ((UserLogNotify) mta.getSentMessages().get(0).getContent());
            int seqN = notify.getSequenceNumber();
            Assert.assertEquals("sequence number " + i, i, seqN);
            Assert.assertTrue("client time okay 1", notify.getClientTimestamp() != 0);
            Assert.assertTrue("backend time okay 1", notify.getBackendTimestamp() != 0);
            Assert.assertTrue("client time okay 2", clientTime <= notify.getClientTimestamp());
            Assert.assertTrue("backend time okay 2", backendTime <= notify.getBackendTimestamp());
            clientTime = notify.getClientTimestamp();
            backendTime = notify.getBackendTimestamp();
        }
    }


    private void checkLogLogin(String label, String login, String sessionId, String connectionId, Param... params) {
        mta.clear();
        LoginTell userInfo = new LoginTell(login, "first", "last", sessionId, Collections.<Privilege> emptySet());
        ConnectionInfo info = new ConnectionInfo(connectionId, userInfo, SessionType.STANDARD);

        manager.logLogin(info);

        List<Message> msgSent = mta.getSentMessages();

        Assert.assertEquals("1 msg sent", 1, msgSent.size());

        Message msg = msgSent.get(0);
        MessageContent content = msg.getContent();

        Assert.assertTrue(label + ": content correct", content instanceof UserLogNotify);

        UserLogNotify notify = (UserLogNotify) content;

        commonChecks(label, notify, sessionId, "session_start");

        checkParams(label, notify, params);
    }


    private void checkLogLogout(String label, String login, String sessionId, String connectionId) {
        mta.clear();
        LoginTell userInfo = new LoginTell(login, "first", "last", sessionId, Collections.<Privilege> emptySet());
        ConnectionInfo info = new ConnectionInfo(connectionId, userInfo, SessionType.STANDARD);

        manager.logLogout(info);

        List<Message> msgSent = mta.getSentMessages();

        Assert.assertEquals("1 msg sent", 1, msgSent.size());

        Message msg = msgSent.get(0);
        MessageContent content = msg.getContent();

        Assert.assertTrue(label + ": content correct", content instanceof UserLogNotify);

        UserLogNotify notify = (UserLogNotify) content;

        commonChecks(label, notify, sessionId, "session_end");
    }


    private void checkLogDocumentQueryAsk(String label, String... terms) {
        mta.clear();
        QueryNodeBool queryNodeBool = new QueryNodeBool(NodeType.AND);
        for (String term : terms) {
            queryNodeBool.addChild(new QueryNodeCompare(Field.AUTHOR, Predicate.EQ, term));
        }
        DocumentQuery query = new DocumentQuery(new DefaultQuery(queryNodeBool), Arrays.asList("dla", "dlb"));
        DocumentQueryAsk contentIn = new DocumentQueryAsk(query, new ResultConfiguration());

        manager.logMessage(connectionInfo.getConnectionId(), userInfo, contentIn);

        List<Message> msgSent = mta.getSentMessages();

        Assert.assertEquals("1 msg sent", 1, msgSent.size());

        Message msg = msgSent.get(0);
        MessageContent content = msg.getContent();

        Assert.assertTrue(label + ": content correct", content instanceof UserLogNotify);

        UserLogNotify notify = (UserLogNotify) content;

        commonChecks(label, notify, "documentqueryask");
        Assert.assertEquals(label + ": only 1 query", 1, notify.getParameters("query").size());
        Assert.assertTrue(label + ": query", notify.getParameters("query").get(0).contains("Fuhr"));
    }


    private void checkLogCancelSearchNotify(String label, String queryId) {
        mta.clear();
        CancelSearchNotify contentIn = new CancelSearchNotify(queryId);
        manager.logMessage(connectionInfo.getConnectionId(), userInfo, contentIn);
        List<Message> msgSent = mta.getSentMessages();

        Assert.assertEquals("1 msg sent", 1, msgSent.size());

        Message msg = msgSent.get(0);
        MessageContent content = msg.getContent();

        Assert.assertTrue(label + ": content correct", content instanceof UserLogNotify);

        UserLogNotify notify = (UserLogNotify) content;

        commonChecks(label, notify, "cancelsearch");

        Assert.assertEquals(label + ": only 1 query to cancel", 1, notify.getParameters("queryid").size());
        Assert.assertTrue(label + ": query", notify.getParameters("queryid").get(0).contains(queryId));
    }


    private void checkLogDocumentQueryResultTell(String label, List<String> oids, Param... params) {
        mta.clear();
        ResultDocumentList results = new ResultDocumentList();
        for (String oid : oids) {
            ResultDocument doc = DocumentFactory.createResultDocument("abstract", "title", 2000, "a");
            doc.setOid(oid);
            doc.setRsv(1 / Double.parseDouble(oid));
            results.add(doc);
        }
        DocumentQueryResultTell contentIn = new DocumentQueryResultTell(results);

        manager.logMessage(connectionInfo.getConnectionId(), userInfo, contentIn);
        List<Message> msgSent = mta.getSentMessages();

        Assert.assertEquals("1 msg sent", 1, msgSent.size());

        Message msg = msgSent.get(0);
        MessageContent content = msg.getContent();

        Assert.assertTrue(label + ": content correct", content instanceof UserLogNotify);

        UserLogNotify notify = (UserLogNotify) content;

        commonChecks(label, notify, "documentquerytell");

        checkParams(label, notify, params);
    }


    private void checkLogAvailableWrappersAsk(String label) {
        mta.clear();
        AvailableWrappersAsk contentIn = new AvailableWrappersAsk(Locale.GERMAN);

        manager.logMessage(connectionInfo.getConnectionId(), userInfo, contentIn);
        List<Message> msgSent = mta.getSentMessages();

        Assert.assertEquals("1 msg sent", 1, msgSent.size());

        Message msg = msgSent.get(0);
        MessageContent content = msg.getContent();

        Assert.assertTrue(label + ": content correct", content instanceof UserLogNotify);

        UserLogNotify notify = (UserLogNotify) content;

        commonChecks(label, notify, "availablewrappersask");
    }


    private void checkLogAvailableWrappersTell(String label, List<String> ids, Param... params) {
        mta.clear();
        List<FrontendWrapperInfo> wrapperInfo = new ArrayList<FrontendWrapperInfo>();
        for (String id : ids) {
            FrontendWrapperInfo info = new FrontendWrapperInfo();
            info.setCategory("category");
            info.setCategoryId("catid");
            info.setDescription("description");
            info.setId(id);
            info.setRemoteName("remotename");
            wrapperInfo.add(info);
        }
        AvailableWrappersTell contentIn = new AvailableWrappersTell(wrapperInfo);
        manager.logMessage(connectionInfo.getConnectionId(), userInfo, contentIn);
        List<Message> msgSent = mta.getSentMessages();

        Assert.assertEquals("1 msg sent", 1, msgSent.size());

        Message msg = msgSent.get(0);
        MessageContent content = msg.getContent();

        Assert.assertTrue(label + ": content correct", content instanceof UserLogNotify);

        UserLogNotify notify = (UserLogNotify) content;

        commonChecks(label, notify, "availablewrapperstell");

        checkParams(label, notify, params);
    }


    private void checkLogDocumentDetailsAsk(String label, List<String> oids, Param... params) {
        mta.clear();
        DocumentDetailsAsk contentIn = new DocumentDetailsAsk(oids);
        manager.logMessage(connectionInfo.getConnectionId(), userInfo, contentIn);
        List<Message> msgSent = mta.getSentMessages();

        Assert.assertEquals("1 msg sent", 1, msgSent.size());

        Message msg = msgSent.get(0);
        MessageContent content = msg.getContent();

        Assert.assertTrue(label + ": content correct", content instanceof UserLogNotify);

        UserLogNotify notify = (UserLogNotify) content;

        commonChecks(label, notify, "documentdetailsask");

        checkParams(label, notify, params);
    }


    private void checkLogTextMessageNotify(String label) {
        mta.clear();
        TextMessageNotify contentIn = new TextMessageNotify(Priority.CHAT, "title", "content", "from");
        manager.logMessage(connectionInfo.getConnectionId(), userInfo, contentIn);
        List<Message> msgSent = mta.getSentMessages();

        Assert.assertEquals("1 msg sent", 1, msgSent.size());

        Message msg = msgSent.get(0);
        MessageContent content = msg.getContent();

        Assert.assertTrue(label + ": content correct", content instanceof UserLogNotify);

        UserLogNotify notify = (UserLogNotify) content;

        commonChecks(label, notify, "textmessage");

        checkParams(label, notify, new Param("content", "content"), new Param("title", "title"), new Param("from",
                        "from"), new Param("prio", "CHAT"));
    }


    private void commonChecks(String label, UserLogNotify notify, String expectedName) {
        commonChecks(label, notify, userInfo.getSessionId(), expectedName);
    }


    private void commonChecks(String label, UserLogNotify notify, String expectedSID, String expectedName) {
        Assert.assertEquals(label + ": event name okay", expectedName, notify.getEventName());
        Assert.assertEquals(label + ": session ID okay", expectedSID, notify.getSessionId());
        Assert.assertTrue(label + ": time stamp okay-ish", notify.getBackendTimestamp() > 0);
    }


    private void checkParams(String label, UserLogNotify notify, Param... params) {
        for (Param p : params) {
            List<String> values = notify.getParameters(p.key);
            boolean valueFound = false;
            for (String value : values) {
                if (value.equals(p.value)) {
                    valueFound = true;
                    break;
                }
            }
            Assert.assertTrue(label + ": parameter " + p.key + " with value " + p.value + " found", valueFound);
        }
    }

}
