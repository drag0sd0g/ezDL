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

package de.unidue.inf.is.ezdl.gframedl.debug;

import java.lang.ref.WeakReference;

import javax.swing.JComponent;
import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;



/**
 * <p>
 * This class is used to detect Event Dispatch Thread rule violations<br>
 * See <a
 * href="http://java.sun.com/docs/books/tutorial/uiswing/misc/threads.html">How
 * to Use Threads</a> for more info
 * </p>
 * <p/>
 * <p>
 * This is a modification of original idea of Scott Delap<br>
 * Initial version of ThreadCheckingRepaintManager can be found here<br>
 * <a href="http://www.clientjava.com/blog/2004/08/20/1093059428000.html">Easily
 * Find Swing Threading Mistakes</a>
 * </p>
 * 
 * @author Scott Delap
 * @author Alexander Potochkin https://swinghelper.dev.java.net/
 */
public class CheckThreadViolationRepaintManager extends RepaintManager {

    private static Logger logger = Logger.getLogger("debug");

    /** The last component. */
    private WeakReference<JComponent> lastComponent;


    /**
     * Instantiates a new check thread violation repaint manager.
     */
    public CheckThreadViolationRepaintManager() {
        super();
    }


    @Override
    public void addDirtyRegion(JComponent component, int x, int y, int w, int h) {
        checkThreadViolations(component);
        super.addDirtyRegion(component, x, y, w, h);
    }


    @Override
    public synchronized void addInvalidComponent(JComponent component) {
        checkThreadViolations(component);
        super.addInvalidComponent(component);
    }


    private void checkThreadViolations(JComponent c) {
        if (!SwingUtilities.isEventDispatchThread()) {
            boolean repaint = false;
            boolean fromSwing = false;
            boolean imageUpdate = false;
            boolean fromJung = false;
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            for (StackTraceElement st : stackTrace) {
                if (st.getClassName().startsWith("edu.uci.ics.jung.")) {
                    fromJung = true;
                }
                if (repaint && st.getClassName().startsWith("javax.swing.")) {
                    fromSwing = true;
                }
                if (repaint && "imageUpdate".equals(st.getMethodName())) {
                    imageUpdate = true;
                }
                if ("repaint".equals(st.getMethodName())) {
                    repaint = true;
                    fromSwing = false;
                }
            }
            if (fromJung && repaint) {
                // ignore exception repaint caused by Jung graph layout.
                return;
            }
            if (imageUpdate) {
                // assuming it is java.awt.image.ImageObserver.imageUpdate(...)
                // image was asynchronously updated, that's ok
                return;
            }
            if (repaint && !fromSwing) {
                // no problems here, since repaint() is thread safe
                return;
            }
            // ignore the last processed component
            if (lastComponent != null && c == lastComponent.get()) {
                return;
            }

            lastComponent = new WeakReference<JComponent>(c);
            logger.error("");
            logger.error("EDT violation detected");
            logger.error("");
            logger.error(c.toString());
            for (StackTraceElement st : stackTrace) {
                logger.error("\tat " + st);
            }
            throw new UiThreadingViolationError();
        }
    }
}
