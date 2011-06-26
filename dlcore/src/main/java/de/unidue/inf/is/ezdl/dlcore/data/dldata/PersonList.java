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

import java.util.List;

import de.unidue.inf.is.ezdl.dlcore.data.Mergeable;



/**
 * Keeps information about multiple {@link Person}s.
 */

public class PersonList extends MergeableArrayList<Person> implements Comparable<PersonList> {

    private static final long serialVersionUID = -1630211297967533841L;


    /**
     * Constructor. Creates an empty list.
     */
    public PersonList() {
        super();
    }


    /**
     * Constructor. Creates a list with the data in the given list.
     * 
     * @param list
     *            the data to include in the list
     */
    public PersonList(List<? extends Person> list) {
        super(list);
    }


    @Override
    public void merge(Mergeable other) {
        if (other instanceof PersonList) {
            final PersonList otherPersonList = (PersonList) other;
            specialMerge(otherPersonList);
        }
        else if (other instanceof Person) {
            PersonList a = new PersonList();
            a.add((Person) other);
            specialMerge(a);
        }
    }


    private void specialMerge(PersonList otherPersonList) {
        for (Person other : otherPersonList) {
            boolean different = true;
            boolean merged = false;
            for (Person our : this) {
                if (our.specialEquals(other)) {
                    different = false;
                    String otherFirstName = other.getFirstName();
                    String ourFirstName = our.getFirstName();
                    boolean otherFirstNameLonger = (otherFirstName != null) && (ourFirstName != null)
                                    && (otherFirstName.length() > ourFirstName.length());
                    if ((ourFirstName == null) || otherFirstNameLonger) {
                        our.setFirstName(otherFirstName);
                        merged = true;
                        break;
                    }
                }
            }
            if (different && !merged) {
                add(other);
            }
        }
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result;
        return result;
    }


    @Override
    public int compareTo(PersonList o) {
        if (o == null) {
            return 1;
        }
        final String thisStr = toString();
        final String oStr = o.toString();
        return thisStr.compareTo(oStr);
    }


    @Override
    public String toStringHeader() {
        return "PersonList";
    }
}
