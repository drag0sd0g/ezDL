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

package de.unidue.inf.is.ezdl.dlcore.message.content;

import de.unidue.inf.is.ezdl.dlcore.ErrorConstants;



public class ErrorNotify implements MessageContent {

    private static final long serialVersionUID = 72277930818085727L;

    private ErrorConstants error;

    private String description;


    public ErrorNotify(ErrorConstants error) {
        this.error = error;
    }


    public ErrorNotify(String description) {
        this.description = description;
    }


    /**
     * @return the error
     */
    public ErrorConstants getError() {
        return error;
    }


    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((error == null) ? 0 : error.hashCode());
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
        ErrorNotify other = (ErrorNotify) obj;
        if (description == null) {
            if (other.description != null) {
                return false;
            }
        }
        else if (!description.equals(other.description)) {
            return false;
        }
        if (error == null) {
            if (other.error != null) {
                return false;
            }
        }
        else if (!error.equals(other.error)) {
            return false;
        }
        return true;
    }


    @Override
    public String toString() {
        StringBuffer out = new StringBuffer();
        out.append("{ErrorNotify ");
        out.append(error);
        out.append(": ");
        out.append(description);
        out.append("}");
        return out.toString();
    }

}
