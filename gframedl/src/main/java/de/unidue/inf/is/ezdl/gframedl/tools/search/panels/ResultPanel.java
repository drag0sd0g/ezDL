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

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SortOrder;
import javax.swing.event.ListSelectionListener;

import org.jdesktop.jxlayer.JXLayer;

import de.unidue.inf.is.ezdl.dlcore.data.dldata.Document;
import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;
import de.unidue.inf.is.ezdl.dlcore.data.fields.Order;
import de.unidue.inf.is.ezdl.dlcore.data.fields.Sorting;
import de.unidue.inf.is.ezdl.dlcore.data.query.Query;
import de.unidue.inf.is.ezdl.dlcore.query.RegExTermsOnlyQueryTreeWalker;
import de.unidue.inf.is.ezdl.dlcore.utils.StringUtils;
import de.unidue.inf.is.ezdl.dlfrontend.i18n.I18nSupport;
import de.unidue.inf.is.ezdl.gframedl.components.FilterChangeListener;
import de.unidue.inf.is.ezdl.gframedl.components.GroupBox;
import de.unidue.inf.is.ezdl.gframedl.components.actions.SelectionGetter;
import de.unidue.inf.is.ezdl.gframedl.components.grouping.list.GroupedList;
import de.unidue.inf.is.ezdl.gframedl.tools.search.ResultItem;
import de.unidue.inf.is.ezdl.gframedl.tools.search.ResultListModel;
import de.unidue.inf.is.ezdl.gframedl.tools.search.actions.Actions;
import de.unidue.inf.is.ezdl.gframedl.tools.search.actions.SearchAction.Type;
import de.unidue.inf.is.ezdl.gframedl.tools.search.grouping.ResultListConfig;
import de.unidue.inf.is.ezdl.gframedl.tools.search.panels.searchprogressoverlay.SearchProgressOverlay;
import de.unidue.inf.is.ezdl.gframedl.tools.search.projection.CompactProjection;
import de.unidue.inf.is.ezdl.gframedl.tools.search.projection.DetailedProjection;
import de.unidue.inf.is.ezdl.gframedl.tools.search.projection.Projection;
import de.unidue.inf.is.ezdl.gframedl.tools.search.resultList.ResultList;



/**
 * The panel that contains the result list.
 * 
 * @author tbeckers
 */
public final class ResultPanel extends GroupBox implements SelectionGetter {

    private static final long serialVersionUID = -8386686412829274629L;

    static final boolean USE_GROUPED_LIST = true;

    private JPanel controlResultPanel;

    /**
     * The USE_GROUPED_LIST constant determines which list implementation is
     * used.
     */
    private ResultList resultList;
    private GroupedList groupedList;

    private SearchControlsPanel searchControlPanel;
    private ResultListModel resultListModel;

    private List<String> queryTermsForHighlighting = new ArrayList<String>();

    private int resultCount;

    private SearchProgressOverlay busyPainterUI;


    /**
     * Constructor.
     */
    public ResultPanel() {
        super(I18nSupport.getInstance().getLocString("ezdl.controls.resultlistpanel.label"));
        if (USE_GROUPED_LIST) {
            initGroupedList();
        }
        else {
            initNormalList();
        }
    }


    private void initGroupedList() {
        ResultListConfig.initStrings();
        groupedList = new GroupedList();
        groupedList.setConfig(new ResultListConfig(this));

        initCommon();
        JXLayer<JComponent> resultListScrollPaneLayer = createJXLayer(groupedList);
        controlResultPanel.add(resultListScrollPaneLayer, BorderLayout.CENTER);
        initSearchActionUI();
        initLayout();
    }


    private void initNormalList() {
        resultList = new ResultList(this);

        initCommon();
        JXLayer<JComponent> resultListScrollPaneLayer = createJXLayer(new JScrollPane(resultList));
        controlResultPanel.add(resultListScrollPaneLayer, BorderLayout.CENTER);
        initSearchActionUI();
        initLayout();
    }


    private void initLayout() {
        setLayout(new BorderLayout());
        add(controlResultPanel, BorderLayout.CENTER);
    }


