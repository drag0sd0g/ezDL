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

package de.unidue.inf.is.ezdl.dlbackend.agent.requesthandling;

import junit.framework.Assert;

import org.junit.Test;

import de.unidue.inf.is.ezdl.dlbackend.AbstractBackendTestBase;
import de.unidue.inf.is.ezdl.dlbackend.agent.handlers.RequestHandler;
import de.unidue.inf.is.ezdl.dlbackend.mock.MockRequest;



/**
 * @author mjordan
 */
public class RequestHandlerMapTest extends AbstractBackendTestBase {

    @Test
    public void testEmpty() {
        RequestHandlerMap map = new RequestHandlerMap();
        map.halt();
    }


    @Test
    public void testPut1Handler() {
        RequestHandlerMap map = new RequestHandlerMap();

        RequestHandler handler = new MockRequest();
        map.put("reqid", handler);

        map.halt();
    }


    @Test
    public void testPut2Handlers() {
        RequestHandlerMap map = new RequestHandlerMap();

        addHandlers(2, map, false);

        map.halt();
    }


    @Test
    public void testPut3Handler() {
        RequestHandlerMap map = new RequestHandlerMap();

        addHandlers(3, map, false);

        map.halt();
    }


    @Test
    public void testRemoveExistingHandler() {
        RequestHandlerMap map = new RequestHandlerMap();
        addHandlers(4, map, false);

        Assert.assertEquals("map size 1", 4, map.size());
        RequestHandler handler = map.remove("reqid1");
        Assert.assertNotNull(handler);
        Assert.assertEquals("map size 2", 3, map.size());

        map.halt();
    }


    @Test
    public void testRemoveNonExistingHandler() {
        RequestHandlerMap map = new RequestHandlerMap();
        addHandlers(4, map, false);

        Assert.assertEquals("map size 1", 4, map.size());
        RequestHandler handler = map.remove("reqid1doesnotexist");
        Assert.assertNull("handler null", handler);
        Assert.assertEquals("map size 2", 4, map.size());

        map.halt();
    }


    @Test
    public void testGetHandler() {
        RequestHandlerMap map = new RequestHandlerMap();
        addHandlers(4, map, false);
        Assert.assertEquals("map size 1", 4, map.size());

        RequestHandler handler = map.get("reqid1");
        Assert.assertNotNull("handler not null", handler);
        Assert.assertEquals("map size 2", 4, map.size());

        map.halt();
    }


    @Test
    public void testGetHandlerForUnknownRequest() {
        RequestHandlerMap map = new RequestHandlerMap();
        addHandlers(4, map, false);
        Assert.assertEquals("map size 1", 4, map.size());

        RequestHandler handler = map.get("reqidnotknown");
        Assert.assertNull("handler null", handler);
        Assert.assertEquals("map size 2", 4, map.size());

        map.halt();
    }


    @Test
    public void testGetHandlerForHaltedRequest() {
        RequestHandlerMap map = new RequestHandlerMap();
        addHandlers(4, map, false);
        Assert.assertEquals("map size 1", 4, map.size());

        RequestHandler handler = map.get("reqid1");
        Assert.assertNotNull("handler not null", handler);
        Assert.assertEquals("map size 2", 4, map.size());

        handler.halt();

        RequestHandler handler2 = map.get("reqid1");
        Assert.assertNull("handler null", handler2);
        Assert.assertEquals("map size 3", 3, map.size());

        map.halt();
    }


    @Test
    public void testShutdown() {
        RequestHandlerMap map = new RequestHandlerMap();
        addHandlers(4, map, false);
        Assert.assertEquals("map size 1", 4, map.size());

        RequestHandler handler = map.get("reqid1");
        Assert.assertNotNull("handler not null", handler);
        Assert.assertEquals("map size 3", 4, map.size());

        map.halt();

        Assert.assertEquals("map size after shutdown", 0, map.size());

        Assert.assertTrue(map.isHalted());

        RequestHandler handler2 = map.get("reqid1");
        Assert.assertNull("handler null", handler2);

        Assert.assertEquals("map size after remove #1", 0, map.size());

        RequestHandler handler3 = map.get("reqid1notexists");
        Assert.assertNull("handler null", handler3);

        Assert.assertEquals("map size after remove #2", 0, map.size());

        addHandlers(5, map, true);

        Assert.assertEquals("map size after adding 5", 0, map.size());

    }


    private void addHandlers(int count, RequestHandlerMap map, boolean shouldBeShutdown) {
        for (int i = 0; (i < count); i++) {
            RequestHandler handler = new MockRequest();
            map.put("reqid" + i, handler);
            Assert.assertEquals("map not shutdown", shouldBeShutdown, map.isHalted());
        }
    }

}
