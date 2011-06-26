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

package de.unidue.inf.is.ezdl.dlcore.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import org.junit.Test;

import de.unidue.inf.is.ezdl.dlcore.AbstractTestBase;



/**
 * If any of these tests fail, the protocol version probably has to be increased
 * to make sure that clients comply with the changed protocol.
 * 
 * @author mjordan
 */
public class HttpMessagingUtilsTest extends AbstractTestBase {

    /**
     * Test method for
     * {@link de.unidue.inf.is.ezdl.dlcore.utils.HttpMessagingUtils#writeChunk(java.io.BufferedWriter, java.lang.String)}
     * . If this test fails, the protocol version probably has to be increased
     * to make sure that clients comply with the changed protocol.
     */
    @Test
    public void testWriteChunk() {
        checkWrite("1", "test");
        checkWrite("2", "test\r\nbla");
        checkWrite("3", "test\r\nbla\r\n");
        checkWrite("4", "<message>\n  <from></from>\n  <to></to>  " + "<content class=\"documentQueryAsk\">    "
                        + "<query>      " + "<condition class=\"baseCondition\">" + "        <sign>true</sign>"
                        + "        <field>YEAR</field>" + "        <predicate>=</predicate>"
                        + "        <fieldValue>2</fieldValue>" + "</condition>      " + "<wrapperList>"
                        + "        <string>Dummy</string>" + "</wrapperList>    " + "</query>    " + "<resultConfig>"
                        + "      <sortings class=\"java.util.Arrays$ArrayList\">"
                        + "        <a class=\"sorting-array\">" + "          <sorting>"
                        + "            <field>RSV</field>" + "            <order>DESCENDING</order>"
                        + "          </sorting>" + "        </a>" + "      </sortings>"
                        + "      <startDocNumber>0</startDocNumber>" + "      <endDocNumber>-1</endDocNumber>"
                        + "      <fields>" + "        <field>AUTHOR</field>" + "        <field>TITLE</field>"
                        + "        <field>YEAR</field>" + "        <field>RSV</field>" + "      </fields>"
                        + "    </resultConfig>" + "    <maxDurationMs>10000</maxDurationMs>"
                        + "    <requestId></requestId>" + "    <usingCache>false</usingCache>" + "  </content>"
                        + "  <requestId>e23b635a-8dcb-46db-8c52-7fd82f5f7cd6</requestId></message>\n");
    }


    /**
     * Test method for
     * {@link de.unidue.inf.is.ezdl.dlcore.utils.HttpMessagingUtils#readChunk(java.io.BufferedReader)}
     * . If this test fails, the protocol version probably has to be increased
     * to make sure that clients comply with the changed protocol.
     */
    @Test
    public void testReadChunk() {
        checkRead("1", "test");
        checkRead("2", "1\r2");
        checkRead("3", "1\n2");
        checkRead("4", "1\r\n2");
        checkRead("5", "1\n\r2");
        checkRead("6", "1\r\r\n2");
        checkRead("7", "1\r\n\n2");
        checkRead("8", "1\r\n\n\n2");
        checkRead("9", "1\r\n\n\n2");
        checkRead("10", "1\n\r\r\n2");
        checkRead("11", "1\n\r\r\n2");
    }


    private String checkWrite(String id, String message) {
        StringWriter stringWriter = new StringWriter();
        BufferedWriter writer = new BufferedWriter(stringWriter);

        try {
            HttpMessagingUtils.writeChunk(writer, message);
        }
        catch (IOException e) {
            e.printStackTrace();
            fail("Exception" + e);
        }
        String written = stringWriter.getBuffer().toString();
        assertTrue(id + ":\\r\\n only at the end: [" + written + "]", chunkTerminationOkay(written));

        return written;
    }


    private boolean chunkTerminationOkay(String written) {
        String[] out = written.split("[^\r]\r\n");
        return (out.length == 1) && (written.lastIndexOf("\r\n") == written.length() - 2);
    }


    private void checkRead(String id, String message) {
        String written = checkWrite(id, message);
        StringReader stringReader = new StringReader(written);
        BufferedReader reader = new BufferedReader(stringReader);
        String read = null;
        try {
            read = HttpMessagingUtils.readChunk(reader);
        }
        catch (IOException e) {
            e.printStackTrace();
            fail("Exception" + e);
        }
        assertEquals(id + ": read as written", message, read);
        getLogger().debug("[" + read + "]");
    }
}
