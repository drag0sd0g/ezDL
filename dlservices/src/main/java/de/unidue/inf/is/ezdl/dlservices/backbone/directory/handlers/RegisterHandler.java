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

import java.util.UUID;

import de.unidue.inf.is.ezdl.dlbackend.agent.Reusable;
import de.unidue.inf.is.ezdl.dlbackend.agent.StartedBy;
import de.unidue.inf.is.ezdl.dlbackend.agent.handlers.AbstractRequestHandler;
import de.unidue.inf.is.ezdl.dlbackend.data.agent.AgentRecord;
import de.unidue.inf.is.ezdl.dlbackend.data.agent.WrapperRecord;
import de.unidue.inf.is.ezdl.dlbackend.message.Message;
import de.unidue.inf.is.ezdl.dlbackend.message.content.RegisterAsk;
import de.unidue.inf.is.ezdl.dlbackend.message.content.RegisterTell;
import de.unidue.inf.is.ezdl.dlcore.EzDLConstants;
import de.unidue.inf.is.ezdl.dlcore.data.wrappers.WrapperInfo;
import de.unidue.inf.is.ezdl.dlcore.message.content.ErrorNotify;
import de.unidue.inf.is.ezdl.dlcore.message.content.MessageContent;
import de.unidue.inf.is.ezdl.dlservices.backbone.directory.Directory;
import de.unidue.inf.is.ezdl.dlservices.backbone.directory.cache.AgentCache;



/**
 * Processes the registration of an agent.
 * 
 * @author mjordan
 */
@Reusable
@StartedBy(RegisterAsk.class)
public class RegisterHandler extends AbstractRequestHandler {

    @Override
    protected boolean work(Message message) {
        boolean handled = true;
        MessageContent content = message.getContent();

        if (content instanceof RegisterAsk) {
            RegisterAsk register = (RegisterAsk) content;
            process(message, register);
        }
        else {
            handled = false;
        }

        return handled;
    }


    private synchronized void process(Message message, RegisterAsk content) {
        Directory directory = (Directory) getAgent();
        AgentCache agentCache = directory.getAgentCache();

        String version = content.getVersion();
        if (!EzDLConstants.CLIENT_VERSION.equals(version)) {
            send(message.tell(new ErrorNotify("version")));
        }
        else {
            AgentRecord agentInfo = null;
            final String agentName = message.getFrom();
            final String service = content.getService();
            final WrapperInfo wrapperInfo = content.getWrapperInfo();
            if (wrapperInfo != null) {
                agentInfo = new WrapperRecord(service, agentName, wrapperInfo);
            }
            else {
                agentInfo = new AgentRecord(service, agentName);
            }
            final String sharedSecret = generateSharedSecret();
            agentInfo.setSharedSecret(sharedSecret);

            final boolean added = agentCache.addAgent(agentInfo);

            if (added) {
                send(message.tell(new RegisterTell(sharedSecret)));
            }
            else {
                /*
                 * We do not send a reply because this is probably a situation
                 * where an agent erroneously tries to register under the same
                 * name as an already active agent. In this situation, the reply
                 * would go to the already registered agent, which would just
                 * not make any sense.
                 */
            }
        }
    }


    String generateSharedSecret() {
        return UUID.randomUUID().toString();
    }

}
