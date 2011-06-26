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

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import de.unidue.inf.is.ezdl.dlcore.data.dldata.Document;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.library.Group;
import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;
import de.unidue.inf.is.ezdl.dlservices.library.manager.referencesystems.OnlineReferenceSystem;
import de.unidue.inf.is.ezdl.dlservices.library.manager.referencesystems.ReferenceSystemException;
import de.unidue.inf.is.ezdl.dlservices.library.manager.referencesystems.bibsonomy.xml.XMLUtils;



/** Implements the connection to bibsonomy */
public final class Bibsonomy extends OnlineReferenceSystem {

    private String username;
    private String apiKey;
    private BibsonomyUtils bibsonomyUtils;
    private XMLUtils xmlUtils;
    private Properties props;


    /** returns a short description about the Wrapper */
    public static String getReferenceSystemName() {
        return "Bibsonomy Reference System";
    }


    /** Returns the required authentication parameters */
    @Override
    public Map<String, String> getRequiredAuthParameters() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("Username", "");
        params.put("API Key", "");
        return params;
    }


    @Override
    /** No other parameters are needed in Bibsonomy */
    public Map<String, String> getOtherRequiredParameters() {
        HashMap<String, String> params = new HashMap<String, String>();
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
        if (requiredAuthParameters.get("Username") != null && requiredAuthParameters.get("Username").length() > 0) {
            this.username = requiredAuthParameters.get("Username");
        }
        else {
            throw new ReferenceSystemException("Missing parameter", "Missing bibsonomy 'Username' parameter");
        }

        if (requiredAuthParameters.get("API Key") != null && requiredAuthParameters.get("API Key").length() > 0) {
            this.apiKey = requiredAuthParameters.get("API Key");
        }
        else {
            throw new ReferenceSystemException("Missing parameter", "Missing bibsonomy 'API Key' parameter");
        }

        this.props = props;
        bibsonomyUtils = new BibsonomyUtils(username, apiKey);
        xmlUtils = new XMLUtils();
    }


    /** Adds a reference to Bibsonomy */
    @Override
    public void addReference(Document document) throws Exception {
        String url = getProperty("create_document_url") + username + "/posts";
        String response = bibsonomyUtils.sendPostRequest(url, xmlUtils.convertDocumentToXML(document, username));
        document.setFieldValue(Field.REFERENCESYSTEMID, xmlUtils.parseCreateDocumentResponse(response));
        document.setFieldValue(Field.REFERENCESYSTEM, getReferenceSystemName());
    }


    /** Read all references from Bibsonomy */
    @Override
    public List<Document> getReferences() throws Exception {
        List<Document> documents = new ArrayList<Document>();
        // get Bookmarks
        documents.addAll(getListOfBookmarks());
        // get Posts
        documents.addAll(getListOfPosts());

        // Set the ReferenceSystemName to every reference
        for (Document doc : documents) {
            doc.setFieldValue(Field.REFERENCESYSTEM, getReferenceSystemName());
        }

        return documents;
    }


    /** Removes a reference from the user's library. */
    @Override
    public void removeReference(Document document) throws Exception {
        if (document.getFieldValue(Field.REFERENCESYSTEMID) != null) {
            String url = getProperty("remove_document_url") + username + "/posts/"
                            + document.getFieldValue(Field.REFERENCESYSTEMID);
            try {
                bibsonomyUtils.sendDeleteRequest(url);
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
        if (document.getFieldValue(Field.REFERENCESYSTEMID) != null) {
            String url = getProperty("update_document_url") + username + "/posts/"
                            + document.getFieldValue(Field.REFERENCESYSTEMID);
            try {
                String response = bibsonomyUtils.sendPutRequest(url, xmlUtils.convertDocumentToXML(document, username));
                document.setFieldValue(Field.REFERENCESYSTEMID, xmlUtils.parseCreateDocumentResponse(response));
            }
            catch (ReferenceSystemException re) {
                // Error update bibtex. Post is a bookmark
                if (re.getHttpCode().equals(Integer.toString(HttpURLConnection.HTTP_NOT_FOUND))) {
                    String response = bibsonomyUtils.sendPutRequest(url,
                                    xmlUtils.convertDocumentToXMLBookmark(document, username));
                    document.setFieldValue(Field.REFERENCESYSTEMID, xmlUtils.parseCreateDocumentResponse(response));
                }
                else {
                    throw re;
                }
            }
        }
    }


    /**
     * returns all groups which are stored in the reference system Bibsonomy
     * does not provide this functionality still yet
     */
    @Override
    public List<Group> getGroups() throws Exception {
        return null;
    }


    /**
     * Adds a group to the online referencesystem Bibsonomy does not provide
     * this functionality still yet
     */
    @Override
    public void addGroup(Group group) throws Exception {

    }


    /**
     * Removes a group from the online reference system Bibsonomy does not
     * provide this functionality still yet
     */
    @Override
    public void removeGroup(Group group) throws Exception {

    }


    /** Returns a List of Bookmarks. */
    private List<Document> getListOfBookmarks() throws Exception {
        String url = getProperty("get_bookmark_url") + "&user=" + this.username;
        String response = bibsonomyUtils.sendGetRequest(url);
        return xmlUtils.parseListOfBookmarks(response);
    }


    /** Returns a List of Posts. */
    private List<Document> getListOfPosts() throws Exception {
        String url = getProperty("get_post_url") + "&user=" + this.username;
        String response = bibsonomyUtils.sendGetRequest(url);
        return xmlUtils.parseListOfPosts(response);

    }


    /** read property */
    private String getProperty(String prop) {
        return props.getProperty("Bibsonomy." + prop, "");
    }

}
