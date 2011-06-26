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

package de.unidue.inf.is.ezdl.gframedl;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import de.unidue.inf.is.ezdl.dlcore.message.content.TextMessageNotify;
import de.unidue.inf.is.ezdl.dlfrontend.i18n.I18nSupport;



/**
 * A class to show a list of messages of the day. It is also possible to cancel
 * special kinds of messages like INFO messages.
 * 
 * @author markus
 */
public final class MOTD extends JDialog implements ActionListener, ItemListener {

    private static final long serialVersionUID = 1L;

    private static final String MESSAGE_TYPE = "WARN";

    private JPanel jContentPane = null;
    private JEditorPane messageArea = null;
    private JPanel buttonPanel = null;
    private JButton okButton = null;
    private JButton nextButton = null;
    private JButton backButton = null;
    private JPanel controlPanel = null;
    private JPanel splitPicturePanel = null;
    private JScrollPane messageScroll = null;
    private JCheckBox cancelCheckBox = null;
    private JLabel pictureLabel = null;
    private I18nSupport i18n = I18nSupport.getInstance();
    private List<JButton> buttonList = null;
    private List<TextMessageNotify> messageList;
    private Integer listPosition = -1;
    private boolean isWarn = false;


    /**
     * A constructor which get a list of TextMessageNotify objects.
     * 
     * @param parent
     *            The parent window.
     * @param messageList
     *            A list of all messages to show.
     */
    public MOTD(List<TextMessageNotify> messageList, Window parent) {
        super(parent);

        this.messageList = messageList;
        buttonList = new ArrayList<JButton>();
        messageListLookup(true);
        if (listPosition != -1) {
            String content = messageList.get(listPosition).getContent();
            String type = messageList.get(listPosition).getPriority().toString();
            String titel = messageList.get(listPosition).getTitle();
            if (content != null && type != null && titel != null) {
                initialize(content, type, titel);
            }
        }
    }


    /**
     * This method initializes the frame and makes him visible.
     * 
     * @param message
     *            The first message to show
     * @param type
     *            The type (Priority) of the message
     * @param titel
     *            The title of the message
     */
    private void initialize(String message, String type, String titel) {
        // setModalityType(ModalityType.APPLICATION_MODAL);
        setSize(393, 312);
        setContentPane(getJContentPane(message, type, titel));
        setTitle(i18n.getLocString("ezdl.motd.title"));
        maxButtonSize();
        disableButtons();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        // getRootPane().setDefaultButton(okButton);
        setVisible(true);
    }


    /**
     * This method initializes the ContentPane of the whole frame and add sub
     * panels.
     * 
     * @return The ContentPane of the frame.
     */
    private JPanel getJContentPane(String message, String type, String titel) {
        if (jContentPane == null) {
            BorderLayout borderLayout = new BorderLayout();
            jContentPane = new JPanel();
            jContentPane.setLayout(borderLayout);
            jContentPane.add(getSplitPicturePanel(message, type, titel), BorderLayout.CENTER);
            jContentPane.add(getButtonPanel(), BorderLayout.SOUTH);

        }
        return jContentPane;
    }


    /**
     * This method initializes a JEditorPane which contains the message.
     * 
     * @return A JEditorPane with the message.
     */
    private JEditorPane getMessageArea(String message, String titel) {
        if (messageArea == null) {
            messageArea = new JEditorPane();
            messageArea.setContentType("text/html");

            setMessage(message, titel);
            messageArea.setEditable(false);
            messageArea.setBackground(new JLabel().getBackground());
            messageArea.setCaretPosition(0);
        }
        return messageArea;
    }


    /**
     * A setter method to set the content of the message in a JEditorPane.
     * 
     * @param message
     *            The message to set
     * @param titel
     *            The title of the message
     */
    private void setMessage(String message, String titel) {
        if (messageArea != null) {
            messageArea.setSize(0, 0);
            messageArea.setText(createHTMLConetent(message, titel));
        }
    }


