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

package de.unidue.inf.is.ezdl.gframedl.utils;

import java.awt.Color;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;



/**
 * This class contains utility methods for HTML-based hightlighting in
 * {@link String} s.
 * 
 * @author tbeckers
 */
public final class HighlightingUtils {

    private HighlightingUtils() {
    }


    private static String highlight(String htmlString, boolean escape, List<String> highlightStrings, Matcher matcher,
                    String foregroundColor, String backgroundColor) {
        if (htmlString != null) {
            if (highlightStrings.isEmpty() || highlightStrings.size() == 1 && highlightStrings.get(0).isEmpty()) {
                return htmlString;
            }

            StringBuilder result = new StringBuilder();
            int lastStop = 0;
            while (matcher.find()) {
                int start = matcher.start(1);
                int end = matcher.end(1);

                int i = htmlString.substring(0, start).lastIndexOf(">");
                int j = htmlString.substring(0, start).lastIndexOf("<");
                boolean r = i > j || (i == -1 && j == -1);

                if (r) {
                    if (escape) {
                        result.append(escape(htmlString.substring(lastStop, start)));
                    }
                    else {
                        result.append(htmlString.substring(lastStop, start));
                    }
                    result.append("<font bgcolor=\"" + backgroundColor + "\" color=\"" + foregroundColor + "\">");
                    if (escape) {
                        result.append(escape(htmlString.substring(start, end)));
                    }
                    else {
                        result.append(htmlString.substring(start, end));

                    }
                    result.append("</font>");
                    lastStop = end;
                }
            }
            if (escape) {
                result.append(escape(htmlString.substring(lastStop, htmlString.length())));
            }
            else {
                result.append(htmlString.substring(lastStop, htmlString.length()));
            }

            return result.toString();
        }
        else {
            return "";
        }
    }


    /**
     * Highlights terms in a HTML string.
     * 
     * @param htmlString
     *            The {@link String} that contains terms to highlight
     * @param escapeInput
     *            If the input {@link String} should be escaped in the output
     * @param highlightStrings
     *            {@link List} of {@link String}s that should be highlighted
     * @param escapeHighlightStrings
     *            If the highlight string should be escaped
     * @param foregroundColor
     *            The foreground {@link Color} of the highlighted parts
     * @param backgroundColor
     *            The background {@link Color} of the highlighted parts
     * @return The highlighted {@link String}
     */
    public static String highlightTerms(String htmlString, boolean escapeInput, List<String> highlightStrings,
                    boolean escapeHighlightStrings, String foregroundColor, String backgroundColor) {
        Matcher matcher = matcher(highlightStrings, escapeHighlightStrings, htmlString, true);
        if (matcher != null) {
            return highlight(htmlString, escapeInput, highlightStrings, matcher, foregroundColor, backgroundColor);
        }
        else {
            return htmlString;
        }
    }


    /**
     * Highlights terms or part of terms in a HTML string.
     * 
     * @param htmlString
     *            The {@link String} that contains parts to highlight
     * @param escapeInput
     *            If the input {@link String} should be escaped in the output
     * @param highlightStrings
     *            {@link List} of {@link String}s that should be highlighted
     * @param escapeHighlightStrings
     *            If the highlight string should be escaped
     * @param foregroundColor
     *            The foreground {@link Color} of the highlighted parts
     * @param backgroundColor
     *            The background {@link Color} of the highlighted parts
     * @return The highlighted {@link String}
     */
    public static String highlightParts(String htmlString, boolean escapeInput, List<String> highlightStrings,
                    boolean escapeHighlightStrings, String foregroundColor, String backgroundColor) {
        Matcher matcher = matcher(highlightStrings, escapeHighlightStrings, htmlString, false);
        if (matcher != null) {
            return highlight(htmlString, escapeInput, highlightStrings, matcher, foregroundColor, backgroundColor);
        }
        else {
            return htmlString;
        }
    }


    /**
     * Returns {@link Matcher} for highlighting.
     * 
     * @param highlightStrings
     * @param escapeHighlightStrings
     * @param stringToHighlight
     * @param highlightOnlyWords
     *            true, if only words should be highlighted
     * @return
     */
    public static Matcher matcher(List<String> highlightStrings, boolean escapeHighlightStrings,
                    String stringToHighlight, boolean highlightOnlyWords) {
        if (stringToHighlight == null || highlightStrings.isEmpty() || highlightStrings.size() == 1
                        && highlightStrings.get(0).isEmpty()) {
            return null;
        }

        String p = highlightOnlyWords ? "\\b(" : "(";
        for (int i = 0; i < highlightStrings.size(); i++) {
            String queryTerm = highlightStrings.get(i);
            if (escapeHighlightStrings) {
                p += Pattern.quote(queryTerm);
            }
            else {
                p += queryTerm;

            }
            if (i < highlightStrings.size() - 1) {
                p += "|";
            }
        }
        p += highlightOnlyWords ? ")\\b" : ")";
        Pattern pattern = Pattern.compile(p, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(stringToHighlight);
        return matcher;
    }


    /**
     * Coverts a {@link Color} to a hex value for HTML.
     * 
     * @param color
     *            The {@link Color} to convert
     * @return The hex value for HTML
     */
    public static String colorToHex(Color color) {
        return "#" + Integer.toHexString(color.getRGB() & 0x00ffffff);
    }


    private static String escape(Object o) {
        if (o != null) {
            return StringEscapeUtils.escapeXml(o.toString());
        }
        else {
            return "";
        }
    }

}
