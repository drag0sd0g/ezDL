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



public class PersonListTest {

    @Test
    public void twoLists() {
        PersonList a = new PersonList();
        PersonList b = new PersonList();
        Assert.assertEquals(a, b);
        a.add(new Person("Jens Kapitza"));
        b.merge(a);
        Assert.assertEquals(a, b);

        a.add(new Person(null, "Gluek"));
        b.add(new Person("Hanz Gluek"));
        a.merge(b);
        Assert.assertEquals(a, b);
    }


    @Test
    public void testMerge2a() {
        PersonList a = new PersonList();
        a.add(new Person("Andrew Simon"));
        a.add(new Person("Matthias Jordan"));
        a.add(new Person("Norbert Fuhr"));
        PersonList b = new PersonList();
        b.add(new Person("A Simon"));
        b.add(new Person("M Jordan"));
        b.add(new Person("N Fuhr"));

        a.merge(b);

        Assert.assertEquals("list size", 3, a.size());
        Assert.assertEquals(new Person("Andrew", "Simon"), a.get(0));
        Assert.assertEquals(new Person("Matthias", "Jordan"), a.get(1));
        Assert.assertEquals(new Person("Norbert", "Fuhr"), a.get(2));
    }


    @Test
    public void testMerge2b() {
        PersonList a = new PersonList();
        a.add(new Person("Andrew Simon"));
        a.add(new Person("M Jordan"));
        a.add(new Person("Norbert Fuhr"));
        PersonList b = new PersonList();
        b.add(new Person("A Simon"));
        b.add(new Person("Matthias Jordan"));
        b.add(new Person("N Fuhr"));

        a.merge(b);

        Assert.assertEquals("list size", 3, a.size());
        Assert.assertEquals(new Person("Andrew", "Simon"), a.get(0));
        Assert.assertEquals(new Person("Matthias", "Jordan"), a.get(1));
        Assert.assertEquals(new Person("Norbert", "Fuhr"), a.get(2));
    }


    @Test
    public void testMerge2c() {
        PersonList a = new PersonList();
        a.add(new Person("Andrew Simon"));
        a.add(new Person("M Jordan"));
        a.add(new Person("Norbert Fuhr"));
        PersonList b = new PersonList();
        b.add(new Person("A Simon"));
        b.add(new Person("Matthias Jordan"));
        b.add(new Person("N Fuhr"));

        b.merge(a);

        Assert.assertEquals("list size", 3, b.size());
        Assert.assertEquals(new Person("Andrew", "Simon"), b.get(0));
        Assert.assertEquals(new Person("Matthias", "Jordan"), b.get(1));
        Assert.assertEquals(new Person("Norbert", "Fuhr"), b.get(2));
    }


    @Test
    public void testMerge2d() {
        PersonList a = new PersonList();
        a.add(new Person("Andrew Simon"));
        a.add(new Person("M Jordan"));
        PersonList b = new PersonList();
        b.add(new Person("A Simon"));
        b.add(new Person("Matthias Jordan"));
        b.add(new Person("N Fuhr"));

        b.merge(a);

        Assert.assertEquals("list size", 3, b.size());
        Assert.assertEquals(new Person("Andrew", "Simon"), b.get(0));
        Assert.assertEquals(new Person("Matthias", "Jordan"), b.get(1));
        Assert.assertEquals(new Person("N", "Fuhr"), b.get(2));
    }


    @Test
    public void testMerge2e() {
        PersonList a = new PersonList();
        a.add(new Person("Andrew Simon"));
        a.add(new Person("M Jordan"));
        PersonList b = new PersonList();
        b.add(new Person("Matthias Jordan"));
        b.add(new Person("N Fuhr"));

        b.merge(a);

        Assert.assertEquals("list size", 3, b.size());
        Assert.assertEquals(new Person("Matthias", "Jordan"), b.get(0));
        Assert.assertEquals(new Person("N", "Fuhr"), b.get(1));
        Assert.assertEquals(new Person("Andrew", "Simon"), b.get(2));
    }


