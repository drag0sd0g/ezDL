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

package de.unidue.inf.is.ezdl.dlcore.log;

import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import de.unidue.inf.is.ezdl.dlcore.AbstractTestBase;
import de.unidue.inf.is.ezdl.dlcore.message.content.UserLogNotify;



public class UserLogNotifyTest extends AbstractTestBase {

    private UserLogNotify userLogNotify;


    @Before
    public void init() {
        userLogNotify = new UserLogNotify("sessionId", "eventName");
        userLogNotify.addParameter("a", "value1");
        userLogNotify.addParameter("b", "value2");
        userLogNotify.addParameter("b", "value3");
        userLogNotify.addParameter("c", "value4");
        userLogNotify.addParameter("d", "value5");
        userLogNotify.addParameter("d0", "value6");
        userLogNotify.addParameter("d1999", "value7");
        userLogNotify.addParameter("d2", "value8");
        userLogNotify.addParameter("d03", "value9");
    }


    @Test
    public void testGetParameterSequence() {
        List<String> result = userLogNotify.getParameterCollection("d");
        Assert.assertEquals(Arrays.asList("value5", "value6", "value8", "value9", "value7"), result);
    }


    @Test
    public void testGetSingleParameter() {
        String result = userLogNotify.getSingleParameter("b");
        Assert.assertEquals("value2", result);
    }
}
