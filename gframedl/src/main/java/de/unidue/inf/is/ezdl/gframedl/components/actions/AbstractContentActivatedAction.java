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

package de.unidue.inf.is.ezdl.gframedl.components.actions;

import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;



/**
 * This abstract action is activated if the content of the client changes (e.g.
 * if items are dragged into a list).
 * 
 * @author mjordan
 */
public abstract class AbstractContentActivatedAction extends AbstractAction {

    private static final long serialVersionUID = -3409553110594297815L;

    private ContentGetter client;

    private boolean checkAll = false;


    /**
     * Creates a new Action for the given source.
     * 
     * @param client
     *            the object from which to retrieve objects to act on
     */
    public AbstractContentActivatedAction(ContentGetter client) {
        this.client = client;
        setEnabled(false);
        client.addListDataListener(new LDL());
    }


    /**
     * Returns the {@link SelectionGetter} instance that is linked to the
     * {@link Action}.
     * 
     * @return the {@link SelectionGetter} instance
     */
    protected ContentGetter getClient() {
        return client;
    }


    private class LDL implements ListDataListener {

        @Override
        public void contentsChanged(ListDataEvent e) {
            handle();
        }


        @Override
        public void intervalAdded(ListDataEvent e) {
            handle();
        }


        @Override
        public void intervalRemoved(ListDataEvent e) {
            handle();
        }


        private void handle() {
            List<?> selected = getClient().getContentObjects();
            boolean predicate = false;
            for (Object s : selected) {
                if (canHandle(s)) {
                    predicate = true;
                    if (!checkAll) {
                        break;
                    }
                }
                else {
                    predicate = false;
                    if (checkAll) {
                        break;
                    }
                }
            }
            setEnabled(predicate);
        }

    }


    /**
     * Sets the checkAll mode.
     * <p>
     * If checkAll is true, {@link AbstractActivatedAction#canHandle(Object)}
     * has to return true for all objects selected in the client to enable the
     * action. if checkAll is false,
     * {@link AbstractActivatedAction#canHandle(Object)} only has to return true
     * for one (any) object to enable the action.
     * 
     * @param checkAll
     */
    protected void setCheckAll(boolean checkAll) {
        this.checkAll = checkAll;
    }


    /**
     * Returns if the Action can handle the given object.
     * 
     * @param s
     *            the object to examine
     * @return true, if the Action can handle the object, else false. By default
     *         returns true for all objects.
     */
    protected boolean canHandle(Object s) {
        return true;
    }

}
