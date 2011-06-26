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

import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;
import de.unidue.inf.is.ezdl.dlcore.data.query.Query;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNode;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeBase;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeBool;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeBool.NodeType;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeCompare;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeCompare.Predicate;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeProximity;



/**
 * Performs a bottom-up iteration of a tree of {@link QueryNode} objects.
 * Results of subtrees are then assembled to give the result of the whole tree.
 * 
 * @author mjordan
 */
public abstract class AbstractBottomUpQueryConverter implements QueryConverter {

    /**
     * Represents a node.
     * <p>
     * Holds the string representation and also the node type to allow
     * determining whether the node's representation has to be enclosed by
     * parentheses.
     * 
     * @author mj
     */
    protected class NodeRep {

        /**
         * The representation of a node. E.g. "(term1 OR term2) AND term3".
         */
        public String representation;
        /**
         * The type of the node.
         */
        public NodeType nodeType;
    }


    public AbstractBottomUpQueryConverter() {
        super();
    }


    private class Walker extends DepthFirstQueryWalker<NodeRep> {

        public Walker(QueryNodeVisitor<NodeRep> visitor) {
            super(visitor);
        }


        @Override
        protected boolean includeSubresult(QueryNode node, QueryNode child, NodeRep subresult) {
            return (subresult != null) && (subresult.representation != null);
        }
    }


    private class Visitor implements QueryNodeVisitor<NodeRep> {

        @Override
        public NodeRep visit(QueryNodeCompare compare) {
            final NodeRep nr = new NodeRep();
            nr.nodeType = null;
            nr.representation = convertCompare(compare);
            return nr;
        }


        @Override
        public NodeRep visit(QueryNodeProximity proximity) {
            final NodeRep nr = new NodeRep();
            nr.nodeType = null;
            nr.representation = convertProximity(proximity);
            return nr;
        }


        @Override
        public NodeRep visitAnd(QueryNodeBool and, List<NodeRep> subresults) {
            return visitBool(and, subresults);
        }


        @Override
        public NodeRep visitOr(QueryNodeBool or, List<NodeRep> subresults) {
            return visitBool(or, subresults);
        }


        private NodeRep visitBool(QueryNodeBool queryNodeBool, List<NodeRep> subresults) {
            final StringBuilder out = new StringBuilder();

            boolean firstChildSeen = false;
            for (NodeRep nr : subresults) {
                final boolean useParentheses = useParenthesesForChild(queryNodeBool, nr.nodeType);
                if (!firstChildSeen) {
                    firstChildSeen = true;
                }
                else {
                    out.append(operator(queryNodeBool));
                }

                if (useParentheses) {
                    out.append(parenthesisOpen());
                }
                out.append(nr.representation);
                if (useParentheses) {
                    out.append(parenthesisClosed());
                }
            }

            NodeRep nr = new NodeRep();
            nr.nodeType = queryNodeBool.getType();
            if (!firstChildSeen) {
                nr.representation = null;
            }
            else {
                nr.representation = out.toString();
            }
            return nr;
        }

    }


    @Override
    public String convert(Query query) {
        final QueryNode root = query.getTree();
        if (root != null) {
            if (useDeMorgan()) {
                deMorgan(root);
            }
            Visitor visitor = new Visitor();
            final Walker walker = new Walker(visitor);
            NodeRep out = walker.calculate(root);
            if (out != null) {
                return out.representation;
            }
        }
        return null;
    }


    private void deMorgan(QueryNode queryNode) {
        AllBooleanNodesQueryTreeWalker johnny = new AllBooleanNodesQueryTreeWalker();
        johnny.walk(queryNode);
        for (QueryNodeBool queryNodeBool : johnny.getQueryNodes()) {
            if (queryNodeBool.isNegated()) {
                flattenNegation(queryNodeBool);
            }
        }
    }


