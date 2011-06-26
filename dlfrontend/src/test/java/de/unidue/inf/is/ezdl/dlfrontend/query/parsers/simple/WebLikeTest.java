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

package de.unidue.inf.is.ezdl.dlfrontend.query.parsers.simple;

import junit.framework.Assert;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.ParserRuleReturnScope;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.CommonTree;
import org.junit.Before;
import org.junit.Test;

import de.unidue.inf.is.ezdl.dlcore.AbstractTestBase;



public class WebLikeTest extends AbstractTestBase {

    @Before
    public void init() {
    }


    @Test
    public void testQuery_terms() {
        testQuery_term("1", "test", "test");
        testQuery_term("2", "test-term", "test-term");
        testQuery_term("3", "don't", "don't");
    }


    @Test
    public void testQuery_phraseterms() {
        testQuery_phraseterm("1", "test", "test");
        testQuery_phraseterm("2", "test rest of the text", "test");
        testQuery_phraseterm("3", "\"test\"", "\"test\"");
        testQuery_phraseterm("4", "\"test\" rest of the text", "\"test\"");
        testQuery_phraseterm("5", "\"test a really long phrase\"", "\"test a really long phrase\"");
    }


    @Test
    public void testQuery_fieldterms() {
        testQuery_fieldterm("1", "field:\"a phrase\"", "(: field \"a phrase\")");
        testQuery_fieldterm("2", "field:term", "(: field term)");
        testQuery_fieldterm("3", "field=term", "(= field term)");
        testQuery_fieldterm("4", "field<term", "(< field term)");
        testQuery_fieldterm("5", "field<=term", "(<= field term)");
        testQuery_fieldterm("6", "field>term", "(> field term)");
        testQuery_fieldterm("7", "field>=term", "(>= field term)");
        testQuery_fieldterm("8", ">=term", "(>= term)");
    }


    @Test
    public void testQuery_fieldexpressions() {
        testQuery_fieldexpression("1", "term", "term");
        testQuery_fieldexpression("2", "a:term", "(: a term)");
        testQuery_fieldexpression("3", "NOT term", "(NOT term)");
        testQuery_fieldexpression("4", "NOT a:term", "(NOT (: a term))");
        testQuery_fieldexpression("5", "-term", "(NOT term)");
        testQuery_fieldexpression("6", "-test-term", "(NOT test-term)");
        testQuery_fieldexpression("7", "-title:term", "(NOT (: title term))");
        testQuery_fieldexpression("8", "-free-text:test-term", "(NOT (: free-text test-term))");
    }


    @Test
    public void testQuery_subquery() {
        testQuery_subquery("1", "term", "term");
        testQuery_subquery("2", "NOT term", "(NOT term)");
        testQuery_subquery("3", "aterm AND bterm", "(AND aterm bterm)");
        testQuery_subquery("4", "NOT aterm AND bterm", "(AND (NOT aterm) bterm)");
        testQuery_subquery("5", "\"NOT aterm\" AND bterm", "(AND \"NOT aterm\" bterm)");
        testQuery_subquery("6", "\"aterm\" AND NOT bterm", "(AND \"aterm\" (NOT bterm))");
        testQuery_subquery("7", "( a AND b ) OR  ( c AND d )", "(OR (AND a b) (AND c d))");
        testQuery_subquery("8", "a AND b", "(AND a b)");
        testQuery_subquery("9", "a AND b AND c", "(AND a b c)");
        testQuery_subquery("10", "(a AND b)", "(AND a b)");
        testQuery_subquery("11", "(author:a AND title:b)", "(AND (: author a) (: title b))");
        testQuery_subquery("12", "(author:\"Norbert Fuhr\" AND title:don't) OR (text:free AND NOT text:beer)",
                        "(OR (AND (: author \"Norbert Fuhr\") (: title don't)) (AND (: text free) (NOT (: text beer))))");
    }


    @Test
    public void testQuery_query() {
        testQuery_query("1", "term", "term null");
        testQuery_query("2", ">term", "(> term) null");
        testQuery_query("3", "NOT term", "(NOT term) null");
        testQuery_query("4", "aterm AND bterm", "(AND aterm bterm) null");
        testQuery_query("5", "a AND b", "(AND a b) null");
        testQuery_query("6", "(a AND b)", "(AND a b) null");
        testQuery_query("7", "a b", "(AND a b) null");
        testQuery_query("7a", "a b c d e", "(AND a b c d e) null");
        testQuery_query("8", "NOT aterm AND bterm", "(AND (NOT aterm) bterm) null");
        testQuery_query("9", "\"NOT aterm\" AND bterm", "(AND \"NOT aterm\" bterm) null");
        testQuery_query("10", "\"aterm\" AND NOT bterm", "(AND \"aterm\" (NOT bterm)) null");
        testQuery_query("11", "( a AND b ) OR  ( c AND d )", "(OR (AND a b) (AND c d)) null");
        testQuery_query("12", "(author:a AND title:b)", "(AND (: author a) (: title b)) null");
        testQuery_query("13", "(author:\"Norbert Fuhr\" AND title:don't) OR (text:free AND NOT text:beer)",
                        "(OR (AND (: author \"Norbert Fuhr\") (: title don't)) (AND (: text free) (NOT (: text beer)))) null");
        testQuery_query("14", "(author:\"Norbert Fuhr\" AND title:don't AND year>=2000)",
                        "(AND (: author \"Norbert Fuhr\") (: title don't) (>= year 2000)) null");
        testQuery_query("15", "a OR b OR c AND d AND e OR f AND g AND h OR i",
                        "(AND (OR a b c) d (OR e f) g (OR h i)) null");
        testQuery_query("16", "a OR (b AND c) OR d OR e", "(OR a (AND b c) d e) null");
        testQuery_query("17", "a OR b OR (c AND d AND e) OR (f AND g AND h) OR i",
                        "(OR a b (AND c d e) (AND f g h) i) null");
        testQuery_query("18", "a OR NOT b OR (c AND d AND NOT e) OR (f AND g AND h) OR i",
                        "(OR a (NOT b) (AND c d (NOT e)) (AND f g h) i) null");
        testQuery_query("19", "x:a OR NOT y:b OR (z:c AND x:d AND NOT ye) OR (f AND g AND h) OR i",
                        "(OR (: x a) (NOT (: y b)) (AND (: z c) (: x d) (NOT ye)) (AND f g h) i) null");
    }


