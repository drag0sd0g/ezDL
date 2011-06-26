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
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import de.unidue.inf.is.ezdl.dlcore.data.dldata.DLObject;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.Document;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.TermWithWeight;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.TermWithWeightList;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.URLList;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.library.EditDocument;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.library.Group;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.library.GroupList;
import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;
import de.unidue.inf.is.ezdl.dlfrontend.dispatcher.Dispatcher;
import de.unidue.inf.is.ezdl.dlfrontend.i18n.I18nSupport;
import de.unidue.inf.is.ezdl.gframedl.Icons;
import de.unidue.inf.is.ezdl.gframedl.components.TextComponentPopupMenu;
import de.unidue.inf.is.ezdl.gframedl.events.LibraryEvent;
import de.unidue.inf.is.ezdl.gframedl.tools.details.DetailView;



/** Detail View for editing a document */
public class EditDocumentDetailView extends JScrollPane implements DetailView {

    private static final long serialVersionUID = -3956265997104920122L;

    private DLObject object;
    private EditDocumentPanel editDocumentPanel;


    /**
     * Constructor.
     */
    public EditDocumentDetailView() {
        super();
    }


    @Override
    public void setObject(DLObject o, List<String> s) {

        object = o;
        if (o != null) {
            setEditDocument();
        }
    }


    @Override
    public DLObject getObject() {
        return object;
    }


    @Override
    public String getTabName() {
        return I18nSupport.getInstance().getLocString("ezdl.tools.library.editDetail");
    }


    @Override
    public Icon getIcon() {
        return Icons.MEDIA_GROUP.get16x16();
    }


    private void setEditDocument() {
        EditDocument ed = (EditDocument) getObject();
        editDocumentPanel = new EditDocumentPanel(ed);
        setViewportView(editDocumentPanel);
    }


    @Override
    public List<Action> getPossibleActions() {
        return null;
    }


    /** Save document */
    private void save() {
        Document document = ((EditDocument) object).getDocument();

        // document is a self created note
        if (document.getOid() == null) {
            document.setOid("OWNNOTE" + Long.toString(System.currentTimeMillis()));
            document.setTitle((editDocumentPanel.txtNotes.getText().length() > 20 ? editDocumentPanel.txtNotes
                            .getText().substring(0, 20) : editDocumentPanel.txtNotes.getText()));
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            document.setYear(cal.get(Calendar.YEAR));
        }

        if (document.getOid().startsWith("OWNNOTE")) {
            // If it is a own note then save urls
            // otherwise URL ist not saved. Because document is updated
            // automatically from the wrappers
            URLList urlList = (URLList) document.getFieldValue(Field.URLS);
            if (urlList == null) {
                urlList = new URLList();
                document.setFieldValue(Field.URLS, urlList);
            }
            else {
                urlList.clear();
            }

            StringTokenizer tok = new StringTokenizer(editDocumentPanel.txtURL.getText(), "\n");

            while (tok.hasMoreTokens()) {
                try {
                    urlList.add(new URL(tok.nextToken()));
                }
                catch (Exception e) {

                }
            }
        }

        // Save tags
        TermWithWeightList tagList = (TermWithWeightList) document.getFieldValue(Field.TAGS);
        if (tagList == null) {
            tagList = new TermWithWeightList();
            document.setFieldValue(Field.TAGS, tagList);
        }
        else {
            tagList.clear();
        }

        StringTokenizer tok = new StringTokenizer(editDocumentPanel.txtTags.getText(), " ");

        while (tok.hasMoreTokens()) {
            tagList.add(new TermWithWeight(tok.nextToken(), 1));
        }

        // Save notes
        document.setFieldValue(Field.NOTE, editDocumentPanel.txtNotes.getText());

        // Save groups
        GroupList groupList = (GroupList) document.getFieldValue(Field.GROUPS);
        if (groupList == null) {
            groupList = new GroupList();
            document.setFieldValue(Field.GROUPS, groupList);
        }
        else {
            groupList.clear();
        }

        for (int i = 0; i < editDocumentPanel.memberGroups.getModel().getSize(); i++) {
            groupList.add((Group) editDocumentPanel.memberGroups.getModel().getElementAt(i));
        }

        LibraryEvent libraryEvent = new LibraryEvent(this, document, LibraryEvent.SAVE_DOCUMENT);
        Dispatcher.postEvent(libraryEvent);

        resetView();
    }


