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

package de.unidue.inf.is.ezdl.dlservices.backbone.mta.gated;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import de.unidue.inf.is.ezdl.dlbackend.agent.AbstractAgent;
import de.unidue.inf.is.ezdl.dlbackend.agent.handlers.RequestHandler;
import de.unidue.inf.is.ezdl.dlbackend.data.agent.AgentStatus;
import de.unidue.inf.is.ezdl.dlbackend.data.agent.StringAgentStatus;
import de.unidue.inf.is.ezdl.dlbackend.message.Message;
import de.unidue.inf.is.ezdl.dlbackend.message.content.CancelRequestNotify;
import de.unidue.inf.is.ezdl.dlbackend.misc.SimpleMailSender;
import de.unidue.inf.is.ezdl.dlcore.log.SessionType;
import de.unidue.inf.is.ezdl.dlcore.message.MTAMessage;
import de.unidue.inf.is.ezdl.dlcore.message.MTAMessageCoder;
import de.unidue.inf.is.ezdl.dlcore.message.content.AliveAsk;
import de.unidue.inf.is.ezdl.dlcore.message.content.AliveTell;
import de.unidue.inf.is.ezdl.dlcore.message.content.AvailableWrappersAsk;
import de.unidue.inf.is.ezdl.dlcore.message.content.CancelSearchNotify;
import de.unidue.inf.is.ezdl.dlcore.message.content.ClearQueryHistoryNotify;
import de.unidue.inf.is.ezdl.dlcore.message.content.DocumentDetailsAsk;
import de.unidue.inf.is.ezdl.dlcore.message.content.DocumentQueryAsk;
import de.unidue.inf.is.ezdl.dlcore.message.content.LoginAsk;
import de.unidue.inf.is.ezdl.dlcore.message.content.LoginTell;
import de.unidue.inf.is.ezdl.dlcore.message.content.LogoutAsk;
import de.unidue.inf.is.ezdl.dlcore.message.content.MessageContent;
import de.unidue.inf.is.ezdl.dlcore.message.content.QueryHistoryAsk;
import de.unidue.inf.is.ezdl.dlcore.message.content.RelatedTermsAsk;
import de.unidue.inf.is.ezdl.dlcore.message.content.StoreQueryHistoryNotify;
import de.unidue.inf.is.ezdl.dlcore.message.content.SynonymousTermsAsk;
import de.unidue.inf.is.ezdl.dlcore.message.content.UserLogNotify;
import de.unidue.inf.is.ezdl.dlcore.message.content.library.AddGroupNotify;
import de.unidue.inf.is.ezdl.dlcore.message.content.library.AddToLibraryNotify;
import de.unidue.inf.is.ezdl.dlcore.message.content.library.DeleteGroupNotify;
import de.unidue.inf.is.ezdl.dlcore.message.content.library.DeleteLibraryNotify;
import de.unidue.inf.is.ezdl.dlcore.message.content.library.GroupsAsk;
import de.unidue.inf.is.ezdl.dlcore.message.content.library.InitializeLibraryAsk;
import de.unidue.inf.is.ezdl.dlcore.message.content.library.LibraryAsk;
import de.unidue.inf.is.ezdl.dlcore.message.content.library.ReferenceSystemsAsk;
import de.unidue.inf.is.ezdl.dlcore.misc.EzDLException;
import de.unidue.inf.is.ezdl.dlcore.utils.PropertiesUtils;
import de.unidue.inf.is.ezdl.dlcore.utils.StringUtils;
import de.unidue.inf.is.ezdl.dlservices.backbone.mta.gated.handlers.LoginHandler;
import de.unidue.inf.is.ezdl.dlservices.backbone.mta.gated.handlers.TextMessageRequestHandler;
import de.unidue.inf.is.ezdl.dlservices.backbone.mta.gated.userlog.DefaultUserLogManager;
import de.unidue.inf.is.ezdl.dlservices.backbone.mta.gated.userlog.UserLogManager;



