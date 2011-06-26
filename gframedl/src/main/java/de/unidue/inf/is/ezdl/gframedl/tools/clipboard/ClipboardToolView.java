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

package de.unidue.inf.is.ezdl.gframedl.tools.clipboard;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.List;

import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;

import de.unidue.inf.is.ezdl.dlcore.data.dldata.DLObject;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.Person;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.Term;
import de.unidue.inf.is.ezdl.dlfrontend.i18n.I18nSupport;
import de.unidue.inf.is.ezdl.gframedl.Icons;
import de.unidue.inf.is.ezdl.gframedl.components.TextComponentPopupMenu;
import de.unidue.inf.is.ezdl.gframedl.components.TextPrompt;
import de.unidue.inf.is.ezdl.gframedl.export.ExportAction;
import de.unidue.inf.is.ezdl.gframedl.tools.AbstractToolView;
import de.unidue.inf.is.ezdl.gframedl.tools.Tool;
import de.unidue.inf.is.ezdl.gframedl.tools.clipboard.actions.ClearClipboardAction;
import de.unidue.inf.is.ezdl.gframedl.tools.clipboard.actions.CreateObjectAction;
import de.unidue.inf.is.ezdl.gframedl.transfer.DefaultTextComponentTransferHandler;



public final class ClipboardToolView extends AbstractToolView {

    private static final long serialVersionUID = -8624018270224973444L;


    class TypeChooserRenderer extends DefaultListCellRenderer {

        private static final long serialVersionUID = -8524968831429363239L;


        public TypeChooserRenderer() {
            setOpaque(true);
        }


        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
                        boolean cellHasFocus) {
            JLabel jl = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            Icon image = null;
            if (i18n.getLocString("ezdl.objects.author").equals(value.toString())) {
                image = Icons.MEDIA_AUTHOR.get16x16();
            }
            else {
                image = Icons.MEDIA_TERM.get16x16();
            }
            if (image != null) {
                jl.setIcon(image);
            }
            jl.setText(value.toString());
            return jl;
        }

    }


    // event handler for text field
    private class TextBoxEventHandler extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                handleCreateObject();
            }
        }

    }


    private JButton addObjectButton;
    private JTextField addObjectEntryField;
    private JPanel addObjectPanel;
    private JPanel buttonPanel;
    private ClipboardListPanel clipboardListPanel;
    private I18nSupport i18n = I18nSupport.getInstance();
    private JComboBox typeChooser;


    public ClipboardToolView(Tool tool) {
        super(tool);
        createContent();
    }


    private void createContent() {
        setLayout(new BorderLayout());
        clipboardListPanel = new ClipboardListPanel(this);
        add(clipboardListPanel, BorderLayout.CENTER);
        add(getButtonPanel(), BorderLayout.SOUTH);
        add(getAddObjectPanel(), BorderLayout.NORTH);

        TextBoxEventHandler teh = new TextBoxEventHandler();

        getAddObjectEntryField().addKeyListener(teh);
        setEnabled(true);
    }


    public JTextField getAddObjectEntryField() {
        return addObjectEntryField;
    }


    protected JPanel getAddObjectPanel() {
        if (addObjectPanel == null) {
            addObjectPanel = new JPanel(new BorderLayout());
            addObjectButton = new JButton(new CreateObjectAction(this));
            addObjectPanel.add(addObjectButton, BorderLayout.EAST);

            addObjectPanel.add(getTypeChooser(), BorderLayout.WEST);

            addObjectEntryField = new JTextField();
            TextPrompt.addDefaultPrompt(i18n.getLocString("ezdl.tools.clipboard.addprompt"), addObjectEntryField);
            TextComponentPopupMenu.addPopupMenu(addObjectEntryField);
            addObjectEntryField.setTransferHandler(new DefaultTextComponentTransferHandler(addObjectEntryField));
            addObjectPanel.add(addObjectEntryField, BorderLayout.CENTER);

            JLabel addObjectPanelInfo = new JLabel();
            addObjectPanelInfo.setLabelFor(addObjectEntryField);
            addObjectPanelInfo.setText(i18n.getLocString("ezdl.tools.clipboard.addinfo"));
            addObjectPanel.add(addObjectPanelInfo, BorderLayout.NORTH);
        }
        return addObjectPanel;
    }


    protected String getAddObjectText() {
        return getAddObjectEntryField().getText();
    }


    protected String getAddObjectType() {
        return getTypeChooser().getSelectedItem().toString();
    }


    protected JPanel getButtonPanel() {
        if (buttonPanel == null) {
            buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

            buttonPanel.add(new JButton(new ClearClipboardAction(clipboardListPanel)));
            buttonPanel.add(new JButton(new ExportAction(clipboardListPanel)));
        }
        return buttonPanel;
    }


    protected JComboBox getTypeChooser() {
        if (typeChooser == null) {
            String types[] = {
                            i18n.getLocString("ezdl.objects.term"), i18n.getLocString("ezdl.objects.author")
            };
            typeChooser = new JComboBox(types);
            typeChooser.setRenderer(new TypeChooserRenderer());
            typeChooser.setSelectedIndex(0);
            typeChooser.setMaximumSize(new Dimension(16, 20));
        }
        return typeChooser;
    }


    /**
     * Clear text field for object creation.
     */
    protected void resetTextField() {
        getAddObjectEntryField().setText("");

        // fake a focus change to reset prompt
        dispatchEvent(new FocusEvent(getAddObjectEntryField(), FocusEvent.FOCUS_GAINED));
        dispatchEvent(new FocusEvent(getAddObjectEntryField(), FocusEvent.FOCUS_LOST));

    }


    /**
     * Create a new DataObject and add to listmodel.
     * 
     * @return
     */
    public boolean handleCreateObject() {
        String text = getAddObjectText();
        if (text.isEmpty()) {
            return false;
        }
        String type = getAddObjectType();
        DLObject dobj;
        if (I18nSupport.getInstance().getLocString("ezdl.objects.author").equals(type)) {
            dobj = new Person(text);
        }
        else {
            dobj = new Term(text);
        }
        resetTextField();
        return clipboardListPanel.handleAddObjects(Arrays.asList(dobj));
    }


    boolean handleAddObjects(List<DLObject> objects) {
        return clipboardListPanel.handleAddObjects(objects);
    }
}