    /**
     * This method initializes a sub panel which contains all buttons.
     * 
     * @return The button panel
     */
    private JPanel getButtonPanel() {
        if (buttonPanel == null) {
            FlowLayout flowLayout = new FlowLayout();
            flowLayout.setHgap(40);
            buttonPanel = new JPanel();
            buttonPanel.setLayout(flowLayout);
            buttonPanel.add(getControlPanel(), null);
        }
        return buttonPanel;
    }


    /**
     * This method initializes the OK button to close the dialog.
     * 
     * @return The OK button.
     */
    private JButton getOKButton() {
        if (okButton == null) {
            okButton = new JButton();
            okButton.setText(i18n.getLocString("ezdl.motd.ok"));
            okButton.addActionListener(this);
            buttonList.add(okButton);

        }
        return okButton;
    }


    /**
     * This method initializes the next button. With this button a user can
     * select the next message entry.
     * 
     * @return The next button.
     */
    private JButton getNextButton() {
        if (nextButton == null) {
            nextButton = new JButton(Icons.TURN_OVER_ACTION_RIGHT.get16x16());
            nextButton.setText(i18n.getLocString("ezdl.motd.next"));
            nextButton.setHorizontalTextPosition(SwingConstants.LEFT);
            nextButton.addActionListener(this);
            buttonList.add(nextButton);
        }
        return nextButton;
    }


    /**
     * This method initializes the previous button. With this button a user can
     * select a previous message entry.
     * 
     * @return The previous button.
     */
    private JButton getBackButton() {
        if (backButton == null) {
            backButton = new JButton(Icons.TURN_OVER_ACTION_LEFT.get16x16());
            backButton.setText(i18n.getLocString("ezdl.motd.previous"));
            backButton.addActionListener(this);
            buttonList.add(backButton);
        }
        return backButton;
    }


    /**
     * A getter for a panel which contains the messageArea. These panel make it
     * possible to scroll.
     * 
     * @param message
     *            The content of the message
     * @param titel
     *            The title of the message
     * @return A panel with messageArea
     */
    private JScrollPane getMessageScroll(String message, String titel) {
        if (messageScroll == null) {
            messageScroll = new JScrollPane();
            messageScroll.setViewportView(getMessageArea(message, titel));

        }
        return messageScroll;
    }


    /**
     * This method initializes a panel which contains the control elements like
     * buttons or check boxes.
     * 
     * @return The panel with content.
     */
    private JPanel getControlPanel() {
        if (controlPanel == null) {
            controlPanel = new JPanel();
            controlPanel.setLayout(new GridBagLayout());
            GridBagConstraints gridBagBackConstraints = new GridBagConstraints();
            gridBagBackConstraints.gridx = 0;
            gridBagBackConstraints.insets = new Insets(0, 0, 0, 20);
            gridBagBackConstraints.gridy = 1;
            GridBagConstraints gridBagOKConstraints = new GridBagConstraints();
            gridBagOKConstraints.gridx = 1;
            gridBagOKConstraints.insets = new Insets(0, 0, 0, 20);
            gridBagOKConstraints.gridy = 1;
            GridBagConstraints gridBagNextConstraints = new GridBagConstraints();
            gridBagNextConstraints.gridx = 2;
            gridBagNextConstraints.insets = new Insets(0, 0, 0, 0);
            gridBagNextConstraints.gridy = 1;
            GridBagConstraints gridBagCheckConstraints = new GridBagConstraints();
            gridBagCheckConstraints.gridx = 0;
            gridBagCheckConstraints.gridwidth = 2;
            gridBagCheckConstraints.anchor = GridBagConstraints.SOUTHWEST;
            gridBagCheckConstraints.insets = new Insets(0, 0, 20, 0);
            gridBagCheckConstraints.gridy = 0;
            controlPanel.add(getCancelCheckBox(), gridBagCheckConstraints);
            controlPanel.add(getBackButton(), gridBagBackConstraints);
            controlPanel.add(getOKButton(), gridBagOKConstraints);
            controlPanel.add(getNextButton(), gridBagNextConstraints);
        }
        return controlPanel;
    }


