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

package de.unidue.inf.is.ezdl.dlservices.search.handlers;

import java.util.Arrays;

import junit.framework.Assert;

import org.junit.Test;

import de.unidue.inf.is.ezdl.dlbackend.AbstractBackendTestBase;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.Document;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.Person;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.PersonList;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.TextDocument;
import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;
import de.unidue.inf.is.ezdl.dlcore.data.fields.Order;
import de.unidue.inf.is.ezdl.dlcore.data.fields.Sorting;
import de.unidue.inf.is.ezdl.dlcore.data.query.ResultConfiguration;
import de.unidue.inf.is.ezdl.dlcore.data.query.ResultDocument;
import de.unidue.inf.is.ezdl.dlcore.data.query.ResultDocumentList;



/**
 * @author mj
 */
public class DocumentListFilterTest extends AbstractBackendTestBase {

    private static final int[] years = {
                    2009, 2008, 2001, 2007, 2009, 2008, 2009, 2007, 2009
    };


    @Test
    public void testOriginalUntouched() {
        Sorting sorting = new Sorting(Field.RSV, Order.DESCENDING);
        ResultConfiguration resultConfig = new ResultConfiguration(2, 5, Arrays.asList(Field.AUTHOR), sorting);
        DocumentListFilter filter = new DocumentListFilter(resultConfig);

        ResultDocumentList list = getDocumentList(20);

        for (ResultDocument rd : list) {
            Document d = rd.getDocument();
            Assert.assertNotNull(d.getFieldValue(Field.TITLE));
        }

        filter.process(list);

        for (ResultDocument rd : list) {
            Document d = rd.getDocument();
            Assert.assertNotNull(d.getFieldValue(Field.TITLE));
        }

    }


    @Test
    public void testFilterFields() {
        testFilterFields(
                        3,
                        "{DataList [{ResultDocument 0. [source] null: ''title0'' (-2147483648)}, {ResultDocument 0.33 [source] null: ''title1'' (-2147483648)}, {ResultDocument 0.66 [source] null: ''title2'' (-2147483648)}]}",
                        Field.TITLE);
        testFilterFields(
                        3,
                        "{DataList [{ResultDocument 0. [source] {PersonList [author]}: ''null'' (-2147483648)}, {ResultDocument 0.33 [source] {PersonList [author]}: ''null'' (-2147483648)}, {ResultDocument 0.66 [source] {PersonList [author]}: ''null'' (-2147483648)}]}",
                        Field.AUTHOR);
        testFilterFields(
                        2,
                        "{DataList [{ResultDocument 0. [source] {PersonList [author]}: ''title0'' (2009)}, {ResultDocument 0. [source] {PersonList [author]}: ''title1'' (2008)}]}",
                        Field.AUTHOR, Field.TITLE, Field.YEAR);
    }


    @Test
    public void testSortListNull() {
        testSorting("s1",
                        "{DataList [{ResultDocument 0. [source] {PersonList [author]}: ''title0'' (2009)}, {ResultDocument 0. [source] {PersonList [author]}: ''title1'' (2008)}, {ResultDocument 0. [source] {PersonList [author]}: ''title2'' (2001)}, {ResultDocument 0.60 [source] {PersonList [author]}: ''title3'' (2007)}, {ResultDocument 0. [source] {PersonList [author]}: ''title4'' (2009)}]}", //
                        new Sorting[] {
                            null
                        });
    }


