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

package de.unidue.inf.is.ezdl.dlservices.backbone.mta.gated.userlog;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

import de.unidue.inf.is.ezdl.dlbackend.agent.Agent;
import de.unidue.inf.is.ezdl.dlbackend.agent.RequestIDFactory;
import de.unidue.inf.is.ezdl.dlbackend.message.Message;
import de.unidue.inf.is.ezdl.dlcore.log.UserLogConstants;
import de.unidue.inf.is.ezdl.dlcore.message.content.LoginTell;
import de.unidue.inf.is.ezdl.dlcore.message.content.MessageContent;
import de.unidue.inf.is.ezdl.dlcore.message.content.UserLogNotify;
import de.unidue.inf.is.ezdl.dlcore.utils.StringUtils;
import de.unidue.inf.is.ezdl.dlservices.backbone.mta.gated.ConnectionInfo;



abstract class AbstractUserLogManager implements UserLogManager {

    private final Logger logger = Logger.getLogger(AbstractUserLogManager.class);

    private String agentNameUserLogAgent;
    private Agent mta;
    private Map<String, AtomicInteger> sequenceNumbers = Collections
                    .synchronizedMap(new HashMap<String, AtomicInteger>());


    /**
     * Creates a new instance of the manager for the given agent.
     * 
     * @param agent
     *            the agent used for sending the log messages
     * @param agentNameUserLog
     *            the name of the receiver of the log messages
     */
    public AbstractUserLogManager(Agent agent, String agentNameUserLog) {
        this.mta = agent;
        this.agentNameUserLogAgent = agentNameUserLog;
    }


    protected void sendToUserLog(String connectionId, final UserLogNotify notify) {
        if (!StringUtils.isEmpty(agentNameUserLogAgent)) {
            notify.backendTimestampNow();
            notify.setSequenceNumber(getNextSequenceNumber(connectionId));
            final String agentName = mta.agentName();
            final String rid = RequestIDFactory.getInstance().getNextRequestID(agentName);
            final Message msg = new Message(agentName, agentNameUserLogAgent, notify, rid);
            mta.send(msg);
        }
    }


    protected int getNextSequenceNumber(String connectionId) {
        AtomicInteger seqN = sequenceNumbers.get(connectionId);
        if (seqN == null) {
            synchronized (sequenceNumbers) {
                seqN = sequenceNumbers.get(connectionId);
                if (seqN == null) {
                    seqN = new AtomicInteger(-1);
                    sequenceNumbers.put(connectionId, seqN);
                }
            }
        }
        return seqN.incrementAndGet();
    }


    /**
     * Logs a login event.
     * 
     * @param info
     *            the {@link ConnectionInfo} to log
     */
    @Override
    public void logLogin(ConnectionInfo info) {
        logger.debug("Logging in connection: " + info);
        if (agentNameUserLogAgent != null) {
            final LoginTell userInfo = info.getUserInfo();
            final UserLogNotify notify = new UserLogNotify(userInfo.getSessionId(),
                            UserLogConstants.EVENT_NAME_SESSION_START);
            notify.addParameter("login", userInfo.getLogin());
            notify.addParameter("connection", info.getConnectionId());
            notify.addParameter("type", getSessionType(info));
            sendToUserLog(info.getConnectionId(), notify);
        }
    }


    private String getSessionType(ConnectionInfo info) {
        switch (info.getSessionType()) {
            case DEBUG: {
                return "debug";
            }
            case STANDARD: {
                return "normal";
            }
            default: {
                throw new UnsupportedOperationException("Unknown session type " + info.getSessionType());
            }
        }
    }


    /**
     * Logs a logout event.
     * 
     * @param info
     *            the {@link ConnectionInfo} to log
     */
    @Override
    public void logLogout(ConnectionInfo info) {
        logger.debug("Logging out connection: " + info);
        if (agentNameUserLogAgent != null) {
            final LoginTell userInfo = info.getUserInfo();
            final UserLogNotify notify = new UserLogNotify(userInfo.getSessionId(),
                            UserLogConstants.EVENT_NAME_SESSION_END);
            sendToUserLog(info.getConnectionId(), notify);
            sequenceNumbers.remove(info.getConnectionId());
        }
    }


    @Override
    public void logMessage(String connectionId, LoginTell userInfo, MessageContent content) {
        if (content instanceof UserLogNotify) {
            sendToUserLog(connectionId, (UserLogNotify) content);
        }
        else {
            UserLogNotify logNotify = logEvent(userInfo.getSessionId(), content);
            if (logNotify != null) {
                sendToUserLog(connectionId, logNotify);
            }
        }
    }


    protected abstract UserLogNotify logEvent(String sessionId, MessageContent content);
}
