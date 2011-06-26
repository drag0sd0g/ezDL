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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.Normalizer;
import java.util.Iterator;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import de.unidue.inf.is.ezdl.dlcore.data.dldata.MergeableList;



public final class StringUtils {

    private static Logger logger = Logger.getLogger(StringUtils.class);

    private static final long KILOBYTE = 1024;
    private static final long MEGABYTE = KILOBYTE * 1024;
    private static final long GIGABYTE = MEGABYTE * 1024;


    private StringUtils() {
    }


    public static boolean isEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }


    /**
     * Removes the ' from a string.
     * 
     * @param value
     *            string to be encoded
     * @return encoded string
     */

    public static String encodeValue(String value) {
        try {
            return value.replaceAll("'", "");
        }
        catch (Exception exception) {
            logger.error("Exception caught: ", exception);
        }
        return "";
    }


    /**
     * Given an amount of bytes, return a string representation in Bytes,
     * Kilobytes, Megabytes or Gigabytes Examples: Given 1024 bytes -> "1KB"
     * Given 1536 bytes -> 1.5KB"
     * 
     * @param size
     *            amount of bytes
     * @return String representation in Bytes, Kilobytes, Megabytes or Gigabytes
     */
    public static String fromByteToMegaOrGiga(long size) {
        if (size < KILOBYTE) {
            return size + " Bytes";
        }
        else if (size < MEGABYTE) {
            return toString((double) size / KILOBYTE, 2) + " KB";
        }
        else if (size < GIGABYTE) {
            return toString((double) size / MEGABYTE, 2) + " MB";
        }
        else {
            return toString((double) size / GIGABYTE, 2) + " GB";
        }
    }


    /**
     * Returns a double value as a string with a given number of decimal digits.
     * 
     * @param value
     *            double value
     * @param numberOfDecimals
     *            number of decimal digits
     * @return string with a given number of decimal digits
     */
    public static String toString(double value, int numberOfDecimals) {
        DecimalFormat df = new DecimalFormat("#.#");
        df.setMinimumFractionDigits(numberOfDecimals);
        return df.format(value);
    }


    /**
     * Present the {@link MergeableList} as {@link String}.
     * 
     * @param dataList
     *            the {@link MergeableList} Object.
     * @param maxItems
     *            the max number of items to use in {@link #toString()}
     * @return a string from a {@link MergeableList}
     * @throws NullPointerException
     *             if dataList is null.
     */
    public static String toString(MergeableList<?> dataList, String header, int maxItems) {
        StringBuilder out = new StringBuilder("{" + header + " [");
        buildString(dataList, maxItems, out);

        out.append("]}");
        return out.toString();
    }


    // private static String toStringSimple(MergeableList<?> dataList, int
    // maxItems) {
    // StringBuilder out = new StringBuilder("[");
    // buildString(dataList, maxItems, out);
    // out.append("]");
    // return out.toString();
    // }

    private static void buildString(MergeableList<?> dataList, int maxItems, StringBuilder out) {
        int number = maxItems;
        if (dataList.size() <= number) {
            number = dataList.size();
        }

        for (int i = 0; (i < number); i++) {
            out.append(dataList.get(i).toString());
            if (i < (number - 1)) {
                out.append(", ");
            }
        }

        if (dataList.size() > maxItems) {
            out.append(" (").append(dataList.size()).append(" results total)");
        }
    }


    public static String normalize(String s) {
        String result = Normalizer.normalize(s, java.text.Normalizer.Form.NFD);
        return result.replaceAll("ß", "ss").replaceAll("[^\\p{ASCII}]", "");
    }


    /**
     * Checks if a string contains non Ascii caracters.
     * 
     * @param s
     *            the string to check
     * @return if the string contains non Ascii caracters
     */
    public static boolean containsNonAsciiCharacters(String s) {
        return !s.matches("\\p{ASCII}*");
    }


    public static String utf8Decode(String toDecode) {
        String decoded = toDecode;
        try {
            decoded = decode(toDecode, "UTF-8");
        }
        catch (IllegalArgumentException iae) {
            // Do nothing
        }
        catch (UnsupportedEncodingException uee) {
            logger.error("Could not encode string: " + toDecode, uee);
        }
        catch (Exception e) {
            logger.error("Could not encode string: " + toDecode, e);
        }
        return decoded;
    }


    /*
     * URLDecoder.decode() is of course, for decoding encoded URIs, so it will
     * barf if it encounters something not allowed in an encoded URI, like e.g.
     * an lonesome '%' -- let's just assume tht a '%' followed by anything that
     * is not a two digit hexadecimal code is meant to be an actual '%'
     */
    public static String decode(String s, String enc) throws UnsupportedEncodingException {

        boolean needToChange = false;
        int numChars = s.length();
        StringBuffer sb = new StringBuffer(numChars > 500 ? numChars / 2 : numChars);
        int i = 0;

        if (enc.isEmpty()) {
            throw new UnsupportedEncodingException("URLDecoder: empty string enc parameter");
        }

        char c;
        byte[] bytes = null;
        while (i < numChars) {
            c = s.charAt(i);
            SWITCH: switch (c) {
                case '+':
                    sb.append(' ');
                    i++;
                    needToChange = true;
                    break;
                case '%':
                    if (bytes == null) {
                        bytes = new byte[(numChars - i) / 3];
                    }
                    int pos = 0;
                    while (((i + 2) < numChars) && (c == '%')) {
                        try {
                            bytes[pos++] = (byte) Integer.parseInt(s.substring(i + 1, i + 3), 16);
                        }
                        catch (NumberFormatException e) {
                            // this wasn't a byte encoding after all
                            sb.append(s.substring(i, i += 3));
                            break SWITCH;
                        }
                        i += 3;
                        if (i < numChars) {
                            c = s.charAt(i);
                        }
                    }
                    sb.append(new String(bytes, 0, pos, enc));

                    /*
                     * A trailing, incomplete byte encoding like '%c' ... we're
                     * gonna assume that this is meant to be the actual
                     * characters, and won't throw an exception
                     */
                    if ((i < numChars) && (c == '%')) {
                        sb.append(s.substring(i + 1));
                    }
                    needToChange = true;
                    break;
                default:
                    sb.append(c);
                    i++;
                    break;
            }
        }

        return (needToChange ? sb.toString() : s);
    }


    /**
     * Shortens a string, e.g. Apfelkuchen -> Apfelk...
     * 
     * @param string
     *            The string to shorten
     * @param maxLength
     *            The max length after the shortening
     * @return The shortended string
     */
    public static String shortenString(String string, int maxLength) {
        if (maxLength < 3) {
            throw new IllegalArgumentException("maxLength must be > 2");
        }
        if (string.length() <= maxLength) {
            return string;
        }
        else {
            return string.substring(0, maxLength - 3).trim() + "...";
        }
    }


    /**
     * Returns true if all characters in the String are upper case.
     * 
     * @param str
     *            the String to check
     * @return true, if all characters are upper case. Else false.
     */
    public static boolean allCaps(String str) {
        int charCount = str.length();
        for (int i = 0; (i < charCount); i++) {
            char ch = str.charAt(i);
            if (!Character.isUpperCase(ch)) {
                return false;
            }
        }
        return true;
    }


    /**
     * Creates a single {@link String} with a seperator between all strings.
     * 
     * @param list
     *            list of string values
     * @param separator
     *            a seperator string
     * @return a single {@link String} with a seperator between all strings
     */
    public static String join(List<String> list, String separator) {
        StringBuilder sb = new StringBuilder();
        Iterator<String> it = list.iterator();
        while (it.hasNext()) {
            sb.append(it.next());
            if (it.hasNext()) {
                sb.append(separator);
            }
        }
        return sb.toString();
    }


    /**
     * Compresses a string.
     * 
     * @param s
     *            The string to compress
     * @return The compressed string
     */
    public static String compress(String s) {
        ByteArrayOutputStream baos = null;
        try {
            byte[] input = s.getBytes("UTF-8");
            Deflater compresser = new Deflater();
            compresser.setLevel(Deflater.BEST_COMPRESSION);
            compresser.setInput(input);
            compresser.finish();
            baos = new ByteArrayOutputStream();
            while (!compresser.finished()) {
                byte[] output = new byte[1024];
                int compressedDataLength = compresser.deflate(output);
                baos.write(output, 0, compressedDataLength);
            }
            baos.flush();
            return Base64.encodeBase64String(baos.toByteArray());

        }
        catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage(), e);
        }
        catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        finally {
            ClosingUtils.close(baos);
        }
        return "";
    }


    /**
     * Decompresses a string.
     * 
     * @param s
     *            The string to decompress
     * @return The decompressed string
     */
    public static String decompress(String s) {
        ByteArrayOutputStream baos = null;
        try {
            Inflater ifl = new Inflater();
            ifl.setInput(Base64.decodeBase64(s));

            baos = new ByteArrayOutputStream();
            while (!ifl.finished()) {
                byte[] buff = new byte[1024];
                int count = ifl.inflate(buff);
                baos.write(buff, 0, count);
            }
            baos.flush();
            byte[] output = baos.toByteArray();

            return new String(output, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage(), e);
        }
        catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        catch (DataFormatException e) {
            logger.error(e.getMessage(), e);
        }
        finally {
            ClosingUtils.close(baos);
        }
        return "";
    }
}
