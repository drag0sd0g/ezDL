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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.unidue.inf.is.ezdl.dlcore.AbstractTestBase;
import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;
import de.unidue.inf.is.ezdl.dlcore.data.query.DefaultQuery;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeBool;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeBool.NodeType;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeCompare;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeCompare.Predicate;



/**
 * Tests the {@link TermsOnlyQueryConverter}.
 */
public class TermsOnlyQueryConverterTest extends AbstractTestBase {

    private QueryConverter queryConverter;


    @Before
    public void init() {
        queryConverter = new TermsOnlyQueryConverter();
    }


    /**
     * Constructs a query and tests if the converted form is consistent with the
     * expectations.
     */
    @Test
    public void testConvert1() {
        QueryNodeBool queryNodeBool1 = new QueryNodeBool(NodeType.OR);
        queryNodeBool1.addChild(new QueryNodeCompare(Field.AUTHOR, Predicate.EQ, "a"));
        queryNodeBool1.addChild(new QueryNodeCompare(Field.TITLE, Predicate.LTE, "b"));

        QueryNodeBool queryNodeBool2 = new QueryNodeBool(NodeType.OR);
        queryNodeBool2.addChild(new QueryNodeCompare(Field.YEAR, Predicate.LT, "c"));
        queryNodeBool2.addChild(new QueryNodeCompare(Field.AUTHOR, Predicate.GT, "d"));

        QueryNodeBool queryNodeBool3 = new QueryNodeBool(NodeType.OR);
        queryNodeBool3.addChild(new QueryNodeCompare(Field.CITATION, Predicate.EQ, "?-e+"));
        queryNodeBool3.addChild(new QueryNodeCompare(Field.TEXT, Predicate.GTE, "\\*(f)"));

        QueryNodeBool queryNodeBool4 = new QueryNodeBool(NodeType.AND);
        queryNodeBool4.addChild(queryNodeBool1);
        queryNodeBool4.addChild(queryNodeBool2);
        queryNodeBool4.addChild(queryNodeBool3);

        Assert.assertEquals("a b c d ?-e+ \\*(f)", queryConverter.convert(new DefaultQuery(queryNodeBool4)));
    }


    /**
     * Tests behavior in presence of multiple nodes with the same content.
     */
    @Test
    public void testConvert2() {
        QueryNodeBool queryNodeBool1 = new QueryNodeBool(NodeType.OR);
        queryNodeBool1.addChild(new QueryNodeCompare(Field.AUTHOR, Predicate.EQ, "a"));
        queryNodeBool1.addChild(new QueryNodeCompare(Field.TITLE, Predicate.LTE, "b"));

        QueryNodeBool queryNodeBool2 = new QueryNodeBool(NodeType.OR);
        queryNodeBool2.addChild(new QueryNodeCompare(Field.YEAR, Predicate.LT, "a"));
        queryNodeBool2.addChild(new QueryNodeCompare(Field.AUTHOR, Predicate.GT, "d"));

        QueryNodeBool queryNodeBool3 = new QueryNodeBool(NodeType.OR);
        queryNodeBool3.addChild(new QueryNodeCompare(Field.CITATION, Predicate.EQ, "b"));
        queryNodeBool3.addChild(new QueryNodeCompare(Field.TEXT, Predicate.GTE, "\\*(f)"));

        QueryNodeBool queryNodeBool4 = new QueryNodeBool(NodeType.AND);
        queryNodeBool4.addChild(queryNodeBool1);
        queryNodeBool4.addChild(queryNodeBool2);
        queryNodeBool4.addChild(queryNodeBool3);

        Assert.assertEquals("a b a d b \\*(f)", queryConverter.convert(new DefaultQuery(queryNodeBool4)));
    }
}
