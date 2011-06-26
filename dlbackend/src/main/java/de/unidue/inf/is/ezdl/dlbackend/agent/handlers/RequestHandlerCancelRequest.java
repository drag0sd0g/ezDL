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

import de.unidue.inf.is.ezdl.dlbackend.agent.Agent;
import de.unidue.inf.is.ezdl.dlbackend.agent.StartedBy;
import de.unidue.inf.is.ezdl.dlbackend.message.Message;
import de.unidue.inf.is.ezdl.dlbackend.message.content.CancelRequestNotify;
import de.unidue.inf.is.ezdl.dlcore.message.content.MessageContent;



/**
 * Handler for CancelRequestNotify messages. Calls Agent.killRequestHandler().
 */
@StartedBy(CancelRequestNotify.class)
public class RequestHandlerCancelRequest extends AbstractRequestHandler {

    @Override
    protected boolean work(Message message) {
        boolean handled = true;
        MessageContent messageContent = message.getContent();

        if (messageContent instanceof CancelRequestNotify) {
            handle((CancelRequestNotify) messageContent);
        }
        else {
            handled = false;
        }
        halt();
        return handled;
    }


    private void handle(CancelRequestNotify messageContent) {
        getLogger().debug("Canceling request");
        final Agent agent = getAgent();
        final String requestId = messageContent.getRequestID();
        final boolean sendPartials = messageContent.isSendPartialResults();
        agent.killRequestHandler(requestId, sendPartials);
    }

}
