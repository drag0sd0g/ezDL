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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.unidue.inf.is.ezdl.dlcore.AbstractTestBase;



public class TimedCacheTest extends AbstractTestBase {

    private static final String KEY = "x";
    private static final String VALUE = "123";


    private class MockCache implements Cache {

        private Map<Object, Object> cache = new HashMap<Object, Object>();


        @Override
        public void put(Object key, Object value) {
            cache.put(key, value);
        }


        @Override
        public void remove(Object key) {
            cache.remove(key);
        }


        @Override
        public Object get(Object key) {
            return cache.get(key);
        }


        @Override
        public void flush() {
        }


        @Override
        public void shutdown() {
        }


        @Override
        public void clear() {
        }

    }


    private TimedCache timedCache;


    @Before
    public void init() {
        timedCache = new TimedCache(new MockCache(), 2, TimeUnit.SECONDS);
    }


    @After
    public void shutdownCache() {
        timedCache.shutdown();
    }


    @Test
    public void testDeletedAfterTimeout() {
        timedCache.put(KEY, VALUE);
        sleep(5000);
        Assert.assertNull(timedCache.get(KEY));
    }


    @Test
    public void testNotDeletedBeforeTimeout() {
        timedCache.put(KEY, VALUE);
        Assert.assertEquals(VALUE, timedCache.get(KEY));
    }

}
