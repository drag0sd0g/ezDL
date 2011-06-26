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

package de.unidue.inf.is.ezdl.dlwrapper.wrappers;

import java.net.MalformedURLException;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import de.unidue.inf.is.ezdl.dlbackend.data.StoredDocument;
import de.unidue.inf.is.ezdl.dlbackend.data.StoredDocumentList;
import de.unidue.inf.is.ezdl.dlbackend.wrappers.SourceID;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.Document;
import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;
import de.unidue.inf.is.ezdl.dlcore.data.query.DefaultQuery;
import de.unidue.inf.is.ezdl.dlcore.data.query.DocumentQuery;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeBool;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeCompare;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeCompare.Predicate;
import de.unidue.inf.is.ezdl.dlcore.query.QueryConverter;
import de.unidue.inf.is.ezdl.dlcore.query.SolrQueryConverter;



/**
 * Basic implementation for Solr-based wrappers.
 * 
 * @author tbeckers
 */
public abstract class AbstractSolrWrapper extends AbstractWrapper {

    /**
     * The maximum number of results this wrapper should retrieve.
     */
    private static final int MAX_RESULTS = 500;
    /**
     * Indicates the API to use in the source ID.
     */
    protected static final String SOURCE_API_SOLR = "solr";
    /**
     * A query converter to convert queries into Lucene queries-
     */
    private QueryConverter queryConverter;
    /**
     * The Solr server that is wrapped.
     */
    private SolrServer server;


    /**
     * Constructor.
     */
    public AbstractSolrWrapper() {
        super();
        queryConverter = new SolrQueryConverter(getFieldMapping());
        server = createNewSolrServer();
    }


    /**
     * Constructor for unit tests.
     * 
     * @param solrServer
     *            The Solr server that should be used for queries.
     */
    public AbstractSolrWrapper(SolrServer solrServer) {
        super();
        queryConverter = new SolrQueryConverter(getFieldMapping());
        server = solrServer;
    }


    /**
     * Returns the source ID to use for the wrapper.
     * 
     * @param dlId
     *            the id of the DL
     * @return the source ID
     */
    protected static final SourceID getSourceID(String dlId) {
        return new SourceID(dlId, SOURCE_API_SOLR);
    }


    protected abstract Field getIdField();


    /**
     * Returns the mapping of fields. The mapping maps ezDL field names to field
     * names for the Solr index.
     * 
     * @return the mapping of fields
     */
    protected abstract Map<Field, String> getFieldMapping();


    /**
     * Creates a new Solr server connection.
     * 
     * @return a new Solr server connection
     */
    private SolrServer createNewSolrServer() {
        try {
            CommonsHttpSolrServer s = new CommonsHttpSolrServer(getSolrServerUrl());
            s.setConnectionTimeout(10000);
            s.setSoTimeout(2000);
            return s;
        }
        catch (MalformedURLException ex) {
            throw new IllegalStateException(ex);
        }
    }


    /**
     * Returns the URL of the Solr server.
     * 
     * @return the URL of the Solr server
     */
    protected abstract String getSolrServerUrl();


    /**
     * Returns the maximum number of results that should be handled by this
     * wrapper.
     * 
     * @return the maximum number of results that should be handled by this
     *         wrapper
     */
    protected int getMaxNumberOfResults() {
        return MAX_RESULTS;
    }


    @Override
    public final StoredDocumentList process(DocumentQuery documentQuery) {
        String solrQueryString = queryConverter.convert(documentQuery.getQuery());

        getLogger().info("Query is: " + solrQueryString);

        SolrQuery query = new SolrQuery();
        query.setIncludeScore(true);
        query.setRows(getMaxNumberOfResults());
        query.setQuery(solrQueryString);

        try {
            QueryResponse response = server.query(query);
            SolrDocumentList hits = response.getResults();
            StoredDocumentList result = new StoredDocumentList();
            for (int i = 0; (i < hits.size()) && !isHalted(); i++) {
                SolrDocument doc = hits.get(i);
                StoredDocument stored = readDocumentFromSolrIndex(doc);
                result.add(stored);
            }
            if (!isHalted()) {
                return result;
            }
            else {
                return new StoredDocumentList();
            }
        }
        catch (SolrServerException e) {
            getLogger().error(e.getMessage(), e);
        }
        return new StoredDocumentList();
    }


    /**
     * Retrieves Solr document by its id.
     * 
     * @param id
     *            The id
     * @param field
     *            The id field
     * @return The Solr document with the specified id
     */
    protected final SolrDocument retrieveSolrDocumentForId(String id, Field field) {
        SolrQuery query = new SolrQuery();
        query.setIncludeScore(true);
        query.setRows(1);
        QueryNodeCompare queryNodeCompare = new QueryNodeCompare(field, Predicate.EQ, id);
        query.setQuery(queryConverter.convert(new DefaultQuery(queryNodeCompare)));
        QueryResponse response;
        try {
            response = server.query(query);
            SolrDocumentList hits = response.getResults();
            if (hits.size() == 1) {
                return hits.get(0);
            }
        }
        catch (SolrServerException e) {
            getLogger().error(e.getMessage(), e);
        }
        return null;
    }


    protected final int notNullValue(Integer number) {
        return number == null ? 0 : number;
    }


    /**
     * Returns document from a Solr document.
     * 
     * @param solrDocument
     *            The Solr document
     * @return Document from the Solr document
     */
    protected abstract StoredDocument readDocumentFromSolrIndex(SolrDocument solrDocument);


    @Override
    protected StoredDocumentList process(QueryNodeBool node) {
        return null;
    }


    @Override
    public void askDetails(StoredDocumentList incomplete) {
        for (StoredDocument stored : incomplete) {
            String id = getDetailInfo(stored);
            if (id != null) {
                SolrDocument solrDocument = retrieveSolrDocumentForId(id, getIdField());
                if (solrDocument != null) {
                    Document document = stored.getDocument();
                    updateDocumentDetails(solrDocument, document);
                    setDetailTimestampToCurrent(stored);
                }
            }
        }
    }


    protected abstract void updateDocumentDetails(SolrDocument solrDocument, Document document);

}