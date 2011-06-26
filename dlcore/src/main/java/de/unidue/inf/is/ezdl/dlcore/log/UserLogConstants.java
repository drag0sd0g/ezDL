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

package de.unidue.inf.is.ezdl.dlcore.log;

/**
 * Constants for the user log code.
 * <p>
 * Using these constants makes it easier to get reproducible logging.
 * 
 * @author mjordan
 */
public final class UserLogConstants {

    private UserLogConstants() {
    }


    /**
     * To indicate a "normal" session - i.e. the default case - not in an
     * evaluation setting, not for test or debugging purposes.
     */
    public static final String SESSION_TYPE_NORMAL = "normal";
    /**
     * To indicate an evaluation session.
     */
    public static final String SESSION_TYPE_EVALUATION = "eval";
    /**
     * To indicate a test or debugging session.
     */
    public static final String SESSION_TYPE_TEST = "test";

    /**
     * The name of a session start event.
     */
    public static final String EVENT_NAME_SESSION_START = "session_start";
    /**
     * The name of a session end event.
     */
    public static final String EVENT_NAME_SESSION_END = "session_end";

    /*
     * Contants that should be self-explaining.
     */
    public static final String EVENT_NAME_DOCUMENT_QUERY_ASK = "documentqueryask";
    public static final String EVENT_NAME_DOCUMENT_QUERY_TELL = "documentquerytell";
    public static final String EVENT_NAME_AVAILABLE_WRAPPERS_ASK = "availablewrappersask";
    public static final String EVENT_NAME_AVAILABLE_WRAPPERS_TELL = "availablewrapperstell";
    public static final String EVENT_NAME_DOCUMENT_DETAILS_ASK = "documentdetailsask";
    public static final String EVENT_NAME_DOCUMENT_DETAILS_TELL = "documentdetailstell";
    public static final String EVENT_NAME_TEXT_MESSAGE = "textmessage";
    public static final String EVENT_NAME_CANCEL_SEARCH = "cancelsearch";

}
