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

package de.unidue.inf.is.ezdl.dlservices.library.manager.referencesystems.mendeley.json;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.unidue.inf.is.ezdl.dlcore.data.OIDFactory;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.Document;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.Person;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.PersonList;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.TermWithWeight;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.TermWithWeightList;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.TextDocument;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.URLList;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.library.Group;
import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;
import de.unidue.inf.is.ezdl.dlservices.library.manager.referencesystems.ReferenceSystemException;



/** provides JSON functionality */
public class JSONUtils {

    protected final Logger log = Logger.getLogger(JSONUtils.class.getName());


    /** Parses the Library Response from Mendeley */
    public JSONLibrary parseLibraryResponse(String response) throws ReferenceSystemException {
        try {
            JSONObject library = new JSONObject(response);

            JSONLibrary jsonLibrary = new JSONLibrary();
            jsonLibrary.setTotal_results(library.getString("total_results"));
            jsonLibrary.setTotal_pages(library.getString("total_pages"));
            jsonLibrary.setCurrent_page(library.getString("current_page"));
            jsonLibrary.setItems_per_page(library.getString("items_per_page"));

            JSONArray document_ids = library.getJSONArray("document_ids");

            for (int i = 0; i < document_ids.length(); i++) {
                jsonLibrary.getDocument_ids().add(document_ids.getString(i));
            }

            return jsonLibrary;

        }
        catch (JSONException e) {
            throw new ReferenceSystemException("Error on parsing 'Library JSON Response' from Mendeley", e.getMessage());
        }
    }


    public List<JSONGroup> parseGroupResponse(String response) throws ReferenceSystemException {
        try {
            JSONArray groups = new JSONArray(response);
            ArrayList<JSONGroup> list = new ArrayList<JSONGroup>();

            for (int i = 0; i < groups.length(); i++) {
                JSONGroup jsonGroup = new JSONGroup();
                JSONObject group = groups.getJSONObject(i);

                jsonGroup.setId(group.getString("id"));
                jsonGroup.setName(group.getString("name"));
                jsonGroup.setSize(group.getInt("size"));
                jsonGroup.setType(group.getString("type"));

                list.add(jsonGroup);
            }
            return list;

        }
        catch (JSONException e) {
            throw new ReferenceSystemException("Error on parsing 'Group JSON Response' from Mendeley", e.getMessage());
        }
    }


    /**
     * Parses the document details response from Mendeley end returns a document
     * object
     */
    public Document parseDocumentDetailsResponse(String docid, String response) throws ReferenceSystemException {
        TextDocument document = new TextDocument();

        try {
            document.setFieldValue(Field.REFERENCESYSTEMID, docid);

            JSONObject jsondocument = new JSONObject(response);

            // TITLE
            if (jsondocument.has("title")) {
                document.setTitle(jsondocument.getString("title"));
            }

            // AUTHORS
            if (jsondocument.has("authors")) {
                PersonList personList = new PersonList();

                JSONArray authors = jsondocument.getJSONArray("authors");
                for (int i = 0; i < authors.length(); i++) {
                    Person p = new Person(authors.getString(i));
                    personList.add(p);
                }
                document.setAuthorList(personList);
            }

            // TAGS
            if (jsondocument.has("tags")) {
                TermWithWeightList tagList = new TermWithWeightList();
                JSONArray tags = jsondocument.getJSONArray("tags");
                for (int i = 0; i < tags.length(); i++) {
                    tagList.add(new TermWithWeight(tags.getString(i), 1));

                }
                document.setFieldValue(Field.TAGS, tagList);
            }

            // YEAR
            if (jsondocument.has("year")) {
                document.setYear(Integer.parseInt(jsondocument.getString("year")));
            }

            // URLS
            if (jsondocument.has("url")) {
                URLList urllist = new URLList();
                try {
                    String[] urls = jsondocument.getString("url").split("\n");
                    for (String url : urls) {
                        urllist.add(new URL(url));
                    }
                }
                catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
                document.setDetailURLs(urllist);
            }

            // PUBLISHER
            if (jsondocument.has("publisher")) {
                document.setFieldValue(Field.PUBLISHER, jsondocument.getString("publisher"));
            }

            // NOTES
            if (jsondocument.has("notes")) {
                document.setFieldValue(Field.NOTE, jsondocument.getString("notes"));
            }

            // ISBN
            if (jsondocument.has("isbn")) {
                document.setFieldValue(Field.ISBN, jsondocument.getString("isbn"));
            }

            // ISSN
            if (jsondocument.has("issn")) {
                document.setFieldValue(Field.ISSN, jsondocument.getString("issn"));
            }

            // NOTES
            if (jsondocument.has("notes")) {
                document.setFieldValue(Field.NOTE, jsondocument.getString("notes"));
            }

        }
        catch (JSONException e) {
            throw new ReferenceSystemException("Error on parsing 'Document Details JSON Response' from Mendeley",
                            e.getMessage());
        }
        // calculate OID
        String oid = OIDFactory.calcOid(document);

        if (oid == null) {
            oid = "tmp:" + document.getFieldValue(Field.REFERENCESYSTEMID);
        }

        document.setOid(oid);
        return document;
    }


