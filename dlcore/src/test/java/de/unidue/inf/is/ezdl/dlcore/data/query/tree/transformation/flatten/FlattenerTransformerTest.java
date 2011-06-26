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
package de.unidue.inf.is.ezdl.dlcore.data.query.tree.transformation.flatten;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNode;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeBool;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeBool.NodeType;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeCompare;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeCompare.Predicate;



public class FlattenerTransformerTest {

    private FlattenerTransformer transformer;


    @Before
    public void setUp() throws Exception {
        transformer = new FlattenerTransformer();
    }


    @Test
    public void test1() {
        final QueryNodeCompare c = new QueryNodeCompare(Field.TITLE, "a");
        transformer.transform(c);
        Assert.assertEquals("(4=a)", c.toString());
    }


    /**
     * Tests lonely child situation (e.g. single compare node below boolean
     * node).
     */
    @Test
    public void test2() {
        final QueryNodeCompare c = new QueryNodeCompare(Field.TITLE, "a");
        final QueryNodeBool b = new QueryNodeBool();
        b.addChild(c);
        final QueryNode newRoot = transformer.transform(b);
        Assert.assertEquals("(4=a)", newRoot.toString());
    }


    /**
     * Tests normal AND tree: a AND b.
     */
    @Test
    public void test3() {
        final QueryNodeCompare c1 = new QueryNodeCompare(Field.TITLE, "a");
        final QueryNodeCompare c2 = new QueryNodeCompare(Field.TITLE, "b");
        final QueryNodeBool b = new QueryNodeBool();
        b.addChild(c1);
        b.addChild(c2);
        final QueryNode newRoot = transformer.transform(b);
        Assert.assertEquals("{AND[4]: (4=a)(4=b)}", newRoot.toString());
    }


    /**
     * Tests normal OR tree: a OR b.
     */
    @Test
    public void test4() {
        final QueryNodeBool b = getBoolTree(NodeType.AND);
        final QueryNode newRoot = transformer.transform(b);
        Assert.assertEquals("{AND[4]: (4=a)(4=b)}", newRoot.toString());
    }


    @Test
    public void test5() {
        final QueryNodeBool b = getBoolTree(NodeType.OR);
        final QueryNode newRoot = transformer.transform(b);
        Assert.assertEquals("{OR[4]: (4=a)(4=b)}", newRoot.toString());
    }


    /**
     * Nested AND trees: (a AND b) AND (c AND d).
     */
    @Test
    public void test6a() {
        final QueryNodeBool b1 = getBoolTree(NodeType.AND, "a", "b");
        final QueryNodeBool b2 = getBoolTree(NodeType.AND, "c", "d");
        b1.addChild(b2);
        final QueryNode newRoot = transformer.transform(b1);
        Assert.assertEquals("{AND[4]: (4=a)(4=b)(4=c)(4=d)}", newRoot.toString());
    }


    /**
     * Nested AND trees: a AND (b AND c).
     */
    @Test
    public void test6b() {
        final QueryNodeBool b1 = getBoolTree(NodeType.AND);
        final QueryNodeBool b2 = getBoolTree(NodeType.AND);
        b1.addChild(b2);
        final QueryNode newRoot = transformer.transform(b1);
        Assert.assertEquals("{AND[4]: (4=a)(4=b)}", newRoot.toString());
    }


    /**
     * Nested OR trees: (a OR b) OR (c OR d).
     */
    @Test
    public void test7a() {
        final QueryNodeBool b1 = getBoolTree(NodeType.OR, "a", "b");
        final QueryNodeBool b2 = getBoolTree(NodeType.OR, "c", "d");
        b1.addChild(b2);
        final QueryNode newRoot = transformer.transform(b1);
        Assert.assertEquals("{OR[4]: (4=a)(4=b)(4=c)(4=d)}", newRoot.toString());
    }


