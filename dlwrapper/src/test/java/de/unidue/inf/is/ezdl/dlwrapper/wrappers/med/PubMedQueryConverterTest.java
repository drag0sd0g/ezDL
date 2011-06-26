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

package de.unidue.inf.is.ezdl.dlwrapper.wrappers.med;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;
import de.unidue.inf.is.ezdl.dlcore.data.query.DefaultQuery;
import de.unidue.inf.is.ezdl.dlcore.data.query.Query;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeBool;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeBool.NodeType;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeCompare;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeCompare.Predicate;



public class PubMedQueryConverterTest {

    private static final Predicate EQ = Predicate.EQ;

    private PubMedQueryConverter converter;


    @Before
    public void setUp() throws Exception {
        converter = new PubMedQueryConverter();
    }


    @Test
    public void test1() {
        QueryNodeCompare au = new QueryNodeCompare(Field.AUTHOR, EQ, "Fuhr");
        QueryNodeBool con = new QueryNodeBool();
        con.addChild(au);
        checkConvert("complex", con, "Fuhr[AUTHOR]");
    }


    @Test
    public void test2() {
        QueryNodeCompare au = new QueryNodeCompare(Field.TITLE, EQ, "coffee");
        QueryNodeBool con = new QueryNodeBool();
        con.addChild(au);
        checkConvert("complex", con, "coffee[TITL]");
    }


    @Test
    public void test3() {
        QueryNodeCompare au = new QueryNodeCompare(Field.TITLE, EQ, "coffee");
        QueryNodeCompare ti = new QueryNodeCompare(Field.AUTHOR, EQ, "Fuhr");
        QueryNodeBool con = new QueryNodeBool();
        con.addChild(au);
        con.addChild(ti);
        checkConvert("complex", con, "coffee[TITL] AND Fuhr[AUTHOR]");
    }


    @Test
    public void test4() {
        QueryNodeCompare au = new QueryNodeCompare(Field.TITLE, EQ, "coffee");
        QueryNodeCompare ti = new QueryNodeCompare(Field.AUTHOR, EQ, "Smith");
        QueryNodeBool or = new QueryNodeBool(NodeType.OR);
        or.addChild(au);
        or.addChild(ti);

        QueryNodeCompare ti2 = new QueryNodeCompare(Field.TITLE, EQ, "cancer");

        QueryNodeBool and = new QueryNodeBool();
        and.addChild(or);
        and.addChild(ti2);

        checkConvert("complex", and, "(coffee[TITL] OR Smith[AUTHOR]) AND cancer[TITL]");
    }


    private void checkConvert(String label, QueryNodeBool cond, String expected) {
        final Query q = new DefaultQuery(cond);
        Assert.assertEquals(label, expected, converter.convert(q));
    }

}
