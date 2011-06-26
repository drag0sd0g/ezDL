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

package de.unidue.inf.is.ezdl.gframedl.tools.search.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import de.unidue.inf.is.ezdl.dlfrontend.i18n.I18nSupport;



public final class ProjectionAction extends AbstractAction {

    private static final long serialVersionUID = -1754811787126862647L;


    // private ToolController tc;

    public ProjectionAction() {
        super(I18nSupport.getInstance().getLocString("ezdl.actions.refresh"));
        // tc = ToolController.getInstance();
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        // SearchTool tool = tc.getTool(SearchTool.class);
    }

}
