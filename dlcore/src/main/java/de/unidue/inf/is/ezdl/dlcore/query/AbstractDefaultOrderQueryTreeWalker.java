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

import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNode;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeBool;



public abstract class AbstractDefaultOrderQueryTreeWalker extends AbstractQueryTreeWalker {

    @Override
    public final void walk(QueryNode queryNode) {
        walkRec(queryNode, null, 0, 1);
    }


    private void walkRec(QueryNode queryNode, QueryNode parent, int parentChildrenIndex, int parentChildrenCount) {
        process(queryNode, (QueryNodeBool) parent, parentChildrenIndex, parentChildrenCount);
        if (queryNode instanceof QueryNodeBool) {
            final QueryNodeBool boolNode = (QueryNodeBool) queryNode;
            final List<QueryNode> children = boolNode.children();
            int j = 0;
            for (QueryNode child : children) {
                walkRec(child, queryNode, j, children.size());
                j++;
            }
        }
    }
}
