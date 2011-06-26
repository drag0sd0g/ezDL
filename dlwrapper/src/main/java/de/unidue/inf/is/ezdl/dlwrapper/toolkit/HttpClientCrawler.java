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

/*
 * Created on Feb 22, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package de.unidue.inf.is.ezdl.dlwrapper.toolkit;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.log4j.Logger;

import de.unidue.inf.is.ezdl.dlcore.utils.IOUtils;
import de.unidue.inf.is.ezdl.dlcore.utils.StringUtils;



public class HttpClientCrawler {

    private List<String> cookies;


    public enum Method {
        GET, POST;
    }


    private Logger logger = Logger.getLogger(HttpClientCrawler.class);

    private HttpClient client;
    private InputStream in;

    /**
     * Internally handle method, that needs to be closed
     */
    private HttpRequestBase method;
    /**
     * URL of a website or a CGI script
     */
    private URL url;
    /**
     * The encoding to use.
     */
    private String encoding;


    /**
     * Creates a new client crawler for a given URL and an encoding that uses a
     * proxy.
     * 
     * @param _url
     *            the URL to crawl
     * @param _encoding
     *            the encoding to expect
     * @param proxyHost
     *            the host name of the proxy
     * @param proxyPort
     *            the port number of the proxy
     */
    public HttpClientCrawler(URL _url, String _encoding, String proxyHost, int proxyPort) {
        this(_url, _encoding);
        client = new DefaultHttpClient();
        if (proxyHost == null) {
            throw new IllegalArgumentException("Proxy must not be null");
        }
        logger.debug("Using proxy " + proxyHost + ":" + proxyPort);
        HttpHost proxy = new HttpHost(proxyHost, proxyPort);
        client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
    }


    /**
     * Creates a new client crawler that uses a direct connection to the given
     * site.
     * 
     * @param _url
     *            the URL to crawl
     * @param _encoding
     *            the encoding to expect
     */
    public HttpClientCrawler(URL _url, String _encoding) {
        this.url = _url;
        this.encoding = _encoding;
        client = new DefaultHttpClient();
    }


    /**
     * Creates a new client crawler that uses a direct connection to the given
     * site.
     * 
     * @param _url
     *            the URL to crawl
     * @param _encoding
     *            the encoding to expect
     */
    public HttpClientCrawler(String _url, String _encoding) throws MalformedURLException {
        this(new URL(_url), _encoding);
    }


    public void setCookieJar(List<String> cookies) {
        this.cookies = cookies;
    }


    /**
     * Returns the URL of a website or a CGI script.
     * 
     * @return URL of a website or a CGI script.
     */
    public URL getURL() {
        return url;
    }


    /**
     * @return the encoding used by crawler
     */
    public String getEncoding() {
        return encoding;
    }


    /**
     * Opens an InputStream to a executed CGI script and returns the open
     * InputStream.
     * 
     * @param method
     *            method to send parameters to CGI script.
     * @param params
     *            parameters to be send to CGI script.
     * @param formFields
     *            form fields to send
     * @return open InputStream to CGI script.
     * @throws IOException
     *             if an I/O error occurs while open the connection.
     */
    protected InputStream fetch(Method method, List<NameValuePair> params, List<NameValuePair> formFields)
                    throws IOException {
        switch (method) {
            case GET:
                return get(params);
            case POST:
                return post(params, formFields);
            default:
                return null;
        }
    }


    /**
     * Open connection to server and sends via GET method the parameters.
     * 
     * @param params
     *            to send request parameters
     * @return opened inputstream with content
     * @throws IOException
     *             if an I/O error occurs while open the connection or send the
     *             parameters
     */
    protected InputStream get(List<NameValuePair> params) {
        InputStream in = null;
        String urlString = null;
        if (params != null) {
            final String encodedParams = encodeParams(params);
            urlString = getURL().toString();
            if (!encodedParams.isEmpty()) {
                final String connector = (urlString.contains("?") ? "&" : "?");
                urlString += connector + encodedParams;
            }
        }
        else {
            urlString = getURL().toString();
        }

        HttpGet get = new HttpGet(urlString);
        in = fetchData(urlString, get);

        return in;
    }


    private InputStream fetchData(String urlWithParams, HttpRequestBase _method) {
        int statusCode = -1;
        int maxAttempts = 1;

        method = _method;
        if (cookies != null) {
            for (String cookie : cookies) {
                logger.info("Sending cookie to remote party: " + cookie);
                method.addHeader(new BasicHeader("Cookie", cookie));
            }
        }

        HttpResponse response = null;
        for (int attempt = 0; statusCode == -1 && attempt < maxAttempts; attempt++) {
            try {
                // execute the method.
                logMethod(_method);
                response = client.execute(method);
                statusCode = response.getStatusLine().getStatusCode();
            }
            catch (IOException e) {
                logger.error("Error in executing post Method: " + e.getLocalizedMessage(), e);
            }

            logger.info("attempt=" + attempt + "   statusCode of get Request: " + statusCode);
        }
        // Check that we didn't run out of retries.
        if (statusCode == -1) {
            logger.error("Error: POST requests failed. url: " + urlWithParams);
        }

        // Read the response body.
        try {
            if (response != null) {
                in = response.getEntity().getContent();
                if (cookies != null) {
                    Header[] cookieHdrs = response.getHeaders("Set-Cookie");
                    for (Header cookieHdr : cookieHdrs) {
                        logger.info("Remote party sent cookie: " + cookieHdr.getName() + "  --  "
                                        + cookieHdr.getValue());
                        cookies.add(cookieHdr.getValue());
                    }
                }
            }
        }
        catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

        return in;
    }


    private void logMethod(HttpRequestBase _method) throws IOException {
        String entity = "";
        if (_method instanceof HttpPost) {
            InputStream s = ((HttpPost) method).getEntity().getContent();
            entity = IOUtils.readInputStreamAsString(s);
        }
        logger.debug(method.getMethod() + ": " + method.getURI() + "  --  " + entity);
    }


    protected InputStream post(List<NameValuePair> params, List<NameValuePair> formFields) {
        String urlWithParams = getURL().toString();

        final String encodeParams = encodeParams(params);
        if (!StringUtils.isEmpty(encodeParams)) {
            urlWithParams += "?" + encodeParams;
        }

        String result;
        try {
            result = fetchPost(new URL(urlWithParams), formFields);
            return new ByteArrayInputStream(result.getBytes());
        }
        catch (UnknownHostException e) {
            logger.error(e);
        }
        catch (IOException e) {
            logger.error(e);
        }
        catch (HttpException e) {
            logger.error(e);
        }

        return null;
    }


    public InputStream getInputStream() {
        return in;
    }


    public void close() {
        // Release the connection.
        method.abort();
    }


    public String fetchPost(URL url, List<NameValuePair> formFields) throws UnknownHostException, IOException,
                    HttpException {
        InputStream in = null;
        String urlString = url.toString();

        HttpPost post = new HttpPost(urlString);

        if (formFields != null) {
            final UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(formFields, HTTP.UTF_8);
            post.setEntity(urlEncodedFormEntity);
        }
        in = fetchData(urlString, post);
        if (in != null) {
            final String response = IOUtils.readBufferAsString(new BufferedReader(new InputStreamReader(in)));
            return response;
        }
        return null;
    }


    /**
     * Encodes all parameters with the URLEncoder. The result is urlencoded and
     * has the the form key1=value&key2=value2...
     * 
     * @param params
     *            parameters to encode
     * @return encoded parameters which might be an empty String ("") but never
     *         null
     */
    protected String encodeParams(final List<NameValuePair> p) {
        boolean firstPair = true;
        StringBuilder content = new StringBuilder(200);
        for (NameValuePair nvp : p) {

            String key = nvp.getName();
            String value = nvp.getValue();

            if (value != null) {
                if (!firstPair) {
                    content.append("&");
                }
                try {
                    content.append(URLEncoder.encode(key, encoding));
                    content.append("=").append(URLEncoder.encode(value, encoding));
                    firstPair = false;
                }
                catch (IOException ex) {
                    logger.error("This machine do not know " + encoding
                                    + " or ISO-8859-1 or null pointer exception, nit it is OK.", ex);
                }
            }
        }
        return content.toString();
    }

}
