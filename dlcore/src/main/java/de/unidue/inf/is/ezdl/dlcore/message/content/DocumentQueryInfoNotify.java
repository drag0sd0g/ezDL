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
 * Notifies the receiver about the progress of an ongoing search.
 * <p>
 * The notification basically says that a certain wrapper returned a given
 * number of documents, resulting in a given total number of documents.
 * 
 * @author mjordan
 */
public class DocumentQueryInfoNotify implements MessageContent {

    private static final long serialVersionUID = 1840506346081772096L;

    private String requestId;

    private String wrapperName;

    private int wrapperResults;

    private int totalCount;


    /**
     * Creates a new object with the given values and a null wrapper name and 0
     * results for the (null) wrapper.
     * 
     * @param requestId
     *            the ID of the request that sent the original query for this
     *            search
     * @param totalCount
     *            the total number of documents found for this query until now
     */
    public DocumentQueryInfoNotify(String requestId, int totalCount) {
        this(requestId, totalCount, null, 0);
    }


    /**
     * Creates a new object with the given values.
     * 
     * @param requestId
     *            the request ID
     * @param totalCount
     *            the total number of documents found for this query until now
     * @param wrapperName
     *            the name of the wrapper this piece of information is about
     * @param wrapperResults
     *            the number of result documents the given wrapper returned
     */
    public DocumentQueryInfoNotify(String requestId, int totalCount, String wrapperName, int wrapperResults) {
        super();
        if (requestId == null) {
            throw new IllegalArgumentException("queryId must not be null");
        }
        this.requestId = requestId;
        this.totalCount = totalCount;
        this.wrapperName = wrapperName;
        this.wrapperResults = wrapperResults;
    }


    /**
     * Returns the request ID, which is never null.
     * <p>
     * The query's request ID can be used later to retrieve results from the
     * same query without the DA having to ask the wrappers again.
     * 
     * @return the queryId
     */
    public String getRequestId() {
        return requestId;
    }


    /**
     * Returns the number of documents found if duplicates are considered one
     * single document.
     * 
     * @return the number of documents after merging
     */
    public int getCount() {
        return totalCount;
    }


    /**
     * Returns the number of results that the wrapper found.
     * 
     * @return the number of results that the wrapper found
     */
    public int getWrapperResultCount() {
        return wrapperResults;
    }


    /**
     * Returns the name of the wrapper.
     * 
     * @return the name of the wrapper
     */
    public String getWrapperName() {
        return wrapperName;
    }


    @Override
    public String toString() {
        StringBuffer out = new StringBuffer();
        out.append("{DocumentQueryInfoNotify ");
        out.append(getRequestId());
        out.append(' ');
        out.append(wrapperName).append(':').append(wrapperResults);
        out.append(' ');
        out.append('(').append(totalCount).append(')');
        out.append('}');
        return out.toString();
    }


    @Override
    public boolean equals(Object o) {
        if (o instanceof DocumentQueryInfoNotify) {
            DocumentQueryInfoNotify m = (DocumentQueryInfoNotify) o;
            return toString().equals(m.toString());
        }
        return false;
    }
}
