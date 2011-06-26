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

import java.io.File;
import java.util.Properties;

import de.unidue.inf.is.ezdl.dlcore.utils.PropertiesUtils;
import de.unidue.inf.is.ezdl.dlcore.utils.SystemUtils;



/**
 * This class provides a convinient way to access the user configuration for the
 * ezDL user frontend. The functionality related to reading and writing the
 * actual properties files is delegated to PropertiesTools.
 */
public final class Config {

    /**
     * The instance of this singleton.
     */
    private static volatile Config instance;
    /**
     * Where the properties are actually stored.
     */
    private static Properties localProps;

    private static File perspectiveDir;
    private static String userSetPropertyDir;


    private Config() {
        getAndInitFrontendConfigDir();
    }


    /**
     * Reads the user property
     * 
     * @param property
     * @return
     */
    public String getUserProperty(String property) {
        return getUserProperty(property, null);
    }


    /**
     * Reads the user property
     * 
     * @param property
     * @return
     */
    public boolean getUserPropertyAsBoolean(String property) {
        return Boolean.valueOf(getUserProperty(property, null));
    }


    /**
     * Reads the user property.
     * <p>
     * If the property is not found, 0 is returned.
     * 
     * @param property
     *            the property key
     * @return the integer value of the property of 0 if there is no property
     *         with the given name
     */
    public int getUserPropertyAsInt(String property) {
        int value = 0;
        try {
            value = Integer.valueOf(getUserProperty(property, null));
        }
        catch (NumberFormatException e) {
        }
        return value;
    }


    /**
     * Reads a user property, treating it as a boolean variable.
     * 
     * @param property
     *            the property key
     * @param defaultValue
     *            the default value
     * @return the value of the key or the default value.
     */
    public boolean getUserPropertyAsBoolean(String property, boolean defaultValue) {
        String prop = localProps.getProperty(property);
        if (prop == null) {
            return defaultValue;
        }

        try {
            return Boolean.valueOf(prop);
        }
        catch (Exception e) {
            return defaultValue;
        }
    }


    /**
     * Reads a user property, treating it as a int variable.
     * 
     * @param property
     *            the property key
     * @param defaultValue
     *            the default value
     * @return the value of the key or the default value.
     */
    public int getUserPropertyAsInt(String property, int defaultValue) {
        String prop = localProps.getProperty(property);
        if (prop == null) {
            return defaultValue;
        }

        try {
            return Integer.valueOf(prop);
        }
        catch (NumberFormatException e) {
            return defaultValue;
        }
    }


    public String getUserProperty(String property, String defaultValue) {
        String prop = localProps.getProperty(property);
        String sysProp = System.getProperty(property);
        if (prop == null) {
            prop = defaultValue;
        }
        if (sysProp != null) {
            prop = sysProp;
        }
        return prop;
    }


    public void setUserProperty(String key, String value) {
        if (value != null) {
            localProps.setProperty(key, value);
        }
    }


    public void setUserProperty(String key, boolean value) {
        localProps.setProperty(key, String.valueOf(value));
    }


    public void setUserProperty(String key, int value) {
        localProps.setProperty(key, String.valueOf(value));
    }


    /**
     * Removes property entry from file.
     * 
     * @param key
     *            property which has to be removed
     */
    public void removeUserProperty(String key) {
        localProps.remove(key);
    }


    /**
     * Loads the user properties again from file.
     * 
     * @return the new properties object
     */
    public Properties refreshProperties() {
        localProps = null;
        localProps = readHomeProps();
        return localProps;
    }


    /**
     * Writes user preferences to file.
     */
    public void writeUserPreferences() {
        File f = getAndInitFrontendConfigDir();
        PropertiesUtils.writeUserPreferences(f, localProps);
    }


    public File getPerspectiveDir() {
        return perspectiveDir;
    }


    /**
     * Returns an instance of this singleton.
     * 
     * @return the instance
     */
    public static Config getInstance() {
        // double checked pattern
        if (instance == null) {
            synchronized (Config.class) {
                if (instance == null) {
                    instance = new Config();
                    readHomeProps();
                }
            }
        }
        return instance;
    }


    private static File getAndInitFrontendConfigDir() {
        File propertyDir = getPropertyDirName();
        if (propertyDir.exists() && propertyDir.isDirectory() || (!propertyDir.exists() && propertyDir.mkdirs())) {
            File file = new File(propertyDir, "perspectives");
            PropertiesUtils.mkdirsIfNotExists(file);
            perspectiveDir = file;
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


    private static Properties readHomeProps() {
        if (localProps == null) {
            localProps = PropertiesUtils.readHomeProps(getPropertyDirName());
        }
        return localProps;
    }

}