    private void resetView() {
        // setObject((ReferenceSystem) object);
        editDocumentPanel.removeAll();
        this.repaint();
        this.revalidate();
        object = null;
    }


    /** Abort, show document */
    private void cancel() {
        resetView();
    }


    /** Panel for editing */
    private class EditDocumentPanel extends JPanel {

        private static final long serialVersionUID = 2293181803360675086L;

        private EditDocument ed;
        private JList availableGroups, memberGroups;
        private JLabel labelTags, labelGroups, labelAvailableGroups, labelMemberGroups, labelInfo, labelNotes,
                        labelURL, labelInfoGroup;
        private JTextField txtTags;
        private JTextArea txtNotes, txtURL;
        private DefaultListModel aivailableLm, memberLm;
        private JScrollPane scrollAvailableGroups, scrollMemberGroups, scrollNotes, scrollURLs;
        private JButton saveButton, cancelButton;


        public EditDocumentPanel(EditDocument ed) {
            super();
            this.ed = ed;

            SpringLayout layout = new SpringLayout();
            this.setLayout(layout);

            labelTags = new JLabel(I18nSupport.getInstance().getLocString(
                            "ezdl.controls.resultlistpanel.label.extract.tags"));

            txtTags = new JTextField();
            TextComponentPopupMenu.addPopupMenu(txtTags);

            labelGroups = new JLabel(I18nSupport.getInstance().getLocString("ezdl.tools.library.groups"));
            labelAvailableGroups = new JLabel(I18nSupport.getInstance().getLocString(
                            "ezdl.tools.library.group.available"));
            labelMemberGroups = new JLabel(I18nSupport.getInstance().getLocString("ezdl.tools.library.group.member"));

            labelInfo = new JLabel(I18nSupport.getInstance().getLocString("ezdl.tools.library.editDetail.info"));
            labelInfoGroup = new JLabel(I18nSupport.getInstance().getLocString(
                            "ezdl.tools.library.editDetail.infogroup"));

            availableGroups = new JList();
            memberGroups = new JList();
            scrollAvailableGroups = new JScrollPane(availableGroups);
            scrollMemberGroups = new JScrollPane(memberGroups);

            labelURL = new JLabel(I18nSupport.getInstance().getLocString("field.url"));
            txtURL = new JTextArea();
            TextComponentPopupMenu.addPopupMenu(txtURL);
            scrollURLs = new JScrollPane(txtURL);

            if (ed.getDocument().getOid() != null && !ed.getDocument().getOid().startsWith("OWNNOTE")) {
                txtURL.setVisible(false);
            }

            labelNotes = new JLabel(I18nSupport.getInstance().getLocString("ezdl.tools.library.notes"));
            txtNotes = new JTextArea();
            TextComponentPopupMenu.addPopupMenu(txtNotes);
            scrollNotes = new JScrollPane(txtNotes);

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

            add(labelTags);
            add(txtTags);
            add(labelGroups);
            add(scrollAvailableGroups);
            add(scrollMemberGroups);
            add(labelAvailableGroups);
            add(labelMemberGroups);
            add(labelInfo);
            add(saveButton);
            add(cancelButton);
            add(labelNotes);
            add(scrollNotes);
            add(labelURL);
            add(scrollURLs);
            add(labelInfoGroup);

            layout.putConstraint(SpringLayout.NORTH, labelTags, 10, SpringLayout.NORTH, this);
            layout.putConstraint(SpringLayout.WEST, labelTags, 5, SpringLayout.WEST, this);

            layout.putConstraint(SpringLayout.NORTH, txtTags, 5, SpringLayout.NORTH, this);
            layout.putConstraint(SpringLayout.WEST, txtTags, 50, SpringLayout.EAST, labelTags);
            layout.putConstraint(SpringLayout.EAST, txtTags, -100, SpringLayout.EAST, this);

            layout.putConstraint(SpringLayout.NORTH, labelNotes, 10, SpringLayout.SOUTH, labelTags);
            layout.putConstraint(SpringLayout.WEST, labelNotes, 5, SpringLayout.WEST, this);

            layout.putConstraint(SpringLayout.NORTH, scrollNotes, 5, SpringLayout.SOUTH, labelTags);
            layout.putConstraint(SpringLayout.WEST, scrollNotes, 0, SpringLayout.WEST, txtTags);
            layout.putConstraint(SpringLayout.EAST, scrollNotes, -100, SpringLayout.EAST, this);
            layout.putConstraint(SpringLayout.SOUTH, scrollNotes, 50, SpringLayout.SOUTH, labelTags);

            layout.putConstraint(SpringLayout.NORTH, labelURL, 5, SpringLayout.SOUTH, scrollNotes);
            layout.putConstraint(SpringLayout.WEST, labelURL, 5, SpringLayout.WEST, this);

            layout.putConstraint(SpringLayout.NORTH, scrollURLs, 0, SpringLayout.NORTH, labelURL);
            layout.putConstraint(SpringLayout.WEST, scrollURLs, 0, SpringLayout.WEST, scrollNotes);
            layout.putConstraint(SpringLayout.EAST, scrollURLs, -100, SpringLayout.EAST, this);
            layout.putConstraint(SpringLayout.SOUTH, scrollURLs, 50, SpringLayout.SOUTH, labelURL);

            layout.putConstraint(SpringLayout.NORTH, labelGroups, 10, SpringLayout.SOUTH, scrollURLs);
            layout.putConstraint(SpringLayout.WEST, labelGroups, 5, SpringLayout.WEST, this);

            layout.putConstraint(SpringLayout.NORTH, scrollMemberGroups, 5, SpringLayout.SOUTH, labelGroups);
            layout.putConstraint(SpringLayout.WEST, scrollMemberGroups, 0, SpringLayout.WEST, scrollURLs);
            layout.putConstraint(SpringLayout.EAST, scrollMemberGroups, 170, SpringLayout.WEST, scrollURLs);
            layout.putConstraint(SpringLayout.SOUTH, scrollMemberGroups, 100, SpringLayout.SOUTH, labelGroups);

            layout.putConstraint(SpringLayout.NORTH, scrollAvailableGroups, 5, SpringLayout.SOUTH, labelGroups);
            layout.putConstraint(SpringLayout.WEST, scrollAvailableGroups, 40, SpringLayout.EAST, scrollMemberGroups);
            layout.putConstraint(SpringLayout.EAST, scrollAvailableGroups, 210, SpringLayout.EAST, scrollMemberGroups);
            layout.putConstraint(SpringLayout.SOUTH, scrollAvailableGroups, 100, SpringLayout.SOUTH, labelGroups);

            layout.putConstraint(SpringLayout.NORTH, labelMemberGroups, 5, SpringLayout.NORTH, labelGroups);
            layout.putConstraint(SpringLayout.WEST, labelMemberGroups, 0, SpringLayout.WEST, scrollMemberGroups);

            layout.putConstraint(SpringLayout.NORTH, labelAvailableGroups, 5, SpringLayout.NORTH, labelGroups);
            layout.putConstraint(SpringLayout.WEST, labelAvailableGroups, 0, SpringLayout.WEST, scrollAvailableGroups);

            layout.putConstraint(SpringLayout.NORTH, labelInfo, 5, SpringLayout.SOUTH, scrollMemberGroups);
            layout.putConstraint(SpringLayout.WEST, labelInfo, 0, SpringLayout.WEST, scrollMemberGroups);

            layout.putConstraint(SpringLayout.NORTH, labelInfoGroup, 5, SpringLayout.SOUTH, scrollMemberGroups);
            layout.putConstraint(SpringLayout.WEST, labelInfoGroup, 15, SpringLayout.EAST, labelInfo);

            layout.putConstraint(SpringLayout.NORTH, saveButton, 20, SpringLayout.SOUTH, labelInfo);
            layout.putConstraint(SpringLayout.WEST, saveButton, 5, SpringLayout.WEST, this);

            layout.putConstraint(SpringLayout.NORTH, cancelButton, 0, SpringLayout.NORTH, saveButton);
            layout.putConstraint(SpringLayout.WEST, cancelButton, 10, SpringLayout.EAST, saveButton);

            fillUrls();
            fillTags();
            fillNotes();
            fillAvailableGroups();
            fillMemberGroups();
        }


