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

package de.unidue.inf.is.ezdl.dlwrapper.wrappers.cs.acm;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;
import de.unidue.inf.is.ezdl.dlcore.data.query.DefaultQuery;
import de.unidue.inf.is.ezdl.dlcore.data.query.Query;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNode;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeBool;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeBool.NodeType;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeCompare;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeCompare.Predicate;
import de.unidue.inf.is.ezdl.dlcore.query.QueryConverterTestBase;



/**
 * Test for the converter for the ACM DL advanced search.
 * 
 * @author mjordan
 */
public class ACMAdvancedQueryConverterTest extends QueryConverterTestBase {

    private static final Predicate EQ = Predicate.EQ;


    @Before
    public void setup() {
        converter = new ACMAdvancedQueryConverter();
    }


    @Test
    public void testTestConvertBase() {
        checkConvertBase("1a", Field.AUTHOR, EQ, "Fuhr", "(Author:Fuhr)");
        checkConvertBase("1b", Field.AUTHOR, EQ, "N Fuhr", "(Author:\"N Fuhr\")");
        checkConvertBase("1c", Field.AUTHOR, EQ, "Norbert Fuhr", "(Author:\"Norbert Fuhr\")");
        checkConvertBase("2", Field.TITLE, EQ, "Testttitle", "(Title:Testttitle)");
        checkConvertBase("3", Field.TEXT, EQ, "freetext", "(Abstract:freetext)");
        checkConvertBase("4", Field.YEAR, EQ, "1990", null);
        checkConvertBase("5", Field.AUTHOR, EQ, "Cinnéide", "(Author:Cinnéide)");
    }


    @Test
    public void testTestConvertList1() {
        QueryNodeCompare au = new QueryNodeCompare(Field.AUTHOR, EQ, "Fuhr");
        checkConvert("complex", au, "(Author:Fuhr)");
    }


    @Test
    public void testTestConvertList2() {
        QueryNodeCompare au = new QueryNodeCompare(Field.AUTHOR, EQ, "Fuhr");
        QueryNodeCompare ti = new QueryNodeCompare(Field.TITLE, EQ, "retrieval");
        QueryNodeBool con = new QueryNodeBool();
        con.addChild(au);
        con.addChild(ti);
        checkConvert("complex", con, "(Author:Fuhr) AND (Title:retrieval)");
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
        checkConvert("complex", con, "(Author:Fuhr) AND (Title:retrieval) AND (Title:information)");
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
        checkConvert("complex", dis, "((Author:Fuhr) AND (Title:retrieval)) OR (Title:information)");
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
        checkConvert("complex", con, "(Title:retrieval) AND (Title:information)");
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
        checkConvert("complex", con, "(Title:retrieval) AND (Title:information)");
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
        checkConvert("complex", con, "(Title:retrieval) AND (Title:information)");
    }


    @Test
    public void testTestConvertListDefective4() {
        QueryNodeCompare ti2 = new QueryNodeCompare(Field.TITLE, EQ, "information");
        QueryNodeCompare au = new QueryNodeCompare(Field.YEAR, EQ, "2000");
        QueryNodeBool con = new QueryNodeBool();
        con.addChild(ti2);
        con.addChild(au);
        checkConvert("complex", con, "(Title:information)");
    }


    @Test
    public void testTestConvertListDefective5() {
        QueryNodeCompare au = new QueryNodeCompare(Field.YEAR, EQ, "2000");
        QueryNodeCompare ti2 = new QueryNodeCompare(Field.TITLE, EQ, "information");
        QueryNodeBool con = new QueryNodeBool();
        con.addChild(au);
        con.addChild(ti2);
        checkConvert("complex", con, "(Title:information)");
    }


    @Test
    public void testTestConvertListDefective6() {
        QueryNodeCompare au = new QueryNodeCompare(Field.YEAR, EQ, "2000");
        QueryNodeBool con = new QueryNodeBool();
        con.addChild(au);
        checkConvert("complex", con, null);
    }


    @Test
    public void testTestConvertYear() {
        QueryNodeCompare au = new QueryNodeCompare(Field.YEAR, EQ, "2000");
        checkConvert("complex", au, null);
    }


    private void checkConvertBase(String label, Field field, Predicate predicate, String value, String expected) {
        QueryNodeCompare au1 = new QueryNodeCompare(field, predicate, value);
        checkConvert(label, au1, expected);
    }


    private void checkConvert(String label, QueryNode cond, String expected) {
        Query q = new DefaultQuery(cond);
        Assert.assertEquals(label, expected, converter.convert(q));
    }


    @Override
    protected AllFeaturesConfig getAllFeaturesQueryConfig() {
        AllFeaturesConfig config = super.getAllFeaturesQueryConfig();
        config.field2 = Field.AUTHOR;
        config.queryStr = "((Title:information) OR (not Title:retrieval)) AND (Author:term1) AND (Author:term2) AND (Title:abcde)";
        return config;
    }

}
