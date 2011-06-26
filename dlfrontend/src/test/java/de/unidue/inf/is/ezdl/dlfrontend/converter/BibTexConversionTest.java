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



public class BibTexConversionTest extends AbstractConversionTest {

    private static final String ID = "4dd4c18a-2853-4081-bd3c-a7cc1e2595f8";


    @Before
    public void setUp() throws Exception {

        class TestableBibTexConversionStrategy extends BibTexConversionStrategy {

            @Override
            protected String getUUID() {
                return ID;
            }
        }
        cs = new TestableBibTexConversionStrategy();

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

        documentExpectedOutput = "@article{" + ID + ",\n" + //
                        "title = {" + title + "},\n" + //
                        "author = {Lustig, Peter and Dieter, Hans and Lolz, Roflmao},\n" + //
                                                                                           // "journal = {"
                                                                                           // +
                                                                                           // journalTitle
                                                                                           // +
                                                                                           // "},\n"
                                                                                           // +
                                                                                           // //
                        "year = {" + year + "}\n" + //
                        // "conference = {" + conferenceTitle + "}\n" + //
                        "}\n";

        conferenceExpectedOutput = conferenceTitle;
        journalExpectedOutput = journalTitle;

        documentOutput = cs.print(document).toString();
        // conferenceOutput = cs.print(document.getConference()).toString();
        // journalOutput = cs.print(document.getJournal()).toString();
    }

}
