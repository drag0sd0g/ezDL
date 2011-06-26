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

package de.unidue.inf.is.ezdl.gframedl.tools.clipboard;

import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.Arrays;
import java.util.EventObject;
import java.util.List;

import javax.swing.TransferHandler.TransferSupport;

import org.apache.log4j.Logger;

import de.unidue.inf.is.ezdl.dlcore.data.dldata.DLObject;
import de.unidue.inf.is.ezdl.dlfrontend.dispatcher.Dispatcher;
import de.unidue.inf.is.ezdl.gframedl.Icons;
import de.unidue.inf.is.ezdl.gframedl.IconsTuple;
import de.unidue.inf.is.ezdl.gframedl.events.AddToClipboardEvent;
import de.unidue.inf.is.ezdl.gframedl.tools.AbstractTool;
import de.unidue.inf.is.ezdl.gframedl.tools.ToolView;
import de.unidue.inf.is.ezdl.gframedl.transfer.DataFlavors;



public final class ClipboardTool extends AbstractTool {

    public static final String I18N_PREFIX = "ezdl.tools.clipboard.";

    private Logger logger = Logger.getLogger(ClipboardTool.class);


    public ClipboardTool() {
        init();
    }


    @Override
    public boolean canImportButtonDrop(TransferSupport support) {
        return true;
    }


    @Override
    public List<ToolView> createViews() {
        ClipboardToolView c = new ClipboardToolView(this);

        return Arrays.<ToolView> asList(c);
    }


    @Override
    protected IconsTuple getIcon() {
        return Icons.CLIPBOARD_TOOL.toIconsTuple();
    }


    @Override
    protected String getI18nPrefix() {
        return I18N_PREFIX;
    }


    @Override
    public boolean handleEzEvent(EventObject ev) {
        if (ev instanceof AddToClipboardEvent) {
            getView().handleAddObjects(Arrays.asList(((AddToClipboardEvent) ev).getObject()));
        }
        return false;
    }


    private ClipboardToolView getView() {
        return (ClipboardToolView) getDefaultView();
    }


    @SuppressWarnings("unchecked")
    @Override
    public boolean importDataFromButtonDrop(TransferSupport support) {
        try {
            if (support.isDataFlavorSupported(DataFlavors.DL_OBJECT)) {
                List<DLObject> object = (List<DLObject>) support.getTransferable().getTransferData(
                                DataFlavors.DL_OBJECT);
                getView().handleAddObjects(object);
                logger.info(object);
            }
            return true;
        }
        catch (UnsupportedFlavorException e) {
            logger.error(e.getMessage(), e);
        }
        catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return false;
    }


    private void init() {
        Dispatcher.registerInterest(this, AddToClipboardEvent.class);
    }

}
