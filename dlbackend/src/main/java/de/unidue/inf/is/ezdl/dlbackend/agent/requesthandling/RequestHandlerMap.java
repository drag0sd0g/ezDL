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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import de.unidue.inf.is.ezdl.dlbackend.agent.handlers.RequestHandler;
import de.unidue.inf.is.ezdl.dlbackend.data.agent.RequestHandlerInfo;
import de.unidue.inf.is.ezdl.dlcore.Haltable;



/**
 * RequestHandlerMap manages a map from request ID's to RequestHandler objects.
 * It is used by the RequestHandlerStore to keep track of what RequestHandler is
 * handling what request.
 * 
 * @see RequestHandler
 * @author mjordan
 */
final class RequestHandlerMap implements Haltable {

    /**
     * The map of request ID to request object. If this is null, the map is
     * disabled.
     */
    private volatile Map<String, RequestHandler> requestMap = new ConcurrentHashMap<String, RequestHandler>();


    /**
     * Returns the RequestHandler for the given request ID.
     * 
     * @param requestID
     *            the ID of the RequestHandler object to return
     * @return the RequestHandler reference or null if no running RequestHandler
     *         is known for the ID given
     */
    public RequestHandler get(String requestID) {
        RequestHandler handler = null;
        if (!isHalted()) {
            handler = requestMap.get(requestID);
            if ((handler != null) && handler.isHalted()) {
                requestMap.remove(requestID);
                handler = null;
            }
        }
        return handler;
    }


    /**
     * Inserts a RequestHandler into the map.
     * 
     * @param requestId
     *            the ID of the request that the handler handles
     * @param handler
     *            the handler itself.
     */
    public void put(String requestId, RequestHandler handler) {
        if (!isHalted() && (requestId != null) && (handler != null)) {
            purge();
            requestMap.put(requestId, handler);
        }
    }


    /**
     * Removes a request from the map.
     * 
     * @param requestId
     *            the key for the map
     * @return the removed request or null, if the RequestHandlerMap is disabled
     *         or the key does not exist
     */
    public RequestHandler remove(String requestId) {
        RequestHandler handler = null;
        if (!isHalted()) {
            handler = requestMap.remove(requestId);
        }
        return handler;
    }


    /**
     * Returns a map with request infos.
     * 
     * @return the map, which might be empty, but never null
     */
    public Map<String, RequestHandlerInfo> getRequestInfo() {
        Map<String, RequestHandlerInfo> rmap = new HashMap<String, RequestHandlerInfo>();
        if (!isHalted()) {
            for (String key : requestMap.keySet()) {
                RequestHandler handler = requestMap.get(key);
                RequestHandlerInfo record = handler.getInfo();
                rmap.put(key, record);
            }
        }
        return rmap;
    }


    /**
     * Shuts the map down. After calling this method, there is no request
     * information anymore left in the RequestHandlerMap and no new information
     * is accepted.
     * 
     * @return the collection of RequestHandlers that was still active when the
     *         map was shut down, or null if the RequestHandlerMap is already
     *         shut down
     */
    private Collection<RequestHandler> disableMap() {
        List<RequestHandler> handlers = new ArrayList<RequestHandler>();
        if (!isHalted()) {
            Map<String, RequestHandler> oldMap = requestMap;
            requestMap = null;
            handlers.addAll(oldMap.values());
        }

        return handlers;
    }


    /**
     * Kills all running handlers and shuts the internal map down so no new
     * handlers can be created.
     */
    @Override
    public void halt() {
        Collection<RequestHandler> handlers = disableMap();
        for (RequestHandler handler : handlers) {
            handler.halt();
        }
    }


    /**
     * Returns true, if the RequestHandlerMap is shut down.
     * 
     * @return true, if the map is shut down, else false
     */
    @Override
    public boolean isHalted() {
        return (requestMap == null);
    }


    /**
     * Returns the number of request handlers in the map.
     * 
     * @return the number of request handlers in the map
     */
    public int size() {
        int size = 0;
        if (requestMap != null) {
            size = requestMap.size();
        }
        return size;
    }


    /**
     * Scans the map for halted handlers and removes them from the map.
     */
    private void purge() {
        List<String> deletableIds = new LinkedList<String>();

        for (Entry<String, RequestHandler> entry : requestMap.entrySet()) {
            String id = entry.getKey();
            RequestHandler handler = entry.getValue();
            if ((handler == null) || (handler.isHalted())) {
                deletableIds.add(id);
            }
        }

        for (String id : deletableIds) {
            requestMap.remove(id);
        }

    }
}
