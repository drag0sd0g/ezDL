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

package de.unidue.inf.is.ezdl.dlwrapper.wrappers.cs.ieee;

import java.util.Collections;
import java.util.Date;
import java.util.Properties;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import de.unidue.inf.is.ezdl.dlbackend.data.StoredDocument;
import de.unidue.inf.is.ezdl.dlbackend.data.StoredDocumentList;
import de.unidue.inf.is.ezdl.dlbackend.mock.MockAgent;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.Document;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.Person;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.PersonList;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.TextDocument;
import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;
import de.unidue.inf.is.ezdl.dlcore.data.query.DefaultQuery;
import de.unidue.inf.is.ezdl.dlcore.data.query.DocumentQuery;
import de.unidue.inf.is.ezdl.dlcore.data.query.Query;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeBool;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeCompare;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeCompare.Predicate;
import de.unidue.inf.is.ezdl.dlcore.query.YearRangeConverter;
import de.unidue.inf.is.ezdl.dlcore.query.YearRangeConverter.YearRange;
import de.unidue.inf.is.ezdl.dlwrapper.wrappers.AbstractBasicToolkitWrapper;
import de.unidue.inf.is.ezdl.dlwrapper.wrappers.ToolkitWrapperTestBase;



/**
 * @author mjordan
 */
public class IEEEWrapperTest extends ToolkitWrapperTestBase {

    private static final long YEAR_2010_LONG = 1282828778000l;
    private MockAgent agent;
    private IEEEWrapper wrapper;


    /**
     * @throws java.lang.Exception
     */
    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        agent = getAgent();

