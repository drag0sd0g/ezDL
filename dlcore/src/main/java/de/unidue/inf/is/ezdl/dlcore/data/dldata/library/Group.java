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

import de.unidue.inf.is.ezdl.dlcore.data.Mergeable;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.AbstractDLObject;



/** Represents a group object. Used in library */
public class Group extends AbstractDLObject {

    private static final long serialVersionUID = -5282734673912156968L;

    public static final String TYPE_PRIVATE = "private";
    public static final String TYPE_OPEN = "open";
    public static final String TYPE_INVITE = "invite";

    /** intern Group ID */
    String id;

    /** Name of the Group */
    private String name;

    /** Intern online Reference ID. for example interen Mendeley Group ID */
    private String referenceSystemId;

    /** Name of the online Referencesystem */
    private String referenceSystem;

    /** Type of group in online Referencesytem (public, private) */
    private String type;

    /** should group saved online */
    private boolean save_online;


    public Group() {
        this.id = null;
        this.name = null;
        this.referenceSystem = null;
        this.referenceSystemId = null;
        this.type = null;
        save_online = false;
    }


    /**
     * @param id
     *            Intern ID of the group
     * @param name
     *            Name of the Group
     */
    public Group(String id, String name) {
        this.id = id;
        this.name = name;
        this.referenceSystemId = null;
        this.referenceSystem = null;
        this.type = null;
        save_online = false;

    }


    /**
     * @param id
     *            intern ID of the Group
     * @param name
     *            Name of the Group
     * @param referenceSystem
     *            Name of the online referencesystem where the group is saved.
     *            for example: Mendeley
     * @param referenceSystemId
     *            Intern online referencesystem group id
     */
    public Group(String id, String name, String referenceSystem, String referenceSystemId, String type) {
        this.id = id;
        this.name = name;
        this.referenceSystemId = referenceSystemId;
        this.referenceSystem = referenceSystem;
        this.type = type;
        save_online = false;
    }


    @Override
    public boolean isSimilar(Mergeable obj) {
        if (obj instanceof Group) {
            return name.equals(obj);
        }
        return false;
    }


    /** Returns the intern ID of the Group */
    public String getId() {
        return id;
    }


    /** Sets the intern ID of the Group */
    public void setId(String id) {
        this.id = id;
    }


    /** Returns the name of the group */
    public String getName() {
        return name;
    }


    /** Sets the name of the group */
    public void setName(String name) {
        this.name = name;
    }


    /**
     * Returns the intern online ReferenceSystem ID. for example intern Mendeley
     * Group ID
     */
    public String getReferenceSystemId() {
        return this.referenceSystemId;
    }


    /**
     * Sets the intern online ReferenceSystem ID. for example intern Mendeley
     * Group ID
     */
    public void setReferenceSystemId(String referenceSystemId) {
        this.referenceSystemId = referenceSystemId;
    }


    /**
     * Returns the name of the online Referencesystem in which the group is
     * saved
     */
    public String getReferenceSystem() {
        return this.referenceSystem;
    }


    /** Sets the name of the online Referencesystem in which the group is saved */
    public void setReferenceSystem(String referenceSystem) {
        this.referenceSystem = referenceSystem;
    }


    /** Returns the type of the group. public, private... */
    public String getType() {
        return this.type;
    }


    /** Sets the type of the group. public, private ... */
    public void setType(String type) {
        this.type = type;
    }


    /** Sets if group should be created in online referencesystem or not */
    public void setSaveOnline(boolean online) {
        this.save_online = online;
    }


    /** Returns if group should be created in online referencesystem or not */
    public boolean getSaveOnline() {
        return save_online;
    }


    /** Automaticall genereates a unique id and sets the id */
    public void generateId() {
        String hid;
        if (name != null) {
            if (name.length() > 5) {
                hid = name.trim().substring(0, 5) + Long.toString(System.currentTimeMillis());
            }
            else {
                hid = name.trim() + Long.toString(System.currentTimeMillis());
            }
        }
        else {
            hid = "Grp:" + Long.toString(System.currentTimeMillis());
        }

        this.id = hid;
    }


    /** Returns if group is a online group or not */
    public boolean onlineGroup() {
        if (this.referenceSystemId != null && this.referenceSystemId.length() > 0) {
            return true;
        }
        else {
            return false;
        }
    }


    @Override
    public String asString() {
        return getName();
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null || id == null) ? 0 : (name + id).hashCode());
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
        Group other = (Group) obj;
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        }
        else if (!id.equals(other.id)) {
            return false;
        }
        return true;
    }
}
