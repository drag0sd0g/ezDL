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

package de.unidue.inf.is.ezdl.dlservices;

import java.util.Properties;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.unidue.inf.is.ezdl.dlbackend.AbstractBackendTestBase;
import de.unidue.inf.is.ezdl.dlbackend.agent.Agent;
import de.unidue.inf.is.ezdl.dlbackend.agent.connectors.AgentConnector;
import de.unidue.inf.is.ezdl.dlbackend.message.Message;
import de.unidue.inf.is.ezdl.dlbackend.mock.MockAgentConnector;
import de.unidue.inf.is.ezdl.dlbackend.mock.MockORB;
import de.unidue.inf.is.ezdl.dlcore.message.content.AliveAsk;
import de.unidue.inf.is.ezdl.dlcore.misc.ConnectionFailedException;
import de.unidue.inf.is.ezdl.dlcore.utils.PropertiesUtils;
import de.unidue.inf.is.ezdl.dlservices.backbone.directory.Directory;



public class ServicesTest extends AbstractBackendTestBase {

    private static final String DIRECTORY_NAME = "somedummyagentname";

    MockORB orb;

    Directory directory;


    @Before
    public void init() {
        orb = new MockORB();

        directory = new Directory();
        initAgent(directory, DIRECTORY_NAME, "directory.properties");

        try {
            directory.goOnline();
            sleep(1000);
        }
        catch (ConnectionFailedException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void bla() {
        Message msg = new Message();
        msg.setFrom("SantaClaus");
        msg.setTo(DIRECTORY_NAME);
        msg.setRequestId("BullshitRequest");
        msg.setContent(new AliveAsk());
        sleep(1 * 1000);

        // Nothing much to test for now, but you can look at the Directory's
        // AgentLog.
        Assert.assertTrue(true);
    }


    @After
    public void tearDown() {
        directory.halt();
    }


    private void initAgent(Agent agent, String name, String propertiesFileName) {
        Properties props = PropertiesUtils.readPropertiesFromFileTree(propertiesFileName);

        agent.init(name, props);
        AgentConnector connector = new MockAgentConnector(orb, agent);
        agent.setConnector(connector);
    }

}
