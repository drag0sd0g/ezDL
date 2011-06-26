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

package de.unidue.inf.is.ezdl.dlservices.backbone.mta.gated.http;

import java.util.LinkedList;
import java.util.List;

import de.unidue.inf.is.ezdl.dlbackend.message.Message;
import de.unidue.inf.is.ezdl.dlcore.message.MTAMessage;
import de.unidue.inf.is.ezdl.dlservices.backbone.mta.gated.TextMessageManager;



/**
 * This is to mock the MTA away and to track calls to some methods that are
 * expected, given that the {@link TextMessageManager} works as advertised.
 * 
 * @author mjordan
 */
class MockGatedHttpMTA extends GatedHttpMTA {

    // List<MTAMessage> sentToHttpMsgs = new LinkedList<MTAMessage>();
    // List<String> sentToHttpConIds = new LinkedList<String>();
    // List<MTAMessage> sentTextToAllMsgs = new LinkedList<MTAMessage>();
    // List<Message> sentMessages = new LinkedList<Message>();
    List<MTAMessage> sentToHttpMsgs;
    List<String> sentToHttpConIds;
    List<MTAMessage> sentTextToAllMsgs;
    List<Message> sentMessages;


    public MockGatedHttpMTA() {
        clear();
    }


    @Override
    public void sendToClient(String connectionId, MTAMessage mtaMessage) {
        sentToHttpConIds.add(connectionId);
        sentToHttpMsgs.add(mtaMessage);
    }


    @Override
    public void sendTextToAll(MTAMessage mtaMessage) {
        sentTextToAllMsgs.add(mtaMessage);
    }


    List<String> getSentToHttpConIds() {
        return sentToHttpConIds;
    }


    List<MTAMessage> getSentToHttpMsgs() {
        return sentToHttpMsgs;
    }


    /**
     * @return the sentTextToAllMsgs
     */
    public List<MTAMessage> getSentTextToAllMsgs() {
        return sentTextToAllMsgs;
    }


    @Override
    public void send(Message message) {
        sentMessages.add(message);
    }


    /**
     * @return the sentMessages
     */
    public List<Message> getSentMessages() {
        return sentMessages;
    }


    void clear() {
        sentToHttpMsgs = new LinkedList<MTAMessage>();
        sentToHttpConIds = new LinkedList<String>();
        sentTextToAllMsgs = new LinkedList<MTAMessage>();
        sentMessages = new LinkedList<Message>();
    }


    @Override
    public TextMessageManager getTextManager() {
        return new TextMessageManager(this);
    }


    @Override
    public void terminateConnection(String connectionId) {
    }


    @Override
    public void terminateConnection(String connectionId, Message farewellMessage) {
    }

}