    @Test
    public void testSortList() {
        testSorting("s1",
                        "{DataList [{ResultDocument 0. [source] {PersonList [author]}: ''title0'' (2009)}, {ResultDocument 0. [source] {PersonList [author]}: ''title1'' (2008)}, {ResultDocument 0. [source] {PersonList [author]}: ''title2'' (2001)}, {ResultDocument 0.60 [source] {PersonList [author]}: ''title3'' (2007)}, {ResultDocument 0. [source] {PersonList [author]}: ''title4'' (2009)}]}", //
                        new Sorting(Field.RSV, Order.ASCENDING));
        testSorting("s2",
                        "{DataList [{ResultDocument 0. [source] {PersonList [author]}: ''title4'' (2009)}, {ResultDocument 0.60 [source] {PersonList [author]}: ''title3'' (2007)}, {ResultDocument 0. [source] {PersonList [author]}: ''title2'' (2001)}, {ResultDocument 0. [source] {PersonList [author]}: ''title1'' (2008)}, {ResultDocument 0. [source] {PersonList [author]}: ''title0'' (2009)}]}", //
                        new Sorting(Field.RSV, Order.DESCENDING));
        testSorting("s3",
                        "{DataList [{ResultDocument 0. [source] {PersonList [author]}: ''title0'' (2009)}, {ResultDocument 0. [source] {PersonList [author]}: ''title4'' (2009)}, {ResultDocument 0. [source] {PersonList [author]}: ''title1'' (2008)}, {ResultDocument 0.60 [source] {PersonList [author]}: ''title3'' (2007)}, {ResultDocument 0. [source] {PersonList [author]}: ''title2'' (2001)}]}", //
                        new Sorting(Field.YEAR, Order.DESCENDING));
        testSorting("s4",
                        "{DataList [{ResultDocument 0. [source] {PersonList [author]}: ''title2'' (2001)}, {ResultDocument 0.60 [source] {PersonList [author]}: ''title3'' (2007)}, {ResultDocument 0. [source] {PersonList [author]}: ''title1'' (2008)}, {ResultDocument 0. [source] {PersonList [author]}: ''title0'' (2009)}, {ResultDocument 0. [source] {PersonList [author]}: ''title4'' (2009)}]}",//
                        new Sorting(Field.YEAR, Order.ASCENDING));

        // Test sorting by two fields
        testSorting("s5",
                        10,
                        "{DataList [{ResultDocument 0. [source] {PersonList [author]}: ''title2'' (2001)}, {ResultDocument 0.70 [source] {PersonList [author]}: ''title7'' (2007)}, {ResultDocument 0.30 [source] {PersonList [author]}: ''title3'' (2007)}, {ResultDocument 0. [source] {PersonList [author]}: ''title5'' (2008)}, {ResultDocument 0. [source] {PersonList [author]}: ''title1'' (2008)}, {ResultDocument 0. [source] {PersonList [author]}: ''title9'' (2009)}, {ResultDocument 0. [source] {PersonList [author]}: ''title8'' (2009)}, {ResultDocument 0.60 [source] {PersonList [author]}: ''title6'' (2009)}, {ResultDocument 0. [source] {PersonList [author]}: ''title4'' (2009)}, {ResultDocument 0. [source] {PersonList [author]}: ''title0'' (2009)}]}", //
                        new Sorting(Field.RSV, Order.DESCENDING), //
                        new Sorting(Field.YEAR, Order.ASCENDING));

    }


