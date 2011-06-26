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

package de.unidue.inf.is.ezdl.dlcore.data.fields;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;

import de.unidue.inf.is.ezdl.dlcore.data.dldata.Document;
import de.unidue.inf.is.ezdl.dlcore.data.query.ResultDocument;



public final class Sorting implements Serializable {

    private static final long serialVersionUID = 5354365900207711265L;

    private Field field;
    private Order order;


    /**
     * Creates a descending sorting on RSV.
     */
    public Sorting() {
        this(Field.RSV, Order.DESCENDING);
    }


    public Sorting(Field field, Order order) {
        super();
        if (!field.isSortable()) {
            throw new IllegalArgumentException("field must be sortable");
        }
        this.field = field;
        this.order = order;
    }


    public Field getField() {
        return field;
    }


    public Order getOrder() {
        return order;
    }


    public Comparator<Document> getComparator() {
        return order == Order.DESCENDING ? Collections.reverseOrder(field.getComparator()) : field.getComparator();
    }


    public Comparator<ResultDocument> getResultComparator() {
        return order == Order.DESCENDING ? Collections.reverseOrder(field.getResultComparator()) : field
                        .getResultComparator();
    }
}
