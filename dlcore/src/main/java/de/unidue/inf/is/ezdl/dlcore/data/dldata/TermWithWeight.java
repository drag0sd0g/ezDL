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



public class TermWithWeight extends AbstractDLObject {

    private static final long serialVersionUID = -4861030755187953081L;

    private final String theTerm;
    private double weight;


    public TermWithWeight(String term, double weight) {
        theTerm = term;
        this.weight = weight;
    }


    /**
     * Note: this method might be extended by using an actual similarity method
     * for strings (e.g., edit distance).
     */
    @Override
    public boolean isSimilar(Mergeable obj) {
        if (obj instanceof TermWithWeight) {
            return theTerm.equals(((TermWithWeight) obj).theTerm);
        }
        return false;
    }


    public String getTerm() {
        return theTerm;
    }


    public double getWeight() {
        return weight;
    }


    @Override
    public String asString() {
        return getTerm();
    }

}
