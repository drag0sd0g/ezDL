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

package de.unidue.inf.is.ezdl.gframedl.tools.search;

import javax.swing.DefaultListModel;



/**
 * The model of the result list. Features a refresh method.
 * 
 * @author tbeckers
 */
public class ResultListModel extends DefaultListModel {

    private static final long serialVersionUID = 1104299871955185164L;


    /**
     * Constructor.
     */
    public ResultListModel() {
        super();
    }


    /**
     * Refreshes the row that has the given object ID.
     * 
     * @param oid
     *            the object ID of the item to refresh
     */
    public void refresh(String oid) {
        for (int i = 0; i < size(); i++) {
            ResultItem resultItem = (ResultItem) get(i);
            if (resultItem.getDocId().equals(oid)) {
                refresh(i);
            }
        }
    }


    /**
     * Refreshes the row with the given number.
     * 
     * @param rowNumber
     *            the row number to refresh
     */
    public void refresh(int rowNumber) {
        fireContentsChanged(this, rowNumber, rowNumber);
    }

}
