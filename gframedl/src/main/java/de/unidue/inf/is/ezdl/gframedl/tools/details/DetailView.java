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

package de.unidue.inf.is.ezdl.gframedl.tools.details;

import java.util.List;

import javax.swing.Action;
import javax.swing.Icon;

import de.unidue.inf.is.ezdl.dlcore.data.dldata.DLObject;



/**
 * A DetailView for a specific class must
 * <ul>
 * <li>implement this Interface</li>
 * <li>be derived from Component</li>
 * <li>have a parameterless constructor</li>
 * <li>be registered at DetailViewFactory</li>
 * </ul>
 */
public interface DetailView {

    /**
     * The object to see details of.
     * 
     * @param o
     *            the {@link DLObject}
     * @param highlightStrings
     *            list of {@link String}s that should be somehow highlighted
     */
    public void setObject(DLObject o, List<String> highlightStrings);


    /**
     * The object to see details of.
     */
    public DLObject getObject();


    /**
     * The name of tab.
     */
    public String getTabName();


    /**
     * The icon of tab, null is possible.
     */
    public Icon getIcon();


    /**
     * Should return a List of possible Actions for this DV, may be null. If
     * Actions are returned, they should have an Icon + Name. This method is
     * used by DetailViewContainer.
     */
    public List<Action> getPossibleActions();

}
