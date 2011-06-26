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

package de.unidue.inf.is.ezdl.gframedl.transfer;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.TransferHandler;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;

import org.apache.log4j.Logger;

import de.unidue.inf.is.ezdl.dlcore.data.dldata.DLObject;



/**
 * A default {@link TransferHandler} for {@link JTextComponent}s. Supports
 * drag-and-drop as well as copy-and-paste.
 * 
 * @author tbeckers
 */
public class DefaultTextComponentTransferHandler extends TransferHandler {

    private static final long serialVersionUID = 927873729411144850L;

    private static Logger logger = Logger.getLogger(DefaultTextComponentTransferHandler.class);

    private JTextComponent textComponent;

    private TransferHandler previousHandler;


    public DefaultTextComponentTransferHandler(JTextComponent textComponent) {
        super();
        this.textComponent = textComponent;
        this.previousHandler = textComponent.getTransferHandler();
    }


    @Override
    public int getSourceActions(JComponent c) {
        return TransferHandler.COPY;
    }


    @Override
    public boolean importData(TransferSupport support) {
        try {
            List<String> sList = createStringList(support);
            return importStringList(support, sList);
        }
        catch (UnsupportedFlavorException e) {
            logger.error(e.getMessage(), e);
        }
        catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return false;
    }


    protected boolean importStringList(TransferSupport support, List<String> sList) {
        int pos = -1;
        if (textComponent.getSelectedText() != null) {
            try {
                textComponent.getDocument().remove(textComponent.getSelectionStart(),
                                textComponent.getSelectedText().length());
            }
            catch (BadLocationException e) {
                logger.error(e.getMessage(), e);
            }
        }
        if (support.isDrop()) {
            pos = textComponent.viewToModel(support.getDropLocation().getDropPoint());

        }
        else {
            pos = textComponent.getCaretPosition();
        }

        if (pos >= 0) {
            for (String s : sList) {
                try {
                    textComponent.getDocument().insertString(pos, s, null);
                    return true;
                }
                catch (BadLocationException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        return false;
    }


    @SuppressWarnings("unchecked")
    protected List<String> createStringList(TransferSupport support) throws UnsupportedFlavorException, IOException {
        List<String> sList = new ArrayList<String>();
        Transferable transferable = support.getTransferable();
        if (transferable.isDataFlavorSupported(DataFlavors.DL_OBJECT)) {
            List<DLObject> objects = (List<DLObject>) transferable.getTransferData(DataFlavors.DL_OBJECT);
            for (DLObject object : objects) {
                sList.add(object.asString());
            }
        }
        else if (transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            String s = (String) transferable.getTransferData(DataFlavor.stringFlavor);
            sList.add(s);
        }
        return sList;
    }


    @Override
    @Deprecated
    // required by DefaultCaret
    public boolean importData(JComponent comp, Transferable t) {
        return importData(new TransferSupport(comp, t));
    }


    @Override
    public void exportToClipboard(JComponent comp, Clipboard clip, int action) throws IllegalStateException {
        previousHandler.exportToClipboard(comp, clip, action);
    }


    @Override
    public boolean canImport(TransferSupport support) {
        return DataFlavors.canImport(support, DataFlavor.stringFlavor, DataFlavors.TERM, DataFlavors.AUTHOR,
                        DataFlavors.YEAR);
    }


    protected JTextComponent getTextComponent() {
        return textComponent;
    }

}
