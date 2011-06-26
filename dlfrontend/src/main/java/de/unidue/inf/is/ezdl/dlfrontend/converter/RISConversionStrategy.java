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

package de.unidue.inf.is.ezdl.dlfrontend.converter;

import java.util.List;

import de.unidue.inf.is.ezdl.dlcore.data.dldata.Person;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.TextDocument;
import de.unidue.inf.is.ezdl.dlcore.utils.StringUtils;



/**
 * Conversion strategy class. Converts document to RIS format.
 */
public class RISConversionStrategy implements DocumentConversionStrategy {

    private static final String SEP = "  - ";

    private static final String NEWLINE = "\r\n";


    @Override
    public ExportResult print(TextDocument document) {
        StringBuilder ris = new StringBuilder();
        append(ris, "TY", "JOUR");
        append(ris, "TI", document.getTitle());
        append(ris, "PY", Integer.toString(document.getYear()));
        for (Person au : document.getAuthorList()) {
            append(ris, "A1", author(au));
        }
        append(ris, "N2", formatAbstract(document));
        ris.append(NEWLINE).append("ER").append(SEP);
        ExportResultText out = new ExportResultText(ris);
        return out;
    }


    private String author(Person au) {
        final String lastName = au.getLastName();
        final String firstName = au.getFirstName();
        if (!StringUtils.isEmpty(lastName)) {
            if (!StringUtils.isEmpty(firstName)) {
                return lastName + "," + firstName;
            }
            else {
                return lastName;
            }
        }
        else {
            return null;
        }
    }


    private String formatAbstract(TextDocument document) {
        final String abs = document.getAbstract();
        if (!StringUtils.isEmpty(abs)) {
            return abs.replace("\r\n", " ").replace("\n", " ").trim();
        }
        return null;
    }


    @Override
    public ExportResult print(List<TextDocument> documents) {
        StringBuilder out = new StringBuilder();

        for (TextDocument document : documents) {
            out.append(print(document));
        }

        return new ExportResultText(out);
    }


    private void append(StringBuilder ris, String tag, String content) {
        if (content != null) {
            ris.append(NEWLINE).append(tag).append(SEP).append(content);
        }
    }

}
