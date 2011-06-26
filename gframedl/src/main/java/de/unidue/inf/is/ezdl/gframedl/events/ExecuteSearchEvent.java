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

import java.util.List;

import de.unidue.inf.is.ezdl.dlcore.data.query.Query;



/**
 * Event that signals that some (canned) search query should be executed.
 * <p>
 * This event can be dispatched e.g. in tools like the query history tool that
 * present a collection of prepared search queries that can be selected and run
 * automatically.
 */
public class ExecuteSearchEvent extends GFrameEvent {

    /**
     * Serial blabla.
     */
    private static final long serialVersionUID = 5315199664566427389L;
    /**
     * The query to maybe execute.
     */
    private Query query;
    /**
     * The list of wrapper.
     */
    private List<String> wrappers;
    /**
     * If set to true, the query within should be executed. Otherwise it is only
     * to be displayed by the recipient.
     */
    private boolean shouldBeExecuted;


    /**
     * The constructor.
     * 
     * @param eventSource
     *            who sent the event
     * @param query
     *            the query
     * @param shouldBeExecuted
     *            true if the query is to be executed, false otherwise (e.g. to
     *            only display it).
     */
    public ExecuteSearchEvent(Object eventSource, List<String> wrappers, Query query, boolean shouldBeExecuted) {
        super(eventSource);
        this.query = query;
        this.wrappers = wrappers;
        this.shouldBeExecuted = shouldBeExecuted;
    }


    /**
     * Returns the query.
     * 
     * @return the query
     */
    public Query getQuery() {
        return query;
    }


    public List<String> getWrappers() {
        return wrappers;
    }


    /**
     * Returns if the query should be executed.
     * 
     * @return true, if so. Else false.
     */
    public boolean shouldBeExecuted() {
        return shouldBeExecuted;
    }

}
