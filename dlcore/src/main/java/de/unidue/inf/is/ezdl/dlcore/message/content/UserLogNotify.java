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

package de.unidue.inf.is.ezdl.dlcore.message.content;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import de.unidue.inf.is.ezdl.dlcore.log.UserLogConstants;
import de.unidue.inf.is.ezdl.dlcore.misc.NaturalOrderComparator;



/**
 * The message content that transports an event for the user log.
 * 
 * @author mjordan
 */
public final class UserLogNotify implements MessageContent {

    private static final long serialVersionUID = 2036572166336516444L;
    /**
     * The session ID to log for.
     */
    private String sessionId;
    /**
     * The number of this event in the sequence of all events in this session.
     */
    private int sequenceNumber;
    /**
     * The time stamp of when the event was processed in the back end (e.g.
     * MTA).
     */
    private long backendTimestamp;
    /**
     * The client-local time stamp of when the event happened.
     */
    private long clientTimestamp;
    /**
     * The name of the event.
     */
    private String eventName;
    /**
     * The parameters.
     */
    private ListMultimap<String, String> parameters;


    /**
     * Creates a new log event.
     * <p>
     * The client-local time stamp is set to the time at the creation of this
     * object.
     * 
     * @param sessionId
     *            the session ID to log the event for
     * @param eventName
     *            the name of the event. Please consult {@link UserLogConstants}
     *            for some possible values. To ensure consistent logging, make
     *            sure that the name of the event doesn't change over time.
     */
    public UserLogNotify(String sessionId, String eventName) {
        super();
        this.clientTimestamp = System.currentTimeMillis();
        this.sessionId = sessionId;
        this.eventName = eventName;
        this.parameters = ArrayListMultimap.create();
    }


    /**
     * Adds a parameter given its name and value.
     * <p>
     * If multiple values to the given parameters are to be logged (e.g.
     * multiple result items), call this method for each item in the list. <b>Do
     * not use this method for concatenations of values like "a,b,c" because it
     * would make parsing these lists more difficult if it had to be done on the
     * client's side.</b>
     * 
     * @param name
     *            the name of the parameter
     * @param value
     *            the value of the parameter
     */
    public void addParameter(String name, String value) {
        parameters.put(name, value);
    }


    /**
     * @see #addParameter(String, String)
     */
    public void addParameter(String name, int value) {
        parameters.put(name, Integer.toString(value));
    }


    /**
     * @see #addParameter(String, String)
     */
    public void addParameter(String name, long value) {
        parameters.put(name, Long.toString(value));
    }


    /**
     * Returns the session ID.
     * 
     * @return the session ID
     */
    public String getSessionId() {
        return sessionId;
    }


    /**
     * Returns the number of this event in the sequence of all events for this
     * session.
     * 
     * @return the number of this event in the sequence of all events for this
     *         session
     */
    public int getSequenceNumber() {
        return sequenceNumber;
    }


    /**
     * Sets the sequence number.
     * 
     * @param sequenceNumber
     *            the new sequence number
     */
    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }


    /**
     * Returns the back end time stamp.
     * <p>
     * The back end time stamp is the time stamp that is added when the object
     * arrives at the MTA. The client-oriented time stamp can also be tracked.
     * 
     * @return the time stamp
     * @see #getClientTimestamp()
     * @see #UserLogNotify(String, String)
     * @see #UserLogNotify(String, String, String)
     */
    public long getBackendTimestamp() {
        return backendTimestamp;
    }


    /**
     * Returns the client-local time stamp.
     * 
     * @return the time stamp that was added when the object was created in the
     *         client
     * @see #UserLogNotify(String, String)
     * @see #UserLogNotify(String, String, String)
     */
    public long getClientTimestamp() {
        return clientTimestamp;
    }


    /**
     * Sets the back end time stamp to current time.
     */
    public void backendTimestampNow() {
        this.backendTimestamp = System.currentTimeMillis();
    }


    /**
     * Returns the event name.
     * 
     * @return the event name
     */
    public String getEventName() {
        return eventName;
    }


    /**
     * Returns a reference to the parameter map.
     * 
     * @return a reference to the parameter map
     */
    public ListMultimap<String, String> getParameters() {
        return parameters;
    }


    /**
     * Returns the values for a paramater name.
     * 
     * @param paramName
     *            The name of the parameter
     * @return the values for a paramater name
     */
    public List<String> getParameters(String paramName) {
        return parameters.get(paramName);
    }


    /**
     * Returns the values of a list of parameters by the base name of the
     * parameter.
     * 
     * @param paramName
     *            The base name
     * @return the list of parameters
     */
    public List<String> getParameterCollection(String paramName) {
        List<String> result = new ArrayList<String>();
        Collection<Entry<String, String>> entries = parameters.entries();
        List<Entry<String, String>> entryList = new ArrayList<Entry<String, String>>(entries);
        Collections.sort(entryList, new Comparator<Entry<String, String>>() {

            private NaturalOrderComparator comparator = new NaturalOrderComparator();


            @Override
            public int compare(Entry<String, String> o1, Entry<String, String> o2) {
                return comparator.compare(o1.getKey(), o2.getKey());
            }
        });
        for (Entry<String, String> entry : entryList) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (key.matches(paramName + "\\d*")) {
                result.add(value);
            }
        }
        return result;
    }


    /**
     * Returns a single value (the first) for a paramater name.
     * 
     * @param paramName
     *            The name of the parameter
     * @return a single value (the first) for a paramater name
     */
    public String getSingleParameter(String paramName) {
        List<String> params = parameters.get(paramName);
        if (!params.isEmpty()) {
            return params.get(0);
        }
        else {
            return null;
        }
    }


    @Override
    public String toString() {
        return "{UserLogNotify " + sequenceNumber + " " + eventName + "@" + backendTimestamp + " sid: " + sessionId
                        + " [" + parameters + "]}";
    }
}
