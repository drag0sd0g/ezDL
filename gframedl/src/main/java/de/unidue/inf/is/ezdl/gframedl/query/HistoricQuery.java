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

package de.unidue.inf.is.ezdl.gframedl.query;

import java.util.Date;
import java.util.List;

import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;
import de.unidue.inf.is.ezdl.dlcore.data.query.Query;
import de.unidue.inf.is.ezdl.dlfrontend.helper.FieldRegistry;
import de.unidue.inf.is.ezdl.dlfrontend.i18n.I18nSupport;
import de.unidue.inf.is.ezdl.dlfrontend.query.QueryFactory;
import de.unidue.inf.is.ezdl.dlfrontend.query.parsers.simple.WebLikeFactory;
import de.unidue.inf.is.ezdl.gframedl.components.Filterable;



public class HistoricQuery extends de.unidue.inf.is.ezdl.dlcore.data.query.HistoricQuery implements Filterable {

    private static final long serialVersionUID = -9141904526426010331L;

    private static QueryFactory queryFactory = new WebLikeFactory(FieldRegistry.getInstance(), Field.TEXT);


    public HistoricQuery(List<String> wrappers, Query query, String queryId, int searchResultsCount, Date timestamp) {
        super(wrappers, query, queryId, searchResultsCount, timestamp);
    }


    @Override
    public String asString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getQuery().toString());
        sb.append(" (");
        sb.append(I18nSupport.getInstance().getLocString("ezdl.tools.history.results"));
        sb.append(": ");
        sb.append(getSearchResultsCount());
        sb.append(") ");
        return sb.toString();
    }


    @Override
    public String toFilterString() {
        return queryFactory.getTextForQueryNode(getQuery().getTree());
    }

}
