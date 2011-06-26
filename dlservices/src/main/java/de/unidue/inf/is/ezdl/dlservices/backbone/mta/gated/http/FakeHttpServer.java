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

package de.unidue.inf.is.ezdl.dlservices.backbone.mta.gated.http;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import de.unidue.inf.is.ezdl.dlcore.utils.ClosingUtils;
import de.unidue.inf.is.ezdl.dlservices.backbone.mta.gated.MessageHandler;
import de.unidue.inf.is.ezdl.dlservices.backbone.mta.gated.Server;



/**
 * The FakeHttpServer is a small and very, very dump HTTP server. It listens to
 * new connections on a given port and passes the connection to a new
 * {@link ConnectionThread} that does all the hard work.
 * <p>
 * The only thing but dealing with new connections that is done in this class is
 * forwarding chunks between MTA and ConnectionThread.
 * 
 * @author mjordan
 */
final class FakeHttpServer extends Thread implements Server {

    /**
     * The state of the server.
     */
    private enum RunningState {
        INIT, RUNNING, LEAVING, HALTED;
    }


    /**
     * The time in milliseconds to wait until {@link ServerSocket#accept()}
     * returns even if no new connection could be established. This should be
     * fairly small so ending the FakeHttpServer doesn't take ages.
     */
    public static final int LISTEN_TIMEOUT_MS = 500;

    /**
     * The server socket that the FakeHttpServer listens on.
     */
    private final ServerSocket serverSocket;
    /**
     * Used to delegate message handling.
     */
    private final MessageHandler messageHandler;
    /**
     * The serialized ping message used to keep the connection open.
     */
    private final String pingMessage;
    /**
     * The state of the server.
     */
    private volatile RunningState runningState = RunningState.INIT;
    /**
     * The map between connection ID's and connection threads.
     */
    private Map<String, ConnectionThread> connections;
    /**
     * The logger.
     */
    private final Logger logger = Logger.getLogger(getClass());


    /**
     * Creates a new FakeHttpServer.
     * 
     * @param messageHandler
     *            the handler of messages
     * @param hostName
     *            the host name the server binds to
     * @param port
     *            the port to listen on
     * @param pingMessage
     *            the serialized message to send in order to keep the connection
     *            open
     * @throws IOException
     */
    public FakeHttpServer(MessageHandler messageHandler, String hostName, int port, String pingMessage)
                    throws IOException {
        this.messageHandler = messageHandler;
        this.pingMessage = pingMessage;

        InetAddress hostAddress = InetAddress.getByName(hostName);
        this.serverSocket = new ServerSocket(port, 0, hostAddress);
        this.serverSocket.setSoTimeout(LISTEN_TIMEOUT_MS);
        this.serverSocket.setReuseAddress(true);

        this.connections = new ConcurrentHashMap<String, ConnectionThread>();

        setName("MTA.HttpServerThread");
        start();
    }


    /**
     * Listens on the given port and creates new ConnectionThreads for incomming
     * connections.
     */
    @Override
    public void run() {
        runningState = RunningState.RUNNING;
        Socket socket = null;

        while (runningState == RunningState.RUNNING) {
            try {
                socket = serverSocket.accept();
                messageHandler.log("connection opened", "Remote party " + socket.getRemoteSocketAddress());
                String connectionId = getNewConnectionId(socket);
                ConnectionThread connectionThread = new ConnectionThread(this, socket, connectionId, pingMessage);
                connections.put(connectionId, connectionThread);
                connectionThread.start();
            }
            catch (SocketTimeoutException e) {
                // we don't really care
            }
            catch (IOException e) {
                halt();
            }
        }
        closeResources(socket);
        runningState = RunningState.HALTED;
    }


    /**
     * Closes the resources of the server and the {@link ConnectionThread}
     * objects.
     * 
     * @param socket
     *            the socket to close
     */
    private void closeResources(Socket socket) {
        if (socket != null) {
            ClosingUtils.close(socket);
            for (ConnectionThread con : connections.values()) {
                con.halt();
            }
        }
    }


    /**
     * Calculates a new connection ID.
     * 
     * @param socket
     *            the socket to calculate an ID for
     * @return the ID
     */
    private String getNewConnectionId(Socket socket) {
        String port = Integer.toHexString(socket.getLocalPort());
        String time = Long.toHexString(System.currentTimeMillis());
        StringBuffer out = new StringBuffer();
        out.append(port).append(':').append(time);
        return out.toString();
    }


    @Override
    public void halt() {
        runningState = RunningState.LEAVING;
    }


    @Override
    public boolean isHalted() {
        return runningState == RunningState.HALTED;
    }


    @Override
    public void haltConnection(String connectionId) {
        ConnectionThread conn = connections.get(connectionId);
        if (conn != null) {
            conn.halt();
        }
    }


    @Override
    public void send(String connectionId, String chunk) {
        ConnectionThread connection = connections.get(connectionId);
        if (connection != null) {
            connection.send(chunk);
        }
    }


    @Override
    public boolean receive(String connectionId, String chunk) {
        return messageHandler.handleFromClient(connectionId, chunk);
    }


    @Override
    public void connectionLost(String connectionId) {
        logger.debug("connection lost " + connectionId);
        messageHandler.handleConnectionLost(connectionId);
        connections.remove(connectionId);
    }


    @Override
    public String getClientHost(String connectionId) {
        ConnectionThread ct = connections.get(connectionId);
        if (ct != null) {
            return ct.getClientHost();
        }
        else {
            return null;
        }
    }


    @Override
    public int connections() {
        return connections.size();
    }
}