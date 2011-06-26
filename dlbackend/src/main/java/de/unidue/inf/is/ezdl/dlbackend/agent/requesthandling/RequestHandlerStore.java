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

package de.unidue.inf.is.ezdl.dlbackend.agent.requesthandling;

import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import de.unidue.inf.is.ezdl.dlbackend.agent.Agent;
import de.unidue.inf.is.ezdl.dlbackend.agent.handlers.RequestHandler;
import de.unidue.inf.is.ezdl.dlbackend.data.agent.RequestHandlerInfo;
import de.unidue.inf.is.ezdl.dlbackend.message.Message;
import de.unidue.inf.is.ezdl.dlcore.Haltable;



/**
 * Handles the management of RequestHandlers.
 * 
 * @author mjordan
 */
public class RequestHandlerStore implements Haltable {

    /**
     * Reference to the registry of available handlers.
     */
    private RequestHandlerFactory registry;
    /**
     * Reference to the map of running handlers.
     */
    private RequestHandlerMap map;
    /**
     * Reference to the agent.
     */
    private Agent agent;
    /**
     * The logger.
     */
    private final Logger logger = Logger.getLogger(RequestHandlerStore.class);


    /**
     * Creates a new instance.
     * 
     * @param agent
     *            a reference to the agent who owns the object
     */
    public RequestHandlerStore(Agent agent) {
        this.agent = agent;
        registry = new RequestHandlerFactory(agent);
        map = new RequestHandlerMap();
    }


    /**
     * Initializes the handler registry.
     * 
     * @param handlers
     *            the set of RequestHandler classes that are ready for
     *            instantiation
     */
    public void initHandlers(Set<Class<? extends RequestHandler>> handlers) {
        registry.initHandlers(handlers);
    }


    /**
     * Adds a RequestHandler object to the map of running RequestHandlers and
     * starts it.
     * 
     * @param requestHandler
     *            the RequestHandler to add and start
     */
    private void addRequestHandler(RequestHandler requestHandler) {
        if (requestHandler != null) {
            log("new request", requestHandler.getRequestId());
            map.put(requestHandler.getRequestId(), requestHandler);
        }
    }


    /**
     * Returns a RequestHandler for the given message.
     * <p>
     * First, an attempt is made to find a handler for the request ID in the
     * message. If no handler is running that handles the given request ID, a
     * new handler is created.
     * <p>
     * If a handler could be found or created, the message is passed to the
     * handler and a reference to the handler returned.
     * 
     * @param message
     *            the message to find a RequestHandler for
     * @return the RequestHandler or null if no suitable RequestHandler could be
     *         found or built
     */
    public RequestHandler getHandler(Message message) {
        String requestId = message.getRequestId();
        RequestHandler requestHandler = null;

        logger.debug("Getting RequestHandler from map for " + requestId);
        requestHandler = map.get(requestId);
        logger.debug("Got RequestHandler " + requestHandler);

        if ((requestHandler == null) && (!map.isHalted())) {
            logger.debug("RequestHandler not running. Getting new one from Registry.");
            requestHandler = getNewRequest(message);
            logger.debug("Got handler from registry: " + requestHandler);

            if (requestHandler != null) {
                addRequestHandler(requestHandler);
            }
        }
        return requestHandler;
    }


    /**
     * Gets a RequestHandler from the handlerRegistry or null if no suitable
     * class is found.
     * 
     * @param message
     *            the message to handle
     * @return the RequestHandler object of null if no such object can be
     *         instantiated
     */
    private RequestHandler getNewRequest(Message message) {
        RequestHandler r = registry.getHandler(message.getContent(), message.getRequestId());
        return r;
    }


    /**
     * {@inheritDoc}
     * <p>
     * Halts the {@link RequestHandlerMap} and the {@link RequestHandlerFactory}
     * .
     */
    @Override
    public void halt() {
        map.halt();
        registry.halt();
    }


    @Override
    public boolean isHalted() {
        return map.isHalted();
    }


    /**
     * Returns information on the running RequestHandlers.
     * 
     * @return request handler information, which might be empty, but never null
     */
    public Map<String, RequestHandlerInfo> getRequestInfo() {
        return map.getRequestInfo();
    }


    /**
     * Kills the RequestHandler given by its reference.
     * 
     * @param handler
     *            reference to the RequestHandler to kill
     */
    public void killHandler(RequestHandler handler, boolean sendPartialResults) {
        if (handler != null) {
            killHandler(handler.getRequestId(), sendPartialResults);
        }
    }


    /**
     * Kills the RequestHandler given by the request ID that is handled by it.
     * 
     * @param requestId
     *            the request ID
     */
    public void killHandler(String requestId, boolean sendPartialResults) {
        RequestHandler handler = map.remove(requestId);
        if (handler != null) {
            handler.setSendPartialResults(sendPartialResults);
            handler.halt();
        }
    }


    /**
     * Logs an event to the agent log.
     * 
     * @param type
     * @param message
     */
    private void log(String type, String message) {
        if (agent.getLog() != null) {
            agent.getLog().add(type, message);
        }
    }

}
