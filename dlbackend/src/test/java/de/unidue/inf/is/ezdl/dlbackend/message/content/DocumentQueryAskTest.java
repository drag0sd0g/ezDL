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

package de.unidue.inf.is.ezdl.dlbackend.message.content;

import java.util.Arrays;

import junit.framework.Assert;

import org.junit.Test;

import de.unidue.inf.is.ezdl.dlbackend.AbstractBackendTestBase;
import de.unidue.inf.is.ezdl.dlbackend.message.Message;
import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;
import de.unidue.inf.is.ezdl.dlcore.data.query.DefaultQuery;
import de.unidue.inf.is.ezdl.dlcore.data.query.DocumentQuery;
import de.unidue.inf.is.ezdl.dlcore.data.query.ResultConfiguration;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeCompare;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeCompare.Predicate;
import de.unidue.inf.is.ezdl.dlcore.message.content.DocumentQueryAsk;



public class DocumentQueryAskTest extends AbstractBackendTestBase {

    @Test
    public void testEquals() {

        Message msg1 = getDocumentAsk();
        Message msg2 = getDocumentAsk();

        Assert.assertEquals(msg1, msg2);
    }


    private Message getDocumentAsk() {
        QueryNodeCompare node = new QueryNodeCompare(Field.AUTHOR, Predicate.EQ, "fuhr");
        DocumentQuery query = new DocumentQuery(new DefaultQuery(node), Arrays.asList("Dummy1"));

        Message message = new Message();
        message.setFrom("client");
        message.setTo("DA");
        ResultConfiguration resultConfig = new ResultConfiguration();
        DocumentQueryAsk content = new DocumentQueryAsk(query, resultConfig);
        content.setMaxDurationMs(10000);
        message.setContent(content);
        message.setRequestId("reqid");
        return message;
    }

}
