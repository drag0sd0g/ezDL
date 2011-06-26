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

package de.unidue.inf.is.ezdl.dlbackend.security;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import de.unidue.inf.is.ezdl.dlbackend.agent.Agent;
import de.unidue.inf.is.ezdl.dlbackend.message.Message;
import de.unidue.inf.is.ezdl.dlcore.cache.Cache;
import de.unidue.inf.is.ezdl.dlcore.cache.ConcurrentMapCache;
import de.unidue.inf.is.ezdl.dlcore.cache.TimedCache;
import de.unidue.inf.is.ezdl.dlcore.message.content.PrivilegeAsk;
import de.unidue.inf.is.ezdl.dlcore.message.content.PrivilegeTell;
import de.unidue.inf.is.ezdl.dlcore.misc.EzDLException;
import de.unidue.inf.is.ezdl.dlcore.misc.TimeoutException;
import de.unidue.inf.is.ezdl.dlcore.security.Privilege;
import de.unidue.inf.is.ezdl.dlcore.security.SecurityException;



/**
 * This default implementation of a {@link SecurityManager} asks the user agent
 * wether a privilege request should be granted.
 * 
 * @author tbeckers
 */
public class DefaultSecurityManager implements SecurityManager {

    /**
     * Key in privilege cache
     * 
     * @author tbeckers
     */
    private static class Key {

        private Privilege privilege;
        private String sessionId;


        public Key(Privilege privilege, String sessionId) {
            super();
            this.privilege = privilege;
            this.sessionId = sessionId;
        }


        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((privilege == null) ? 0 : privilege.hashCode());
            result = prime * result + ((sessionId == null) ? 0 : sessionId.hashCode());
            return result;
        }


        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            if (privilege == null) {
                if (other.privilege != null) {
                    return false;
                }
            }
            else if (!privilege.equals(other.privilege)) {
                return false;
            }
            if (sessionId == null) {
                if (other.sessionId != null) {
                    return false;
                }
            }
            else if (!sessionId.equals(other.sessionId)) {
                return false;
            }
            return true;
        }

    }


    private Logger logger = Logger.getLogger(DefaultSecurityManager.class);

    /**
     * The cache of privileges, session ids and their granting state.
     */
    private Cache privilegesCache;
    /**
     * The agent to which this security manager belongs.
     */
    private Agent agent;
    /**
     * Keeps track if the manager is running or not.
     */
    private boolean running;


    /**
     * Constructor.
     * 
     * @param agent
     *            the agent that uses this security manager
     */
    public DefaultSecurityManager(Agent agent) {
        this.privilegesCache = new TimedCache(new ConcurrentMapCache(), 10, TimeUnit.MINUTES);
        this.agent = agent;
        this.running = true;
    }


    @Override
    public void check(Privilege privilege, String sessionId) throws SecurityException {
        Key key = key(privilege, sessionId);
        Boolean state = valueFromCache(key);
        if (state == null) {
            try {
                sendPrivilegeAskRequest(privilege, sessionId);
            }
            catch (TimeoutException e) {
                throw new SecurityException();
            }
            check(valueFromCache(key));
        }
        else {
            check(state);
        }
    }


    /**
     * Send message to user agent, receive reply and put into cache.
     * 
     * @param privilege
     *            The privilege
     * @param sessionId
     *            The session id
     * @throws TimeoutException
     *             If the time limit exceeded
     */
    private void sendPrivilegeAskRequest(Privilege privilege, String sessionId) throws TimeoutException {
        try {
            Message tell = agent.ask(new Message(agent.agentName(), agent.findAgent("/service/user"), new PrivilegeAsk(
                            privilege, sessionId), UUID.randomUUID().toString()));
            if (tell.getContent() instanceof PrivilegeTell) {
                PrivilegeTell privilegeTell = (PrivilegeTell) tell.getContent();
                privilegesCache.put(new Key(privilegeTell.getPrivilege(), privilegeTell.getSessionId()),
                                privilegeTell.isPermitted());
            }
            else {
                throw new TimeoutException();
            }
        }
        catch (EzDLException e) {
            logger.error(e.getMessage(), e);
        }
    }


    /**
     * Constructs a key for the cache
     * 
     * @param privilege
     *            A privilege
     * @param sessionId
     *            A session id
     * @return The key for the cache
     */
    private Key key(Privilege privilege, String sessionId) {
        Key key = new Key(privilege, sessionId);
        return key;
    }


    /**
     * Throws a security Exception if required.
     * 
     * @param state
     *            The granting state
     * @throws SecurityException
     *             If the state is not true
     */
    private void check(Boolean state) throws SecurityException {
        if (state == null || state == false) {
            throw new SecurityException();
        }
    }


    @Override
    public boolean has(Privilege privilege, String sessionId) {
        try {
            check(privilege, sessionId);
        }
        catch (SecurityException e) {
            return false;
        }
        return true;
    }


    /**
     * Returns a value from cache.
     * 
     * @param key
     *            The key
     * @return The vaue from cache
     */
    private Boolean valueFromCache(Key key) {
        Boolean state = (Boolean) privilegesCache.get(key);
        return state;
    }


    @Override
    public void halt() {
        privilegesCache.shutdown();
        running = false;
    }


    @Override
    public boolean isHalted() {
        return running;
    }

}
