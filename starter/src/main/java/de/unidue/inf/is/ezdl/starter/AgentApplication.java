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

package de.unidue.inf.is.ezdl.starter;

import java.util.Properties;
import java.util.UUID;

import org.apache.log4j.Logger;

import de.unidue.inf.is.ezdl.dlbackend.Logging;
import de.unidue.inf.is.ezdl.dlbackend.agent.Agent;
import de.unidue.inf.is.ezdl.dlbackend.agent.connectors.ActiveMqJmsAgentConnector;
import de.unidue.inf.is.ezdl.dlbackend.agent.connectors.AgentConnector;
import de.unidue.inf.is.ezdl.dlbackend.agent.connectors.CorbaAgentConnector;
import de.unidue.inf.is.ezdl.dlbackend.agent.connectors.HttpAgentConnector;
import de.unidue.inf.is.ezdl.dlcore.misc.ConnectionFailedException;
import de.unidue.inf.is.ezdl.dlcore.utils.PropertiesUtils;



/**
 * Convenience class to start an agent.
 * 
 * @author ezDL team
 */
public final class AgentApplication {

    private static Logger logger = Logger.getLogger(AgentApplication.class);

    private static final String CORBA = "corba";
    private static final String JMS = "jms";
    private static final String HTTP = "http";


    /**
     * This class is not instantiable.
     */
    private AgentApplication() {
        // we do nothing at all
    }


    /**
     * Main class. Usage: AgentApplication [orbParams] &lt;class_name&gt;
     * &lt;agent_name&gt;
     * 
     * @param args
     *            the arguments that are passed from the command line
     * @throws ClassNotFoundException
     *             if no class is found in classpath
     * @throws ConnectionFailedException
     *             if no connection can be established
     */
    public static void main(String args[]) throws ClassNotFoundException, ConnectionFailedException {
        String className = null;
        String agentName = null;
        String propertiesFileName = null;

        if (args.length < 3) {
            showUsageAndExit();
        }

        propertiesFileName = args[args.length - 3];
        className = args[args.length - 2];
        agentName = args[args.length - 1];

        Logging.initLogging();
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {

            @Override
            public void uncaughtException(Thread t, Throwable e) {
                logger.error("Uncaught exception in Thread " + t.getName(), e);
            }
        });

        Properties props = PropertiesUtils.readPropertiesFromFileTree(propertiesFileName);

        Agent agent = null;
        String method = "";
        try {
            agent = (Agent) Class.forName(className).newInstance();

            if (agent.init(agentName, props) == true) {

                method = props.getProperty("connector", CORBA);

                AgentConnector connector = null;

                if (CORBA.equalsIgnoreCase(method)) {
                    connector = getCorbaClient(args, agent, props);
                }
                else if (HTTP.equalsIgnoreCase(method)) {
                    connector = getHttpClient(agent, props);
                }
                else if (JMS.equalsIgnoreCase(method)) {
                    connector = getJmsClient(agent, props);
                }
                else {
                    connector = getCorbaClient(args, agent, props);
                    logger.error(method + " is not supported. Using CORBA.");
                }

                agent.setConnector(connector);
                agent.goOnline();
            }
        }
        catch (InstantiationException e) {
            System.err.println("Could not initialize agent class " + className
                            + ". Please make sure it has a default constructor.");
        }
        catch (IllegalAccessException e) {
            System.err.println("The agent class " + className + " can't be accessed.");
        }
        catch (ClassNotFoundException e) {
            String classPath = System.getProperty("java.class.path", ".");
            System.err.println("Cannot find agent class " + className + " in the class path " + classPath);
        }
        catch (ConnectionFailedException e) {
            System.err.println("Could not connect to the agent back end.");
            if (CORBA.equalsIgnoreCase(method)) {
                System.err.println("Please make sure orbd is executed first " //
                                + "and you have set the right properties (port and host)");
            }
            agent.halt();
        }
    }


    /**
     * Returns an initialized connector for a CORBA-based connection.
     * 
     * @param args
     *            command line args that contain "-ORBInitialPort"
     * @param agent
     *            reference to the agent for callbacks
     * @param props
     *            the properties
     * @return the ready-to-use CORBA connector
     */
    private static AgentConnector getCorbaClient(String args[], Agent agent, Properties props)
                    throws ConnectionFailedException {
        AgentConnector connector = null;
        try {
            connector = new CorbaAgentConnector(args, agent);
        }
        catch (IllegalArgumentException e) {
            throw new ConnectionFailedException("it failed", e);
        }
        return connector;
    }


    /**
     * Returns an initialized connector for a JMS-based connection.
     * 
     * @param agent
     *            reference to the agent for callbacks
     * @param props
     *            the properties
     * @return the ready-to-use JMS connector
     */
    private static AgentConnector getJmsClient(Agent agent, Properties props) {
        String providerUrl = props.getProperty("jms.provider.url", "tcp://localhost:61616");
        AgentConnector agentConnector = new ActiveMqJmsAgentConnector(agent, providerUrl);
        return agentConnector;
    }


    /**
     * Returns an initialized connector for an HTTP-based connection.
     * 
     * @param agent
     *            reference to the agent for callbacks
     * @param props
     *            the properties
     * @return the ready-to-use HTTP connector
     */
    private static AgentConnector getHttpClient(Agent agent, Properties props) throws ConnectionFailedException {
        String mtaHost = props.getProperty("mta.host", "localhost");
        int mtaPort = PropertiesUtils.getIntProperty(props, "mta.port", 8080);
        int timeOutSecs = PropertiesUtils.getIntProperty(props, "mta.connectionTimeoutSecs", 5);

        String randomPart = UUID.randomUUID().toString();
        agent.init("ua" + randomPart, props);
        return new HttpAgentConnector(agent, mtaHost, mtaPort, timeOutSecs);
    }


    /**
     * Prints usage information.
     */
    private static void showUsageAndExit() {
        System.err.println("Usage: agent.AgentApplication [orbParams] <properties_name> <class_name> <agent_name>");
        System.exit(1);
    }
}
