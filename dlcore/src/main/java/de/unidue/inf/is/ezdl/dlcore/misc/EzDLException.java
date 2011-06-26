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

package de.unidue.inf.is.ezdl.dlcore.misc;

/**
 * Allgemeine Klasse für eine Exception in ezDL.
 */
public class EzDLException extends Exception {

    private static final long serialVersionUID = 4005335294745502818L;


    /**
     * Standard-Konstruktor.
     */
    public EzDLException() {
        super();
    }


    /**
     * Standard-Konstruktor.
     */
    public EzDLException(Throwable e) {
        super(e);
    }


    /**
     * Standard-Konstruktor.
     */
    public EzDLException(String s) {
        super(s);
    }

}
