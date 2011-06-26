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

package de.unidue.inf.is.ezdl.dlcore.message.content;

/**
 * Allows to transport textual content used for e.g. "Message of the day" or
 * warning notifications.
 * 
 * @author mjordan
 */
public class TextMessageNotify implements MessageContent {

    private static final long serialVersionUID = -8639282086298862237L;


    /**
     * The priority levels that may be used to filter the messages on the client
     * side.
     * 
     * @author mjordan
     */
    public enum Priority {
        INFO, CHAT, WARN
    };


    private Priority priority;

    private String title;

    private String content;

    private String from;


    /**
     * Constructor.
     * 
     * @param priority
     *            the priority of the message
     * @param title
     *            the title - may be null
     * @param content
     *            the content of the message
     * @param from
     *            the sender
     */
    public TextMessageNotify(Priority priority, String title, String content, String from) {
        this.priority = priority;
        this.title = title;
        this.content = content;
        this.from = from;
    }


    /**
     * @return the priority
     */
    public Priority getPriority() {
        return priority;
    }


    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }


    /**
     * @return the content
     */
    public String getContent() {
        return content;
    }


    /**
     * @return the from
     */
    public String getFrom() {
        return from;
    }


    @Override
    public String toString() {
        return "{TextMessageNotify " + priority + " from " + from + ": " + content + "}";
    }
}
