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

package de.unidue.inf.is.ezdl.dlcore.security;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.log4j.Logger;



public final class Hash {

    private static final String ENCODING = "UTF-8";

    private static Logger logger = Logger.getLogger(Hash.class);


    private String bytes2String(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            String hexString = Integer.toHexString(0x00FF & b);
            if (hexString.length() == 1) {
                sb.append("0");
            }
            sb.append(hexString);
        }
        return sb.toString();
    }


    public String sha1(String string) {
        try {
            byte[] digest;
            byte[] bytes = string.getBytes(ENCODING);
            synchronized (Hash.class) {
                MessageDigest sha1 = MessageDigest.getInstance("SHA1");
                digest = sha1.digest(bytes);
            }
            return bytes2String(digest);
        }
        catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage(), e);
            throw new UnsupportedOperationException();
        }
        catch (NoSuchAlgorithmException e) {
            logger.error(e.getMessage(), e);
            throw new UnsupportedOperationException();
        }
    }

}
