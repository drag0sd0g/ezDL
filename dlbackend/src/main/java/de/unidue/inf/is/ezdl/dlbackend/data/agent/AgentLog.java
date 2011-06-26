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

package de.unidue.inf.is.ezdl.dlbackend.data.agent;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;



/**
 * Log for agent events. This class is used to log events that occur in ezDL.
 * This log differs from the Log4j log in that it records also normal events and
 * is intended to be retrieved using the agent directory web interface
 * implemented in DirectoryWeb.
 */
public final class AgentLog implements Serializable {

    private static final long serialVersionUID = 3805261269920521677L;

    /**
     * The date format for log messages.
     */
    static final String DATE_FORMAT_STRING = "dd.MM.yyyy HH:mm:ss";
    /**
     * Default number of log entries that is kept.
     */
    public static final int MAX_ENTRIES_DEFAULT = 30;

    /**
     * The list of log entries.
     */
    private List<LogEntry> logVec;
    /**
     * The currently configured maximum number of log entries to keep.
     */
    private int maxEntries = MAX_ENTRIES_DEFAULT;
    /**
     * True, if logging is enabled.
     */
    private boolean loggingEnabled = true;


    public AgentLog() {
        this(MAX_ENTRIES_DEFAULT);
    }


    /**
     * Private constructor.
     * 
     * @param maxEntries
     *            the maximum number of entries to be kept
     */
    private AgentLog(int maxEntries) {
        setMax(maxEntries);
        logVec = new ArrayList<LogEntry>(maxEntries + 1);
    }


    /**
     * Creates a new Log with the default number of entries.
     * 
     * @return an AgentLog object that can be used to log agent events.
     */
    public static AgentLog startLog() {
        return startLog(MAX_ENTRIES_DEFAULT);
    }


    /**
     * Creates a new Log.
     * 
     * @param maxEntries
     *            the maximum number of entries. 0 means unlimited.
     * @return an AgentLog object that can be used to log agent events.
     */
    public static AgentLog startLog(int maxEntries) {

        AgentLog log;

        log = new AgentLog(maxEntries);

        String hostName = null;
        String ipAddress = null;
        try {
            InetAddress host;
            host = InetAddress.getLocalHost();
            hostName = host.getHostName();
            ipAddress = host.getHostAddress();
        }
        catch (UnknownHostException e) {
            Logger staticLog = Logger.getLogger(AgentLog.class);
            staticLog.warn("Could not get local host name. Guessing");
            hostName = "localhost (guessed)";
            ipAddress = "127.0.0.2";
        }

        log.add("log start", "Host: " + hostName + " (" + ipAddress + ")" + " / User: "
                        + System.getProperties().getProperty("user.name", "?"));

        return log;
    }


    /**
     * Adds a new test line to the log.
     * 
     * @param type
     *            the type of the log entry. The type is a text string
     *            describing what the log line is about. E.g.
     *            "received message".
     * @param toLog
     *            the line to log
     */
    public synchronized void add(String type, String toLog) {
        if (loggingEnabled) {
            if (maxEntries > 0) {
                while (logVec.size() >= maxEntries) {
                    logVec.remove(0);
                }
            }
            logVec.add(new LogEntry(type, toLog));
        }
    }


    /**
     * Removes all log entries.
     */
    public synchronized void clear() {
        logVec.clear();
    }


    /**
     * Returns the maximum number of log entries to keep.
     * 
     * @return the maximum number of log entries to keep
     */
    public synchronized int getMax() {
        return maxEntries;
    }


    /**
     * The number of log entries currently in the log.
     * 
     * @return the number of log entries currently in the log
     */
    public synchronized int getSize() {
        return logVec.size();
    }


    /**
     * Sets the logging to be enabled or not.
     */
    public synchronized void setLogging(boolean enabled) {
        this.loggingEnabled = enabled;
    }


    /**
     * Sets the maximum number of entries to keep.
     * 
     * @param maxEntries
     *            the maximum number of entries to keep
     */
    public synchronized void setMax(int maxEntries) {
        this.maxEntries = maxEntries;
    }


    /**
     * Returns the raw log data.
     * 
     * @return the raw log data
     */
    public List<LogEntry> getLogData() {
        return logVec;
    }


    @Override
    public String toString() {
        StringBuffer out = new StringBuffer();

        for (LogEntry entry : getLogData()) {
            out.append('[');
            out.append(entry.toString());
            out.append(']');
        }
        return out.toString();

    }
}
