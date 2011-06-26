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

package de.unidue.inf.is.ezdl.dlcore.coding;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

import de.unidue.inf.is.ezdl.dlcore.AbstractTestBase;
import de.unidue.inf.is.ezdl.dlcore.data.query.ResultDocumentList;
import de.unidue.inf.is.ezdl.dlcore.message.MTAMessage;
import de.unidue.inf.is.ezdl.dlcore.message.content.DocumentQueryResultTell;
import de.unidue.inf.is.ezdl.dlcore.message.content.MessageContent;
import de.unidue.inf.is.ezdl.dlcore.utils.XmlUtils;



public class XMLStrategyTest extends AbstractTestBase {

    private final StringCodingStrategy strategy = new XMLStrategy();


    @Before
    public void init() {
    }


    @Test
    public void testEncodeDecode() throws IOException {
        testMessageEncodeDecode("null", null, "");
        testMessageEncodeDecode("ML", new DocumentQueryResultTell(new ResultDocumentList()), "id");
    }


    private void testMessageEncodeDecode(String id, MessageContent c, String r) throws IOException {
        MTAMessage message = new MTAMessage();
        message.setContent(c);
        message.setRequestId(r);
        String test = strategy.encode(message);

        Document doc = XmlUtils.xmlDoc(test);
        Assert.assertNotNull("document null means XML invalid: " + id, doc);

        MTAMessage decodedMessage = (MTAMessage) strategy.decode(test);
        Assert.assertEquals("Decoded equals encoded " + id, message.toString(), decodedMessage.toString());
    }

}
