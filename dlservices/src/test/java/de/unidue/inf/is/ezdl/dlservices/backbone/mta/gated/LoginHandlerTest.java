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

package de.unidue.inf.is.ezdl.dlservices.backbone.mta.gated;

import java.util.HashSet;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import de.unidue.inf.is.ezdl.dlbackend.AbstractBackendTestBase;
import de.unidue.inf.is.ezdl.dlbackend.agent.Agent;
import de.unidue.inf.is.ezdl.dlbackend.message.Message;
import de.unidue.inf.is.ezdl.dlcore.ErrorConstants;
import de.unidue.inf.is.ezdl.dlcore.message.MTAMessage;
import de.unidue.inf.is.ezdl.dlcore.message.content.ErrorNotify;
import de.unidue.inf.is.ezdl.dlcore.message.content.LoginAsk;
import de.unidue.inf.is.ezdl.dlcore.message.content.LoginTell;
import de.unidue.inf.is.ezdl.dlcore.message.content.MessageContent;
import de.unidue.inf.is.ezdl.dlcore.misc.TimeoutException;
import de.unidue.inf.is.ezdl.dlcore.security.Privilege;
import de.unidue.inf.is.ezdl.dlservices.backbone.mta.gated.handlers.LoginHandler;



/**
 * Tests the {@link LoginHandler}.
 * 
 * @author mjordan
 */
public class LoginHandlerTest extends AbstractBackendTestBase {

    private static final String CONNECTION_ID = "a";

    private static final String LOGIN_WRONG = "wrong";

    private static final String LOGIN_RIGHT = "right";

    private static final String MOCKGMTA = "mockgmta";

    private static final String TESTUSERAGENT = "testuseragent";


    /**
     * Extending the {@link LoginHandler} so we can test it better.
     * 
     * @author mjordan
     */
    class TestableLoginHandler extends LoginHandler {

        public TestableLoginHandler(AbstractGatedMTA agent) {
            super(agent);
        }


        @Override
        protected Message askUserAgentForAuthentication(Agent agent, Message loginMsg) throws TimeoutException {
            LoginAsk ask = (LoginAsk) loginMsg.getContent();
            MessageContent content = null;
            String login = ask.getLogin();
            if (LOGIN_RIGHT.equals(login)) {
                String first = "mockfirstname";
                String last = "mocklastname";
                String sid = "mocksessionid";
                Set<Privilege> privileges = new HashSet<Privilege>();
                content = new LoginTell(login, first, last, sid, privileges);
            }
            else if (LOGIN_WRONG.equals(login)) {
                content = new ErrorNotify(ErrorConstants.LOGIN_WRONG_PASSWORD);
            }
            else {
                throw new TimeoutException();
            }
            Message answer = loginMsg.tell(content);

            return answer;
        }


        @Override
        protected String getUserAgent() {
            return TESTUSERAGENT;
        }
    }


    private MockGatedMTA gmta;
    TestableLoginHandler handler;


    @Before
    public void init() {
        gmta = new MockGatedMTA();
        gmta.init(MOCKGMTA, getTestProperties());
        handler = new TestableLoginHandler(gmta);
    }


    /**
     * Tests behavior of the {@link LoginHandler} when the UserAgent would
     * return a valid LoginTell message with user information.
     */
    @Test
    public void testLoginRight() {

        MTAMessage clientMsg = getClientMsg(LOGIN_RIGHT);

        handler.handleLogin(CONNECTION_ID, clientMsg);

        Assert.assertTrue("connection exists", gmta.getConnections().containsKey(CONNECTION_ID));
        Assert.assertEquals("connection right", LOGIN_RIGHT, gmta.getConnections().get(CONNECTION_ID).getUserInfo()
                        .getLogin());
    }


    /**
     * Tests behavior of the {@link LoginHandler} when the UserAgent would
     * return an error message in response to wrong credentials.
     */
    @Test
    public void testLoginWrong() {

        MTAMessage clientMsg = getClientMsg(LOGIN_WRONG);

        handler.handleLogin(CONNECTION_ID, clientMsg);

        Assert.assertFalse("connection exists not", gmta.getConnections().containsKey(CONNECTION_ID));
    }


    /**
     * Tests behavior of the {@link LoginHandler} when the UserAgent wouldn't
     * answer at all.
     */
    @Test
    public void testLoginTimeout() {

        MTAMessage clientMsg = getClientMsg("timeout");

        handler.handleLogin(CONNECTION_ID, clientMsg);

        Assert.assertFalse("connection exists not", gmta.getConnections().containsKey(CONNECTION_ID));
    }


    MTAMessage getClientMsg(String login) {
        MTAMessage clientMsg = new MTAMessage();
        clientMsg.setContent(new LoginAsk(login, "passwd"));
        clientMsg.setRequestId("rid");
        return clientMsg;
    }

}
