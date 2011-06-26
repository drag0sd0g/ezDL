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

package de.unidue.inf.is.ezdl.dlbackend.agent;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.unidue.inf.is.ezdl.dlbackend.AbstractAgentTestBase;
import de.unidue.inf.is.ezdl.dlbackend.agent.handlers.RequestHandler;
import de.unidue.inf.is.ezdl.dlbackend.agent.handlers.RequestHandlerCancelRequest;
import de.unidue.inf.is.ezdl.dlbackend.message.Message;
import de.unidue.inf.is.ezdl.dlbackend.message.content.CancelRequestNotify;
import de.unidue.inf.is.ezdl.dlbackend.message.content.LogAsk;
import de.unidue.inf.is.ezdl.dlbackend.message.content.RegisterAsk;
import de.unidue.inf.is.ezdl.dlbackend.mock.LongRunningMockMessageContent;
import de.unidue.inf.is.ezdl.dlbackend.mock.LongRunningMockRequestHandler;
import de.unidue.inf.is.ezdl.dlbackend.mock.MockAgentConnector;
import de.unidue.inf.is.ezdl.dlbackend.mock.MockORB;
import de.unidue.inf.is.ezdl.dlbackend.mock.MockSecurityManager;
import de.unidue.inf.is.ezdl.dlcore.message.content.AliveAsk;
import de.unidue.inf.is.ezdl.dlcore.misc.ConnectionFailedException;
import de.unidue.inf.is.ezdl.dlcore.misc.EzDLException;
import de.unidue.inf.is.ezdl.dlcore.misc.TimeoutException;



public class AbstractAgentTest extends AbstractAgentTestBase {

    private static final String AGENT_NAME = "somedummyagentname";


    private class TestableAgent extends AbstractAgent {

        private List<Message> messagesSent = new LinkedList<Message>();


        @Override
        public Set<Class<? extends RequestHandler>> setupRequestHandlers() {
            Set<Class<? extends RequestHandler>> handlers = super.setupRequestHandlers();
            handlers.add(LongRunningMockRequestHandler.class);
            handlers.add(RequestHandlerCancelRequest.class);
            return handlers;
        }


        @Override
        public void initOnline() {
            getLogger().info("act()");
        };


        @Override
        public String getServiceName() {
            return "/agent/name";
        }


        @Override
        public void send(Message message) {
            messagesSent.add(message);
            super.send(message);
        }


        /**
         * @return the messagesSent
         */
        public List<Message> getMessagesSent() {
            return messagesSent;
        }

    }


    TestableAgent agent;

    MockORB orb;

    MockAgentConnector connector;


    @Before
    public void init() {
        orb = new MockORB();

        agent = new TestableAgent();
        initAgent(agent, AGENT_NAME);

        try {
            agent.goOnline();
            sleep(100);
        }
        catch (ConnectionFailedException e) {
            e.printStackTrace();
        }
    }


    @After
    public void theEnd() {
        agent.halt();
    }


    private void initAgent(Agent agent, String name) {
        Properties props = new Properties();
        props.setProperty("dir.timeout", "10");

        agent.init(name, new MockSecurityManager(), props);
        connector = new MockAgentConnector(orb, agent);
        agent.setConnector(connector);
    }


    /*
     * The tests.
     */
    @Test
    public void testFindAgent() {
        try {
            agent.findAgent("agentname");
        }
        catch (EzDLException e) {
            if (!(e instanceof TimeoutException)) {
                e.printStackTrace();
            }
        }

        new AssertWaiter() {

            @Override
            protected boolean isConditionMet() {
                String expected = "{AgentNameAsk for agentname}";
                return agent.getMessagesSent().toString().contains(expected);
            }
        }.assertGetsOkay("Message sent to directory");
    }


