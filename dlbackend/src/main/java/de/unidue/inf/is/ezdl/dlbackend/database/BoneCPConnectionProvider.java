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

package de.unidue.inf.is.ezdl.dlbackend.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import com.jolbox.bonecp.BoneCPDataSource;



/**
 * This implementation of {@link ConnectionProvider} is based on BoneCP.
 * 
 * @author tbeckers
 */
public final class BoneCPConnectionProvider implements ConnectionProvider {

    private BoneCPDataSource dataSource;
    private boolean autoCommit;


    /**
     * Creates a new instance.
     * 
     * @param url
     *            the db url
     * @param user
     *            the db user
     * @param password
     *            the db password
     * @param autoCommit
     *            if the provided connections should have set the
     *            <code>autoCommit</code> property to <code>true</code>
     */
    public BoneCPConnectionProvider(String url, String user, String password, boolean autoCommit) {
        dataSource = setupDataSource(url, user, password, autoCommit);
        this.autoCommit = autoCommit;
    }


    /**
     * Creates a new instance.
     * 
     * @param props
     *            db url, user and password as {@link Properties}
     * @param autoCommit
     *            if the provided connections should have set the
     *            <code>autoCommit</code> property to <code>true</code>
     */
    public BoneCPConnectionProvider(Properties props, boolean autoCommit) {
        String url = props.getProperty("db.url");
        String user = props.getProperty("db.user");
        String password = props.getProperty("db.password");
        this.autoCommit = autoCommit;
        dataSource = setupDataSource(url, user, password, autoCommit);
    }


    @Override
    public synchronized Connection connection() throws SQLException {
        Connection connection = dataSource.getConnection();
        connection.setAutoCommit(autoCommit);
        return connection;
    }


    private BoneCPDataSource setupDataSource(String connectURI, String user, String password, boolean autoCommit) {
        BoneCPDataSource ds = new BoneCPDataSource();
        ds.setJdbcUrl(connectURI);
        ds.setUsername(user);
        ds.setPassword(password);
        return ds;
    }

}
