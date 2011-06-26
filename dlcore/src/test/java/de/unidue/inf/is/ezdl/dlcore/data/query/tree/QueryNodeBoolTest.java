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
package de.unidue.inf.is.ezdl.dlcore.data.query.tree;

import junit.framework.Assert;

import org.junit.Test;

import de.unidue.inf.is.ezdl.dlcore.AbstractTestBase;
import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeBool.NodeType;



/**
 * Tests {@link QueryNodeCompare}.
 * 
 * @author mjordan
 */
public class QueryNodeBoolTest extends AbstractTestBase {

    @Test
    public void testConstructor1() {
        final QueryNodeBool bool = new QueryNodeBool();
        Assert.assertEquals("field code", Field.FIELDCODE_NONE, bool.getFieldCode());
        Assert.assertEquals("type", NodeType.AND, bool.getType());
    }


    @Test
    public void testConstructor2a() {
        final QueryNodeBool bool = new QueryNodeBool(NodeType.OR);
        Assert.assertEquals("field code", Field.FIELDCODE_NONE, bool.getFieldCode());
        Assert.assertEquals("type", NodeType.OR, bool.getType());
    }


    @Test
    public void testConstructor2b() {
        final QueryNodeBool bool = new QueryNodeBool(NodeType.AND);
        Assert.assertEquals("field code", Field.FIELDCODE_NONE, bool.getFieldCode());
        Assert.assertEquals("type", NodeType.AND, bool.getType());
    }


    @Test
    public void testAddChild1() {
        final QueryNodeBool bool = new QueryNodeBool(NodeType.AND);
        Assert.assertEquals(0, bool.childCount());
        Assert.assertEquals("field code 1", Field.FIELDCODE_NONE, bool.getFieldCode());
        final QueryNodeCompare comp1 = new QueryNodeCompare(Field.TITLE, "a");
        bool.addChild(comp1);
        Assert.assertEquals(1, bool.childCount());
        Assert.assertEquals("field code 2", Field.TITLE, bool.getFieldCode());
        final QueryNodeCompare comp2 = new QueryNodeCompare(Field.TITLE, "b");
        bool.addChild(comp2);
        Assert.assertEquals(2, bool.childCount());
        Assert.assertEquals("field code 3", Field.TITLE, bool.getFieldCode());
        final QueryNodeCompare comp3 = new QueryNodeCompare(Field.TITLE, "c");
        bool.addChild(comp3);
        Assert.assertEquals(3, bool.childCount());
        Assert.assertEquals("[(4=a), (4=b), (4=c)]", bool.children().toString());
        Assert.assertEquals("field code 4", Field.TITLE, bool.getFieldCode());
    }


    @Test
    public void testAddChild2() {
        final QueryNodeBool bool = new QueryNodeBool(NodeType.AND);
        Assert.assertEquals(0, bool.childCount());
        Assert.assertEquals("field code 1", Field.FIELDCODE_NONE, bool.getFieldCode());
        final QueryNodeCompare comp1 = new QueryNodeCompare(Field.TITLE, "a");
        bool.addChild(comp1);
        Assert.assertEquals(1, bool.childCount());
        Assert.assertEquals("field code 2", Field.TITLE, bool.getFieldCode());
        final QueryNodeCompare comp2 = new QueryNodeCompare(Field.AUTHOR, "b");
        bool.addChild(comp2);
        Assert.assertEquals(2, bool.childCount());
        Assert.assertEquals("field code 3", Field.FIELDCODE_MIXED, bool.getFieldCode());
        final QueryNodeCompare comp3 = new QueryNodeCompare(Field.TITLE, "c");
        bool.addChild(comp3);
        Assert.assertEquals(3, bool.childCount());
        Assert.assertEquals("[(4=a), (1003=b), (4=c)]", bool.children().toString());
        Assert.assertEquals("field code 4", Field.FIELDCODE_MIXED, bool.getFieldCode());
    }


