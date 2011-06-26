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

package de.unidue.inf.is.ezdl.dlfrontend.query;

import java.util.LinkedList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import de.unidue.inf.is.ezdl.dlcore.AbstractTestBase;
import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNode;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeBool;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeBool.NodeType;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeCompare;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeCompare.Predicate;



public class QueryNodeTest extends AbstractTestBase {

    private String repeat(char c, int count) {
        StringBuffer out = new StringBuffer();
        for (int i = 0; (i < count); i++) {
            out.append(c);
        }
        return out.toString();
    }


    @Test
    public void testQueryNodeCompare() {
        checkQueryNodeCompare(Field.AUTHOR, Predicate.EQ, "");
        checkQueryNodeCompare(Field.AUTHOR, Predicate.EQ, "a");
        checkQueryNodeCompare(Field.AUTHOR, Predicate.EQ, repeat('a', 2000));
    }


    public QueryNodeCompare checkQueryNodeCompare(Field field, Predicate rel, String val) {
        QueryNodeCompare node = new QueryNodeCompare(field, rel, val);
        Assert.assertEquals("field code", field, node.getFieldCode());
        Assert.assertEquals("rel op", rel, node.getPredicate());
        Assert.assertEquals("val", val, node.getTokensAsString());
        Assert.assertEquals("parent", null, node.getParent());
        return node;
    }


    @Test
    public void testQueryNodeBool() {
        QueryNodeBool node = new QueryNodeBool();
        Assert.assertEquals("childCount not 0", 0, node.childCount());
        Assert.assertNotNull("children list null", node.children());
        Assert.assertEquals("field code not none", Field.FIELDCODE_NONE, node.getFieldCode());
        Assert.assertEquals("type not AND", NodeType.AND, node.getType());
        node.setType(NodeType.AND);
        Assert.assertEquals("type not AND after set", NodeType.AND, node.getType());
        node.setType(NodeType.OR);
        Assert.assertEquals("type not OR after set", NodeType.OR, node.getType());
    }


