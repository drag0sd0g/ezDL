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

package de.unidue.inf.is.ezdl.dlservices.library.manager.referencesystems.connotea;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.log4j.Logger;

import de.unidue.inf.is.ezdl.dlservices.library.manager.referencesystems.ReferenceSystemException;
import de.unidue.inf.is.ezdl.dlservices.library.manager.referencesystems.connotea.rdf.RDFUtils;



/** Helper class for connotea */
public final class ConnoteaUtils {

    protected final Logger log = Logger.getLogger(ConnoteaUtils.class.getName());

    private String username;
    private String password;
    private String authHeader;


    public ConnoteaUtils(String username, String password) {
        this.username = username;
        this.password = password;
        authHeader = "Basic " + new String(Base64.encodeBase64((this.username + ":" + this.password).getBytes()));
    }


    /** send a GET Request and return the response as string */
    String sendGetRequest(String url) throws Exception {
        String sresponse;
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet(url);

        // set authentication header
        httpget.addHeader("Authorization", authHeader);

        HttpResponse response = httpclient.execute(httpget);

        // Check status code
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
            throw new ReferenceSystemException(httpcode, "Connotea Error", RDFUtils.parseError(sresponse));
        }
        return sresponse;
    }


    /** send a POST Request and return the response as string */
    String sendPostRequest(String url, String bodytext) throws Exception {
        String sresponse;

        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(url);

        // set authentication header
        httppost.addHeader("Authorization", authHeader);

        // very important. otherwise there comes a invalid request error
        httppost.getParams().setBooleanParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, false);

        StringEntity body = new StringEntity(bodytext);
        body.setContentType("application/x-www-form-urlencoded");

        httppost.setEntity(body);

        HttpResponse response = httpclient.execute(httppost);

        // check status code.
        if (response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK
                        || response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_CREATED
                        || response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_NOT_FOUND) {
            // Not found too. no exception should be thrown.
            HttpEntity entity = response.getEntity();

            sresponse = readResponseStream(entity.getContent());
            httpclient.getConnectionManager().shutdown();
        }
        else {
            HttpEntity entity = response.getEntity();
            sresponse = readResponseStream(entity.getContent());
            String httpcode = Integer.toString(response.getStatusLine().getStatusCode());
            httpclient.getConnectionManager().shutdown();
            throw new ReferenceSystemException(httpcode, "Connotea Error", RDFUtils.parseError(sresponse));
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
