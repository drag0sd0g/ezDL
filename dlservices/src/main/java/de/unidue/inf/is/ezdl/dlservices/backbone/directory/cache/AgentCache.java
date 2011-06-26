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

package de.unidue.inf.is.ezdl.dlservices.backbone.directory.cache;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import de.unidue.inf.is.ezdl.dlbackend.data.agent.AgentRecord;
import de.unidue.inf.is.ezdl.dlbackend.data.agent.WrapperRecord;
import de.unidue.inf.is.ezdl.dlcore.utils.StringUtils;



/**
 * This class keeps information about agents.
 * <p>
 * Agents are identified by their unique name and organized by a service name
 * that looks similar to a Unix path name (e.g. "/wrappers/dl/acm").
 * 
 * @author mjordan
 */
public class AgentCache {

    /**
     * The logger.
     */
    private static final Logger logger = Logger.getLogger(AgentCache.class);

    private final Map<String, AgentRecord> agentMap = new HashMap<String, AgentRecord>();


    private String getKey(AgentRecord agentInfo) {
        return agentInfo.getName();
    }


    /**
     * Adds an AgentRecord to the cache.
     * 
     * @param agentInfo
     *            the information to add
     * @return true if the agent information was added. Else false.
     */
    public boolean addAgent(AgentRecord agentInfo) {
        boolean addedAgent = false;
        final String key = getKey(agentInfo);
        final boolean serviceSet = !StringUtils.isEmpty(agentInfo.getService());
        if (!key.isEmpty() && !agentMap.containsKey(key) && serviceSet) {
            agentMap.put(key, agentInfo);
            addedAgent = true;
        }
        return addedAgent;
    }


    /**
     * Returns the internal map between service names and AgentRecords.
     * 
     * @return the map
     */
    public Map<String, AgentRecord> getDirMap() {
        return agentMap;
    }


    /**
     * Removes an agent by its name.
     * 
     * @param agentName
     *            the name of the agent to remove
     * @param sharedSecret
     *            the secret that has to match the agent's record in order to
     *            actually remove the record
     * @return true, if the agent was removed. Else false.
     */
    public boolean removeAgent(String agentName, String sharedSecret) {
        boolean success = false;
        if (agentName != null && agentMap.containsKey(agentName)) {
            AgentRecord agent = agentMap.get(agentName);
            if (sharedSecretOkay(sharedSecret, agent)) {
                agentMap.remove(agentName);
                success = true;
            }
        }
        return success;
    }


    /**
     * Removes an agent by its name regardless of the shared secret.
     * 
     * @param agentName
     *            the name of the agent to remove
     * @return true, if the agent was removed. Else false.
     */
    public boolean removeAgent(String agentName) {
        boolean success = false;
        if (agentName != null && agentMap.containsKey(agentName)) {
            agentMap.remove(agentName);
            success = true;
        }
        return success;
    }


    private boolean sharedSecretOkay(String sharedSecret, AgentRecord agent) {
        return !StringUtils.isEmpty(sharedSecret) && agent.getSharedSecret().equals(sharedSecret);
    }


    /**
     * Returns a list of the agents that are registered under a given service.
     * 
     * @param serviceRoot
     *            the service the agents registered under are to be retrieved
     * @return the list of agents which might be empty, but never null
     */
    public List<AgentRecord> createAgentList(String serviceRoot) {
        List<AgentRecord> list = new LinkedList<AgentRecord>();

        for (AgentRecord agent : agentMap.values()) {
            final String service = agent.getService();
            if (!StringUtils.isEmpty(service) && service.startsWith(serviceRoot)) {
                list.add(agent);
            }
        }
        return list;
    }


    /**
     * Retrieves the AgentRecord for an agent identified by the name.
     * 
     * @param nameStr
     *            the name of the agent
     * @return the AgentRecord or null if no agent is found with the given name
     */
    public AgentRecord getByName(String nameStr) {
        AgentRecord service = null;
        if (agentMap.containsKey(nameStr)) {
            service = agentMap.get(nameStr);
        }
        return service;
    }


    /**
     * Retrieves the AgentRecord for an agent identified by the service.
     * 
     * @param service
     *            the service name of the agent
     * @return the AgentRecord or null if no agent is found with the given
     *         service
     */
    public AgentRecord getByService(String service) {
        for (AgentRecord agent : agentMap.values()) {
            if (agent.getService().equals(service)) {
                return agent;
            }
        }
        return null;
    }


    /**
     * Returns the list of wrappers that are registered.
     * 
     * @return the list of wrappers
     */
    public List<WrapperRecord> getWrappers() {
        List<WrapperRecord> wrappers = new LinkedList<WrapperRecord>();
        List<AgentRecord> agents = createAgentList("/");
        for (AgentRecord agent : agents) {
            if (agent instanceof WrapperRecord) {
                WrapperRecord wrapperAgent = (WrapperRecord) agent;
                wrappers.add(wrapperAgent);
            }
        }
        return wrappers;
    }


    /**
     * Returns if an agent given by its name is registered.
     * 
     * @param agentName
     *            the name of the agent to look up
     * @return true if the agent is active. Else false.
     */
    public boolean isAgentRegistered(String agentName) {
        boolean registered = false;
        for (AgentRecord agent : agentMap.values()) {
            if (agent.getName().equals(agentName)) {
                registered = true;
                break;
            }
        }
        logger.debug("Agent " + agentName + " is active: " + registered);
        return registered;
    }
}
