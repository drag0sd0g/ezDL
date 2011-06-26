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

package de.unidue.inf.is.ezdl.dlbackend.agent;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import de.unidue.inf.is.ezdl.dlbackend.agent.connectors.AgentConnector;
import de.unidue.inf.is.ezdl.dlbackend.agent.handlers.RequestHandler;
import de.unidue.inf.is.ezdl.dlbackend.agent.handlers.RequestHandlerAliveAsk;
import de.unidue.inf.is.ezdl.dlbackend.agent.handlers.RequestHandlerCancelRequest;
import de.unidue.inf.is.ezdl.dlbackend.agent.handlers.RequestHandlerKillAsk;
import de.unidue.inf.is.ezdl.dlbackend.agent.handlers.RequestHandlerLogAsk;
import de.unidue.inf.is.ezdl.dlbackend.agent.handlers.RequestHandlerReqMapAsk;
import de.unidue.inf.is.ezdl.dlbackend.agent.handlers.RequestHandlerStatusAsk;
import de.unidue.inf.is.ezdl.dlbackend.agent.requesthandling.RequestHandlerStore;
import de.unidue.inf.is.ezdl.dlbackend.data.agent.AgentLog;
import de.unidue.inf.is.ezdl.dlbackend.data.agent.AgentStatus;
import de.unidue.inf.is.ezdl.dlbackend.data.agent.RequestHandlerInfo;
import de.unidue.inf.is.ezdl.dlbackend.data.agent.StringAgentStatus;
import de.unidue.inf.is.ezdl.dlbackend.message.Message;
import de.unidue.inf.is.ezdl.dlbackend.message.content.DeregisterAsk;
import de.unidue.inf.is.ezdl.dlbackend.message.content.LogAsk;
import de.unidue.inf.is.ezdl.dlbackend.message.content.LogTell;
import de.unidue.inf.is.ezdl.dlbackend.message.content.RegisterAsk;
import de.unidue.inf.is.ezdl.dlbackend.message.content.RegisterTell;
import de.unidue.inf.is.ezdl.dlbackend.message.content.UserIdAsk;
import de.unidue.inf.is.ezdl.dlbackend.message.content.UserIdTell;
import de.unidue.inf.is.ezdl.dlbackend.security.DefaultSecurityManager;
import de.unidue.inf.is.ezdl.dlbackend.security.SecurityManager;
import de.unidue.inf.is.ezdl.dlcore.cache.Cache;
import de.unidue.inf.is.ezdl.dlcore.cache.ConcurrentMapCache;
import de.unidue.inf.is.ezdl.dlcore.cache.TimedCache;
import de.unidue.inf.is.ezdl.dlcore.message.content.ErrorNotify;
import de.unidue.inf.is.ezdl.dlcore.message.content.MessageContent;
import de.unidue.inf.is.ezdl.dlcore.misc.ClientVersionException;
import de.unidue.inf.is.ezdl.dlcore.misc.ConnectionFailedException;
import de.unidue.inf.is.ezdl.dlcore.misc.EzDLException;
import de.unidue.inf.is.ezdl.dlcore.misc.PropertiesKeys;
import de.unidue.inf.is.ezdl.dlcore.misc.TimeoutException;
import de.unidue.inf.is.ezdl.dlcore.utils.PropertiesUtils;



/**
 * Replaces the internal and the external agent from a project whose name is not
 * to be mentioned. The AbstractAgent implements most functionality of an agent.
 * Actual agent implementations have to implement the abstract methods and can
 * overwrite some methods.
 */
public abstract class AbstractAgent implements Agent {

    /**
     * Default value for the timeouts.
     */
    private static final int TIMEOUT_DEFAULT_MS = 5000;
    /**
     * The logger.
     */
    private final Logger logger = Logger.getLogger(getClass());
    /**
     * The name of this agent.
     */
    private volatile String agentName;
    /**
     * The connector to use for connecting to other clients
     */
    private AgentConnector connector;
    /**
     * Reference to the properties of the agent.
     */
    private Properties props;
    /**
     * The directory agent's name.
     */
    private String directoryName = null;
    /**
     * Reference to the agent message log.
     */
    private AgentLog log;
    /**
     * The management object for request handlers.
     */
    private RequestHandlerStore handlerStore;
    /**
     * Handles waiting for messages that don't have a RequestHandler to handle
     * them.
     */
    private MessageWaiter msgWaiter;
    /**
     * Used to decouple sending a message from the agent's thread.
     */
    private SendQueue sendQueue;
    /**
     * The security manager of this agent.
     */
    private SecurityManager securityManager;
    /**
     * True, if the Agent is running and not halted.
     */
    private boolean running = true;
    /**
     * True, if LogAsk and LogTell messages should be logged, too. See
     * {@link PropertiesKeys#LOG_LOGASKTELL}.
     */
    private boolean logLogAskTell;
    /**
     * Cache for user IDs.
     */
    private Cache sessionIdToUserIdCache;
    /**
     * The secret obtained from registering with the Directory and used for
     * authenticating with the Directory.
     */
    private String sharedSecret;
    /**
     * The resolver that deals with finding agents and caching their names.
     */
    private AgentNameResolver resolver;


