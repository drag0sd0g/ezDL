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

package de.unidue.inf.is.ezdl.gframedl.perspectives;

import java.awt.Component;

import javax.swing.Icon;

import net.infonode.docking.View;
import de.unidue.inf.is.ezdl.gframedl.tools.ToolView;



/**
 * A custom subclass of the docking framework view
 */
class EzDLView extends View {

    private static final long serialVersionUID = 8257797077019827367L;


    /**
     * Creates a new tool view
     * 
     * @param toolView
     *            the view
     */
    public EzDLView(ToolView toolView) {
        super(" " + toolView.getToolViewName(), toolView.getParentTool().getSmallIcon(), toolView.getPanel());
    }


    public EzDLView(String title, Icon icon, Component component) {
        super(" " + title, icon, component);
    }

}