    @Test
    public void testSlice() {
        int inf = ResultConfiguration.INF_DOCS;
        testSliced("sl1", 15, 0, 0,
                        "{DataList [{ResultDocument 0. [source] {PersonList [author]}: ''title0'' (2009)}]}");
        testSliced("sl2",
                        15,
                        0,
                        inf,
                        "{DataList [{ResultDocument 0. [source] {PersonList [author]}: ''title0'' (2009)}, {ResultDocument 0.06 [source] {PersonList [author]}: ''title1'' (2008)}, {ResultDocument 0.13 [source] {PersonList [author]}: ''title2'' (2001)}, {ResultDocument 0. [source] {PersonList [author]}: ''title3'' (2007)}, {ResultDocument 0.26 [source] {PersonList [author]}: ''title4'' (2009)}, {ResultDocument 0.33 [source] {PersonList [author]}: ''title5'' (2008)}, {ResultDocument 0. [source] {PersonList [author]}: ''title6'' (2009)}, {ResultDocument 0.46 [source] {PersonList [author]}: ''title7'' (2007)}, {ResultDocument 0.53 [source] {PersonList [author]}: ''title8'' (2009)}, {ResultDocument 0. [source] {PersonList [author]}: ''title9'' (2009)}, {ResultDocument 0.66 [source] {PersonList [author]}: ''title10'' (2008)}, {ResultDocument 0.73 [source] {PersonList [author]}: ''title11'' (2001)}, {ResultDocument 0. [source] {PersonList [author]}: ''title12'' (2007)}, {ResultDocument 0.86 [source] {PersonList [author]}: ''title13'' (2009)}, {ResultDocument 0.93 [source] {PersonList [author]}: ''title14'' (2008)}]}");
        testSliced("sl3",
                        15,
                        0,
                        5,
                        "{DataList [{ResultDocument 0. [source] {PersonList [author]}: ''title0'' (2009)}, {ResultDocument 0.06 [source] {PersonList [author]}: ''title1'' (2008)}, {ResultDocument 0.13 [source] {PersonList [author]}: ''title2'' (2001)}, {ResultDocument 0. [source] {PersonList [author]}: ''title3'' (2007)}, {ResultDocument 0.26 [source] {PersonList [author]}: ''title4'' (2009)}, {ResultDocument 0.33 [source] {PersonList [author]}: ''title5'' (2008)}]}");
        testSliced("sl4",
                        15,
                        5,
                        10,
                        "{DataList [{ResultDocument 0.33 [source] {PersonList [author]}: ''title5'' (2008)}, {ResultDocument 0. [source] {PersonList [author]}: ''title6'' (2009)}, {ResultDocument 0.46 [source] {PersonList [author]}: ''title7'' (2007)}, {ResultDocument 0.53 [source] {PersonList [author]}: ''title8'' (2009)}, {ResultDocument 0. [source] {PersonList [author]}: ''title9'' (2009)}, {ResultDocument 0.66 [source] {PersonList [author]}: ''title10'' (2008)}]}");
        testSliced("sl5", 15, 14, 14,
                        "{DataList [{ResultDocument 0.93 [source] {PersonList [author]}: ''title14'' (2008)}]}");
    }


    private void testSliced(String id, int count, int startNo, int endNo, String expected) {
        Sorting sorting = new Sorting(Field.RSV, Order.DESCENDING);
        ResultConfiguration resultConfig = new ResultConfiguration(startNo, endNo, Arrays.asList(Field.AUTHOR), sorting);
        DocumentListFilter filter = new DocumentListFilter(resultConfig);

        ResultDocumentList list = getDocumentList(count);

        ResultDocumentList slicedList = filter.sliceList(list);

        int resultCount = (endNo - startNo + 1);
        if (endNo == ResultConfiguration.INF_DOCS) {
            resultCount = count;
        }

        Assert.assertEquals(id, resultCount, slicedList.size());
        Assert.assertEquals(id, expected, slicedList.toString());
    }


