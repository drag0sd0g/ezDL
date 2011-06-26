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

package de.unidue.inf.is.ezdl.dlcore.utils;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import de.unidue.inf.is.ezdl.dlcore.data.dldata.Person;



public class PersonNameMatcherTest {

    PersonNameMatcher matcher;


    @Before
    public void setup() {
        matcher = PersonNameMatcher.instance();
    }


    @Test
    public void testRuleBasedMatch() {
        Assert.assertTrue("1",
                        matcher.specialEquals(new Person("Keith van Rijsbergen"), new Person("C.J. van Rijsbergen")));
    }


    @Test
    public void testRuleBasedDontMatch() {
        Assert.assertFalse("1",
                        matcher.specialEquals(new Person("Keith van Rijsbergen"), new Person("C.J. van Ijsbergen")));
    }


    @Test
    public void testBasicMatch() {
        Assert.assertTrue("1", matcher.specialEquals(new Person("Mel O. Cinneide"), new Person("Cinneide")));
        Assert.assertTrue("2", matcher.specialEquals(new Person("Mel í Cinnéide"), new Person("Cinnéide")));
        Assert.assertTrue("3a", matcher.specialEquals(new Person("Dick van Dyke"), new Person("van Dyke")));
        Assert.assertTrue("3b", matcher.specialEquals(new Person("Dick van Dyke"), new Person("Dyke")));
    }


    @Test
    public void testBasicDontMatch() {
        Assert.assertFalse("1", matcher.specialEquals(new Person("Mel O. Cinneide"), new Person("Cinnéide")));
        Assert.assertFalse("3c", matcher.specialEquals(new Person("Dick Dyke"), new Person("van Dyke")));
    }


    @Test
    public void testSplitFirstName() {
        checkSplitFirstName("A", "A");
        checkSplitFirstName("Andrew", "Andrew");

        checkSplitFirstName("A J", "A", "J");
        checkSplitFirstName("A J B", "A", "J", "B");

        checkSplitFirstName("A.", "A.");
        checkSplitFirstName("A. J.", "A.", "J.");
        checkSplitFirstName("A. J. B.", "A.", "J.", "B.");

        checkSplitFirstName("AJ", "A", "J");
        checkSplitFirstName("AJB", "A", "J", "B");

        checkSplitFirstName("A.J.", "A", "J");
        checkSplitFirstName("A.J", "A", "J");
        checkSplitFirstName("A.J.B.", "A", "J", "B");
        checkSplitFirstName("A.J.B", "A", "J", "B");
    }


    private void checkSplitFirstName(String name, String... expParts) {
        String parts[] = matcher.splitFirstName(name);
        Assert.assertEquals(name + " len", expParts.length, parts.length);
        for (int i = 0; (i < expParts.length); i++) {
            Assert.assertEquals(name + " eq", expParts[i], parts[i]);
        }
    }


    @Test
    public void firstNamePartSpecialEqualsTest() {
        checkFirstNamePartSpecialEqualsTrue("a", "a");
        checkFirstNamePartSpecialEqualsTrue("A", "A");

        checkFirstNamePartSpecialEqualsTrue("A", "A.");
        checkFirstNamePartSpecialEqualsTrue("A.", "A");

        checkFirstNamePartSpecialEqualsTrue("A", "Andrew");
        checkFirstNamePartSpecialEqualsTrue("A.", "Andrew");
        checkFirstNamePartSpecialEqualsTrue("Andr.", "Andrew");
        checkFirstNamePartSpecialEqualsTrue("Andr", "Andrew");

        checkFirstNamePartSpecialEqualsFalse("a", "b");
        checkFirstNamePartSpecialEqualsFalse("A", "B");
        checkFirstNamePartSpecialEqualsFalse("A", "Bert");
        checkFirstNamePartSpecialEqualsFalse("A.", "Bert");
        checkFirstNamePartSpecialEqualsFalse("Andr.", "Andy");
        checkFirstNamePartSpecialEqualsFalse("Andr", "Andy");
    }


