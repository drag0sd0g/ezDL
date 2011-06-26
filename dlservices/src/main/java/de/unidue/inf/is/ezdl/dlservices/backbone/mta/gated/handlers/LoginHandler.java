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

package de.unidue.inf.is.ezdl.dlservices.backbone.mta.gated.handlers;

import org.apache.log4j.Logger;

import de.unidue.inf.is.ezdl.dlbackend.agent.Agent;
import de.unidue.inf.is.ezdl.dlbackend.message.Message;
import de.unidue.inf.is.ezdl.dlcore.ErrorConstants;
import de.unidue.inf.is.ezdl.dlcore.message.MTAMessage;
import de.unidue.inf.is.ezdl.dlcore.message.content.ErrorNotify;
import de.unidue.inf.is.ezdl.dlcore.message.content.LoginAsk;
import de.unidue.inf.is.ezdl.dlcore.message.content.LoginTell;
import de.unidue.inf.is.ezdl.dlcore.message.content.MessageContent;
import de.unidue.inf.is.ezdl.dlcore.misc.TimeoutException;
import de.unidue.inf.is.ezdl.dlcore.security.Hash;
import de.unidue.inf.is.ezdl.dlservices.backbone.mta.gated.AbstractGatedMTA;
import de.unidue.inf.is.ezdl.dlservices.backbone.mta.gated.http.GatedHttpMTA;



/**
 * Handles login requests by the client of the {@link GatedHttpMTA}.
 * 
 * @author mjordan
 */
public class LoginHandler {

    /**
     * The logger.
     */
    private final Logger logger = Logger.getLogger(getClass());

    /**
     * Reference to the agent that runs this handler.
     */
    private AbstractGatedMTA agent;
    /**
     * The hasher for the login password.
     */
    private Hash hash = new Hash();


    /**
     * Constructor
     * 
     * @param agent
     *            the reference of the agent this handler works for
     */
    public LoginHandler(AbstractGatedMTA agent) {
        this.agent = agent;
    }


    /**
     * Handles a login request.
     * 
     * @param connectionId
     *            the ID of the connection the login message was sent over
     * @param foreignMessage
     *            the login message itself
     */
    public void handleLogin(String connectionId, MTAMessage foreignMessage) {
        MessageContent foreignContent = foreignMessage.getContent();
        if (!(foreignContent instanceof LoginAsk)) {
            return;
        }

        LoginAsk loginAsk = (LoginAsk) foreignContent;

        boolean loginSuccess = false;
        Message answer = null;
        LoginTell tell = null;
        try {
            Message loginMsg = getLoginMessageForUserAgent(loginAsk);

            answer = askUserAgentForAuthentication(agent, loginMsg);

            MessageContent answerContent = answer.getContent();
            if (answerContent instanceof LoginTell) {
                tell = (LoginTell) answerContent;
                loginSuccess = true;
            }
        }
        catch (TimeoutException e) {
            logger.warn("Message to user agent resulted in timeout.");
        }

        if (loginSuccess) {
            agent.loginUser(connectionId, loginAsk, tell);
        }
        else {
            sendErrorAndTerminate(connectionId, answer);
        }
    }


    /**
     * Generates the message the handler sends to the UserAgent.
     * 
     * @param foreignMessage
     *            the message to take the login information from
     * @return the message for the user agent
     */
    private Message getLoginMessageForUserAgent(LoginAsk foreignAsk) {
        String login = foreignAsk.getLogin();
        String passwd = foreignAsk.getSecret();
        passwd = hash.sha1(passwd);

        MessageContent ourAsk = new LoginAsk(login, passwd);
        String rid = agent.getNextRequestID();
        Message loginMsg = new Message(agent.agentName(), getUserAgent(), ourAsk, rid);
        return loginMsg;
    }


    /**
     * Sends an error message to the HTTP client and terminates the connection
     * afterwards.
     * 
     * @param connectionId
     *            the ID of the connection to terminate
     * @param answer
     *            the answer that the UserAgent might have sent or null, if no
     *            such message
     */
    private void sendErrorAndTerminate(String connectionId, Message answer) {
        if (answer == null) {
            ErrorNotify content = new ErrorNotify(ErrorConstants.SERVER_NOT_READY);
            answer = new Message();
            answer.setContent(content);
        }
        agent.terminateConnection(connectionId, answer);
    }


    /**
     * Asks the user agent to authenticate the client given the login message.
     * 
     * @param agent
     *            the agent to use for sending the Message
     * @param loginMsg
     *            the login message to send to the user agent
     * @return the answer send by the login message
     * @throws TimeoutException
     */
    protected Message askUserAgentForAuthentication(Agent agent, Message loginMsg) throws TimeoutException {
        Message answer;
        answer = agent.ask(loginMsg);
        return answer;
    }


    /**
     * Returns the name of the agent that gets the authentication messages.
     * 
     * @return the agent name
     */
    protected String getUserAgent() {
        return agent.getLoginLogoutReceiverName();
    }

}
