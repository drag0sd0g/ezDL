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

package de.unidue.inf.is.ezdl.dlcore.data.query;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import de.unidue.inf.is.ezdl.dlcore.data.dldata.Document;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.MergeableArrayList;



public class ResultDocumentList extends MergeableArrayList<ResultDocument> implements Serializable {

    private static final long serialVersionUID = 8191093064775417376L;


    /**
     * Constructor. Creates an empty list.
     */
    public ResultDocumentList() {
        super();
    }


    /**
     * Creates a list with initial data.
     * 
     * @param inputData
     *            the initial
     */
    public ResultDocumentList(ResultDocumentList inputData) {
        super(inputData);
    }


    /**
     * Returns the raw {@link Document} objects in this list as a list.
     * 
     * @return the list of {@link Document} objects
     */
    public List<Document> asListOfDocuments() {
        List<Document> result = new ArrayList<Document>();
        for (ResultDocument resultDocument : this) {
            result.add(resultDocument.getDocument());
        }
        return result;
    }

}
