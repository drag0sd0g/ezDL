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

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import de.unidue.inf.is.ezdl.dlbackend.agent.Agent;
import de.unidue.inf.is.ezdl.dlbackend.agent.connectors.AgentConnector;
import de.unidue.inf.is.ezdl.dlbackend.message.Message;
import de.unidue.inf.is.ezdl.dlcore.misc.ConnectionFailedException;



/**
 * Connector for the ORBMock mock ORB.
 * 
 * @author mjordan
 */
public class MockAgentConnector extends Thread implements AgentConnector {

    protected final Logger logger = Logger.getLogger(MockAgentConnector.class);

    private MockORB orb;

    private Agent agent;

    private List<Message> messagesSent;

    private boolean isOnline = false;


    public MockAgentConnector(MockORB orb, Agent agent) {
        this.orb = orb;
        this.agent = agent;
        clearMessagesSent();
    }


    @Override
    public void run() {
        orb.connectAgent(agent.agentName(), agent);
        while (true) {
            try {
                sleep(10);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void goOnline() throws ConnectionFailedException {
        start();
        isOnline = true;
    }


    @Override
    public void send(Message message) {
        messagesSent.add(message);
        orb.sendMessage(message);
    }


    @Override
    public void goOffline() {
        orb.disconnectAgent(agent);
    }


    public List<Message> getMessagesSent() {
        return messagesSent;
    }


    public void clearMessagesSent() {
        messagesSent = new LinkedList<Message>();
    }


    @Override
    public boolean isOnline() {
        return isOnline;
    }
}
