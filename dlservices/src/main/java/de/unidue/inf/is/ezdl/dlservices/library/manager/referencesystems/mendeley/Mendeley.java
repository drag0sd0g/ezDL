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

import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.Document;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.library.Group;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.library.GroupList;
import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;
import de.unidue.inf.is.ezdl.dlservices.library.manager.referencesystems.OnlineReferenceSystem;
import de.unidue.inf.is.ezdl.dlservices.library.manager.referencesystems.ReferenceSystemException;
import de.unidue.inf.is.ezdl.dlservices.library.manager.referencesystems.mendeley.json.JSONGroup;
import de.unidue.inf.is.ezdl.dlservices.library.manager.referencesystems.mendeley.json.JSONGroupDetail;
import de.unidue.inf.is.ezdl.dlservices.library.manager.referencesystems.mendeley.json.JSONLibrary;
import de.unidue.inf.is.ezdl.dlservices.library.manager.referencesystems.mendeley.json.JSONUtils;



/** Implements the connection to mendeley */
public final class Mendeley extends OnlineReferenceSystem {

    private String request_token_url;
    private String access_token_url;
    private String authorize_website_url;
    private Properties props;
    private String consumerkey;
    private String consumersecret;
    private MendeleyUtils mendeleyUtils;
    private JSONUtils jsonUtils;
    private OAuthAccessor accessor;


    public Mendeley() {
    }


    /** returns a short description about the Wrapper */
    public static String getReferenceSystemName() {
        return "Mendeley Reference System";
    }


    /**
     * Returns the required authentication parameters. No one needed in mendeley
     */
    @Override
    public Map<String, String> getRequiredAuthParameters() {
        Map<String, String> params = new HashMap<String, String>();
        return params;
    }


