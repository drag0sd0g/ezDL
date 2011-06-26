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

package de.unidue.inf.is.ezdl.dlfrontend.comm;

import java.util.EventObject;

import de.unidue.inf.is.ezdl.dlcore.message.content.MessageContent;



public final class BackendEvent extends EventObject {

    private static final long serialVersionUID = 7447821870272375499L;

    private MessageContent content;
    private String requestId;


    public BackendEvent(Object eventSource) {
        super(eventSource);
    }


    public BackendEvent(Object eventSource, MessageContent content) {
        super(eventSource);
        this.content = content;
    }


    public MessageContent getContent() {
        return content;
    }


    public String getRequestId() {
        return this.requestId;
    }


    public void setContent(MessageContent content) {
        this.content = content;
    }


    public void setRequestId(String refid) {
        this.requestId = refid;
    }
}
