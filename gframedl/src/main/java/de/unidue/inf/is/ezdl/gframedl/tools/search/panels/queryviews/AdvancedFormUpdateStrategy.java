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

package de.unidue.inf.is.ezdl.gframedl.tools.search.panels.queryviews;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JTextField;

import org.antlr.runtime.RecognitionException;

import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;
import de.unidue.inf.is.ezdl.dlcore.data.query.DefaultQuery;
import de.unidue.inf.is.ezdl.dlcore.data.query.Query;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNode;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeBool;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeBool.NodeType;
import de.unidue.inf.is.ezdl.dlfrontend.query.NoSuchFieldCodeException;
import de.unidue.inf.is.ezdl.dlfrontend.query.QueryFactory;



/**
 * Strategy is such a strong word. This class knows how to make entries for
 * search form text fields if given information on these text fields and a query
 * factory to work on the query.
 * <p>
 * This is used to update the AdvancedFormQueryView text fields and factored out
 * of there to make testing easier.
 * <p>
 * This class does not interfere in any way with a text field or any other GUI
 * element.
 * <p>
 * There is no intent to create interchangeable strategy classes so there is no
 * interface to keep bloat to a minimum.
 * 
 * @author mjordan
 */
public class AdvancedFormUpdateStrategy {

    /**
     * String for exceptions.
     */
    private static final String QUERY_TOO_COMPLEX = "Cannot display query because query is too complex";
    /**
     * Reference to the field information map in FormQueryView.
     */
    Map<Field, FieldInfo> fields;
    /**
     * The factory that takes care of parsing queries.
     */
    QueryFactory queryFactory;


    /**
     * Used to return information about the fields that need to be updated.
     */
    public static class QueryField {

        /**
         * The field code of the field.
         */
        public Field fieldCode;
        /**
         * The text in the field.
         */
        public String text;


        @Override
        public String toString() {
            return fieldCode + ": " + text;
        }
    }


    /**
     * Helper class to canonicalize the query tree into one that has an AND node
     * at the root and then at most one child per field code.
     * 
     * @author mj
     */
    private static class NodeCollection {

        /**
         * Store for the query node lists. Quasi a multi map.
         */
        Map<Field, List<QueryNode>> nodes = new EnumMap<Field, List<QueryNode>>(Field.class);


        /**
         * Puts a node under the field code specific list in the node store.
         * 
         * @param node
         *            the node
         */
        public void collectNode(QueryNode node) {
            Field key = node.getFieldCode();
            List<QueryNode> fieldNodes = nodes.get(key);
            if (fieldNodes == null) {
                fieldNodes = new LinkedList<QueryNode>();
                nodes.put(key, fieldNodes);
            }
            fieldNodes.add(node);
        }


        /**
         * Returns the list of new second-level child nodes.
         * 
         * @return the node list that can be plugged under an AND node.
         */
        public List<QueryNode> getNewAndNodes() {
            List<QueryNode> list = new LinkedList<QueryNode>();
            Collection<List<QueryNode>> nodeLists = nodes.values();
            for (List<QueryNode> nodeList : nodeLists) {
                if (nodeList.size() == 1) {
                    list.add(nodeList.get(0));
                }
                else {
                    QueryNodeBool andNode = new QueryNodeBool(NodeType.AND);
                    for (QueryNode node : nodeList) {
                        andNode.addChild(node);
                    }
                    list.add(andNode);
                }
            }
            return list;
        }
    }


    /**
     * Constructor.
     * 
     * @param fields
     *            information about the fields present in the form.
     * @param queryFactory
     *            the factory used to deal with the query
     */
    public AdvancedFormUpdateStrategy(Map<Field, FieldInfo> fields, QueryFactory queryFactory) {
        this.fields = fields;
        this.queryFactory = queryFactory;
    }


    /**
     * The business method of this strategy class.
     * 
     * @param tree
     *            the root of the query tree to work on
     * @return a list of Field objects that contain information about which
     *         field has to be set to which query text.
     * @throws QueryViewUpdateException
     */
    public List<QueryField> getFieldStrings(QueryNode treeRoot) throws QueryViewUpdateException {
        List<QueryField> fieldStrings = new ArrayList<QueryField>();
        if (treeRoot != null) {

            if (treeRoot.getFieldCode() != Field.FIELDCODE_MIXED) {
                // root has only nodes below that have one single
                // shared field code. So we can map the whole tree into a
                // single field.
                QueryField field = plugTreeIntoCorrespondingField(treeRoot);
                fieldStrings.add(field);
            }
            else {
                // Last hope: AND node with single-field-code
                // children
                if (treeRoot instanceof QueryNodeBool) {
                    QueryNodeBool boolTree = (QueryNodeBool) treeRoot;
                    if (boolTree.isOfType(NodeType.AND)) {
                        QueryNodeBool tree = gatherAndNodes(boolTree);
                        fieldStrings = updateFromCanonicalBooleanTree(tree);
                    }
                    else {
                        throw new QueryViewUpdateException(QUERY_TOO_COMPLEX);
                    }
                }
            }

        }

        return fieldStrings;
    }


