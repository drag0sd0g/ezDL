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

package de.unidue.inf.is.ezdl.dlwrapper.wrappers.dummy;

import java.util.Arrays;
import java.util.UUID;

import org.apache.log4j.Logger;

import de.unidue.inf.is.ezdl.dlbackend.ServiceNames;
import de.unidue.inf.is.ezdl.dlbackend.data.StoredDocument;
import de.unidue.inf.is.ezdl.dlbackend.data.StoredDocumentList;
import de.unidue.inf.is.ezdl.dlbackend.wrappers.SourceID;
import de.unidue.inf.is.ezdl.dlbackend.wrappers.SourceInfo;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.Document;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.Person;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.PersonList;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.TextDocument;
import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;
import de.unidue.inf.is.ezdl.dlcore.data.query.DocumentQuery;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeBool;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeCompare;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeProximity;
import de.unidue.inf.is.ezdl.dlcore.query.AbstractDefaultOrderQueryTreeWalker;
import de.unidue.inf.is.ezdl.dlwrapper.wrappers.AbstractWrapper;



/**
 * DummyWrapper sends back a couple of hard-coded dummy results. It can be
 * configured by sending a query for year=n, where n is the number of required
 * result items.
 * 
 * @author mjordan
 */
public final class DummyWrapper extends AbstractWrapper {

    private class DummyQueryTreeWalker extends AbstractDefaultOrderQueryTreeWalker {

        int result = -1;


        @Override
        protected void process(QueryNodeProximity node, QueryNodeBool parent, int parentChildrenIndex,
                        int parentChildrenCount) {
        }


        @Override
        protected void process(QueryNodeCompare node, QueryNodeBool parent, int parentChildrenIndex,
                        int parentChildrenCount) {
            if (Field.YEAR == node.getFieldCode()) {
                result = Integer.parseInt(node.getTokensAsString());
            }
        }


        @Override
        protected void process(QueryNodeBool node, QueryNodeBool parent, int parentChildrenIndex,
                        int parentChildrenCount) {
        }


        public int getResult() {
            return result;
        }
    }


    private static final String SERVICE_NAME = ServiceNames.getServiceNameForDL("dummy");
    private static final String DL_ID = "dummy";
    private static final SourceID sourceId = new SourceID(DL_ID, SourceID.API_WEB);

    private static final int SLEEP_EVERY_N_RESULTS = 10;
    private static final int SLEEP_MS = 20;

    private static final String COMPLETED = "COMPLETED BY DUMMY WRAPPER!!";
    static final int RESULT_LIST_SIZE = 20;

    private Logger logger = Logger.getLogger(DummyWrapper.class);


    /**
     * Constructor.
     */
    public DummyWrapper() {
        super();
    }


    @Override
    public String getServiceName() {
        return SERVICE_NAME;
    }


    @Override
    public SourceID getSourceID() {
        return sourceId;
    }


    @Override
    public StoredDocumentList process(DocumentQuery dQuery) {
        StoredDocumentList result = new StoredDocumentList();
        int count = getCountFromQuery(dQuery);
        for (int i = 0; ((!isHalted()) && (i < count)); i++) {
            Document d = new TextDocument();
            d.setAuthorList(new PersonList(Arrays.asList(new Person("Ein Autor"))));
            d.setTitle("Ein tolle Buchstaben- und Zahlenkombination: " + UUID.randomUUID().toString());
            d.setYear(i);
            StoredDocument stored = new StoredDocument(d);
            stored.addSource(new SourceInfo(sourceId, "nodetailsbutwhocares"));
            result.add(stored);
            if ((i % SLEEP_EVERY_N_RESULTS) == 0) {
                sleep(SLEEP_MS);
            }
        }
        if (isHalted()) {
            logger.info("query aborted");
            result = new StoredDocumentList();
        }
        else {
            logger.info("query processed");
        }
        return result;
    }


    /**
     * Extracts the year clause's value from the query, if one exists. Otherwise
     * returns the default number of results.
     * 
     * @param dQuery
     *            the query
     * @return the number of results required
     */
    private int getCountFromQuery(DocumentQuery dQuery) {
        DummyQueryTreeWalker johnny = new DummyQueryTreeWalker();
        johnny.walk(dQuery.getQuery().getTree());
        if (johnny.getResult() != -1) {
            return johnny.getResult();
        }
        else {
            return RESULT_LIST_SIZE;
        }
    }


    private void sleep(int ms) {
        try {
            Thread.sleep(ms);
        }
        catch (InterruptedException e) {
        }
    }


    @Override
    public void askDetails(StoredDocumentList incomplete) {
        for (StoredDocument d : incomplete) {
            if (d.getDocument() instanceof TextDocument) {
                ((TextDocument) d.getDocument()).setAbstract(COMPLETED + "   " + d.getDocument().getTitle());
                setDetailTimestampToCurrent(d);
            }
        }
    }


    @Override
    protected StoredDocumentList process(QueryNodeBool node) {
        return null;
    }


    @Override
    protected boolean documentIsValid(StoredDocument stored) {
        return true;
    }

}