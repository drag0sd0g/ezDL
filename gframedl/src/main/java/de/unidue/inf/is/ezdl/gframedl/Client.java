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

import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;
import java.util.EventObject;
import java.util.Properties;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import org.apache.log4j.Logger;

import de.unidue.inf.is.ezdl.dlcore.ErrorConstants;
import de.unidue.inf.is.ezdl.dlcore.data.User;
import de.unidue.inf.is.ezdl.dlcore.log.SessionType;
import de.unidue.inf.is.ezdl.dlcore.message.content.ErrorNotify;
import de.unidue.inf.is.ezdl.dlcore.message.content.LoginAsk;
import de.unidue.inf.is.ezdl.dlcore.message.content.LoginErrorNotify;
import de.unidue.inf.is.ezdl.dlcore.message.content.LoginTell;
import de.unidue.inf.is.ezdl.dlcore.message.content.LogoutAsk;
import de.unidue.inf.is.ezdl.dlcore.message.content.MessageContent;
import de.unidue.inf.is.ezdl.dlcore.message.content.UserLogNotify;
import de.unidue.inf.is.ezdl.dlcore.misc.ClientVersionException;
import de.unidue.inf.is.ezdl.dlcore.misc.ConnectionFailedException;
import de.unidue.inf.is.ezdl.dlcore.misc.DCCPClient;
import de.unidue.inf.is.ezdl.dlcore.utils.PropertiesUtils;
import de.unidue.inf.is.ezdl.dlcore.utils.SystemUtils;
import de.unidue.inf.is.ezdl.dlfrontend.comm.BackendEvent;
import de.unidue.inf.is.ezdl.dlfrontend.comm.BackendProxy;
import de.unidue.inf.is.ezdl.dlfrontend.comm.HttpBackendCommunicator;
import de.unidue.inf.is.ezdl.dlfrontend.dispatcher.Dispatcher;
import de.unidue.inf.is.ezdl.dlfrontend.dispatcher.EventReceiver;
import de.unidue.inf.is.ezdl.dlfrontend.i18n.I18nSupport;
import de.unidue.inf.is.ezdl.dlfrontend.security.DefaultSecurityInfo;
import de.unidue.inf.is.ezdl.dlfrontend.security.SecurityInfo;
import de.unidue.inf.is.ezdl.gframedl.events.ExitEvent;
import de.unidue.inf.is.ezdl.gframedl.events.OnlineToggle;



final class Client implements EventReceiver {

    private static final Logger logger = Logger.getLogger(Client.class);

    private Application application;

    private boolean connectedAndLoggedIn;

    private Desktop desktop;
    private SplashScreen splash;
    private User userInfo;
    private BackendProxy backendProxy;
    private SecurityInfo securityInfo;

    private String sessionId;
    private SessionType sessionType;

    private Config conf = Config.getInstance();


    Client(Application application) {
        this.application = application;

        Dispatcher.registerInterest(this, ExitEvent.class);
        Dispatcher.registerInterest(this, OnlineToggle.class);
    }


    private Desktop getDesktop() {
        if (desktop == null) {
            desktop = new DefaultDesktop(application);
        }
        return desktop;
    }


    BackendProxy getBackendProxy() {
        return backendProxy;
    }


    String getSessionId() {
        if (!isConnectedAndLoggedIn()) {
            sessionId = "OFFLINE";
        }
        return sessionId;
    }


    SecurityInfo getSecurityInfo() {
        return securityInfo;
    }


    synchronized void login() {
        BackendEvent askLogin = new BackendEvent(this);
        LoginAsk login1 = new LoginAsk(getUser().getLogin(), getUser().getPwd(), sessionType);
        MessageContent loginAsk = login1;
        askLogin.setContent(loginAsk);

        Dispatcher.registerInterest(this, BackendEvent.class);

        Dispatcher.postEvent(askLogin);
        setConnectedAndLoggedIn(true);
    }


    synchronized boolean isConnectedAndLoggedIn() {
        return connectedAndLoggedIn;
    }


    synchronized void setConnectedAndLoggedIn(boolean c) {
        connectedAndLoggedIn = c;
    }


    void logoutAndDisconnect() {
        if (isConnectedAndLoggedIn()) {
            LogoutAsk logout = new LogoutAsk(getSessionId());
            MessageContent msg = logout;

            BackendEvent goOfflineMsg = new BackendEvent(this);
            goOfflineMsg.setContent(msg);
            Dispatcher.postEvent(goOfflineMsg);
            Dispatcher.unregister(getBackendProxy());
        }
        setConnectedAndLoggedIn(false);
    }


    private void showErrorAndReLogin(ErrorConstants error) {
        showErrorAndReLogin(error.toString());
    }


    private void showErrorAndReLogin(final String error) {
        Runnable r = new Runnable() {

            @Override
            public void run() {
                logoutAndDisconnect();
                JOptionPane.showMessageDialog(Client.this.splash.getWindow(),
                                I18nSupport.getInstance().getLocString("error.login")
                                                + I18nSupport.getInstance().getLocString("error.login." + error),
                                "Connection", JOptionPane.ERROR_MESSAGE);
                splash.reset();
            }
        };
        SwingUtilities.invokeLater(r);
    }