    /**
     * Gathers the AND nodes with a common fieldCode under one common AND node.
     * <p>
     * A tree might be like this: {AND AU:a FT:b FT:c}. What we want is a tree
     * that looks like this: {AND AU:a {AND FT:b FT:c}}
     * 
     * @param treeRoot
     *            the root of the original tree, which is an AND node
     * @return a new treeRoot that has new AND nodes for the nodes with
     *         duplicate field code.
     */
    private QueryNodeBool gatherAndNodes(QueryNodeBool treeRoot) {
        List<QueryNode> children = treeRoot.children();
        NodeCollection nodes = new NodeCollection();
        for (QueryNode child : children) {
            nodes.collectNode(child);
        }
        List<QueryNode> newChildNodes = nodes.getNewAndNodes();
        QueryNodeBool newRoot = new QueryNodeBool(NodeType.AND);
        newRoot.setChildren(newChildNodes);
        return newRoot;
    }


    /**
     * This method updates the view using a query tree that has an AND node at
     * the root and below only children that have a non-mixed field code. E.g.
     * Author=a AND (Title=b OR Title=c).
     * <p>
     * If the query is of more complex kind, an exception is thrown.
     * 
     * @param boolRoot
     * @throws NoSuchFieldCodeException
     */
    private List<QueryField> updateFromCanonicalBooleanTree(QueryNodeBool boolRoot) throws QueryViewUpdateException {
        List<QueryField> fieldStrings = new ArrayList<QueryField>();
        boolean doesntMapToFields = false;

        if (boolRoot.isOfType(QueryNodeBool.NodeType.AND)) {
            List<QueryNode> children = boolRoot.children();
            for (QueryNode child : children) {
                if (child.getFieldCode() == Field.FIELDCODE_MIXED) {
                    doesntMapToFields = true;
                }
                else {
                    QueryField field = plugTreeIntoCorrespondingField(child);
                    fieldStrings.add(field);
                }
            }
        }
        else {
            doesntMapToFields = true;
        }

        if (doesntMapToFields) {
            throw new QueryViewUpdateException(QUERY_TOO_COMPLEX);
        }
        return fieldStrings;
    }


    /**
     * Takes a query tree (by its root), renders a string out of it and plugs
     * the string into the form field that corresponds to the tree's field code.
     * 
     * @param treeRoot
     *            the root node of the tree with one common field code.
     */
    private QueryField plugTreeIntoCorrespondingField(QueryNode treeRoot) throws QueryViewUpdateException {
        QueryField field = new QueryField();
        Field fieldCode = treeRoot.getFieldCode();
        FieldInfo fieldInfo = fields.get(fieldCode);

        if (fieldInfo != null) {
            String queryText = queryFactory.getTextForQueryNode(treeRoot, fieldInfo.fieldCode);
            field.fieldCode = fieldInfo.fieldCode;
            field.text = queryText;
        }
        else {
            throw new QueryViewUpdateException("Cannot display query because proper field not present");
        }
        return field;
    }


    /**
     * Returns the query that is represented by the contents of the fields.
     * 
     * @param fields
     *            the information about the fields that contain the query
     * @return the Query object along with the QueryNode tree
     */
    public Query getQueryFromFields(Collection<FieldInfo> fields) {
        QueryNodeBool andNode = new QueryNodeBool(QueryNodeBool.NodeType.AND);
        Query query;

        for (FieldInfo field : fields) {
            Query subQuery = getSubQuery(field.textField, field.fieldCode);
            if (subQuery != null) {
                andNode.addChild(subQuery.getTree());
            }
        }

        switch (andNode.childCount()) {
            case 0: {
                query = new DefaultQuery();
                break;
            }
            case 1: {
                query = new DefaultQuery(andNode.getChildAt(0));
                break;
            }
            default: {
                query = new DefaultQuery(andNode);
            }
        }

        return query;
    }


    /**
     * Takes text in a text field and the field's code and returns the query
     * that this string represents in the context of the field code.
     * 
     * @param textField
     *            the text field
     * @param fieldCode
     *            the field code of the text, indicating what a term without any
     *            modifier means (author name, year, ...)
     * @return
     */
    private Query getSubQuery(JTextField textField, Field fieldCode) {
        String queryString = textField.getText().trim();
        if (queryString != null) {
            boolean illformedQuery = false;
            Query query;
            try {
                query = queryFactory.parse(queryString, fieldCode);
                return query;
            }
            catch (RecognitionException e1) {
                illformedQuery = true;
            }
            catch (NoSuchFieldCodeException e1) {
                illformedQuery = true;
            }
            finally {
                if ((queryString != null) && (queryString.length() != 0)) {
                    textField.setForeground(illformedQuery ? Color.RED : Color.BLACK);
                }
            }
        }
        return null;
    }
}
