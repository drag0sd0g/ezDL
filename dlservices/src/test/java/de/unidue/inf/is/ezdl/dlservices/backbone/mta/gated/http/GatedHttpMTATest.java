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

package de.unidue.inf.is.ezdl.dlservices.backbone.mta.gated.http;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import de.unidue.inf.is.ezdl.dlbackend.AbstractAgentTestBase;
import de.unidue.inf.is.ezdl.dlbackend.agent.Agent;
import de.unidue.inf.is.ezdl.dlbackend.agent.connectors.AgentConnector;
import de.unidue.inf.is.ezdl.dlbackend.message.Message;
import de.unidue.inf.is.ezdl.dlbackend.mock.MockAgentConnector;
import de.unidue.inf.is.ezdl.dlbackend.mock.MockORB;
import de.unidue.inf.is.ezdl.dlcore.ErrorConstants;
import de.unidue.inf.is.ezdl.dlcore.message.MTAMessage;
import de.unidue.inf.is.ezdl.dlcore.message.MTAMessageCoder;
import de.unidue.inf.is.ezdl.dlcore.message.content.AliveAsk;
import de.unidue.inf.is.ezdl.dlcore.message.content.AliveTell;
import de.unidue.inf.is.ezdl.dlcore.message.content.ErrorNotify;
import de.unidue.inf.is.ezdl.dlcore.message.content.LoginAsk;
import de.unidue.inf.is.ezdl.dlcore.message.content.LoginTell;
import de.unidue.inf.is.ezdl.dlcore.message.content.LogoutAsk;
import de.unidue.inf.is.ezdl.dlcore.message.content.MessageContent;
import de.unidue.inf.is.ezdl.dlcore.message.content.TextMessageNotify;
import de.unidue.inf.is.ezdl.dlcore.message.content.TextMessageNotify.Priority;
import de.unidue.inf.is.ezdl.dlcore.misc.ConnectionFailedException;
import de.unidue.inf.is.ezdl.dlcore.misc.TimeoutException;
import de.unidue.inf.is.ezdl.dlcore.security.Privilege;
import de.unidue.inf.is.ezdl.dlcore.utils.ClosingUtils;
import de.unidue.inf.is.ezdl.dlcore.utils.HttpMessagingUtils;
import de.unidue.inf.is.ezdl.dlservices.backbone.mta.gated.ConnectionInfo;
import de.unidue.inf.is.ezdl.dlservices.backbone.mta.gated.MockTextMessageManager;
import de.unidue.inf.is.ezdl.dlservices.backbone.mta.gated.TextMessageManager;
import de.unidue.inf.is.ezdl.dlservices.backbone.mta.gated.handlers.LoginHandler;



/**
 * If any of these tests fail, the protocol version probably has to be increased
 * to make sure that clients comply with the changed protocol.
 * 
 * @author mjordan
 */
public class GatedHttpMTATest extends AbstractAgentTestBase {

    private static final boolean[] WRITE_ERRORS_ALL = new boolean[] {
                    true, true, true, true, true, true, true
    };
    private static final boolean[] WRITE_ERRORS_NONE = new boolean[] {
                    false, false, false, false, false, false, false
    };

    private static final boolean[] WRITE_ERRORS_SOME = new boolean[] {
                    false, false, true, false, true, false, false
    };
    private static final String LOGIN = "login";
    private static final String WRONG_LOGIN = "wrong";
    private static final String TIMEOUT_LOGIN = "timeout";

    private static final String MOCKUSERAGENT = "mockuseragent";
    private static final String MOCKSESSIONID = "mocksessionid";
    private static final String TESTMTA_NAME = "testmta";
    private static final int MTA_PORT_START = 9876;
    private static final String MTA_HOST = "localhost";

    private static int mtaPort = MTA_PORT_START;

    private MockORB orb = new MockORB();
    private TestableGatedHttpMTA mta;


    /**
     * The {@link MockLoginHandler} is used to mock the User Agent away so that
     * login processes can be performed without actually sending messages back
     * and forth.
     * 
     * @author mjordan
     */
    private class MockLoginHandler extends LoginHandler {

        public MockLoginHandler(GatedHttpMTA agent) {
            super(agent);
        }


        private LoginTell getLoginTell(String login) {
            String first = "mockfirstname";
            String last = "mocklastname";
            String sid = MOCKSESSIONID;
            Set<Privilege> privileges = new HashSet<Privilege>();
            LoginTell content = new LoginTell(login, first, last, sid, privileges);
            content.setLastLoginTime(1l);
            return content;
        }