        private void fillAvailableGroups() {
            aivailableLm = new DefaultListModel();

            for (Group g : ed.getGroups()) {
                aivailableLm.addElement(g);
            }
            availableGroups.setModel(aivailableLm);
            availableGroups.addMouseListener(new ListEventHandler());

        }


        private void fillTags() {
            TermWithWeightList tagList = (TermWithWeightList) ed.getDocument().getFieldValue(Field.TAGS);

            if (tagList != null) {
                StringBuilder sb = new StringBuilder();
                String seperator = "";

                for (TermWithWeight t : tagList) {
                    sb.append(seperator);
                    seperator = " ";
                    sb.append(t.getTerm());
                }
                txtTags.setText(sb.toString());
            }
        }


        private void fillUrls() {
            URLList ul = (URLList) ed.getDocument().getFieldValue(Field.URLS);
            StringBuilder sb = new StringBuilder();
            String seperator = "";
            if (ul != null) {
                for (URL u : ul) {
                    sb.append(seperator);
                    seperator = "\n";
                    sb.append(u.toString());
                }
                txtURL.setText(sb.toString());
            }

        }


        private void fillNotes() {
            String notes = (String) ed.getDocument().getFieldValue(Field.NOTE);
            if (notes != null) {
                txtNotes.setText(notes);
            }
        }


