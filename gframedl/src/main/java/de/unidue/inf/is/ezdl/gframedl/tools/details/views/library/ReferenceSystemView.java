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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.Action;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import de.unidue.inf.is.ezdl.dlcore.data.dldata.DLObject;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.library.ReferenceSystem;
import de.unidue.inf.is.ezdl.dlfrontend.dispatcher.Dispatcher;
import de.unidue.inf.is.ezdl.dlfrontend.i18n.I18nSupport;
import de.unidue.inf.is.ezdl.gframedl.Icons;
import de.unidue.inf.is.ezdl.gframedl.components.TextComponentPopupMenu;
import de.unidue.inf.is.ezdl.gframedl.events.LibraryEvent;
import de.unidue.inf.is.ezdl.gframedl.tools.details.DetailView;



/** Detail view for the online reference system (which system, settings...) */
public class ReferenceSystemView extends JScrollPane implements DetailView {

    private static final long serialVersionUID = -510808190808360946L;

    private DLObject object;
    private ReferenceSystemPanel refPanel;


    public ReferenceSystemView() {
        super();
    }


    @Override
    public void setObject(DLObject o, List<String> l) {

        object = o;
        if (o != null) {
            setReferenceSystem();
        }
    }


    @Override
    public DLObject getObject() {
        return object;
    }


    @Override
    public String getTabName() {
        return I18nSupport.getInstance().getLocString("ezdl.tools.library.referencesystem");
    }


    @Override
    public Icon getIcon() {
        return Icons.MEDIA_URL.get16x16();
    }


    private void setReferenceSystem() {
        ReferenceSystem rs = (ReferenceSystem) getObject();
        refPanel = new ReferenceSystemPanel(rs);
        setViewportView(refPanel);
    }


    @Override
    public List<Action> getPossibleActions() {
        return null;
    }


    private void resetView() {
        // setObject((ReferenceSystem) object);
        refPanel.removeAll();
        this.repaint();
        this.revalidate();
        object = null;
    }


    /** Save reference System */
    private void save() {
        ReferenceSystem refSystem = (ReferenceSystem) object;
        refSystem.setName((String) refPanel.comboReferenceSystems.getSelectedItem());

        // Set the parameters
        HashMap<String, String> parameters = new HashMap<String, String>();
        for (int i = 0; i < refPanel.parametersLabels.size(); i++) {
            parameters.put(refPanel.parametersLabels.get(i).getText(), refPanel.parametersTextFields.get(i).getText());
        }
        refSystem.setRequiredParameters(parameters);

        LibraryEvent libraryEvent = new LibraryEvent(this, refSystem, LibraryEvent.CHANGE_REFERENCESYSTEM);
        Dispatcher.postEvent(libraryEvent);

        resetView();
    }


    /** Cancel */
    private void cancel() {
        resetView();
    }


    /** Delete settings */
    private void delete() {
        ReferenceSystem refSystem = (ReferenceSystem) object;
        LibraryEvent libraryEvent = new LibraryEvent(this, refSystem, LibraryEvent.DELETE_REFERENCESYSTEM);
        Dispatcher.postEvent(libraryEvent);

        resetView();
    }


    /** Work offline button was pressed */
    private void workOffline() {
        ReferenceSystem refSystem = (ReferenceSystem) object;
        LibraryEvent libraryEvent = new LibraryEvent(this, refSystem, LibraryEvent.WORK_OFFLINE);
        Dispatcher.postEvent(libraryEvent);
        resetView();
    }


    /** Panel for editing */
    private class ReferenceSystemPanel extends JPanel {

        private static final long serialVersionUID = -4295549765776831221L;

        private ReferenceSystem rs;
        private JPanel parametersPanel = null;
        private List<JLabel> parametersLabels;
        private List<JTextField> parametersTextFields;
        private SpringLayout layout;
        private JButton saveButton, cancelButton, deleteButton, offlineButton;

        private JLabel labelReferenceSystems, labelWarning;
        private JComboBox comboReferenceSystems;


