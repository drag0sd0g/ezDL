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

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.core.CoreContainer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import de.unidue.inf.is.ezdl.dlbackend.AbstractBackendTestBase;



public class BibDBSolrWrapperTest extends AbstractBackendTestBase {

    @SuppressWarnings("unused")
    private BibDBSolrWrapper bibDBSolrWrapper;


    @Before
    public void init() {
        System.setProperty("solr.solr.home", "");
        CoreContainer.Initializer initializer = new CoreContainer.Initializer();
        CoreContainer coreContainer;
        SolrServer solrServer;
        try {
            coreContainer = initializer.initialize();
            solrServer = new EmbeddedSolrServer(coreContainer, "c");
            bibDBSolrWrapper = new BibDBSolrWrapper(solrServer);
        }
        catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }


    @Ignore
    @Test
    public void testProcessQuery() {
        // TODO
    }


    @Ignore
    @Test
    public void testAskDocument() {
        // TODO
    }

}
