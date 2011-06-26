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

package de.unidue.inf.is.ezdl.gframedl.events;

import java.util.Date;
import java.util.List;

import de.unidue.inf.is.ezdl.dlcore.data.query.Query;



/**
 * The {@link SearchEvent} is posted to notify subscribers about a search having
 * finished and results having arrived.
 */
public class SearchEvent extends GFrameEvent {

    private static final long serialVersionUID = 5315199664566427389L;

    private Query query;
    private int searchResultsCount;
    private Date timestamp;
    private String queryId;
    private List<String> wrappers;


    public SearchEvent(Object eventSource, Query query, int searchResultsCount, Date timestamp, String queryId,
                    List<String> wrappers) {
        super(eventSource);
        this.query = query;
        this.searchResultsCount = searchResultsCount;
        if (timestamp != null) {
            this.timestamp = new Date(timestamp.getTime());
        }
        this.queryId = queryId;
        this.wrappers = wrappers;
    }


    public Query getQuery() {
        return query;
    }


    public int getSearchResultsCount() {
        return searchResultsCount;
    }


    public Date getTimestamp() {
        return timestamp != null ? new Date(timestamp.getTime()) : null;
    }


    public String getQueryId() {
        return queryId;
    }


    public List<String> getWrappers() {
        return wrappers;
    }
}
