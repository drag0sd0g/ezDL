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

package de.unidue.inf.is.ezdl.gframedl.export;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import de.unidue.inf.is.ezdl.dlfrontend.i18n.I18nSupport;
import de.unidue.inf.is.ezdl.gframedl.Icons;
import de.unidue.inf.is.ezdl.gframedl.components.actions.AbstractSelectionActivatedAction;
import de.unidue.inf.is.ezdl.gframedl.components.actions.SelectionGetter;
import de.unidue.inf.is.ezdl.gframedl.events.ExportEvent;



/**
 * The export action that triggers the sending of an {@link ExportEvent}.
 * 
 * @author mjordan
 */
public final class ExportAction extends AbstractSelectionActivatedAction {

    private static final long serialVersionUID = -3409553110594297815L;


    /**
     * Creates a new ExportAction for the given source.
     * 
     * @param source
     *            the source from which to retrieve selected objects for export
     */
    public ExportAction(SelectionGetter source) {
        super(source);
        String name = I18nSupport.getInstance().getLocString("ezdl.actions.export");
        putValue(Action.SHORT_DESCRIPTION, name);
        putValue(Action.SMALL_ICON, Icons.EXPORT_ACTION.get16x16());
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        final SelectionGetter source = getClient();
        ExportEvent.fireExportEvent(source, source.getSelectedObjects());
    }

}
