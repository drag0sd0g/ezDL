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

package de.unidue.inf.is.ezdl.dlwrapper.toolkit;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import de.unidue.inf.is.ezdl.dlcore.utils.ClosingUtils;
import de.unidue.inf.is.ezdl.dlcore.utils.StringUtils;
import de.unidue.inf.is.ezdl.dlwrapper.toolkit.HttpClientCrawler.Method;



/**
 * Class to get easy access to the power that this toolkit offers. With this
 * class you can execute a single command like fetch, cleanHTML and parse or
 * give a wrapper configuration and let this class do the work for you.
 * <p>
 * <h2>Wrapper configuration</h2>
 * <p>
 * A wrapper configuration file is in XML format. There are some examples in the
 * directory <code>dlwrapper/src/main/resources/wrapperconfigs</code> that give
 * some idea what is possible and how.
 * <h3>Encoding hell</h3>
 * <p>
 * The encoding used by the web pages parsed with the ToolkitAPI varies wildly.
 * You can tell the Toolkit which encoding to use for communicating with a
 * remote DL by using the <code>encoding</code> parameter in the
 * <code>fetch</code> clause. E.g.
 * </p>
 * 
 * <pre>
 * &lt;fetch method="GET" url="$url" encoding="utf8" /&gt;
 * </pre>
 * <p>
 * Valid encoding names are those accepted by
 * {@link URLEncoder#encode(String, String)}.
 * </p>
 * <h3>Config items</h3>
 * <p>
 * <h4>usecookies</h4>
 * <p>
 * Initializes the use of cookies in situation where it is needed or generally
 * helps. There are servers that try to set a session cookie. If that fails,
 * they encode the session ID into the URL. That is often unwanted because these
 * session IDs end up in the details URLs and just use storage space while
 * offering no information at all. In these situations, <code>usecookies</code>
 * is helpful.
 * </p>
 * <p>
 * Parameters: none
 * </p>
 * <p>
 * Example:
 * 
 * <pre>
 * &lt;usecookies/&gt;
 * </pre>
 * 
 * </p>
 * <h4>fetch</h4>
 * <p>
 * Fetches a URL. <code>fetch</code> has a couple of attributes and inner nodes.
 * <p>
 * The attributes
 * <ul>
 * <li>minPage - the first page (starting with 1) that the fetch clause should
 * be executed at. Defaults to 1.</li>
 * <li>maxPage - the last page (starting with 1) that the fetch clause should be
 * executed at. Defaults to {@link Integer#MAX_VALUE}</li>
 * <li>method - the HTTP method to use. E.g. "GET" or "POST"</li>
 * <li>url - the base URL to retrieve. The URL can be expanded by parameters
 * using inner nodes.</li>
 * <li>encoding - the encoding to use. E.g. "UTF-8" or "ISO-8859-1".</li>
 * </ul>
 * <p>
 * The inner nodes
 * <ul>
 * <li>param - a parameter to set. Has the attributes "name" and "value".</li>
 * <li>form - a form parameter to set when using the POST method.
 * <code>form</code> works just like <code>param</code> but adds the data to the
 * body of the HTTP message.</li>
 * </ul>
 * ...
 * <p>
 * Several fetch clauses can be combined, e.g. restricting them to specific page
 * ranges. E.g. using one fetch clause for pages 1 until 1 and another one for
 * pages starting with 1 (but without a maxPages attribute) can be used to fool
 * the remote party into thinking that e.g. an actual search form has been
 * loaded. The first fetch clause would then fetch the form (maybe initializing
 * a session), the second would be used to post the form. This method is
 * especially useful if combined with <code>usecookies</code> to store session
 * data.
 * </p>
 * <h5>Examples</h5>
 * <p>
 * 
 * <pre>
 * &lt;fetch minPage="1" method="GET" url="$rooturl" encoding="utf8"/&gt;
 * </pre>
 * 
 * <pre>
 * </p>
 */
public class ToolkitAPI {

    /**
     * Default maximum number of errors that are allowed to occur before a
     * runtime exception is thrown. This is pretty large to keep compatibility
     * with clients.
     */
    private static final int DEFAULT_MAX_ERROR_COUNTER = Integer.MAX_VALUE;

