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

package de.unidue.inf.is.ezdl.dlcore.data.extractor;

import java.util.List;



/**
 * This interface will help to provide a base set of methods to be presented in
 * different ways.
 * 
 * @author Jens Kapitza
 * @version 01.03.2010
 */
public interface ExtractionResult {

    /**
     * @return the total number of matches (e.g. the count of the items.)
     */
    int getTotal();


    /**
     * @return the minimum count. (e.g. the lowest number in the list.)
     */
    int getMinimum();


    /**
     * @return the maximum count. (e.g. the highest number in the list.)
     */
    int getMaximum();


    /**
     * @return a {@link List} of {@link Entry}s which is used to display it as
     *         table.
     */
    List<Entry> tableData();


    /**
     * Normalize the {@link Entry} set to a given range.
     * 
     * @param startRange
     *            the range start value.
     * @param stopRange
     *            the range stop value.
     * @throws IllegalArgumentException
     *             if stopRange < startRange || startRange < 0.
     * @return a {@link List} of {@link Entry}s mapped into the range.
     */
    List<Entry> cloudData(int startRange, int stopRange);
}
