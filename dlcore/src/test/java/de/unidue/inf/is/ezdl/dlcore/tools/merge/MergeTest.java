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

package de.unidue.inf.is.ezdl.dlcore.tools.merge;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import de.unidue.inf.is.ezdl.dlcore.AbstractTestBase;
import de.unidue.inf.is.ezdl.dlcore.DocumentFactory;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.TextDocument;
import de.unidue.inf.is.ezdl.dlcore.data.query.ResultDocument;
import de.unidue.inf.is.ezdl.dlcore.tools.merge.data.NextLevel;
import de.unidue.inf.is.ezdl.dlcore.tools.merge.data.SimpleObject;



public class MergeTest extends AbstractTestBase {

    @Test
    public void mergeNextLevel() throws Exception {
        NextLevel target = new NextLevel();
        NextLevel left = new NextLevel();
        NextLevel r1 = new NextLevel();

        r1.setAge(31);
        r1.setName("SimpleObject");
        left.setFlat("bla");
        r1.setNames(new ArrayList<String>(Arrays.asList("balba", "fadgshfgd", "gfshg")));

        target.xmerge(r1, left);
        r1.getNames().remove(0);
        Assert.assertNotSame(r1.getNames(), target.getNames());
        Assert.assertEquals(r1.getAge(), target.getAge());
        Assert.assertNull(left.getName());
        Assert.assertEquals(r1.getName(), target.getName());
        Assert.assertEquals(left.getAge(), 0); // default should -1
        Assert.assertEquals(left.getFlat(), target.getFlat());

    }


    @Test
    public void mergeSimpleObject() throws Exception {
        SimpleObject target = new SimpleObject();
        SimpleObject left = new SimpleObject();
        SimpleObject right = new SimpleObject();

        left.setAge(31);
        right.setName("SimpleObject");

        target.xmerge(right, left);
        Assert.assertEquals(left.getAge(), target.getAge());
        Assert.assertNull(left.getName());
        Assert.assertEquals(right.getName(), target.getName());
        Assert.assertEquals(right.getAge(), 0); // default should -1

    }


    /**
     * This test case checks merging a document whose abstract is there with a
     * document that has a valid abstract, too.
     */
    @Test
    public void testMergeString1() {
        ResultDocument document1 = DocumentFactory.createResultDocument("abstract1", "title", 2001, "Matthias Jordan");

        ResultDocument document2 = DocumentFactory.createResultDocument("abstract2", "title", 2001, "Matthias Jordan");

        document1.getDocument().merge(document2.getDocument());

        TextDocument doc = ((TextDocument) document1.getDocument());
        Assert.assertEquals("title still okay", "title", doc.getTitle());
        Assert.assertEquals("abstract updated", "abstract1", doc.getAbstract());

    }


    /**
     * This test case checks merging a document whose abstract is there, but
     * null, with a document that has a valid abstract.
     */
    @Test
    public void testMergeWithNull1() {
        ResultDocument document1 = DocumentFactory.createResultDocument(null, "title", 2001, "Matthias Jordan");

        ResultDocument document2 = DocumentFactory.createResultDocument("abstract", "title", 2001, "Matthias Jordan");

        document1.getDocument().merge(document2.getDocument());

        TextDocument doc = ((TextDocument) document1.getDocument());
        Assert.assertEquals("title still okay", "title", doc.getTitle());
        Assert.assertEquals("abstract updated", "abstract", doc.getAbstract());

    }


    /**
     * This test case checks merging a document whose abstract is there, but
     * null, with a document that has a valid abstract.
     */
    @Test
    public void testMergeWithNull2() {
        ResultDocument document1 = DocumentFactory.createResultDocument("abstract", "title", 2001, "Matthias Jordan");

        ResultDocument document2 = DocumentFactory.createResultDocument(null, "title", 2001, "Matthias Jordan");

        document1.getDocument().merge(document2.getDocument());

        TextDocument doc = ((TextDocument) document1.getDocument());
        Assert.assertEquals("title still okay", "title", doc.getTitle());
        Assert.assertEquals("abstract updated", "abstract", doc.getAbstract());

    }


    /**
     * This test case checks merging a document whose abstract is there, but
     * emtpy, with a document that has a valid abstract.
     */
    @Test
    public void testMergeWithEmtpyString1() {
        ResultDocument document1 = DocumentFactory.createResultDocument("abstract", "title", 2001, "Matthias Jordan");

        ResultDocument document2 = DocumentFactory.createResultDocument("", "title", 2001, "Matthias Jordan");

        document1.getDocument().merge(document2.getDocument());

        TextDocument doc = ((TextDocument) document1.getDocument());
        Assert.assertEquals("title still okay", "title", doc.getTitle());
        Assert.assertEquals("abstract updated", "abstract", doc.getAbstract());

    }


    /**
     * This test case checks merging a document whose abstract is there, but
     * emtpy, with a document that has a valid abstract.
     */
    @Test
    public void testMergeWithEmtpyString2() {
        ResultDocument document1 = DocumentFactory.createResultDocument("", "title", 2001, "Matthias Jordan");

        ResultDocument document2 = DocumentFactory.createResultDocument("abstract", "title", 2001, "Matthias Jordan");

        document1.getDocument().merge(document2.getDocument());

        TextDocument doc = ((TextDocument) document1.getDocument());
        Assert.assertEquals("title still okay", "title", doc.getTitle());
        Assert.assertEquals("abstract updated", "abstract", doc.getAbstract());

    }


    /**
     * This test case checks merging a document whose abstract is there, but
     * null, with a document that has a valid abstract.
     */
    @Test
    public void testMergeAuthorList() {
        ResultDocument document1 = DocumentFactory.createResultDocument(null, "title", 2001, "Matthias Jordan");

        ResultDocument document2 = DocumentFactory.createResultDocument(null, "title", 2001, "M. Jordan");

        document1.getDocument().merge(document2.getDocument());

        Assert.assertEquals("title still okay", "title", document1.getDocument().getTitle());
        getLogger().debug(document1.getDocument().getAuthorList());
        Assert.assertEquals("authors merged", "{PersonList [Matthias Jordan]}", document1.getDocument().getAuthorList()
                        .toString());
    }
}