    @Test
    public void testProcess() {
        testProcess("p1",
                        new Sorting(Field.RSV, Order.DESCENDING),
                        "{DataList [{ResultDocument 0. [source] {PersonList [author]}: ''title4'' (-2147483648)}, {ResultDocument 0.60 [source] {PersonList [author]}: ''title3'' (-2147483648)}, {ResultDocument 0. [source] {PersonList [author]}: ''title2'' (-2147483648)}, {ResultDocument 0. [source] {PersonList [author]}: ''title1'' (-2147483648)}, {ResultDocument 0. [source] {PersonList [author]}: ''title0'' (-2147483648)}]}",
                        0, ResultConfiguration.INF_DOCS, Field.AUTHOR, Field.TITLE);
        testProcess("p2",
                        new Sorting(Field.RSV, Order.ASCENDING),
                        "{DataList [{ResultDocument 0. [source] {PersonList [author]}: ''title0'' (-2147483648)}, {ResultDocument 0. [source] {PersonList [author]}: ''title1'' (-2147483648)}, {ResultDocument 0. [source] {PersonList [author]}: ''title2'' (-2147483648)}, {ResultDocument 0.60 [source] {PersonList [author]}: ''title3'' (-2147483648)}, {ResultDocument 0. [source] {PersonList [author]}: ''title4'' (-2147483648)}]}",
                        0, ResultConfiguration.INF_DOCS, Field.AUTHOR, Field.TITLE);

        testProcess("p3",
                        new Sorting(Field.AUTHOR, Order.DESCENDING),
                        "{DataList [{ResultDocument 0. [source] {PersonList [author]}: ''title0'' (-2147483648)}, {ResultDocument 0. [source] {PersonList [author]}: ''title1'' (-2147483648)}, {ResultDocument 0. [source] {PersonList [author]}: ''title2'' (-2147483648)}, {ResultDocument 0.60 [source] {PersonList [author]}: ''title3'' (-2147483648)}, {ResultDocument 0. [source] {PersonList [author]}: ''title4'' (-2147483648)}]}",
                        0, ResultConfiguration.INF_DOCS, Field.AUTHOR, Field.TITLE);
        testProcess("p4",
                        new Sorting(Field.AUTHOR, Order.ASCENDING),
                        "{DataList [{ResultDocument 0. [source] {PersonList [author]}: ''title0'' (-2147483648)}, {ResultDocument 0. [source] {PersonList [author]}: ''title1'' (-2147483648)}, {ResultDocument 0. [source] {PersonList [author]}: ''title2'' (-2147483648)}, {ResultDocument 0.60 [source] {PersonList [author]}: ''title3'' (-2147483648)}, {ResultDocument 0. [source] {PersonList [author]}: ''title4'' (-2147483648)}]}",
                        0, ResultConfiguration.INF_DOCS, Field.AUTHOR, Field.TITLE);

        testProcess("p5",
                        new Sorting(Field.RSV, Order.DESCENDING),
                        "{DataList [{ResultDocument 0. [source] {PersonList [author]}: ''null'' (-2147483648)}, {ResultDocument 0.60 [source] {PersonList [author]}: ''null'' (-2147483648)}, {ResultDocument 0. [source] {PersonList [author]}: ''null'' (-2147483648)}, {ResultDocument 0. [source] {PersonList [author]}: ''null'' (-2147483648)}, {ResultDocument 0. [source] {PersonList [author]}: ''null'' (-2147483648)}]}",
                        0, ResultConfiguration.INF_DOCS, Field.AUTHOR);
        testProcess("p6",
                        new Sorting(Field.RSV, Order.ASCENDING),
                        "{DataList [{ResultDocument 0. [source] {PersonList [author]}: ''null'' (-2147483648)}, {ResultDocument 0. [source] {PersonList [author]}: ''null'' (-2147483648)}, {ResultDocument 0. [source] {PersonList [author]}: ''null'' (-2147483648)}, {ResultDocument 0.60 [source] {PersonList [author]}: ''null'' (-2147483648)}, {ResultDocument 0. [source] {PersonList [author]}: ''null'' (-2147483648)}]}",
                        0, ResultConfiguration.INF_DOCS, Field.AUTHOR);

        testProcess("p7",
                        new Sorting(Field.AUTHOR, Order.DESCENDING),
                        "{DataList [{ResultDocument 0. [source] {PersonList [author]}: ''null'' (-2147483648)}, {ResultDocument 0. [source] {PersonList [author]}: ''null'' (-2147483648)}, {ResultDocument 0. [source] {PersonList [author]}: ''null'' (-2147483648)}, {ResultDocument 0.60 [source] {PersonList [author]}: ''null'' (-2147483648)}, {ResultDocument 0. [source] {PersonList [author]}: ''null'' (-2147483648)}]}",
                        0, ResultConfiguration.INF_DOCS, Field.AUTHOR);
        testProcess("p8",
                        new Sorting(Field.AUTHOR, Order.ASCENDING),
                        "{DataList [{ResultDocument 0. [source] {PersonList [author]}: ''null'' (-2147483648)}, {ResultDocument 0. [source] {PersonList [author]}: ''null'' (-2147483648)}, {ResultDocument 0. [source] {PersonList [author]}: ''null'' (-2147483648)}, {ResultDocument 0.60 [source] {PersonList [author]}: ''null'' (-2147483648)}, {ResultDocument 0. [source] {PersonList [author]}: ''null'' (-2147483648)}]}",
                        0, ResultConfiguration.INF_DOCS, Field.AUTHOR);

        testProcess("p9",
                        new Sorting(Field.AUTHOR, Order.DESCENDING),
                        "{DataList [{ResultDocument 0. [source] {PersonList [author]}: ''null'' (-2147483648)}, {ResultDocument 0. [source] {PersonList [author]}: ''null'' (-2147483648)}, {ResultDocument 0. [source] {PersonList [author]}: ''null'' (-2147483648)}, {ResultDocument 0.60 [source] {PersonList [author]}: ''null'' (-2147483648)}, {ResultDocument 0. [source] {PersonList [author]}: ''null'' (-2147483648)}]}",
                        0, 5, Field.AUTHOR);

        testProcess("p10",
                        new Sorting(Field.AUTHOR, Order.ASCENDING),
                        "{DataList [{ResultDocument 0. [source] {PersonList [author]}: ''null'' (-2147483648)}, {ResultDocument 0. [source] {PersonList [author]}: ''null'' (-2147483648)}, {ResultDocument 0. [source] {PersonList [author]}: ''null'' (-2147483648)}, {ResultDocument 0.60 [source] {PersonList [author]}: ''null'' (-2147483648)}, {ResultDocument 0. [source] {PersonList [author]}: ''null'' (-2147483648)}]}",
                        0, 5, Field.AUTHOR);

        testProcess("p11", new Sorting(Field.RSV, Order.ASCENDING),
                        "{DataList [{ResultDocument 0.60 [source] {PersonList [author]}: ''null'' (-2147483648)}]}", 3,
                        3, Field.AUTHOR);

        testProcess("p12",
                        new Sorting(Field.RSV, Order.ASCENDING),
                        "{DataList [{ResultDocument 0. [source] {PersonList [author]}: ''null'' (-2147483648)}, {ResultDocument 0.60 [source] {PersonList [author]}: ''null'' (-2147483648)}]}",
                        2, 3, Field.AUTHOR);

        testProcess("p13",
                        new Sorting(Field.RSV, Order.ASCENDING),
                        "{DataList [{ResultDocument 0. [source] null: ''null'' (-2147483648)}, {ResultDocument 0.60 [source] null: ''null'' (-2147483648)}]}",
                        2, 3, new Field[] {});
    }


