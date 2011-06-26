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

import de.unidue.inf.is.ezdl.dlcore.Haltable;
import de.unidue.inf.is.ezdl.dlcore.message.MTAMessage;
import de.unidue.inf.is.ezdl.dlcore.misc.ConnectionFailedException;



/**
 * Communication component to the backend.
 */
interface BackendCommunicator extends Haltable {

    public void init(String mtaHost, int mtaPort, int timeOutSecs) throws ConnectionFailedException;


    /**
     * Sends a message to the backend.
     * <p/>
     * <b>Implementations of this interface must ensure that this method will be
     * non-blocking!</b>
     * 
     * @param msg
     */
    public void send(MTAMessage msg);


    public void addMessageReceivedListener(MessageReceivedListener messageReceivedListener);

}
