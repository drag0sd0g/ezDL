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

package de.unidue.inf.is.ezdl.dlcore.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import de.unidue.inf.is.ezdl.dlcore.misc.PropertiesKeys;



/**
 * This class knows about properties files. It defines which files are expected
 * and where they are stored and which one to use in which context (e.g.
 * backend, frontend). It also defines how lists in properties are separated. It
 * does not know about which keys to expect in a properties file. That is to be
 * defined by the classes that use the keys.
 * 
 * @author mjordan
 */
public class PropertiesUtils {

    /**
     * Separates list items.
     */
    private static final String LIST_SEPARATOR = ",";
    /**
     * Name of the global properties file that is shipped with the ezDL JARs.
     */
    private static final String PACKAGE_PROPERTIES = "global.properties";
    /**
     * Name of the shared agent property file.
     */
    private static final String AGENT_PROPERTIES = "agent.properties";
    /**
     * Name of the property file that contains end user account information and
     * auto-saved config.
     */
    private static final String CLIENT_PROPERTIES = "user.properties";

    /**
     * Cached result of determining the property directory
     */
    private static File propertyDir = null;
    /**
     * The log.
     */
    private static Logger logger = Logger.getLogger(PropertiesUtils.class);
    private static String userSetPropertyDir;

    /**
     * Username default key
     */
    private static final String USER_NAME = "username";
    /**
     * Username default value
     */
    private static final String USER_NAME_DEFAULT = "visitor";
    /**
     * Password default key
     */
    private static final String USER_PASSWORD = "password";
    /**
     * Password default value
     */
    private static final String USER_PASSWORD_DEFAULT = "visitor";


    /**
     * Reads a properties file. The file name can take several forms.
     * <ul>
     * <li>If the file name starts with a "/", it is treated as an absolute
     * name.</li>
     * <li>If the file name does not start with a "/", the file is read from the
     * working directory.</li>
     * </ul>
     * If a file <tt>agent.properties</tt> exists in the property dir, its
     * contents are read first and overwritten with those properties whose file
     * name was passed as fileName. This way global and common properties can be
     * configured centrally and only agent-dependent changes have to be
     * configured for each agent.
     * 
     * @param fileName
     * @return
     */
    public static Properties readPropertiesFromFileTree(String fileName) {
        File home = getAndInitBackendPropertyDir();

        Properties packageProps = readInternalProperties(PACKAGE_PROPERTIES);

        File agentPropsFile = new File(home, AGENT_PROPERTIES);
        Properties agentProps = readPropsFromFile(agentPropsFile, false);

        Properties props = new Properties();
        collectProperties(props, packageProps);
        collectProperties(props, agentProps);

        if (fileName != null) {
            File localFile = null;
            File file = new File(fileName);
            if (file.isAbsolute()) {
                localFile = file;
            }
            else {
                localFile = new File(home, fileName);
            }
            Properties localProps = readPropsFromFile(localFile, false);
            collectProperties(props, localProps);
        }

        Properties autoProps = getAutoProperties();
        collectProperties(props, autoProps);

        return props;
    }


    /**
     * Aggregates properties from inProps into the Properties object collector,
     * overwriting already existing keys in collector.
     * 
     * @param collector
     *            the destination Properties object
     * @param inProps
     *            the Properties object whose key value pairs are to be merged
     *            into collector
     */
    public static void collectProperties(Properties collector, Properties inProps) {
        for (Entry<?, ?> entr : inProps.entrySet()) {
            collector.setProperty(entr.getKey().toString(), entr.getValue().toString());
        }
    }


    /**
     * Reads properties file from within JAR file.
     * 
     * @param name
     *            the file name to read
     * @return the properties object
     */
    private static Properties readInternalProperties(String name) {
        Properties packageProps = new Properties();
        InputStream is = null;
        try {
            logger.info("Loading package properties: " + name);
            is = PropertiesUtils.class.getClassLoader().getResourceAsStream(name);
            if (is != null) {
                packageProps.load(is);
                is.close();
            }
        }
        catch (Exception exc) {
            logger.error("No " + name + " found\n", exc);
        }
        finally {
            ClosingUtils.close(is);
        }
        return packageProps;
    }


