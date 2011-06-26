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

package de.unidue.inf.is.ezdl.dlfrontend.converter;

/**
 * The result of an export.
 * 
 * @author mjordan
 */
public interface ExportResult {

    /**
     * A string that represents the exported data or null if the data is binary.
     * 
     * @return the data or null if the data is binary
     */
    String asString();


    /**
     * A byte array that represents the exported data.
     * 
     * @return the byte array
     */
    byte[] asByteArray();


    /**
     * Returns true if the export result is in binary format. Else false.
     * 
     * @return true if the export result is in binary format. Else false.
     */
    boolean isBinary();
}
