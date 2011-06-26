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

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import de.unidue.inf.is.ezdl.dlbackend.AbstractBackendTestBase;
import de.unidue.inf.is.ezdl.dlbackend.message.Message;
import de.unidue.inf.is.ezdl.dlbackend.mock.MockAgent;
import de.unidue.inf.is.ezdl.dlcore.message.content.PrivilegeAsk;
import de.unidue.inf.is.ezdl.dlcore.message.content.PrivilegeTell;
import de.unidue.inf.is.ezdl.dlcore.security.Privilege;
import de.unidue.inf.is.ezdl.dlservices.mock.MockUserStore;
import de.unidue.inf.is.ezdl.dlservices.user.store.UserStore;



public class PrivilegeHandlerTest extends AbstractBackendTestBase {

    private static final String UA_NAME = "MockUA";
    private MockAgent mockAgent;
    private MockUserStore mockUserStore;


    @Before
    public void init() {
        mockAgent = new MockAgent(UA_NAME);
        mockUserStore = new MockUserStore();
    }


    @Test
    public void testPrivilegeAsk() {
        PrivilegeHandler handler = new PrivilegeHandler() {

            @Override
            protected UserStore getStore() {
                return mockUserStore;
            }
        };

        handler.init("reqid", mockAgent);

        PrivilegeAsk ask = new PrivilegeAsk(Privilege.DUMMY, "sessionId");
        Message msg = new Message("client", UA_NAME, ask, "reqid");

        handler.work(msg);

        PrivilegeTell notify = new PrivilegeTell(Privilege.DUMMY, "sessionId", true);
        Message tell = new Message(UA_NAME, "client", notify, "reqid");

        Assert.assertTrue("Error msg sent", mockAgent.getMessagesSent().contains(tell));
    }

}
