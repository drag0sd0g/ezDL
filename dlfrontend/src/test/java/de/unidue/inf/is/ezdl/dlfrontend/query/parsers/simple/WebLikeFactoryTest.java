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

import org.antlr.runtime.RecognitionException;
import org.junit.Before;
import org.junit.Test;

import de.unidue.inf.is.ezdl.dlcore.AbstractTestBase;
import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;
import de.unidue.inf.is.ezdl.dlcore.data.query.Query;
import de.unidue.inf.is.ezdl.dlfrontend.helper.FieldRegistry;
import de.unidue.inf.is.ezdl.dlfrontend.query.NoSuchFieldCodeException;
import de.unidue.inf.is.ezdl.dlfrontend.query.QueryFactory;



public class WebLikeFactoryTest extends AbstractTestBase {

    private static Field AUTHOR = Field.AUTHOR;
    private static Field YEAR = Field.YEAR;
    private static Field DEFAULT = Field.TEXT;

    private QueryFactory factory = new WebLikeFactory(FieldRegistry.getInstance(), DEFAULT);


    @Before
    public void init() {
    }


    @Test
    public void testParse() {
        checkParse("1", "term", "(" + DEFAULT + "=term)");
        checkParse("1a", ">term", "(" + DEFAULT + ">term)");
        checkParse("1b", "Author>term", "(" + AUTHOR + ">term)");
        checkParse("1c", "tANDy", "(" + DEFAULT + "=tANDy)");
        checkParse("2", "NOT term", "(NOT " + DEFAULT + "=term)");
        checkParse("3", "\"information retrieval\"", "(" + DEFAULT + "=information retrieval)");
        checkParse("3a", "\"information AND retrieval\"", "(" + DEFAULT + "=information AND retrieval)");
        checkParse("3b", "\"tANDy\"", "(" + DEFAULT + "=tANDy)");
        checkParse("4", "NOT   \"information retrieval\"", "(NOT " + DEFAULT + "=information retrieval)");
        checkParse("5", "term   AND      \"other term\"", "{AND[" + DEFAULT + "]: (" + DEFAULT + "=term)(" + DEFAULT
                        + "=other term)}");
        checkParse("6", "term    OR synonym", "{OR[" + DEFAULT + "]: (" + DEFAULT + "=term)(" + DEFAULT + "=synonym)}");
        checkParse("7", "syn1 OR syn2 OR syn3 AND term", "{AND[" + DEFAULT + "]: {OR[" + DEFAULT + "]: " + "("
                        + DEFAULT + "=syn1)(" + DEFAULT + "=syn2)(" + DEFAULT + "=syn3)}(" + DEFAULT + "=term)}");

        checkParse("8", "NOT notted AND (Author:a OR b)", "{AND[-2]: (NOT " + DEFAULT + "=notted){OR[-2]: (" + AUTHOR
                        + "=a)(" + DEFAULT + "=b)}}");
        checkParse("9", "(a OR b) AND (c OR (d AND e))", "{AND[" + DEFAULT + "]: {OR[" + DEFAULT + "]: (" + DEFAULT
                        + "=a)(" + DEFAULT + "=b)}{OR[" + DEFAULT + "]: (" + DEFAULT + "=c){AND[" + DEFAULT + "]: ("
                        + DEFAULT + "=d)(" + DEFAULT + "=e)}}}");
        checkParse("10", "a AN", "{AND[" + DEFAULT + "]: (" + DEFAULT + "=a)(" + DEFAULT + "=AN)}");
        checkParse("10a", "a b c", "{AND[1046]: (1046=a)(1046=b)(1046=c)}");
        checkParse("10b", "a b c OR 1 2 3", "{AND[1046]: (1046=a)(1046=b){OR[1046]: (1046=c)(1046=1)}(1046=2)(1046=3)}");
        checkParse("10c", "(a b c) OR (1 2 3)",
                        "{OR[1046]: {AND[1046]: (1046=a)(1046=b)(1046=c)}{AND[1046]: (1046=1)(1046=2)(1046=3)}}");
        checkParse("11", "a AND", null);
        checkParse("12", "a AND (b AND c)", "{AND[" + DEFAULT + "]: (" + DEFAULT + "=a){AND[" + DEFAULT + "]: ("
                        + DEFAULT + "=b)(" + DEFAULT + "=c)}}");
        checkParse("13", "a AND NOT b AND Author=c", "{AND[-2]: (" + DEFAULT + "=a)(NOT " + DEFAULT + "=b)(" + AUTHOR
                        + "=c)}");
        checkParse("14", "Author=aa OR Author=bb AND Year=22", "{AND[-2]: {OR[1003]: (1003=aa)(1003=bb)}(31=22)}");

        checkParse("spacing 1", "(a AND b)OR(c AND d)",
                        "{OR[1046]: {AND[1046]: (1046=a)(1046=b)}{AND[1046]: (1046=c)(1046=d)}}");
    }


