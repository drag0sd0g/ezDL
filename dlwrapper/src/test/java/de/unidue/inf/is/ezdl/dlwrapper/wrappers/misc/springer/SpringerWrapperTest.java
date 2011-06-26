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

package de.unidue.inf.is.ezdl.dlwrapper.wrappers.misc.springer;

import java.util.Arrays;
import java.util.Date;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import de.unidue.inf.is.ezdl.dlbackend.data.StoredDocument;
import de.unidue.inf.is.ezdl.dlbackend.data.StoredDocumentList;
import de.unidue.inf.is.ezdl.dlbackend.mock.MockAgent;
import de.unidue.inf.is.ezdl.dlbackend.wrappers.SourceInfo;
import de.unidue.inf.is.ezdl.dlcore.DocumentFactory;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.Document;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.TextDocument;
import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;
import de.unidue.inf.is.ezdl.dlcore.data.query.DefaultQuery;
import de.unidue.inf.is.ezdl.dlcore.data.query.DocumentQuery;
import de.unidue.inf.is.ezdl.dlcore.data.query.Query;
import de.unidue.inf.is.ezdl.dlcore.data.query.QueryFactory;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeBool;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeCompare;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeCompare.Predicate;
import de.unidue.inf.is.ezdl.dlcore.query.YearRangeConverter;
import de.unidue.inf.is.ezdl.dlcore.query.YearRangeConverter.YearRange;
import de.unidue.inf.is.ezdl.dlwrapper.wrappers.ToolkitWrapperTestBase;



/**
 * Test suite for SpringerWrapper
 * 
 * @author at
 */
public class SpringerWrapperTest extends ToolkitWrapperTestBase {

    private MockAgent agent;
    private TestableToolkitAPI toolkit;
    private SpringerWrapper wrapper;
    private DocumentQuery query;


    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        agent = new MockAgent();
        toolkit = new TestableToolkitAPI("springer");
        wrapper = new SpringerWrapper(toolkit);

