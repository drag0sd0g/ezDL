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

package de.unidue.inf.is.ezdl.dlbackend.mock;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import de.unidue.inf.is.ezdl.dlbackend.agent.Agent;
import de.unidue.inf.is.ezdl.dlbackend.agent.connectors.AgentConnector;
import de.unidue.inf.is.ezdl.dlbackend.data.agent.AgentLog;
import de.unidue.inf.is.ezdl.dlbackend.data.agent.AgentStatus;
import de.unidue.inf.is.ezdl.dlbackend.data.agent.RequestHandlerInfo;
import de.unidue.inf.is.ezdl.dlbackend.data.agent.StringAgentStatus;
import de.unidue.inf.is.ezdl.dlbackend.message.Message;
import de.unidue.inf.is.ezdl.dlbackend.security.SecurityManager;
import de.unidue.inf.is.ezdl.dlcore.misc.ConnectionFailedException;
import de.unidue.inf.is.ezdl.dlcore.misc.EzDLException;
import de.unidue.inf.is.ezdl.dlcore.misc.TimeoutException;



public class MockAgent implements Agent {

    public static final String REQID = "reqid";

    private final Logger logger = Logger.getLogger(MockAgent.class);

    private List<Message> messagesSent = new LinkedList<Message>();
    private String name = "MockAgent";
    private SecurityManager securityManager = new MockSecurityManager();
    private boolean running = true;

    private Message nextAnswer;

    Properties properties = new Properties();


    public MockAgent() {
        super();
    }


    public MockAgent(String name) {
        this.name = name;
    }


    @Override
    public String agentName() {
        return name;
    }


    @Override
    public String findAgent(String service) throws EzDLException {
        logger.debug("findAgent(String " + service + ")");
        return null;
    }


    @Override
    public Map<String, String> findAgentsByService(String... services) {
        logger.debug("findAgentsByService(String... " + services + ")");
        return null;
    }


    @Override
    public String[] findAllAgentNames(String service) throws EzDLException {
        logger.debug("findAllAgentNames(String " + service + ")");
        return null;
    }


    @Override
    public Map<String, String> findAllAgents(String service) throws EzDLException {
        logger.debug("findAllAgents(String " + service + ")");
        return null;
    }


    @Override
    public AgentLog getLog() {
        logger.debug("getLog()");
        return null;
    }


    @Override
    public String getNextRequestID() {
        logger.debug("getNextRequestID()");
        return REQID;
    }


    @Override
    public Properties getProperties() {
        logger.debug("getProperties()");
        return properties;
    }


    @Override
    public void goOnline() throws ConnectionFailedException {
        logger.debug("goOnline()");
    }


    @Override
    public void halt() {
        logger.debug("halt()");
        running = false;
    }


    @Override
    public boolean init(String agentName, Properties props) {
        logger.debug("init(String " + agentName + ", Properties props)");
        this.properties = props;
        this.name = agentName;
        return true;
    }


    @Override
    public boolean init(String agentName, SecurityManager securityManager, Properties props) {
        logger.debug("init(String " + agentName + ", SecurityManager securityManager, Properties props)");
        return init(agentName, null, props);
    }


    @Override
    public void killRequestHandler(String requestID, boolean sendPartials) {
        logger.debug("killRequest(String " + requestID + ", " + sendPartials + ")");
    }


    @Override
    public Message receive(Message message) {
        logger.debug("receive(Message " + message + ")");
        return message;
    }


    @Override
    public void send(Message message) {
        logger.debug("SENT: " + message);
        messagesSent.add(message);
    }


    @Override
    public void send(String[] agentlist, Message message) {
        for (String agent : agentlist) {
            Message msg = new Message(message);
            msg.setTo(agent);
            send(msg);
        }
    }


    @Override
    public void setConnector(AgentConnector connector) {
        logger.debug("setClient(AgentConnector connector)");
    }


    /**
     * @return the messagesSent
     */
    public List<Message> getMessagesSent() {
        return messagesSent;
    }


    public void clearMessagesSent() {
        messagesSent.clear();
    }


    @Override
    public String getDirectoryName() {
        return "directoryName";
    }


    @Override
    public Map<String, RequestHandlerInfo> getRequestInfo() {
        return Collections.emptyMap();
    }


    @Override
    public boolean isHalted() {
        return !running;
    }


    @Override
    public SecurityManager getSecurityManager() {
        return securityManager;
    }


    @Override
    public Message ask(Message message) throws TimeoutException {
        send(message);
        return nextAnswer;
    }


    public void setNextAskAnswer(Message nextAnswer) {
        this.nextAnswer = nextAnswer;
    }


    @Override
    public int userIdForSessionId(String sessionId) {
        return -1;
    }


    @Override
    public String getSharedSecret() {
        return null;
    }


    @Override
    public AgentStatus getStatus() {
        return new StringAgentStatus("OK");
    }
}