        @Override
        protected Message askUserAgentForAuthentication(Agent agent, Message loginMsg) throws TimeoutException {
            String login = ((LoginAsk) loginMsg.getContent()).getLogin();
            MessageContent tell = null;

            if (login.equals(TIMEOUT_LOGIN)) {
                throw new TimeoutException();
            }
            else if (login.equals(WRONG_LOGIN)) {
                tell = new ErrorNotify(ErrorConstants.LOGIN_WRONG_PASSWORD);
            }
            else {
                tell = getLoginTell(login);
            }

            Message message = loginMsg.tell(tell);

            return message;
        }

    }


    /**
     * This is to facilitate testing the MTA.
     * 
     * @author mjordan
     */
    private class TestableGatedHttpMTA extends GatedHttpMTA {

        List<String> strRecFromHttp = new LinkedList<String>();
        List<MTAMessage> msgUnderstoodFromHttp = new LinkedList<MTAMessage>();
        List<Message> msgSent = new LinkedList<Message>();
        List<MTAMessage> msgForwarded = new LinkedList<MTAMessage>();
        List<Message> msgToHttp = new LinkedList<Message>();
        Set<String> terminatedConnections = new HashSet<String>();
        MockTextMessageManager tmm;


        @Override
        public void initOnline() {
            getLogger().debug("Pretending to act");
            initServer(MTA_HOST, mtaPort);
            initTextMessageManager();
        }


        @Override
        protected void initTextMessageManager() {
            tmm = new MockTextMessageManager(this);
        }


        @Override
        public void handleToClient(String connectionId, Message unhandledMessage) {
            msgToHttp.add(unhandledMessage);
        }


        @Override
        protected LoginHandler getLoginHandlerInstance() {
            return new MockLoginHandler(this);
        }


        @Override
        public void halt() {
            getLogger().debug("Pretending to halt");
            getServer().halt();
        }


        @Override
        public boolean isHalted() {
            return getServer().isHalted();
        }


        @Override
        public boolean handleFromClient(String connectionId, String messageStr) {
            String record = connectionId + "|" + messageStr;
            strRecFromHttp.add(record);
            getLogger().debug("MTA FROM HTTP: " + record);
            return super.handleFromClient(connectionId, messageStr);
        }


        @Override
        protected void forwardMessageFromClient(String connectionId, MTAMessage message) {
            msgForwarded.add(message);
            super.forwardMessageFromClient(connectionId, message);
        }


        @Override
        protected MTAMessage getMessageFromClient(String connectionId, String messageStr) {
            MTAMessage message = super.getMessageFromClient(connectionId, messageStr);
            if (message != null) {
                msgUnderstoodFromHttp.add(message);
            }
            return message;
        }


        @Override
        public void send(Message message) {
            msgSent.add(message);
            super.send(message);
        }


        public List<String> getReceivedStrings() {
            return strRecFromHttp;
        }


        public List<MTAMessage> getUnderstoodMessages() {
            return msgUnderstoodFromHttp;
        }


        @SuppressWarnings("unused")
        public List<Message> getMessagesSent() {
            return msgSent;
        }


        public List<MTAMessage> getMessagesForwarded() {
            return msgForwarded;
        }


        public List<Message> getMessagesToHttp() {
            return msgToHttp;
        }


        @Override
        public String getLoginLogoutReceiverName() {
            return MOCKUSERAGENT;
        }


        @Override
        public void terminateConnection(String connectionId) {
            terminatedConnections.add(connectionId);
            super.terminateConnection(connectionId);

        }


        @Override
        public void terminateConnection(String connectionId, Message farewellMessage) {
            terminatedConnections.add(connectionId);
            super.terminateConnection(connectionId, farewellMessage);
        }


        public boolean connectionTerminated() {
            return terminatedConnections.size() != 0;
        }


        List<MTAMessage> sentToHttpMsgs = new LinkedList<MTAMessage>();
        List<String> sentToHttpConIds = new LinkedList<String>();
        List<MTAMessage> sentTextToAllMsgs = new LinkedList<MTAMessage>();


        @Override
        public void sendToClient(String connectionId, MTAMessage mtaMessage) {
            sentToHttpConIds.add(connectionId);
            sentToHttpMsgs.add(mtaMessage);
        }


