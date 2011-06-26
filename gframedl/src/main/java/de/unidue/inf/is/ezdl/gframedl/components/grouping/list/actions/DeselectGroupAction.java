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

package de.unidue.inf.is.ezdl.gframedl.components.grouping.list.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;

import de.unidue.inf.is.ezdl.gframedl.components.grouping.list.EquivalenceClass;
import de.unidue.inf.is.ezdl.gframedl.components.grouping.list.GroupedList;
import de.unidue.inf.is.ezdl.gframedl.components.grouping.resources.Resources;



/**
 * Deselect a group in the Grouped list.
 * 
 * @author RB1
 */
public class DeselectGroupAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    private static Icon icon = Resources.I_DESELECT_GROUP;

    private GroupedList list;
    private EquivalenceClass group;


    /**
     * Constructor.
     * 
     * @param list
     * @param group
     */
    public DeselectGroupAction(GroupedList list, EquivalenceClass group) {
        super();
        this.list = list;
        this.group = group;
        putValue(Action.SMALL_ICON, icon);
        putValue(Action.NAME, Resources.S_DESELECT_GROUP);
    }


    @Override
    public void actionPerformed(ActionEvent arg0) {
        list.deSelectGroup(group);
    }
}
