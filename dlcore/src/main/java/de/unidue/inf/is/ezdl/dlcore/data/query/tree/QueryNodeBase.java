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

package de.unidue.inf.is.ezdl.dlcore.data.query.tree;

/**
 * A base class for leaf nodes of the query tree.
 * <p>
 * The class can be used to determine if a node is a leaf node type or nor.
 */
public abstract class QueryNodeBase extends QueryNode {

    private static final long serialVersionUID = -5661740608951972458L;


    /**
     * Clones the node.
     * 
     * @return a freshly created clone of the node.
     */
    public abstract QueryNodeBase plainClone();

}
