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

/**
 * This is a {@link QueryNode} that represents a proximity search.
 * <p>
 * Proximity search is searching of terms in a given word distance. E.g.
 * searching for "term1" and "term2" in maximum distance 1 would yield a result
 * for the string "term1 term2" and for the string "term1 foo term2" but not for
 * the string "term1 foo bar term2".
 * 
 * @author mjordan
 */
public class QueryNodeProximity extends QueryNodeBase {

    private static final long serialVersionUID = 1L;

    /**
     * The terms.
     */
    private final String[] terms = new String[2];

    /**
     * The maximum distance in words between the two given terms.
     */
    private int maxDistance;


    /**
     * Instantiate a new QueryNode object.
     */
    public QueryNodeProximity(String term0, int maxDistance, String term1) {
        super();
        setTerms(term0, term1);
        setMaxDistance(maxDistance);
    }


    /**
     * The terms.
     * <p>
     * This is guaranteed to be an array of size 2 and the strings in the array
     * are guaranteed not to be null.
     * 
     * @return the terms
     */
    public String[] getTerms() {
        return terms;
    }


    /**
     * Sets the terms.
     * 
     * @param terms
     *            the terms to set
     */
    public void setTerms(String[] terms) {
        if ((terms == null) || (terms.length != 2)) {
            throw new IllegalArgumentException("No term must be null.");
        }
        setTerms(terms[0], terms[1]);
    }


    /**
     * Sets the terms.
     * 
     * @param term0
     *            the first term to set
     * @param term1
     *            the second term to set
     */
    public void setTerms(String term0, String term1) {
        if ((term0 == null) || (term1 == null)) {
            throw new IllegalArgumentException("No term must be null.");
        }
        this.terms[0] = term0;
        this.terms[1] = term1;
    }


    /**
     * The maximum distance.
     * <p>
     * This is guaranteed to be larger or equal 0.
     * 
     * @return the maxDistance
     */
    public int getMaxDistance() {
        return maxDistance;
    }


    /**
     * Sets the maximum distance the terms may be separated in words.
     * 
     * @param maxDistance
     *            the maxDistance to set
     */
    public void setMaxDistance(int maxDistance) {
        if (maxDistance < 0) {
            throw new IllegalArgumentException("maxDistance must be >= 0");
        }
        this.maxDistance = maxDistance;
    }


    @Override
    public QueryNodeBase plainClone() {
        QueryNodeProximity queryNode = new QueryNodeProximity(getTerms()[0], getMaxDistance(), getTerms()[1]);
        queryNode.setFieldCode(getFieldCode());
        queryNode.setNegated(isNegated());
        return queryNode;
    }


    /**
     * Return a string that is a structural representation of this node in the
     * form {Node-type [field] list-of-children} or (field code = value). This
     * is for old-school debugging.
     * 
     * @return a decent string representation of the query tree including its
     *         structure.
     */
    @Override
    public String toString() {
        final int field = getFieldCode().asInt();
        return field + "={" + terms[0] + " /" + maxDistance + " " + terms[1] + "}";
    }

}
