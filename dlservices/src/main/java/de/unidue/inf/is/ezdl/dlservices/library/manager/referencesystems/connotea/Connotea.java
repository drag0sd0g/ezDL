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
import de.unidue.inf.is.ezdl.dlservices.library.manager.referencesystems.connotea.rdf.RDFUtils;



/** Implements the connection to connotea */
public final class Connotea extends OnlineReferenceSystem {

    private String username;
    private String apiKey;
    private ConnoteaUtils connoteaUtils;
    private RDFUtils rdfUtils;
    private Properties props;


    /** returns a short description about the Wrapper */
    public static String getReferenceSystemName() {
        return "Connotea Reference System";
    }


    /** Returns the required authentication parameters */
    @Override
    public Map<String, String> getRequiredAuthParameters() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("Username", "");
        params.put("Password", "");
        return params;
    }


    @Override
    /** No other parameters are needed in Connotea */
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
            throw new ReferenceSystemException("Missing parameter", "Missing connotea 'Username' parameter");
        }

        if (requiredAuthParameters.get("Password") != null && requiredAuthParameters.get("Password").length() > 0) {
            this.apiKey = requiredAuthParameters.get("Password");
        }
        else {
            throw new ReferenceSystemException("Missing parameter", "Missing connotea 'Password' parameter");
        }

        this.props = props;
        connoteaUtils = new ConnoteaUtils(username, apiKey);
        rdfUtils = new RDFUtils();
    }


    /** Adds a reference to connotea */
    @Override
    public void addReference(Document document) throws Exception {
        String url = getProperty("create_document_url");
        String paramurl = rdfUtils.convertDocumentToFormParams(document);
        // Without a url it as no sense to save it online. Connotea is URL based
        if (paramurl != null) {
            String response = connoteaUtils.sendPostRequest(url, paramurl);
            document.setFieldValue(Field.REFERENCESYSTEMID, rdfUtils.readAddDocumentReturn(response));
            document.setFieldValue(Field.REFERENCESYSTEM, getReferenceSystemName());
        }
    }


    /** Read all references from connotea */
    @Override
    public List<Document> getReferences() throws Exception {
        List<Document> documents = new ArrayList<Document>();

        String url = getProperty("get_posts_url") + this.username;
        String response = connoteaUtils.sendGetRequest(url);
        documents.addAll(rdfUtils.parseListOfPosts(response));

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
            String url = getProperty("remove_document_url");
            try {
                String paramurl = rdfUtils.getUrlOfDocument(document);
                if (paramurl != null) {
                    connoteaUtils.sendPostRequest(url, paramurl);
                }

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
            String url = getProperty("edit_document_url");
            String paramurl = rdfUtils.convertDocumentToFormParams(document);
            // Without a url it as no sense to save it online. Connotea is URL
            // based
            if (paramurl != null) {
                connoteaUtils.sendPostRequest(url, paramurl);
            }
        }
    }


    /**
     * returns all groups which are stored in the reference system Connotea does
     * not provide this functionality still yet
     */
    @Override
    public List<Group> getGroups() throws Exception {
        return null;
    }


    /**
     * Adds a group to the online referencesystem Connotea does not provide this
     * functionality still yet
     */
    @Override
    public void addGroup(Group group) throws Exception {
    }


    /**
     * Removes a group from the online reference system Connotea does not
     * provide this functionality still yet
     */
    @Override
    public void removeGroup(Group group) throws Exception {
    }


    /** read property */
    private String getProperty(String prop) {
        return props.getProperty("Connotea." + prop, "");
    }

}
