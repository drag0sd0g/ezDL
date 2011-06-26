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

package de.unidue.inf.is.ezdl.dlbackend.agent.connectors;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

import org.apache.log4j.Logger;

import de.unidue.inf.is.ezdl.dlbackend.agent.Agent;
import de.unidue.inf.is.ezdl.dlbackend.message.Message;
import de.unidue.inf.is.ezdl.dlbackend.message.MessageStringCoder;
import de.unidue.inf.is.ezdl.dlcore.misc.ConnectionFailedException;
import de.unidue.inf.is.ezdl.dlcore.misc.ConnectionState;
import de.unidue.inf.is.ezdl.dlcore.utils.ClosingUtils;



/**
 * Can be used to connect agents to the backend using an HTTP connection to the
 * MTA.
 * <p>
 * Since HTTP connections run over the MTA and the MTA assigns the client name,
 * the agent name is only used to construct message IDs and can be set randomly.
 * 
 * @author mjordan
 */
public class HttpAgentConnector extends Thread implements AgentConnector {

    /**
     * What we send as the client version.
     */
    private static final String CLIENT_VERSION_LINE = "User-Agent: EzDLExternalAgent/1.0";
    /**
     * The encoding used.
     */
    private static final String ENCODING = "UTF-8";
    /**
     * The HTTP version string. We send 1.1 because that is where connections
     * don't have to be torn down after one request.
     */
    private static final String HTTP_VERSION_STRING = "HTTP/1.1";
    /**
     * The HTTP way to terminate a line.
     */
    private static final String CRLF = "\r\n";
    /**
     * The time to wait in milliseconds between checks if the connection is
     * already established. The total maximum waiting time is CONNECTION_TRIES *
     * CONNECTION_TIMEOUT_MS milliseconds.
     */
    private static final int CONNECTION_TIMEOUT_PER_TRY_MS = 100;
    /**
     * The logger.
     */
    private final Logger logger = Logger.getLogger(HttpAgentConnector.class);
    /**
     * The state of the connection state machine.
     */
    private volatile ConnectionState state = ConnectionState.INIT;
    /**
     * The host name of the other side of the connection.
     */
    private String mtaHost;
    /**
     * The port number of the other side of the connection.
     */
    private int mtaPort;
    /**
     * The globally used connection socket.
     */
    private Socket mtaConnection;
    /**
     * The input stream that runs over mtaConnection.
     */
    private BufferedReader in;
    /**
     * The output stream that runs over mtaConnection.
     */
    private OutputStream out;
    /**
     * The reference to the agent for whom this connector works. Used for call
     * backs when receiving messages.
     */
    private Agent agent;
    /**
     * The connection ID. Whatever that means.
     */
    private String connection = "";
    /**
     * The number of tries between CONNECTION_TIMEOUT wait cycles.
     */
    private int connectionTries = 50;


    /**
     * The constructor.
     * 
     * @param agent
     *            the agent for whom this connector works.
     * @param mHost
     *            the host name of the other side of the connection
     * @param mPort
     *            the port number of the other side of the connection
     * @param timeOutsecs
     *            the connection timeout in seconds
     */
    public HttpAgentConnector(Agent agent, String mHost, int mPort, int timeOutSecs) {
        this.agent = agent;
        mtaHost = mHost;
        mtaPort = mPort;
        state = ConnectionState.INIT;
        connectionTries = (1000 * timeOutSecs) / CONNECTION_TIMEOUT_PER_TRY_MS;
    }


    /**
     * Connects to the MTA.
     */
    private ConnectionState connect() {
        logger.debug("connect() connecting");
        state = ConnectionState.OPENING;
        StringBuffer request = new StringBuffer();
        logger.info("** Trying host: " + mtaHost);
        logger.info("** Trying port: " + mtaPort);

        try {
            mtaConnection = new Socket(mtaHost, mtaPort);
            request.append("GET /connect ").append(HTTP_VERSION_STRING).append(CRLF);
            request.append(CLIENT_VERSION_LINE).append(CRLF);
            request.append(CRLF);
            String requestStr = request.toString();

            out = mtaConnection.getOutputStream();
            out.write(requestStr.getBytes(ENCODING));
            out.flush();

            in = new BufferedReader(new InputStreamReader(mtaConnection.getInputStream(), ENCODING));
            String s;
            while ((s = in.readLine()) != null) {
                if (s.equals("")) {
                    break;
                }
            }
            state = ConnectionState.OPEN;
            logger.debug("connect() connected");
        }
        catch (Exception e) {
            logger.error("Unable to connect: ", e);
            ClosingUtils.close(in);
            ClosingUtils.close(out);
            ClosingUtils.close(mtaConnection);
            state = ConnectionState.FAILED;
        }

        return state;
    }


