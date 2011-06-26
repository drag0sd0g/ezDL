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

package de.unidue.inf.is.ezdl.dlservices.library.manager.referencesystems.bibsonomy.xml;

import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import de.unidue.inf.is.ezdl.dlcore.data.OIDFactory;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.Person;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.PersonList;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.TermWithWeight;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.TermWithWeightList;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.TextDocument;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.URLList;
import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;
import de.unidue.inf.is.ezdl.dlservices.library.manager.referencesystems.ReferenceSystemException;



/** Provides Bibsonomy XML functionality */
public class XMLUtils {

    /**
     * Parses the List of bookmarks and returns a Collection of reference
     * Objects
     */
    @SuppressWarnings("unchecked")
    public List<de.unidue.inf.is.ezdl.dlcore.data.dldata.Document> parseListOfBookmarks(String response)
                    throws ReferenceSystemException {
        try {
            org.jdom.Document doc = new SAXBuilder().build(new StringReader(response));

            Element bibsonomy = doc.getRootElement();

            List<Element> posts = bibsonomy.getChild("posts").getChildren("post");
            List<de.unidue.inf.is.ezdl.dlcore.data.dldata.Document> documents = new ArrayList<de.unidue.inf.is.ezdl.dlcore.data.dldata.Document>();

            // for each post
            for (Element post : posts) {
                de.unidue.inf.is.ezdl.dlcore.data.dldata.Document document = new TextDocument();

                // description of post
                if (post.getAttributeValue("description") != null) {
                    document.setFieldValue(Field.NOTE, post.getAttributeValue("description"));
                }

                Element bookmark = post.getChild("bookmark");

                // intern Bookmark id of Bibsonomy
                if (bookmark.getAttributeValue("intrahash") != null) {
                    document.setFieldValue(Field.REFERENCESYSTEMID, bookmark.getAttributeValue("intrahash"));
                }

                // URL
                if (bookmark.getAttributeValue("url") != null) {

                    URLList urllist = new URLList();
                    urllist.add(new URL(bookmark.getAttributeValue("url")));
                    document.setDetailURLs(urllist);
                }

                // TITLE
                if (bookmark.getAttributeValue("title") != null) {
                    document.setTitle(bookmark.getAttributeValue("title"));
                }

                // Tags
                List<Element> tags = post.getChildren("tag");
                TermWithWeightList tagList = new TermWithWeightList();

                for (Element tag : tags) {
                    tagList.add(new TermWithWeight(tag.getAttributeValue("name"), 1));

                }
                document.setFieldValue(Field.TAGS, tagList);

                // calculate OID
                String oid = OIDFactory.calcOid(document);

                if (oid == null) {
                    oid = "tmp:" + document.getFieldValue(Field.REFERENCESYSTEMID);
                }
                document.setOid(oid);

                documents.add(document);
            }
            return documents;
        }
        catch (Exception e) {
            throw new ReferenceSystemException("Error on parsing XML 'List of Bookmarks' response from Bibsonomy",
                            e.toString());
        }
    }


