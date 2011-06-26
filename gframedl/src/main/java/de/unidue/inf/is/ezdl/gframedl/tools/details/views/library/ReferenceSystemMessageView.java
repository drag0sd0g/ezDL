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

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.List;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import org.apache.log4j.Logger;

import de.unidue.inf.is.ezdl.dlcore.data.dldata.DLObject;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.library.ReferenceSystemMessage;
import de.unidue.inf.is.ezdl.dlfrontend.dispatcher.Dispatcher;
import de.unidue.inf.is.ezdl.dlfrontend.i18n.I18nSupport;
import de.unidue.inf.is.ezdl.gframedl.Icons;
import de.unidue.inf.is.ezdl.gframedl.components.TextComponentPopupMenu;
import de.unidue.inf.is.ezdl.gframedl.events.LibraryEvent;
import de.unidue.inf.is.ezdl.gframedl.tools.details.DetailView;
import de.unidue.inf.is.ezdl.gframedl.tools.library.LibraryTool;



/** Shows a message from the onilne reference system */
public class ReferenceSystemMessageView extends JScrollPane implements DetailView {

    private static final long serialVersionUID = 5500102266067722699L;

    private Logger logger = Logger.getLogger(LibraryTool.class);

    private DLObject object;
    private ReferenceSystemMessagePanel refPanel;


    public ReferenceSystemMessageView() {
        super();
    }


    @Override
    public void setObject(DLObject o, List<String> l) {

        object = o;
        if (o != null) {
            setReferenceSystemMessage();
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


    private void setReferenceSystemMessage() {

        ReferenceSystemMessage rsm = (ReferenceSystemMessage) getObject();
        refPanel = new ReferenceSystemMessagePanel(rsm);
        setViewportView(refPanel);
        refPanel.repaint();
        refPanel.revalidate();
    }


    @Override
    public List<Action> getPossibleActions() {
        return null;
    }


    /** Reset view */
    private void resetView() {
        refPanel.removeAll();
        this.repaint();
        this.revalidate();
        object = null;
    }


    /** submit verifier button was pressed */
    private void submitCode() {
        ReferenceSystemMessage rsm = (ReferenceSystemMessage) getObject();
        rsm.getParameters().put("verifier", refPanel.txtVerifier.getText());
        LibraryEvent libraryEvent = new LibraryEvent(this, rsm, LibraryEvent.SEND_VERIFIER);
        Dispatcher.postEvent(libraryEvent);

        resetView();
    }


    /** Work offline button was pressed */
    private void workOffline() {
        ReferenceSystemMessage rsm = (ReferenceSystemMessage) getObject();
        LibraryEvent libraryEvent = new LibraryEvent(this, rsm, LibraryEvent.WORK_OFFLINE);
        Dispatcher.postEvent(libraryEvent);
        resetView();
    }


    /** Choose reference system button was pressed */
    private void chooseReferenceSystem() {
        ReferenceSystemMessage rsm = (ReferenceSystemMessage) getObject();
        LibraryEvent libraryEvent = new LibraryEvent(this, rsm, LibraryEvent.CHOOSE_REFERENCESYSTEM);
        Dispatcher.postEvent(libraryEvent);
        resetView();
    }


    private class ReferenceSystemMessagePanel extends JPanel {

        private static final long serialVersionUID = -4295549765776831221L;
        private ReferenceSystemMessage rsm;

        private SpringLayout layout;
        private JLabel labelMessage, labelHeadline, labelNoBrowser;
        private JTextField txtVerifier;
        private JButton submitButton, offlineButton, referenceSystemButton;
        private JTextArea textArea, txtURL;


        public ReferenceSystemMessagePanel(ReferenceSystemMessage rsm) {
            super();
            this.rsm = rsm;

            layout = new SpringLayout();
            this.setLayout(layout);

            /**
             * User action required. for example show authentication URL from
             * Mendeley
             */
            if (rsm.getType() == ReferenceSystemMessage.MENDELEY_USER_ACTION_REQUIRED) {
                createUserActionMendeleyPanel();
            }
            else if (rsm.getType() == ReferenceSystemMessage.NO_REFERENCE_SYSTEM_INFO) {
                createNoReferenceSystemPanel();
            }
            else {
                // Show error
                JLabel labelError = new JLabel("Error");
                labelError.setFont(new java.awt.Font("Tahoma", 1, 18));
                textArea = new JTextArea();
                textArea.setText(rsm.getMessage() + "\n\n HTTPCode:" + rsm.getHttpCode() + "\n\n Description: "
                                + rsm.toString());
                // JLabel labelDescription = new JLabel(rsm.getMessage() +
                // "\n HTTPCode:" + rsm.getHttpCode() + "\n Description" +
                // rsm.toString());
                textArea.setEditable(false);
                textArea.setLineWrap(true);
                textArea.setWrapStyleWord(true);
                TextComponentPopupMenu.addPopupMenu(textArea);

                offlineButton = new JButton();
                offlineButton.setText(I18nSupport.getInstance().getLocString(
                                "ezdl.tools.library.referencesystem.offline"));
                offlineButton.setIcon(Icons.MEDIA_OFFLINEURL.get16x16());
                offlineButton.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        workOffline();
                    }
                });

                JScrollPane scrollpane = new JScrollPane(textArea);
                add(labelError);
                add(scrollpane);
                add(offlineButton);

                layout.putConstraint(SpringLayout.NORTH, labelError, 10, SpringLayout.NORTH, this);
                layout.putConstraint(SpringLayout.WEST, labelError, 5, SpringLayout.WEST, this);

                layout.putConstraint(SpringLayout.NORTH, scrollpane, 10, SpringLayout.SOUTH, labelError);
                layout.putConstraint(SpringLayout.WEST, scrollpane, 5, SpringLayout.WEST, this);
                layout.putConstraint(SpringLayout.EAST, scrollpane, -5, SpringLayout.EAST, this);
                layout.putConstraint(SpringLayout.SOUTH, scrollpane, -50, SpringLayout.SOUTH, this);

                layout.putConstraint(SpringLayout.NORTH, offlineButton, 10, SpringLayout.SOUTH, scrollpane);
                layout.putConstraint(SpringLayout.WEST, offlineButton, 5, SpringLayout.WEST, this);

            }

        }


