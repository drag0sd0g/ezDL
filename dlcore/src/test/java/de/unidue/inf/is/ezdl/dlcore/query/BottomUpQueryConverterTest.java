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

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;
import de.unidue.inf.is.ezdl.dlcore.data.query.Query;
import de.unidue.inf.is.ezdl.dlcore.data.query.QueryFactory;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeProximity;



public class BottomUpQueryConverterTest extends QueryConverterTestBase {

    private class SampleBUQC extends AbstractBottomUpQueryConverter {

        @Override
        protected String convertProximity(QueryNodeProximity node) {
            return node.getFieldCode() + ":" + Arrays.asList(node.getTerms()) + "/" + node.getMaxDistance();
        }


        private String neg(boolean neg) {
            return neg ? "NOT " : "";
        }


        private String fieldQuery(String p, boolean negated, Field fieldCode, String fieldValue) {
            return neg(negated) + fieldCode + p + fieldValue;
        }


        @Override
        protected String fieldQueryEQ(boolean negated, Field fieldCode, String fieldValue) {
            return fieldQuery("=", negated, fieldCode, fieldValue);
        }


        @Override
        protected String fieldQueryGT(boolean negated, Field fieldCode, String fieldValue) {
            return fieldQuery(">", negated, fieldCode, fieldValue);
        }


        @Override
        protected String fieldQueryGTE(boolean negated, Field fieldCode, String fieldValue) {
            return fieldQuery(">=", negated, fieldCode, fieldValue);
        }


        @Override
        protected String fieldQueryLT(boolean negated, Field fieldCode, String fieldValue) {
            return fieldQuery("<", negated, fieldCode, fieldValue);
        }


        @Override
        protected String fieldQueryLTE(boolean negated, Field fieldCode, String fieldValue) {
            return fieldQuery("<=", negated, fieldCode, fieldValue);
        }


        @Override
        protected String wildcardMulti() {
            return "#";
        }


        @Override
        protected String wildcardSingle() {
            return "$";
        }


        @Override
        protected String escapeToken(String token) {
            return token.toLowerCase();
        }


        @Override
        protected String andOperator() {
            return " AND ";
        }


        @Override
        protected String orOperator() {
            return " OR ";
        }


        @Override
        protected String parenthesisOpen() {
            return "(";
        }


        @Override
        protected String parenthesisClosed() {
            return ")";
        }

    }


    private class YearNotSupportedConverter extends SampleBUQC {

        @Override
        protected String fieldQueryEQ(boolean negated, Field fieldCode, String fieldValue) {
            if (fieldCode == Field.YEAR) {
                return null;
            }
            return super.fieldQueryEQ(negated, fieldCode, fieldValue);
        }
    }


    @Before
    public void setup() {
        converter = new SampleBUQC();
    }


    @Test
    public void testConvert() {
        final Query q = QueryFactory.getTitleAndYearQuery(2000).getQuery();
        final String queryStr = converter.convert(q);
        Assert.assertEquals("query: " + q, "(4=information OR 4=retrieval) AND 31=2000", queryStr);
    }


    /**
     * Tests conversion using a converter that does not support
     * {@link Field#YEAR}.
     */
    @Test
    public void testConvertYearUnsupported1() {
        converter = new YearNotSupportedConverter();
        final Query q = QueryFactory.getTitleAndYearQuery(2000).getQuery();
        final String queryStr = converter.convert(q);
        Assert.assertEquals("query: " + q, "(4=information OR 4=retrieval)", queryStr);
    }


    /**
     * Tests conversion using a converter that does not support
     * {@link Field#YEAR} on a query that has only the unsupported nodes.
     */
    @Test
    public void testConvertYearUnsupported2() {
        converter = new YearNotSupportedConverter();
        final Query q = QueryFactory.getYearQuery(2000).getQuery();
        final String queryStr = converter.convert(q);
        Assert.assertEquals("query: " + q, null, queryStr);
    }

}
