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

package de.unidue.inf.is.ezdl.dlwrapper.wrappers.misc.wiley;

import java.util.Arrays;
import java.util.Date;
import java.util.Properties;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import de.unidue.inf.is.ezdl.dlbackend.data.StoredDocument;
import de.unidue.inf.is.ezdl.dlbackend.data.StoredDocumentList;
import de.unidue.inf.is.ezdl.dlbackend.mock.MockAgent;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.Document;
import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;
import de.unidue.inf.is.ezdl.dlcore.data.query.DefaultQuery;
import de.unidue.inf.is.ezdl.dlcore.data.query.DocumentQuery;
import de.unidue.inf.is.ezdl.dlcore.data.query.Query;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeBool;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeBool.NodeType;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeCompare;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeCompare.Predicate;
import de.unidue.inf.is.ezdl.dlcore.query.YearRangeConverter;
import de.unidue.inf.is.ezdl.dlcore.query.YearRangeConverter.YearRange;
import de.unidue.inf.is.ezdl.dlwrapper.wrappers.AbstractBasicToolkitWrapper;
import de.unidue.inf.is.ezdl.dlwrapper.wrappers.ToolkitWrapperTestBase;



/**
 * Test class for the {@link WileyWrapper}.
 * 
 * @author mjordan
 */
public class WileyWrapperTest extends ToolkitWrapperTestBase {

    private static final long TIMESTAMP_31_12_2010__12_12_12 = 1293819132000l;
    private static final Predicate EQ = Predicate.EQ;

    private MockAgent agent;

