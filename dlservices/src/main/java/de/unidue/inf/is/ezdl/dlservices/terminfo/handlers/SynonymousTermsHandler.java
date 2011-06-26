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

package de.unidue.inf.is.ezdl.dlservices.terminfo.handlers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.unidue.inf.is.ezdl.dlbackend.agent.StartedBy;
import de.unidue.inf.is.ezdl.dlbackend.agent.handlers.AbstractRequestHandler;
import de.unidue.inf.is.ezdl.dlbackend.message.Message;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.Term;
import de.unidue.inf.is.ezdl.dlcore.message.content.MessageContent;
import de.unidue.inf.is.ezdl.dlcore.message.content.SynonymousTermsAsk;
import de.unidue.inf.is.ezdl.dlcore.message.content.SynonymousTermsTell;
import de.unidue.inf.is.ezdl.dlservices.terminfo.services.TermInfo;
import de.unidue.inf.is.ezdl.dlservices.terminfo.services.TermInfoService;
import de.unidue.inf.is.ezdl.dlservices.terminfo.services.TermInfos;
import de.unidue.inf.is.ezdl.dlservices.terminfo.services.dummy.DummySynonymousTermsService;



@StartedBy(SynonymousTermsAsk.class)
public class SynonymousTermsHandler extends AbstractRequestHandler {

    private TermInfoService relatedTermsService = new DummySynonymousTermsService();


    @Override
    protected boolean work(Message message) {
        boolean handled = true;
        MessageContent content = message.getContent();

        if (content instanceof SynonymousTermsAsk) {
            handleSynonymousTermsAsk(message, (SynonymousTermsAsk) content);
        }
        else {
            handled = false;
        }

        return handled;
    }


    private void handleSynonymousTermsAsk(Message message, SynonymousTermsAsk content) {
        TermInfos termInfos = relatedTermsService.getTermInfos(Arrays.asList(content.getTerm()));
        List<TermInfo> l = termInfos.getTermInfos();
        List<Term> terms = new ArrayList<Term>();
        for (TermInfo termInfo : l) {
            terms.add(termInfo.getTerm());
        }
        send(message.tell(new SynonymousTermsTell(terms)));
    }

}
