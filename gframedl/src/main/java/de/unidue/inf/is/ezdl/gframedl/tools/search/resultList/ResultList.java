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

package de.unidue.inf.is.ezdl.gframedl.tools.search.resultList;

import java.awt.Rectangle;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.TransferHandler;

import org.jdesktop.swingx.JXList;

import de.unidue.inf.is.ezdl.dlcore.data.dldata.DLObject;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.Document;
import de.unidue.inf.is.ezdl.dlcore.query.RegExTermsOnlyQueryTreeWalker;
import de.unidue.inf.is.ezdl.dlfrontend.i18n.I18nSupport;
import de.unidue.inf.is.ezdl.gframedl.ToolController;
import de.unidue.inf.is.ezdl.gframedl.components.dynamiclist.DynamicList;
import de.unidue.inf.is.ezdl.gframedl.events.AddToClipboardEvent;
import de.unidue.inf.is.ezdl.gframedl.events.DetailEvent;
import de.unidue.inf.is.ezdl.gframedl.events.ExportEvent;
import de.unidue.inf.is.ezdl.gframedl.events.SeeRelationsEvent;
import de.unidue.inf.is.ezdl.gframedl.tools.search.ResultItem;
import de.unidue.inf.is.ezdl.gframedl.tools.search.ResultListModel;
import de.unidue.inf.is.ezdl.gframedl.tools.search.SearchTool;
import de.unidue.inf.is.ezdl.gframedl.tools.search.panels.ResultPanel;
import de.unidue.inf.is.ezdl.gframedl.transfer.DLObjectTransferable;
import de.unidue.inf.is.ezdl.gframedl.transfer.DataFlavors;



/**
 * The result list.
 */
public final class ResultList extends DynamicList {

    private static final long serialVersionUID = -3100000039551581219L;
    private ResultPanel resultPanel;