    /**
     * Nested OR trees: (a OR b) OR (c OR d).
     */
    @Test
    public void test7b() {
        final QueryNodeBool b1 = getBoolTree(NodeType.OR);
        final QueryNodeBool b2 = getBoolTree(NodeType.OR);
        b1.addChild(b2);
        final QueryNode newRoot = transformer.transform(b1);
        Assert.assertEquals("{OR[4]: (4=a)(4=b)}", newRoot.toString());
    }


    /**
     * Nested mixed trees: a AND (b OR c).
     */
    @Test
    public void test8() {
        final QueryNodeBool b1 = getBoolTree(NodeType.AND);
        final QueryNodeBool b2 = getBoolTree(NodeType.OR);
        b1.addChild(b2);
        final QueryNode newRoot = transformer.transform(b1);
        Assert.assertEquals("{AND[4]: (4=a)(4=b){OR[4]: (4=a)(4=b)}}", newRoot.toString());
    }


    /**
     * Nested mixed trees: a OR (b AND c).
     */
    @Test
    public void test9() {
        final QueryNodeBool b1 = getBoolTree(NodeType.OR);
        final QueryNodeBool b2 = getBoolTree(NodeType.AND);
        b1.addChild(b2);
        final QueryNode newRoot = transformer.transform(b1);
        Assert.assertEquals("{OR[4]: (4=a)(4=b){AND[4]: (4=a)(4=b)}}", newRoot.toString());
    }


    /**
     * Deeply nested mixed trees: (a AND c AND e) OR (a AND c AND f) ...
     */
    @Test
    public void test10() {

        QueryNodeBool or1 = new QueryNodeBool(NodeType.OR);
        or1.addChild(getAndNode("a", "c", "e"));
        or1.addChild(getAndNode("a", "c", "f"));

        QueryNodeBool or2 = new QueryNodeBool(NodeType.OR);
        or2.addChild(getAndNode("a", "d", "e"));
        or2.addChild(getAndNode("a", "d", "f"));

        QueryNodeBool or3 = new QueryNodeBool(NodeType.OR);
        or3.addChild(getAndNode("b", "c", "e"));
        or3.addChild(getAndNode("b", "c", "f"));

        QueryNodeBool or4 = new QueryNodeBool(NodeType.OR);
        or4.addChild(getAndNode("b", "d", "e"));
        or4.addChild(getAndNode("b", "d", "f"));

        QueryNodeBool or12 = new QueryNodeBool(NodeType.OR);
        or12.addChild(or1);
        or12.addChild(or2);

        QueryNodeBool or34 = new QueryNodeBool(NodeType.OR);
        or34.addChild(or3);
        or34.addChild(or4);

        QueryNodeBool or1234 = new QueryNodeBool(NodeType.OR);
        or1234.addChild(or12);
        or1234.addChild(or34);

        final QueryNode newRoot = transformer.transform(or1234);
        Assert.assertEquals("{OR[4]: " //
                        + "{AND[4]: (4=a)(4=c)(4=e)}"//
                        + "{AND[4]: (4=a)(4=c)(4=f)}" //
                        + "{AND[4]: (4=a)(4=d)(4=e)}" //
                        + "{AND[4]: (4=a)(4=d)(4=f)}" //
                        + "{AND[4]: (4=b)(4=c)(4=e)}" //
                        + "{AND[4]: (4=b)(4=c)(4=f)}" //
                        + "{AND[4]: (4=b)(4=d)(4=e)}" //
                        + "{AND[4]: (4=b)(4=d)(4=f)}}", newRoot.toString());
    }


    /**
     * Test with subtrees that contain nodes that are the same as some in
     * supertrees.
     */
    @Test
    public void test11() {

        QueryNodeBool and = new QueryNodeBool(NodeType.AND);
        and.addChild(getAndNode("a", "c", "e"));
        and.addChild(getAndNode("a", "c", "f"));

        final QueryNode newRoot = transformer.transform(and);
        Assert.assertEquals("{AND[4]: (4=a)(4=c)(4=e)(4=f)}", newRoot.toString());
    }


