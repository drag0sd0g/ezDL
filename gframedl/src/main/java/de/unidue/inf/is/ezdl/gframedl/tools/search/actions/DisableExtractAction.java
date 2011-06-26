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
import javax.swing.Action;

import de.unidue.inf.is.ezdl.dlfrontend.i18n.I18nSupport;
import de.unidue.inf.is.ezdl.gframedl.Icons;
import de.unidue.inf.is.ezdl.gframedl.tools.search.SearchTool;
import de.unidue.inf.is.ezdl.gframedl.tools.search.panels.SearchControlsPanel;



/**
 * Default action for the extract button in the {@link SearchControlsPanel} of
 * the {@link SearchTool}. Is added to the list of Actions so that the extract
 * button can be disabled while a search is in progress.
 * 
 * @author tacke
 */
public class DisableExtractAction extends AbstractAction {

    private static final long serialVersionUID = 1254412767194167787L;


    /**
     * Constructor, that adds text label and icon of the extract action.
     */
    public DisableExtractAction() {
        putValue(Action.NAME, I18nSupport.getInstance().getLocString("ezdl.controls.resultlistpanel.label.extract"));
        putValue(Action.SMALL_ICON, Icons.EXTRACT_ACTION.get16x16());
    }


    @Override
    public void actionPerformed(ActionEvent e) {
    }

}
