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
import de.unidue.inf.is.ezdl.dlfrontend.helper.FieldRegistry;



/**
 * Factory for Queries. Your one-stop resource for converting a query string
 * into the internal query representation and back to a string.
 * 
 * @author mjordan
 */
public abstract class AbstractQueryFactory implements QueryFactory {

    /**
     * Internal mapping between field identifiers like "author" and their
     * numerical representation.
     */
    private FieldRegistry registry;

    /**
     * Which field code to assume if none supplied explicitly in the query.
     */
    private Field defaultFieldCode;


    public AbstractQueryFactory(FieldRegistry registry, Field defaultFieldCode) {
        this.registry = registry;
        this.defaultFieldCode = defaultFieldCode;
    }


    /*
     * (non-Javadoc)
     * @see
     * de.unidue.inf.is.ezdl.gframedl.query.QueryFactory#getTextForQueryNode
     * (de.unidue.inf.is.ezdl.gframedl.query.QueryNode)
     */
    @Override
    public abstract String getTextForQueryNode(QueryNode node);


    /*
     * (non-Javadoc)
     * @see
     * de.unidue.inf.is.ezdl.gframedl.query.QueryFactory#getTextForQueryNode
     * (de.unidue.inf.is.ezdl.gframedl.query.QueryNode,
     * de.unidue.inf.is.ezdl.dlcore.data.fields.Field)
     */
    @Override
    public abstract String getTextForQueryNode(QueryNode node, Field defaultFieldCode);


    /*
     * (non-Javadoc)
     * @see
     * de.unidue.inf.is.ezdl.gframedl.query.QueryFactory#parse(java.lang.String)
     */
    @Override
    public abstract Query parse(String query) throws RecognitionException, NoSuchFieldCodeException;


    /*
     * (non-Javadoc)
     * @see
     * de.unidue.inf.is.ezdl.gframedl.query.QueryFactory#parse(java.lang.String,
     * de.unidue.inf.is.ezdl.dlcore.data.fields.Field)
     */
    @Override
    public abstract Query parse(String query, Field defaultFieldCode) throws RecognitionException,
                    NoSuchFieldCodeException;


    /*
     * (non-Javadoc)
     * @see
     * de.unidue.inf.is.ezdl.gframedl.query.QueryFactory#cleanPhrase(java.lang
     * .String)
     */
    @Override
    public abstract String cleanPhrase(String text);


    /**
     * @return the registry
     */
    protected FieldRegistry getRegistry() {
        return registry;
    }


    /**
     * @param registry
     *            the registry to set
     */
    protected void setRegistry(FieldRegistry registry) {
        this.registry = registry;
    }


    /**
     * @return the defaultFieldCode
     */
    protected Field getDefaultFieldCode() {
        return defaultFieldCode;
    }


    /**
     * @param defaultFieldCode
     *            the defaultFieldCode to set
     */
    protected void setDefaultFieldCode(Field defaultFieldCode) {
        this.defaultFieldCode = defaultFieldCode;
    }

}
