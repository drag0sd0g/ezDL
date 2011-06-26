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

package de.unidue.inf.is.ezdl.dlservices.library.store;

import java.util.Properties;



public class LibraryStoreFactory {

    public static final String DBStore = "DBStore";


    /**
     * Returns a new Library store
     * 
     * @param store
     *            The desired store. for exampe: LibraryStoreFactory.XMLStore or
     *            LibraryStoreFactory.DBStore
     * @param props
     *            The properties of the desired store
     * @return
     */
    public static LibraryStore getLibraryStore(String store, Properties props) {

        if (store.equals(DBStore)) {
            return new DBLibraryStore(props);
        }
        else {
            return null;
        }
    }

}
