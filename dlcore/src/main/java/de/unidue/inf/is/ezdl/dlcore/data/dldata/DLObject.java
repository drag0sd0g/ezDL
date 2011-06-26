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

import java.io.Serializable;

import de.unidue.inf.is.ezdl.dlcore.data.Mergeable;



/**
 * Root class of all objects that can be stored in a personal store.
 * <p>
 * There can be multiple kinds of personal storage mechanisms in ezDL, most
 * notably the personal library. Objects that can be stored in these places have
 * to implement the {@link DLObject} interface in order to have a common ground
 * for handling them.
 */

public interface DLObject extends Serializable, Mergeable {

    /**
     * The Object ID of this object.
     * 
     * @return the OID
     */
    String getOid();


    /**
     * Returns a human-readable string representation of this object.
     * <p>
     * This representation is absolutely not intended for debugging purposes so
     * there must not be any fancy formatting in the output of this method.
     * 
     * @return a human-readable string representation
     */
    String asString();

}
