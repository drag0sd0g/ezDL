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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ListModel;
import javax.swing.event.ListSelectionListener;

import org.jdesktop.swingx.JXList;

import de.unidue.inf.is.ezdl.dlcore.data.dldata.DLObject;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.TextDocument;
import de.unidue.inf.is.ezdl.dlfrontend.i18n.I18nSupport;
import de.unidue.inf.is.ezdl.gframedl.Icons;
import de.unidue.inf.is.ezdl.gframedl.components.actions.SelectionGetter;
import de.unidue.inf.is.ezdl.gframedl.tools.AbstractToolView;
import de.unidue.inf.is.ezdl.gframedl.tools.Tool;



/** The View Class of the library */
public final class LibraryToolView extends AbstractToolView implements SelectionGetter {

    private static final long serialVersionUID = 3326812008707711436L;

    private JPanel toolbarPanel;
    private JPanel splitPanel;
    private JPanel groupsPanel;
    private GroupListPanel groupListPanel;
    private LibraryListPanel libraryListPanel;
    private JToolBar toolbar;
    private JButton toolbarEditButton;
    private JButton toolbarDeleteButton;
    private JButton toolbarAddGroupButton;
    private JButton toolbarEditGroupButton;
    private JButton toolbarDeleteGroupButton;
    private JButton toolbarReferenceSystemButton;
    private JButton toolbarReSynchronizeButton;
    private JButton toolbarNewButton;
    private JSplitPane splitPane;
    private JScrollPane groupListScrollPane;

    private I18nSupport i18n = I18nSupport.getInstance();


    public LibraryToolView(Tool tool) {
        super(tool);
        createContent();
    }


    private void createContent() {
        setLayout(new BorderLayout());
        add(getToolbarPanel(), BorderLayout.NORTH);
        add(getSplitPanel(), BorderLayout.CENTER);
    }


    protected JPanel getLibraryListPanel() {
        if (libraryListPanel == null) {
            libraryListPanel = new LibraryListPanel();
        }
        return libraryListPanel;
    }


