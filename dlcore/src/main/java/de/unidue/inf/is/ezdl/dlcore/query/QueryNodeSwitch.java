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

import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNode;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeBool;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeCompare;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeProximity;



public class QueryNodeSwitch<ResultType> {

    private QueryNodeVisitor<ResultType> visitor;


    public QueryNodeSwitch(QueryNodeVisitor<ResultType> visitor) {
        this.visitor = visitor;
    }


    public ResultType calc(QueryNode node) {
        if (node instanceof QueryNodeBool) {
            return calc((QueryNodeBool) node, new ArrayList<ResultType>());
        }
        else if (node instanceof QueryNodeCompare) {
            return visitor.visit((QueryNodeCompare) node);
        }
        else if (node instanceof QueryNodeProximity) {
            return visitor.visit((QueryNodeProximity) node);
        }
        else {
            throw new IllegalArgumentException("Unknown node type " + node.getClass());
        }
    }


    public ResultType calc(QueryNodeBool node, List<ResultType> subresults) {
        ResultType result = null;
        switch (node.getType()) {
            case AND: {
                result = visitor.visitAnd(node, subresults);
                break;
            }
            case OR: {
                result = visitor.visitOr(node, subresults);
                break;
            }
            default: {
                throw new IllegalArgumentException("Unknown node type " + node.getType());
            }
        }
        return result;

    }

}
