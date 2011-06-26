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

import java.util.List;

import de.unidue.inf.is.ezdl.dlbackend.agent.Reusable;
import de.unidue.inf.is.ezdl.dlbackend.agent.StartedBy;
import de.unidue.inf.is.ezdl.dlbackend.agent.handlers.AbstractRequestHandler;
import de.unidue.inf.is.ezdl.dlbackend.data.agent.AgentRecord;
import de.unidue.inf.is.ezdl.dlbackend.message.Message;
import de.unidue.inf.is.ezdl.dlbackend.message.content.AgentNameAllAsk;
import de.unidue.inf.is.ezdl.dlcore.message.content.MessageContent;
import de.unidue.inf.is.ezdl.dlservices.backbone.directory.Directory;



/**
 * Processes an {@link AgentNameAllAsk} message by answering with the list of
 * records about registered agents.
 * 
 * @author mjordan
 */
@Reusable
@StartedBy(AgentNameAllAsk.class)
public class AgentNameAllHandler extends AbstractRequestHandler {

    @Override
    protected boolean work(Message message) {
        boolean handled = true;
        MessageContent content = message.getContent();

        if (content instanceof AgentNameAllAsk) {
            AgentNameAllAsk agentNameAllAsk = (AgentNameAllAsk) content;
            process(message, agentNameAllAsk);
        }
        else {
            handled = false;
        }

        return handled;
    }


    private void process(Message message, AgentNameAllAsk content) {
        final Directory directory = (Directory) getAgent();
        final String service = content.getPath();
        final List<AgentRecord> agentMap = directory.getAgentList(service);
        directory.sendAnswer(message, agentMap);
    }

}
