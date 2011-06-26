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

package de.unidue.inf.is.ezdl.gframedl.components.grouping;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import de.unidue.inf.is.ezdl.gframedl.components.grouping.components.CollapseAllAction;
import de.unidue.inf.is.ezdl.gframedl.components.grouping.components.ExpandAllAction;
import de.unidue.inf.is.ezdl.gframedl.components.grouping.components.GroupSeparatorBorder;
import de.unidue.inf.is.ezdl.gframedl.components.grouping.components.TitleBtn;



/**
 * GroupsContainer, a container which contains a number of collapsible
 * GroupContainers.
 * 
 * @author R.Tipografov
 */
public class GroupsContainer extends JPanel {

    private static final long serialVersionUID = 1L;

    protected JScrollPane groupScroll;
    protected JPanel whitespace = new JPanel();
    protected JPanel groupPanel = new JPanel();

    private int row;
    protected JLabel titleLabel;
    protected JPanel titlePanel;


    /**
     * Constructs a GroupsContainer without a title panel.
     */
    public GroupsContainer() {
        super();
        setLayout(new GridBagLayout());

        groupPanel = new JPanel();
        groupPanel.setLayout(new GridBagLayout());

        groupPanel.setBackground(Color.white);

        groupScroll = new JScrollPane(groupPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        setBorder(groupScroll.getBorder());
        groupScroll.setBorder(null);
        groupScroll.getVerticalScrollBar().setUnitIncrement(50);
        groupScroll.getVerticalScrollBar().setBlockIncrement(100);

        addWhiteSpace(this, groupScroll, 0);
        setDoubleBuffered(true);
    }


    /**
     * Constructs a GroupsContainer with a title panel.
     */
    public GroupsContainer(String title, Icon icon) {
        this();
        titleLabel = new JLabel(title);
        if (icon != null) {
            titleLabel.setIcon(icon);
        }
        this.removeAll();
        titlePanel = getTitlePanel();
        addGroup(this, titlePanel, 0);
        addWhiteSpace(this, groupScroll, 1);
    }


    /**
     * Expands all contained groups.
     */
    public void expandAll() {
        Component c;
        for (int i = 0; i < groupPanel.getComponentCount(); i++) {
            c = groupPanel.getComponent(i);
            if (c instanceof GroupContainer) {
                ((GroupContainer) c).expand();
            }
        }
    }


    /**
     * Collapses all contained groups.
     */
    public void collapseAll() {
        Component c;
        for (int i = 0; i < groupPanel.getComponentCount(); i++) {
            c = groupPanel.getComponent(i);
            if (c instanceof GroupContainer) {
                ((GroupContainer) c).collapse();
            }
        }
    }


    /**
     * Adds a new group.
     */
    public void addGroupContainer(GroupContainer g) {
        addGroup(groupPanel, g, row);
        addWhiteSpace(groupPanel, whitespace, row + 1);
        row++;
    }


    /**
     * Removes a group.
     */
    public void removeGroupContainer(GroupContainer g) {
        groupPanel.remove(g);
    }


    /**
     * Removes all groups.
     */
    public void removeAllGroups() {
        groupPanel.removeAll();
    }


    protected void resetViewPosition() {
        groupScroll.getViewport().setViewPosition(new Point(0, 0));
    }


    private void addWhiteSpace(JPanel p, JComponent comp, int row) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = row;
        c.anchor = GridBagConstraints.PAGE_START;
        c.weightx = 1.0f;
        c.weighty = 1.0f;
        c.fill = GridBagConstraints.BOTH;
        p.add(comp, c);
    }


    private void addGroup(JPanel p, Component component, int row) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = row;
        c.anchor = GridBagConstraints.PAGE_START;
        c.weightx = 1.0f;
        c.fill = GridBagConstraints.HORIZONTAL;
        p.add(component, c);
    }


    protected JPanel getTitlePanel() {
        JPanel result = new JPanel();
        result.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.LINE_START;
        result.add(titleLabel, c);

        c = new GridBagConstraints();
        c.gridx = 1;
        c.weightx = 1.0f;
        c.fill = GridBagConstraints.HORIZONTAL;
        result.add(new JPanel(), c);

        c = new GridBagConstraints();
        c.gridx = 2;
        c.gridy = 0;
        c.anchor = GridBagConstraints.LINE_END;
        c.weighty = 1.0f;
        result.add(new TitleBtn(new ExpandAllAction(this)), c);

        c = new GridBagConstraints();
        c.gridx = 3;
        c.gridy = 0;
        c.weighty = 1.0f;
        c.anchor = GridBagConstraints.LINE_END;
        result.add(new TitleBtn(new CollapseAllAction(this)), c);

        result.setBorder(GroupSeparatorBorder.DEFAULT_GROUPS_CONTAINER_TITLE_BORDER);
        return result;
    }
}
