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

package de.unidue.inf.is.ezdl.dlbackend.security;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import de.unidue.inf.is.ezdl.dlbackend.AbstractBackendTestBase;
import de.unidue.inf.is.ezdl.dlbackend.message.Message;
import de.unidue.inf.is.ezdl.dlbackend.mock.MockAgent;
import de.unidue.inf.is.ezdl.dlcore.message.content.PrivilegeAsk;
import de.unidue.inf.is.ezdl.dlcore.message.content.PrivilegeTell;
import de.unidue.inf.is.ezdl.dlcore.security.Privilege;
import de.unidue.inf.is.ezdl.dlcore.security.SecurityException;



public class DefaultSecurityManagerTest extends AbstractBackendTestBase {

    private static final String REQUEST_ID = "requestId";
    private static final String MOCK_AGENT_NAME = "MockAgent";
    private static final String UA_NAME = "UA";
    private static final String SESSION_ID = "sessionId";

    private DefaultSecurityManager securityManager;
    private MockAgent agent;


    @Before
    public void init() {
        agent = new MockAgent();
        agent.init(MOCK_AGENT_NAME, null);
        securityManager = new DefaultSecurityManager(agent);
    }


    @Test(expected = SecurityException.class)
    public void testCheckPrivilege() throws SecurityException {
        setupPrivilegeTell(false);

        securityManager.check(Privilege.DUMMY, SESSION_ID);
        checkPrivilegeAskMessageSent(Privilege.DUMMY, SESSION_ID, false);
    }


    @Test
    public void testHasPrivilege() {
        setupPrivilegeTell(false);

        Assert.assertFalse(securityManager.has(Privilege.DUMMY, SESSION_ID));
        checkPrivilegeAskMessageSent(Privilege.DUMMY, SESSION_ID, false);
    }


    @Test()
    public void testCheckPrivilege2() throws SecurityException {
        setupPrivilegeTell(true);

        securityManager.check(Privilege.DUMMY, SESSION_ID);
        checkPrivilegeAskMessageSent(Privilege.DUMMY, SESSION_ID, true);
    }


    @Test
    public void testHasPrivilege2() {
        setupPrivilegeTell(true);

        Assert.assertTrue(securityManager.has(Privilege.DUMMY, SESSION_ID));
        checkPrivilegeAskMessageSent(Privilege.DUMMY, SESSION_ID, true);
    }


    private void checkPrivilegeAskMessageSent(Privilege privilege, String sessionId, boolean privilegeGranted) {
        if (agent.getMessagesSent().size() == 1) {
            PrivilegeAsk privilegeAsk = (PrivilegeAsk) agent.getMessagesSent().get(0).getContent();
            Assert.assertTrue(privilegeAsk.getPrivilege().equals(privilege));
            Assert.assertTrue(privilegeAsk.getSessionId().equals(sessionId));
        }
        else {
            Assert.fail();
        }
    }


    private void setupPrivilegeTell(boolean privilegeGranted) {
        agent.setNextAskAnswer(new Message(UA_NAME, MOCK_AGENT_NAME, new PrivilegeTell(Privilege.DUMMY, SESSION_ID,
                        privilegeGranted), REQUEST_ID));
    }

}
