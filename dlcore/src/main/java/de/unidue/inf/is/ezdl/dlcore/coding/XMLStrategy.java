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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.XStreamException;

import de.unidue.inf.is.ezdl.dlcore.utils.ClosingUtils;



/**
 * Object coding strategy that uses XML for the serialization.
 * 
 * @author mjordan
 */
public class XMLStrategy implements StringCodingStrategy {

    private static final String ENCODING = "UTF-8";

    private XStream xStream = new XStream();


    @Override
    public String encode(Object object) throws IOException {
        ByteArrayOutputStream outputStream = null;
        try {
            outputStream = new ByteArrayOutputStream();
            Writer writer = new OutputStreamWriter(outputStream, ENCODING);
            xStream.toXML(object, writer);
            return outputStream.toString(ENCODING);
        }
        catch (XStreamException e) {
            throw new IOException(e);
        }
        finally {
            ClosingUtils.close(outputStream);
        }
    }


    @Override
    public Object decode(String string) throws IOException {
        ByteArrayInputStream inputStream = null;
        try {
            inputStream = new ByteArrayInputStream(string.getBytes(ENCODING));
            Reader reader = new InputStreamReader(inputStream, ENCODING);
            return xStream.fromXML(reader);
        }
        catch (XStreamException e) {
            throw new IOException(e);
        }
        finally {
            ClosingUtils.close(inputStream);
        }
    }

}
