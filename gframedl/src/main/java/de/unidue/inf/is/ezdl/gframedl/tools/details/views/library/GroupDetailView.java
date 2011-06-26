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

package de.unidue.inf.is.ezdl.gframedl.tools.details.views.library;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import de.unidue.inf.is.ezdl.dlcore.data.dldata.DLObject;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.library.Group;
import de.unidue.inf.is.ezdl.dlfrontend.dispatcher.Dispatcher;
import de.unidue.inf.is.ezdl.dlfrontend.i18n.I18nSupport;
import de.unidue.inf.is.ezdl.gframedl.Icons;
import de.unidue.inf.is.ezdl.gframedl.events.LibraryEvent;
import de.unidue.inf.is.ezdl.gframedl.tools.details.DetailView;



/** Detail View for Groups used in Library */
public class GroupDetailView extends JScrollPane implements DetailView {

    private static final long serialVersionUID = 7841643688901885796L;

    private DLObject object;
    private GroupPanel groupPanel;


    /**
     * Constructor.
     */
    public GroupDetailView() {
        super();

    }


    @Override
    public void setObject(DLObject o, List<String> l) {

        object = o;
        if (o != null) {
            setGroup();
        }

    }


    @Override
    public DLObject getObject() {
        return object;
    }


    @Override
    public String getTabName() {
        return I18nSupport.getInstance().getLocString("ezdl.tools.library.group");
    }


    @Override
    public Icon getIcon() {
        return Icons.MEDIA_GROUP.get16x16();
    }


    private void setGroup() {
        Group g = (Group) getObject();

        groupPanel = new GroupPanel(g);
        setViewportView(groupPanel);

    }


    @Override
    public List<Action> getPossibleActions() {
        return null;
    }


    private void resetView() {
        groupPanel.removeAll();
        this.repaint();
        this.revalidate();
        object = null;
    }


    /** Group Panel for editing */
    private class GroupPanel extends JPanel {

        private static final long serialVersionUID = 4401269624239384399L;

        private JButton saveGroupButton, cancelGroupButton, deleteGroupButton;
        private Group group;
        private JTextField txtGroupName, txtGroupId, txtReferenceSystem, txtReferenceSystemId;
        private JComboBox comboType;
        private JCheckBox checkOnline;


