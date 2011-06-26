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

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.util.Arrays;
import java.util.List;

import de.unidue.inf.is.ezdl.dlcore.data.dldata.DLObject;



/**
 * Drag and drop {@link Transferable} for {@link DLObject}s.
 * 
 * @author tbeckers
 */
public class DLObjectTransferable implements Transferable {

    private List<? extends DLObject> dlObjects;
    private DataFlavor dataFlavor;


    public DLObjectTransferable(List<? extends DLObject> dlObjects, DataFlavor dataFlavor) {
        this.dlObjects = dlObjects;
        this.dataFlavor = dataFlavor;
    }


    public DLObjectTransferable(DLObject dlObject) {
        this(Arrays.asList(dlObject), DataFlavors.getDataFlavorForClass(dlObject.getClass()));
    }


    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
        if (!flavor.equals(dataFlavor) && !flavor.equals(DataFlavors.DL_OBJECT)) {
            throw new UnsupportedFlavorException(flavor);
        }
        return dlObjects;
    }


    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[] {
                        dataFlavor, DataFlavors.DL_OBJECT
        };
    }


    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return (flavor.equals(dataFlavor) || flavor.equals(DataFlavors.DL_OBJECT));
    }

}