        @Override
        public void sendTextToAll(MTAMessage mtaMessage) {
            sentTextToAllMsgs.add(mtaMessage);
        }


        @SuppressWarnings("unused")
        List<String> getSentToHttpConIds() {
            return sentToHttpConIds;
        }


        List<MTAMessage> getSentToHttpMsgs() {
            return sentToHttpMsgs;
        }


        /**
         * @return the sentTextToAllMsgs
         */
        public List<MTAMessage> getSentTextToAllMsgs() {
            return sentTextToAllMsgs;
        }


        @SuppressWarnings("unused")
        void clear() {
            sentToHttpMsgs = new LinkedList<MTAMessage>();
            sentToHttpConIds = new LinkedList<String>();
            sentTextToAllMsgs = new LinkedList<MTAMessage>();
        }


        @Override
        public TextMessageManager getTextManager() {
            return tmm;
        }


        @Override
        public Map<String, ConnectionInfo> getConnections() {
            return super.getConnections();
        }
    }


    public void setup(Properties properties) {
        mtaPort++;
        mta = new TestableGatedHttpMTA();
        mta.init(TESTMTA_NAME, properties);
        AgentConnector connector = new MockAgentConnector(orb, mta);
        mta.setConnector(connector);

        try {
            mta.goOnline();
        }
        catch (ConnectionFailedException e) {
            e.printStackTrace();
        }

        new MTAOnlineAssertWaiter().assertGetsOkay("MTA online", 5000, 100);

        sleep(1000);
        getLogger().info("====== Setup finished");
    }


    @After
    public void tearDown() {
        getLogger().info("====== Tearing down");
        getLogger().info("Strings received: " + mta.getReceivedStrings());
        getLogger().info("Messages received: " + mta.getUnderstoodMessages().toString());
        mta.halt();
        (new AssertWaiter() {

            @Override
            protected boolean isConditionMet() {

                return mta.isHalted();
            }
        }).assertGetsOkay("MTA halted", 5000, 200);
        mta = null;
        sleep(1000);
        getLogger().info("Continuing with next test");
    }


    /**
     * Tests the consequences of sending a {@link LoginAsk} message to the MTA
     * via HTTP.
     */
    @Test
    public void loginTest() {
        setup(new Properties());
        Assert.assertTrue("mta online", mta.isOnline());

        Socket socket = null;
        try {
            socket = new Socket(MTA_HOST, mtaPort);
            socket.setSoTimeout(500);
            write(socket, "GET blabla\r\nHost: blabla\r\n\r\n");
            getLogger().info("read from socket after GET chunk: " + read(socket));
            writeln(socket, getLoginMessageString());

            new ConnectionWaiter(LOGIN).assertGetsOkay("connection there");
        }
        catch (UnknownHostException e) {
            getLogger().error("Unknown host: " + MTA_HOST, e);
            Assert.fail("mta http server not at host localhost");
        }
        catch (IOException e) {
            getLogger().error("IOException", e);
            Assert.fail("mta http server not at port " + mtaPort);
        }
        finally {
            ClosingUtils.close(socket);
        }
    }


    /**
     * Tests the situation where a client tries to log in but the message to the
     * user agent is not answered.
     */
    @Test
    public void testLoginButUserAgentDoesntAnswer() {
        setup(new Properties());
        Assert.assertTrue("mta online", mta.isOnline());

        Socket socket = null;
        try {
            socket = new Socket(MTA_HOST, mtaPort);
            socket.setSoTimeout(500);
            write(socket, "GET blabla\r\nHost: blabla\r\n\r\n");
            getLogger().info("read from socket after GET chunk: " + read(socket));
            writeln(socket, getLoginMessageString(TIMEOUT_LOGIN));

            new HttpErrorMessageWaiter(ErrorConstants.SERVER_NOT_READY).assertGetsOkay("connection there");
        }
        catch (UnknownHostException e) {
            getLogger().error("Unknown host: " + MTA_HOST, e);
            Assert.fail("mta http server not at host localhost");
        }
        catch (IOException e) {
            getLogger().error("IOException", e);
            Assert.fail("mta http server not at port " + mtaPort);
        }
        finally {
            ClosingUtils.close(socket);
        }
    }