    /**
     * Test for some corner cases.
     */
    @Test
    public void testQuery_query_nifty() {
        testQuery_query("AND token is not a token in a term 1", "tANDy", "tANDy null");
        testQuery_query("AND token is not a token in a term 2", "ANDy", "ANDy null");
        testQuery_query("AND token is not a token in a term 3", "tAND", "tAND null");
        testQuery_query("AND token is not a token in a phrase 1", "\"a AND b\"", "\"a AND b\" null");
        testQuery_query("AND token is not a token in a phrase 2", "\"AND b a\"", "\"AND b a\" null");
        testQuery_query("AND token is not a token in a phrase 3", "\"b a AND\"", "\"b a AND\" null");

        testQuery_query("- token is not a NOT token in a term 1", "a-b", "a-b null");
        testQuery_query("- token is not a  NOT token in a term 2", "ab-", "ab- null");
        testQuery_query("- token is  NOT a token at the beginning of a term", "-ab", "(NOT ab) null");
        testQuery_query("- token is not a  NOT token in a phrase 1", "\"a-b\"", "\"a-b\" null");
        testQuery_query("- token is not a  NOT token in a phrase 2", "\"-ab\"", "\"-ab\" null");
        testQuery_query("- token is not a  NOT token in a phrase 3", "\"ab-\"", "\"ab-\" null");

        testQuery_query("NOT token is not a  NOT token in a term 1", "NOTa", "NOTa null");
        testQuery_query("NOT token is not a  NOT token in a term 2", "aNOT", "aNOT null");
        testQuery_query("NOT token is not a  NOT token in a phrase 1", "\"NOT a\"", "\"NOT a\" null");

        testQuery_query("OR parsed", "(a AND b) OR (c AND d)", "(OR (AND a b) (AND c d)) null");
        testQuery_query("OR parsed if between parens", "(a AND b)OR(c AND d)", "(OR (AND a b) (AND c d)) null");
    }


    public void testQuery_query(String message, String query, String expectedTree) {
        WebLikeParser parser = getParser(query);

        WebLikeParser.query_return tree = null;
        try {
            tree = parser.query();
        }
        catch (RecognitionException e) {
            e.printStackTrace();
            Assert.fail("Parser failed.");
        }

        check(message, query, expectedTree, tree);
    }


    public void testQuery_subquery(String message, String query, String expectedTree) {
        WebLikeParser parser = getParser(query);

        WebLikeParser.subquery_return tree = null;
        try {
            tree = parser.subquery();
        }
        catch (RecognitionException e) {
            e.printStackTrace();
            Assert.fail("Parser failed.");
        }

        check(message, query, expectedTree, tree);
    }


    public void testQuery_fieldexpression(String message, String query, String expectedTree) {
        WebLikeParser parser = getParser(query);

        WebLikeParser.fieldexpression_return tree = null;
        try {
            tree = parser.fieldexpression();
        }
        catch (RecognitionException e) {
            e.printStackTrace();
            Assert.fail("Parser failed.");
        }

        check(message, query, expectedTree, tree);
    }


    public void testQuery_fieldterm(String message, String query, String expectedTree) {
        WebLikeParser parser = getParser(query);

        WebLikeParser.fieldterm_return tree = null;
        try {
            tree = parser.fieldterm();
        }
        catch (RecognitionException e) {
            e.printStackTrace();
            Assert.fail("Parser failed.");
        }

        check(message, query, expectedTree, tree);
    }


    public void testQuery_phraseterm(String message, String query, String expectedTree) {
        WebLikeParser parser = getParser(query);

        WebLikeParser.phraseterm_return tree = null;
        try {
            tree = parser.phraseterm();
        }
        catch (RecognitionException e) {
            e.printStackTrace();
            Assert.fail("Parser failed.");
        }

        check(message, query, expectedTree, tree);
    }


    public void testQuery_term(String message, String query, String expectedTree) {
        WebLikeParser parser = getParser(query);

        WebLikeParser.term_return tree = null;
        try {
            tree = parser.term();
        }
        catch (RecognitionException e) {
            e.printStackTrace();
            Assert.fail("Parser failed.");
        }

        check(message, query, expectedTree, tree);
    }


    private WebLikeParser getParser(String query) {
        WebLikeLexer lex = new WebLikeLexer(new ANTLRStringStream(query));
        CommonTokenStream tokens = new CommonTokenStream(lex);
        WebLikeParser parser = new WebLikeParser(tokens);
        return parser;
    }


    private void check(String message, String query, String expectedTree, ParserRuleReturnScope tree) {
        String treeOut = "#fail";
        if (tree != null) {
            treeOut = ((CommonTree) tree.getTree()).toStringTree();
            // System.out.println("Input: " + query + "   \toutput: " +
            // treeOut);
        }
        Assert.assertEquals(message, expectedTree, treeOut);
    }

}
