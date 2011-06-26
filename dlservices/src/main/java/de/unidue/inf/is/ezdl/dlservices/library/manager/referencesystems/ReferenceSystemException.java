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

package de.unidue.inf.is.ezdl.dlservices.library.manager.referencesystems;

/** Exception class which is used by the referencesystems */
public class ReferenceSystemException extends Exception {

    private static final long serialVersionUID = 5814006182634753113L;

    public static int MENDELEY_VERIFIER_REQUIRED = 1;

    private String httpCode;
    private String errordescription;
    private int otherRequired;
    private String url;


    public ReferenceSystemException() {
        super();
        httpCode = "";
    }


    /**
     * a user action is required like the mendeley vierfier have to be sent
     * 
     * @param otherRequired
     *            required type. for exampel
     *            ReferenceSystemException.MENDELEY_VERIFIER_REQUIRED
     * @param url
     *            for example authentication url for mendeley
     */
    public ReferenceSystemException(int otherRequired, String url) {
        this.otherRequired = otherRequired;
        this.url = url;
    }


    public ReferenceSystemException(String errordescription, String exceptionmsg) {
        super(exceptionmsg);
        this.errordescription = errordescription;
        httpCode = "";
        otherRequired = 0;
        url = null;
    }


    public ReferenceSystemException(String httpCode, String errordescription, String exceptionmsg) {
        super(exceptionmsg);
        this.errordescription = errordescription;
        this.httpCode = httpCode;
        otherRequired = 0;
        url = null;
    }


    /** Returns the http Code of the error description */
    public String getHttpCode() {
        return this.httpCode;
    }


    /** Returns the error description */
    public String getErrordescription() {
        return this.errordescription;
    }


    /**
     * returns the required user action like
     * ReferenceSystemException.MENDELEY_VERIFIER_REQUIRED
     */
    public int getOtherRequired() {
        return otherRequired;
    }


    /** Returns the url for the required user action. null if there is no url */
    public String getUrl() {
        return url;
    }
}
