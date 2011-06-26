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

package de.unidue.inf.is.ezdl.dlwrapper.wrappers.cs.acm;

import java.util.Collections;
import java.util.Properties;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import de.unidue.inf.is.ezdl.dlbackend.data.StoredDocument;
import de.unidue.inf.is.ezdl.dlbackend.data.StoredDocumentList;
import de.unidue.inf.is.ezdl.dlbackend.mock.MockAgent;
import de.unidue.inf.is.ezdl.dlbackend.wrappers.SourceInfo;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.TextDocument;
import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;
import de.unidue.inf.is.ezdl.dlcore.data.query.DefaultQuery;
import de.unidue.inf.is.ezdl.dlcore.data.query.DocumentQuery;
import de.unidue.inf.is.ezdl.dlcore.data.query.Query;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeBool;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeCompare;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeCompare.Predicate;
import de.unidue.inf.is.ezdl.dlwrapper.wrappers.ToolkitWrapperTestBase;



/**
 * @author mjordan
 */
public class ACMWrapperTest extends ToolkitWrapperTestBase {

    private MockAgent agent;
    private ACMWrapper wrapper;
    private DocumentQuery query;


    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        super.setUp();
        agent = getAgent();

        TestableToolkitAPI api = new TestableToolkitAPI("acm");
        // api.setProxy("test.ezdl.de", 3128);
        wrapper = new ACMWrapper(api);
        wrapper.init(agent, null);
    }


    protected MockAgent getAgent() {
        if (agent == null) {
            agent = new MockAgent();
            Properties props = new Properties();
            props.setProperty("log", "off");
            props.setProperty(ACMWrapper.MAX_ERRORS_KEY, "3");
            props.setProperty(ACMWrapper.MAX_FETCH_PAGES_KEY, "3");
            agent.init("mockagent", props);
        }
        return agent;
    }


    /**
     * Test method for
     * {@link de.unidue.inf.is.ezdl.dlwrapper.wrappers.AbstractWrapper#askDocument(de.unidue.inf.is.ezdl.dlcore.data.query.DocumentQuery, boolean)}
     * with author searches.
     */
    @Test
    public void testAskDocumentAuthor1() {
        checkAskDocumentAutoId(Field.AUTHOR, "Norbert Fuhr", 60);
    }


    /**
     * Test method for
     * {@link de.unidue.inf.is.ezdl.dlwrapper.wrappers.AbstractWrapper#askDocument(de.unidue.inf.is.ezdl.dlcore.data.query.DocumentQuery, boolean)}
     * with author searches.
     */
    @Test
    public void testAskDocumentAuthor2() {
        checkAskDocumentAutoId(Field.AUTHOR, "Nick Belkin", 24);
    }


    @Test
    public void testAskDocumentAuthor3() {
        checkAskDocumentAutoId(Field.AUTHOR, "Edith Law", 4);
    }


    /**
     * Test method for
     * {@link de.unidue.inf.is.ezdl.dlwrapper.wrappers.AbstractWrapper#askDocument(de.unidue.inf.is.ezdl.dlcore.data.query.DocumentQuery, boolean)}
     * .
     */
    @Test
    public void testAskDocumentTitle() {
        setTestId("askDocumentTitle");
        checkAskDocumentAutoId(Field.TITLE, "information retrieval", 60);
    }


    /**
     * Test method for
     * {@link de.unidue.inf.is.ezdl.dlwrapper.wrappers.AbstractWrapper#askDocument(de.unidue.inf.is.ezdl.dlcore.data.query.DocumentQuery, boolean)}
     * . Addresses bug #283
     */
    @Test
    public void testAskDocumentTitle2() {
        setTestId("askDocumentTitle2");
        QueryNodeBool d = new QueryNodeBool();
        addConditionToBool(d, Field.TITLE, "natural language");
        addConditionToBool(d, Field.TITLE, "processing system");
        addConditionToBool(d, Field.TITLE, "user needs");
        addConditionToBool(d, Field.TITLE, "information retrieval");
        Query q = new DefaultQuery(d);
        DocumentQuery query = new DocumentQuery(q, Collections.<String> emptyList());
        StoredDocumentList results = wrapper.askDocument(query, false);

        getLogger().error("Results: " + results.toString());
        getLogger().error("Result count: " + results.size());
        Assert.assertEquals("result size", 1, results.size());

        StoredDocument stored = results.get(0);
        TextDocument doc = (TextDocument) stored.getDocument();
        Assert.assertEquals("authors count", 1, doc.getAuthorList().size());
        Assert.assertEquals("author", "David Andrew Campbell", doc.getAuthorList().get(0).toString());
    }


    /**
     * Test method for queries that consist of only a YEAR clause that cannot be
     * translated to an ACM query.
     */
    @Test
    public void testAskYearOnly() {
        setTestId("askYearOnly");
        setRecordingMode(true);
        QueryNodeCompare d = new QueryNodeCompare(Field.YEAR, Predicate.EQ, "2000");
        Query q = new DefaultQuery(d);
        DocumentQuery query = new DocumentQuery(q, Collections.<String> emptyList());
        StoredDocumentList results = wrapper.askDocument(query, false);

        getLogger().error("Results: " + results.toString());
        getLogger().error("Result count: " + results.size());
        Assert.assertEquals("result size", 0, results.size());
        Assert.assertEquals("errors", 0, wrapper.getErrorCounter());
    }


    /**
     * Tests a search for a string with special characters.
     */
    @Test
    public void testAskSpecialCharacters() {
        setTestId("askSpecialCharacters");
        checkAskDocument(Field.AUTHOR, "Cinnéide", 16, 0);
    }


    /**
     * Tests the behavior against unexpected HTML.
     */
    @Test
    public void testErrorCounter_unexpectedHTML() {
        setTestId("errorCounter_unexpectedHTML");
        checkAskDocument(Field.AUTHOR, "Belkin", 0, 1);
    }


    /**
     * Tests for correct processing of input that lacks an authors element.
     * Expected is: drop the defective document and increase the error counter.
     */
    @Test
    public void testErrorCounter_noAuthors() {
        setTestId("errorCounter_noAuthors");
        checkAskDocument(Field.AUTHOR, "Belkin", 59, 1);
    }


    /**
     * Tests if the agent is halted if the input contains too many errors -- in
     * this case missing author name fields in 4 results.
     */
    @Test
    public void testErrorCounter_tooManyErrors() {
        setTestId("errorCounter_tooManyErrors");
        StoredDocumentList results = wrapper.askDocument(createDocumentQuery(Field.AUTHOR, "Belkin"), false);

        Assert.assertEquals("error count", 4, wrapper.getErrorCounter());
        Assert.assertEquals("result empty", 0, results.size());
        Assert.assertEquals("agent halted", true, agent.isHalted());
    }


    /**
     * Connection error: the result is empty but the agent is not halted.
     */
    @Test
    public void testErrorCounter_connectionError() {
        setTestId("errorCounter_connectionError");
        setSimulateConnectionProblem(true);
        StoredDocumentList results = wrapper.askDocument(createDocumentQuery(Field.AUTHOR, "Belkin"), false);

        Assert.assertEquals("error count", 1, wrapper.getErrorCounter());
        Assert.assertEquals("result empty", 0, results.size());
        Assert.assertEquals("agent halted", false, agent.isHalted());
    }


    /**
     * This tests if getting a short result list with only one page (and, thus,
     * no links to following pages) does not result in an error.
     */
    @Test
    public void testShortResultList() {
        setTestId("shortResultList");
        QueryNodeBool d = new QueryNodeBool();
        addConditionToBool(d, Field.AUTHOR, "fuhr");
        addConditionToBool(d, Field.TITLE, "models");
        Query q = new DefaultQuery(d);
        query = new DocumentQuery(q, Collections.<String> emptyList());
        StoredDocumentList results = wrapper.askDocument(query, false);

        getLogger().error("Results: " + results.toString());
        getLogger().error("Result count: " + results.size());

        Assert.assertEquals("no error logged", 0, wrapper.getErrorCounter());
    }


    /**
     * Tests the parsing of a valid details page.
     */
    @Test
    public void testAskDetails1() {
        setTestId("askDetails1");
        StoredDocumentList incomplete = new StoredDocumentList();
        StoredDocument stored = new StoredDocument(new TextDocument());
        String details = "citation.cfm?id=1063266.1063268&coll=GUIDE,GUIDE&dl=GUIDE," //
                        + "GUIDE&CFID=85804683&CFTOKEN=37722408";
        stored.addSource(new SourceInfo(wrapper.getSourceID(), details));
        incomplete.add(stored);

        TextDocument doc = (TextDocument) incomplete.get(0).getDocument();
        Assert.assertNull("abstract empty", doc.getAbstract());

        wrapper.askDetails(incomplete);

        Assert.assertNotNull("abstract full", doc.getAbstract());
        Assert.assertTrue("abstract contains correct text", doc.getAbstract().startsWith("This introductory paper"));
        Assert.assertEquals("title contains correct text", "Cross-language information retrieval: the way ahead",
                        doc.getTitle());
    }


    /**
     * Tests the parsing of a valid details page.
     */
    @Test
    public void testAskDetails2() {
        setTestId("askDetails2");
        StoredDocumentList incomplete = new StoredDocumentList();
        StoredDocument stored = new StoredDocument(new TextDocument());
        String details = "citation.cfm?id=567292.567294&coll=GUIDE&dl=GUIDE&CFID=86246997&CFTOKEN=90854599";
        stored.addSource(new SourceInfo(wrapper.getSourceID(), details));
        incomplete.add(stored);

        TextDocument doc = (TextDocument) incomplete.get(0).getDocument();

        Assert.assertNull("abstract empty", doc.getAbstract());

        wrapper.askDetails(incomplete);

        Assert.assertNotNull("abstract full", doc.getAbstract());
        Assert.assertTrue("abstract contains correct text", doc.getAbstract().startsWith("Retrieval models form"));
        Assert.assertTrue("title contains correct text", doc.getTitle().equals("Models in information retrieval"));
    }


    /**
     * Tests the parsing of a valid details page.
     */
    @Test
    public void testAskDetails3() {
        setTestId("askDetails3");
        StoredDocumentList incomplete = new StoredDocumentList();
        StoredDocument stored = new StoredDocument(new TextDocument());
        String details = "citation.cfm?id=1141753.1141764&coll=GUIDE&dl=GUIDE&CFID=86246997&CFTOKEN=90854599";
        stored.addSource(new SourceInfo(wrapper.getSourceID(), details));
        incomplete.add(stored);

        TextDocument doc = (TextDocument) incomplete.get(0).getDocument();

        Assert.assertNull("abstract empty", doc.getAbstract());

        wrapper.askDetails(incomplete);

        Assert.assertNotNull("abstract full", doc.getAbstract());
        Assert.assertTrue("abstract contains correct text", doc.getAbstract().startsWith("In this paper we introduce"));
        Assert.assertTrue(
                        "title contains correct text",
                        doc.getTitle()
                                        .equals("Probabilistic, object-oriented logics for annotation-based retrieval in digital libraries"));
    }


    /**
     * Tests the parsing of a valid details page.
     */
    @Test
    public void testAskDetails4() {
        setTestId("askDetails4");
        StoredDocumentList incomplete = new StoredDocumentList();
        StoredDocument stored = new StoredDocument(new TextDocument());
        String details = "citation.cfm?id=1527090.1527107&coll=GUIDE,GUIDE&dl=GUIDE,GUIDE&CFID=86534004&CFTOKEN=66373750";
        stored.addSource(new SourceInfo(wrapper.getSourceID(), details));
        incomplete.add(stored);

        TextDocument doc = (TextDocument) incomplete.get(0).getDocument();

        Assert.assertNull("abstract empty", doc.getAbstract());

        wrapper.askDetails(incomplete);

        Assert.assertNotNull("abstract full", doc.getAbstract());
        Assert.assertTrue("abstract contains correct text", doc.getAbstract()
                        .startsWith("Knowledge Management Systems"));
        Assert.assertTrue("title contains correct text", doc.getTitle().equals(
                        "The role of trust in promoting organizational knowledge seeking "
                                        + "using knowledge management systems: An empirical investigation"));
    }


    /**
     * Tests the parsing of a valid details page.
     */
    @Test
    public void testAskDetails5() {
        setTestId("askDetails5");
        StoredDocumentList incomplete = new StoredDocumentList();
        StoredDocument stored = new StoredDocument(new TextDocument());
        String details = "citation.cfm?id=283320.283341&coll=GUIDE,GUIDE&dl=GUIDE,GUIDE&CFID=84662402&CFTOKEN=93120781";
        stored.addSource(new SourceInfo(wrapper.getSourceID(), details));
        incomplete.add(stored);

        TextDocument doc = (TextDocument) incomplete.get(0).getDocument();

        final String abstract1 = doc.getAbstract();
        Assert.assertNull("abstract empty", abstract1);

        wrapper.askDetails(incomplete);

        Assert.assertNull("abstract still empty", abstract1);
        Assert.assertEquals("one author", 1, doc.getAuthorList().size());
        Assert.assertEquals("author name correct", "Donal O'Regan", doc.getAuthorList().get(0).asString());
        Assert.assertTrue("title contains correct text",
                        doc.getTitle().equals("Random fixed point theory with applications"));
    }


    /**
     * Tests the parsing of a valid details page.
     */
    @Test
    public void testAskDetails6() {
        setTestId("askDetails6");
        StoredDocumentList incomplete = new StoredDocumentList();
        StoredDocument stored = new StoredDocument(new TextDocument());
        String details = "citation.cfm?id=892007&coll=GUIDE&dl=GUIDE&CFID=85062804&CFTOKEN=19711076";
        stored.addSource(new SourceInfo(wrapper.getSourceID(), details));
        incomplete.add(stored);

        TextDocument doc = (TextDocument) incomplete.get(0).getDocument();

        Assert.assertNull("abstract empty", doc.getAbstract());

        wrapper.askDetails(incomplete);

        Assert.assertNotNull("abstract full", doc.getAbstract());
        Assert.assertEquals("one author", 1, doc.getAuthorList().size());
        Assert.assertEquals("author name correct", "Donald E. Knuth", doc.getAuthorList().get(0).asString());
        Assert.assertFalse("abstract not empty", doc.getAbstract().isEmpty());
        Assert.assertTrue("title contains correct text",
                        doc.getTitle().equals("Sorting and Searching - errata and addenda."));
    }


    /**
     * Tests the parsing of a valid details page.
     */
    @Test
    public void testAskDetails7() {
        setTestId("askDetails7");
        StoredDocumentList incomplete = new StoredDocumentList();
        StoredDocument stored = new StoredDocument(new TextDocument());
        String details = "citation.cfm?id=1241109.1241324&coll=GUIDE,GUIDE&dl=GUIDE,GUIDE&CFID=88520934&CFTOKEN=22198051";
        stored.addSource(new SourceInfo(wrapper.getSourceID(), details));
        incomplete.add(stored);

        TextDocument doc = (TextDocument) incomplete.get(0).getDocument();

        Assert.assertNull("abstract empty", doc.getAbstract());

        wrapper.askDetails(incomplete);

        Assert.assertNotNull("abstract full", doc.getAbstract());
        Assert.assertTrue(
                        "abstract right",
                        doc.getAbstract()
                                        .startsWith("Through the recent NTCIR workshops, patent retrieval casts many challenging issues"));
        Assert.assertEquals("four authors", 4, doc.getAuthorList().size());
        Assert.assertEquals("author name 1 correct", "In-Su Kang", doc.getAuthorList().get(0).asString());
        Assert.assertEquals("author name 2 correct", "Seung-Hoon Na", doc.getAuthorList().get(1).asString());
        Assert.assertEquals("author name 3 correct", "Jungi Kim", doc.getAuthorList().get(2).asString());
        Assert.assertEquals("author name 4 correct", "Jong-Hyeok Lee", doc.getAuthorList().get(3).asString());
        Assert.assertFalse("abstract not empty", doc.getAbstract().isEmpty());
        Assert.assertTrue("title contains correct text", doc.getTitle().equals("Cluster-based patent retrieval"));
    }


    /**
     * Tests the parsing of a valid details page that has an author and an
     * advisor, latter one of which we don't want to be included in the authors
     * list. Addresses bug #283.
     */
    @Test
    public void testAskDetails8() {
        setTestId("askDetails8");
        StoredDocumentList incomplete = new StoredDocumentList();
        StoredDocument stored = new StoredDocument(new TextDocument());
        String details = "citation.cfm?id=1104332&coll=GUIDE&dl=GUIDE&CFID=90068445&CFTOKEN=78328421";
        stored.addSource(new SourceInfo(wrapper.getSourceID(), details));
        incomplete.add(stored);

        TextDocument doc = (TextDocument) incomplete.get(0).getDocument();

        Assert.assertNull("abstract empty", doc.getAbstract());

        wrapper.askDetails(incomplete);

        Assert.assertNotNull("abstract full", doc.getAbstract());
        Assert.assertTrue("abstract right", doc.getAbstract().startsWith("An important goal"));
        Assert.assertEquals("one author", 1, doc.getAuthorList().size());
        Assert.assertEquals("author name 1 correct", "David Andrew Campbell", doc.getAuthorList().get(0).asString());
        Assert.assertFalse("abstract not empty", doc.getAbstract().isEmpty());
        Assert.assertTrue(
                        "title contains correct text",
                        doc.getTitle()
                                        .equals("A natural language processing system to assess user needs in information retrieval"));
    }


    /**
     * Tests the parsing of a valid details page that has a full-text link.
     */
    @Test
    public void testAskDetails9() {
        setTestId("askDetails9");
        StoredDocumentList incomplete = new StoredDocumentList();
        StoredDocument stored = new StoredDocument(new TextDocument());
        String details = "citation.cfm?id=1364742.1364763&coll=DL&dl=GUIDE&CFID=113145429&CFTOKEN=12958527";
        stored.addSource(new SourceInfo(wrapper.getSourceID(), details));
        incomplete.add(stored);

        TextDocument doc = (TextDocument) incomplete.get(0).getDocument();

        Assert.assertNull("abstract empty", doc.getAbstract());

        wrapper.askDetails(incomplete);

        Assert.assertNotNull("abstract full", doc.getAbstract());
        Assert.assertFalse("abstract not empty", doc.getAbstract().isEmpty());
        Assert.assertTrue("abstract right", doc.getAbstract().startsWith("We give a survey"));
        Assert.assertEquals("two authors", 2, doc.getAuthorList().size());
        Assert.assertEquals("author name 1 correct", "Norbert Fuhr", doc.getAuthorList().get(0).asString());
        Assert.assertTrue("title contains correct text",
                        doc.getTitle().equals("Advances in XML retrieval: the INEX initiative"));
        // Assert.assertTrue("details PDF", doc.getFieldValue(Field.URLS) !=
        // null);
    }


    /**
     * Tests the parsing of a valid details page.
     */
    // @Test is inactive because this situation is currently not handled
    // anywhere
    public void testAskDetailsJournal() {
        setTestId("askDetailsJournal");
        StoredDocumentList incomplete = new StoredDocumentList();
        StoredDocument stored = new StoredDocument(new TextDocument());
        String details = "citation.cfm?id=1240326&coll=GUIDE,GUIDE&dl=GUIDE,GUIDE&CFID=84662402&CFTOKEN=93120781";
        stored.addSource(new SourceInfo(wrapper.getSourceID(), details));
        incomplete.add(stored);

        TextDocument doc = (TextDocument) incomplete.get(0).getDocument();

        Assert.assertNull("abstract empty", doc.getAbstract());

        wrapper.askDetails(incomplete);

        Assert.assertNotNull("abstract full", doc.getAbstract());
        Assert.assertTrue("abstract contains correct text", doc.getAbstract()
                        .startsWith("Knowledge Management Systems"));
        Assert.assertTrue("title contains correct text", doc.getTitle().equals(
                        "The role of trust in promoting organizational knowledge seeking "
                                        + "using knowledge management systems: An empirical investigation"));
    }


    /**
     * Tests the parsing of a defective details page.
     */
    @Test
    public void testAskDetailsDefective1() {
        setTestId("askDetailsDefective1");
        StoredDocumentList incomplete = new StoredDocumentList();
        StoredDocument stored = new StoredDocument(new TextDocument());
        String details = "citation.cfm?id=567292.567294&coll=GUIDE&dl=GUIDE&CFID=86246997&CFTOKEN=90854599";
        stored.addSource(new SourceInfo(wrapper.getSourceID(), details));
        incomplete.add(stored);

        TextDocument doc = (TextDocument) incomplete.get(0).getDocument();

        Assert.assertNull("abstract empty", doc.getAbstract());

        wrapper.askDetails(incomplete);

        Assert.assertNull("abstract still empty", doc.getAbstract());
        Assert.assertEquals("error counter", 1, wrapper.getErrorCounter());
    }


    /**
     * Tests if broken HTML tags like "<td </td>" are repaired correctly.
     */
    @Test
    public void testHtmlDefective() {
        setTestId("brokenHtmlTags");
        checkAskDocument(Field.AUTHOR, "Norbert Fuhr", 0, 0);
    }


    private void checkAskDocumentAutoId(Field field, String term, int resultCountExpected) {
        setTestId("ask-" + field + "-" + term);
        checkAskDocument(field, term, resultCountExpected, 0);
    }


    private void checkAskDocument(Field field, String term, int resultCountExpected, int errorCountExpected) {
        StoredDocumentList results = wrapper.askDocument(createDocumentQuery(field, term), false);

        getLogger().error("Results: " + results.toString());
        getLogger().error("Result count: " + results.size());

        Assert.assertEquals(term + ": result size", resultCountExpected, results.size());
        Assert.assertEquals("error counter", errorCountExpected, wrapper.getErrorCounter());
    }

}
