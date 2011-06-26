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

import de.unidue.inf.is.ezdl.dlcore.data.query.DocumentQuery;
import de.unidue.inf.is.ezdl.dlcore.data.query.ResultConfiguration;



/**
 * The message content that transports a query to the agents concerned with
 * query processing.
 * <p>
 * Contents are the query itself and a description of the desired results, e.g.
 * which fields to send (thin clients might not want to receive full abstracts
 * since they cannot display them anyway).
 * 
 * @see DocumentQueryResultTell
 */

public class DocumentQueryAsk implements MessageContent {

    private static final long serialVersionUID = 6740355358225396808L;

    private static final int DEFAULT_MAX_DURATION_MS = 10000;

    private DocumentQuery query;
    private ResultConfiguration resultConfig;
    private int maxDurationMs = DEFAULT_MAX_DURATION_MS;
    private String requestId = "";

    /**
     * If set to true, the wrappers are asked to use their cache.
     */
    private boolean usingCache = false;


    public DocumentQueryAsk(DocumentQuery query, ResultConfiguration resultConfig) {
        this.query = query;
        this.resultConfig = resultConfig;
    }


    /**
     * @return the query
     */
    public DocumentQuery getQuery() {
        return query;
    }


    /**
     * @param query
     *            the query to set
     */
    public void setQuery(DocumentQuery query) {
        this.query = query;
    }


    /**
     * @return the maxDurationMs
     */
    public int getMaxDurationMs() {
        return maxDurationMs;
    }


    /**
     * Sets the maximum duration of the search process in milliseconds.
     * <p>
     * Default is 10000 ms.
     * 
     * @param maxDurationMs
     *            the maximum duration to set in milliseconds
     */
    public void setMaxDurationMs(int maxDurationMs) {
        this.maxDurationMs = maxDurationMs;
    }


    /**
     * @return the requestId
     */
    public String getRequestId() {
        return requestId;
    }


    /**
     * @param requestId
     *            the requestId to set
     */
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }


    /**
     * @return the usingCache
     */
    public boolean isUsingCache() {
        return usingCache;
    }


    /**
     * @param usingCache
     *            the usingCache to set
     */
    public void setUsingCache(boolean usingCache) {
        this.usingCache = usingCache;
    }


    /**
     * @return the resultConfig
     */
    public ResultConfiguration getResultConfig() {
        return resultConfig;
    }


    @Override
    public String toString() {
        return "{DocumentQueryAsk " + requestId + " " + maxDurationMs + "ms :" + query + "}";
    }


    @Override
    public boolean equals(Object o) {
        return toString().equals(o.toString());
    }

}
