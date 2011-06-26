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

package de.unidue.inf.is.ezdl.dlwrapper.wrappers.misc.mendeley;

import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.unidue.inf.is.ezdl.dlbackend.AbstractBackendTestBase;
import de.unidue.inf.is.ezdl.dlbackend.data.StoredDocument;
import de.unidue.inf.is.ezdl.dlbackend.data.StoredDocumentList;
import de.unidue.inf.is.ezdl.dlbackend.wrappers.SourceInfo;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.TextDocument;
import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;
import de.unidue.inf.is.ezdl.dlcore.data.query.DefaultQuery;
import de.unidue.inf.is.ezdl.dlcore.data.query.DocumentQuery;
import de.unidue.inf.is.ezdl.dlcore.data.query.Query;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeBool;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeCompare;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeCompare.Predicate;
import de.unidue.inf.is.ezdl.dlwrapper.Wrapper;
import de.unidue.inf.is.ezdl.dlwrapper.wrappers.DummyNet;



public class MendeleyWrapperTest extends AbstractBackendTestBase {

    private Wrapper wrapper;
    private DummyNet dummyNet;


    @Before
    public void setup() {
        dummyNet = new DummyNet();
        wrapper = new MendeleyWrapper(dummyNet);
    }


    @Test
    public void testSearch() throws IOException {
        QueryNodeCompare bc = new QueryNodeCompare(Field.TITLE, Predicate.EQ, "retrieval");
        QueryNodeCompare bc2 = new QueryNodeCompare(Field.YEAR, Predicate.EQ, "2003");
        QueryNodeBool conjunction = new QueryNodeBool();
        conjunction.addChild(bc);
        conjunction.addChild(bc2);
        StoredDocumentList result = search(
                        conjunction,
                        "http://www.mendeley.com/oapi/documents/search/retrieval%202003?consumer_key=bf7f57eea978ad72eb6725226e9b891704caf5784&items=500",
                        "/mendeley/m1");

        Assert.assertTrue(result.size() == 269);
        Assert.assertEquals("IBM research TRECVID-2003 video retrieval system", result.get(0).getDocument().getTitle());
        Assert.assertEquals(2003, result.get(0).getDocument().getYear());
    }


    @Test
    public void testDetails() throws IOException {
        StoredDocumentList result = details(
                        "f3110250-6d00-11df-a2b2-0026b95e3eb7",
                        "http://www.mendeley.com/oapi/documents/details/f3110250-6d00-11df-a2b2-0026b95e3eb7?consumer_key=bf7f57eea978ad72eb6725226e9b891704caf5784",
                        "/mendeley/m2");

        Assert.assertTrue(result.size() == 1);
        Assert.assertEquals(
                        "Imagine a world where you walk up to a computer and sing the gong fragment that has been plaguing you since breakfast. The computer accepts your off-key singing, corrects your request, and promptly suggests to you that 'Camptown Races' is the cause of your irritation. You I confirm the computer's suggestion by listening to one of the many MP3 files it has found.",
                        result.get(0).getDocument().getFieldValue(Field.ABSTRACT));
    }


    private StoredDocumentList search(QueryNodeBool condition, String uri, String resultResource) throws IOException {
        dummyNet.put(uri, IOUtils.toString(getClass().getResourceAsStream(resultResource)));
        final Query q = new DefaultQuery(condition);
        DocumentQuery dq = new DocumentQuery(q, Arrays.asList("Test"));
        StoredDocumentList result = wrapper.askDocument(dq, false);
        return result;
    }


    private StoredDocumentList details(String id, String uri, String resultResource) throws IOException {
        dummyNet.put(uri, IOUtils.toString(getClass().getResourceAsStream(resultResource)));

        StoredDocumentList docs = new StoredDocumentList();
        StoredDocument sd = new StoredDocument(new TextDocument());
        sd.addSource(new SourceInfo(wrapper.getSourceID(), id));
        docs.add(sd);
        wrapper.askDetails(docs);
        return docs;
    }

}
