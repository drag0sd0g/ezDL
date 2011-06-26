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

package de.unidue.inf.is.ezdl.dlcore.data.importer;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import de.unidue.inf.is.ezdl.dlcore.AbstractTestBase;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.Document;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.Person;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.PersonList;
import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;



/**
 * @author mjordan
 */
public class RISImporterTest extends AbstractTestBase {

    RISImporter importer = new RISImporter();


    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }


    @Test
    public void test() {
        Document res = importer.convert("TY  - JOUR\n" + //
                        "AU  - Shannon,Claude E.\n" + //
                        "PY  - 1948/07//\n" + //
                        "TI  - A Mathematical Theory of Communication\n" + //
                        "JO  - Bell System Technical Journal\n" + //
                        "SP  - 379\n" + //
                        "EP  - 423\n" + //
                        "VL  - 27\n" + //
                        "ER  -\n");
        Assert.assertEquals("A Mathematical Theory of Communication", res.getFieldValue(Field.TITLE));
        PersonList authors = new PersonList();
        authors.add(new Person("Claude E.", "Shannon"));
        Assert.assertEquals(authors, res.getFieldValue(Field.AUTHOR));
        Assert.assertEquals("Bell System Technical Journal", res.getFieldValue(Field.JOURNAL));
        Assert.assertEquals(1948, res.getFieldValue(Field.YEAR));
        Assert.assertEquals("379-423", res.getFieldValue(Field.PAGES));
        Assert.assertEquals("27", res.getFieldValue(Field.VOLUME));
    }


    @Test
    public void test2() {
        String txt = "TY  - JOUR\n"
                        + //
                        "AU  - Kay, E.\n"
                        + //
                        "AU  - Leigh, D.\n"
                        + //
                        "AU  - Zerbetto, F.\n"
                        + //
                        "TI  - Synthetische molekulare Motoren und mechanische Maschinen\n"
                        + //
                        "JO  - Angewandte Chemie\n"
                        + //
                        "JA  - Angewandte Chemie\n"
                        + //
                        "VL  - 119\n"
                        + //
                        "IS  - 1-2\n"
                        + //
                        "PB  - WILEY-VCH Verlag\n"
                        + //
                        "SN  - 1521-3757\n"
                        + //
                        "UR  - http://dx.doi.org/10.1002/ange.200504313\n"
                        + //
                        "DO  - 10.1002/ange.200504313\n"
                        + //
                        "SP  - 72\n"
                        + //
                        "EP  - 196\n"
                        + //
                        "KW  - Molekulare Maschinen\n"
                        + //
                        "KW  - Nanotechnologie\n"
                        + //
                        "KW  - Nichtkovalente Wechselwirkungen\n"
                        + //
                        "KW  - Supramolekulare Chemie\n"
                        + //
                        "PY  - 2007\n"
                        + //
                        "AB  - Abstract 10.1002/ange.200504313.abs In der Natur spielen gesteuerte Bewegungen auf molekularer Ebene bei vielen Prozessen eine Schlüsselrolle. Das Schließen der Lücke zwischen der aktuellen Generation synthetischer Verbindungen, bei denen hauptsächlich elektronische und chemische Effekte genutzt werden, und makroskopischen Maschinen, deren Funktionsfähigkeit auf der synchronisierten Bewegung von Maschinenteilen beruht, wäre ein großer Erfolg. Dieses Forschungsgebiet wird derzeit intensiv bearbeitet und wächst außerordentlich schnell. Die ersten Überlegungen zu molekularen Maschinen reichen allerdings weiter zurück in die Vergangenheit, in eine Zeit, in der die Konzepte vom statistischen Verhalten der Materie und die Gesetze der Thermodynamik formuliert wurden. Wir umreißen hier die Erfolgsgeschichte der Bändigung molekularer Bewegungen, der Beherrschung der grundlegenden Prinzipien, an denen sich das Design zu orientieren hat, und der Fortschritte bei der Anwendung synthetischer Systeme, die durch mechanische Bewegung Aufgaben verrichten können. Ferner werden wir auf einige ungelöste Probleme eingehen.\n"
                        + //
                        "ER  -\n";
        Document res = importer.convert(txt);
        Assert.assertEquals("Synthetische molekulare Motoren und mechanische Maschinen", res.getFieldValue(Field.TITLE));
        PersonList expectedAuthors = new PersonList();
        expectedAuthors.add(new Person("E.", "Kay"));
        expectedAuthors.add(new Person("D.", "Leigh"));
        expectedAuthors.add(new Person("F.", "Zerbetto"));
        final Object docAuthors = res.getFieldValue(Field.AUTHOR);
        Assert.assertEquals(expectedAuthors, docAuthors);
        Assert.assertEquals("Angewandte Chemie", res.getFieldValue(Field.JOURNAL));
        Assert.assertEquals(2007, res.getFieldValue(Field.YEAR));
        Assert.assertEquals("72-196", res.getFieldValue(Field.PAGES));
        Assert.assertEquals("119", res.getFieldValue(Field.VOLUME));
        Assert.assertEquals("1-2", res.getFieldValue(Field.NUMBER));
        Assert.assertEquals("10.1002/ange.200504313", res.getFieldValue(Field.DOI));
        Assert.assertTrue(((String) res.getFieldValue(Field.ABSTRACT))
                        .startsWith("Abstract 10.1002/ange.200504313.abs In der Natur spielen"));
        Assert.assertTrue(((String) res.getFieldValue(Field.ABSTRACT)).endsWith("Probleme eingehen."));
    }


    @Test
    public void test3() {
        String txt = "TY  - BOOK\n" + //
                        "TI  - Elektrochemische Korrosion bei gleichzeitiger Einwirkung mechanischer Beanspruchung\n" + //
                        "AU  - Spähn, H.\n" + //
                        "PB  - WILEY-VCH Verlag GmbH & Co. KGaA\n" + //
                        "SN  - 9783527625659\n" + //
                        "UR  - http://dx.doi.org/10.1002/9783527625659.ch2b\n" + //
                        "DO  - 10.1002/9783527625659.ch2b\n" + //
                        "SP  - 193\n" + //
                        "EP  - 420\n" + //
                        "KW  - elektrochemische Korrosion\n" + //
                        "KW  - mechanische Beanspruchung\n" + //
                        "KW  - Erosionskorrosion\n" + //
                        "KW  - Kavitationskorrosion\n" + //
                        "T2  - Korrosion und Korrosionsschutz\n" + //
                        "PY  - 2009\n" + //
                        "AB  - Summary 10.1002/9783527625659.ch2b.abs Das Kapitel enthält die folgenden Abschnitte:\n" + //
                        "\n" + //
                        "* Spannungsrißkorrosion\n" + //
                        "* Überblick\n" + //
                        "* Anodische Spannungsrißkorrosion\n" + //
                        "* Kathodische, wasserstoffinduzierte Spannungsrißkorrosion\n" + //
                        "ER  -\n";
        Document res = importer.convert(txt);
        Assert.assertEquals("Elektrochemische Korrosion bei gleichzeitiger Einwirkung mechanischer Beanspruchung",
                        res.getFieldValue(Field.TITLE));
        PersonList expectedAuthors = new PersonList();
        expectedAuthors.add(new Person("H.", "Spähn"));
        final Object docAuthors = res.getFieldValue(Field.AUTHOR);
        Assert.assertEquals(expectedAuthors, docAuthors);
        Assert.assertEquals(null, res.getFieldValue(Field.JOURNAL));
        Assert.assertEquals(2009, res.getFieldValue(Field.YEAR));
        Assert.assertEquals("193-420", res.getFieldValue(Field.PAGES));
        Assert.assertEquals(null, res.getFieldValue(Field.VOLUME));
        Assert.assertEquals(null, res.getFieldValue(Field.NUMBER));
        Assert.assertEquals("10.1002/9783527625659.ch2b", res.getFieldValue(Field.DOI));
        Assert.assertTrue(((String) res.getFieldValue(Field.ABSTRACT))
                        .startsWith("Summary 10.1002/9783527625659.ch2b.abs Das Kapitel"));
        Assert.assertTrue(((String) res.getFieldValue(Field.ABSTRACT)).endsWith("Spannungsrißkorrosion"));
    }
}
