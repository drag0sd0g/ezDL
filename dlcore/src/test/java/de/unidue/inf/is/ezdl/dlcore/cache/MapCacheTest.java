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

package de.unidue.inf.is.ezdl.dlcore.cache;

import java.util.Arrays;

import org.junit.Test;

import de.unidue.inf.is.ezdl.dlcore.AbstractTestBase;
import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;
import de.unidue.inf.is.ezdl.dlcore.data.query.DefaultQuery;
import de.unidue.inf.is.ezdl.dlcore.data.query.DocumentQuery;
import de.unidue.inf.is.ezdl.dlcore.data.query.ResultDocumentList;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeCompare;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeCompare.Predicate;



public class MapCacheTest extends AbstractTestBase {

    @Test
    public void test() {
        MapCache cache = new MapCache();

        DocumentQuery query = getQuery("Dummy1");
        ResultDocumentList data = new ResultDocumentList();

        cache.put(query, data);

        getLogger().debug("Data put into cache: " + data);
        getLogger().debug("Key used: " + query);
        getLogger().debug("Data in cache: " + cache.getMap());

        DocumentQuery query2 = getQuery("Dummy1");
        ResultDocumentList data2 = (ResultDocumentList) cache.get(query2);

        getLogger().debug("Data got from cache: " + data2);
        getLogger().debug("Key used: " + query2);

        getLogger().debug("Data in cache: " + cache.getMap());
    }


    private DocumentQuery getQuery(String... wrapperNames) {
        QueryNodeCompare node = new QueryNodeCompare(Field.AUTHOR, Predicate.EQ, "fuhr");
        DocumentQuery query = new DocumentQuery(new DefaultQuery(node), Arrays.asList(wrapperNames));
        return query;
    }
}
