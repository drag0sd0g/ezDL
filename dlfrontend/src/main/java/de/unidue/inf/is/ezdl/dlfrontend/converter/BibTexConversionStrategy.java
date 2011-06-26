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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.unidue.inf.is.ezdl.dlcore.data.dldata.Person;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.PersonList;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.TextDocument;
import de.unidue.inf.is.ezdl.dlcore.utils.StringUtils;



/**
 * Conversion strategy class. Converts document to BibTex format.
 */
public class BibTexConversionStrategy implements DocumentConversionStrategy {

    /**
     * Generates random id
     * 
     * @return returns random id for BibTex entry
     */
    protected String getUUID() {
        return UUID.randomUUID().toString();
    }


    @Override
    public ExportResultText print(TextDocument document) {

        StringBuilder bibTex = new StringBuilder();

        bibTex.append("@article{").append(getUUID()).append(",\n");
        bibTex.append("title = {").append(document.getTitle()).append("},\n");
        bibTex.append("author = {");
        PersonList al = document.getAuthorList();

        List<String> authors = new ArrayList<String>();
        if (al != null) {
            for (Person a : al) {
                authors.add(a.getLastName() + ", " + a.getFirstName());
            }
        }
        bibTex.append(StringUtils.join(authors, " and "));
        bibTex.append("},\n");
        bibTex.append("year = {").append(document.getYear()).append("}\n");
        bibTex.append("}\n");

        ExportResultText out = new ExportResultText(bibTex);

        return out;
    }


    @Override
    public ExportResult print(List<TextDocument> documents) {
        StringBuilder outStr = new StringBuilder();

        boolean firstItem = true;
        for (TextDocument doc : documents) {
            ExportResultText res = print(doc);
            if (!firstItem) {
                outStr.append("\n");
            }
            firstItem = false;
            outStr.append(res.asString());
        }

        ExportResultText out = new ExportResultText(outStr);
        return out;
    }

}
