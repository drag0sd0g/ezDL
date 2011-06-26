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
import de.unidue.inf.is.ezdl.dlcore.utils.merge.MergeUtils;



/**
 * This is a easy way to implement a new DLObject. Since some methods do have a
 * default behavior we will provide it here that other tools can rely on it.
 * 
 * @author Jens Kapitza
 */
public abstract class AbstractDLObject implements DLObject {

    private static final long serialVersionUID = 7588944601579608856L;


    @Override
    public void merge(Mergeable dlObject) {
        MergeUtils.merge(this, dlObject);
    }


    @Override
    public String getOid() {
        return String.valueOf(hashCode());
    }


    @Override
    public String toString() {
        return asString();
    }

}
