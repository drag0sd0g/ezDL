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

import java.util.Collections;

import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNode;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeBool;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeBool.NodeType;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeCompare;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeCompare.Predicate;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeProximity;



public class QueryFactory {

    private static final Predicate EQ = Predicate.EQ;


    /**
     * Returns a query 'ezdl OR supporting OR libraries OR federated OR
     * Author="Norbert Fuhr"'.
     * 
     * @return the query
     */
    public static DocumentQuery getQuery1() {
        QueryNodeBool d = new QueryNodeBool(NodeType.OR);

        d.addChild(new QueryNodeCompare(Field.TEXT, EQ, "ezdl"));
        d.addChild(new QueryNodeCompare(Field.TEXT, EQ, "supporting"));
        d.addChild(new QueryNodeCompare(Field.TEXT, EQ, "libraries"));
        d.addChild(new QueryNodeCompare(Field.TEXT, EQ, "federated"));
        d.addChild(new QueryNodeCompare(Field.AUTHOR, EQ, "Norbert Fuhr"));
        DefaultQuery q = new DefaultQuery(d);
        DocumentQuery documentQuery = new DocumentQuery(q, Collections.<String> emptyList());

        return documentQuery;
    }


    /**
     * Returns a query 'Title=Information OR Retrieval'.
     * 
     * @return the query
     */
    public static DocumentQuery getQuery2() {
        QueryNodeBool d = new QueryNodeBool(NodeType.OR);

        d.addChild(new QueryNodeCompare(Field.TITLE, EQ, "information"));
        d.addChild(new QueryNodeCompare(Field.TITLE, EQ, "retrieval"));
        DefaultQuery q = new DefaultQuery(d);
        DocumentQuery documentQuery = new DocumentQuery(q, Collections.<String> emptyList());

        return documentQuery;
    }


    /**
     * Returns a query that has the form 'Year=...' with '...' being the year
     * given as a parameter.
     * 
     * @param year
     *            the year to ask for
     * @return the query
     */
    public static DocumentQuery getYearQuery(int year) {
        QueryNodeBool d = new QueryNodeBool(NodeType.OR);

        QueryNodeBool c = new QueryNodeBool(NodeType.AND);
        d.addChild(c);

        QueryNodeCompare queryNodeCompare = new QueryNodeCompare(Field.YEAR, EQ, Integer.toString(year));
        c.addChild(queryNodeCompare);

        DefaultQuery q = new DefaultQuery(d);
        DocumentQuery documentQuery = new DocumentQuery(q, Collections.<String> emptyList());

        return documentQuery;
    }


    /**
     * Returns a query with a title and a year
     * 
     * @param the
     *            year
     * @return the query
     */
    public static DocumentQuery getTitleAndYearQuery(int year) {
        QueryNodeBool d = new QueryNodeBool(NodeType.OR);

        d.addChild(new QueryNodeCompare(Field.TITLE, EQ, "information"));
        d.addChild(new QueryNodeCompare(Field.TITLE, EQ, "retrieval"));

        QueryNodeBool c = new QueryNodeBool(NodeType.AND);
        c.addChild(d);

        QueryNodeCompare queryNodeCompare = new QueryNodeCompare(Field.YEAR, EQ, Integer.toString(year));
        c.addChild(queryNodeCompare);

        DefaultQuery q = new DefaultQuery(c);
        DocumentQuery documentQuery = new DocumentQuery(q, Collections.<String> emptyList());

        return documentQuery;
    }


    /**
     * Creates a query tree that includes all features of the {@link QueryNode}
     * hierarchy.
     * 
     * @param year
     *            a year
     * @return the query tree
     */
    public static DocumentQuery getAllFeaturesQuery(Field field1, Field field2, int year) {
        QueryNodeBool d = new QueryNodeBool(NodeType.OR);

        d.addChild(new QueryNodeCompare(field1, EQ, "information"));
        final QueryNodeCompare child = new QueryNodeCompare(field1, EQ, "retrieval");
        child.setNegated(true);
        d.addChild(child);

        QueryNodeBool c = new QueryNodeBool(NodeType.AND);
        c.addChild(d);

        QueryNodeCompare queryNodeCompare = new QueryNodeCompare(Field.YEAR, EQ, Integer.toString(year));
        c.addChild(queryNodeCompare);

        QueryNodeProximity prox = new QueryNodeProximity("term1", 3, "term2");
        prox.setFieldCode(field2);
        c.addChild(prox);

        QueryNodeCompare wildcards = new QueryNodeCompare();
        wildcards.setFieldCode(field1);
        wildcards.setPredicate(EQ);
        wildcards.addToken("abc");
        wildcards.addToken(QueryNodeCompare.WILDCARD_SINGLE);
        wildcards.addToken("de");
        wildcards.addToken(QueryNodeCompare.WILDCARD_MULTIPLE);
        c.addChild(wildcards);

        DefaultQuery q = new DefaultQuery(c);
        DocumentQuery documentQuery = new DocumentQuery(q, Collections.<String> emptyList());
        return documentQuery;
    }
}
