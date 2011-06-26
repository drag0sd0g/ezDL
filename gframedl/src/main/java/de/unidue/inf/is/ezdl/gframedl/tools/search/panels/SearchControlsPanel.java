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

package de.unidue.inf.is.ezdl.gframedl.tools.search.panels;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DropMode;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SpringLayout;

import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;
import de.unidue.inf.is.ezdl.dlcore.data.fields.Order;
import de.unidue.inf.is.ezdl.dlcore.data.fields.Sorting;
import de.unidue.inf.is.ezdl.dlcore.utils.SystemUtils;
import de.unidue.inf.is.ezdl.dlcore.utils.SystemUtils.OperatingSystem;
import de.unidue.inf.is.ezdl.dlfrontend.i18n.I18nSupport;
import de.unidue.inf.is.ezdl.gframedl.components.FilterChangeListener;
import de.unidue.inf.is.ezdl.gframedl.components.FilterTextField;
import de.unidue.inf.is.ezdl.gframedl.components.JComboBoxVAlign;
import de.unidue.inf.is.ezdl.gframedl.components.TextComponentPopupMenu;
import de.unidue.inf.is.ezdl.gframedl.export.ExportAction;
import de.unidue.inf.is.ezdl.gframedl.tools.extraction.ExtractionType;
import de.unidue.inf.is.ezdl.gframedl.tools.search.actions.Actions;
import de.unidue.inf.is.ezdl.gframedl.tools.search.actions.ExtractionAction;
import de.unidue.inf.is.ezdl.gframedl.tools.search.projection.Projection;
import de.unidue.inf.is.ezdl.gframedl.tools.search.renderers.SortOrderListCellRenderer;
import de.unidue.inf.is.ezdl.gframedl.tools.search.renderers.SortingListCellRenderer;
import de.unidue.inf.is.ezdl.gframedl.transfer.DefaultTextComponentTransferHandler;



/**
 * A panel containing search controls like filtering, sorting and extraction.
 * 
 * @author tbeckers
 */
public final class SearchControlsPanel extends JPanel {

    private static final long serialVersionUID = 3350768210461407507L;

    private JLabel label;
    private JComboBox sortingCombobox;
    private JComboBox sortingOrderComboBox;
    private JLabel filterLabel;
    private FilterTextField filterTextField;
    private JButton extractButton;
    private JPopupMenu menu;

    private I18nSupport i18n = I18nSupport.getInstance();

    private SpringLayout layout;

    private ResultPanel resultPanel;


    /**
     * Constructor.
     * 
     * @param sortableFields
     *            the list of fields that should be supported in sort operations
     * @param projections
     *            the list of projections that should be supported. This
     *            parameter is currently not used.
     * @param resultList
     */
    public SearchControlsPanel(ResultPanel resultPanel, List<Field> sortableFields,
                    List<? extends Projection> projections) {
        super();

        this.resultPanel = resultPanel;
        layout = new SpringLayout();
        setLayout(layout);
        setPreferredSize(new Dimension(250, 40));

        if (SystemUtils.OS == OperatingSystem.MAC_OS) {
            setBorder(BorderFactory.createEmptyBorder(0, 5, 10, 5));
        }
        else {
            setBorder(BorderFactory.createEmptyBorder(5, 5, 10, 5));
        }

        init(sortableFields, projections);
    }