    /**
     * Default constructor.
     */
    protected AbstractAgent() {
        super();
    }


    /**
     * Initialize the agent. This method runs essential pre-start code.
     * 
     * @param args
     *            Command line arguments
     * @param agentName
     *            Name of the agent
     * @param props
     *            The properties object
     * @return true, if Agent was successfully initialized. Else false.
     */
    @Override
    public boolean init(String agentName, Properties props) {
        return init(agentName, new DefaultSecurityManager(this), props);
    }


    /**
     * Initialize the agent. This method runs essential pre-start code.
     * 
     * @param args
     *            Command line arguments
     * @param agentName
     *            Name of the agent
     * @param securityManager
     *            the security manager
     * @param props
     *            The properties object
     * @return true, because this initialization does not fail (so we hope)
     */
    @Override
    public boolean init(String agentName, SecurityManager securityManager, Properties props) {
        this.props = props;
        this.agentName = agentName;
        final String timeoutKey = PropertiesKeys.DIR_TIMEOUT;
        int dirTimeoutMs = PropertiesUtils.getIntProperty(props, timeoutKey, TIMEOUT_DEFAULT_MS);
        this.msgWaiter = new MessageWaiter(dirTimeoutMs);
        this.securityManager = securityManager;
        this.sessionIdToUserIdCache = new TimedCache(new ConcurrentMapCache(), 10, TimeUnit.MINUTES);
        this.handlerStore = new RequestHandlerStore(this);
        logger.info("Starting agent " + agentName);

        final String agentLogMaxSize = PropertiesKeys.LOG_MAXSIZE;
        int agentLogSize = PropertiesUtils.getIntProperty(props, agentLogMaxSize, AgentLog.MAX_ENTRIES_DEFAULT);
        log = AgentLog.startLog(agentLogSize);

        logLogAskTell = "true".equals(props.getProperty(PropertiesKeys.LOG_LOGASKTELL, "false"));

        final String dirNameKey = PropertiesKeys.DIR_NAME;
        final String defaultDirName = PropertiesKeys.DIR_NAME_DEFAULT;
        directoryName = props.getProperty(dirNameKey, defaultDirName);
        initRequestHandlers();
        resolver = new AgentNameResolver(this);
        logger.info("Agent " + agentName + " initialized.");
        return true;
    }


    @Override
    public SecurityManager getSecurityManager() {
        return securityManager;
    }


    @Override
    public int userIdForSessionId(String sessionId) {
        Integer userIdFromCache = (Integer) sessionIdToUserIdCache.get(sessionId);
        if (userIdFromCache != null) {
            return userIdFromCache;
        }
        else {
            try {
                Message message = new Message();
                message.setTo(findAgent("/service/user"));
                message.setFrom(agentName());
                message.setContent(new UserIdAsk(sessionId));
                Message tellMessage = ask(message);
                UserIdTell tell = (UserIdTell) tellMessage.getContent();
                int userId = tell.getUserId();
                sessionIdToUserIdCache.put(sessionId, userId);
                return userId;
            }
            catch (TimeoutException e) {
                logger.error(e.getMessage(), e);
                throw new IllegalArgumentException();
            }
            catch (EzDLException e) {
                logger.error(e.getMessage(), e);
                throw new IllegalArgumentException();
            }
        }
    }


    /**
     * Initializes the RequestHandlers of the agent that handle incoming
     * requests.
     */
    private void initRequestHandlers() {
        Set<Class<? extends RequestHandler>> requestHandlers = setupRequestHandlers();
        handlerStore.initHandlers(requestHandlers);
    }


    /**
     * Returns the set of RequestHandlers that the agent uses to process
     * incoming messages. This has to be overwritten and chained using
     * super.setupRequestHandlers() to initialize an implementation of
     * AbstractAgent.
     * 
     * @return the set of RequestHandlers used to process incoming messages
     */
    protected Set<Class<? extends RequestHandler>> setupRequestHandlers() {
        Set<Class<? extends RequestHandler>> handlers = new HashSet<Class<? extends RequestHandler>>();
        handlers.add(RequestHandlerLogAsk.class);
        handlers.add(RequestHandlerReqMapAsk.class);
        handlers.add(RequestHandlerKillAsk.class);
        handlers.add(RequestHandlerAliveAsk.class);
        handlers.add(RequestHandlerCancelRequest.class);
        handlers.add(RequestHandlerStatusAsk.class);
        return handlers;
    }


