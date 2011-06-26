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



public class StringCoder {

    private StringCodingStrategy strategy;


    /**
     * Constructor.
     */
    public StringCoder(StringCodingStrategy strategy) {
        this.strategy = strategy;
    }


    /**
     * Encodes a object using the internal strategy instance.
     * 
     * @param object
     *            the object to encode.
     * @return the string representation of the object
     * @throws IOException
     */
    public String encode(Object object) throws IOException {
        return strategy.encode(object);
    }


    /**
     * Decodes a string using the internal strategy instance.
     * 
     * @param string
     *            a string that was encoded using the encode() method.
     * @return the object or null if the conversion failed
     * @throws IOException
     */
    public Object decode(String string) throws IOException {
        return strategy.decode(string);
    }

}