    @Test
    public void testRemoveChild1() {
        final QueryNodeBool bool = new QueryNodeBool(NodeType.AND);
        final QueryNodeCompare comp1 = new QueryNodeCompare(Field.TITLE, "a");
        bool.addChild(comp1);
        final QueryNodeCompare comp2 = new QueryNodeCompare(Field.AUTHOR, "b");
        bool.addChild(comp2);
        final QueryNodeCompare comp3 = new QueryNodeCompare(Field.TITLE, "c");
        bool.addChild(comp3);
        bool.removeChild(comp1);
        Assert.assertEquals(2, bool.childCount());
        Assert.assertEquals("[(1003=b), (4=c)]", bool.children().toString());
        Assert.assertEquals("field code 1", Field.FIELDCODE_MIXED, bool.getFieldCode());
    }


    @Test
    public void testRemoveChild2() {
        final QueryNodeBool bool = new QueryNodeBool(NodeType.AND);
        final QueryNodeCompare comp1 = new QueryNodeCompare(Field.TITLE, "a");
        bool.addChild(comp1);
        final QueryNodeCompare comp2 = new QueryNodeCompare(Field.AUTHOR, "b");
        bool.addChild(comp2);
        final QueryNodeCompare comp3 = new QueryNodeCompare(Field.TITLE, "c");
        bool.addChild(comp3);
        bool.removeChild(comp2);
        Assert.assertEquals(2, bool.childCount());
        Assert.assertEquals("[(4=a), (4=c)]", bool.children().toString());
        Assert.assertEquals("field code 1", Field.TITLE, bool.getFieldCode());
    }


    @Test
    public void testRemoveChild3() {
        final QueryNodeBool bool = new QueryNodeBool(NodeType.AND);
        final QueryNodeCompare comp1 = new QueryNodeCompare(Field.TITLE, "a");
        bool.addChild(comp1);
        final QueryNodeCompare comp2 = new QueryNodeCompare(Field.AUTHOR, "b");
        bool.addChild(comp2);
        final QueryNodeCompare comp3 = new QueryNodeCompare(Field.TITLE, "c");
        bool.addChild(comp3);
        bool.removeChild(comp3);
        Assert.assertEquals(2, bool.childCount());
        Assert.assertEquals("[(4=a), (1003=b)]", bool.children().toString());
        Assert.assertEquals("field code 1", Field.FIELDCODE_MIXED, bool.getFieldCode());
    }


    @Test
    public void testRemoveChild4() {
        final QueryNodeBool bool = new QueryNodeBool(NodeType.AND);
        final QueryNodeCompare comp1 = new QueryNodeCompare(Field.TITLE, "a");
        bool.addChild(comp1);
        final QueryNodeCompare comp2 = new QueryNodeCompare(Field.AUTHOR, "b");
        bool.addChild(comp2);
        final QueryNodeCompare comp3 = new QueryNodeCompare(Field.TITLE, "c");
        bool.addChild(comp3);
        bool.removeChild(comp3);
        bool.removeChild(comp1);
        Assert.assertEquals(1, bool.childCount());
        Assert.assertEquals("[(1003=b)]", bool.children().toString());
        Assert.assertEquals("field code 1", Field.AUTHOR, bool.getFieldCode());
        bool.removeChild(comp2);
        Assert.assertEquals("[]", bool.children().toString());
        Assert.assertEquals("field code 2", Field.FIELDCODE_NONE, bool.getFieldCode());
    }


