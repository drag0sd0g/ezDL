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

package de.unidue.inf.is.ezdl.gframedl.tools.details.views;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.util.Arrays;
import java.util.EventObject;
import java.util.LinkedList;
import java.util.List;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.RepaintManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.unidue.inf.is.ezdl.dlcore.data.dldata.DLObject;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.TextDocument;
import de.unidue.inf.is.ezdl.dlcore.data.query.ResultDocument;
import de.unidue.inf.is.ezdl.dlcore.message.content.DocumentDetailsAsk;
import de.unidue.inf.is.ezdl.dlcore.message.content.DocumentDetailsTell;
import de.unidue.inf.is.ezdl.dlcore.message.content.MessageContent;
import de.unidue.inf.is.ezdl.dlcore.utils.StringUtils;
import de.unidue.inf.is.ezdl.dlfrontend.comm.BackendEvent;
import de.unidue.inf.is.ezdl.dlfrontend.converter.DocumentConversionStrategy;
import de.unidue.inf.is.ezdl.dlfrontend.dispatcher.Dispatcher;
import de.unidue.inf.is.ezdl.dlfrontend.dispatcher.EventReceiver;
import de.unidue.inf.is.ezdl.gframedl.Colors;
import de.unidue.inf.is.ezdl.gframedl.Icons;
import de.unidue.inf.is.ezdl.gframedl.components.actions.SelectionGetter;
import de.unidue.inf.is.ezdl.gframedl.converter.HTMLConversionStrategy;
import de.unidue.inf.is.ezdl.gframedl.export.ExportAction;
import de.unidue.inf.is.ezdl.gframedl.tools.details.actions.PrintAction;
import de.unidue.inf.is.ezdl.gframedl.utils.HighlightingUtils;



/**
 * DetailView for {@link TextDocument}.
 */
public final class TextDocumentDetailView extends DefaultDetailView implements EventReceiver, Printable,
                SelectionGetter {

    private static final long serialVersionUID = -7111853516098251631L;

    private static final String HIGHLIGHT_COLOR_FOREGROUND = HighlightingUtils
                    .colorToHex(Colors.HIGHLIGHT_FOREGROUND_COLOR);
    private static final String HIGHLIGHT_COLOR_BACKGROUND = HighlightingUtils
                    .colorToHex(Colors.HIGHLIGHT_BACKGROUND_COLOR);

    private DocumentConversionStrategy docStrategy = new HTMLConversionStrategy();
    private boolean detailRequestSent;
    private List<String> highlightStrings;


    /**
     * Constructor.
     */
    public TextDocumentDetailView() {
        super();
        Dispatcher.registerInterest(this, BackendEvent.class);
    }


    @Override
    public String getTabName() {
        if (object != null) {
            return StringUtils.shortenString(((TextDocument) object).getTitle(), 10);
        }
        else {
            return null;
        }
    }


    @Override
    public void setObject(DLObject o, List<String> highlightStrings) {
        super.setObject(o, highlightStrings);
        this.highlightStrings = highlightStrings;
        if (o != null) {
            if (o instanceof TextDocument) {
                text();
            }
            if (!detailRequestSent) {
                requestDetails();
            }
        }
    }


    @Override
    public TextDocument getObject() {
        return (TextDocument) super.getObject();
    }


    private void requestDetails() {
        BackendEvent ask = new BackendEvent(this);
        DocumentDetailsAsk dda = new DocumentDetailsAsk(Arrays.asList((object).getOid()));
        ask.setContent(dda);
        Dispatcher.postEvent(ask);
        detailRequestSent = true;
        setBusy(true);
    }


    private void updateDetails(DocumentDetailsTell ddt) {
        for (ResultDocument d : ddt.getResults()) {
            if (d.getOid().equals(object.getOid())) {
                setObject(d.getDocument(), highlightStrings);
                setBusy(false);
            }
        }
    }


    @Override
    public boolean handleEzEvent(EventObject ev) {
        if (ev instanceof BackendEvent) {
            MessageContent messageContent = ((BackendEvent) ev).getContent();
            if (messageContent instanceof DocumentDetailsTell) {
                updateDetails((DocumentDetailsTell) messageContent);
            }
        }
        return true;
    }


    @Override
    public Icon getIcon() {
        if (object != null) {
            TextDocument d = getObject();
            return Icons.getIconForDocument(d);
        }
        else {
            return null;
        }
    }


    private void text() {
        TextDocument d = getObject();
        String text = docStrategy.print(d).toString();
        text = HighlightingUtils.highlightTerms(text, false, highlightStrings, false, HIGHLIGHT_COLOR_FOREGROUND,
                        HIGHLIGHT_COLOR_BACKGROUND);
        editorPane.setText(text);
    }


    @Override
    public int print(Graphics g, PageFormat pageFormat, int pageIndex) throws PrinterException {
        if (pageIndex > 0) {
            return (NO_SUCH_PAGE);
        }
        else {
            int x = (int) pageFormat.getImageableX() + 1;
            int y = (int) pageFormat.getImageableY() + 1;
            int hx = (int) pageFormat.getImageableHeight() - 1;
            int hy = (int) pageFormat.getImageableWidth() - 1;
            g.translate(x, y);
            RepaintManager currentManager = RepaintManager.currentManager(editorPane);
            currentManager.setDoubleBufferingEnabled(false);
            Dimension dim = editorPane.getSize();
            editorPane.setSize(hy, hx);
            editorPane.paint(g);
            editorPane.setSize(dim);
            currentManager.setDoubleBufferingEnabled(true);
            return (PAGE_EXISTS);
        }
    }


    @Override
    public List<Action> getPossibleActions() {
        List<Action> result = super.getPossibleActions();
        result.add(new PrintAction(this, this));
        result.add(new ExportAction(this));
        return result;
    }


    @Override
    public List<Object> getSelectedObjects() {
        LinkedList<Object> list = new LinkedList<Object>();
        list.add(getObject());
        return list;
    }


    @Override
    public void addListSelectionListener(ListSelectionListener listener) {
        listener.valueChanged(new ListSelectionEvent(this, 0, 0, false));
    }
}
