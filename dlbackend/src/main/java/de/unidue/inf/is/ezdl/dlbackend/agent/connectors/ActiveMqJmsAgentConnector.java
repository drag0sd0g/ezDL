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

import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;

import de.unidue.inf.is.ezdl.dlbackend.agent.Agent;



/**
 * A JMS-based agent connector for Apache ActiveMQ (<a
 * href="http://activemq.apache.org/">http://activemq.apache.org/</a>).
 * 
 * @author tbeckers
 */
public class ActiveMqJmsAgentConnector extends AbstractJmsAgentConnector {

    public ActiveMqJmsAgentConnector(Agent agent, String jmsProviderUrl) {
        super(agent, jmsProviderUrl);
    }


    @Override
    protected MessageConsumer retrieveQueueMessageConsumer(Session session, String queueName) throws JMSException {
        MessageConsumer messageConsumer = session.createConsumer(new ActiveMQQueue(queueName), null, false);
        return messageConsumer;
    }


    @Override
    protected MessageProducer retrieveQueueMessageProducer(Session session, String queueName) throws JMSException {
        MessageProducer messageProducer = session.createProducer(new ActiveMQQueue(queueName));
        messageProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
        return messageProducer;
    }


    @Override
    protected ConnectionFactory retrieveConnectionFactory(String jmsProviderUrl) {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(jmsProviderUrl);
        factory.setExclusiveConsumer(true);
        factory.setUseAsyncSend(true);
        return factory;
    }

}