    /** converts a reference object into a JSON String */
    public String convertReferenceToJSON(Document document, String groupid) throws ReferenceSystemException {

        JSONObject jsonDocument = new JSONObject();

        try {
            // TITLE
            jsonDocument.put("title", document.getTitle());

            // URL
            URLList urllist = document.getDetailURLs();
            String url = "";
            String seperator = "";
            if (urllist != null) {
                for (URL u : urllist) {
                    url = url + seperator + u.toExternalForm();
                }
            }
            jsonDocument.put("website", url);
            jsonDocument.put("type", "Journal Article");

            // YEAR
            jsonDocument.put("year", document.getYear());

            // NOTES
            if (document.getFieldValue(Field.NOTE) != null) {
                jsonDocument.put("notes", document.getFieldValue(Field.NOTE));
            }

            // TAGS
            if (document.getFieldValue(Field.TAGS) != null) {
                JSONArray tags = new JSONArray();
                TermWithWeightList tagList = (TermWithWeightList) document.getFieldValue(Field.TAGS);

                for (TermWithWeight t : tagList) {
                    tags.put(t.getTerm());
                }
                jsonDocument.put("tags", tags);
            }

            // AUTHORS
            if (document.getAuthorList() != null) {
                JSONArray authors = new JSONArray();
                PersonList personList = document.getAuthorList();

                for (Person p : personList) {
                    authors.put(p.asString());
                }
                jsonDocument.put("authors", authors);
            }

            // NOTES
            if (document.getFieldValue(Field.NOTE) != null) {
                jsonDocument.put("notes", document.getFieldValue(Field.NOTE));
            }

            // Group ID
            if (groupid != null && groupid.length() > 0) {
                jsonDocument.put("group_id", groupid);
            }

        }
        catch (JSONException e) {
            throw new ReferenceSystemException("Error on creating JSON Text from reference", e.getMessage());
        }
        return jsonDocument.toString();
    }


    /** Converts a group object to a JSON String for Mendeley */
    public String convertGroupToJSON(Group group) throws ReferenceSystemException {
        JSONObject jsonGroup = new JSONObject();

        try {
            // NAME
            jsonGroup.put("name", group.getName());

            // TYPE
            if (group.getType() != null
                            && group.getType().length() > 0
                            && (group.getType().equals(Group.TYPE_PRIVATE) || group.getType().equals(Group.TYPE_OPEN) || group
                                            .getType().equals(Group.TYPE_INVITE))) {
                jsonGroup.put("type", group.getType());
            }
            else {
                // standard is private group
                jsonGroup.put("type", Group.TYPE_INVITE);
            }
        }
        catch (JSONException e) {
            throw new ReferenceSystemException("Error on creating JSON Text from group", e.getMessage());
        }
        return jsonGroup.toString();
    }


    /** Parses the create document response from Mendeley */
    public String parseCreateDocumentResponse(String response) throws ReferenceSystemException {
        try {
            JSONObject document_id = new JSONObject(response);
            if (document_id.has("document_id")) {
                return document_id.getString("document_id");
            }
            else {
                throw new ReferenceSystemException("Error on parsing 'JSON create Document Response' from Mendeley",
                                null, response);
            }

        }
        catch (JSONException e) {
            throw new ReferenceSystemException("Error on parsing 'JSON create Document Response' from Mendeley",
                            e.getMessage());
        }
    }


    /** Parses the create group response from Mendeley */
    public String parseCreateGroupResponse(String response) throws ReferenceSystemException {
        try {
            JSONObject group_id = new JSONObject(response);
            if (group_id.has("group_id")) {
                return group_id.getString("group_id");
            }
            else {
                throw new ReferenceSystemException("Error on parsing 'JSON create Group Response' from Mendeley", null,
                                response);
            }

        }
        catch (JSONException e) {
            throw new ReferenceSystemException("Error on parsing 'JSON create Group Response' from Mendeley",
                            e.getMessage());
        }
    }


    /** Parses the Group detail Response from Mendeley */
    public JSONGroupDetail parseGroupDetailResponse(String response) throws ReferenceSystemException {
        try {
            JSONObject group = new JSONObject(response);

            JSONGroupDetail jsonGroupDetail = new JSONGroupDetail();
            jsonGroupDetail.setTotal_results(group.getString("total_results"));
            jsonGroupDetail.setTotal_pages(group.getString("total_pages"));
            jsonGroupDetail.setCurrent_page(group.getString("current_page"));
            jsonGroupDetail.setItems_per_page(group.getString("items_per_page"));
            jsonGroupDetail.setGroup_id(group.getString("group_id"));
            jsonGroupDetail.setGroup_invite_only(group.getString("group_invite_only"));
            jsonGroupDetail.setGroup_name(group.getString("group_name"));
            jsonGroupDetail.setGroup_type(group.getString("group_type"));

            JSONArray document_ids = group.getJSONArray("document_ids");

            for (int i = 0; i < document_ids.length(); i++) {
                jsonGroupDetail.getDocument_ids().add(document_ids.getString(i));
            }

            return jsonGroupDetail;

        }
        catch (JSONException e) {
            throw new ReferenceSystemException("Error on parsing 'Library JSON Response' from Mendeley", e.getMessage());

        }
    }


    /** Parses an error response from Mendeley */
    public static String parseError(String response) {
        try {
            JSONObject error = new JSONObject(response);
            return error.getString("error");

        }
        catch (JSONException e) {
            // Not a JSON Object. Return the original Response
            return response;
        }
    }

}
