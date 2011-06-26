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

package de.unidue.inf.is.ezdl.gframedl.tools.details.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import de.unidue.inf.is.ezdl.dlfrontend.i18n.I18nSupport;
import de.unidue.inf.is.ezdl.gframedl.Icons;
import de.unidue.inf.is.ezdl.gframedl.events.AddToLibraryEvent;
import de.unidue.inf.is.ezdl.gframedl.tools.Tool;
import de.unidue.inf.is.ezdl.gframedl.tools.details.DetailView;



/**
 * Save the DLObject in the library.
 */
public class CopyToLibraryAction extends AbstractAction {

    private static final long serialVersionUID = 2019180901929830983L;

    private Tool tool;
    private DetailView detailView;


    public CopyToLibraryAction(Tool t, DetailView detailView) {
        super(I18nSupport.getInstance().getLocString("ezdl.actions.copyToLibrary"));
        this.tool = t;
        this.detailView = detailView;
        putValue(Action.SMALL_ICON, Icons.COPY_TO_LIBRARY.get16x16());
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        AddToLibraryEvent.fireLibraryEvent(tool, detailView.getObject());
    }
}
