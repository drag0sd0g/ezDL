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

package de.unidue.inf.is.ezdl.gframedl.components.checkboxlist;

import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;



/**
 * Custom list model for {@link CheckBoxJList}
 * 
 * @author tbeckers
 */
public class CheckBoxListModel extends AbstractListModel {

    private static final long serialVersionUID = 8972754259787110419L;

    private List<CheckBoxListItem> data;


    /**
     * Instantiates the model with an empty list.
     */
    public CheckBoxListModel() {
        this.data = new ArrayList<CheckBoxListItem>();
    }


    /**
     * Instantiates the model with a list of {@link CheckBoxListItem}
     * 
     * @param list
     *            of items
     */
    public CheckBoxListModel(List<CheckBoxListItem> data) {
        this.data = data;
    }


    @Override
    public Object getElementAt(int index) {
        if (index < getSize() && index >= 0) {
            return data.get(index);
        }
        else {
            return null;
        }
    }


    @Override
    public int getSize() {
        return data.size();
    }


    /**
     * Returns a <code>List</code> with the selected {@link CheckBoxListItem}
     * items.
     * 
     * @return selected items
     */
    public List<CheckBoxListItem> getCheckedItems() {
        List<CheckBoxListItem> result = new ArrayList<CheckBoxListItem>();
        for (CheckBoxListItem item : data) {
            if (item.isSelected()) {
                result.add(item);
            }
        }
        return result;
    }


    /**
     * Add the supplied items to the model
     * 
     * @param items
     *            to be added
     */
    public void setItems(List<CheckBoxListItem> items) {
        if (items != null) {
            data.clear();
            data.addAll(items);
        }
        fireIntervalAdded(this, 0, getSize() - 1);
    }


    public List<CheckBoxListItem> getItems() {
        return data;
    }
}
