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

import java.net.BindException;

import org.apache.activemq.broker.BrokerService;
import org.apache.log4j.Logger;

import de.unidue.inf.is.ezdl.dlbackend.Logging;



public final class ActiveMqBroker {

    private static Logger logger = Logger.getLogger(ActiveMqBroker.class);


    private ActiveMqBroker() {
    }


    public static void main(String[] args) {
        try {
            Logging.initLogging();

            BrokerService broker = new BrokerService();
            broker.addConnector("tcp://localhost:61616");
            broker.start();
        }
        catch (Exception e) {
            if (e.getCause() instanceof BindException) {
                logger.info(e.getMessage());
                logger.info("ActiveMQ broker is already running or a port is already in use by another process");
            }
            else {
                logger.error(e.getMessage(), e);
            }
        }
    }

}
