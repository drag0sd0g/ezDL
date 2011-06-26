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

import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import de.unidue.inf.is.ezdl.dlbackend.agent.Agent;
import de.unidue.inf.is.ezdl.dlbackend.data.agent.RequestHandlerInfo;
import de.unidue.inf.is.ezdl.dlbackend.message.Message;
import de.unidue.inf.is.ezdl.dlbackend.mock.MockAgent;



public abstract class AbstractAgentTestBase extends AbstractBackendTestBase {

    /**
     * Check if a given number of RequestHandlers is running.
     * 
     * @author mjordan
     */
    protected class RunningHandlerCountCheck extends AssertWaiter {

        /**
         * The agent to check handlers of.
         */
        private Agent agent;
        /**
         * The numbers of handlers that are expected.
         */
        private int countExpected;


        /**
         * Creates a new checker.
         * 
         * @param agent
         *            the agent whose handler count is to be checked
         * @param countExpected
         *            the expected number of handlers
         */
        public RunningHandlerCountCheck(Agent agent, int countExpected) {
            this.countExpected = countExpected;
            this.agent = agent;
        }


        @Override
        protected boolean isConditionMet() {
            return (getRunningCount() == countExpected);
        }


        /**
         * Returns the number of running RequestHandlers.
         * 
         * @param info
         *            the info map
         * @return the number of running handlers
         */
        private int getRunningCount() {
            int count = 0;
            Map<String, RequestHandlerInfo> info = agent.getRequestInfo();

            for (RequestHandlerInfo handlerInfo : info.values()) {
                if (handlerInfo.isRunning()) {
                    count++;
                }
            }

            return count;
        }
    }


    /**
     * Send a message to an agent concurrently.
     * <p>
     * This object sends the message returned by
     * {@link DeferredMessageSender#getReply()} to the agent given in the
     * constructor if {@link DeferredMessageSender#isConditionMet()} returns
     * true of if the timeout given in the constructor expires.
     * 
     * @author mj
     */
    protected abstract class DeferredMessageSender implements Runnable {

        private MockAgent mockAgent;

        private int timeoutMs;


        public DeferredMessageSender(MockAgent agent, int timeoutMs) {
            this.mockAgent = agent;
            this.timeoutMs = timeoutMs;
            Executor executor = Executors.newSingleThreadExecutor();
            executor.execute(this);
        }


        @Override
        public void run() {
            waitForConditionMet();
            mockAgent.receive(getReply());
        }


        private void waitForConditionMet() {
            long startedAt = System.currentTimeMillis();
            boolean conditionMet = false;
            while (!conditionMet && (System.currentTimeMillis() < (startedAt + timeoutMs))) {
                conditionMet = isConditionMet();
                sleep(20);
            }
            if (conditionMet) {
                getLogger().debug("Message sent. Continuing.");
            }
            else {
                getLogger().debug("Message still not sent. Continuing due to timeout.");
            }
        }


        protected abstract boolean isConditionMet();


        protected abstract Message getReply();

    }

}
