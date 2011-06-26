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

package de.unidue.inf.is.ezdl.dlbackend.agent;

import java.util.HashSet;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import de.unidue.inf.is.ezdl.dlbackend.AbstractBackendTestBase;



/**
 * Test for the RequestIDFactory. This class initializes THREAD_COUNT threads
 * that somehow simultaneously ask for a new RequestID. The test fails if any
 * two ids are equal.
 * 
 * @author mjordan
 */
public class RequestIDFactoryTest extends AbstractBackendTestBase {

    /**
     * How many threads to start simultaneously.
     */
    private static final int THREAD_COUNT = 100;
    /**
     * How many attempts to make to get a non-null ID from a thread.
     */
    private static final int MAX_TRIES = 10000;
    /**
     * The local part to use for the IDs.
     */
    private static final String LOCAL_PART = "localhost";


    private class IDRequester extends Thread {

        private String id = null;


        @Override
        public void run() {
            id = (RequestIDFactory.getInstance().getNextRequestID(LOCAL_PART));
        }


        /**
         * @return the id
         */
        public String getID() {
            return id;
        }

    }


    private IDRequester[] threads = new IDRequester[THREAD_COUNT];


    @Before
    public void init() {
        for (int i = 0; (i < THREAD_COUNT); i++) {
            threads[i] = new IDRequester();
        }
    }


    @Test
    public void getNextRequestIDTest() {
        for (int i = 0; (i < THREAD_COUNT); i++) {
            threads[i].start();
        }

        try {
            Thread.sleep(1000);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }

        Set<String> idSet = new HashSet<String>();
        for (int i = 0; (i < THREAD_COUNT); i++) {
            String id = null;
            int tryCounter = 0;
            while ((id == null) && (tryCounter < MAX_TRIES)) {
                id = threads[i].getID();
                tryCounter++;
            }
            Assert.assertTrue("ID " + id + " was already in the set.", idSet.add(id));
        }

    }

}