        private void fillMemberGroups() {
            GroupList groupList = (GroupList) ed.getDocument().getFieldValue(Field.GROUPS);
            memberLm = new DefaultListModel();

            if (groupList != null) {
                for (Group g : groupList) {
                    memberLm.addElement(g);
                }
            }

            memberGroups.setModel(memberLm);
            memberGroups.addMouseListener(new ListEventHandler());
        }


        private void handleAddGroup() {
            if (!memberLm.contains(availableGroups.getSelectedValue())) {
                Group selGroup = (Group) availableGroups.getSelectedValue();
                boolean add = true;

                if (selGroup.onlineGroup()) {
                    // Look if document not member of other onlinegroup
                    Enumeration<?> e = memberLm.elements();
                    while (e.hasMoreElements()) {
                        Group g = (Group) e.nextElement();
                        if (g.onlineGroup()) {
                            add = false;
                            break;
                        }
                    }
                }
                if (add) {
                    memberLm.addElement(availableGroups.getSelectedValue());
                }

            }
        }


        private void handleRemoveGroup() {
            memberLm.removeElement(memberGroups.getSelectedValue());
        }


        /** Eventhandler for List */
        private class ListEventHandler extends MouseAdapter implements KeyListener {

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DELETE) {

                }
            }


            @Override
            public void mouseClicked(MouseEvent e) {

                if (e.getClickCount() == 2) {
                    if (e.getSource() == availableGroups) {
                        handleAddGroup();
                    }
                    if (e.getSource() == memberGroups) {
                        handleRemoveGroup();
                        // on double click do default action
                    }
                }
            }


            @Override
            public void keyReleased(KeyEvent e) {
            }


            @Override
            public void keyTyped(KeyEvent e) {
            }

        }

    }

}
