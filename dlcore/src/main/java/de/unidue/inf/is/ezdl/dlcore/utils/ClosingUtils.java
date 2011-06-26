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

package de.unidue.inf.is.ezdl.dlcore.utils;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;



/**
 * Utility methods for closing streams, sockets, ...
 */
public final class ClosingUtils {

    private ClosingUtils() {
    }


    /**
     * Closes a closable object (e.g. InputStreams and OutputStreams)
     * <p>
     * Note: This method does not throw an IOException
     * </p>
     * 
     * @param closables
     *            The closable objects that should be closed (<code>null</code>
     *            is permitted)
     */
    public static void close(Closeable... closables) {
        if (closables != null) {
            for (Closeable closable : closables) {
                if (closable != null) {
                    try {
                        closable.close();
                    }
                    catch (IOException e) {
                    }
                }
            }
        }
    }


    /**
     * Closes a xml encoder
     * <p>
     * Note: This method does not throw an IOException
     * </p>
     * 
     * @param encoders
     *            The xml encoders that should be closed (<code>null</code> is
     *            permitted)
     */
    public static void close(XMLEncoder... encoders) {
        if (encoders != null) {
            for (XMLEncoder encoder : encoders) {
                if (encoder != null) {
                    encoder.close();
                }
            }
        }
    }


    /**
     * Closes a xml decoder
     * <p>
     * Note: This method does not throw an IOException
     * </p>
     * 
     * @param decoders
     *            The xml decoders that should be closed (<code>null</code> is
     *            permitted)
     */
    public static void close(XMLDecoder... decoders) {
        if (decoders != null) {
            for (XMLDecoder decoder : decoders) {
                if (decoder != null) {
                    decoder.close();
                }
            }
        }
    }


    /**
     * Closes a server socket
     * <p>
     * Note: This method does not throw an IOException
     * </p>
     * 
     * @param sockets
     *            The server sockets that should be closed (<code>null</code> is
     *            permitted)
     */
    public static void close(ServerSocket... sockets) {
        if (sockets != null) {
            for (ServerSocket socket : sockets) {
                if (socket != null) {
                    try {
                        socket.close();
                    }
                    catch (IOException e) {
                    }
                }
            }
        }
    }


    /**
     * Closes a socket
     * <p>
     * Note: This method does not throw an IOException
     * </p>
     * 
     * @param sockets
     *            The sockets that should be closed (<code>null</code> is
     *            permitted)
     */
    public static void close(Socket... sockets) {
        if (sockets != null) {
            for (Socket socket : sockets) {
                if (socket != null) {
                    try {
                        socket.close();
                    }
                    catch (IOException e) {
                    }
                }
            }
        }
    }


    /**
     * Closes a result set
     * <p>
     * Note: This method does not throw an SQLException
     * </p>
     * 
     * @param resultSets
     *            The result sets that should be closed (<code>null</code> is
     *            permitted)
     */
    public static void close(ResultSet... resultSets) {
        if (resultSets != null) {
            for (ResultSet resultSet : resultSets) {
                if (resultSet != null) {
                    try {
                        resultSet.close();
                    }
                    catch (SQLException e) {
                    }
                }
            }
        }
    }


    /**
     * Closes a connection
     * <p>
     * Note: This method does not throw an SQLException
     * </p>
     * 
     * @param connections
     *            The connections that should be closed (<code>null</code> is
     *            permitted)
     */
    public static void close(Connection... connections) {
        if (connections != null) {
            for (Connection connection : connections) {
                if (connection != null) {
                    try {
                        connection.close();
                    }
                    catch (SQLException e) {
                    }
                }
            }
        }
    }


    /**
     * Closes a statement
     * <p>
     * Note: This method does not throw an SQLException
     * </p>
     * 
     * @param statements
     *            The statements that should be closed (<code>null</code> is
     *            permitted)
     */
    public static void close(Statement... statements) {
        if (statements != null) {
            for (Statement statement : statements) {
                if (statement != null) {
                    try {
                        statement.close();
                    }
                    catch (SQLException e) {
                    }
                }
            }
        }
    }

}
