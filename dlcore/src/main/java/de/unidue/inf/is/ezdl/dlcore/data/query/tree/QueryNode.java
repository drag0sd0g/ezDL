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

import java.io.Serializable;

import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;
import de.unidue.inf.is.ezdl.dlcore.data.query.Query;



/**
 * QueryNode is an abstract class for all nodes in a query tree.
 */
public abstract class QueryNode implements Serializable {

    private static final long serialVersionUID = 7657460528596217739L;

    /**
     * If true, this node is logically negated (as in "NOT Fuhr").
     */
    private boolean negated = false;
    /**
     * The parent node of this one in the tree or null if there isn't any.
     */
    private QueryNodeBool parent;

    /**
     * The field code of this sub tree. Possible values: FIELDCODE_MIXED, if the
     * field codes of the lower sub trees differ or a positive integer if they
     * are the same.
     */
    protected Field fieldCode = Field.FIELDCODE_NONE;


    /**
     * Instantiate a new QueryNode object.
     */
    public QueryNode() {
        super();
    }


    /**
     * Removes this node out of the tree.
     * <p>
     * It removes itself from the list of children of its parent.
     * 
     * @see Query#removeNode(QueryNode)
     */
    public void remove() {
        if (parent != null) {
            parent.removeChild(this);
        }
        setParent(null);
    }


    /**
     * @return the field code of this node/tree.
     */
    public Field getFieldCode() {
        return fieldCode;
    }


    /**
     * Sets the code for the meta data tag, e.g. for author or title.
     */
    public void setFieldCode(Field newField) {
        fieldCode = newField;
        updateFieldCodeRebuild();
    }


    /**
     * Recalculates the field code when the new field code cannot be calculated
     * somehow else.
     */
    protected void updateFieldCodeRebuild() {
        QueryNode parent = getParent();
        if (parent != null) {
            parent.updateFieldCodeRebuild();
        }
    }


    /**
     * Returns a reference to the parent of this node.
     * <p>
     * 
     * @return the parent of the node or null, if the node is the root of the
     *         tree.
     */
    public QueryNodeBool getParent() {
        return parent;
    }


    /**
     * Makes the given node the parent of this node.
     */
    public void setParent(QueryNodeBool newParent) {
        parent = newParent;
    }


    /**
     * Returns whether the node is negated or not.
     * 
     * @return true if the node is negated. Else false.
     */
    public boolean isNegated() {
        return negated;
    }


    /**
     * Negate the node.
     * 
     * @param newNegated
     *            true if the node should be negated. Else false.
     */
    public final void setNegated(boolean newNegated) {
        negated = newNegated;
    }


    /**
     * Returns a string that is a structural representation of this node in the
     * form {Node-type [field] list-of-children} or (field code = value). This
     * is for old-school debugging.
     * 
     * @return a decent string representation of the query tree including its
     *         structure.
     */
    @Override
    public abstract String toString();

}
