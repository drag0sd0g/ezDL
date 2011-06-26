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

package de.unidue.inf.is.ezdl.dlcore.data.extractor;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import de.unidue.inf.is.ezdl.dlcore.AbstractTestBase;
import de.unidue.inf.is.ezdl.dlcore.DocumentFactory;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.Document;



/**
 * This is a simple test case to show the usage of ExtractorService.
 * 
 * @author Jens Kapitza
 */
public class AuthorExtractorTest extends AbstractTestBase {

    /**
     * The service we will test.
     */
    private ExtractorService service = new AuthorExtractor();


    @Test
    public void extractAuthors() {
        final Document data = DocumentFactory.createDocument("TestData", 2009, "Matthias Jordan", "Sascha Kriewel");
        final Document data2 = DocumentFactory.createDocument("TestData2", 2010, "Jens Kapitza", "Matthias Jordan");
        List<Document> list = new ArrayList<Document>();
        list.add(data);
        list.add(data2);

        ExtractionResult authors = service.extract(list);
        List<Entry> authorList = authors.cloudData(6, 8);

        StringBuilder str = new StringBuilder();
        // authors.getTotal | getMinimum | getMaximum
        Assert.assertEquals(4, authors.getTotal());
        Assert.assertEquals(1, authors.getMinimum());
        Assert.assertEquals(2, authors.getMaximum());
        for (Entry entry : authorList) {
            str.append(getHtml(entry.getKey(), entry.getValue()));
        }
        Assert.assertEquals(
                        "<span class='size6'>Jens Kapitza</span><span class='size8'>Matthias Jordan</span><span class='size6'>Sascha Kriewel</span>",
                        str.toString());

    }


    @Test
    public void extractNPEAuthors() {
        final Document data = DocumentFactory.createDocument("TestData", 2009);
        data.setAuthorList(null);
        List<Document> list = new ArrayList<Document>();

        ExtractionResult authors = service.extract(list);
        List<Entry> authorList = authors.cloudData(6, 8);

        StringBuilder str = new StringBuilder();
        // authors.getTotal | getMinimum | getMaximum
        Assert.assertEquals(0, authors.getTotal());
        Assert.assertEquals(Integer.MAX_VALUE, authors.getMinimum());
        Assert.assertEquals(Integer.MIN_VALUE, authors.getMaximum());
        for (Entry entry : authorList) {
            str.append(getHtml(entry.getKey(), entry.getValue()));
        }
        Assert.assertEquals("", str.toString());

    }


    private String getHtml(String key, Integer value) {
        return String.format("<span class='size%d'>%s</span>", value, key);
    }
}
