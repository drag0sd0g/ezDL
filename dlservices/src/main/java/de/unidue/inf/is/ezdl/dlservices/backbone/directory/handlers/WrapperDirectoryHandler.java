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

package de.unidue.inf.is.ezdl.dlservices.backbone.directory.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.unidue.inf.is.ezdl.dlbackend.agent.Reusable;
import de.unidue.inf.is.ezdl.dlbackend.agent.StartedBy;
import de.unidue.inf.is.ezdl.dlbackend.agent.handlers.AbstractRequestHandler;
import de.unidue.inf.is.ezdl.dlbackend.data.agent.WrapperRecord;
import de.unidue.inf.is.ezdl.dlbackend.message.Message;
import de.unidue.inf.is.ezdl.dlcore.data.wrappers.FrontendWrapperInfo;
import de.unidue.inf.is.ezdl.dlcore.message.content.AvailableWrappersAsk;
import de.unidue.inf.is.ezdl.dlcore.message.content.AvailableWrappersTell;
import de.unidue.inf.is.ezdl.dlcore.message.content.MessageContent;
import de.unidue.inf.is.ezdl.dlservices.backbone.directory.Directory;
import de.unidue.inf.is.ezdl.dlservices.backbone.directory.cache.AgentCache;



/**
 * Processes an {@link AvailableWrappersAsk} message by answering with
 * information about the avaiable wrappers.
 * 
 * @author mjordan
 */
@Reusable
@StartedBy(AvailableWrappersAsk.class)
public class WrapperDirectoryHandler extends AbstractRequestHandler {

    @Override
    protected boolean work(Message message) {
        boolean handled = true;
        MessageContent content = message.getContent();

        if (content instanceof AvailableWrappersAsk) {
            AvailableWrappersAsk register = (AvailableWrappersAsk) content;
            process(message, register);
        }
        else {
            handled = false;
        }

        return handled;
    }


    private void process(Message message, AvailableWrappersAsk awa) {
        Directory directory = (Directory) getAgent();
        AgentCache agentCache = directory.getAgentCache();

        List<WrapperRecord> wrappers = agentCache.getWrappers();
        List<FrontendWrapperInfo> wrapperInfos = new ArrayList<FrontendWrapperInfo>();
        for (WrapperRecord wrapperRecord : wrappers) {
            FrontendWrapperInfo frontendWrapperInfo = new FrontendWrapperInfo();
            frontendWrapperInfo.setId(wrapperRecord.getInfo().getId());
            frontendWrapperInfo.setRemoteName(wrapperRecord.getInfo().getRemoteName());
            frontendWrapperInfo.setSmallIconData(wrapperRecord.getInfo().getSmallIconData());
            frontendWrapperInfo.setLargeIconData(wrapperRecord.getInfo().getLargeIconData());
            frontendWrapperInfo.setCategoryId(wrapperRecord.getInfo().getCategoryId());
            frontendWrapperInfo.setProposedTimeoutSec(wrapperRecord.getInfo().getProposedMinimumTimeoutSec());
            String category = wrapperRecord.getInfo().getCategory().get(awa.getLocale());
            if (category == null) {
                category = wrapperRecord.getInfo().getCategory().get(Locale.ENGLISH);
            }
            frontendWrapperInfo.setCategory(category);
            String description = wrapperRecord.getInfo().getDescription().get(awa.getLocale());
            if (description == null) {
                description = wrapperRecord.getInfo().getCategory().get(Locale.ENGLISH);
            }
            frontendWrapperInfo.setDescription(description);
            wrapperInfos.add(frontendWrapperInfo);
        }
        AvailableWrappersTell answer = new AvailableWrappersTell(wrapperInfos);
        directory.send(message.tell(answer));
    }

}
