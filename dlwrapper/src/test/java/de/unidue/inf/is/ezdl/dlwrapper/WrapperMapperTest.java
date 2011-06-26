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

package de.unidue.inf.is.ezdl.dlwrapper;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import junit.framework.Assert;
import net.sf.ehcache.CacheException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.unidue.inf.is.ezdl.dlbackend.AbstractAgentTestBase;
import de.unidue.inf.is.ezdl.dlbackend.agent.connectors.AgentConnector;
import de.unidue.inf.is.ezdl.dlbackend.agent.handlers.RequestHandler;
import de.unidue.inf.is.ezdl.dlbackend.message.Message;
import de.unidue.inf.is.ezdl.dlbackend.message.content.CancelRequestNotify;
import de.unidue.inf.is.ezdl.dlbackend.message.content.RegisterAsk;
import de.unidue.inf.is.ezdl.dlbackend.mock.MockAgentConnector;
import de.unidue.inf.is.ezdl.dlbackend.mock.MockORB;
import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;
import de.unidue.inf.is.ezdl.dlcore.data.query.DefaultQuery;
import de.unidue.inf.is.ezdl.dlcore.data.query.DocumentQuery;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeCompare;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeCompare.Predicate;
import de.unidue.inf.is.ezdl.dlcore.message.content.AliveAsk;
import de.unidue.inf.is.ezdl.dlcore.message.content.DocumentQueryAsk;
import de.unidue.inf.is.ezdl.dlcore.misc.ConnectionFailedException;
import de.unidue.inf.is.ezdl.dlwrapper.wrappers.dummy.DummyWrapper;



/**
 * Tests the {@link WrapperMapper} using the {@link DummyWrapper} as delegate.
 * 
 * @author mjordan
 */
public class WrapperMapperTest extends AbstractAgentTestBase {

    private static final int WAITMS = 100;

    private static final String AGENT_NAME = "DummyWrapper";


    private class TestableWrapperMapper extends WrapperMapper {

        private List<Message> messagesSent = new LinkedList<Message>();
        private boolean throwCacheException = false;


        public void setThrowCacheException(boolean throwCacheException) {
            this.throwCacheException = throwCacheException;
        }


        @Override
        public Set<Class<? extends RequestHandler>> setupRequestHandlers() {
            Set<Class<? extends RequestHandler>> handlers = super.setupRequestHandlers();
            return handlers;
        }


        @Override
        public void initOnline() {
            getLogger().info("act()");
        }


        @Override
        public String getServiceName() {
            return "/agent/name";
        }


        @Override
        public void send(Message message) {
            messagesSent.add(message);
        }


        @Override
        public void deregisterAgent() {
            getLogger().info("Acted like I deregistered.");
        }


        /**
         * @return the messagesSent
         */
        public List<Message> getMessagesSent() {
            return messagesSent;
        }


        @Override
        void initCache() throws CacheException {
            if (throwCacheException) {
                throw new CacheException("Fake exception. Totally expected.");
            }
            else {
                super.initCache();
            }
        }

    }


    TestableWrapperMapper agent;

    MockORB orb;


    @Before
    public void init() {
        orb = new MockORB();

        agent = new TestableWrapperMapper();
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


    private Properties getWrapperProps() {
        Properties props = new Properties();
        props.setProperty("dir.timeout", "10");
        props.setProperty("wrapperclass", DummyWrapper.class.getName());
        props.setProperty("info.remotename", "Some Remote DL");
        props.setProperty("info.category", "cs");
        props.setProperty("info.category.de", "Informatik");
        props.setProperty("info.category.en", "Computer science");
        props.setProperty("info.description.de", "Irgend eine DL");
        props.setProperty("info.description.en", "Some random DL");
        return props;
    }


    private void initAgent(WrapperMapper agent, String name) {
        Properties props = getWrapperProps();

        agent.init(name, props);
        AgentConnector connector = new MockAgentConnector(orb, agent);
        agent.setConnector(connector);
    }


    /*
     * The tests.
     */

    @Test
    public void testUnhandledMessage() {
        Message msg = new Message();
        msg.setFrom("SantaClaus");
        msg.setTo(AGENT_NAME);
        msg.setRequestId("SomeRequest");
        msg.setContent(new RegisterAsk(""));

        agent.receive(msg);
        sleep(1 * WAITMS);

        Assert.assertEquals("No message should have been sent", 0, agent.getMessagesSent().size());
    }


    @Test
    public void testReceiveAliveAsk() {
        Message msg = new Message();
        msg.setFrom("SantaClaus");
        msg.setTo(AGENT_NAME);
        msg.setRequestId("SomeRequest");
        msg.setContent(new AliveAsk());

        agent.receive(msg);
        (new AssertWaiter() {

            @Override
            protected boolean isConditionMet() {
                System.out.println(agent.getMessagesSent().toString());
                return agent.getMessagesSent().toString().contains("AliveTell");
            }
        }).assertGetsOkay("Tell sent", 1 * WAITMS, 50);
    }


    @Test
    public void testReceiveCancelRequestNotify() {
        String initialRequestId = "SomeRequest";

        Message longRunningMsg = new Message();
        longRunningMsg.setFrom("Eve");
        longRunningMsg.setTo("SantaClaus");
        longRunningMsg.setRequestId(initialRequestId);
        DocumentQuery query = getQuery(40000, "Dummy");
        DocumentQueryAsk ask = new DocumentQueryAsk(query, null);

        longRunningMsg.setContent(ask);

        getLogger().info("First message");
        agent.receive(longRunningMsg);

        // Wait a bit until the request is created.
        new RunningHandlerCountCheck(agent, 1).assertStaysOkay("1", 2000, 10);

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

        Assert.assertEquals(
                        "[From: SantaClaus / To: Eve / Request: SomeRequest /  Message: {DocumentQueryStoredTell {DataList []}}]", //
                        agent.getMessagesSent().toString());
    }


    private DocumentQuery getQuery(int count, String... wrapperNames) {
        QueryNodeCompare comp = new QueryNodeCompare(Field.YEAR, Predicate.EQ, Integer.toString(count));
        DefaultQuery q = new DefaultQuery(comp);
        DocumentQuery query = new DocumentQuery(q, Arrays.asList(wrapperNames));
        return query;
    }


    /**
     * Tests if the agent would halt itself if the cache doesn't initialize.
     * (See bug #448)
     */
    @Test
    public void testHaltOnCacheProblem1() {
        agent = new TestableWrapperMapper();
        agent.setThrowCacheException(true);
        Properties props = getWrapperProps();

        final boolean res = agent.init("agent", props);
        Assert.assertFalse(res);
    }


    /**
     * Tests if the agent would start normally if the cache does initialize.
     * (See bug #448)
     */
    @Test
    public void testHaltOnCacheProblem2() {
        agent = new TestableWrapperMapper();
        agent.setThrowCacheException(false);
        Properties props = getWrapperProps();

        final boolean res = agent.init("agent", props);
        Assert.assertTrue(res);
    }

}
