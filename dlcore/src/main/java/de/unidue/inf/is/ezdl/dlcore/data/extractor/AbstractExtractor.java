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

import java.util.Collection;

import org.apache.log4j.Logger;

import de.unidue.inf.is.ezdl.dlcore.data.dldata.Document;
import de.unidue.inf.is.ezdl.dlcore.data.query.ResultDocument;



/**
 * This class is intended to help extract stuff.
 */
public abstract class AbstractExtractor implements ExtractorService {

    protected static Logger logger = Logger.getLogger(AbstractExtractor.class);


    /**
     * the real implementation of "all but authors"
     * 
     * @param from
     *            the object we are reading information from
     * @return the extracted information
     */
    protected abstract ExtractionResult extract(Document from);


    @Override
    public ExtractionResult extract(Collection<? extends Object> from) {
        ExtractionResultImpl result = new ExtractionResultImpl();
        for (Object d : from) {
            ExtractionResult extracted = null;
            if (d instanceof Document) {
                extracted = extract((Document) d);
            }
            else if (d instanceof ResultDocument) {
                extracted = extract((ResultDocument) d);
            }
            result.merge(extracted);
        }
        return result;
    }


    protected ExtractionResult extract(ResultDocument from) {
        return extract(from.getDocument());
    }

}
