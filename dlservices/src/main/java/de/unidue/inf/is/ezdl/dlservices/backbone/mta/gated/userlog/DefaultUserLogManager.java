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

package de.unidue.inf.is.ezdl.dlservices.backbone.mta.gated.userlog;

import java.util.List;

import de.unidue.inf.is.ezdl.dlbackend.agent.Agent;
import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;
import de.unidue.inf.is.ezdl.dlcore.data.fields.Sorting;
import de.unidue.inf.is.ezdl.dlcore.data.query.DocumentQuery;
import de.unidue.inf.is.ezdl.dlcore.data.query.ResultConfiguration;
import de.unidue.inf.is.ezdl.dlcore.data.query.ResultDocument;
import de.unidue.inf.is.ezdl.dlcore.data.wrappers.FrontendWrapperInfo;
import de.unidue.inf.is.ezdl.dlcore.log.UserLogConstants;
import de.unidue.inf.is.ezdl.dlcore.message.content.AvailableWrappersAsk;
import de.unidue.inf.is.ezdl.dlcore.message.content.AvailableWrappersTell;
import de.unidue.inf.is.ezdl.dlcore.message.content.CancelSearchNotify;
import de.unidue.inf.is.ezdl.dlcore.message.content.DocumentDetailsAsk;
import de.unidue.inf.is.ezdl.dlcore.message.content.DocumentDetailsTell;
import de.unidue.inf.is.ezdl.dlcore.message.content.DocumentQueryAsk;
import de.unidue.inf.is.ezdl.dlcore.message.content.DocumentQueryResultTell;
import de.unidue.inf.is.ezdl.dlcore.message.content.MessageContent;
import de.unidue.inf.is.ezdl.dlcore.message.content.TextMessageNotify;
import de.unidue.inf.is.ezdl.dlcore.message.content.UserLogNotify;
import de.unidue.inf.is.ezdl.dlcore.query.SolrQueryConverter;



/**
 * Takes care of everything user-log related.
 * 
 * @author mjordan
 */
public class DefaultUserLogManager extends AbstractUserLogManager {

    private SolrQueryConverter queryConverter = new SolrQueryConverter();


    public DefaultUserLogManager(Agent agent, String agentNameUserLog) {
        super(agent, agentNameUserLog);
    }


    @Override
    protected UserLogNotify logEvent(String sessionId, MessageContent content) {
        UserLogNotify logNotify = null;
        if (content instanceof DocumentQueryAsk) {
            logNotify = log(sessionId, (DocumentQueryAsk) content);
        }
        else if (content instanceof DocumentQueryResultTell) {
            logNotify = log(sessionId, (DocumentQueryResultTell) content);
        }
        else if (content instanceof CancelSearchNotify) {
            logNotify = log(sessionId, (CancelSearchNotify) content);
        }
        else if (content instanceof AvailableWrappersAsk) {
            logNotify = log(sessionId, UserLogConstants.EVENT_NAME_AVAILABLE_WRAPPERS_ASK);
        }
        else if (content instanceof AvailableWrappersTell) {
            logNotify = log(sessionId, (AvailableWrappersTell) content);
        }
        else if (content instanceof DocumentDetailsAsk) {
            logNotify = log(sessionId, (DocumentDetailsAsk) content);
        }
        else if (content instanceof DocumentDetailsTell) {
            logNotify = log(sessionId, UserLogConstants.EVENT_NAME_DOCUMENT_DETAILS_TELL);
        }
        else if (content instanceof TextMessageNotify) {
            logNotify = log(sessionId, (TextMessageNotify) content);
        }
        return logNotify;
    }


    private UserLogNotify log(String sessionId, TextMessageNotify content) {
        UserLogNotify notify = new UserLogNotify(sessionId, UserLogConstants.EVENT_NAME_TEXT_MESSAGE);
        notify.addParameter("from", content.getFrom());
        notify.addParameter("title", content.getTitle());
        notify.addParameter("prio", content.getPriority().toString());
        notify.addParameter("content", content.getContent());
        return notify;
    }


    private UserLogNotify log(String sessionId, CancelSearchNotify content) {
        UserLogNotify notify = new UserLogNotify(sessionId, UserLogConstants.EVENT_NAME_CANCEL_SEARCH);
        notify.addParameter("queryid", content.getQueryID());
        return notify;
    }


    private UserLogNotify log(String sessionId, DocumentDetailsAsk content) {
        UserLogNotify notify = new UserLogNotify(sessionId, UserLogConstants.EVENT_NAME_DOCUMENT_DETAILS_ASK);
        List<String> oids = content.getOids();
        for (String oid : oids) {
            notify.addParameter("oid", oid);
        }
        return notify;
    }


    private UserLogNotify log(String sessionId, AvailableWrappersTell content) {
        UserLogNotify notify = new UserLogNotify(sessionId, UserLogConstants.EVENT_NAME_AVAILABLE_WRAPPERS_TELL);
        List<FrontendWrapperInfo> wrappers = content.getWrapperInfos();
        for (FrontendWrapperInfo w : wrappers) {
            notify.addParameter("wrapper", w.getId());
        }
        return notify;
    }


    private UserLogNotify log(String sessionId, DocumentQueryAsk content) {
        UserLogNotify notify = new UserLogNotify(sessionId, UserLogConstants.EVENT_NAME_DOCUMENT_QUERY_ASK);
        DocumentQuery query = content.getQuery();
        String queryStr = queryConverter.convert(query.getQuery());
        notify.addParameter("query", queryStr);

        List<String> dls = query.getDLList();
        if (dls != null) {
            for (String dl : dls) {
                notify.addParameter("dl", dl);
            }
        }

        List<String> attributes = query.getQuery().getAttributeValues();
        if (attributes != null) {
            for (String attribute : attributes) {
                notify.addParameter("attribute", attribute);
            }
        }

        ResultConfiguration resultConfig = content.getResultConfig();
        notify.addParameter("startdocnum", resultConfig.getStartDocNumber());
        notify.addParameter("enddocnum", resultConfig.getEndDocNumber());

        List<Field> fields = resultConfig.getFields();
        if (fields != null) {
            for (Field field : fields) {
                notify.addParameter("field", field.asInt());
            }
        }

        List<Sorting> sortings = resultConfig.getSortings();
        if (sortings != null) {
            for (Sorting sorting : sortings) {
                if (sorting != null) {
                    notify.addParameter("sorting", sorting.getField().asInt());
                    notify.addParameter("order", sorting.getOrder().getName());
                }
            }
        }

        notify.addParameter("maxduration", content.getMaxDurationMs());
        return notify;
    }


    private UserLogNotify log(String sessionId, DocumentQueryResultTell content) {
        UserLogNotify notify = new UserLogNotify(sessionId, UserLogConstants.EVENT_NAME_DOCUMENT_QUERY_TELL);
        notify.addParameter("totaldoccount", content.getTotalDocCount());
        for (ResultDocument doc : content.getResults()) {
            notify.addParameter("item", doc.getOid());
            notify.addParameter("rsv", Double.toString(doc.getRsv()));
        }
        return notify;
    }


    private UserLogNotify log(String sessionId, String eventName) {
        UserLogNotify notify = new UserLogNotify(sessionId, eventName);
        return notify;
    }

}