    @Override
    public String agentName() {
        return agentName;
    }


    /**
     * The abstract implementation needs to know the service name therefore we
     * do need to force the subclass to implement this method. We need to make
     * sure the default constructor is not used cause we need the empty one to
     * construct the class and initialize the agent.
     * 
     * @return the service name of the agent.
     */
    public abstract String getServiceName();


    /**
     * Set the AgentConnector used to connect this agent to the other agents in
     * the backend.
     */
    @Override
    public void setConnector(AgentConnector client) {
        this.connector = client;
    }


    /**
     * Try to find the agents given by service. Ignore any error and return
     * empty Map if error occurs.
     * 
     * @param services
     *            the service where to search
     * @return the unmodifiableMap of agents or empty one if any error happens
     */
    @Override
    public Map<String, String> findAgentsByService(String... services) {
        return resolver.findAgentsByService(services);
    }


    /**
     * {@inheritDoc}
     * <p>
     * This implementation of {@link #findAgent(String)} is cached and only
     * contacts the directory if no information is locally available.
     */
    @Override
    public String findAgent(String service) throws EzDLException {
        return resolver.findAgent(service);
    }


    @Override
    public synchronized String[] findAllAgentNames(String service) throws EzDLException {
        return resolver.findAllAgentNames(service);
    }


    @Override
    public synchronized Map<String, String> findAllAgents(String service) throws EzDLException {
        return resolver.findAllAgents(service);
    }


    @Override
    public Properties getProperties() {
        return props;
    }


    @Override
    public AgentLog getLog() {
        return log;
    }


    @Override
    public String getNextRequestID() {
        return RequestIDFactory.getInstance().getNextRequestID(agentName());
    }


    /**
     * Agent-dependent initialization. Supposed to register the agent with the
     * Directory. Before the time this method is called:
     * <ul>
     * <li>Communication with the backend is set up.</li>
     * <li>The agent name is initialized.</li>
     * <li>The agent log is initialized.</li>
     * </ul>
     * <p>
     * Registers the agent with the Directory. If the registration doesn't work,
     * the {@link #halt()} is called, which stops the agent.
     */
    protected void initOnline() {
        logger.debug("initOnline() entered.");
        try {
            registerAgent();
        }
        catch (EzDLException e) {
            logger.error("Registration of " + getServiceName() + " failed!", e);
            halt();
        }
        logger.debug("initOnline() left");
    }


    /**
     * {@inheritDoc}
     * <p>
     * Unregisters the agent from the directory, halts the handler store, the
     * security manager and the send queue and disconnects from the network.
     */
    @Override
    public void halt() {
        logger.debug("halting");
        deregisterAgent();

        handlerStore.halt();
        securityManager.halt();
        sessionIdToUserIdCache.shutdown();

        if (sendQueue != null) {
            sendQueue.halt();
        }

        if (connector != null) {
            try {
                connector.goOffline();
            }
            catch (IOException e) {
                logger.error("shutdown with error", e);
            }
        }

        running = false;
    }


    @Override
    public boolean isHalted() {
        return !running;
    }


    @Override
    public void killRequestHandler(String requestId, boolean sendPartialResults) {
        handlerStore.killHandler(requestId, sendPartialResults);
    }


    protected synchronized void registerAgent() throws EzDLException {
        String requestId = getNextRequestID();

        RegisterAsk register = getRegisterContent();
        Message registerMessage = createDirMessage(register, requestId);

        Message dirMsg = ask(registerMessage);

        final MessageContent content = dirMsg.getContent();
        if (content instanceof ErrorNotify) {
            ErrorNotify notify = (ErrorNotify) content;
            if ("version".equals(notify.getDescription())) {
                throw new ClientVersionException();
            }
            throw new EzDLException("Error:" + notify.getDescription());
        }
        else {
            if (content instanceof RegisterTell) {
                RegisterTell tell = (RegisterTell) content;
                sharedSecret = tell.getSharedSecret();
            }
            log("register successful", register.getService());
        }
    }


    protected RegisterAsk getRegisterContent() {
        final String service = getServiceName();
        final RegisterAsk register = new RegisterAsk(service);
        return register;
    }


    protected synchronized void deregisterAgent() {
        final String requestId = getNextRequestID();
        final DeregisterAsk deregister = new DeregisterAsk(sharedSecret);
        send(createDirMessage(deregister, requestId));
    }