/**
 * The reason why this MTA is called "gated" is that it makes the backend
 * resemble a gated community of agents: the user client only talks to the MTA.
 * There is no connection whatsoever to any backend agent. The MTA dispatches
 * every message from the client to the intended receiver agent so that the
 * client does not have to have any knowledge about the backend.
 * 
 * @author mjordan
 */
public abstract class AbstractGatedMTA extends AbstractAgent implements MessageHandler {

    /**
     * Default port number for the fake HTTP server.
     */
    private static final int KEY_MTA_PORT_DEFAULT = 4567;

    /**
     * Default port number for the fake HTTP server.
     */
    private static final String KEY_MTA_HOST_DEFAULT = "localhost";

    /**
     * The name of the host on which this agent runs.
     */
    private static final String HOST_NAME;

    static {
        String hostName = "";
        try {
            hostName = InetAddress.getLocalHost().getHostName();
        }
        catch (UnknownHostException e) {
        }
        HOST_NAME = hostName;
    }

    /**
     * Reference to the {@link Server} that is used for the connection to the
     * client.
     */
    private Server server;
    /**
     * Information on the sessions.
     */
    private Map<String, ConnectionInfo> connections = Collections
                    .synchronizedMap(new HashMap<String, ConnectionInfo>());
    /**
     * Delegate for handling {@link LoginAsk} messages.
     */
    private LoginHandler loginHandler;
    /**
     * The name of the agent that deals with user sessions and authentication.
     */
    private String agentNameUserAgent;
    /**
     * The class that handles text messages like MOTD.
     */
    private TextMessageManager textManager;
    /**
     * The class that handles the user logging.
     */
    private UserLogManager userLog;
    /**
     * The mail address for the mail notifications of user logins/logouts.
     */
    private String loginLogoutMail;
    /**
     * If mail notifications should be sent.
     */
    private boolean sendLoginLogoutMail;


    @Override
    public void initOnline() {
        initTextMessageManager();

        final Properties props = getProperties();
        String loginLogoutMailProp = props.getProperty("notification.mail");
        if (!StringUtils.isEmpty(loginLogoutMailProp)) {
            loginLogoutMail = loginLogoutMailProp;
            sendLoginLogoutMail = true;
        }
        else {
            sendLoginLogoutMail = false;
        }

        super.initOnline();

        try {
            agentNameUserAgent = findAgent("/service/user");
        }
        catch (EzDLException e) {
            getLogger().error("Could not resolve user agent. Terminating.");
            halt();
            return;
        }

        initUserLog();

        final int port = PropertiesUtils.getIntProperty(getProperties(), getHostPropertyKey(), KEY_MTA_PORT_DEFAULT);
        final String host = props.getProperty(getPortPropertyKey(), KEY_MTA_HOST_DEFAULT);
        initServer(host, port);
    }


    protected abstract String getHostPropertyKey();


    protected abstract String getPortPropertyKey();


    private void initUserLog() {
        String agentNameUserLogAgent = null;

        try {
            agentNameUserLogAgent = findAgent("/service/userlog");
            userLog = new DefaultUserLogManager(this, agentNameUserLogAgent);
        }
        catch (EzDLException e) {
            getLogger().warn("Could not resolve user log agent. Cannot log user actions.");
        }
    }


    @Override
    protected Set<Class<? extends RequestHandler>> setupRequestHandlers() {
        Set<Class<? extends RequestHandler>> rh = super.setupRequestHandlers();
        rh.add(TextMessageRequestHandler.class);
        return rh;
    }


    /**
     * Initializes the {@link TextMessageManager}.
     * <p>
     * This method exists so it can be overridden for testing.
     */
    protected void initTextMessageManager() {
        textManager = new TextMessageManager(this);
    }


    /**
     * Returns the reference to the {@link TextMessageManager}.
     * 
     * @return the reference to the {@link TextMessageManager}
     */
    public TextMessageManager getTextManager() {
        return textManager;
    }


