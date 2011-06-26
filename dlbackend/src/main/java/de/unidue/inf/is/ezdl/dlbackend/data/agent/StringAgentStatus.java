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

package de.unidue.inf.is.ezdl.dlbackend.data.agent;

/**
 * A basic {@link AgentStatus} that uses a String to transport its information.
 * 
 * @author mjordan
 */
public class StringAgentStatus implements AgentStatus {

    private String status;


    /**
     * Creates a new status string "OK".
     */
    public StringAgentStatus() {
        this("OK");
    }


    /**
     * Creates a new status with the given message.
     * 
     * @param statusMsg
     *            the status message
     */
    public StringAgentStatus(String statusMsg) {
        setStatus(statusMsg);
    }


    public void setStatus(String statusMsg) {
        if (statusMsg == null) {
            throw new IllegalArgumentException("status must not be null");
        }
        this.status = statusMsg;
    }


    /**
     * Returns the status string.
     * 
     * @return the status string which is never null
     */
    public String getStatus() {
        return status;
    }


    @Override
    public String asString() {
        return status;
    }
}
