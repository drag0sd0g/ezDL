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

package de.unidue.inf.is.ezdl.dlwrapper.wrappers.cs.citeseer;

import java.util.Calendar;
import java.util.GregorianCalendar;

import junit.framework.Assert;

import org.junit.Test;

import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;
import de.unidue.inf.is.ezdl.dlcore.data.query.DefaultQuery;
import de.unidue.inf.is.ezdl.dlcore.data.query.Query;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNode;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeBool;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeBool.NodeType;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeCompare;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeCompare.Predicate;



/**
 * Test suite for SpringerQueryConverter
 * 
 * @author tacke
 */
public class CiteseerQueryConverterTest {

    CiteseerQueryConverter converter = new CiteseerQueryConverter();

    private static final Predicate EQ = Predicate.EQ;
    private static final Predicate LTE = Predicate.LTE;
    private static final Predicate GTE = Predicate.GTE;


    @Test
    public void testTestConvertBase() {
        checkConvertBase("1", Field.AUTHOR, EQ, "Fuhr", "author:(Fuhr)");
        checkConvertBase("2", Field.TITLE, EQ, "Testttitle", "title:(Testttitle)");
        checkConvertBase("3", Field.TEXT, EQ, "freetext", "abstract:(freetext)");
        checkConvertBase("4", Field.YEAR, EQ, "1990", null);
    }


    @Test
    public void testTestConvertList1() {
        QueryNodeCompare au = new QueryNodeCompare(Field.AUTHOR, EQ, "Fuhr");
        QueryNodeBool con = new QueryNodeBool();
        con.addChild(au);
        checkConvert("complex", con, "author:(Fuhr)");
    }


    @Test
    public void testTestConvertList2() {
        QueryNodeCompare au = new QueryNodeCompare(Field.AUTHOR, EQ, "Fuhr");
        QueryNodeCompare ti = new QueryNodeCompare(Field.TITLE, EQ, "retrieval");
        QueryNodeBool con = new QueryNodeBool();
        con.addChild(au);
        con.addChild(ti);
        checkConvert("complex", con, "author:(Fuhr) AND title:(retrieval)");
    }


    @Test
    public void testTestConvertList3() {
        QueryNodeCompare au = new QueryNodeCompare(Field.AUTHOR, EQ, "Fuhr");
        QueryNodeCompare ti1 = new QueryNodeCompare(Field.TITLE, EQ, "retrieval");
        QueryNodeCompare ti2 = new QueryNodeCompare(Field.TITLE, EQ, "information");
        QueryNodeCompare ti3 = new QueryNodeCompare(Field.YEAR, EQ, "2000");
        QueryNodeBool con = new QueryNodeBool();
        con.addChild(au);
        con.addChild(ti1);
        con.addChild(ti2);
        con.addChild(ti3);
        checkConvert("complex", con, "(author:(Fuhr) AND title:(retrieval) AND title:(information)) AND year:2000");
    }


    @Test
    public void testYearLessThan() {
        QueryNodeBool d1 = new QueryNodeBool(NodeType.OR);
        QueryNodeBool d2 = new QueryNodeBool(NodeType.OR);

        QueryNodeCompare title1 = new QueryNodeCompare(Field.TITLE, EQ, "information");
        QueryNodeCompare title2 = new QueryNodeCompare(Field.TITLE, EQ, "retrieval");
        QueryNodeCompare author1 = new QueryNodeCompare(Field.AUTHOR, EQ, "fuhr");
        QueryNodeCompare author2 = new QueryNodeCompare(Field.AUTHOR, EQ, "belkin");

        d1.addChild(title1);
        d1.addChild(title2);
        d2.addChild(author1);
        d2.addChild(author2);

        QueryNodeBool c = new QueryNodeBool();
        c.addChild(d1);
        c.addChild(d2);

        QueryNodeCompare yearRange = new QueryNodeCompare(Field.YEAR, LTE, "2000");

        c.addChild(yearRange);

        try {
            checkConvert("complex", c,
                            "((title:(information) OR title:(retrieval)) AND (author:(fuhr) OR author:(belkin))) AND year:[1900 TO 2000]");
        }
        catch (NullPointerException e) {
            Assert.fail();
        }
    }


    @Test
    public void testYearGreaterThan() {
        QueryNodeBool d1 = new QueryNodeBool(NodeType.OR);
        QueryNodeBool d2 = new QueryNodeBool(NodeType.OR);

        QueryNodeCompare title1 = new QueryNodeCompare(Field.TITLE, EQ, "information");
        QueryNodeCompare title2 = new QueryNodeCompare(Field.TITLE, EQ, "retrieval");
        QueryNodeCompare author1 = new QueryNodeCompare(Field.AUTHOR, EQ, "fuhr");
        QueryNodeCompare author2 = new QueryNodeCompare(Field.AUTHOR, EQ, "belkin");

        d1.addChild(title1);
        d1.addChild(title2);
        d2.addChild(author1);
        d2.addChild(author2);

        QueryNodeBool c = new QueryNodeBool();
        c.addChild(d1);
        c.addChild(d2);

        QueryNodeCompare yearRange = new QueryNodeCompare(Field.YEAR, GTE, "2000");

        c.addChild(yearRange);

        Calendar cal = new GregorianCalendar();
        int year = cal.get(Calendar.YEAR);

        try {
            checkConvert("complex", c,
                            "((title:(information) OR title:(retrieval)) AND (author:(fuhr) OR author:(belkin))) AND year:[2000 TO "
                                            + Integer.toString(year) + "]");
        }
        catch (NullPointerException e) {
            Assert.fail();
        }
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
        checkConvert("complex", con, "(title:(retrieval) AND title:(information)) AND year:2000");
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
        checkConvert("complex", con, "(title:(retrieval) AND title:(information)) AND year:2000");
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
        checkConvert("complex", con, "(title:(retrieval) AND title:(information)) AND year:2000");
    }


    @Test
    public void testTestConvertListDefective4() {
        QueryNodeCompare ti2 = new QueryNodeCompare(Field.TITLE, EQ, "information");
        QueryNodeCompare au = new QueryNodeCompare(Field.YEAR, EQ, "2000");
        QueryNodeBool con = new QueryNodeBool();
        con.addChild(ti2);
        con.addChild(au);
        checkConvert("complex", con, "(title:(information)) AND year:2000");
    }


    @Test
    public void testTestConvertListDefective5() {
        QueryNodeCompare au = new QueryNodeCompare(Field.YEAR, EQ, "2000");
        QueryNodeCompare ti2 = new QueryNodeCompare(Field.TITLE, EQ, "information");
        QueryNodeBool con = new QueryNodeBool();
        con.addChild(au);
        con.addChild(ti2);
        checkConvert("complex", con, "(title:(information)) AND year:2000");
    }


    private void checkConvertBase(String label, Field field, Predicate predicate, String value, String expected) {
        QueryNodeCompare au1 = new QueryNodeCompare(field, predicate, value);
        checkConvert(label, au1, expected);
    }


    private void checkConvert(String label, QueryNode cond, String expected) {
        Query q = new DefaultQuery(cond);
        Assert.assertEquals(label, expected, converter.convert(q));
    }
}
