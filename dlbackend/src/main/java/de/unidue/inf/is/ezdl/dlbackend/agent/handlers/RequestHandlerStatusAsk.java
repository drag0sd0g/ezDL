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

package de.unidue.inf.is.ezdl.dlbackend.agent.handlers;

import de.unidue.inf.is.ezdl.dlbackend.agent.Reusable;
import de.unidue.inf.is.ezdl.dlbackend.agent.StartedBy;
import de.unidue.inf.is.ezdl.dlbackend.data.agent.AgentStatus;
import de.unidue.inf.is.ezdl.dlbackend.message.Message;
import de.unidue.inf.is.ezdl.dlbackend.message.content.StatusAsk;
import de.unidue.inf.is.ezdl.dlbackend.message.content.StatusTell;
import de.unidue.inf.is.ezdl.dlcore.message.content.MessageContent;



/**
 * Handles {@link StatusAsk} messages by replying with a {@link StatusTell}
 * message.
 * 
 * @author mjordan
 */
@Reusable
@StartedBy(StatusAsk.class)
public class RequestHandlerStatusAsk extends AbstractRequestHandler {

    @Override
    public boolean work(Message message) {
        boolean handled = true;
        MessageContent content = message.getContent();

        if (content instanceof StatusAsk) {
            AgentStatus status = getAgent().getStatus();
            send(message.tell(new StatusTell(status)));
        }
        else {
            handled = false;
        }

        return handled;
    }

}
