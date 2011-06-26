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

package de.unidue.inf.is.ezdl.dlservices.log.store;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.google.common.collect.Multimap;

import de.unidue.inf.is.ezdl.dlbackend.database.ConnectionProvider;
import de.unidue.inf.is.ezdl.dlcore.message.content.UserLogNotify;
import de.unidue.inf.is.ezdl.dlcore.utils.ClosingUtils;



/**
 * A implementation of the user log store backed by a SQL database.
 * 
 * @author tbeckers
 */
public class DBUserLogStore implements UserLogStore {

    private static final String TABLE_PREFIX = "ula_";

    private static final String TABLE_NAME_SESSIONS = tableName("logsession");
    private static final String TABLE_NAME_EVENT = tableName("logevent");
    private static final String TABLE_NAME_PARAMS = tableName("logeventparams");

    private static Logger logger = Logger.getLogger(DBUserLogStore.class);

    private ConnectionProvider connectionProvider;


    public DBUserLogStore(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }


    private static final String tableName(String name) {
        return TABLE_PREFIX + name;
    }


    @Override
    public void init() {
    }


    @Override
    public void storeUserLog(UserLogNotify logNotify) {
        Connection connection = null;
        try {
            connection = connectionProvider.connection();

            int sequenceNumber = logNotify.getSequenceNumber();
            String sessionId = logNotify.getSessionId();
            long timestamp = logNotify.getBackendTimestamp();
            long localTimestamp = logNotify.getClientTimestamp();
            String eventName = logNotify.getEventName();
            Multimap<String, String> parameters = logNotify.getParameters();

            PreparedStatement preparedStatement = connection
                            .prepareStatement(
                                            "insert into "
                                                            + TABLE_NAME_EVENT
                                                            + " (sessionid, sequencenumber, eventtimestamp, eventtimestampms, eventlocaltimestamp, eventlocaltimestampms, name) values (?, ?, ?, ?, ?, ?, ?)",
                                            Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, sessionId);
            preparedStatement.setInt(2, sequenceNumber);
            preparedStatement.setTimestamp(3, new Timestamp(timestamp));
            preparedStatement.setLong(4, timestamp);
            preparedStatement.setTimestamp(5, new Timestamp(localTimestamp));
            preparedStatement.setLong(6, localTimestamp);
            preparedStatement.setString(7, eventName);
            preparedStatement.execute();

            String eventid;
            ResultSet rs2 = preparedStatement.getGeneratedKeys();
            if (rs2.next()) {
                eventid = rs2.getString(1);
            }
            else {
                throw new SQLException();
            }

            PreparedStatement preparedStatement2 = connection.prepareStatement("insert into " + TABLE_NAME_PARAMS
                            + " (eventid, paramname, paramvalue, sequence) values (?, ?, ?, ?)");
            for (Entry<String, Collection<String>> entry : parameters.asMap().entrySet()) {
                List<String> values = (List<String>) entry.getValue();
                for (int i = 0; i < values.size(); i++) {
                    String value = values.get(i);
                    preparedStatement2.setString(1, eventid);
                    preparedStatement2.setString(2, entry.getKey());
                    preparedStatement2.setString(3, value);
                    preparedStatement2.setInt(4, i);

                    preparedStatement2.addBatch();
                }

            }
            preparedStatement2.executeBatch();
            connection.commit();
        }
        catch (SQLException e) {
            logger.error(e.getMessage(), e);
            rollback(connection);
        }
        finally {
            ClosingUtils.close(connection);
        }
    }


    @Override
    public void login(String sessionId, String login, long start, String type) {
        Connection connection = null;
        try {
            connection = connectionProvider.connection();
            PreparedStatement preparedStatement = connection.prepareStatement("insert into " + TABLE_NAME_SESSIONS
                            + " (sessionid, login, start, stop, type) values(?, ?, ?, null, ?)");
            preparedStatement.setString(1, sessionId);
            preparedStatement.setString(2, login);
            preparedStatement.setTimestamp(3, new Timestamp(start));
            preparedStatement.setString(4, type);
            preparedStatement.execute();
            connection.commit();
        }
        catch (SQLException e) {
            logger.error(e.getMessage(), e);
            rollback(connection);
        }
        finally {
            ClosingUtils.close(connection);
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


    @Override
    public void logout(String sessionId, long stop) {
        Connection connection = null;
        try {
            connection = connectionProvider.connection();
            PreparedStatement preparedStatement = connection.prepareStatement("update " + TABLE_NAME_SESSIONS
                            + " set stop=? where sessionid=?");
            preparedStatement.setTimestamp(1, new Timestamp(stop));
            preparedStatement.setString(2, sessionId);
            preparedStatement.execute();
            connection.commit();
        }
        catch (SQLException e) {
            logger.error(e.getMessage(), e);
            rollback(connection);
        }
        finally {
            ClosingUtils.close(connection);
        }
    }

}