    private static Logger logger = Logger.getLogger(ToolkitAPI.class);

    /**
     * Random variable for creating ids.
     */
    private static Random random = new Random();

    /**
     * Id counter.
     */
    private static int ids;

    private String configName;
    private URL baseurl;
    private Document configFile;
    private String outputEncoding = "UTF-8";

    /**
     * Logging directory.
     */
    private File logDir;

    /**
     * Logging directory w.r.t. the current configuration file name.
     */
    private File confLogDir;

    private int maxErrorCounter = DEFAULT_MAX_ERROR_COUNTER;
    private int errorCounter = 0;

    /**
     * Host name of the proxy to use.
     */
    private String proxyHost;
    /**
     * Port number of the proxy to use.
     */
    private int proxyPort;
    /**
     * The list of cookies that were sent in the session.
     */
    protected List<String> cookies;
    /**
     * Counts the pages already read. No page read is 0.
     */
    private int pageNumber = 1;


    /**
     * Constructor of this class.
     */
    public ToolkitAPI() {
        baseurl = null;
    }


    public ToolkitAPI(URL configurationURL) {
        baseurl = null;
        setConfigFile(configurationURL);
    }


    /**
     * Reads and executes a wrapperconfiguration.
     * 
     * @param configurationURL
     *            url of the wrapperconfiguration
     * @param props
     *            global property hash
     */
    public Object execute(URL configurationURL, Map<String, Object> props) {
        setConfigFile(configurationURL);
        return execute(props);
    }


    /**
     * Takes the already read configfile and executes a wrapperconfiguration.
     * 
     * @param configurationURL
     *            url of the wrapperconfiguration
     * @param props
     *            global property hash
     */
    public Object execute(Map<String, Object> props) {
        return execute(configFile, props);
    }


    /**
     * Executes completely a wrapperconfigution.
     * 
     * @param configuraiton
     *            to be executed wrapperconfiguration
     * @param props
     *            global property hash
     */
    public Object execute(Document configuration, Map<String, Object> props) {
        Object result = null;
        if (confLogDir == null && logDir != null && configName != null) {
            confLogDir = new File(logDir, configName);
            confLogDir.mkdirs();
        }
        String requestID = getID();

        Element config = Tools.getFirstChildElement(configuration.getDocumentElement());
        while (config != null) {
            try {
                result = execute(config, props, result, requestID);
            }
            catch (SAXException e) {
                result = null;
            }
            if (result == null) {
                break;
            }
            config = Tools.getNextSiblingElement(config);
        }

        if (result == null) {
            logError("Result is null");
        }

        if (confLogDir != null) {
            try {
                Tools.save(new File(confLogDir, requestID + ".result"), result);
            }
            catch (Exception e) {
                logError("execute()", e);
            }
        }
        pageNumber++;
        return result;
    }


    /**
     * Returns an id which can used for creating file names.
     * 
     * @return id
     */
    private synchronized String getID() {
        return System.currentTimeMillis() + "-" + random.nextInt() + "-" + ids++;
    }


    private Object execute(Element config, Map<String, Object> props, Object lastresult, String requestID)
                    throws SAXException {
        Date start = new Date();
        final String tagName = config.getTagName();
        logger.debug("Executing " + tagName);
        if (tagName.equals("document")) {
            Object o = document(config, props);
            info(tagName, o, start);
            return o;
        }
        if (tagName.equals("load")) {
            Object o = load(config, props, requestID);
            info(tagName, o, start);
            return o;
        }
        if (tagName.equals("usecookies")) {
            if (cookies == null) {
                cookies = new LinkedList<String>();
            }
            return "";
        }
        if (tagName.equals("fetch")) {
            Object o = fetch(lastresult, config, props, requestID);
            info(tagName, o, start);
            return o;
        }
        if (tagName.equals("noclean")) {
            Object o = cleanXML((InputStream) lastresult, config);
            info(tagName, o, start);
            return o;
        }
        if (tagName.equals("clean")) {
            Object o = cleanHTML((InputStream) lastresult, config);
            info(tagName, o, start);
            return o;
        }
        if (tagName.equals("cleanXML")) {
            Object o = Tools.xmlDoc(Tools.cleanInputStream((InputStream) lastresult));
            info(tagName, o, start);
            return o;
        }
        if (tagName.equals("cleanOriginal")) {
            Object o = cleanHTML((InputStream) lastresult, config);
            info(tagName, o, start);
            return o;
        }
        if (tagName.equals("structured")) {
            Object o = getStructure((Document) lastresult, props);
            info(tagName, o, start);
            return o;
        }
        if (tagName.equals("parse")) {
            Object o = parse((Document) lastresult, config, props);
            info(tagName, o, start);
            return o;
        }
        if (tagName.equals("save")) {
            save(config, (InputStream) lastresult);
            return null;
        }
        return null;
    }


