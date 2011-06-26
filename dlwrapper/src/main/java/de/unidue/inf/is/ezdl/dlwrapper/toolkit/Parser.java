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

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.unidue.inf.is.ezdl.dlcore.utils.StringUtils;
import de.unidue.inf.is.ezdl.dlcore.utils.XmlUtils;



/**
 * Class to parse an DOM Document Node by using XPATH.
 */

public class Parser {

    private Logger logger = Logger.getLogger(Parser.class);

    /**
     * Parser element node of the wrapperconfiguration.
     */
    protected Element configuration;

    private Map<String, Object> properties;
    private URL baseurl;


    /**
     * Constructor of this class.
     * 
     * @param configuration
     *            parserconfiguration
     */
    public Parser(Element configuration) {
        this.configuration = configuration;
    }


    /**
     * @param doc
     * @param properties
     * @param baseurl
     * @return
     */
    public Object parse(Document doc, Map<String, Object> properties, URL baseurl) throws SAXException {
        this.properties = properties;
        this.baseurl = baseurl;
        return parse(doc, configuration);
    }


    private Object parse(Node contextNode, Element config) throws SAXException {
        if (config == null) {
            return null;
        }
        if (config.getTagName().equals("verify")) {
            final boolean inputAsExpected = verify(contextNode, config);
            if (!inputAsExpected) {
                return null;
            }
            config = getNextSiblingElement(config);
        }
        if (config.getTagName().equals("collect")) {
            return collect(contextNode, config);
        }
        if (config.getTagName().equals("concat")) {
            return concat(contextNode, config);
        }
        if (config.getTagName().equals("context")) {
            return context(contextNode);
        }
        if (config.getTagName().equals("extract")) {
            return extract(contextNode, config);
        }
        if (config.getTagName().equals("iterate")) {
            return iterate(contextNode, config);
        }
        if (config.getTagName().equals("parse")) {
            return parse(contextNode, getFirstChildElement(config));
        }
        if (config.getTagName().equals("property")) {
            return property(contextNode, config);
        }
        if (config.getTagName().equals("text")) {
            return text(contextNode, config);
        }
        if (config.getTagName().equals("url")) {
            return url(contextNode, config);
        }
        return null;
    }


    private Object context(Node contextNode) {
        logger.debug("Context: " + Tools.nodeAsString(contextNode) + "\n");
        System.out.println("Context: " + Tools.nodeAsString(contextNode) + "\n");
        return null;
    }


    private Object parse(String context, Element config) {
        if (config == null) {
            return context;
        }

        if (config.getTagName().equals("context")) {
            logger.debug("Context: " + context + "\n");
            System.out.println("Context: " + context + "\n");
            return context;
        }
        if (config.getTagName().equals("substitute")) {
            return substitute(context, config);
        }
        if (config.getTagName().equals("split")) {
            return split(context, config);
        }
        if (config.getTagName().equals("find")) {
            return find(context, config);
        }
        if (config.getTagName().equals("findone")) {
            return findone(context, config);
        }
        return context;
    }


    private boolean verify(Node contextNode, Element config) throws SAXException {
        final Document d = Tools.xmlDoc(Tools.nodeAsString(contextNode));

        Element nextConfig = getFirstChildElement(config);
        while (nextConfig != null) {
            final String tagName = nextConfig.getTagName();
            if ("expect".equals(tagName)) {
                final String expected = nextConfig.getAttribute("regex");

                if (!StringUtils.isEmpty(expected)) {

                    final Object o = parse(d.getDocumentElement(), getFirstChildElement(nextConfig));

                    if (o instanceof String) {
                        final String text = (String) o;
                        final boolean matches = (text.matches(expected));
                        if (!matches) {
                            logger.debug("Verification failed: '" + expected + "' not found");
                            return false;
                        }
                    }
                }
            }
            else if ("context".equals(tagName)) {
                context(contextNode);
            }
            nextConfig = getNextSiblingElement(nextConfig);
        }
        return true;
    }


    private Object collect(Node contextNode, Element config) throws SAXException {
        String sep = config.getAttribute("separator");
        if (sep == null) {
            sep = " ";
        }

        StringBuilder out = new StringBuilder();

        Element nextConfig = getFirstChildElement(config);
        Object obj = parse(contextNode, nextConfig);

        if (obj instanceof List<?>) {
            List<?> list = (List<?>) obj;
            for (Object o : list) {
                if (o instanceof String) {
                    String str = (String) o;
                    if (out.length() != 0) {
                        out.append(sep);
                    }
                    out.append(str.trim());
                }
            }
        }
        String result = out.toString().trim();
        return result;
    }


    private Object concat(Node contextNode, Element config) {
        String xpath = config.getAttribute("xpath");
        Node node = contextNode;

        if (xpath != null && (!xpath.equals(""))) {
            node = (Node) XmlUtils.evaluateXPathExpression(xpath, contextNode, XPathConstants.NODE);
            if (node == null) {
                return null;
            }
        }
        String text = Tools.extractAllTextNodes(node);

        Element child = getFirstChildElement(config);
        return (child == null) ? text : parse(text, child);
    }


    private Object text(Node contextNode, Element config) {
        String xpath = config.getAttribute("xpath");

        XPath xp = XPathFactory.newInstance().newXPath();

        try {
            String text = xp.evaluate(xpath, contextNode);
            Element child = getFirstChildElement(config);

            return (child == null) ? text : parse(text, child);
        }
        catch (XPathExpressionException e) {
            logger.error("TransformerException in concat!", e);
        }
        return null;
    }