    @Test
    public void testSetChildField1() {
        final QueryNodeBool bool = new QueryNodeBool(NodeType.AND);
        final QueryNodeCompare comp1 = new QueryNodeCompare(Field.TITLE, "a");
        bool.addChild(comp1);
        final QueryNodeCompare comp2 = new QueryNodeCompare(Field.AUTHOR, "b");
        bool.addChild(comp2);
        final QueryNodeCompare comp3 = new QueryNodeCompare(Field.TITLE, "c");
        bool.addChild(comp3);
        Assert.assertEquals("field code 1", Field.FIELDCODE_MIXED, bool.getFieldCode());
        Assert.assertEquals(3, bool.childCount());
        Assert.assertEquals("[(4=a), (1003=b), (4=c)]", bool.children().toString());

        comp1.setFieldCode(Field.ABSTRACT);
        Assert.assertEquals("field code 1", Field.FIELDCODE_MIXED, bool.getFieldCode());
        comp2.setFieldCode(Field.ABSTRACT);
        Assert.assertEquals("field code 1", Field.FIELDCODE_MIXED, bool.getFieldCode());
        comp3.setFieldCode(Field.ABSTRACT);
        Assert.assertEquals("field code 1", Field.ABSTRACT, bool.getFieldCode());

        comp2.setFieldCode(Field.AUTHOR);
        Assert.assertEquals("field code 1", Field.FIELDCODE_MIXED, bool.getFieldCode());
    }


    @Test
    public void testSetChildField2() {
        final QueryNodeBool root = new QueryNodeBool(NodeType.OR);

        final QueryNodeBool bool1 = new QueryNodeBool(NodeType.AND);
        final QueryNodeCompare comp11 = new QueryNodeCompare(Field.TITLE, "a");
        bool1.addChild(comp11);
        final QueryNodeCompare comp12 = new QueryNodeCompare(Field.AUTHOR, "b");
        bool1.addChild(comp12);
        final QueryNodeCompare comp13 = new QueryNodeCompare(Field.TITLE, "c");
        bool1.addChild(comp13);

        Assert.assertEquals("field code 1", Field.FIELDCODE_MIXED, bool1.getFieldCode());
        Assert.assertEquals(3, bool1.childCount());
        Assert.assertEquals("[(4=a), (1003=b), (4=c)]", bool1.children().toString());

        final QueryNodeBool bool2 = new QueryNodeBool(NodeType.AND);
        final QueryNodeCompare comp21 = new QueryNodeCompare(Field.TITLE, "aa");
        bool2.addChild(comp21);
        final QueryNodeCompare comp22 = new QueryNodeCompare(Field.TITLE, "bb");
        bool2.addChild(comp22);
        final QueryNodeCompare comp23 = new QueryNodeCompare(Field.TITLE, "cc");
        bool2.addChild(comp23);

        Assert.assertEquals("field code 2", Field.TITLE, bool2.getFieldCode());
        Assert.assertEquals(3, bool2.childCount());
        Assert.assertEquals("[(4=aa), (4=bb), (4=cc)]", bool2.children().toString());

        root.addChild(bool1);
        root.addChild(bool2);

        Assert.assertEquals("field code 3", Field.FIELDCODE_MIXED, root.getFieldCode());
        Assert.assertEquals(2, root.childCount());
        Assert.assertEquals("[{AND[-2]: (4=a)(1003=b)(4=c)}, {AND[4]: (4=aa)(4=bb)(4=cc)}]", root.children().toString());

        Assert.assertEquals("field code 11", Field.TITLE, comp11.getFieldCode());
        Assert.assertEquals("field code 12", Field.AUTHOR, comp12.getFieldCode());
        Assert.assertEquals("field code 13", Field.TITLE, comp13.getFieldCode());
        Assert.assertEquals("field code 21", Field.TITLE, comp21.getFieldCode());
        Assert.assertEquals("field code 22", Field.TITLE, comp22.getFieldCode());
        Assert.assertEquals("field code 23", Field.TITLE, comp23.getFieldCode());

        Assert.assertEquals("field code a1", Field.FIELDCODE_MIXED, bool1.getFieldCode());
        Assert.assertEquals("field code a2", Field.TITLE, bool2.getFieldCode());

        comp12.setFieldCode(Field.TITLE);
        Assert.assertEquals("field code b1", Field.TITLE, comp12.getFieldCode());
        Assert.assertEquals("field code b2", Field.TITLE, bool1.getFieldCode());
        Assert.assertEquals("field code b3", Field.TITLE, root.getFieldCode());

        comp12.setFieldCode(Field.ABSTRACT);
        Assert.assertEquals("field code c1", Field.ABSTRACT, comp12.getFieldCode());
        Assert.assertEquals("field code c2a", Field.FIELDCODE_MIXED, bool1.getFieldCode());
        Assert.assertEquals("field code c2b", Field.TITLE, bool2.getFieldCode());
        Assert.assertEquals("field code c3", Field.FIELDCODE_MIXED, root.getFieldCode());
    }