    @Test
    public void testMerge3a() {
        PersonList a = new PersonList();
        a.add(new Person("Andrew Simon"));
        PersonList b = new PersonList();
        b.add(new Person("Matthias Jordan"));

        b.merge(a);

        Assert.assertEquals("list size", 2, b.size());
        Assert.assertEquals(new Person("Matthias", "Jordan"), b.get(0));
        Assert.assertEquals(new Person("Andrew", "Simon"), b.get(1));
    }


    @Test
    public void testMerge3b() {
        PersonList a = new PersonList();
        a.add(new Person("Andrew Simon"));
        PersonList b = new PersonList();
        b.add(new Person("Matthias Jordan"));

        a.merge(b);

        Assert.assertEquals("list size", 2, a.size());
        Assert.assertEquals(new Person("Andrew", "Simon"), a.get(0));
        Assert.assertEquals(new Person("Matthias", "Jordan"), a.get(1));
    }


    @Test
    public void testMerge3c() {
        PersonList a = new PersonList();
        a.add(new Person("Andrew Simon"));
        PersonList b = new PersonList();

        a.merge(b);

        Assert.assertEquals("list size", 1, a.size());
        Assert.assertEquals(new Person("Andrew", "Simon"), a.get(0));
    }


    @Test
    public void testMerge3d() {
        PersonList a = new PersonList();
        a.add(new Person("Andrew Simon"));
        PersonList b = new PersonList();

        b.merge(a);

        Assert.assertEquals("list size", 1, b.size());
        Assert.assertEquals(new Person("Andrew", "Simon"), b.get(0));
    }


    @Test
    public void testMerge4a() {
        PersonList a = new PersonList();
        a.add(new Person("Andrew Jackson Simon"));
        a.add(new Person("M Jordan"));
        a.add(new Person("Henry Dorsett Case"));
        a.add(new Person("Donald E. Knuth"));
        a.add(new Person("B.A. Baracus "));
        PersonList b = new PersonList();
        b.add(new Person("A Simon"));

        b.merge(a);

        Assert.assertEquals("list size", 5, b.size());
        Assert.assertEquals(new Person("Andrew Jackson", "Simon"), b.get(0));
        Assert.assertEquals(new Person("M", "Jordan"), b.get(1));
        Assert.assertEquals(new Person("Henry Dorsett", "Case"), b.get(2));
        Assert.assertEquals(new Person("Donald E.", "Knuth"), b.get(3));
        Assert.assertEquals(new Person("B.A.", "Baracus"), b.get(4));
    }


    @Test
    public void testMerge4b() {
        PersonList a = new PersonList();
        a.add(new Person("Andrew Jackson Simon"));
        a.add(new Person("M Jordan"));
        a.add(new Person("Henry Dorsett Case"));
        a.add(new Person("Donald E. Knuth"));
        a.add(new Person("B.A. Baracus "));
        PersonList b = new PersonList();
        b.add(new Person("A Simon"));
        b.add(new Person("Donald Ervin Knuth"));

        a.merge(b);

        Assert.assertEquals("list size", 5, a.size());
        Assert.assertEquals(new Person("Andrew Jackson", "Simon"), a.get(0));
        Assert.assertEquals(new Person("M", "Jordan"), a.get(1));
        Assert.assertEquals(new Person("Henry Dorsett", "Case"), a.get(2));
        Assert.assertEquals(new Person("Donald Ervin", "Knuth"), a.get(3));
        Assert.assertEquals(new Person("B.A.", "Baracus"), a.get(4));
    }


    @Test
    public void testMerge4c() {
        PersonList a = new PersonList();
        a.add(new Person("Andrew Jackson Simon"));
        a.add(new Person("M Jordan"));
        a.add(new Person("Henry Dorsett Case"));
        a.add(new Person("Donald E. Knuth"));
        a.add(new Person("B.A. Baracus "));
        PersonList b = new PersonList();
        b.add(new Person("A Simon"));
        b.add(new Person("Donald Ervin Knuth"));
        b.add(new Person("Bosco Al Baracus"));

        a.merge(b);

        Assert.assertEquals("list size", 5, a.size());
        Assert.assertEquals(new Person("Andrew Jackson", "Simon"), a.get(0));
        Assert.assertEquals(new Person("M", "Jordan"), a.get(1));
        Assert.assertEquals(new Person("Henry Dorsett", "Case"), a.get(2));
        Assert.assertEquals(new Person("Donald Ervin", "Knuth"), a.get(3));
        Assert.assertEquals(new Person("Bosco Al", "Baracus"), a.get(4));
    }


