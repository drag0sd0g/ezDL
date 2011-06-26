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
import java.util.regex.Pattern;

import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;



/**
 * Compare node of the query tree.
 * <p>
 * It represents a compare statement of the form 'field' 'compare
 * operator' 'value', e.g. Author = 'Arthur Miller'. The field value contains a
 * field code, whose meaning is defined, and can be look up in a
 * {@link QueryRegistry QueryRegistry}.<BR>
 * In the query tree all compare nodes are leafs.
 * <p>
 * {@link QueryNodeCompare} can contain values with wildcards.
 */
public class QueryNodeCompare extends QueryNodeBase {

    /**
     * Placeholder for exactly one single character.
     */
    public static final String WILDCARD_SINGLE = "$";
    /**
     * Placeholder for any number of characters.
     */
    public static final String WILDCARD_MULTIPLE = "#";


    /**
     * Possible predicates.
     * 
     * @author mjordan
     */
    public enum Predicate {
        LT("<"), LTE("<="), EQ("="), GTE(">="), GT(">");

        private String meaning;


        Predicate(String meaning) {
            this.meaning = meaning;
        }


        @Override
        public String toString() {
            return meaning;
        }


        /**
         * Returns the Predicate value that is equivalent to the parameter
         * 
         * @param operator
         *            the string representation of the Predicate value to return
         * @return the Predicate value.
         * @throws IllegalArgumentException
         *             if no Predicate can be matched to the input
         */
        public static Predicate fromString(String operator) {
            Predicate out = null;
            for (Predicate p : values()) {
                if (p.toString().equals(operator)) {
                    out = p;
                }
            }
            if (out == null) {
                throw new IllegalArgumentException("Predicate " + operator + " not found");
            }
            return out;
        }
    }


    private static final long serialVersionUID = 5611634827395992807L;
    /**
     * The default relation operation AKA comparison operator.
     */
    public static final Predicate DEFAULT_PREDICATE = Predicate.EQ;
    /**
     * The comparison of this node. E.g. "=" or ">".
     */
    private Predicate predicate = DEFAULT_PREDICATE;
    /**
     * The values of this node.
     */
    private List<String> tokens;


    /**
     * Creates a new empty compare (leaf) node.
     */
    public QueryNodeCompare() {
        super();
        this.tokens = new ArrayList<String>();
    }


    /**
     * Creates a new compare equal (leaf) node.
     * <p>
     * It represents a new equal node with the given field and value. <BR>
     * Form: field = value
     * 
     * @param newField
     *            the field of the new node
     * @param newFalue
     *            the value of the new node
     */
    public QueryNodeCompare(Field newField, String newValue) {
        this();
        setFieldCode(newField);
        predicate = DEFAULT_PREDICATE;
        setToken(newValue);
    }


    /**
     * Creates a new compare (leaf) node with the given data.
     * 
     * @param newField
     *            the field of the new node
     * @param newRelOp
     *            the predicate of the new node
     * @param newFalue
     *            the value of the new node
     */
    public QueryNodeCompare(Field newField, Predicate newRelOp, String newValue) {
        this(newField, newValue);
        predicate = newRelOp;
    }


    /**
     * Returns the predicate of the node.
     * 
     * @return the predicate of the node. E.g. Predicate.EQ, the node performs a
     *         "=" comparison.
     */
    public Predicate getPredicate() {
        return predicate;
    }


    /**
     * Returns the tokens of the node.
     * 
     * @return the tokens that make the value of the node.
     */
    public List<String> getTokens() {
        return tokens;
    }


    /**
     * Appends a new token to the value of the node.
     * 
     * @param newToken
     *            the new token to append.
     */
    public void addToken(String newToken) {
        tokens.add(newToken);
    }


    /**
     * Sets the list of tokens to contain only the new value.
     * 
     * @param newToken
     *            set the new token. If newToken is null, then the token list
     *            will be empty.
     */
    public void setToken(String newToken) {
        tokens.clear();
        if (newToken != null) {
            addToken(newToken);
        }
    }


    /**
     * Sets the list of tokens to contain only the new value.
     * 
     * @param tokens
     *            the new token list which must not be null.
     * @throws IllegalArgumentException
     *             if the list of tokens is null
     */
    public void setTokens(List<String> tokens) {
        if (tokens == null) {
            throw new IllegalArgumentException("tokens must not be null");
        }
        this.tokens = tokens;
    }


    /**
     * Sets the predicate , e.g. '=' or '>=', of the leaf node.
     * 
     * @param newPredicate
     *            the predicate to set
     */
    public void setPredicate(Predicate newPredicate) {
        predicate = newPredicate;
    }


    /**
     * Returns a string representation of the value with all wildcards.
     * 
     * @return a string that represents the node. Wildcards are represented by
     *         {@link #WILDCARD_SINGLE} and {@link #WILDCARD_MULTIPLE},
     *         respectively.
     */
    public String getTokensAsString() {
        StringBuilder builder = new StringBuilder();
        for (String token : tokens) {
            builder.append(token);
        }
        return builder.toString();
    }


    /**
     * Returns if the value contains wildcards.
     * 
     * @return true if wildcards are present. Else false.
     */
    public boolean hasWildcards() {
        for (String token : tokens) {
            if (isTokenWildcard(token)) {
                return true;
            }
        }
        return false;
    }


    /**
     * Returns if a given token is a wildcard.
     * 
     * @param token
     *            the token to test
     * @return true if the token is a wildcard. Else false.
     */
    public static boolean isTokenWildcard(String token) {
        return (isTokenWildcardMulti(token)) || (isTokenWildcardSingle(token));
    }


    /**
     * Returns if a given token is a single character wildcard.
     * 
     * @param token
     *            the token to test
     * @return true if the token is a single character wildcard. Else false.
     */
    public static boolean isTokenWildcardSingle(String token) {
        return WILDCARD_SINGLE.equals(token);
    }


    /**
     * Returns if a given token is a multi-character wildcard.
     * 
     * @param token
     *            the token to test
     * @return true if the token is a multi-character wildcard. Else false.
     */
    public static boolean isTokenWildcardMulti(String token) {
        return WILDCARD_MULTIPLE.equals(token);
    }


    /**
     * Returns a String that represents the node. The form is "(fieldcode=text)"
     * or "(NOT fieldcode=text)", depending on if the node is negated.
     * 
     * @return a string representation of the receiver
     */
    @Override
    public String toString() {
        StringBuffer out = new StringBuffer();

        out.append('(');
        if (isNegated()) {
            out.append("NOT ");
        }
        out.append(getFieldCode()).append(predicate).append(getTokensAsString());
        out.append(')');
        return out.toString();
    }


    /**
     * @return returns the node as a regular expression pattern
     */
    public String asRegEx() {
        StringBuilder regEx = new StringBuilder();
        for (String token : tokens) {
            if (!isTokenWildcard(token)) {
                regEx.append(Pattern.quote(token));
            }
            else if (isTokenWildcardSingle(token)) {
                regEx.append("\\w");
            }
            else if (isTokenWildcardMulti(token)) {
                regEx.append("\\w*");
            }
        }
        return regEx.toString();
    }


    @Override
    public QueryNodeBase plainClone() {
        QueryNodeCompare queryNode = new QueryNodeCompare();
        queryNode.setFieldCode(getFieldCode());
        queryNode.setNegated(isNegated());
        queryNode.setPredicate(getPredicate());
        queryNode.setTokens(getTokens());
        return queryNode;
    }


    @Override
    protected void updateFieldCodeRebuild() {
        super.updateFieldCodeRebuild();
    }
}
