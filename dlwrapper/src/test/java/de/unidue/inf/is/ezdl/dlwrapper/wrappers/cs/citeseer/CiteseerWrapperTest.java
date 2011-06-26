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

package de.unidue.inf.is.ezdl.dlwrapper.wrappers.cs.citeseer;

import java.net.URL;
import java.util.Arrays;

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
import de.unidue.inf.is.ezdl.dlwrapper.wrappers.ToolkitWrapperTestBase;



/**
 * Test suite for CiteseerWrapper
 * 
 * @author tacke
 */
public class CiteseerWrapperTest extends ToolkitWrapperTestBase {

    private MockAgent agent;
    private TestableToolkitAPI toolkit;
    private CiteseerWrapper wrapper;
    private DocumentQuery query;


    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        agent = new MockAgent();
        toolkit = new TestableToolkitAPI("citeseer");
        wrapper = new CiteseerWrapper(toolkit);

        wrapper.init(agent, null);
    }


    @Test
    public void testSessionIDs() {
        setTestId("testSessionIDs");

        query = QueryFactory.getTitleAndYearQuery(2000);
        StoredDocumentList result = wrapper.askDocument(query, false);
        for (StoredDocument stored : result) {
            for (URL url : stored.getDocument().getDetailURLs()) {
                final String urlStr = url.toString();
                Assert.assertFalse("no session id found", urlStr.matches(".*jsessionid.*"));
            }
        }
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
    public void testDetails1() {
        setTestId("testDetails1");

        StoredDocumentList incomplete = new StoredDocumentList();
        Document indoc = DocumentFactory.createDocument("title", 1998, "author1", "author2");
        StoredDocument stored = new StoredDocument(indoc);
        String details = "/viewdoc/summary?doi=10.1.1.160.6685";
        stored.addSource(new SourceInfo(wrapper.getSourceID(), details));
        incomplete.add(stored);

        TextDocument doc = (TextDocument) incomplete.get(0).getDocument();

        wrapper.askDetails(incomplete);

        Assert.assertNull("journal empty", doc.getFieldValue(Field.JOURNAL));
        Assert.assertTrue("abstract ok", doc.getAbstract().startsWith("A probabilistic justification"));
        Assert.assertEquals("title ok", "title", doc.getTitle());
        Assert.assertEquals("year ok", 1998, doc.getYear());
        Assert.assertEquals("author count ok", 2, doc.getAuthorList().size());
        // Assert.assertEquals("pages ok", "194-206",
        // doc.getFieldValue(Field.PAGES));
    }


    @Test
    public void testDetails2() {
        setTestId("testDetails2");

        StoredDocumentList incomplete = new StoredDocumentList();
        Document indoc = DocumentFactory.createDocument("title", 2001, "author1", "author2");
        StoredDocument stored = new StoredDocument(indoc);
        String details = "/viewdoc/summary?doi=10.1.1.19.9400";
        stored.addSource(new SourceInfo(wrapper.getSourceID(), details));
        incomplete.add(stored);

        TextDocument doc = (TextDocument) incomplete.get(0).getDocument();
        Assert.assertNull("abstract empty", doc.getAbstract());

        wrapper.askDetails(incomplete);

        Assert.assertEquals("title ok", "title", doc.getTitle());
        Assert.assertEquals("year ok", 2001, doc.getYear());
        Assert.assertEquals("author count ok", 2, doc.getAuthorList().size());
        Assert.assertTrue("abstract ok", doc.getAbstract()
                        .startsWith("Drawing on the correspondence between the graph"));
        Assert.assertTrue("abstract ok", doc.getAbstract().trim().endsWith("Several applications  are considered."));
        Assert.assertEquals("journal ok", "Advances in Neural Information Processing Systems 14",
                        doc.getFieldValue(Field.JOURNAL));
    }


    @Test
    public void testDetails3() {
        setTestId("testDetails3");

        StoredDocumentList incomplete = new StoredDocumentList();
        Document indoc = DocumentFactory.createDocument("title", 2004, "author1", "author2");
        StoredDocument stored = new StoredDocument(indoc);
        String details = "/viewdoc/summary?doi=10.1.1.78.2757";
        stored.addSource(new SourceInfo(wrapper.getSourceID(), details));
        incomplete.add(stored);

        TextDocument doc = (TextDocument) incomplete.get(0).getDocument();
        Assert.assertNull("abstract empty", doc.getAbstract());

        wrapper.askDetails(incomplete);

        Assert.assertEquals("title ok", "title", doc.getTitle());
        Assert.assertEquals("year ok", 2004, doc.getYear());
        Assert.assertEquals("author count ok", 2, doc.getAuthorList().size());
        Assert.assertTrue("abstract ok", doc.getAbstract().startsWith("We consider the general problem"));
        Assert.assertTrue("abstract ok", doc.getAbstract().endsWith("and text classification. 1."));
        Assert.assertEquals("journal ok", "Machine Learning", doc.getFieldValue(Field.JOURNAL));
    }

}