    @Test
    public void testMerge4d() {
        PersonList a = new PersonList();
        a.add(new Person("Andrew Jackson Simon"));
        a.add(new Person("M Jordan"));
        a.add(new Person("Henry Dorsett Case"));
        a.add(new Person("Donald E. Knuth"));
        a.add(new Person("B.A. Baracus "));
        PersonList b = new PersonList();
        b.add(new Person("A Simon"));
        b.add(new Person("Donald Ervin Knuth"));
        b.add(new Person("Bosco Al Baracus"));
        b.add(new Person("HD Case"));

        a.merge(b);

        Assert.assertEquals("list size", 5, a.size());
        Assert.assertEquals(new Person("Andrew Jackson", "Simon"), a.get(0));
        Assert.assertEquals(new Person("M", "Jordan"), a.get(1));
        Assert.assertEquals(new Person("Henry Dorsett", "Case"), a.get(2));
        Assert.assertEquals(new Person("Donald Ervin", "Knuth"), a.get(3));
        Assert.assertEquals(new Person("Bosco Al", "Baracus"), a.get(4));
    }


    @Test
    public void testMerge4e() {
        PersonList a = new PersonList();
        a.add(new Person("Donald E. Knuth"));
        a.add(new Person("B.A. Baracus "));
        PersonList b = new PersonList();
        b.add(new Person("A Simon"));
        b.add(new Person("HD Case"));

        a.merge(b);

        Assert.assertEquals("list size", 4, a.size());
        Assert.assertEquals(new Person("Donald E.", "Knuth"), a.get(0));
        Assert.assertEquals(new Person("B.A.", "Baracus"), a.get(1));
        Assert.assertEquals(new Person("A", "Simon"), a.get(2));
        Assert.assertEquals(new Person("HD", "Case"), a.get(3));
    }


    @Test
    public void testMerge5a() {
        PersonList a = new PersonList();
        a.add(new Person("Donald E. Knuth"));
        a.add(new Person("B.A. Baracus "));

        a.merge(new Person("A", "Simon"));

        Assert.assertEquals("list size", 3, a.size());
        Assert.assertEquals(new Person("Donald E.", "Knuth"), a.get(0));
        Assert.assertEquals(new Person("B.A.", "Baracus"), a.get(1));
        Assert.assertEquals(new Person("A", "Simon"), a.get(2));
    }


    @Test
    public void testMerge5b() {
        PersonList a = new PersonList();
        a.add(new Person("Donald E. Knuth"));
        a.add(new Person("B.A. Baracus "));

        a.merge(new Person("DE", "Knuth"));

        Assert.assertEquals("list size", 2, a.size());
        Assert.assertEquals(new Person("Donald E.", "Knuth"), a.get(0));
        Assert.assertEquals(new Person("B.A.", "Baracus"), a.get(1));
    }


    @Test
    public void testMerge6a() {
        PersonList a = new PersonList();
        a.add(new Person("Donald E. Knuth"));
        a.add(new Person("B.A. Baracus "));

        a.merge(new Person(null, "Knuth"));
        a.merge(new Person(null, "Baracus"));

        Assert.assertEquals("list size", 2, a.size());
        Assert.assertEquals(new Person("Donald E.", "Knuth"), a.get(0));
        Assert.assertEquals(new Person("B.A.", "Baracus"), a.get(1));
    }


    @Test
    public void testMergeA() {
        PersonList a = new PersonList();
        a.add(new Person("Matthias Jordan"));
        PersonList b = new PersonList();
        b.add(new Person("M Jordan"));

        a.merge(b);

        Assert.assertEquals("list size", 1, a.size());
    }


    @Test
    public void testEqual() {
        PersonList a = new PersonList();
        a.add(new Person("Donald E. Knuth"));
        a.add(new Person("B.A. Baracus "));

        PersonList b = new PersonList();
        b.add(new Person("Donald E. Knuth"));
        b.add(new Person("B.A. Baracus "));

        Assert.assertEquals(a, b);
    }
}
