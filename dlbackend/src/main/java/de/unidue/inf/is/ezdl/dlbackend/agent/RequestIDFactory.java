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

package de.unidue.inf.is.ezdl.dlbackend.agent;

import java.util.concurrent.atomic.AtomicLong;



/**
 * Contains knowledge about request IDs used in ezDL.
 * 
 * @author mj
 */
public final class RequestIDFactory {

    /**
     * Defines the maximum length used for the ID strings. This is not enforced
     * anywhere, just informational.
     */
    public static final int MAX_LENGTH = 100;
    /**
     * The instance of this singleton.
     */
    private static RequestIDFactory instance = null;
    /**
     * The counter used in generating the IDs.
     */
    private AtomicLong requestCounter = new AtomicLong(0);


    /**
     * Private constructor so getInstance() has to be used.
     */
    private RequestIDFactory() {
    }


    /**
     * Returns an instance of this singleton.
     * 
     * @return an instance of the factory
     */
    public static synchronized RequestIDFactory getInstance() {
        if (instance == null) {
            instance = new RequestIDFactory();
        }
        return instance;
    }


    /**
     * Calculates a unique request ID.
     * 
     * @param localPart
     *            the local part to enforce global uniqueness.
     * @return the ID
     */
    public String getNextRequestID(String localPart) {
        String hexCounter = null;
        hexCounter = Long.toHexString(requestCounter.incrementAndGet());
        String uid = new java.rmi.server.UID().toString();
        StringBuffer requestID = new StringBuffer(MAX_LENGTH);
        requestID.append(hexCounter).append('.').append(uid).append('@').append(localPart);
        return requestID.toString();
    }


    /**
     * Calculates a request ID that can be used for Notify messages that don't
     * require the request ID to be unique.
     * 
     * @return a request ID
     */
    public String getNextRequestID() {
        return getNextRequestID("");
    }
}
