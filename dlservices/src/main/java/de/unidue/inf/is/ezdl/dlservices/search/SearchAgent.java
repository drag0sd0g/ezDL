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

package de.unidue.inf.is.ezdl.dlservices.search;

import java.util.Set;

import de.unidue.inf.is.ezdl.dlbackend.agent.AbstractAgent;
import de.unidue.inf.is.ezdl.dlbackend.agent.handlers.RequestHandler;
import de.unidue.inf.is.ezdl.dlbackend.cache.EhCache;
import de.unidue.inf.is.ezdl.dlcore.cache.Cache;
import de.unidue.inf.is.ezdl.dlservices.search.handlers.DocumentQueryHandler;



/**
 * The search agent is one of the main agents for ezDL. It provides several
 * functions like document search, citation and reference search etc.
 */
public class SearchAgent extends AbstractAgent {

    /**
     * Service name of this agent.
     */
    private static final String SERVICE_NAME = "/service/search";
    /**
     * The cache to keep short-term data like recent answers from wrappers.
     */
    private Cache cache = new EhCache("SA", SearchAgent.class.getResource("/cache/ehcache.xml"));


    @Override
    protected Set<Class<? extends RequestHandler>> setupRequestHandlers() {
        Set<Class<? extends RequestHandler>> handlers = super.setupRequestHandlers();
        handlers.add(DocumentQueryHandler.class);
        return handlers;
    }


    @Override
    public String getServiceName() {
        return SERVICE_NAME;
    }


    /**
     * Returns the internal cache.
     * 
     * @return the cache, which is never null
     */
    public Cache getCache() {
        return cache;
    }


    @Override
    public void halt() {
        super.halt();
        cache.shutdown();
    }

}
