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

/**
 * Handler of messages from client.
 */
public interface MessageHandler {

    /**
     * Handles messages received from the client.
     * 
     * @param connectionId
     *            the ID of the connection that deals with the client connection
     * @param chunk
     *            the chunk to handle
     * @return true if the chunk could be handled. Else false (especially in
     *         case of errors).
     */
    boolean handleFromClient(String connectionId, String chunk);


    /**
     * To be called when the client hung up the connection. Logs the user out of
     * the UserAgent and unregisters the connection.
     * 
     * @param connectionId
     *            the ID of the connection that was just lost
     */
    void handleConnectionLost(String connectionId);


    /**
     * Logs message.
     * 
     * @param type
     *            the type
     * @param message
     *            the actual message to log
     */
    void log(String type, String message);
}
