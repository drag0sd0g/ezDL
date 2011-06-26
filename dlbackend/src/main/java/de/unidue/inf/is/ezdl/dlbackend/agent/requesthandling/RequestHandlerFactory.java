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

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import org.apache.log4j.Logger;

import de.unidue.inf.is.ezdl.dlbackend.agent.Agent;
import de.unidue.inf.is.ezdl.dlbackend.agent.Reusable;
import de.unidue.inf.is.ezdl.dlbackend.agent.StartedBy;
import de.unidue.inf.is.ezdl.dlbackend.agent.handlers.RequestHandler;
import de.unidue.inf.is.ezdl.dlbackend.message.Message;
import de.unidue.inf.is.ezdl.dlcore.Haltable;
import de.unidue.inf.is.ezdl.dlcore.message.content.MessageContent;
import de.unidue.inf.is.ezdl.dlcore.misc.ExceptionAwareThreadPoolExecutor;



/**
 * Factory that instantiates {@link RequestHandler} objects for incoming
 * {@link Message} objects according to the {@link StartedBy} Annotation of a
 * suitable RequestHandler. If a RequestHandler is tagged with the
 * {@link Reusable} Annotation, the RequestHandler object is cached and reused
 * for subsequent requests.
 * 
 * @see RequestHandler
 * @see StartedBy
 * @see Reusable
 * @author mj
 */
final class RequestHandlerFactory implements Haltable {

    /**
     * The logger.
     */
    private final Logger logger = Logger.getLogger(RequestHandlerFactory.class);
    /**
     * The stash of known handler classes by message content type.
     */
    private Map<Class<? extends MessageContent>, Class<? extends RequestHandler>> knownHandlers;
    /**
     * The stash of running reusable handlers.
     */
    private Map<Class<? extends MessageContent>, RequestHandler> reusableHandlers;
    /**
     * Used to execute the RequestHandlers in separate threads.
     */
    private ExecutorService executorService;
    /**
     * Reference to the agent who owns this registry.
     */
    private Agent agent;


    /**
     * Constructor.
     * 
     * @param agent
     *            reference to the agent for which the handlers are to be
     *            created
     */
    public RequestHandlerFactory(Agent agent) {
        this.agent = agent;
        executorService = new ExceptionAwareThreadPoolExecutor();
        knownHandlers = new HashMap<Class<? extends MessageContent>, Class<? extends RequestHandler>>();
        reusableHandlers = new HashMap<Class<? extends MessageContent>, RequestHandler>();
    }


    /**
     * Adds classes in the given set to the internal stash.
     * 
     * @param handlers
     *            the set of classes to add as RequstHandlers
     */
    public void initHandlers(Set<Class<? extends RequestHandler>> handlers) {
        knownHandlers.clear();
        for (Class<? extends RequestHandler> handler : handlers) {
            StartedBy handledMessageContents = handler.getAnnotation(StartedBy.class);
            if (handledMessageContents != null) {
                for (Class<? extends MessageContent> c : handledMessageContents.value()) {
                    if (knownHandlers.containsKey(c)) {
                        logger.error(c + " handled by multiple request handlers. Perpetrator: " + handler
                                        + " - skipped!");
                    }
                    else {
                        knownHandlers.put(c, handler);
                    }
                }
            }
        }
    }


    /**
     * Gets a ready-to-use RequestHandler object for the given MessageContent.
     * 
     * @param content
     *            the MessageContent to handle
     * @param requestId
     *            the request ID of the request to handle
     * @return the RequestHandler object or null if no RequestHandler can be
     *         instantiated
     */
    public RequestHandler getHandler(MessageContent content, String requestId) {
        RequestHandler handler = null;
        Class<? extends MessageContent> contentClass = content.getClass();
        handler = getReusableHandler(contentClass);

        boolean noReusableHandlerFound = handler == null;
        if (noReusableHandlerFound) {

            Class<? extends RequestHandler> handlerClass = knownHandlers.get(contentClass);
            if (handlerClass != null) {

                handler = instantiateNewHandler(handlerClass, requestId);
                if (handler != null) {

                    if (classIsReusable(handler)) {
                        logger.debug(handler.getClass() + " is reusable");
                        reusableHandlers.put(contentClass, handler);
                    }

                    startHandlerThread(handler);
                }
            }
        }

        if (handler == null) {
            logger.info("Neither a new nor a used request handler for " + content);
        }

        return handler;
    }


    /**
     * Starts the handler in a new thread.
     * 
     * @param handler
     *            the handler to start
     */
    private void startHandlerThread(RequestHandler handler) {
        logger.debug("started thread for " + handler + " in factory");
        executorService.submit(handler);
    }


    /**
     * Returns, if a class is reusable.
     * 
     * @param handler
     *            the handler to check
     * @return
     */
    private boolean classIsReusable(RequestHandler handler) {
        Reusable reusable = handler.getClass().getAnnotation(Reusable.class);
        return reusable != null;
    }


    /**
     * Returns a running, reusable handler.
     * 
     * @param content
     *            the content the handler is supposed to handle
     * @return a handler instance or null if no instance exists
     */
    private RequestHandler getReusableHandler(Class<? extends MessageContent> content) {
        RequestHandler handler = reusableHandlers.get(content);
        if (handler != null) {
            logger.debug("Found reusable handler for " + content + ": " + handler);
        }
        return handler;
    }


    /**
     * Creates a new RequestHandler instance.
     * 
     * @param handlerclass
     *            the class for which to create a new handler
     * @param requestId
     *            the requestId to use for the new handler
     * @return a new, initialized handler or null in case of an error
     */
    private RequestHandler instantiateNewHandler(Class<? extends RequestHandler> handlerclass, String requestId) {
        RequestHandler request = null;
        try {
            Constructor<? extends RequestHandler> c = handlerclass.getConstructor();
            request = c.newInstance();
            request.init(requestId, agent);
        }
        catch (Exception e) {
            logger.error("Could not create request handler of class " + handlerclass);
            request = null;
        }
        return request;
    }


    /**
     * {@inheritDoc}
     * <p>
     * The {@link ExecutorService} is shutdown.
     * 
     * @see ExecutorService#shutdown()
     */
    @Override
    public void halt() {
        executorService.shutdown();
    }


    @Override
    public boolean isHalted() {
        return executorService.isShutdown();
    }
}
