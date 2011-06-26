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

package de.unidue.inf.is.ezdl.dlwrapper.wrappers.med;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import de.unidue.inf.is.ezdl.dlbackend.AbstractBackendTestBase;
import de.unidue.inf.is.ezdl.dlbackend.data.StoredDocument;
import de.unidue.inf.is.ezdl.dlbackend.data.StoredDocumentList;
import de.unidue.inf.is.ezdl.dlbackend.wrappers.SourceInfo;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.Document;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.Person;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.TextDocument;



public class PubMedWrapperTest extends AbstractBackendTestBase {

    private PubMedWrapper wrapper;


    @Before
    public void setUp() throws Exception {
        wrapper = new PubMedWrapper();
    }


    @Test
    public void testGetAuthor1() {
        Person author = new Person();
        author.setFirstName("WL");
        author.setLastName("Zhang");
        Assert.assertEquals(author, wrapper.authorFromString("Zhang WL"));
    }


    @Test
    public void testGetAuthor2() {
        Person author = new Person();
        author.setFirstName("RM");
        author.setLastName("van Dam");
        Assert.assertEquals(author, wrapper.authorFromString("van Dam RM"));
    }


    @Test
    public void testGetYear1() {
        Assert.assertEquals(2009, wrapper.getYear("2009 Jun"));
    }


    @Test
    public void testGetYear2() {
        Assert.assertEquals(2008, wrapper.getYear("2008 Aug"));
    }


    @Test
    public void testGetYear3() {
        Assert.assertEquals(2007, wrapper.getYear("2007"));
    }


    @Test
    public void testGetYear4() {
        Assert.assertEquals(1985, wrapper.getYear("1985 Mar 8"));
    }


    @Test
    public void testGetYear5() {
        Assert.assertEquals(2009, wrapper.getYear("2009/10"));
    }


    @Test
    public void testGetYear5a() {
        Assert.assertEquals(2009, wrapper.getYear("2009/10/01"));
    }


    @Test
    public void testGetYear6() {
        Assert.assertEquals(Document.YEAR_INVALID, wrapper.getYear("09/10/11"));
    }


    /**
     * This is not meant as an automatic test case.
     */
    public void testDetails() {
        PubMedWrapper wrapper = new PubMedWrapper();
        StoredDocumentList incomplete = new StoredDocumentList();
        TextDocument doc = new TextDocument();
        StoredDocument stored = new StoredDocument(doc);
        stored.addSource(new SourceInfo(wrapper.getSourceID(), "20593120"));
        incomplete.add(stored);
        wrapper.askDetails(incomplete);

        getLogger().debug("answer: " + incomplete);

        for (StoredDocument s : incomplete) {
            getLogger().debug("doc: " + s.getDocument().getDetailURLs());
        }
    }
}