    /**
     * Initializes the {@link Server}.
     * 
     * @param host
     *            the host name that the server should bind to
     * @param port
     *            the port that the server should listen on
     */
    protected void initServer(String host, int port) {
        try {
            AliveAsk content = new AliveAsk();
            MTAMessage message = new MTAMessage(content, "");
            String pingMessage = MTAMessageCoder.getInstance().encode(message);
            getLogger().info("MTA running on port " + port);
            server = newServer(this, host, port, pingMessage);
        }
        catch (IOException e) {
            getLogger().error(e.getMessage(), e);
            halt();
        }
    }


    protected abstract Server newServer(MessageHandler messageHandler, String host, int port, String pingMessage)
                    throws IOException;


    @Override
    public void halt() {
        if (server != null) {
            server.halt();
        }
        super.halt();
    }


    @Override
    public boolean isHalted() {
        return isServerHalted() && super.isHalted();
    }


    private boolean isServerHalted() {
        if (server == null) {
            return true;
        }
        return server.isHalted();
    }


    @Override
    public Message receive(Message message) {
        Message unhandledMessage = super.receive(message);

        if (unhandledMessage != null) {
            String connectionId = unhandledMessage.getRequestInternalId();
            if (connectionId != null) {
                handleToClient(connectionId, unhandledMessage);
            }
        }
        return unhandledMessage;
    }


    // /////////////////////////////////////////////////////////////////////
    //
    // Backend -> Client
    //

    /**
     * Handles messages received from the backend that should be forwarded to a
     * client.
     * 
     * @param connectionId
     *            the ID of the connection object that deals with the client
     *            connection
     * @param unhandledMessage
     *            the message to forward to a client
     */
    public void handleToClient(String connectionId, Message unhandledMessage) {
        MTAMessage mtaMessage = new MTAMessage();
        mtaMessage.setContent(unhandledMessage.getContent());
        mtaMessage.setRequestId(unhandledMessage.getRequestId());

        sendToClient(connectionId, mtaMessage);
    }


    /**
     * Sends a message to all connected clients.
     * 
     * @param mtaMessage
     *            the message to send
     */
    public void sendTextToAll(MTAMessage mtaMessage) {
        Set<String> connectionIds = getConnections().keySet();
        for (String connectionId : connectionIds) {
            sendToClient(connectionId, mtaMessage);
        }
    }


    /**
     * Sends messages to a client.
     * 
     * @param connectionId
     *            the ID of the connection object that deals with the client
     *            connection
     * @param mtaMessage
     *            the message to forward to a client
     */
    public void sendToClient(String connectionId, MTAMessage mtaMessage) {
        userLogMessage(connectionId, mtaMessage);
        String messageStr;
        try {
            messageStr = MTAMessageCoder.getInstance().encode(mtaMessage);
            server.send(connectionId, messageStr);
        }
        catch (IOException e) {
            getLogger().error(e.getMessage(), e);
        }
    }


    // /////////////////////////////////////////////////////////////////////
    //
    // Client -> backend
    //

    @Override
    public boolean handleFromClient(String connectionId, String chunk) {
        /*
         * Nice for debugging but bad in production because we might log a chunk
         * that is a 500 MB avi file sent to the wrong port.
         */
        // log("client2back", chunk);
        MTAMessage message = getMessageFromClient(connectionId, chunk);
        if (message != null) {
            handleMessageFromClient(connectionId, message);
            return true;
        }
        else {
            return false;
        }
    }


