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

import java.io.CharConversionException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import de.unidue.inf.is.ezdl.dlbackend.data.agent.AgentRecord;
import de.unidue.inf.is.ezdl.dlbackend.message.Message;
import de.unidue.inf.is.ezdl.dlbackend.message.content.AgentNameAllAsk;
import de.unidue.inf.is.ezdl.dlbackend.message.content.AgentNameAllTell;
import de.unidue.inf.is.ezdl.dlbackend.message.content.AgentNameAsk;
import de.unidue.inf.is.ezdl.dlbackend.message.content.AgentNameTell;
import de.unidue.inf.is.ezdl.dlcore.message.content.MessageContent;
import de.unidue.inf.is.ezdl.dlcore.misc.EzDLException;



/**
 * The AgentNameResolver takes care of resolving {@link Agent} names and caching
 * them for more speed and less traffic.
 * 
 * @author mjordan
 */
class AgentNameResolver {

    /**
     * The logger.
     */
    private final Logger logger = Logger.getLogger(getClass());
    /**
     * A cache of agent names. Maps service names like "/service/mta" to agent
     * names like "MTA".
     */
    private static volatile Map<String, String> nameCache = new HashMap<String, String>();
    /**
     * Reference to the agent who owns this resolver.
     */
    private AbstractAgent agent;


    /**
     * Creates a new resolver for the given agent.
     * 
     * @param agent
     *            the agent to work for
     */
    public AgentNameResolver(AbstractAgent agent) {
        this.agent = agent;
    }


    /**
     * /** Finds the name of an agent in the directory.
     * <p>
     * This implementation of {@link #findAgent(String)} is cached and only
     * contacts the directory if no information is locally available.
     * 
     * @param service
     *            the service name of the agent (e.g. "/service/mta")
     * @return the name of the agent (e.g. "MTA")
     * @throws EzDLException
     */
    public String findAgent(String service) throws EzDLException {
        if (!nameCache.containsKey(service)) {
            synchronized (this) {
                if (!nameCache.containsKey(service)) {
                    String requestId = agent.getNextRequestID();
                    MessageContent ask = new AgentNameAsk(service);
                    Message message = agent.createDirMessage(ask, requestId);

                    Message dirMsg = agent.ask(message);

                    MessageContent content = dirMsg.getContent();
                    if (content instanceof AgentNameTell) {
                        AgentNameTell tell = (AgentNameTell) content;
                        String name = tell.getAgentInfo().getName();
                        nameCache.put(service, name);
                    }
                }
            }
        }
        return nameCache.get(service);
    }


    /**
     * Try to find the agents given by service. Ignore any error and return
     * empty Map if error occurs.
     * 
     * @param services
     *            the service where to search
     * @return the unmodifiableMap of agents or empty one if any error happens
     */
    public Map<String, String> findAgentsByService(String... services) {
        Map<String, String> map = new HashMap<String, String>();
        for (String service : services) {
            if (logger.isDebugEnabled()) {
                if (service.endsWith("/")) {
                    CharConversionException cce = new CharConversionException();
                    cce.fillInStackTrace();
                    logger.debug("remove last / in " + service, cce);
                }
            }
            try {
                map.putAll(findAllAgents(service));
            }
            catch (EzDLException e) {
                logger.error("findAllAgents() went wrong: ", e);
            }
        }
        return Collections.unmodifiableMap(map);
    }


    /**
     * Finds all agents that implement a subclass of a given service.
     * 
     * @param service
     *            the service root to look at (e.g. "/wrappers")
     * @return a list of agents that are under the given service name root.
     * @throws EzDLException
     */
    public synchronized String[] findAllAgentNames(String service) throws EzDLException {
        Map<String, String> agents = findAllAgents(service);
        return agents.values().toArray(new String[agents.values().size()]);
    }


    /**
     * Finds all agents under a given service name root.
     * 
     * @param service
     *            the service to look at (e.g. "/wrappers")
     * @return a map of services to agent names of the agents that are under the
     *         given service name root.
     * @throws EzDLException
     */
    public synchronized Map<String, String> findAllAgents(String service) throws EzDLException {
        String requestId = agent.getNextRequestID();

        AgentNameAllAsk ask = new AgentNameAllAsk(service);
        Message message = agent.createDirMessage(ask, requestId);

        Message dirMsg = agent.ask(message);

        MessageContent content = dirMsg.getContent();
        if (!(content instanceof AgentNameAllTell)) {
            throw new EzDLException("Received unexpected message content type");
        }

        Collection<AgentRecord> agentRecords = ((AgentNameAllTell) content).getAgentList();

        Map<String, String> agents = new HashMap<String, String>();
        for (AgentRecord agent : agentRecords) {
            agents.put(agent.getService(), agent.getName());
        }
        return agents;
    }

}
