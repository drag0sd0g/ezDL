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

package de.unidue.inf.is.ezdl.dlbackend.agent.connectors;

import java.io.IOException;

import de.unidue.inf.is.ezdl.dlbackend.message.Message;
import de.unidue.inf.is.ezdl.dlcore.misc.ConnectionFailedException;



/**
 * The AgentConnector interface describes objects that can handle the
 * communication between agents. Classes implementing AgentConnector must also
 * extend Thread so that they can wait for incoming messages without blocking
 * the actual agent.
 * <p>
 * Normally, an AgentConnector gets a reference to the agent passes at
 * initialization so it can call back to the agent to handle incoming messages.
 * 
 * @author mjordan
 */
public interface AgentConnector {

    /**
     * Goes online and starts the communication.
     * 
     * @throws ConnectionFailedException
     */
    void goOnline() throws ConnectionFailedException;


    /**
     * Close a connection.
     * 
     * @throws IOException
     *             if we can not shutdown normaly cause of io-error
     */
    void goOffline() throws IOException;


    /**
     * Returns, if the connector is online and connected or not.
     * 
     * @return true, if the connector is online. Else false.
     */
    boolean isOnline();


    /**
     * Sends a message.
     * 
     * @param message
     *            the Message
     * @throws IOException
     *             if the message can not be delivert to the end-point
     */
    void send(Message message) throws IOException;

}