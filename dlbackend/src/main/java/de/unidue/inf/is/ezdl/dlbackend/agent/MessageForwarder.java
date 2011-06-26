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

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import de.unidue.inf.is.ezdl.dlbackend.agent.timer.Timeable;
import de.unidue.inf.is.ezdl.dlbackend.agent.timer.Timer;
import de.unidue.inf.is.ezdl.dlbackend.message.Message;
import de.unidue.inf.is.ezdl.dlbackend.message.content.CancelRequestNotify;
import de.unidue.inf.is.ezdl.dlcore.Haltable;
import de.unidue.inf.is.ezdl.dlcore.message.content.MessageContent;



/**
 * Helper for sending a message to a collection of agents and waiting for the
 * results.
 * 
 * @author mjordan
 */
public class MessageForwarder implements Timeable, Haltable {

    private static final String WAKEUP_KEY_TIMEOUT = "timeout";
    /**
     * The logger.
     */
    private static Logger logger = Logger.getLogger(MessageForwarder.class);

    /**
     * The timer is needed to set a timeout for outstanding answers.
     */
    private Timer timer;
    /**
     * The set of names of the agents that we haven't received a message from,
     * yet.
     */
    private Set<String> outstandingAgentNames;
    /**
     * The agent to use for sending.
     */
    private MessageForwarderClient client;
    /**
     * The request ID to use for the messages that are sent.
     */
    private String requestId;
    /**
     * True, as long as the forwarder continues doing anything.
     */
    private volatile boolean running = true;


    /**
     * Constructor.
     * 
     * @param agent
     *            the agent to use for sending the messages
     * @param requestId
     *            the request ID to put into the messages
     */
    public MessageForwarder(MessageForwarderClient client, String requestId) {
        this.client = client;
        this.requestId = requestId;
        this.outstandingAgentNames = new HashSet<String>();

    }


    /**
     * Forwards a MessageContent object to a given list of agents.
     * 
     * @param reqId
     *            the requestId to use
     * @param content
     *            the object to forward
     * @param agentList
     *            the names of the agents to forward to
     */
    private void forwardMessageContent(String reqId, MessageContent content, Collection<String> agentList) {
        if ((agentList != null) && (agentList.size() > 0)) {
            for (String toAgent : agentList) {
                if (toAgent != null) {
                    try {
                        Message msg = new Message(client.agentName(), toAgent, content, reqId);
                        client.send(msg);
                        outstandingAgentNames.add(toAgent);
                    }
                    catch (Exception ef) {
                        logger.error("Exception", ef);
                    }
                }
            }
        }
    }


    /**
     * Blocks until either the messages are arrived completely or the timeout
     * expires.
     */
    public void waitForAnswers() {
        while (isWaiting()) {
            try {
                Thread.sleep(200);
            }
            catch (InterruptedException e) {
            }
        }
    }


    /**
     * Ends the MessageForwarder, stops the timer and cancels requests at agents
     * that haven't answered, yet.
     */
    @Override
    public void halt() {
        logger.debug("halt() called");
        stopTimer();
        running = false;
        sendCancelToOutstandingAgents();
        logger.debug("halt() finished");
    }


    /**
     * Sends a cancel message to wrappers that have not yet replied.
     */
    private void sendCancelToOutstandingAgents() {
        CancelRequestNotify content = new CancelRequestNotify(requestId);
        List<String> wrapperList = new LinkedList<String>();
        for (String wrapperName : outstandingAgentNames) {
            wrapperList.add(wrapperName);
        }
        String id = client.getNextRequestID();
        forwardMessageContent(id, content, wrapperList);
    }


    /**
     * Forwards the message to the agents.
     * 
     * @param agentList
     * @param content
     * @param timeout
     */
    public void forwardMessageToAgents(Collection<String> agentList, MessageContent content, int timeout) {
        forwardMessageContent(requestId, content, agentList);
        startTimer(WAKEUP_KEY_TIMEOUT, timeout);
    }


    /**
     * Stops the timer if one is running.
     */
    private void stopTimer() {
        if (timer != null) {
            timer.killTimer();
        }
    }


    /**
     * Starts a new timer.
     * 
     * @param id
     *            the id that is passed to the handler upon expiration of the
     *            waiting time
     * @param durationMs
     *            the time to wait in milliseconds
     */
    private void startTimer(String id, int durationMs) {
        timer = new Timer();
        timer.init(durationMs, this, id);
        timer.startTimer();
    }


    @Override
    public void wakeup(String ID) {
        if (ID.equals(WAKEUP_KEY_TIMEOUT)) {
            logger.debug("Woke up because of timeout.");
            if (!isHalted()) {
                logger.debug("Not halted. So ending the waiting.");
                halt();
                logger.debug("Signaling timeout to client " + client.agentName());
                client.timeout();
            }
            else {
                logger.debug("Woke up and found myself halted.");
            }
        }
    }


    /**
     * Empty implementation. Does not do jack.
     */
    @Override
    public void wakeup(Message message) {
        throw new UnsupportedOperationException("Please call wakeup(String) instead.");
    }


    @Override
    public boolean isHalted() {
        return !running;
    }


    /**
     * Calling this signals that the agent whose name is given sent a message.
     * 
     * @param agent
     *            the agent that sent a message
     */
    public void noteReceived(String agent) {
        outstandingAgentNames.remove(agent);
    }


    /**
     * Returns if the {@link MessageForwarder} is still waiting for answers.
     * 
     * @return true, if waiting. Else false.
     */
    public boolean isWaiting() {
        return (outstandingAgentNames.size() != 0) && !isHalted();
    }


    /**
     * Returns the names of the agents that haven't send an answer, yet.
     * 
     * @return the names of the agents that haven't send an answer, yet.
     */
    public List<String> stillWaitedFor() {
        List<String> out = new LinkedList<String>();
        for (String name : outstandingAgentNames) {
            out.add(name);
        }
        return out;
    }
}