    /**
     * Tests if a serialized message written to a socket connected to the MTA
     * socket is correctly received.
     */
    @Test
    public void writeTest() throws IOException {
        setup(new Properties());
        Assert.assertTrue("mta online", mta.isOnline());
        MessageContent content = new LoginAsk("login", "secret");
        MTAMessage message = new MTAMessage(content, "rid");
        String encodedMessage = MTAMessageCoder.getInstance().encode(message);

        Socket socket = null;
        try {
            socket = new Socket(MTA_HOST, mtaPort);
            socket.setSoTimeout(500);
            write(socket, "GET blabla\r\nHost: blabla\r\n\r\n");
            getLogger().info("read from socket after GET chunk: " + read(socket));

            writeln(socket, encodedMessage);
        }
        catch (UnknownHostException e) {
            getLogger().error("Unknown host: " + MTA_HOST, e);
            Assert.fail("mta http server not at host localhost");
        }
        catch (IOException e) {
            getLogger().error("IOException", e);
            Assert.fail("mta http server not at port " + mtaPort);
        }
        finally {
            ClosingUtils.close(socket);
        }

        new MessageAssertWaiter(false, message).assertGetsOkay("message received");
        getLogger().info(mta.getReceivedStrings());
    }


    /**
     * Tests if a timeout on the sender's side results in message loss on the
     * MTA side. This was added to test for a problem where a timeout exception
     * in the receiver thread in the {@link ConnectionThread} ended the whole
     * receiver thread and resulted in an end of receiving messages.
     */
    @Test
    public void timeoutTest() throws IOException {
        setup(new Properties());
        Assert.assertTrue("mta online", mta.isOnline());

        MessageContent content = new AliveAsk();
        MTAMessage message = new MTAMessage(content, "rid");
        String encodedMessage = MTAMessageCoder.getInstance().encode(message);

        MessageContent content2 = new AliveAsk();
        MTAMessage message2 = new MTAMessage(content2, "rid2");
        String encodedMessage2 = MTAMessageCoder.getInstance().encode(message2);

        Socket socket = null;
        try {
            socket = new Socket(MTA_HOST, mtaPort);
            socket.setSoTimeout(500);
            write(socket, "GET blabla\r\nHost: blabla\r\n\r\n");
            getLogger().info("read from socket after GET chunk: " + read(socket));

            writeln(socket, getLoginMessageString());

            writeln(socket, encodedMessage);

            sleep(ConnectionThread.SOCKET_TIMEOUT_MS + 2000);

            writeln(socket, encodedMessage2);
        }
        catch (UnknownHostException e) {
            getLogger().error("Unknown host: " + MTA_HOST, e);
            Assert.fail("mta http server not at host localhost");
        }
        catch (IOException e) {
            getLogger().error("IOException", e);
            Assert.fail("mta http server not at port " + mtaPort);
        }
        finally {
            ClosingUtils.close(socket);
        }

        new MessageAssertWaiter(true, message).assertGetsOkay("message received");
        new MessageAssertWaiter(true, message2).assertGetsOkay("message2 received");

        getLogger().info(mta.getReceivedStrings());
    }


    /**
     * Tests if sending messages without being authenticated results in the
     * expected and deserved loss of connection.
     */
    @Test
    public void writeTestWithoutLogin() {
        setup(new Properties());

        Assert.assertTrue("mta online", mta.isOnline());

        List<MTAMessage> messages = new LinkedList<MTAMessage>();

        Socket socket = null;
        try {
            socket = new Socket(MTA_HOST, mtaPort);
            socket.setSoTimeout(500);
            write(socket, "GET blabla\r\nHost: blabla\r\n\r\n");
            getLogger().info("read from socket after GET chunk: " + read(socket));

            try {
                writeMessage(socket, getAliveMessage("1"));

                new ConnectionTerminatedWaiter().assertGetsOkay("connection terminated", 3000, 100);

                writeBullshitToSocket(WRITE_ERRORS_ALL, messages, socket);
            }
            catch (IOException e) {
                getLogger().debug("This is a totally expected IOException. No worries!", e);
            }

            // The first message is always received.
            MTAMessage expectedMessage = getAliveMessage("1");
            new MessageAssertWaiter(false, expectedMessage).assertGetsOkay("message received: " + expectedMessage,
                            5000, 200);

            // The following messages are not received because
            // the authentication failed and the stream was closed.
            for (MTAMessage message : messages) {
                new MessageNotForwardedAssertWaiter(message).assertStaysOkay("message not forwarded: " + message, 3000,
                                200);
            }
        }
        catch (UnknownHostException e) {
            getLogger().error("Unknown host: " + MTA_HOST, e);
            Assert.fail("mta http server not at host localhost");
        }
        catch (IOException e) {
            getLogger().error("IOException", e);
            Assert.fail("mta http server not at port " + mtaPort);
        }
        finally {
            ClosingUtils.close(socket);
        }
    }


