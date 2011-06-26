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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Date;

import org.apache.log4j.Logger;

import de.unidue.inf.is.ezdl.dlcore.Haltable;
import de.unidue.inf.is.ezdl.dlcore.utils.ClosingUtils;
import de.unidue.inf.is.ezdl.dlservices.backbone.mta.gated.Server;



/**
 * ConnectionThread is the class that deals with data exchange over a single
 * specific open socket. It answers any HTTP request that comes in with a dummy
 * answer. After that it keeps the connection open and deals with incoming
 * chunks and messages.
 * <p>
 * It uses two additional threads to deal with receiving and sending data
 * chunks. A chunk here is a string of data that belongs to a single message and
 * is separated somehow.
 * <p>
 * The re-occurring ping messages are sent in the wait loop of the
 * ConnectionThread itself and used for keeping the connection open in case HTTP
 * proxies are involved. Beware that keeping the connection over a proxy has not
 * been tested as of the time of writing of these lines.
 * <p>
 * 
 * @author mjordan
 */
class ConnectionThread extends Thread implements Haltable {

    /**
     * The wait time in milliseconds between "ping" messages (e.g. AskAlive).
     */
    private static final int PING_SLEEP_MS = 10000;
    /**
     * The time in milliseconds that the sender thread sleeps if no data is
     * currently in the send queue.
     */
    static final int SEND_SLEEP_MS = 50;
    /**
     * The time in milliseconds that the receiver thread sleeps if the input
     * queue is empty.
     */
    static final int RECEIVE_SLEEP_MS = 50;
    /**
     * The timeout in milliseconds after which the input stream returns even if
     * no data could be read.
     */
    public static final int SOCKET_TIMEOUT_MS = 500;
    /**
     * The grace period for letting the send queue run empty.
     */
    static final int SEND_QUEUE_TIMEOUT_MS = 1000;
    /**
     * The line end in HTTP.
     */
    private static final String CRLF = "\r\n";
    /**
     * The encoding for the input/output stream readers/writer.
     */
    private static final String ENCODING = "UTF-8";
    /**
     * The server version to send. This is just a gag because nobody really
     * cares.
     */
    private static final String SERVER_VERSION = "EzDLServer/1.0";

    /**
     * The logger.
     */
    private final Logger logger = Logger.getLogger(getClass());

    /**
     * The sender thread.
     */
    private SendThread sender;
    /**
     * The receiver thread.
     */
    private ReceiveThread receiver;
    /**
     * The socket used to exchange data.
     */
    private final Socket socket;
    /**
     * Reference to the HTTP server to deal with incoming data.
     */
    private final Server server;
    /**
     * The ID of this connection so the FakeHttpServer knows where the data
     * comes from.
     */
    private final String connectionId;
    /**
     * The serialized version of the ping message.
     */
    private final String pingMessage;
    /**
     * The current state of the connection thread.
     */
    private volatile RunningState connectionThreadRunning = RunningState.INIT;


    /**
     * The possible states of the connection.
     */
    private enum RunningState {
        INIT, RUNNING, LEAVING, HALTED;
    }


    /**
     * Creates a new ConnectionThread.
     * 
     * @param server
     *            the server that owns the thread and the socket
     * @param socket
     *            the socket to use for data exchange
     * @param connectionId
     *            the ID of this connection (used to identify the sender thread
     *            on the FakeHttpServer's side)
     * @param pingMessage
     *            the serialized form of the ping message to send without any
     *            chunk separator
     */
    public ConnectionThread(Server server, Socket socket, String connectionId, String pingMessage) {
        this.socket = socket;
        this.server = server;
        this.connectionId = connectionId;
        this.pingMessage = pingMessage;
        setName("MTA.ConnectionThread " + connectionId);
    }


