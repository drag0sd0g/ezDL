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

package de.unidue.inf.is.ezdl.gframedl.tools.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;

import de.unidue.inf.is.ezdl.dlcore.data.dldata.Document;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.Person;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.PersonList;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.TextDocument;
import de.unidue.inf.is.ezdl.dlcore.data.query.ResultDocument;
import de.unidue.inf.is.ezdl.dlcore.utils.StringUtils;
import de.unidue.inf.is.ezdl.dlfrontend.i18n.I18nSupport;
import de.unidue.inf.is.ezdl.gframedl.Icons;
import de.unidue.inf.is.ezdl.gframedl.components.Filterable;



/**
 * Internal model for the result item in the {@link SearchTool}.
 * 
 * @author tbeckers
 */
public final class ResultItem implements Filterable {

    private Document document;
    private ResultDocument resultDocument;
    private String docId;
    private double rsv;
    private String sources;

    private String title;
    private int year;
    private String authors;
    private Icon icon;


    /**
     * Constructor.
     * 
     * @param d
     *            the document to take values from
     * @param wrapperNames
     */
    public ResultItem(ResultDocument d, Map<String, String> wrapperNames) {
        document = d.getDocument();
        resultDocument = d;
        docId = document.getOid();

        title = document.getTitle();
        if (title == null) {
            title = I18nSupport.getInstance().getLocString("strings.unknown_title");
        }

        year = document.getYear();

        if (document.getAuthorList() == null) {
            authors = I18nSupport.getInstance().getLocString("strings.unknown_authors");
        }
        else {
            PersonList authorList = document.getAuthorList();
            StringBuilder sb = new StringBuilder();
            Iterator<Person> it = authorList.iterator();
            while (it.hasNext()) {
                Person author = it.next();
                sb.append(author.toString());
                if (it.hasNext()) {
                    sb.append("; ");
                }
            }
            authors = sb.toString();
        }

        rsv = d.getRsv();

        if (document.getClass() == TextDocument.class) {
            icon = Icons.MEDIA_TEXT.get16x16();
        }

        sources = getSources(d, wrapperNames);
        if (sources == null) {
            sources = "";
        }
    }


    /**
     * Returns the list of sources in a format as "source1, source2, source3",
     * with the sources listed in alphabetical order.
     * 
     * @param d
     *            the document to extract sources from
     * @param wrapperNames
     * @return the list of sources
     */
    private String getSources(ResultDocument d, Map<String, String> wrapperNames) {
        StringBuffer out = new StringBuffer();

        List<String> sources = new ArrayList<String>(d.getSources());
        Collections.sort(sources);
        if (sources.size() != 0) {
            String firstSource = sources.get(0);

            out.append(wrapperNameById(wrapperNames, firstSource));

            for (int i = 1; (i < sources.size()); i++) {
                String a = sources.get(i);
                out.append(", ");
                out.append(wrapperNameById(wrapperNames, a));
            }
        }
        return out.toString();
    }


    private static String wrapperNameById(Map<String, String> wrapperNames, String id) {
        if (wrapperNames != null) {
            String name = wrapperNames.get(id);
            if (!StringUtils.isEmpty(name)) {
                return name;
            }
        }
        return id;
    }


    public String getDocId() {
        return docId;
    }


    public String getTitle() {
        return title;
    }


    public int getYear() {
        return year;
    }


    public String getAuthors() {
        return authors;
    }


    public double getRsv() {
        return rsv;
    }


    public Icon getIcon() {
        return icon;
    }


    public String getSourceDLs() {
        return sources;
    }


    public Document getDocument() {
        return document;
    }


    public ResultDocument getResultDocument() {
        return resultDocument;
    }


    @Override
    public String toString() {
        return title + " " + authors + " " + year + " " + sources;
    }


    @Override
    public String toFilterString() {
        return (title + " " + authors + " " + year + " " + sources).replaceAll("[\\p{Punct}&&[^-]]", "");
    }
}
