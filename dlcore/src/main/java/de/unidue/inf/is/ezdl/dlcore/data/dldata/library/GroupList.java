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

package de.unidue.inf.is.ezdl.dlcore.data.dldata.library;

import de.unidue.inf.is.ezdl.dlcore.data.dldata.MergeableArrayList;



public class GroupList extends MergeableArrayList<Group> {

    private static final long serialVersionUID = -3512833484116081683L;


    public GroupList() {
        super();
    }


    /** Check if List contains Group with given id */
    public boolean contains(String groupId, String referencesystemId) {
        for (Group g : this) {
            if (g.getId().equals(groupId)) {
                return true;
            }
            else if (referencesystemId != null && referencesystemId.length() > 0 && g.getReferenceSystemId() != null
                            && g.getReferenceSystemId().equals(referencesystemId)) {
                return true;
            }
        }
        return false;
    }


    // Returns the group with the given groupId
    public Group getGroup(String groupId) {

        for (Group g : this) {
            if (g.getId().equals(groupId)) {
                return g;
            }
        }
        return null;
    }

}