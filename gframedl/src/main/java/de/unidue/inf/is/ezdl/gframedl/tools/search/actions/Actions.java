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

package de.unidue.inf.is.ezdl.gframedl.tools.search.actions;

public final class Actions {

    private Actions() {
    }


    public static final SearchAction SEARCH_ACTION = new SearchAction();

    public static final SortByAction SORT_BY_ACTION = new SortByAction();

    public static final SortOrderAction SORT_ORDER_ACTION = new SortOrderAction();

    public static final FilterAction FILTER_ACTION = new FilterAction();

    public static final ProjectionAction PROJECTION_ACTION = new ProjectionAction();

    public static final ClearQueryAction CLEAR_QUERY_ACTION = new ClearQueryAction();

    public static final WrapperUpdateAction WRAPPER_UPDATE_ACTION = new WrapperUpdateAction();

    public static final WrapperCategoryAction WRAPPER_CATEGORY_ACTION = new WrapperCategoryAction();

    public static final DisableExtractAction DISABLE_EXTRACT_ACTION = new DisableExtractAction();

    public static final ShowNowAction SHOW_NOW_ACTION = new ShowNowAction();

}
