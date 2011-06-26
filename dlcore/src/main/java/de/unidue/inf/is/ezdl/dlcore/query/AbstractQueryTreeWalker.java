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

import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNode;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeBool;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeCompare;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeProximity;



abstract class AbstractQueryTreeWalker implements QueryTreeWalker {

    protected void process(QueryNode node, QueryNodeBool parent, int parentChildrenIndex, int parentChildrenCount) {
        if (node instanceof QueryNodeBool) {
            process((QueryNodeBool) node, parent, parentChildrenIndex, parentChildrenCount);
        }
        else if (node instanceof QueryNodeCompare) {
            process((QueryNodeCompare) node, parent, parentChildrenIndex, parentChildrenCount);
        }
        else if (node instanceof QueryNodeProximity) {
            process((QueryNodeProximity) node, parent, parentChildrenIndex, parentChildrenCount);
        }
        else {
            throw new IllegalArgumentException("Unknown type " + node.getClass());
        }
    }


    protected abstract void process(QueryNodeBool node, QueryNodeBool parent, int parentChildrenIndex,
                    int parentChildrenCount);


    protected abstract void process(QueryNodeCompare node, QueryNodeBool parent, int parentChildrenIndex,
                    int parentChildrenCount);


    protected abstract void process(QueryNodeProximity node, QueryNodeBool parent, int parentChildrenIndex,
                    int parentChildrenCount);
}
