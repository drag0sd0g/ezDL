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

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeBool;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeBool.NodeType;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeCompare;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeCompare.Predicate;
import de.unidue.inf.is.ezdl.dlcore.query.YearRangeConverter.YearRange;



/**
 * Tests the {@link YearRangeConverter}.
 * 
 * @author mjordan
 */
public class YearRangeConverterTest {

    YearRangeConverter converter;


    @Before
    public void setUp() throws Exception {
        converter = new YearRangeConverter();
    }


    @Test
    public void testConvertYearRange1() {
        QueryNodeCompare queryNodeCompare = new QueryNodeCompare(Field.YEAR, Predicate.GT, "2000");
        YearRange range = converter.convertYearRange(queryNodeCompare);
        YearRange expected = converter.new YearRange();
        expected.minYear = 2001;
        Assert.assertEquals(expected, range);
    }


    @Test
    public void testConvertYearRangeBug1() {
        QueryNodeCompare queryNodeCompare = new QueryNodeCompare(Field.YEAR, Predicate.GT, "");
        YearRange range = converter.convertYearRange(queryNodeCompare);
        YearRange expected = converter.new YearRange();
        expected.minYear = null;
        Assert.assertEquals(expected, range);
    }


    @Test
    public void testConvertYearRange2() {
        QueryNodeCompare queryNodeCompare = new QueryNodeCompare(Field.YEAR, Predicate.GTE, "2000");
        YearRange range = converter.convertYearRange(queryNodeCompare);
        YearRange expected = converter.new YearRange();
        expected.minYear = 2000;
        Assert.assertEquals(expected, range);
    }


    @Test
    public void testConvertYearRange3() {
        QueryNodeCompare queryNodeCompare = new QueryNodeCompare(Field.YEAR, Predicate.LT, "2000");
        YearRange range = converter.convertYearRange(queryNodeCompare);
        YearRange expected = converter.new YearRange();
        expected.maxYear = 1999;
        Assert.assertEquals(expected, range);
    }


    @Test
    public void testConvertYearRange4() {
        QueryNodeCompare queryNodeCompare = new QueryNodeCompare(Field.YEAR, Predicate.LTE, "2000");
        YearRange range = converter.convertYearRange(queryNodeCompare);
        YearRange expected = converter.new YearRange();
        expected.maxYear = 2000;
        Assert.assertEquals(expected, range);
    }


    @Test
    public void testConvertYearRange5() {
        QueryNodeCompare queryNodeCompare = new QueryNodeCompare(Field.YEAR, Predicate.EQ, "2000");
        YearRange range = converter.convertYearRange(queryNodeCompare);
        YearRange expected = converter.new YearRange();
        expected.minYear = 2000;
        expected.maxYear = 2000;
        Assert.assertEquals(expected, range);
    }


    @Test
    public void testConvertYearRange6() {
        QueryNodeBool qnb = getMinMaxConditionGTELTE("2000", "3000");
        YearRange range = converter.convertYearRange(qnb);
        YearRange expected = converter.new YearRange();
        expected.minYear = 2000;
        expected.maxYear = 3000;
        Assert.assertEquals(expected, range);
    }


    private QueryNodeBool getMinMaxConditionGTELTE(String min, String max) {
        QueryNodeCompare queryNodeCompare1 = new QueryNodeCompare(Field.YEAR, Predicate.GTE, min);
        QueryNodeCompare queryNodeCompare2 = new QueryNodeCompare(Field.YEAR, Predicate.LTE, max);
        QueryNodeBool queryNodeBool = new QueryNodeBool(NodeType.OR);
        queryNodeBool.addChild(queryNodeCompare1);
        queryNodeBool.addChild(queryNodeCompare2);
        return queryNodeBool;
    }


    @Test
    public void testConvertYearRange7() {
        QueryNodeBool dis = getMinMaxConditionGTLT("2000", "3000");
        YearRange range = converter.convertYearRange(dis);
        YearRange expected = converter.new YearRange();
        expected.minYear = 2001;
        expected.maxYear = 2999;
        Assert.assertEquals(expected, range);
    }


    private QueryNodeBool getMinMaxConditionGTLT(String min, String max) {
        QueryNodeCompare queryNodeCompare1 = new QueryNodeCompare(Field.YEAR, Predicate.GT, min);
        QueryNodeCompare queryNodeCompare2 = new QueryNodeCompare(Field.YEAR, Predicate.LT, max);
        QueryNodeBool queryNodeBool = new QueryNodeBool(NodeType.OR);
        queryNodeBool.addChild(queryNodeCompare1);
        queryNodeBool.addChild(queryNodeCompare2);
        return queryNodeBool;
    }


    @Test
    public void testConvertYearRange8() {
        QueryNodeBool qnb1 = getMinMaxConditionGTLT("2000", "3000");
        QueryNodeBool qnb2 = getMinMaxConditionGTELTE("2000", "3000");
        QueryNodeBool qnb3 = new QueryNodeBool(NodeType.AND);
        qnb3.addChild(qnb1);
        qnb3.addChild(qnb2);
        YearRange range = converter.convertYearRange(qnb3);
        YearRange expected = converter.new YearRange();
        expected.minYear = 2000;
        expected.maxYear = 3000;
        Assert.assertEquals(expected, range);
    }


    @Test
    public void testConvertYearRange9() {
        QueryNodeBool qnb1 = getMinMaxConditionGTLT("1000", "4000");
        QueryNodeBool qnb2 = getMinMaxConditionGTELTE("2000", "3000");
        QueryNodeBool qnb3 = new QueryNodeBool(NodeType.AND);
        qnb3.addChild(qnb1);
        qnb3.addChild(qnb2);
        YearRange range = converter.convertYearRange(qnb3);
        YearRange expected = converter.new YearRange();
        expected.minYear = 1001;
        expected.maxYear = 3999;
        Assert.assertEquals(expected, range);
    }


    @Test
    public void testConvertYearRangeNone() {
        YearRange range = converter.convertYearRange(new QueryNodeCompare(Field.AUTHOR, Predicate.EQ, "Jordan"));
        YearRange expected = converter.new YearRange();
        expected.minYear = null;
        expected.maxYear = null;
        Assert.assertEquals(expected, range);
    }

}