    /**
     * Tests if sending AliveTell messages without being authenticated results
     * in the unexpected loss of connection. Unexpected, because sometimes the
     * MTA sends an AliveAsk to the client pretty soon after the client
     * establishes a connection. The client then answers with an AliveTell
     * without being authenticated, yet. In this case, the connection should not
     * be dropped.
     */
    @Test
    public void writeTestAliveTellWithoutLogin() {
        setup(new Properties());

        Assert.assertTrue("mta online", mta.isOnline());

        List<MTAMessage> messages = new LinkedList<MTAMessage>();

        Socket socket = null;
        try {
            socket = new Socket(MTA_HOST, mtaPort);
            socket.setSoTimeout(500);
            write(socket, "GET blabla\r\nHost: blabla\r\n\r\n");
            getLogger().info("read from socket after GET chunk: " + read(socket));

            MessageContent content = new AliveTell();
            MTAMessage aliveTellMessage = new MTAMessage(content, "rid");

            try {
                writeMessage(socket, aliveTellMessage);

                new ConnectionNotTerminatedWaiter().assertStaysOkay("connection not terminated", 3000, 100);

                writeBullshitToSocket(WRITE_ERRORS_NONE, messages, socket);
            }
            catch (IOException e) {
                getLogger().debug("This is a totally expected IOException. No worries!", e);
            }

            // The first message is always received.
            MTAMessage expectedMessage = aliveTellMessage;
            new MessageAssertWaiter(false, expectedMessage).assertGetsOkay("message received: " + expectedMessage,
                            5000, 200);

            // The following messages are not received because
            // the authentication failed and the stream was closed.
            for (MTAMessage message : messages) {
                new MessageNotForwardedAssertWaiter(message).assertStaysOkay("message not forwarded: " + message, 3000,
                                200);
            }

            Assert.assertTrue("Connection has been terminated", mta.connectionTerminated());
        }
        catch (UnknownHostException e) {
            getLogger().error("Unknown host: " + MTA_HOST, e);
            Assert.fail("mta http server not at host localhost");
        }
        catch (IOException e) {
            getLogger().error("IOException", e);
            Assert.fail("mta http server not at port " + mtaPort);
        }
        finally {
            ClosingUtils.close(socket);
        }
    }


    /**
     * Tests the consequences of erroneous lines inserted into the socket
     * stream.
     */
    @Test
    public void writeTestWithError() {
        setup(new Properties());
        Assert.assertTrue("mta online", mta.isOnline());

        List<MTAMessage> messages = new LinkedList<MTAMessage>();

        Socket socket = null;
        try {
            socket = new Socket(MTA_HOST, mtaPort);
            socket.setSoTimeout(500);
            write(socket, "GET blabla\r\nHost: blabla\r\n\r\n");
            getLogger().info("read from socket after GET chunk: " + read(socket));

            // This time we authenticate first
            writeln(socket, getLoginMessageString());

            // Now we write some stuff to the socket
            messages.add(writeMessage(socket, getAliveMessage("1")));
            writeBullshitToSocket(WRITE_ERRORS_SOME, messages, socket);

            for (MTAMessage message : messages) {
                new MessageAssertWaiter(true, message).assertGetsOkay("message received: " + message, 5000, 200);
            }
        }
        catch (UnknownHostException e) {
            getLogger().error("Unknown host: " + MTA_HOST, e);
            Assert.fail("mta http server not at host localhost");
        }
        catch (IOException e) {
            getLogger().error("IOException", e);
            Assert.fail("mta http server not at port " + mtaPort);
        }
        finally {
            ClosingUtils.close(socket);
        }
    }


