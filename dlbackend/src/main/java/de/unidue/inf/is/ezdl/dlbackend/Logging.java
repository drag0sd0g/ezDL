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

package de.unidue.inf.is.ezdl.dlbackend;

import java.io.File;

import org.apache.log4j.PropertyConfigurator;



public final class Logging {

    private Logging() {
    }


    public static void initLogging() {
        String logConfigFile = System.getProperty("ezdl.log.config");
        if (logConfigFile == null) {
            System.out.println("reading logger configuration from internal properties file (no external logging configuration specified)");
            PropertyConfigurator.configure(Logging.class.getResource("/log/logging.properties"));
        }
        else {
            File f = new File(logConfigFile);
            System.out.println("trying to read logging configuration from " + f != null ? f.getAbsolutePath() : f);
            if (f.exists()) {
                System.out.println("reading logger configuration from properties file " + f.getAbsolutePath());
                PropertyConfigurator.configure(logConfigFile);
            }
            else {
                System.out.println("reading logger configuration from internal properties file");
                PropertyConfigurator.configure(Logging.class.getResource("/log/logging.properties"));
            }
        }
    }
}
