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

package de.unidue.inf.is.ezdl.dlcore.misc;

public final class PropertiesKeys {

    private PropertiesKeys() {
    }


    /**
     * The related value is automatically set to contain the name of the config
     * directory.
     */
    public static final String AUTO_CONFIG_DIR = "configdir";

    public static final String WEBADMIN_USER = "webadmin.user";
    public static final String WEBADMIN_PASSWORD = "webadmin.password";
    public static final String WEBADMIN_PORT = "webadmin.port";

    public static final String MTA_NAMES = "mta.agentnames";
    public static final String MTA_NAMES_DEFAULT = "EzDLMTA";

    /**
     * The properties key that contains the maximum number of entries in the
     * agent log.
     */
    public static final String LOG_MAXSIZE = "log.maxsize";
    /**
     * Set to "true" if LogAsk and LogTell should be logged, too.
     */
    public static final String LOG_LOGASKTELL = "log.LogAskTell";

    /**
     * The property key that holds the name of the directory agent.
     */
    public static final String DIR_NAME = "agentname.directory";
    public static final String DIR_NAME_DEFAULT = "EzDLDirectory";
    /**
     * The property name of the timeout for answers from the directory.
     */
    public static final String DIR_TIMEOUT = "dir.timeout";

}
