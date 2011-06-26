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

import java.io.IOException;

import org.apache.log4j.Logger;
import org.omg.CORBA.Any;
import org.omg.CORBA.MARSHAL;

import de.unidue.inf.is.agent.AgentServant;
import de.unidue.inf.is.agent.Communicate;
import de.unidue.inf.is.agent.KQML;
import de.unidue.inf.is.agent.Performative;
import de.unidue.inf.is.ezdl.dlbackend.agent.Agent;
import de.unidue.inf.is.ezdl.dlbackend.message.Message;
import de.unidue.inf.is.ezdl.dlbackend.message.MessageStringCoder;
import de.unidue.inf.is.ezdl.dlcore.misc.ConnectionFailedException;



/**
 * Merely an adaptor that connects the Corba classes to the interface that ezDL
 * agents expect.
 * 
 * @author mjordan
 */
public class CorbaAgentConnector extends AgentServant implements AgentConnector {

    /**
     * The logger.
     */
    protected final Logger logger = Logger.getLogger(CorbaAgentConnector.class);
    /**
     * True, if the connector is online (connected), else false.
     */
    private boolean isOnline = false;

    private Agent agent;


    public CorbaAgentConnector(String[] args, Agent agent) {
        try {
            Communicate.init(args);
        }
        catch (org.omg.CORBA.COMM_FAILURE e1) {
            throw new IllegalArgumentException("Communication error - probably ORB not found", e1);
        }
        catch (Exception e) {
            logger.error("Caught exception", e);
        }

        this.agent = agent;
        String agentName = agent.agentName();
        logger.info("Starting CORBA agent " + agentName);
        try {
            init(agentName);
        }
        catch (Exception e) {
            logger.error("CORBA Agent " + agentName + " could not initialize.", e);
        }

        logger.info("CORBA Agent " + agentName + " initialized.");
    }


    public void start() {
        startServant();
    }


    public void stop() {
        shutdownServant();
    }


    @Override
    protected void act() {
    }


    @Override
    public void send(Message message) {
        Any content = Communicate.orb().create_any();

        try {
            String msg = MessageStringCoder.getInstance().encode(message);
            String logLine = msg.substring(0, 100);
            logger.debug(logLine);
            content.insert_wstring(msg);
        }
        catch (MARSHAL e) {
            logger.error("content creation did not work", e);
        }
        catch (IOException e) {
            logger.error("content creation did not work", e);
        }

        try {
            KQML.send(logger, name(), Performative.PERF_TELL, message.getTo(), message.getRequestId(),
                            message.getRequestId(), "text/xml", "TELL", content);
        }
        catch (Exception e) {
            logger.error("Could not send using KQML.send", e);
        }

    }


    /**
     * Wandelt die Performatives der Agent-Lib in ezDL-Nachrichten um.
     * 
     * @param perf
     *            Performative der Agent-Lib
     */
    @Override
    public synchronized void perform(Performative perf) throws Exception {
        switch (perf.get_type()) {
            case Performative.PERF_TELL:
                String msg = perf.get_content().extract_wstring();
                Message m = MessageStringCoder.getInstance().decode(msg);
                agent.receive(m);
                break;
            case Performative.PERF_SORRY:
                logger.debug("Sorry");
                break;
            default:
                getRadio().reply(this, perf, Performative.PERF_SORRY, "");
                break;
        }
    }


    public void handleConnectionAbort() {
        isOnline = false;
        agent.halt();
    }


    @Override
    public void goOnline() throws ConnectionFailedException {
        start();
        OnlineGoer t = new OnlineGoer();
        t.start();
        isOnline = true;
    }


    @Override
    public void goOffline() {
        isOnline = false;
        stop(); // kill Agent now ; and all his threads!
        try {
            Communicate.shutdown();
        }
        catch (Exception e) {
            logger.error("shutdown failed: ", e);
        }
    }


    @Override
    public boolean isOnline() {
        return isOnline;
    }


    private class OnlineGoer extends Thread {

        @Override
        public void run() {
            try {
                Communicate.run();
            }
            catch (Throwable t) {
                logger.error(t.getMessage(), t);
                handleConnectionAbort();
            }
        }
    }
}