    /**
     * Tests if an MOTD is sent if none is defined.
     */
    @Test
    public void testWithoutMOTDSet() {
        setup(new Properties());
        Assert.assertTrue("mta online", mta.isOnline());

        Socket socket = null;
        try {
            socket = new Socket(MTA_HOST, mtaPort);
            socket.setSoTimeout(500);
            write(socket, "GET blabla\r\nHost: blabla\r\n\r\n");
            getLogger().info("read from socket after GET chunk: " + read(socket));

            // This time we authenticate first
            writeln(socket, getLoginMessageString());

            waitForLoginTell();

            // Now we can be sure we're logged in.
            TextMessageNotify text = null;
            for (MTAMessage m : mta.getSentToHttpMsgs()) {
                if (m.getContent() instanceof TextMessageNotify) {
                    text = (TextMessageNotify) m.getContent();
                }
            }
            Assert.assertNull("motd not sent", text);

        }
        catch (UnknownHostException e) {
            getLogger().error("Unknown host: " + MTA_HOST, e);
            Assert.fail("mta http server not at host localhost");
        }
        catch (IOException e) {
            getLogger().error("IOException", e);
            Assert.fail("mta http server not at port " + mtaPort);
        }
        finally {
            ClosingUtils.close(socket);
        }
    }


    /**
     * Tests if an MOTD is sent if one is defined.
     */
    @Test
    public void testWithMOTDSet() {
        Properties p = new Properties();
        p.put(TextMessageManager.FILE_NAME_KEY, "foo");
        setup(p);
        ((MockTextMessageManager) mta.getTextManager()).setMotd("MOTD");
        Assert.assertTrue("mta online", mta.isOnline());

        Socket socket = null;
        try {
            socket = new Socket(MTA_HOST, mtaPort);
            socket.setSoTimeout(500);
            write(socket, "GET blabla\r\nHost: blabla\r\n\r\n");
            getLogger().info("read from socket after GET chunk: " + read(socket));

            // This time we authenticate first
            writeln(socket, getLoginMessageString());

            waitForLoginTell();

            // Now we can be sure we're logged in.

            TextMessageNotify text = null;
            for (MTAMessage m : mta.getSentToHttpMsgs()) {
                if (m.getContent() instanceof TextMessageNotify) {
                    text = (TextMessageNotify) m.getContent();
                }
            }
            Assert.assertNotNull("motd sent", text);
            Assert.assertEquals("correct motd string sent", "MOTD", text.getContent());

        }
        catch (UnknownHostException e) {
            getLogger().error("Unknown host: " + MTA_HOST, e);
            Assert.fail("mta http server not at host localhost");
        }
        catch (IOException e) {
            getLogger().error("IOException", e);
            Assert.fail("mta http server not at port " + mtaPort);
        }
        finally {
            ClosingUtils.close(socket);
        }
    }


    /**
     * Tests if an MOTD is sent if one is defined.
     */
    @Test
    public void testBroadcastMessage() {
        Properties p = new Properties();
        p.put(TextMessageManager.ALLOWED_SENDER_KEY, "admin");
        setup(p);
        Assert.assertTrue("mta online", mta.isOnline());

        Socket socket = null;
        try {
            socket = new Socket(MTA_HOST, mtaPort);
            socket.setSoTimeout(500);
            write(socket, "GET blabla\r\nHost: blabla\r\n\r\n");
            getLogger().info("read from socket after GET chunk: " + read(socket));

            // This time we authenticate first
            writeln(socket, getLoginMessageString());

            waitForLoginTell();

            TextMessageNotify content = new TextMessageNotify(Priority.WARN, "title", "BROADCAST", "admin");
            Message message = new Message("from", "to", content, "rid");
            mta.receive(message);

            new BroadcastWaiter("BROADCAST").assertGetsOkay("broadcast msg found");

        }
        catch (UnknownHostException e) {
            getLogger().error("Unknown host: " + MTA_HOST, e);
            Assert.fail("mta http server not at host localhost");
        }
        catch (IOException e) {
            getLogger().error("IOException", e);
            Assert.fail("mta http server not at port " + mtaPort);
        }
        finally {
            ClosingUtils.close(socket);
        }
    }


    private void waitForLoginTell() {
        new AssertWaiter() {

            @Override
            protected boolean isConditionMet() {
                getLogger().debug(mta.getMessagesToHttp());
                return mta.getMessagesToHttp().size() == 1;
            }
        }.assertGetsOkay("1 msg sent");

        Assert.assertTrue("login tell sent", (mta.getMessagesToHttp().get(0).getContent() instanceof LoginTell));
    }


