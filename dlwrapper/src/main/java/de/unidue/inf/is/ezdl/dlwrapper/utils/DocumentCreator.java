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

package de.unidue.inf.is.ezdl.dlwrapper.utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import de.unidue.inf.is.ezdl.dlcore.data.dldata.Document;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.Person;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.PersonList;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.TextDocument;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.URLList;
import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;
import de.unidue.inf.is.ezdl.dlcore.utils.StringUtils;
import de.unidue.inf.is.ezdl.dlwrapper.Wrapper;
import de.unidue.inf.is.ezdl.dlwrapper.toolkit.ToolkitAPI;
import de.unidue.inf.is.ezdl.dlwrapper.wrappers.AbstractBasicToolkitWrapper;



/**
 * The DocumentCreator makes it easy to convert the output of the
 * {@link ToolkitAPI} as called by {@link AbstractBasicToolkitWrapper} into an
 * ezDL {@link Document}.
 * <p>
 * The {@link DocumentCreator} assumes that the toolkit config uses specific
 * field names for its output as given in the constants in this class whose
 * names begin with HT_KEY. The contents of these fields are trimmed if
 * necessary (see {@link String#trim()}. Fields are only set if there is any
 * content for them.
 * <p>
 * Every conversion should run through this class or a descendant so that new
 * fields are added to many {@link Wrapper} implementations at the same time.
 * <p>
 * The given implementation is thread-safe.
 * 
 * @author mjordan
 */
public class DocumentCreator {

    private static final String HT_KEY_YEAR = "year";
    private static final String HT_KEY_AUTHORS = "authors";
    private static final String HT_KEY_JOURNAL = "journal";
    private static final String HT_KEY_DETAILLINK = "detaillink";
    private static final String HT_KEY_TITLE = "title";
    private static final Object HT_KEY_ABSTRACT = "abstract";
    private static final Object HT_KEY_VOLUME = "volume";
    private static final Object HT_KEY_PAGES = "pages";

    /**
     * The logger.
     */
    private Logger logger = Logger.getLogger(DocumentCreator.class);


    /**
     * Returns a logger.
     * 
     * @return a logger
     */
    protected Logger getLogger() {
        return logger;
    }


    /**
     * Creates a document using the info in the document map that is created
     * using {@link ToolkitAPI}.
     * 
     * @param docInfoMap
     *            the document information map
     * @return an actual ezDL {@link Document}
     */
    public Document createDocumentFromMap(Map<String, Object> docInfoMap) {
        getLogger().debug("Map: " + docInfoMap);
        Document doc = createNewDocument(docInfoMap);

        handleTitle(doc, (String) docInfoMap.get(HT_KEY_TITLE));
        handleJournal(doc, (String) docInfoMap.get(HT_KEY_JOURNAL));
        handleAbstract(doc, (String) docInfoMap.get(HT_KEY_ABSTRACT));
        handlePages(doc, (String) docInfoMap.get(HT_KEY_PAGES));
        handleVolume(doc, (String) docInfoMap.get(HT_KEY_VOLUME));
        handleDetailLink(doc, docInfoMap.get(HT_KEY_DETAILLINK));
        handleAuthors(doc, docInfoMap.get(HT_KEY_AUTHORS));
        handleYear(doc, (String) docInfoMap.get(HT_KEY_YEAR));

        getLogger().debug("Document: " + doc);
        return doc;
    }


    /**
     * Creates a new document for the data in the document info map.
     * 
     * @param docInfoMap
     *            information on the document to create
     * @return The standard implementation in this class always returns a
     *         {@link TextDocument}. This can be overridden to allow for
     *         different {@link Document} implementations.
     */
    protected Document createNewDocument(Map<String, Object> docInfoMap) {
        TextDocument doc = new TextDocument();
        return doc;
    }


    /**
     * Handles the title by inserting the trimmed content of the parameter into
     * the {@link Field#TITLE} of the document.
     * 
     * @param doc
     *            the document to insert into
     * @param title
     *            the title to insert, if it is not empty
     * @see StringUtils#isEmpty(String)
     */
    protected void handleTitle(Document doc, String title) {
        setFieldValueTrimmedIfNotEmpty(doc, Field.TITLE, title);
    }


    /**
     * Handles the journal title by inserting the trimmed content of the
     * parameter into the {@link Field#JOURNAL} of the document.
     * 
     * @param doc
     *            the document to insert into
     * @param journalName
     *            the journal name to insert, if it is not empty
     * @see StringUtils#isEmpty(String)
     */
    protected void handleJournal(Document doc, String journalName) {
        setFieldValueTrimmedIfNotEmpty(doc, Field.JOURNAL, journalName);
    }


    /**
     * Handles the abstract by inserting the trimmed content of the parameter
     * into the {@link Field#ABSTRACT} of the document.
     * 
     * @param doc
     *            the document to insert into
     * @param abstrakt
     *            the abstract to insert, if it is not empty
     * @see StringUtils#isEmpty(String)
     */
    protected void handleAbstract(Document doc, String abstrakt) {
        if (abstrakt != null && !abstrakt.equalsIgnoreCase("An abstract is not available.")) {
            setFieldValueTrimmedIfNotEmpty(doc, Field.ABSTRACT, abstrakt);
        }
    }


