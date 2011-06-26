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
import de.unidue.inf.is.ezdl.dlbackend.message.Message;
import de.unidue.inf.is.ezdl.dlbackend.message.content.DeregisterAsk;
import de.unidue.inf.is.ezdl.dlcore.message.content.ErrorNotify;
import de.unidue.inf.is.ezdl.dlcore.message.content.MessageContent;
import de.unidue.inf.is.ezdl.dlservices.backbone.directory.Directory;
import de.unidue.inf.is.ezdl.dlservices.backbone.directory.cache.AgentCache;



/**
 * Processes the deregistation of an agent.
 * 
 * @author mjordan
 */
@Reusable
@StartedBy(DeregisterAsk.class)
public class DeregisterHandler extends AbstractRequestHandler {

    @Override
    protected boolean work(Message message) {
        boolean handled = true;
        MessageContent content = message.getContent();
        if (content instanceof DeregisterAsk) {
            DeregisterAsk deregister = (DeregisterAsk) content;
            process(message, deregister);
        }
        else {
            handled = false;
        }

        return handled;
    }


    private synchronized void process(Message message, DeregisterAsk content) {
        Directory directory = (Directory) getAgent();
        AgentCache agentCache = directory.getAgentCache();

        boolean successfullyRemoved = agentCache.removeAgent(message.getFrom(), content.getSharedSecret());
        if (!successfullyRemoved) {
            send(message.tell(new ErrorNotify("Unknown Service.")));
        }
    }

}
