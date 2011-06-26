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

import de.unidue.inf.is.ezdl.dlcore.data.dldata.Document;



/**
 * This interface will help to provide services for extraction.
 * 
 * @author Jens Kapitza
 * @version 28.02.2010
 */
public interface ExtractorService {

    /**
     * Extracts data from {@link Document}s.
     * 
     * @param from
     *            the {@link Document}s we will extract information from
     * @return a collection of objects. <b>null</b> if an error occurs.
     */
    ExtractionResult extract(Collection<? extends Object> from);

}
