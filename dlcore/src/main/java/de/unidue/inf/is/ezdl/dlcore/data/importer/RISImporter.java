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

package de.unidue.inf.is.ezdl.dlcore.data.importer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;

import de.unidue.inf.is.ezdl.dlcore.data.dldata.Document;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.Person;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.PersonList;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.TextDocument;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.URLList;
import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;
import de.unidue.inf.is.ezdl.dlcore.utils.StringUtils;



/**
 * Importer for RIS files.
 * <p>
 * This importer is NOT capable of multithreading.
 * 
 * @author mjordan
 */
public class RISImporter implements DocumentImporter {

    private static final String PAGES_SEP = "-";

    private String lastHeader;


    @Override
    public Document convert(String input) {
        Document doc = null;
        lastHeader = null;
        BufferedReader reader = new BufferedReader(new StringReader(input));
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                doc = convertLine(doc, line);
                if (doc == null) {
                    // An error occurred or the document type is unknown
                    return null;
                }
            }
        }
        catch (IOException e) {
            doc = null;
        }
        return doc;
    }


    private Document convertLine(Document doc, String line) {
        String[] parts = line.split("  - ");
        String header;
        String content;

        if ((parts.length == 2) && (parts[0].length() == 2)) {
            header = parts[0];
            content = parts[1];
        }
        else if ((parts.length == 1) && (parts[0].matches("..  -"))) {
            header = parts[0];
            content = "";
        }
        else {
            header = lastHeader;
            content = line;
        }

        if (header == null) {
            return doc;
        }

        header = header.trim();
        content = content.trim();

        if (StringUtils.isEmpty(content)) {
            return doc;
        }

        if ("TY".equals(header)) {
            doc = createSuitableDocument(content);
        }

        if (doc == null) {
            return null;
        }

        if ("TI".equals(header) || "T1".equals(header)) {
            doc.setTitle(content);
        }
        else if ("A1".equals(header) || "A2".equals(header) || "AU".equals(header)) {
            processAuthors(doc, content);
        }
        else if ("PY".equals(header)) {
            setYear(doc, content);
        }
        else if ("SP".equals(header)) {
            String pages = (String) doc.getFieldValue(Field.PAGES);
            if (StringUtils.isEmpty(pages)) {
                doc.setFieldValue(Field.PAGES, content + PAGES_SEP);
            }
            else if (pages.startsWith(PAGES_SEP)) {
                doc.setFieldValue(Field.PAGES, content + pages);
            }
        }
        else if ("EP".equals(header)) {
            String pages = (String) doc.getFieldValue(Field.PAGES);
            if (StringUtils.isEmpty(pages)) {
                doc.setFieldValue(Field.PAGES, PAGES_SEP + content);
            }
            else if (pages.endsWith(PAGES_SEP)) {
                doc.setFieldValue(Field.PAGES, pages + content);
            }
        }
        else if ("JF".equals(header) || "JA".equals(header) || "JO".equals(header)) {
            doc.setFieldValue(Field.JOURNAL, content);
        }
        else if ("VL".equals(header)) {
            doc.setFieldValue(Field.VOLUME, content);
        }
        else if ("IS".equals(header)) {
            doc.setFieldValue(Field.NUMBER, content);
        }
        else if ("PB".equals(header)) {
            doc.setFieldValue(Field.PUBLISHER, content);
        }
        else if ("N2".equals(header) || "AB".equals(header)) {
            String abs = (String) doc.getFieldValue(Field.ABSTRACT);
            if (abs != null) {
                abs = abs + " " + content;
            }
            else {
                abs = content;
            }
            doc.setFieldValue(Field.ABSTRACT, abs);
        }
        else if ("L1".equals(header) || "L2".equals(header)) {
            URLList urls = doc.getDetailURLs();
            if (urls == null) {
                urls = new URLList();
            }
            try {
                urls.add(new URL(content));
            }
            catch (MalformedURLException e) {
            }
            doc.setDetailURLs(urls);
        }
        else if ("DO".equals(header)) {
            doc.setFieldValue(Field.DOI, content);
        }

        lastHeader = header;

        return doc;
    }


    private void processAuthors(Document doc, String content) {
        PersonList authors = doc.getAuthorList();
        if (authors == null) {
            authors = new PersonList();
        }
        String parts[] = content.split(",");
        if (parts.length == 2) {
            authors.add(new Person(parts[1].trim(), parts[0].trim()));
        }

        doc.setAuthorList(authors);
    }


    private void setYear(Document doc, String content) {
        String[] parts = content.split("/");
        if (parts.length == 0) {
            return;
        }
        try {
            final int year = Integer.parseInt(parts[0]);
            doc.setYear(year);
        }
        catch (NumberFormatException e) {
        }
    }


    private Document createSuitableDocument(String content) {
        if ("JOUR".equals(content) || "MGZN".equals(content) || "PAMP".equals(content) || "RPRT".equals(content)
                        || "THES".equals(content) || "BOOK".equals(content)) {
            return new TextDocument();
        }
        return null;
    }

}
