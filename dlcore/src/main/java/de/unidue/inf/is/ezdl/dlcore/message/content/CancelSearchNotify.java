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

/**
 * Notifies the receiver that a search has been canceled.
 * <p>
 * This class looks very similar to CancelRequestNotify. This class is used to
 * signify that a search is to be cancelled while the other class cancels
 * arbitrary requests.
 * 
 * @author mjordan
 */
public class CancelSearchNotify implements MessageContent {

    private static final long serialVersionUID = -290797812849846261L;

    /**
     * The request ID to cancel.
     */
    private String requestId;
    /**
     * True if partial results are to be sent. Else false.
     */
    private boolean sendPartialResults;


    /**
     * Creates a new object for the given query, not requesting partial results
     * to be returned.
     * 
     * @param queryID
     *            the ID of the query to cancel
     */
    public CancelSearchNotify(String queryID) {
        this(queryID, false);
    }


    /**
     * Creates a new object with the given parameters.
     * 
     * @param queryID
     *            the ID to cancel
     * @param sendPartialResults
     *            true if partial results should be returned. Else false.
     */
    public CancelSearchNotify(String queryID, boolean sendPartialResults) {
        this.requestId = queryID;
        this.sendPartialResults = sendPartialResults;
    }


    /**
     * @return the queryID
     */
    public String getQueryID() {
        return requestId;
    }


    /**
     * Returns if partial results should be sent.
     * 
     * @return true if partial results are to be sent. Else false.
     */
    public boolean isSendPartialResults() {
        return sendPartialResults;
    }


    @Override
    public String toString() {
        return "{Cancel " + requestId + (sendPartialResults ? " (get partials)" : " (drop partials)") + "}";
    }

}