    @Test
    public void testQueryNodeBoolAddChild() {
        QueryNodeBool root = new QueryNodeBool(NodeType.AND);

        addChild(root, Field.AUTHOR, Predicate.EQ, "a");
        Assert.assertEquals("child count", 1, root.childCount());
        Assert.assertEquals("field code", Field.AUTHOR, root.getFieldCode());
        Assert.assertEquals("negated", false, root.isNegated());
        Assert.assertEquals("child list", "[(1003=a)]", root.children().toString());
        Assert.assertEquals("parent", root.getChildAt(0).getParent(), root);

        addChild(root, Field.AUTHOR, Predicate.EQ, "b");
        Assert.assertEquals("child count", 2, root.childCount());
        Assert.assertEquals("field code", Field.AUTHOR, root.getFieldCode());
        Assert.assertEquals("negated", false, root.isNegated());
        Assert.assertEquals("child list", "[(1003=a), (1003=b)]", root.children().toString());
        Assert.assertEquals("parent", root.getChildAt(0).getParent(), root);
        Assert.assertEquals("parent", root.getChildAt(1).getParent(), root);

        addChild(root, Field.CITATION, Predicate.EQ, "c");
        Assert.assertEquals("child count", 3, root.childCount());
        Assert.assertEquals("field code", Field.FIELDCODE_MIXED, root.getFieldCode());
        Assert.assertEquals("negated", false, root.isNegated());
        Assert.assertEquals("child list", "[(1003=a), (1003=b), (5002=c)]", root.children().toString());
        Assert.assertEquals("parent", root.getChildAt(0).getParent(), root);
        Assert.assertEquals("parent", root.getChildAt(1).getParent(), root);
        Assert.assertEquals("parent", root.getChildAt(2).getParent(), root);

        addChild(root, Field.CITATION, Predicate.EQ, "d");
        Assert.assertEquals("child count", 4, root.childCount());
        Assert.assertEquals("field code", Field.FIELDCODE_MIXED, root.getFieldCode());
        Assert.assertEquals("negated", false, root.isNegated());
        Assert.assertEquals("child list", "[(1003=a), (1003=b), (5002=c), (5002=d)]", root.children().toString());
        Assert.assertEquals("parent", root.getChildAt(0).getParent(), root);
        Assert.assertEquals("parent", root.getChildAt(1).getParent(), root);
        Assert.assertEquals("parent", root.getChildAt(2).getParent(), root);
        Assert.assertEquals("parent", root.getChildAt(3).getParent(), root);

        root.removeChild(root.getChildAt(0));
        Assert.assertEquals("child list", "[(1003=b), (5002=c), (5002=d)]", root.children().toString());
        Assert.assertEquals("field code", Field.FIELDCODE_MIXED, root.getFieldCode());

        root.removeChild(root.getChildAt(0));
        Assert.assertEquals("child list", "[(5002=c), (5002=d)]", root.children().toString());
        Assert.assertEquals("field code", Field.CITATION, root.getFieldCode());

        List<QueryNode> newChildren = new LinkedList<QueryNode>();
        newChildren.add(checkQueryNodeCompare(Field.AUTHOR, Predicate.EQ, "a"));
        newChildren.add(checkQueryNodeCompare(Field.AUTHOR, Predicate.EQ, "b"));
        newChildren.add(checkQueryNodeCompare(Field.AUTHOR, Predicate.EQ, "c"));
        root.setChildren(newChildren);
        Assert.assertEquals("child count", 3, root.childCount());
        Assert.assertEquals("field code", Field.AUTHOR, root.getFieldCode());
        Assert.assertEquals("negated", false, root.isNegated());
        Assert.assertEquals("child list", "[(1003=a), (1003=b), (1003=c)]", root.children().toString());
        Assert.assertEquals("parent", root.getChildAt(0).getParent(), root);
        Assert.assertEquals("parent", root.getChildAt(1).getParent(), root);
        Assert.assertEquals("parent", root.getChildAt(2).getParent(), root);

        root.addChild(checkQueryNodeCompare(Field.AUTHOR, Predicate.EQ, "d"), 2);
        Assert.assertEquals("child list", "[(1003=a), (1003=b), (1003=d), (1003=c)]", root.children().toString());
        Assert.assertEquals("parent", root.getChildAt(0).getParent(), root);
        Assert.assertEquals("parent", root.getChildAt(1).getParent(), root);
        Assert.assertEquals("parent", root.getChildAt(2).getParent(), root);
        Assert.assertEquals("parent", root.getChildAt(3).getParent(), root);

        root.addChild(checkQueryNodeCompare(Field.AUTHOR, Predicate.EQ, "e"), 4);
        Assert.assertEquals("child list", "[(1003=a), (1003=b), (1003=d), (1003=c), (1003=e)]", root.children()
                        .toString());
        Assert.assertEquals("field code", Field.AUTHOR, root.getFieldCode());

        QueryNodeCompare child = (QueryNodeCompare) root.getChildAt(0);
        child.setFieldCode(Field.ABSTRACT);
        Assert.assertEquals("child list", "[(62=a), (1003=b), (1003=d), (1003=c), (1003=e)]", root.children()
                        .toString());
        Assert.assertEquals("field code child", Field.ABSTRACT, child.getFieldCode());
        Assert.assertEquals("field code root", Field.FIELDCODE_MIXED, root.getFieldCode());

        root.replaceChild(root.getChildAt(0), checkQueryNodeCompare(Field.AUTHOR, Predicate.EQ, "z"));
        Assert.assertEquals("child list", "[(1003=z), (1003=b), (1003=d), (1003=c), (1003=e)]", root.children()
                        .toString());
        Assert.assertEquals("field code root", Field.AUTHOR, root.getFieldCode());

        root.getChildAt(1).remove();
        Assert.assertEquals("child list", "[(1003=z), (1003=d), (1003=c), (1003=e)]", root.children().toString());
        Assert.assertEquals("field code root", Field.AUTHOR, root.getFieldCode());

        QueryNodeCompare child2 = (QueryNodeCompare) root.getChildAt(2);
        child2.setFieldCode(Field.ARTIST);
        Assert.assertEquals("child list", "[(1003=z), (1003=d), (5030=c), (1003=e)]", root.children().toString());
        Assert.assertEquals("field code root", Field.FIELDCODE_MIXED, root.getFieldCode());
        child2.remove();
        Assert.assertEquals("child list", "[(1003=z), (1003=d), (1003=e)]", root.children().toString());
        Assert.assertEquals("field code root", Field.AUTHOR, root.getFieldCode());
    }


    private void addChild(QueryNodeBool root, Field field, Predicate rel, String val) {
        QueryNodeCompare comp = new QueryNodeCompare(field, rel, val);
        root.addChild(comp);
    }

}
