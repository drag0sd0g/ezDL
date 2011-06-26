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

package de.unidue.inf.is.ezdl.dlcore.data.query.tree.transformation;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import de.unidue.inf.is.ezdl.dlcore.AbstractTestBase;
import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNode;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeBool;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeBool.NodeType;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeCompare;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeCompare.Predicate;



public class TreeWalkerDNFTransformerTest extends AbstractTestBase {

    private static String AU = Field.AUTHOR.toString();
    private static String TI = Field.TITLE.toString();
    private static String FT = Field.TEXT.toString();
    private static String CI = Field.CITATION.toString();
    private static String YE = Field.YEAR.toString();

    private QueryTreeTransformer transformer;


    @Before
    public void setup() {
        transformer = new TreeWalkerDNFTransformer();
    }


    @Ignore
    @Test
    public void testDNF() {
        QueryNodeBool queryNodeBool1 = new QueryNodeBool(NodeType.OR);
        queryNodeBool1.addChild(new QueryNodeCompare(Field.AUTHOR, Predicate.EQ, "a"));
        queryNodeBool1.addChild(new QueryNodeCompare(Field.TITLE, Predicate.EQ, "b"));

        QueryNodeBool queryNodeBool2 = new QueryNodeBool(NodeType.OR);
        queryNodeBool2.addChild(new QueryNodeCompare(Field.YEAR, Predicate.EQ, "c"));
        queryNodeBool2.addChild(new QueryNodeCompare(Field.AUTHOR, Predicate.EQ, "d"));

        QueryNodeBool queryNodeBool3 = new QueryNodeBool(NodeType.OR);
        queryNodeBool3.addChild(new QueryNodeCompare(Field.CITATION, Predicate.EQ, "e"));
        queryNodeBool3.addChild(new QueryNodeCompare(Field.TEXT, Predicate.EQ, "f"));

        QueryNodeBool queryNodeBool4 = new QueryNodeBool(NodeType.AND);
        queryNodeBool4.addChild(queryNodeBool1);
        queryNodeBool4.addChild(queryNodeBool2);
        queryNodeBool4.addChild(queryNodeBool3);

        Assert.assertEquals("in",
                        "{AND[-2]: {OR[-2]: (1003=a)(4=b)}{OR[-2]: (31=c)(1003=d)}{OR[-2]: (5002=e)(1046=f)}}",
                        queryNodeBool4.toString());
        Assert.assertEquals("out", "((" + CI + "=e AND " + YE + "=c AND " + AU + "=a) OR (" + FT + "=f AND " + YE
                        + "=c AND " + AU + "=a) OR (" + CI + "=e AND " + AU + "=d AND " + AU + "=a) OR (" + FT
                        + "=f AND " + AU + "=d AND " + AU + "=a) OR (" + CI + "=e AND " + YE + "=c AND " + TI
                        + "=b) OR (" + FT + "=f AND " + YE + "=c AND " + TI + "=b) OR (" + CI + "=e AND " + AU
                        + "=d AND " + TI + "=b) OR (" + FT + "=f AND " + AU + "=d AND " + TI + "=b))", transformer
                        .transform(queryNodeBool4).toString());

    }


    @Test
    public void test() {
        QueryNodeBool queryNodeBool1 = new QueryNodeBool(NodeType.OR);
        queryNodeBool1.addChild(new QueryNodeCompare(Field.AUTHOR, Predicate.EQ, "a"));
        queryNodeBool1.addChild(new QueryNodeCompare(Field.TITLE, Predicate.EQ, "b"));

        QueryNodeBool queryNodeBool2 = new QueryNodeBool(NodeType.OR);
        queryNodeBool2.addChild(new QueryNodeCompare(Field.YEAR, Predicate.EQ, "c"));
        queryNodeBool2.addChild(new QueryNodeCompare(Field.AUTHOR, Predicate.EQ, "d"));

        QueryNodeBool queryNodeBool3 = new QueryNodeBool(NodeType.OR);
        queryNodeBool3.addChild(new QueryNodeCompare(Field.CITATION, Predicate.EQ, "e"));
        queryNodeBool3.addChild(new QueryNodeCompare(Field.TEXT, Predicate.EQ, "f"));

        QueryNodeBool queryNodeBool4 = new QueryNodeBool(NodeType.AND);
        queryNodeBool4.addChild(queryNodeBool1);
        queryNodeBool4.addChild(queryNodeBool2);
        queryNodeBool4.addChild(queryNodeBool3);

        getLogger().debug("before: " + queryNodeBool4);

        TreeWalkerDNFTransformer transformer = new TreeWalkerDNFTransformer();
        QueryNode out = transformer.transform(queryNodeBool4);

        getLogger().debug("After: " + out);

    }

}