    private void init(List<Field> sortableFields, List<? extends Projection> projections) {
        JButton exportButton = new JButton(new ExportAction(resultPanel));
        label = new JLabel();
        label.setText(i18n.getLocString("ezdl.controls.resultlistpanel.label.sortby"));
        sortingCombobox = new JComboBoxVAlign(exportButton);
        sortingCombobox.setRenderer(new SortingListCellRenderer());
        sortingCombobox.setAction(Actions.SORT_BY_ACTION);

        sortingOrderComboBox = new JComboBoxVAlign(exportButton);
        sortingOrderComboBox.setRenderer(new SortOrderListCellRenderer());
        sortingOrderComboBox.addItem(Order.DESCENDING);
        sortingOrderComboBox.addItem(Order.ASCENDING);
        sortingOrderComboBox.setAction(Actions.SORT_ORDER_ACTION);

        filterLabel = new JLabel(i18n.getLocString("ezdl.controls.label.filter"));
        if (ResultPanel.USE_GROUPED_LIST) {
            filterTextField = new FilterTextField(resultPanel.getGroupedList());

        }
        else {
            filterTextField = new FilterTextField(resultPanel.getResultList());
        }
        filterTextField.setAction(Actions.FILTER_ACTION);
        filterTextField.setDropMode(DropMode.INSERT);
        filterTextField.setDragEnabled(true);
        filterTextField.setTransferHandler(new DefaultTextComponentTransferHandler(filterTextField));
        TextComponentPopupMenu.addPopupMenu(filterTextField);

        menu = new JPopupMenu();

        for (ExtractionType type : ExtractionType.values()) {
            menu.add(new ExtractionAction(type));
        }

        extractButton = new JButton(Actions.DISABLE_EXTRACT_ACTION);

        extractButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                menu.setVisible(!menu.isVisible());
            }
        });

        extractButton.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                JButton cmp = (JButton) e.getSource();

                int y = cmp.getHeight() - cmp.getBorder().getBorderInsets(cmp).bottom;

                if (cmp.isEnabled() && e.getButton() == MouseEvent.BUTTON1) {
                    // FIXME weird location problem running Mac OS (possibly
                    // other OSes)
                    menu.show(cmp, 0, y);
                }
            }
        });

        extractButton.setToolTipText(i18n.getLocString("ezdl.controls.resultlistpanel.label.extract_long"));

        sortingCombobox.setPreferredSize(new Dimension(120, 25));
        sortingCombobox.setMinimumSize(new Dimension(120, 25));

        sortingOrderComboBox.setPreferredSize(new Dimension(60, 25));
        sortingOrderComboBox.setMinimumSize(new Dimension(60, 25));

        filterTextField.setPreferredSize(new Dimension(150, 25));
        filterTextField.setMinimumSize(new Dimension(150, 25));

        add(exportButton);
        add(label);
        add(sortingCombobox);
        add(sortingOrderComboBox);
        add(extractButton);
        add(filterLabel);
        add(filterTextField);

        layout.putConstraint(SpringLayout.WEST, exportButton, 0, SpringLayout.WEST, this);
        layout.putConstraint(SpringLayout.NORTH, exportButton, 1, SpringLayout.NORTH, this);

        layout.putConstraint(SpringLayout.WEST, label, 5, SpringLayout.EAST, exportButton);
        layout.putConstraint(SpringLayout.NORTH, label, 7, SpringLayout.NORTH, this);

        layout.putConstraint(SpringLayout.WEST, sortingCombobox, 5, SpringLayout.EAST, label);
        layout.putConstraint(SpringLayout.NORTH, sortingCombobox, 4, SpringLayout.NORTH, this);

        layout.putConstraint(SpringLayout.WEST, sortingOrderComboBox, 3, SpringLayout.EAST, sortingCombobox);
        layout.putConstraint(SpringLayout.NORTH, sortingOrderComboBox, 4, SpringLayout.NORTH, this);

        layout.putConstraint(SpringLayout.WEST, extractButton, 19, SpringLayout.EAST, sortingOrderComboBox);
        layout.putConstraint(SpringLayout.NORTH, extractButton, 1, SpringLayout.NORTH, this);

        layout.putConstraint(SpringLayout.WEST, filterLabel, 19, SpringLayout.EAST, extractButton);
        layout.putConstraint(SpringLayout.NORTH, filterLabel, 7, SpringLayout.NORTH, this);

        layout.putConstraint(SpringLayout.WEST, filterTextField, 5, SpringLayout.EAST, filterLabel);
        layout.putConstraint(SpringLayout.EAST, filterTextField, 0, SpringLayout.EAST, this);
        layout.putConstraint(SpringLayout.NORTH, filterTextField, 3, SpringLayout.NORTH, this);
    }


    void activateSortingEntries(List<Field> sortableFields) {
        sortingCombobox.removeAllItems();
        for (Field sortableField : sortableFields) {
            sortingCombobox.addItem(sortableField);
        }
        sortingCombobox.setSelectedItem(Field.RSV);
    }


    public void addFilterChangeListener(FilterChangeListener listener) {
        filterTextField.addFilterChangeListener(listener);
    }


    public Sorting getSorting() {
        if (sortingCombobox.getComponentCount() != 0) {
            final Object selectedItem = sortingCombobox.getSelectedItem();
            if (selectedItem instanceof Field) {
                final Field selectedField = (Field) selectedItem;
                return new Sorting(selectedField, (Order) sortingOrderComboBox.getSelectedItem());
            }
        }

        return new Sorting();
    }


    public String getFilter() {
        return filterTextField.getText();
    }


    public void setFilter(String filter) {
        filterTextField.setText(filter);
    }

}