    private Object substitute(String context, Element config) {
        String pattern = config.getAttribute("pattern");
        String replaceWith = config.getAttribute("replacewith");
        String result = Tools.substitute(pattern, replaceWith, context);

        Element child = getFirstChildElement(config);
        return (child == null) ? result : parse(result, child);
    }


    private Collection<Object> split(String context, Element config) {
        String delimiter = config.getAttribute("delimiter");
        Collection<Object> result = new ArrayList<Object>();
        StringTokenizer st = new StringTokenizer(context, delimiter, false);
        Element child = getFirstChildElement(config);
        while (st.hasMoreTokens()) {
            String token = st.nextToken();

            result.add((child == null) ? token : parse(token, child));
        }
        return (result.size() == 0) ? null : result;
    }


    private Map<String, Object> find(String context, Element config) {
        Map<String, Object> result = new HashMap<String, Object>();
        while (config != null) {
            String key = config.getAttribute("key");
            String pattern = config.getAttribute("pattern");
            Object value = Tools.find(pattern, context);
            Element child = getFirstChildElement(config);
            if (value != null && child != null) {
                value = parse((String) value, child);
            }
            if (value != null) {
                result.put(key, value);
            }
            config = getNextSiblingElement(config);
        }
        return (result.size() == 0) ? null : result;
    }


    private Object findone(String context, Element config) {
        Object result = null;
        while (config != null) {
            String pattern = config.getAttribute("pattern");
            Object value = Tools.find(pattern, context);
            Element child = getFirstChildElement(config);
            if (value != null && child != null) {
                value = parse((String) value, child);
            }
            if (value != null) {
                result = value;
            }
            config = getNextSiblingElement(config);
        }
        return result;
    }


    private URL url(Node contextNode, Element config) {
        String xpath = config.getAttribute("xpath");

        XPath xp = XPathFactory.newInstance().newXPath();

        try {
            String link = xp.evaluate(xpath, contextNode);
            return (link == null || link.length() == 0) ? null : new URL(baseurl, link);
        }
        catch (XPathExpressionException e) {
            logger.error("TransformerException in url!", e);

        }
        catch (java.net.MalformedURLException e) {
            System.err.println("MalformedURLException in url!");
            System.err.println(e);
            logger.error("TransformerException in url!", e);

        }
        return null;
    }


    /**
     * This method iterates over all <extracts/> and places the key, values into
     * a hashtable
     * 
     * @param contextNode
     * @param config
     * @return Hashtable with all key, value pairs
     */
    private Map<String, Object> extract(Node contextNode, Element config) throws SAXException {
        Map<String, Object> result = new HashMap<String, Object>();
        while (config != null) {
            String key = config.getAttribute("key");
            Object value = parse(contextNode, getFirstChildElement(config));
            if (value != null) {
                result.put(key, value);
            }
            config = getNextSiblingElement(config);
        }
        if (result.size() == 0) {
            return null;
        }
        return result;
    }


    private Object property(Node contextNode, Element config) throws SAXException {
        while (config != null && config.getTagName().equals("property")) {
            String name = config.getAttribute("name");
            Object value = parse(contextNode, getFirstChildElement(config));
            if (value != null) {
                properties.put(name, value);
            }
            config = getNextSiblingElement(config);
        }
        return parse(contextNode, config);
    }


    /**
     * This method iterates over all Nodes split by the xpath expression and
     * returns a Vector.
     * 
     * @param contextNode
     * @param config
     * @return
     */
    private Collection<Object> iterate(Node contextNode, Element config) {
        Collection<Object> result = new ArrayList<Object>();

        if (contextNode == null) {
            System.err.println("ContextNode  is null");
            logger.error("ContexNode is null in iterate");

            return result;
        }
        if (config == null) {
            System.err.println("Config  is null");
            logger.error("Config is null in iterate");
            return result;
        }
        String xpath = config.getAttribute("xpath");

        if (xpath == null) {
            logger.error("XPath is null in iterate");
            return result;
        }

        try {
            Document d = Tools.xmlDoc(Tools.nodeAsString(contextNode));

            XPath xp = XPathFactory.newInstance().newXPath();

            NodeList nodes = (NodeList) xp.evaluate(xpath, d.getDocumentElement(), XPathConstants.NODESET);
            Element nextConfig = getFirstChildElement(config);
            for (int i = 0; i < nodes.getLength(); i++) {
                Object obj = parse(nodes.item(i), nextConfig);
                if (obj != null) {
                    result.add(obj);
                }
            }
        }
        catch (XPathExpressionException e) {
            logger.error("TransformerException in iterate!", e);
        }
        catch (Exception r) {
            r.printStackTrace();
        }
        // if (result.size() == 0) {
        // return null;
        // }
        return result;
    }


    private Element getFirstChildElement(Node element) {
        Node child = element.getFirstChild();
        if (child != null && child.getNodeType() != Node.ELEMENT_NODE) {
            return getNextSiblingElement(child);
        }
        return (Element) child;
    }


    private Element getNextSiblingElement(Node node) {
        Node sibling = node.getNextSibling();
        while (sibling != null && sibling.getNodeType() != Node.ELEMENT_NODE) {
            sibling = sibling.getNextSibling();
        }
        return (Element) sibling;
    }
}
