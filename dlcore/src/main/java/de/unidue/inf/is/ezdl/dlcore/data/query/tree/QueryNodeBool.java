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

package de.unidue.inf.is.ezdl.dlcore.data.query.tree;

import java.util.ArrayList;
import java.util.List;

import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;



/**
 * Internal boolean node of the query tree.
 * <p>
 * The node represents the boolean operators AND and OR.
 */
public class QueryNodeBool extends QueryNode {

    private static final long serialVersionUID = -8037976912343536116L;


    /**
     * NodeType encodes the values that a boolean node can assume.
     */
    public enum NodeType {
        AND("AND"), OR("OR");

        /**
         * The string representation of this type.
         */
        private String text;


        /**
         * Constructor
         * 
         * @param text
         *            the string representation to use.
         */
        private NodeType(String text) {
            this.text = text;
        }


        @Override
        public String toString() {
            return text;
        }
    }


    /**
     * The type of this node.
     */
    private NodeType type;

    /**
     * A list of children.
     */
    private final List<QueryNode> children;


    /**
     * Creates a new QueryNodeBool object. The default type is AND.
     */
    public QueryNodeBool() {
        super();
        type = NodeType.AND;
        children = new ArrayList<QueryNode>();
        removeAllChildren();
    }


    /**
     * Creates new object of the given type.
     */
    public QueryNodeBool(NodeType newType) {
        this();
        type = newType;
    }


    /**
     * @return the type of the boolean operator.
     */
    public NodeType getType() {
        return type;
    }


    /**
     * Sets the type of the boolean operator.
     * 
     * @param type
     *            the type to set.
     */
    public void setType(NodeType type) {
        this.type = type;
    }


    /**
     * Adds the given node to the list of children.
     * 
     * @param child
     *            the QueryNode to add
     */
    public void addChild(QueryNode child) {
        if (child == null) {
            throw new IllegalArgumentException("child must not be null");
        }
        child.setParent(this);
        children.add(child);
        updateFieldCodeRebuild();
    }


    /**
     * Adds a child at the specified position in the list of children.
     * <p>
     * Inserts the given node at the specified position and shifts any existing
     * nodes to the right (adds one to their index).
     */
    public void addChild(QueryNode child, int pos) {
        if (child == null) {
            throw new IllegalArgumentException("child must not be null");
        }
        child.setParent(this);
        children.add(pos, child);
        updateFieldCodeRebuild();
    }


    /**
     * Returns the number of direct children.
     * 
     * @return the number of direct children
     */
    public int childCount() {
        return children.size();
    }


    /**
     * Returns the child at the specified position.
     * <p>
     * The ordering of the children is left to right.
     * 
     * @param childIndex
     *            the index of the child to retrieve
     * @return the child at that position
     * @throws IndexOutOfBoundsException
     */
    public QueryNode getChildAt(int childIndex) {
        return children.get(childIndex);
    }


    /**
     * Returns the index of the given node in the objects list of children.
     * <p>
     * If the receiver does not contain the node, -1 will be returned.
     * 
     * @param node
     *            the node whose index to get
     * @return the index of the given node or -1 if the node is not found
     */
    public int getIndexOfChild(QueryNode node) {
        return children.indexOf(node);
    }


    /**
     * Removes all children of this node.
     */
    protected void removeAllChildren() {
        children.clear();
        setFieldCode(Field.FIELDCODE_NONE);
    }


    /**
     * Removes the given node from the list of children.
     * <p>
     * All following children will be moved one position left (the index is
     * reduced by one).
     * 
     * @param child
     *            the node to remove
     */
    public void removeChild(QueryNode child) {
        children.remove(child);
        updateFieldCodeRebuild();
    }


    /**
     * Replaces <code>oldChild</code> with <code>newChild</code> in the list of
     * children.
     * 
     * @param oldChild
     *            the child to replace
     * @param newChild
     *            the child to replace oldChild with
     */
    public void replaceChild(QueryNode oldChild, QueryNode newChild) {
        if (newChild == null) {
            throw new IllegalArgumentException("newChild must not be null");
        }

        final int childIdx = children.indexOf(oldChild);
        if (childIdx != -1) {
            children.set(childIdx, newChild);
            updateFieldCodeRebuild();
        }
    }


    /**
     * Returns a List of all direct children.
     * <p>
     * Guaranteed never to be null.
     * 
     * @return the list of children
     */
    public List<QueryNode> children() {
        return children;
    }


    /**
     * Makes the given list the list of children.
     */
    public void setChildren(List<QueryNode> newChildren) {
        if (newChildren == null) {
            throw new IllegalArgumentException("Children must not be null");
        }

        children.clear();
        children.addAll(newChildren);

        for (QueryNode child : newChildren) {
            child.setParent(this);
        }

        updateFieldCodeRebuild();
    }


    /**
     * Recalculates the field code when the new field code cannot be calculated
     * somehow else.
     */
    @Override
    protected void updateFieldCodeRebuild() {
        Field oldField = getFieldCode();
        fieldCode = Field.FIELDCODE_NONE;

        for (QueryNode child : children) {
            if (fieldCode == Field.FIELDCODE_NONE) {
                fieldCode = child.getFieldCode();
            }
            else if (fieldCode != child.getFieldCode()) {
                fieldCode = Field.FIELDCODE_MIXED;
                break;
            }
        }

        final boolean fieldCodeChanged = (oldField != fieldCode);
        if (fieldCodeChanged && (getParent() != null)) {
            getParent().updateFieldCodeRebuild();
        }
    }


    /**
     * Compares the given type with the actual type and returns the result.
     * 
     * @param qType
     *            the type to compare to
     * @return true if the node is of the given type. Else false.
     */
    public boolean isOfType(NodeType qType) {
        return (type == qType);
    }


    /**
     * Returns a string representation of the node's operator (AND or OR).
     * 
     * @return a string representation of the receiver
     */
    private String nodeText() {
        String prefix = "";
        if (isNegated()) {
            prefix = "N";
        }
        return prefix + type;
    }


    /**
     * Returns a string representation of the node and its children.
     * <p>
     * The form is {OPERATOR[fieldcode]: ...}, where "..." is the list of
     * children, each of which can in turn be a tree with children.
     */
    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();

        out.append('{');
        out.append(nodeText());
        out.append('[').append(getFieldCode()).append("]: ");
        for (int i = 0; (i < childCount()); i++) {
            out.append(getChildAt(i).toString());
        }
        out.append('}');
        return out.toString();
    }

}