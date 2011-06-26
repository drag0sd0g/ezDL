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

package de.unidue.inf.is.ezdl.dlservices.library.manager.referencesystems.mendeley;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URLDecoder;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthMessage;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.log4j.Logger;

import de.unidue.inf.is.ezdl.dlcore.utils.ClosingUtils;
import de.unidue.inf.is.ezdl.dlservices.library.manager.referencesystems.ReferenceSystemException;
import de.unidue.inf.is.ezdl.dlservices.library.manager.referencesystems.mendeley.json.JSONUtils;



/** Helper class for Mendeley. Implements OAUTH */
final class MendeleyUtils {

    protected final Logger log = Logger.getLogger(MendeleyUtils.class.getName());


    /** request requestToken from mendeley */
    String getRequestToken(OAuthAccessor accessor, String request_token_url, String callbackUrl) throws Exception {

        Map<String, String> params = new HashMap<String, String>();
        params.put("oauth_callback", callbackUrl);

        String response = sendOAUTHGetRequest(accessor, request_token_url, params);

        Map<String, String> responseParams = parseResponseParams(response);
        accessor.requestToken = responseParams.get(OAuth.OAUTH_TOKEN);
        accessor.tokenSecret = responseParams.get(OAuth.OAUTH_TOKEN_SECRET);

        return response;
    }


    /** returns the response with the access token */
    String getAccessToken(OAuthAccessor accessor, String verifier, String access_token_url) throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        params.put("oauth_verifier", verifier);
        params.put(OAuth.OAUTH_TOKEN, accessor.requestToken);

        String response = sendOAUTHGetRequest(accessor, access_token_url, params);

        Map<String, String> responseParams = parseResponseParams(response);
        accessor.tokenSecret = responseParams.get(OAuth.OAUTH_TOKEN_SECRET);
        accessor.accessToken = responseParams.get(OAuth.OAUTH_TOKEN);

        return response;
    }


    /** Send a GET request. OAUTH signation is made automatically */
    String sendOAUTHGetRequest(OAuthAccessor accessor, String url, Map<String, String> parameters) throws Exception {

        OAuthMessage oauthmessage = accessor.newRequestMessage("GET", url, parameters.entrySet());
        String urloauth = OAuth.addParameters(oauthmessage.URL, oauthmessage.getParameters());
        return sendGetRequest(urloauth);
    }


    /** Send a POST request. OAUTH signation is made automatically */
    String sendOAUTHPostRequest(OAuthAccessor accessor, String url, Map<String, String> parameters) throws Exception {

        OAuthMessage oauthmessage = accessor.newRequestMessage("POST", url, parameters.entrySet());

        return sendPostRequest(url, OAuth.formEncode(oauthmessage.getParameters()));
    }


    /** Send a DELETE request. OAUTH signation is made automatically */
    String sendOAUTHDeleteRequest(OAuthAccessor accessor, String url, Map<String, String> parameters) throws Exception {
        OAuthMessage oauthmessage = accessor.newRequestMessage("DELETE", url, parameters.entrySet());
        String urloauth = OAuth.addParameters(oauthmessage.URL, oauthmessage.getParameters());
        return sendDeleteRequest(urloauth);
    }


    /** send GET REQUEST */
    private String sendGetRequest(String url) throws Exception {
        String sresponse;
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpget = new HttpGet(url);
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
                throw new ReferenceSystemException(httpcode, "Mendeley Error", JSONUtils.parseError(sresponse));
            }
        }
        catch (UnknownHostException e) {
            throw new ReferenceSystemException("", "Unknow Host Exception", e.toString());
        }
        return sresponse;
    }


    /** send POST REQUEST */
    private String sendPostRequest(String url, String bodytext) throws Exception {
        String sresponse;
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);

            // very important. otherwise there comes a request error
            httppost.getParams().setBooleanParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, false);

            StringEntity body = new StringEntity(bodytext);
            body.setContentType("application/x-www-form-urlencoded");
            httppost.setEntity(body);

            HttpResponse response = httpclient.execute(httppost);

            // Check statuscode
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
                throw new ReferenceSystemException(httpcode, "Mendeley Error", JSONUtils.parseError(sresponse));
            }
        }
        catch (UnknownHostException e) {
            throw new ReferenceSystemException("", "Unknow Host Exception", e.toString());
        }
        return sresponse;

    }


    /** send DELETE REQUEST */
    private String sendDeleteRequest(String url) throws Exception {
        String sresponse;

        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpDelete httpdelete = new HttpDelete(url);

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
                throw new ReferenceSystemException(httpcode, "Mendeley Error", JSONUtils.parseError(sresponse));
            }
        }
        catch (UnknownHostException e) {
            throw new ReferenceSystemException("", "Unknow Host Exception", e.toString());
        }
        return sresponse;
    }


    /** reads inputstream and returns it as a string */
    private String readResponseStream(InputStream inputStream) throws Exception {
        BufferedReader reader = null;

        try {
            StringBuilder sb = new StringBuilder();
            reader = new BufferedReader(new InputStreamReader(inputStream));

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
        finally {
            ClosingUtils.close(reader);
        }
    }


    /** parse parameters of response bodys */
    private Map<String, String> parseResponseParams(String body) throws Exception {
        Map<String, String> results = new HashMap<String, String>();
        for (String keyValuePair : body.split("&")) {
            String[] kvp = keyValuePair.split("=");

            if (kvp.length > 1) {
                results.put(kvp[0], URLDecoder.decode(kvp[1], "UTF-8"));
            }
            else {
                throw new ReferenceSystemException("Mendeley Error: ", body);
            }
        }
        return results;
    }
}
