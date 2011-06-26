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

import de.unidue.inf.is.ezdl.dlcore.Haltable;



/**
 * 
 */
public interface Server extends Haltable {

    /**
     * Halts a particular connection, dropping the client.
     * 
     * @param connectionId
     *            the ID of the connection to drop
     */
    void haltConnection(String connectionId);


    /**
     * Sends a chunk to the server that has the given ID.
     * <p>
     * A chunk is a single piece of information that belongs together - e.g. a
     * serialized object or an XML message.
     * 
     * @param connectionId
     *            the connection ID of the receiving object
     * @param chunk
     *            the chunk to send
     */
    void send(String connectionId, String chunk);


    /**
     * Receives a chunk from the thread that has the given ID.
     * 
     * @param connectionId
     *            the connection ID
     * @param chunk
     *            the chunk to send
     * @return true if the chunk was handled. Else false.
     * @see #send(String, String)
     */
    boolean receive(String connectionId, String chunk);


    void connectionLost(String connectionId);


    String getClientHost(String connectionId);


    /**
     * Returns the number of open connections.
     * 
     * @return the number of connections
     */
    int connections();

}