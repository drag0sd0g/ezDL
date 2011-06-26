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

package de.unidue.inf.is.ezdl.dlfrontend.query.parsers.simple;

import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeBool;
import de.unidue.inf.is.ezdl.dlfrontend.helper.FieldRegistry;



/**
 * This variant of the {@link WebLikeFactory} operates on the assumption that OR
 * is weaker than AND, in contrast to the regular WebLikeFactory.
 * 
 * @author mjordan
 */
public class WebLikeFactoryWeakOr extends WebLikeFactory {

    public WebLikeFactoryWeakOr(FieldRegistry registry) {
        super(registry);
    }


    @Override
    protected StringBuffer doStuff(QueryNodeBool up, final QueryNodeBool boolNode, StringBuffer localTerm) {
        boolean orNodeInsideANDclause = ((boolNode.isOfType(OR)) && (up != null) && (up.isOfType(AND)));
        if (orNodeInsideANDclause) {
            // Parenthesis due to an OR inside an AND clause (like
            // "(a or b) AND c")
            StringBuffer parensTerm = new StringBuffer();
            parensTerm.append('(').append(localTerm).append(')');
            localTerm = parensTerm;
        }
        return localTerm;
    }

}
