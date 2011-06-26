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
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;



/**
 * Cache that provides timeouts for stored values.
 * 
 * @author tbeckers
 */
public final class TimedCache implements Cache {

    private static final int CLEANING_INTERVAL_MS = 200;

    private static Logger logger = Logger.getLogger(TimedCache.class);

    private Cache cache;
    private Map<Object, Long> timestamps;
    private int timeout;
    private TimeUnit timeUnit;
    private ScheduledExecutorService executorService;


    public TimedCache(Cache cache, int timeOut, TimeUnit timeUnit) {
        this.executorService = Executors.newSingleThreadScheduledExecutor();
        this.cache = cache;
        this.timestamps = new HashMap<Object, Long>();
        this.timeout = timeOut;
        this.timeUnit = timeUnit;
        startCleaner();
    }


    private void startCleaner() {
        Runnable r = new Runnable() {

            @Override
            public void run() {
                long currentTime = System.currentTimeMillis();
                Set<Object> keysToRemove = new HashSet<Object>();
                for (Entry<Object, Long> entry : timestamps.entrySet()) {
                    Long timestamp = timestamps.get(entry.getKey());
                    if (timestamp != null && currentTime - timestamp > timeUnit.toMillis(timeout)) {
                        keysToRemove.add(entry.getKey());
                    }
                }
                for (Object key : keysToRemove) {
                    cache.remove(key);
                }
            }
        };
        executorService.scheduleWithFixedDelay(r, 200, CLEANING_INTERVAL_MS, TimeUnit.MILLISECONDS);
    }


    @Override
    public synchronized void put(Object key, Object value) {
        cache.put(key, value);
        timestamps.put(key, System.currentTimeMillis());
    }


    @Override
    public synchronized Object get(Object key) {
        return cache.get(key);
    }


    @Override
    public synchronized void remove(Object key) {
        cache.remove(key);
    }


    @Override
    public synchronized void flush() {
        cache.flush();
    }


    @Override
    public synchronized void shutdown() {
        executorService.shutdown();
        try {
            executorService.awaitTermination(500, TimeUnit.MILLISECONDS);
        }
        catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
        cache.shutdown();
    }


    @Override
    public synchronized void clear() {
        cache.clear();
        timestamps.clear();
    }

}
