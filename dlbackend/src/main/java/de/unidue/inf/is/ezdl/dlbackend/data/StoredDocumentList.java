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

package de.unidue.inf.is.ezdl.dlbackend.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import de.unidue.inf.is.ezdl.dlcore.data.dldata.Document;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.MergeableArrayList;
import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;



/**
 * Keeps many {@link StoredDocument} objects and helps with accessing and
 * merging.
 */
public class StoredDocumentList extends MergeableArrayList<StoredDocument> implements Serializable {

    private static final long serialVersionUID = 115714403866904450L;


    /**
     * Constructor. Creates an empty list.
     */
    public StoredDocumentList() {
        super();
    }


    /**
     * Creates a list with initial data.
     * 
     * @param inputData
     *            the initial
     */
    public StoredDocumentList(StoredDocumentList inputData) {
        super(inputData);
    }


    /**
     * Returns the raw {@link Document} objects in this list as a list.
     * 
     * @return the list of {@link Document} objects
     */
    public List<Document> asListOfDocuments() {
        List<Document> result = new ArrayList<Document>();
        for (StoredDocument resultDocument : this) {
            result.add(resultDocument.getDocument());
        }
        return result;
    }


    public void filter(StoredDocumentFilter filter) {
        List<StoredDocument> defective = new LinkedList<StoredDocument>();
        for (StoredDocument stored : this) {
            boolean valid = filter.isValid(stored);
            if (!valid) {
                defective.add(stored);
            }
        }
        for (StoredDocument d : defective) {
            remove(d);
        }
    }


    public interface StoredDocumentFilter {

        boolean isValid(StoredDocument stored);
    }


    /**
     * Finds a {@link StoredDocument} whose {@link Document} has a given field
     * with the given value.
     * 
     * @param field
     *            the field to search on
     * @param value
     *            the value to search for
     * @return the {@link StoredDocument} that contains a {@link Document} with
     *         the given parameters or null if no such document can be found
     */
    public StoredDocument findDocument(Field field, Object value) {
        for (StoredDocument stored : this) {
            Document inList = stored.getDocument();
            final Object inListDOI = inList.getFieldValue(field);
            if (inListDOI.equals(value)) {
                return stored;
            }
        }
        return null;
    }

}
