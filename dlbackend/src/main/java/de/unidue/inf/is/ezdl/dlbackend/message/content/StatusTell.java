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

package de.unidue.inf.is.ezdl.dlbackend.message.content;

import de.unidue.inf.is.ezdl.dlbackend.data.agent.AgentStatus;
import de.unidue.inf.is.ezdl.dlcore.message.content.MessageContent;



/**
 * Response to a {@link StatusAsk} message.
 * 
 * @author mjordan
 */
public class StatusTell implements MessageContent {

    private static final long serialVersionUID = 1L;

    /**
     * The status to be transported.
     */
    private AgentStatus status;


    /**
     * Creates a new status response with the given status.
     * 
     * @param status
     *            the status to send
     */
    public StatusTell(AgentStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("status must not be null");
        }
        this.status = status;
    }


    /**
     * Returns the status.
     * 
     * @return the status, which is never null
     */
    public AgentStatus getStatus() {
        return status;
    }


    @Override
    public String toString() {
        return "{StatusTell " + status.asString() + "}";
    }
}
