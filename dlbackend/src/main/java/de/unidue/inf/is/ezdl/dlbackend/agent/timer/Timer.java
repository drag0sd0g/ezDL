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

import de.unidue.inf.is.ezdl.dlbackend.message.Message;



/**
 * The ezDL equivalent of a wake up call service.
 * <p>
 * The Timer is initialized with either a Message or a String and waits for a
 * given time. After that time, the Timer wakes up and delivers the signal
 * (Message or String) to the Timeable given.
 */
public class Timer extends Thread {

    private enum TimerState {
        RAW, // state before being initialized
        SET, // state after initialization
        RUNNING, // while running (after "start")
        EXPIRED, // after the timer expired
        KILLED
        // the timer has been stopped explicitly
    };


    /**
     * The time to wait before Timer goes off.
     */
    private long time;
    /**
     * The Timeable that gets the signal.
     */
    private Timeable timeable = null;
    /**
     * The time when the Timer wakes up and delivers the signal.
     */
    private long endTime;
    /**
     * The message to deliver. This might be either a String or a Message
     * object.
     */
    private Object message;

    private volatile TimerState state = TimerState.RAW;


    /**
     * Constructor. Creates the Timer and starts its Thread.
     */
    public Timer() {
    }


    /**
     * Initializes the Timer with a Message object.
     * 
     * @param time
     *            the time in milliseconds to wait before the message is
     *            delivered to timeable
     * @param timeable
     *            the Timeable to deliver to
     * @param msg
     *            the message to deliver
     */
    public void init(long time, Timeable timeable, Message msg) {
        this.time = time;
        this.timeable = timeable;
        this.message = msg;
        this.state = TimerState.SET;
    }


    /**
     * Initializes the Timer with a String object.
     * 
     * @param time
     *            the time in milliseconds to wait before the message is
     *            delivered to timeable
     * @param timeable
     *            the Timeable to deliver to
     * @param ID
     *            the String to deliver
     */
    public void init(long time, Timeable timeable, String ID) {
        this.time = time;
        this.timeable = timeable;
        this.message = ID;
        this.state = TimerState.SET;
    }


    /**
     * Returns, if the Timer is still waiting.
     * 
     * @return true, if the Timer is waiting, else false
     */
    public boolean isWaiting() {
        return state == TimerState.RUNNING;
    }


    /**
     * Starts the timer.
     */
    public synchronized void startTimer() {
        if (state == TimerState.SET) {
            endTime = getNow() + time;
            start();
            notifyAll();
        }
        else {
            throw new RuntimeException("You have to call init() before starting the timer");
        }
    }


    /**
     * Kills the Timer, stops the Thread.
     */
    public synchronized void killTimer() {
        state = TimerState.KILLED;
        interrupt();
        notifyAll();
    }


    @Override
    public synchronized void run() {

        state = TimerState.RUNNING;

        while (state == TimerState.RUNNING) {
            waitUntilTimeoutExpires();

            // System.out.println("state after waiting: " + state);

            if (state == TimerState.EXPIRED) {
                if (message instanceof Message) {
                    timeable.wakeup((Message) message);
                }
                else if (message instanceof String) {
                    timeable.wakeup((String) message);
                }
            }
        }
    }


    private void waitUntilTimeoutExpires() {
        long timeLeftToWait;
        while ((timeLeftToWait = endTime - getNow()) > 0) {
            try {
                // System.out.println("endtime " + endTime + " waiting " +
                // timeLeftToWait);
                waitTimer(timeLeftToWait);
            }
            catch (InterruptedException e) {
                // System.out.println("interrupted");
            }
            if (state != TimerState.RUNNING) {
                return;
            }
        }

        state = TimerState.EXPIRED;
        // System.out.println("left wait loop");
    }


    void waitTimer(long timeLeftToWait) throws InterruptedException {
        wait(timeLeftToWait);
    }


    /**
     * Returns the current time stamp. This method is there for convenience and
     * to enable mock objects of this class.
     * 
     * @return the current time
     */
    protected long getNow() {
        return System.currentTimeMillis();
    }

}
