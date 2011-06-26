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

import java.net.URL;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import de.unidue.inf.is.ezdl.dlcore.cache.Cache;



/**
 * Uses ehache for caching.
 * 
 * @author mjordan
 */
public class EhCache implements Cache {

    private net.sf.ehcache.Cache cache;


    public EhCache(String cacheName) {
        this(cacheName, null);
    }


    public EhCache(String cacheName, URL configuration) throws CacheException {
        CacheManager cacheManager;
        if (configuration != null) {
            cacheManager = CacheManager.create(configuration);
        }
        else {
            cacheManager = CacheManager.create();
        }
        cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            cacheManager.addCache(cacheName);
            cache = cacheManager.getCache(cacheName);
        }
    }


    @Override
    public Object get(Object key) {
        Element element = cache.get(key);
        if (element != null) {
            return element.getValue();
        }
        else {
            return null;
        }
    }


    @Override
    public void remove(Object key) {
        cache.remove(key);
    }


    @Override
    public void put(Object key, Object value) {
        Element element = new Element(key, value);
        cache.put(element);
    }


    @Override
    public void flush() {
        cache.flush();
    }


    @Override
    public void shutdown() {
        CacheManager.getInstance().shutdown();
    }


    @Override
    public void clear() {
        cache.removeAll();
    }
}
