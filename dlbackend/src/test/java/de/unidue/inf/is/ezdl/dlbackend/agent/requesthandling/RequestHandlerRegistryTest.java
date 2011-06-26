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

import java.util.HashSet;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import de.unidue.inf.is.ezdl.dlbackend.AbstractBackendTestBase;
import de.unidue.inf.is.ezdl.dlbackend.agent.Agent;
import de.unidue.inf.is.ezdl.dlbackend.agent.Reusable;
import de.unidue.inf.is.ezdl.dlbackend.agent.StartedBy;
import de.unidue.inf.is.ezdl.dlbackend.agent.handlers.RequestHandler;
import de.unidue.inf.is.ezdl.dlbackend.message.content.LogAsk;
import de.unidue.inf.is.ezdl.dlbackend.message.content.LogTell;
import de.unidue.inf.is.ezdl.dlbackend.mock.MockAgent;
import de.unidue.inf.is.ezdl.dlbackend.mock.MockRequest;
import de.unidue.inf.is.ezdl.dlcore.message.content.AliveAsk;
import de.unidue.inf.is.ezdl.dlcore.message.content.MessageContent;



public class RequestHandlerRegistryTest extends AbstractBackendTestBase {

    Agent mockAgent = new MockAgent();

    RequestHandlerFactory registry = new RequestHandlerFactory(mockAgent);


    @Reusable
    @StartedBy(LogAsk.class)
    public static class MockRequestLogAsk extends MockRequest {
    }


    @StartedBy(LogTell.class)
    public static class MockRequestLogTell extends MockRequest {
    }


    @StartedBy(LogAsk.class)
    public static class MockRequestLogAskDupe extends MockRequest {
    }


    @StartedBy(AliveAsk.class)
    class MockRequestAliveAsk extends MockRequest {
    }


    @Before
    public void initHandlers() {
        Set<Class<? extends RequestHandler>> handlers = setupRequestHandlers();
        registry.initHandlers(handlers);
    }


    Set<Class<? extends RequestHandler>> setupRequestHandlers() {
        Set<Class<? extends RequestHandler>> out = new HashSet<Class<? extends RequestHandler>>();
        out.add(MockRequestLogAsk.class);
        out.add(MockRequestLogTell.class);
        out.add(MockRequestAliveAsk.class);
        return out;
    }


    Set<Class<? extends RequestHandler>> setupRequestHandlersDuplicate() {
        Set<Class<? extends RequestHandler>> out = new HashSet<Class<? extends RequestHandler>>();
        out.add(MockRequestLogAskDupe.class);
        out.add(MockRequestLogAsk.class);
        out.add(MockRequestLogTell.class);
        return out;
    }


    @Test
    public void getHandlerTestAsk() {
        MessageContent content = new LogAsk();
        RequestHandler request = registry.getHandler(content, "");
        Assert.assertNotNull(request);
        Assert.assertEquals(MockRequestLogAsk.class, request.getClass());
    }


    @Test
    public void getHandlerTestTell() {
        MessageContent content = new LogTell(null);
        RequestHandler request = registry.getHandler(content, "");
        Assert.assertNotNull(request);
        Assert.assertEquals(MockRequestLogTell.class, request.getClass());
    }


    @Test
    public void getHandlerTestFails() {
        MessageContent content = new AliveAsk();
        RequestHandler request = registry.getHandler(content, "");
        Assert.assertNull("Fails because handler is not public", request);
    }


    @Test
    public void reusableTest() {
        MessageContent content = new LogAsk();
        RequestHandler request1 = registry.getHandler(content, "");
        RequestHandler request2 = registry.getHandler(content, "");
        RequestHandler request3 = registry.getHandler(content, "");

        Assert.assertEquals(request1, request2);
        Assert.assertEquals(request2, request3);
    }
}
