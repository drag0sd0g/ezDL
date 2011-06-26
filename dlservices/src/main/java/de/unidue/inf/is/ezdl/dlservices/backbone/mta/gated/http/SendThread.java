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

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.log4j.Logger;

import de.unidue.inf.is.ezdl.dlcore.Haltable;
import de.unidue.inf.is.ezdl.dlcore.utils.HttpMessagingUtils;
import de.unidue.inf.is.ezdl.dlcore.utils.QueueUtils;



/**
 * The sending thread. Maintains a queue of chunks to send and sends them one
 * after another.
 * 
 * @author mjordan
 */
class SendThread extends Thread implements Haltable {

    private Logger logger = Logger.getLogger(SendThread.class);

    /**
     * The connection thread that owns this thread.
     */
    private ConnectionThread connection;
    /**
     * The writer to write to.
     */
    private BufferedWriter writer;
    /**
     * The queue of chunks to send.
     */
    private Queue<String> queue = new ConcurrentLinkedQueue<String>();
    /**
     * True, as long as the thread is supposed to keep running and sending.
     */
    private volatile boolean running = true;


    /**
     * Creates a new sender thread.
     * 
     * @param connection
     *            the connection thread that owns the sender thread
     * @param writer
     *            the writer to write data to
     */
    public SendThread(ConnectionThread connection, BufferedWriter writer) {
        this.connection = connection;
        this.writer = writer;
        setName("SendThread " + connection.getConnectionId());
    }


    /**
     * Queue a chunk for writing.
     * 
     * @param chunk
     *            the chunk to queue
     */
    public void send(String chunk) {
        if (running) {
            queue.add(chunk);
        }
        else {
            logger.warn("Connection about to halt so dropping chunk " + chunk);
        }
    }


    @Override
    public void run() {
        while (!isHalted()) {
            try {
                String message = queue.poll();
                if (message != null) {
                    HttpMessagingUtils.writeChunk(writer, message);
                }
                else {
                    try {
                        Thread.sleep(ConnectionThread.SEND_SLEEP_MS);
                    }
                    catch (InterruptedException e) {
                        //
                    }
                }
            }
            catch (IOException e) {
                logger.debug("Sender thread: Connection lost. Halting connection " + connection.getConnectionId());
                halt();
                connection.halt();
            }
        }
        logger.debug("send loop left");
    }


    @Override
    public synchronized void halt() {
        running = false;
        interrupt();
        QueueUtils.waitUntilEmpty(queue, ConnectionThread.SEND_QUEUE_TIMEOUT_MS);
    }


    @Override
    public boolean isHalted() {
        return !running && queue.isEmpty();
    }

}