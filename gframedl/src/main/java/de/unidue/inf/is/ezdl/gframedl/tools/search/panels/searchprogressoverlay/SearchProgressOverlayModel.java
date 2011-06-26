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

package de.unidue.inf.is.ezdl.gframedl.tools.search.panels.searchprogressoverlay;

import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;



/**
 * A model which encapsulates all {@link BusyWrapperSpinnterItem} and offers
 * access methods.
 * 
 * @author markus
 */
class SearchProgressOverlayModel extends AbstractListModel {

    private static final long serialVersionUID = -9028796021508483041L;

    private List<SearchProgressSpinnerItem> data = new ArrayList<SearchProgressSpinnerItem>();


    public SearchProgressOverlayModel(List<SearchProgressSpinnerItem> data) {
        this.data = data;
    }


    @Override
    public SearchProgressSpinnerItem getElementAt(int index) {
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
     * Add an item.
     * 
     * @param items
     *            The item which should be added.
     */
    public void addItems(List<SearchProgressSpinnerItem> items) {
        data.addAll(items);
    }


    /**
     * Get an specific item.
     * 
     * @return The specific item.
     */
    public List<SearchProgressSpinnerItem> getItems() {
        return data;
    }

}
