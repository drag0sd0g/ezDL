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

package de.unidue.inf.is.ezdl.gframedl.tools.library;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.DefaultListModel;

import de.unidue.inf.is.ezdl.dlcore.data.dldata.Document;



/** Listmodel for the librarylist */
public class LibraryListModel extends DefaultListModel {

    private static final long serialVersionUID = 5024326076833907576L;


    public LibraryListModel() {
        super();
    }


    /** Checks if a document with the given OID is already in the List Model */
    public boolean containsOid(String oid) {
        Enumeration<?> e = this.elements();
        while (e.hasMoreElements()) {
            if (((Document) e.nextElement()).getOid().equals(oid)) {
                return true;
            }
        }
        return false;
    }


    /**
     * Returns the document with the given OID
     * 
     * @param oid
     *            document with the given OID
     * @return the Document with the given OID, null if not found
     */
    public Document getDocument(String oid) {
        Enumeration<?> e = this.elements();
        while (e.hasMoreElements()) {
            Document tmpd = (Document) e.nextElement();
            if (tmpd.getOid().equals(oid)) {
                return tmpd;
            }
        }
        return null;
    }


    public List<Document> getDocuments() {
        List<Document> documents = new ArrayList<Document>();
        Enumeration<?> e = this.elements();
        while (e.hasMoreElements()) {
            documents.add((Document) e.nextElement());
        }
        return documents;
    }
}
