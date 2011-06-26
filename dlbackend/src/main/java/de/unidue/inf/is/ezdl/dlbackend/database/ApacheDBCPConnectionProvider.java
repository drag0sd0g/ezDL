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

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;



/**
 * This implementation of {@link ConnectionProvider} is based on Apache DBCP.
 * 
 * @author tbeckers
 */
public final class ApacheDBCPConnectionProvider implements ConnectionProvider {

    private PoolingDataSource dataSource;
    private PoolableConnectionFactory poolableConnectionFactory;


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
    public ApacheDBCPConnectionProvider(String url, String user, String password, boolean autoCommit) {
        dataSource = setupDataSource(url, user, password, autoCommit);
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
    public ApacheDBCPConnectionProvider(Properties props, boolean autoCommit) {
        String url = props.getProperty("db.url");
        String user = props.getProperty("db.user");
        String password = props.getProperty("db.password");
        dataSource = setupDataSource(url, user, password, autoCommit);
    }


    @Override
    public synchronized Connection connection() throws SQLException {
        return dataSource.getConnection();
    }


    private PoolingDataSource setupDataSource(String connectURI, String user, String password, boolean autoCommit) {
        GenericObjectPool.Config config = new GenericObjectPool.Config();
        config.maxActive = 15;
        config.maxIdle = 10;
        config.minIdle = 3;
        config.maxWait = 1000;

        ObjectPool connectionPool = new GenericObjectPool(null, config);

        ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(connectURI, user, password);

        poolableConnectionFactory = new PoolableConnectionFactory(connectionFactory, connectionPool, null, null, false,
                        true);
        poolableConnectionFactory.setDefaultAutoCommit(autoCommit);
        PoolingDataSource poolingDataSource = new PoolingDataSource(connectionPool);

        return poolingDataSource;
    }

}
