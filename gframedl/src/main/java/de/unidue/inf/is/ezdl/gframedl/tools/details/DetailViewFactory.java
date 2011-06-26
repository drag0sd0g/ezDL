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

package de.unidue.inf.is.ezdl.gframedl.tools.details;

import java.awt.Component;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import de.unidue.inf.is.ezdl.dlcore.data.dldata.DLObject;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.Person;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.Term;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.TextDocument;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.library.EditDocument;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.library.Group;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.library.ReferenceSystem;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.library.ReferenceSystemMessage;
import de.unidue.inf.is.ezdl.gframedl.query.HistoricQuery;
import de.unidue.inf.is.ezdl.gframedl.tools.details.views.AuthorDetailView;
import de.unidue.inf.is.ezdl.gframedl.tools.details.views.DefaultDetailView;
import de.unidue.inf.is.ezdl.gframedl.tools.details.views.HistoricQueryDetailView;
import de.unidue.inf.is.ezdl.gframedl.tools.details.views.TermDetailView;
import de.unidue.inf.is.ezdl.gframedl.tools.details.views.TextDocumentDetailView;
import de.unidue.inf.is.ezdl.gframedl.tools.details.views.library.EditDocumentDetailView;
import de.unidue.inf.is.ezdl.gframedl.tools.details.views.library.GroupDetailView;
import de.unidue.inf.is.ezdl.gframedl.tools.details.views.library.ReferenceSystemMessageView;
import de.unidue.inf.is.ezdl.gframedl.tools.details.views.library.ReferenceSystemView;



/**
 * A central list of classes, a DetailView is defined for
 */
public class DetailViewFactory {

    private static Logger logger = Logger.getLogger(DetailViewFactory.class);

    private static Map<Class<?>, Class<?>> viewComponents = new HashMap<Class<?>, Class<?>>();

    /**
     * You can add DetailView's to this list, or register them at runtime.
     */
    static {
        addViewComponent(TextDocument.class, TextDocumentDetailView.class);
        addViewComponent(Person.class, AuthorDetailView.class);
        addViewComponent(Term.class, TermDetailView.class);
        addViewComponent(HistoricQuery.class, HistoricQueryDetailView.class);
        addViewComponent(Group.class, GroupDetailView.class);
        addViewComponent(EditDocument.class, EditDocumentDetailView.class);
        addViewComponent(ReferenceSystem.class, ReferenceSystemView.class);
        addViewComponent(ReferenceSystemMessage.class, ReferenceSystemMessageView.class);
    }


    /**
     * All DetailView's for specific classes have to register themselves.
     * 
     * @param contentClass
     *            The class displayed by the view
     * @param viewClass
     *            the DetailView class which is responsible for display
     * @return boolean - fails if the conditions for viewClass (see DetailView)
     *         are not met.
     */
    public static boolean addViewComponent(Class<?> contentClass, Class<?> viewClass) {
        boolean result = false;
        if ((contentClass != null) && (viewClass != null)) {
            if (isDerivedFrom(viewClass, Component.class) && isInterImplemented(viewClass, DetailView.class)) {
                viewComponents.put(contentClass, viewClass);
                result = true;
            }
        }
        return result;
    }


    /**
     * Returns the DetailView for the object. If there is no specific DetailView
     * for the object-class defined, a DefaultDetailView instance is returned.
     */
    public static DetailViewContainer getDetailViewFor(DLObject o, List<String> highlightStrings) {
        Component dv;
        DetailViewContainer result;
        if (viewComponents.containsKey(o.getClass())) {
            try {
                dv = (Component) viewComponents.get(o.getClass()).newInstance();
            }
            catch (Exception e) {
                logger.error(e.getMessage(), e);
                dv = new DefaultDetailView();
            }
        }
        else {
            dv = new DefaultDetailView();
        }
        result = new DetailViewContainer(dv);
        ((DetailView) dv).setObject(o, highlightStrings);
        return result;
    }


    private static boolean isInterImplemented(Class<?> clazz, Class<?> inter) {
        boolean result = false;
        if ((clazz != null) && (inter != null)) {
            for (Class<?> i : clazz.getInterfaces()) {
                result = result || i.equals(inter);
            }
            if ((!result) && (clazz.getSuperclass() != null)) {
                result = result || isInterImplemented(clazz.getSuperclass(), inter);
            }
        }
        return result;
    }


    private static boolean isDerivedFrom(Class<?> clazz, Class<?> ancestor) {
        boolean result = false;
        if ((clazz != null) && (ancestor != null)) {
            result = clazz.equals(ancestor);
            if ((!result) && (clazz.getSuperclass() != null)) {
                result = result || isDerivedFrom(clazz.getSuperclass(), ancestor);
            }
        }
        return result;
    }
}