        /**
         * Constructor.
         * 
         * @param hquery
         *            the HistoricQuery to display.
         */
        @SuppressWarnings("unused")
        public GroupPanel(Group group) {
            super(new SpringLayout());
            SpringLayout layout = (SpringLayout) this.getLayout();

            this.group = group;

            JLabel labelGroupId = new JLabel(I18nSupport.getInstance().getLocString("ezdl.tools.library.group.id"));
            txtGroupId = new JTextField();
            if (group.getId() != null) {
                txtGroupId.setText(group.getId());
            }
            txtGroupId.setEditable(false);
            txtGroupId.setEnabled(false);

            JLabel labelGroupName = new JLabel(I18nSupport.getInstance().getLocString("ezdl.tools.library.group.name"));
            txtGroupName = new JTextField();
            if (group.getName() != null) {
                txtGroupName.setText(group.getName());
            }

            JLabel labelReferenceSystem = new JLabel(I18nSupport.getInstance().getLocString(
                            "ezdl.tools.library.group.referencesystem"));
            txtReferenceSystem = new JTextField();
            if (group.getReferenceSystem() != null) {
                txtReferenceSystem.setText(group.getReferenceSystem());
            }
            txtReferenceSystem.setEditable(false);
            txtReferenceSystem.setEnabled(false);

            JLabel labelReferenceSystemId = new JLabel(I18nSupport.getInstance().getLocString(
                            "ezdl.tools.library.group.referencesystemid"));
            txtReferenceSystemId = new JTextField();
            if (group.getReferenceSystemId() != null) {
                txtReferenceSystemId.setText(group.getReferenceSystemId());
            }
            txtReferenceSystemId.setEditable(false);
            txtReferenceSystemId.setEnabled(false);

            JLabel labelOnline = new JLabel(I18nSupport.getInstance()
                            .getLocString("ezdl.tools.library.group.askonline"));
            checkOnline = new JCheckBox();

            checkOnline.addItemListener(new ItemListener() {

                @Override
                public void itemStateChanged(ItemEvent e) {
                    if (((JCheckBox) e.getSource()).isSelected()) {
                        comboType.setEnabled(true);
                    }
                    else {
                        comboType.setEnabled(false);
                    }
                }
            });

            JLabel labelType = new JLabel(I18nSupport.getInstance().getLocString("ezdl.tools.library.group.type"));
            comboType = new JComboBox();
            comboType.addItem(Group.TYPE_PRIVATE);
            comboType.addItem(Group.TYPE_OPEN);
            comboType.addItem(Group.TYPE_INVITE);

            if (!checkOnline.isSelected()) {
                comboType.setEnabled(false);
            }

            if (group.getType() != null && group.getType().length() > 0) {
                comboType.setSelectedItem(group.getType());
            }

            saveGroupButton = new JButton();
            saveGroupButton.setText(I18nSupport.getInstance().getLocString("ezdl.actions.save"));
            saveGroupButton.setIcon(Icons.SAVE.get16x16());
            saveGroupButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    saveGroup();
                }
            });

            cancelGroupButton = new JButton();
            cancelGroupButton.setText(I18nSupport.getInstance().getLocString("ezdl.tools.library.cancel"));
            cancelGroupButton.setIcon(Icons.CANCEL.get16x16());
            cancelGroupButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    cancelGroup();
                }
            });

            deleteGroupButton = new JButton();
            deleteGroupButton.setText(I18nSupport.getInstance().getLocString("ezdl.actions.delete"));
            deleteGroupButton.setIcon(Icons.DELETE.get16x16());
            deleteGroupButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    deleteGroup();
                }
            });

            add(labelGroupName);
            add(txtGroupName);
            add(comboType);
            add(labelType);
            add(saveGroupButton);
            add(deleteGroupButton);
            add(cancelGroupButton);
            add(labelOnline);
            add(checkOnline);

            layout.putConstraint(SpringLayout.NORTH, labelGroupName, 10, SpringLayout.NORTH, this);
            layout.putConstraint(SpringLayout.WEST, labelGroupName, 5, SpringLayout.WEST, this);

            layout.putConstraint(SpringLayout.NORTH, txtGroupName, 5, SpringLayout.NORTH, this);
            layout.putConstraint(SpringLayout.WEST, txtGroupName, 100, SpringLayout.EAST, labelGroupId);
            layout.putConstraint(SpringLayout.EAST, txtGroupName, -100, SpringLayout.EAST, this);

            layout.putConstraint(SpringLayout.NORTH, labelOnline, 19, SpringLayout.SOUTH, labelGroupName);
            layout.putConstraint(SpringLayout.WEST, labelOnline, 5, SpringLayout.WEST, this);

            layout.putConstraint(SpringLayout.NORTH, checkOnline, 10, SpringLayout.SOUTH, txtGroupName);
            layout.putConstraint(SpringLayout.WEST, checkOnline, 10, SpringLayout.EAST, labelOnline);

            layout.putConstraint(SpringLayout.NORTH, labelType, 10, SpringLayout.SOUTH, labelOnline);
            layout.putConstraint(SpringLayout.WEST, labelType, 5, SpringLayout.WEST, this);

            layout.putConstraint(SpringLayout.NORTH, comboType, 0, SpringLayout.NORTH, labelType);
            layout.putConstraint(SpringLayout.WEST, comboType, 0, SpringLayout.WEST, txtGroupName);

            layout.putConstraint(SpringLayout.NORTH, saveGroupButton, 40, SpringLayout.SOUTH, labelType);
            layout.putConstraint(SpringLayout.WEST, saveGroupButton, 5, SpringLayout.WEST, this);

            layout.putConstraint(SpringLayout.NORTH, deleteGroupButton, 0, SpringLayout.NORTH, saveGroupButton);
            layout.putConstraint(SpringLayout.WEST, deleteGroupButton, 10, SpringLayout.EAST, saveGroupButton);

            layout.putConstraint(SpringLayout.NORTH, cancelGroupButton, 0, SpringLayout.NORTH, saveGroupButton);
            layout.putConstraint(SpringLayout.WEST, cancelGroupButton, 10, SpringLayout.EAST, deleteGroupButton);

            if (group.onlineGroup()) {
                // onilne groups can't be changed
                checkOnline.setSelected(true);
                checkOnline.setEnabled(false);
                txtGroupName.setEnabled(false);
                comboType.setEnabled(false);
                saveGroupButton.setEnabled(false);

            }

        }


        private void saveGroup() {
            if (group.getId() == null) {
                // Generate unique ID
                String hid;
                if (txtGroupName.getText().trim().length() > 5) {
                    hid = txtGroupName.getText().trim().substring(0, 5) + Long.toString(System.currentTimeMillis());
                }
                else {
                    hid = txtGroupName.getText().trim() + Long.toString(System.currentTimeMillis());
                }
                group.setId(hid);
            }

            group.setName(txtGroupName.getText());

            group.setType((String) comboType.getSelectedItem());

            // Save in online referencesystem?
            if (checkOnline.isSelected()) {
                group.setSaveOnline(true);
            }
            else {
                group.setSaveOnline(false);
            }

            LibraryEvent libraryEvent = new LibraryEvent(this, group, LibraryEvent.ADD_GROUP);
            Dispatcher.postEvent(libraryEvent);

            resetView();

        }


        private void cancelGroup() {
            resetView();
        }


        private void deleteGroup() {
            LibraryEvent libraryEvent = new LibraryEvent(this, group, LibraryEvent.DELETE_GROUP);
            Dispatcher.postEvent(libraryEvent);

            resetView();
        }

    }

}
