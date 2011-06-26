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
package de.unidue.inf.is.ezdl.dlcore.data.query.tree.transformation.flatten;

import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNode;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.transformation.QueryTreeTransformer;
import de.unidue.inf.is.ezdl.dlcore.query.DepthFirstQueryWalker;
import de.unidue.inf.is.ezdl.dlcore.query.QueryTreeWalker;



/**
 * Removes unnecessary nesting from a query tree.
 * <p>
 * The tree is transformed by
 * <ul>
 * <li>moving children of an AND within an AND up one level</li>
 * <li>moving children of an OR within an OR up one level</li>
 * <li>removing empty subtrees</li>
 * <li>moving single children up one level (e.g. {AND (4=a)})</li>
 * <li>removing duplicate nodes (e.g. Title=retrieval AND Title=retrieval)
 * </ul>
 * 
 * @author mj
 */
public class FlattenerTransformer implements QueryTreeTransformer {

    /**
     * Removes unnecessary nesting.
     * <p>
     * E.g. transform (a AND (b AND c)) to (a AND b AND c).
     */
    @Override
    public QueryNode transform(QueryNode root) {
        final Flattener flattener = new Flattener(root);
        final QueryTreeWalker walker = new DepthFirstQueryWalker<Object>(flattener);
        walker.walk(root);
        final QueryNode newRoot = flattener.getRoot();
        return newRoot;
    }

}
