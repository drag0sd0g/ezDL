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

import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;
import de.unidue.inf.is.ezdl.dlcore.data.query.Query;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNode;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeBool;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeCompare;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeProximity;



/**
 * In quite a few cases, year ranges are not part of the query but of some query
 * metadata. E.g. PubMed has a query field in the URL and a "mindate" and a
 * "maxdate" additionally to the query. So this class has a way to extract these
 * two pieces of information from the query.
 * 
 * @author mjordan
 */
public class YearRangeConverter {

    /**
     * Contains the minimum and maximum of a year range either of which can be
     * null to indicate not being bounded in that direction.
     * <p>
     * Example: A range "everything until 2005" would have {@link #minYear} set
     * to null.
     * 
     * @author mjordan
     */
    public class YearRange {

        public Integer minYear;
        public Integer maxYear;


        /**
         * Creates a new year range object with minimum and maximum year set to
         * null.
         */
        public YearRange() {
            super();
        }


        @Override
        public String toString() {
            return "[" + minYear + "," + maxYear + "]";
        }


        @Override
        public boolean equals(Object o) {
            if (o instanceof YearRange) {
                YearRange other = (YearRange) o;
                final boolean minEq = (minYear == other.minYear)
                                || ((minYear != null) && (minYear.equals(other.minYear)));
                final boolean maxEq = (maxYear == other.maxYear)
                                || ((maxYear != null) && (maxYear.equals(other.maxYear)));
                return minEq && maxEq;
            }
            return false;
        }
    }


    /**
     * Given a {@link QueryNode}, this method finds the minimum and maximum year
     * given in any of the subnodes.
     * <p>
     * If there are multiple candidates for either border, the most extreme
     * (smallest/greatest) value is chosen. A non-existent border is indicated
     * by a null value in the year range. E.g. no year clauses in the query
     * returns a YearRange with null and null as lower and upper bound.
     * <p>
     * This method is thread-safe.
     * 
     * @param queryNode
     *            the query node of a supposed query tree
     * @return the minimum and maximum year found in any
     *         {@link QueryNodeCompare} under the query node given.
     */
    public YearRange convertYearRange(QueryNode queryNode) {
        final YearSearcher searcher = new YearSearcher();
        searcher.walk(queryNode);
        final YearRange range = searcher.getRange();

        return range;
    }


    /**
     * Given a {@link Query}, this method finds the minimum and maximum year
     * given in any of the subnodes of the query's tree if such a tree exists.
     * 
     * @see #convertYearRange(QueryNode)
     * @param query
     *            the query
     * @return the minimum and maximum year found in any
     *         {@link QueryNodeCompare} under the query node given.
     */
    public YearRange convertYearRange(Query query) {
        if ((query == null) || (query.getTree() == null)) {
            return new YearRange();
        }
        return convertYearRange(query.getTree());
    }


    private class YearSearcher extends AbstractDefaultOrderQueryTreeWalker {

        private YearRange range = new YearRange();


        @Override
        protected void process(QueryNodeBool node, QueryNodeBool parent, int parentChildrenIndex,
                        int parentChildrenCount) {
            // Don't do anything
        }


        @Override
        protected void process(QueryNodeCompare node, QueryNodeBool parent, int parentChildrenIndex,
                        int parentChildrenCount) {
            if (node.getFieldCode() == Field.YEAR) {
                handleYearCondition(node);
            }
        }


        @Override
        protected void process(QueryNodeProximity node, QueryNodeBool parent, int parentChildrenIndex,
                        int parentChildrenCount) {
            // Don't do anything
        }


        private void handleYearCondition(QueryNodeCompare base) {
            if (base.hasWildcards()) {
                return;
            }
            int queryYear = 0;
            try {
                queryYear = new Integer(base.getTokensAsString());
            }
            catch (NumberFormatException e) {
                return;
            }
            switch (base.getPredicate()) {
                case EQ: {
                    if ((range.minYear == null) && (range.maxYear == null)) {
                        range.minYear = queryYear;
                        range.maxYear = queryYear;
                    }
                    else {
                        maybeLowerMinYear(range, queryYear);
                        maybeRaiseMaxYear(range, queryYear);
                    }
                    break;
                }
                case GT: {
                    maybeLowerMinYear(range, queryYear + 1);
                    break;
                }
                case GTE: {
                    maybeLowerMinYear(range, queryYear);
                    break;
                }
                case LT: {
                    maybeRaiseMaxYear(range, queryYear - 1);
                    break;
                }
                case LTE: {
                    maybeRaiseMaxYear(range, queryYear);
                    break;
                }
                default: {
                    // Nothing really
                }
            }
        }


        private void maybeRaiseMaxYear(YearRange range, Integer queryYear) {
            if ((range.maxYear == null) || (range.maxYear.compareTo(queryYear) < 0)) {
                range.maxYear = queryYear;
            }
        }


        private void maybeLowerMinYear(YearRange range, Integer queryYear) {
            if ((range.minYear == null) || (range.minYear.compareTo(queryYear) > 0)) {
                range.minYear = queryYear;
            }
        }


        public YearRange getRange() {
            return range;
        }
    }
}
