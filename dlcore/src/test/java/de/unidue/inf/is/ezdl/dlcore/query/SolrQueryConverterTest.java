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

import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.util.Version;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.unidue.inf.is.ezdl.dlcore.AbstractTestBase;
import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;
import de.unidue.inf.is.ezdl.dlcore.data.query.DefaultQuery;
import de.unidue.inf.is.ezdl.dlcore.data.query.Query;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeBool;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeBool.NodeType;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeCompare;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeCompare.Predicate;



public class SolrQueryConverterTest extends AbstractTestBase {

    private QueryConverter queryConverter;


    @Before
    public void init() {
        queryConverter = new SolrQueryConverter();
    }


    @Test
    public void testConvert() {
        QueryNodeBool queryNodeBool1 = new QueryNodeBool(NodeType.OR);
        QueryNodeCompare queryNodeCompare1 = new QueryNodeCompare(Field.AUTHOR, Predicate.EQ, "a");
        QueryNodeCompare queryNodeCompare2 = new QueryNodeCompare(Field.TITLE, Predicate.LTE, "b");
        queryNodeBool1.addChild(queryNodeCompare1);
        queryNodeBool1.addChild(queryNodeCompare2);

        QueryNodeBool queryNodeBool2 = new QueryNodeBool(NodeType.OR);
        QueryNodeCompare queryNodeCompare3 = new QueryNodeCompare(Field.YEAR, Predicate.LT, "c");
        QueryNodeCompare queryNodeCompare4 = new QueryNodeCompare(Field.AUTHOR, Predicate.GT, "d");
        queryNodeBool2.addChild(queryNodeCompare3);
        queryNodeBool2.addChild(queryNodeCompare4);

        QueryNodeBool queryNodeBool3 = new QueryNodeBool(NodeType.OR);
        QueryNodeCompare queryNodeCompare5 = new QueryNodeCompare(Field.CITATION, Predicate.EQ, "?-e+");
        QueryNodeCompare queryNodeCompare6 = new QueryNodeCompare(Field.TEXT, Predicate.GTE, "\\*(f)");
        queryNodeBool3.addChild(queryNodeCompare5);
        queryNodeBool3.addChild(queryNodeCompare6);

        QueryNodeBool queryNodeBool4 = new QueryNodeBool(NodeType.AND);
        queryNodeBool4.addChild(queryNodeBool1);
        queryNodeBool4.addChild(queryNodeBool2);
        queryNodeBool4.addChild(queryNodeBool3);

        Query q1 = new DefaultQuery(queryNodeBool4);

        Assert.assertEquals(
                        "(1003:a OR 4:[* TO b]) AND (31:{* TO c} OR 1003:{d TO *}) AND (5002:\\?\\-e\\+ OR 1046:[\\\\\\*\\(f\\) TO *])",
                        queryConverter.convert(q1));

        try {
            org.apache.lucene.search.Query query = new QueryParser(Version.LUCENE_30, Field.TEXT.toString(),
                            new SimpleAnalyzer()).parse(queryConverter.convert(q1));
            Assert.assertNotNull(query);
        }
        catch (ParseException e) {
            Assert.fail("Parser error");
        }
    }


    @Test
    public void testPhrase() {
        QueryNodeBool queryNodeBool = new QueryNodeBool(NodeType.OR);
        QueryNodeCompare queryNodeCompare1 = new QueryNodeCompare(Field.AUTHOR, Predicate.EQ, "a b");
        QueryNodeCompare queryNodeCompare2 = new QueryNodeCompare(Field.YEAR, Predicate.EQ, "b c");
        queryNodeBool.addChild(queryNodeCompare1);
        queryNodeBool.addChild(queryNodeCompare2);

        Assert.assertEquals("1003:\"a b\" OR 31:\"b c\"", queryConverter.convert(new DefaultQuery(queryNodeBool)));
    }


    @Test
    public void testNot1() {
        QueryNodeBool queryNodeBool = new QueryNodeBool(NodeType.AND);
        QueryNodeCompare queryNodeCompare1 = new QueryNodeCompare(Field.AUTHOR, Predicate.EQ, "a");
        queryNodeCompare1.setNegated(true);
        QueryNodeCompare queryNodeCompare2 = new QueryNodeCompare(Field.YEAR, Predicate.EQ, "b");
        queryNodeBool.addChild(queryNodeCompare1);
        queryNodeBool.addChild(queryNodeCompare2);

        QueryNodeBool queryNodeBool2 = new QueryNodeBool(NodeType.OR);
        queryNodeBool2.setNegated(true);
        QueryNodeCompare queryNodeCompare3 = new QueryNodeCompare(Field.AUTHOR, Predicate.EQ, "c");
        queryNodeBool2.addChild(queryNodeCompare3);
        queryNodeBool2.addChild(queryNodeBool);

        Assert.assertEquals("NOT 1003:c AND (1003:a OR NOT 31:b)",
                        queryConverter.convert(new DefaultQuery(queryNodeBool2)));
    }


