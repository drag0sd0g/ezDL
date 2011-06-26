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

import java.net.URL;
import java.util.ArrayList;



public class URLList extends ArrayList<URL> {

    private static final long serialVersionUID = 5731075880231396818L;


    public URLList() {
        super();

    }


    /**
     * Merges another {@link URLList} into this one. List items that are the
     * same (based on {@link #equals(Object)}) will not be added.
     * 
     * @param otherValue
     *            the other object to merge in - only URLLists are processed
     */
    public void merge(Object otherValue) {
        if (otherValue instanceof URLList) {
            URLList otherList = (URLList) otherValue;
            for (URL url : otherList) {
                if (!contains(url)) {
                    add(url);
                }
            }
        }
    }

}