    /** Parses the List of posts and returns a Collection of document Objects */
    @SuppressWarnings("unchecked")
    public List<de.unidue.inf.is.ezdl.dlcore.data.dldata.Document> parseListOfPosts(String response)
                    throws ReferenceSystemException {
        try {
            org.jdom.Document doc = new SAXBuilder().build(new StringReader(response));

            Element bibsonomy = doc.getRootElement();

            List<Element> posts = bibsonomy.getChild("posts").getChildren("post");
            List<de.unidue.inf.is.ezdl.dlcore.data.dldata.Document> documents = new ArrayList<de.unidue.inf.is.ezdl.dlcore.data.dldata.Document>();

            // for each post
            for (Element post : posts) {
                de.unidue.inf.is.ezdl.dlcore.data.dldata.Document document = new TextDocument();

                // description of post
                if (post.getAttributeValue("description") != null
                                && !post.getAttributeValue("description").equals("empty")) {
                    document.setFieldValue(Field.NOTE, post.getAttributeValue("description"));
                }

                Element bibtex = post.getChild("bibtex");

                // intern reference id of Bibsonomy
                if (bibtex.getAttributeValue("intrahash") != null) {
                    document.setFieldValue(Field.REFERENCESYSTEMID, bibtex.getAttributeValue("intrahash"));
                }

                // URL
                if (bibtex.getAttributeValue("url") != null) {
                    URLList urllist = new URLList();
                    urllist.add(new URL(bibtex.getAttributeValue("url")));
                    document.setDetailURLs(urllist);
                }

                // TITLE
                if (bibtex.getAttributeValue("title") != null) {
                    document.setTitle(bibtex.getAttributeValue("title"));
                }

                // YEAR
                if (bibtex.getAttributeValue("year") != null) {
                    document.setYear(Integer.parseInt(bibtex.getAttributeValue("year")));
                }

                // PUBLISHER
                if (bibtex.getAttributeValue("publisher") != null
                                && !bibtex.getAttributeValue("publisher").equals("empty")) {
                    document.setFieldValue(Field.PUBLISHER, bibtex.getAttributeValue("publisher"));
                }

                // Author, are delimited by 'and' in Bibsonomy
                String[] authors = bibtex.getAttributeValue("author").split("and");
                PersonList personList = new PersonList();
                for (int i = 0; i < authors.length; i++) {
                    if (!authors[i].trim().equals("empty")) {
                        Person p = new Person(authors[i].trim());
                        personList.add(p);
                    }
                }
                document.setAuthorList(personList);

                // Tags
                List<Element> tags = post.getChildren("tag");
                TermWithWeightList tagList = new TermWithWeightList();

                for (Element tag : tags) {
                    if (!tag.getAttributeValue("name").equals("empty")) {
                        tagList.add(new TermWithWeight(tag.getAttributeValue("name"), 1));
                    }

                }
                document.setFieldValue(Field.TAGS, tagList);

                // calculate OID
                String oid = OIDFactory.calcOid(document);

                if (oid == null) {
                    oid = "tmp:" + document.getFieldValue(Field.REFERENCESYSTEMID);
                }
                document.setOid(oid);

                documents.add(document);
            }
            return documents;
        }
        catch (Exception e) {
            throw new ReferenceSystemException("Error on parsing XML 'List of Posts' response from Bibsonomy",
                            e.toString());
        }
    }


    /** Converts a document object to a XML String */
    public String convertDocumentToXML(de.unidue.inf.is.ezdl.dlcore.data.dldata.Document document, String username)
                    throws ReferenceSystemException {
        try {
            org.jdom.Document doc = new org.jdom.Document();
            Element bibsonomy = new Element("bibsonomy");

            // Post
            Element post = new Element("post");
            String description = "empty";
            if (document.getFieldValue(Field.NOTE) != null) {
                description = (String) document.getFieldValue(Field.NOTE);
            }

            post.setAttribute("description", description);

            // User
            post.addContent(new Element("user").setAttribute("name", username));

            // Tags
            TermWithWeightList tagList = (TermWithWeightList) document.getFieldValue(Field.TAGS);
            int tagcount = 0;
            if (tagList != null) {
                for (TermWithWeight t : tagList) {
                    tagcount++;
                    post.addContent(new Element("tag").setAttribute("name", t.getTerm()));
                }
            }
            if (tagcount == 0) {
                // Add empty tag because a tag is required
                post.addContent(new Element("tag").setAttribute("name", "empty"));
            }

            // POST Type. Save all references as POSTs
            Element postType = new Element("bibtex");
            postType.setAttribute("title", document.getTitle());
            postType.setAttribute("entrytype", "article");

            // PUBLISHER
            String publisher = "empty";
            if (document.getFieldValue(Field.PUBLISHER) != null) {
                publisher = (String) document.getFieldValue(Field.PUBLISHER);
            }
            postType.setAttribute("publisher", publisher);

            // URLs
            URLList urllist = document.getDetailURLs();
            if (urllist != null) {
                // can only save one URL
                if (urllist.size() >= 1) {
                    postType.setAttribute("url", urllist.get(0).toExternalForm());
                }
            }

            // YEAR
            postType.setAttribute("year", Integer.toString(document.getYear()));

            String bibtexKey = "";

            // AUTHORS
            PersonList personList = document.getAuthorList();
            if (personList != null) {

                String author = "";
                String delim = "";

                for (Person p : personList) {
                    author += delim + p.asString();
                    delim = " and ";

                    if (bibtexKey.length() == 0) {
                        bibtexKey = p.getLastName();
                    }
                }
                if (author.length() == 0) {
                    author = "empty";
                }

                postType.setAttribute("author", author);
            }
            else {
                postType.setAttribute("author", "empty");
            }

            bibtexKey += Integer.toString(document.getYear());
            postType.setAttribute("bibtexKey", bibtexKey);

            post.addContent(postType);
            bibsonomy.addContent(post);
            doc.addContent(bibsonomy);

            XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
            return out.outputString(doc);

        }
        catch (Exception e) {
            throw new ReferenceSystemException("Error on converting reference object to XML", e.toString());
        }
    }


