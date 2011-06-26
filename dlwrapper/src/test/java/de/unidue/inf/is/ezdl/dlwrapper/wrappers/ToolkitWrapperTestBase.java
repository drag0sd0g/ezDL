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

package de.unidue.inf.is.ezdl.dlwrapper.wrappers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.NameValuePair;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.unidue.inf.is.ezdl.dlbackend.AbstractBackendTestBase;
import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;
import de.unidue.inf.is.ezdl.dlcore.data.query.DefaultQuery;
import de.unidue.inf.is.ezdl.dlcore.data.query.DocumentQuery;
import de.unidue.inf.is.ezdl.dlcore.data.query.Query;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeBool;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeCompare;
import de.unidue.inf.is.ezdl.dlwrapper.Wrapper;
import de.unidue.inf.is.ezdl.dlwrapper.toolkit.HttpClientCrawler;
import de.unidue.inf.is.ezdl.dlwrapper.toolkit.ToolkitAPI;



/**
 * This class is a base class for test cases for {@link ToolkitAPI}-based
 * {@link Wrapper} classes.
 * <p>
 * It has a way to mock the {@link ToolkitAPI} in order to test for error
 * conditions and pre-defined input to make sure testing happens under
 * controlled conditions.
 * 
 * @author mjordan
 */
public abstract class ToolkitWrapperTestBase extends AbstractBackendTestBase {

    private String testId = "none";

    private static AtomicInteger streamNumber = new AtomicInteger(0);

    private boolean fileModeRecord = false;

    private volatile boolean simulateConnectionProblem = false;


    /**
     * The TestableToolkitAPI is a mock layer for the {@link ToolkitAPI}. It can
     * be switched between two modes. In the one mode, it directly accesses a
     * remote resource using an HTTP connection and saves the downloaded data
     * into a given directory according to some directory structure. In this
     * mode it is kind of a "tee" for HTTP connections. In the second mode, the
     * resources are read from the previously saved files, allowing for offline
     * testing and - even more important - testing arbitrarily altered input
     * data - e.g. to allow for testing the behaviour under certain error
     * conditions such as a changed page layout.
     * <p>
     * To change the mode, use
     * {@link ToolkitWrapperTestBase#setRecordingMode(boolean)}.
     * <p>
     * To set the directory name for a particular test case, use
     * {@link ToolkitWrapperTestBase#setTestId(String)}.
     * <p>
     * To simulate a connection problem, use
     * {@link ToolkitWrapperTestBase#setSimulateConnectionProblem(boolean)}.
     * 
     * @author mjordan
     */
    public class TestableToolkitAPI extends ToolkitAPI {

        private String dirName = "common";


        /**
         * The constructor creates a new object, using the parameter given as
         * the directory name in the resources directory to save and read the
         * data.
         * 
         * @param dirName
         *            the directory name relative to the resources directory to
         *            save and read the data.
         */
        public TestableToolkitAPI(String dirName) {
            this.dirName = dirName;
        }


        @Override
        protected HttpClientCrawler getHttpClientCrawler(URL baseurl, String encoding) {
            HttpClientCrawler crawler;
            try {
                if ((getProxyHost() != null) && (getProxyPort() > 0) && (getProxyPort() < 65535)) {
                    getLogger().debug("Proxy crawler");
                    crawler = new MockCrawler(dirName, baseurl, encoding, getProxyHost(), getProxyPort());
                }
                else {
                    crawler = new MockCrawler(dirName, baseurl.toString(), encoding);
                }
                crawler.setCookieJar(cookies);
            }
            catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }

