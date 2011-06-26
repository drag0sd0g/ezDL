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

import org.junit.Assert;
import org.junit.Test;

import de.unidue.inf.is.ezdl.dlbackend.AbstractBackendTestBase;
import de.unidue.inf.is.ezdl.dlbackend.BackendDocumentFactory;
import de.unidue.inf.is.ezdl.dlbackend.wrappers.SourceID;
import de.unidue.inf.is.ezdl.dlbackend.wrappers.SourceInfo;
import de.unidue.inf.is.ezdl.dlcore.data.OIDFactory;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.Document;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.MergeableArrayList;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.MergeableList;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.TextDocument;
import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;



public class StoredDocumentListTest extends AbstractBackendTestBase {

    @Test
    public void testMerge() {
        SourceInfo aSource = new SourceInfo(new SourceID("a", "aa"), "detail");
        SourceInfo bSource = new SourceInfo(new SourceID("b", "bb"), "detail");

        MergeableList<StoredDocument> mergeableList1 = new MergeableArrayList<StoredDocument>();
        mergeableList1.add(createDocument("title 1", 2000, null, aSource, "a b", "c d"));
        mergeableList1.add(createDocument("title 2", 2001, null, aSource, "a b", "c d"));

        Assert.assertEquals("1a", 2, mergeableList1.size());
        Assert.assertEquals("1b", 1, mergeableList1.get(0).getSources().size());

        MergeableList<StoredDocument> mergeableList2 = new MergeableArrayList<StoredDocument>();
        mergeableList2.add(createDocument("title 1", 2000, null, bSource, "a b", "c d"));

        Assert.assertEquals("2a", 1, mergeableList2.size());
        Assert.assertEquals("2b", 1, mergeableList2.get(0).getSources().size());

        MergeableList<StoredDocument> mergeableList3 = new MergeableArrayList<StoredDocument>();
        mergeableList3.add(createDocument("title 1", 2000, null, new SourceInfo(new SourceID("a", "aa"), "detail",
                        new Date()), "a b", "c d"));

        Assert.assertEquals("3a", 1, mergeableList3.size());
        Assert.assertEquals("3b", 1, mergeableList3.get(0).getSources().size());

        mergeableList1.merge(mergeableList2);

        Assert.assertEquals("4a", 2, mergeableList1.size());
        Assert.assertEquals("4b", 1, mergeableList1.get(0).getSources().size());
        final SourceInfo[] array1 = mergeableList1.get(0).getSources().toArray(new SourceInfo[1]);
        Assert.assertNull("4c", array1[0].getDetailTimestamp());

        mergeableList1.merge(mergeableList3);

        Assert.assertEquals("5a", 2, mergeableList1.size());
        Assert.assertEquals("5b", 1, mergeableList1.get(0).getSources().size());
        final SourceInfo[] array2 = mergeableList1.get(0).getSources().toArray(new SourceInfo[1]);
        Assert.assertNotNull("5c", array2[0].getDetailTimestamp());
    }


    public StoredDocument createDocument(String title, int year, String abs, SourceInfo source, String... authors) {
        StoredDocument stored = BackendDocumentFactory.createStoredDocument(abs, title, year, authors);
        stored.clearSources();
        stored.addSource(source);
        stored.getDocument().setOid(OIDFactory.calcOid(stored.getDocument()));
        return stored;
    }


    @Test
    public void testFindDocument1a() {
        StoredDocumentList list = getFakeList();
        checkFakeList(list, "a");
    }


    @Test
    public void testFindDocument1b() {
        StoredDocumentList list = getFakeList();
        checkFakeList(list, "b");
    }


    @Test
    public void testFindDocument1c() {
        StoredDocumentList list = getFakeList();
        checkFakeList(list, "c");
    }


    @Test
    public void testFindDocument1d() {
        StoredDocumentList list = getFakeList();
        checkFakeList(list, "d");
    }


    @Test
    public void testFindDocument1e() {
        StoredDocumentList list = getFakeList();
        checkFakeList(list, "e");
    }


    @Test
    public void testFindDocument1f() {
        StoredDocumentList list = getFakeList();
        Assert.assertTrue(list.findDocument(Field.DOI, "f") == null);
    }


    private StoredDocumentList getFakeList() {
        StoredDocumentList list = new StoredDocumentList();
        addFakeDoc(list, "a");
        addFakeDoc(list, "b");
        addFakeDoc(list, "c");
        addFakeDoc(list, "d");
        addFakeDoc(list, "e");
        return list;
    }


    private void addFakeDoc(StoredDocumentList list, String doi) {
        Document doc = new TextDocument();
        doc.setFieldValue(Field.DOI, doi);
        list.add(new StoredDocument(doc));
    }


    private void checkFakeList(StoredDocumentList list, String doi) {
        Assert.assertTrue(list.findDocument(Field.DOI, doi) != null);
        Assert.assertEquals(doi, list.findDocument(Field.DOI, doi).getDocument().getFieldValue(Field.DOI));
    }

}