    private void info(String tagName, Object o, Date start) {
        // System.out.println("ToolkitAPI.execute -> " + tagName + " took " +
        // ((new java.util.Date()).getTime() - start.getTime()) +
        // " milisecs. object size: " );
    }


    /**
     * Reads a XML Document node from the global property hash.
     * 
     * @param config
     *            to be executed document element of a wrapperconfiguration.
     * @param props
     *            global property hash.
     * @return XML Document node.
     */

    public Document document(Element config, Map<String, Object> props) {
        return (Document) props.get(config.getAttribute("property"));
    }


    /**
     * Opens a file to read an HTML webpage.
     * 
     * @param config
     *            to be executed load element of a wrapperconfiguration
     * @return an open InputStream to the webpage.
     */
    public InputStream load(Element config, Map<String, Object> props) {
        return load(config, props, null);
    }


    /**
     * Opens a file to read an HTML webpage.
     * 
     * @param config
     *            to be executed load element of a wrapperconfiguration
     * @param requestID
     *            request ID, used for saving the URL
     * @return an open InputStream to the webpage.
     */
    public InputStream load(Element config, Map<String, Object> props, String requestID) {
        String filename = config.getAttribute("filename");
        if (filename.startsWith("$")) {
            filename = filename.substring(1);
            filename = (String) props.get(filename);
        }
        InputStream result = null;
        logURL(baseurl, null, requestID);
        logger.debug("Loading \"" + filename + "\" ...");
        try {
            result = new FileInputStream(new File(filename));
        }
        catch (Exception e) {
            logError("Caught IOException in open!", e);
        }
        return result;
    }


    /**
     * Executes a fetch element of a wrapperconfiguration.
     * 
     * @param config
     *            to be executed fetch element of a wrapperconfiguration
     * @param props
     *            global property hash
     * @return an open InputStream to the wanted webpage
     */
    public Object fetch(Object lastResult, Element config, Map<String, Object> props) {
        return fetch(lastResult, config, props, null);
    }


    public Object fetch(Object lastResult, Element config, Map<String, Object> props, String requestID) {
        int minPage;
        int maxPage;
        try {
            final String minPageStr = config.getAttribute("minPage");
            minPage = Integer.parseInt(minPageStr);
        }
        catch (NumberFormatException e) {
            minPage = 0;
        }
        try {
            final String maxPageStr = config.getAttribute("maxPage");
            maxPage = Integer.parseInt(maxPageStr);
        }
        catch (NumberFormatException e) {
            maxPage = Integer.MAX_VALUE;
        }
        if ((minPage <= pageNumber) && (pageNumber <= maxPage)) {
            InputStream o = doFetch(config, props, requestID);
            return o;
        }
        else {
            if (lastResult == null) {
                // return something non-null
                return "";
            }
            else {
                return lastResult;
            }
        }
    }


