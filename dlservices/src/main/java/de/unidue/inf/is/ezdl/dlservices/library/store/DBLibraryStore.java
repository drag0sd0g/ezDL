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

package de.unidue.inf.is.ezdl.dlservices.library.store;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import de.unidue.inf.is.ezdl.dlbackend.database.BoneCPConnectionProvider;
import de.unidue.inf.is.ezdl.dlbackend.database.ConnectionProvider;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.Document;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.library.Group;
import de.unidue.inf.is.ezdl.dlcore.utils.ClosingUtils;
import de.unidue.inf.is.ezdl.dlservices.queryhistory.store.DBQueryHistoryStore;



/** Implements a DB Library Store */
public class DBLibraryStore implements LibraryStore {

    private static final String TABLE_NAME_LIBRARY = "lib_library";
    private static final String TABLE_NAME_GROUPS = "lib_groups";

    private static Logger logger = Logger.getLogger(DBQueryHistoryStore.class);

    private ConnectionProvider connectionProvider;


    public DBLibraryStore(Properties dbProp) {
        this.connectionProvider = new BoneCPConnectionProvider(dbProp, false);
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
    public void addDocument(Document document, int userId) {
        Connection con = null;
        PreparedStatement st = null;
        try {

            con = connectionProvider.connection();
            st = con.prepareStatement("insert into " + TABLE_NAME_LIBRARY + " (ooid, data, userid) values (?,?,?)");
            st.setString(1, document.getOid());
            st.setObject(2, document);
            st.setInt(3, userId);
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


    /** Returns the library as a List of Document Objects */
    @Override
    public List<Document> getLibrary(int userId) {
        Connection con = null;
        PreparedStatement st = null;
        ResultSet rst = null;
        List<Document> resultList = new ArrayList<Document>();

        try {
            con = connectionProvider.connection();
            st = con.prepareStatement("select * from " + TABLE_NAME_LIBRARY + " where userId = ?");
            st.setInt(1, userId);
            rst = st.executeQuery();
            while (rst.next()) {
                resultList.add(decode(rst.getBlob("data").getBinaryStream()));
            }
            con.commit();

        }
        catch (SQLException e) {
            rollback(con);
            logger.error(e.getMessage(), e);
        }
        finally {
            ClosingUtils.close(rst);
            ClosingUtils.close(st);
            ClosingUtils.close(con);
        }

        return resultList;

    }


    /** Returns the Document with the given OID, if not found: return is null */
    @Override
    public Document getDocument(String oid, int userId) {
        Connection con = null;
        PreparedStatement st = null;
        ResultSet rst = null;
        Document result = null;

        try {
            con = connectionProvider.connection();
            st = con.prepareStatement("select data from " + TABLE_NAME_LIBRARY + " where ooid = ? and userId = ?");
            st.setString(1, oid);
            st.setInt(2, userId);

            rst = st.executeQuery();
            if (rst.next()) {
                result = decode(rst.getBlob("data").getBinaryStream());
            }
            con.commit();
        }
        catch (Exception e) {
            rollback(con);
            logger.error(e.getMessage(), e);
        }
        finally {
            ClosingUtils.close(rst);
            ClosingUtils.close(st);
            ClosingUtils.close(con);
        }
        return result;
    }


    /** Remove a Document from the Library */
    @Override
    public void removeDocument(Document d, int userId) {
        Connection con = null;
        PreparedStatement st = null;

        try {
            con = connectionProvider.connection();
            st = con.prepareStatement("delete from " + TABLE_NAME_LIBRARY + " where ooid = ? and userid = ?");
            st.setString(1, d.getOid());
            st.setInt(2, userId);
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


    /** Updates the given document in the store. Compares the two ooids */
    @Override
    public void updateDocument(Document d, int userId) {
        Connection con = null;
        PreparedStatement st = null;

        try {
            con = connectionProvider.connection();
            st = con.prepareStatement("update " + TABLE_NAME_LIBRARY + " set data = ? where ooid = ? and userid = ? ");

            st.setObject(1, d);
            st.setString(2, d.getOid());
            st.setInt(3, userId);
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


    /** Returns the groups of the user */
    @Override
    public List<Group> getGroups(int userId) {
        Connection con = null;
        PreparedStatement st = null;
        ResultSet rst = null;
        List<Group> groups = new ArrayList<Group>();

        try {
            con = connectionProvider.connection();
            st = con.prepareStatement("select * from " + TABLE_NAME_GROUPS + " where userId = ?");
            st.setInt(1, userId);

            rst = st.executeQuery();
            while (rst.next()) {
                String id = rst.getString("groupid");
                String name = rst.getString("name");
                String referenceSystemId = rst.getString("referencesystemid");
                String referenceSystem = rst.getString("referencesystem");
                String type = rst.getString("types");

                groups.add(new Group(id, name, referenceSystem, referenceSystemId, type));
            }
            con.commit();
        }
        catch (Exception e) {
            rollback(con);
            logger.error(e.getMessage(), e);
        }
        finally {
            ClosingUtils.close(rst);
            ClosingUtils.close(st);
            ClosingUtils.close(con);
        }
        return groups;
    }


    /** Get the Group with the given group id */
    @Override
    public Group getGroup(String groupId, int userId) {
        Connection con = null;
        PreparedStatement st = null;
        ResultSet rst = null;
        Group group = null;

        try {
            con = connectionProvider.connection();
            st = con.prepareStatement("select * from " + TABLE_NAME_GROUPS + " where userId = ? and groupid = ?");
            st.setInt(1, userId);
            st.setString(2, groupId);

            rst = st.executeQuery();
            if (rst.next()) {
                String id = rst.getString("groupid");
                String name = rst.getString("name");
                String referenceSystemId = rst.getString("referencesystemid");
                String referenceSystem = rst.getString("referencesystem");
                String type = rst.getString("types");

                group = new Group(id, name, referenceSystem, referenceSystemId, type);
            }
            con.commit();
        }
        catch (Exception e) {
            rollback(con);
            logger.error(e.getMessage(), e);
        }
        finally {
            ClosingUtils.close(rst);
            ClosingUtils.close(st);
            ClosingUtils.close(con);
        }
        return group;
    }


    /** Add a group */
    @Override
    public void addGroup(Group group, int userId) {
        Connection con = null;
        PreparedStatement st = null;
        try {

            con = connectionProvider.connection();
            st = con.prepareStatement("insert into "
                            + TABLE_NAME_GROUPS
                            + " (groupid, name, referencesystemid, referencesystem, userid, types) values (?,?,?,?,?,?)");
            st.setString(1, group.getId());
            st.setString(2, group.getName());
            st.setString(3, group.getReferenceSystemId());
            st.setString(4, group.getReferenceSystem());
            st.setInt(5, userId);
            st.setString(6, group.getType());
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


    /** Update group */
    @Override
    public void updateGroup(Group group, int userId) {
        Connection con = null;
        PreparedStatement st = null;

        try {

            con = connectionProvider.connection();
            st = con.prepareStatement("update "
                            + TABLE_NAME_GROUPS
                            + " set name = ?, referencesystemid = ?, referencesystem = ?, types = ? where groupid = ? and userid = ?");

            st.setString(1, group.getName());
            st.setString(2, group.getReferenceSystemId());
            st.setString(3, group.getReferenceSystem());
            st.setString(4, group.getType());
            st.setString(5, group.getId());
            st.setInt(6, userId);
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


    /** Deletes a group */
    @Override
    public void removeGroup(Group group, int userId) {
        Connection con = null;
        PreparedStatement st = null;

        try {
            con = connectionProvider.connection();
            st = con.prepareStatement("delete from " + TABLE_NAME_GROUPS + " where groupid = ? and userid = ?");
            st.setString(1, group.getId());
            st.setInt(2, userId);
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


    /** rollback the transaction */
    private static void rollback(Connection connection) {
        try {
            connection.rollback();
        }
        catch (SQLException e) {
            logger.error(e.getMessage(), e);
        }
    }


    private Document decode(InputStream is) {
        try {
            ObjectInputStream ois = new ObjectInputStream(is);
            Object x = ois.readObject();
            return (Document) x;

        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }
}
