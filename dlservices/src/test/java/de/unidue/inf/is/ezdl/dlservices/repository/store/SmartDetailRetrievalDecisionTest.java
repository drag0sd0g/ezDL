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

package de.unidue.inf.is.ezdl.dlservices.repository.store;

import java.util.Calendar;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import de.unidue.inf.is.ezdl.dlbackend.AbstractBackendTestBase;
import de.unidue.inf.is.ezdl.dlbackend.BackendDocumentFactory;
import de.unidue.inf.is.ezdl.dlbackend.data.StoredDocument;
import de.unidue.inf.is.ezdl.dlbackend.wrappers.SourceID;
import de.unidue.inf.is.ezdl.dlbackend.wrappers.SourceInfo;
import de.unidue.inf.is.ezdl.dlcore.data.OIDFactory;
import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;



public class SmartDetailRetrievalDecisionTest extends AbstractBackendTestBase {

    private static final String FULL_ABSTRACT_CONTENT = "abstract";

    private SmartDetailRetrievalDecision strategy = new SmartDetailRetrievalDecision();


    @Test
    public void testNonFullRecentDocument() {
        StoredDocument document = createNewStoredNonFullDocument(calcCurrentYear());
        Assert.assertTrue(strategy.detailRetrievalSensible(document));
    }


    @Test
    public void testNonFullBorderlineUnrecent1() {
        final int offset = SmartDetailRetrievalDecision.PUBLISHING_DATE_THRESHOLD_YEARS;
        StoredDocument document = createNewStoredNonFullDocument(calcCurrentYear() - (offset - 1));
        Assert.assertTrue(strategy.detailRetrievalSensible(document));
    }


    @Test
    public void testNonFullBorderlineUnrecent2() {
        final int offset = SmartDetailRetrievalDecision.PUBLISHING_DATE_THRESHOLD_YEARS;
        StoredDocument document = createNewStoredNonFullDocument(calcCurrentYear() - offset);
        Assert.assertFalse(strategy.detailRetrievalSensible(document));
    }


    @Test
    public void testNonFullBorderlineUnrecent3() {
        final int offset = SmartDetailRetrievalDecision.PUBLISHING_DATE_THRESHOLD_YEARS;
        StoredDocument document = createNewStoredNonFullDocument(calcCurrentYear() - (offset + 1));
        Assert.assertFalse(strategy.detailRetrievalSensible(document));
    }


    @Test
    public void testNonFullUnrecentAndNoSourceInfo() {
        final int offset = SmartDetailRetrievalDecision.PUBLISHING_DATE_THRESHOLD_YEARS;
        StoredDocument document = createNewStoredNonFullDocument(calcCurrentYear() - (offset + 1));
        document.getSources().clear();
        document.addSource(new SourceInfo(new SourceID("id2", "a"), "detail", null));
        Assert.assertTrue(strategy.detailRetrievalSensible(document));
    }


    @Test
    public void testNonFullUnrecentAndOldSourceInfo() {
        final int offset = SmartDetailRetrievalDecision.PUBLISHING_DATE_THRESHOLD_YEARS;
        StoredDocument document = createNewStoredNonFullDocument(calcCurrentYear() - (offset + 1));
        document.getSources().clear();
        document.addSource(new SourceInfo(new SourceID("id2", "a"), "detail", new Date(1l)));
        Assert.assertFalse(strategy.detailRetrievalSensible(document));
    }


    @Test
    public void testNonFullUnrecentAndRecentSourceInfo() {
        final int offset = SmartDetailRetrievalDecision.PUBLISHING_DATE_THRESHOLD_YEARS;
        StoredDocument document = createNewStoredNonFullDocument(calcCurrentYear() - (offset + 1));
        document.getSources().clear();
        document.addSource(new SourceInfo(new SourceID("id2", "a"), "detail", new Date()));
        Assert.assertFalse(strategy.detailRetrievalSensible(document));
    }


    @Test
    public void testNonFullRecentAndOldSourceInfo() {
        StoredDocument document = createNewStoredNonFullDocument(calcCurrentYear());
        document.getSources().clear();
        document.addSource(new SourceInfo(new SourceID("id2", "a"), "detail", new Date(1l)));
        Assert.assertTrue(strategy.detailRetrievalSensible(document));
    }


    @Test
    public void testNonFullRecentAndRecentSourceInfo() {
        StoredDocument document = createNewStoredNonFullDocument(calcCurrentYear());
        document.getSources().clear();
        document.addSource(new SourceInfo(new SourceID("id2", "a"), "detail", new Date()));
        Assert.assertFalse(strategy.detailRetrievalSensible(document));
    }


    @Test
    public void testFull() {
        StoredDocument document = createNewStoredFullDocument();
        Assert.assertFalse(strategy.detailRetrievalSensible(document));
    }


    public StoredDocument createNewStoredFullDocument() {
        StoredDocument storedDocument = createNewStoredNonFullDocument(calcCurrentYear());
        storedDocument.getDocument().setFieldValue(Field.ABSTRACT, FULL_ABSTRACT_CONTENT);
        return storedDocument;
    }


    public StoredDocument createNewStoredNonFullDocument(int year) {
        StoredDocument storedDocument = BackendDocumentFactory.createStoredDocument(null, "title", year, "author1",
                        "author2");
        String oid = OIDFactory.calcOid(storedDocument.getDocument());
        storedDocument.getDocument().setOid(oid);
        return storedDocument;
    }


    private int calcCurrentYear() {
        final Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        final int currentYear = c.get(Calendar.YEAR);
        return currentYear;
    }
}