    /**
     * Executes a fetch element of a wrapperconfiguration.
     * 
     * @param config
     *            to be executed fetch element of a wrapperconfiguration
     * @param props
     *            global property hash
     * @param requestID
     *            request ID, used for saving the URL
     * @return an open InputStream to the wanted webpage
     */
    public InputStream doFetch(Element config, Map<String, Object> props, String requestID) {
        String method = config.getAttribute("method");
        String url = config.getAttribute("url");

        if (url.startsWith("$")) {
            url = url.substring(1);
            Object value = props.get(url);
            url = (value instanceof String) ? (String) value : ((URL) value).toString();
        }

        url = url.replaceAll(" ", "%20");

        try {
            baseurl = new URL(url);
        }
        catch (java.net.MalformedURLException e) {
            logError("Caught MalformedURLException in config!", e);
            return null;
        }
        String encoding = config.getAttribute("encoding");
        setOutputEncoding(encoding);
        HttpClientCrawler crawler = getHttpClientCrawler(baseurl, encoding);
        Element param = Tools.getFirstChildElement(config);
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        List<NameValuePair> formFields = new ArrayList<NameValuePair>();

        param = collectParams(props, param, params, formFields);

        try {
            logger.debug("Fetching \"" + baseurl + "\" ...");
            logURL(baseurl, params, requestID);
            InputStream result = null;
            // pageNumber++;
            if (method.equalsIgnoreCase("get")) {
                result = crawler.fetch(Method.GET, params, null);
            }
            else if (method.equalsIgnoreCase("post")) {
                result = crawler.fetch(Method.POST, params, formFields);
            }
            baseurl = crawler.getURL();
            return result;
        }
        catch (Exception e) {
            logError("Caught IOException in fetch!", e);
        }
        return null;
    }


    private Element collectParams(Map<String, Object> props, Element param, List<NameValuePair> params,
                    List<NameValuePair> formFields) {
        while (param != null) {
            final String nodeName = param.getNodeName();
            final String paramName = param.getAttribute("name");
            if (!StringUtils.isEmpty(paramName)) {
                String value = param.getAttribute("value");
                if (value.startsWith("$")) {
                    value = value.substring(1);
                    final Object valueObj = props.get(value);
                    if (valueObj instanceof String) {
                        addParamToList(params, formFields, nodeName, paramName, (String) valueObj);
                    }
                    else if (valueObj instanceof List<?>) {
                        addListToList(params, formFields, nodeName, paramName, valueObj);
                    }
                }
                else {
                    addParamToList(params, formFields, nodeName, paramName, value);
                }
            }
            else {
                String value = param.getAttribute("value");
                if (value.startsWith("$")) {
                    value = value.substring(1);
                    final Object valueObj = props.get(value);
                    if (valueObj instanceof String) {
                        String[] parts = ((String) valueObj).split("&");
                        for (String part : parts) {
                            String[] subparts = part.split("=");
                            String subName = "";
                            String subValue = "";
                            if (subparts.length == 2) {
                                subValue = subparts[1];
                            }
                            if (subparts.length != 0) {
                                subName = subparts[0];
                                addParamToList(params, formFields, nodeName, subName, subValue);
                            }
                        }
                    }
                }
                else {
                    addParamToList(params, formFields, nodeName, paramName, value);
                }
            }

            param = Tools.getNextSiblingElement(param);
        }
        return param;
    }


    @SuppressWarnings("unchecked")
    private void addListToList(List<NameValuePair> params, List<NameValuePair> formFields, String nodeName,
                    String paramName, Object valueObj) {
        List<Object> valueList = (List<Object>) valueObj;
        for (Object listVal : valueList) {
            if (listVal instanceof String) {
                addParamToList(params, formFields, nodeName, paramName, (String) listVal);
            }
        }
    }


    private void addParamToList(List<NameValuePair> params, List<NameValuePair> formFields, final String nodeName,
                    final String paramName, String value) {
        NameValuePair p = new BasicNameValuePair(paramName, value);
        if ("param".equals(nodeName)) {
            params.add(p);
        }
        else if ("form".equals(nodeName)) {
            formFields.add(p);
        }
    }


    /**
     * Logs the URL and the URL parameters.
     * 
     * @param baseurl
     *            URL
     * @param params
     *            parameters
     * @param requestID
     *            request ID
     * @status finished 2004-03-28 (HN)
     */
    private void logURL(URL baseurl, List<NameValuePair> params, String requestID) {
        if (confLogDir != null && requestID != null) {
            PrintWriter pw = null;
            try {
                pw = new PrintWriter(new FileWriter(new File(confLogDir, requestID + ".url")));
                pw.println(baseurl);
                if (params != null) {
                    for (NameValuePair nvp : params) {
                        pw.println(nvp.getName() + ": " + nvp.getValue());
                    }
                }
                pw.close();
            }
            catch (IOException e) {
                logError(e.getMessage(), e);
            }
            finally {
                ClosingUtils.close(pw);
            }
        }
    }