    /** Converts a document object to a Bibsonomy Bookmakr XML String */
    public String convertDocumentToXMLBookmark(de.unidue.inf.is.ezdl.dlcore.data.dldata.Document document,
                    String username) throws ReferenceSystemException {
        try {
            org.jdom.Document doc = new org.jdom.Document();
            Element bibsonomy = new Element("bibsonomy");

            // Post
            Element post = new Element("post");
            String description = "empty";
            if (document.getFieldValue(Field.NOTE) != null) {
                description = (String) document.getFieldValue(Field.NOTE);
            }

            post.setAttribute("description", description);

            // User
            post.addContent(new Element("user").setAttribute("name", username));

            // Tags
            TermWithWeightList tagList = (TermWithWeightList) document.getFieldValue(Field.TAGS);
            if (tagList != null) {
                for (TermWithWeight t : tagList) {
                    post.addContent(new Element("tag").setAttribute("name", t.getTerm()));
                }
            }
            else {
                // Add empty tag becaus a tag is required
                post.addContent(new Element("tag").setAttribute("name", "empty"));
            }

            // POST Type. Save all references as POSTs
            Element postType = new Element("bookmark");
            postType.setAttribute("title", document.getTitle());

            // URLs
            URLList urllist = document.getDetailURLs();
            if (urllist != null) {
                // can only save one URL
                if (urllist.size() >= 1) {
                    postType.setAttribute("url", urllist.get(0).toExternalForm());
                }
            }

            post.addContent(postType);
            bibsonomy.addContent(post);
            doc.addContent(bibsonomy);

            XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
            return out.outputString(doc);

        }
        catch (Exception e) {
            throw new ReferenceSystemException("Error on converting reference object to XML", e.toString());
        }
    }


    /**
     * Parses the createDocument response from Bibsonomy. Returns the intern
     * document id
     */
    public String parseCreateDocumentResponse(String response) throws ReferenceSystemException {
        try {
            org.jdom.Document doc = new SAXBuilder().build(new StringReader(response));
            Element bibsonomy = doc.getRootElement();

            return bibsonomy.getChildTextNormalize("resourcehash");

        }
        catch (Exception e) {
            throw new ReferenceSystemException("Error on parsing 'Create Document Response' from Bibsonomy",
                            e.getMessage());
        }
    }


    /** Parses an error response from Bibsonomy */
    public static String parseError(String response) {
        try {
            org.jdom.Document doc = new SAXBuilder().build(new StringReader(response));

            Element bibsonomy = doc.getRootElement();
            return bibsonomy.getChildTextNormalize("error");

        }
        catch (Exception e) {
            // Not a XML Object. Return the original Response
            return response;
        }
    }
}
