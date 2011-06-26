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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;



/**
 * Utility methods for I/O handling.
 */
public final class IOUtils {

    private static final int BUFFER_SIZE = 1024;


    /**
     * Returns a {@link URLConnection} specified by a given url string.
     * 
     * @param urlString
     *            A url as string
     * @return A url connection
     * @throws IOException
     *             If an IO exception occurs
     */
    public static URLConnection getConnection(final String urlString) throws IOException {
        final URL url = new URL(urlString);
        final URLConnection connection = url.openConnection();
        return connection;
    }


    /**
     * Returns a {@link URLConnection} specified by a given {@link URI}.
     * 
     * @param uri
     *            A {@link URI}
     * @return A url connection
     * @throws IOException
     *             If an IO exception occurs
     */
    public static URLConnection getConnection(final URI uri) throws IOException {
        final URL url = uri.toURL();
        final URLConnection connection = url.openConnection();
        return connection;
    }


    /**
     * Reads a string from a given {@link URLConnection} with a given charset
     * encoding.
     * 
     * @param connection
     *            A url connection
     * @param charset
     *            A charset as string, e.g. "UTF-8"
     * @return A String read from a given url connection
     */
    public static String readURL(final URLConnection connection, final String charset) throws IOException {
        final StringBuilder builder = new StringBuilder();
        final byte[] array = readBinary(connection);
        builder.append(new String(array, charset));
        return builder.toString();
    }


    /**
     * Reads a byte array from a given {@link URLConnection}.
     * 
     * @param connection
     *            A url connection
     * @return The byte array read from a given url connection
     */
    public static byte[] readBinary(final URLConnection connection) throws IOException {
        ByteArrayOutputStream baos = null;
        InputStream input = null;
        try {
            input = connection.getInputStream();
            baos = readBinaryStream(input);
            return baos.toByteArray();
        }
        finally {
            ClosingUtils.close(baos, input);
        }
    }


    /**
     * Reads a byte array from a given {@link URLConnection}.
     * 
     * @param connection
     *            A url connection
     * @return The byte array read from a given url connection
     */
    public static byte[] readBinary(final File file) throws IOException {
        ByteArrayOutputStream baos = null;
        InputStream input = null;
        try {
            input = new FileInputStream(file);
            baos = readBinaryStream(input);
            return baos.toByteArray();
        }
        finally {
            ClosingUtils.close(baos, input);
        }
    }


    /**
     * Reads a byte array from a given {@link InputStream}.
     * 
     * @return The byte array read from the stream
     */
    public static byte[] readBinary(InputStream input) throws IOException {
        ByteArrayOutputStream baos = null;
        try {
            baos = readBinaryStream(input);
            return baos.toByteArray();
        }
        finally {
            ClosingUtils.close(baos, input);
        }
    }


    private static ByteArrayOutputStream readBinaryStream(InputStream input) throws IOException {
        ByteArrayOutputStream baos;
        baos = new ByteArrayOutputStream(BUFFER_SIZE);
        final byte[] array = new byte[BUFFER_SIZE];
        int read;
        while ((read = input.read(array)) != -1) {
            baos.write(array, 0, read);
        }
        baos.flush();
        baos.close();
        return baos;
    }


    /**
     * Reads the contents of a reader into a string.
     * 
     * @param in
     *            the reader
     * @return the string
     */
    public static String readBufferAsString(BufferedReader in) {
        StringBuffer out = new StringBuffer();
        String read = null;
        try {
            while ((read = in.readLine()) != null) {
                out.append(read).append('\n');
            }
        }
        catch (IOException e) {
        }
        return out.toString();
    }


    public static String readInputStreamAsString(InputStream s) {
        return readBufferAsString(new BufferedReader(new InputStreamReader(s)));
    }

}
