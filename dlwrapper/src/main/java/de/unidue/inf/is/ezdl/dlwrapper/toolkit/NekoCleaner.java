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

package de.unidue.inf.is.ezdl.dlwrapper.toolkit;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;
import org.cyberneko.html.HTMLConfiguration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;



/**
 * Class to clean easy HTML documents. This class uses the power of the class
 * org.w3c.tidy.Tidy package. It also offers to substitute some strings in the
 * HTML document, before processing it with Tidy.
 */
public class NekoCleaner {

    private Logger logger = Logger.getLogger(NekoCleaner.class);

    private Element configuration;
    private String outputEncoding = "UTF-8";


    /**
     * Constructor of this class.
     */
    public NekoCleaner() {
    }


    /**
     * Constructor of this class.
     * 
     * @param configuration
     *            cleanerconfiguration
     */
    public NekoCleaner(Element configuration, String outputEncoding) {
        this.configuration = configuration;
        this.outputEncoding = outputEncoding;
    }


    /**
     * Reads a webpage from an open InputStream, cleans it and returns the
     * corresponding XML DOM document node.
     * 
     * @param in
     *            open InputStream to the webpage.
     * @return corresponding XML DOM document node.
     */
    public Document cleanStream(InputStream in) {

        org.apache.xerces.xni.parser.XMLParserConfiguration c = new HTMLConfiguration();
        // c.setFeature("http://cyberneko.org/html/features/augmentations",
        // true);
        c.setProperty("http://cyberneko.org/html/properties/names/elems", "lower");
        c.setProperty("http://cyberneko.org/html/properties/names/attrs", "lower");
        org.apache.xerces.parsers.DOMParser parser = new org.apache.xerces.parsers.DOMParser(c);
        parser.setDTDSource(null);

        try {
            BufferedReader inBuf = new BufferedReader(new InputStreamReader(in, outputEncoding));
            InputSource is = new InputSource(inBuf);
            is.setEncoding("UTF-8");
            parser.parse(is);
        }
        catch (SAXException e) {
            logger.error(e.getMessage(), e);
        }
        catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

        return parser.getDocument();
    }


    /**
     * Reads a webpage from an open InputStream, cleans it and returns the
     * corresponding XML DOM document node.
     * 
     * @param in
     *            open InputStream to the webpage.
     * @return corresponding XML DOM document node.
     */
    public Document clean(InputStream in) {
        String page = Tools.readInputStream(in);

        if (configuration != null) {
            if (Tools.getFirstChildElement(configuration) != null) {
                page = replaceSt(page);
                in = new ByteArrayInputStream(page.getBytes());
            }
        }

        Document doc = cleanStream(in);
        return doc;
    }


    private String replaceSt(String html) {
        Element child = Tools.getFirstChildElement(configuration);
        while (child != null) {
            String replaceWith = child.getAttribute("replacewith");
            replaceWith = replaceWith.replaceAll("&lt;", "<");
            replaceWith = replaceWith.replaceAll("&gt;", ">");
            replaceWith = replaceWith.replaceAll("&quot;", "\"");
            String pattern = child.getAttribute("pattern");
            html = html.replaceAll(pattern, replaceWith);

            child = Tools.getNextSiblingElement(child);
        }

        return html;
    }
}