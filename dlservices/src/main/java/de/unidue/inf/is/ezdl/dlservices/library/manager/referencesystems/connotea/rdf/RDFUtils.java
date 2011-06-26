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

package de.unidue.inf.is.ezdl.dlservices.library.manager.referencesystems.connotea.rdf;

import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;

import de.unidue.inf.is.ezdl.dlcore.data.OIDFactory;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.TermWithWeight;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.TermWithWeightList;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.TextDocument;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.URLList;
import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;
import de.unidue.inf.is.ezdl.dlservices.library.manager.referencesystems.ReferenceSystemException;



/** Provides Connotea XML RDF functionality */
public class RDFUtils {

    private static final String Namespace_standard = "http://www.connotea.org/2005/01/schema#";
    private static final String Namespace_dcterms = "http://purl.org/dc/terms/";
    private static final String Namespace_dc = "http://purl.org/dc/elements/1.1/";


    // private static final String Namespace_rdf =
    // "http://www.w3.org/1999/02/22-rdf-syntax-ns#";

    /** Converts a Document Object into a HTML form-style set of key=value */
    public String convertDocumentToFormParams(de.unidue.inf.is.ezdl.dlcore.data.dldata.Document document) {
        StringBuffer bf = new StringBuffer();

        // URLs
        bf.append("uri=");
        URLList urllist = document.getDetailURLs();
        if (urllist != null) {
            // can only save one URL
            if (urllist.size() >= 1) {
                bf.append(urllist.get(0).toExternalForm());
            }
            else {
                // NO URL
                // ABORT. Connotea is URL based. without an url it has no sense
                // to save it in connotea
                return null;
            }
        }
        else {
            // ABORT. Connotea is URL based. without an url it has no sense to
            // save it in connotea
            return null;
        }

        // Tags
        bf.append("&tags=");
        TermWithWeightList tagList = (TermWithWeightList) document.getFieldValue(Field.TAGS);
        String seperator = "";
        int tagcount = 0;
        if (tagList != null) {
            for (TermWithWeight t : tagList) {
                tagcount++;
                bf.append(seperator);
                bf.append(t.getTerm());
                seperator = " ";
            }
        }
        if (tagcount == 0) {
            // Add empty tag because a tag is required
            bf.append("empty");
        }

        // Title
        bf.append("&title=");
        bf.append(document.getTitle());

        // Description
        if (document.getFieldValue(Field.NOTE) != null) {
            bf.append("&description=");
            bf.append((String) document.getFieldValue(Field.NOTE));
        }

        // All posts are private
        bf.append("&private=1");
        return bf.toString();
    }


    /** Parses the List of posts and returns a Collection of document Objects */
    @SuppressWarnings("unchecked")
    public List<de.unidue.inf.is.ezdl.dlcore.data.dldata.Document> parseListOfPosts(String response)
                    throws ReferenceSystemException {
        try {
            List<de.unidue.inf.is.ezdl.dlcore.data.dldata.Document> documents = new ArrayList<de.unidue.inf.is.ezdl.dlcore.data.dldata.Document>();

            Document doc = new SAXBuilder().build(new StringReader(response));
            Element rdf = doc.getRootElement();

            Namespace ns = Namespace.getNamespace(Namespace_standard);

            List<Element> posts = rdf.getChildren("Post", ns);

            // for each post
            for (Element post : posts) {
                de.unidue.inf.is.ezdl.dlcore.data.dldata.Document document = new TextDocument();

                // TITLE
                Element title = post.getChild("title", ns);
                if (title != null) {
                    document.setTitle(title.getValue());
                }
                else {
                    document.setTitle("No Title");
                }

                // Description
                Element description = post.getChild("description", ns);
                if (description != null) {
                    document.setFieldValue(Field.NOTE, description.getValue());

                }

                // ID
                String id = post.getAttributeValue("about",
                                Namespace.getNamespace("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#"));
                if (id != null) {
                    document.setFieldValue(Field.REFERENCESYSTEMID, id);
                }

                // URL
                Element uri = post.getChild("uri", ns)
                                .getChild("URI", Namespace.getNamespace("dcterms", Namespace_dcterms))
                                .getChild("link", ns);
                // URL
                if (uri != null && uri.getValue().indexOf("http://NO-CONNOTEA-URL/") == -1) {
                    URLList urllist = new URLList();
                    urllist.add(new URL(uri.getValue()));
                    document.setDetailURLs(urllist);
                }

                // Tags
                List<Element> tags = post.getChildren("subject", Namespace.getNamespace("dc", Namespace_dc));
                TermWithWeightList tagList = new TermWithWeightList();
                for (Element tag : tags) {
                    if (!tag.getValue().equals("empty")) {
                        tagList.add(new TermWithWeight(tag.getValue(), 1));
                    }
                }
                document.setFieldValue(Field.TAGS, tagList);

                // calculate OID
                String oid = OIDFactory.calcOid(document);

                if (oid == null) {
                    if (document.getFieldValue(Field.REFERENCESYSTEMID) != null) {
                        oid = "tmp:" + document.getFieldValue(Field.REFERENCESYSTEMID);
                    }
                    else {
                        oid = "tmp:" + System.currentTimeMillis();
                    }
                }
                document.setOid(oid);

                documents.add(document);
            }

            return documents;
        }
        catch (Exception e) {
            throw new ReferenceSystemException("Error on parsing XML 'List of Posts' response from Connotea",
                            e.toString());
        }

    }


    public String getUrlOfDocument(de.unidue.inf.is.ezdl.dlcore.data.dldata.Document document)
                    throws ReferenceSystemException {
        StringBuffer bf = new StringBuffer();

        // URLs
        bf.append("uri=");
        URLList urllist = document.getDetailURLs();
        if (urllist != null) {
            // can only save one URL
            if (urllist.size() >= 1) {
                bf.append(urllist.get(0).toExternalForm());
            }
            else {
                // NO URL
                // ABORT. Connotea is URL based. without an url it has no sense
                // to save it in connotea
                return null;
            }
        }
        else {
            // ABORT. Connotea is URL based. without an url it has no sense to
            // save it in connotea
            return null;
        }
        return bf.toString();

    }


    /** Parses an error response from Connotea */
    public static String parseError(String response) {
        try {
            Document doc = new SAXBuilder().build(new StringReader(response));

            Element rdf = doc.getRootElement();
            return rdf.getChild("Response", Namespace.getNamespace(Namespace_standard)).getChildTextNormalize(
                            "message", Namespace.getNamespace(Namespace_standard));

        }
        catch (Exception e) {
            // Not a XML Object. Return the original Response
            return response;
        }
    }


    /**
     * Parse the response from a create document request and return the new
     * location which is used as ReferenceSystemId
     */
    public String readAddDocumentReturn(String response) {
        try {
            Document doc = new SAXBuilder().build(new StringReader(response));

            Element rdf = doc.getRootElement();
            return rdf.getChild("Response", Namespace.getNamespace(Namespace_standard)).getChildTextNormalize(
                            "location", Namespace.getNamespace(Namespace_standard));

        }
        catch (Exception e) {
            return "";
        }
    }

}
