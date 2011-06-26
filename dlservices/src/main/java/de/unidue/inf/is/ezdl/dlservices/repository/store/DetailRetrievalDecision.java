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

package de.unidue.inf.is.ezdl.dlservices.repository.store;

import de.unidue.inf.is.ezdl.dlbackend.data.StoredDocument;



/**
 * This strategy decides if a given document lacks any details and retrieving
 * them is a good idea.
 * <p>
 * What "good idea" exactly means in this context is expressed in the
 * implementation of the strategy.
 * 
 * @author mjordan
 * @see SmartDetailRetrievalDecision
 * @see SimpleDetailRetrievalDecision
 */
public interface DetailRetrievalDecision {

    /**
     * Tests if a given document should be completed somehow.
     * 
     * @param stored
     *            the document to test
     * @return true, if details should be added. Else false.
     */
    boolean detailRetrievalSensible(StoredDocument stored);
}
