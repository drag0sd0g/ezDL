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
package de.unidue.inf.is.ezdl.dlcore.data.query.tree.transformation;

import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNode;



/**
 * Transforms a query tree into a different layout.
 * 
 * @author mj
 */
public interface QueryTreeTransformer {

    /**
     * Transforms the query tree with the given root node into a different
     * layout.
     * <p>
     * The output might be a totally different structure of {@link QueryNode}
     * objects or the same objects but interlinked differently.
     * 
     * @param root
     *            the root node of the tree to transform.
     * @return the root node of the transformed tree.
     */
    QueryNode transform(QueryNode root);
}
