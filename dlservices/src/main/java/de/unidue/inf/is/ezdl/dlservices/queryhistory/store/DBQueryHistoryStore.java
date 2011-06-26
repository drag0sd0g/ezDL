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

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import de.unidue.inf.is.ezdl.dlbackend.database.BoneCPConnectionProvider;
import de.unidue.inf.is.ezdl.dlbackend.database.ConnectionProvider;
import de.unidue.inf.is.ezdl.dlcore.coding.StringCodingStrategy;
import de.unidue.inf.is.ezdl.dlcore.coding.XMLStrategy;
import de.unidue.inf.is.ezdl.dlcore.data.query.HistoricQuery;
import de.unidue.inf.is.ezdl.dlcore.utils.ClosingUtils;



public final class DBQueryHistoryStore implements QueryHistoryStore {

    private static final int QUERY_HISTORY_SIZE = 40;

    private static final String TABLE_PREFIX = "qha_";
    private static final String TABLE_NAME_QUERYHISTORY = tableName("queryhistory");

    private static Logger logger = Logger.getLogger(DBQueryHistoryStore.class);

    private StringCodingStrategy stringCodingStrategy;
    private ConnectionProvider connectionProvider;


    /**
     * Constructor initializes this class with a connection to a database.
     * 
     * @param dbProp
     *            is a connection to a database.
     */
    public DBQueryHistoryStore(Properties dbProp) {
        this.stringCodingStrategy = new XMLStrategy();
        this.connectionProvider = new BoneCPConnectionProvider(dbProp, false);
    }


    private static final String tableName(String name) {
        return TABLE_PREFIX + name;
    }


    @Override
    public boolean testConnection() {
        Connection conn = null;
        try {
            conn = connectionProvider.connection();
        }
        catch (SQLException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        finally {
            ClosingUtils.close(conn);
        }
        return true;
    }


    @Override
    public List<HistoricQuery> getQueryHistory(int userId) {
        List<HistoricQuery> result = new ArrayList<HistoricQuery>();

        Connection con = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            con = connectionProvider.connection();
            st = con.prepareStatement("select query from " + TABLE_NAME_QUERYHISTORY
                            + " where userId = ? order by timestamp desc");
            st.setInt(1, userId);
            rs = st.executeQuery();
            for (int i = 0; i < QUERY_HISTORY_SIZE && rs.next(); i++) {
                HistoricQuery historicQuery;
                historicQuery = (HistoricQuery) stringCodingStrategy.decode(rs.getString(1));
                if (historicQuery != null) {
                    result.add(historicQuery);
                }
            }
            con.commit();
        }
        catch (SQLException e) {
            rollback(con);
            logger.error(e.getMessage(), e);
        }
        catch (IOException e) {
            rollback(con);
            logger.error(e.getMessage(), e);
        }
        finally {
            ClosingUtils.close(rs);
            ClosingUtils.close(st);
            ClosingUtils.close(con);
        }
        return result;
    }


    @Override
    public void storeQuery(HistoricQuery query, int userId) {
        Connection con = null;
        PreparedStatement st = null;
        try {
            con = connectionProvider.connection();
            st = con.prepareStatement("insert into " + TABLE_NAME_QUERYHISTORY
                            + " (query, timestamp, userid) values (?,?,?)");
            st.setString(1, stringCodingStrategy.encode(query));
            st.setTimestamp(2, new Timestamp(query.getTimestamp().getTime()));
            st.setInt(3, userId);
            st.execute();
            con.commit();
        }
        catch (SQLException e) {
            rollback(con);
            logger.error(e.getMessage(), e);
        }
        catch (IOException e) {
            rollback(con);
            logger.error(e.getMessage(), e);
        }
        finally {
            ClosingUtils.close(st);
            ClosingUtils.close(con);
        }
    }


    @Override
    public void clear(int userId) {
        Connection con = null;
        PreparedStatement st = null;
        try {
            con = connectionProvider.connection();
            st = con.prepareStatement("delete from " + TABLE_NAME_QUERYHISTORY + " where userid = ?");
            st.setInt(1, userId);
            st.execute();
            con.commit();
        }
        catch (SQLException e) {
            rollback(con);
            logger.error(e.getMessage(), e);
        }
        finally {
            ClosingUtils.close(st);
            ClosingUtils.close(con);
        }
    }


    private static void rollback(Connection connection) {
        try {
            connection.rollback();
        }
        catch (SQLException e) {
            logger.error(e.getMessage(), e);
        }
    }

}
