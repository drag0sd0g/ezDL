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

package de.unidue.inf.is.ezdl.dlbackend.mock;

import de.unidue.inf.is.ezdl.dlbackend.agent.StartedBy;
import de.unidue.inf.is.ezdl.dlbackend.agent.handlers.AbstractRequestHandler;
import de.unidue.inf.is.ezdl.dlbackend.message.Message;



/**
 * Sleeps 20 seconds and is killable to be able to test canceling request.
 * 
 * @author mjordan
 */
@StartedBy(LongRunningMockMessageContent.class)
public class LongRunningMockRequestHandler extends AbstractRequestHandler {

    /**
     * The time that the request handler runs/waits to simulate activity.
     */
    private static final int WAIT_TIME_MS = 20000;
    /**
     * The time in milliseconds the handler sleeps in a row.
     */
    private static final int PEEK_TIME_MS = 20;


    @Override
    protected boolean work(Message message) {
        long startMs = System.currentTimeMillis();
        long endMs = startMs + WAIT_TIME_MS;
        getLogger().info("Acting like I work (but in reality I sleep for " //
                        + WAIT_TIME_MS / 1000 + " seconds, peeking every " + PEEK_TIME_MS + " ms)");
        try {
            do {
                wait(PEEK_TIME_MS);
            }
            while (!isHalted() && System.currentTimeMillis() < endMs);
            if (isHalted()) {
                getLogger().info("Got killed");
            }
            getLogger().info("isHalted() is " + isHalted() + " after " + (System.currentTimeMillis() - startMs));
        }
        catch (InterruptedException e) {
        }
        getLogger().info("Finished acting like I work");
        halt();
        return true;
    }

}
