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

package de.unidue.inf.is.ezdl.dlservices.repository.store.repositories;

import java.util.Arrays;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.unidue.inf.is.ezdl.dlbackend.AbstractBackendTestBase;
import de.unidue.inf.is.ezdl.dlbackend.BackendDocumentFactory;
import de.unidue.inf.is.ezdl.dlbackend.data.StoredDocument;
import de.unidue.inf.is.ezdl.dlbackend.data.StoredDocumentList;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.Document;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.TextDocument;



public abstract class AbstractRepositoryTest extends AbstractBackendTestBase {

    private DocumentRepository repo;

    private String[] keys = {
                    "1", "2", "3", "4", "zz"
    };


    protected abstract DocumentRepository createRepo();


    @Before
    public void setup() {
        repo = createRepo();
        removeDocuments();
        fillRepo(repo, keys[0], "Some paper title", 2000, "Matthias Jordan");
        fillRepo(repo, keys[1], "Towards a new paper title", 2000, "Matthias Jordan");
        fillRepo(repo, keys[2], "Model and taxonomy for paper titles", 2000, "Matthias Jordan");
        fillRepo(repo, keys[3], "Paper titles are surprisingly useful", 2000, "Matthias Jordan");
    }


    @After
    public void tearDown() {
        removeDocuments();
        destroyRepo();
    }


    private void removeDocuments() {
        for (String key : keys) {
            repo.removeDocument(key);
        }
    }


    protected abstract void destroyRepo();


    @Test
    public void repoTest() {
        StoredDocument d1 = repo.getDocument(keys[1]);
        Assert.assertEquals("1", "Towards a new paper title", d1.getDocument().getTitle());

        StoredDocument d2 = repo.getDocument(keys[2]);
        Assert.assertEquals("2", "Model and taxonomy for paper titles", d2.getDocument().getTitle());

        StoredDocument d3 = repo.getDocument(keys[1]);
        Assert.assertEquals("3", "Towards a new paper title", d3.getDocument().getTitle());
    }


    @Test
    public void multipleTest() {
        StoredDocumentList documentList = repo.getDocuments(Arrays.asList(keys[2], keys[3], "nex"));
        Assert.assertEquals("length", 2, documentList.size());
        Assert.assertTrue("",
                        "Model and taxonomy for paper titles".equals(documentList.get(0).getDocument().getTitle()));
        Assert.assertTrue("",
                        "Paper titles are surprisingly useful".equals(documentList.get(1).getDocument().getTitle()));
    }


    @Test
    public void test() {
        StoredDocument document = BackendDocumentFactory.createStoredDocument(null, "title", 2001, "Matthias Jordan");
        String oid = keys[4];
        getRepository().addDocument(oid, document);
        StoredDocument readFirst = repo.getDocument(oid);
        Assert.assertEquals("title set okay", document.getDocument().getTitle(), readFirst.getDocument().getTitle());
        Assert.assertNull("abstract null", ((TextDocument) readFirst.getDocument()).getAbstract());

        ((TextDocument) document.getDocument()).setAbstract("abstract");
        getRepository().addDocument(oid, document);

        Document read = repo.getDocument(oid).getDocument();
        Assert.assertEquals("title still okay", document.getDocument().getTitle(), read.getTitle());
        Assert.assertEquals("abstract updated", "abstract", ((TextDocument) read).getAbstract());
    }


    private void fillRepo(DocumentRepository repo, String oid, String title, int year, String... authors) {
        StoredDocument d = BackendDocumentFactory.createStoredDocument("", title, year, authors);
        d.getDocument().setOid(oid);
        repo.addDocument(oid, d);
    }


    protected DocumentRepository getRepository() {
        return repo;
    }
}
