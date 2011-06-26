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

package de.unidue.inf.is.ezdl.dlcore.message;

import java.io.Serializable;

import de.unidue.inf.is.ezdl.dlcore.message.content.MessageContent;



public final class MTAMessage implements Serializable {

    private static final long serialVersionUID = -4305507117816828445L;

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
     * Default constructor. Creates a new message with an empty message content
     * and an empty request ID.
     */
    public MTAMessage() {
        this(null, "");
    }


    /**
     * Creates a new Message object with given parameters.
     * 
     * @param content
     *            the payload of the message
     * @param requestId
     *            the request ID.
     */
    public MTAMessage(MessageContent content, String requestId) {
        setContent(content);
        setRequestId(requestId);
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
    public MTAMessage(MTAMessage original) {
        setContent(original.getContent());
        setRequestId(original.getRequestId());
    }


    /**
     * Creates an answer to this message with the given content.
     * 
     * @param content
     *            the content to use as the answer
     * @return the answer message
     */
    public MTAMessage tell(MessageContent content) {
        MTAMessage answer = new MTAMessage(content, getRequestId());
        return answer;
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


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((content == null) ? 0 : content.hashCode());
        result = prime * result + ((requestId == null) ? 0 : requestId.hashCode());
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
        MTAMessage other = (MTAMessage) obj;
        if (content == null) {
            if (other.content != null) {
                return false;
            }
        }
        else if (!content.equals(other.content)) {
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
        return true;
    }

}
