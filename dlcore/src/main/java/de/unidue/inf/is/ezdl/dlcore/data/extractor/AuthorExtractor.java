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
import de.unidue.inf.is.ezdl.dlcore.data.dldata.Person;



/**
 * Implementation of the author extraction strategy. This class do analyze a
 * {@link Document} object and extract the author names. We do not make a
 * phonetic analyze on this names. The Problem of detecting "J. Kapitza" and
 * "Jens Kapitza" is still present. This terms differ from each other and have a
 * value count of 1 for each and not 2 for one of them.
 * 
 * @author Jens Kapitza
 * @version 28.02.2010
 */
public class AuthorExtractor extends AbstractExtractor {

    /**
     * We will extract the information from the {@link Document} as described in
     * the class documentation.
     * 
     * @param from
     *            the object which encapsulate the information.
     * @return the result list with the {@link Entry}s each of them is an
     *         author.
     */
    @Override
    protected ExtractionResult extract(Document from) {
        ExtractionResultImpl result = new ExtractionResultImpl();
        if (from.getAuthorList() != null) {
            for (Person a : from.getAuthorList()) {
                Entry e = new EntryImpl(a.toString());
                result.add(e);
            }
        }
        return result;
    }

}
