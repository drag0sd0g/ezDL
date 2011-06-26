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

package de.unidue.inf.is.ezdl.dlwrapper;

import java.util.Properties;
import java.util.Set;

import net.sf.ehcache.CacheException;

import org.apache.log4j.Logger;

import de.unidue.inf.is.ezdl.dlbackend.agent.AbstractAgent;
import de.unidue.inf.is.ezdl.dlbackend.agent.handlers.RequestHandler;
import de.unidue.inf.is.ezdl.dlbackend.cache.EhCache;
import de.unidue.inf.is.ezdl.dlbackend.message.content.RegisterAsk;
import de.unidue.inf.is.ezdl.dlcore.cache.Cache;
import de.unidue.inf.is.ezdl.dlcore.data.wrappers.WrapperInfo;
import de.unidue.inf.is.ezdl.dlcore.utils.PropertiesUtils;
import de.unidue.inf.is.ezdl.dlwrapper.handlers.DocumentDetailsFillHandler;
import de.unidue.inf.is.ezdl.dlwrapper.handlers.WrapperRequestHandler;



/**
 * The WrapperMapper is both an ezDL agent (towards the ezDL back end) and a
 * Wrapper (towards some DL resource). The wrapper tasks are delegated to a
 * specialized Wrapper class that is created at instantiation.
 */
public class WrapperMapper extends AbstractAgent {

    /**
     * The maximum number of threads allowed to execute this wrapper
     * concurrently.
     */
    private static final int MAX_SESSIONS_DEFAULT = Integer.MAX_VALUE;
    /**
     * The property key that configures the maximum number of concurrent
     * sessions (queries) for this wrapper.
     */
    private static final String MAX_SESSIONS_KEY = "maxSessions";
    /**
     * The properties key that defines the class of the Wrapper delegate.
     */
    private static final String WRAPPER_CLASS_KEY = "wrapperclass";
    /**
     * The logger.
     */
    private final Logger logger = Logger.getLogger(WrapperMapper.class);
    /**
     * The Wrapper to delegate requests to.
     */
    private String wrapperClass;
    /**
     * Information about the wrapper that the mapper is using.
     */
    private WrapperInfo wrapperInfo;
    /**
     * The service name to register with.
     */
    private String serviceName;
    /**
     * The cache.
     */
    private Cache cache;


    /**
     * Initializes the agent and the Wrapper delegate, using the properties key
     * WRAPPER_CLASS_KEY.
     * 
     * @return true, if Agent was successfully initialized. false, else.
     */
    @Override
    public boolean init(String agentName, Properties props) {
        super.init(agentName, props);
        try {
            wrapperClass = props.getProperty(WRAPPER_CLASS_KEY);
            if (wrapperClass == null) {
                String message = "Unable to find key '" + WRAPPER_CLASS_KEY + "' in properties!";
                logger.error(message);
                halt();
                throw new IllegalArgumentException(message);
            }
            verifyWrapperClass();
            initCache();
            return true;
        }
        catch (CacheException e) {
            System.err.println("Could not initialize Cache correctly. " + "Reason: " + e.getMessage());
            halt();
            return false;
        }
    }


    /**
     * Checks if the wrapper class is existent and kills the agent, if not.
     */
    private void verifyWrapperClass() {
        try {
            Wrapper testWrapperInstance = (Wrapper) Class.forName(wrapperClass).newInstance();
            testWrapperInstance.init(this, cache);
            wrapperInfo = testWrapperInstance.getWrapperInfo();
            serviceName = testWrapperInstance.getServiceName();
            testWrapperInstance.halt();
        }
        catch (InstantiationException e) {
            haltWithErrorMsg("WrapperMapper: Can't find or instantiate class " + wrapperClass + "!", e);
        }
        catch (IllegalAccessException e) {
            haltWithErrorMsg("WrapperMapper: Can't find or instantiate class " + wrapperClass + "!", e);
        }
        catch (ClassNotFoundException e) {
            haltWithErrorMsg("WrapperMapper: Can't find or instantiate class " + wrapperClass + "!", e);
        }

        if (wrapperInfo == null) {
            haltWithErrorMsg("WrapperMapper: Can't find wrapper description in properties!", null);
        }
    }


    private void haltWithErrorMsg(String message, Exception e) {
        if (e != null) {
            logger.error(message, e);
        }
        else {
            logger.error(message);
        }

        halt();

        if (e != null) {
            throw new IllegalArgumentException(message, e);

        }
        else {
            throw new IllegalArgumentException(message);

        }
    }


    /**
     * Initializes the cache.
     * <p>
     * Package visable for testing reasons.
     */
    void initCache() throws CacheException {
        cache = new EhCache(agentName(), WrapperMapper.class.getResource("/cache/ehcache.xml"));
    }


    @Override
    protected Set<Class<? extends RequestHandler>> setupRequestHandlers() {
        Set<Class<? extends RequestHandler>> requestHandlers = super.setupRequestHandlers();
        requestHandlers.add(WrapperRequestHandler.class);
        requestHandlers.add(DocumentDetailsFillHandler.class);
        return requestHandlers;
    }


    /**
     * {@inheritDoc}
     * <p>
     * The agent is halted and the cache shut down.
     * 
     * @see AbstractAgent#halt()
     * @see Cache#shutdown()
     */
    @Override
    public void halt() {
        super.halt();
        if (cache != null) {
            cache.shutdown();
        }
    }


    @Override
    protected RegisterAsk getRegisterContent() {
        String service = getServiceName();
        RegisterAsk register = new RegisterAsk(service, wrapperInfo);
        return register;
    }


    @Override
    public String getServiceName() {
        return serviceName;
    }


    /**
     * Returns a reference to the Wrapper delegate.
     * 
     * @return the reference to the Wrapper delegate
     */
    public Wrapper getNewWrapperInstance() {
        Wrapper wrapper = null;
        try {
            wrapper = (Wrapper) Class.forName(wrapperClass).newInstance();
            wrapper.init(this, cache);
        }
        catch (Exception e) {
            logger.error("Exception: ", e);
        }
        return wrapper;
    }


    /**
     * Returns the cache.
     * 
     * @return the cache
     */
    public Cache getCache() {
        return cache;
    }


    /**
     * Returns the maximum number of threads the wrapper is supposed to run in
     * parallel.
     * 
     * @return the maximum thread count
     */
    public int maxSessionCount() {
        int count = PropertiesUtils.getIntProperty(getProperties(), MAX_SESSIONS_KEY, MAX_SESSIONS_DEFAULT);
        getLogger().debug("Using max sessions: " + count);
        return count;
    }

}
