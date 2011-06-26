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

package de.unidue.inf.is.ezdl.gframedl.helper;

import java.util.concurrent.TimeUnit;

import de.unidue.inf.is.ezdl.dlcore.data.dldata.DLObject;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.Document;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.Person;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.PersonList;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.Term;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.TermWithWeight;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.TermWithWeightList;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.library.Group;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.library.GroupList;
import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;
import de.unidue.inf.is.ezdl.dlfrontend.helper.FieldRegistry;
import de.unidue.inf.is.ezdl.dlfrontend.query.QueryFactory;
import de.unidue.inf.is.ezdl.dlfrontend.query.parsers.simple.WebLikeFactory;
import de.unidue.inf.is.ezdl.gframedl.query.HistoricQuery;



/**
 * renders various (data)objects as short strings for use in a JList or similar
 * components
 * 
 * @author kriewel
 */
public final class ListItemStringRenderer {

    public static final boolean AUTHOR_LONG = false;
    public static final boolean AUTHOR_SHORT = true;
    private static QueryFactory queryFactory = new WebLikeFactory(FieldRegistry.getInstance(), Field.TEXT);


    private ListItemStringRenderer() {
    }


    public static String render(HistoricQuery q) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        sb.append(queryFactory.getTextForQueryNode(q.getQuery().getTree()));
        sb.append("<br/>");
        sb.append(q.getWrappers());
        sb.append(" (");
        sb.append(q.getSearchResultsCount());
        sb.append(") ");
        sb.append(milliseconds2String(q.getTimespan()));
        sb.append("</html>");
        return sb.toString();
    }


    public static String render(Person a) {
        return render(a, AUTHOR_LONG);
    }


    public static String render(Person a, boolean shortform) {
        StringBuilder sb = new StringBuilder();
        if (shortform) {
            sb.append(a.getInitials());
            sb.append(". ");
        }
        else {
            sb.append(a.getFirstName());
            sb.append(" ");
        }
        sb.append(a.getLastName());
        return sb.toString();
    }


    public static String render(Document d) {
        StringBuilder sb = new StringBuilder();
        Document m = d;
        PersonList al = m.getAuthorList();
        if (!(al == null || al.isEmpty())) {
            if (al.size() > 2) {
                sb.append(render(al.get(0), AUTHOR_SHORT));
                sb.append(" et. al.");
                sb.append(" ");
            }
            else {
                sb.append(render(al.get(0), AUTHOR_SHORT));
                if (al.size() == 2) {
                    sb.append(", ");
                    sb.append(render(al.get(1), AUTHOR_SHORT));
                }
                sb.append(" ");
            }
        }
        if (m.getYear() > 0) {
            sb.append("(");
            sb.append(m.getYear());
            sb.append(").");
        }
        sb.append("\"");
        sb.append(m.getTitle());
        sb.append("\"");
        return sb.toString();
    }


    public static String render(Document d, boolean multiline) {
        if (!multiline) {
            return render(d);
        }
        else {
            StringBuilder sb = new StringBuilder();
            Document m = d;

            sb.append("<html><body>");
            sb.append("<strong>");
            sb.append(m.getTitle());
            sb.append("</strong>");
            sb.append("<br>");

            PersonList al = m.getAuthorList();
            if (!(al == null || al.isEmpty())) {
                if (al.size() > 2) {
                    sb.append(render(al.get(0), AUTHOR_SHORT));
                    sb.append(" et. al.");
                    sb.append(" ");
                }
                else {
                    sb.append(render(al.get(0), AUTHOR_SHORT));
                    if (al.size() == 2) {
                        sb.append(", ");
                        sb.append(render(al.get(1), AUTHOR_SHORT));
                    }
                    sb.append(" ");
                }
            }
            if (m.getYear() > 0) {
                sb.append("(");
                sb.append(m.getYear());
                sb.append(").");
            }

            // Tags
            boolean br = false;
            TermWithWeightList tagList = (TermWithWeightList) m.getFieldValue(Field.TAGS);
            if (tagList != null) {

                for (TermWithWeight t : tagList) {
                    if (!br) {
                        sb.append("<br>");
                        br = true;
                    }
                    sb.append("<font size=-2 COLOR='#0000FF'><U>");
                    sb.append(t.getTerm());
                    sb.append("</U></font>&nbsp;&nbsp");
                }
            }

            // Groups
            GroupList groupList = (GroupList) m.getFieldValue(Field.GROUPS);

            if (groupList != null) {
                for (Group g : groupList) {
                    if (!br) {
                        sb.append("<br>");
                        br = true;
                    }
                    sb.append("<font size=-2 COLOR='#760118'>");
                    sb.append(g.getName());
                    sb.append("</font>&nbsp;&nbsp");
                }
            }

            sb.append("</body></html>");
            return sb.toString();
        }
    }


    public static String render(DLObject d) {
        if (d instanceof Term) {
            return ((Term) d).getTerm();
        }
        else if (d instanceof Person) {
            return render((Person) d, AUTHOR_LONG);
        }
        else if (d instanceof HistoricQuery) {
            return render((HistoricQuery) d);
        }
        else if (d instanceof Document) {
            return render((Document) d);
        }
        return d.toString();
    }


    private static String milliseconds2String(long millis) {
        long days = TimeUnit.MILLISECONDS.toDays(millis);
        millis = millis - TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis = millis - TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        StringBuilder sb = new StringBuilder();
        if (days != 0) {
            sb.append(days);
            sb.append(" d ");
        }
        if (hours != 0 || days != 0) {
            sb.append(hours);
            sb.append(" h ");
        }
        sb.append(minutes);
        sb.append(" min");
        return sb.toString();
    }

}
