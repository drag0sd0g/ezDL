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

package de.unidue.inf.is.ezdl.dlservices.repository.store.repositories;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import de.unidue.inf.is.ezdl.dlbackend.coding.BytesCoder;
import de.unidue.inf.is.ezdl.dlbackend.coding.JBossSerializationStrategy;
import de.unidue.inf.is.ezdl.dlbackend.data.StoredDocument;
import de.unidue.inf.is.ezdl.dlbackend.database.ConnectionProvider;
import de.unidue.inf.is.ezdl.dlcore.utils.ClosingUtils;



/**
 * Half-baked repository implementation until we have a better one. Stores
 * Document objects serialized as XML strings in an SQL database.
 * 
 * @author mj
 */
public class DBRepository extends AbstractRepository {

    /**
     * The table name we use.
     */
    private static final String DOCUMENT = "repo_document";
    /**
     * The SQL command to get a Document object.
     */
    private static final String GET = "SELECT data FROM " + DOCUMENT + " WHERE id=?";
    /**
     * The SQL command to check if a document is already in the database.
     */
    private static final String EXISTS = "SELECT 'true' FROM " + DOCUMENT + " WHERE id=?";
    /**
     * The SQL command to insert a new object.
     */
    private static final String PUT = "INSERT INTO " + DOCUMENT + " (id, data) VALUES (?, ?)";
    /**
     * The SQL command to update an existing object.
     */
    private static final String UPDATE = "UPDATE " + DOCUMENT + " SET data=? WHERE id=?";
    /**
     * The SQL command to retrieve the number of rows/objects in the DB.
     */
    private static final String COUNT = "SELECT COUNT(id) FROM " + DOCUMENT;
    /**
     * The SQL command to delete a document.
     */
    private static final String REMOVE = "DELETE FROM " + DOCUMENT + " WHERE id=?";
    /**
     * The minimum length for oids that must use an shorter hash code as
     * database id.
     */
    private static final int OID_LENGTH_FOR_HASHCODE = 255;

    private static Logger logger = Logger.getLogger(DBRepository.class);

    /**
     * The factory that produces connections.
     */
    private ConnectionProvider provider;
    /**
     * The bytes coder.
     */
    private BytesCoder bytesCoder;


    /**
     * Creates a new repository, using the given factory to connect to an SQL
     * database.
     * 
     * @param provider
     *            the connection provider
     */
    public DBRepository(ConnectionProvider provider) {
        this.provider = provider;
        bytesCoder = new BytesCoder(new JBossSerializationStrategy());
    }


    @Override
    public StoredDocument getDocument(String oid) {
        Connection con = null;
        PreparedStatement st = null;
        ResultSet res = null;
        StoredDocument out = null;
        final String databaseIdForOid = databaseIdForOid(oid);
        try {
            con = provider.connection();
            st = con.prepareStatement(GET);
            st.setString(1, databaseIdForOid);
            res = st.executeQuery();
            if (res.next()) {
                Blob clob = res.getBlob(1);
                out = decode(clob.getBinaryStream());
            }
            con.commit();
        }
        catch (SQLException e) {
            rollback(con);
            getLogger().error("Error selecting " + databaseIdForOid, e);
        }
        finally {
            ClosingUtils.close(res);
            ClosingUtils.close(st);
            ClosingUtils.close(con);
        }
        return out;
    }


    private static String databaseIdForOid(String oid) {
        if (oid.length() >= OID_LENGTH_FOR_HASHCODE) {
            return String.valueOf(oid.hashCode());
        }
        else {
            return oid;
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
    protected void putAsIs(String oid, StoredDocument document) {
        Connection con = null;
        PreparedStatement st = null;
        PreparedStatement st2 = null;

        byte[] encoded = encode(document);
        ByteArrayInputStream is = new ByteArrayInputStream(encoded);

        String databaseIdForOid = databaseIdForOid(oid);

        try {
            con = provider.connection();

            st = con.prepareStatement(EXISTS);
            st.setString(1, databaseIdForOid);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                String s = rs.getString(1);
                if (Boolean.valueOf(s)) {
                    st2 = con.prepareStatement(UPDATE);
                    st2.setBlob(1, is);
                    st2.setString(2, databaseIdForOid);
                    st2.execute();
                }
                else {
                    getLogger().error("Error while checking if row exists");
                }
            }
            else {
                st2 = con.prepareStatement(PUT);
                st2.setString(1, databaseIdForOid);
                st2.setBlob(2, is);
                st2.execute();
            }
            con.commit();
        }
        catch (SQLException e) {
            rollback(con);
            getLogger().error("Error putting " + databaseIdForOid, e);
        }
        finally {
            ClosingUtils.close(st, st2);
            ClosingUtils.close(con);
        }
    }


    private byte[] encode(StoredDocument d) {
        return bytesCoder.encode(d);
    }


    private StoredDocument decode(InputStream is) {
        try {
            return (StoredDocument) bytesCoder.decode(IOUtils.toByteArray(is));
        }
        catch (IOException e) {
            logger.error(e.getMessage(), e);
            return null;
        }

    }


    @Override
    public int getRepositorySize() {
        int count = 0;
        Connection con = null;
        PreparedStatement st = null;
        ResultSet res = null;
        try {
            con = provider.connection();
            st = con.prepareStatement(COUNT);
            res = st.executeQuery();
            if (res.next()) {
                count = res.getInt(1);
            }
            con.commit();
        }
        catch (SQLException e) {
            rollback(con);
            getLogger().error("Error getting count", e);
        }
        finally {
            ClosingUtils.close(res);
            ClosingUtils.close(st);
            ClosingUtils.close(con);
        }

        return count;
    }


    @Override
    public void removeDocument(String oid) {
        Connection con = null;
        PreparedStatement st = null;
        try {
            con = provider.connection();
            st = con.prepareStatement(REMOVE);
            st.setString(1, databaseIdForOid(oid));
            st.execute();
            con.commit();
        }
        catch (SQLException e) {
            rollback(con);
            getLogger().error("Error removing " + databaseIdForOid(oid), e);
        }
        finally {
            ClosingUtils.close(st);
            ClosingUtils.close(con);
        }
    }
}
