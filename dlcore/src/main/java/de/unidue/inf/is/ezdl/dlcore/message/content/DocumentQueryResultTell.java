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

import de.unidue.inf.is.ezdl.dlcore.data.query.ResultDocumentList;



/**
 * The answer to a {@link DocumentQueryAsk} message content.
 */

public class DocumentQueryResultTell extends AbstractResultDocumentTell {

    private static final long serialVersionUID = -3059907919510305502L;

    /**
     * The total number of results available.
     */
    private int totalDocCount;


    /**
     * Creates a new object with the given results and setting the total result
     * size to the size of the results given.
     * 
     * @param results
     *            the result list
     */
    public DocumentQueryResultTell(ResultDocumentList results) {
        this(results, results.size());
    }


    /**
     * Creates a new object with the given results and the total result size
     * given.
     * 
     * @param results
     *            the result list
     * @param totalDocCount
     *            the total number of results available for the related query
     */
    public DocumentQueryResultTell(ResultDocumentList results, int totalDocCount) {
        super(results);
        this.totalDocCount = totalDocCount;
    }


    /**
     * @return the totalDocCount
     */
    public int getTotalDocCount() {
        return totalDocCount;
    }


    /**
     * @param totalDocCount
     *            the totalDocCount to set
     */
    public void setTotalDocCount(int totalDocCount) {
        this.totalDocCount = totalDocCount;
    }


    @Override
    public String toString() {
        return toInnerString("DocumentQueryTell");
    }

}
