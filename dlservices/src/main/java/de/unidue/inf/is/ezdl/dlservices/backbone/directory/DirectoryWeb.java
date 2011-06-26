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

package de.unidue.inf.is.ezdl.dlservices.backbone.directory;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.eclipse.jetty.http.security.Constraint;
import org.eclipse.jetty.http.security.Password;
import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.MappedLoginService;
import org.eclipse.jetty.security.SecurityHandler;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.UserIdentity;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.Resource;

import de.unidue.inf.is.ezdl.dlbackend.data.agent.AgentLog;
import de.unidue.inf.is.ezdl.dlbackend.data.agent.AgentRecord;
import de.unidue.inf.is.ezdl.dlbackend.data.agent.AgentStatus;
import de.unidue.inf.is.ezdl.dlbackend.data.agent.LogEntry;
import de.unidue.inf.is.ezdl.dlbackend.data.agent.RequestHandlerInfo;
import de.unidue.inf.is.ezdl.dlbackend.message.Message;
import de.unidue.inf.is.ezdl.dlbackend.message.content.LogAsk;
import de.unidue.inf.is.ezdl.dlbackend.message.content.LogTell;
import de.unidue.inf.is.ezdl.dlbackend.message.content.RequestMapAsk;
import de.unidue.inf.is.ezdl.dlbackend.message.content.RequestMapTell;
import de.unidue.inf.is.ezdl.dlbackend.message.content.StatusAsk;
import de.unidue.inf.is.ezdl.dlbackend.message.content.StatusTell;
import de.unidue.inf.is.ezdl.dlbackend.misc.TemplateParser;
import de.unidue.inf.is.ezdl.dlcore.Haltable;
import de.unidue.inf.is.ezdl.dlcore.message.content.MessageContent;
import de.unidue.inf.is.ezdl.dlcore.misc.PropertiesKeys;
import de.unidue.inf.is.ezdl.dlcore.misc.TimeoutException;
import de.unidue.inf.is.ezdl.dlcore.utils.ClosingUtils;
import de.unidue.inf.is.ezdl.dlcore.utils.HtmlUtils;
import de.unidue.inf.is.ezdl.dlcore.utils.IOUtils;



/**
 * Web Server for the administration of ezDL.
 * 
 * @author tbeckers
 */
public final class DirectoryWeb implements Haltable {

    private final class AgentInfoHandler extends AbstractHandler {

        @Override
        public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
                        throws IOException, ServletException {
            response.setStatus(HttpServletResponse.SC_OK);

            String requestURI = request.getRequestURI();
            if (requestURI.startsWith(PREFIX + ACTION_STATUS)) {
                response.setContentType(CONTENT_TYPE_TEXT_PLAIN);
                status(request, response);
            }
            else if (requestURI.startsWith(PREFIX + ACTION_AGENTLIST)) {
                response.setContentType(CONTENT_TYPE_TEXT_HTML);
                agentList(request, response);
            }
            else if (requestURI.startsWith(PREFIX + ACTION_HTMLLOG)) {
                response.setContentType(CONTENT_TYPE_TEXT_HTML);
                agentLog(request, response);
            }
            else if (requestURI.startsWith(PREFIX + ACTION_HTMLDIRLOG)) {
                response.setContentType(CONTENT_TYPE_TEXT_HTML);
                agentLog(request, response);
            }
            else if (requestURI.startsWith(PREFIX + ACTION_HTMLREQUEST)) {
                response.setContentType(CONTENT_TYPE_TEXT_HTML);
                agentRequest(request, response);
            }
            else if (requestURI.startsWith(PREFIX + ACTION_DIRPROPS)) {
                response.setContentType(CONTENT_TYPE_TEXT_PLAIN);
                dirProps(request, response);
            }
        }
    }


    private static final String CONTENT_TYPE_TEXT_HTML = "text/html";
    private static final String CONTENT_TYPE_TEXT_PLAIN = "text/plain";

    private static final String PARAMETER_KILLDIR = "killdir";
    private static final String PARAMETER_KILL = "kill";

    private static final String ACTION_DIRPROPS = "dirprops";
    private static final String ACTION_AGENTLIST = "agentlist";
    private static final String ACTION_HTMLREQUEST = "htmlrequest";
    private static final String ACTION_HTMLDIRLOG = "htmldirlog";
    private static final String ACTION_HTMLLOG = "htmllog";
    private static final String ACTION_STATUS = "status";

