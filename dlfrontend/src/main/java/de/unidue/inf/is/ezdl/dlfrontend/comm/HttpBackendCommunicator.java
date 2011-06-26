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

package de.unidue.inf.is.ezdl.dlfrontend.comm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import de.unidue.inf.is.ezdl.dlcore.message.MTAMessage;
import de.unidue.inf.is.ezdl.dlcore.message.MTAMessageCoder;
import de.unidue.inf.is.ezdl.dlcore.message.content.AliveAsk;
import de.unidue.inf.is.ezdl.dlcore.message.content.AliveTell;
import de.unidue.inf.is.ezdl.dlcore.misc.ConnectionFailedException;
import de.unidue.inf.is.ezdl.dlcore.misc.ConnectionState;
import de.unidue.inf.is.ezdl.dlcore.utils.ClosingUtils;
import de.unidue.inf.is.ezdl.dlcore.utils.HttpMessagingUtils;



public final class HttpBackendCommunicator implements BackendCommunicator {

    /**
     * What we send as the client version.
     */
    private static final String CLIENT_VERSION_LINE = "User-Agent: EzDLFrontend/1.0";
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
     * The encoding for the input/output stream readers/writer.
     */
    private static final String ENCODING = "UTF-8";

    private final Logger logger = Logger.getLogger(HttpBackendCommunicator.class);

    /**
     * The state of the connection to the backend.
     */
    private volatile ConnectionState state;

    private ScheduledExecutorService receiverExecutorService;
    private ScheduledExecutorService delivererExecutorService;
    private ScheduledExecutorService senderExecutorService;
    /**
     * The list of listeners for received messages.
     */
    private List<MessageReceivedListener> listeners;

    /**
     * The socket of the connection to the backend.
     */
    private Socket mtaSocket;
    /**
     * The output writer of the connection to the backend.
     */
    private BufferedWriter out;
    /**
     * The input reader of the connection to the backend.
     */
    private BufferedReader in;

    private Queue<String> sendQueue;
    private Queue<String> receiveQueue;


    public HttpBackendCommunicator() {
        this.state = ConnectionState.INIT;

        this.sendQueue = new ConcurrentLinkedQueue<String>();
        this.receiveQueue = new ConcurrentLinkedQueue<String>();

        this.listeners = new CopyOnWriteArrayList<MessageReceivedListener>();
        this.receiverExecutorService = Executors.newSingleThreadScheduledExecutor();
        this.delivererExecutorService = Executors.newSingleThreadScheduledExecutor();
        this.senderExecutorService = Executors.newSingleThreadScheduledExecutor();
    }


    @Override
    public void init(String mtaHost, int mtaPort, int timeOutSecs) throws ConnectionFailedException {
        if (state != ConnectionState.INIT) {
            throw new IllegalStateException();
        }

        try {
            state = ConnectionState.OPENING;
            connect(mtaHost, mtaPort);
        }
        catch (IOException e) {
            logger.error("Unable to connect: ", e);
            state = ConnectionState.FAILED;
            throw new ConnectionFailedException(e);
        }
        state = ConnectionState.OPEN;
        startReceiver();
        startDeliverer();
        startSender();
    }


    private void checkIfInitialized() {
        if (state.compareTo(ConnectionState.INIT) < 0) {
            throw new IllegalStateException();
        }
    }


    private void connect(String mtaHost, int mtaPort) throws IOException, UnsupportedEncodingException {
        this.mtaSocket = new Socket(mtaHost, mtaPort);
        this.out = new BufferedWriter(new OutputStreamWriter(mtaSocket.getOutputStream(), ENCODING));
        this.in = new BufferedReader(new InputStreamReader(mtaSocket.getInputStream(), ENCODING));

        StringBuffer request = new StringBuffer();
        request.append("GET /connect ").append(HTTP_VERSION_STRING).append(CRLF);
        request.append(CLIENT_VERSION_LINE).append(CRLF);
        request.append(CRLF);
        String requestStr = request.toString();

        out.append(requestStr);
        out.flush();

        String s;
        while ((s = in.readLine()) != null) {
            if (s.isEmpty()) {
                break;
            }
        }
    }


    @Override
    public void addMessageReceivedListener(MessageReceivedListener messageReceivedListener) {
        listeners.add(messageReceivedListener);
    }


    @Override
    public void send(MTAMessage msg) {
        checkIfInitialized();
        String outStr;
        try {
            outStr = MTAMessageCoder.getInstance().encode(msg);
            sendQueue.offer(outStr);
        }
        catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }


    private void startSender() {
        Runnable sender = new Runnable() {

            @Override
            public void run() {
                if (state == ConnectionState.OPEN) {
                    String data = null;
                    data = sendQueue.poll();
                    if (data != null) {
                        try {
                            HttpMessagingUtils.writeChunk(out, data);
                        }
                        catch (IOException e) {
                            logger.error(e.getMessage(), e);
                        }
                    }
                }
            }
        };
        senderExecutorService.scheduleWithFixedDelay(sender, 20, 20, TimeUnit.MILLISECONDS);
    }


    private void startReceiver() {
        Runnable receiver = new Runnable() {

            @Override
            public void run() {
                try {
                    if (state == ConnectionState.OPEN && in.ready()) {
                        String data = HttpMessagingUtils.readChunk(in);
                        if (data != null) {
                            receiveQueue.offer(data);
                        }
                    }
                }
                catch (IOException e) {
                    logger.error("Failed to read from client.", e);
                }
            }

        };
        receiverExecutorService.scheduleWithFixedDelay(receiver, 20, 20, TimeUnit.MILLISECONDS);
    }


    private void startDeliverer() {
        Runnable deliverer = new Runnable() {

            @Override
            public void run() {
                if (state == ConnectionState.OPEN) {
                    String data = null;
                    data = receiveQueue.poll();
                    if (data != null) {
                        MTAMessage message;
                        try {
                            message = MTAMessageCoder.getInstance().decode(data);
                            if (message.getContent() instanceof AliveAsk) {
                                // logger.debug("Ping received");
                                send(message.tell(new AliveTell()));
                                // logger.debug("Pong sent");
                            }
                            else {
                                fireMessageReceived(message);
                            }
                        }
                        catch (IOException e) {
                            logger.error(e.getMessage(), e);
                        }
                    }
                }
            }
        };
        delivererExecutorService.scheduleWithFixedDelay(deliverer, 20, 20, TimeUnit.MILLISECONDS);
    }


    private void fireMessageReceived(MTAMessage message) {
        for (MessageReceivedListener listener : listeners) {
            listener.receive(message);
        }
    }


    @Override
    public void halt() {
        state = ConnectionState.CLOSING;
        senderExecutorService.shutdown();
        receiverExecutorService.shutdown();
        delivererExecutorService.shutdown();

        awaitTermination(senderExecutorService);
        awaitTermination(receiverExecutorService);
        awaitTermination(delivererExecutorService);

        ClosingUtils.close(mtaSocket);
        state = ConnectionState.CLOSED;
    }


    private void awaitTermination(ExecutorService executorService) {
        try {
            executorService.awaitTermination(500, TimeUnit.MILLISECONDS);
        }
        catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
    }


    @Override
    public boolean isHalted() {
        return state == ConnectionState.CLOSED;
    }

}
