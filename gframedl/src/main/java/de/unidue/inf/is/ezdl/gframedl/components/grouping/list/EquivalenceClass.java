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

package de.unidue.inf.is.ezdl.gframedl.components.grouping.list;

import javax.swing.Icon;



/**
 * The equivalence class is a group in the GroupedList. If you derive it, which
 * is not necessary in most cases ensure that the compareTo and equals methods
 * are implemented consistent.
 * 
 * @author RB1
 */
public class EquivalenceClass implements Comparable<EquivalenceClass> {

    private String name;
    private Icon icon;

    private boolean lastGroup;


    /**
     * Constructor. The icon can be null.
     * 
     * @param name
     * @param icon
     */
    public EquivalenceClass(String name, Icon icon) {
        this.name = name;
        this.icon = icon;
        lastGroup = false;
    }


    /**
     * The name of the group.
     * 
     * @return
     */
    public String getName() {
        return name;
    }


    /**
     * The icon of the group. May be null.
     * 
     * @return
     */
    public Icon getIcon() {
        return icon;
    }


    @Override
    public int compareTo(EquivalenceClass o) {
        if (!lastGroup) {
            return name.compareTo(o.getName());
        }
        else {
            int result = name.compareTo(o.getName());
            if (result != 0) {
                return 1;
            }
            else {
                return 0;
            }
        }
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
        EquivalenceClass other = (EquivalenceClass) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        }
        else if (!name.equals(other.name)) {
            return false;
        }
        return true;
    }


    /**
     * The lastGroup property affects the comparator. If set to true the group
     * appears at the end of the list.
     * 
     * @param lastGroup
     */
    public void setLastGroup(boolean lastGroup) {
        this.lastGroup = lastGroup;
    }


    /**
     * The lastGroup property affects the comparator. If set to true the group
     * appears at the end of the list.
     * 
     * @param lastGroup
     */
    public boolean isLastGroup() {
        return lastGroup;
    }
}
