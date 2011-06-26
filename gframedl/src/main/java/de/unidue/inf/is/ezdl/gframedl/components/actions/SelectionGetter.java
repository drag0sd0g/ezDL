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

package de.unidue.inf.is.ezdl.gframedl.components.actions;

import java.util.List;

import javax.swing.event.ListSelectionListener;



/**
 * SelectionGetter implementors can be asked about which objects are currently
 * selected by the user. If the selection changes, they notify a
 * {@link ListSelectionListener} about the change.
 * 
 * @author mjordan
 */
public interface SelectionGetter {

    /**
     * Returns the list of selected objects.
     * 
     * @return the list of selected DL objects
     */
    List<?> getSelectedObjects();


    /**
     * Adds the given {@link ListSelectionListener} to the implementor.
     * 
     * @param listener
     *            the {@link ListSelectionListener} to add
     */
    void addListSelectionListener(ListSelectionListener listener);
}