        TestableToolkitAPI api = new TestableToolkitAPI("ieee");
        // api.setProxy("test.ezdl.de", 3128);
        wrapper = new IEEEWrapper(api);
        wrapper.init(agent, null);
        setRecordingMode(false);
    }


    protected MockAgent getAgent() {
        if (agent == null) {
            agent = new MockAgent();
            Properties props = new Properties();
            props.setProperty("log", "off");
            props.setProperty(IEEEWrapper.MAX_ERRORS_KEY, "3");
            props.setProperty(AbstractBasicToolkitWrapper.MAX_FETCH_RESULTS_KEY, "100");
            agent.init("mockagent", props);
        }
        return agent;
    }


    @Test
    public void testGetAuthorListSequentialForm() {
        checkAuthorList("1", "Jitka Fuhr; Norbert Fuhr;", "Jitka Fuhr", "Norbert Fuhr");
        checkAuthorList("2", "Jitka Fuhr;", "Jitka Fuhr");
    }


    @Test
    public void testGetAuthorListInvertedCommaForm() {
        checkAuthorList("1", "Fuhr, Jitka; Fuhr, Norbert;", "Jitka Fuhr", "Norbert Fuhr");
        checkAuthorList("2", "Fuhr, Jitka;", "Jitka Fuhr");
    }


    @Test
    public void testGetAuthorListMixed() {
        checkAuthorList("1", "Fuhr, Jitka; Norbert Fuhr;", "Jitka Fuhr", "Norbert Fuhr");
        checkAuthorList("2", "Fuhr, Jitka; Norbert Fuhr", "Jitka Fuhr", "Norbert Fuhr");
    }


    private void checkAuthorList(String msg, String rawString, String... expectedAuthors) {
        IEEEDocumentCreator creator = new IEEEDocumentCreator();
        PersonList authors = creator.getAuthorList(rawString);
        int i = 0;
        for (String expectedAuthor : expectedAuthors) {
            Assert.assertEquals(msg + ": author " + i, new Person(expectedAuthor), authors.get(i));
            i++;
        }
    }


    /**
     * Test method for
     * {@link de.unidue.inf.is.ezdl.dlwrapper.wrappers.AbstractWrapper#askDocument(de.unidue.inf.is.ezdl.dlcore.data.query.DocumentQuery, boolean)}
     * with author searches.
     */
    @Test
    public void testAskDocumentAuthor1() {
        checkAskDocumentAutoId(Field.AUTHOR, "Fuhr", 47);
    }


    /**
     * Test method for
     * {@link de.unidue.inf.is.ezdl.dlwrapper.wrappers.AbstractWrapper#askDocument(de.unidue.inf.is.ezdl.dlcore.data.query.DocumentQuery, boolean)}
     * with author searches.
     */
    @Test
    public void testAskDocumentAuthor1a() {
        checkAskDocumentAutoId(Field.AUTHOR, "Belkin", 83);
    }


    @Test
    public void testAskDocumentAuthor2() {
        checkAskDocumentAutoId(Field.AUTHOR, "van Rijsbergen", 4);
    }


    @Test
    public void testAskDocumentTitle() {
        checkAskDocumentAutoId(Field.TITLE, "information retrieval", 89);
    }


    @Test
    public void testYearConverter1() {
        YearRange range = (new YearRangeConverter()).new YearRange();
        range.minYear = 2006;
        range.maxYear = 2006;
        Assert.assertEquals("2006_2006_Publication_Year",
                        wrapper.getYearRangeParameter(range, new Date(YEAR_2010_LONG)));
    }


    @Test
    public void testYearConverter2() {
        YearRange range = (new YearRangeConverter()).new YearRange();
        Assert.assertEquals(null, wrapper.getYearRangeParameter(range, new Date(YEAR_2010_LONG)));
    }


    @Test
    public void testYearConverter3() {
        YearRange range = (new YearRangeConverter()).new YearRange();
        range.minYear = 2000;
        range.maxYear = 2010;
        Assert.assertEquals("2000_2010_Publication_Year",
                        wrapper.getYearRangeParameter(range, new Date(YEAR_2010_LONG)));
    }


    @Test
    public void testYearConverter5() {
        YearRange range = (new YearRangeConverter()).new YearRange();
        range.maxYear = 2010;
        Assert.assertEquals("1900_2010_Publication_Year",
                        wrapper.getYearRangeParameter(range, new Date(YEAR_2010_LONG)));
    }


    @Test
    public void testYearConverter6() {
        YearRange range = (new YearRangeConverter()).new YearRange();
        range.minYear = 2000;
        Assert.assertEquals("2000_2010_Publication_Year",
                        wrapper.getYearRangeParameter(range, new Date(YEAR_2010_LONG)));
    }


    @Test
    public void testAskDocumentComplex() {
        setTestId("complex1");
        QueryNodeBool d = new QueryNodeBool();
        addConditionToBool(d, Field.TITLE, "test");
        addConditionToBool(d, Field.AUTHOR, "belkin");
        addConditionToBool(d, Field.YEAR, "2006");
        final Query q = new DefaultQuery(d);
        DocumentQuery query = new DocumentQuery(q, Collections.<String> emptyList());
        StoredDocumentList results = wrapper.askDocument(query, false);

        getLogger().error("Results: " + results.toString());
        getLogger().error("Result count: " + results.size());
        Assert.assertEquals("result size", 1, results.size());

        StoredDocument stored = results.get(0);
        TextDocument doc = (TextDocument) stored.getDocument();
        Assert.assertEquals("authors count", 2, doc.getAuthorList().size());
        Assert.assertEquals("author", "V.V. Belkin", doc.getAuthorList().get(0).toString());
        Assert.assertEquals("author", "S.G. Sharshunov", doc.getAuthorList().get(1).toString());
    }


    @Test
    public void testAskDocumentYearRange() {
        setTestId("yearRange1");
        final int minYear = 2000;
        final int maxYear = 2005;
        QueryNodeBool d = new QueryNodeBool();
        addConditionToBool(d, Field.TITLE, "test");
        d.addChild(new QueryNodeCompare(Field.YEAR, Predicate.GTE, "" + minYear));
        d.addChild(new QueryNodeCompare(Field.YEAR, Predicate.LTE, "" + maxYear));
        final Query q = new DefaultQuery(d);
        DocumentQuery query = new DocumentQuery(q, Collections.<String> emptyList());
        StoredDocumentList results = wrapper.askDocument(query, false);

        getLogger().error("Results: " + results.toString());
        getLogger().error("Result count: " + results.size());
        Assert.assertEquals("result size", 100, results.size());

        for (StoredDocument result : results) {
            Document doc = result.getDocument();
            int year = doc.getYear();
            Assert.assertTrue(year >= minYear && year <= maxYear);
        }
    }


    /**
     * Tests for broken abstracts by contained mark-up.
     */
    @Test
    public void testAskDocumentBug1() {
        setTestId("bug1");
        QueryNodeBool d = new QueryNodeBool();
        addConditionToBool(d, Field.TEXT, "porno");
        final Query q = new DefaultQuery(d);
        DocumentQuery query = new DocumentQuery(q, Collections.<String> emptyList());
        StoredDocumentList results = wrapper.askDocument(query, false);

        getLogger().error("Results: " + results.toString());
        getLogger().error("Result count: " + results.size());
        Assert.assertEquals("result size", 6, results.size());

        StoredDocument stored = results.get(1);
        TextDocument doc = (TextDocument) stored.getDocument();
        Assert.assertFalse("abstract", doc.getAbstract().endsWith("a new method from the feature"));
        Assert.assertFalse("abstract", doc.getAbstract().endsWith("Read More»"));
    }


    /**
     * Tests for correct handling of an empty result set.
     */
    @Test
    public void testAskDocumentBug2() {
        setRecordingMode(false);
        setTestId("bug2");
        QueryNodeBool d = new QueryNodeBool();
        addConditionToBool(d, Field.TEXT, "keyworddoesntexistinanydocument");
        final Query q = new DefaultQuery(d);
        DocumentQuery query = new DocumentQuery(q, Collections.<String> emptyList());
        StoredDocumentList results = wrapper.askDocument(query, false);

        getLogger().error("Results: " + results.toString());
        getLogger().error("Result count: " + results.size());
        Assert.assertEquals("result size", 0, results.size());
        Assert.assertEquals("error count", 0, wrapper.getErrorCounter());
    }


    /**
     * Tests for correct handling of a defective page.
     */
    @Test
    public void testAskDocumentBug3() {
        setRecordingMode(false);
        setTestId("bug3");
        QueryNodeBool d = new QueryNodeBool();
        addConditionToBool(d, Field.TEXT, "keyworddoesntexistinanydocument");
        final Query q = new DefaultQuery(d);
        DocumentQuery query = new DocumentQuery(q, Collections.<String> emptyList());
        StoredDocumentList results = wrapper.askDocument(query, false);

        getLogger().error("Results: " + results.toString());
        getLogger().error("Result count: " + results.size());
        Assert.assertEquals("result size", 0, results.size());
        Assert.assertEquals("error count", 1, wrapper.getErrorCounter());
    }


    private void checkAskDocumentAutoId(Field field, String term, int resultCountExpected) {
        setTestId("ask-" + field + "-" + term);
        checkAskDocument(field, term, resultCountExpected);
    }


    private void checkAskDocument(Field field, String term, int resultCountExpected) {
        StoredDocumentList results = wrapper.askDocument(createDocumentQuery(field, term), false);

        getLogger().error("Results: " + results.toString());
        getLogger().error("Result count: " + results.size());

        Assert.assertEquals(term + ": result size", resultCountExpected, results.size());
    }

}