    private void initSearchActionUI() {
        Actions.SEARCH_ACTION.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if ("type".equals(evt.getPropertyName())) {
                    boolean lock = (Type) evt.getNewValue() == Type.CANCEL;
                    busyPainterUI.setLocked(lock);
                }
            }
        });
    }


    private void initCommon() {
        resultCount = 0;

        searchControlPanel = new SearchControlsPanel(this, new ArrayList<Field>(Field.SORTABLE_FIELDS),
                        Arrays.<Projection> asList(new CompactProjection(), new DetailedProjection()));

        searchControlPanel.addFilterChangeListener(new FilterChangeListener() {

            @Override
            public void filterHasChanged(String filter) {
                updateTitle(resultCountString(!StringUtils.isEmpty(filter)));
                repaint();
            }
        });

        controlResultPanel = new JPanel();
        controlResultPanel.setLayout(new BorderLayout());
        controlResultPanel.add(searchControlPanel, BorderLayout.NORTH);
    }


    private JXLayer<JComponent> createJXLayer(JComponent component) {
        JXLayer<JComponent> resultListScrollPaneLayer = new JXLayer<JComponent>(component);
        busyPainterUI = new SearchProgressOverlay();
        resultListScrollPaneLayer.setUI(busyPainterUI);
        return resultListScrollPaneLayer;
    }


    /**
     * Getter for query terms as regular expression.
     * 
     * @return
     */
    public List<String> getQueryTermsForHighlighting() {
        return queryTermsForHighlighting;
    }


    /**
     * Sets the query for later use for highlighting.
     * 
     * @param query
     *            the query to set
     */
    public void setQueryForHighlighting(Query query) {
        RegExTermsOnlyQueryTreeWalker regExTermsOnlyQueryTreeWalker = new RegExTermsOnlyQueryTreeWalker();
        regExTermsOnlyQueryTreeWalker.walk(query.getTree());
        queryTermsForHighlighting = regExTermsOnlyQueryTreeWalker.queryTerms();
        System.out.println(queryTermsForHighlighting);
    }


    /**
     * Adds the {@link ResultItem} objects of the given list to the internal
     * model.
     * 
     * @param partialResults
     *            the list of objects to add
     */
    public void addResultListData(List<ResultItem> partialResults) {
        resultCount = partialResults.size();
        updateTitle(resultCountString(false));
        updateUI();
        for (ResultItem ri : partialResults) {
            resultListModel.addElement(ri);
        }
    }


    /**
     * Refreshes the row with the given number.
     * 
     * @param rowNumber
     *            the row number to refresh
     */
    public void refresh(int rowNumber) {
        ((ResultListModel) resultList.getModel()).refresh(rowNumber);
    }


    /**
     * Refreshes the row of the result list that has the document with the given
     * ID.
     * 
     * @param oid
     *            the object ID of the document whose row to refresh
     */
    public void refresh(String oid) {
        ((ResultListModel) resultList.getModel()).refresh(oid);
    }


    /**
     * Clears this panel.
     */
    public void clear() {
        updateTitle(I18nSupport.getInstance().getLocString("ezdl.controls.resultlistpanel.label"));
        updateUI();
        ResultListModel model = new ResultListModel();
        resultListModel = model;
        if (USE_GROUPED_LIST) {
            groupedList.setModel(model);
        }
        else {
            resultList.setModel(model);
        }
        clearFilter();
    }


    public String getFilter() {
        return searchControlPanel.getFilter();
    }


    /**
     * Clears the filter.
     */
    private void clearFilter() {
        searchControlPanel.setFilter("");
        if (USE_GROUPED_LIST) {
            groupedList.setRowFilter(null);
        }
        else {
            resultList.setRowFilter(null);
        }
    }


    /**
     * Returns the sorting.
     * 
     * @return the sorting
     */
    public Sorting getSorting() {
        return searchControlPanel.getSorting();
    }


    /**
     * Getter for the result list.
     * 
     * @return
     */
    public ResultList getResultList() {
        return resultList;
    }


    /**
     * Getter for the grouped list.
     * 
     * @return
     */
    public GroupedList getGroupedList() {
        return groupedList;
    }


    /**
     * Applies the sorting settings.
     */
    public void applySorting() {
        if (USE_GROUPED_LIST) {
            final Sorting sorting = searchControlPanel.getSorting();
            groupedList.setAutoCreateRowSorter(true);
            groupedList.setSortOrder(sorting.getOrder() == Order.ASCENDING ? SortOrder.ASCENDING : SortOrder.DESCENDING);
            groupedList.setComparator(new Comparator<ResultItem>() {

                @Override
                public int compare(ResultItem o1, ResultItem o2) {
                    if (sorting.getField().isSearchable()) {
                        return sorting.getField().getComparator().compare(o1.getDocument(), o2.getDocument());
                    }
                    else {
                        return sorting.getField().getResultComparator()
                                        .compare(o1.getResultDocument(), o2.getResultDocument());
                    }
                }
            });

            groupedList.toggleSortOrder();
            groupedList.toggleSortOrder();
        }
        else {
            final Sorting sorting = searchControlPanel.getSorting();
            resultList.setAutoCreateRowSorter(true);
            resultList.setSortOrder(sorting.getOrder() == Order.ASCENDING ? SortOrder.ASCENDING : SortOrder.DESCENDING);
            resultList.setComparator(new Comparator<ResultItem>() {

                @Override
                public int compare(ResultItem o1, ResultItem o2) {
                    if (sorting.getField().isSearchable()) {
                        return sorting.getField().getComparator().compare(o1.getDocument(), o2.getDocument());
                    }
                    else {
                        return sorting.getField().getResultComparator()
                                        .compare(o1.getResultDocument(), o2.getResultDocument());
                    }
                }
            });

            resultList.toggleSortOrder();
            resultList.toggleSortOrder();
        }
    }


    /**
     * Prepare a string which show the number of results.
     * 
     * @param filtered
     *            If true show only the number of results if false show the
     *            number of filtered result and the number of results.
     * @return The number of results.
     */
    private String resultCountString(boolean filtered) {
        if (USE_GROUPED_LIST) {
            String defaultString = I18nSupport.getInstance().getLocString("ezdl.controls.resultlistpanel.label");
            if (filtered) {
                return defaultString + ": " + groupedList.getElementCount() + " (" + resultCount + ")";
            }
            else {
                return defaultString + ": " + resultCount;
            }
        }
        else {
            String defaultString = I18nSupport.getInstance().getLocString("ezdl.controls.resultlistpanel.label");
            if (filtered) {
                return defaultString + ": " + resultList.getElementCount() + " (" + resultCount + ")";
            }
            else {
                return defaultString + ": " + resultCount;
            }
        }
    }


    public void updateSorting(List<Field> fields) {
        Set<Field> fieldsInResults = collectResultItemFields();
        List<Field> filteredFields = new LinkedList<Field>(fields);
        filteredFields.retainAll(fieldsInResults);
        searchControlPanel.activateSortingEntries(filteredFields);
    }


    private Set<Field> collectResultItemFields() {
        if (USE_GROUPED_LIST) {
            Set<Field> fields = new HashSet<Field>();
            final List<Object> results = groupedList.getVisibleElements();
            for (Object o : results) {
                if (o instanceof ResultItem) {
                    final Document document = ((ResultItem) o).getResultDocument().getDocument();
                    final Set<Field> itemFields = document.getFields();
                    for (Field itemField : itemFields) {
                        if (document.getFieldValue(itemField) != null) {
                            fields.add(itemField);
                        }
                    }
                }
            }
            fields.add(Field.RSV);
            return fields;
        }
        else {
            Set<Field> fields = new HashSet<Field>();
            final int resultSize = resultList.getElementCount();
            for (int i = 0; (i < resultSize); i++) {
                Object o = resultList.getElementAt(i);
                if (o instanceof ResultItem) {
                    final Document document = ((ResultItem) o).getResultDocument().getDocument();
                    final Set<Field> itemFields = document.getFields();
                    for (Field itemField : itemFields) {
                        if (document.getFieldValue(itemField) != null) {
                            fields.add(itemField);
                        }
                    }
                }
            }
            fields.add(Field.RSV);
            return fields;
        }
    }


    @Override
    public List<Object> getSelectedObjects() {
        if (USE_GROUPED_LIST) {
            List<Object> selection = groupedList.getSelectedValues();
            List<Object> objects = new LinkedList<Object>();
            for (Object o : selection) {
                objects.add(((ResultItem) o).getDocument());
            }
            return objects;
        }
        else {
            LinkedList<Object> objects = new LinkedList<Object>();
            if (resultList != null) {
                final Object[] object = resultList.getSelectedValues();
                for (Object o : object) {
                    objects.add(((ResultItem) o).getDocument());
                }
            }
            return objects;
        }
    }


    public SearchProgressOverlay getBusyPainterUI() {
        return busyPainterUI;
    }


    @Override
    public void addListSelectionListener(ListSelectionListener listener) {
        if (USE_GROUPED_LIST) {
            groupedList.addListSelectionListener(listener);
        }
        else {
            resultList.addListSelectionListener(listener);
        }
    }


    public int getPositionInModel(Object o) {
        return resultListModel.indexOf(o);
    }
}
