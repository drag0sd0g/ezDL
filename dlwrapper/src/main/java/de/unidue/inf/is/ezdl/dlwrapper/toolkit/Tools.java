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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;



/**
 * This Class contains function for testing and debugging.
 */
public final class Tools {

    private Tools() {
    }


    public static Document xmlDoc(String xml) throws SAXException {
        if (!xml.startsWith("<?xml version=\"1.0\"")) {
            xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + xml;
        }

        javax.xml.parsers.DocumentBuilderFactory factory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
        // new DocumentBuilder
        // factory.setExpandEntityReferences(true);
        DocumentBuilder builder = null;
        try {
            factory.setValidating(false);
            factory.setSchema(null);
            builder = factory.newDocumentBuilder();
            builder.setEntityResolver(new EntityResolver() {

                @Override
                public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
                    return new InputSource(new StringReader(""));
                }
            });
        }
        catch (ParserConfigurationException e) {
            // LOG.error(e.getMessage(),e);
        }

        // use Dom level 2 Interface DOMImplementation, to have API for
        // System.err.println("tools.XMLDoc: before\n " + xml + "\n");
        xml = xml.replaceAll(" & ", " &amp; ");
        Document doc = builder.newDocument();
        try {
            doc = builder.parse(new InputSource(new StringReader(xml)));
        }
        catch (InternalError e) {
            System.err.println("XML" + xml + "\n" + e.getMessage());
            e.printStackTrace();
        }
        catch (SAXException e1) {
            // displayExceptionToUser(e1);
            System.err.println("XML" + xml + "\n" + e1.getMessage());
            e1.printStackTrace();
            throw e1;
        }
        catch (IOException e2) {
            System.err.println("XML" + xml + "\n" + e2.getMessage());
            e2.printStackTrace();
            // LOG.error("XML" + xml + "\n" + e2.getMessage(),e2);
        }
        catch (Exception e) {
            // LOG.error("XML" + xml + "\n" + e.getMessage(),e);
            System.err.println("XML" + xml + "\n" + e.getMessage());
            e.printStackTrace();
        }
        finally {
            // throw EzDLCouldNotParseException;
        }