    /**
     * This method initializes a CheckBox for canceling info messages.
     * 
     * @return The cancelChekBox
     */
    private JCheckBox getCancelCheckBox() {
        if (cancelCheckBox == null) {
            cancelCheckBox = new JCheckBox(i18n.getLocString("ezdl.motd.messageCancel"));
            cancelCheckBox.addItemListener(this);
            cancelCheckBox.setSelected(Config.getInstance().getUserPropertyAsBoolean("MOTD", false));
        }
        return cancelCheckBox;
    }


    /**
     * A Getter for the label which contain the warning icon.
     * 
     * @return A label with icon.
     */
    private JLabel getPictureLabel() {
        if (pictureLabel == null) {
            Icon icon = UIManager.getIcon("OptionPane.warningIcon");
            pictureLabel = new JLabel(icon);
        }
        return pictureLabel;
    }


    /**
     * A method to generate the content for the panel which contains the warning
     * label and the text container component.
     * 
     * @param message
     *            The content of the message
     * @param type
     *            The type (Priority) of the message
     * @param titel
     *            The title of the message
     * @return The panel which shows the content
     */
    private JPanel getSplitPicturePanel(String message, String type, String titel) {
        if (splitPicturePanel == null) {
            splitPicturePanel = new JPanel();
            GridBagConstraints messageConstraints = new GridBagConstraints();
            messageConstraints.fill = GridBagConstraints.BOTH;
            messageConstraints.gridy = 0;
            messageConstraints.weightx = 1.0;
            messageConstraints.weighty = 1.0;
            messageConstraints.gridx = 1;
            messageConstraints.insets = new Insets(20, 20, 20, 20);
            GridBagConstraints pictureConstraints = new GridBagConstraints();
            pictureConstraints.gridx = 0;
            pictureConstraints.gridy = 0;
            pictureConstraints.insets = new Insets(20, 20, 20, 20);
            splitPicturePanel.setLayout(new GridBagLayout());
            splitPicturePanel.add(getPictureLabel(), pictureConstraints);
            getPictureLabel().setVisible(isWarn);
            splitPicturePanel.add(getMessageScroll(message, titel), messageConstraints);
        }
        return splitPicturePanel;

    }


    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == getOKButton()) {
            this.dispose();
        }

        if (source == getNextButton() && listPosition < this.messageList.size() - 1) {
            messageListLookup(true);
            disableButtons();
            updateGUI();
            return;
        }

        if (source == getBackButton() && listPosition >= 1) {
            messageListLookup(false);
            disableButtons();
            updateGUI();
            return;
        }

    }


    @Override
    public void itemStateChanged(ItemEvent e) {
        Object source = e.getItemSelectable();

        if (source == getCancelCheckBox()) {
            Config.getInstance().setUserProperty("MOTD", getCancelCheckBox().isSelected());
            Config.getInstance().writeUserPreferences();
        }
    }


    /**
     * A method to scale all buttons to the same size.
     */
    private void maxButtonSize() {
        Dimension maxButtonDimension = new Dimension(0, 0);
        for (JButton button : buttonList) {
            if (button.getPreferredSize().getWidth() > maxButtonDimension.getWidth()) {
                maxButtonDimension = button.getPreferredSize();
            }
        }
        for (JButton button : buttonList) {
            button.setPreferredSize(maxButtonDimension);
        }

    }


    /**
     * A method to disable buttons if the edges of the message list is reached.
     */
    private void disableButtons() {
        this.getBackButton().setEnabled(false);
        this.getNextButton().setEnabled(false);
        if (getCancelCheckBox().isSelected()) {
            for (int i = listPosition + 1; i < messageList.size(); i++) {
                if (messageList.get(i).getPriority().toString().equals(MESSAGE_TYPE)) {
                    this.getNextButton().setEnabled(true);

                }
            }
            for (int i = listPosition - 1; i >= 0; i--) {
                if (messageList.get(i).getPriority().toString().equals(MESSAGE_TYPE)) {
                    this.getBackButton().setEnabled(true);

                }
            }

        }
        else {
            if (!(listPosition == 0)) {
                this.getBackButton().setEnabled(true);
            }
            if (!(listPosition == messageList.size() - 1)) {
                this.getNextButton().setEnabled(true);
            }
        }
    }


    /**
     * A method to repaint the GUI.
     */
    private void updateGUI() {
        warnCheck(messageList.get(listPosition).getPriority().toString());
        clearLabel(isWarn);

        String content = messageList.get(listPosition).getContent();
        String type = messageList.get(listPosition).getPriority().toString();
        String titel = messageList.get(listPosition).getTitle();
        if (content != null && type != null && titel != null) {
            setMessage(messageList.get(listPosition).getContent(), messageList.get(listPosition).getTitle());
            this.repaint();
        }

    }


    /**
     * A method to verify whether it is a warning message.
     * 
     * @param type
     *            Type of the message (Priority).
     * @return The result of comparison
     */
    private boolean warnCheck(String type) {
        if (type.equals(MESSAGE_TYPE)) {
            isWarn = true;
            return true;
        }
        isWarn = false;
        return false;
    }


    /**
     * A method to hide the warn label, if the message is only a info message.
     * 
     * @param type
     *            A parameter to specify whether the label is hidden. If false
     *            hide the label.
     */
    private void clearLabel(boolean type) {
        getPictureLabel().setVisible(type);
    }


    /**
     * A method to generate a HTML statement which contains all relevant
     * informations of one message.
     * 
     * @param message
     *            The content of the Message
     * @param titel
     *            The title of the Message
     * @return A HTML statement
     */

    private String createHTMLConetent(String message, String titel) {
        StringBuilder tempString = new StringBuilder();
        tempString.append("<html><head></head><body>");
        // tempString.append("<div  style=\"");
        // tempString.append("width:").append(messageArea.getSize().getWidth()).append("px; ");
        // tempString.append("height:").append(messageArea.getSize().getHeight()).append("px;");
        // tempString.append("\">");
        tempString.append("<h3 align=\"center\">");
        tempString.append(titel);
        tempString.append("</h3>");
        tempString.append(format(message));
        // tempString.append("</div>");
        tempString.append("</body></html>");
        return tempString.toString();
    }


    /**
     * Wraps paragraphs in plain text with &lt;p&gt; tags for nicer display in
     * HTML.
     * 
     * @param text
     *            the text to wrap
     * @return the wrapped text
     */
    private String format(String text) {
        if (text.startsWith("<")) {
            return text;
        }

        text = text.replaceAll("\n\n", "</p><p>");
        text = "<p>" + text + "</p>";
        return text;
    }


    /**
     * A Method to find the next relevant entry of the message list. Information
     * messages are filtered out in need.
     * 
     * @param runningDirection
     *            Search direction of the next entry. True for the next entry
     *            and false for the previous entry.
     */
    private void messageListLookup(boolean runningDirection) {
        if (runningDirection) {
            for (int i = listPosition + 1; i < messageList.size(); i++) {
                if (warnCheck(messageList.get(i).getPriority().toString())
                                || !Config.getInstance().getUserPropertyAsBoolean("MOTD", false)) {
                    listPosition = i;
                    return;
                }
            }

        }
        else {
            for (int i = listPosition - 1; i >= 0; i--) {
                if (warnCheck(messageList.get(i).getPriority().toString())
                                || !Config.getInstance().getUserPropertyAsBoolean("MOTD", false)) {

                    listPosition = i;
                    return;
                }
            }
        }
    }

}
