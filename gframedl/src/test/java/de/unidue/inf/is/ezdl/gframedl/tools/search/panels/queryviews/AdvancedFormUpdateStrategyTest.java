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

package de.unidue.inf.is.ezdl.gframedl.tools.search.panels.queryviews;

import java.util.Collection;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;

import org.antlr.runtime.RecognitionException;
import org.junit.Before;
import org.junit.Test;

import de.unidue.inf.is.ezdl.dlcore.AbstractTestBase;
import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;
import de.unidue.inf.is.ezdl.dlcore.data.query.Query;
import de.unidue.inf.is.ezdl.dlfrontend.helper.FieldRegistry;
import de.unidue.inf.is.ezdl.dlfrontend.query.NoSuchFieldCodeException;
import de.unidue.inf.is.ezdl.dlfrontend.query.QueryFactory;
import de.unidue.inf.is.ezdl.dlfrontend.query.parsers.simple.WebLikeFactory;
import de.unidue.inf.is.ezdl.gframedl.tools.search.panels.queryviews.AdvancedFormUpdateStrategy.QueryField;



public class AdvancedFormUpdateStrategyTest extends AbstractTestBase {

    private static final Field TI = Field.TITLE;
    private static final Field YE = Field.YEAR;
    private static final Field FT = Field.TEXT;
    private static final Field AU = Field.AUTHOR;

    Map<Field, FieldInfo> fields = new EnumMap<Field, FieldInfo>(Field.class);
    QueryFactory factory = new WebLikeFactory(FieldRegistry.getInstance());

    AdvancedFormUpdateStrategy strategy = new AdvancedFormUpdateStrategy(fields, factory);


    private class FieldCounter {

        Map<Field, Integer> fieldCounts = new EnumMap<Field, Integer>(Field.class);


        public FieldCounter() {
        }


        public synchronized void count(Field fieldCode) {
            Integer count = getCount(fieldCode);
            count++;
            fieldCounts.put(fieldCode, count);
        }


        public synchronized Integer getCount(Field fieldCode) {
            Field key = fieldCode;
            Integer count = fieldCounts.get(key);
            if (count == null) {
                count = new Integer(0);
            }
            return count;
        }


        public int maxFieldCount() {
            int maxCount = 0;
            Collection<Integer> values = fieldCounts.values();
            for (Integer value : values) {
                if (value > maxCount) {
                    maxCount = value;
                }
            }
            return maxCount;
        }
    }


    public AdvancedFormUpdateStrategyTest() {
        createFieldInfo(AU);
        createFieldInfo(TI);
        createFieldInfo(YE);
        createFieldInfo(FT);
    }


    private void createFieldInfo(Field fieldCode) {
        FieldInfo field = new FieldInfo();
        field.fieldCode = fieldCode;
        // field.textField = new PromptTextField();
        fields.put(field.fieldCode, field);
    }


    @Before
    public void init() {
    }


    @Test
    public void testGetFieldStrings() {
        checkQuery("1", "test", "{1046: test}");
        checkQuery("2", "testa AND testb", "{1046: testa AND testb}");
        checkQuery("2a", "testa OR testb", "{1046: testa OR testb}");
        checkQuery("3", "testc AND testa AND testb", "{1046: testc AND testa AND testb}");
        checkQuery("3a", "testc OR testa AND testb", "{1046: testc OR testa AND testb}");
        checkQuery("4", "Author=author AND testa AND testb", "{1003: author}", "{1046: testa AND testb}");
        checkQuery("5", "Author=authora AND Author=authorb AND testa AND testb", "{1003: authora AND authorb}",
                        "{1046: testa AND testb}");
        checkQuery("5a", "Author=authora OR Author=authorb AND testa OR testb", "{1003: authora OR authorb}",
                        "{1046: testa OR testb}");
        checkQuery("6", "Author=authorb AND testa AND Author=authora AND testb", "{1003: authorb AND authora}",
                        "{1046: testa AND testb}");
        checkQuery("6a", "Author=authorb AND testa OR Author=authora AND testb", (String[]) null);
        checkQuery("7", "Author=a OR Title=b", (String[]) null);
        checkQuery("8", "Author=aa OR Author=bb AND Year=22", "{1003: aa OR bb}", "{31: 22}");
    }


    private void checkQuery(String message, String queryString, String... expectedFields) {
        try {
            Query query = factory.parse(queryString, FT);
            List<QueryField> fields = strategy.getFieldStrings(query.getTree());

            Set<String> expected = new HashSet<String>();

            if (expectedFields != null) {
                for (String field : expectedFields) {
                    expected.add(field);
                }
            }

            FieldCounter counter = new FieldCounter();
            for (QueryField field : fields) {
                counter.count(field.fieldCode);
                StringBuffer actual = new StringBuffer();
                actual.append("{");
                actual.append(field.fieldCode);
                actual.append(": ");
                actual.append(field.text);
                actual.append("}");
                if (!expected.contains(actual.toString())) {
                    Assert.fail("Not in expected: " + actual);
                }
                else {
                    expected.remove(actual.toString());
                }
            }

            if (expected.size() >= 1) {
                Assert.fail("Some expected fields were not found: " + expected.toString());
            }

            int maxCount = counter.maxFieldCount();
            if (maxCount > 1) {
                Assert.fail("More than one line belongs to the same field");
            }

        }
        catch (RecognitionException e) {
            e.printStackTrace();
            Assert.fail("Query syntax not correct");
        }
        catch (NoSuchFieldCodeException e) {
            e.printStackTrace();
            Assert.fail("Query syntax not correct");
        }
        catch (QueryViewUpdateException e) {
            if (expectedFields != null) {
                e.printStackTrace();
                Assert.fail("Update failed but wasn't expected to");
            }
        }
    }


    // @Test
    public void testGetQueryFromOneFields() {
        checkGetQueryFromFields("1", "aaaa", "", "", "", "(" + AU + "=aaaa)");
        checkGetQueryFromFields("2", "", "aaaa", "", "", "(" + TI + "=aaaa)");
        checkGetQueryFromFields("3", "", "", "aaaa", "", "(" + YE + "=aaaa)");
        checkGetQueryFromFields("4", "", "", "", "aaaa", "(" + FT + "=aaaa)");
        checkGetQueryFromFields("5", "", "", "", "aaaa", "(" + FT + "=aaaa)");
    }


    // @Test
    public void testGetQueryFromFields() {
        checkGetQueryFromFields("6", "bbbb", "", "", "aaaa", "{AND[-2]: (1046=aaaa)(1003=bbbb)}");
        // checkGetQueryFromFields("7", "Fuhr OR Schneiderman",
        // "interactive retrieval", ">1999", "eyetracker",
        // "{AND[-2]: {OR[1003]: (1003=Fuhr)(1003=Schneiderman)}(1046=eyetracker){AND[4]: (4=interactive)(4=retrieval)}(31>1999)}");
        // checkGetQueryFromFields("8", "aa OR bb", "", "22", "",
        // "{AND[-2]: {OR[1003]: (1003=aa)(1003=bb)}(31=22)}");
    }


    private void checkGetQueryFromFields(String message, String author, String title, String year, String text,
                    String expected) {
        FieldInfo a = fields.get(AU);
        FieldInfo t = fields.get(TI);
        FieldInfo y = fields.get(YE);
        FieldInfo f = fields.get(FT);

        a.textField.setText(author);
        t.textField.setText(title);
        y.textField.setText(year);
        f.textField.setText(text);

        Query query = strategy.getQueryFromFields(fields.values());
        Assert.assertEquals(message, expected, query.toString());
    }

}
