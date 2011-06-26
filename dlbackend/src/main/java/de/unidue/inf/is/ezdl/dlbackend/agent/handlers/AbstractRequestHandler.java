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

package de.unidue.inf.is.ezdl.dlbackend.agent.handlers;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import de.unidue.inf.is.ezdl.dlbackend.agent.Agent;
import de.unidue.inf.is.ezdl.dlbackend.agent.Reusable;
import de.unidue.inf.is.ezdl.dlbackend.agent.StartedBy;
import de.unidue.inf.is.ezdl.dlbackend.data.agent.AgentLog;
import de.unidue.inf.is.ezdl.dlbackend.data.agent.RequestHandlerInfo;
import de.unidue.inf.is.ezdl.dlbackend.message.Message;
import de.unidue.inf.is.ezdl.dlbackend.security.SecurityManager;



/**
 * Base implementation of the RequestHandler interface. Only the method
 * {@link #work(Message)} has to be implemented to make this working. (Pun
 * intended.) Implementations have to be annotated by the {@link StartedBy}
 * annotation.
 * <p>
 * Implementations that are not tagged {@link Reusable} don't have to call
 * {@link #halt()} explicitly. This is done in
 * {@link #processNextMessageFromQueue()} automatically.
 * <p>
 * AbstractRequestHandler provides a running thread, a queue for incoming
 * messages, an AgentLog for event logging and some helper methods for message
 * sending.
 * 
 * @see RequestHandler
 * @see StartedBy
 */
public abstract class AbstractRequestHandler implements RequestHandler {

    /**
     * The logger.
     */
    private final Logger logger = Logger.getLogger(AbstractRequestHandler.class);
    /**
     * The queue of messages this RequestHandler has yet to handle.
     */
    private List<Message> msgQueue = Collections.synchronizedList(new LinkedList<Message>());
    /**
     * The agent-wide unique ID of the request that this RequestHandler handles.
     * This is set once in {@link #init(String, Agent)}, which is normally
     * called within the RequestHandlerStore.
     */
    private String requestId;
    /**
     * Reference to the agent who owns the RequestHandler.
     */
    private Agent agent;
    /**
     * RequestHandlers, too, have an AgentLog.
     */
    private AgentLog requestLog;
    /**
     * The request continues to process messages as long as running is true.
     */
    private volatile boolean running = true;
    /**
     * For uptime reporting.
     */
    private long timestampStarted;
    /**
     * The first message that started this RequestHandler.
     */
    private Message initialMessage;
    /**
     * The object that manages authorization-related things.
     */
    private SecurityManager securityManager;
    /**
     * True, if the request should return partial results. False, if only
     * complete results are accepted.
     */
    private boolean sendPartialResults;


    @Override
    public void init(String requestId, Agent agent) {
        if (requestId == null) {
            throw new IllegalArgumentException("Request ID must not be null");
        }

        if (this.requestId == null) {
            this.requestId = requestId;
            this.agent = agent;
            this.requestLog = null;
            this.securityManager = agent.getSecurityManager();
            this.timestampStarted = System.currentTimeMillis();
        }
        else {
            throw new IllegalStateException("init() must not be called twice");
        }
    }


    /**
     * Returns a reference to the {@link Agent} who owns this
     * {@link RequestHandler}.
     * 
     * @return a reference to the agent
     */
    protected Agent getAgent() {
        return agent;
    }


    /**
     * @return a reference to the {@link SecurityManager}
     */
    protected SecurityManager getSecurityManager() {
        return securityManager;
    }


    @Override
    public String getRequestId() {
        return requestId;
    }


