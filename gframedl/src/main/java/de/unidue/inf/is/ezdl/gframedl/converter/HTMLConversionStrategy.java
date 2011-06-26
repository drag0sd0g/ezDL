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

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;

import de.unidue.inf.is.ezdl.dlcore.data.dldata.Person;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.PersonList;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.Term;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.TextDocument;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.URLList;
import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;
import de.unidue.inf.is.ezdl.dlcore.utils.StringUtils;
import de.unidue.inf.is.ezdl.dlfrontend.converter.DocumentConversionStrategy;
import de.unidue.inf.is.ezdl.dlfrontend.converter.ExportResult;
import de.unidue.inf.is.ezdl.dlfrontend.converter.ExportResultText;
import de.unidue.inf.is.ezdl.dlfrontend.converter.SimpleObjectConversionStrategy;
import de.unidue.inf.is.ezdl.dlfrontend.i18n.I18nSupport;
import de.unidue.inf.is.ezdl.gframedl.helper.EzDLProtocolHandler;



/**
 * Conversion strategy class. Converts document to HTML format.
 */
public class HTMLConversionStrategy implements DocumentConversionStrategy, SimpleObjectConversionStrategy {

    private I18nSupport i18n = I18nSupport.getInstance();

    private String styleSheet = stylesheetPath();

    private static Logger logger = Logger.getLogger(HTMLConversionStrategy.class);


    @Override
    public ExportResult print(TextDocument document) {
        LinkedList<TextDocument> list = new LinkedList<TextDocument>();
        list.add(document);
        return print(list);
    }


    @Override
    public ExportResult print(List<TextDocument> documents) {
        StringBuilder html = new StringBuilder();
        html.append("<html><head><link rel=\"stylesheet\" type=\"text/css\" href=\"").append(styleSheet)
                        .append("\"></head><body>");

        for (TextDocument document : documents) {
            printSingleDocument(document, html);
        }
        html.append("</body></html>");

        ExportResultText out = new ExportResultText(html);
        return out;
    }


    private void printSingleDocument(TextDocument document, StringBuilder html) {
        html.append("<h2>").append(escape(document.getTitle())).append("</h2>");
        if (document.getYear() != Integer.MIN_VALUE) {
            html.append("<p><b>").append(i18n.getLocString("field.year")).append("</b>: ").append(document.getYear())
                            .append("</p>");
        }
        PersonList al = document.getAuthorList();
        if ((al != null) && (!al.isEmpty())) {
            String authorLabel = getAuthorFieldLabel(al);
            html.append("<p><b>").append(escape(authorLabel)).append("</b></p>");
            html.append(generateListIcon("author"));
            for (Person a : al) {
                html.append("<li>").append(createAuthorlink(a)).append("</li>");
            }
            html.append("</ul>");
        }

        if (!StringUtils.isEmpty(document.getAbstract())) {
            html.append("<p><b>").append(i18n.getLocString("field.abstract")).append("</b></p>");
            html.append("<div>");
            printToHTMLescaped(html, "p", document.getAbstract());
            html.append("</div>");
        }

        URLList urls = (URLList) document.getFieldValue(Field.URLS);
        if (urls != null && !urls.isEmpty()) {
            html.append("<p><b>").append(i18n.getLocString("field.detaillinks")).append("</b></p>");
            html.append("<ul>");
            for (URL url : urls) {
                String urlStr = url.toString();
                html.append("<li><a href=\"" + urlStr + "\">").append(urlStr).append("</a></li>");
            }
            html.append("</ul>");
        }
        html.append("<br/><br/><br/><br/><br/>");
    }


    @Override
    public ExportResult print(Person person) {
        StringBuilder html = new StringBuilder();
        html.append("<html><head><link rel=\"stylesheet\" type=\"text/css\" href=\"").append(styleSheet)
                        .append("\"></head><body>");
        html.append(generateListIcon("author"));
        html.append("<li>").append(createAuthorlink(person)).append("</li>");
        html.append("</ul>");
        html.append("</body></html>");

        ExportResultText out = new ExportResultText(html);
        return out;
    }


    @Override
    public ExportResult print(Term term) {
        StringBuilder html = new StringBuilder();
        html.append("<html><head><link rel=\"stylesheet\" type=\"text/css\" href=\"").append(styleSheet)
                        .append("\"></head><body>");
        html.append(generateListIcon("term"));
        printToHTMLescaped(html, "li", term.getTerm());
        html.append("</ul>");
        html.append("</body></html>");

        ExportResultText out = new ExportResultText(html);
        return out;
    }


    private String escape(String string) {
        return StringEscapeUtils.escapeHtml(string);
    }


    /**
     * Returns a localized string that means "author" or "authors" (plural),
     * depending on how many authors are in the list.
     * 
     * @param al
     *            the author list to generate the label for
     * @return the localized label
     */
    private String getAuthorFieldLabel(PersonList al) {
        String authorLabel = null;
        final boolean moreThanOneElementInList = (al.size() >= 2);
        if (moreThanOneElementInList) {
            authorLabel = i18n.getLocString("field.authors");
        }
        else {
            authorLabel = i18n.getLocString("field.author");
        }
        return authorLabel;
    }


    /**
     * Generates an ul tag with imageicons for listitems
     * 
     * @param name
     *            Defines an id for the css file and gets an icon depending on
     *            the name
     * @return generated ul tag with id and icon
     */
    private StringBuffer generateListIcon(String name) {
        StringBuffer tmp = new StringBuffer();
        String iconPath = iconPath(name);
        tmp.append("<ul id =\"").append(name).append("\"").append(" style=\" list-style-image: url(").append(iconPath)
                        .append(");\">");
        return tmp;
    }


    /**
     * Calculates the path for an icon with the given name.
     * 
     * @param name
     *            the name of the icon (e.g. "author")
     * @return the full path for that icon (e.g. /tmp/...../author_16.png")
     */
    protected String iconPath(String name) {
        String iconPath;
        iconPath = getClass().getResource("/icons/16x16/mime/" + name + "_16.png").toString();
        return iconPath;
    }


    /**
     * Calculates the path for the CSS style sheet.
     * 
     * @return the full path for the style sheet (e.g.
     *         /tmp/...../detailView.css")
     */
    protected String stylesheetPath() {
        return getClass().getResource("/detailView.css").toString();
    }


    private String createAuthorlink(Person person) {
        String authorlink = person.getFirstName() + " " + person.getLastName();
        try {
            return "<a href=\"ezdl://" + EzDLProtocolHandler.INTERNAL_QUERY + "?Author=%22"
                            + URLEncoder.encode(person.getFirstName(), "UTF-8") + "+"
                            + URLEncoder.encode(person.getLastName(), "UTF-8") + "%22\">" + authorlink + "</a>";
        }
        catch (UnsupportedEncodingException e) {
            logger.error(e);
        }
        return authorlink;
    }


    /**
     * Appends a HTML tag surrounded String to the HTML output StringBuffer,
     * escaping the content.
     * 
     * @param html
     *            StringBuilder for HTML output
     * @param htmlTag
     *            tag which surrounds the content string
     * @param content
     *            string which has to be printed to HTML
     */
    private void printToHTMLescaped(StringBuilder html, String htmlTag, String content) {
        html.append("<").append(htmlTag).append(">").append(escape(content)).append("</").append(htmlTag).append(">");
    }

}
