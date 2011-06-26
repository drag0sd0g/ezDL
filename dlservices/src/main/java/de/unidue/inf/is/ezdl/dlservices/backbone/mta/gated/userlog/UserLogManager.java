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

package de.unidue.inf.is.ezdl.dlservices.backbone.mta.gated.userlog;

import de.unidue.inf.is.ezdl.dlcore.message.content.LoginTell;
import de.unidue.inf.is.ezdl.dlcore.message.content.MessageContent;
import de.unidue.inf.is.ezdl.dlservices.backbone.mta.gated.ConnectionInfo;



/**
 * Defines how the user logging is done.
 */
public interface UserLogManager {

    /**
     * Logs a message content for the session and context given by the user
     * information in the {@link LoginTell}.
     * 
     * @param userInfo
     *            the session and user information
     * @param content
     *            the content to log
     */
    public void logMessage(String connectionId, LoginTell userInfo, MessageContent content);


    /**
     * Logs a login event.
     * 
     * @param info
     *            the {@link ConnectionInfo} to log
     */
    public void logLogin(ConnectionInfo info);


    /**
     * Logs a logout event.
     * 
     * @param info
     *            the {@link ConnectionInfo} to log
     */
    public void logLogout(ConnectionInfo info);

}