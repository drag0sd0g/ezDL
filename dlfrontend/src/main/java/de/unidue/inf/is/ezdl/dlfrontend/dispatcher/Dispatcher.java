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

package de.unidue.inf.is.ezdl.dlfrontend.dispatcher;

import java.util.Collections;
import java.util.EventObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;



/**
 * The Dispatcher is meant to be the single Point of distribution for
 * application events throughout a single (maybe multithreaded) Application. It
 * can dispatch selfd-efined events (derived from java.util.Event) either to
 * specific Objects or to any number of interested Objects that have previously
 * subscribed to certain topics of interest. These Interests can either be
 * defined in terms of event classes (analogous to the News-Subscriber pattern)
 * or by defining instances or classes of EventSources.
 */
public final class Dispatcher {

    /**
     * Initialization on demand holder pattern. The only safe way to create a
     * singleton in Java.
     */
    private static class Holder {

        private static final Dispatcher INSTANCE = new Dispatcher();
    }


    /**
     * Problems can occur, when swing components are modified in another than
     * the swing thread - and since posted events were handled in the
     * dispatchers own thread, these were bound to appear sooner or later.
     * therfore, the default for dispatching posted events is now to use the
     * swing 'invokeLater' feature, to make sure, they are executed within the
     * swing thread context. Set 'dispatchPostedInSwingThread' to false, to let
     * the Dispatchers own thread work them
     */
    public static boolean dispatchPostedInSwingThread = true;

    /**
     * set this prior to any access to the Dispatcher to alter the queue size
     */
    public static int initialQueueSize = 100;

    // needs to be static so "sendEvent" or "threadEvent" can access it
    private static Map<EventReceiver, RecipientInfo> listeners = Collections
                    .synchronizedMap(new HashMap<EventReceiver, RecipientInfo>());

    /**
     * Enable logging output on how and when events are actually delivered to
     * the recipients.
     */
    public static boolean logEventDispatch = false;

    private static Logger logger = Logger.getLogger(Dispatcher.class);

    /**
     * Enable logging output of the various ways, events are delivered to the
     * dispatcher
     */
    public static boolean logIncomingEvents = false;

    /** Enable the logging output of the various "registerInterest" methods */
    public static boolean logRegistration = false;
    public static boolean logWithThreadInfo = false;

    private static ExecutorService executorService = Executors.newCachedThreadPool();


    /** constructor: only called from within "getSingelton()" (usually) */
    private Dispatcher() {
        super();
    }


    private EventReceiver findChainParent0(EventReceiver view) {
        RecipientInfo ri = listeners.get(view);
        if (ri != null) {
            RecipientInfo riPar = ri.getChainParent();
            if (riPar != null) {
                return riPar.getRecipient();
            }
        }
        return null;
    }


    private synchronized void postEvent0(EventReceiver target, EventObject ev, boolean inSwingThread) {
        if (logIncomingEvents) {
            log("EVI", "enqueue event " + ev + " for " + target + (inSwingThread ? " into SWING" : ""));
        }

        if (inSwingThread) {
            javax.swing.SwingUtilities.invokeLater(new EventRunner(target, ev));
        }
        else {
            executorService.submit(new EventRunner(target, ev));
        }
    }


    private RecipientInfo getRecipientInfo(EventReceiver view) {
        RecipientInfo rpi = listeners.get(view);
        if (rpi == null) {
            synchronized (listeners) {
                rpi = listeners.get(view);
                if (rpi == null) {
                    rpi = new RecipientInfo(view);
                    listeners.put(view, rpi);
                    if (logRegistration) {
                        log("EVR", "new listener " + view);
                    }
                }
            }
        }
        return rpi;
    }


    private void registerEventClassInterest0(EventReceiver view, Class<? extends EventObject> cls) {
        RecipientInfo rpi = getRecipientInfo(view);
        rpi.addEventInterest(cls);
        if (logRegistration) {
            log("EVR", view + " is interested in EventClass " + cls.getName());
        }
    }


    private synchronized void unregister0(EventReceiver view) {
        RecipientInfo ri = listeners.get(view);
        if (ri != null) {
            // unregister with our own parent - if there is one
            RecipientInfo rPar = ri.getChainParent();
            if (rPar != null) {
                if (logRegistration) {
                    log("EVR", " unreg " + view + " from chain Parent " + rPar.getRecipient());
                }
                rPar.removeChainChild(ri);
            }
            // unregister all the eventChidren from the Dispatcher recursively
            RecipientInfo[] children = ri.getChainChildren();
            // need a fixed copy - children will unregister in iteration
            if (children != null) {
                for (RecipientInfo element : children) {
                    EventReceiver er = element.getRecipient();
                    if (logRegistration) {
                        log("EVR", " unreg child " + er + " recursively from " + view);
                    }
                    unregister0(er);
                }
            }
        }
        if (logRegistration) {
            if (listeners.remove(view) == null) {
                log("EVR", " unreg " + view + ": was not registered!!!");
            }
            else {
                log("EVR", " unregistered " + view);
            }
        }
        else {
            listeners.remove(view);
        }
    }


