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

package de.unidue.inf.is.ezdl.dlbackend;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.io.IOUtils;

import de.unidue.inf.is.ezdl.dlbackend.database.ApacheDBCPConnectionProvider;
import de.unidue.inf.is.ezdl.dlbackend.database.ConnectionProvider;
import de.unidue.inf.is.ezdl.dlcore.AbstractTestBase;
import de.unidue.inf.is.ezdl.dlcore.utils.ClosingUtils;



/**
 * Common super class for backend tests.
 */
public abstract class AbstractBackendTestBase extends AbstractTestBase {

    private ConnectionProvider provider;


    public AbstractBackendTestBase() {
        super();
    }


    /**
     * Initializes the test database connection.
     * 
     * @param autoCommit
     *            if the connection should be commit automatically
     */
    protected final void initDatabaseConnection(boolean autoCommit) {
        provider = new ApacheDBCPConnectionProvider("jdbc:h2:mem:test", "sa", "sa", autoCommit);
    }


    /**
     * Creates tables in the test database.
     * 
     * @param createTableStatementsResourceName
     */
    protected final void createTables(String createTableStatementsResourceName) {
        Connection connection = null;
        try {
            InputStream is = getClass().getResourceAsStream(createTableStatementsResourceName);
            String createTableStatements = IOUtils.toString(is, "UTF-8");
            String[] createTableStatementsArray = createTableStatements.split("\\n\\s*\\n");
            connection = provider.connection();
            Statement st = connection.createStatement();
            for (String createTableStatement : createTableStatementsArray) {
                st.addBatch(createTableStatement);
            }
            st.executeBatch();
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        finally {
            ClosingUtils.close(connection);
        }
    }


    /**
     * Closes and clears the test database.
     */
    protected final void closeDatabaseConnection() {
        Connection connection = null;
        try {
            connection = provider.connection();
            connection.createStatement().execute("drop all objects");
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
        finally {
            ClosingUtils.close(connection);
        }
    }


    /**
     * Returns the {@link ConnectionProvider} of the test database.
     * 
     * @return {@link ConnectionProvider} of the test database
     */
    protected final ConnectionProvider connectionProvider() {
        if (provider == null) {
            throw new IllegalStateException("db is not initialized");
        }
        return provider;
    }
}
