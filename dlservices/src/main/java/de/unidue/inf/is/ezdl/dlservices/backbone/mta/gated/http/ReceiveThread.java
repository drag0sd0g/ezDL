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
import java.io.IOException;
import java.net.SocketTimeoutException;

import org.apache.log4j.Logger;

import de.unidue.inf.is.ezdl.dlcore.Haltable;
import de.unidue.inf.is.ezdl.dlcore.utils.HttpMessagingUtils;



/**
 * The receiver thread. Forwards received chunks to the FakeHttpServer.
 * 
 * @author mjordan
 */
class ReceiveThread extends Thread implements Haltable {

    /**
     * The maximum numbers of errors per connection tolerated before
     * {@link #terminateConnection(String)} is called on that connection.
     */
    public static final int ERRORS_BEFORE_CONNECTION_KILLED = 5;

    private Logger logger = Logger.getLogger(ReceiveThread.class);

    private ConnectionThread connection;
    /**
     * The reader to read data from.
     */
    private BufferedReader reader;
    /**
     * True, as long as the reader is supposed to keep reading.
     */
    private volatile boolean running = true;
    private int errorCount = 0;


    /**
     * Creates a new reader thread.
     * 
     * @param reader
     *            the reader to read from
     */
    public ReceiveThread(ConnectionThread connection, BufferedReader reader) {
        this.connection = connection;
        this.reader = reader;
        setName("ReceiveThread " + this.connection.getConnectionId());
    }


    @Override
    public void run() {
        while (!isHalted()) {
            try {
                String chunk = HttpMessagingUtils.readChunk(reader);
                if (chunk != null) {
                    boolean ok = connection.receive(chunk);
                    if (!ok) {
                        errorCount++;
                        if (errorCount > ERRORS_BEFORE_CONNECTION_KILLED) {
                            halt();
                            connection.halt();
                        }
                    }
                }
                else {
                    try {
                        Thread.sleep(ConnectionThread.RECEIVE_SLEEP_MS);
                    }
                    catch (InterruptedException e) {
                        //
                    }
                }
            }
            catch (SocketTimeoutException e) {
                // Couldn't care any less
            }
            catch (IOException e) {
                logger.debug("Receiver thread: Connection lost. Halting connection " + connection.getConnectionId());
                halt();
                connection.halt();
            }
        }
        logger.debug("receiver loop left");
    }


    @Override
    public synchronized void halt() {
        running = false;
        interrupt();
    }


    @Override
    public boolean isHalted() {
        return !running;
    }

}