    @Test
    public void testReplaceChild1() {
        final QueryNodeBool root = new QueryNodeBool(NodeType.OR);

        final QueryNodeBool bool1 = new QueryNodeBool(NodeType.AND);
        final QueryNodeCompare comp11 = new QueryNodeCompare(Field.TITLE, "a");
        bool1.addChild(comp11);
        final QueryNodeCompare comp12 = new QueryNodeCompare(Field.AUTHOR, "b");
        bool1.addChild(comp12);
        final QueryNodeCompare comp13 = new QueryNodeCompare(Field.TITLE, "c");
        bool1.addChild(comp13);

        Assert.assertEquals("field code 1", Field.FIELDCODE_MIXED, bool1.getFieldCode());
        Assert.assertEquals(3, bool1.childCount());
        Assert.assertEquals("[(4=a), (1003=b), (4=c)]", bool1.children().toString());

        final QueryNodeBool bool2 = new QueryNodeBool(NodeType.AND);
        final QueryNodeCompare comp21 = new QueryNodeCompare(Field.TITLE, "aa");
        bool2.addChild(comp21);
        final QueryNodeCompare comp22 = new QueryNodeCompare(Field.TITLE, "bb");
        bool2.addChild(comp22);
        final QueryNodeCompare comp23 = new QueryNodeCompare(Field.TITLE, "cc");
        bool2.addChild(comp23);

        Assert.assertEquals("field code 2", Field.TITLE, bool2.getFieldCode());
        Assert.assertEquals(3, bool2.childCount());
        Assert.assertEquals("[(4=aa), (4=bb), (4=cc)]", bool2.children().toString());

        root.addChild(bool1);
        root.addChild(bool2);

        Assert.assertEquals("field code 3", Field.FIELDCODE_MIXED, root.getFieldCode());
        Assert.assertEquals(2, root.childCount());
        Assert.assertEquals("[{AND[-2]: (4=a)(1003=b)(4=c)}, {AND[4]: (4=aa)(4=bb)(4=cc)}]", root.children().toString());

        Assert.assertEquals("field code 11", Field.TITLE, comp11.getFieldCode());
        Assert.assertEquals("field code 12", Field.AUTHOR, comp12.getFieldCode());
        Assert.assertEquals("field code 13", Field.TITLE, comp13.getFieldCode());
        Assert.assertEquals("field code 21", Field.TITLE, comp21.getFieldCode());
        Assert.assertEquals("field code 22", Field.TITLE, comp22.getFieldCode());
        Assert.assertEquals("field code 23", Field.TITLE, comp23.getFieldCode());

        Assert.assertEquals("field code a1", Field.FIELDCODE_MIXED, bool1.getFieldCode());
        Assert.assertEquals("field code a2", Field.TITLE, bool2.getFieldCode());

        // /

        QueryNodeCompare newChild1 = new QueryNodeCompare(Field.ABSTRACT, "xx");
        bool2.replaceChild(comp21, newChild1);

        Assert.assertEquals("field code xx1", Field.FIELDCODE_MIXED, bool2.getFieldCode());
        Assert.assertEquals("field code xx2", Field.FIELDCODE_MIXED, root.getFieldCode());
        Assert.assertEquals("[{AND[-2]: (4=a)(1003=b)(4=c)}, {AND[-2]: (62=xx)(4=bb)(4=cc)}]", root.children()
                        .toString());

        QueryNodeCompare newChild2 = new QueryNodeCompare(Field.TITLE, "yy");
        bool1.replaceChild(comp12, newChild2);

        Assert.assertEquals("field code xxx1", Field.TITLE, bool1.getFieldCode());
        Assert.assertEquals("field code xxx2", Field.FIELDCODE_MIXED, bool2.getFieldCode());
        Assert.assertEquals("field code xxx3", Field.FIELDCODE_MIXED, root.getFieldCode());
        Assert.assertEquals("[{AND[4]: (4=a)(4=yy)(4=c)}, {AND[-2]: (62=xx)(4=bb)(4=cc)}]", root.children().toString());
    }


