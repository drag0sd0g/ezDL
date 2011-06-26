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

import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;

import de.unidue.inf.is.ezdl.dlbackend.ServiceNames;
import de.unidue.inf.is.ezdl.dlbackend.data.StoredDocument;
import de.unidue.inf.is.ezdl.dlbackend.data.StoredDocumentList;
import de.unidue.inf.is.ezdl.dlbackend.mock.MockAgent;
import de.unidue.inf.is.ezdl.dlbackend.wrappers.SourceID;
import de.unidue.inf.is.ezdl.dlcore.AbstractTestBase;
import de.unidue.inf.is.ezdl.dlcore.data.query.DocumentQuery;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeBool;
import de.unidue.inf.is.ezdl.dlcore.data.wrappers.WrapperInfo;
import de.unidue.inf.is.ezdl.dlwrapper.Wrapper;



/**
 * @author mj
 */
public class AbstractWrapperTest extends AbstractTestBase {

    private class TestWrapper extends AbstractWrapper {

        private static final String DL_ID = "test";

        private final SourceID SOURCE_ID = new SourceID(DL_ID, "x");


        @Override
        public void askDetails(StoredDocumentList incomplete) {
        }


        @Override
        public String getServiceName() {
            ServiceNames.getServiceNameForDL(DL_ID);
            return null;
        }


        @Override
        public SourceID getSourceID() {
            return SOURCE_ID;
        }


        @Override
        protected boolean documentIsValid(StoredDocument stored) {
            return true;
        }


        @Override
        protected StoredDocumentList process(DocumentQuery query) {
            // TODO Auto-generated method stub
            return null;
        }


        @Override
        protected StoredDocumentList process(QueryNodeBool conjunction) {
            // TODO Auto-generated method stub
            return null;
        }

    }


    @Test
    public void testGetWrapperInfo() {
        MockAgent agent = new MockAgent();
        Properties p = new Properties();
        p.put("info.category", "test");
        p.put("info.remotename", "TestDL");
        p.put("info.category.en", "Test");
        p.put("info.description.en", "Test description");
        agent.init("agentname", p);
        Wrapper testWrapper = new TestWrapper();
        testWrapper.init(agent, null);
        WrapperInfo info = testWrapper.getWrapperInfo();
        Assert.assertNotNull("got object", info);
        Assert.assertEquals("Category okay", "test", info.getCategoryId());
        Assert.assertEquals("remote DL", "TestDL", info.getRemoteName());
        Assert.assertNotNull("large icon", info.getLargeIconData());
        Assert.assertNotNull("small icon", info.getSmallIconData());
    }


    @Test
    public void testGetWrapperInfoMissingIcons() {
        MockAgent agent = new MockAgent();
        Properties p = new Properties();
        p.put("info.category", "test");
        p.put("info.remotename", "TestDL");
        p.put("info.category.en", "Test");
        p.put("info.description.en", "Test description");
        agent.init("agent_has_no_icons", p);
        Wrapper testWrapper = new TestWrapper();
        testWrapper.init(agent, null);
        WrapperInfo info = testWrapper.getWrapperInfo();
        Assert.assertNotNull("got object", info);
        Assert.assertEquals("Category okay", "test", info.getCategoryId());
        Assert.assertEquals("remote DL", "TestDL", info.getRemoteName());
        Assert.assertNull("large icon", info.getLargeIconData());
        Assert.assertNull("small icon", info.getSmallIconData());
    }
}
