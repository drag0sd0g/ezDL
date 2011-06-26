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

package de.unidue.inf.is.ezdl.dlcore.data.dldata;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Assert;
import org.junit.Test;

import de.unidue.inf.is.ezdl.dlcore.AbstractTestBase;
import de.unidue.inf.is.ezdl.dlcore.DocumentFactory;
import de.unidue.inf.is.ezdl.dlcore.data.Mergeable;
import de.unidue.inf.is.ezdl.dlcore.data.OIDFactory;



public class MergeableListTest extends AbstractTestBase {

    @Test
    public void testMergeObject1() {
        TextDocument withAbstract = createDocument("title 2", 2001, "abstract", "j g", "e w");
        TextDocument without = createDocument("title 2", 2001, null, "j g", "e w");

        without.merge(withAbstract);
        Assert.assertEquals("", "abstract", without.getAbstract());
    }


    @Test
    public void testMergeObject2() {
        TextDocument withAbstract = createDocument("title 2", 2001, "abstract", "j g", "e w");
        TextDocument without = createDocument("title 2", 2001, null, "j g", "e w");

        withAbstract.merge(without);
        Assert.assertEquals("", "abstract", withAbstract.getAbstract());
    }


    @Test
    public void testMergeObjectURLList1() {
        try {
            TextDocument a = createDocument("title 2", 2001, null, "j g", "e w");
            a.addDetailURL(new URL("http://a/b"));
            TextDocument b = createDocument("title 2", 2001, null, "j g", "e w");
            b.addDetailURL(new URL("http://b/b"));
            b.merge(a);
            Assert.assertEquals("a", "[http://b/b, http://a/b]", b.getDetailURLs().toString());
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
            Assert.fail("URL encoding issue");
        }
    }


    @Test
    public void testMergeObjectURLList2() {
        try {
            TextDocument a = createDocument("title 2", 2001, null, "j g", "e w");
            a.addDetailURL(new URL("http://a/b"));
            a.addDetailURL(new URL("http://b/b"));
            TextDocument b = createDocument("title 2", 2001, null, "j g", "e w");
            b.addDetailURL(new URL("http://b/b"));
            b.merge(a);
            Assert.assertEquals("a", "[http://b/b, http://a/b]", b.getDetailURLs().toString());
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
            Assert.fail("URL encoding issue");
        }
    }


    @Test
    public void testMergeObjectURLList3() {
        try {
            TextDocument a = createDocument("title 2", 2001, null, "j g", "e w");
            a.addDetailURL(new URL("http://b/b"));
            TextDocument b = createDocument("title 2", 2001, null, "j g", "e w");
            b.addDetailURL(new URL("http://b/b"));
            b.addDetailURL(new URL("http://a/b"));
            b.merge(a);
            Assert.assertEquals("a", "[http://b/b, http://a/b]", b.getDetailURLs().toString());
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
            Assert.fail("URL encoding issue");
        }
    }


    @Test
    public void testMergeObjectURLList4() {
        try {
            TextDocument a = createDocument("title 2", 2001, null, "j g", "e w");
            TextDocument b = createDocument("title 2", 2001, null, "j g", "e w");
            b.addDetailURL(new URL("http://b/b"));
            b.addDetailURL(new URL("http://a/b"));
            b.merge(a);
            Assert.assertEquals("a", "[http://b/b, http://a/b]", b.getDetailURLs().toString());
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
            Assert.fail("URL encoding issue");
        }
    }


    @Test
    public void testMerge1() {
        MergeableList<Mergeable> mergeableList1 = new MergeableArrayList<Mergeable>();
        mergeableList1.add(createDocument("title 1", 2000, null, "a b", "c d"));
        mergeableList1.add(createDocument("title 2", 2001, null, "j g", "e w"));
        mergeableList1.add(createDocument("title 3", 2002, null, "i z", "n e"));

        MergeableList<Mergeable> mergeableList2 = new MergeableArrayList<Mergeable>();
        mergeableList2.add(createDocument("title 4", 2000, null, "a b", "a b"));
        mergeableList2.add(createDocument("title 2", 2001, "abstract", "j g", "e w"));
        mergeableList2.add(createDocument("title 5", 2002, "abstract", "a s", "a d"));

        mergeableList1.merge(mergeableList2);

        Assert.assertEquals(5, mergeableList1.size());
    }


    @Test
    public void testMerge2() {
        MergeableList<Mergeable> mergeableList1 = new MergeableArrayList<Mergeable>();
        mergeableList1.add(createMergeable("s43"));
        mergeableList1.add(createMergeable("s48"));
        mergeableList1.add(createMergeable("s13"));

        MergeableList<Mergeable> mergeableList2 = new MergeableArrayList<Mergeable>();
        mergeableList2.add(createMergeable("s55"));
        mergeableList2.add(createMergeable("s87"));
        mergeableList2.add(createMergeable("s43"));

        mergeableList1.merge(mergeableList2);

        Assert.assertEquals(5, mergeableList1.size());
    }


    public TextDocument createDocument(String title, int year, String abs, String... authors) {
        TextDocument d = DocumentFactory.createDocument(title, year, authors);
        d.setAbstract(abs);
        d.setOid(OIDFactory.calcOid(d));
        return d;
    }


    private final class CustomMergeable implements Mergeable {

        private String string;


        public CustomMergeable(String string) {
            this.string = string;
        }


        @Override
        public void merge(Mergeable other) {
            if (other instanceof CustomMergeable) {
                string = string + (other);
            }
        }


        @Override
        public boolean isSimilar(Mergeable other) {
            if (other instanceof CustomMergeable) {
                return string.equals(((CustomMergeable) other).string);
            }
            return false;
        }


        @Override
        public String toString() {
            return string;
        }

    }


    public Mergeable createMergeable(String s) {
        return new CustomMergeable(s);
    }
}