    public Object getStructure(Document document, Map<String, Object> props) {
        String result = "";
        String input = Tools.nodeAsString(document.getDocumentElement());
        int shift = 0;
        int lasttag = 0;
        int counter = 0;
        int cmp = input.length();
        if (cmp > 10000) {
            logger.info("This page is very large. Structuring will take at least " + ((cmp / 10000)) + " minutes!!!");
        }
        // input=input.replaceFirst("^.*<body.*?>", "<body>");
        input = input.replaceAll("<!--.*?-->", ""); // remove all comments!
        String part1 = "";
        String part2 = "start!";

        while ((!input.equals("")) && (counter < 40) && (!part2.equals("")) && (shift >= 0)) {
            // counter++;
            lasttag = 0;
            int index1 = input.indexOf('<');
            int index2 = input.indexOf('>') + 1;
            part1 = "";
            part2 = "";
            part1 = input.substring(0, index2);

            if (part1.matches("</.*?>")) { // end part of a tag
                lasttag = 1;
                part2 = part1;
                index2 = input.indexOf('>') + 1;
                input = input.substring(index2, input.length());
                index1 = 0;
                index2 = input.indexOf('<');
                if (index2 != -1) {
                    part2 = part2 + input.substring(index1, index2);
                    input = input.substring(index2, input.length());
                }
            }
            else if (part1.matches("<.*?/>")) { // single tag
                part2 = part1;
                lasttag = 2;
                index2 = input.indexOf('>') + 1;
                input = input.substring(index2, input.length());
                index1 = 0;
                index2 = input.indexOf('<');
                if (index2 != -1) {
                    part2 = part2 + input.substring(index1, index2);
                    input = input.substring(index2, input.length());
                }
            }
            else if (part1.matches("<.*>")) { // first part of a tag
                lasttag = 3;
                part2 = part1;
                index2 = input.indexOf('>') + 1;
                input = input.substring(index2, input.length());
                index1 = 0;
                index2 = input.indexOf('<');
                if (index2 != -1) {
                    part2 = part2 + input.substring(index1, index2);
                    input = input.substring(index2, input.length());
                }
            }
            if (lasttag == 3) {
                if (!part2.equals("")) {
                    for (int i = 0; i < shift; i++) {
                        result = result + " ";
                    }
                    result = result + part2 + "\n";
                }
                shift++;
            }
            else if (lasttag == 1) {
                shift--;
                if (!part2.equals("")) {
                    for (int i = 0; i < shift; i++) {
                        result = result + " ";
                    }
                    result = result + part2 + "\n";
                }
            }
            else if (lasttag == 2) {
                if (!part2.equals("")) {
                    for (int i = 0; i < shift; i++) {
                        result = result + " ";
                    }
                    result = result + part2 + "\n";
                }
            }
            if (cmp >= (input.length() + 10000)) {
                cmp = input.length();
                logger.info("Still " + cmp + " bytes to go. Stay tuned! :-)");
            }
        }
        return result;
    }


    /**
     * Reads webpage from an open InputStream, cleans it and returns the
     * corresponding XML DOM document node.
     * 
     * @param in
     *            open InputStream to webpage
     * @param config
     *            clean element of a wrapperconfiguration
     * @return corresponding XML DOM document node
     */
    public Document cleanHTML(InputStream in, Element config) {

        if (in == null) {
            return null;
        }
        // Document doc = cleaner.cleanWithoutEntityCleaning(in);
        NekoCleaner cleaner2 = new NekoCleaner(config, outputEncoding);
        Document doc2 = cleaner2.clean(in);
        // System.out.println(Tools.xmlString(doc2));
        return doc2;
    }


