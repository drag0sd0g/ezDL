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

import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;



class RecipientInfo {

    private List<RecipientInfo> chainChildren;
    private RecipientInfo chainParent;
    private List<Class<? extends EventObject>> likesEvents;
    private EventReceiver recipient;


    public RecipientInfo(EventReceiver rcp) {
        recipient = rcp;
        likesEvents = new ArrayList<Class<? extends EventObject>>();
        chainChildren = new ArrayList<RecipientInfo>();
    }


    public synchronized void addChainChild(RecipientInfo child) {
        chainChildren.add(child);
    }


    public void addEventInterest(Class<? extends EventObject> eClass) {
        likesEvents.add(eClass);
    }


    public synchronized RecipientInfo[] getChainChildren() {
        if (chainChildren == null) {
            return new RecipientInfo[0];
        }
        int count = 0;
        for (RecipientInfo element : chainChildren) {
            if (element != null) {
                count++;
            }
        }
        if (count == 0) {
            return null;
        }
        RecipientInfo[] result = new RecipientInfo[count];
        count = 0;
        for (RecipientInfo element : chainChildren) {
            if (element != null) {
                result[count++] = element;
            }
        }
        return result;
    }


    public RecipientInfo getChainParent() {
        return chainParent;
    }


    public EventReceiver getRecipient() {
        return recipient;
    }


    public boolean isInterested(EventObject ev) {

        for (int i = 0; i < likesEvents.size(); i++) {
            if (likesEvents.get(i).isInstance(ev)) {
                return true;
            }
        }

        // we did not find anything at all...
        return false;
    }


    public void removeChainChild(RecipientInfo child) {
        if (chainChildren == null) {
            return; // should not happen
        }
        chainChildren.remove(child);
    }


    public void removeEventInterest(Class<? extends EventObject> eventType) {
        boolean removed = likesEvents.remove(eventType);
        if (!removed) {
            Dispatcher.log("EVR", "could not unregister EventInterest " + eventType);
        }
        else {
            Dispatcher.log("EVR", "unregister EventInterest " + eventType);
        }
    }


    public void setChainParent(RecipientInfo daddy) {
        chainParent = daddy;
    }

}
