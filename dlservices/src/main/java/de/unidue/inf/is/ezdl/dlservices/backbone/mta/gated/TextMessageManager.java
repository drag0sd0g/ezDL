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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Properties;

import org.apache.log4j.Logger;

import de.unidue.inf.is.ezdl.dlbackend.agent.RequestIDFactory;
import de.unidue.inf.is.ezdl.dlcore.message.MTAMessage;
import de.unidue.inf.is.ezdl.dlcore.message.content.LoginTell;
import de.unidue.inf.is.ezdl.dlcore.message.content.TextMessageNotify;
import de.unidue.inf.is.ezdl.dlcore.message.content.TextMessageNotify.Priority;
import de.unidue.inf.is.ezdl.dlcore.security.Privilege;
import de.unidue.inf.is.ezdl.dlcore.utils.IOUtils;
import de.unidue.inf.is.ezdl.dlcore.utils.StringUtils;



/**
 * Manages the details of MOTD sending.
 * 
 * @author mj
 */
public class TextMessageManager {

    private static final String MOTD_TITLE_DEFAULT = "Message of the day";
    private static final String SYSTEM_SENDER = "system";
    private static final String TMM_PREFIX = "msg.";

    public static final String FILE_NAME_KEY = TMM_PREFIX + "motd.file";
    public static final String MOTD_TITLE_KEY = TMM_PREFIX + "motd.title";
    public static final String ALLOWED_SENDER_KEY = TMM_PREFIX + "allowedsender";

    /**
     * The logger.
     */
    private final Logger logger = Logger.getLogger(getClass());

    /**
     * Reference to the MTA.
     */
    private AbstractGatedMTA agent;
    /**
     * The name of the sender who is allowed to send broadcast messages.
     */
    private String allowedSender;
    /**
     * The title of MOTD messages.
     */
    private String motdTitle;


    /**
     * Initialize the TextMessageManager and read the MOTD file.
     * 
     * @param mta
     *            reference to the MTA to work for
     */
    public TextMessageManager(AbstractGatedMTA mta) {
        this.agent = mta;
        final Properties props = agent.getProperties();
        allowedSender = props.getProperty(ALLOWED_SENDER_KEY);
        motdTitle = props.getProperty(MOTD_TITLE_KEY, MOTD_TITLE_DEFAULT);
    }


    private String getMotd(LoginTell tell) {
        final Properties props = agent.getProperties();
        String fileName = props.getProperty(FILE_NAME_KEY);
        String motd = null;

        if (!StringUtils.isEmpty(fileName)) {
            File file = new File(fileName);
            final long fileDate = fileDate(file);
            final long lastLoginDate = tell.getLastLoginTime();
            final boolean isRealUser = tell.getPrivileges().contains(Privilege.STORE_PERSONAL_DATA);
            if (!isRealUser || (fileDate > lastLoginDate)) {
                motd = readMotdFromFile(file);
            }
        }
        logger.info("MOTD is " + motd);
        return motd;
    }


    long fileDate(File file) {
        return file.lastModified();
    }


    String readMotdFromFile(File file) {
        String motd = null;
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            motd = IOUtils.readBufferAsString(br);
        }
        catch (FileNotFoundException e) {
            logger.info("Could not find " + file.getAbsolutePath() + " - not sendind MOTD.");
        }
        return motd;
    }


    /**
     * Sends an MOTD message to the given connection if an MOTD is defined.
     * 
     * @param connectionId
     *            the ID of the connection to send the MOTD to.
     * @param tell
     *            the login message that contains the data of the user who just
     *            logged in
     */
    public void handleMotdMessage(String connectionId, LoginTell tell) {
        MTAMessage motd = getMotdMessage(tell);
        if (motd != null) {
            agent.sendToClient(connectionId, motd);
        }
    }


    /**
     * Returns an MTAMessage that contains the current message of the day.
     * 
     * @param tell
     *            the login message that contains the data of the user who just
     *            logged in
     * @return the MOTD message or null, if the MOTD is not set.
     */
    MTAMessage getMotdMessage(LoginTell tell) {
        MTAMessage msg = null;
        String motd = getMotd(tell);
        if (!StringUtils.isEmpty(motd)) {
            TextMessageNotify notify = new TextMessageNotify(Priority.INFO, motdTitle, motd, "");
            String rid = RequestIDFactory.getInstance().getNextRequestID();
            msg = new MTAMessage(notify, rid);
        }
        return msg;
    }


    /**
     * Sends a message notification to all connected clients.
     * <p>
     * The sender name is replaced by {@link #SYSTEM_SENDER}.
     * 
     * @param text
     *            the notification to send
     */
    public void handleNewsMessage(TextMessageNotify text) {
        if (!StringUtils.isEmpty(allowedSender) && text.getFrom().equals(allowedSender)) {
            String rid = RequestIDFactory.getInstance().getNextRequestID();
            TextMessageNotify out = new TextMessageNotify(text.getPriority(), text.getTitle(), text.getContent(),
                            SYSTEM_SENDER);
            MTAMessage mtaMessage = new MTAMessage(out, rid);
            logger.warn("Sending text message " + text.getContent() + " to all clients");
            agent.sendTextToAll(mtaMessage);
            logger.warn("Sending text message finished.");
        }
        else {
            logger.warn("Got text message " + text.getContent() + " from unauthorized sender \"" + text.getFrom()
                            + "\"");
        }
    }
}
