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
import java.util.HashMap;
import java.util.Map;

import javax.swing.TransferHandler.TransferSupport;

import de.unidue.inf.is.ezdl.dlcore.data.dldata.DLObject;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.Document;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.Person;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.PersonList;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.Term;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.TermList;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.Year;



/**
 * Class for {@link DataFlavor} constants and related utility methods.
 * 
 * @author tbeckers
 */
public final class DataFlavors {

    private DataFlavors() {
    }


    private static Map<Class<?>, DataFlavor> map = new HashMap<Class<?>, DataFlavor>();

    public static final DataFlavor DL_OBJECT = newDataFlavor(DLObject.class);

    public static final DataFlavor AUTHOR = newDataFlavor(Person.class);
    public static final DataFlavor AUTHOR_LIST = newDataFlavor(PersonList.class);
    public static final DataFlavor DOCUMENT = newDataFlavor(Document.class);
    public static final DataFlavor TERM = newDataFlavor(Term.class);
    public static final DataFlavor TERM_LIST = newDataFlavor(TermList.class);
    public static final DataFlavor YEAR = newDataFlavor(Year.class);


    private static DataFlavor newDataFlavor(Class<?> clazz) {
        DataFlavor dataFlavor = new DataFlavor(clazz, clazz.getSimpleName());
        map.put(clazz, dataFlavor);
        return dataFlavor;
    }


    /**
     * Returns the {@link DataFlavor} for a given {@link Class}
     * 
     * @param clazz
     *            the class
     * @return the data flavor
     */
    public static DataFlavor getDataFlavorForClass(Class<?> clazz) {
        DataFlavor dataFlavor = map.get(clazz);
        if (clazz != null) {
            return dataFlavor;
        }
        else {
            throw new IllegalArgumentException();
        }
    }


    /**
     * Checks if a {@link DataFlavor} is available for a given {@link Class}.
     * 
     * @param clazz
     *            the class
     * @return if a data flavor is available
     */
    public static boolean isDataFlavorAvailable(Class<?> clazz) {
        return map.get(clazz) != null;
    }


    /**
     * Checks if a {@link TransferSupport} can import {@link DataFlavor}s.
     * 
     * @param transferSupport
     *            the transfer support
     * @param dataFlavors
     *            Array of data flavors
     * @return if the transfer support can import data flavors
     */
    public static boolean canImport(TransferSupport transferSupport, DataFlavor... dataFlavors) {
        for (DataFlavor dataFlavor : dataFlavors) {
            if (transferSupport.isDataFlavorSupported(dataFlavor)) {
                return true;
            }
        }
        return false;
    }

}