    /**
     * Transforms the chunk passed from the {@link Server} with the given ID
     * into a {@link Message}.
     * 
     * @param connectionId
     *            the ID of the connection the chunk came from
     * @param messageStr
     *            the chunk to transform
     * @return the {@link Message} object or null if the chunk could not be
     *         transformed into a Message (e.g. because the deserialization went
     *         wrong or because there is no way to map the
     *         {@link MessageContent} received within the message to a message
     *         content type the backend knows)
     */
    protected MTAMessage getMessageFromClient(String connectionId, String messageStr) {
        try {
            return MTAMessageCoder.getInstance().decode(messageStr);
        }
        catch (IOException e) {
            getLogger().error("Message from client could not be transformed: " + messageStr, e);
            return null;
        }
    }


    /**
     * Handles correctly parsed messages that arrived from the client. Handling
     * might also involve dropping unauthorized messages or dropping the
     * connection.
     * 
     * @param connectionId
     *            the ID of the connection over which the message arrived
     * @param message
     *            the message to handle
     */
    protected void handleMessageFromClient(String connectionId, MTAMessage message) {
        MessageContent content = message.getContent();
        if (content instanceof LoginAsk) {
            getLoginHandlerInstance().handleLogin(connectionId, message);
        }
        else if (content instanceof AliveTell) {
            /*
             * We don't log AliveTell but we have to handle it so that
             * AliveTells that arrive when the connection is not yet
             * authenticated don't terminate the connection.
             */
        }
        else if (isConnectionAuthenticated(connectionId)) {
            forwardMessageFromClient(connectionId, message);
        }
        else {
            terminateConnection(connectionId);
        }
    }


    /**
     * Returns an instance of the {@link LoginHandler}.
     * 
     * @return the LoginHandler
     */
    protected synchronized LoginHandler getLoginHandlerInstance() {
        if (loginHandler == null) {
            loginHandler = new LoginHandler(this);
        }
        return loginHandler;
    }


    /**
     * Sends the given message "as is" to the receiver in the backend.
     * 
     * @param message
     *            the message to send
     */
    protected void forwardMessageFromClient(String connectionId, MTAMessage message) {
        userLogMessage(connectionId, message);
        final Message tMessage = transformMessage(connectionId, message);

        if (tMessage != null) {
            send(tMessage);
        }
        else {
            getLogger().info("Dropped unknown message content " + message);
        }
    }


    /**
     * Transforms an inbound client message into a message for some agent.
     * 
     * @param connectionId
     *            the ID of the connection over which the message arrived
     * @param message
     *            the message to transform
     * @return the transformed message
     */
    private Message transformMessage(String connectionId, MTAMessage message) {
        Message transformedMessage = new Message();

        final MessageTransformation mt = transform(message.getContent());

        if (mt != null) {
            transformedMessage.setRequestId(message.getRequestId());
            transformedMessage.setRequestInternalId(connectionId);
            transformedMessage.setFrom(agentName());
            transformedMessage.setTo(mt.getTo());
            transformedMessage.setContent(mt.getMessageContent());
        }
        else {
            transformedMessage = null;
        }
        return transformedMessage;
    }


