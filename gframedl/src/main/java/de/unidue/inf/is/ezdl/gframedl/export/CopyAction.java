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

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import de.unidue.inf.is.ezdl.dlfrontend.converter.ExportResult;
import de.unidue.inf.is.ezdl.dlfrontend.i18n.I18nSupport;
import de.unidue.inf.is.ezdl.gframedl.Icons;



/**
 * The action that triggers copying an export result to the system clipboard.
 * 
 * @author mjordan
 */
class CopyAction extends AbstractAction {

    private static final long serialVersionUID = -6693241153390070720L;

    private ExportDialog dialog;


    /**
     * Creates a new copy action that uses the given {@link ExportDialog} to get
     * export results from.
     * 
     * @param dialog
     *            the dialog to get export results from
     */
    CopyAction(ExportDialog dialog) {
        this.dialog = dialog;
        putValue(Action.NAME, I18nSupport.getInstance().getLocString("ezdl.actions.copyToClipboard"));
        putValue(Action.SMALL_ICON, Icons.COPY_TO_CLIPBOARD.get16x16());
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        ExportResult result = dialog.results();
        if (result != null) {
            StringSelection contents = new StringSelection(result.asString());
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(contents, null);
        }
    }

}
