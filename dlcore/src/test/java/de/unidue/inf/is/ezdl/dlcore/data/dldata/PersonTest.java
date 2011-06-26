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

import org.junit.Assert;
import org.junit.Test;



public class PersonTest {

    @Test
    public void createAuthor() {
        Person a = new Person();
        Person b = new Person();
        Assert.assertEquals(a, b);

        a = new Person("Jens Kapitza");
        b = new Person("Jens", "Kapitza");
        Assert.assertEquals(a, b);
    }


    @Test
    public void createAuthorStrange() {
        Assert.assertEquals("1", "Rijsbergen", (new Person("Rijsbergen")).getLastName());
        Assert.assertEquals("2", "Rijsbergen", (new Person("Keith Rijsbergen")).getLastName());
        Assert.assertEquals("3", "van Rijsbergen", (new Person("van Rijsbergen")).getLastName());
        Assert.assertEquals("4", "Cinneide", (new Person("Mel O. Cinneide")).getLastName());
    }


    @Test
    public void merge() {
        Person a = new Person(null, "Kapitza");
        Person b = new Person("Jens", "Kapitza");
        a.merge(b);
        Assert.assertEquals(new Person("Jens", "Kapitza"), a);
    }


    // @Test
    public void specialEqualsTestDoubleInitials() {
        Person a = new Person("AJ Simon");
        Person b = new Person("Andrew Jackson Simon");

        Assert.assertTrue("equal", a.specialEquals(b));
    }


    @Test
    public void specialEqualsTestSingleInitial() {
        checkSpecialEqualsTrue("1a", "A Simon", "Andrew Simon");
        checkSpecialEqualsTrue("1b", "Andrew Simon", "A Simon");
        checkSpecialEqualsTrue("2", "B Simon", "Bert Simon");
        checkSpecialEqualsTrue("3", "Z Simon", "Zed Simon");
        checkSpecialEqualsTrue("4a", "E Simon", "Ed Simon");
        checkSpecialEqualsTrue("4b", "Ed Simon", "E Simon");
        checkSpecialEqualsTrue("5", "X Simon", "X Simon");
        checkSpecialEqualsTrue("6", "A Simon", "Simon");

        checkSpecialEqualsFalse("1", "A Simon", "Bert Simon");
        checkSpecialEqualsFalse("2", "A Simon", "Ed Simon");
        // checkSpecialEqualsFalse("3", "A Simon", "Andrew Jackson Simon");
    }


    private void checkSpecialEqualsTrue(String label, String nameA, String nameB) {
        Person a = new Person(nameA);
        Person b = new Person(nameB);

        Assert.assertTrue("equal: " + label, a.specialEquals(b));
    }


    private void checkSpecialEqualsFalse(String label, String nameA, String nameB) {
        Person a = new Person(nameA);
        Person b = new Person(nameB);

        Assert.assertTrue("not equal:" + label, !a.specialEquals(b));
    }


    @Test
    public void constructorTest() {
        checkConstructor("Andrew Simon", "Andrew", "Simon");
        checkConstructor("A Simon", "A", "Simon");
        checkConstructor("v Schmidt", "", "v Schmidt");
        // checkConstructor("Anton v Schmidt", "Anton", "v Schmidt");
        // checkConstructor("A v Schmidt", "A", "v Schmidt");
    }


    private void checkConstructor(String fullName, String firstName, String lastName) {
        Person a = new Person(fullName);

        Assert.assertEquals("first name", firstName, a.getFirstName());
        Assert.assertEquals("last name", lastName, a.getLastName());
    }

}