    /**
     * Returns a strategy for transforming a message based on the content
     * passed.
     * 
     * @param content
     *            the content to base the transformation on
     * @return the transformation strategy
     */
    private MessageTransformation transform(MessageContent content) {
        MessageTransformation mt = null;
        try {
            if (content instanceof AvailableWrappersAsk) {
                mt = new MessageTransformation(getDirectoryName(), content);
            }
            if (content instanceof DocumentQueryAsk) {
                mt = new MessageTransformation(findAgent("/service/search"), content);
            }
            if (content instanceof DocumentDetailsAsk) {
                mt = new MessageTransformation(findAgent("/service/repository"), content);
            }
            if (content instanceof QueryHistoryAsk) {
                mt = new MessageTransformation(findAgent("/service/queryhistory"), content);
            }
            if (content instanceof StoreQueryHistoryNotify) {
                mt = new MessageTransformation(findAgent("/service/queryhistory"), content);
            }
            if (content instanceof ClearQueryHistoryNotify) {
                mt = new MessageTransformation(findAgent("/service/queryhistory"), content);
            }
            if (content instanceof RelatedTermsAsk) {
                mt = new MessageTransformation(findAgent("/service/terminfo"), content);
            }
            if (content instanceof SynonymousTermsAsk) {
                mt = new MessageTransformation(findAgent("/service/terminfo"), content);
            }
            if (content instanceof LibraryAsk) {
                mt = new MessageTransformation(findAgent("/service/library"), content);
            }
            if (content instanceof AddToLibraryNotify) {
                mt = new MessageTransformation(findAgent("/service/library"), content);
            }
            if (content instanceof DeleteLibraryNotify) {
                mt = new MessageTransformation(findAgent("/service/library"), content);
            }
            if (content instanceof GroupsAsk) {
                mt = new MessageTransformation(findAgent("/service/library"), content);
            }
            if (content instanceof InitializeLibraryAsk) {
                mt = new MessageTransformation(findAgent("/service/library"), content);
            }
            if (content instanceof AddGroupNotify) {
                mt = new MessageTransformation(findAgent("/service/library"), content);
            }
            if (content instanceof DeleteGroupNotify) {
                mt = new MessageTransformation(findAgent("/service/library"), content);
            }
            if (content instanceof ReferenceSystemsAsk) {
                mt = new MessageTransformation(findAgent("/service/library"), content);
            }
            if (content instanceof UserLogNotify) {
                // Do not handle UserLogNotify
            }
            if (content instanceof CancelSearchNotify) {
                CancelSearchNotify cancel = (CancelSearchNotify) content;
                final String queryID = cancel.getQueryID();
                final boolean sendPartialResults = cancel.isSendPartialResults();
                content = new CancelRequestNotify(queryID, sendPartialResults);
                mt = new MessageTransformation(findAgent("/service/search"), content);
            }
        }
        catch (EzDLException e) {
            getLogger().error("Could not transform incoming message", e);
            mt = null;
        }
        return mt;
    }


    // /////////////////////////////////////////////////////////////////////
    //
    // Connection handling
    //

    /**
     * Logs in a user, sending the given tell message to the client with the
     * given connection ID.
     * 
     * @param connectionId
     *            the connection ID to log in on
     * @param tell
     *            the message content to forward to the client
     */
    public void loginUser(String connectionId, LoginAsk ask, LoginTell tell) {
        forwardTellToClient(connectionId, tell);
        final ConnectionInfo info = new ConnectionInfo(connectionId, tell, ask.getSessionType());
        connections.put(connectionId, info);

        if (userLog != null) {
            userLog.logLogin(info);
        }

        sendUserLoginMailNotification(tell.getLogin(), connectionId);

        getTextManager().handleMotdMessage(connectionId, tell);
    }


    /**
     * Forwards the {@link LoginTell} message to the client.
     * 
     * @param connectionId
     *            the ID of the connection to send to
     * @param tell
     *            the login message to forward
     */
    private void forwardTellToClient(String connectionId, LoginTell tell) {
        if (tell != null) {
            Message toClient = new Message();
            toClient.setContent(tell);
            handleToClient(connectionId, toClient);
        }
    }


    /**
     * Returns if the connection with the given ID is authenticated.
     * 
     * @return true, if authenticated. Else false.
     */
    private boolean isConnectionAuthenticated(String connectionId) {
        boolean authenticated = false;
        ConnectionInfo session = connections.get(connectionId);
        if (session != null) {
            authenticated = (session.getUserInfo() != null);
        }
        return authenticated;
    }


    /**
     * Terminates the given connection and unregisters it.
     * 
     * @param connectionId
     *            the ID of the connection to terminate
     */
    public void terminateConnection(String connectionId) {
        getLogger().info("Terminating connection " + connectionId);
        server.haltConnection(connectionId);
        connections.remove(connectionId);
    }


    /**
     * Terminates a connection after sending a farewell message to the client.
     * 
     * @param connectionId
     *            the ID of the connection to terminate
     * @param farewellMessage
     *            the last message that the client will get
     */
    public void terminateConnection(String connectionId, Message farewellMessage) {
        handleToClient(connectionId, farewellMessage);
        terminateConnection(connectionId);
    }


