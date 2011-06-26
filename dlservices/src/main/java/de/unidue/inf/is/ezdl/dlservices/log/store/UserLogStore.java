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

package de.unidue.inf.is.ezdl.dlservices.log.store;

import de.unidue.inf.is.ezdl.dlcore.message.content.UserLogNotify;



/**
 * The storage for the user log events.
 * 
 * @author tbeckers
 */
public interface UserLogStore {

    /**
     * Initializes log db.
     */
    public void init();


    /**
     * A user has logged in into the system
     * 
     * @param sessionId
     * @param login
     * @param startTimestamp
     * @param type
     */
    public void login(String sessionId, String login, long startTimestamp, String type);


    /**
     * A user has logged out from the system.
     * 
     * @param sessionId
     * @param stopTimestamp
     */
    public void logout(String sessionId, long stopTimestamp);


    /**
     * An user log event is stored.
     * 
     * @param logNotify
     */
    public void storeUserLog(UserLogNotify logNotify);

}