    /**
     * Constructor.
     */
    public ResultList(ResultPanel resultPanel) {
        super(ResultListRendererFactory.sharedInstance);
        setAutoCreateRowSorter(true);
        this.resultPanel = resultPanel;
        setToolTipText("Dummytext to ensure the createToolTip method is called.");

        setModel(new ResultListModel());
        setDragEnabled(true);
        setTransferHandler(new ResultItemTransferHandler());

        addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    ResultItem elem = (ResultItem) getElementAt(getSelectedIndex());
                    if (elem != null) {
                        DetailEvent.fireDetailEvent(this, elem.getDocument(), DetailEvent.OpenMode.DEFAULT,
                                        queryTerms());
                    }
                }
            }
        });

        addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(final MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3 && getModel().getSize() > 0) {
                    int index = locationToIndex(e.getPoint());
                    if (!isSelectedIndex(index)) {
                        setSelectedIndex(index);
                    }
                }

            }


            @Override
            public void mouseClicked(MouseEvent e) {
                if ((e.getButton() == MouseEvent.BUTTON1)
                                && ((e.getModifiers() & InputEvent.CTRL_MASK) != InputEvent.CTRL_MASK)
                                && ((e.getModifiers() & InputEvent.SHIFT_MASK) != InputEvent.SHIFT_MASK)) {
                    int index = locationToIndex(e.getPoint());
                    Rectangle rc = getCellBounds(index, index);
                    if ((index != -1) && rc.contains(e.getPoint())) {
                        ResultItem elem = (ResultItem) getElementAt(index);
                        DetailEvent.fireDetailEvent(this, elem.getDocument(), DetailEvent.OpenMode.DEFAULT,
                                        queryTerms());
                    }
                }
                if (e.getButton() == MouseEvent.BUTTON2 && getModel().getSize() > 0) {
                    int index = locationToIndex(e.getPoint());
                    Rectangle rc = getCellBounds(index, index);
                    if ((index != -1) && rc.contains(e.getPoint())) {
                        ResultItem elem = (ResultItem) getElementAt(index);
                        DetailEvent.fireDetailEvent(this, elem.getDocument(), DetailEvent.OpenMode.NEW_TAB,
                                        queryTerms());
                    }
                }
                if (e.getButton() == MouseEvent.BUTTON3 && getModel().getSize() > 0) {
                    showContextMenu(e);
                }

            }
        });
    }


    /**
     * Returns the associated ResultPanel.
     * 
     * @return
     */
    public ResultPanel getResultPanel() {
        return resultPanel;
    }


    /**
     * ResultItemTransferHandler.
     */
    private static class ResultItemTransferHandler extends TransferHandler {

        private static final long serialVersionUID = 1L;


        @Override
        protected Transferable createTransferable(JComponent c) {
            JXList list = (JXList) c;
            Object[] values = list.getSelectedValues();
            List<Document> documents = new ArrayList<Document>();
            for (Object value : values) {
                documents.add(((ResultItem) value).getDocument());
            }
            return new DLObjectTransferable(documents, DataFlavors.getDataFlavorForClass(Document.class));
        }


        @Override
        public int getSourceActions(JComponent c) {
            return TransferHandler.COPY;
        }
    }


    private void showContextMenu(MouseEvent evt) {
        JPopupMenu menu = new JPopupMenu();
        menu.add(menuItemOpenInNewWindow());
        menu.add(menuItemOpenInNewTab());
        menu.add(menuItemCopyToClipboard());
        menu.add(menuItemExport());
        menu.add(menuItemSeeRelations());
        menu.show(this, evt.getX(), evt.getY());
    }


    private JMenuItem menuItemExport() {
        JMenuItem item = new JMenuItem(I18nSupport.getInstance().getLocString("ezdl.actions.export"));
        item.setEnabled(!isSelectionEmpty());
        item.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isSelectionEmpty()) {
                    ExportEvent.fireExportEvent(this, resultPanel.getSelectedObjects());
                }
            }
        });
        return item;
    }


    private JMenuItem menuItemCopyToClipboard() {
        JMenuItem item = new JMenuItem(I18nSupport.getInstance().getLocString(
                        SearchTool.I18N_PREFIX + "result.copy_to_clipboard"));
        item.setEnabled(!isSelectionEmpty());
        item.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isSelectionEmpty()) {
                    for (Object object : resultPanel.getSelectedObjects()) {
                        if (object instanceof DLObject) {
                            AddToClipboardEvent.fireClipboardEvent(this, (DLObject) object);
                        }
                    }
                }
            }
        });
        return item;
    }


    private JMenuItem menuItemOpenInNewWindow() {
        JMenuItem item = new JMenuItem(I18nSupport.getInstance().getLocString(
                        SearchTool.I18N_PREFIX + "result.open_in_new_window"));
        item.setEnabled(!isSelectionEmpty());
        item.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isSelectionEmpty()) {
                    for (Object object : resultPanel.getSelectedObjects()) {
                        if (object instanceof DLObject) {
                            DetailEvent.fireDetailEvent(this, (DLObject) object, DetailEvent.OpenMode.NEW_WINDOW,
                                            queryTerms());
                        }
                    }
                }
            }
        });
        return item;
    }


    private JMenuItem menuItemOpenInNewTab() {
        JMenuItem item = new JMenuItem(I18nSupport.getInstance().getLocString(
                        SearchTool.I18N_PREFIX + "result.open_in_new_tab"));
        item.setEnabled(!isSelectionEmpty());
        item.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isSelectionEmpty()) {
                    for (Object object : resultPanel.getSelectedObjects()) {
                        if (object instanceof DLObject) {
                            DetailEvent.fireDetailEvent(this, (DLObject) object, DetailEvent.OpenMode.NEW_TAB,
                                            queryTerms());
                        }
                    }
                }
            }
        });
        return item;
    }


    private JMenuItem menuItemSeeRelations() {
        JMenuItem item = new JMenuItem(I18nSupport.getInstance().getLocString("ezdl.relations.seeRelations"));
        item.setEnabled(!isSelectionEmpty());
        item.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isSelectionEmpty()) {
                    List<DLObject> objects = new ArrayList<DLObject>();
                    for (Object object : resultPanel.getSelectedObjects()) {
                        if (object instanceof DLObject) {
                            objects.add((DLObject) object);
                        }
                    }
                    SeeRelationsEvent.fireRelationsEvent(this, objects);
                }
            }
        });
        return item;
    }


    /**
     * Get terms in current query as regular expressions.
     * 
     * @return terms in current query
     */
    private static List<String> queryTerms() {
        List<String> result = new ArrayList<String>();
        SearchTool searchTool = ToolController.getInstance().getTool(SearchTool.class);

        RegExTermsOnlyQueryTreeWalker johnny = new RegExTermsOnlyQueryTreeWalker();
        johnny.walk(searchTool.getCurrentSearch().query.getTree());
        result.addAll(johnny.queryTerms());

        return result;
    }
}