    /**
     * Reads Inputstream hopefully in XML, cleans it and returns the
     * corresponding XML DOM document node.
     * 
     * @param in
     *            open InputStream to webpage
     * @param config
     *            clean element of a wrapperconfiguration
     * @return corresponding XML DOM document node
     */
    public Document cleanXML(InputStream in, Element config) {
        return cleanHTML(in, config);
    }


    /**
     * Executes a parser element of a wrapperconfiguration on a XML document
     * node.
     * 
     * @param doc
     *            XML DOM document node
     * @param config
     *            parser element of a wrapperconfiguraiton
     * @param props
     *            global property hash
     * @return the extracted information
     */
    public Object parse(Document doc, Element config, Map<String, Object> props) throws SAXException {
        Parser parser = new Parser(config);
        if (doc == null) {
            return null;
        }
        return parser.parse(doc, props, baseurl);
    }


    /**
     * Executes a save element of a wrapperconfiguration
     * 
     * @param config
     *            save element of a wrapperconfiguration
     * @param in
     *            an open InputStream to a webpage
     */
    public void save(Element config, InputStream in) {
        String filename = config.getAttribute("filename");
        logger.debug("Saving \"" + filename + "\" ...");
        Tools.saveInputStream(in, filename);
    }


    public Document getConfigFile() {
        return configFile;
    }


    public void setConfigFile(URL url) {
        configName = url.getPath();
        int h = configName.lastIndexOf("/");
        if (h != -1) {
            configName = configName.substring(h + 1);
        }
        h = configName.indexOf(".");
        if (h != -1) {
            configName = configName.substring(0, h);
        }
        confLogDir = null;
        configFile = Tools.readXMLFile(url);
    }


    public void setConfigFile(Document document) {
        configFile = document;
    }


    /**
     * Returns the logging directory.
     * 
     * @return logging directory or null, if no logging directory is given
     */
    public File getLogDir() {
        return logDir;
    }


    /**
     * Sets the logging directory
     * 
     * @param file
     *            logging directory or null, if no logging is wanted
     */
    public void setLogDir(File file) {
        logDir = file;
        confLogDir = null;
    }


    public String getOutputEncoding() {
        return outputEncoding;
    }


    public void setOutputEncoding(String output_encoding) {
        this.outputEncoding = output_encoding;
    }


    protected HttpClientCrawler getHttpClientCrawler(URL baseurl, String encoding) {
        HttpClientCrawler crawler;
        logger.debug("Proxy: " + proxyHost + ":" + proxyPort);
        if ((getProxyHost() != null) && (getProxyPort() > 0) && (getProxyPort() < 65535)) {
            logger.debug("Proxy crawler");
            crawler = new HttpClientCrawler(baseurl, encoding, getProxyHost(), getProxyPort());
        }
        else {
            logger.debug("Normal crawler");
            crawler = new HttpClientCrawler(baseurl, encoding);
        }
        crawler.setCookieJar(cookies);
        return crawler;
    }


    public void setProxy(String hostName, int portNumber) {
        this.proxyHost = hostName;
        this.proxyPort = portNumber;
    }


    protected String getProxyHost() {
        return proxyHost;
    }


    protected int getProxyPort() {
        return proxyPort;
    }


    /**
     * Resets the error counter.
     */
    public void resetErrorCounter() {
        errorCounter = 0;
    }


    /**
     * Returns the error counter that indicates how many parsing or other errors
     * were encountered since the last call to {@link #resetErrorCounter()}.
     * 
     * @return the error counter
     */
    public int getErrorCounter() {
        return errorCounter;
    }


    /**
     * Sets the maximum number of errors that are allowed to occur between two
     * calls to {@link #resetErrorCounter()}.
     * 
     * @param maxErrorCounter
     *            the maximum number of errors allowed
     */
    public void setMaxErrorCounter(int maxErrorCounter) {
        this.maxErrorCounter = maxErrorCounter;
    }


    /**
     * Increases the error counter.
     */
    private void logError(String message) {
        logger.error(message);
        increaseErrorCounter();
    }


    private void logError(String message, Throwable e) {
        logger.error(message, e);
        increaseErrorCounter();
    }


    private void increaseErrorCounter() {
        errorCounter++;
        if (errorCounter > maxErrorCounter) {
            throw new ToolkitFaultException();
        }
    }

}
