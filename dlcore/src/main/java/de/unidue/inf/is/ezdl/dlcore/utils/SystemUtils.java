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



/**
 * utility class for OS specific things
 */
public final class SystemUtils {

    public enum OperatingSystem {
        WINDOWS, LINUX, MAC_OS, SOLARIS, UNKNOWN;
    }


    /**
     * MacOS-specific name of the ezDL config directory.
     */
    private static final String EZDL_DIR_NAME_MACOS = "ezdl";
    /**
     * Windows-specific name of the ezDL config directory.
     */
    private static final String EZDL_DIR_NAME_WINDOWS = "ezdl";
    /**
     * Linux-specific name of the ezDL config directory.
     */
    private static final String EZDL_DIR_NAME_LINUX = ".ezdl";

    /**
     * The operating system detected by ezDL
     */
    public static final OperatingSystem OS;

    static {
        String os = getOSString().toLowerCase();
        if (os.contains("linux")) {
            OS = OperatingSystem.LINUX;
        }
        else if (os.contains("windows")) {
            OS = OperatingSystem.WINDOWS;
        }
        else if (os.contains("mac os")) {
            OS = OperatingSystem.MAC_OS;
        }
        else if (os.contains("solaris")) {
            OS = OperatingSystem.SOLARIS;
        }
        else {
            OS = OperatingSystem.UNKNOWN;
        }
    }


    private SystemUtils() {
    }


    /**
     * Returns the property directory. If the directory does not exist, it is
     * created, including all necessary parent directories.
     * 
     * @return the property directory
     * @throws RuntimeException
     *             if the directory is not there and cannot be created.
     */
    public static File getPropertyDir() {
        String homeDirName;

        if (OperatingSystem.WINDOWS == OS) {
            homeDirName = System.getenv("APPDATA") + File.separator + EZDL_DIR_NAME_WINDOWS;
        }
        else {
            homeDirName = System.getProperty("user.home") + File.separator;
            if (OperatingSystem.MAC_OS == OS) {
                homeDirName += "Library" + File.separator + "Preferences" + File.separator + EZDL_DIR_NAME_MACOS;
            }
            else {
                homeDirName += EZDL_DIR_NAME_LINUX;
            }
        }

        File homeDir = new File(homeDirName);
        if ((homeDir.exists() && homeDir.isDirectory()) || (!homeDir.exists() && homeDir.mkdirs())) {
            return homeDir;
        }
        else {
            throw new RuntimeException("PropertyDir not there or cannot be created: " + homeDirName);
        }
    }


    public static String getOSString() {
        return System.getProperty("os.name");
    }

}