    /**
     * Reads properties from given file. IOExceptions encountered during the
     * read attempt are either ignored or logged, depending on the verbose
     * switch.
     * 
     * @param filename
     *            the name of the file to read
     * @param verbose
     *            if true, IOExceptions are logged.
     * @return a Properties object that might be empty if an IOException
     *         occurred
     */
    public static Properties readPropsFromFile(File file, boolean verbose) {
        Properties props = new Properties();
        FileInputStream stream = null;
        try {
            logger.info("Reading properties from " + file.getAbsolutePath());
            stream = new FileInputStream(file);
            props.load(stream);
            stream.close();
        }
        catch (IOException e) {
            if (verbose) {
                logger.error("Loading Properties failed: ", e);
            }
        }
        finally {
            ClosingUtils.close(stream);
        }
        return props;
    }


    /**
     * Read client properties. The file CLIENT_PROPERTIES is loaded from the
     * home directory of the user.
     */
    public static Properties readHomeProps(File configDir) {
        Properties homeProps = null;
        File file = new File(configDir, CLIENT_PROPERTIES);

        if (file.isFile()) {
            logger.info("Found user.properties at: " + file.getAbsolutePath());
            homeProps = PropertiesUtils.readPropsFromFile(file, true);
        }
        else {
            logger.info("Did not find a user.properties file at: " + file.getAbsolutePath());
            logger.info("Loading default values!");

            homeProps = new Properties();
            homeProps.put(USER_NAME, USER_NAME_DEFAULT);
            homeProps.put(USER_PASSWORD, USER_PASSWORD_DEFAULT);
        }
        return homeProps;
    }


    /**
     * Returns automatically determined properties.
     * 
     * @return automatically determined properties
     */
    private static Properties getAutoProperties() {
        Properties props = new Properties();
        props.setProperty(PropertiesKeys.AUTO_CONFIG_DIR, getAndInitBackendPropertyDir().getAbsolutePath());
        return props;
    }


    /**
     * Returns the ezdl property dir as a File Object according to the
     * underlying operating system.
     * 
     * @return
     */
    private static File getAndInitBackendPropertyDir() {

        if (propertyDir == null) {
            propertyDir = getPropertyDirName();
            mkdirsIfNotExists(new File(propertyDir, "perspectives"));
        }
        return propertyDir;
    }


    private static File getPropertyDirName() {
        if (userSetPropertyDir != null) {
            return new File(userSetPropertyDir);
        }
        else {
            return SystemUtils.getPropertyDir();
        }
    }


    /**
     * Sets the name of the directory to read user properties from.
     * <p>
     * The directory will be created if it does not exist and can be created. If
     * this is not set, the default is taken from
     * {@link SystemUtils#getPropertyDir()}.
     * 
     * @param dirName
     *            the name of the directory to use
     */
    public static void setPropertyDir(String dirName) {
        userSetPropertyDir = dirName;
    }


    /**
     * Creates a directory if it does not exist.
     * 
     * @param dir
     *            the directory to create
     */
    public static void mkdirsIfNotExists(File file) {
        if (!file.exists()) {
            file.mkdirs();
        }
    }


    /**
     * Write user properties. The properties get written to the file
     * CLIENT_PROPERTIES.
     * 
     * @param directory
     *            the object describing the directory to write to.
     * @param props
     *            the properties to write
     */
    public static void writeUserPreferences(File directory, Properties props) {
        FileOutputStream cf = null;
        try {
            cf = new FileOutputStream(directory.getAbsoluteFile() + File.separator + CLIENT_PROPERTIES);
            props.store(cf, "Header");
            cf.close();
        }
        catch (Exception e) {
            logger.error("Storing  Properties failed: ", e);
        }
        finally {
            ClosingUtils.close(cf);
        }
    }


    /**
     * Returns the list of tokens in the property with the given key. The tokens
     * are separated by {@link #LIST_SEPARATOR}.
     * 
     * @param props
     *            the properties object
     * @param key
     *            the key of the list property
     * @return the list of tokens.
     */
    public static List<String> getPropertyList(Properties props, String key) {
        List<String> tokens = new LinkedList<String>();
        StringTokenizer st = new StringTokenizer(props.getProperty(key), LIST_SEPARATOR);
        while (st.hasMoreTokens()) {
            tokens.add(st.nextToken().trim());
        }
        return tokens;
    }


    public static int getIntProperty(Properties props, String key, int defaultValue) {
        int value;
        try {
            value = Integer.parseInt(props.getProperty(key, "notanumber"));
        }
        catch (NumberFormatException e) {
            value = defaultValue;
        }
        return value;
    }
}
