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

import junit.framework.Assert;

import org.apache.solr.common.SolrDocument;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import de.unidue.inf.is.ezdl.dlbackend.data.StoredDocument;
import de.unidue.inf.is.ezdl.dlcore.AbstractTestBase;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.Person;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.PersonList;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.TextDocument;
import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;



public class DBLPSolrWrapperTest extends AbstractTestBase {

    private DBLPSolrWrapper dblpSolrWrapper;


    @Before
    public void init() {
        // System.setProperty("solr.solr.home", "");
        // CoreContainer.Initializer initializer = new
        // CoreContainer.Initializer();
        // CoreContainer coreContainer;
        // SolrServer solrServer;
        // try {
        // coreContainer = initializer.initialize();
        // solrServer = new EmbeddedSolrServer(coreContainer, "c");
        // dblpSolrWrapper = new DBLPSolrWrapper(solrServer);
        // }
        // catch (Exception e) {
        // Assert.fail(e.getMessage());
        // }
        dblpSolrWrapper = new DBLPSolrWrapper();
    }


    @Test
    public void testProcessQuery1() {
        SolrDocument sd = getSolrDocument();
        StoredDocument stored = dblpSolrWrapper.readDocumentFromSolrIndex(sd);
        Assert.assertNotNull(stored);
        Assert.assertEquals("title", "Challenges for test and design for test.", stored.getDocument().getTitle());
        Assert.assertEquals("authors", "{PersonList [Anton Chichkov]}", stored.getDocument().getAuthorList().toString());
        Assert.assertEquals("year", 2009, stored.getDocument().getYear());
    }


    @Test
    public void testUpdateDocumentDetails() {
        SolrDocument sd = getSolrDocument();
        TextDocument doc = new TextDocument();
        dblpSolrWrapper.updateDocumentDetails(sd, doc);
        Assert.assertEquals("title", "DDECS", doc.getFieldValue(Field.BOOKTITLE));
        Assert.assertNotNull(doc.getDetailURLs());
        Assert.assertEquals("url count", 2, doc.getDetailURLs().size());
        Assert.assertEquals("url 1", "http://dx.doi.org/10.1109/DDECS.2009.5012086", doc.getDetailURLs().get(0)
                        .toString());
        Assert.assertEquals("url 2", "http://www.informatik.uni-trier.de/~ley/db/conf/ddecs/ddecs2009.html#Chichkov09",
                        doc.getDetailURLs().get(1).toString());
        Assert.assertEquals("pages", "3", doc.getFieldValue(Field.PAGES));
    }


    private SolrDocument getSolrDocument() {
        SolrDocument sd = new SolrDocument();
        sd.put("title", "Challenges for test and design for test.");
        sd.put("url", "db/conf/ddecs/ddecs2009.html#Chichkov09");
        sd.put("booktitle", "DDECS");
        sd.put("crossref", "conf/ddecs/2009");
        sd.put("ee", "http://dx.doi.org/10.1109/DDECS.2009.5012086");
        sd.put("key", "conf/ddecs/Chichkov09");
        sd.put("mdate", "2009-07-05T22:00:00Z");
        sd.put("pages", "3");
        sd.put("year", new Integer(2009));
        PersonList authors = new PersonList();
        authors.add(new Person("Anton Chichkov"));
        sd.put("author", authors);
        return sd;
    }


    @Ignore
    @Test
    public void testAskDocument() {
        // TODO
    }

}