    private void flattenNegation(QueryNode queryNode) {
        if (queryNode instanceof QueryNodeBase) {
            queryNode.setNegated(!queryNode.isNegated());
        }
        else if (queryNode instanceof QueryNodeBool) {
            queryNode.setNegated(false);
            QueryNodeBool queryNodeBool = (QueryNodeBool) queryNode;
            NodeType nodeType = queryNodeBool.getType();
            switch (nodeType) {
                case OR: {
                    queryNodeBool.setType(NodeType.AND);
                    break;
                }
                case AND: {
                    queryNodeBool.setType(NodeType.OR);
                    break;
                }
                default: {
                    throw new RuntimeException();
                }
            }

            for (QueryNode child : queryNodeBool.children()) {
                if (child.isNegated()) {
                    child.setNegated(false);
                }
                else {
                    flattenNegation(child);
                }
            }
        }
    }


    /**
     * Determines if a child query should be surrounded by parentheses.
     * <p>
     * The default implementation returns true if an OR node is below an AND
     * node - e.g. <code>a AND (b OR c)</code>.
     * 
     * @param node
     *            the node (in the example the AND node) for whom to check the
     *            child (in the example the string <code>b OR c</code>.
     * @param childType
     *            the type of the child. In the example it would be
     *            {@link NodeType#OR}.
     * @return true if the substring should be surrounded by parentheses
     */
    protected boolean useParenthesesForChild(QueryNodeBool node, NodeType childType) {
        final boolean childIsOr = (childType == NodeType.OR);
        final boolean orUnderAnd = node.isOfType(NodeType.AND) && childIsOr;
        return orUnderAnd;
    }


    /**
     * Returns the string representation for the node's type.
     * <p>
     * E.g. AND or OR.
     * 
     * @param node
     *            the node
     * @return the string representation for the node's type.
     */
    private String operator(QueryNodeBool node) {
        final String out;
        final NodeType type = node.getType();
        switch (type) {
            case AND: {
                out = andOperator();
                break;
            }
            case OR: {
                out = orOperator();
                break;
            }
            default: {
                throw new IllegalArgumentException("Unknown node type " + type);
            }
        }
        return out;
    }


    /**
     * Converts a {@link QueryNodeCompare} object to a String.
     * <p>
     * The output is not surrounded by parentheses. If parentheses are needed to
     * included this in a larger query string, the parent level has to surround
     * the output with suitable parentheses.
     * <p>
     * A Lucene converter could return <code>field:Term</code>.
     * 
     * @param node
     *            the node to convert
     * @return the string representation of the node
     */
    protected String convertCompare(QueryNodeCompare node) {
        final String fieldValue = fieldValue(node);
        switch (node.getPredicate()) {
            case EQ: {
                return fieldQueryEQ(node.isNegated(), node.getFieldCode(), fieldValue);
            }
            case GT: {
                return fieldQueryGT(node.isNegated(), node.getFieldCode(), fieldValue);
            }
            case GTE: {
                return fieldQueryGTE(node.isNegated(), node.getFieldCode(), fieldValue);
            }
            case LT: {
                return fieldQueryLT(node.isNegated(), node.getFieldCode(), fieldValue);
            }
            case LTE: {
                return fieldQueryLTE(node.isNegated(), node.getFieldCode(), fieldValue);
            }
            default: {
                throw new IllegalArgumentException("unexpected query syntax");
            }
        }
    }


    private String fieldValue(QueryNodeCompare queryNodeCompare) {
        String fieldValue;
        if (queryNodeCompare.hasWildcards()) {
            final StringBuilder out = new StringBuilder();
            final List<String> tokens = queryNodeCompare.getTokens();
            for (String token : tokens) {
                if (QueryNodeCompare.isTokenWildcardMulti(token)) {
                    out.append(wildcardMulti());
                }
                else if (QueryNodeCompare.isTokenWildcardSingle(token)) {
                    out.append(wildcardSingle());
                }
                else {
                    out.append(escapeToken(token));
                }
            }
            fieldValue = out.toString();
        }
        else {
            fieldValue = queryNodeCompare.getTokensAsString();
            fieldValue = escapeToken(fieldValue);
        }
        return fieldValue;
    }