            return crawler;
        }

    }


    private class MockCrawler extends HttpClientCrawler {

        private String htmlDirPathStr;


        public MockCrawler(String dirName, URL url, String encoding, String proxyHost, int proxyPort)
                        throws MalformedURLException {
            super(url, encoding, proxyHost, proxyPort);
            init(dirName);
        }


        public MockCrawler(String dirName, String url, String encoding) throws MalformedURLException {
            super(url, encoding);
            getLogger().debug("MockCrawler() URL: " + url);
            init(dirName);
        }


        private void init(String dirName) throws IllegalArgumentException {
            final String fullDirNameStr = "/" + dirName + "/.sentinel";
            URL htmlDir = this.getClass().getResource(fullDirNameStr);
            if (htmlDir == null) {
                throw new IllegalArgumentException("Sentinel file " + fullDirNameStr + " not found");
            }
            getLogger().debug("Reading " + htmlDir.getPath());
            try {
                File htmlDirPath = new File(htmlDir.toURI());
                htmlDirPathStr = htmlDirPath.getParent();
            }
            catch (URISyntaxException e) {
                getLogger().error("File not found!", e);
                throw new IllegalArgumentException("");
            }
        }


        @Override
        protected InputStream fetch(Method method, List<NameValuePair> params, List<NameValuePair> formFields)
                        throws IOException {
            getLogger().debug("CRAWLER fetch(M,L): " + params);
            InputStream is = null;
            if (fileModeRecord) {
                is = super.fetch(method, params, formFields);
                if (is != null) {
                    is = saveStream(is, streamNumber.incrementAndGet());
                }
            }
            else {
                if (simulateConnectionProblem) {
                    throw new IOException("Simulated connection problem");
                }
                is = getFileStream(streamNumber.incrementAndGet());
            }
            return is;
        }


        private InputStream saveStream(InputStream stream, int num) {
            InputStream newInStream = null;
            File out = getFile(num);
            getLogger().debug("Saving stream data into " + out);

            try {
                FileOutputStream fos = new FileOutputStream(out);
                int readCount;
                int bufLen = 10240;
                byte[] buf = new byte[bufLen];
                while ((readCount = stream.read(buf, 0, bufLen)) != -1) {
                    fos.write(buf, 0, readCount);
                }

                stream.close();
                newInStream = getFileStream(num);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            return newInStream;
        }


        private InputStream getFileStream(int number) throws FileNotFoundException {
            File file = getFile(number);
            FileInputStream fis = new FileInputStream(file);
            return fis;
        }


        private File getFile(int num) {
            getLogger().debug("File " + num + " ##########################################");

            File dir = new File(htmlDirPathStr + "/" + testId);
            dir.mkdir();
            File file = new File(dir, "input-" + num + ".html");
            return file;
        }
    }


    /**
     * Resets the streamNumber to 0.
     * 
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        streamNumber.set(0);
    }


    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }


    @Test
    public void dummyTest() {
        // Dummy to please JUnit
    }


    /**
     * Creates a simple {@link DocumentQuery} with a query for a single field.
     * 
     * @param field
     *            the field to query
     * @param term
     *            the term to query for
     * @return the DocumentQuery
     */
    protected DocumentQuery createDocumentQuery(Field field, String term) {
        DocumentQuery query = new DocumentQuery(getQuery(field, term), Collections.<String> emptyList());
        return query;
    }


    private Query getQuery(Field field, String term) {
        QueryNodeBool d = new QueryNodeBool();
        addConditionToBool(d, field, term);
        Query q = new DefaultQuery(d);
        return q;
    }


    protected static void addConditionToBool(QueryNodeBool parent, Field field, String term) {
        QueryNodeCompare comp = new QueryNodeCompare(field, term);
        parent.addChild(comp);
    }


    /**
     * Sets the test ID that is used for file names of mocked HTML downloads.
     * 
     * @param testId
     *            the test ID
     */
    protected void setTestId(String testId) {
        this.testId = testId;
    }


    /**
     * Set to true, if a connection problem is to be simulated.
     * 
     * @param simulateProblem
     *            true, if connection problem, else false
     */
    protected void setSimulateConnectionProblem(boolean simulateProblem) {
        this.simulateConnectionProblem = simulateProblem;
    }


    /**
     * Sets the recording mode.
     * <p>
     * <b>Record on</b> means that the connections of the Toolkit crawler are
     * real, live connections to remote sites. The downloaded content is passed
     * to the ToolkitAPI but also saved in the file system in a directory with
     * the name of the current test ID.
     * <p>
     * <b>Record off</b> means that the files formerly saved to disk are now
     * loaded in order to "mock the internet away" and make offline and
     * deterministic testing of wrappers feasible.
     * <p>
     * The default recording mode is "off".
     * 
     * @param recordOn
     *            true, if connections are live and recorded to disk. False, if
     *            connections are mocked and content loaded from disk
     */
    protected void setRecordingMode(boolean recordOn) {
        this.fileModeRecord = recordOn;
    }
}