    /**
     * Flattening with negated nodes.
     */
    @Test
    public void test12a() {
        final QueryNodeBool and = new QueryNodeBool(NodeType.AND);
        final QueryNodeCompare a = new QueryNodeCompare(Field.AUTHOR, "a");
        a.setNegated(true);
        final QueryNodeCompare b = new QueryNodeCompare(Field.AUTHOR, "a");
        and.addChild(a);
        and.addChild(b);
        final QueryNode newRoot = transformer.transform(and);

        Assert.assertEquals("", "{AND[1003]: (NOT 1003=a)(1003=a)}", newRoot.toString());
    }


    /**
     * Flattening with negated nodes.
     * <p>
     * Testing the situation that occurs when a query is dropped into a query
     * field and combined with the existing query.
     * <p>
     * a AND (NAND a)
     */
    @Test
    public void test12b() {
        final QueryNodeBool and = new QueryNodeBool(NodeType.AND);

        final QueryNodeCompare a = new QueryNodeCompare(Field.AUTHOR, "a");
        and.addChild(a);

        final QueryNodeBool and2 = new QueryNodeBool(NodeType.AND);
        and2.setNegated(true);

        final QueryNodeCompare b = new QueryNodeCompare(Field.AUTHOR, "a");
        and2.addChild(b);
        and.addChild(and2);

        System.out.println(and);
        final QueryNode newRoot = transformer.transform(and);

        Assert.assertEquals("", "{AND[1003]: (1003=a)(NOT 1003=a)}", newRoot.toString());
    }


    /**
     * Flattening with negated nodes.
     * <p>
     * Testing the situation that occurs when a query is dropped into a query
     * field and combined with the existing query.
     * <p>
     * a AND (NAND NOT a)
     */
    @Test
    public void test12c() {
        final QueryNodeBool and = new QueryNodeBool(NodeType.AND);

        final QueryNodeCompare a = new QueryNodeCompare(Field.AUTHOR, "a");
        and.addChild(a);

        final QueryNodeBool and2 = new QueryNodeBool(NodeType.AND);
        and2.setNegated(true);

        final QueryNodeCompare b = new QueryNodeCompare(Field.AUTHOR, "a");
        b.setNegated(true);
        and2.addChild(b);
        and.addChild(and2);

        System.out.println(and);
        final QueryNode newRoot = transformer.transform(and);

        Assert.assertEquals("", "{AND[1003]: (1003=a)}", newRoot.toString());
    }


    private QueryNodeBool getAndNode(String a1, String a2, String a3) {
        QueryNodeBool queryNodeBool1 = new QueryNodeBool(NodeType.AND);
        queryNodeBool1.addChild(new QueryNodeCompare(Field.TITLE, Predicate.EQ, a1));
        queryNodeBool1.addChild(new QueryNodeCompare(Field.TITLE, Predicate.EQ, a2));
        queryNodeBool1.addChild(new QueryNodeCompare(Field.TITLE, Predicate.EQ, a3));
        return queryNodeBool1;
    }


    private QueryNodeBool getBoolTree(NodeType type) {
        final QueryNodeCompare c1 = new QueryNodeCompare(Field.TITLE, "a");
        final QueryNodeCompare c2 = new QueryNodeCompare(Field.TITLE, "b");
        final QueryNodeBool b = new QueryNodeBool(type);
        b.addChild(c1);
        b.addChild(c2);
        return b;
    }


    private QueryNodeBool getBoolTree(NodeType type, String a, String b) {
        final QueryNodeCompare c1 = new QueryNodeCompare(Field.TITLE, a);
        final QueryNodeCompare c2 = new QueryNodeCompare(Field.TITLE, b);
        final QueryNodeBool bn = new QueryNodeBool(type);
        bn.addChild(c1);
        bn.addChild(c2);
        return bn;
    }

}
