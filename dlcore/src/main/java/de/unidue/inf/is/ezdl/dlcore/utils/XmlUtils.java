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

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;



/**
 * Utility methods for XML handling.
 */
public final class XmlUtils {

    private static Logger logger = Logger.getLogger(XmlUtils.class);

    private static ThreadLocal<XPath> xPath = new ThreadLocal<XPath>() {

        @Override
        protected XPath initialValue() {
            return XPathFactory.newInstance().newXPath();

        };
    };


    private XmlUtils() {
    }


    /**
     * Evaluates an XPath-Expression and returns the result as a Node or
     * NodeList
     * 
     * @param xPathExpression
     *            XPath expression
     * @param node
     *            XML node
     * @param returnType
     *            mostly XPathConstants.NODESET or .NODE
     * @return result, null if an error ocurred
     */
    public static Object evaluateXPathExpression(String xPathExpression, Node node, QName returnType) {
        try {
            return xPath.get().evaluate(xPathExpression, node, returnType);
        }
        catch (XPathExpressionException exception) {
            logger.error("Exception caught: ", exception);
            return null;
        }
    }


    /**
     * Retrieves an attribute from document doc, element elementName with name
     * attributeName
     * 
     * @param doc
     * @param elementName
     * @param attributeName
     * @return
     */
    public static String getAttrValueOfElement(Document doc, String elementName, String attributeName) {
        Element elem = doc.getDocumentElement();
        if (elem.getTagName().equals(elementName)) {
            return elem.getAttribute(attributeName);
        }
        NodeList anl = doc.getDocumentElement().getElementsByTagName(elementName);
        if (anl == null) {
            return "";
        }
        Element elementWithAttribute = (Element) anl.item(0);
        if (elementWithAttribute == null) {
            return "";
        }

        return elementWithAttribute.getAttribute(attributeName);
    }


    /**
     * Returns the first element childnode of a given element.
     * 
     * @param element
     *            given element
     * @return fist element childnode of the given element
     */
    public static Element getFirstChildElement(Node element) {
        if (element == null) {
            return null;
        }
        Node child = element.getFirstChild();
        if (child != null && child.getNodeType() != Node.ELEMENT_NODE) {
            return getNextSiblingElement(child);
        }
        return (Element) child;
    }


    /**
     * Returns the next element siblingnode of a given node.
     * 
     * @param node
     *            given node
     * @return next element siblingnode of the given node
     */
    public static Element getNextSiblingElement(Node node) {
        Node sibling = node.getNextSibling();
        while (sibling != null && sibling.getNodeType() != Node.ELEMENT_NODE) {
            sibling = sibling.getNextSibling();
        }
        return (Element) sibling;
    }


    /**
     * Returns the root elementname of a document
     * 
     * @param MendeleyDoc
     *            doc
     * @return String
     */
    public static String getRootElement(Document doc) {
        Element elem = doc.getDocumentElement();
        if (elem != null) {
            return elem.getTagName();
        }
        return null;
    }


    public static String getRootElementTagName(String xmlString) {
        int len = xmlString.length();
        if (len > 50) {
            len = 50;
        }

        int start = -1;
        if (xmlString.startsWith("<?")) {
            start = xmlString.indexOf('<', 2) + 1;
        }
        else {
            start = xmlString.indexOf('<') + 1;
        }
        if (start > 0) {
            int end = xmlString.indexOf(' ', start);
            int end2 = xmlString.indexOf('>', start);

            if (end > end2 || end < 0) {
                end = end2;
            }
            return xmlString.substring(start, end);
        }
        return "";
    }


    /**
     * Returns the value of an element within a DOM document. The element name
     * has to be unique.
     * 
     * @param doc
     * @param element
     */
    public static String getValueOfElement(Document doc, String element) {
        Element elem = doc.getDocumentElement();
        return getValueOfElement(elem, element);
    }


    public static String getValueOfElement(Element element, String elementName) {
        if (element.getTagName().equals(elementName)) {
            return element.getFirstChild().getNodeValue();
        }
        NodeList anl = element.getElementsByTagName(elementName);
        if (anl == null) {
            return "";
        }
        Node agentnode = anl.item(0);
        if (agentnode == null) {
            return "";
        }
        agentnode = agentnode.getFirstChild();
        if (agentnode == null) {
            return "";
        }
        return agentnode.getNodeValue();
    }


    /**
     * Returns the value of an element within a DOM document. The element name
     * has to be unique.
     * 
     * @param doc
     * @param element
     */
    public static Element getElementWithName(Document doc, String element) {
        if (doc == null) {
            return null;
        }
        Element elem = doc.getDocumentElement();
        if (elem.getTagName().equals(element)) {
            return elem;
        }
        NodeList anl = doc.getDocumentElement().getElementsByTagName(element);
        if (anl == null) {
            return null;
        }
        Element elementWithAttribute = (Element) anl.item(0);
        if (elementWithAttribute == null) {
            return null;
        }
        return elementWithAttribute;
    }