    private void writeBullshitToSocket(boolean[] writeErrors, List<MTAMessage> messages, Socket socket)
                    throws IOException {
        messages.add(writeMessage(socket, getAliveMessage("2")));
        if (writeErrors[0]) {
            writeln(socket, "blabla");
        }
        if (writeErrors[1]) {
            writeln(socket, "blabla\r\n");
        }
        messages.add(writeMessage(socket, getAliveMessage("3")));
        if (writeErrors[2]) {
            writeln(socket, "blabla\n");
        }
        messages.add(writeMessage(socket, getAliveMessage("4")));
        if (writeErrors[3]) {
            writeln(socket, "\r");
        }
        messages.add(writeMessage(socket, getAliveMessage("5")));
        if (writeErrors[4]) {
            write(socket, "blabla");
        }
        writeMessage(socket, getAliveMessage("not_received"));
        if (writeErrors[5]) {
            writeln(socket, "<message ");
        }
        messages.add(writeMessage(socket, getAliveMessage("6")));
        messages.add(writeMessage(socket, getAliveMessage("7")));
        if (writeErrors[6]) {
            writeln(socket, "blabla\n");
        }
        messages.add(writeMessage(socket, getAliveMessage("8")));
    }


    /**
     * Tests the consequences of dropping the connection on the client side
     * after logging in first. Expected behavior is that the internal connection
     * information is dropped and a logout message is sent to the UserAgent.
     */
    @Test
    public void logoutTestWithPriorLogin() {
        setup(new Properties());
        Assert.assertTrue("mta online", mta.isOnline());

        logoutTestInner(true);
    }


    /**
     * Tests the consequences of dropping the connection on the client side
     * without logging in first. Expected behavior is that the internal
     * connection information is dropped and a logout message is sent to the
     * UserAgent.
     */
    @Test
    public void logoutTestWithoutPriorLogin() {
        setup(new Properties());
        Assert.assertTrue("mta online", mta.isOnline());

        logoutTestInner(false);
    }


    private void logoutTestInner(boolean logIn) {
        Socket socket = null;
        try {
            socket = new Socket("localhost", mtaPort);
            socket.setSoLinger(false, 1);
            socket.setSoTimeout(500);
            write(socket, "GET blabla\r\nHost: blabla\r\n\r\n");
            getLogger().info("read from socket after GET chunk: " + read(socket));
            if (logIn) {
                writeln(socket, getLoginMessageString());
            }

        }
        catch (UnknownHostException e) {
            e.printStackTrace();
            Assert.fail("mta http server not at host localhost");
        }
        catch (IOException e) {
            e.printStackTrace();
            Assert.fail("mta http server not at port " + mtaPort);
        }
        finally {
            ClosingUtils.close(socket);
            getLogger().info("Socket closed. Obeying grace time for MTA to recognize closed socket.");
            int serverGraceMs = FakeHttpServer.LISTEN_TIMEOUT_MS;
            int connectionGraceMs = ConnectionThread.SOCKET_TIMEOUT_MS;
            int waitTimeMs = serverGraceMs + connectionGraceMs;
            sleep(waitTimeMs + 1000);
            getLogger().info("ended grace time");
        }
        if (logIn) {
            LogoutAsk logout = new LogoutAsk(MOCKSESSIONID);
            Message expected = new Message(TESTMTA_NAME, mta.getLoginLogoutReceiverName(), logout, "");
            new MessageSentAssertWaiter(expected).assertGetsOkay("Logout message sent", 1000, 200);
        }
    }


    private MTAMessage writeMessage(Socket socket, MTAMessage message) throws IOException {
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        String encodedMessage = MTAMessageCoder.getInstance().encode(message);
        HttpMessagingUtils.writeChunk(out, encodedMessage);
        return message;
    }


    private MTAMessage getAliveMessage(String id) {
        MessageContent content = new AliveAsk();
        MTAMessage message = new MTAMessage(content, id);
        return message;
    }


    private String getLoginMessageString() throws IOException {
        return getLoginMessageString(LOGIN);
    }


    private String getLoginMessageString(String login) throws IOException {
        LoginAsk content = new LoginAsk(login, "secret");
        MTAMessage loginMessage = new MTAMessage(content, "rid");
        String encodedMessage = MTAMessageCoder.getInstance().encode(loginMessage);
        return encodedMessage;
    }


    private void writeln(Socket socket, String msg) throws IOException {
        write(socket, msg + "\r\n");
    }


    private void write(Socket socket, String msg) throws IOException {
        OutputStream stream = socket.getOutputStream();
        stream.write(msg.getBytes());
        stream.flush();
    }


