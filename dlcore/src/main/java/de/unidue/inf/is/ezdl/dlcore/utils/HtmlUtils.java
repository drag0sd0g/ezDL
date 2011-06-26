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

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;



public final class HtmlUtils {

    private HtmlUtils() {
    }


    public static String getAttrValue(String attr, String value) {
        if (value != null && value.length() > 0) {
            return "\n<b>" + attr + ":</b>&nbsp; " + value + "<br>";
        }
        return "";
    }


    public static String getAttrValue(String attr, String value, String unit) {
        if (value != null && value.length() > 0) {
            return "\n<b>" + attr + ":</b>&nbsp; " + value + " " + unit + "<br>";
        }
        return "";
    }


    public static String getDescrItem(String item) {
        if (item != null) {
            return "<dd>" + item + "</dd>\n";
        }
        return "";
    }


    public static String getDescrItems(List<String> items) {
        StringBuffer descrItems = new StringBuffer();
        for (String item : items) {
            descrItems.append("<dd>" + item + "</dd>\n");
        }
        return descrItems.toString();
    }


    public static String getDescrLabel(String label) {
        if (label != null) {
            return "<dt><b>" + label + "</b></dt>\n";
        }
        return "";
    }


    public static String getDL(String descrLabel, String descrItems) {
        return "\n<dl>" + descrLabel + descrItems + "</dl>";
    }


    public static String getHTMLFooter() {
        return "\n</body>\n</html>";
    }


    public static String getHTMLHeader() {
        return "<html>\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n"
                        + "<head><title>Document</title></head>\n<body>";
    }


    public static String getImageLink(String imageName, String altLabel, int size) {
        return "<img src=\"" + imageName + ".png\" " + "border=0 height=" + size + " width=" + size + " alt=\""
                        + altLabel + "\">";
    }


    public static String getLink(String url, String text) {
        return "<a href=\"" + url + "\" >" + text + "</a> ";
    }


    public static void translateURL(String text, StringBuffer sb, String label) {
        String url = "ezdl://translate?text=" + text.replaceAll(" ", "+");

        sb.append(" <a href=\"");
        sb.append(url);
        sb.append("\" target=\"other\">(").append(label).append(")</a>");
    }


    public static String getTagcloudHTML(Map<String, Integer> tags) {

        StringBuffer tagcloud = new StringBuffer();
        tagcloud.append("<html>");

        if (tags == null || tags.keySet().size() == 0) {
            return "";
        }

        LinkedList<Integer> tagFrequency = new LinkedList<Integer>();
        tagFrequency.addAll(tags.values());
        Collections.sort(tagFrequency, Collections.reverseOrder());

        // cut list at 50 elements
        int cutoff = (tagFrequency.size() >= 50) ? 50 : tagFrequency.size();
        tagFrequency = new LinkedList<Integer>(tagFrequency.subList(0, cutoff));

        int minOccurence = Collections.min(tagFrequency);
        int maxOccurence = Collections.max(tagFrequency);

        Iterator<String> i = tags.keySet().iterator();
        while (i.hasNext()) {

            String term = i.next();
            if (tags.get(term) < minOccurence) {
                continue;
            }
            int fontSize = (int) (7 * (tags.get(term).doubleValue() / maxOccurence));
            fontSize = (fontSize < 1) ? 1 : fontSize;

            tagcloud.append("<font size=\"" + fontSize + "\">" + term + "</font> ");
        }

        tagcloud.append("</html>");

        return tagcloud.toString();
    }


    /**
     * Normalizes a String for HTML output
     * 
     * @param s
     * @return
     */
    public static String normalize(String s) {
        StringBuffer str = new StringBuffer();

        int len = (s != null) ? s.length() : 0;

        for (int i = 0; i < len; i++) {
            char ch = s.charAt(i);
            switch (ch) {
                case '<': {
                    str.append("&lt;");
                    break;
                }
                case '>': {
                    str.append("&gt;");
                    break;
                }
                case '&': {
                    str.append("&amp;");
                    break;
                }
                case '"': {
                    str.append("&quot;");
                    break;
                }
                case '\r':
                case '\n': {
                    str.append("&#");
                    str.append(Integer.toString(ch));
                    str.append(';');
                    break;
                }
                default: {
                    str.append(ch);
                }
            }
        }

        return (str.toString());
    }


    public static String getWrappedHTML(String input, int length) {
        StringBuffer output = new StringBuffer();
        int total = 0;
        StringTokenizer st = new StringTokenizer(input);
        while (st.hasMoreTokens()) {
            String next = st.nextToken();
            if (total + next.length() <= length) {
                output.append(next);
                output.append(" ");
                total += next.length();
            }
            else {
                output.append("<br>" + next + " ");
                total = next.length();
            }
        }

        return output.toString().trim();

    }

}
