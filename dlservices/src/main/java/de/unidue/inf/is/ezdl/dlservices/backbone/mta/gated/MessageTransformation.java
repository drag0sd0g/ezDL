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

package de.unidue.inf.is.ezdl.dlservices.backbone.mta.gated;

import de.unidue.inf.is.ezdl.dlcore.message.content.MessageContent;



/**
 * Transformation that helps mapping messages from the client to internally used
 * message.
 * 
 * @author tbeckers
 */
public final class MessageTransformation {

    private String to;
    private MessageContent messageContent;


    /**
     * Constructur.
     * 
     * @param to
     *            receiver
     * @param messageContent
     *            the message content to forward
     */
    public MessageTransformation(String to, MessageContent messageContent) {
        super();
        this.to = to;
        this.messageContent = messageContent;
    }


    /**
     * Returns the receiver of the message.
     * 
     * @return the receiving agent
     */
    public String getTo() {
        return to;
    }


    /**
     * Returns the message content to forward
     * 
     * @return the message content to forward
     */
    public MessageContent getMessageContent() {
        return messageContent;
    }
}