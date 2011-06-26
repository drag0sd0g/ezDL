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

package de.unidue.inf.is.ezdl.dlbackend;

import de.unidue.inf.is.ezdl.dlbackend.message.Message;
import de.unidue.inf.is.ezdl.dlbackend.mock.MockAgent;



/**
 * @author mjordan
 */
public class RequestHandlerTestBase extends AbstractAgentTestBase {

    /**
     * Waits until the number of messages the given MockAgent has sent reaches
     * the given number.
     * <p>
     * Uses the default timeout of {@link AssertWaiter}.
     * 
     * @author mjordan
     */
    protected class MessageSentCountWaiter extends AssertWaiter {

        private MockAgent mockAgent;

        private int expectedCount;


        /**
         * Creates a new Waiter.
         * 
         * @param mockAgent
         *            the agent to test for
         * @param messagesSent
         *            the number of messages that are expected.
         */
        public MessageSentCountWaiter(MockAgent mockAgent, int messagesSent) {
            this.mockAgent = mockAgent;
            this.expectedCount = messagesSent;
        }


        @Override
        protected boolean isConditionMet() {
            return mockAgent.getMessagesSent().size() >= expectedCount;
        }
    };


    /**
     * Tests if a certain message is sent by a MockAgent.
     * <p>
     * Uses the default timeout of {@link AssertWaiter}.
     * 
     * @author mjordan
     */
    protected class MessageSentWaiter extends AssertWaiter {

        private MockAgent mockAgent;

        private Message message;


        /**
         * Creates a new Waiter.
         * 
         * @param mockAgent
         *            the MockAgent to test
         * @param message
         *            the message that is expected to be sent.
         */
        public MessageSentWaiter(MockAgent mockAgent, Message message) {
            this.mockAgent = mockAgent;
            this.message = message;
        }


        @Override
        protected boolean isConditionMet() {
            System.out.println(mockAgent.getMessagesSent());
            return mockAgent.getMessagesSent().contains(message);
        }
    }


    /**
     * Same as {@link MessageSentWaiter} but for the receiving side.
     * <p>
     * Uses the default timeout of {@link AssertWaiter}.
     * 
     * @author mjordan
     */
    protected class MessageReceivedWaiter extends AssertWaiter {

        private MockAgent mockAgent;

        private Message expected;


        /**
         * Creates a new Waiter.
         * 
         * @param mockAgent
         *            the agent to test
         * @param expected
         *            the message that is expected to be received
         */
        public MessageReceivedWaiter(MockAgent mockAgent, Message expected) {
            this.mockAgent = mockAgent;
            this.expected = expected;
        }


        @Override
        protected boolean isConditionMet() {
            return mockAgent.getMessagesSent().contains(expected);
        }
    }

}