    private WileyWrapper wrapper;


    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        agent = new MockAgent();
        Properties props = new Properties();
        props.put(AbstractBasicToolkitWrapper.MAX_FETCH_RESULTS_KEY, "100");
        agent.init("mockagent", props);
        TestableToolkitAPI toolkit = new TestableToolkitAPI("wiley");
        wrapper = new WileyWrapper(toolkit);
        wrapper.init(agent, null);
    }


    @Test
    public void testNullResults() {
        setTestId("testNullResults");

        QueryNodeBool d = new QueryNodeBool();
        d.addChild(new QueryNodeCompare(Field.AUTHOR, EQ, "xxxxcccczzzz"));
        d.addChild(new QueryNodeCompare(Field.TITLE, EQ, "xxxxcccczzzz"));
        final Query q = new DefaultQuery(d);
        DocumentQuery query = new DocumentQuery(q, Arrays.asList("foo"));

        StoredDocumentList result = wrapper.askDocument(query, false);

        Assert.assertEquals("result size", 0, result.size());
        Assert.assertEquals("error counter", 0, wrapper.getErrorCounter());
    }


    @Test
    public void testDefectivePage() {
        setTestId("testDefectivePage");

        QueryNodeBool d = new QueryNodeBool();
        d.addChild(new QueryNodeCompare(Field.AUTHOR, EQ, "xxxxcccczzzz"));
        d.addChild(new QueryNodeCompare(Field.TITLE, EQ, "xxxxcccczzzz"));
        final Query q = new DefaultQuery(d);
        DocumentQuery query = new DocumentQuery(q, Arrays.asList(new String[] {
            "foo"
        }));

        StoredDocumentList result = wrapper.askDocument(query, false);

        Assert.assertEquals("result size", 0, result.size());
        Assert.assertEquals("error counter", 1, wrapper.getErrorCounter());
    }


    @Test
    public void test2() {
        setTestId("test2");

        DocumentQuery query = createDocumentQuery(Field.TITLE, "ranking");
        StoredDocumentList result = wrapper.askDocument(query, false);

        Assert.assertEquals("result size", 100, result.size());
        Assert.assertEquals("error counter", 0, wrapper.getErrorCounter());

        checkResult(result, "10.1111/j.1745-6592.1993.tb00076.x",
                        "Comparison of Federal Hazardous Waste Site Ranking Models Detailed", 1993);
        checkResult(result, "10.1111/1467-9310.00246",
                        "Analysis, ranking and selection of R&D projects in a portfolio", 2002);
        checkResult(result, "10.1002/div.2160", "Ranking by Return on Equity", 2005);
    }


    @Test
    public void test3() {
        setTestId("test3");

        QueryNodeBool d = new QueryNodeBool(NodeType.OR);
        d.addChild(new QueryNodeCompare(Field.TEXT, EQ, "libraries"));
        d.addChild(new QueryNodeCompare(Field.AUTHOR, EQ, "Fuhr"));
        final Query q = new DefaultQuery(d);
        DocumentQuery query = new DocumentQuery(q, Arrays.asList(new String[] {
            "foo"
        }));

        StoredDocumentList result = wrapper.askDocument(query, false);

        Assert.assertEquals("result size", 100, result.size());
        Assert.assertEquals("error counter", 0, wrapper.getErrorCounter());

        checkResult(result, "10.1002/(SICI)1097-0282(199908)50:2<111::AID-BIP1>3.0.CO;2-N",
                        "Improvement of side-chain modeling in proteins with the self-consistent mean field theory " + //
                                        "method based on an analysis of the factors influencing prediction", 1999);
    }


    /**
     * Tests for Bug #404 (Wrapper doesn't find anything for "test" in free
     * text).
     */
    @Test
    public void testBug404() {
        setTestId("testBug404");

        DocumentQuery query = createDocumentQuery(Field.TEXT, "test");
        StoredDocumentList result = wrapper.askDocument(query, false);

        Assert.assertEquals("result size", 100, result.size());
        Assert.assertEquals("error counter", 0, wrapper.getErrorCounter());

        checkResult(result, "10.1002/9783527624188.ch6", "Akuttoxizitätstest mit Daphnia magna", 2009);
    }


    @Test
    public void testYearRange0() {
        YearRangeConverter c = new YearRangeConverter();
        YearRange range = c.new YearRange();
        range.maxYear = 2000;
        String result = wrapper.getYearRangeParameter(range, new Date(TIMESTAMP_31_12_2010__12_12_12));
        Assert.assertEquals("output", "dateRange=between&startYear=1000&endYear=2000", result);
    }


    @Test
    public void testYearRange1() {
        YearRangeConverter c = new YearRangeConverter();
        YearRange range = c.new YearRange();
        range.minYear = 1900;
        String result = wrapper.getYearRangeParameter(range, new Date(TIMESTAMP_31_12_2010__12_12_12));
        Assert.assertEquals("output", "dateRange=between&startYear=1900&endYear=2010", result);
    }


    @Test
    public void testYearRange2() {
        YearRangeConverter c = new YearRangeConverter();
        YearRange range = c.new YearRange();
        range.minYear = 1900;
        range.maxYear = 2000;
        String result = wrapper.getYearRangeParameter(range, new Date(TIMESTAMP_31_12_2010__12_12_12));
        Assert.assertEquals("output", "dateRange=between&startYear=1900&endYear=2000", result);
    }


    @Test
    public void testYearRange3() {
        YearRangeConverter c = new YearRangeConverter();
        YearRange range = c.new YearRange();
        String result = wrapper.getYearRangeParameter(range, new Date(TIMESTAMP_31_12_2010__12_12_12));
        Assert.assertEquals("output", "dateRange=allDates&startYear=&endYear=", result);
    }


    /**
     * The following test is deactivated because Wiley doesn't seem to care much
     * about the date range - at least not in terms of document dates. It is
     * possible to get a document from 1987 with this query if it has been
     * published in 2005.
     */
    public void testYearRangeSearch() {
        setTestId("testYearRangeSearch");

        QueryNodeBool d = new QueryNodeBool();
        d.addChild(new QueryNodeCompare(Field.TEXT, EQ, "libraries"));
        d.addChild(new QueryNodeCompare(Field.YEAR, Predicate.GTE, "2003"));
        d.addChild(new QueryNodeCompare(Field.YEAR, Predicate.LTE, "2007"));
        final Query q = new DefaultQuery(d);
        DocumentQuery query = new DocumentQuery(q, Arrays.asList(new String[] {
            "foo"
        }));

        StoredDocumentList result = wrapper.askDocument(query, false);
        Assert.assertEquals("error counter", 0, wrapper.getErrorCounter());

        for (StoredDocument stored : result) {
            final Document doc = stored.getDocument();
            final int year = doc.getYear();
            if (year != Document.YEAR_INVALID) {
                Assert.assertTrue("year range ok for " + doc, (2003 <= year) && (year <= 2007));
            }
        }

    }


    private void checkResult(StoredDocumentList result, String doi, String title, int year) {
        final StoredDocument stored = result.findDocument(Field.DOI, doi);
        Assert.assertNotNull("doc found: " + doi, stored);
        final Document doc = stored.getDocument();
        Assert.assertNotNull(doc);
        Assert.assertEquals("title ok", title, doc.getFieldValue(Field.TITLE));
        Assert.assertEquals("year ok", year, doc.getFieldValue(Field.YEAR));
        Assert.assertNotNull("details URL", doc.getDetailURLs());
    }

}
