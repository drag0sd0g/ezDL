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

package de.unidue.inf.is.ezdl.gframedl.components.grouping.list;

import de.unidue.inf.is.ezdl.gframedl.components.grouping.resources.Resources;



/**
 * The base class for grouping relations. This implementation is used as the
 * default.
 * 
 * @author RB1
 */
public class GroupByRelation {

    private static final long serialVersionUID = 1L;

    /**
     * In the case of no grouping (this implementation) all objects belong to
     * this class.
     */
    public static final EquivalenceClass ALL = new EquivalenceClass(Resources.S_ALL, null);


    /**
     * Constructor.
     */
    public GroupByRelation() {
    }


    /**
     * The name of the relation. This method must be overriden by the
     * descendents.
     * 
     * @return
     */
    public String getName() {
        return Resources.S_NO_GROUPING;
    }


    /**
     * The method assigns an object to an EquivalenceClass. This method must be
     * overriden by the descendents.
     * 
     * @param o
     * @return
     */
    public EquivalenceClass assignObject(Object o) {
        return ALL;
    }


    /**
     * Method is called by the groupBy combobox.
     */
    @Override
    public String toString() {
        return getName();
    }


    @Override
    public boolean equals(Object o) {
        if (o instanceof GroupByRelation) {
            return getName().equals(((GroupByRelation) o).getName());
        }
        else {
            return false;
        }
    }
}
