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

import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import de.unidue.inf.is.ezdl.dlbackend.AbstractBackendTestBase;
import de.unidue.inf.is.ezdl.dlcore.message.MTAMessage;
import de.unidue.inf.is.ezdl.dlcore.message.content.LoginTell;
import de.unidue.inf.is.ezdl.dlcore.message.content.TextMessageNotify;
import de.unidue.inf.is.ezdl.dlcore.message.content.TextMessageNotify.Priority;
import de.unidue.inf.is.ezdl.dlcore.security.Privilege;



/**
 * Tests the {@link TextMessageManager}.
 * 
 * @author mjordan
 */
public class TextMessageManagerTest extends AbstractBackendTestBase {

    private MockGatedMTA mta;
    private MockTextMessageManager manager;


    @Before
    public void setup() {
        mta = new MockGatedMTA();
        Properties props = new Properties();
        props.put(TextMessageManager.FILE_NAME_KEY, "foo");
        mta.init("mta", props);
        manager = new MockTextMessageManager(mta);
    }


    /**
     * A visitor logs on and the MOTD has a newer timestamp (10l) than the last
     * log time of the user (1l). Since this is a visitor, an MOTD should be
     * sent.
     */
    @Test
    public void test_MotdNew_Visitor() {
        manager.setFileDate(10l);
        checkMotd("1", "motd", true, 1l);
    }


    /**
     * A visitor logs on and the MOTD has an older timestamp (10l) than the last
     * log time of the user (20l). Since this is a visitor, an MOTD should be
     * sent anyway.
     */
    @Test
    public void test_MotdOld_Visitor() {
        manager.setFileDate(10l);
        checkMotd("1", "motd", true, 20l);
    }


    /**
     * A non-visitor logs on and the MOTD has a newer timestamp (10l) than the
     * last log time of the user (1l). Since this is a non-visitor, an MOTD
     * should be sent.
     */
    @Test
    public void test_MotdNew_NonVisitor() {
        manager.setFileDate(10l);
        checkMotd("1", "motd", true, 1l, Privilege.STORE_PERSONAL_DATA);
    }


    /**
     * A non-visitor logs on and the MOTD has an older timestamp (10l) than the
     * last log time of the user (20l). Since this is a non-visitor and the user
     * has probably seen the MOTD, an MOTD should not be sent.
     */
    @Test
    public void test_MotdOld_NonVisitor() {
        manager.setFileDate(10l);
        checkMotd("1", "motd", false, 20l, Privilege.STORE_PERSONAL_DATA);
    }


    private LoginTell getTell(long lastLog, Privilege... pp) {
        final Set<Privilege> privs = new TreeSet<Privilege>();
        for (Privilege p : pp) {
            privs.add(p);
        }
        final LoginTell tell = new LoginTell("user", "firstname", "lastname", "sessionid", privs);
        tell.setLastLoginTime(lastLog);
        return tell;
    }


    private void checkMotd(String connection, String motdStr, boolean msgExpected, long lastLog, Privilege... pp) {
        mta.clear();
        manager.setMotd(motdStr);
        manager.handleMotdMessage(connection, getTell(lastLog, pp));

        if (msgExpected) {
            Assert.assertEquals("1 motd sent to HTTP", 1, mta.getSentToClientConIds().size());
            Assert.assertTrue("motd string sent to spec. connection", mta.getSentToClientConIds().contains(connection));

            Assert.assertEquals("1 motd msg sent", 1, mta.getSentToClientMsgs().size());
            MTAMessage motdMsg = mta.getSentToClientMsgs().get(0);
            TextMessageNotify motd = (TextMessageNotify) motdMsg.getContent();
            Assert.assertEquals("motd is INFO", Priority.INFO, motd.getPriority());
            Assert.assertEquals("motd message okay", motdStr, motd.getContent());
        }
        else {
            Assert.assertEquals("no motd sent 1", 0, mta.getSentToClientConIds().size());
            Assert.assertEquals("no motd sent 1", 0, mta.getSentToClientMsgs().size());
        }
    }


    /**
     * Tests the processing of text messages as broadcast messages.
     */
    @Test
    public void testTextMessage() {
        checkTextMessage("ab", false, Priority.INFO, "title", "content", "from");
        checkTextMessage("from", true, Priority.INFO, "title", "content", "from");
        checkTextMessage("from", true, Priority.INFO, "title", "content", "from");
    }


    /**
     * Tests the processing of text messages as broadcast messages by
     * unauthorized senders.
     */
    @Test
    public void testTextMessageUnauthorized() {
        checkTextMessageUnauthorized(Priority.INFO, "title", "content", "from");
        checkTextMessageUnauthorized(Priority.WARN, "title", "content", "from");
        checkTextMessageUnauthorized(Priority.INFO, "title", "content", "from");
    }


    private void checkTextMessageUnauthorized(Priority prio, String title, String content, String from) {
        mta.clear();
        TextMessageNotify textIn = new TextMessageNotify(prio, title, content, from);
        manager.handleNewsMessage(textIn);

        Assert.assertEquals("no text sent to HTTP", 0, mta.getSentToClientConIds().size());
        Assert.assertEquals("no text msg sent", 0, mta.getSentToClientMsgs().size());
    }


    private void checkTextMessage(String allowedSender, boolean expected, Priority prio, String title, String content,
                    String from) {
        Properties p = mta.getProperties();
        if (allowedSender != null) {
            p.put(TextMessageManager.ALLOWED_SENDER_KEY, allowedSender);
        }
        manager = new MockTextMessageManager(mta);
        mta.clear();
        TextMessageNotify textIn = new TextMessageNotify(prio, title, content, from);
        manager.handleNewsMessage(textIn);

        if (expected) {

            Assert.assertEquals("text sent to HTTP", 1, mta.getSentTextToAllMsgs().size());

            MTAMessage motdMsg = mta.getSentTextToAllMsgs().get(0);
            TextMessageNotify motdOut = (TextMessageNotify) motdMsg.getContent();
            Assert.assertEquals("motd is INFO", Priority.INFO, motdOut.getPriority());
            Assert.assertEquals("motd message okay", content, motdOut.getContent());
        }
        else {
            Assert.assertEquals("no text sent to HTTP", 0, mta.getSentToClientConIds().size());
            Assert.assertEquals("no text msg sent", 0, mta.getSentToClientMsgs().size());
        }
    }
}
