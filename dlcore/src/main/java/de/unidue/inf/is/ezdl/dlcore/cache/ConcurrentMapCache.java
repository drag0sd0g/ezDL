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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;



/**
 * Keeps data in a ConcurrentHashMap.
 */
public class ConcurrentMapCache implements Cache {

    private Map<Object, Object> cache = new ConcurrentHashMap<Object, Object>();


    @Override
    public void put(Object key, Object stuff) {
        if (!cache.containsKey(key)) {
            cache.put(key, stuff);
        }
        else {
            throw new IllegalStateException("key " + key + " already in cache");
        }
    }


    @Override
    public Object get(Object key) {
        return cache.get(key);
    }


    @Override
    public void remove(Object key) {
        cache.remove(key);
    }


    /**
     * Returns the internal map for testing purposes.
     * 
     * @return the internal map
     */
    Map<Object, Object> getMap() {
        return cache;
    }


    @Override
    public void flush() {
        // shrug
    }


    @Override
    public void shutdown() {
        // shrug
    }


    @Override
    public void clear() {
        cache.clear();
    }
}
