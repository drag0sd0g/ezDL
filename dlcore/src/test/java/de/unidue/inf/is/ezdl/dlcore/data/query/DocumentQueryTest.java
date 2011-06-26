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

package de.unidue.inf.is.ezdl.dlcore.data.query;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import de.unidue.inf.is.ezdl.dlcore.AbstractTestBase;
import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeCompare;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeCompare.Predicate;



public class DocumentQueryTest extends AbstractTestBase {

    @Test
    public void testHashCode() {
        checkHashCode();
        checkHashCode("Dummy1");
        checkHashCode("Dummy1", "Dummy2");
    }


    @Test
    public void testEquals() {
        checkEquals();
        checkEquals("Dummy1");
        checkEquals("Dummy1", "Dummy2");
    }


    private void checkEquals(String... wrapperNames) {
        DocumentQuery query1 = getQuery(wrapperNames);
        DocumentQuery query2 = getQuery(wrapperNames);

        Assert.assertTrue(query1.equals(query2));
    }


    private void checkHashCode(String... wrapperNames) {
        DocumentQuery query1 = getQuery(wrapperNames);
        DocumentQuery query2 = getQuery(wrapperNames);

        Assert.assertEquals(query1.hashCode(), query2.hashCode());
    }


    private DocumentQuery getQuery(String... wrapperNames) {
        QueryNodeCompare node = new QueryNodeCompare(Field.AUTHOR, Predicate.EQ, "fuhr");
        DocumentQuery query = new DocumentQuery(new DefaultQuery(node), Arrays.asList(wrapperNames));
        return query;
    }
}
