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

package de.unidue.inf.is.ezdl.dlcore;

import java.util.Properties;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import de.unidue.inf.is.ezdl.dlcore.utils.PropertiesUtils;



/**
 * The common ground for tests in EzDL.
 * <p>
 * Sets the logger configuration.
 * 
 * @author mjordan
 */
public abstract class AbstractTestBase {

    /**
     * The file name of the properties file used for testing.
     */
    private static final String TESTING_PROPERTIES = "testing.properties";
    /**
     * The logger name. Doesn't appear anywhere so we can use a generic name.
     */
    private static final String LOGGER_NAME = "standard";
    /**
     * The logger.
     */
    private static Logger logger;
    /**
     * The properties usable for testing.
     */
    private Properties props;
    /**
     * If a test class should be skipped.
     */
    private boolean skip;


    public AbstractTestBase() {
        PropertyConfigurator.configure(AbstractTestBase.class.getResource("/log/logging.properties"));
        logger = Logger.getLogger(LOGGER_NAME);
        props = PropertiesUtils.readPropertiesFromFileTree(TESTING_PROPERTIES);
    }


    /**
     * Returns the logger.
     * 
     * @return the logger
     */
    protected Logger getLogger() {
        return logger;
    }


    /**
     * Checks if a test should be skipped.
     * 
     * @return if a test should be skipped
     */
    protected boolean skip() {
        return skip;
    }


    /**
     * Sets if tests should be skipped
     * 
     * @param skip
     *            if tests should be skipped
     */
    protected final void setSkip(boolean skip) {
        this.skip = skip;
    }


    protected Properties getTestProperties() {
        return props;
    }


    /**
     * Sleeps ms milliseconds.
     * 
     * @param ms
     *            number of milliseconds to sleep
     */
    protected void sleep(int ms) {
        try {
            Thread.sleep(ms);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    /**
     * Waits a given time to see if a condition holds during that whole time or
     * at some point until the time is passed.
     * <p>
     * This is useful for testing concurrent stuff.
     * 
     * @author mjordan
     */
    public abstract class AssertWaiter {

        /**
         * Default timeout in milliseconds.
         */
        private static final int TIMEOUT_MS = 3000;
        /**
         * Default peek frequency in 1/milliseconds.
         */
        private static final int PEEK_FREQ_MS = 20;


        /**
         * Checks every peekFreqMs if the given condition (@see
         * {@link #isConditionMet()}) is met.
         * 
         * @param message
         *            the message to set in the Assert
         * @param until
         *            If until is true, then the test fails if the condition is
         *            not met until timeoutMs is passed. If until is false, then
         *            the test fails if the condition is false at some point
         *            during timeoutMs (i.e. if it is not true the whole time of
         *            timeoutMs milliseconds).
         * @param timeoutMs
         *            the timeout
         * @param peekFreqMs
         *            check the condition every peekFreqMs milliseconds
         */
        private synchronized void assertWithTimeout(String message, boolean until, int timeoutMs, int peekFreqMs) {
            long startMs = System.currentTimeMillis();
            boolean conditionMet = false;
            boolean checkTimeStillRunning = true;
            boolean keepLooping = true;

            try {
                do {
                    wait(peekFreqMs);
                    checkTimeStillRunning = ((startMs + timeoutMs) > System.currentTimeMillis());
                    conditionMet = isConditionMet();

                    if (until) {
                        keepLooping = !conditionMet;
                    }
                    else {
                        keepLooping = conditionMet;
                    }
                }
                while (checkTimeStillRunning && keepLooping);
                String success = conditionMet ? "successfully" : "(FAILED)";
                logger.info("Exiting wait " + success + " after " //
                                + (System.currentTimeMillis() - startMs) //
                                + " ms (for \"" + message + "\")");
            }
            catch (InterruptedException e) {
            }

            Assert.assertTrue(message, conditionMet);
        }


        /**
         * Checks every peekFreqMs if the given condition (@see
         * {@link #isConditionMet()}) is met until the given timeout is passed.
         * 
         * @param message
         *            the message to set in the Assert
         * @param timeoutMs
         *            the timeout
         * @param peekFreqMs
         *            check the condition every peekFreqMs milliseconds
         */
        public void assertGetsOkay(String message, int timeoutMs, int peekFreqMs) {
            assertWithTimeout(message + " (get)", true, timeoutMs, peekFreqMs);
        }


        /**
         * Checks every peekFreqMs if the given condition (@see
         * {@link #isConditionMet()}) is met the whole time during the given
         * timeout period.
         * 
         * @param message
         *            the message to set in the Assert
         * @param timeoutMs
         *            the timeout
         * @param peekFreqMs
         *            check the condition every peekFreqMs milliseconds
         */
        public void assertStaysOkay(String message, int timeoutMs, int peekFreqMs) {
            assertWithTimeout(message + " (stay)", false, timeoutMs, peekFreqMs);
        }


        /**
         * Checks every peekFreqMs if the given condition (@see
         * {@link #isConditionMet()}) is met until the given timeout is passed.
         * <p>
         * A timeout of 3 seconds an a peek interval of 20 ms is used.
         * 
         * @param message
         *            the message to set in the Assert
         */
        public void assertGetsOkay(String message) {
            assertWithTimeout(message, true, TIMEOUT_MS, PEEK_FREQ_MS);
        }


        /**
         * Checks every peekFreqMs if the given condition (@see
         * {@link #isConditionMet()}) is met the whole time during the given
         * timeout period.
         * <p>
         * A timeout of 3 seconds an a peek interval of 20 ms is used.
         * 
         * @param message
         *            the message to set in the Assert
         */
        public void assertStaysOkay(String message) {
            assertWithTimeout(message, false, TIMEOUT_MS, PEEK_FREQ_MS);
        }


        /**
         * Checks every peekFreqMs if the given condition (@see
         * {@link #isConditionMet()}) is met after a grace period of 3 seconds
         * and stays met for 3 seconds afterwards.
         * <p>
         * A timeout of 3 seconds an a peek interval of 20 ms is used.
         * 
         * @param message
         *            the message to set in the Assert
         */
        public void assertGetsAndStaysOkay(String message) {
            assertGetsOkay(message);
            assertStaysOkay(message);
        }


        /**
         * Returns if the condition to be tested is met.
         * <p>
         * Overwrite this to return the condition you want to get tested.
         * 
         * @return true if the condition is met. Else false.
         */
        protected abstract boolean isConditionMet();
    }
}
