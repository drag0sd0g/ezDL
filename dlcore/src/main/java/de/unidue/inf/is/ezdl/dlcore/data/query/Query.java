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
import java.util.List;

import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNode;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeBool;



public interface Query extends Serializable {

    /**
     * Return the root node of the query tree.
     */
    QueryNode getTree();


    /**
     * @return a list of strings that contain the terms of the query
     */
    List<String> getAttributeValues();


    /**
     * Transforms the query into DNF.
     * 
     * @return the query node tree in disjunctive normal form (i.e. a
     *         disjunction of conjunctions - e.g. "(A AND B) OR (C AND D)")
     */
    QueryNode asDNF();


    /**
     * Transforms the query info CNF.
     * 
     * @return the query node tree in conjunctive normal form (i.e. a
     *         conjunction of disjunctions - e.g. "(A OR B) AND (C OR D)")
     */
    QueryNode asCNF();


    List<QueryNodeBool> asConjunctionList();
}
