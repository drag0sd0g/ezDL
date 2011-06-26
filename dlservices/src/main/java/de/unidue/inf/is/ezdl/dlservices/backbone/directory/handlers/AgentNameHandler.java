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

package de.unidue.inf.is.ezdl.dlservices.backbone.directory.handlers;

import de.unidue.inf.is.ezdl.dlbackend.agent.Reusable;
import de.unidue.inf.is.ezdl.dlbackend.agent.StartedBy;
import de.unidue.inf.is.ezdl.dlbackend.agent.handlers.AbstractRequestHandler;
import de.unidue.inf.is.ezdl.dlbackend.data.agent.AgentRecord;
import de.unidue.inf.is.ezdl.dlbackend.message.Message;
import de.unidue.inf.is.ezdl.dlbackend.message.content.AgentNameAsk;
import de.unidue.inf.is.ezdl.dlbackend.message.content.AgentNameTell;
import de.unidue.inf.is.ezdl.dlcore.message.content.ErrorNotify;
import de.unidue.inf.is.ezdl.dlcore.message.content.MessageContent;
import de.unidue.inf.is.ezdl.dlservices.backbone.directory.Directory;
import de.unidue.inf.is.ezdl.dlservices.backbone.directory.cache.AgentCache;



/**
 * Processes an {@link AgentNameAsk} message by answering with the info record
 * about the agent.
 * 
 * @author mjordan
 */
@Reusable
@StartedBy(AgentNameAsk.class)
public class AgentNameHandler extends AbstractRequestHandler {

    @Override
    protected boolean work(Message message) {
        boolean handled = true;
        MessageContent content = message.getContent();

        if (content instanceof AgentNameAsk) {
            AgentNameAsk agentNameAsk = (AgentNameAsk) content;
            process(message, agentNameAsk);
        }
        else {
            handled = false;
        }

        return handled;
    }


    private void process(Message message, AgentNameAsk content) {
        Directory directory = (Directory) getAgent();
        AgentCache agentCache = directory.getAgentCache();

        String service = content.getService();

        AgentRecord agent = agentCache.getByService(service);
        if (agent != null) {
            sendAnswer(message, agent.getName(), service);
        }
        else {
            send(message.tell(new ErrorNotify("Unkown Service.")));
        }
    }


    /**
     * Sends agent information (name and service) in an answer to the given
     * message.
     * 
     * @param message
     *            the message
     * @param agent
     *            the agent the information is about
     * @param service
     *            the service of the agent
     */
    private void sendAnswer(Message message, final String agent, final String service) {
        AgentRecord agentInfo = new AgentRecord(service, agent);
        AgentNameTell content = new AgentNameTell(agentInfo);
        Message answer = message.tell(content);
        send(answer);
    }

}
