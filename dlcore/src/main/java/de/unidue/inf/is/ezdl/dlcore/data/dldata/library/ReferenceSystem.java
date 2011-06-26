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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.unidue.inf.is.ezdl.dlcore.data.Mergeable;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.AbstractDLObject;



/** Represents an available online reference system like mendeley, bibsonomy ... */
public class ReferenceSystem extends AbstractDLObject {

    private static final long serialVersionUID = 8051996551095828572L;

    private boolean workOffline;
    private String name;

    // required authentication parameters
    private Map<String, String> requiredParameters;

    // other parameters which are needed bei the referencesystem.
    // are set automatically. like access_token in mendeley
    private Map<String, String> otherParameters;

    // other available reference systems. only need in the detail view to change
    // system.
    private List<ReferenceSystem> availableSystems;


    /**
     * Constructor
     * 
     * @param name
     *            Name of the reference system
     * @param requiredParameters
     *            required parameters of the reference system.
     */
    public ReferenceSystem(String name, Map<String, String> requiredParameters) {
        this.name = name;
        this.requiredParameters = requiredParameters;
        this.availableSystems = null;
        this.otherParameters = new HashMap<String, String>();
        workOffline = false;
    }


    /**
     * Constructor
     * 
     * @param name
     *            Name of the reference system
     * @param requiredParameters
     *            required parameters of the reference system. Have to be set by
     *            the user
     * @param otherParameters
     *            other Parameters which are needed. Are set automatically
     */
    public ReferenceSystem(String name, Map<String, String> requiredParameters, Map<String, String> otherParameters) {
        this.name = name;
        this.requiredParameters = requiredParameters;
        this.availableSystems = null;
        this.otherParameters = otherParameters;
        workOffline = false;
    }


    public ReferenceSystem() {
        this.name = null;
        this.requiredParameters = null;
        this.availableSystems = null;
        this.otherParameters = new HashMap<String, String>();
        workOffline = false;
    }


    /** Returns the name of the reference system */
    public String getName() {
        return name;
    }


    /** Returns the required parameters */
    public Map<String, String> getRequiredParameters() {
        return requiredParameters;
    }


    /** Set the name of the reference system */
    public void setName(String name) {
        this.name = name;
    }


    /** Set the required parameters of the reference system */
    public void setRequiredParameters(Map<String, String> requiredParameters) {
        this.requiredParameters = requiredParameters;
    }


    /**
     * set other available reference systems. only need in the detail view to
     * change system
     **/
    public void setOtherAvailableReferenceSystems(List<ReferenceSystem> availableSystems) {
        this.availableSystems = availableSystems;
    }


    public void setOtherParameters(Map<String, String> otherParameters) {
        this.otherParameters = otherParameters;
    }


    /**
     * get other available reference systems. only need in the detail view to
     * change system
     **/
    public List<ReferenceSystem> getOtherAvailableReferenceSystems() {
        return availableSystems;
    }


    /** Returns other needed parameters. Like access_token in mendeley ... */
    public Map<String, String> getOtherParameters() {
        return otherParameters;
    }


    /** Set Work offline */
    public void workOffline(boolean workOffline) {
        this.workOffline = workOffline;
    }


    /** Returns if work Offline */
    public boolean workOffline() {
        return workOffline;
    }


    @Override
    public boolean isSimilar(Mergeable obj) {
        if (obj instanceof ReferenceSystem) {
            return name.equals(((ReferenceSystem) obj).name);
        }
        return false;
    }


    @Override
    public String asString() {
        return "ReferenceSystem:" + name;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : (name).hashCode());
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
        ReferenceSystem other = (ReferenceSystem) obj;

        if (!name.equals(other.name)) {
            return false;
        }

        return true;
    }

}
