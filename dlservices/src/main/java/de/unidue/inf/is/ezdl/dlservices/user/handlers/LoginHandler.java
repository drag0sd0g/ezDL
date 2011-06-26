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

package de.unidue.inf.is.ezdl.dlservices.user.handlers;

import java.util.Set;
import java.util.UUID;

import org.apache.log4j.Logger;

import de.unidue.inf.is.ezdl.dlbackend.agent.Reusable;
import de.unidue.inf.is.ezdl.dlbackend.agent.StartedBy;
import de.unidue.inf.is.ezdl.dlbackend.message.Message;
import de.unidue.inf.is.ezdl.dlcore.ErrorConstants;
import de.unidue.inf.is.ezdl.dlcore.data.User;
import de.unidue.inf.is.ezdl.dlcore.message.content.LoginAsk;
import de.unidue.inf.is.ezdl.dlcore.message.content.LoginErrorNotify;
import de.unidue.inf.is.ezdl.dlcore.message.content.LoginTell;
import de.unidue.inf.is.ezdl.dlcore.message.content.MessageContent;
import de.unidue.inf.is.ezdl.dlcore.security.Privilege;
import de.unidue.inf.is.ezdl.dlservices.user.store.UserStore;



/**
 * Handles a {@link LoginAsk} message by authenticating the user against the
 * {@link UserStore} and answering with the set of privileges for the user.
 * 
 * @author mjordan
 */
@Reusable
@StartedBy(LoginAsk.class)
public class LoginHandler extends AbstractUserAgentHandler {

    /**
     * The logger.
     */
    private Logger logger = Logger.getLogger(LoginHandler.class);


    @Override
    protected boolean work(Message message) {
        boolean handled = true;
        MessageContent content = message.getContent();

        if (content instanceof LoginAsk) {
            LoginAsk loginAsk = (LoginAsk) content;
            handleLogin(message, loginAsk);
        }
        else {
            handled = false;
        }

        return handled;
    }


    /**
     * This method handles the login of a user, given a name and password. The
     * function right now only returns the users name as userid. It should
     * return a session ID, which has to be unique.
     * 
     * @param Message
     *            message
     */
    private void handleLogin(Message message, LoginAsk content) {

        String sessionId = newSessionId();

        String login = content.getLogin();
        String pwd = content.getSecret();

        try {
            if (getStore().login(login, pwd)) {
                User user = getStore().getUser(login);

                String first = user.getFirstName();
                String last = user.getLastName();
                long lastLogin = user.getLastLoginTime();
                Set<Privilege> privilegesForUserLogin = getStore().getPrivilegesForUserLogin(user.getLogin());
                LoginTell tell = new LoginTell(login, first, last, sessionId, privilegesForUserLogin);
                tell.setLastLoginTime(lastLogin);
                send(message.tell(tell));
                getStore().saveSessionIdForUserLogin(sessionId, user.getLogin());
            }
            else {
                sendError(message);
            }
        }
        catch (Exception e) {
            sendUnexpectedError(message);
            logger.error(message + " triggered an error: ", e);
        }
    }


    protected String newSessionId() {
        return UUID.randomUUID().toString();
    }


    private void sendError(Message message) {
        LoginErrorNotify error = new LoginErrorNotify(ErrorConstants.LOGIN_WRONG_PASSWORD.toString());
        send(message.tell(error));
    }


    private void sendUnexpectedError(Message message) {
        LoginErrorNotify error = new LoginErrorNotify(ErrorConstants.SERVER_NOT_READY.toString());
        send(message.tell(error));
    }

}
