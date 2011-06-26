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

package de.unidue.inf.is.ezdl.gframedl.tools.library;

import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import de.unidue.inf.is.ezdl.dlcore.data.dldata.library.Group;
import de.unidue.inf.is.ezdl.dlfrontend.i18n.I18nSupport;
import de.unidue.inf.is.ezdl.gframedl.Icons;



/** The Panel where the groups will displayed */
public class GroupListPanel extends JPanel {

    private static final long serialVersionUID = 2719856978773101615L;

    private List<GroupLabel> groups;


    public GroupListPanel() {
        super();
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        groups = new ArrayList<GroupLabel>();
    }


    /** Adds a group Label to the GUI and returns the label */
    public GroupLabel addGroup(Group group) {
        GroupLabel tmp = containsGroupId(group.getId());

        // Group with given ID already there
        if (tmp != null) {
            tmp.override(group);
            if (group.onlineGroup()) {
                tmp.setIcon(Icons.MEDIA_GROUP_ONLINE.get16x16());
                tmp.setToolTipText(I18nSupport.getInstance().getLocString("ezdl.tools.library.group.online"));
            }
            else {
                tmp.setIcon(Icons.MEDIA_GROUP.get16x16());
                tmp.setToolTipText("");
            }
            return null;
        }
        else {
            GroupLabel groupLabel = new GroupLabel(group);
            if (group.onlineGroup()) {
                groupLabel.setIcon(Icons.MEDIA_GROUP_ONLINE.get16x16());
                groupLabel.setToolTipText(I18nSupport.getInstance().getLocString("ezdl.tools.library.group.online"));
            }
            else {
                groupLabel.setIcon(Icons.MEDIA_GROUP.get16x16());
            }
            add(groupLabel);
            groups.add(groupLabel);
            return groupLabel;
        }
    }


    /** Deletes a group from the GUI */
    public void deleteGroup(Group group) {
        GroupLabel tmp = containsGroupId(group.getId());
        if (tmp != null) {
            remove(tmp);
            groups.remove(tmp);
        }
    }


    /**
     * Checks if given group ID is already displayed
     * 
     * @param id
     * @return GroupLabel with the given ID, null if not found
     */
    private GroupLabel containsGroupId(String id) {
        for (GroupLabel gl : groups) {
            if (gl.getGroup().getId().equals(id)) {
                return gl;
            }
        }
        return null;
    }


    /** Returns all groups */
    public List<Group> getGroups() {
        List<Group> rgroups = new ArrayList<Group>();

        for (GroupLabel gl : groups) {
            rgroups.add(gl.getGroup());
        }

        return rgroups;
    }
}
