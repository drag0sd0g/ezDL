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

package de.unidue.inf.is.ezdl.dlservices.user.store;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;

import de.unidue.inf.is.ezdl.dlbackend.database.BoneCPConnectionProvider;
import de.unidue.inf.is.ezdl.dlbackend.database.ConnectionProvider;
import de.unidue.inf.is.ezdl.dlcore.data.User;
import de.unidue.inf.is.ezdl.dlcore.security.Privilege;
import de.unidue.inf.is.ezdl.dlcore.utils.ClosingUtils;



public final class DBUserStore implements UserStore {

    private static final String TABLE_PREFIX = "ua_";

    private static final String TABLE_NAME_SESSIONHISTORY = tableName("sessionhistory");
    private static final String TABLE_NAME_USER = tableName("user");
    private static final String TABLE_NAME_USER2ROLE = tableName("user2role");
    private static final String TABLE_NAME_ROLE = tableName("role");
    private static final String TABLE_NAME_ROLE2PRIVILEGE = tableName("role2privilege");
    private static final String TABLE_NAME_PRIVILEGE = tableName("privilege");

    private Logger logger = Logger.getLogger(DBUserStore.class);

    private ConnectionProvider connectionProvider;

    private static final String USER_LOGIN = "login";
    private static final String USER_ID = "id";


    /**
     * Constructor initializes this class with a connection to a database.
     * 
     * @param dbProp
     *            is a connection to a database.
     */
    public DBUserStore(Properties dbProp) {
        this.connectionProvider = new BoneCPConnectionProvider(dbProp, true);
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


    private static final String tableName(String name) {
        return TABLE_PREFIX + name;
    }


    @Override
    public User getUser(String login) {
        logger.info("getUser " + login);
        User thisUser = new User();

        Connection conn = null;
        PreparedStatement stm = null;
        ResultSet rs = null;
        PreparedStatement stm2 = null;
        ResultSet rs2 = null;
        try {
            conn = connectionProvider.connection();
            stm = conn.prepareStatement("select " + USER_ID + ", " + USER_LOGIN + ", lastname, firstname from "
                            + TABLE_NAME_USER + " where " + USER_LOGIN + "=?");
            stm.setString(1, login);
            rs = stm.executeQuery();
            if (rs.next()) {
                thisUser.setLastName(rs.getString("lastname"));
                thisUser.setFirstName(rs.getString("firstname"));
                thisUser.setLogin(rs.getString(USER_LOGIN));

                final int id = rs.getInt(USER_ID);

                stm2 = conn.prepareStatement("select max(timestamp) from " + TABLE_NAME_SESSIONHISTORY + " where uid=?");
                stm2.setInt(1, id);
                rs2 = stm2.executeQuery();
                if (rs2.next()) {
                    Timestamp timestamp = rs2.getTimestamp(1);
                    if (timestamp != null) {
                        thisUser.setLastLoginTime(timestamp.getTime());
                    }
                }
                return thisUser;
            }
        }
        catch (SQLException e) {
            logger.error(e);
        }
        finally {
            ClosingUtils.close(rs);
            ClosingUtils.close(rs2);
            ClosingUtils.close(stm);
            ClosingUtils.close(stm2);
            ClosingUtils.close(conn);
        }
        return null;
    }


    @Override
    public boolean login(String userName, String password) throws Exception {
        String entrypasswd = null;
        String sql = "select password from " + TABLE_NAME_USER + " where " + USER_LOGIN + "=?";

        Connection con;
        con = connectionProvider.connection();
        PreparedStatement stm = null;
        ResultSet rs = null;

        try {
            stm = con.prepareStatement(sql);
            stm.setString(1, userName);
            rs = stm.executeQuery();
            if (rs.next()) {
                entrypasswd = rs.getString("password");
            }
            rs.close();
        }
        catch (Exception e) {
            throw new Exception("User does not exist", e);
        }
        finally {
            ClosingUtils.close(rs);
            ClosingUtils.close(stm);
            ClosingUtils.close(con);
        }
        return password.equals(entrypasswd);
    }


    @Override
    public void saveSessionIdForUserLogin(String sessionId, String login) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String command = "insert into " + TABLE_NAME_SESSIONHISTORY + "(sessionid, uid, timestamp) values(?, ?, ?)";

        Connection con = null;
        try {
            con = connectionProvider.connection();
            PreparedStatement stm = con.prepareStatement(command);
            stm.setString(1, sessionId);
            stm.setInt(2, uidForLogin(login));
            stm.setTimestamp(3, timestamp);

            stm.executeUpdate();
        }
        catch (SQLException e) {
            logger.error(e.getMessage(), e);
        }
        finally {
            ClosingUtils.close(con);
        }
    }


