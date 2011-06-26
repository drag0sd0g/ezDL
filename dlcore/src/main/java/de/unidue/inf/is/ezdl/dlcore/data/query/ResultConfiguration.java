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
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;
import de.unidue.inf.is.ezdl.dlcore.data.fields.Order;
import de.unidue.inf.is.ezdl.dlcore.data.fields.Sorting;



/**
 * Configuration of document query results.
 */

public class ResultConfiguration implements Serializable {

    private static final long serialVersionUID = -797056795533178292L;

    /**
     * If the end number of the interval to return is {@value #INF_DOCS}, all
     * available documents are returned.
     */
    public static final int INF_DOCS = -1;
    /**
     * The list of sortings to obey, in the order of increasing importance.
     */
    private List<Sorting> sortings;
    /**
     * The index of the first document to return.
     */
    private int startDocNumber;
    /**
     * The index of the last document to return.
     */
    private int endDocNumber;
    /**
     * The list of fields that should be in the documents
     */
    private List<Field> fields;


    /**
     * Creates a new ResultConfiguration with sorting by descending order or
     * RSV, starting with the first document, returning as many documents as
     * possible without any fields.
     */
    public ResultConfiguration() {
        this(0, INF_DOCS, new LinkedList<Field>(), new Sorting(Field.RSV, Order.DESCENDING));
    }


    /**
     * Creates a new ResultConfiguration with sorting by descending order or
     * RSV, starting with the first document, returning as many documents as
     * possible with the given fields.
     * 
     * @param fields
     *            the fields to return
     */
    public ResultConfiguration(List<Field> fields) {
        this(0, INF_DOCS, fields, new Sorting(Field.RSV, Order.DESCENDING));
    }


    /**
     * Creates a new ResultConfiguration with the given sorting, returning as
     * many documents as possible without any fields.
     * 
     * @param sorting
     *            the sorting. See the remarks about sorting at
     *            {@link #ResultConfiguration(int, int, List, Sorting...)}
     * @see #ResultConfiguration(int, int, List, Sorting...)
     */
    public ResultConfiguration(Sorting sorting) {
        this(0, INF_DOCS, new LinkedList<Field>(), sorting);
    }


    /**
     * Creates a ResultConfiguration object.
     * 
     * @param startDocNumber
     *            the index of the first document to return, starting with 0
     * @param endDocNumber
     *            the index of the last document to return, starting with 0. The
     *            special value {@value #INF_DOCS} can be used to signal
     *            interest in all documents that are available.
     * @param fields
     *            the fields that should be returned. All other fields are
     *            nulled.
     * @param sortings
     *            the list of sortings, starting from the least important one.
     *            E.g. if the result list should be sorted by year and documents
     *            with equal years should be sorted by RSV, the list of sortings
     *            should be Field.RSV and then Field.YEAR
     */
    public ResultConfiguration(int startDocNumber, int endDocNumber, List<Field> fields, Sorting... sortings) {
        super();
        this.sortings = Arrays.asList(sortings);
        this.startDocNumber = startDocNumber;
        this.endDocNumber = endDocNumber;
        this.fields = fields;
    }


    /**
     * @return the sorting
     */
    public List<Sorting> getSortings() {
        return sortings;
    }


    /**
     * @return the startDocNumber
     */
    public int getStartDocNumber() {
        return startDocNumber;
    }


    /**
     * @return the endDocNumber
     */
    public int getEndDocNumber() {
        return endDocNumber;
    }


    /**
     * @return the fields
     */
    public List<Field> getFields() {
        return fields;
    }

}