    /** Returns the other required parameters. */
    @Override
    public Map<String, String> getOtherRequiredParameters() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("requestToken", "");
        params.put("tokenSecret", "");
        params.put("accessToken", "");
        return params;
    }


    /*
     * Initializes the Wrapper
     * @param requiredParameters contains the required authentication parameters
     * which the wrapper need. the required parameters can be obtained by the
     * getRequriedAuthInf() method
     * @param props Property file
     * @param otherParameters Contains parameters which are needed bei the
     * referenceSystem like access_token in mendley Are set automatically
     */
    @Override
    public void initialize(Map<String, String> requiredAuthParameters, Map<String, String> otherParameters,
                    Properties props) throws Exception {
        this.props = props;
        this.request_token_url = getProperty("request_token_url");
        this.access_token_url = getProperty("access_token_url");
        this.authorize_website_url = getProperty("authorize_website_url");
        this.consumerkey = getProperty("consumerkey");
        this.consumersecret = getProperty("consumersecret");

        mendeleyUtils = new MendeleyUtils();
        jsonUtils = new JSONUtils();
        accessor = new OAuthAccessor(new OAuthConsumer("oob", consumerkey, consumersecret, null));

        // Check if we have already the request token, tokenSecret and
        // accessToken.
        if (otherParameters.get("requestToken") != null && otherParameters.get("requestToken").length() > 0
                        && otherParameters.get("tokenSecret") != null
                        && otherParameters.get("tokenSecret").length() > 0) {
            accessor.requestToken = otherParameters.get("requestToken");
            accessor.tokenSecret = otherParameters.get("tokenSecret");

            // has already accessToken. Nothing has to be done
            if (otherParameters.get("accessToken") != null && otherParameters.get("accessToken").length() > 0) {
                accessor.accessToken = otherParameters.get("accessToken");
            }
            else if (otherParameters.get("$verifier") != null) {
                // has verifier.
                // request accessToken
                mendeleyUtils.getAccessToken(accessor, otherParameters.get("$verifier"), access_token_url);
                otherParameters.put("tokenSecret", accessor.tokenSecret);
                otherParameters.put("accessToken", accessor.accessToken);
            }
            else {
                // send Auth URL again
                throw new ReferenceSystemException(ReferenceSystemException.MENDELEY_VERIFIER_REQUIRED, getAuthURL());
            }
        }
        else {
            mendeleyUtils.getRequestToken(accessor, request_token_url, "oob");
            otherParameters.put("requestToken", accessor.requestToken);
            otherParameters.put("tokenSecret", accessor.tokenSecret);

            // Authentication url have to be shown where User types in the
            // verifier
            throw new ReferenceSystemException(ReferenceSystemException.MENDELEY_VERIFIER_REQUIRED, getAuthURL());
        }
    }


    /** Returns all references stored in Mendeley */
    @Override
    public List<Document> getReferences() throws Exception {
        JSONLibrary jsonLibrary = null;
        ArrayList<Document> references = new ArrayList<Document>();
        int page = 0;

        // references in library
        do {
            jsonLibrary = getArticleDocumentIds(page);

            // Get details for every document_id
            for (int i = 0; i < jsonLibrary.getDocument_ids().size(); i++) {
                references.add(getDocumentDetails(jsonLibrary.getDocument_ids().get(i)));
            }
            page++;
        }
        while (page < Integer.parseInt(jsonLibrary.getTotal_pages()));

        // references saved in groups
        List<Group> groups = getGroups();
        for (Group g : groups) {
            JSONGroupDetail jsonDetailGroup = null;
            page = 0;

            do {
                jsonDetailGroup = getGroupDetails(page, g.getReferenceSystemId());
                // Get details for every document_id
                for (int i = 0; i < jsonDetailGroup.getDocument_ids().size(); i++) {
                    Document d = getDocumentDetails(jsonDetailGroup.getDocument_ids().get(i));
                    GroupList gl = (GroupList) d.getFieldValue(Field.GROUPS);

                    if (gl == null) {
                        gl = new GroupList();
                    }

                    gl.add(g);
                    d.setFieldValue(Field.GROUPS, gl);
                    references.add(d);
                }
                page++;
            }
            while (page < Integer.parseInt(jsonDetailGroup.getTotal_pages()));

        }

        return references;
    }


    /** Returns list of article document ids */
    private JSONLibrary getArticleDocumentIds(int page) throws Exception {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("page", Integer.toString(page));
        String response = mendeleyUtils.sendOAUTHGetRequest(accessor, getProperty("library_url"), parameters);
        return jsonUtils.parseLibraryResponse(response);
    }


    private JSONGroupDetail getGroupDetails(int page, String groupid) throws Exception {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("page", Integer.toString(page));
        String url = getProperty("get_groups_url") + groupid + "/";
        String response = mendeleyUtils.sendOAUTHGetRequest(accessor, url, parameters);
        return jsonUtils.parseGroupDetailResponse(response);
    }


    /**
     * Returns citation info for a specific paper, such as authors, user tags,
     * publication outlet, year, abstract, PubMed ID if available, etc.
     */
    private Document getDocumentDetails(String docid) throws Exception {
        String url = getProperty("document_detail_url") + docid + "/";
        String response = mendeleyUtils.sendOAUTHGetRequest(accessor, url, new HashMap<String, String>());
        Document document = jsonUtils.parseDocumentDetailsResponse(docid, response);

        // Important
        document.setFieldValue(Field.REFERENCESYSTEM, Mendeley.getReferenceSystemName());

        return document;
    }


    /** Adds a new document in the user's library. */
    @Override
    public void addReference(Document document) throws Exception {

        GroupList groupList = (GroupList) document.getFieldValue(Field.GROUPS);
        String groupid = "";

        // Reference can only be in one mendeley group.
        if (groupList != null) {
            for (Group g : groupList) {
                if (g.onlineGroup()) {
                    groupid = g.getReferenceSystemId();
                    break;
                }
            }
        }
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("document", jsonUtils.convertReferenceToJSON(document, groupid));
        String response = mendeleyUtils.sendOAUTHPostRequest(accessor, getProperty("create_document_url"), parameters);

        // set the intern Mendeley ID to the document
        document.setFieldValue(Field.REFERENCESYSTEMID, jsonUtils.parseCreateDocumentResponse(response));
        document.setFieldValue(Field.REFERENCESYSTEM, getReferenceSystemName());
    }


    /** Removes a reference from the user's library. */
    @Override
    public void removeReference(Document document) throws Exception {
        if (document.getFieldValue(Field.REFERENCESYSTEMID) != null) {
            String url = getProperty("remove_document_url") + document.getFieldValue(Field.REFERENCESYSTEMID) + "/";
            try {
                mendeleyUtils.sendOAUTHDeleteRequest(accessor, url, new HashMap<String, String>());
            }
            catch (ReferenceSystemException re) {
                // Not found HTTP code comes. Document already deleted
                if (!re.getHttpCode().equals(Integer.toString(HttpURLConnection.HTTP_NOT_FOUND))) {
                    throw re;
                }
            }
        }
    }


    /** Update a reference from the reference system */
    @Override
    public void updateReference(Document document) throws Exception {
        // in mendeley ther is no update function
        // reference have to be delete and added new
        removeReference(document);
        addReference(document);
    }


    /** returns all groups which are stored in the reference system */
    @Override
    public List<Group> getGroups() throws Exception {
        String url = getProperty("get_groups_url");
        String response = mendeleyUtils.sendOAUTHGetRequest(accessor, url, new HashMap<String, String>());
        List<JSONGroup> jsonGroups = jsonUtils.parseGroupResponse(response);

        List<Group> groups = new ArrayList<Group>();

        for (JSONGroup g : jsonGroups) {
            Group gr = new Group();

            gr.setName(g.getName());
            gr.setReferenceSystem(getReferenceSystemName());
            gr.setReferenceSystemId(g.getId());
            gr.setType(g.getType());
            gr.setId(g.getId());

            groups.add(gr);
        }
        return groups;
    }


    /** Adds a group to the online referencesystem */
    @Override
    public void addGroup(Group group) throws Exception {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("group", jsonUtils.convertGroupToJSON(group));
        String response = mendeleyUtils.sendOAUTHPostRequest(accessor, getProperty("create_group_url"), parameters);
        group.setReferenceSystemId(jsonUtils.parseCreateGroupResponse(response));
        group.setReferenceSystem(getReferenceSystemName());
    }


    /** Removes a group from the online reference system */
    @Override
    public void removeGroup(Group group) throws Exception {
        if (group.onlineGroup()) {
            String url = getProperty("delete_group_url") + group.getReferenceSystemId() + "/";
            try {
                mendeleyUtils.sendOAUTHDeleteRequest(accessor, url, new HashMap<String, String>());
            }
            catch (ReferenceSystemException re) {
                // Forbidden HTTP code comes. Use is not the owner of the group.
                // So he can't delete
                // Try to leave group
                if (re.getHttpCode().equals(Integer.toString(HttpURLConnection.HTTP_FORBIDDEN))) {
                    url = getProperty("delete_group_url") + group.getReferenceSystemId() + "/leave/";
                    mendeleyUtils.sendOAUTHDeleteRequest(accessor, url, new HashMap<String, String>());
                }
                else {
                    throw re;
                }
            }
        }
    }


    /** Request the authenication URL (OAUTH) */
    private String getAuthURL() throws Exception {
        // generate authenticatin URL
        String authurl = authorize_website_url + "?" + OAuth.OAUTH_CONSUMER_KEY + "="
                        + URLEncoder.encode(accessor.consumer.consumerKey, "UTF-8");
        authurl = authurl + "&" + OAuth.OAUTH_TOKEN + "=" + URLEncoder.encode(accessor.requestToken, "UTF-8");
        return authurl;
    }


    /** read property */
    private String getProperty(String prop) {
        return props.getProperty("Mendeley." + prop, "");
    }

}
