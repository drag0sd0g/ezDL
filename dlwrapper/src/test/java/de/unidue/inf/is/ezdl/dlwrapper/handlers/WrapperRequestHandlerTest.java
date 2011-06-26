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

package de.unidue.inf.is.ezdl.dlwrapper.handlers;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import de.unidue.inf.is.ezdl.dlbackend.AbstractBackendTestBase;
import de.unidue.inf.is.ezdl.dlbackend.data.StoredDocument;
import de.unidue.inf.is.ezdl.dlbackend.data.StoredDocumentList;
import de.unidue.inf.is.ezdl.dlbackend.message.Message;
import de.unidue.inf.is.ezdl.dlbackend.wrappers.SourceID;
import de.unidue.inf.is.ezdl.dlcore.cache.Cache;
import de.unidue.inf.is.ezdl.dlcore.cache.MapCache;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.Document;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.Person;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.PersonList;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.TextDocument;
import de.unidue.inf.is.ezdl.dlcore.data.query.DocumentQuery;
import de.unidue.inf.is.ezdl.dlcore.data.query.QueryFactory;
import de.unidue.inf.is.ezdl.dlcore.data.query.ResultConfiguration;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeBool;
import de.unidue.inf.is.ezdl.dlcore.data.wrappers.WrapperInfo;
import de.unidue.inf.is.ezdl.dlcore.message.content.DocumentQueryAsk;
import de.unidue.inf.is.ezdl.dlwrapper.Wrapper;
import de.unidue.inf.is.ezdl.dlwrapper.WrapperMapper;
import de.unidue.inf.is.ezdl.dlwrapper.wrappers.AbstractWrapper;



public class WrapperRequestHandlerTest extends AbstractBackendTestBase {

    class MockWrapperMapper extends WrapperMapper {

        Properties props = new Properties();


        public MockWrapperMapper() {
            props.setProperty("wrapperclass", "de.unidue.inf.is.ezdl.dlwrapper.WrapperRequestTest.MockWrapper");
            props.setProperty("query", "true");
        }


        @Override
        public String agentName() {
            return "wrappermapper";
        }


        public List<Message> messagesSent = new LinkedList<Message>();


        @Override
        public void send(Message message) {
            messagesSent.add(message);
        }


        @Override
        public Wrapper getNewWrapperInstance() {
            return wrapper;
        }


        @Override
        public int maxSessionCount() {
            return 1;
        }
    }


    class MockWrapper extends AbstractWrapper {

        @Override
        public WrapperInfo getWrapperInfo() {
            return new WrapperInfo();
        }


        @Override
        protected StoredDocumentList process(DocumentQuery query) {
            StoredDocumentList list = new StoredDocumentList();
            Document data = new TextDocument();
            Person author = new Person("Ben Goldacre");
            PersonList authors = new PersonList(Arrays.asList(author));
            data.setAuthorList(authors);
            data.setTitle("Bad science");
            data.setYear(2008);
            StoredDocument stored = new StoredDocument(data);
            list.add(stored);
            return list;
        }


        @Override
        public void askDetails(StoredDocumentList incomplete) {
            // Not tested anyway
        }


        @Override
        protected StoredDocumentList process(QueryNodeBool conjunction) {
            return null;
        }


        @Override
        public SourceID getSourceID() {
            return new SourceID("", "");
        }


        @Override
        protected boolean documentIsValid(StoredDocument stored) {
            return true;
        }


        @Override
        public String getServiceName() {
            return "wrapper";
        }

    }


    WrapperRequestHandler request;
    MockWrapperMapper wrapperMapper;
    Wrapper wrapper;
    Cache cache = new MapCache();


    @Before
    public void init() {
        wrapperMapper = new MockWrapperMapper();

        wrapper = new MockWrapper();
        wrapper.init(wrapperMapper, cache);

        request = new WrapperRequestHandler();
        request.init("requestid", wrapperMapper);
    }


    @Test
    public void testWrapper() {

        ResultConfiguration resultConfig = new ResultConfiguration();
        DocumentQuery query = QueryFactory.getQuery1();
        DocumentQueryAsk content = new DocumentQueryAsk(query, resultConfig);
        Message message = new Message("from", "to", content, "reqid");

        request.work(message);

        Assert.assertEquals("Exactly 1 msg sent", 1, wrapperMapper.messagesSent.size());
        getLogger().debug(wrapperMapper.messagesSent);
        Assert.assertEquals(
                        "[From: to / To: from / Request: reqid /  Message: {DocumentQueryStoredTell {DataList [{StoredDocument from [] missed: [] doc: {PersonList [Ben Goldacre]}: ''Bad science'' (2008)}]}}]",
                        wrapperMapper.messagesSent.toString());
    }
}
