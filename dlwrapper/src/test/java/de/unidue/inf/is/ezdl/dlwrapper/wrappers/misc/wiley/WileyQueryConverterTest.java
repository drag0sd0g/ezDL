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

package de.unidue.inf.is.ezdl.dlwrapper.wrappers.misc.wiley;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import de.unidue.inf.is.ezdl.dlbackend.AbstractBackendTestBase;
import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;
import de.unidue.inf.is.ezdl.dlcore.data.query.DefaultQuery;
import de.unidue.inf.is.ezdl.dlcore.data.query.Query;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeBool;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeBool.NodeType;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeCompare;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeCompare.Predicate;



/**
 * @author mjordan
 */
public class WileyQueryConverterTest extends AbstractBackendTestBase {

    WileyQueryConverter converter = new WileyQueryConverter();


    @Test
    public void testGetChildFields1() {
        QueryNodeBool and = new QueryNodeBool();
        and.addChild(new QueryNodeCompare(Field.TITLE, Predicate.EQ, "a"));
        List<Field> cf = converter.getChildFields(and);
        Assert.assertEquals("[4]", cf.toString());
    }


    @Test
    public void testGetChildFields2() {
        QueryNodeBool and = new QueryNodeBool();
        and.addChild(new QueryNodeCompare(Field.TITLE, Predicate.EQ, "a"));
        and.addChild(new QueryNodeCompare(Field.AUTHOR, Predicate.EQ, "a"));
        and.addChild(new QueryNodeCompare(Field.TEXT, Predicate.EQ, "a"));
        List<Field> cf = converter.getChildFields(and);
        Assert.assertEquals("[4, 1003, 1046]", cf.toString());
    }


    @Test
    public void testGetChildFields3() {
        QueryNodeBool and = new QueryNodeBool();
        and.addChild(new QueryNodeCompare(Field.TITLE, Predicate.EQ, "a"));
        and.addChild(new QueryNodeCompare(Field.TITLE, Predicate.EQ, "b"));
        QueryNodeBool or = new QueryNodeBool(NodeType.OR);
        or.addChild(new QueryNodeCompare(Field.TITLE, Predicate.EQ, "c"));
        or.addChild(new QueryNodeCompare(Field.YEAR, Predicate.EQ, "d"));
        and.addChild(or);
        List<Field> cf = converter.getChildFields(and);
        Assert.assertEquals("[4, 4, -2]", cf.toString());
    }


    @Test
    public void testGenerateSingleFieldQuery0() {
        QueryNodeCompare a = new QueryNodeCompare(Field.TITLE, Predicate.EQ, "a");
        String query = converter.generateSingleFieldQuery(a);
        Assert.assertEquals("a", query);
    }


    @Test
    public void testGenerateSingleFieldQuery1() {
        QueryNodeBool and = new QueryNodeBool();
        and.addChild(new QueryNodeCompare(Field.TITLE, Predicate.EQ, "a"));
        String query = converter.generateSingleFieldQuery(and);
        Assert.assertEquals("a", query);
    }


    @Test
    public void testGenerateSingleFieldQuery2() {
        QueryNodeBool and = new QueryNodeBool();
        and.addChild(new QueryNodeCompare(Field.TITLE, Predicate.EQ, "a"));
        and.addChild(new QueryNodeCompare(Field.TITLE, Predicate.EQ, "b"));
        String query = converter.generateSingleFieldQuery(and);
        Assert.assertEquals("a AND b", query);
    }


    @Test
    public void testGenerateSingleFieldQuery3() {
        QueryNodeBool and = new QueryNodeBool();
        and.addChild(new QueryNodeCompare(Field.TITLE, Predicate.EQ, "a"));
        and.addChild(new QueryNodeCompare(Field.TITLE, Predicate.EQ, "b"));
        QueryNodeBool or = new QueryNodeBool(NodeType.OR);
        or.addChild(new QueryNodeCompare(Field.TITLE, Predicate.EQ, "c"));
        or.addChild(new QueryNodeCompare(Field.YEAR, Predicate.EQ, "d"));
        and.addChild(or);
        String query = converter.generateSingleFieldQuery(and);
        Assert.assertEquals("a AND b AND (c OR d)", query);
    }


    @Test
    public void testGenerateSingleFieldQuery() {
        QueryNodeBool or = new QueryNodeBool(NodeType.OR);
        or.addChild(new QueryNodeCompare(Field.TITLE, Predicate.EQ, "c"));
        or.addChild(new QueryNodeCompare(Field.TITLE, Predicate.EQ, "d"));
        String query = converter.generateSingleFieldQuery(1, Field.TITLE, or);
        Assert.assertEquals("searchRowCriteria[1].queryString=c OR d&searchRowCriteria[1].fieldName=document-title",
                        query);
    }


    @Test
    public void testGetFieldedQuery0a() {
        QueryNodeBool and = new QueryNodeBool();
        and.addChild(new QueryNodeCompare(Field.AUTHOR, Predicate.EQ, "a"));
        and.addChild(new QueryNodeCompare(Field.TEXT, Predicate.EQ, "b"));
        QueryNodeBool or = new QueryNodeBool(NodeType.OR);
        or.addChild(new QueryNodeCompare(Field.TITLE, Predicate.EQ, "c"));
        or.addChild(new QueryNodeCompare(Field.TITLE, Predicate.EQ, "d"));
        and.addChild(or);
        String query = converter.getFieldedQuery(and);
        Assert.assertEquals(
                        "searchRowCriteria[0].queryString=a&searchRowCriteria[0].fieldName=author&searchRowCriteria[0].booleanConnector=and&"
                                        + //
                                        "searchRowCriteria[1].queryString=b&searchRowCriteria[1].fieldName=all-fields&searchRowCriteria[1].booleanConnector=and&"
                                        + //
                                        "searchRowCriteria[2].queryString=c OR d&searchRowCriteria[2].fieldName=document-title",
                        query);
    }


    @Test
    public void testGetFieldedQuery0b() {
        QueryNodeCompare and = new QueryNodeCompare(Field.AUTHOR, Predicate.EQ, "a");
        String query = converter.getFieldedQuery(and);
        Assert.assertEquals("searchRowCriteria[0].queryString=a&searchRowCriteria[0].fieldName=author", query);
    }


    @Test
    public void testQueryApproximation0() {
        boolean exceptionThrown = false;
        QueryNodeBool and = new QueryNodeBool();
        and.addChild(new QueryNodeCompare(Field.AUTHOR, Predicate.EQ, "a"));
        and.addChild(new QueryNodeCompare(Field.TEXT, Predicate.EQ, "b"));
        QueryNodeBool or = new QueryNodeBool(NodeType.OR);
        or.addChild(new QueryNodeCompare(Field.TITLE, Predicate.EQ, "c"));
        or.addChild(new QueryNodeCompare(Field.AUTHOR, Predicate.EQ, "d"));
        and.addChild(or);
        try {
            converter.getQueryApproximation(and);
        }
        catch (UnsupportedOperationException e) {
            exceptionThrown = true;
        }
        Assert.assertTrue(exceptionThrown);
    }


    @Test
    public void test() {
        QueryNodeBool d = new QueryNodeBool(NodeType.OR);
        final Predicate EQ = Predicate.EQ;
        d.addChild(new QueryNodeCompare(Field.TEXT, EQ, "libraries"));
        d.addChild(new QueryNodeCompare(Field.AUTHOR, EQ, "Norbert Fuhr"));
        final Query q = new DefaultQuery(d);
        final String result = converter.convert(q);
        getLogger().debug(result);
    }
}
