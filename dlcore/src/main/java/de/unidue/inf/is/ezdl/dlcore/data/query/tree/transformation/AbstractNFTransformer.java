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

package de.unidue.inf.is.ezdl.dlcore.data.query.tree.transformation;

import java.util.List;

import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNode;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeBase;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeBool;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeBool.NodeType;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.transformation.TruthTableCreator.Type;
import de.unidue.inf.is.ezdl.dlcore.query.AllBaseNodesQueryTreeWalker;



abstract class AbstractNFTransformer implements QueryTreeTransformer {

    @Override
    public final QueryNode transform(QueryNode root) {
        TruthTableCreator truthTableCreator = new TruthTableCreator();

        AllBaseNodesQueryTreeWalker queryTreeWalker = new AllBaseNodesQueryTreeWalker();
        queryTreeWalker.walk(root);
        List<QueryNodeBase> queryNodes = queryTreeWalker.getQueryNodes();

        List<Integer> termIndices = truthTableCreator.create(root, queryNodes, termType());

        QueryNodeBool queryNodeBool = new QueryNodeBool(outerNodeType());
        for (int index : termIndices) {
            queryNodeBool.addChild(minTerm(index, queryNodes));
        }

        return queryNodeBool;
    }


    private QueryNodeBool minTerm(int index, List<QueryNodeBase> queryNodes) {
        String binaryString = Integer.toBinaryString(index);
        binaryString = TruthTableCreator.fill(queryNodes.size(), binaryString);

        QueryNodeBool queryNodeBool = new QueryNodeBool(innerNodeType());
        for (int j = 0; j < queryNodes.size(); j++) {
            QueryNodeBase queryNode = queryNodes.get(j);
            queryNode = queryNode.plainClone();

            if (invertNegation(j, binaryString)) {
                queryNode.setNegated(!queryNode.isNegated());
            }

            queryNodeBool.addChild(queryNode);
        }
        return queryNodeBool;
    }


    public abstract boolean invertNegation(int index, String binaryString);


    public abstract NodeType innerNodeType();


    public abstract NodeType outerNodeType();


    public abstract Type termType();
}