    /**
     * Skips the data sent from the client (a fake request), answers with a fake
     * answer and then starts sender and receiver threads.
     */
    @Override
    public void run() {
        connectionThreadRunning = RunningState.RUNNING;
        BufferedReader in = null;
        BufferedWriter out = null;
        try {
            socket.setSoTimeout(SOCKET_TIMEOUT_MS);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream(), ENCODING));
            socket.getInputStream();
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), ENCODING));

            skipHeader(in);

            sendHeader(out, 200, "", 0, new Date());

            handleInputOutput(in, out);

            pingUntilConnectionClosed();

            sender.halt();
            receiver.halt();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            ClosingUtils.close(in, out);
            ClosingUtils.close(socket);

            server.connectionLost(getConnectionId());
            connectionThreadRunning = RunningState.HALTED;
            logger.debug("+++++ ConnectionThread closed " + getConnectionId());
        }
    }


    /**
     * Sends the header along with the given data.
     * 
     * @param out
     * @param code
     * @param contentType
     * @param contentLength
     * @param lastModified
     * @throws IOException
     */
    private static void sendHeader(Writer out, int code, String contentType, long contentLength, Date lastModified)
                    throws IOException {
        out.write(("HTTP/1.1 " + code + " OK\r\n" //
                        + "Date: " + new Date().toString() + CRLF //
                        + "Server: " + SERVER_VERSION + CRLF //
                        + "Content-Type: " + contentType + CRLF //
                        + "Expires: Thu, 01 Dec 2001 16:00:00 GMT\r\n" //
                        + ((contentLength != -1) ? "Content-Length: " + contentLength + CRLF : "") //
                        + "Last-modified: " + lastModified.toString() + CRLF + CRLF));
        out.flush();
    }


    /**
     * Sends static ping messages ({@link #pingMessage}) as long as the
     * connection thread has the state RUNNING.
     */
    private void pingUntilConnectionClosed() {
        while (connectionThreadRunning == RunningState.RUNNING) {
            sender.send(pingMessage);
            try {
                Thread.sleep(PING_SLEEP_MS);
            }
            catch (InterruptedException e) {
                logger.debug("Got interrupted during ping sleep. Running state is " + connectionThreadRunning);
            }
        }
    }


    /**
     * Reads incoming data from the stream until an empty line is found.
     * 
     * @param in
     *            the stream to skip the header in
     * @throws IOException
     */
    private static void skipHeader(BufferedReader in) throws IOException {
        if (in != null) {
            String line = null;
            do {
                line = in.readLine();
                // We just skip the data
            }
            while ((line != null) && !line.isEmpty());
        }
    }


    /**
     * Starts the sender and receiver threads.
     * 
     * @param in
     *            the input reader to read from
     * @param out
     *            the output writer to write to
     * @throws IOException
     */
    private void handleInputOutput(BufferedReader in, BufferedWriter out) throws IOException {
        sender = new SendThread(this, out);
        sender.start();
        receiver = new ReceiveThread(this, in);
        receiver.start();
    }


    @Override
    public synchronized void halt() {
        connectionThreadRunning = RunningState.LEAVING;
        interrupt();
    }


    @Override
    public boolean isHalted() {
        return connectionThreadRunning == RunningState.HALTED;
    }


    /**
     * Queues a chunk to be sent.
     * 
     * @param chunk
     *            the chunk to send
     */
    public void send(String chunk) {
        sender.send(chunk);
    }


    /**
     * Deals with a received chunk.
     * 
     * @param chunk
     *            the incoming chunk
     * @return true if the chunk was handled. Else false.
     */
    public boolean receive(String chunk) {
        return server.receive(getConnectionId(), chunk);
    }


    public String getClientHost() {
        SocketAddress sa = socket.getRemoteSocketAddress();
        if (sa instanceof InetSocketAddress) {
            return ((InetSocketAddress) sa).getAddress().getCanonicalHostName();
        }
        else {
            return null;
        }
    }


    String getConnectionId() {
        return connectionId;
    }
}
