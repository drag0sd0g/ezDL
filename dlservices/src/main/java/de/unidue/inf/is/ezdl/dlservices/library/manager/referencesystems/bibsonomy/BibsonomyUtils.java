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

package de.unidue.inf.is.ezdl.dlservices.library.manager.referencesystems.bibsonomy;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.UnknownHostException;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;

import de.unidue.inf.is.ezdl.dlservices.library.manager.referencesystems.ReferenceSystemException;
import de.unidue.inf.is.ezdl.dlservices.library.manager.referencesystems.bibsonomy.xml.XMLUtils;
import de.unidue.inf.is.ezdl.dlservices.library.manager.referencesystems.mendeley.json.JSONUtils;



/** Helper class for bibsonomy */
public final class BibsonomyUtils {

    protected final Logger log = Logger.getLogger(BibsonomyUtils.class.getName());

    private String authHeader;


    public BibsonomyUtils(String username, String apiKey) {
        authHeader = "Basic " + new String(Base64.encodeBase64((username + ":" + apiKey).getBytes()));
    }


    /** send a GET Request and return the response as string */
    String sendGetRequest(String url) throws Exception {
        String sresponse;
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet(url);

        // set authentification header
        httpget.addHeader("Authorization", authHeader);

        HttpResponse response = httpclient.execute(httpget);

        // check status code
        if (response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
            HttpEntity entity = response.getEntity();
            sresponse = readResponseStream(entity.getContent());
            httpclient.getConnectionManager().shutdown();
        }
        else {
            HttpEntity entity = response.getEntity();
            sresponse = readResponseStream(entity.getContent());
            String httpcode = Integer.toString(response.getStatusLine().getStatusCode());
            httpclient.getConnectionManager().shutdown();
            throw new ReferenceSystemException(httpcode, "Bibsonomy Error", XMLUtils.parseError(sresponse));
        }
        return sresponse;
    }


    /** send a POST request and return the response as string */
    String sendPostRequest(String url, String bodytext) throws Exception {
        String sresponse;

        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(url);

        // add authorization header
        httppost.addHeader("Authorization", authHeader);

        StringEntity body = new StringEntity(bodytext);
        httppost.setEntity(body);

        HttpResponse response = httpclient.execute(httppost);

        // check statuscode
        if (response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK
                        || response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_CREATED) {
            HttpEntity entity = response.getEntity();
            sresponse = readResponseStream(entity.getContent());
            httpclient.getConnectionManager().shutdown();
        }
        else {
            HttpEntity entity = response.getEntity();
            sresponse = readResponseStream(entity.getContent());
            String httpcode = Integer.toString(response.getStatusLine().getStatusCode());
            httpclient.getConnectionManager().shutdown();
            throw new ReferenceSystemException(httpcode, "Bibsonomy Error", XMLUtils.parseError(sresponse));
        }
        return sresponse;

    }


    /** send a POST request and return the response as string */
    String sendPutRequest(String url, String bodytext) throws Exception {
        String sresponse;

        HttpClient httpclient = new DefaultHttpClient();
        HttpPut httpput = new HttpPut(url);

        // add authorization header
        httpput.addHeader("Authorization", authHeader);

        StringEntity body = new StringEntity(bodytext);
        httpput.setEntity(body);

        HttpResponse response = httpclient.execute(httpput);

        // check statuscode
        if (response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK
                        || response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_CREATED) {
            HttpEntity entity = response.getEntity();
            sresponse = readResponseStream(entity.getContent());
            httpclient.getConnectionManager().shutdown();
        }
        else {
            HttpEntity entity = response.getEntity();
            sresponse = readResponseStream(entity.getContent());
            String httpcode = Integer.toString(response.getStatusLine().getStatusCode());
            httpclient.getConnectionManager().shutdown();
            throw new ReferenceSystemException(httpcode, "Bibsonomy Error", XMLUtils.parseError(sresponse));
        }
        return sresponse;

    }


    /** send DELETE REQUEST */
    String sendDeleteRequest(String url) throws Exception {
        String sresponse;

        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpDelete httpdelete = new HttpDelete(url);

            // add authorization header
            httpdelete.addHeader("Authorization", authHeader);

            HttpResponse response = httpclient.execute(httpdelete);

            // Check status code
            if (response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK
                            || response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_NO_CONTENT) {
                if (response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_NO_CONTENT) {
                    sresponse = "";
                }
                else {
                    HttpEntity entity = response.getEntity();

                    sresponse = readResponseStream(entity.getContent());
                }
                httpclient.getConnectionManager().shutdown();
            }
            else {
                HttpEntity entity = response.getEntity();
                sresponse = readResponseStream(entity.getContent());
                String httpcode = Integer.toString(response.getStatusLine().getStatusCode());
                httpclient.getConnectionManager().shutdown();
                throw new ReferenceSystemException(httpcode, "Bibsonomy Error", JSONUtils.parseError(sresponse));
            }
        }
        catch (UnknownHostException e) {
            throw new ReferenceSystemException("", "Unknow Host Exception", e.toString());
        }
        return sresponse;
    }


    /** reads the inputstream and returns a string object */
    private String readResponseStream(InputStream inputStream) throws Exception {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        String line = reader.readLine();
        while (line != null) {
            sb.append(line);
            sb.append("\n");
            line = reader.readLine();
        }
        reader.close();
        inputStream.close();
        return sb.toString().trim();
    }
}
