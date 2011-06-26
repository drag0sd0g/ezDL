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

package de.unidue.inf.is.ezdl.dlservices.mock;

import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import de.unidue.inf.is.ezdl.dlbackend.agent.connectors.AgentConnector;
import de.unidue.inf.is.ezdl.dlbackend.data.agent.AgentLog;
import de.unidue.inf.is.ezdl.dlbackend.message.Message;
import de.unidue.inf.is.ezdl.dlbackend.mock.MockAgent;
import de.unidue.inf.is.ezdl.dlcore.misc.ConnectionFailedException;
import de.unidue.inf.is.ezdl.dlcore.misc.EzDLException;



public class MockDirectoryAgent extends MockAgent {

    private final Logger logger = Logger.getLogger(MockAgent.class);


    @Override
    public String agentName() {
        return "MockDA";
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
        return null;
    }


    @Override
    public Properties getProperties() {
        logger.debug("getProperties()");
        return null;
    }


    @Override
    public void goOnline() throws ConnectionFailedException {
        logger.debug("goOnline()");
    }


    @Override
    public void halt() {
        logger.debug("halt()");
    }


    @Override
    public boolean init(String agentName, Properties props) {
        logger.debug("init(String " + agentName + ", Properties props)");
        return true;
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
        logger.debug("send(Message " + message + ")");
    }


    @Override
    public void send(String[] agentlist, Message message) {
        logger.debug("send(String[] " + agentlist + ", Message " + message + ")");
    }


    @Override
    public void setConnector(AgentConnector connector) {
        logger.debug("setClient(AgentConnector connector)");
    }

}
