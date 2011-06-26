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

package de.unidue.inf.is.ezdl.dlcore.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.unidue.inf.is.ezdl.dlcore.data.dldata.Document;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.Person;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.PersonList;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.TextDocument;



/**
 * This is the object ID factory. It builds object IDs from given objects.
 * <p>
 * CODER, BEWARE! If you change the constants in this class or the behavior, it
 * is totally possible that the whole stuff that people stored in ezDL is no
 * longer available because the keys are wrong.
 * <p>
 * CHANGING THIS CODE MEANS MAJOR TESTING ON A REPLICA OF THE LIVE SYSTEM.
 * <p>
 * The main problem, though, is that incomplete documents in the repository
 * cannot be merged any longer with new documents because they might not
 * generate the same object ID and thus cannot be matched against each other.
 * 
 * @author mjordan
 */
public final class OIDFactory {

    private static final char SEP = ':';
    private static final String FILTER_STRING = "[^a-zA-Z0-9]";


    /**
     * Calculates an object ID for the given document.
     * <p>
     * Currently this takes the following pieces of information:
     * <ul>
     * <li>List of authors</li>
     * <li>Title</li>
     * <li>Year</li>
     * </ul>
     * 
     * @param d
     *            the document
     * @return the object ID or null if the document had insufficient data.
     */
    public static String calcOid(Document d) {
        StringBuffer out = new StringBuffer();
        String type = getType(d);
        String title = d.getTitle();
        PersonList authors = d.getAuthorList();

        if ((type == null) || (title == null) || (authors == null) || (authors.isEmpty())) {
            return null;
        }
        out.append(type);
        out.append(SEP);
        out.append(normalizeString(title));
        out.append(SEP);
        out.append(d.getYear());
        if (d.getAuthorList() != null) {
            out.append(SEP);
            out.append(normalizeAuthors(authors));
        }
        return out.toString();
    }


    private static String getType(Document d) {
        String type = "nan";
        Class<?> clazz = d.getClass();
        if (clazz.equals(TextDocument.class)) {
            type = "txt";
        }
        else {
            throw new IllegalArgumentException("The document type " + clazz + " is not known to the OIDFactory");
        }
        return type;
    }


    private static String normalizeAuthors(PersonList authorList) {
        List<String> lastNames = new ArrayList<String>();
        for (Person a : authorList) {
            String lastName = a.getLastName();
            if ((lastName != null) && (!lastName.isEmpty())) {
                lastNames.add(lastName.substring(0, 1));
            }
        }

        String[] names = lastNames.toArray(new String[lastNames.size()]);
        Arrays.sort(names);

        StringBuffer out = new StringBuffer();
        for (String n : names) {
            out.append(n);
        }
        return filterLetters(out.toString().toLowerCase());
    }


    private static String normalizeString(String str) {
        String out = str.toLowerCase();
        out = out.replaceAll("[ \t\n\r]", "");
        return filterLetters(out.toLowerCase());
    }


    private static String filterLetters(String str) {
        return str.replaceAll(FILTER_STRING, "");
    }
}
