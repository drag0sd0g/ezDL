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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;



/**
 * The class DocumentQuery implements a query for documents.
 * <p>
 * The contents of this class is basically everything that, if it changes,
 * results in a different set of results.
 * <p>
 * Different condition: different results.
 * <p>
 * Different list of wrappers: different results.
 */
public class DocumentQuery implements Serializable {

    private static final long serialVersionUID = -4756744934603856328L;

    /**
     * Query.
     */
    private Query query;
    /**
     * DL List to ask
     */
    private List<String> dlList;


    /**
     * Creates a new object with the given query and list of Digital Libraries
     * to search.
     * 
     * @param query
     *            the query to use
     * @param dlList
     *            the list of DLs to search
     */
    public DocumentQuery(Query query, List<String> dlList) {
        super();
        this.query = query;
        this.dlList = new ArrayList<String>(dlList);
    }


    @Override
    public String toString() {
        StringBuffer out = new StringBuffer();
        out.append('{');
        out.append('[').append(query).append(']');
        out.append('(').append(dlList).append(')');
        out.append('}');
        return out.toString();
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((query == null) ? 0 : query.hashCode());
        result = prime * result + ((dlList == null) ? 0 : dlList.hashCode());
        return result;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        DocumentQuery other = (DocumentQuery) obj;
        if (query == null) {
            if (other.query != null) {
                return false;
            }
        }
        else if (!query.equals(other.query)) {
            return false;
        }
        if (dlList == null) {
            if (other.dlList != null) {
                return false;
            }
        }
        else if (!dlList.equals(other.dlList)) {
            return false;
        }
        return true;
    }


    /**
     * @return the quey
     */
    public Query getQuery() {
        return query;
    }


    /**
     * @return the list of DLs to ask
     */
    public List<String> getDLList() {
        return new ArrayList<String>(dlList);
    }


    /**
     * Removes DLs from DL list.
     * 
     * @param dl
     *            The DLs to remove
     * @return if a DL was actually removed
     */
    public boolean removeFromDLList(Collection<String> dls) {
        return dlList.removeAll(dls);
    }

}