    @Override
    public void send(Message message) {
        if (!message.checkAndDecreaseTtl()) {
            logger.error("Detected message loop. Dropping message " + message);
            log.add("message loop", message.toString());
            return;
        }

        log("sending message", message);
        if (message.getTo().equals(agentName())) {
            logger.debug("Locally delivered message " + message);
            receive(message);
        }
        else {
            if (sendQueue != null) {
                sendQueue.send(message);
            }
        }
        // log("sent message", message.shortForm());
    }


    @Override
    public void send(String[] agentNameList, Message message) {
        for (String agentName : agentNameList) {
            send(new Message(message.getFrom(), agentName, message.getContent(), message.getRequestId()));
        }
    }


    @Override
    public Message ask(Message message) throws TimeoutException {
        Message answer = null;
        send(message);
        answer = msgWaiter.waitForRequestId(message.getRequestId());
        return answer;
    }


    /**
     * Receives a message.
     * <p>
     * The message is checked against looping and then forwarded to the message
     * queue of a suitable RequestHandler. The method returns as soon as this is
     * done and does not block until the message is processed.
     * 
     * @param message
     *            the message to process
     */
    @Override
    public Message receive(Message message) {
        log("received message", message);
        logger.debug("Received message" + message);

        logger.debug("Getting new handler for " + message);
        RequestHandler requestHandler = handlerStore.getHandler(message);
        logger.debug("Got handler for " + message + " - it is " + requestHandler);

        if (requestHandler != null) {
            requestHandler.addMessageToQueue(message);
            logger.info("Handling message " + message);
        }
        else if (!msgWaiter.messageArrived(message)) {
            logger.info("Received unhandled message " + message);
            return message;
        }
        return null;
    }


    @Override
    public void goOnline() throws ConnectionFailedException {
        sendQueue = new SendQueue(connector);
        connector.goOnline();
        initOnline();
    }


    /**
     * Returns the online state of the agent.
     * 
     * @return true, if the agent is online and connected. Else false.
     */
    public boolean isOnline() {
        return connector.isOnline();
    }


    /**
     * Creates a message from this agent to the directory.
     * 
     * @param content
     *            the content to send
     * @param requestId
     *            the request ID
     * @return the message object
     */
    public Message createDirMessage(MessageContent content, String requestId) {
        final String dirName = getDirectoryName();
        final Message msg = new Message(agentName(), dirName, content, requestId);
        return msg;
    }


    /**
     * Logs a message to the agent log.
     * <p>
     * Messages that contain {@link LogAsk} or {@link LogTell} content types are
     * not logged, if {@link #logLogAskTell} is true.
     * 
     * @param type
     * @param message
     */
    protected void log(String type, Message message) {
        if (getLog() != null) {
            final MessageContent content = message.getContent();
            final boolean isLogMsg = (content instanceof LogAsk) || (content instanceof LogTell);
            if (logLogAskTell || !isLogMsg) {
                getLog().add(type, message.toString());
            }
        }
    }


    /**
     * Logs an event to the agent log.
     * 
     * @param type
     * @param message
     */
    protected void log(String type, String message) {
        if (getLog() != null) {
            getLog().add(type, message);
        }
    }


    /**
     * Returns the logger.
     * 
     * @return the logger
     */
    protected Logger getLogger() {
        return logger;
    }


    @Override
    public Map<String, RequestHandlerInfo> getRequestInfo() {
        return handlerStore.getRequestInfo();
    }


    @Override
    public String getDirectoryName() {
        return directoryName;
    }


    /**
     * Returns the agents waiter.
     * 
     * @return the agents waiter
     */
    protected MessageWaiter getMessageWaiter() {
        return msgWaiter;
    }


    @Override
    public String getSharedSecret() {
        return sharedSecret;
    }


    @Override
    public AgentStatus getStatus() {
        AgentStatus status;
        StringWriter writer = new StringWriter();
        try {
            props.store(writer, "");
            StringBuilder statusMsg = new StringBuilder();
            statusMsg.append("Properties:\n").append(writer.toString()).append("\n");
            statusMsg.append("Send queue size: ").append(sendQueue.size()).append("\n");
            ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
            statusMsg.append("Thread count now: ").append(threadMXBean.getThreadCount()).append("\n");
            statusMsg.append("Threads started total: ").append(threadMXBean.getTotalStartedThreadCount()).append("\n");
            status = new StringAgentStatus(statusMsg.toString());
        }
        catch (IOException e) {
            final String message = "Error writing properties to in-memory Writer";
            logger.error(message);
            status = new StringAgentStatus(message);
        }

        return status;
    }

}
