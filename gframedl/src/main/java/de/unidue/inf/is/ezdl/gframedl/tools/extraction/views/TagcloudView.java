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

package de.unidue.inf.is.ezdl.gframedl.tools.extraction.views;

import java.awt.Point;
import java.awt.datatransfer.Transferable;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JTextPane;
import javax.swing.TransferHandler;
import javax.swing.border.Border;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.Utilities;
import javax.swing.text.rtf.RTFEditorKit;

import de.unidue.inf.is.ezdl.dlcore.data.extractor.Entry;
import de.unidue.inf.is.ezdl.dlcore.data.extractor.ExtractionResult;
import de.unidue.inf.is.ezdl.dlfrontend.i18n.I18nSupport;
import de.unidue.inf.is.ezdl.gframedl.ToolController;
import de.unidue.inf.is.ezdl.gframedl.events.ExtractionEvent;
import de.unidue.inf.is.ezdl.gframedl.tools.extraction.ExtractionTool;
import de.unidue.inf.is.ezdl.gframedl.tools.extraction.ExtractionType;
import de.unidue.inf.is.ezdl.gframedl.transfer.DLObjectTransferable;



/**
 * A {@link TagcloudView} takes an {@link ExtractionEvent} and displays it in
 * form of a list of terms in alphabetical order with single elements scaled
 * according to their relative frequency.
 * 
 * @see AbstractExtractionView
 * @author tacke
 */
public final class TagcloudView extends AbstractExtractionView {

    private static final String ITEM_SEPARATOR = "  ";

    private static final long serialVersionUID = 3150759230843591912L;

    private static final int MAX_CLOUD_SIZE = 500;

    private JTextPane tagPanel = null;

    private DefaultStyledDocument doc = new DefaultStyledDocument();
    private SimpleAttributeSet defaultStyle = null;
    private SimpleAttributeSet tagStyle = null;

    private I18nSupport i18n = I18nSupport.getInstance();
    private String name = i18n.getLocString("ezdl.tools.extraction.views.tagcloud");;

    private Border defaultBorder = BorderFactory.createEmptyBorder(10, 15, 10, 15);


    public TagcloudView() {

        initialize(getTagPanel());
    }


    private void initialize(JComponent source) {

        setDoubleBuffered(true);
        getViewport().add(source);
    }


    private static void selectPhrase(JTextPane tagPanel, int offset) throws BadLocationException {

        if (customAreaSelected(tagPanel)) {
            return;
        }

        if (clickedInsideItemSeparator(tagPanel, offset)) {
            return;
        }

        int endoffset = tagPanel.getSelectionEnd();
        int startoffset = tagPanel.getSelectionStart();
        final int sepLen = ITEM_SEPARATOR.length();

        endoffset = Utilities.getWordEnd(tagPanel, offset);
        while (!tagPanel.getText(endoffset, sepLen).equals(ITEM_SEPARATOR)) {
            endoffset = Utilities.getWordEnd(tagPanel, endoffset + 1);
        }
        tagPanel.setSelectionEnd(endoffset);

        startoffset = Utilities.getWordStart(tagPanel, offset);
        while ((startoffset >= sepLen) && !tagPanel.getText(startoffset - sepLen, sepLen).equals(ITEM_SEPARATOR)) {
            startoffset = Utilities.getWordStart(tagPanel, startoffset - 1);
        }
        tagPanel.setSelectionStart(startoffset);
    }


    private static boolean clickedInsideItemSeparator(JTextPane tagPanel, int offset) throws BadLocationException {
        int tStart = Utilities.getWordStart(tagPanel, offset);
        int tEnd = Utilities.getWordEnd(tagPanel, offset);
        String tText = tagPanel.getText(tStart, tEnd - tStart);
        return ITEM_SEPARATOR.equals(tText);
    }


    private static boolean customAreaSelected(JTextPane tagPanel) {
        int endoffset = tagPanel.getSelectionEnd();
        int startoffset = tagPanel.getSelectionStart();
        return startoffset != endoffset;
    }


