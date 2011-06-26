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

import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.JRootPane;
import javax.swing.WindowConstants;

import org.apache.log4j.Logger;

import de.unidue.inf.is.ezdl.dlcore.data.User;
import de.unidue.inf.is.ezdl.dlcore.log.SessionType;
import de.unidue.inf.is.ezdl.dlcore.utils.SystemUtils;
import de.unidue.inf.is.ezdl.dlcore.utils.SystemUtils.OperatingSystem;
import de.unidue.inf.is.ezdl.dlfrontend.dispatcher.Dispatcher;
import de.unidue.inf.is.ezdl.dlfrontend.i18n.I18nSupport;
import de.unidue.inf.is.ezdl.dlfrontend.security.SecurityInfo;
import de.unidue.inf.is.ezdl.gframedl.events.ExitEvent;



/**
 * The ezDL desktop application.
 */
final class DesktopApplication extends JFrame implements Application {

    private static final long serialVersionUID = 4070917662723679271L;

    private static final String DESKTOP_MAXIMIZED = "desktop.maximized";
    private static final String DESKTOP_Y = "desktop.y";
    private static final String DESKTOP_X = "desktop.x";
    private static final String DESKTOP_HEIGHT = "desktop.height";
    private static final String DESKTOP_WIDTH = "desktop.width";

    private static final Logger logger = Logger.getLogger(DesktopApplication.class);

    private Client client;

    private Config conf = Config.getInstance();


    public DesktopApplication(GraphicsConfiguration graphicsConfiguration) {
        super(graphicsConfiguration);
        initialize();
    }


    private void initialize() {
        this.client = new Client(this);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        Image logo = Images.LOGO_EZDL_APP_ICON.getImage();
        setIconImages(Arrays.asList(logo.getScaledInstance(16, 16, Image.SCALE_SMOOTH),
                        logo.getScaledInstance(32, 32, Image.SCALE_SMOOTH),
                        logo.getScaledInstance(64, 64, Image.SCALE_SMOOTH),
                        logo.getScaledInstance(128, 128, Image.SCALE_SMOOTH)));

        addComponentListener(new ComponentAdapter() {

            @Override
            public void componentResized(ComponentEvent e) {
                storeWindowPreferences();
            }


            @Override
            public void componentMoved(ComponentEvent e) {
                storeWindowPreferences();
            }

        });

        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                logger.info("Go offline");
                Dispatcher.postEvent(new ExitEvent(""));
            }

        });
        initScreenSize();

    }


    @Override
    public void setRootPane(JRootPane root) {
        super.setRootPane(root);
    }


    @Override
    public void setTitleInfo(String title) {
        super.setTitle(I18nSupport.getInstance().getLocString("ezdl.apptitle") + title);
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


    private void storeWindowPreferences() {
        if (getExtendedState() == Frame.MAXIMIZED_BOTH) {
            conf.setUserProperty(DESKTOP_MAXIMIZED, String.valueOf(true));
        }
        else {
            conf.setUserProperty(DESKTOP_WIDTH, String.valueOf(getWidth()));
            conf.setUserProperty(DESKTOP_HEIGHT, String.valueOf(getHeight()));
            conf.setUserProperty(DESKTOP_MAXIMIZED, String.valueOf(false));
        }

        if (isVisible()) {
            conf.setUserProperty(DESKTOP_X, String.valueOf(getLocationOnScreen().x));
            conf.setUserProperty(DESKTOP_Y, String.valueOf(getLocationOnScreen().y));
        }
    }


    private void initScreenSize() {
        int width;
        int height;
        int x = -1;
        int y = -1;
        boolean maximized;
        try {
            width = Integer.valueOf(conf.getUserProperty(DESKTOP_WIDTH));
            height = Integer.valueOf(conf.getUserProperty(DESKTOP_HEIGHT));
            x = Integer.valueOf(conf.getUserProperty(DESKTOP_X));
            y = Integer.valueOf(conf.getUserProperty(DESKTOP_Y));
            maximized = Boolean.valueOf(conf.getUserProperty(DESKTOP_MAXIMIZED));
        }
        catch (Exception e) {
            logger.info("using deafult values for screen size");

            GraphicsConfiguration graphicsConfiguration = getGraphicsConfiguration();
            Rectangle bounds = graphicsConfiguration.getBounds();

            width = (int) (bounds.width * 0.8);
            height = (int) (bounds.height * 0.8);
            maximized = false;
        }
        setSize(width, height);
        if (x < 0 || y < 0) {
            setLocationRelativeTo(null);
        }
        else {
            setLocation(x, y);
        }
        if (maximized) {
            setExtendedState(Frame.MAXIMIZED_BOTH);
        }
    }


    // workaround for bug (fixed in JDK 1.7)
    // http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6365898
    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);

        boolean maximized = Boolean.valueOf(conf.getUserProperty(DESKTOP_MAXIMIZED));
        if (SystemUtils.OS != OperatingSystem.WINDOWS && maximized && b) {
            setExtendedState(Frame.MAXIMIZED_BOTH);
        }
    }


    @Override
    public Window getWindow() {
        return this;
    }


    @Override
    public User getUser() {
        return client.getUser();
    }


    @Override
    public void setSplashScreen(SplashScreen splash) {
        client.setSplashScreen(splash);
    }


    /**
     * Sets the property userInfo and forwards it to the desktop instance.
     */
    @Override
    public void setUserInfo(User newUserInfo) {
        client.setUserInfo(newUserInfo);
    }


    /**
     * Set the session type.
     * 
     * @param sessionType
     */
    @Override
    public void setSessionType(SessionType sessionType) {
        client.setSessionType(sessionType);
    }

}
