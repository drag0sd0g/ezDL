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

import org.junit.Assert;
import org.junit.Test;

import de.unidue.inf.is.ezdl.dlbackend.AbstractBackendTestBase;
import de.unidue.inf.is.ezdl.dlbackend.message.Message;
import de.unidue.inf.is.ezdl.dlbackend.message.content.LogAsk;
import de.unidue.inf.is.ezdl.dlbackend.mock.MockAgent;
import de.unidue.inf.is.ezdl.dlbackend.mock.MockAgentConnector;
import de.unidue.inf.is.ezdl.dlbackend.mock.MockORB;



/**
 * @author mjordan
 */
public class SendQueueTest extends AbstractBackendTestBase {

    private MockORB orb = new MockORB();
    private MockAgent agent = new MockAgent("mockagent");
    private MockAgentConnector connector = new MockAgentConnector(orb, agent);


    /**
     * Test method for
     * {@link de.unidue.inf.is.ezdl.dlbackend.agent.SendQueue#send(de.unidue.inf.is.ezdl.dlbackend.message.Message)}
     * .
     */
    @Test
    public void testSend() {
        SendQueue queue = new SendQueue(connector);
        final Message sentMsg = new Message("from", "to", new LogAsk(), "reqid");
        queue.send(new Message("from", "to", new LogAsk(), "reqid"));

        new AssertWaiter() {

            @Override
            protected boolean isConditionMet() {
                boolean messageSent = connector.getMessagesSent().contains(sentMsg);
                boolean onlyOneMsgSent = connector.getMessagesSent().size() == 1;
                return messageSent && onlyOneMsgSent;
            }
        }.assertGetsAndStaysOkay("Message sent");

        Assert.assertTrue("queue empty", queue.isEmpty());
    }


    /**
     * Test method for
     * {@link de.unidue.inf.is.ezdl.dlbackend.agent.SendQueue#halt()}.
     */
    @Test
    public void testHalt() {
        SendQueue queue = new SendQueue(connector);
        final Message sentMsg = new Message("from", "to", new LogAsk(), "reqid");
        queue.send(new Message("from", "to", new LogAsk(), "reqid"));

        new AssertWaiter() {

            @Override
            protected boolean isConditionMet() {
                boolean messageSent = connector.getMessagesSent().contains(sentMsg);
                boolean onlyOneMsgSent = connector.getMessagesSent().size() == 1;
                return messageSent && onlyOneMsgSent;
            }
        }.assertGetsAndStaysOkay("Message sent");

        queue.halt();

        queue.send(new Message("from", "to", new LogAsk(), "reqid"));

        new AssertWaiter() {

            @Override
            protected boolean isConditionMet() {
                boolean messageSent = connector.getMessagesSent().contains(sentMsg);
                boolean onlyOneMsgSent = connector.getMessagesSent().size() == 1;
                return messageSent && onlyOneMsgSent;
            }
        }.assertStaysOkay("Message sent");

        Assert.assertTrue("queue halted", queue.isHalted());
    }

}
