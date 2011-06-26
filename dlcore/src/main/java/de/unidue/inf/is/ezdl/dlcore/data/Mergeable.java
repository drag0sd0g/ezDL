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

package de.unidue.inf.is.ezdl.dlcore.data;

/**
 * Interface to allow merging similar objects.
 * <p>
 * E.g. two result lists could be merged, merging similar documents into one and
 * adding documents that differ from the others.
 * 
 * @author mj
 */
public interface Mergeable {

    /**
     * Merges information with another mergeable object.
     * 
     * @param other
     *            the other object to be merged into this one.
     */
    void merge(Mergeable other);


    /**
     * Test, if we can assume this will be equal. For authors, this could mean
     * that e.g. "Jens Kapitza" and "null Kapitza" are the same with a simple
     * pattern.
     * 
     * @param other
     *            the object to compare to
     * @return true if the object is similar. Else false.
     */
    boolean isSimilar(Mergeable other);

}
