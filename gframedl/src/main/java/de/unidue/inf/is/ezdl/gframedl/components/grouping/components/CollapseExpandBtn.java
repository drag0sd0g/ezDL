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
import java.awt.event.ActionListener;

import de.unidue.inf.is.ezdl.gframedl.components.grouping.GroupContainer;
import de.unidue.inf.is.ezdl.gframedl.components.grouping.resources.Resources;



/**
 * The button collapses/expands the owner GroupContainer.
 * 
 * @author RT
 */
public class CollapseExpandBtn extends TitleBtn implements ActionListener {

    private static final long serialVersionUID = 1L;
    private GroupContainer owner;


    public CollapseExpandBtn(GroupContainer owner) {
        super(null);
        this.owner = owner;
        this.addActionListener(this);
        setIconState();
    }


    /**
     * Sets the icon according to the owner isCollapsed state.
     */
    public void setIconState() {
        if (owner.isCollapsed()) {
            setIcon(Resources.I_EXPAND);
        }
        else {
            setIcon(Resources.I_COLLAPSE);
        }
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        owner.setCollapsed(!owner.isCollapsed());
    }
}