    @Override
    public void handleConnectionLost(String connectionId) {
        getLogger().debug("Closing connection for " + connectionId);
        logoutConnectionAtUserAgent(connectionId);
        connections.remove(connectionId);
    }


    /**
     * Sends a {@link LogoutAsk} message to the user agent.
     * 
     * @param connectionId
     *            the ID of the connection whose user is to be logged out
     */
    private void logoutConnectionAtUserAgent(String connectionId) {
        ConnectionInfo info = connections.get(connectionId);

        if (info != null) {
            if (userLog != null) {
                userLog.logLogout(info);
            }
            LoginTell userInfo = info.getUserInfo();
            String sessionId = userInfo.getSessionId();
            LogoutAsk ask = new LogoutAsk(sessionId);
            String rid = getNextRequestID();
            Message message = new Message(agentName(), getLoginLogoutReceiverName(), ask, rid);
            send(message);

            sendUserLogoutMailNotification(userInfo.getLogin());
        }
    }


    /**
     * Returns the name of the agent that should receive {@link LoginAsk} or
     * {@link LogoutAsk} messages.
     * 
     * @return the agent name
     */
    public String getLoginLogoutReceiverName() {
        return agentNameUserAgent;
    }


    private void userLogMessage(String connectionId, MTAMessage message) {
        if (userLog != null) {
            ConnectionInfo session = connections.get(connectionId);
            if (session != null) {
                LoginTell userInfo = session.getUserInfo();
                userLog.logMessage(connectionId, userInfo, message.getContent());
            }
            else {
                getLogger().warn("Tried to log an event for a non-existing session.");
            }
        }
    }


    void sendUserLoginMailNotification(String login, String connectionId) {
        if (sendLoginLogoutMail) {
            String clientHostName = server.getClientHost(connectionId);
            SimpleMailSender.send("localhost", loginLogoutMail, loginLogoutMail, "Login (" + HOST_NAME + "): User "
                            + login + " logged in", "User: " + login + " logged into ezDL\non host: " + HOST_NAME
                            + "\nfrom host: " + clientHostName);
        }
    }


    void sendUserLogoutMailNotification(String login) {
        if (sendLoginLogoutMail) {
            SimpleMailSender.send("localhost", loginLogoutMail, loginLogoutMail, "Logout (" + HOST_NAME + "): User "
                            + login + " logged out", "User: " + login + " logged out");
        }
    }


    @Override
    public void log(String type, String message) {
        getLog().add(type, message);

    }


    @Override
    public AgentStatus getStatus() {
        String superStatus = super.getStatus().asString();
        StringBuilder status = new StringBuilder();
        status.append(superStatus);
        status.append("Connections: ").append(server.connections()).append('\n');
        status.append("Sessions: ").append(connections.size()).append('\n');

        final Collection<ConnectionInfo> connectionInfos = connections.values();
        for (ConnectionInfo connection : connectionInfos) {
            final String id = connection.getConnectionId();
            final SessionType type = connection.getSessionType();
            final LoginTell userTell = connection.getUserInfo();
            final long loginTime = connection.getLoginTime();
            status.append(id).append(" (").append(type).append("): ");
            status.append(userTell.getLogin()).append(" since ").append(new Date(loginTime));
            status.append('\n');
        }
        return new StringAgentStatus(status.toString());
    }


    // /////////////////////////////////////////////////////////////////////
    //
    // Testing
    //

    /**
     * Returns a reference to the server for testing purposes.
     * 
     * @return the reference to the server
     */
    protected Server getServer() {
        return server;
    }


    /**
     * Returns the connection map.
     * 
     * @return the map between connection IDs and their authentication
     *         information
     */
    protected Map<String, ConnectionInfo> getConnections() {
        return connections;
    }

}
