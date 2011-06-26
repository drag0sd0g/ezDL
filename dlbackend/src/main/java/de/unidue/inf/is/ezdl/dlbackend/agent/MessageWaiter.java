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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.unidue.inf.is.ezdl.dlbackend.message.Message;
import de.unidue.inf.is.ezdl.dlcore.misc.TimeoutException;



/**
 * Store that keeps track of messages that are still outstanding.
 */
// TODO
public class MessageWaiter {

    /**
     * The timeout for answers from the directory.
     */
    private int timeoutMs;
    /**
     * This map keeps messages that arrived without having a RequestHandler to
     * handle them.
     */
    private Map<String, Message> arrived = new HashMap<String, Message>();
    /**
     * Keeps a list of request IDs of outstanding messages.
     */
    private Set<String> awaitedRequestIDs = new HashSet<String>();


    /**
     * Constructor.
     * 
     * @param timeoutMs
     *            the time in milliseconds before the wait times out.
     */
    public MessageWaiter(int timeoutMs) {
        this.timeoutMs = timeoutMs;
    }


    /**
     * Waits <code>dirTimeoutMs</code> milliseconds for a message.
     * 
     * @param requestId
     *            the request ID of the message to wait for
     * @return the message, which is never null.
     * @throws TimeoutException
     *             in case that the message does not arrive in time
     */
    public synchronized Message waitForRequestId(String requestId) throws TimeoutException {
        long timeout = System.currentTimeMillis();
        Message message = arrived.remove(requestId);
        awaitedRequestIDs.add(requestId);

        while ((System.currentTimeMillis() - timeout <= timeoutMs) && (message == null)) {
            try {
                wait(timeoutMs);
            }
            catch (InterruptedException e) {
            }
            message = arrived.remove(requestId);
            if (message != null) {
                break;
            }
        }

        if (message == null) {
            throw new TimeoutException();
        }

        return message;
    }


    /**
     * Handles an arrived message if somebody is waiting for it.
     * 
     * @param message
     *            the message that just arrived
     * @return true, if the message was handled by the wait queue, false if
     *         somebody else has to handle it
     */
    public synchronized boolean messageArrived(Message message) {
        boolean handled = false;
        final String requestId = message.getRequestId();
        if (awaitedRequestIDs.contains(requestId)) {
            awaitedRequestIDs.remove(requestId);
            arrived.put(requestId, message);
            notifyAll();
            handled = true;
        }
        return handled;
    }

}
