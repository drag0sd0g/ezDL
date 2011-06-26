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

package de.unidue.inf.is.ezdl.dlwrapper.wrappers.cs.ieee;

import junit.framework.Assert;

import org.junit.Test;

import de.unidue.inf.is.ezdl.dlbackend.AbstractBackendTestBase;
import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;
import de.unidue.inf.is.ezdl.dlcore.data.query.DefaultQuery;
import de.unidue.inf.is.ezdl.dlcore.data.query.Query;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNode;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeBool;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeBool.NodeType;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeCompare;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeCompare.Predicate;



/**
 * Test for the converter for the ACM DL advanced search.
 * 
 * @author mjordan
 */
public class IEEEAdvancedQueryConverterTest extends AbstractBackendTestBase {

    private IEEEAdvancedQueryConverter converter = new IEEEAdvancedQueryConverter();

    private static final Predicate EQ = Predicate.EQ;


    @Test
    public void testTestConvertBase() {
        checkConvertBase("1", Field.AUTHOR, EQ, "Fuhr", "(\"Author\":Fuhr)");
        checkConvertBase("2", Field.TITLE, EQ, "Testttitle", "(\"Document Title\":Testttitle)");
        checkConvertBase("3", Field.TEXT, EQ, "freetext", "(\"Abstract\":freetext)");
        // checkConvertBase("4", Field.YEAR, "=", "1990", "(Year:1990)"); //
        // Year is not supported in ACM
        checkConvertBase("4", Field.AUTHOR, EQ, "Cinnéide", "(\"Author\":Cinnéide)");
    }


    @Test
    public void testTestConvertList1() {
        QueryNodeCompare au = new QueryNodeCompare(Field.AUTHOR, EQ, "Fuhr");
        QueryNodeBool con = new QueryNodeBool();
        con.addChild(au);
        checkConvert("complex", con, "(\"Author\":Fuhr)");
    }


    @Test
    public void testTestConvertList2() {
        QueryNodeCompare au = new QueryNodeCompare(Field.AUTHOR, EQ, "Fuhr");
        QueryNodeCompare ti = new QueryNodeCompare(Field.TITLE, EQ, "retrieval");
        QueryNodeBool con = new QueryNodeBool();
        con.addChild(au);
        con.addChild(ti);
        checkConvert("complex", con, "(\"Author\":Fuhr) AND (\"Document Title\":retrieval)");
    }


    @Test
    public void testTestConvertList3() {
        QueryNodeCompare au = new QueryNodeCompare(Field.AUTHOR, EQ, "Fuhr");
        QueryNodeCompare ti1 = new QueryNodeCompare(Field.TITLE, EQ, "retrieval");
        QueryNodeCompare ti2 = new QueryNodeCompare(Field.TITLE, EQ, "information");
        QueryNodeBool con = new QueryNodeBool();
        con.addChild(au);
        con.addChild(ti1);
        con.addChild(ti2);
        checkConvert("complex", con,
                        "(\"Author\":Fuhr) AND (\"Document Title\":retrieval) AND (\"Document Title\":information)");
    }


    /**
     * This is a test for a bug that shouldn't show up as long as the ACM
     * wrapper is actually processing only conjunctions.
     */
    @Test
    public void testTestConvertList4() {
        QueryNodeCompare au = new QueryNodeCompare(Field.AUTHOR, EQ, "Fuhr");
        QueryNodeCompare ti1 = new QueryNodeCompare(Field.TITLE, EQ, "retrieval");
        QueryNodeBool con = new QueryNodeBool();
        con.addChild(au);
        con.addChild(ti1);

        QueryNodeBool dis = new QueryNodeBool(NodeType.OR);
        QueryNodeCompare ti2 = new QueryNodeCompare(Field.TITLE, EQ, "information");
        dis.addChild(con);
        dis.addChild(ti2);
        checkConvert("complex", dis,
                        "((\"Author\":Fuhr) AND (\"Document Title\":retrieval)) OR (\"Document Title\":information)");
    }


    @Test
    public void testTestConvertListDefective1() {
        QueryNodeCompare au = new QueryNodeCompare(Field.YEAR, EQ, "2000");
        QueryNodeCompare ti1 = new QueryNodeCompare(Field.TITLE, EQ, "retrieval");
        QueryNodeCompare ti2 = new QueryNodeCompare(Field.TITLE, EQ, "information");
        QueryNodeBool con = new QueryNodeBool();
        con.addChild(au);
        con.addChild(ti1);
        con.addChild(ti2);
        checkConvert("complex", con, "(\"Document Title\":retrieval) AND (\"Document Title\":information)");
    }


    @Test
    public void testTestConvertListDefective2() {
        QueryNodeCompare ti1 = new QueryNodeCompare(Field.TITLE, EQ, "retrieval");
        QueryNodeCompare au = new QueryNodeCompare(Field.YEAR, EQ, "2000");
        QueryNodeCompare ti2 = new QueryNodeCompare(Field.TITLE, EQ, "information");
        QueryNodeBool con = new QueryNodeBool();
        con.addChild(ti1);
        con.addChild(au);
        con.addChild(ti2);
        checkConvert("complex", con, "(\"Document Title\":retrieval) AND (\"Document Title\":information)");
    }


    @Test
    public void testTestConvertListDefective3() {
        QueryNodeCompare ti1 = new QueryNodeCompare(Field.TITLE, EQ, "retrieval");
        QueryNodeCompare ti2 = new QueryNodeCompare(Field.TITLE, EQ, "information");
        QueryNodeCompare au = new QueryNodeCompare(Field.YEAR, EQ, "2000");
        QueryNodeBool con = new QueryNodeBool();
        con.addChild(ti1);
        con.addChild(ti2);
        con.addChild(au);
        checkConvert("complex", con, "(\"Document Title\":retrieval) AND (\"Document Title\":information)");
    }


    @Test
    public void testTestConvertListDefective4() {
        QueryNodeCompare ti2 = new QueryNodeCompare(Field.TITLE, EQ, "information");
        QueryNodeCompare au = new QueryNodeCompare(Field.YEAR, EQ, "2000");
        QueryNodeBool con = new QueryNodeBool();
        con.addChild(ti2);
        con.addChild(au);
        checkConvert("complex", con, "(\"Document Title\":information)");
    }


    @Test
    public void testTestConvertListDefective5() {
        QueryNodeCompare au = new QueryNodeCompare(Field.YEAR, EQ, "2000");
        QueryNodeCompare ti2 = new QueryNodeCompare(Field.TITLE, EQ, "information");
        QueryNodeBool con = new QueryNodeBool();
        con.addChild(au);
        con.addChild(ti2);
        checkConvert("complex", con, "(\"Document Title\":information)");
    }


    @Test
    public void testTestConvertListDefective6() {
        QueryNodeCompare au = new QueryNodeCompare(Field.YEAR, EQ, "2000");
        QueryNodeBool con = new QueryNodeBool();
        con.addChild(au);
        checkConvert("complex", con, null);
    }


    private void checkConvertBase(String label, Field field, Predicate predicate, String value, String expected) {
        QueryNodeCompare au1 = new QueryNodeCompare(field, predicate, value);
        checkConvert(label, au1, expected);
    }


    private void checkConvert(String label, QueryNode cond, String expected) {
        final Query q = new DefaultQuery(cond);
        final String convert = converter.convert(q);
        getLogger().info(label + ": " + convert);
        Assert.assertEquals(label, expected, convert);
    }

}
