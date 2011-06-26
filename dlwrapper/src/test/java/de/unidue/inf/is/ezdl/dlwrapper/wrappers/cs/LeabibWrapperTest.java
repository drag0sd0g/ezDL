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

package de.unidue.inf.is.ezdl.dlwrapper.wrappers.cs;

import java.util.Collections;
import java.util.Properties;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import de.unidue.inf.is.ezdl.dlbackend.data.StoredDocument;
import de.unidue.inf.is.ezdl.dlbackend.data.StoredDocumentList;
import de.unidue.inf.is.ezdl.dlbackend.mock.MockAgent;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.Document;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.TextDocument;
import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;
import de.unidue.inf.is.ezdl.dlcore.data.query.DefaultQuery;
import de.unidue.inf.is.ezdl.dlcore.data.query.DocumentQuery;
import de.unidue.inf.is.ezdl.dlcore.data.query.Query;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeBool;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeCompare;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeCompare.Predicate;
import de.unidue.inf.is.ezdl.dlwrapper.wrappers.AbstractBasicToolkitWrapper;
import de.unidue.inf.is.ezdl.dlwrapper.wrappers.ToolkitWrapperTestBase;



/**
 * @author mjordan
 */
public class LeabibWrapperTest extends ToolkitWrapperTestBase {

    private MockAgent agent;
    private LeabibWrapper wrapper;


    // private DocumentQuery query;

    /**
     * @throws java.lang.Exception
     */
    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        agent = getAgent();

        TestableToolkitAPI api = new TestableToolkitAPI("leabib");
        // api.setProxy("test.ezdl.de", 3128);
        wrapper = new LeabibWrapper(api);
        wrapper.init(agent, null);
    }


    protected MockAgent getAgent() {
        if (agent == null) {
            agent = new MockAgent();
            Properties props = new Properties();
            props.setProperty("log", "off");
            props.setProperty(AbstractBasicToolkitWrapper.MAX_ERRORS_KEY, "3");
            props.setProperty(AbstractBasicToolkitWrapper.MAX_FETCH_PAGES_KEY, "3");
            agent.init("mockagent", props);
        }
        return agent;
    }


    @Test
    public void testTitleSearch1() {
        setTestId("titleSearch1");
        QueryNodeBool d = new QueryNodeBool();
        addConditionToBool(d, Field.TITLE, "algorithm");
        addConditionToBool(d, Field.TITLE, "design");
        final Query q = new DefaultQuery(d);
        DocumentQuery query = new DocumentQuery(q, Collections.<String> emptyList());
        StoredDocumentList results = wrapper.askDocument(query, false);

        getLogger().error("Results: " + results.toString());
        getLogger().error("Result count: " + results.size());
        Assert.assertEquals("result size", 43, results.size());

        StoredDocument stored = results.get(0);
        TextDocument doc = (TextDocument) stored.getDocument();
        Assert.assertEquals("authors count", 2, doc.getAuthorList().size());
        Assert.assertEquals("author", "Jon Kleinberg", doc.getAuthorList().get(0).toString());
        Assert.assertEquals("author", "Éva Tardos", doc.getAuthorList().get(1).toString());

        for (StoredDocument stored1 : results) {
            Document doc1 = stored1.getDocument();
            Assert.assertNotNull(doc1);
            final String title = doc1.getTitle();
            Assert.assertNotNull(title);
            Assert.assertTrue(title + " contains algorithm", title.toLowerCase().contains("algorithm"));
            Assert.assertTrue(title + " contains design", title.toLowerCase().contains("design"));
        }
    }


    /**
     * Tests if the bug is present that four-digit page numbers are interpreted
     * as years.
     */
    @Test
    public void testYearBug() {
        setTestId("titleYearBug");

        QueryNodeCompare d = new QueryNodeCompare(Field.TITLE, Predicate.EQ,
                        "A multi-dimensional version of the I test");
        final Query q = new DefaultQuery(d);
        DocumentQuery query = new DocumentQuery(q, Collections.<String> emptyList());
        StoredDocumentList results = wrapper.askDocument(query, false);

        getLogger().error("Results: " + results.toString());
        getLogger().error("Result count: " + results.size());
        Assert.assertEquals("result size", 1, results.size());

        StoredDocument stored = results.get(0);
        TextDocument doc = (TextDocument) stored.getDocument();
        Assert.assertEquals("year", 2001, doc.getYear());
    }

}