    private void testProcess(String id, Sorting sorting, String expected, int startDoc, int endDoc, Field... fields) {
        ResultConfiguration resultConfig = new ResultConfiguration(startDoc, endDoc, Arrays.asList(fields), sorting);
        DocumentListFilter filter = new DocumentListFilter(resultConfig);

        ResultDocumentList list = getDocumentList(5);

        ResultDocumentList processedList = filter.process(list);
        Assert.assertEquals(id, expected, processedList.toString());
    }


    private void testSorting(String id, String expected, Sorting... sorting) {
        testSorting(id, 5, expected, sorting);
    }


    private void testSorting(String id, int count, String expected, Sorting... sorting) {
        ResultConfiguration resultConfig = new ResultConfiguration(0, ResultConfiguration.INF_DOCS,
                        Arrays.asList(Field.AUTHOR), sorting);
        DocumentListFilter filter = new DocumentListFilter(resultConfig);

        ResultDocumentList list = getDocumentList(count);

        try {
            ResultDocumentList sortedList = filter.sortList(list);
            Assert.assertEquals(id, expected, sortedList.toString());
        }
        catch (NullPointerException e) {
            Assert.fail("null dereferenced");
        }

    }


    public void testFilterFields(int count, String expected, Field... fields) {
        Sorting sorting = new Sorting(Field.RSV, Order.DESCENDING);
        ResultConfiguration resultConfig = new ResultConfiguration(0, ResultConfiguration.INF_DOCS,
                        Arrays.asList(fields), sorting);
        DocumentListFilter filter = new DocumentListFilter(resultConfig);

        ResultDocumentList list = getDocumentList(count);

        ResultDocumentList filteredList = filter.filterFields(list);

        Assert.assertEquals(expected, filteredList.toString());
    }


    private ResultDocumentList getDocumentList(int count) {
        ResultDocumentList list = new ResultDocumentList();

        for (int i = 0; (i < count); i++) {
            Document document = new TextDocument();
            ResultDocument result = new ResultDocument(document);
            double step = 1.0 / (count);
            result.setRsv(step * i);
            result.addSource("source");

            document.setTitle("title" + i);
            PersonList authorList = new PersonList();
            authorList.add(new Person("author"));
            document.setAuthorList(authorList);
            document.setYear(years[i % years.length]);
            document.setOid("id" + i);

            list.add(result);
        }

        return list;
    }
}
