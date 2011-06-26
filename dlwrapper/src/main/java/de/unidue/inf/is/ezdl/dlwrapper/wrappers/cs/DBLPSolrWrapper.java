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

package de.unidue.inf.is.ezdl.dlwrapper.wrappers.cs;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.common.SolrDocument;

import de.unidue.inf.is.ezdl.dlbackend.ServiceNames;
import de.unidue.inf.is.ezdl.dlbackend.data.StoredDocument;
import de.unidue.inf.is.ezdl.dlbackend.wrappers.SourceID;
import de.unidue.inf.is.ezdl.dlbackend.wrappers.SourceInfo;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.Document;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.Person;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.PersonList;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.TextDocument;
import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;
import de.unidue.inf.is.ezdl.dlwrapper.wrappers.AbstractSolrWrapper;



/**
 * Wrapper for DBLP data, uses a SOLR server as data backend.
 * 
 * @author tbeckers
 */
public class DBLPSolrWrapper extends AbstractSolrWrapper {

    private static final String SERVICE_NAME = ServiceNames.getServiceNameForDL("dblp");
    private static final String DL_ID = "dblp";
    private static final SourceID SOURCE_ID = getSourceID(DL_ID);

    private static final String CONTEXT = "/solr2/dblp";
    private static final String HOST = "http://solr.is.inf.uni-due.de";
    private static final String PORT = "9080";
    private static final String SOLR_SERVER_URL = HOST + ":" + PORT + CONTEXT;

    private static final String TITLE = "title";
    private static final String AUTHOR = "author";
    private static final String YEAR = "year";
    private static final String KEY = "key";
    private static final String PAGES = "pages";
    private static final String EE = "ee";
    private static final String BOOKTITLE = "booktitle";
    private static final String TEXT = "text";

    private static final Field ID_FIELD = Field.KEY;

    private static final Map<Field, String> MAPPING;

    static {
        Map<Field, String> mapping = new HashMap<Field, String>();
        mapping.put(Field.TITLE, TITLE);
        mapping.put(Field.YEAR, YEAR);
        mapping.put(Field.AUTHOR, AUTHOR);
        mapping.put(Field.BOOKTITLE, BOOKTITLE);
        mapping.put(Field.URLS, EE);
        mapping.put(Field.PAGES, PAGES);
        mapping.put(Field.TEXT, TEXT);
        mapping.put(Field.KEY, KEY);
        MAPPING = Collections.unmodifiableMap(mapping);
    }


    public DBLPSolrWrapper() {
        super();
    }


    public DBLPSolrWrapper(SolrServer solrServer) {
        super(solrServer);
    }


    @Override
    public String getServiceName() {
        return SERVICE_NAME;
    }


    @Override
    public SourceID getSourceID() {
        return SOURCE_ID;
    }


    @Override
    protected String getSolrServerUrl() {
        return SOLR_SERVER_URL;
    }


    @Override
    protected Field getIdField() {
        return ID_FIELD;
    }


    @Override
    protected StoredDocument readDocumentFromSolrIndex(SolrDocument solrDocument) {
        Document document = new TextDocument();
        document.setTitle((String) solrDocument.getFieldValue(TITLE));
        document.setYear(notNullValue((Integer) solrDocument.getFieldValue(YEAR)));
        Collection<Object> authorFieldValues = solrDocument.getFieldValues(AUTHOR);
        if (authorFieldValues != null) {
            PersonList authorList = new PersonList();
            for (Object authorFieldValue : authorFieldValues) {
                Person author = new Person(authorFieldValue.toString());
                authorList.add(author);
            }
            document.setAuthorList(authorList);
        }
        StoredDocument stored = new StoredDocument(document);
        final String detailsInfo = (String) solrDocument.getFieldValue(getFieldMapping().get(ID_FIELD));
        stored.addSource(new SourceInfo(SOURCE_ID, detailsInfo));
        return stored;
    }


    @Override
    protected void updateDocumentDetails(SolrDocument solrDocument, Document document) {
        addDetailUrl(document, (String) solrDocument.getFieldValue(EE));
        addDetailUrl(document, (String) solrDocument.getFieldValue("url"));
        document.setFieldValue(Field.BOOKTITLE, solrDocument.getFieldValue(BOOKTITLE));
        document.setFieldValue(Field.PAGES, solrDocument.getFieldValue(PAGES));
    }


    private void addDetailUrl(Document document, String urlString) {
        if (urlString != null) {
            URL detailUrl = null;
            try {
                detailUrl = new URL(urlString);
            }
            catch (MalformedURLException e) {
                if (!urlString.trim().startsWith("http")) {
                    getLogger().info("Trying DBLP URL: " + urlString);
                    try {
                        detailUrl = new URL("http://www.informatik.uni-trier.de/~ley/" + urlString);
                    }
                    catch (MalformedURLException e1) {
                        getLogger().error("Defective URL: " + urlString, e1);
                    }
                }

            }
            if (detailUrl != null) {
                document.addDetailURL(detailUrl);
            }
        }
    }


    @Override
    protected Map<Field, String> getFieldMapping() {
        return MAPPING;
    }


    @Override
    protected boolean documentIsValid(StoredDocument stored) {
        // TODO: implement this.
        return true;
    }

}