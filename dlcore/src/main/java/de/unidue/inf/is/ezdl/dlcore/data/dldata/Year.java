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

package de.unidue.inf.is.ezdl.dlcore.data.dldata;

import de.unidue.inf.is.ezdl.dlcore.data.Mergeable;



/**
 * Immutable representation of a simple Year within ezDL.
 * 
 * @author tacke
 */
public class Year extends AbstractDLObject {

    private static final long serialVersionUID = 652816193478920220L;

    private int year;


    /**
     * Instantiated with the desired year.
     * 
     * @param year
     *            as an Integer value
     */
    public Year(int year) {
        this.year = year;
    }


    /**
     * This method might be extended by using an actual similarity method for
     * strings (e.g., edit distance).
     */
    @Override
    public boolean isSimilar(Mergeable obj) {
        if (obj instanceof Year) {
            return year == (((Year) obj).year);
        }
        return false;
    }


    /**
     * Returns the year value.
     * 
     * @return year as an Integer value
     */
    public Integer getYear() {
        return year;
    }


    @Override
    public String asString() {
        return String.valueOf(year);
    }

}
