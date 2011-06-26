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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.SocketException;



/**
 * Serves to encapsulate reading and writing message chunks over a reader/writer
 * connection (e.g. an HTTP socket).
 * <p>
 * A chunk in this context is a part of the input of the stream that is
 * recognized as a whole.
 * <p>
 * The separator between chunks is "\r\n". If "\r\n" is to be sent, it is
 * transformed into "\r\r\n".
 * 
 * @author mjordan
 */
public class HttpMessagingUtils {

    private static final String CRLF = "\r\n";


    /**
     * Reads a chunk from the given Reader.
     * 
     * @param reader
     *            the reader to read from
     * @return the next chunk or null
     * @throws IOException
     *             if an exception occurs (e.g. socket closed)
     */
    public static String readChunk(BufferedReader reader) throws IOException {
        StringBuilder out = new StringBuilder();
        boolean cont = true;
        boolean escapeOn = false;
        while (cont) {
            int cint = reader.read();
            if (cint == -1) {
                cont = false;
                throw new SocketException();
            }
            else {
                char r = (char) cint;
                switch (r) {
                    case '\r': {
                        if (escapeOn) {
                            out.append('\r');
                            escapeOn = false;
                        }
                        else {
                            escapeOn = true;
                        }
                        break;
                    }
                    case '\n': {
                        if (escapeOn) {
                            cont = false;
                        }
                        else {
                            out.append('\n');
                        }
                        break;
                    }
                    default: {
                        if (escapeOn) {
                            out.append('\r');
                        }
                        escapeOn = false;
                        out.append(r);
                    }
                }
            }
        }
        return out.toString();
    }


    public static void writeChunk(BufferedWriter writer, String chunk) throws IOException {
        for (char c : chunk.toCharArray()) {
            switch (c) {
                case '\r': {
                    writer.write("\r\r");
                    break;
                }
                default: {
                    writer.write(c);
                }
            }
        }
        writer.write(CRLF);
        writer.flush();
    }
}
