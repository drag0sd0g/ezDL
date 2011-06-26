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

package de.unidue.inf.is.ezdl.dlservices.log.handlers;

import org.apache.log4j.Logger;

import de.unidue.inf.is.ezdl.dlbackend.agent.StartedBy;
import de.unidue.inf.is.ezdl.dlbackend.agent.handlers.AbstractRequestHandler;
import de.unidue.inf.is.ezdl.dlbackend.message.Message;
import de.unidue.inf.is.ezdl.dlcore.log.UserLogConstants;
import de.unidue.inf.is.ezdl.dlcore.message.content.MessageContent;
import de.unidue.inf.is.ezdl.dlcore.message.content.UserLogNotify;
import de.unidue.inf.is.ezdl.dlcore.utils.StringUtils;
import de.unidue.inf.is.ezdl.dlservices.log.UserLogAgent;
import de.unidue.inf.is.ezdl.dlservices.log.store.UserLogStore;



/**
 * Handles incoming user log events.
 * 
 * @author tbeckers
 */
@StartedBy(UserLogNotify.class)
public class UserLogNotifyHandler extends AbstractRequestHandler {

    private static Logger logger = Logger.getLogger(UserLogNotifyHandler.class);


    @Override
    protected boolean work(Message message) {
        boolean handled = true;

        final MessageContent content = message.getContent();

        if (content instanceof UserLogNotify) {
            handle(message, (UserLogNotify) content);
        }
        else {
            handled = false;
        }
        halt();
        return handled;
    }


    private void handle(Message message, UserLogNotify logNotify) {
        final String eventName = logNotify.getEventName();
        if (eventName.equals(UserLogConstants.EVENT_NAME_SESSION_START)) {
            final String login = logNotify.getSingleParameter("login");
            if (!StringUtils.isEmpty(login)) {
                final String type = logNotify.getSingleParameter("type");
                getUserLogStore().login(logNotify.getSessionId(), login, logNotify.getBackendTimestamp(), type);
            }
            else {
                logger.error("login in log session start message is empty!");
            }
        }
        else if (eventName.equals(UserLogConstants.EVENT_NAME_SESSION_END)) {
            getUserLogStore().logout(logNotify.getSessionId(), logNotify.getBackendTimestamp());
        }
        else {
            getUserLogStore().storeUserLog(logNotify);
        }
    }


    private UserLogStore getUserLogStore() {
        return ((UserLogAgent) getAgent()).getUserLogStore();
    }

}
