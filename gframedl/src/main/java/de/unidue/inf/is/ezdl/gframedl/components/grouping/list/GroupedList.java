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

package de.unidue.inf.is.ezdl.gframedl.components.grouping.list;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.ListModel;
import javax.swing.RowFilter;
import javax.swing.SortOrder;
import javax.swing.SwingUtilities;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jdesktop.swingx.JXList;

import de.unidue.inf.is.ezdl.gframedl.components.grouping.GroupContainer;
import de.unidue.inf.is.ezdl.gframedl.components.grouping.GroupsContainer;
import de.unidue.inf.is.ezdl.gframedl.components.grouping.components.CollapseAllAction;
import de.unidue.inf.is.ezdl.gframedl.components.grouping.components.ExpandAllAction;
import de.unidue.inf.is.ezdl.gframedl.components.grouping.components.GroupSeparatorBorder;
import de.unidue.inf.is.ezdl.gframedl.components.grouping.components.TitleBtn;
import de.unidue.inf.is.ezdl.gframedl.components.grouping.list.actions.DeselectGroupAction;
import de.unidue.inf.is.ezdl.gframedl.components.grouping.list.actions.SelectGroupAction;
import de.unidue.inf.is.ezdl.gframedl.components.grouping.resources.Resources;



/**
 * A Grouped List.
 * 
 * @author R.Tipografov
 */
public class GroupedList extends GroupsContainer implements ListDataListener, ListSelectionListener, FocusListener {

    /**
     * For performance the max count of groups in the list can be reduced.
     */
    public static boolean USE_EQUIVALENCE_CLASS_LIMIT = true;
    /**
     * The max count of groups in the list.
     */
    public static int EQUIVALENCE_CLASS_LIMIT = 100;

    private static final long serialVersionUID = 1L;

    private JComboBox groupByCombo;
    private GroupedListConfig config;
    private DefaultListModel model;
    private GroupByRelation groupBy = new GroupByRelation();
    private Map<EquivalenceClass, GroupContainer> groups = new TreeMap<EquivalenceClass, GroupContainer>();
    private boolean allowMultiListSelect = true;
    private boolean addSelectActions = true;
    private boolean hideGroupTitleForTheAllGroup = true;
    private List<ListSelectionListener> selectionListeners = new CopyOnWriteArrayList<ListSelectionListener>();
    private EquivalenceClass deselectOthersOnFocusGainedRequest = null;

    /*
     * Parameters which are forwarded to the sublist instances at
     * forwardParametes().
     */
    private boolean autoCreateRowSorter = true;
    private RowFilter<? super ListModel, ? super Integer> filter = null;
    private SortOrder sortOrder = SortOrder.ASCENDING;
    private Comparator<?> comparator = null;


    public GroupedList() {
        super(Resources.S_GROUP_BY + ": ", null);
        setModel(new DefaultListModel());
        setConfig(new GroupedListConfig());
    }


    /**
     * Gets the list Configuration.
     */
    public GroupedListConfig getConfig() {
        return config;
    }


    /**
     * Sets the list Configuration.
     * 
     * @param config
     */
    public void setConfig(GroupedListConfig config) {
        this.config = config;
        groupByCombo.removeAllItems();
        List<GroupByRelation> l = config.getRelations();
        for (GroupByRelation gba : l) {
            groupByCombo.addItem(gba);
        }
        if (l.size() > 0) {
            groupBy = l.get(0);
        }
        else {
            groupBy = new GroupByRelation();
        }
        reGroup(groupBy, true);
    }


    /**
     * Returns the list model.
     * 
     * @return
     */
    public ListModel getModel() {
        return model;
    }


    /**
     * Sets the list model.
     * 
     * @param model
     */
    public void setModel(DefaultListModel model) {
        removeAllGroups();
        this.model = model;
        model.addListDataListener(this);
        reGroup(groupBy, true);
    }


