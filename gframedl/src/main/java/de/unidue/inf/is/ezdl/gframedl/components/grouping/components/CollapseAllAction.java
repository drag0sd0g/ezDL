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

package de.unidue.inf.is.ezdl.gframedl.components.grouping.components;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;

import de.unidue.inf.is.ezdl.gframedl.components.grouping.GroupsContainer;
import de.unidue.inf.is.ezdl.gframedl.components.grouping.resources.Resources;



/**
 * Collapse all groups of a GroupsContainer.
 * 
 * @author RB1
 */
public class CollapseAllAction extends AbstractAction {

    private static final long serialVersionUID = 1341962837749602030L;
    private GroupsContainer groups;
    private static Icon icon = Resources.I_COLLAPSE_ALL;


    /**
     * Constructor.
     */
    public CollapseAllAction(GroupsContainer groups) {
        this.groups = groups;
        putValue(Action.SMALL_ICON, icon);
        putValue(Action.NAME, Resources.S_COLLAPSE_ALL);
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        groups.collapseAll();
    }
}
