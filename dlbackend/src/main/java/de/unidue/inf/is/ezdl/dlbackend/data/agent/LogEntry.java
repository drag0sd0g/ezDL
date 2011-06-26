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
import java.text.SimpleDateFormat;
import java.util.Date;



/**
 * A log entry.
 */
public class LogEntry implements Serializable {

    private static final long serialVersionUID = -3493262612240811483L;

    /**
     * The type.
     */
    private String type;
    /**
     * The log message.
     */
    private String logStr;
    /**
     * The time stamp.
     */
    private Date timestamp;


    /**
     * Creates a new log entry with a current time stamp.
     */
    public LogEntry() {
        super();
        timestamp = new Date();
    }


    /**
     * The constructor. The timestamp of the entry is automatically generated.
     * 
     * @param ptype
     *            The type of the entry
     * @param plogStr
     *            The entry itself
     */
    public LogEntry(String ptype, String plogStr) {
        type = ptype;
        logStr = plogStr;
        timestamp = new Date();
    }


    /**
     * Formats the timestamp
     */
    public String formatTimestamp() {
        return new SimpleDateFormat(AgentLog.DATE_FORMAT_STRING).format(timestamp);
    }


    /**
     * Returns the type string of the entry.
     * 
     * @return the type
     */
    public String getType() {
        return type;
    }


    protected void setType(String type) {
        this.type = type;
    }


    /**
     * Returns the logged message.
     * 
     * @return the logged message
     */
    public String getLogStr() {
        return logStr;
    }


    protected void setLogStr(String logStr) {
        this.logStr = logStr;
    }


    /**
     * Returns the time stamp.
     * 
     * @return the time stamp
     */
    public Date getTimestamp() {
        /*
         * returning a copy of timestamp because Date is mutable and strange
         * things could happen if the date object got changed somewhere else.
         */
        return new Date(timestamp.getTime());
    }


    protected void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }


    @Override
    public String toString() {
        StringBuffer out = new StringBuffer();
        // out.append(formatTimestamp());
        // out.append(" ");
        out.append(type);
        out.append(" - ");
        out.append(logStr);
        return out.toString();
    }
}