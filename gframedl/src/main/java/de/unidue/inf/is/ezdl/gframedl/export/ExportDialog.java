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

package de.unidue.inf.is.ezdl.gframedl.export;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.WindowConstants;

import de.unidue.inf.is.ezdl.dlcore.data.dldata.TextDocument;
import de.unidue.inf.is.ezdl.dlfrontend.converter.BibTexConversionStrategy;
import de.unidue.inf.is.ezdl.dlfrontend.converter.DocumentConversionStrategy;
import de.unidue.inf.is.ezdl.dlfrontend.converter.ExportResult;
import de.unidue.inf.is.ezdl.dlfrontend.converter.ExportResultText;
import de.unidue.inf.is.ezdl.dlfrontend.converter.RISConversionStrategy;
import de.unidue.inf.is.ezdl.dlfrontend.i18n.I18nSupport;
import de.unidue.inf.is.ezdl.gframedl.converter.HTMLConversionStrategy;



/**
 * The export dialog that allows to export lists of DLObjects into different
 * formats either to system clipboard or to a file on the local file system.
 * 
 * @author mjordan
 */
public final class ExportDialog extends JDialog {

    private static final int DIALOG_WIDTH = 600;
    private static final int DIALOG_HEIGHT = 300;

    private static final long serialVersionUID = 8847559227645475600L;

    /**
     * The title of the export tool.
     */
    private static final String TITLE = I18nSupport.getInstance().getLocString("ezdl.tools.export.name");
    /**
     * The message that is used to tell the user that a preview is unavailable
     * due to the data being binary.
     */
    private static final String BINARY = I18nSupport.getInstance().getLocString("ezdl.tools.export.message.binary");
    /**
     * The label used for the export format selection ComboBox.
     */
    private static final String TYPE_SELECTION_LABEL = I18nSupport.getInstance().getLocString("ezdl.tools.export.type");

    /**
     * The objects to export.
     */
    private List<Object> exportObjects;
    /**
     * The ComboBox for the export type selection.
     */
    private JComboBox typeSelection;
    /**
     * The preview pane.
     */
    private JTextPane preview;
    /**
     * The conversion result. This is updated by the action of the ComboBox on
     * initialization and each time the user changes the selection.
     */
    private ExportResult result;


    /**
     * The available export formats.
     * 
     * @author mjordan
     */
    private enum Export {
        RIS(I18nSupport.getInstance().getLocString("ezdl.formats.ris"), new RISConversionStrategy()), //
        BIBTEX(I18nSupport.getInstance().getLocString("ezdl.formats.bibtex"), new BibTexConversionStrategy()), //
        HTML(I18nSupport.getInstance().getLocString("ezdl.formats.html"), new HTMLConversionStrategy());

        private String label;
        private DocumentConversionStrategy strategy;


        /**
         * Creates a new export format definition.
         * 
         * @param label
         *            the label to use
         * @param strategy
         *            the strategy
         */
        Export(String label, DocumentConversionStrategy strategy) {
            this.label = label;
            this.strategy = strategy;
        }


        static Export forLabel(String label) {
            for (Export export : values()) {
                if (export.label.equals(label)) {
                    return export;
                }
            }
            return null;
        }
    };


    /**
     * Creates the dialog for the given documents.
     * 
     * @param documents
     *            the documents to export
     * @param owner
     *            the owning {@link Window}
     */
    public ExportDialog(List<Object> documents, Window owner) {
        super(owner);
        this.exportObjects = documents;

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(DIALOG_WIDTH, DIALOG_HEIGHT);
        setTitle(TITLE);
        setModal(true);

        setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.weighty = 0;
        add(getFormatSelector(), c);

        JScrollPane previewScrollPane = new JScrollPane(getPreview());
        c.gridx = 0;
        c.gridy = 1;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 1;
        add(previewScrollPane, c);

        JPanel p = new JPanel();
        p.setLayout(new FlowLayout(FlowLayout.RIGHT));
        p.add(getCopyButton());
        p.add(getSaveButton());

        c.gridx = 0;
        c.gridy = 3;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.weighty = 0;
        add(p, c);

        handleTypeSelectionChanged();

        setLocationRelativeTo(null);
        setVisible(true);
    }


    private Component getFormatSelector() {
        Component out = null;
        if (typeSelection == null) {
            typeSelection = new JComboBox();
            for (Export export : Export.values()) {
                typeSelection.addItem(export.label);
            }
            typeSelection.setAction(new AbstractAction() {

                private static final long serialVersionUID = -7545409192309295397L;


                @Override
                public void actionPerformed(ActionEvent e) {
                    handleTypeSelectionChanged();
                }
            });
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            panel.add(new JLabel(TYPE_SELECTION_LABEL));
            panel.add(typeSelection);
            out = panel;
        }

        return out;
    }


    private Component getPreview() {
        if (preview == null) {
            preview = new JTextPane();
            preview.setSize(200, 100);
        }
        return preview;
    }


    private Component getCopyButton() {
        JButton copy = new JButton();
        copy.setSize(50, 10);
        copy.setAction(new CopyAction(this));
        return copy;
    }


    private Component getSaveButton() {
        JButton save = new JButton();
        save.setSize(50, 10);
        save.setAction(new SaveAction(this));
        return save;
    }


    /**
     * Closes the export dialog.
     */
    void close() {
        setVisible(false);
        dispose();
    }


    private void handleTypeSelectionChanged() {
        result = exportDocuments();

        if (result.isBinary()) {
            preview.setText(BINARY);
            preview.setEditable(false);
        }
        else {
            preview.setText(result.asString());
            preview.setEditable(true);
        }
        preview.setCaretPosition(0);
    }


    private ExportResult exportDocuments() {
        ExportResult result = null;
        final String selectedItem = (String) typeSelection.getSelectedItem();
        final Export export = Export.forLabel(selectedItem);
        if (export != null) {
            LinkedList<TextDocument> tds = new LinkedList<TextDocument>();
            for (Object object : exportObjects) {
                if (object instanceof TextDocument) {
                    tds.add((TextDocument) object);
                }
            }
            result = export.strategy.print(tds);
        }
        return result;
    }


    /**
     * Returns the documents, extracted into the currently chosen format.
     * 
     * @return the extraction results
     */
    ExportResult results() {
        return result.isBinary() ? result : new ExportResultText(preview.getText());
    }
}
