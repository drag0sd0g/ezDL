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

package de.unidue.inf.is.ezdl.dlwrapper.wrappers.dummy;

import java.util.Properties;
import java.util.Random;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import de.unidue.inf.is.ezdl.dlbackend.AbstractBackendTestBase;
import de.unidue.inf.is.ezdl.dlbackend.agent.Agent;
import de.unidue.inf.is.ezdl.dlbackend.data.StoredDocumentList;
import de.unidue.inf.is.ezdl.dlbackend.mock.MockAgent;
import de.unidue.inf.is.ezdl.dlcore.cache.Cache;
import de.unidue.inf.is.ezdl.dlcore.cache.MapCache;
import de.unidue.inf.is.ezdl.dlcore.data.query.DocumentQuery;
import de.unidue.inf.is.ezdl.dlcore.data.query.QueryFactory;



public class DummyWrapperTest extends AbstractBackendTestBase {

    Agent agent;
    DummyWrapper wrapper;
    Cache cache = new MapCache();


    @Before
    public void init() {
        Properties props = new Properties();
        agent = new MockAgent();
        agent.init("agentname", props);
        wrapper = new DummyWrapper();
        wrapper.init(agent, cache);
    }


    @Test
    public void testDocumentQuery1() {
        DocumentQuery documentQuery = QueryFactory.getQuery1();

        StoredDocumentList data = wrapper.process(documentQuery);

        Assert.assertEquals("", DummyWrapper.RESULT_LIST_SIZE, data.size());
    }


    @Test
    public void testDocumentQueryRandomNumber() {
        Random r = new Random();
        int count = 1 + r.nextInt(100);
        DocumentQuery documentQuery = QueryFactory.getYearQuery(count);

        StoredDocumentList data = wrapper.process(documentQuery);

        Assert.assertEquals("", count, data.size());
    }


    @Test
    public void testDocumentQuery0() {
        checkQueryN(0);
    }


    @Test
    public void testDocumentQuery5() {
        checkQueryN(5);
    }


    @Test
    public void testDocumentQuery1000() {
        checkQueryN(1000);
    }


    private void checkQueryN(int count) {
        DocumentQuery documentQuery = QueryFactory.getYearQuery(count);

        StoredDocumentList data = wrapper.process(documentQuery);

        Assert.assertEquals("", count, data.size());
    }

}
