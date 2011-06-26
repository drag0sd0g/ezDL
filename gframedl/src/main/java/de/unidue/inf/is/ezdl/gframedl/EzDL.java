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

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import de.unidue.inf.is.ezdl.dlcore.EzDLConstants;
import de.unidue.inf.is.ezdl.dlcore.data.User;
import de.unidue.inf.is.ezdl.dlcore.log.SessionType;
import de.unidue.inf.is.ezdl.dlcore.utils.PropertiesUtils;
import de.unidue.inf.is.ezdl.dlcore.utils.SystemUtils;
import de.unidue.inf.is.ezdl.dlcore.utils.SystemUtils.OperatingSystem;
import de.unidue.inf.is.ezdl.dlfrontend.dispatcher.Dispatcher;
import de.unidue.inf.is.ezdl.dlfrontend.i18n.I18nSupport;
import de.unidue.inf.is.ezdl.gframedl.debug.CheckThreadViolationRepaintManager;
import de.unidue.inf.is.ezdl.gframedl.helper.InternalProtocolFactory;



/**
 * Start class of the ezDL graphical (Swing) front end.
 */
final class EzDL {

    private static final String OPTION_DIR = "config";
    private static final String OPTION_DEBUG = "debug";
    /**
     * The {@link SessionType} to use if none was passed on the command line.
     */
    private static final SessionType DEFAULT_SESSION_TYPE = SessionType.STANDARD;

    /**
     * The logger.
     */
    private static Logger logger = Logger.getLogger(EzDL.class);

    /**
     * True if client was already started.
     */
    private static boolean started;
    /**
     * The class that parses the command line.
     */
    private static CommandLineParser parser = new PosixParser();
    /**
     * The session type.
     */
    private static SessionType sessionType;


    private EzDL() {
    }


    /**
     * Starts the application.
     * 
     * @param args
     *            an array of command-line arguments
     * @param application
     *            the application
     */
    static void start(String[] args, final Application application, final GraphicsConfiguration graphicsConfiguration) {
        if (!started) {
            started = true;

            parseCommandLine(args);

            URL.setURLStreamHandlerFactory(new InternalProtocolFactory());

            initLogging();

            if (System.getProperty("collection") != null) {
                logger.debug("Found Property: " + System.getProperty("collection"));
            }

            final Config config = Config.getInstance();

            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    initDesktopSystem();

                    application.setSessionType(sessionType);
                    SplashScreen splashScreen = new DefaultSplashScreen(application, graphicsConfiguration, sessionType);
                    application.setSplashScreen(splashScreen);

                    boolean autoLogin = config.getUserPropertyAsBoolean("autologin", false);
                    if (!autoLogin) {
                        splashScreen.showSplash();
                    }
                    else {
                        autoLogin(splashScreen);
                    }
                }
            });

        }
        else {
            throw new IllegalStateException("Already started");
        }
    }


    private static void parseCommandLine(String[] args) {
        CommandLine cmd = null;
        Options parserOptions = new Options();
        parserOptions.addOption(OPTION_DEBUG, false, "marks this session as a debug session");
        OptionBuilder.withArgName(OPTION_DIR);
        OptionBuilder.hasArg();
        OptionBuilder.withDescription("the directory with the config files");
        parserOptions.addOption(OptionBuilder.create(OPTION_DIR));

        try {
            cmd = parser.parse(parserOptions, args);
        }
        catch (ParseException e) {
            HelpFormatter formatter = new HelpFormatter();
            System.out.println("EzDL " + EzDLConstants.CLIENT_VERSION);
            formatter.printHelp("ezdl", parserOptions);
            System.exit(1);
        }

        if (cmd.hasOption(OPTION_DEBUG)) {
            System.out.println("debug option found");
            sessionType = SessionType.DEBUG;
        }
        else {
            sessionType = DEFAULT_SESSION_TYPE;
        }

        if (cmd.hasOption(OPTION_DIR)) {
            String dir = cmd.getOptionValue(OPTION_DIR);
            Config.setPropertyDir(dir);
            PropertiesUtils.setPropertyDir(dir);
            Config.getInstance().refreshProperties();
        }
    }


    private static void autoLogin(SplashScreen splashScreen) {
        Config config = Config.getInstance();
        String userName = config.getUserProperty(SplashScreen.USERNAME_KEY);
        if (userName == null) {
            userName = config.getUserProperty(SplashScreen.USERNAME_KEY, "visitor");
        }
        String password = config.getUserProperty(SplashScreen.PASSWORD_KEY);
        if (password == null) {
            password = config.getUserProperty(SplashScreen.PASSWORD_KEY, "visitor");
        }
        User user = new User();
        user.setLogin(userName);
        user.setPwd(password);

        splashScreen.showSplash(userName, password);
    }


    private static void initDesktopSystem() {
        RepaintManager.setCurrentManager(new CheckThreadViolationRepaintManager());

        Dispatcher.logEventDispatch = false;
        Dispatcher.logIncomingEvents = false;
        Dispatcher.logRegistration = false;
        Dispatcher.logWithThreadInfo = false;

        JFrame.setDefaultLookAndFeelDecorated(false);
        JDialog.setDefaultLookAndFeelDecorated(false);

        I18nSupport.getInstance().init(Config.getInstance().getUserProperty("desktop.language", "en"));

        try {
            if (SystemUtils.OS == OperatingSystem.MAC_OS) {
                System.setProperty("apple.laf.useScreenMenuBar", "true");
            }
            else {
                checkJavaVersion();

                for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            }
        }
        catch (UnsupportedLookAndFeelException e) {
            logger.error(e.getMessage(), e);
        }
        catch (ClassNotFoundException e) {
            logger.error(e.getMessage(), e);
        }
        catch (InstantiationException e) {
            logger.error(e.getMessage(), e);
        }
        catch (IllegalAccessException e) {
            logger.error(e.getMessage(), e);
        }
    }


    private static void checkJavaVersion() {
        Set<String> lafNames = new HashSet<String>();
        for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
            lafNames.add(info.getName());
        }
        if (!lafNames.contains("Nimbus")) {
            String message = "<html>Please install a recent version of Java 1.6 (http://www.java.com)!<br /><br /> Detected version: "
                            + System.getProperty("java.version");

            JOptionPane.showMessageDialog(null, message, "No appropriate Java version detected!",
                            JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }


    private static void initLogging() {
        System.setProperty("appDir", SystemUtils.getPropertyDir().getAbsolutePath());

        URL logConfigUrl = null;
        File logConfigFile = new File(SystemUtils.getPropertyDir(), "/logging.properties");

        if (logConfigFile.exists()) {
            try {
                logConfigUrl = logConfigFile.toURI().toURL();
            }
            catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        else {
            logConfigUrl = EzDL.class.getResource("/log/logging.properties");
        }

        if (logConfigUrl != null) {
            PropertyConfigurator.configure(logConfigUrl);
        }
        else {
            System.err.println("no logger config found!");
        }

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {

            @Override
            public void uncaughtException(Thread t, Throwable e) {
                logger.error("Uncaught exception in Thread " + t.getName(), e);
            }
        });
        System.err.close();
    }


    /**
     * Main method of the desktop application.
     * 
     * @param args
     *            an array of command-line arguments
     */
    public static void main(String[] args) {
        GraphicsConfiguration graphicsConfiguration = GraphicsEnvironment.getLocalGraphicsEnvironment()
                        .getDefaultScreenDevice().getDefaultConfiguration();

        Application app = new DesktopApplication(graphicsConfiguration);
        EzDL.start(args, app, graphicsConfiguration);
    }

}
