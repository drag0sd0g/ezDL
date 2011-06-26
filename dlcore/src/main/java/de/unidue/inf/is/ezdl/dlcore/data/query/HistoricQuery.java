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

package de.unidue.inf.is.ezdl.dlcore.data.query;

import java.util.Date;
import java.util.List;

import de.unidue.inf.is.ezdl.dlcore.data.Mergeable;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.DLObject;



public class HistoricQuery implements DLObject {

    private static final long serialVersionUID = -9141904526426010331L;

    private List<String> wrappers;
    private Query query;
    private String queryId;
    private int searchResultsCount;
    private Date timestamp;


    public HistoricQuery(List<String> wrappers, Query query, String queryId, int searchResultsCount, Date timestamp) {
        this.wrappers = wrappers;
        this.query = query;
        this.queryId = queryId;
        this.searchResultsCount = searchResultsCount;
        if (timestamp != null) {
            this.timestamp = new Date(timestamp.getTime());
        }
    }


    public Query getQuery() {
        return query;
    }


    public String getQueryId() {
        return queryId;
    }


    public int getSearchResultsCount() {
        return searchResultsCount;
    }


    public Date getTimestamp() {
        return timestamp;
    }


    public long getTimespan() {
        return System.currentTimeMillis() - timestamp.getTime();
    }


    public List<String> getWrappers() {
        return wrappers;
    }


    @Override
    public String getOid() {
        return null;
    }


    @Override
    public boolean isSimilar(Mergeable oher) {
        return false;
    }


    @Override
    public void merge(Mergeable other) {
    }


    @Override
    public String asString() {
        return "HistoricQuery [wrappers=" + wrappers + ", query=" + query + ", queryId=" + queryId
                        + ", searchResultsCount=" + searchResultsCount + ", timestamp=" + timestamp + "]";
    }

}
