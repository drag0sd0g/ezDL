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

package de.unidue.inf.is.ezdl.dlcore.query;

import de.unidue.inf.is.ezdl.dlcore.data.query.Query;



/**
 * Common interface for query converters for ezDL queries.
 * 
 * @author tbeckers
 */
public interface QueryConverter {

    /**
     * Converts a ezDL query to a query with a different syntax.
     * <p>
     * If the query cannot be mapped to the desired format, null is returned.
     * 
     * @param query
     *            The {@link Query}
     * @return the converted query as String (maybe even an empty String if the
     *         remote DL allows empty queries) or null if no such conversion is
     *         possible
     */
    public String convert(Query query);

}
