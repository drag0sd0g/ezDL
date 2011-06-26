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

import java.util.Map;
import java.util.Properties;

import de.unidue.inf.is.ezdl.dlbackend.agent.connectors.AgentConnector;
import de.unidue.inf.is.ezdl.dlbackend.data.agent.AgentLog;
import de.unidue.inf.is.ezdl.dlbackend.data.agent.AgentStatus;
import de.unidue.inf.is.ezdl.dlbackend.data.agent.RequestHandlerInfo;
import de.unidue.inf.is.ezdl.dlbackend.message.Message;
import de.unidue.inf.is.ezdl.dlbackend.security.SecurityManager;
import de.unidue.inf.is.ezdl.dlcore.Haltable;
import de.unidue.inf.is.ezdl.dlcore.misc.ConnectionFailedException;
import de.unidue.inf.is.ezdl.dlcore.misc.EzDLException;
import de.unidue.inf.is.ezdl.dlcore.misc.TimeoutException;



/**
 * The interface that Agents have to implement.
 */
public interface Agent extends Haltable {

    /**
     * Initialize the agent.
     * 
     * @param agentName
     *            the name to use
     * @param props
     *            the properties object
     * @return true, if Agent was successfully initialized. Else false.
     */
    boolean init(String agentName, Properties props);


    /**
     * Initialize the agent.
     * 
     * @param agentName
     *            the name to use
     * @param securityManager
     *            the security manager
     * @param props
     *            the properties object
     * @return true, if Agent was successfully initialized. Else false.
     */
    boolean init(String agentName, SecurityManager securityManager, Properties props);


    /**
     * Set the {@link AgentConnector} to use for communication.
     * 
     * @param connector
     *            the connector
     */
    void setConnector(AgentConnector connector);


    /**
     * Return the name of the agent.
     * 
     * @return the agent's name
     */
    String agentName();


    /**
     * Returns the {@link SecurityManager}
     * 
     * @return the security manager
     */
    SecurityManager getSecurityManager();


    /**
     * Finds the name of an agent in the directory.
     * 
     * @param service
     *            the service name of the agent (e.g. "/service/mta")
     * @return the name of the agent (e.g. "MTA")
     * @throws EzDLException
     */
    String findAgent(String service) throws EzDLException;


    /**
     * Finds all agents that implement a subclass of a given service.
     * 
     * @param service
     *            the service root to look at (e.g. "/wrappers")
     * @return a list of agents that are under the given service name root.
     * @throws EzDLException
     */
    String[] findAllAgentNames(String service) throws EzDLException;


    /**
     * Tries to find the agents given by service names.
     * <p>
     * Ignores any error and returns an empty Map if error occurs.
     * 
     * @param services
     *            the service root to search
     * @return the unmodifiableMap of agents or empty one if any error happens
     * @since 23.11.2009
     */
    Map<String, String> findAgentsByService(String... services);


    /**
     * Finds all agents under a given service name root.
     * 
     * @param service
     *            the service to look at (e.g. "/wrappers")
     * @return a map of services to agent names of the agents that are under the
     *         given service name root.
     * @throws EzDLException
     */
    Map<String, String> findAllAgents(String service) throws EzDLException;


    /**
     * Returns the properties of the agent.
     * 
     * @return the properties
     */
    Properties getProperties();


    /**
     * Returns the agent log. Please note that this is <em>not</em> the logger
     * used in the agent but the message log that can be inspected using the
     * Directory web page.
     * 
     * @return the AgentLog object
     */
    AgentLog getLog();


    /**
     * Convenience method that returns a new unique request ID.
     * 
     * @return the new ID
     */
    String getNextRequestID();


    /**
     * Makes the agent go online.
     * 
     * @throws ConnectionFailedException
     */
    void goOnline() throws ConnectionFailedException;


    /**
     * Terminates the request with the ID given in requestID.
     * 
     * @param requestID
     *            the ID of the request to be killed
     */
    void killRequestHandler(String requestID, boolean sendPartialResults);


    /**
     * Processes any incoming message.
     * 
     * @param message
     *            the message
     * @return a message, if it is not handled in receive() or null
     */
    Message receive(Message message);


    /**
     * Sends a message.
     * 
     * @param message
     *            the message to send
     */
    void send(Message message);


    /**
     * Sends a message to multiple agents.
     * 
     * @param agentlist
     *            the list of agent names the message is to be sent to
     * @param message
     *            the message to send
     */
    void send(String[] agentlist, Message message);


    /**
     * Sends the passed message and waits for an answer.
     * 
     * @param message
     *            the message to send
     * @return the returned answer, which is never null
     * @throws TimeoutException
     *             signals that the answer to the message sent did not arrive
     *             within the given timeout.
     */
    Message ask(Message message) throws TimeoutException;


    /**
     * Returns the request map, that maps request IDs to RequestHandlerInfo
     * object, containing information about the running request handlers.
     * 
     * @return the request map
     */
    Map<String, RequestHandlerInfo> getRequestInfo();


    /**
     * Returns the name of the directory.
     * 
     * @return the directory's name
     */
    String getDirectoryName();


    /**
     * Retrieves the user ID for a session ID by asking the user agent.
     * 
     * @param sessionId
     *            the session ID to get the user information for
     * @return
     */
    int userIdForSessionId(String sessionId);


    /**
     * Returns the shared secret that the directory provided upon registration
     * in order to authenticate with the directory.
     * <p>
     * This is mainly used to prevent agents from erroneously deregistering
     * other agents.
     * 
     * @return the shared secret
     */
    String getSharedSecret();


    /**
     * Returns the status of the agent.
     * 
     * @return the status
     */
    AgentStatus getStatus();
}