    /**
     * Returns true if NOTs should be removed in {@link QueryNodeBool}s with De
     * Morgan.
     * 
     * @return if De Morgan should be used
     */
    protected boolean useDeMorgan() {
        return true;
    }


    /**
     * Converts a {@link QueryNodeProximity} object to a String.
     * <p>
     * The output is not surrounded by parentheses. If parentheses are needed to
     * included this in a larger query string, the parent level has to surround
     * the output with suitable parentheses.
     * <p>
     * A Lucene converter could return <code>field:"term1 term2"~2</code>.
     * 
     * @param node
     *            the node to convert
     * @return the string representation of the node
     */
    protected abstract String convertProximity(QueryNodeProximity node);


    /**
     * Returns a string representation for an expression that compares the
     * contents of a given field with a given value using the predicate
     * {@link Predicate#EQ}.
     * 
     * @param negated
     *            true if the expression is negated (e.g. "NOT term1").
     * @param field
     *            the field the value is compared in
     * @param value
     *            the value to compare
     * @return the string representation
     */
    protected abstract String fieldQueryEQ(boolean negated, Field field, String value);


    /**
     * Returns a string representation for an expression that compares the
     * contents of a given field with a given value using the predicate
     * {@link Predicate#GT}.
     * 
     * @param negated
     *            true if the expression is negated (e.g. "NOT term1").
     * @param field
     *            the field the value is compared in
     * @param value
     *            the value to compare
     * @return the string representation
     */
    protected abstract String fieldQueryGT(boolean negated, Field field, String value);


    /**
     * Returns a string representation for an expression that compares the
     * contents of a given field with a given value using the predicate
     * {@link Predicate#GTE}.
     * 
     * @param negated
     *            true if the expression is negated (e.g. "NOT term1").
     * @param field
     *            the field the value is compared in
     * @param value
     *            the value to compare
     * @return the string representation
     */
    protected abstract String fieldQueryGTE(boolean negated, Field field, String value);


    /**
     * Returns a string representation for an expression that compares the
     * contents of a given field with a given value using the predicate
     * {@link Predicate#LT}.
     * 
     * @param negated
     *            true if the expression is negated (e.g. "NOT term1").
     * @param field
     *            the field the value is compared in
     * @param value
     *            the value to compare
     * @return the string representation
     */
    protected abstract String fieldQueryLT(boolean negated, Field field, String value);


    /**
     * Returns a string representation for an expression that compares the
     * contents of a given field with a given value using the predicate
     * {@link Predicate#LTE}.
     * 
     * @param negated
     *            true if the expression is negated (e.g. "NOT term1").
     * @param field
     *            the field the value is compared in
     * @param value
     *            the value to compare
     * @return the string representation
     */
    protected abstract String fieldQueryLTE(boolean negated, Field field, String value);


    /**
     * Returns the wildcard for multiple characters. In bash terms "*".
     * 
     * @return the multi-char wildcard
     */
    protected abstract String wildcardMulti();


    /**
     * Returns the wildcard for one single character. In bash terms "?".
     * 
     * @return the single-char wildcard
     */
    protected abstract String wildcardSingle();


    /**
     * Escapes a token.
     * 
     * @param token
     *            the token to escape
     * @return the escaped token
     */
    protected abstract String escapeToken(String token);


    /**
     * The AND operator.
     * 
     * @return the AND operator
     */
    protected abstract String andOperator();


    /**
     * The OR operator.
     * 
     * @return The OR operator
     */
    protected abstract String orOperator();


    /**
     * The character to use for an open parenthesis.
     * 
     * @return the open parenthesis character
     */
    protected abstract String parenthesisOpen();


    /**
     * The character to use for an closed parenthesis.
     * 
     * @return the closed parenthesis character
     */
    protected abstract String parenthesisClosed();

}
