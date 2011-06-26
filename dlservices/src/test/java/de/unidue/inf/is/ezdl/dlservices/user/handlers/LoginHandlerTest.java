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

import java.util.Collections;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import de.unidue.inf.is.ezdl.dlbackend.AbstractBackendTestBase;
import de.unidue.inf.is.ezdl.dlbackend.message.Message;
import de.unidue.inf.is.ezdl.dlbackend.mock.MockAgent;
import de.unidue.inf.is.ezdl.dlcore.ErrorConstants;
import de.unidue.inf.is.ezdl.dlcore.message.content.LoginAsk;
import de.unidue.inf.is.ezdl.dlcore.message.content.LoginErrorNotify;
import de.unidue.inf.is.ezdl.dlcore.message.content.LoginTell;
import de.unidue.inf.is.ezdl.dlcore.security.Privilege;
import de.unidue.inf.is.ezdl.dlservices.mock.MockUserStore;
import de.unidue.inf.is.ezdl.dlservices.user.store.UserStore;



public class LoginHandlerTest extends AbstractBackendTestBase {

    MockUserStore mockUserStore = new MockUserStore();


    class TestableLoginHandler extends LoginHandler {

        @Override
        protected UserStore getStore() {
            return mockUserStore;
        }


        @Override
        protected String newSessionId() {
            return "sessId";
        }

    }


    private static final String UA_NAME = "MockUA";

    private MockAgent mockAgent;


    @Before
    public void init() {
        mockAgent = new MockAgent(UA_NAME);
    }


    @Test
    public void testLoginWrongPassword() {
        LoginHandler handler = new TestableLoginHandler();

        handler.init("reqid", mockAgent);

        LoginAsk ask = new LoginAsk(MockUserStore.LOGIN, MockUserStore.WRONG_SECRET);
        Message msg = new Message("client", UA_NAME, ask, "reqid");

        handler.work(msg);

        LoginErrorNotify notify = new LoginErrorNotify(ErrorConstants.LOGIN_WRONG_PASSWORD.toString());
        Message tell = new Message(UA_NAME, "client", notify, "reqid");
        Assert.assertTrue("Error msg sent", mockAgent.getMessagesSent().contains(tell));
    }


    @Test
    public void testLoginRightPassword() {
        LoginHandler handler = new TestableLoginHandler();
        handler.init("reqid", mockAgent);

        LoginAsk ask = new LoginAsk(MockUserStore.LOGIN, MockUserStore.RIGHT_SECRET);
        Message msg = new Message("client", UA_NAME, ask, "reqid");

        handler.work(msg);

        getLogger().debug(mockAgent.getMessagesSent());

        LoginTell login = new LoginTell(MockUserStore.LOGIN, "name", "lastname", "sessId",
                        Collections.<Privilege> emptySet());
        Message tell = new Message(UA_NAME, "client", login, "reqid");

        Assert.assertTrue("Login ack sent", mockAgent.getMessagesSent().contains(tell));
    }
}
