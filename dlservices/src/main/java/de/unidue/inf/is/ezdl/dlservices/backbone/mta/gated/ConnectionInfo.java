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

import de.unidue.inf.is.ezdl.dlcore.log.SessionType;
import de.unidue.inf.is.ezdl.dlcore.message.content.LoginTell;



/**
 * Information about running connections. Needed for checking the authentication
 * and maybe authorization of incoming messages from the client.
 * 
 * @author mjordan
 */
public class ConnectionInfo {

    private String connectionId;
    private LoginTell userInfo;
    private SessionType sessionType;
    private long loginTime;


    /**
     * Constructor
     * 
     * @param userInfo
     *            the user infor to set
     * @param connectionId
     *            the connection ID to set
     */
    public ConnectionInfo(String connectionId, LoginTell userInfo, SessionType sessionType) {
        super();
        this.connectionId = connectionId;
        this.userInfo = userInfo;
        this.sessionType = sessionType;
        this.loginTime = System.currentTimeMillis();
    }


    /**
     * @return the connectionId
     */
    public String getConnectionId() {
        return connectionId;
    }


    /**
     * @return the userInfo
     */
    public LoginTell getUserInfo() {
        return userInfo;
    }


    /**
     * @return the session type
     */
    public SessionType getSessionType() {
        return sessionType;
    }


    /**
     * Returns the login time
     * 
     * @return the login time in system time
     * @see System#currentTimeMillis()
     */
    public long getLoginTime() {
        return loginTime;
    }
}