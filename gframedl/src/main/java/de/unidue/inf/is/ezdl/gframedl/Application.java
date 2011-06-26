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

package de.unidue.inf.is.ezdl.gframedl;

import java.awt.Window;

import javax.swing.JRootPane;

import de.unidue.inf.is.ezdl.dlcore.data.User;
import de.unidue.inf.is.ezdl.dlcore.log.SessionType;
import de.unidue.inf.is.ezdl.dlfrontend.security.SecurityInfo;



/**
 * Common interface for applications
 * 
 * @author tbeckers
 */
public interface Application {

    String getSessionId();


    /**
     * Set the session type.
     * 
     * @param sessionType
     */
    void setSessionType(SessionType sessionType);


    SecurityInfo getSecurityInfo();


    void login();


    boolean isConnectedAndLoggedIn();


    void logoutAndDisconnect();


    User getUser();


    /**
     * Sets the property userInfo and forwards it to the desktop instance.
     */
    void setUserInfo(User user);


    void setSplashScreen(SplashScreen splash);


    void setTitleInfo(String title);


    void setRootPane(JRootPane rootPane);


    Window getWindow();


    void setVisible(boolean b);

}