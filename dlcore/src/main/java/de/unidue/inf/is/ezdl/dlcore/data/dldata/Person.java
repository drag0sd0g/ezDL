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
import de.unidue.inf.is.ezdl.dlcore.utils.PersonNameMatcher;
import de.unidue.inf.is.ezdl.dlcore.utils.merge.MergeIfNull;



public class Person extends AbstractDLObject {

    private static final long serialVersionUID = -5648286726031244900L;
    @MergeIfNull
    private String firstName;
    @MergeIfNull
    private String lastName;


    /**
     * Create a empty Author Object.
     */
    public Person() {
        super();
    }


    /**
     * Splitting a String like <code> "Firstname Lastname" </code> into an array
     * where index 0 is Firstname and index 1 is Lastname.
     * <p>
     * This also takes care of German and Dutch names like "van Rijsbergen" or
     * "von der Wiese" where the last name consists of multiple words.
     * 
     * @param name
     *            the Fullname seperated with space
     * @return an array of {first name, last name}, in other words a tuple of
     *         the given name.
     */
    private static String[] splitFirstAndLastName(String name) {
        name = name.trim();
        final int lastSpace = name.lastIndexOf(' ');
        String authorFName = "";
        String authorLName = "";
        final boolean nameBeginsWithInitial = ((lastSpace == 1) && (!name.isEmpty()) && (Character.isUpperCase(name
                        .charAt(0))));
        if (lastSpace >= 2 || nameBeginsWithInitial) {
            authorLName = name.substring(lastSpace + 1).trim();
            String fnRemainder = name.substring(0, lastSpace).trim();

            String[] fnParts = fnRemainder.split(" ");
            for (int i = fnParts.length - 1; (i >= 0); i--) {
                String part = fnParts[i];

                if ((part.length() != 0) && part.toLowerCase().equals(part)) {
                    authorLName = part + " " + authorLName;
                }
            }

            authorFName = name.substring(0, name.length() - authorLName.length()).trim();
        }
        else {
            authorLName = name;
        }

        return new String[] {
                        authorFName, authorLName
        };
    }


    /**
     * Create a Author with a name.
     * 
     * @param authorFirstAndLastName
     *            the Fullname of the Author
     */
    public Person(String authorFirstAndLastName) {
        this(splitFirstAndLastName(authorFirstAndLastName != null ? authorFirstAndLastName : ""));
    }


    /**
     * Create a Author if a name.
     * <p>
     * This does not work for more than two parameters. If more than two
     * parameters are passed, those after the seconds parameter are ignored.
     * 
     * @param nameparts
     *            Is an Array where the first parameter is the Firstname and the
     *            second one is the Lastname. e.g. {Firstname,Lastname}
     */
    public Person(String... nameparts) {
        this();
        if (nameparts.length > 0) {
            firstName = nameparts[0];
        }
        if (nameparts.length > 1) {
            lastName = nameparts[1];
        }
        if (getLastName() == null) {
            throw new IllegalArgumentException("The last name must not be null (but it was)!");
        }
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
        result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
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
        Person other = (Person) obj;
        if (firstName == null) {
            if (other.firstName != null) {
                return false;
            }
        }
        else if (!firstName.equals(other.firstName)) {
            return false;
        }
        if (lastName == null) {
            if (other.lastName != null) {
                return false;
            }
        }
        else if (!lastName.equals(other.lastName)) {
            return false;
        }
        return true;
    }


    /**
     * @return the initials of the firstname. this are just the first chars of
     *         each firstname part
     */
    public String getInitials() {
        String fName = firstName;
        StringBuilder initial = new StringBuilder("");
        if ((fName != null) && (fName.length() > 0)) {
            initial.append(fName.charAt(0));
            String[] fNames = fName.split(" |-");
            if (fNames.length > 1) {
                initial.append(fNames[1].charAt(0));
            }
        }
        return initial.toString();
    }


    /**
     * @return the first name
     */
    public String getFirstName() {
        return firstName;
    }


    /**
     * set the firstname
     * 
     * @param firstName
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }


    /**
     * @return the last name
     */
    public String getLastName() {
        return lastName;
    }


    /**
     * set the last name
     * 
     * @param lastName
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }


    @Override
    public String asString() {
        StringBuffer out = new StringBuffer();
        if (!"".equals(firstName)) {
            out.append(firstName);
        }
        if (!"".equals(lastName)) {
            out.append(" ");
            out.append(lastName);
        }
        return out.toString().trim();
    }


    @Override
    public boolean isSimilar(Mergeable obj) {
        if (equals(obj)) {
            return true;
        }
        if (obj instanceof Person) {
            Person b = (Person) obj;
            if ((firstName == null || b.firstName == null) && lastName != null && lastName.equals(b.lastName)) {
                return true;
            }
            if ((lastName == null || b.lastName == null) && firstName != null && firstName.equals(b.firstName)) {
                return true;
            }
        }
        return false;
    }


    public boolean specialEquals(Person other) {
        return PersonNameMatcher.instance().specialEquals(this, other);
    }


    public boolean isInFormMJackson() {
        return PersonNameMatcher.instance().isInFormMJackson(this);
    }
}