        public ReferenceSystemPanel(ReferenceSystem rs) {
            super();
            this.rs = rs;

            layout = new SpringLayout();
            this.setLayout(layout);

            labelReferenceSystems = new JLabel(I18nSupport.getInstance().getLocString(
                            "ezdl.tools.library.group.referencesystems"));
            comboReferenceSystems = new JComboBox();

            setComboBox();

            add(labelReferenceSystems);
            add(comboReferenceSystems);

            layout.putConstraint(SpringLayout.NORTH, labelReferenceSystems, 10, SpringLayout.NORTH, this);
            layout.putConstraint(SpringLayout.WEST, labelReferenceSystems, 5, SpringLayout.WEST, this);

            layout.putConstraint(SpringLayout.NORTH, comboReferenceSystems, 5, SpringLayout.NORTH, this);
            layout.putConstraint(SpringLayout.WEST, comboReferenceSystems, 20, SpringLayout.EAST, labelReferenceSystems);
            layout.putConstraint(SpringLayout.EAST, comboReferenceSystems, 350, SpringLayout.EAST,
                            labelReferenceSystems);

            setParametersPanel();

            saveButton = new JButton();
            saveButton.setText(I18nSupport.getInstance().getLocString("ezdl.actions.save"));
            saveButton.setIcon(Icons.SAVE.get16x16());
            saveButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    save();
                }
            });

            cancelButton = new JButton();
            cancelButton.setText(I18nSupport.getInstance().getLocString("ezdl.tools.library.cancel"));
            cancelButton.setIcon(Icons.CANCEL.get16x16());
            cancelButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    cancel();
                }
            });

            deleteButton = new JButton();
            deleteButton.setText(I18nSupport.getInstance().getLocString("ezdl.actions.delete"));
            deleteButton.setIcon(Icons.DELETE.get16x16());
            deleteButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    delete();
                }
            });

            offlineButton = new JButton();
            offlineButton.setText(I18nSupport.getInstance().getLocString("ezdl.tools.library.referencesystem.offline"));
            offlineButton.setIcon(Icons.MEDIA_OFFLINEURL.get16x16());
            offlineButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    workOffline();
                }
            });

            add(saveButton);
            add(cancelButton);
            add(deleteButton);
            add(offlineButton);

            layout.putConstraint(SpringLayout.NORTH, saveButton, -50, SpringLayout.SOUTH, this);
            layout.putConstraint(SpringLayout.WEST, saveButton, 5, SpringLayout.WEST, this);

            layout.putConstraint(SpringLayout.NORTH, deleteButton, 0, SpringLayout.NORTH, saveButton);
            layout.putConstraint(SpringLayout.WEST, deleteButton, 10, SpringLayout.EAST, saveButton);

            layout.putConstraint(SpringLayout.NORTH, cancelButton, 0, SpringLayout.NORTH, saveButton);
            layout.putConstraint(SpringLayout.WEST, cancelButton, 10, SpringLayout.EAST, deleteButton);

            layout.putConstraint(SpringLayout.NORTH, offlineButton, 0, SpringLayout.NORTH, saveButton);
            layout.putConstraint(SpringLayout.WEST, offlineButton, 10, SpringLayout.EAST, cancelButton);

            // ReferenceSystem already chossen.
            // Lock change other reference system. first you need to delete the
            // old referencesystem.
            // then you can choose a new one
            if (rs.getName() != null && !rs.workOffline()) {
                saveButton.setEnabled(false);
                comboReferenceSystems.setEnabled(false);
                labelWarning = new JLabel(I18nSupport.getInstance().getLocString(
                                "ezdl.tools.library.referencesystem.warning"));
                add(labelWarning);
                layout.putConstraint(SpringLayout.NORTH, labelWarning, 10, SpringLayout.SOUTH, labelReferenceSystems);
                layout.putConstraint(SpringLayout.WEST, labelWarning, 0, SpringLayout.WEST, labelReferenceSystems);

            }

        }


        private void setParametersPanel() {
            if (parametersPanel != null) {
                this.remove(parametersPanel);
            }

            parametersPanel = new JPanel();
            SpringLayout playout = new SpringLayout();
            parametersPanel.setLayout(playout);

            this.add(parametersPanel);

            parametersLabels = new ArrayList<JLabel>();
            parametersTextFields = new ArrayList<JTextField>();

            for (ReferenceSystem r : rs.getOtherAvailableReferenceSystems()) {
                // this referencesystem is selected
                if (r.getName().equals(comboReferenceSystems.getSelectedItem())) {
                    for (Entry<String, String> e : r.getRequiredParameters().entrySet()) {
                        parametersLabels.add(new JLabel(e.getKey()));
                        JTextField tmp = new JTextField();
                        TextComponentPopupMenu.addPopupMenu(tmp);

                        // There are already saved settings for this reference
                        // system
                        // Set value
                        if (r.getName().equals(rs.getName())) {
                            tmp.setText(rs.getRequiredParameters().get(e.getKey()));
                        }

                        parametersTextFields.add(tmp);
                    }
                }
            }

            // Add compontens
            for (int i = 0; i < parametersLabels.size(); i++) {
                parametersPanel.add(parametersLabels.get(i));
                parametersPanel.add(parametersTextFields.get(i));
            }

            // Set layout
            this.add(parametersPanel);

            for (int i = 0; i < parametersLabels.size(); i++) {
                if (i == 0) {
                    playout.putConstraint(SpringLayout.NORTH, parametersLabels.get(i), 0, SpringLayout.NORTH,
                                    parametersPanel);
                }
                else {
                    playout.putConstraint(SpringLayout.NORTH, parametersLabels.get(i), 10, SpringLayout.SOUTH,
                                    parametersLabels.get(i - 1));
                }

                playout.putConstraint(SpringLayout.WEST, parametersLabels.get(i), 0, SpringLayout.WEST, parametersPanel);
                playout.putConstraint(SpringLayout.NORTH, parametersTextFields.get(i), -5, SpringLayout.NORTH,
                                parametersLabels.get(i));
                playout.putConstraint(SpringLayout.WEST, parametersTextFields.get(i), 120, SpringLayout.WEST,
                                parametersPanel);
                playout.putConstraint(SpringLayout.EAST, parametersTextFields.get(i), -30, SpringLayout.EAST,
                                parametersPanel);

            }

            layout.putConstraint(SpringLayout.NORTH, parametersPanel, 20, SpringLayout.SOUTH, comboReferenceSystems);
            layout.putConstraint(SpringLayout.WEST, parametersPanel, 5, SpringLayout.WEST, this);
            layout.putConstraint(SpringLayout.EAST, parametersPanel, 5, SpringLayout.EAST, this);
            layout.putConstraint(SpringLayout.SOUTH, parametersPanel, -100, SpringLayout.SOUTH, this);

            this.repaint();
            this.revalidate();

        }


        /** Fill combobox with available reference Systems */
        private void setComboBox() {
            for (ReferenceSystem r : rs.getOtherAvailableReferenceSystems()) {
                comboReferenceSystems.addItem(r.getName());

            }

            if (rs.getName() != null) {
                comboReferenceSystems.setSelectedItem(rs.getName());
            }

            comboReferenceSystems.setRenderer(new DefaultListCellRenderer() {

                private static final long serialVersionUID = -5460014450312978883L;


                @Override
                public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
                                boolean cellHasFocus) {
                    JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected,
                                    cellHasFocus);
                    label.setOpaque(true);
                    label.setIcon(Icons.MEDIA_URL.get16x16());
                    label.setText((String) value);

                    return label;
                }
            });

            comboReferenceSystems.addItemListener(new ItemListener() {

                @Override
                public void itemStateChanged(ItemEvent e) {
                    setParametersPanel();
                }
            });

        }
    }

}