    @Test
    public void testRemove1() {
        final QueryNodeBool root = new QueryNodeBool(NodeType.OR);

        final QueryNodeBool bool1 = new QueryNodeBool(NodeType.AND);
        final QueryNodeCompare comp11 = new QueryNodeCompare(Field.TITLE, "a");
        bool1.addChild(comp11);
        final QueryNodeCompare comp12 = new QueryNodeCompare(Field.AUTHOR, "b");
        bool1.addChild(comp12);
        final QueryNodeCompare comp13 = new QueryNodeCompare(Field.TITLE, "c");
        bool1.addChild(comp13);

        Assert.assertEquals("field code 1", Field.FIELDCODE_MIXED, bool1.getFieldCode());
        Assert.assertEquals(3, bool1.childCount());
        Assert.assertEquals("[(4=a), (1003=b), (4=c)]", bool1.children().toString());

        final QueryNodeBool bool2 = new QueryNodeBool(NodeType.AND);
        final QueryNodeCompare comp21 = new QueryNodeCompare(Field.TITLE, "aa");
        bool2.addChild(comp21);
        final QueryNodeCompare comp22 = new QueryNodeCompare(Field.TITLE, "bb");
        bool2.addChild(comp22);
        final QueryNodeCompare comp23 = new QueryNodeCompare(Field.TITLE, "cc");
        bool2.addChild(comp23);

        Assert.assertEquals("field code 2", Field.TITLE, bool2.getFieldCode());
        Assert.assertEquals(3, bool2.childCount());
        Assert.assertEquals("[(4=aa), (4=bb), (4=cc)]", bool2.children().toString());

        root.addChild(bool1);
        root.addChild(bool2);

        Assert.assertEquals("field code 3", Field.FIELDCODE_MIXED, root.getFieldCode());
        Assert.assertEquals(2, root.childCount());
        Assert.assertEquals("[{AND[-2]: (4=a)(1003=b)(4=c)}, {AND[4]: (4=aa)(4=bb)(4=cc)}]", root.children().toString());

        Assert.assertEquals("field code 11", Field.TITLE, comp11.getFieldCode());
        Assert.assertEquals("field code 12", Field.AUTHOR, comp12.getFieldCode());
        Assert.assertEquals("field code 13", Field.TITLE, comp13.getFieldCode());
        Assert.assertEquals("field code 21", Field.TITLE, comp21.getFieldCode());
        Assert.assertEquals("field code 22", Field.TITLE, comp22.getFieldCode());
        Assert.assertEquals("field code 23", Field.TITLE, comp23.getFieldCode());

        Assert.assertEquals("field code a1", Field.FIELDCODE_MIXED, bool1.getFieldCode());
        Assert.assertEquals("field code a2", Field.TITLE, bool2.getFieldCode());

        // /

        comp23.remove();
        Assert.assertEquals("[{AND[-2]: (4=a)(1003=b)(4=c)}, {AND[4]: (4=aa)(4=bb)}]", root.children().toString());

        bool2.remove();
        Assert.assertEquals("[{AND[-2]: (4=a)(1003=b)(4=c)}]", root.children().toString());
    }

}