    @Test
    public void testNot2() {
        QueryNodeBool queryNodeBool = new QueryNodeBool(NodeType.OR);
        QueryNodeCompare queryNodeCompare1 = new QueryNodeCompare(Field.AUTHOR, Predicate.EQ, "a");
        queryNodeCompare1.setNegated(true);
        QueryNodeCompare queryNodeCompare2 = new QueryNodeCompare(Field.YEAR, Predicate.EQ, "b");
        queryNodeBool.addChild(queryNodeCompare1);
        queryNodeBool.addChild(queryNodeCompare2);

        QueryNodeBool queryNodeBool2 = new QueryNodeBool(NodeType.AND);
        queryNodeBool2.setNegated(true);
        QueryNodeCompare queryNodeCompare3 = new QueryNodeCompare(Field.AUTHOR, Predicate.EQ, "c");
        queryNodeBool2.addChild(queryNodeCompare3);
        queryNodeBool2.addChild(queryNodeBool);

        Assert.assertEquals("NOT 1003:c OR 1003:a AND NOT 31:b",
                        queryConverter.convert(new DefaultQuery(queryNodeBool2)));
    }


    @Test
    public void testNot3() {
        QueryNodeBool queryNodeBool = new QueryNodeBool(NodeType.OR);
        QueryNodeCompare queryNodeCompare1 = new QueryNodeCompare(Field.AUTHOR, Predicate.EQ, "a");
        queryNodeCompare1.setNegated(true);
        QueryNodeCompare queryNodeCompare2 = new QueryNodeCompare(Field.YEAR, Predicate.EQ, "b");
        QueryNodeCompare queryNodeCompare3 = new QueryNodeCompare(Field.AUTHOR, Predicate.EQ, "c");
        queryNodeBool.addChild(queryNodeCompare1);
        queryNodeBool.addChild(queryNodeCompare2);
        queryNodeBool.addChild(queryNodeCompare3);

        Assert.assertEquals("NOT 1003:a OR 31:b OR 1003:c", queryConverter.convert(new DefaultQuery(queryNodeBool)));
    }


    @Test
    public void testConvertOR() {
        QueryNodeBool queryNodeBool = new QueryNodeBool(NodeType.OR);
        QueryNodeCompare queryNodeCompare1 = new QueryNodeCompare(Field.AUTHOR, Predicate.EQ, "a");
        QueryNodeCompare queryNodeCompare2 = new QueryNodeCompare(Field.AUTHOR, Predicate.EQ, "b");
        queryNodeBool.addChild(queryNodeCompare1);
        queryNodeBool.addChild(queryNodeCompare2);

        Query q1 = new DefaultQuery(queryNodeBool);

        Assert.assertEquals("1003:a OR 1003:b", queryConverter.convert(q1));

        try {
            org.apache.lucene.search.Query query = new QueryParser(Version.LUCENE_30, Field.TEXT.toString(),
                            new SimpleAnalyzer()).parse(queryConverter.convert(q1));
            Assert.assertNotNull(query);
        }
        catch (ParseException e) {
            Assert.fail("Parser error");
        }
    }


    @Test
    public void testConvertAND() {
        QueryNodeBool queryNodeBool = new QueryNodeBool(NodeType.AND);
        QueryNodeCompare queryNodeCompare1 = new QueryNodeCompare(Field.AUTHOR, Predicate.EQ, "a");
        QueryNodeCompare queryNodeCompare2 = new QueryNodeCompare(Field.AUTHOR, Predicate.EQ, "b");
        queryNodeBool.addChild(queryNodeCompare1);
        queryNodeBool.addChild(queryNodeCompare2);

        Query q1 = new DefaultQuery(queryNodeBool);
        Query q2 = new DefaultQuery(q1.asDNF());

        Assert.assertEquals("1003:a AND 1003:b", queryConverter.convert(q1));
        Assert.assertEquals("1003:a AND 1003:b", queryConverter.convert(q2));

        try {
            org.apache.lucene.search.Query query = new QueryParser(Version.LUCENE_30, Field.TEXT.toString(),
                            new SimpleAnalyzer()).parse(queryConverter.convert(q1));
            Assert.assertNotNull(query);
            org.apache.lucene.search.Query query2 = new QueryParser(Version.LUCENE_30, Field.TEXT.toString(),
                            new SimpleAnalyzer()).parse(queryConverter.convert(q2));
            Assert.assertNotNull(query2);
        }
        catch (ParseException e) {
            Assert.fail("Parser error");
        }
    }

}
