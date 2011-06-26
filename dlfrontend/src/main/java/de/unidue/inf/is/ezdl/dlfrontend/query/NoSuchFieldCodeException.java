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

package de.unidue.inf.is.ezdl.dlfrontend.query;

/**
 * This exeption is thrown by a <EM>QueryRegistry</EM>.<BR>
 * It is throws if there is no entry for the requested field in one of its
 * mappings. An alternative field may be given, which should be used instead.
 * 
 * @see QueryRegistry
 */
public class NoSuchFieldCodeException extends Exception {

    private static final long serialVersionUID = 6706654865150548602L;

    public int alternativeField = 0;


    /**
     * Construct a new exception. No further reason for the error is given.
     */
    public NoSuchFieldCodeException() {
        super();
    }


    /**
     * Construct a new exception. The reason for the error should be specified
     * in the <EN>message</EM>
     * 
     * @param message
     *            java.lang.String
     */
    public NoSuchFieldCodeException(String message) {
        super(message);
    }


    /**
     * Return an alternative fieldcode. This field should be used instead of the
     * requested one.
     * 
     * @return int
     */
    public int getAlternativeField() {
        return alternativeField;
    }


    /**
     * Specifie an alternative fieldcode. The field given should be used instead
     * of the requested one.
     * 
     * @param newAlternativeField
     *            int
     */
    public void setAlternativeField(int newAlternativeField) {
        alternativeField = newAlternativeField;
    }
}
