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

package de.unidue.inf.is.ezdl.dlcore.data.extractor;

import de.unidue.inf.is.ezdl.dlcore.data.dldata.Document;
import de.unidue.inf.is.ezdl.dlcore.data.query.ResultDocument;



/**
 * This class is just a simple year extraction service. From all
 * {@link Document} objects the year value is extracted.
 * 
 * @author Matthias Jordan
 */
public class LibraryExtractor extends AbstractExtractor {

    /**
     * extract the year. The year is normally an integer but to be more common
     * we will convert it to an {@link String}
     * 
     * @param d
     *            a document
     * @return the extracted informations. in this case the year.
     */
    @Override
    protected ExtractionResult extract(Document d) {
        return new ExtractionResultImpl();
    }


    @Override
    protected ExtractionResult extract(ResultDocument from) {
        ExtractionResultImpl result = new ExtractionResultImpl();
        for (String source : from.getSources()) {
            result.add(new EntryImpl(source));
        }
        return result;
    }

}