    private JTextPane getTagPanel() {
        if (tagPanel == null) {
            tagPanel = new JTextPane();
            tagPanel.setDragEnabled(true);
            tagPanel.setTransferHandler(new TagcloudviewItemTransferHandler());
            tagPanel.setEditable(false);
            tagPanel.setEditorKit(new RTFEditorKit());
            tagPanel.setDocument(doc);
            tagPanel.setBorder(defaultBorder);

            tagPanel.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseClicked(MouseEvent e) {

                    if (e.getClickCount() >= 1) {

                        // Select complete phrase on single click
                        try {
                            final Point lastPointClicked = new Point(e.getX(), e.getY());
                            final int offset = tagPanel.viewToModel(lastPointClicked);
                            selectPhrase(tagPanel, offset);

                        }
                        catch (Exception exc) {
                            exc.printStackTrace();
                        }
                    }

                    if (e.getClickCount() == 2) {
                        // TODO Super fancy double-click action
                    }
                    e.consume();
                }


                @Override
                public void mousePressed(MouseEvent e) {
                    e.consume();
                }


                @Override
                public void mouseReleased(MouseEvent e) {
                    e.consume();
                }
            });

        }
        return tagPanel;
    }


    @Override
    public void displayData(ExtractionResult result) {

        List<Entry> results = result.cloudData(10, 46);

        Collections.sort(results, new Comparator<Entry>() {

            @Override
            public int compare(Entry o1, Entry o2) {
                return o2.getValue().compareTo(o1.getValue());
            }

        });

        // Cut list at 120 elements
        int cutoff = (results.size() >= MAX_CLOUD_SIZE) ? MAX_CLOUD_SIZE : results.size();
        results = results.subList(0, cutoff);

        Collections.sort(results);

        try {
            doc.remove(0, doc.getLength());
        }
        catch (BadLocationException e) {
            // Ignore
        }

        for (Entry e : results) {
            String term = e.getKey();

            if (term.endsWith("null")) {
                term = term.substring(0, term.lastIndexOf("null"));
            }

            int fontSize = e.getValue();

            try {
                doc.insertString(doc.getLength(), term, getTagStyle(fontSize));
                doc.insertString(doc.getLength(), ITEM_SEPARATOR, getDefaultStyle());
            }
            catch (BadLocationException e1) {
                // Ignore
            }
        }
    }


    private SimpleAttributeSet getDefaultStyle() {
        if (defaultStyle == null) {
            defaultStyle = new SimpleAttributeSet();
        }
        return defaultStyle;
    }


    private SimpleAttributeSet getTagStyle(int fontSize) {
        if (tagStyle == null) {
            tagStyle = new SimpleAttributeSet();
            StyleConstants.setFontFamily(tagStyle, "lucida");
            StyleConstants.setBold(tagStyle, true);
            StyleConstants.setUnderline(tagStyle, true);
        }

        StyleConstants.setFontSize(tagStyle, fontSize);
        return tagStyle;
    }


    @Override
    public String toString() {
        return name;
    }


    /**
     * Handles the Drag & Drop for {@link TagcloudView} by constructing a
     * {@link DLObjectTransferable} with the selected data.
     * 
     * @author tacke
     */
    private class TagcloudviewItemTransferHandler extends TransferHandler {

        private static final long serialVersionUID = -4707421508713516324L;


        @Override
        protected Transferable createTransferable(JComponent c) {

            JTextPane jTextPane = (JTextPane) c;

            String selected = jTextPane.getSelectedText();

            if (selected != null) {
                final ExtractionType type = ToolController.getInstance().getTool(ExtractionTool.class).getCurrentType();
                final DLObjectTransferable dt = type.getTrans(selected);

                return dt;
            }
            else {
                return null;
            }
        }


        @Override
        public int getSourceActions(JComponent c) {
            return TransferHandler.COPY;
        }

    }
}
