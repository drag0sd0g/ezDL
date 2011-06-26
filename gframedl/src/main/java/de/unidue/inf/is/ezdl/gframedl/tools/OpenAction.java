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

package de.unidue.inf.is.ezdl.gframedl.tools;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;



public class OpenAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    private Tool tool;


    public OpenAction(Tool tool) {
        super();
        this.tool = tool;
        putValue(Action.NAME, tool.getName());
        putValue(Action.LARGE_ICON_KEY, tool.getBigIcon());
        putValue(Action.SMALL_ICON, tool.getSmallIcon());
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        tool.open();
        setEnabled(false);
    }


    public Tool getTool() {
        return tool;
    }
}
