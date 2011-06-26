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

package de.unidue.inf.is.ezdl.dlcore;

import de.unidue.inf.is.ezdl.dlcore.data.OIDFactory;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.Person;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.PersonList;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.TextDocument;
import de.unidue.inf.is.ezdl.dlcore.data.query.ResultDocument;



/**
 * Factory to make it easier getting documents for testing.
 * 
 * @author mjordan
 */
public final class DocumentFactory {

    /**
     * Creates a document.
     * 
     * @param title
     *            title of the object
     * @param year
     *            publication year
     * @param authors
     *            authors
     * @return the object
     */
    public static TextDocument createDocument(String title, int year, String... authors) {
        TextDocument d = new TextDocument();
        PersonList authorList = new PersonList();
        if (authors != null) {
            for (String authorName : authors) {
                Person author = new Person(authorName);
                authorList.add(author);
            }
        }
        d.setAuthorList(authorList);
        d.setTitle(title);
        d.setYear(year);
        final String oid = OIDFactory.calcOid(d);
        if (oid != null) {
            d.setOid(oid);
        }
        return d;
    }


    public static ResultDocument createResultDocument(String abst, String title, int year, String... authors) {
        TextDocument document = DocumentFactory.createDocument(title, year, authors);
        document.setAbstract(abst);
        ResultDocument result = new ResultDocument(document);
        result.addSource("dummysource");
        return result;
    }

}