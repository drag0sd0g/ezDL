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

import java.util.LinkedList;
import java.util.List;

import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNode;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeBase;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeBool;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeBool.NodeType;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeCompare;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeProximity;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.transformation.flatten.FlattenerTransformer;
import de.unidue.inf.is.ezdl.dlcore.query.DepthFirstQueryWalker;
import de.unidue.inf.is.ezdl.dlcore.query.QueryNodeVisitor;



/**
 * Transforms a query tree to disjunctive normal form using a TreeWalker.
 * 
 * @author mj
 */
public class TreeWalkerDNFTransformer implements QueryTreeTransformer {

    private class Walker extends DepthFirstQueryWalker<QueryNode> {

        public Walker(QueryNodeVisitor<QueryNode> visitor) {
            super(visitor);
        }

    }


    private class Visitor implements QueryNodeVisitor<QueryNode> {

        @Override
        public QueryNode visit(QueryNodeCompare compare) {
            return compare;
        }


        @Override
        public QueryNode visit(QueryNodeProximity proximity) {
            return proximity;
        }


        /**
         * Thanks to {@link FlattenerTransformer} the node's children are either
         * OR nodes or {@link QueryNodeBase} so this has to be transformed into
         * DNF.
         */
        @Override
        public QueryNode visitAnd(QueryNodeBool node, List<QueryNode> subresults) {
            QueryNodeBool or = null;
            or = findFirstOrChild(node);
            if (or != null) {
                List<QueryNode> newChildren = new LinkedList<QueryNode>();
                List<QueryNode> rest = findOtherChildren(node, or);

                for (QueryNode orChild : or.children()) {
                    List<QueryNode> newConjunction = new LinkedList<QueryNode>();
                    newConjunction.add(orChild);
                    newConjunction.addAll(rest);
                    final QueryNodeBool and = new QueryNodeBool(NodeType.AND);
                    and.setChildren(newConjunction);
                    newChildren.add(and);
                }
                QueryNodeBool newOr = new QueryNodeBool(NodeType.OR);
                newOr.setChildren(newChildren);
                return visitOr(newOr, null);
            }

            return node;
        }


        private List<QueryNode> findOtherChildren(QueryNodeBool node, QueryNodeBool or) {
            List<QueryNode> rest = new LinkedList<QueryNode>();
            for (QueryNode child : node.children()) {
                if (child != or) {
                    rest.add(child);
                }
            }
            return rest;
        }


        private QueryNodeBool findFirstOrChild(QueryNodeBool node) {
            for (QueryNode child : node.children()) {
                if (child instanceof QueryNodeBool) {
                    final QueryNodeBool bool = (QueryNodeBool) child;
                    if (bool.isOfType(NodeType.OR)) {
                        return bool;
                    }
                }
            }
            return null;
        }


        /**
         * Thanks to {@link FlattenerTransformer} the node's children are either
         * AND nodes or {@link QueryNodeBase} so this is already in DNF.
         */
        @Override
        public QueryNode visitOr(QueryNodeBool node, List<QueryNode> subresults) {
            List<QueryNode> newChildren = new LinkedList<QueryNode>();
            for (QueryNode child : node.children()) {
                if (child instanceof QueryNodeBool) {
                    QueryNodeBool bool = (QueryNodeBool) child;
                    if (((QueryNodeBool) child).isOfType(NodeType.AND)) {
                        newChildren.add(visitAnd(bool, null));
                    }
                }
                else {
                    newChildren.add(child);
                }
            }
            node.setChildren(newChildren);
            return node;
        }

    }


    @Override
    public QueryNode transform(QueryNode root) {
        final FlattenerTransformer flattener = new FlattenerTransformer();

        root = flattener.transform(root);

        final Visitor visitor = new Visitor();
        final Walker walker = new Walker(visitor);
        final QueryNode dnfRoot = walker.calculate(root);

        return flattener.transform(dnfRoot);
    }

}
