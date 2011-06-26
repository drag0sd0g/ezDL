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

package de.unidue.inf.is.ezdl.dlbackend.misc;

import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;



/**
 * A simple email sender class.
 */
public final class SimpleMailSender {

    private static Logger logger = Logger.getLogger(SimpleMailSender.class);


    private SimpleMailSender() {
    }


    /**
     * Sends an e-mail message.
     * 
     * @param smtpServer
     *            the smtp server
     * @param to
     *            to address
     * @param from
     *            from address
     * @param subject
     *            the subject of the mail
     * @param body
     *            the body of the mail
     */
    public static void send(String smtpServer, String to, String from, String subject, String body) {
        try {
            Properties props = System.getProperties();

            props.put("mail.smtp.host", smtpServer);
            Session session = Session.getDefaultInstance(props, null);

            Message msg = new MimeMessage(session);

            msg.setFrom(new InternetAddress(from));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));

            msg.setSubject(subject);
            msg.setText(body);

            msg.setHeader("X-Mailer", "ezDL");
            msg.setSentDate(new Date());

            Transport.send(msg);
        }
        catch (MessagingException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }
}
