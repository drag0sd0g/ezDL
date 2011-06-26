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

package de.unidue.inf.is.ezdl.dlcore.data.dldata;

import de.unidue.inf.is.ezdl.dlcore.data.Mergeable;



/**
 * Immutable representation of a simple Term within ezDL.
 * 
 * @author kriewel
 */
public class Term extends AbstractDLObject {

    private static final long serialVersionUID = -4861030755187953081L;

    private final String theTerm;


    public Term(String term) {
        theTerm = term;
    }


    /**
     * Note: this method might be extended by using an actual similarity method
     * for strings (e.g., edit distance).
     */
    @Override
    public boolean isSimilar(Mergeable obj) {
        if (obj instanceof Term) {
            return theTerm.equals(obj);
        }
        return false;
    }


    public String getTerm() {
        return theTerm;
    }


    @Override
    public String asString() {
        return getTerm();
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((theTerm == null) ? 0 : theTerm.hashCode());
        return result;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Term other = (Term) obj;
        if (theTerm == null) {
            if (other.theTerm != null) {
                return false;
            }
        }
        else if (!theTerm.equals(other.theTerm)) {
            return false;
        }
        return true;
    }
}