    private static String read(Socket socket) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        StringBuffer out = new StringBuffer();
        String read = null;
        while (!(read = in.readLine()).equals("")) {
            out.append(read);
        }
        return out.toString();
    }


    private class MTAOnlineAssertWaiter extends AssertWaiter {

        @Override
        protected boolean isConditionMet() {
            return mta.isOnline();
        }
    }


    private class MessageAssertWaiter extends AssertWaiter {

        private MTAMessage message;

        private boolean forwarded;


        public MessageAssertWaiter(boolean forwarded, MTAMessage message) {
            this.forwarded = forwarded;
            this.message = message;
        }


        @Override
        protected boolean isConditionMet() {
            boolean contains = false;

            List<MTAMessage> messages = null;
            if (forwarded) {
                messages = mta.getMessagesForwarded();
            }
            else {
                messages = mta.getUnderstoodMessages();
            }
            for (MTAMessage recMsg : messages) {
                if (recMsg != null) {
                    // boolean contOk =
                    // recMsg.getContent().equals(message.getContent());
                    boolean contOk = recMsg.equals(message);
                    if (contOk) {
                        contains = true;
                    }
                }
            }
            return contains;
        }
    }


    private class MessageSentAssertWaiter extends AssertWaiter {

        private Message message;


        public MessageSentAssertWaiter(Message message) {
            this.message = message;
        }


        @Override
        protected boolean isConditionMet() {
            boolean contains = false;

            List<Message> sent = orb.getSentMessages();
            for (Message recMsg : sent) {
                if (recMsg != null) {
                    boolean toOk = recMsg.getTo().equals(message.getTo());
                    boolean fromOk = recMsg.getFrom().equals(message.getFrom());
                    boolean contOk = recMsg.getContent().equals(message.getContent());
                    if (toOk && fromOk && contOk) {
                        contains = true;
                        break;
                    }
                }
            }
            return contains;
        }
    }


    private class MessageNotForwardedAssertWaiter extends AssertWaiter {

        private MTAMessage message;


        public MessageNotForwardedAssertWaiter(MTAMessage message) {
            this.message = message;
        }


        @Override
        protected boolean isConditionMet() {
            boolean contains = false;

            List<MTAMessage> recd = mta.getMessagesForwarded();
            for (MTAMessage recMsg : recd) {
                if (recMsg != null) {
                    boolean contOk = recMsg.getContent().equals(message.getContent());
                    if (contOk) {
                        contains = true;
                        break;
                    }
                }
            }
            return !contains;
        }
    }


    private class ConnectionWaiter extends AssertWaiter {

        private String login;


        public ConnectionWaiter(String login) {
            this.login = login;
        }


        @Override
        protected boolean isConditionMet() {
            boolean found = false;
            for (ConnectionInfo info : mta.getConnections().values()) {
                if (info.getUserInfo().getLogin().equals(login)) {
                    found = true;
                    break;
                }
            }
            return found;
        }
    }


    private class HttpErrorMessageWaiter extends AssertWaiter {

        ErrorConstants error;


        public HttpErrorMessageWaiter(ErrorConstants error) {
            this.error = error;
        }


        @Override
        protected boolean isConditionMet() {
            List<Message> toHttp = mta.getMessagesToHttp();
            for (Message msg : toHttp) {
                MessageContent content = msg.getContent();
                if (content instanceof ErrorNotify) {
                    if (((ErrorNotify) content).getError().equals(error)) {
                        return true;
                    }
                }
            }
            return false;
        }
    }


    private class BroadcastWaiter extends AssertWaiter {

        private String expectedContent;


        public BroadcastWaiter(String expectedContent) {
            this.expectedContent = expectedContent;
        }


        @Override
        protected boolean isConditionMet() {
            TextMessageNotify text = null;
            for (MTAMessage m : mta.getSentTextToAllMsgs()) {
                if (m.getContent() instanceof TextMessageNotify) {
                    text = (TextMessageNotify) m.getContent();
                }
            }
            return (text != null) && (expectedContent.equals(text.getContent()));
        }
    }


    private class ConnectionTerminatedWaiter extends AssertWaiter {

        @Override
        protected boolean isConditionMet() {
            return mta.connectionTerminated();
        }
    }


    private class ConnectionNotTerminatedWaiter extends AssertWaiter {

        @Override
        protected boolean isConditionMet() {
            return !mta.connectionTerminated();
        }
    }

}
