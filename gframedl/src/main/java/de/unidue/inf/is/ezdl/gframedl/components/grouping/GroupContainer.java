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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jdesktop.swingx.JXCollapsiblePane;

import de.unidue.inf.is.ezdl.gframedl.components.grouping.components.CollapseAction;
import de.unidue.inf.is.ezdl.gframedl.components.grouping.components.CollapseExpandBtn;
import de.unidue.inf.is.ezdl.gframedl.components.grouping.components.ExpandAction;
import de.unidue.inf.is.ezdl.gframedl.components.grouping.components.GroupSeparatorBorder;
import de.unidue.inf.is.ezdl.gframedl.components.grouping.components.TitleBtn;



/**
 * A container for one group in GroupsContainer.
 */
public class GroupContainer extends JPanel {

    private static final long serialVersionUID = 1L;

    /*
     * Ab einer bestimmten Anzahl an Gruppen, oder bei zu großer komponente in
     * der Gruppe - schmiert die Performance ab -> false.
     */
    private static final boolean IS_ANIMATED = false;
    private static final boolean USE_SINGLE_BTN_FOR_COLLAPSE_EXPAND = true;
    private static final boolean COLLAPSE_EXPAND_ON_TITLE_CLICK = true;

    private CollapseExpandBtn ceBtn;
    private JLabel label;
    private JComponent component;
    private JPanel titlePanel;
    private JXCollapsiblePane pane;
    private List<Action> additionalActions;


    /**
     * Constructor.
     * 
     * @param title
     * @param icon
     *            may be null.
     * @param component
     *            the contained component.
     * @param additionalActions
     *            additional actions displayed at the title, may be null.
     */
    public GroupContainer(String title, Icon icon, JComponent component, List<Action> additionalActions) {
        super();

        label = new JLabel(title);
        if (icon != null) {
            label.setIcon(icon);
        }
        this.component = component;
        this.additionalActions = additionalActions;
        component.setBorder(GroupSeparatorBorder.DEFAULT_GROUP_CONTAINER_COMPONENT_BORDER);

        pane = new JXCollapsiblePane();
        pane.setAnimated(IS_ANIMATED);
        pane.add(component);

        setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.PAGE_START;
        c.weightx = 1.0f;
        c.fill = GridBagConstraints.HORIZONTAL;
        titlePanel = buildTitlePanel();

        add(titlePanel, c);

        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 1;
        c.anchor = GridBagConstraints.PAGE_START;
        c.weightx = 1.0f;
        c.weighty = 1.0f;
        c.fill = GridBagConstraints.BOTH;
        this.add(pane, c);
    }


    /**
     * Returns the collapsed state.
     * 
     * @return
     */
    public boolean isCollapsed() {
        return pane.isCollapsed();
    }


    /**
     * Sets the collapsed state.
     * 
     * @param b
     */
    public void setCollapsed(boolean b) {
        pane.setCollapsed(b);
        if (USE_SINGLE_BTN_FOR_COLLAPSE_EXPAND) {
            ceBtn.setIconState();
        }
    }


    /**
     * Collapses the group.
     */
    public void collapse() {
        pane.setCollapsed(true);
        if (USE_SINGLE_BTN_FOR_COLLAPSE_EXPAND) {
            ceBtn.setIconState();
        }
    }


    /**
     * Expands the group.
     */
    public void expand() {
        pane.setCollapsed(false);
        if (USE_SINGLE_BTN_FOR_COLLAPSE_EXPAND) {
            ceBtn.setIconState();
        }
    }


    /**
     * Returns the label from the title panel.
     * 
     * @return
     */
    public JLabel getLabel() {
        return label;
    }


    /**
     * Returns the contained component.
     * 
     * @return
     */
    public JComponent getComponent() {
        return component;
    }


    private void addAction(JPanel titlePanel, Action action, int col) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = col;
        c.gridy = 0;
        c.anchor = GridBagConstraints.LINE_START;
        titlePanel.add(new TitleBtn(action), c);
    }


    private void addBtn(JPanel titlePanel, TitleBtn btn, int col) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = col;
        c.gridy = 0;
        c.anchor = GridBagConstraints.LINE_START;
        titlePanel.add(btn, c);
    }


    /**
     * Returns the title panel of this group.
     * 
     * @return
     */
    public JPanel getTitlePanel() {
        return titlePanel;
    }


    private JPanel buildTitlePanel() {
        JPanel result = new JPanel();

        result.setLayout(new GridBagLayout());

        int col;
        if (USE_SINGLE_BTN_FOR_COLLAPSE_EXPAND) {
            ceBtn = new CollapseExpandBtn(this);
            addBtn(result, ceBtn, 1);
            col = 2;
        }
        else {
            addAction(result, new ExpandAction(this), 0);
            addAction(result, new CollapseAction(this), 1);
            col = 3;
        }
        if (additionalActions != null) {
            for (Action a : additionalActions) {
                addAction(result, a, col);
                col++;
            }
        }

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = col;
        c.gridy = 0;
        c.anchor = GridBagConstraints.LINE_START;
        c.weightx = 0f;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(0, 20, 0, 0);
        result.add(label, c);
        col++;

        c = new GridBagConstraints();
        c.gridx = col;
        c.weightx = 1.0f;
        c.fill = GridBagConstraints.HORIZONTAL;
        result.add(new JPanel(), c);

        result.setBorder(GroupSeparatorBorder.DEFAULT_GROUP_CONTAINER_TITLE_BORDER);

        Dimension s = new Dimension(200, 26);
        result.setMinimumSize(s);
        result.setPreferredSize(s);
        result.setSize(s);

        if (COLLAPSE_EXPAND_ON_TITLE_CLICK) {
            result.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseClicked(MouseEvent e) {
                    if ((e.getButton() == MouseEvent.BUTTON1) && (e.getClickCount() == 1)) {
                        GroupContainer.this.setCollapsed(!GroupContainer.this.isCollapsed());
                    }
                }

            });
        }

        return result;
    }
}
