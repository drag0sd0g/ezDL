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

package de.unidue.inf.is.ezdl.dlbackend.data;

import java.util.Date;

import junit.framework.Assert;

import org.junit.Test;

import de.unidue.inf.is.ezdl.dlbackend.AbstractBackendTestBase;
import de.unidue.inf.is.ezdl.dlbackend.wrappers.SourceID;
import de.unidue.inf.is.ezdl.dlbackend.wrappers.SourceInfo;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.TextDocument;



public class StoredDocumentTest extends AbstractBackendTestBase {

    private static final String DETAILS_INFO = "detailsInfo";
    private static final SourceID SID = new SourceID("dl", "api");


    @Test
    public void testMergeBothSourceInfoWODate() {
        TextDocument doc = new TextDocument();

        StoredDocument stored1 = getStoredDoc(doc);

        StoredDocument stored2 = getStoredDoc(doc);

        stored1.merge(stored2);
        Assert.assertEquals("", 1, stored1.getSources().size());
        Assert.assertNull("", ((SourceInfo) stored1.getSources().toArray()[0]).getDetailTimestamp());
    }


    @Test
    public void testMergeSecondHasSourceInfoWithDate() {
        TextDocument doc = new TextDocument();

        StoredDocument stored1 = getStoredDoc(doc);

        StoredDocument stored2 = getStoredDoc(doc, new Date());

        stored1.merge(stored2);
        Assert.assertEquals("", 1, stored1.getSources().size());
        Assert.assertNotNull("", ((SourceInfo) stored1.getSources().toArray()[0]).getDetailTimestamp());
    }


    @Test
    public void testMergeBothHaveSourceInfoWithDate() {
        TextDocument doc = new TextDocument();

        StoredDocument stored1 = getStoredDoc(doc);
        stored1.addSource(new SourceInfo(SID, DETAILS_INFO, new Date(1000)));

        StoredDocument stored2 = getStoredDoc(doc, new Date(2000));

        stored1.merge(stored2);
        Assert.assertEquals("", 1, stored1.getSources().size());
        Assert.assertNotNull("", ((SourceInfo) stored1.getSources().toArray()[0]).getDetailTimestamp());
        Assert.assertEquals("", 2000, ((SourceInfo) stored1.getSources().toArray()[0]).getDetailTimestamp().getTime());
    }


    @Test
    public void testMergeFirstHasSourceInfoWithDate() {
        TextDocument doc = new TextDocument();

        StoredDocument stored1 = getStoredDoc(doc, new Date(1000));

        StoredDocument stored2 = getStoredDoc(doc);

        stored1.merge(stored2);
        Assert.assertEquals("", 1, stored1.getSources().size());
        Assert.assertNotNull("", ((SourceInfo) stored1.getSources().toArray()[0]).getDetailTimestamp());
        Assert.assertEquals("", 1000, ((SourceInfo) stored1.getSources().toArray()[0]).getDetailTimestamp().getTime());
    }


    private StoredDocument getStoredDoc(TextDocument doc) {
        StoredDocument stored1 = new StoredDocument(doc);
        stored1.addSource(new SourceInfo(SID, DETAILS_INFO));
        return stored1;
    }


    private StoredDocument getStoredDoc(TextDocument doc, Date date) {
        StoredDocument stored2 = new StoredDocument(doc);
        stored2.addSource(new SourceInfo(SID, DETAILS_INFO, date));
        return stored2;
    }
}