    private void checkFirstNamePartSpecialEqualsTrue(String a, String b) {
        boolean equals = matcher.firstNamePartSpecialEquals(a, b);
        Assert.assertTrue(a + "," + b, equals);
    }


    private void checkFirstNamePartSpecialEqualsFalse(String a, String b) {
        boolean equals = matcher.firstNamePartSpecialEquals(a, b);
        Assert.assertFalse(a + "," + b, equals);
    }


    @Test
    public void test() {
        checkFirstNamesSpecialEqualTrue("A", "A");

        checkFirstNamesSpecialEqualTrue("A", "Andrew");
        checkFirstNamesSpecialEqualTrue("A.", "Andrew");

        checkFirstNamesSpecialEqualTrue("A J", "Andrew Jackson");
        checkFirstNamesSpecialEqualTrue("A. J.", "Andrew Jackson");
        checkFirstNamesSpecialEqualTrue("A.J.", "Andrew Jackson");
        checkFirstNamesSpecialEqualTrue("AJ", "Andrew Jackson");

        checkFirstNamesSpecialEqualTrue("Andrew J", "Andrew Jackson");
        checkFirstNamesSpecialEqualTrue("Andrew J.", "Andrew Jackson");

        checkFirstNamesSpecialEqualTrue("Andrew", "A");
        checkFirstNamesSpecialEqualTrue("Andrew", "A.");

        checkFirstNamesSpecialEqualTrue("Andrew Jackson", "A J");
        checkFirstNamesSpecialEqualTrue("Andrew Jackson", "A. J.");
        checkFirstNamesSpecialEqualTrue("Andrew Jackson", "A.J.");

        checkFirstNamesSpecialEqualTrue("Andrew Jackson", "Andrew J");
        checkFirstNamesSpecialEqualTrue("Andrew Jackson", "Andrew J.");

        checkFirstNamesSpecialEqualTrue("A. Jackson", "Andrew J.");
        checkFirstNamesSpecialEqualTrue("A Jackson", "Andrew J");
        checkFirstNamesSpecialEqualTrue("A. Jackson", "Andrew J");
        checkFirstNamesSpecialEqualTrue("A Jackson", "Andrew J.");

        checkFirstNamesSpecialEqualFalse("A", "B");

        checkFirstNamesSpecialEqualFalse("A", "Bert");
        checkFirstNamesSpecialEqualFalse("A.", "Bert");

        checkFirstNamesSpecialEqualFalse("A J", "Andrew Bert");
        checkFirstNamesSpecialEqualFalse("A. J.", "Andrew Bert");
        checkFirstNamesSpecialEqualFalse("A.J.", "Bert Jackson");
    }


    private void checkFirstNamesSpecialEqualTrue(String a, String b) {
        boolean equals = matcher.firstNamesSpecialEqual(a, b);
        Assert.assertTrue(a + "," + b, equals);
    }


    private void checkFirstNamesSpecialEqualFalse(String a, String b) {
        boolean equals = matcher.firstNamesSpecialEqual(a, b);
        Assert.assertFalse(a + "," + b, equals);
    }


    // @Test
    public void testIsInFormMJackson() {
        checkIsInFormMJacksonTrue("A Simon");
        checkIsInFormMJacksonTrue("A. Simon");
        checkIsInFormMJacksonTrue("AJ Simon");

        checkIsInFormMJacksonFalse("A v Simon");
        checkIsInFormMJacksonFalse("A v. Simon");
        checkIsInFormMJacksonFalse("Andrew Simon");
    }


    private void checkIsInFormMJacksonTrue(String name) {
        Person a = new Person(name);
        Assert.assertTrue(name, PersonNameMatcher.instance().isInFormMJackson(a));
    }


    private void checkIsInFormMJacksonFalse(String name) {
        Person a = new Person(name);
        Assert.assertTrue(name, !PersonNameMatcher.instance().isInFormMJackson(a));
    }

}