    /**
     * Returns if the connection is ready.
     * 
     * @return true, if the connection is open and a connection ID has been
     *         received.
     */
    @Override
    public boolean isOnline() {
        return ((state == ConnectionState.OPEN) && (connection.length() != 0));
    }


    /**
     * Returns if the last connection attempt has failed.
     * 
     * @return true, if the connection attempts has just failed or the
     *         connection is closed
     */
    private boolean failed() {
        return ((state == ConnectionState.FAILED) || (state == ConnectionState.CLOSED));
    }


    /**
     * Opens a connection and polls messages that get dispatched to the agent.
     */
    @Override
    public void run() {
        logger.debug("run() started");
        state = connect();
        logger.debug("connect() left with state " + state);
        try {
            while (state == ConnectionState.OPEN) {
                if (in.ready()) {
                    String s = in.readLine();
                    logger.debug("IN:" + s);
                    while (s != null) {
                        if (s.startsWith("connection: ")) {
                            connection = s.substring(12);
                        }
                        else if (s.startsWith("message: ")) {
                            StringBuilder buffer = new StringBuilder(s.substring(9));
                            // msg lesen
                            while (s.trim().length() > 0) {
                                s = in.readLine();
                                buffer.append(s);
                                buffer.append(CRLF);
                            }
                            Message s1 = MessageStringCoder.getInstance().decode(buffer.toString());
                            agent.receive(s1);

                        }
                        else if (s.startsWith("ping: ")) {
                            logger.debug("Ping received");
                            send("pong");
                        }
                        yield();

                        s = in.readLine();
                    }
                }
            }
        }
        catch (IOException e) {
            logger.error("Faild to read from client.", e);
        }

        goOffline();
        logger.debug("run() left");
    }


    @Override
    public void send(Message message) throws IOException {

        if (!isOnline()) {
            return;
        }

        message.setFrom(connection);
        String outStr = MessageStringCoder.getInstance().encode(message);
        send(outStr);
    }


    /**
     * Sends a message over a socket.
     * 
     * @param sendMTA
     *            the socket to use
     * @param outStr
     *            the message string to send
     */
    private void send(String outStr) throws IOException {
        logger.debug(connection + "-> sending:\r\n " + outStr);
        StringBuffer request = new StringBuffer(16 + outStr.length());
        Socket sendMTA = null;
        OutputStream out = null;

        try {
            sendMTA = new Socket(mtaHost, mtaPort);
            out = sendMTA.getOutputStream();
            request.append("POST /message ").append(HTTP_VERSION_STRING).append(CRLF);
            request.append(CLIENT_VERSION_LINE).append(CRLF);
            request.append("Content-Length: ").append(outStr.length()).append(CRLF);
            request.append(CRLF);
            request.append(outStr);
            request.append(CRLF);
            // detect end of message.
            // evtl. rewrite to use channels
            // jdk 1.7
            request.append(CRLF);
            // never remove the line above
            out.write(request.toString().getBytes(ENCODING));
            out.flush();
            logger.debug("send() sent " + outStr);
        }
        catch (Exception e) {
            logger.error("sending failed for string " + outStr + ": ", e);
            throw new IOException(e);
        }
        finally {
            ClosingUtils.close(out);
            ClosingUtils.close(sendMTA);
        }
    }


    @Override
    public void goOffline() {
        logger.debug("shutdown() closing connection");
        state = ConnectionState.CLOSING;
        try {
            ClosingUtils.close(in);
            ClosingUtils.close(out);
            ClosingUtils.close(mtaConnection);
        }
        catch (Exception e) {
            logger.error("shutdown failed: ", e);
        }
        finally {
            state = ConnectionState.CLOSED;
            logger.debug("shutdown() finished");
        }
    }


    @Override
    public void goOnline() throws ConnectionFailedException {
        if (state.isOpenable()) {
            start();
            waitForConnection();
        }
    }


    /**
     * Waits for a connection and throws some exception in case of a problem.
     * 
     * @throws ConnectionFailedException
     */
    private void waitForConnection() throws ConnectionFailedException {
        int j = 0;
        while (!failed() && !isOnline() && (j < connectionTries)) {
            try {
                Thread.sleep(CONNECTION_TIMEOUT_PER_TRY_MS);
            }
            catch (InterruptedException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
            j++;
        }
        if (!isOnline()) {
            throw new ConnectionFailedException("Starting External Agent Client failed. Server not ready?");
        }
    }

}
