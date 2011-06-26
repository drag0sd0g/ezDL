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

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import de.unidue.inf.is.ezdl.dlbackend.agent.connectors.AgentConnector;
import de.unidue.inf.is.ezdl.dlbackend.message.Message;
import de.unidue.inf.is.ezdl.dlcore.Haltable;
import de.unidue.inf.is.ezdl.dlcore.utils.QueueUtils;



/**
 * SendQueue is a queue of outgoing messages that a special sender thread
 * concurrently sends away using an {@link AgentConnector} object as delegate.
 */
public final class SendQueue implements Haltable {

    /**
     * The SendQueue looks for messages in the queue every {@link #SEND_FREQ_MS}
     * milliseconds.
     */
    private static final int SEND_FREQ_MS = 100;
    /**
     * The SendQueue waits this long foor the queue to run empty after
     * {@link #halt()} is called.
     */
    private static final int FLUSH_TIMEOUT_MS = 1000;
    /**
     * The logger.
     */
    private final Logger logger = Logger.getLogger(SendQueue.class);
    /**
     * The messages to send.
     */
    private Queue<Message> outgoingMessages;
    /**
     * The connector used to send messages.
     */
    private AgentConnector connector;
    /**
     * The executor service that runs the sender thread.
     */
    private ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    /**
     * The SendQueue runs as long as {@link #running} is true.
     */
    private volatile boolean running;


    /**
     * Creates a new SendQueue with the given connector.
     * 
     * @param connector
     *            the connector that is used to send messages
     */
    public SendQueue(AgentConnector connector) {
        this.connector = connector;
        this.outgoingMessages = new ConcurrentLinkedQueue<Message>();
        this.running = true;
        this.executorService.scheduleWithFixedDelay(new SendRunnable(), SEND_FREQ_MS, SEND_FREQ_MS,
                        TimeUnit.MILLISECONDS);
    }


    /**
     * Sends a message.
     * <p>
     * Actually puts the message into a queue so it can be sent by a special
     * sender thread.
     * 
     * @param message
     *            the message to send
     */
    public void send(Message message) {
        if (running) {
            outgoingMessages.add(message);
        }
        else {
            logger.warn("SendQueue shutting down. Not accepting message for delivery: " + message.shortForm());
        }
    }


    /**
     * The sender thread payload. Sends the messages in the queue.
     */
    class SendRunnable implements Runnable {

        @Override
        public void run() {
            Message message = null;
            while ((message = outgoingMessages.poll()) != null) {
                try {
                    logger.debug("SendQueue sending " + message);
                    connector.send(message);
                }
                catch (IOException e) {
                    logger.error("Exception caught", e);
                }
            }
        }
    }


    @Override
    public void halt() {
        running = false;
        QueueUtils.waitUntilEmpty(outgoingMessages, FLUSH_TIMEOUT_MS);
        executorService.shutdown();
    }


    @Override
    public boolean isHalted() {
        return !running;
    }


    /**
     * Returns if the queue is empty.
     * <p>
     * This is only package visible for testing purposes.
     * 
     * @return the queue
     */
    boolean isEmpty() {
        return outgoingMessages.isEmpty();
    }


    /**
     * Returns the number of queued messages.
     * 
     * @return the queue size
     */
    public int size() {
        return outgoingMessages.size();
    }
}
