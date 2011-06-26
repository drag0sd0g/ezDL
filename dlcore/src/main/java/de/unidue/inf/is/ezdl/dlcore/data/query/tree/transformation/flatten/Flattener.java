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
package de.unidue.inf.is.ezdl.dlcore.data.query.tree.transformation.flatten;

import java.util.LinkedList;
import java.util.List;

import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNode;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeBool;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeBool.NodeType;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeCompare;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeProximity;
import de.unidue.inf.is.ezdl.dlcore.query.QueryNodeSwitch;
import de.unidue.inf.is.ezdl.dlcore.query.QueryNodeVisitor;



/**
 * Flattens a single query node.
 * <p>
 * Here, "to flatten" means pulling up nodes that might as well hang under a
 * common node. See {@link FlattenerTransformer} for details.
 * 
 * @author mjordan
 */
class Flattener implements QueryNodeVisitor<Object> {

    private QueryNode root;


    public Flattener(QueryNode root) {
        this.root = root;
    }


    public QueryNode getRoot() {
        return root;
    }


    @Override
    public Object visitAnd(QueryNodeBool node, List<Object> subresults) {
        return visitBool(node, subresults);
    }


    @Override
    public Object visitOr(QueryNodeBool node, List<Object> subresults) {
        return visitBool(node, subresults);
    }


    private Boolean visitBool(QueryNodeBool node, List<Object> subresults) {
        final List<QueryNode> children = new LinkedList<QueryNode>(node.children());

        switch (children.size()) {
            case 0: {
                removeNode(node);
                break;
            }
            case 1: {
                // Tree looks like this:
                // parent->node->lonelyChild
                final QueryNode lonelyChild = children.get(0);
                moveChildToParentLevel(node.getParent(), node, lonelyChild);
                break;
            }
            default: {
                simplifySubtrees(node, children);
                removeDuplicates(node);
            }
        }
        return null;
    }


    private void simplifySubtrees(QueryNodeBool node, final List<QueryNode> children) {
        final NodeType nodeType = node.getType();
        for (QueryNode child : children) {
            final boolean negationEqual = child.isNegated() == node.isNegated();

            if (child instanceof QueryNodeBool) {
                final QueryNodeBool boolChild = (QueryNodeBool) child;

                final boolean typeEqual = boolChild.isOfType(nodeType);

                // if (typeEqual && !negationEqual && (boolChild.childCount() ==
                // 1)) {
                // moveNegatedLoneChild(node, boolChild);
                // }
                // else
                if (typeEqual && negationEqual) {
                    moveChildsChildren(node, boolChild);
                }
            }
        }
    }


    // /**
    // * Moves the single child of boolChild to node.
    // * <p>
    // * The caller guarantees that boolChild has exactly one child.
    // *
    // * @param node
    // * the node to move the single child to
    // * @param boolChild
    // * the boolChild whose single child to move
    // */
    // private void moveNegatedLoneChild(QueryNodeBool node, QueryNodeBool
    // boolChild) {
    // final QueryNode childsChild = boolChild.getChildAt(0);
    // childsChild.setNegated(!childsChild.isNegated());
    // node.removeChild(boolChild);
    // node.addChild(childsChild);
    // }

    private void removeDuplicates(QueryNodeBool node) {
        final List<QueryNode> toDelete = new LinkedList<QueryNode>();
        // Collect duplicate nodes to list
        for (QueryNode n1 : node.children()) {
            for (QueryNode n2 : node.children()) {
                if ((n1 != n2) && nodesAreEqual(n1, n2) && !inList(toDelete, n2)) {
                    toDelete.add(n2);
                }
            }
        }
        // Remove dupes from node
        for (QueryNode child : toDelete) {
            node.removeChild(child);
        }
    }


    private boolean inList(List<QueryNode> toDelete, QueryNode node) {
        for (QueryNode delNode : toDelete) {
            if (nodesAreEqual(delNode, node)) {
                return true;
            }
        }
        return false;
    }


    boolean nodesAreEqual(final QueryNode n1, final QueryNode n2) {
        boolean equal = new QueryNodeSwitch<Boolean>(new QueryNodeVisitor<Boolean>() {

            @Override
            public Boolean visitAnd(QueryNodeBool node, List<Boolean> subresults) {
                return false;
            }


            @Override
            public Boolean visitOr(QueryNodeBool node, List<Boolean> subresults) {
                return false;
            }


            @Override
            public Boolean visit(QueryNodeCompare compare) {
                if (n2 instanceof QueryNodeCompare) {
                    QueryNodeCompare cn2 = (QueryNodeCompare) n2;
                    return //
                    compare.isNegated() == cn2.isNegated() && //
                                    compare.getFieldCode() == cn2.getFieldCode() && //
                                    compare.getTokens().equals(cn2.getTokens());
                }
                return false;
            }


            @Override
            public Boolean visit(QueryNodeProximity proximity) {
                if (n2 instanceof QueryNodeProximity) {
                    QueryNodeProximity pn2 = (QueryNodeProximity) n2;
                    final String[] pt = proximity.getTerms();
                    final String[] p2t = pn2.getTerms();
                    return //
                    proximity.isNegated() == pn2.isNegated() && //
                                    proximity.getFieldCode() == pn2.getFieldCode() && //
                                    proximity.getMaxDistance() == pn2.getMaxDistance() && //
                                    pt[0].equals(p2t[0]) && //
                                    pt[1].equals(p2t[1]);
                }
                return false;
            }
        }).calc(n1);
        return equal;
    }


    private void removeNode(QueryNodeBool node) {
        if (root == node) {
            root = null;
        }
        else {
            node.remove();
        }
    }


    private void moveChildToParentLevel(QueryNodeBool parent, QueryNodeBool node, final QueryNode lonelyChild) {
        lonelyChild.remove();
        if (node.childCount() == 0) {
            node.remove();
        }
        if (node.isNegated()) {
            lonelyChild.setNegated(!lonelyChild.isNegated());
        }
        if (parent != null) {
            parent.addChild(lonelyChild);
        }
        else {
            root = lonelyChild;
        }
    }


    private void moveChildsChildren(QueryNodeBool node, QueryNodeBool boolChild) {
        final List<QueryNode> children = new LinkedList<QueryNode>(boolChild.children());
        for (QueryNode child : children) {
            moveChildToParentLevel(node, boolChild, child);
        }
        if (boolChild.childCount() == 0) {
            boolChild.remove();
        }
    }


    @Override
    public Object visit(QueryNodeCompare compare) {
        return null;
    }


    @Override
    public Object visit(QueryNodeProximity proximity) {
        return null;
    }

}