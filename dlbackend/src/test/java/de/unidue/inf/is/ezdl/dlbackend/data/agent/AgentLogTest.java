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

package de.unidue.inf.is.ezdl.dlbackend.data.agent;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import de.unidue.inf.is.ezdl.dlbackend.AbstractBackendTestBase;



public class AgentLogTest extends AbstractBackendTestBase {

    @Before
    public void init() {
    }


    @Test
    public void testStartLogAgentMax() {
        checkLogAgentMax(1, false);
        checkLogAgentMax(5, false);
        checkLogAgentMax(30, false);
        checkLogAgentMax(1, true);
        checkLogAgentMax(5, true);
        checkLogAgentMax(30, true);
    }


    private void checkLogAgentMax(int max, boolean overFill) {
        AgentLog log = AgentLog.startLog(max);

        addMsgs(log, max);

        if (overFill) {
            log.add("overfill", "overfill");
        }

        Assert.assertEquals("log contains exactly max entries", max, log.getSize());
        Assert.assertEquals("logData contains exactly max entries", max, log.getLogData().size());
    }


    private void addMsgs(AgentLog log, int max) {
        for (int i = 0; (i < max); i++) {
            log.add("type", "msg" + i);
            Assert.assertTrue("log contains at max 'max' entries", log.getSize() <= log.getMax());
        }
    }


    @Test
    public void testStuff() {
        AgentLog log = AgentLog.startLog(1);

        Assert.assertEquals("Only start message", 1, log.getSize());

        Assert.assertTrue("", log.toString().startsWith("[log start"));

        addMsgs(log, 1);

        Assert.assertEquals("Only last message", 1, log.getSize());

        Assert.assertEquals("", "[type - msg0]", log.toString());

        addMsgs(log, 2);

        Assert.assertEquals("Only last message", 1, log.getSize());
        Assert.assertEquals("", "[type - msg1]", log.toString());

        log.setMax(10);

        addMsgs(log, 8);

        Assert.assertEquals("9 new ones", 9, log.getSize());

        addMsgs(log, 8);

        Assert.assertEquals("2+8 new ones", 10, log.getSize());

        log.setMax(5);

        Assert.assertEquals("2+8 new ones", 10, log.getSize());

        addMsgs(log, 3);

        Assert.assertEquals("2+3 new ones", 5, log.getSize());
        Assert.assertEquals("", "[type - msg6][type - msg7][type - msg0]" + "[type - msg1][type - msg2]",
                        log.toString());

    }

}
