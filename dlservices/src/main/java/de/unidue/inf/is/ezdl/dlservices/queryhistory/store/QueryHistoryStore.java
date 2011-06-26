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

package de.unidue.inf.is.ezdl.dlservices.queryhistory.store;

import java.util.List;

import de.unidue.inf.is.ezdl.dlcore.data.query.HistoricQuery;



public interface QueryHistoryStore {

    /**
     * Check if the database connection is working.
     * 
     * @return true, if the connection is okay, else false.
     */
    public boolean testConnection();


    public List<HistoricQuery> getQueryHistory(int userId);


    public void storeQuery(HistoricQuery queries, int userId);


    public void clear(int userId);

}