    /**
     * Regroups the list.
     * 
     * @param groupBy
     */
    private void reGroup(GroupByRelation groupBy, boolean resetViewPos) {
        this.groupBy = groupBy;
        removeAllGroups();
        if (model.getSize() > 0) {
            for (int i = 0; i < model.getSize(); i++) {
                addElem(model.getElementAt(i));
            }
            filterChanged();
            if (resetViewPos) {
                resetViewPosition();
            }
            forwardParameters();
        }
        else {
            repaint();
        }
    }


    /**
     * Adds an element to the model.
     * 
     * @return
     */
    public void addElement(Object o) {
        if (o != null) {
            model.addElement(o);
        }
    }


    /**
     * Removes an element from the model.
     * 
     * @return
     */
    public void removeElement(Object o) {
        if (o != null) {
            model.removeElement(o);
        }
    }


    /**
     * Retrives the selected value.
     * 
     * @return
     */
    public Object getSelectedValue() {
        Object result = null;
        for (EquivalenceClass group : groups.keySet()) {
            JXList l = getList(group);
            Object o = l.getSelectedValue();
            if (o != null) {
                result = o;
                break;
            }
        }
        return result;
    }


    /**
     * Retrives the selected elements.
     * 
     * @return
     */
    public List<Object> getSelectedValues() {
        List<Object> result = new ArrayList<Object>();
        for (EquivalenceClass group : groups.keySet()) {
            JXList l = getList(group);
            Object[] sv = l.getSelectedValues();
            for (Object o : sv) {
                result.add(o);
            }
        }
        return result;
    }


    /**
     * Retrives the selected elements.
     * 
     * @return
     */
    public List<?> getSelectedObjects() {
        return getSelectedValues();
    }


    /**
     * Adds a selection listener.
     * 
     * @param listener
     */
    public void addListSelectionListener(ListSelectionListener listener) {
        selectionListeners.add(listener);
    }


    /**
     * Removes all selection listeners.
     */
    public void removeAllSelectionChangeListeners() {
        selectionListeners.clear();
    }


    /**
     * Removes the selection listener.
     * 
     * @param listener
     * @return
     */
    public boolean removeSelectionChangeListener(ListSelectionListener listener) {
        return selectionListeners.remove(listener);
    }