    private synchronized void unregisterEventInterest0(EventReceiver view, Class<? extends EventObject> ev) {
        RecipientInfo ri = listeners.get(view);
        if (ri != null) {
            ri.removeEventInterest(ev);
        }
    }


    /**
     * find the Parent of this view in a "Chain of Responsibilities".
     * <p>
     * returns <code>null</code> if no parent is declared.
     */
    private static EventReceiver findChainParent(EventReceiver view) {
        return getSingleton().findChainParent0(view);
    }


    /**
     * Return (maybe instantiate first) a reference to the one and only instance
     * of the Dispatcher.
     * 
     * @return The only instance of the Dispatcher that exists
     */
    private static Dispatcher getSingleton() {
        return Holder.INSTANCE;
    }


    static synchronized void log(String type, String msg) {
        if (logWithThreadInfo) {
            logger.info(type + ": " + Thread.currentThread().toString() + " \t" + msg);
        }
        else {
            logger.info(type + ": " + msg);
        }
    }


    /**
     * put the posted Event into the main EventQueue and dispatch them in
     * FirstComeFirstServed Order to the all <em>interested</em> recipients.
     * EventDispatching happens in its own "EventThread" if the variable
     * 'dispatchPostedInSwingThread' is false, otherwise, they are delegated to
     * the Swing-Thread
     */
    public static void postEvent(EventObject ev) {
        getSingleton().postEvent0(null, ev, dispatchPostedInSwingThread);
    }


    /**
     * Put an event to a specific listener into the main EventQueue Dispatched
     * in FirstComeFirstServed Order to the designated recipient.
     * EventDispatching happens in its own "EventThread". if the variable
     * 'dispatchPostedInSwingThread' is false, otherwise, they are delegated to
     * the Swing-Thread
     */
    public static void postEvent(EventReceiver view, EventObject ev) {
        getSingleton().postEvent0(null, ev, dispatchPostedInSwingThread);
    }


    /**
     * The "RegisterInterest" Methods may be used by any "EventReciever" to ask
     * to be sent Events of the specified envent-classes (or subclasses) without
     * regard to the source of the individual Events
     */
    public static void registerInterest(EventReceiver view, Class<? extends EventObject> cls) {
        getSingleton().registerEventClassInterest0(view, cls);
    }


    /**
     * Dispatches the event immediately to all interested parties. it does not
     * return until all recipients have executed their "handleEzEvent" methods
     * sequentially. Send is executed within the calling Thread.
     */
    static void sendEvent(EventObject ev) {
        // unknown recipient - find out, who is interested
        boolean foundInterested = false;
        final HashSet<Entry<EventReceiver, RecipientInfo>> entries = new HashSet<Entry<EventReceiver, RecipientInfo>>(
                        listeners.entrySet());
        for (Entry<EventReceiver, RecipientInfo> entry : entries) {
            final RecipientInfo ri = entry.getValue();
            try {
                if ((ri != null) && ri.isInterested(ev)) {
                    sendEvent(ri.getRecipient(), ev);
                    foundInterested = true;
                }
            }
            catch (Exception e) {
                logger.error("Exception while dispatching", e);
            }
        }
        if (logEventDispatch && !foundInterested) {
            log("EVD", "no one interested in " + ev);
        }
    }


    /**
     * send an Event to one explicit listener (you could probably do this
     * yourself by calling his <tt>handleEvent</tt> method - main advantage is
     * the centralized logging and debugging).
     */
    static void sendEvent(EventReceiver view, EventObject ev) {
        boolean handeled = false;
        try {
            while ((view != null) && (!handeled)) {
                if (ev instanceof SelfDispatchable) {
                    if (!((SelfDispatchable) ev).dispatchSelf(view)) {
                        view = findChainParent(view);
                    }
                    else {
                        handeled = true;
                    }
                }
                else {
                    if (!view.handleEzEvent(ev)) {
                        view = findChainParent(view);
                    }
                    else {
                        handeled = true;
                    }
                }
            }
        }
        catch (Exception e) {
            logger.error("Error: ", e);
            e.printStackTrace();
        }
        if (logEventDispatch && !handeled) {
            log("EVD", "Event " + ev.toString() + " not successfully dispatched");
        }
    }


    /**
     * Prior to destruction, every listener or view that has prevoiously
     * regestered his interests with the dispatcher, is supposed to
     * <tt>unregister</tt> so we wont try to deliver any more Events this way.
     */
    public static void unregister(EventReceiver view) {
        getSingleton().unregister0(view);
    }


    public static void unregisterEventInterest(EventReceiver view, Class<? extends EventObject> evCl) {
        getSingleton().unregisterEventInterest0(view, evCl);
    }

}
