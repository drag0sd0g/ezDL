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

/**
 * 
 */
package de.unidue.inf.is.ezdl.dlcore.message.content;

import de.unidue.inf.is.ezdl.dlcore.data.query.ResultDocumentList;



/**
 * @author mj
 */
public abstract class AbstractResultDocumentTell implements MessageContent {

    private static final long serialVersionUID = -6507249491871827085L;

    private ResultDocumentList results;


    public AbstractResultDocumentTell(ResultDocumentList results) {
        if (results == null) {
            throw new IllegalArgumentException("results must not be null");
        }
        this.results = results;
    }


    /**
     * @return the socuments
     */
    public ResultDocumentList getResults() {
        return results;
    }


    /**
     * Returns the contents of this object in the form
     * "{objectname [resultlist]}" where objectname is the String passed.
     * 
     * @param name
     *            the name of the object
     * @return a fancy string representation
     */
    public String toInnerString(String name) {
        StringBuffer out = new StringBuffer();
        out.append('{').append(name).append(' ');
        out.append(getResults());
        out.append('}');
        return out.toString();
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((results == null) ? 0 : results.hashCode());
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
        AbstractResultDocumentTell other = (AbstractResultDocumentTell) obj;
        if (results == null) {
            if (other.results != null) {
                return false;
            }
        }
        else if (!results.equals(other.results)) {
            return false;
        }
        return true;
    }

}
