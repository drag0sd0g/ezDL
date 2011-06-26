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

import de.unidue.inf.is.ezdl.dlcore.data.dldata.Document;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.Person;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.PersonList;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.TextDocument;
import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNode;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeBool;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeBool.NodeType;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeCompare;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeCompare.Predicate;



/**
 * Tests the {@link Filter} class.
 * 
 * @author mj
 */
public class FilterTest {

    Filter f;


    @Before
    public void setup() {
        f = new Filter();
    }


    // @Test
    public void testSpecialChars() {
        // TODO: Test with author "Cinnéide" and results from ACM that also
        // contain "Cinneide"
    }


    /**
     * Tests if a partial match of an author name makes another partial match of
     * a following authorname match even if the names don't really match.
     */
    @Test
    public void testTestForAuthornameOK() {
        PersonList pl = new PersonList();
        pl.add(new Person("X Nachname"));
        pl.add(new Person("Volker Egal"));
        Assert.assertFalse(f.testForAuthorname(pl, "Volker Nachname"));
    }


    @Test
    public void testTestForTitleOK() {
        checkTestForTitleOK("1a", "information", "information");
        checkTestForTitleOK("1b", " information ", "information");
        checkTestForTitleOK("1c", "     information     ", "information");
        checkTestForTitleOK("1d", "information retrieval", "information");
        checkTestForTitleOK("1e", "bla information", "information");
        checkTestForTitleOK("1f", "bla information retrieval", "information");

        checkTestForTitleOK("2a", "information retrieval", "information retrieval");
        checkTestForTitleOK("2b", " information retrieval ", "information retrieval");
        checkTestForTitleOK("2c", "    information retrieval    ", "information retrieval");
        checkTestForTitleOK("2d", "no information retrieval", "information retrieval");
        checkTestForTitleOK("2e", "information retrieval failure", "information retrieval");
        checkTestForTitleOK("2f", "information retrieval failure", "information retrieval");
        checkTestForTitleOK("2g", "no information retrieval failure", "information retrieval");

        checkTestForTitleOK("3a", " retrieval", "retrieving");
        checkTestForTitleOK("3b", " retrieving", "retrieved");
        checkTestForTitleOK("3c", " retrieving", "retrieval");

        checkTestForTitleOK("1a",
                        "Learning Information Extraction Patterns from Tabular Web Pages without Manual Labelling",
                        "information extraction");

        checkTestForTitleOK("1b",
                        "Learning Information Extraction Patterns from Tabular Web Pages without Manual Labelling",
                        "information extracation");

        checkTestForTitleOK("1c",
                        "Learning Information Extraction Patterns from Tabular Web Pages without Manual Labelling",
                        "extracting patterned");
    }


    @Test
    public void testTestForTitleFails() {
        checkTestForTitleFails("1a",
                        "Learning Information Extraction Patterns from Tabular Web Pages without Manual Labelling",
                        "information retrieval");
        checkTestForTitleFails("1a",
                        "Learning Information Extraction Patterns from Tabular Web Pages without Manual Labelling",
                        "information retrieval");

        checkTestForTitleFails("1c",
                        "Learning Information Extraction Patterns from Tabular Web Pages without Manual Labelling",
                        "extracting from");

    }


    private void checkTestForTitleFails(String label, String title, String query) {
        boolean res = f.testForTitlePhrase(title, query);
        Assert.assertFalse(label, res);
    }


    private void checkTestForTitleOK(String label, String title, String query) {
        boolean res = f.testForTitlePhrase(title, query);
        Assert.assertTrue(label, res);
    }


    @Test
    public void testYear1() {
        checkYear("GTE 1", true, "2000", Predicate.GTE, 2000, 2001, 2002, 2003, 2010);
    }


    @Test
    public void testYear2() {
        checkYear("GTE 2", false, "2000", Predicate.GTE, 1999, 1990);
    }


    @Test
    public void testYear3() {
        checkYear("GT 1", true, "2000", Predicate.GT, 2001, 2002, 2003, 2010);
    }


    @Test
    public void testYear4() {
        checkYear("GT 2", false, "2000", Predicate.GT, 2000, 1999, 1990);
    }


    @Test
    public void testYear5() {
        checkYear("EQ 1", true, "2000", Predicate.EQ, 2000, 2000, 2000);
    }


    @Test
    public void testYear6() {
        checkYear("EQ 2", false, "2000", Predicate.EQ, 1999, 1990, 2001);
    }


    @Test
    public void testYear7() {
        checkYear("LT 1", true, "2000", Predicate.LT, 1999, 1900, 1700);
    }


    @Test
    public void testYear8() {
        checkYear("LT 2", false, "2000", Predicate.LT, 2000, 2001);
    }


    @Test
    public void testYear9() {
        checkYear("LTE 1", true, "2000", Predicate.LTE, 2000, 2000, 1990, 1999, 1800);
    }


    @Test
    public void testYear10() {
        checkYear("LTE 2", false, "2000", Predicate.LTE, 2001, 2002, 2010, 2200);
    }


    private void checkYear(String label, boolean expected, String filterYear, Predicate pred, int... docYears) {
        QueryNodeBool queryNodeBool = new QueryNodeBool(NodeType.AND);
        queryNodeBool.addChild(new QueryNodeCompare(Field.YEAR, pred, filterYear));
        Filter f = new Filter(queryNodeBool);

        for (int docYear : docYears) {
            Document md = new TextDocument();
            md.setYear(docYear);

            Assert.assertEquals(label + ":" + docYear, expected, f.check(md));
        }
    }


    @Test
    public void checkAuthor1() {
        QueryNode q = new QueryNodeCompare(Field.AUTHOR, "Norbert Fuhr");
        Filter f = new Filter(q);

        Document md = new TextDocument();
        PersonList authorList = new PersonList();
        authorList.add(new Person("Norbert Fuhr"));
        md.setAuthorList(authorList);

        Assert.assertEquals(true, f.check(md));
    }


    @Test
    public void checkAuthor2() {
        QueryNode q = new QueryNodeCompare(Field.AUTHOR, "Norbert Fuhr");
        Filter f = new Filter(q);

        Document md = new TextDocument();
        PersonList authorList = new PersonList();
        authorList.add(new Person("Norbert Anders"));
        md.setAuthorList(authorList);

        Assert.assertEquals(false, f.check(md));
    }


    @Test
    public void testBetween() {
        checkYearBetween("Between 1a", true, "2000", "2004", 2000, 2001, 2002, 2003, 2004);
        checkYearBetween("Between 1b", false, "2000", "2004", 1999, 1998, 1700, 2005, 2006, 2100);
    }


    private void checkYearBetween(String label, boolean expected, String lowerYear, String upperYear, int... docYears) {
        QueryNodeBool queryNodeBool = new QueryNodeBool(NodeType.AND);
        queryNodeBool.addChild(new QueryNodeCompare(Field.YEAR, Predicate.GTE, lowerYear));
        queryNodeBool.addChild(new QueryNodeCompare(Field.YEAR, Predicate.LTE, upperYear));
        Filter f = new Filter(queryNodeBool);

        for (int docYear : docYears) {
            Document md = new TextDocument();
            md.setYear(docYear);

            Assert.assertEquals(label + ":" + docYear, expected, f.check(md));
        }
    }
}
