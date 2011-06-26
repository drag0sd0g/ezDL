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

import javax.swing.JApplet;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;

import de.unidue.inf.is.ezdl.dlcore.data.User;
import de.unidue.inf.is.ezdl.dlcore.log.SessionType;
import de.unidue.inf.is.ezdl.dlfrontend.dispatcher.Dispatcher;
import de.unidue.inf.is.ezdl.dlfrontend.security.SecurityInfo;
import de.unidue.inf.is.ezdl.gframedl.events.ExitEvent;



/**
 * The ezDL applet application.
 */
public final class AppletApplication extends JApplet implements Application {

    private static final long serialVersionUID = 4070917662723679271L;

    private Client client;


    public AppletApplication() {
    }


    private void initialize() {
        this.client = new Client(this);
        setName("ezDL");
    }


    @Override
    public void init() {
    }


    @Override
    public void start() {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                EzDL.start(new String[] {}, AppletApplication.this, null);
                initialize();
            }
        });
    }


    @Override
    public void stop() {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                Dispatcher.postEvent(new ExitEvent(""));
            }
        });
    }


    @Override
    public void destroy() {
    }


    @Override
    public void setRootPane(JRootPane root) {
        super.setRootPane(root);
    }


    @Override
    public void setTitleInfo(String title) {
    }


    @Override
    public String getSessionId() {
        return client.getSessionId();
    }


    @Override
    public SecurityInfo getSecurityInfo() {
        return client.getSecurityInfo();
    }


    @Override
    public synchronized void login() {
        client.login();
    }


    @Override
    public synchronized boolean isConnectedAndLoggedIn() {
        return client.isConnectedAndLoggedIn();
    }


    @Override
    public void logoutAndDisconnect() {
        client.logoutAndDisconnect();
    }


    @Override
    public Window getWindow() {
        return null;
    }


    @Override
    public User getUser() {
        return client.getUser();
    }


    @Override
    public void setSplashScreen(SplashScreen splash) {
        client.setSplashScreen(splash);
    }


    @Override
    public void setUserInfo(User newUserInfo) {
        client.setUserInfo(newUserInfo);
    }


    @Override
    public void setSessionType(SessionType sessionType) {
        client.setSessionType(sessionType);
    }
}