    /**
     * Main loop. Runs as long as {@link #running} is true.
     */
    private synchronized void mainLoop() {
        while (running) {
            while (!isMessagQueueEmpty() && running) {
                processNextMessageFromQueue();
            }
            if (running) {
                try {
                    wait();
                }
                catch (InterruptedException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }


    /**
     * Takes a message from the queue and processes it.
     */
    private void processNextMessageFromQueue() {
        Message msg = nextMessageFromQueue();
        if (msg != null) {
            getLogger().debug("Handling " + msg);

            try {
                boolean processed = work(msg);

                if (!processed) {
                    logger.error("Message not handled by RequestHandler. Since RequestHandlers " //
                                    + "should get only messages they can handle, this is most " //
                                    + "probably a programming error." + msg);
                }
            }
            catch (Exception e) {
                logger.error(e.getMessage(), e);
                throw new RuntimeException(e);
            }
        }
        else {
            logger.error("Found a null message in the queue.");
        }
    }


    /**
     * Performs work needed for a certain message. This method is called from
     * {@link #processNextMessageFromQueue()} who guarantees that message is
     * never null.
     * 
     * @param message
     * @return true, if the message has been processed. Else false.
     */
    protected abstract boolean work(Message message);


    /**
     * True, as long as the request is supposed to keep running. Else false.
     * 
     * @return true or false
     */
    @Override
    public boolean isHalted() {
        return !running;
    }


    /**
     * Halts the request.
     */
    @Override
    public synchronized void halt() {
        if (!isHalted()) {
            addLog("halting request", getRequestId());
            running = false;
            notifyAll();
        }
    }


    @Override
    public synchronized void addMessageToQueue(Message msg) {
        if (msg != null) {
            if (initialMessage == null) {
                initialMessage = msg;
            }
            msgQueue.add(msg);
            notifyAll();
        }
    }


    /**
     * Returns if messages are in the queue.
     * 
     * @return true, if queue is empty, else false
     */
    private synchronized boolean isMessagQueueEmpty() {
        return (msgQueue.isEmpty());
    }


    /**
     * Returns the first message in the queue and removes it.
     * 
     * @return the first message or <code>null</code>, if no message is in the
     *         queue
     */
    private synchronized Message nextMessageFromQueue() {
        if (isMessagQueueEmpty()) {
            return null;
        }

        return msgQueue.remove(0);
    }


    /**
     * Starts the request handler.
     */
    @Override
    public final void run() {

        if (getAgent().getLog() != null) {
            getAgent().getLog().add("goal", getRequestId());
        }

        addLog("Entering main loop", getRequestId());

        mainLoop();

        logger.info("Main loop left");
    }


    /**
     * Adds a message transfer note to the AgentLog.
     * 
     * @param type
     *            the type (e.g. "received" or "sent"
     * @param toLog
     *            the message whose transfer is to be logged
     */
    protected void addLog(String type, Message toLog) {
        if (requestLog != null) {
            requestLog.add(type, toLog.toString());
        }
    }


    /**
     * Adds an entry to the AgentLog.
     * 
     * @param type
     *            the type description
     * @param toLog
     *            the log text
     */
    protected void addLog(String type, String toLog) {
        if (requestLog != null) {
            requestLog.add(type, toLog);
        }
    }


    /**
     * Sends a message. Errors are logged.
     * 
     * @param message
     *            the message to send
     */
    public void send(Message message) {
        getAgent().send(message);
    }


    /**
     * Sends a message to a list of receiving agents.
     * 
     * @param agentlist
     *            the list of receivers
     * @param message
     *            the message to send
     */
    protected void send(String agentlist[], Message message) {
        getAgent().send(agentlist, message);
    }


    /**
     * Adds an AgentLog object to this request that can be viewed on the web
     * interface of the DirectoryAgent.
     * 
     * @param maxEntries
     */
    protected void enableLogging(int maxEntries) {
        if (requestLog == null) {
            StringBuffer owner = new StringBuffer();
            owner.append(requestId);
            owner.append('@');
            owner.append(agent.agentName());
            requestLog = AgentLog.startLog(maxEntries);
        }
    }


    /**
     * Returns the logger.
     * 
     * @return the logger
     */
    protected Logger getLogger() {
        return logger;
    }


    /**
     * Returns the first message ever received in this handler. This would
     * normally the message that a longer-running handler wants to reply to in
     * order to send its answer.
     * 
     * @return the initial message
     */
    protected synchronized Message getInitialMessage() {
        return initialMessage;
    }


    @Override
    public RequestHandlerInfo getInfo() {
        long uptimeSec = (System.currentTimeMillis() - timestampStarted) / 1000;
        StringBuffer out = new StringBuffer();
        out.append(toString());

        String infoStr = out.toString();
        RequestHandlerInfo info = new RequestHandlerInfo(infoStr, !isHalted(), uptimeSec);
        return info;
    }


    @Override
    public void setSendPartialResults(boolean sendPartialResults) {
        this.sendPartialResults = sendPartialResults;
    }


    /**
     * Returns if partial results are accepted by the client.
     * 
     * @return true, if the initiator of the request is willing to accept
     *         partial results. False, if only complete results are accepted.
     */
    protected boolean isSendPartialResults() {
        return sendPartialResults;
    }
}
