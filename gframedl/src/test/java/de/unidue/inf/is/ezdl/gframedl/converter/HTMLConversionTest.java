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

package de.unidue.inf.is.ezdl.gframedl.converter;

import org.apache.commons.lang.StringEscapeUtils;
import org.junit.Before;

import de.unidue.inf.is.ezdl.dlfrontend.converter.AbstractConversionTest;
import de.unidue.inf.is.ezdl.dlfrontend.i18n.I18nSupport;
import de.unidue.inf.is.ezdl.gframedl.helper.EzDLProtocolHandler;



public class HTMLConversionTest extends AbstractConversionTest {

    private I18nSupport i18n = I18nSupport.getInstance();

    private String iconPath = "/icons/16x16/mime/";
    private String styleSheetPath = "detailView.css";


    private class TestableHTMLConversionStrategy extends HTMLConversionStrategy {

        @Override
        protected String stylesheetPath() {
            return styleSheetPath;
        }


        @Override
        protected String iconPath(String name) {
            return iconPath + name + "_16.png";
        }
    }


    @Before
    public void setUp() throws Exception {

        String authorLabel = new String(i18n.getLocString("field.authors"));
        // String conferenceLabel = new
        // String(i18n.getLocString("field.conference"));
        String abstractLabel = new String(i18n.getLocString("field.abstract"));
        // String journalLabel = new String(i18n.getLocString("field.journal"));
        String yearLabel = new String(i18n.getLocString("field.year"));

        cs = new TestableHTMLConversionStrategy();

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

        documentExpectedOutput = "<html><head><link rel=\"stylesheet\" type=\"text/css\" href=\""
                        + styleSheetPath
                        + "\"></head>"
                        + //
                        "<body><h2>"
                        + StringEscapeUtils.escapeHtml(title)
                        + "</h2>"
                        + //
                        "<p><b>"
                        + yearLabel
                        + "</b>: "
                        + year
                        + "</p>"
                        + //
                        "<p><b>"
                        + authorLabel
                        + "</b></p>"
                        + //
                        "<ul id =\"author\" style=\" list-style-image: url("
                        + iconPath
                        + "author_16.png);\">"
                        + //
                        "<li><a href=\"ezdl://"
                        + EzDLProtocolHandler.INTERNAL_QUERY
                        + "?Author=%22Peter+Lustig%22\">Peter Lustig</a></li><li><a href=\"ezdl://"
                        + EzDLProtocolHandler.INTERNAL_QUERY
                        + "?Author=%22Hans+Dieter%22\">Hans Dieter</a></li><li><a href=\"ezdl://"
                        + EzDLProtocolHandler.INTERNAL_QUERY
                        + "?Author=%22Roflmao+Lolz%22\">Roflmao Lolz</a></li></ul>" //
                        // + "<p><b>" //
                        // + conferenceLabel + "</b></p>" +
                        // "<ul id =\"conference\" style=\" list-style-image: url("
                        // + iconPath + "conference_16.png);\">" + //
                        // "<li>" +
                        // StringEscapeUtils.escapeHtml(conferenceTitle) +
                        // "</li></ul>" + //
                        // "<p><b>" + journalLabel + "</b></p>" + //
                        // "<ul id =\"journal\" style=\" list-style-image: url("
                        // + iconPath + "journal_16.png);\">" + //
                        // "<li>" + StringEscapeUtils.escapeHtml(journalTitle) +
                        // "</li></ul>" + //
                        + "<p><b>" + abstractLabel + "</b></p>"
                        + //
                        "<div><p>" + StringEscapeUtils.escapeHtml(abstractText)
                        + "</p></div><br/><br/><br/><br/><br/></body></html>";

        conferenceExpectedOutput = "<html><head><body>" + conferenceTitle + "</body></head></html>";
        journalExpectedOutput = "<html><head><body>" + journalTitle + "</body></head></html>";

        documentOutput = cs.print(document).toString();
        // conferenceOutput = cs.print(document.getConference()).toString();
        // journalOutput = cs.print(document.getJournal()).toString();
    }

}
