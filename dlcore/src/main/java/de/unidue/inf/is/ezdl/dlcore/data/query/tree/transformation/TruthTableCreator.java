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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.log4j.Logger;

import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;
import de.unidue.inf.is.ezdl.dlcore.data.query.DefaultQuery;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNode;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeBase;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeCompare;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeProximity;
import de.unidue.inf.is.ezdl.dlcore.query.AbstractBottomUpQueryConverter;



final class TruthTableCreator {

    private static Logger logger = Logger.getLogger(TruthTableCreator.class);


    private class BooleanQueryConverter extends AbstractBottomUpQueryConverter {

        private List<QueryNodeBase> queryNodes = new ArrayList<QueryNodeBase>();
        private String binaryString;


        public BooleanQueryConverter(List<QueryNodeBase> queryNodes) {
            this.queryNodes = queryNodes;
        }


        @Override
        protected String convertProximity(QueryNodeProximity node) {
            return convertQueryNode(node);
        }


        @Override
        public String convertCompare(QueryNodeCompare node) {
            return convertQueryNode(node);
        }


        private String convertQueryNode(QueryNode node) {
            int i = queryNodes.indexOf(node);
            if (i == -1) {
                throw new IllegalArgumentException();
            }
            boolean bool = boolValue(i, binaryString);

            return String.valueOf(bool);
        }


        @Override
        protected String fieldQueryEQ(boolean negated, Field field, String value) {
            throw new UnsupportedOperationException();
        }


        @Override
        protected String fieldQueryGT(boolean negated, Field field, String value) {
            throw new UnsupportedOperationException();
        }


        @Override
        protected String fieldQueryGTE(boolean negated, Field field, String value) {
            throw new UnsupportedOperationException();
        }


        @Override
        protected String fieldQueryLT(boolean negated, Field field, String value) {
            throw new UnsupportedOperationException();
        }


        @Override
        protected String fieldQueryLTE(boolean negated, Field field, String value) {
            throw new UnsupportedOperationException();
        }


        @Override
        protected String wildcardMulti() {
            throw new UnsupportedOperationException();
        }


        @Override
        protected String wildcardSingle() {
            throw new UnsupportedOperationException();
        }


        @Override
        protected String escapeToken(String token) {
            throw new UnsupportedOperationException();
        }


        @Override
        protected String andOperator() {
            return "&&";
        }


        @Override
        protected String orOperator() {
            return "||";
        }


        @Override
        protected String parenthesisOpen() {
            return "(";
        }


        @Override
        protected String parenthesisClosed() {
            return ")";
        }


        public void setBinaryString(String binaryString) {
            this.binaryString = binaryString;
        }

    }


    public enum Type {
        MINTERM, MAXTERM
    }


    public List<Integer> create(QueryNode root, List<QueryNodeBase> queryNodes, Type type) {
        int pow = (int) Math.pow(2, queryNodes.size());
        int bitCount = queryNodes.size();

        BooleanQueryConverter queryConverter = new BooleanQueryConverter(queryNodes);
        List<Integer> resultMin = new ArrayList<Integer>();
        List<Integer> resultMax = new ArrayList<Integer>();
        ScriptEngineManager factory = new ScriptEngineManager();
        ScriptEngine engine = factory.getEngineByName("JavaScript");

        for (int i = 0; i < pow; i++) {
            String binaryString = Integer.toBinaryString(i);
            if (binaryString.length() < bitCount) {
                binaryString = fill(bitCount, binaryString);
            }

            queryConverter.setBinaryString(binaryString);
            String s = queryConverter.convert(new DefaultQuery(root));

            boolean b;
            try {
                b = (Boolean) engine.eval(s);
                if (b) {
                    resultMin.add(i);
                }
                else {
                    resultMax.add(i);
                }
            }
            catch (ScriptException e) {
                logger.error(e.getMessage(), e);
                throw new IllegalStateException();
            }

        }

        switch (type) {
            case MAXTERM:
                return resultMax;
            case MINTERM:
                return resultMin;
            default:
                throw new IllegalArgumentException();
        }
    }


    public static String fill(int bitCount, String binaryString) {
        char[] fill = new char[bitCount - binaryString.length()];
        Arrays.fill(fill, '0');
        String zeroes = new String(fill);
        binaryString = zeroes + binaryString;
        return binaryString;
    }


    public static boolean boolValue(int i, String binaryString) {
        char c = binaryString.charAt(i);
        boolean bool;
        if (c == '0') {
            bool = false;
        }
        else if (c == '1') {
            bool = true;
        }
        else {
            throw new IllegalArgumentException();
        }
        return bool;
    }

}
