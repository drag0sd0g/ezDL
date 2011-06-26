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
 * An export result in text form.
 * 
 * @author mjordan
 */
public class ExportResultText implements ExportResult {

    private String data;


    public ExportResultText(String string) {
        this.data = string;
    }


    public ExportResultText(StringBuilder builder) {
        this.data = builder.toString();
    }


    @Override
    public byte[] asByteArray() {
        return data.getBytes();
    }


    @Override
    public String asString() {
        return data;
    }


    @Override
    public boolean isBinary() {
        return false;
    }


    void setData(String text) {
        this.data = text;
    }


    @Override
    public String toString() {
        return asString();
    }
}