    @Test
    public void testBug351() {
        checkParse("a", "Title=\"a b\"", "(4=a b)");
    }


    @Test
    public void testParseYear() {
        /*
         * because default field is TEXT (1046), not YEAR (31)
         */
        checkParse("not a year range 1", "1000-2000", "(1046=1000-2000)");
        checkParse("not a year range 2", "Baeza-Yates", "(1046=Baeza-Yates)");
        checkParse("not a year range 3", "Ribeiro-Neto", "(1046=Ribeiro-Neto)");

        checkParse("year ranges 1a", "Year=1000-2000", "{AND[31]: (31>=1000)(31<=2000)}");
        checkParse("year ranges 1b", "Year<=2000", "(31<=2000)");
        checkParse("year ranges 1c", "Year=2000-", "(31>=2000)");

        factory = new WebLikeFactory(FieldRegistry.getInstance(), YEAR);

        checkParse("year ranges 2a", "1000-2000", "{AND[31]: (31>=1000)(31<=2000)}");
        checkParse("year ranges 2b", "<=2000", "(31<=2000)");
        checkParse("year ranges 2c", "2000-", "(31>=2000)");
        checkParse("year ranges 2d", "-2000", "(NOT 31=2000)");
    }


    @Test
    public void testOperatorPriority() {
        checkParse("OR stronger than AND", "a OR b OR c AND 1 OR 2 OR 3",
                        "{AND[1046]: {OR[1046]: (1046=a)(1046=b)(1046=c)}{OR[1046]: (1046=1)(1046=2)(1046=3)}}");
    }


    @Test
    public void testUnparse() {
        checkUnparse("1", "Author=Test", "Author=Test", Field.TITLE);
        checkUnparse("1a", "Author:Test", "Author=Test");
        checkUnparse("2", "Year>=2001", "Year>=2001");
        checkUnparse("3", "Year>2001", ">2001", Field.YEAR);
        checkUnparse("4", "FooBar:a AND b", null); // no such field code
        checkUnparse("4", "Text:a AND b", "a AND b");
        checkUnparse("666", "Text:a AND b", "Text=a AND b", Field.AUTHOR);
        checkUnparse("7", "Author=aa OR Author=bb AND Year=22", "Author=aa OR Author=bb AND Year=22", Field.TEXT);
    }


    public void checkParse(String message, String queryString, String expected) {
        try {
            Query query = factory.parse(queryString);
            Assert.assertNotNull(query);
            String actual = query.getTree().toString();
            Assert.assertEquals(message, expected, actual);
        }
        catch (RecognitionException e) {
            if (expected == null) {
                // Error expected so this is actually a success.
                Assert.assertTrue(true);
            }
            else {
                e.printStackTrace();
                Assert.fail("Parser failed.");
            }
        }
        catch (NoSuchFieldCodeException e) {
            e.printStackTrace();
            Assert.fail("Field code unknown.");
        }
    }


    public void checkUnparse(String message, String queryStr, String actual) {
        checkUnparse(message, queryStr, actual, DEFAULT);
    }


    public void checkUnparse(String message, String queryStr, String expected, Field fieldCode) {
        try {
            Query query = factory.parse(queryStr, fieldCode);
            String unparsed = factory.getTextForQueryNode(query.getTree(), fieldCode);
            Assert.assertEquals(message, expected, unparsed);
        }
        catch (RecognitionException e) {
            e.printStackTrace();
            Assert.fail("Somebody tried a wrong query.");
        }
        catch (NoSuchFieldCodeException e) {
            // Conversion failed. If expected is null, don't count this as a
            // failure because failure was expected.
            if (expected != null) {
                e.printStackTrace();
                Assert.fail();
            }
        }

    }

}
