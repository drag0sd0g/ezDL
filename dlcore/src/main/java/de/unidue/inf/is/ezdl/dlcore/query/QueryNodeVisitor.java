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
package de.unidue.inf.is.ezdl.dlcore.query;

import java.util.List;

import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeBool;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeCompare;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeProximity;



/**
 * Interface that describes a class that can handle query node objects.
 * 
 * @author mjordan
 * @param <ResultType>
 *            the result type of operations on each node.
 */
public interface QueryNodeVisitor<ResultType> {

    /**
     * Visits an AND node.
     * <p>
     * Performs operations on an AND node and the results of its children and
     * returns its results.
     * 
     * @param node
     *            the AND node to work on
     * @param subresults
     *            the results that have been returned by operations on the
     *            children of the node
     * @return the result
     */
    ResultType visitAnd(QueryNodeBool node, List<ResultType> subresults);


    /**
     * Visits an OR node.
     * <p>
     * Performs operations on an OR node and the results of its children and
     * returns its results.
     * 
     * @param node
     *            the AND node to work on
     * @param subresults
     *            the results that have been returned by operations on the
     *            children of the node
     * @return the result
     */
    ResultType visitOr(QueryNodeBool node, List<ResultType> subresults);


    /**
     * Visits a compare node.
     * 
     * @param compare
     *            the node to visit
     * @return the result of the operation
     */
    ResultType visit(QueryNodeCompare compare);


    /**
     * Visits a proximity node.
     * 
     * @param proximity
     *            the node to visit
     * @return the result of the operation
     */
    ResultType visit(QueryNodeProximity proximity);

}
