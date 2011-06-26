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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import de.unidue.inf.is.ezdl.dlbackend.agent.Agent;
import de.unidue.inf.is.ezdl.dlbackend.message.Message;



/**
 * Serves to mock the ORB for easier testing of agent communication.
 * 
 * @author mjordan
 */
public class MockORB {

    private Map<String, Agent> agents = new HashMap<String, Agent>();

    private List<Message> sentMessages = new LinkedList<Message>();

    /**
     * The logger.
     */
    protected final Logger logger = Logger.getLogger(MockORB.class);


    public void connectAgent(String name, Agent agent) {
        logger.info("Connecting " + agent.agentName());
        agents.put(name, agent);
    }


    public void sendMessage(Message message) {
        sentMessages.add(message);
        logger.info("Sending message " + message.getContent());
        String receiver = message.getTo();
        Agent agentTo = agents.get(receiver);
        if (agentTo != null) {
            agentTo.receive(message);
        }
        else {
            logger.info("Could not deliver message <" + message.getContent() + " to unknown agent " + receiver);
        }
    }


    public void disconnectAgent(Agent agent) {
        logger.info("Disconnecting " + agent.agentName());
        agents.remove(agent);
    }


    public List<Message> getSentMessages() {
        return sentMessages;
    }
}
