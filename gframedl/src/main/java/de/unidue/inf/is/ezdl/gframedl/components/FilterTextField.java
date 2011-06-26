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

package de.unidue.inf.is.ezdl.gframedl.components;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.jdesktop.swingx.JXList;

import de.unidue.inf.is.ezdl.dlcore.utils.StringUtils;
import de.unidue.inf.is.ezdl.dlfrontend.i18n.I18nSupport;
import de.unidue.inf.is.ezdl.gframedl.components.grouping.list.GroupedList;



/**
 * Text field that is used for filtering {@link JXList}s and {@link JTable}s.
 * 
 * @author tbeckers
 */
public final class FilterTextField extends JTextField {

    private static final long serialVersionUID = 4021482716057788566L;

    private JXList list;
    private JTable table;
    private GroupedList groupedList;
    private List<FilterChangeListener> listeners = new CopyOnWriteArrayList<FilterChangeListener>();


    /**
     * Creates a filter text field for a {@link JXList}
     * 
     * @param list
     *            the list that is filtered
     */
    public FilterTextField(JXList list) {
        this.list = list;
        this.list.setAutoCreateRowSorter(true);
        initTextField();
    }


    /**
     * Creates a filter text field for a {@link GroupedList}
     * 
     * @param list
     *            the list that is filtered
     */
    public FilterTextField(GroupedList list) {
        this.groupedList = list;
        this.groupedList.setAutoCreateRowSorter(true);
        initTextField();
    }


    /**
     * Creates a filter text field for a {@link JTable}
     * 
     * @param table
     *            the table that is filtered
     */
    public FilterTextField(JTable table) {
        throw new UnsupportedOperationException("not implemented");
    }


    public void addFilterChangeListener(FilterChangeListener listener) {
        listeners.add(listener);
    }


    public void removeAllFilterChangeListeners() {
        listeners.clear();
    }


    public boolean removeFilterChangeListener(FilterChangeListener listener) {
        return listeners.remove(listener);
    }


    private void fireFilterChangeListener(final String filter) {
        for (final FilterChangeListener listener : listeners) {
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    listener.filterHasChanged(filter);
                }
            });
        }

    }


    private void initTextField() {
        TextPrompt.addDefaultPrompt(I18nSupport.getInstance().getLocString("ezdl.controls.label.filter.info"), this);
        getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void removeUpdate(DocumentEvent e) {
                applyFilter();
            }


            @Override
            public void insertUpdate(DocumentEvent e) {
                applyFilter();
            }


            @Override
            public void changedUpdate(DocumentEvent e) {
                applyFilter();
            }
        });
    }


    private void applyFilter() {
        String filter = getText().trim();
        boolean normalizeStrings = !StringUtils.containsNonAsciiCharacters(filter);

        if (normalizeStrings) {
            filter = StringUtils.normalize(filter);
        }

        fireFilterChangeListener(filter);

        Pattern pattern = Pattern.compile("(?is)" + Pattern.quote(filter));

        if (list != null) {
            list.setRowFilter(newRowFilter(normalizeStrings, pattern));
        }
        if (groupedList != null) {
            groupedList.setRowFilter(newRowFilter(normalizeStrings, pattern));
        }
        if (table != null) {
            throw new UnsupportedOperationException("not implemented");
        }
    }


    private RowFilter<ListModel, Integer> newRowFilter(final boolean normalizeStrings, final Pattern pattern) {
        return new RowFilter<ListModel, Integer>() {

            @Override
            public boolean include(RowFilter.Entry<? extends ListModel, ? extends Integer> entry) {
                Object value = entry.getValue(0);
                if (!(value instanceof Filterable)) {
                    throw new RuntimeException("model entries must implement " + Filterable.class);
                }
                Filterable f = (Filterable) value;
                String filterString = f.toFilterString();
                if (normalizeStrings) {
                    filterString = StringUtils.normalize(filterString);
                }
                Matcher m = pattern.matcher(filterString);
                return m.find();
            }
        };
    }
}
