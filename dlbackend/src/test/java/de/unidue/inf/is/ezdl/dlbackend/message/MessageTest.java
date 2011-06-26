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

package de.unidue.inf.is.ezdl.dlbackend.message;

import java.util.LinkedList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import de.unidue.inf.is.ezdl.dlbackend.AbstractBackendTestBase;
import de.unidue.inf.is.ezdl.dlbackend.message.content.LogAsk;
import de.unidue.inf.is.ezdl.dlcore.message.content.AliveAsk;
import de.unidue.inf.is.ezdl.dlcore.message.content.AliveTell;
import de.unidue.inf.is.ezdl.dlcore.message.content.ErrorNotify;



public class MessageTest extends AbstractBackendTestBase {

    @Test
    public void testTell() {
        AliveAsk aliveAsk = new AliveAsk();
        Message message = new Message("alice", "bob", aliveAsk, "id");
        message.setRequestInternalId("msgID");

        AliveTell aliveTell = new AliveTell();
        Message tell = message.tell(aliveTell);

        Assert.assertEquals("new sender was receiver", "bob", tell.getFrom());
        Assert.assertEquals("new receiver was sender", "alice", tell.getTo());
        Assert.assertEquals("request ID same", "id", tell.getRequestId());
        Assert.assertEquals("message ID same", "msgID", tell.getRequestInternalId());
    }


    @Test
    public void testEquals() {
        ErrorNotify searchNotify1 = new ErrorNotify("id1");
        ErrorNotify searchNotify2 = new ErrorNotify("id1");
        Message message1 = new Message("alice", "bob", searchNotify1, "id");
        Message message2 = new Message("alice", "bob", searchNotify2, "id");

        Assert.assertEquals(message1, message2);

        message1.setRequestInternalId("msgID");

        Assert.assertNotSame(message1, message2);

        message2.setRequestInternalId("msgID");

        Assert.assertEquals(message1, message2);
    }


    @Test
    public void testListContains1() {
        List<Message> list = new LinkedList<Message>();
        ErrorNotify searchNotify1 = new ErrorNotify("id1");
        ErrorNotify searchNotify2 = new ErrorNotify("id1");
        Message message1 = new Message("alice", "bob", searchNotify1, "id");
        Message message2 = new Message("alice", "bob", searchNotify2, "id");
        list.add(message1);
        Assert.assertTrue("list contains message", list.contains(message2));
    }


    @Test
    public void testListContains2() {
        List<Message> list = new LinkedList<Message>();
        LogAsk ask1 = new LogAsk();
        LogAsk ask2 = new LogAsk();
        Message message1 = new Message("alice", "bob", ask1, "id");
        Message message2 = new Message("alice", "bob", ask2, "id");
        list.add(message1);
        Assert.assertTrue("list contains message", list.contains(message2));
    }
}
