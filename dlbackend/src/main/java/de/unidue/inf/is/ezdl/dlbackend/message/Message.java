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

import java.io.Serializable;

import de.unidue.inf.is.ezdl.dlcore.message.content.MessageContent;



/**
 * This class implements an ezDL message that is used as a wrapper object around
 * a MessageContent payload.
 * <p>
 * Message has three levels of address/routing built in. The first and most
 * obvious one is the receiver address set by {@link #setTo(String)}. This
 * specifies the receiving agent. The agent itself then has to decide which
 * internal object is responsible to handling this message. This is coordinated
 * by the requestId (see {@link #setRequestId(String)}. In the rare case that a
 * single request handler is waiting for two messages of the same message
 * content by the same sender, the optional request internal ID field (see
 * {@link #setRequestInternalId(String)}) can be used to disambiguate.
 */

public final class Message implements Serializable {

    /**
     * Start value for the ttl field.
     */
    private static final int TTL_START = 20;
    /**
     * @since 17.11.2009
     */
    private static final long serialVersionUID = -4305507117816828445L;

    /**
     * Sender.
     */
    private String from;
    /**
     * Receiver.
     */
    private String to;
    /**
     * Content of the message.
     */
    private MessageContent content;
    /**
     * Request ID. This ID is used to deliver a Message to a specific
     * RequestHandler.
     */
    private String requestId;
    /**
     * The request internal ID can be used by a RequestHandler to identify a
     * specific message in situations where a RequestHandler waits for multiple
     * answers at the same time. The internal ID is set in the Ask part of a
     * message conversation and used (copied) in the Tell part (the answer) so
     * the whole ID (or in database terms "primary key") is the combination of
     * internal ID and content type (Ask or Tell).
     */
    private String requestInternalId;
    /**
     * Time to live. This is set to a start value and then decreased each time
     * the message passes though a send method. If the ttl reaches 0, the
     * message is dropped because it might be looping.
     */
    private int ttl;


    /**
     * Default constructor. Creates a new message.
     */
    public Message() {
        this("", "", null, "");
    }


    /**
     * Creates a new Message object with given parameters.
     * 
     * @param from
     *            the sending agent
     * @param to
     *            the receiver's name
     * @param content
     *            the payload of the message
     * @param requestId
     *            the request ID.
     */
    public Message(String from, String to, MessageContent content, String requestId) {
        setFrom(from);
        setTo(to);
        setContent(content);
        setRequestId(requestId);
        this.ttl = TTL_START;
    }


    /**
     * Creates a copy of the given original message. Beware! The values are only
     * linked into the new object, not actually copied. This is okay for the
     * strings because they are immutable. The MessageContent object, though,
     * should not be changed after calling this in order to prevent strange
     * things from happening.
     * 
     * @param original
     *            the original, to be copied, message
     */
    public Message(Message original) {
        setFrom(original.getFrom());
        setTo(original.getTo());
        setContent(original.getContent());
        setRequestId(original.getRequestId());
        setRequestInternalId(original.getRequestInternalId());
        this.ttl = original.ttl;
    }


    /**
     * Creates an answer to this message with the given content.
     * <p>
     * This also copies the TTL field to make sure nobody is playing
     * question-answer ping-pong.
     * 
     * @param content
     *            the content to use as the answer
     * @return the answer message
     */
    public Message tell(MessageContent content) {
        Message answer = new Message(getTo(), getFrom(), content, getRequestId());
        answer.ttl = this.ttl;
        answer.setRequestInternalId(getRequestInternalId());
        return answer;
    }


    /**
     * Returns the sender address.
     * 
     * @return the agent-name
     */
    public String getFrom() {
        return from;
    }


    /**
     * Returns the message content.
     * 
     * @return the message
     */
    public MessageContent getContent() {
        return content;
    }


    /**
     * Returns the request Id.
     * 
     * @return the request Id of this message
     */
    public String getRequestId() {
        return requestId;
    }


    /**
     * The to address of the message.
     * 
     * @return the name of the agent the message should be delivered to.
     */
    public String getTo() {
        return to;
    }


    /**
     * Set from part of the message
     * 
     * @param from
     *            the agent-name
     */
    public void setFrom(String from) {
        this.from = from;
    }


    /**
     * Set the content.
     * 
     * @param content
     *            the content
     */
    public void setContent(MessageContent content) {
        this.content = content;
    }


    /**
     * Sets the request ID.
     * 
     * @param id
     *            the request ID to set
     */
    public void setRequestId(String id) {
        this.requestId = id;
    }


    /**
     * Set the receiver of the message.
     * 
     * @param receiver
     *            the receiver
     */
    public void setTo(String receiver) {
        to = receiver;
    }


    @Override
    public String toString() {
        String result = toStringHeader();
        if (this.getContent() == null) {
            result += "?";
        }
        else {
            result += this.getContent();
        }

        return result;
    }


    private String toStringHeader() {
        StringBuffer result = new StringBuffer();
        result.append("From: ");
        if (this.getFrom() == null) {
            result.append('?');
        }
        else {
            result.append(this.getFrom());
        }
        result.append(" / To: ");
        if (this.getTo() == null) {
            result.append("?");
        }
        else {
            result.append(this.getTo());
        }
        result.append(" / Request: ");
        if (this.getRequestId() == null) {
            result.append("?");
        }
        else {
            result.append(this.getRequestId());
        }
        result.append(" /  Message: ");
        return result.toString();
    }


    /**
     * Short form of toString() to make logging less noisy. Does not dump
     * contents.
     * 
     * @return a short representation of the message
     */
    public String shortForm() {
        StringBuffer out = new StringBuffer();
        out.append(toStringHeader());
        out.append(' ');
        out.append(content.getClass());
        return out.toString();
    }


    /**
     * @return the request internal ID used to identify multiple messages in
     *         ambiguous situations
     */
    public String getRequestInternalId() {
        return requestInternalId;
    }


    /**
     * @param requestInternalId
     *            the request internal ID to set
     */
    public void setRequestInternalId(String requestInternalId) {
        this.requestInternalId = requestInternalId;
    }


    /**
     * Returns if the message's TTL field is still good. This check decreases
     * the TTL field by one.
     * 
     * @return true, if the message should keep on living. Else false.
     */
    public boolean checkAndDecreaseTtl() {
        ttl--;
        boolean good = true;
        if (ttl <= 0) {
            good = false;
        }
        return good;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((content == null) ? 0 : content.hashCode());
        result = prime * result + ((from == null) ? 0 : from.hashCode());
        result = prime * result + ((requestInternalId == null) ? 0 : requestInternalId.hashCode());
        result = prime * result + ((requestId == null) ? 0 : requestId.hashCode());
        result = prime * result + ((to == null) ? 0 : to.hashCode());
        return result;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Message other = (Message) obj;
        if (content == null) {
            if (other.content != null) {
                return false;
            }
        }
        else if (!content.equals(other.content)) {
            return false;
        }
        if (from == null) {
            if (other.from != null) {
                return false;
            }
        }
        else if (!from.equals(other.from)) {
            return false;
        }
        if (requestInternalId == null) {
            if (other.requestInternalId != null) {
                return false;
            }
        }
        else if (!requestInternalId.equals(other.requestInternalId)) {
            return false;
        }
        if (requestId == null) {
            if (other.requestId != null) {
                return false;
            }
        }
        else if (!requestId.equals(other.requestId)) {
            return false;
        }
        if (to == null) {
            if (other.to != null) {
                return false;
            }
        }
        else if (!to.equals(other.to)) {
            return false;
        }
        return true;
    }

}
