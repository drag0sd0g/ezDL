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

package de.unidue.inf.is.ezdl.dlcore.data;

import org.junit.Assert;
import org.junit.Test;

import de.unidue.inf.is.ezdl.dlcore.AbstractTestBase;
import de.unidue.inf.is.ezdl.dlcore.DocumentFactory;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.Document;



/**
 * @author mjordan
 */
public class OIDFactoryTest extends AbstractTestBase {

    @Test
    public void oidTestStuff() {
        testOid("1", "txt:title:2000:bf", "title", 2000, "Norbert Fuhr", "Nick Belkin");
        testOid("1.", "txt:title:2000:bf", "title.", 2000, "Norbert Fuhr", "Nick Belkin");
    }


    @Test
    public void oidTestAuthors() {
        testOid("1", null, "title", 2000);
        testOid("2", null, "title", 2000, (String[]) null);
        testOid("3a", "txt:title:2000:abbcccdgghkkklmmpswww", "title", 2000, "Henry Dorsett Case", "John Doe",
                        "Sheldon Cooper", "Leonard Hofstaedter", "Penny", "Howard Wolowitz", "Raj Koothrappali",
                        "Leslie Winkle", "Dr. Eric Gablehauser", "Barry Kripke", "Stuart", "Bernadette", "John Carter",
                        "Kerry Weaver", "Chuny Marquez", "Abby Lockhart", "Malik McGrath", "Luka Kovac", "Haleh Adams",
                        "Mark Greene", "Peter Benton");
        testOid("3b", "txt:title:2000:abbcccdgghkkklmmpswww", "title", 2000, "Henry Dorsett Case", "John Doe",
                        "Sheldon Cooper", "Leonard Hofstaedter", "Penny", "Bernadette", "Luka Kovac", "Haleh Adams",
                        "Mark Greene", "Peter Benton", "John Carter", "Kerry Weaver", "Chuny Marquez", "Abby Lockhart",
                        "Malik McGrath", "Howard Wolowitz", "Raj Koothrappali", "Leslie Winkle",
                        "Dr. Eric Gablehauser", "Barry Kripke", "Stuart");
    }


    @Test
    public void oidTestTitle() {
        testOid("1", "txt:thisisafreakytitlewithlotsofcrazystuffcrapinit:2000:f",
                        "This is a Freaky tiTle. With lots of 'crazy' stuff & crap in it.", 2000, "Norbert Fuhr");
        testOid("1", "txt:titlewithinit:2000:f", "Title with : in it.", 2000, "Norbert Fuhr");
    }


    private void testOid(String id, String expected, String title, int year, String... authors) {
        Document d = DocumentFactory.createDocument(title, year, authors);
        String oid = OIDFactory.calcOid(d);
        Assert.assertEquals(id, expected, oid);
    }
}
