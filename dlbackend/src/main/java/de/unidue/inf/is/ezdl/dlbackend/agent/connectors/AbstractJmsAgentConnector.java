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

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.log4j.Logger;

import de.unidue.inf.is.ezdl.dlbackend.agent.Agent;
import de.unidue.inf.is.ezdl.dlbackend.message.Message;
import de.unidue.inf.is.ezdl.dlbackend.message.MessageBytesCoder;
import de.unidue.inf.is.ezdl.dlbackend.message.MessageStringCoder;
import de.unidue.inf.is.ezdl.dlcore.misc.ConnectionFailedException;



/**
 * A agent connector for JMS providers.
 * 
 * @author tbeckers
 */
abstract class AbstractJmsAgentConnector implements AgentConnector, MessageListener, ExceptionListener {

    private Logger logger = Logger.getLogger(AbstractJmsAgentConnector.class);

    /**
     * The agent to which this connector belongs to.
     */
    private Agent agent;
    /**
     * The url of the JMS provider,
     */
    private String jmsProviderUrl;
    /**
     * True, if the connector is online (connected), else false.
     */
    private volatile boolean isOnline;
    /**
     * The JMS session of this connector.
     */
    private Session session;
    /**
     * The JMS connection.
     */
    private Connection connection;


    public AbstractJmsAgentConnector(Agent agent, String jmsProviderUrl) {
        this.agent = agent;
        this.jmsProviderUrl = jmsProviderUrl;
    }


    @Override
    public final void goOffline() throws IOException {
        try {
            if (connection != null) {
                connection.close();
            }
        }
        catch (JMSException e) {
            throw new IOException(e);
        }
    }


    @Override
    public final void goOnline() throws ConnectionFailedException {
        try {
            ConnectionFactory factory = retrieveConnectionFactory(jmsProviderUrl);
            connection = factory.createConnection();
            connection.setExceptionListener(this);
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            MessageConsumer messageConsumer = retrieveQueueMessageConsumer(session, agent.agentName());
            connection.start();

            messageConsumer.setMessageListener(this);
        }
        catch (JMSException e) {
            throw new ConnectionFailedException();
        }

        isOnline = true;
    }


    @Override
    public void onException(JMSException e) {
        logger.error(e.getMessage(), e);
    }


    /**
     * Retrieves the queue message consumer.
     * 
     * @param session
     *            the queue session
     * @param queueName
     *            the queue name
     * @return the queue message consumer
     * @throws JMSException
     *             if a JMS problem occured
     */
    protected abstract MessageConsumer retrieveQueueMessageConsumer(Session session, String queueName)
                    throws JMSException;


    /**
     * Retrieves the connection factory.
     * 
     * @param jmsProviderUrl
     *            the url od the JMS provider
     * @return the connection factory
     * @throws JMSException
     *             if a JMS problem occured
     */
    protected abstract ConnectionFactory retrieveConnectionFactory(String jmsProviderUrl);


    @Override
    public final void onMessage(javax.jms.Message jmsMessage) {
        try {
            Message receivedMessage = null;
            if (jmsMessage instanceof BytesMessage) {
                BytesMessage bytesMessage = (BytesMessage) jmsMessage;
                byte[] b = new byte[(int) bytesMessage.getBodyLength()];
                bytesMessage.readBytes(b);
                receivedMessage = MessageBytesCoder.getInstance().decode(b);
            }
            else if (jmsMessage instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) jmsMessage;
                receivedMessage = MessageStringCoder.getInstance().decode(textMessage.getText());
            }
            else {
                logger.error("JMS messages of type " + jmsMessage.getClass() + " cannot be handled");
            }
            if (receivedMessage != null) {
                agent.receive(receivedMessage);
            }
        }
        catch (JMSException e) {
            logger.error(e.getMessage(), e);
        }
        catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }


    @Override
    public final boolean isOnline() {
        return isOnline;
    }


    @Override
    public final synchronized void send(Message message) throws IOException {
        try {
            MessageProducer messageProducer = retrieveQueueMessageProducer(session, message.getTo());
            BytesMessage jmsMessage = session.createBytesMessage();
            jmsMessage.writeBytes(MessageBytesCoder.getInstance().encode(message));
            messageProducer.send(jmsMessage);
        }
        catch (JMSException e) {
            throw new IOException(e);
        }
    }


    /**
     * Retrieves the queue message producer.
     * 
     * @param session
     *            the queue session
     * @param queueName
     *            the queue name
     * @return the queue message producer
     * @throws JMSException
     *             if a JMS problem occured
     */
    protected abstract MessageProducer retrieveQueueMessageProducer(Session session, String queueName)
                    throws JMSException;

}