        private void createUserActionMendeleyPanel() {
            labelHeadline = new JLabel(I18nSupport.getInstance().getLocString("ezdl.tools.library.mendeley.auth"));
            labelHeadline.setFont(new java.awt.Font("Tahoma", 1, 18));

            labelNoBrowser = new JLabel(I18nSupport.getInstance().getLocString("ezdl.tools.library.mendeley.nobrowser"));

            labelMessage = new JLabel(I18nSupport.getInstance().getLocString("ezdl.tools.library.mendeley.verifier"));
            txtVerifier = new JTextField();
            TextComponentPopupMenu.addPopupMenu(txtVerifier);
            txtURL = new JTextArea();
            TextComponentPopupMenu.addPopupMenu(txtURL);
            txtURL.setText(rsm.getUrl());
            txtURL.setEditable(false);
            txtURL.setLineWrap(true);

            try {
                Desktop desktop = Desktop.getDesktop();
                URL url = new URL(rsm.getUrl());
                desktop.browse(url.toURI());
            }
            catch (Exception e) {
                logger.error(e.getMessage(), e);
            }

            submitButton = new JButton();
            submitButton.setText(I18nSupport.getInstance().getLocString("ezdl.actions.save"));
            submitButton.setIcon(Icons.SAVE.get16x16());
            submitButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    submitCode();
                }
            });

            add(labelHeadline);
            add(labelMessage);
            add(txtVerifier);
            add(labelNoBrowser);
            add(txtURL);
            add(submitButton);

            layout.putConstraint(SpringLayout.NORTH, labelHeadline, 10, SpringLayout.NORTH, this);
            layout.putConstraint(SpringLayout.WEST, labelHeadline, 5, SpringLayout.WEST, this);

            layout.putConstraint(SpringLayout.NORTH, labelMessage, 10, SpringLayout.SOUTH, labelHeadline);
            layout.putConstraint(SpringLayout.WEST, labelMessage, 5, SpringLayout.WEST, this);

            layout.putConstraint(SpringLayout.NORTH, txtVerifier, 10, SpringLayout.SOUTH, labelMessage);
            layout.putConstraint(SpringLayout.WEST, txtVerifier, 5, SpringLayout.WEST, this);
            layout.putConstraint(SpringLayout.EAST, txtVerifier, 150, SpringLayout.WEST, this);

            layout.putConstraint(SpringLayout.NORTH, labelNoBrowser, 10, SpringLayout.SOUTH, txtVerifier);
            layout.putConstraint(SpringLayout.WEST, labelNoBrowser, 5, SpringLayout.WEST, this);

            layout.putConstraint(SpringLayout.NORTH, txtURL, 10, SpringLayout.SOUTH, labelNoBrowser);
            layout.putConstraint(SpringLayout.WEST, txtURL, 5, SpringLayout.WEST, this);
            layout.putConstraint(SpringLayout.EAST, txtURL, -20, SpringLayout.EAST, this);
            layout.putConstraint(SpringLayout.SOUTH, txtURL, 70, SpringLayout.SOUTH, labelNoBrowser);

            layout.putConstraint(SpringLayout.NORTH, submitButton, 10, SpringLayout.SOUTH, labelMessage);
            layout.putConstraint(SpringLayout.WEST, submitButton, 10, SpringLayout.EAST, txtVerifier);
        }


        /** Information that there is no referencesystem is selected */
        private void createNoReferenceSystemPanel() {
            labelHeadline = new JLabel(I18nSupport.getInstance().getLocString("ezdl.tools.library.information"));
            labelHeadline.setFont(new java.awt.Font("Tahoma", 1, 18));

            labelMessage = new JLabel(I18nSupport.getInstance().getLocString("ezdl.tools.library.referencesystem.no"));

            offlineButton = new JButton();
            offlineButton.setText(I18nSupport.getInstance().getLocString("ezdl.tools.library.referencesystem.offline"));
            offlineButton.setIcon(Icons.MEDIA_OFFLINEURL.get16x16());
            offlineButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    workOffline();
                }
            });

            referenceSystemButton = new JButton();
            referenceSystemButton.setText(I18nSupport.getInstance().getLocString(
                            "ezdl.tools.library.referencesystem.no.choose"));
            referenceSystemButton.setIcon(Icons.MEDIA_URL.get16x16());
            referenceSystemButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    chooseReferenceSystem();
                }
            });

            add(labelHeadline);
            add(labelMessage);
            add(offlineButton);
            add(referenceSystemButton);

            layout.putConstraint(SpringLayout.NORTH, labelHeadline, 10, SpringLayout.NORTH, this);
            layout.putConstraint(SpringLayout.WEST, labelHeadline, 5, SpringLayout.WEST, this);

            layout.putConstraint(SpringLayout.NORTH, labelMessage, 10, SpringLayout.SOUTH, labelHeadline);
            layout.putConstraint(SpringLayout.WEST, labelMessage, 5, SpringLayout.WEST, this);

            layout.putConstraint(SpringLayout.NORTH, referenceSystemButton, 10, SpringLayout.SOUTH, labelMessage);
            layout.putConstraint(SpringLayout.WEST, referenceSystemButton, 5, SpringLayout.WEST, this);

            layout.putConstraint(SpringLayout.NORTH, offlineButton, 10, SpringLayout.SOUTH, labelMessage);
            layout.putConstraint(SpringLayout.WEST, offlineButton, 10, SpringLayout.EAST, referenceSystemButton);
        }
    }

}
