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

package de.unidue.inf.is.ezdl.dlfrontend.comm;

import java.io.IOException;
import java.util.EventObject;
import java.util.UUID;

import org.apache.log4j.Logger;

import de.unidue.inf.is.ezdl.dlcore.Haltable;
import de.unidue.inf.is.ezdl.dlcore.message.MTAMessage;
import de.unidue.inf.is.ezdl.dlcore.misc.ConnectionFailedException;
import de.unidue.inf.is.ezdl.dlfrontend.dispatcher.Dispatcher;
import de.unidue.inf.is.ezdl.dlfrontend.dispatcher.EventReceiver;



/**
 * The BackendProxy is a proxy or Forwarder/Receiver for Messages to and from
 * the agents and Services and Event Listener for AgentEvents. Messages are
 * translated to Events and vice versa.
 */
public final class BackendProxy implements MessageReceivedListener, EventReceiver, Haltable {

    public static final String CONNECTION_PROPERTIES = "connection.properties";

    private final Logger logger = Logger.getLogger(BackendProxy.class);

    private BackendCommunicator backendCommunicator;


    public BackendProxy(BackendCommunicator backendCommunicator) {
        this.backendCommunicator = backendCommunicator;
    }


    public void init(String mtaHost, int mtaPort, int timeOutSecs) throws ConnectionFailedException {
        backendCommunicator.init(mtaHost, mtaPort, timeOutSecs);
        backendCommunicator.addMessageReceivedListener(this);

        Dispatcher.registerInterest(this, BackendEvent.class);
    }


    @Override
    public boolean handleEzEvent(EventObject ev) {
        boolean ourOwnEvent = (ev.getSource() == this);
        boolean sendableEvent = (ev instanceof BackendEvent);
        if (!sendableEvent || ourOwnEvent) {
            return false;
        }
        // Send it now...
        try {
            send((BackendEvent) ev);
            return true;
        }
        catch (IOException e) {
            logger.error("send failed", e);
        }
        return false;
    }


    @Override
    public void receive(MTAMessage message) {
        logger.debug("Received by backend proxy: \n" + message);

        if (message != null) {
            BackendEvent agentEvent = new BackendEvent(this);
            agentEvent.setContent(message.getContent());
            agentEvent.setRequestId(message.getRequestId());
            Dispatcher.postEvent(agentEvent);
        }
    }


    private void send(BackendEvent ev) throws IOException {
        MTAMessage msg = new MTAMessage();

        msg.setContent(ev.getContent());
        msg.setRequestId(ev.getRequestId() == null ? getNextRequestID() : ev.getRequestId());

        send(msg);
    }


    private void send(MTAMessage msg) {
        backendCommunicator.send(msg);
    }


    private String getNextRequestID() {
        return UUID.randomUUID().toString();
    }


    @Override
    public void halt() {
        backendCommunicator.halt();
    }


    @Override
    public boolean isHalted() {
        return backendCommunicator.isHalted();
    }

}