    @Override
    public boolean handleEzEvent(EventObject ev) {
        if (ev instanceof ExitEvent) {
            handleExitEvent();
            return true;
        }
        else if (ev instanceof OnlineToggle) {
            doToggleOnline((OnlineToggle) ev);
        }
        else if (ev instanceof BackendEvent) {
            BackendEvent ae = (BackendEvent) ev;
            MessageContent messageContent = ae.getContent();
            if (messageContent instanceof LoginTell) {
                handleLoginTell((LoginTell) messageContent);
            }
            else if (messageContent instanceof LoginErrorNotify) {
                showErrorAndReLogin(((LoginErrorNotify) messageContent).getError());
            }
            else if (messageContent instanceof ErrorNotify) {
                showErrorAndReLogin(((ErrorNotify) messageContent).getError());
            }
        }
        return false;
    }


    private void handleExitEvent() {
        for (Window window : Window.getWindows()) {
            window.dispose();
        }
        conf.writeUserPreferences();
        backendProxy.halt();
        Dispatcher.postEvent(new OnlineToggle(this, false));
        Timer t = new Timer(1000, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        t.setRepeats(false);
        t.start();
    }


    private void handleLoginTell(LoginTell loginTell) {
        sessionId = loginTell.getSessionId();
        getUser().setFirstName(loginTell.getFirstName());
        getUser().setLastName(loginTell.getLastName());
        getUser().setLogin(loginTell.getLogin());
        application.setTitleInfo(" - (" + this.getUser().getLogin() + ")");

        setConnectedAndLoggedIn(true);
        securityInfo = new DefaultSecurityInfo(loginTell.getPrivileges());

        application.setRootPane(getDesktop().getDektopRootPane());
        desktop.initTools();
        splash.close();

        logSystemInfo();
    }


    private void logSystemInfo() {
        UserLogNotify userLogNotify = new UserLogNotify(ToolController.getInstance().getSessionId(), "desktopinfo");
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gds = ge.getScreenDevices();
        for (GraphicsDevice gd : gds) {
            DisplayMode dm = gd.getDisplayMode();
            int screenWidth = dm.getWidth();
            int screenHeight = dm.getHeight();
            userLogNotify.addParameter("screenx", screenWidth);
            userLogNotify.addParameter("screeny", screenHeight);
        }

        userLogNotify.addParameter("os", SystemUtils.getOSString());
        userLogNotify.addParameter("java", System.getProperty("java.version"));

        Dispatcher.postEvent(new BackendEvent(this, userLogNotify));
    }


    private void doToggleOnline(OnlineToggle ev) {

        boolean wasConnected = isConnectedAndLoggedIn();
        boolean goingToBeConnected = ev.getNewState();

        if (goingToBeConnected && !wasConnected) {
            final Properties props = new Properties();
            try {
                props.load(DefaultDesktop.class.getResourceAsStream("/" + BackendProxy.CONNECTION_PROPERTIES));
            }
            catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
            Properties propsFromHome = PropertiesUtils.readPropertiesFromFileTree(BackendProxy.CONNECTION_PROPERTIES);
            PropertiesUtils.collectProperties(props, propsFromHome);

            Runnable r = new Runnable() {

                @Override
                public void run() {
                    try {
                        startConnection(props);
                        login();
                    }
                    catch (ConnectionFailedException e) {
                        logger.debug("Exception: ", e);
                        Throwable cause = e.getCause();
                        if (cause instanceof ClientVersionException) {
                            logger.info("Client version wrong");
                            showErrorAndReLogin(ErrorConstants.WRONG_CLIENT_VERSION);
                        }
                        else {
                            showErrorAndReLogin(ErrorConstants.SERVER_NOT_READY);
                        }
                    }
                }

            };
            Thread t = new Thread(r);
            t.start();
        }
        else {
            logoutAndDisconnect();
        }
    }


    /**
     * Starts the {@link BackendProxy}.
     * <p>
     * First gets connection information from the properties file. If the MTA
     * host is not defined, tries to get connection information from the DCCP
     * whose address can be set in the properties.
     * 
     * @param props
     * @throws ConnectionFailedException
     */
    private void startConnection(final Properties props) throws ConnectionFailedException {
        String mtaHost = props.getProperty("mta.host");
        int mtaPort = PropertiesUtils.getIntProperty(props, "mta.port", 8080);
        int timeOutSecs = PropertiesUtils.getIntProperty(props, "mta.connectionTimeoutSecs", 5);

        logger.info("Connecting to " + mtaHost + " port " + mtaPort + " grace time " + timeOutSecs + " seconds.");

        if (mtaHost == null) {
            String dccpUrl = props.getProperty("dccp");
            if (dccpUrl != null) {
                URL mtaUrl = DCCPClient.getConnectionInfoForGui(dccpUrl);
                if (mtaUrl != null) {
                    mtaHost = mtaUrl.getHost();
                    mtaPort = mtaUrl.getPort();
                }
            }
        }

        this.backendProxy = new BackendProxy(new HttpBackendCommunicator());
        getBackendProxy().init(mtaHost, mtaPort, timeOutSecs);
    }


    User getUser() {
        return userInfo;
    }


    void setSplashScreen(SplashScreen splash) {
        this.splash = splash;
    }


    /**
     * Sets the property userInfo and forwards it to the desktop instance.
     */
    void setUserInfo(User newUserInfo) {
        userInfo = newUserInfo;
    }


    /**
     * Set the session type.
     * 
     * @param sessionType
     */

    void setSessionType(SessionType sessionType) {
        this.sessionType = sessionType;
    }

}
