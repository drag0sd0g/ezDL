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

import java.awt.datatransfer.Transferable;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.TransferHandler;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.HighlighterFactory;

import de.unidue.inf.is.ezdl.dlcore.data.extractor.Entry;
import de.unidue.inf.is.ezdl.dlcore.data.extractor.ExtractionResult;
import de.unidue.inf.is.ezdl.dlfrontend.i18n.I18nSupport;
import de.unidue.inf.is.ezdl.gframedl.ToolController;
import de.unidue.inf.is.ezdl.gframedl.tools.extraction.ExtractionTool;
import de.unidue.inf.is.ezdl.gframedl.tools.extraction.ExtractionType;
import de.unidue.inf.is.ezdl.gframedl.transfer.DLObjectTransferable;



/**
 * A <code>TableView</code> takes an <code>ExtractionResult</code> and displays
 * it in the form of a table with two columns: Term and number of occurrences.
 * 
 * @see AbstractExtractionView
 * @author tacke
 */
public final class TableView extends AbstractExtractionView {

    private static final long serialVersionUID = 503736328096276549L;

    private JXTable dataTable = null;
    private AbstractTableModel model = null;
    private List<Entry> data = new LinkedList<Entry>();

    private I18nSupport i18n = I18nSupport.getInstance();
    private String name = i18n.getLocString("ezdl.tools.extraction.views.table");


    public TableView() {
        initialize(getDataTable());
    }


    private void initialize(JComponent source) {
        setDoubleBuffered(true);
        getViewport().add(source);
        setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
    }


    private AbstractTableModel getTableModel() {

        if (model == null) {
            model = new AbstractTableModel() {

                private static final long serialVersionUID = -4460377699570898718L;
                public static final int TERM_INDEX = 0;
                public static final int OCURRENCE_INDEX = 1;


                @Override
                public String getColumnName(int column) {
                    switch (column) {
                        case TERM_INDEX:
                            return i18n.getLocString("ezdl.tools.extraction.views.table.term");
                        case OCURRENCE_INDEX:
                            return i18n.getLocString("ezdl.tools.extraction.views.table.ocurrence");
                        default:
                            return "";
                    }
                }


                @Override
                public int getColumnCount() {
                    return 2;
                }


                @Override
                public int getRowCount() {
                    return data.size();
                }


                @Override
                public Object getValueAt(int rowIndex, int columnIndex) {
                    Entry e = data.get(rowIndex);

                    switch (columnIndex) {
                        case TERM_INDEX:
                            return e.getKey();
                        case OCURRENCE_INDEX:
                            return e.getValue();
                        default:
                            return new Object();
                    }
                }


                @Override
                public Class<?> getColumnClass(int column) {
                    switch (column) {
                        case TERM_INDEX:
                            return String.class;
                        case OCURRENCE_INDEX:
                            return Integer.class;
                        default:
                            return String.class;
                    }
                }
            };
        }
        return model;
    }


    private JTable getDataTable() {
        if (dataTable == null) {

            dataTable = new JXTable(getTableModel());
            dataTable.setSortable(true);
            dataTable.setDragEnabled(true);

            Highlighter highlight = HighlighterFactory.createAlternateStriping();
            dataTable.setHighlighters(highlight);

            dataTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            dataTable.setColumnSelectionAllowed(false);
            dataTable.setFillsViewportHeight(true);

            dataTable.getColumn(1).setCellRenderer(new DefaultTableCellRenderer());

            dataTable.setTransferHandler(new TableviewItemTransferHandler());
        }

        return dataTable;
    }


    @Override
    public void displayData(ExtractionResult result) {

        data = result.tableData();

        if (data == null) {
            return;
        }

        Collections.sort(data, new Comparator<Entry>() {

            @Override
            public int compare(Entry o1, Entry o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });

        model.fireTableDataChanged();
    }


    @Override
    public String toString() {
        return name;
    }


    /**
     * Handles the Drag & Drop for {@link TabledView} by constructing a
     * {@link DLObjectTransferable} with the selected data.
     * 
     * @author tacke
     */
    private static class TableviewItemTransferHandler extends TransferHandler {

        private static final long serialVersionUID = -4707421508713516324L;


        @Override
        protected Transferable createTransferable(JComponent c) {
            JTable table = (JTable) c;
            String selected = table.getModel().getValueAt(table.getSelectedRow(), 0).toString();

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