    /**
     * Handles the title by inserting the trimmed content of the title parameter
     * into the {@link Field#TITLE} of the document.
     * 
     * @param doc
     *            the document to insert into
     * @param title
     *            the title to insert, if it is not empty
     * @see StringUtils#isEmpty(String)
     */
    protected void handlePages(Document doc, String pages) {
        setFieldValueTrimmedIfNotEmpty(doc, Field.PAGES, pages);
    }


    /**
     * Handles the Volume by inserting the trimmed content of the parameter into
     * the {@link Field#VOLUME} of the document.
     * 
     * @param doc
     *            the document to insert into
     * @param volume
     *            the volume to insert, if it is not empty
     * @see StringUtils#isEmpty(String)
     */
    protected void handleVolume(Document doc, String volume) {
        setFieldValueTrimmedIfNotEmpty(doc, Field.VOLUME, volume);
    }


    /**
     * Handles the detail link.
     * <p>
     * The detail link can be either a {@link URL} or a {@link String}. If it is
     * a String, the detail link actually inserted is prefixed by whatever
     * {@link #getDetailUrlPrefix()} returns.
     * 
     * @param doc
     *            the document to insert into
     * @param detailLink
     *            the detail link information to insert after preprocessing
     */
    protected void handleDetailLink(Document doc, Object detailLink) {
        if (detailLink != null) {
            URL detailUrl = null;
            if (detailLink instanceof URL) {
                detailUrl = (URL) detailLink;
            }
            else if (detailLink instanceof String) {
                String detailLinkStr = (String) detailLink;
                try {
                    detailUrl = new URL(getDetailUrlPrefix() + detailLinkStr);
                }
                catch (MalformedURLException e) {
                    logger.warn("Could not parse details URL", e);
                }
            }
            if (detailUrl != null) {
                URLList urls = new URLList();
                final URL cleanedUrl = cleanUrl(detailUrl);
                if (cleanedUrl != null) {
                    urls.add(cleanedUrl);
                    doc.setDetailURLs(urls);
                }
            }
        }
    }


    /**
     * Removed unwanted parts from a detail link URL.
     * <p>
     * This can be used to remove things like session IDs that make two URLs
     * look different even if they point to the same page. This would make
     * merging lists of detail URLs difficult and prone of dupes.
     * 
     * @param detailUrl
     *            the URL to clean
     * @return the cleaned URL or null if an error occurred
     */
    protected URL cleanUrl(URL detailUrl) {
        return detailUrl;
    }


    /**
     * Returns a prefix that is prepended in front of the URL snippet returned
     * by some digital libraries.
     * <p>
     * This method can be used to create valid URLs by prepending a proper
     * prefix to a site-relative URL
     * 
     * @return The standard implementation returns the empty String.
     */
    protected String getDetailUrlPrefix() {
        return "";
    }


    /**
     * Handles authors.
     * <p>
     * The standard implementation assumes that the object given is a
     * {@link List} of {@link String}s that contain author names. The list is
     * passed to {@link #handlePersonList(Document, String...)}.
     * 
     * @param doc
     *            The document to handle authors for
     * @param authorInput
     *            The author input. In cases where the {@link ToolkitAPI} has a
     *            hard time parsing the authors, a snippet of the web page can
     *            be passed to an overridden implementation.
     */
    @SuppressWarnings("unchecked")
    protected void handleAuthors(Document doc, Object authorInput) {
        if (authorInput instanceof List<?>) {
            List<String> authorsList = (List<String>) authorInput;
            handlePersonList(doc, Field.AUTHOR, authorsList.toArray(new String[0]));
        }
    }


    /**
     * Handles a list of {@link String} objects each of which contain a person
     * name by inserting them into the given field of the document.
     * 
     * @param doc
     *            The document to insert the author names into
     * @param personField
     *            the field to insert the {@link Person} list into.
     * @param nameList
     *            The list of names.
     */
    protected void handlePersonList(Document doc, Field personField, String... nameList) {
        PersonList personList = new PersonList();
        for (String author : nameList) {
            Person a = new Person(author);
            personList.add(a);
        }
        doc.setFieldValue(personField, personList);
    }


    /**
     * Handles the year.
     * 
     * @param doc
     *            The document to set the year for.
     * @param year
     *            The year representation. If the year cannot be parsed, the
     *            year field is set to {@link Document#YEAR_INVALID}.
     */
    protected void handleYear(Document doc, String year) {
        try {
            doc.setYear(Integer.parseInt(year));
        }
        catch (NumberFormatException e) {
            doc.setYear(Document.YEAR_INVALID);
        }
    }


    /**
     * Sets a field to a value if the value is not empty.
     * 
     * @param field
     *            the field to set
     * @param str
     *            the value to set the field to
     * @see StringUtils#isEmpty(String)
     */
    protected void setFieldValueTrimmedIfNotEmpty(Document doc, Field field, String str) {
        if (str != null) {
            final String value = str.trim();
            if (!StringUtils.isEmpty(value)) {
                doc.setFieldValue(field, value);
            }
        }
    }

}
