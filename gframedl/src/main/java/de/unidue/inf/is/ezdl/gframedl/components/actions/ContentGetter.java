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

import java.awt.event.ComponentListener;
import java.util.List;

import javax.swing.event.ListDataListener;



/**
 * ContentGetter implementors can be asked about which objects are currently
 * present in them. If the content changes, they notify a
 * {@link ComponentListener} about the change.
 * 
 * @author mjordan
 */
public interface ContentGetter {

    /**
     * Returns the list of objects that are present.
     * 
     * @return the list of present objects
     */
    List<?> getContentObjects();


    /**
     * Adds the given listener to the implementor.
     * 
     * @param listener
     *            the listener to add
     */
    void addListDataListener(ListDataListener listener);
}