    private static final String TEMPLATES = "/templates";
    private static final String HTMLDIR = "/html";

    private static final String PREFIX = "/";

    private static Logger logger = Logger.getLogger(DirectoryWeb.class);

    private Directory dir;
    private String user;
    private String password;
    private Server server;


    /**
     * Constructor.
     * 
     * @param directory
     *            reference to the Directory
     */
    public DirectoryWeb(Directory directory) {
        dir = directory;

        Properties props = dir.getProperties();
        user = props.getProperty(PropertiesKeys.WEBADMIN_USER, "");
        password = props.getProperty(PropertiesKeys.WEBADMIN_PASSWORD, "");

        initServer(props);
    }


    private void initServer(Properties props) {
        HandlerList handlerList = new HandlerList();
        Handler agentHandler = new AgentInfoHandler();

        server = new Server(Integer.parseInt(props.getProperty(PropertiesKeys.WEBADMIN_PORT, "3456")));
        try {
            ResourceHandler resourceHandler = new ResourceHandler();
            resourceHandler.setBaseResource(Resource.newResource(getClass().getResource("/html")));
            handlerList.addHandler(resourceHandler);
        }
        catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

        SecurityHandler sh = initSecurity(agentHandler);
        handlerList.addHandler(sh);
        server.setHandler(handlerList);

        try {
            server.start();
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }


    private SecurityHandler initSecurity(Handler agentHandler) {
        MappedLoginService loginService = new MappedLoginService() {

            @Override
            protected void loadUsers() throws IOException {
            }


            @Override
            protected UserIdentity loadUser(String username) {
                return null;
            }
        };
        loginService.putUser(user, new Password(password), new String[] {
            "admin"
        });

        Constraint constraint = new Constraint();
        constraint.setName(Constraint.__BASIC_AUTH);
        constraint.setRoles(new String[] {
            "admin"
        });
        constraint.setName("ezDL Admin");
        constraint.setAuthenticate(true);

        ConstraintMapping cm = new ConstraintMapping();
        cm.setConstraint(constraint);
        cm.setPathSpec("/*");

        ConstraintSecurityHandler sh = new ConstraintSecurityHandler();
        sh.setLoginService(loginService);
        sh.setConstraintMappings(Arrays.asList(cm));
        sh.setHandler(agentHandler);
        return sh;
    }


    private void status(HttpServletRequest request, HttpServletResponse response) {
        String agent = request.getRequestURI().substring(PREFIX.length() + (ACTION_STATUS + PREFIX).length());

        if (agent != null) {
            String statusStr = "timeout";
            if (agent.equals(dir.agentName())) {
                statusStr = dir.getStatus().asString();
            }
            else {
                try {
                    Message answer = dir.ask(new Message(dir.agentName(), agent, new StatusAsk(), dir
                                    .getNextRequestID()));
                    if (answer.getContent() instanceof StatusTell) {
                        StatusTell tell = (StatusTell) answer.getContent();
                        AgentStatus status = tell.getStatus();
                        statusStr = status.asString();
                    }
                }
                catch (TimeoutException e) {
                    logger.error(e.getMessage(), e);
                }
            }
            write(response, statusStr);
        }
    }


    /**
     * Prints the agent list.
     */
    @SuppressWarnings("rawtypes")
    private void agentList(HttpServletRequest request, HttpServletResponse response) {
        TemplateParser tp = new TemplateParser(null, DirectoryWeb.class.getResourceAsStream(TEMPLATES + "/"
                        + "agentlist.html"));
        TemplateParser loop;
        Map parameter = request.getParameterMap();

        if (parameter.containsKey(PARAMETER_KILL)) {
            String[] agentToKill = (String[]) parameter.get(PARAMETER_KILL);
            dir.killThisAgent(agentToKill[0]);

            try {
                response.sendRedirect(response.encodeRedirectURL(PREFIX + ACTION_AGENTLIST));
            }
            catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }

        if (parameter.containsKey(PARAMETER_KILLDIR)) {
            String killHtml = IOUtils.readInputStreamAsString(DirectoryWeb.class.getResourceAsStream(HTMLDIR
                            + "/kill.html"));

            write(response, killHtml);
            killAgents(request, response);

            final Timer timer = new Timer();
            timer.schedule(new TimerTask() {

                @Override
                public void run() {
                    timer.cancel();
                    dir.halt();
                }
            }, 1000, 1000);
        }
        else {
            tp.addTag("<% prefix %>", PREFIX);
            tp.addTag("<% directoryAgentName %>", dir.agentName());
            tp.doParse();

            if ((loop = tp.loop("<% list %>", "<% /list %>")) != null) {
                boolean evenOdd = true;
                List<AgentRecord> agentList = getSortedAgentList();
                for (AgentRecord agent : agentList) {
                    loop.addTag("<% service %>", agent.getService());
                    loop.addTag("<% agent %>", agent.getName());
                    loop.addTag("<% cssclass %>", evenOdd ? "even" : "odd");
                    evenOdd = !evenOdd; // toggle flag
                    loop.doParse();
                }
                loop.finish();
            }

            write(response, tp.result());
        }
    }


    private List<AgentRecord> getSortedAgentList() {
        List<AgentRecord> agentList = dir.getAgentList("");
        Collections.sort(agentList, new Comparator<AgentRecord>() {

            @Override
            public int compare(AgentRecord o1, AgentRecord o2) {
                return o1.getService().compareTo(o2.getService());
            }
        });
        return agentList;
    }


    private void killAgents(HttpServletRequest request, HttpServletResponse response) {
        List<AgentRecord> agents = dir.getAgentList("");
        // First output of agents to kill
        for (AgentRecord agent : agents) {
            String killLine = "Killing " + agent.getName();
            write(response, killLine + "<br/>");
            logger.info(killLine);
        }
        write(response, "</body></html>");

        // Now kill the agents.
        // This doesn't work in one go in the above loop.
        for (AgentRecord agent : agents) {
            dir.killThisAgent(agent.getName());
        }
    }


    /**
     * Displays a page with the log of a remote agents.
     */
    private void agentLog(HttpServletRequest request, HttpServletResponse response) {
        String agent = "";

        if (request.getRequestURI().startsWith(PREFIX + ACTION_HTMLLOG)) {
            agent = request.getRequestURI().substring(PREFIX.length() + 8);
        }

        boolean isDirLog = false;
        if (request.getRequestURI().startsWith(PREFIX + ACTION_HTMLDIRLOG)) {
            isDirLog = true;
        }

        AgentLog log = null;
        if (isDirLog) {
            agent = dir.getDirectoryName();
            log = dir.getLog();
        }
        else {
            if (agent.isEmpty()) {
                agentList(request, response);
            }
            else {
                LogAsk content = new LogAsk();
                MessageContent answer = askDir(request, response, content, agent);
                if (answer instanceof LogTell) {
                    log = ((LogTell) answer).getLog();
                }
            }
        }

        if (log != null) {
            write(response, createAgentLog(log, agent));
        }
    }


    /**
     * Displays a page with information about known RequestHandler objects at a
     * remote agent.
     */
    private void agentRequest(HttpServletRequest request, HttpServletResponse response) {
        String agent = "";

        if (request.getRequestURI().startsWith(PREFIX + ACTION_HTMLREQUEST)) {
            agent = request.getRequestURI().substring(PREFIX.length() + ACTION_HTMLREQUEST.length() + 1);
        }
        if (agent.isEmpty()) {
            agentList(request, response);
        }
        else {
            Map<String, RequestHandlerInfo> map = null;
            if (agent.equals(dir.agentName())) {
                map = dir.getRequestInfo();
            }
            else {
                RequestMapAsk content = new RequestMapAsk();
                MessageContent answer = askDir(request, response, content, agent);
                if (answer instanceof RequestMapTell) {
                    map = ((RequestMapTell) answer).getRequestMap();
                }
            }
            if (map != null) {
                write(response, createRequestMap(map, agent));
            }
        }
    }


    /**
     * Uses the Directory to send a message to a remote agent.
     */
    private MessageContent askDir(HttpServletRequest request, HttpServletResponse response, MessageContent content,
                    String agent) {
        MessageContent answer = null;
        if (dir.isAgentRegistered(agent)) {
            try {
                answer = dir.ask(content, agent);
            }
            catch (TimeoutException e) {
                error(request, response, "Agent \"" + agent + "\" Timeout!");
            }
        }
        return answer;
    }


    /**
     * Creates a page with the agent log.
     */
    private String createAgentLog(AgentLog log, String agent) {
        TemplateParser loop;
        TemplateParser tp = new TemplateParser(null, DirectoryWeb.class.getResourceAsStream(TEMPLATES
                        + "/agentlog.html"));
        tp.addTag("<% agent %>", agent);
        tp.addTag("<% prefix %>", PREFIX);
        tp.doParse();

        if ((loop = tp.loop("<% loglist %>", "<% /loglist %>")) != null) {
            List<LogEntry> logEntries = log.getLogData();
            List<LogEntry> reverseEntries = reverse(logEntries);

            for (LogEntry entry : reverseEntries) {
                String time = entry.formatTimestamp();
                String line = entry.getLogStr();
                String type = entry.getType();
                loop.addTag("<% type %>", HtmlUtils.normalize(type));
                loop.addTag("<% timestamp %>", HtmlUtils.normalize(time));
                loop.addTag("<% log %>", HtmlUtils.normalize(line));
                loop.doParse();
            }
            loop.finish();
        }
        return tp.result();
    }


    private List<LogEntry> reverse(List<LogEntry> logEntries) {
        List<LogEntry> reversed = new ArrayList<LogEntry>(logEntries.size());
        for (LogEntry entry : logEntries) {
            reversed.add(0, entry);
        }
        return reversed;
    }


    /**
     * Creates a page with the request map for an agent.
     */
    private String createRequestMap(Map<String, RequestHandlerInfo> map, String agent) {
        TemplateParser loop;
        TemplateParser tp = new TemplateParser(null, DirectoryWeb.class.getResourceAsStream(TEMPLATES
                        + "/agentrequest.html"));
        tp.addTag("<% agent %>", agent);
        tp.addTag("<% prefix %>", PREFIX);
        tp.doParse();

        if ((loop = tp.loop("<% requestlist %>", "<% /requestlist %>")) != null) {
            for (String key : map.keySet()) {
                RequestHandlerInfo handler = map.get(key);
                String info = handler.getInfo();
                boolean running = handler.isRunning();

                StringBuffer out = new StringBuffer();
                out.append(running ? "RUNNING" : "HALTED");
                out.append(" ");
                out.append(" uptime ").append(handler.getUptimeSec()).append("s");
                out.append(" ");
                out.append(info);

                loop.addTag("<% name %>", HtmlUtils.normalize(key));
                loop.addTag("<% info %>", HtmlUtils.normalize(out.toString()));
                loop.doParse();
            }
            loop.finish();
        }
        return tp.result();
    }


    /**
     * Shows an error.
     */
    private void error(HttpServletRequest request, HttpServletResponse response, String error) {
        TemplateParser tp = new TemplateParser(null, DirectoryWeb.class.getResourceAsStream(TEMPLATES + "/"
                        + "error.html"));
        tp.addTag("<% error %>", error);
        tp.doParse();

        write(response, tp.result());
    }


    /**
     * Sends a page containing the properties.
     */
    private void dirProps(HttpServletRequest request, HttpServletResponse response) {
        try {
            write(response, "ezDL Properties:\n");
            Properties prop = dir.getProperties();

            write(response, propertiesToString(prop));

            write(response, "\n\n================================================================\n\n");

            write(response, "System Properties:\n");
            write(response, propertiesToString(System.getProperties()));
        }
        catch (Exception e) {
            write(response, "An error occurred during dumping the properties.");
        }
    }


    private static String propertiesToString(Properties properties) {
        StringWriter writer = null;
        try {
            writer = new StringWriter();
            properties.store(writer, "");
            return writer.toString();
        }
        catch (IOException e) {
            logger.error(e.getMessage(), e);
            return "";
        }
        finally {
            ClosingUtils.close(writer);
        }
    }


    private static void write(HttpServletResponse response, String s) {
        try {
            response.getWriter().println(s);
            response.getWriter().flush();
        }
        catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }


    @Override
    public void halt() {
        if (server != null) {
            try {
                server.stop();
            }
            catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }


    @Override
    public boolean isHalted() {
        return server.isStopped();
    }

}