    /**
     * Only one index is fired, and is adjusting, depending on the event of the
     * sublist. Because the GL can have more than one selected interval.
     */
    private void fireSelectionChangeListener(boolean valIsAdjusting) {
        if (selectionListeners.size() > 0) {
            final int idx = GroupedList.this.getSelectedIndex();
            final boolean valueIsAdjusting = valIsAdjusting;
            for (final ListSelectionListener listener : selectionListeners) {
                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        ListSelectionEvent ev = new ListSelectionEvent(GroupedList.this, idx, idx, valueIsAdjusting);
                        listener.valueChanged(ev);
                    }
                });
            }
        }
    }


    /**
     * Retrives the selected index in them model. You should use
     * getSelectedValue for performance reasons.
     * 
     * @return
     */
    public int getSelectedIndex() {
        int result = -1;
        Object o = getSelectedValue();
        if (o != null) {
            result = model.indexOf(o);
        }
        return result;
    }


    /**
     * Retrives the selected indices in them model. You should use
     * getSelectedValues for performance reasons.
     * 
     * @return
     */
    public List<Integer> getSelectedIndices() {
        List<Integer> result = new ArrayList<Integer>();
        List<Object> so = getSelectedValues();
        for (Object o : so) {
            result.add(new Integer(model.indexOf(o)));
        }
        return result;
    }


    /**
     * Returns the number of elements visible in the view. It can differ to the
     * number of model elements, if a filter is used.
     * 
     * @return
     */
    public int getElementCount() {
        int result = 0;
        for (EquivalenceClass group : groups.keySet()) {
            result += getList(group).getElementCount();
        }
        return result;
    }


    /**
     * Retrieves the elements visible, can differ from the items in model if a
     * filter is used.
     * 
     * @return
     */
    public List<Object> getVisibleElements() {
        List<Object> result = new ArrayList<Object>();
        for (EquivalenceClass group : groups.keySet()) {
            JXList l = getList(group);
            for (int i = 0; i < l.getElementCount(); i++) {
                result.add(l.getElementAt(i));
            }
        }
        return result;
    }


    /**
     * Returns the element with the given model index. In opposite to a JXList
     * the model index is used because no consistent view index exists.
     * 
     * @param modelIndex
     * @return
     */
    public Object getElementAt(int modelIndex) {
        return model.getElementAt(modelIndex);
    }


    /**
     * Clears the selection.
     */
    public void clearSelection() {
        for (EquivalenceClass group : groups.keySet()) {
            JXList l = getList(group);
            l.clearSelection();
        }
        fireSelectionChangeListener(false);
    }


    /**
     * Allows a selection on more than one sublist.
     * 
     * @param b
     */
    public void setMultiListSelectAllowed(boolean b) {
        allowMultiListSelect = b;
    }


    /**
     * Allows a selection on more than one sublist.
     * 
     * @param b
     */
    public boolean isMultiListSelectAllowed() {
        return allowMultiListSelect;
    }


    /**
     * Determines the select/deselect buttons on the group title to be shown.
     * 
     * @param b
     */
    public void setSelectActionsEnabled(boolean b) {
        addSelectActions = b;
    }


    /**
     * Determines the select/deselect buttons on the group title to be shown.
     * 
     * @param b
     */
    public boolean getSelectActionsEnabled() {
        return addSelectActions;
    }


    /**
     * Select all elements of a given group.
     * 
     * @param c
     */
    public void selectGroup(EquivalenceClass c) {
        if (groups.containsKey(c)) {
            JXList l = getList(c);
            l.setSelectionInterval(0, l.getElementCount() - 1);
            if (!allowMultiListSelect) {
                deselectOthers(c);
            }
        }
    }


    /**
     * Deselect all elements of a given group.
     * 
     * @param c
     */
    public void deSelectGroup(EquivalenceClass c) {
        if (groups.containsKey(c)) {
            JXList l = getList(c);
            l.setSelectionInterval(0, l.getElementCount() - 1);
            l.clearSelection();
        }
    }


    private JXList getList(EquivalenceClass group) {
        return (JXList) groups.get(group).getComponent();
    }


    @Override
    protected JPanel getTitlePanel() {
        JPanel result = new JPanel();
        result.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.LINE_START;
        c.insets = new Insets(0, 5, 0, 0);
        result.add(titleLabel, c);

        c = new GridBagConstraints();
        groupByCombo = new JComboBox();
        groupByCombo.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent evt) {
                JComboBox cb = (JComboBox) evt.getSource();
                Object o = cb.getSelectedItem();
                reGroup((GroupByRelation) o, true);
            }
        });
        c.gridx = 1;
        c.gridy = 0;
        c.anchor = GridBagConstraints.LINE_START;
        result.add(groupByCombo, c);

        c = new GridBagConstraints();
        c.gridx = 2;
        c.weightx = 1.0f;
        c.fill = GridBagConstraints.HORIZONTAL;
        result.add(new JPanel(), c);

        c = new GridBagConstraints();
        c.gridx = 3;
        c.weightx = 1.0f;
        c.fill = GridBagConstraints.HORIZONTAL;
        result.add(new JPanel(), c);

        c = new GridBagConstraints();
        c.gridx = 4;
        c.gridy = 0;
        c.anchor = GridBagConstraints.LINE_END;
        result.add(new TitleBtn(new ExpandAllAction(this)), c);

        c = new GridBagConstraints();
        c.gridx = 5;
        c.gridy = 0;
        c.anchor = GridBagConstraints.LINE_END;
        result.add(new TitleBtn(new CollapseAllAction(this)), c);

        result.setBorder(GroupSeparatorBorder.DEFAULT_GROUPS_CONTAINER_TITLE_BORDER);
        return result;
    }


    private class SubListMouseListener extends MouseAdapter {

        private EquivalenceClass subListKey;


        public SubListMouseListener(EquivalenceClass subListKey) {
            this.subListKey = subListKey;
        }


        @Override
        public void mousePressed(final MouseEvent e) {
            if (((e.getButton() == MouseEvent.BUTTON1))
                            && (!allowMultiListSelect || ((e.getModifiers() & InputEvent.SHIFT_MASK) != InputEvent.SHIFT_MASK))) {
                deselectOthers(subListKey);
            }
        }
    }


    /**
     * Method ensures that seleceted cell is visible when changing sublist with
     * Keyboard.
     * 
     * @param group
     * @param row
     */
    private void resetViewPositionToRowOnDownscroll(EquivalenceClass group, int row) {
        JXList l = getList(group);
        Rectangle rc = l.getCellBounds(row, row);
        rc = SwingUtilities.convertRectangle(l, rc, groupPanel);
        Rectangle vr = groupPanel.getVisibleRect();
        Point up = new Point(vr.x, rc.y);
        Point down = new Point(vr.x, rc.y + rc.height);
        if (!((vr.contains(up) && vr.contains(down)))) {
            int scrollStep = down.y - (vr.y + vr.height);
            Point p = new Point(vr.x, vr.y + scrollStep);
            groupScroll.getViewport().setViewPosition(p);
        }
    }


    /**
     * Method ensures that seleceted cell is visible when changing sublist with
     * Keyboard.
     * 
     * @param group
     * @param row
     */
    private void resetViewPositionToRowOnUpscroll(EquivalenceClass group, int row) {
        JXList l = getList(group);
        Rectangle rc = l.getCellBounds(row, row);
        rc = SwingUtilities.convertRectangle(l, rc, groupPanel);
        Rectangle vr = groupPanel.getVisibleRect();
        Point up = new Point(vr.x, rc.y);
        Point down = new Point(vr.x, rc.y + rc.height);
        if (!((vr.contains(up) && vr.contains(down)))) {
            if (row == 0) {
                GroupContainer gc = groups.get(group);
                Point p = new Point(vr.x, rc.y - gc.getTitlePanel().getHeight());
                groupScroll.getViewport().setViewPosition(p);
            }
            else {
                Point p = new Point(vr.x, rc.y);
                groupScroll.getViewport().setViewPosition(p);
            }
        }
    }


    private class SubListKeyListener extends KeyAdapter {

        private EquivalenceClass subListKey;


        public SubListKeyListener(EquivalenceClass subListKey) {
            this.subListKey = subListKey;
        }


        @Override
        public void keyPressed(KeyEvent e) {
            JXList thisList = getList(subListKey);
            if (((e.getKeyCode() == KeyEvent.VK_DOWN) || (e.getKeyCode() == KeyEvent.VK_PAGE_DOWN))
                            && (thisList.getSelectedIndex() != -1)
                            && (thisList.getMaxSelectionIndex() == thisList.getElementCount() - 1)) {
                EquivalenceClass next = getNextClass(subListKey);
                if (next != null) {
                    GroupContainer gc = groups.get(next);
                    gc.expand();

                    if (!allowMultiListSelect || ((e.getModifiers() & InputEvent.SHIFT_MASK) != InputEvent.SHIFT_MASK)) {
                        deselectOthersOnFocusGainedRequest = next;
                    }
                    JXList nl = getList(next);
                    nl.requestFocusInWindow();
                    nl.setSelectedIndex(0);

                    resetViewPositionToRowOnDownscroll(next, 0);
                }
            }
            if (((e.getKeyCode() == KeyEvent.VK_UP) || (e.getKeyCode() == KeyEvent.VK_PAGE_UP))
                            && (thisList.getSelectedIndex() != -1) && (thisList.getMinSelectionIndex() == 0)) {
                EquivalenceClass prev = getPrevClass(subListKey);
                if (prev != null) {
                    GroupContainer gc = groups.get(prev);
                    gc.expand();
                    if (!allowMultiListSelect || ((e.getModifiers() & InputEvent.SHIFT_MASK) != InputEvent.SHIFT_MASK)) {
                        deselectOthersOnFocusGainedRequest = prev;
                    }
                    JXList nl = getList(prev);
                    nl.requestFocusInWindow();
                    nl.setSelectedIndex(nl.getElementCount() - 1);

                    resetViewPositionToRowOnUpscroll(prev, nl.getElementCount() - 1);
                }
                else {
                    resetViewPosition();
                }
            }
        }
    }


    private EquivalenceClass getNextClass(EquivalenceClass key) {
        EquivalenceClass result = null;
        Object[] keys = groups.keySet().toArray();
        for (int i = 0; i < keys.length; i++) {
            if (keys[i].equals(key) && ((i + 1) < keys.length)) {
                result = (EquivalenceClass) keys[i + 1];
            }
        }
        return result;
    }


    private EquivalenceClass getPrevClass(EquivalenceClass key) {
        EquivalenceClass result = null;
        Object[] keys = groups.keySet().toArray();
        for (int i = 0; i < keys.length; i++) {
            if (keys[i].equals(key) && ((i - 1) >= 0)) {
                result = (EquivalenceClass) keys[i - 1];
            }
        }
        return result;
    }


    private void deselectOthers(EquivalenceClass key) {
        for (EquivalenceClass group : groups.keySet()) {
            if (!group.equals(key)) {
                JXList l = getList(group);
                l.clearSelection();
            }
        }
    }


    private void deselectOthers(JXList list) {
        for (EquivalenceClass group : groups.keySet()) {
            JXList l = getList(group);
            if (!l.equals(list)) {
                l.clearSelection();
            }
        }
    }


    private EquivalenceClass getGroupKeyOfList(JXList l) {
        EquivalenceClass result = null;
        for (EquivalenceClass group : groups.keySet()) {
            if (getList(group).equals(l)) {
                result = group;
                break;
            }
        }
        return result;
    }


    /**
     * FocusListner of the sublist instances.
     * 
     * @param arg0
     */
    @Override
    public void focusGained(FocusEvent e) {
        if (deselectOthersOnFocusGainedRequest != null) {
            deselectOthers(deselectOthersOnFocusGainedRequest);
            deselectOthersOnFocusGainedRequest = null;
        }
        if (e.getSource() instanceof JXList) {
            JXList l = (JXList) e.getSource();
            if (l.getSelectedIndex() == -1) {
                l.setSelectedIndex(0);
                deselectOthers(l);
                EquivalenceClass ec = getGroupKeyOfList(l);
                if (ec != null) {
                    resetViewPositionToRowOnDownscroll(ec, 0);
                }
            }
        }
    }


    /**
     * FocusListner of the sublist instances.
     * 
     * @param arg0
     */
    @Override
    public void focusLost(FocusEvent arg0) {
    }


    private void addNewGroup(EquivalenceClass key) {
        JXList l = config.getEmptyListInstance();
        l.setModel(config.getEmptyModel());
        l.addMouseListener(new SubListMouseListener(key));
        l.addKeyListener(new SubListKeyListener(key));
        l.addListSelectionListener(this);
        l.addFocusListener(this);
        l.setVerifyInputWhenFocusTarget(false);
        forwardParameters(l);
        List<Action> additionalActions = null;
        if (addSelectActions) {
            additionalActions = new ArrayList<Action>();
            additionalActions.add(new SelectGroupAction(this, key));
            additionalActions.add(new DeselectGroupAction(this, key));
        }
        GroupContainer ngc = new GroupContainer(key.getName(), key.getIcon(), l, additionalActions);
        groups.put(key, ngc);
        if (hideGroupTitleForTheAllGroup && key.equals(GroupByRelation.ALL)) {
            ngc.getTitlePanel().setVisible(false);
        }
        super.removeAllGroups();
        for (EquivalenceClass s : groups.keySet()) {
            GroupContainer gc = groups.get(s);
            addGroupContainer(gc);
        }
    }


    /**
     * Method inherited form GroupsContainer.
     */
    @Override
    public void removeAllGroups() {
        for (EquivalenceClass group : groups.keySet()) {
            JXList l = getList(group);
            l.removeListSelectionListener(this);
            l.removeFocusListener(this);
            config.removingListInstance(l);
        }
        super.removeAllGroups();
        groups.clear();
    }


    @Override
    public void collapseAll() {
        if (!(hideGroupTitleForTheAllGroup && groups.containsKey(GroupByRelation.ALL))) {
            super.collapseAll();
        }
    }


    private EquivalenceClass getOthersClass() {
        EquivalenceClass result = new EquivalenceClass(Resources.S_OTHER + " " + groupBy.getName(), null);
        result.setLastGroup(true);
        return result;
    }


    private void addElem(Object o) {
        if (o != null) {
            EquivalenceClass group = groupBy.assignObject(o);
            if (!groups.containsKey(group)) {
                if (!USE_EQUIVALENCE_CLASS_LIMIT) {
                    addNewGroup(group);
                }
                else {
                    if (groups.keySet().size() < EQUIVALENCE_CLASS_LIMIT) {
                        addNewGroup(group);
                    }
                    else {
                        group = getOthersClass();
                        if (!groups.containsKey(group)) {
                            addNewGroup(group);
                        }
                    }
                }
            }
            JXList l = (JXList) groups.get(group).getComponent();
            DefaultListModel dlm = (DefaultListModel) l.getModel();
            dlm.addElement(o);
        }
    }


    @Override
    public void contentsChanged(ListDataEvent arg0) {
        reGroup(groupBy, false);
    }


    @Override
    public void intervalAdded(ListDataEvent arg0) {
        for (int i = arg0.getIndex0(); i <= arg0.getIndex1(); i++) {
            addElem(model.getElementAt(i));
        }
    }


    @Override
    public void intervalRemoved(ListDataEvent arg0) {
        reGroup(groupBy, false);
    }


    private void filterChanged() {
        for (EquivalenceClass group : groups.keySet()) {
            GroupContainer gc = groups.get(group);
            JXList l = (JXList) gc.getComponent();
            if (l.getElementCount() > 0) {
                gc.setVisible(true);
            }
            else {
                gc.setVisible(false);
            }
        }
    }


    private void forwardParameters() {
        for (EquivalenceClass group : groups.keySet()) {
            forwardParameters(getList(group));
        }
    }


    /*
     * Parameters which are forwarded to the SubList instances.
     */
    private void forwardParameters(JXList l) {
        l.setAutoCreateRowSorter(autoCreateRowSorter);
        l.setRowFilter(filter);
        l.setSortOrder(sortOrder);
        l.setComparator(comparator);
    }


    /**
     * Forwarded parameter/method see JXList.
     * 
     * @return
     */
    public boolean isAutoCreateRowSorter() {
        return autoCreateRowSorter;
    }


    /**
     * Forwarded parameter/method see JXList.
     * 
     * @return
     */
    public void setAutoCreateRowSorter(boolean autoCreateRowSorter) {
        this.autoCreateRowSorter = autoCreateRowSorter;
        forwardParameters();
    }


    /**
     * Forwarded parameter/method see JXList.
     * 
     * @return
     */
    public RowFilter<? super ListModel, ? super Integer> getRowFilter() {
        return filter;
    }


    /**
     * Forwarded parameter/method see JXList.
     * 
     * @return
     */
    public void setRowFilter(RowFilter<? super ListModel, ? super Integer> filter) {
        this.filter = filter;
        forwardParameters();
        filterChanged();
    }


    /**
     * Forwarded parameter/method see JXList.
     * 
     * @return
     */
    public SortOrder getSortOrder() {
        return sortOrder;
    }


    /**
     * Forwarded parameter/method see JXList.
     * 
     * @return
     */
    public void setSortOrder(SortOrder sortOrder) {
        this.sortOrder = sortOrder;
        forwardParameters();
    }


    /**
     * Forwarded parameter/method see JXList.
     * 
     * @return
     */
    public Comparator<?> getComparator() {
        return comparator;
    }


    /**
     * Forwarded parameter/method see JXList.
     * 
     * @return
     */
    public void setComparator(Comparator<?> comparator) {
        this.comparator = comparator;
        forwardParameters();
    }


    /**
     * Forwarded parameter/method see JXList.
     * 
     * @return
     */
    public void toggleSortOrder() {
        for (EquivalenceClass group : groups.keySet()) {
            getList(group).toggleSortOrder();
        }
    }


    @Override
    public void valueChanged(ListSelectionEvent e) {
        this.fireSelectionChangeListener(e.getValueIsAdjusting());
    }
}