    public static Document createDocument(InputStream stream) {
        try {
            InputSource in = new InputSource(stream);
            DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
            fact.setNamespaceAware(true);
            return fact.newDocumentBuilder().parse(in);
        }
        catch (Exception e) {
            logger.error("Can not create Document!", e);
            return null;
        }
    }


    public static String xml2String(Node node) {
        // return ElementToString(element);
        StringWriter output = new StringWriter();
        try {
            // Use a Transformer for output
            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer transformer = tFactory.newTransformer();
            // transformer.setOutputProperty("indent", "yes");
            DOMSource source = new DOMSource(node);
            StreamResult result = new StreamResult(output);
            transformer.transform(source, result);
        }
        catch (TransformerConfigurationException tce) {
            // Error generated by the parser
            logger.error("* Transformer Factory error", tce);

            // Use the contained exception, if any
            Throwable x = tce;
            if (tce.getException() != null) {
                x = tce.getException();
            }
            logger.error("Exception caught: ", x);
        }
        catch (TransformerException te) {
            // Error generated by the parser
            logger.error("* Transformation error", te);

            // Use the contained exception, if any
            Throwable x = te;
            if (te.getException() != null) {
                x = te.getException();
            }
            logger.error("Exception caught: ", x);
        }
        return output.toString();
    }


    public static Document xmlDoc(String xml) {
        return xmlDoc(xml, false);
    }


    public static Document xmlDoc(String xml, boolean ignoreMissingDTD) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(false);

        Document doc = null;
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
            if (ignoreMissingDTD) {
                builder.setEntityResolver(new EntityResolver() {

                    @Override
                    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
                        return new InputSource(new StringReader(""));
                    }
                });
            }

            doc = builder.parse(new InputSource(new StringReader(xml)));
        }
        catch (SAXException e) {
            logger.error(e.getMessage(), e);
        }
        catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        catch (ParserConfigurationException e) {
            logger.error(e.getMessage(), e);
        }
        return doc;
    }


    /**
     * Wandelt ein Teil eines XML-Documents in einen String um.
     * 
     * @param doc
     *            das XML-Document
     * @return ein XML-String
     */
    public static String xmlString(Document doc, Element elem) {
        if (doc == null) {
            return "";
        }

        if (elem == null) {
            return "";
        }

        return xml2String(elem);
    }


    /**
     * Wandelt ein Teil eines XML-Documents in einen String um.
     * 
     * @param doc
     *            das XML-Document
     * @return ein XML-String
     */
    public static String xmlString(Document doc, Element elem, boolean noXMLHeader) {
        if (doc == null) {
            return "";
        }

        if (elem == null) {
            return "";
        }

        String xml = xml2String(elem);
        if (noXMLHeader) {
            xml = xml.replaceFirst("<\\?.*\\?>", "");
        }
        return xml;
    }


    /**
     * Wandelt ein Teil eines XML-Documents in einen String um.
     * 
     * @param doc
     *            the XML-Document
     * @return an XML-String
     */
    public static String xmlString(Element elem) {
        if (elem == null) {
            return "";
        }

        return xml2String(elem);
    }


    /**
     * Gets an attribute from the i-th occurence of an element in an Document
     * 
     * @param element
     *            name of the element
     * @param doc
     *            the Document
     * @param i
     *            the number of the occurence
     * @param attribute
     *            name of the attribute
     * @return the value of the attribute, null if not found
     */
    public static String getAttribute(String element, Document doc, int i, String attribute) {
        Element elem = doc.getDocumentElement();

        if (elem.getTagName().equals(element)) {
            return elem.getAttribute(attribute).trim();
        }

        NodeList anl = doc.getDocumentElement().getElementsByTagName(element);

        if (anl == null) {
            return null;
        }

        Node agentnode = anl.item(i - 1);

        if (agentnode == null) {
            return null;
        }

        Element out = (Element) agentnode;

        return out.getAttribute(attribute).trim();
    }


    /**
     * Determines the root element name. This is at best a heuristic and trades
     * precision for speed.
     * 
     * @param xmlString
     *            the string to get the root element from
     * @return the root element name. If the input cannot be parsed, an empty
     *         string is returned.
     */
    public static String getRootElementName(String xmlString) {
        int offset = xmlString.indexOf("?>");

        if (offset > 0) {
            offset += 2;
        }

        int end = 0;

        int braceOpenPos = xmlString.indexOf('<', offset);
        if (braceOpenPos == -1) {
            return "";
        }
        int begin = braceOpenPos + 1;
        for (int i = begin; (end == 0) && (i < xmlString.length()); i++) {
            char current = xmlString.charAt(i);
            switch (current) {
                case ' ':
                case '/':
                case '>':
                    end = i;
                    break;
                default: //
            }
        }

        return xmlString.substring(begin, end);
    }

}