        wrapper.init(agent, null);
    }


    @Test
    public void test1() {
        setTestId("test1");

        query = QueryFactory.getTitleAndYearQuery(2000);
        wrapper.askDocument(query, false);
    }


    @Test
    public void testNullResults() {
        setTestId("testNullResults");

        QueryNodeBool d = new QueryNodeBool();
        d.addChild(new QueryNodeCompare(Field.AUTHOR, Predicate.EQ, "xxxxcccczzzz"));
        d.addChild(new QueryNodeCompare(Field.TITLE, Predicate.EQ, "xxxxcccczzzz"));
        final Query q = new DefaultQuery(d);
        DocumentQuery query = new DocumentQuery(q, Arrays.asList("foo"));

        StoredDocumentList result = wrapper.askDocument(query, false);

        Assert.assertEquals(0, result.size());
        Assert.assertEquals(0, wrapper.getErrorCounter());
    }


    @Test
    public void testBug413() {
        setTestId("testBug413");
        final QueryNodeCompare c = new QueryNodeCompare(Field.TEXT, Predicate.EQ, "test");
        final Query q = new DefaultQuery(c);
        DocumentQuery query = new DocumentQuery(q, Arrays.asList("foo"));

        StoredDocumentList result = wrapper.askDocument(query, false);

        Assert.assertEquals(29, result.size());
        Assert.assertEquals(1, wrapper.getErrorCounter());
    }


    @Test
    public void testDetails1() {
        setTestId("testDetails1");

        StoredDocumentList incomplete = new StoredDocumentList();
        Document indoc = DocumentFactory.createDocument("title", 2000, "author1", "author2");
        StoredDocument stored = new StoredDocument(indoc);
        String details = "/content/dbha3xmf6qrql6cd/";
        stored.addSource(new SourceInfo(wrapper.getSourceID(), details));
        incomplete.add(stored);

        TextDocument doc = (TextDocument) incomplete.get(0).getDocument();
        Assert.assertNull("abstract empty", doc.getAbstract());

        wrapper.askDetails(incomplete);

        Assert.assertEquals("title ok", "title", doc.getTitle());
        Assert.assertEquals("year ok", 2000, doc.getYear());
        Assert.assertEquals("author count ok", 2, doc.getAuthorList().size());
        Assert.assertTrue("abstract ok", doc.getAbstract().startsWith("When distributed, heterogeneous digital"));
        Assert.assertTrue("abstract ok", doc.getAbstract().endsWith("for an integrated solution."));
        Assert.assertEquals("journal ok", "Research and AdvancedTechnology for Digital Libraries",
                        doc.getFieldValue(Field.JOURNAL));
        Assert.assertEquals("volume ok", "2769/2003", doc.getFieldValue(Field.VOLUME));
        Assert.assertEquals("pages ok", "194-206", doc.getFieldValue(Field.PAGES));
    }


    @Test
    public void testDetails2() {
        setTestId("testDetails2");

        StoredDocumentList incomplete = new StoredDocumentList();
        Document indoc = DocumentFactory.createDocument("title", 2000, "author1", "author2");
        StoredDocument stored = new StoredDocument(indoc);
        String details = "/content/64r603qr3vl63890/";
        stored.addSource(new SourceInfo(wrapper.getSourceID(), details));
        incomplete.add(stored);

        TextDocument doc = (TextDocument) incomplete.get(0).getDocument();
        Assert.assertNull("abstract empty", doc.getAbstract());

        wrapper.askDetails(incomplete);

        Assert.assertEquals("title ok", "title", doc.getTitle());
        Assert.assertEquals("year ok", 2000, doc.getYear());
        Assert.assertEquals("author count ok", 2, doc.getAuthorList().size());
        Assert.assertTrue("abstract ok", doc.getAbstract().startsWith("Mathematical basis of interpretation"));
        Assert.assertTrue("abstract ok", doc.getAbstract().endsWith("three examples of NDE methods."));
        Assert.assertEquals("journal ok", "KSCE Journal of Civil Engineering", doc.getFieldValue(Field.JOURNAL));
        Assert.assertEquals("pages ok", "11-16", doc.getFieldValue(Field.PAGES));
        Assert.assertEquals("volume ok", "5", doc.getFieldValue(Field.VOLUME));
    }


    @Test
    public void testDetails3() {
        setTestId("testDetails3");

        StoredDocumentList incomplete = new StoredDocumentList();
        Document indoc = DocumentFactory.createDocument("title", 2000, "author1", "author2");
        StoredDocument stored = new StoredDocument(indoc);
        String details = "/content/q320j88up797x60n/";
        stored.addSource(new SourceInfo(wrapper.getSourceID(), details));
        incomplete.add(stored);

        TextDocument doc = (TextDocument) incomplete.get(0).getDocument();
        Assert.assertNull("abstract empty", doc.getAbstract());

        wrapper.askDetails(incomplete);

        Assert.assertEquals("title ok", "title", doc.getTitle());
        Assert.assertEquals("year ok", 2000, doc.getYear());
        Assert.assertEquals("author count ok", 2, doc.getAuthorList().size());
        Assert.assertTrue("abstract ok", doc.getAbstract().startsWith("Thyroid function and serum"));
        Assert.assertTrue("abstract ok", doc.getAbstract().endsWith("activity in breast milk."));
        Assert.assertEquals("journal ok", "European Journal of Pediatrics", doc.getFieldValue(Field.JOURNAL));
        Assert.assertEquals("volume ok", "145", doc.getFieldValue(Field.VOLUME));
        Assert.assertEquals("pages ok", "143-147", doc.getFieldValue(Field.PAGES));
    }


    /**
     * SpringerLink always searches for a date range, therefore single years
     * have to be converted to a range too. Day and month always have to be
     * added.
     */
    @Test
    public void testGetYearRangeParameter() {
        YearRange range = (new YearRangeConverter()).new YearRange();
        range.minYear = 2000;
        range.maxYear = 2000;
        String year = wrapper.getYearRangeParameter(range, new Date());

        Assert.assertEquals("db=20000101&de=20001231", year);
    }


    @Test
    public void testGetYearRangeParameter2() {
        YearRange range = (new YearRangeConverter()).new YearRange();
        range.minYear = 2000;
        range.maxYear = 2010;
        String year = wrapper.getYearRangeParameter(range, new Date());

        Assert.assertEquals("db=20000101&de=20101231", year);
    }


    @Test
    public void testGetYearRangeParameterNoYearRange() {
        YearRange range = (new YearRangeConverter()).new YearRange();
        range.minYear = null;
        range.maxYear = null;
        String year = wrapper.getYearRangeParameter(range, new Date());

        Assert.assertEquals(null, year);
    }

}
