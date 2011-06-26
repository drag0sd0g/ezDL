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

package de.unidue.inf.is.ezdl.dlservices.backbone.directory;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import de.unidue.inf.is.ezdl.dlbackend.agent.AbstractAgent;
import de.unidue.inf.is.ezdl.dlbackend.agent.handlers.RequestHandler;
import de.unidue.inf.is.ezdl.dlbackend.data.agent.AgentRecord;
import de.unidue.inf.is.ezdl.dlbackend.message.Message;
import de.unidue.inf.is.ezdl.dlbackend.message.content.AgentNameAllTell;
import de.unidue.inf.is.ezdl.dlbackend.message.content.KillAsk;
import de.unidue.inf.is.ezdl.dlcore.message.content.MessageContent;
import de.unidue.inf.is.ezdl.dlcore.misc.TimeoutException;
import de.unidue.inf.is.ezdl.dlservices.backbone.directory.cache.AgentCache;
import de.unidue.inf.is.ezdl.dlservices.backbone.directory.handlers.AgentNameAllHandler;
import de.unidue.inf.is.ezdl.dlservices.backbone.directory.handlers.AgentNameHandler;
import de.unidue.inf.is.ezdl.dlservices.backbone.directory.handlers.DeregisterHandler;
import de.unidue.inf.is.ezdl.dlservices.backbone.directory.handlers.RegisterHandler;
import de.unidue.inf.is.ezdl.dlservices.backbone.directory.handlers.WrapperDirectoryHandler;



/**
 * The Directory is the central agent that allows other agents to register,
 * deregister and find other agents based on service names.
 */
public final class Directory extends AbstractAgent {

    /**
     * Store for information about the agents the directory knows about.
     */
    private AgentCache agentCache = new AgentCache();
    /**
     * Reference to the web server.
     */
    private DirectoryWeb web;


    @Override
    protected Set<Class<? extends RequestHandler>> setupRequestHandlers() {
        Set<Class<? extends RequestHandler>> handlers = super.setupRequestHandlers();
        handlers.add(AgentNameAllHandler.class);
        handlers.add(AgentNameHandler.class);
        handlers.add(DeregisterHandler.class);
        handlers.add(RegisterHandler.class);
        handlers.add(WrapperDirectoryHandler.class);
        return handlers;
    }


    @Override
    public void initOnline() {
        web = new DirectoryWeb(this);
    }


    /**
     * Sends a MessageContent object to a remote agent using a message. Then it
     * waits for the result and returns it.
     * 
     * @param content
     *            the content to send
     * @param agent
     *            the agent to send the content to
     * @return the content of the message in the answer of the agent asked or
     *         null if no such message was received.
     * @throws TimeoutException
     */
    public synchronized MessageContent ask(MessageContent content, String agent) throws TimeoutException {
        final String ds = getNextRequestID();

        send(new Message(agentName(), agent, content, ds));

        Message answer = getMessageWaiter().waitForRequestId(ds);
        MessageContent out = null;
        if (answer != null) {
            out = answer.getContent();
        }
        return out;
    }


    @Override
    public void halt() {
        super.halt();
        if (web != null) {
            web.halt();
        }
    }


    @Override
    public synchronized void deregisterAgent() {
        getLogger().info("Not actually deregistering because I am the Directory.");
    }


    /**
     * Kills a remove agent whose name is given.
     * 
     * @param nameStr
     *            the name of the agent
     */
    public void killThisAgent(String nameStr) {
        AgentRecord agentToKill = agentCache.getByName(nameStr);

        if (agentToKill != null) {
            agentCache.removeAgent(agentToKill.getName());

            KillAsk content = new KillAsk(agentToKill.getSharedSecret());
            String ds = getNextRequestID();

            send(new Message(agentName(), agentToKill.getName(), content, ds));
        }
    }


    /**
     * Sends agent information about multiple agents in an answer to the given
     * message.
     * 
     * @param message
     *            the message to answer
     * @param agents
     *            the agent information
     */
    public void sendAnswer(Message message, Collection<AgentRecord> agents) {
        AgentNameAllTell content = new AgentNameAllTell(agents);
        Message answer = message.tell(content);
        send(answer);
    }


    /**
     * Returns the list of agents under the given service prefix (i.e. all
     * agents whose service name starts with the given prefix).
     * 
     * @param serviceRoot
     *            the service name root
     * @return the list of agent information objects
     */
    public List<AgentRecord> getAgentList(String serviceRoot) {
        return agentCache.createAgentList(serviceRoot);
    }


    @Override
    public String getServiceName() {
        throw new UnsupportedOperationException("Not implemented");
    }


    /**
     * Returns the agent cache that has information about the registered agents.
     * 
     * @return the reference to the agent cache
     */
    public AgentCache getAgentCache() {
        return agentCache;
    }


    public boolean isAgentRegistered(String agentName) {
        return agentCache.isAgentRegistered(agentName);
    }
}
