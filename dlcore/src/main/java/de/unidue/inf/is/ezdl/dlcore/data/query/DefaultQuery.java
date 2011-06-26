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
import java.util.ArrayList;
import java.util.List;

import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNode;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeBool;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeBool.NodeType;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.transformation.DefaultCNFTransformer;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.transformation.QueryTreeTransformer;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.transformation.TreeWalkerDNFTransformer;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.transformation.flatten.FlattenerTransformer;
import de.unidue.inf.is.ezdl.dlcore.query.TermsOnlyQueryTreeWalker;



/**
 * Default query implementation.
 */
public class DefaultQuery implements Query, Serializable {

    private static final long serialVersionUID = 5694831907562854922L;

    /**
     * The query node tree. This is where "the query" is stored.
     */
    protected QueryNode tree;


    /**
     * Creates a new query with an empty query tree.
     */
    public DefaultQuery() {
        super();
    }


    /**
     * Creates a new query with the tree whose root is given.
     * 
     * @param root
     *            the root of the query tree to use
     */
    public DefaultQuery(QueryNode root) {
        this();
        this.tree = root;
    }


    @Override
    public QueryNode getTree() {
        return tree;
    }


    /**
     * Returns a String representation of the query.
     * 
     * @return a string representation of the receiver
     */
    @Override
    public String toString() {
        StringBuffer out = new StringBuffer();
        String queryTreeStr = "(empty)";
        QueryNode tree = getTree();
        if (tree != null) {
            queryTreeStr = tree.toString();
        }
        out.append(queryTreeStr);
        return out.toString();
    }


    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Query)) {
            return false;
        }
        return toString().equalsIgnoreCase(obj.toString());
    }


    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }


    @Override
    public List<String> getAttributeValues() {
        TermsOnlyQueryTreeWalker walker = new TermsOnlyQueryTreeWalker();
        walker.walk(getTree());
        return walker.queryTerms();
    }


    @Override
    public QueryNode asCNF() {
        final QueryTreeTransformer cnfTransformer = new DefaultCNFTransformer();
        tree = cnfTransformer.transform(tree);
        return tree;
    }


    @Override
    public QueryNode asDNF() {
        final QueryTreeTransformer dnfTransformer = new TreeWalkerDNFTransformer();
        tree = dnfTransformer.transform(tree);
        return tree;
    }


    @Override
    public List<QueryNodeBool> asConjunctionList() {
        asDNF();
        List<QueryNodeBool> cl = new ArrayList<QueryNodeBool>();
        if (tree instanceof QueryNodeBool) {
            QueryNodeBool bool = (QueryNodeBool) tree;
            switch (bool.getType()) {
                case OR: {
                    for (QueryNode child : bool.children()) {
                        cl.add(conjunctionize(child));
                    }
                    break;
                }
                case AND: {
                    cl.add(bool);
                    break;
                }
                default: {
                    throw new IllegalArgumentException("Unknown node type " + bool.getType());
                }
            }
        }
        else {
            cl.add(conjunctionize(tree));
        }
        return cl;
    }


    private QueryNodeBool conjunctionize(QueryNode node) {
        if (node instanceof QueryNodeBool) {
            return (QueryNodeBool) node;
        }
        else {
            QueryNodeBool c = new QueryNodeBool(NodeType.AND);
            c.addChild(node);
            return c;
        }
    }


    public static QueryNode and(QueryNode a, QueryNode b) {
        final QueryNodeBool and = new QueryNodeBool(NodeType.AND);
        if (a != null) {
            and.addChild(a);
        }
        if (b != null) {
            and.addChild(b);
        }
        final FlattenerTransformer f = new FlattenerTransformer();
        return f.transform(and);
    }
}
