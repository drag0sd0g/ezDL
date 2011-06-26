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

package de.unidue.inf.is.ezdl.gframedl.tools.search.panels.queryviews;

import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNode;



/**
 * Defines methods for interaction with query views. These are traditionally
 * forms (e.g. Google-like or four-field form with author, title, ..., but can
 * also be fancy things like the Parallel Coordinates interface.
 * 
 * @author mjordan
 */
public interface QueryView {

    public static final int MAX_FIELD_INPUT_LENGTH = 2000;


    /**
     * Updates the view. This means, the query form implemented by the
     * implementor of this interface uses the SearchTool-global query object to
     * update its form (e.g. text fields, sliders, ...). This can be thought of
     * as the reverse method of {@link #updateQuery()}.
     */
    void updateView();


    /**
     * Updates the SearchTool-global query object using the visible query
     * representation in the view. This can be thought of as the reverse method
     * of {@link #updateView()}.
     */
    void updateQuery();


    /**
     * Resets the view. All UI elements are reset to their initial/neutral
     * state. E.g. form fields are cleared, date selectors set to the current
     * date or whatever is appropriate.
     */
    void resetView();


    /**
     * Set the view to either usable or not usable. A usable view accepts input
     * while a non-usable one does not.
     * 
     * @param state
     *            the state of the view
     */
    void setUsable(UsabilityState state);


    /**
     * Returns if the view has some sort of error that would make it unusable.
     * 
     * @return true, if the view has an error, else false
     */
    boolean isErroneous();


    void updateWithHistoricQuery(QueryNode andNode);

}
