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

package de.unidue.inf.is.ezdl.dlbackend.cache;

import java.util.Arrays;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.unidue.inf.is.ezdl.dlbackend.AbstractBackendTestBase;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.Document;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.Person;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.PersonList;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.TextDocument;
import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;
import de.unidue.inf.is.ezdl.dlcore.data.query.DefaultQuery;
import de.unidue.inf.is.ezdl.dlcore.data.query.DocumentQuery;
import de.unidue.inf.is.ezdl.dlcore.data.query.ResultDocument;
import de.unidue.inf.is.ezdl.dlcore.data.query.ResultDocumentList;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeCompare;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeCompare.Predicate;



public class EhCacheTest extends AbstractBackendTestBase {

    private EhCache cache;


    @Before
    public void init() {
        cache = new EhCache("test", EhCacheTest.class.getResource("/cache/ehcache.xml"));
    }


    @Test
    public void testPutAndGet() {
        DocumentQuery query = getQuery("Dummy1");
        ResultDocumentList data = new ResultDocumentList();
        Document document = new TextDocument();
        document.setTitle("Bla bla");
        document.setAuthorList(new PersonList(Arrays.asList(new Person("Donald Duck"))));
        ResultDocument stored = new ResultDocument(document);
        data.add(stored);

        cache.put(query, data);

        System.out.println("Data put into cache: " + data);
        System.out.println("Key used: " + query);

        DocumentQuery query2 = getQuery("Dummy1");
        ResultDocumentList data2 = (ResultDocumentList) cache.get(query2);

        System.out.println("Data got from cache: " + data2);
        System.out.println("Key used: " + query2);

        Assert.assertEquals(data, data2);
    }


    @Test
    public void testGetWithKeyNotInCache() {
        DocumentQuery query = getQuery("Dummy1");
        ResultDocumentList data = (ResultDocumentList) cache.get(query);

        Assert.assertNull(data);
    }


    @Test
    public void testPutWithNull() {
        try {
            cache.put(null, null);
        }
        catch (Exception e) {
            Assert.fail("unexpected exception");
        }
    }


    @After
    public void deleteCache() {
        cache.clear();
    }


    private DocumentQuery getQuery(String... wrapperNames) {
        QueryNodeCompare node = new QueryNodeCompare(Field.AUTHOR, Predicate.EQ, "fuhr");
        DocumentQuery query = new DocumentQuery(new DefaultQuery(node), Arrays.asList(wrapperNames));
        return query;
    }
}
