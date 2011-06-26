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

package de.unidue.inf.is.ezdl.dlbackend.agent.timer;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import de.unidue.inf.is.ezdl.dlbackend.AbstractBackendTestBase;
import de.unidue.inf.is.ezdl.dlbackend.message.Message;



/**
 * Test for the Timer. Sends String messages to a test object and sees what
 * arrived when.
 * 
 * @author mjordan
 */
public class TimerTest extends AbstractBackendTestBase {

    protected static final Logger logger = Logger.getLogger(TimerTest.class);


    class TestableTimer extends Timer {

        private volatile long timestamp = 0;


        public synchronized void setTime(long time) {
            // System.out.println("setTime(): " + time);
            this.timestamp = time;
            notify();
        }


        @Override
        protected long getNow() {
            // System.out.println("getNow(): " + timestamp);
            return timestamp;
        }


        @Override
        void waitTimer(long timeLeftToWait) throws InterruptedException {
            wait(1);
        }
    }


    class TestTimeable implements Timeable {

        List<String> stringsReceived;
        List<Message> messagesReceived;


        public TestTimeable() {
            stringsReceived = Collections.synchronizedList(new LinkedList<String>());
            messagesReceived = Collections.synchronizedList(new LinkedList<Message>());
        }


        @Override
        public void wakeup(String message) {
            logger.info("Timeable receiving message " + message);
            stringsReceived.add(message);
        }


        @Override
        public void wakeup(Message message) {
            messagesReceived.add(message);
        }


        /**
         * @return the stringsReceived
         */
        public List<String> getStringsReceived() {
            return stringsReceived;
        }


        /**
         * @return the messagesReceived
         */
        public List<Message> getMessagesReceived() {
            return messagesReceived;
        }

    }


    TestTimeable timeable;


    @Before
    public void init() {
        timeable = new TestTimeable();
    }


    @Test
    public void dummyTest() {
        // stand-in until the tests are working
    }


    /**
     * Inspection after a too short wait.
     */
    @Test
    public void timerTestTooEarly() {
        TestableTimer timer = new TestableTimer();
        timer.init(1000, timeable, "msg1");
        timer.startTimer();
        timer.setTime(200);

        new StringsReceivedWaiter("[]").assertStaysOkay("1");
    }


    /**
     * Inspection after a long enough time.
     */
    @Test
    public void timerTestLater() {
        TestableTimer timer = new TestableTimer();
        timer.init(500, timeable, "msg1");
        timer.startTimer();
        timer.setTime(1000);

        new StringsReceivedWaiter("[msg1]").assertGetsOkay("1");
    }


    /**
     * Two inspections: one too early and one late enough.
     */
    @Test
    public void timerTestRepeatedMeasure() {
        TestableTimer timer = new TestableTimer();
        timer.init(1000, timeable, "msg1");
        timer.startTimer();

        timer.setTime(800);
        new StringsReceivedWaiter("[]").assertStaysOkay("1");

        timer.setTime(900);
        new StringsReceivedWaiter("[]").assertStaysOkay("2");

        timer.setTime(1000);
        new StringsReceivedWaiter("[msg1]").assertGetsOkay("3");

        timer.setTime(1200);
        new StringsReceivedWaiter("[msg1]").assertStaysOkay("4");
    }


    /**
     * Multiple strings sent by multiple timers.
     */
    @Test
    public void timerTestRepeatedMeasure2() {
        TestableTimer timer1000 = new TestableTimer();
        timer1000.init(1000, timeable, "msg1000");
        timer1000.startTimer();

        TestableTimer timer500 = new TestableTimer();
        timer500.init(500, timeable, "msg500");
        timer500.startTimer();

        timer1000.setTime(300);
        timer500.setTime(300);

        new StringsReceivedWaiter("[]").assertStaysOkay("1");

        timer1000.setTime(400);
        timer500.setTime(400);

        new StringsReceivedWaiter("[]").assertStaysOkay("2");

        timer1000.setTime(500);
        timer500.setTime(500);

        new StringsReceivedWaiter("[msg500]").assertGetsOkay("3", 5000, 50);

        timer1000.setTime(600);
        timer500.setTime(600);

        new StringsReceivedWaiter("[msg500]").assertStaysOkay("4", 5000, 50);

        timer1000.setTime(1000);
        timer500.setTime(1000);

        new StringsReceivedWaiter("[msg500, msg1000]").assertGetsOkay("5");

    }


    class StringsReceivedWaiter extends AssertWaiter {

        private String expected;


        public StringsReceivedWaiter(String expected) {
            this.expected = expected;
        }


        @Override
        protected boolean isConditionMet() {
            final List<String> stringsReceived = timeable.getStringsReceived();
            // System.out.println(stringsReceived);
            return stringsReceived.toString().equals(expected);
        }

    }

}
