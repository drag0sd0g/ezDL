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

package de.unidue.inf.is.ezdl.dlfrontend.query;

import org.antlr.runtime.RecognitionException;

import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;
import de.unidue.inf.is.ezdl.dlcore.data.query.Query;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNode;



public interface QueryFactory {

    /**
     * Serializes a query tree recursively.
     * 
     * @return java.lang.String
     */
    String getTextForQueryNode(QueryNode node);


    /**
     * Serializes a query tree recursively.
     * 
     * @return java.lang.String
     */
    String getTextForQueryNode(QueryNode node, Field defaultFieldCode);


    /**
     * Converts a query string into the internal query representation.
     * 
     * @param query
     *            the query string
     * @return a Query object
     * @throws RecognitionException
     * @throws NoSuchFieldCodeException
     */
    Query parse(String query) throws RecognitionException, NoSuchFieldCodeException;


    /**
     * Converts a query string into the internal query representation.
     * 
     * @param query
     *            the query string
     * @param defaultFieldCode
     *            the field code to use if none is supplied in the query string
     * @return a Query object
     * @throws RecognitionException
     * @throws NoSuchFieldCodeException
     */
    Query parse(String query, Field defaultFieldCode) throws RecognitionException, NoSuchFieldCodeException;


    String cleanPhrase(String text);

}