    @Test
    public void testUnhandledMessage() {
        Message msg = new Message();
        msg.setFrom("SantaClaus");
        msg.setTo(AGENT_NAME);
        msg.setRequestId("SomeRequest");
        msg.setContent(new RegisterAsk(""));

        agent.receive(msg);

        new AssertWaiter() {

            @Override
            protected boolean isConditionMet() {
                return agent.getMessagesSent().size() == 0;
            }
        }.assertStaysOkay("No message should have been sent");
    }


    @Test
    public void testReceiveAliveAsk() {
        Message msg = new Message();
        msg.setFrom("SantaClaus");
        msg.setTo(AGENT_NAME);
        msg.setRequestId("SomeRequest");
        msg.setContent(new AliveAsk());

        agent.receive(msg);

        new AssertWaiter() {

            @Override
            protected boolean isConditionMet() {
                return agent.getMessagesSent().toString().contains("AliveTell");
            }
        }.assertGetsOkay("Tell sent");
    }


    @Test
    public void testReceiveLogAskOkay() throws EzDLException {
        Message msg = new Message();
        msg.setFrom(agent.getDirectoryName());
        msg.setTo("SantaClaus");
        msg.setRequestId("SomeRequest");
        msg.setContent(new LogAsk());

        agent.receive(msg);

        new AssertWaiter() {

            @Override
            protected boolean isConditionMet() {
                return agent.getMessagesSent().toString().contains("LogTell");
            }
        }.assertGetsOkay("Tell sent: sender is right");
    }


    @Test
    public void testReceiveLogAskNotOkay() {
        Message msg = new Message();
        msg.setFrom("Eve");
        msg.setTo("SantaClaus");
        msg.setRequestId("SomeRequest");
        msg.setContent(new LogAsk());

        agent.receive(msg);

        new AssertWaiter() {

            @Override
            protected boolean isConditionMet() {
                return agent.getMessagesSent().size() == 0;
            }
        }.assertStaysOkay("Tell not sent: sender is wrong");
    }


    @Test
    public void testReceiveCancelRequestNotify() {
        String initialRequestId = "SomeRequest";

        Message longRunningMsg = new Message();
        longRunningMsg.setFrom("Eve");
        longRunningMsg.setTo("SantaClaus");
        longRunningMsg.setRequestId(initialRequestId);
        longRunningMsg.setContent(new LongRunningMockMessageContent());

        getLogger().info("First message");
        agent.receive(longRunningMsg);

        // Wait a bit until the request is created.
        new RunningHandlerCountCheck(agent, 1).assertGetsOkay("1", 2000, 10);

        // Now let the LongRunningMockRequestHandler run some time
        sleep(2000);

        // Now send the cancel message
        Message cancelMsg = new Message();
        cancelMsg.setFrom("Eve");
        cancelMsg.setTo("SantaClaus");
        cancelMsg.setRequestId("SomeRequest2");
        cancelMsg.setContent(new CancelRequestNotify(initialRequestId));

        getLogger().info("Second message");
        agent.receive(cancelMsg);

        // Wait until no handlers
        new RunningHandlerCountCheck(agent, 0).assertGetsOkay("2", 2000, 10);

    }


    @Test
    public void testLoopingMessage() {
        Message longRunningMsg = new Message();
        longRunningMsg.setFrom("Eve");
        longRunningMsg.setTo("SantaClaus");
        longRunningMsg.setRequestId("reqid");
        longRunningMsg.setContent(new LongRunningMockMessageContent());

        for (int i = 0; (i < 25); i++) {
            getLogger().info("Resend " + i);
            agent.send(longRunningMsg);
        }

        new ConnectorSentWaiter(20).assertStaysOkay("Messages never more than 20");
    }


    private class ConnectorSentWaiter extends AssertWaiter {

        private int expectedSize;


        public ConnectorSentWaiter(int expectedSize) {
            this.expectedSize = expectedSize;
        }


        @Override
        protected boolean isConditionMet() {
            int size = connector.getMessagesSent().size();
            return (size <= expectedSize);
        }
    }
}
