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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;

import org.apache.log4j.Logger;

import de.unidue.inf.is.ezdl.dlcore.utils.ClosingUtils;
import de.unidue.inf.is.ezdl.dlfrontend.converter.ExportResult;
import de.unidue.inf.is.ezdl.dlfrontend.i18n.I18nSupport;
import de.unidue.inf.is.ezdl.gframedl.Icons;



/**
 * The action that triggers saving an export result to the file system.
 * 
 * @author mjordan
 */
class SaveAction extends AbstractAction {

    private static final long serialVersionUID = -2604839164332552731L;

    private static Logger logger = Logger.getLogger(SaveAction.class);

    private ExportDialog dialog;


    /**
     * Creates a new save action that uses the given {@link ExportDialog} to get
     * export results from.
     * 
     * @param dialog
     *            the dialog to get export results from
     */
    SaveAction(ExportDialog dialog) {
        this.dialog = dialog;
        putValue(Action.NAME, I18nSupport.getInstance().getLocString("ezdl.actions.save"));
        putValue(Action.SMALL_ICON, Icons.SAVE.get16x16());
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int res = fc.showSaveDialog(dialog);
        if (res == JFileChooser.APPROVE_OPTION) {
            export(fc.getSelectedFile());
        }
    }


    private void export(File file) {
        final ExportResult result = dialog.results();
        if (result != null) {
            FileOutputStream saveFile = null;
            try {
                saveFile = new FileOutputStream(file);
                saveFile.write(result.asByteArray());
            }
            catch (FileNotFoundException e) {
                logger.error(e.getMessage(), e);
            }
            catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
            finally {
                ClosingUtils.close(saveFile);
            }
        }
        dialog.close();
    }

}
