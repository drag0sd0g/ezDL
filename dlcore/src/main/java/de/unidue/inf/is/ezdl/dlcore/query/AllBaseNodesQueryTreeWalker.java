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

import java.util.ArrayList;
import java.util.List;

import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeBase;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeBool;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeCompare;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeProximity;



public class AllBaseNodesQueryTreeWalker extends AbstractDefaultOrderQueryTreeWalker {

    private List<QueryNodeBase> queryNodes = new ArrayList<QueryNodeBase>();


    @Override
    protected void process(QueryNodeProximity node, QueryNodeBool parent, int parentChildrenIndex,
                    int parentChildrenCount) {
        queryNodes.add(node);
    }


    @Override
    protected void process(QueryNodeCompare node, QueryNodeBool parent, int parentChildrenIndex, int parentChildrenCount) {
        queryNodes.add(node);
    }


    @Override
    protected void process(QueryNodeBool node, QueryNodeBool parent, int parentChildrenIndex, int parentChildrenCount) {
    }


    public List<QueryNodeBase> getQueryNodes() {
        return queryNodes;
    }
}