        return doc;
    }


    /**
     * Reads an XML Document from a file.
     * 
     * @param filename
     *            name of the file which includes the XML Document.
     * @return read XML Document or null if an error occurs.
     */
    public static Document readXMLFile(String filename) {
        try {
            System.out.println("Reading XML file '" + filename + "' ...");
            return createDocument(new FileInputStream(filename));
        }
        catch (Exception e) {
            System.err.println("Can not open file " + filename + "!");
            System.err.println(e);
            return null;
        }
    }


    /**
     * Reads an XML Document from a file.
     * 
     * @param url
     *            URL of the file which includes the XML Document.
     * @return read XML Document or null if an error occurs.
     */
    public static Document readXMLFile(URL url) {
        try {
            System.out.println("Reading XML file \"" + url + "\" ...");
            return createDocument(url.openStream());
        }
        catch (java.io.IOException e) {
            System.err.println("Caught IOException in readXMLFile!");
            System.err.println(e);
            return null;
        }
    }


    public static Document createDocument(InputStream stream) {
        try {
            InputSource in = new InputSource(stream);
            DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
            fact.setNamespaceAware(true);
            return fact.newDocumentBuilder().parse(in);
        }
        catch (Exception e) {
            System.err.println("Can not create Document!");
            System.err.println(e);
            return null;
        }
    }


    /**
     * Writes a textrepresentation of a node to a PrintStream.
     * 
     * @param node
     *            to be printed node
     * @param out
     *            PrintStream in which to print
     */
    public static void printNode(Node node, PrintStream out) {
        try {
            Transformer serializer = TransformerFactory.newInstance().newTransformer();
            serializer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            if (isTextNode(node)) {
                StringBuffer sb = new StringBuffer(node.getNodeValue());
                for (Node nn = node.getNextSibling(); isTextNode(nn); nn = nn.getNextSibling()) {
                    sb.append(nn.getNodeValue());
                }
                out.print(sb);
            }
            else {
                serializer.transform(new DOMSource(node), new StreamResult(out));
            }
            out.println();
        }
        catch (Exception e) {
            System.err.println(e);
        }
    }


    /**
     * Checks in a node contains only text. Nodes that contains only text are
     * the attributes of an element or textnodes.
     * 
     * @param node
     *            Node to be checked
     * @return true if the node is a textnode; otherwise false.
     */
    public static boolean isTextNode(Node node) {
        if (node == null) {
            return false;
        }
        short nodeType = node.getNodeType();
        return nodeType == Node.CDATA_SECTION_NODE || nodeType == Node.TEXT_NODE || nodeType == Node.ATTRIBUTE_NODE;
    }


    /**
     * return the textrepresentation of a node.
     * 
     * @param node
     *            node to be printed.
     * @return the textrepresentation of the given node.
     */
    public static String nodeAsString(Node node) {
        if (node.getNodeType() == Node.DOCUMENT_NODE) {
            node = ((Document) node).getDocumentElement();
        }
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            try {
                OutputFormat format = new OutputFormat(node.getOwnerDocument());
                format.setOmitXMLDeclaration(true);
                StringWriter out = new StringWriter();
                XMLSerializer serial = new XMLSerializer(out, format);
                serial.asDOMSerializer();
                serial.serialize((Element) node);
                return out.toString();
            }
            catch (Exception e) {
                System.err.println("Exception in nodeAsString!");
                System.err.println(e);
                return null;
            }
        }
        return null;
    }


    /**
     * Executes a PERL substitution like "s/.../.../" on a string.
     * 
     * @param regexp
     *            PERL substitution expression.
     * @param s
     *            Text on which the substitution will be executed.
     * @return result of the substitution.
     */
    public static String substitute(String regexp, String s) {
        StringTokenizer tokens = new StringTokenizer(regexp, "/", false);
        tokens.nextToken(); // s
        String pattern = tokens.nextToken();
        String replaceWith = tokens.hasMoreElements() ? tokens.nextToken() : "";
        return substitute(pattern, replaceWith, s);
    }


    /**
     * Executes a substitution on a string.
     * 
     * @param pattern
     *            what to replace
     * @param replaceWith
     *            with what to replace
     * @param s
     *            Text on which the substitution will be executed.
     * @return result of the substitution.
     */
    public static String substitute(String pattern, String replaceWith, String s) {
        try {
            Pattern p = Pattern.compile(pattern);
            Matcher m = p.matcher(s);
            return m.replaceAll(replaceWith);
        }
        catch (java.util.regex.PatternSyntaxException e) {
            System.err.println("PatternSyntaxException in substitute!");
            System.err.println(e);
        }
        return s;
    }


    /**
     * Sets the proxy property in this system.
     * 
     * @param host
     *            Hostname of the proxy server
     * @param port
     *            Port of the proxy server
     */
    public static void setProxy(String host, int port) {
        System.getProperties().put("proxySet", "true");
        System.getProperties().put("proxyHost", host);
        System.getProperties().put("proxyPort", Integer.toString(port));
    }


    /**
     * Reads characters from an InputStream and returns them in a string.
     * 
     * @param in
     *            to be read inputstream.
     * @return Text read from the inputstream.
     */
    public static String readInputStream(InputStream in) {
        return processInputStream(in, true);
    }


    /**
     * Reads characters from an InputStream, removes unwanted characters and
     * returns them in a string.
     * 
     * @param in
     *            to be read inputstream.
     * @return Text read from the inputstream.
     */
    public static String cleanInputStream(InputStream in) {
        return processInputStream(in, true);
    }


    private static String processInputStream(InputStream in, boolean clean) {
        StringBuilder bResult = new StringBuilder(10000);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        }
        catch (UnsupportedEncodingException e1) {
            System.err.println("Could not encode Inputstream");
            e1.printStackTrace();
        }
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                bResult.append(line).append('\n');
            }
            reader.close();
        }
        catch (java.io.IOException e) {
            System.err.println("Error while reading webpage: " + e);
        }

        String firstClean = bResult.toString();
        if (clean) {
            firstClean = cleanLine(firstClean);
        }

        return firstClean;
    }


    // TODO use cleanLine from Tools (dlcore)?
    public static String cleanLine(String xml) {

        /** Fetch non entities from string */
        String non_entities = "&(?![a-zA-Z]{2,6};|#[0-9]{3,5};)";

        xml = xml.replaceAll(non_entities, "&amp;");
        xml = xml.replaceAll(" < ", " &lt; ");
        // xml = xml.replaceAll(" > "," &gt; ");
        xml = xml.replaceAll("&#x14;", " -- ");
        xml = xml.replaceAll("&mdash;", " -- ");
        xml = xml.replaceAll("&#x6;", " ");
        xml = xml.replaceAll("&#x7;", " ");
        xml = xml.replaceAll("&#x8;", " ");
        xml = xml.replaceAll("&#124;", " ");
        xml = xml.replaceAll("&#xc;", " ");
        xml = xml.replaceAll("&#268;", "C");
        xml = xml.replaceAll("&#263;", "c");
        xml = xml.replaceAll("&#x1c;", " ");
        xml = xml.replaceAll("&ldquo;", "");
        xml = xml.replaceAll("&rdquo;", "");
        xml = xml.replaceAll("&iacut;", "");
        xml = xml.replaceAll("&lquo;", "");
        xml = xml.replaceAll("&rquo;", "");
        xml = xml.replaceAll("&nbsp;", " ");
        xml = xml.replaceAll("&copy;", "");
        return xml;
    }


    /**
     * Reads characters from an InputStream an writes them in a file.
     * 
     * @param in
     *            to be read inputstream.
     * @param filename
     *            name of the to be written file.
     */
    public static void saveInputStream(InputStream in, String filename) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        try {
            FileWriter file = new FileWriter(filename);
            while (reader.ready()) {
                file.write(reader.readLine() + "\n");
            }
            file.close();
            reader.close();
        }
        catch (java.io.IOException e) {
            System.err.println("IOException was caught in savaInputStream!");
            System.err.println(e);
        }
    }


    /**
     * Returns the first element childnode of a given element.
     * 
     * @param element
     *            given element
     * @return fist element childnode of the given element
     */
    public static Element getFirstChildElement(Node element) {
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
     * Extract and concatanate all text nodes of a node.
     * 
     * @param node
     *            The element node
     * @return Concatenated text nodes
     */
    public static String extractAllTextNodes(Node node) {
        if (node.getNodeType() == Node.TEXT_NODE) {
            return node.getNodeValue();
        }
        StringBuilder result = new StringBuilder();
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            result.append(extractAllTextNodes(children.item(i)));
        }
        return result.toString();
    }


    /**
     * Tries to find a substring in a string that matches a pattern.
     * 
     * @param pattern
     *            Pattern to match.
     * @param s
     *            String in which be searched.
     * @return First substring that matches or null otherwise.
     */
    public static String find(String pattern, String s) {
        try {
            Pattern p = Pattern.compile(pattern);
            Matcher m = p.matcher(s);
            if (m.find()) {
                // System.err.println("m.group(): " + m.group());
                return m.group();
            }

            // return (m.find() ? m.group() : null);
        }
        catch (java.util.regex.PatternSyntaxException e) {
            System.err.println("PatternSyntaxException in find!");
            System.err.println(e);
        }
        return null;
    }


    /**
     * Wandelt ein XML-Document in einen String um.
     * 
     * @param doc
     *            das XML-Document
     * @return ein XML-String
     */
    public static String xmlString(Document doc) {
        return xmlString(doc, doc.getDocumentElement());
    }


    /**
     * Wandelt ein Teil eines XML-Documents in einen String um.
     * 
     * @param doc
     *            das XML-Document
     * @return ein XML-String
     */
    public static String xmlString(Document doc, Element elm) {
        if (doc == null) {
            return "";
        }

        try {
            OutputFormat format = new OutputFormat(doc);
            StringWriter stringOut = new StringWriter();
            XMLSerializer serial = new XMLSerializer(stringOut, format);
            serial.asDOMSerializer();

            serial.serialize(elm);

            return stringOut.toString();
        }
        catch (Exception e) {
            System.err.println(e);
        }

        return "";
    }


    /**
     * Saves the given object.
     * 
     * @param file
     *            file used for saving
     * @param result
     *            object to be saved
     */
    public static void save(File file, Object result) throws IOException {
        if (result == null) {
            Logger.getLogger(ToolkitAPI.class).error("Result is null for file " + file);
            return;
        }
        if (file == null) {
            Logger.getLogger(ToolkitAPI.class).error("File is null");
            if (result != null) {
                try {
                    StringWriter sw = new StringWriter();
                    save(new PrintWriter(sw), result);
                    Logger.getLogger(ToolkitAPI.class).error("Object: " + sw);
                }
                catch (Exception ex) {
                    Logger.getLogger(ToolkitAPI.class).error("Serialisation did not work", ex);
                }
            }
            return;
        }
        PrintWriter pw = new PrintWriter(new FileWriter(file));
        save(pw, result);
        pw.close();
    }


    /**
     * Serialises the given object as XML. Only instances of Vector, Hashtable
     * and URL can be handled correctly, everything else is serialised as a
     * string.
     * 
     * @param pw
     *            PrintWriter for serialising the XML
     * @param object
     *            object to be saved
     */
    private static void save(PrintWriter pw, Object object) {
        if (object instanceof Vector) {
            Vector<?> vec = (Vector<?>) object;
            pw.println("<vector>");
            for (Object element : vec) {
                pw.println("<item>");
                save(pw, element);
                pw.println("</item>");
            }
            pw.println("</vector");
            return;
        }
        if (object instanceof Hashtable) {
            Hashtable<?, ?> hash = (Hashtable<?, ?>) object;
            pw.println("<hashtable>");
            for (Object key : hash.keySet()) {
                pw.println("<item key=\"" + key + "\">");
                save(pw, hash.get(key));
                pw.println("</item>");
            }
            pw.println("</hashtable>");
            return;
        }
        if (object instanceof URL) {
            URL url = (URL) object;
            pw.println("<url href=\"" + url + "\"/>");
            return;
        }
        pw.println("<string>" + toXML(object.toString()) + "</string>");
    }


    /**
     * Converts some characters in a string into entities: These characters are
     * converted:
     * <ul>
     * <li>&szlig;</li>
     * <li>&quot;</li>
     * <li>&lt;</li>
     * <li>&gt;</li>
     * <li>&amp;</li>
     * </ul>
     * 
     * @param text
     *            text
     * @return converted text
     */
    public static String toXML(String text) {
        text = replace(text, "&", "&amp;");
        text = replace(text, "\"", "&quot;");
        text = replace(text, "<", "&lt;");
        text = replace(text, ">", "&gt;");
        return text;

    }


    /**
     * Replaces all occurences of a string by another string.
     * 
     * @param str
     *            string to modify
     * @param matchStr
     *            string to replace
     * @param replaceStr
     *            replacement string
     * @return modified string
     */
    public static String replace(String str, String matchStr, String replaceStr) {
        if (str == null) {
            return null;
        }
        int i;
        int h = 0;
        StringBuffer returnStr = new StringBuffer();
        while ((i = str.indexOf(matchStr, h)) != -1) {
            returnStr.append(str.substring(h, i) + replaceStr);
            h = i + matchStr.length();
        }
        returnStr.append(str.substring(h));
        return returnStr.toString();
    }


    /**
     * Loads the given object.
     * 
     * @param file
     *            file which is loaded
     * @return loaded object
     */
    public static Object load(File file) {
        Document doc = readXMLFile(file.toString());
        return load(doc.getDocumentElement());
    }


    /**
     * Deserialises the given object from XML. Only instances of Vector,
     * Hashtable and URL can be handled correctly, everything else is
     * deserialised as a string.
     * 
     * @param element
     *            element to be deserialised
     * @return deserialised object
     */
    private static Object load(Element element) {
        String name = element.getTagName();
        if (name.equals("vector")) {
            NodeList nl = element.getChildNodes();
            Vector<Object> vec = new Vector<Object>();
            for (int i = 0; i < nl.getLength(); i++) {
                Node n = nl.item(i);
                if (n instanceof Element) {
                    Element e = (Element) n;
                    if (e.getTagName().equals("item")) {
                        vec.add(load(getFirstChildElement(e)));
                    }
                }
            }
            return vec;
        }
        if (name.equals("hashtable")) {
            NodeList nl = element.getChildNodes();
            Hashtable<Object, Object> hash = new Hashtable<Object, Object>(nl.getLength());
            for (int i = 0; i < nl.getLength(); i++) {
                Node n = nl.item(i);
                if (n instanceof Element) {
                    Element e = (Element) n;
                    if (e.getTagName().equals("item")) {
                        hash.put(e.getAttribute("key"), load(getFirstChildElement(e)));
                    }
                }
            }
            return hash;
        }
        if (name.equals("url")) {
            try {
                return new URL(element.getAttribute("href"));
            }
            catch (MalformedURLException e) {
                e.printStackTrace();
            }
            return null;
        }
        if (name.equals("string")) {
            return element.getFirstChild().getNodeValue();
        }
        return null;
    }

}