    private int uidForLogin(String login) throws SQLException {
        Connection conn = null;
        try {
            conn = connectionProvider.connection();
            if (conn != null) {
                PreparedStatement preparedStatement = conn.prepareStatement("SELECT " + USER_ID + " FROM "
                                + TABLE_NAME_USER + " WHERE " + USER_LOGIN + "=?");
                preparedStatement.setString(1, login);
                ResultSet rs = preparedStatement.executeQuery();
                if (rs.next()) {
                    return rs.getInt(USER_ID);
                }
                else {
                    throw new SQLException();
                }
            }
            else {
                logger.error("no db connection");
                throw new SQLException();
            }

        }
        finally {
            ClosingUtils.close(conn);
        }
    }


    private String loginForUid(int uid) throws SQLException {
        Connection conn = null;
        try {
            conn = connectionProvider.connection();
            if (conn != null) {
                PreparedStatement preparedStatement = conn.prepareStatement("SELECT " + USER_LOGIN + " FROM "
                                + TABLE_NAME_USER + " WHERE " + USER_ID + "=?");
                preparedStatement.setInt(1, uid);
                ResultSet rs = preparedStatement.executeQuery();
                if (rs.next()) {
                    return rs.getString(USER_LOGIN);
                }
                else {
                    throw new SQLException();
                }
            }
            else {
                logger.error("no db connection");
                throw new SQLException();
            }

        }
        finally {
            ClosingUtils.close(conn);
        }
    }


    @Override
    public int getUserIdForSessionId(String sessionId) {
        try {
            return uidForSessionId(sessionId);
        }
        catch (SQLException e) {
            logger.error(e.getMessage(), e);
            return -1;
        }
    }


    private int uidForSessionId(String sessionId) throws SQLException {
        Connection conn = null;
        try {
            conn = connectionProvider.connection();
            if (conn != null) {
                PreparedStatement preparedStatement = conn.prepareStatement("SELECT uid FROM "
                                + TABLE_NAME_SESSIONHISTORY + " WHERE sessionid = ?");
                preparedStatement.setString(1, sessionId);
                ResultSet rs = preparedStatement.executeQuery();
                if (rs.next()) {
                    return rs.getInt("uid");
                }
                else {
                    throw new SQLException();
                }
            }
            else {
                logger.error("no db connection");
                throw new SQLException();
            }

        }
        finally {
            ClosingUtils.close(conn);
        }
    }


    @Override
    public boolean checkPrivilege(Privilege privilege, String sessionId) {
        try {
            return getPrivilegesForUserLogin(loginForUid(uidForSessionId(sessionId))).contains(privilege);
        }
        catch (SQLException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }


    @Override
    public Set<Privilege> getPrivilegesForUserLogin(String login) {
        Set<Privilege> result = new HashSet<Privilege>();
        Connection conn = null;
        try {
            conn = connectionProvider.connection();
            if (conn != null) {
                PreparedStatement preparedStatement = conn.prepareStatement("SELECT p.Name FROM " + TABLE_NAME_USER
                                + " u JOIN " + TABLE_NAME_USER2ROLE + " ur ON u." + USER_ID + " = ur.uid JOIN "
                                + TABLE_NAME_ROLE + " r ON ur.rid = r.id JOIN " + TABLE_NAME_ROLE2PRIVILEGE
                                + " rp ON r.id = rp.rid JOIN " + TABLE_NAME_PRIVILEGE + " p ON rp.pid = p.id "
                                + "WHERE u." + USER_LOGIN + " = ?");
                preparedStatement.setString(1, login);
                ResultSet rs = preparedStatement.executeQuery();
                while (rs.next()) {
                    String privilegeString = rs.getString("Name");
                    Privilege p = Privilege.valueOf(privilegeString);
                    result.add(p);
                }
            }
            else {
                logger.error("no db connection");
            }
        }
        catch (SQLException e) {
            logger.error(e.getMessage(), e);
        }
        finally {
            ClosingUtils.close(conn);
        }
        return result;
    }
}
