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

package de.unidue.inf.is.ezdl.dlfrontend.converter;

import org.junit.Before;



public class RISConversionTest extends AbstractConversionTest {

    @Before
    public void setUp() throws Exception {

        cs = new RISConversionStrategy();

        title = "Java and other OOP languages";
        journalTitle = "Google 4tw journal";
        conferenceTitle = "Java World OOP Language Conference";
        year = 1997;
        abstractText = "Diese 8. Auflage des Java-Kultbuches wurde gründlich " + //
                        "überarbeitet und aktualisiert. Besonders Einsteiger mit" + //
                        " Programmierkenntnissen und Industrieprogrammierer profitieren" + //
                        " von diesem umfassenden Werk. Tipps und Tricks aus den Java-FAQs " + //
                        "werden regelmäßig mit in die Insel aufgenommen, um wirklich das" + //
                        " abzudecken, was Sie im Alltag brauchen. Die Einführung in die " + //
                        "Sprache Java ist anschaulich und immer praxisorientiert. Schnell " + //
                        "geht es weiter mit fortgeschrittenen Themen wie Threads, Swing," + //
                        " Netzwerkprogrammierung, JavaBeans, RMI, XML-Verarbeitung mit Java," + //
                        " Servlets und Java ServerPages, JDBC und vielem mehr. Neu in dieser Fassung:" + //
                        " JAXB-API, umfangreichere Abschnitte zu regulären Ausdrücken und viele neue Beispiele.";

        document = createDocument(title, year, journalTitle, conferenceTitle, abstractText, "Peter Lustig",
                        "Hans Dieter", "Roflmao Lolz");

        documentExpectedOutput = "\r\nTY  - JOUR" + //
                        "\r\nTI  - " + title + //
                        "\r\nPY  - 1997" + //
                        "\r\nA1  - Lustig,Peter" + //
                        "\r\nA1  - Dieter,Hans" + //
                        "\r\nA1  - Lolz,Roflmao" + //
                        // "U1 - " + journalTitle + "\n" + //
                        // "U2 - " + conferenceTitle + "\n" + //
                        "\r\nN2  - " + abstractText + //
                        "\r\nER  - ";

        conferenceExpectedOutput = "U2 - " + conferenceTitle + "\n";

        journalExpectedOutput = "U1 - " + journalTitle + "\n";

        documentOutput = cs.print(document).toString();
        // conferenceOutput = cs.print(document.getConference()).toString();
        // journalOutput = cs.print(document.getJournal()).toString();

    }

}
