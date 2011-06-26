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
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeCompare.Predicate;



/**
 * Tests {@link QueryNodeCompare}.
 * 
 * @author mjordan
 */
public class QueryNodeCompareTest extends AbstractTestBase {

    @Test
    public void testConstructor1a() {
        final QueryNodeCompare comp = new QueryNodeCompare(Field.TITLE, Predicate.EQ, "term1");
        Assert.assertEquals("field code", Field.TITLE, comp.getFieldCode());
        Assert.assertEquals("parent", null, comp.getParent());
        Assert.assertEquals("negated", false, comp.isNegated());
        Assert.assertEquals("predicate", Predicate.EQ, comp.getPredicate());
        Assert.assertEquals("tokens1", "[term1]", comp.getTokens().toString());
        Assert.assertEquals("tokens2", "term1", comp.getTokensAsString());
        Assert.assertEquals("wildcards", false, comp.hasWildcards());
    }


    @Test
    public void testConstructor1b() {
        final QueryNodeCompare comp = new QueryNodeCompare(Field.ABSTRACT, Predicate.LT, "term2");
        Assert.assertEquals("field code", Field.ABSTRACT, comp.getFieldCode());
        Assert.assertEquals("parent", null, comp.getParent());
        Assert.assertEquals("negated", false, comp.isNegated());
        Assert.assertEquals("predicate", Predicate.LT, comp.getPredicate());
        Assert.assertEquals("tokens1", "[term2]", comp.getTokens().toString());
        Assert.assertEquals("tokens2", "term2", comp.getTokensAsString());
        Assert.assertEquals("wildcards", false, comp.hasWildcards());
    }


    @Test
    public void testConstructor2() {
        final QueryNodeCompare comp = new QueryNodeCompare(Field.ABSTRACT, "term2");
        Assert.assertEquals("field code", Field.ABSTRACT, comp.getFieldCode());
        Assert.assertEquals("parent", null, comp.getParent());
        Assert.assertEquals("negated", false, comp.isNegated());
        Assert.assertEquals("predicate", QueryNodeCompare.DEFAULT_PREDICATE, comp.getPredicate());
        Assert.assertEquals("tokens1", "[term2]", comp.getTokens().toString());
        Assert.assertEquals("tokens2", "term2", comp.getTokensAsString());
        Assert.assertEquals("wildcards", false, comp.hasWildcards());
    }


    @Test
    public void testDefaultConstructor1() {
        final QueryNodeCompare comp = new QueryNodeCompare();
        Assert.assertEquals("field code", Field.FIELDCODE_NONE, comp.getFieldCode());
        Assert.assertEquals("parent", null, comp.getParent());
        Assert.assertEquals("negated", false, comp.isNegated());
        Assert.assertEquals("predicate", Predicate.EQ, comp.getPredicate());
        Assert.assertEquals("tokens1", 0, comp.getTokens().size());
        Assert.assertEquals("wildcards", false, comp.hasWildcards());
    }


    @Test
    public void testDefaultConstructor2() {
        final QueryNodeCompare comp = new QueryNodeCompare();
        comp.setFieldCode(Field.AUTHOR);
        comp.setNegated(true);
        comp.addToken("abc");
        comp.addToken(QueryNodeCompare.WILDCARD_MULTIPLE);
        comp.addToken("de");
        comp.addToken(QueryNodeCompare.WILDCARD_SINGLE);
        Assert.assertEquals("field code", Field.AUTHOR, comp.getFieldCode());
        Assert.assertEquals("parent", null, comp.getParent());
        Assert.assertEquals("negated", true, comp.isNegated());
        Assert.assertEquals("predicate", Predicate.EQ, comp.getPredicate());
        Assert.assertEquals("tokens1", "[abc, #, de, $]", comp.getTokens().toString());
        Assert.assertEquals("tokens2", "abc#de$", comp.getTokensAsString());
        Assert.assertEquals("wildcards", true, comp.hasWildcards());
    }


    @Test
    public void testAsRegEx1() {
        final QueryNodeCompare comp = new QueryNodeCompare(Field.ABSTRACT, Predicate.LT, "term2");
        Assert.assertEquals("\\Qterm2\\E", comp.asRegEx());
    }


    @Test
    public void testAsRegEx2() {
        final QueryNodeCompare comp = new QueryNodeCompare();
        comp.setFieldCode(Field.AUTHOR);
        comp.setNegated(true);
        comp.addToken("abc");
        comp.addToken(QueryNodeCompare.WILDCARD_MULTIPLE);
        comp.addToken("de");
        comp.addToken(QueryNodeCompare.WILDCARD_SINGLE);
        Assert.assertEquals("\\Qabc\\E\\w*\\Qde\\E\\w", comp.asRegEx());
    }
}