    protected JPanel getSplitPanel() {
        if (splitPanel == null) {
            splitPanel = new JPanel(new BorderLayout());
            splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, getGroupsPanel(), getLibraryListPanel());
            splitPane.setContinuousLayout(true);
            splitPanel.add(splitPane);
        }
        return splitPanel;
    }


    private JPanel getGroupsPanel() {
        if (groupsPanel == null) {
            groupsPanel = new JPanel(new BorderLayout());
            JLabel label = new JLabel(i18n.getLocString("ezdl.tools.library.groups"));
            Font f = label.getFont();
            label.setFont(f.deriveFont(f.getStyle() ^ Font.BOLD));
            groupsPanel.add(label, BorderLayout.NORTH);
            groupListScrollPane = new JScrollPane();
            groupListScrollPane.setViewportView(getGroupListPanel());
            groupsPanel.add(groupListScrollPane, BorderLayout.CENTER);
        }
        return groupsPanel;
    }


    public GroupListPanel getGroupListPanel() {
        if (groupListPanel == null) {
            groupListPanel = new GroupListPanel();
        }
        return groupListPanel;
    }


    protected JPanel getToolbarPanel() {
        if (toolbarPanel == null) {
            toolbarPanel = new JPanel(new BorderLayout());
            toolbarPanel.add(getToolBar());
        }
        return toolbarPanel;
    }


    public JButton getToolbarEditButton() {
        return toolbarEditButton;
    }


    public JButton getToolbarDeleteButton() {
        return toolbarDeleteButton;
    }


    public JButton getToolbarAddGroupButton() {
        return toolbarAddGroupButton;
    }


    public JButton getToolbarEditGroupButton() {
        return toolbarEditGroupButton;
    }


    public JButton getToolbarDeleteGroupButton() {
        return toolbarDeleteGroupButton;
    }


    public JButton getToolbarReferenceSystemButton() {
        return toolbarReferenceSystemButton;
    }


    public JButton getToolbarNewButton() {
        return toolbarNewButton;
    }


    public JButton getToolbarReSynchronizeButton() {
        return toolbarReSynchronizeButton;
    }


    protected void setListModel(ListModel lm) {
        getObjectList().setModel(lm);
    }


    public JTextField getSearchTextField() {
        return libraryListPanel.getSearchTextField();
    }


    protected JToolBar getToolBar() {
        if (toolbar == null) {
            toolbar = new JToolBar();
            toolbar.setFloatable(false);

            toolbarEditButton = new JButton();
            toolbarEditButton.setIcon(Icons.EDIT.get22x22());
            toolbarEditButton.setToolTipText(i18n.getLocString("ezdl.tools.library.toolbarEditButton"));
            toolbar.add(toolbarEditButton);

            toolbarNewButton = new JButton();
            toolbarNewButton.setIcon(Icons.ADD_NEW.get22x22());
            toolbarNewButton.setToolTipText(i18n.getLocString("ezdl.tools.library.toolbarNewButton"));
            toolbar.add(toolbarNewButton);

            toolbarDeleteButton = new JButton();
            toolbarDeleteButton.setIcon(Icons.DELETE.get22x22());
            toolbarDeleteButton.setToolTipText(i18n.getLocString("ezdl.tools.library.toolbarDeleteButton"));
            toolbar.add(toolbarDeleteButton);

            toolbarAddGroupButton = new JButton();
            toolbarAddGroupButton.setIcon(Icons.ADD_GROUP.get22x22());
            toolbarAddGroupButton.setToolTipText(i18n.getLocString("ezdl.tools.library.toolbarAddGroupButton"));
            toolbar.add(toolbarAddGroupButton);

            toolbarEditGroupButton = new JButton();
            toolbarEditGroupButton.setIcon(Icons.EDIT_GROUP.get22x22());
            toolbarEditGroupButton.setToolTipText(i18n.getLocString("ezdl.tools.library.toolbarEditGroupButton"));
            toolbar.add(toolbarEditGroupButton);

            toolbarDeleteGroupButton = new JButton();
            toolbarDeleteGroupButton.setIcon(Icons.DELETE_GROUP.get22x22());
            toolbarDeleteGroupButton.setToolTipText(i18n.getLocString("ezdl.tools.library.toolbarDeleteGroupButton"));
            toolbar.add(toolbarDeleteGroupButton);

            toolbarReferenceSystemButton = new JButton();
            toolbarReferenceSystemButton.setIcon(Icons.MEDIA_URL.get22x22());
            toolbarReferenceSystemButton.setToolTipText(i18n
                            .getLocString("ezdl.tools.library.toolbarReferenceSystemButton"));
            toolbar.add(toolbarReferenceSystemButton);

            toolbarReSynchronizeButton = new JButton();
            toolbarReSynchronizeButton.setIcon(Icons.RESYNCH.get22x22());
            toolbarReSynchronizeButton.setToolTipText(i18n
                            .getLocString("ezdl.tools.library.toolbarReSynchronizeButton"));
            toolbar.add(toolbarReSynchronizeButton);
        }
        return toolbar;
    }


    public void updateSplitPaneDivider() {
        int width = 0;
        for (Component c : groupListPanel.getComponents()) {
            if (width < c.getPreferredSize().width) {
                width = c.getPreferredSize().width;
            }
        }
        splitPane.setDividerLocation(width + 20);
    }


    public void showMessageBox(String message, String title) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.OK_CANCEL_OPTION);
    }


    @Override
    public List<DLObject> getSelectedObjects() {
        LinkedList<DLObject> out = new LinkedList<DLObject>();
        Object[] sel = getObjectList().getSelectedValues();
        for (Object o : sel) {
            if (o instanceof TextDocument) {
                out.add((DLObject) o);
            }
        }
        return out;
    }


    @Override
    public void addListSelectionListener(ListSelectionListener listener) {
        libraryListPanel.getList().addListSelectionListener(listener);
    }


    protected JXList getObjectList() {
        return libraryListPanel.getList();
    }
}
