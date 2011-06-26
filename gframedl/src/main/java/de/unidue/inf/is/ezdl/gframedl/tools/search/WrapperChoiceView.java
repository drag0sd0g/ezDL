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

package de.unidue.inf.is.ezdl.gframedl.tools.search;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EventObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListDataListener;

import org.jdesktop.swingx.JXList;

import de.unidue.inf.is.ezdl.dlcore.data.wrappers.FrontendWrapperInfo;
import de.unidue.inf.is.ezdl.dlfrontend.dispatcher.Dispatcher;
import de.unidue.inf.is.ezdl.dlfrontend.dispatcher.EventReceiver;
import de.unidue.inf.is.ezdl.dlfrontend.helper.FrontendWrapperInfoComparator;
import de.unidue.inf.is.ezdl.dlfrontend.i18n.I18nSupport;
import de.unidue.inf.is.ezdl.gframedl.Config;
import de.unidue.inf.is.ezdl.gframedl.components.FilterTextField;
import de.unidue.inf.is.ezdl.gframedl.components.JComboBoxVAlign;
import de.unidue.inf.is.ezdl.gframedl.components.TextComponentPopupMenu;
import de.unidue.inf.is.ezdl.gframedl.components.actions.AbstractContentActivatedAction;
import de.unidue.inf.is.ezdl.gframedl.components.actions.ContentGetter;
import de.unidue.inf.is.ezdl.gframedl.components.checkboxlist.CheckBoxJList;
import de.unidue.inf.is.ezdl.gframedl.components.checkboxlist.CheckBoxListCellRenderer;
import de.unidue.inf.is.ezdl.gframedl.components.checkboxlist.CheckBoxListItem;
import de.unidue.inf.is.ezdl.gframedl.components.checkboxlist.CheckBoxListModel;
import de.unidue.inf.is.ezdl.gframedl.events.ExitEvent;
import de.unidue.inf.is.ezdl.gframedl.events.GFrameEvent;
import de.unidue.inf.is.ezdl.gframedl.tools.AbstractToolView;
import de.unidue.inf.is.ezdl.gframedl.tools.Tool;
import de.unidue.inf.is.ezdl.gframedl.tools.search.actions.Actions;



/**
 * A view that has the available digital libraries displayed along with a
 * checkbox and a description, grouped into several categories, switchable by a
 * {@link JComboBoxVAlign}.
 * 
 * @author mjordan
 */
public final class WrapperChoiceView extends AbstractToolView implements EventReceiver, ContentGetter {

    private static final long serialVersionUID = 1931730376406509670L;

    private static final String WRAPPER_PROPERTY_PREFIX = "searchtool.dls.wrapper.";
    private static final String EMPTY_WRAPPER_LIST = "wrapper_list_empty";

    public static final String EMPTY = "";

    /**
     * Defines which is the biggest proposed timeout acceptable for any wrapper
     * to be checked by default on the first start. E.g. if this is 10 then, for
     * new installations, only wrappers are checked that answer faster than
     * about 10 seconds.
     */
    private static final int MAX_TIMEOUT_FOR_DEFAULT_WRAPPERS_S = 10;

    private JXList list;
    private JPanel filterPanel;
    private JComboBox wrapperCategoryCombobox;
    private CheckBoxListModel model;
    private JButton refreshButton;

    private Set<String> wrappersFromLastSession;

    /**
     * The label of the "all" category of wrappers - i.e. the one that contains
     * all wrappers of all categories.
     */
    private String categoryLabelAll;


    private class WrapperSelections {

        private class LocalInfo {

            public FrontendWrapperInfo info;
            public boolean currentlyPresent;
            public boolean selected;
        }


        private Map<String, LocalInfo> allWrappersSelections = new HashMap<String, LocalInfo>();


        private String key(FrontendWrapperInfo info) {
            return info.getId();
        }


        public boolean isSelected(FrontendWrapperInfo wrapper) {
            return allWrappersSelections.get(key(wrapper)).selected;
        }


        public boolean contains(FrontendWrapperInfo wrapper) {
            return allWrappersSelections.containsKey(key(wrapper));
        }


        public void put(FrontendWrapperInfo wrapper, boolean selected) {
            LocalInfo info = new LocalInfo();
            info.info = wrapper;
            info.selected = selected;
            allWrappersSelections.put(key(wrapper), info);
        }


        public void update(FrontendWrapperInfo wrapper) {
            LocalInfo info = allWrappersSelections.get(key(wrapper));
            info.info = wrapper;
            allWrappersSelections.put(key(wrapper), info);
        }


        public void update(List<FrontendWrapperInfo> wrapperList, Set<String> selectIfNew) {
            for (FrontendWrapperInfo wrapper : wrapperList) {
                if (!contains(wrapper)) {
                    final boolean wrapperPreviouslySelected = selectIfNew.contains(wrapper.getId());
                    put(wrapper, wrapperPreviouslySelected);
                }
                else {
                    update(wrapper);
                }
            }

            for (LocalInfo wrapper : allWrappersSelections.values()) {
                wrapper.currentlyPresent = containsWrapper(wrapperList, wrapper);
            }
        }


        private boolean containsWrapper(List<FrontendWrapperInfo> wrapperList, LocalInfo wrapper) {
            for (FrontendWrapperInfo info : wrapperList) {
                if (info.getId().equals(wrapper.info.getId())) {
                    return true;
                }
            }
            return false;
        }


        public void setSelected(String value, boolean selected) {
            LocalInfo info = allWrappersSelections.get(value);
            info.selected = selected;
        }


        public List<FrontendWrapperInfo> getActive() {
            List<FrontendWrapperInfo> list = new LinkedList<FrontendWrapperInfo>();
            for (LocalInfo info : allWrappersSelections.values()) {
                if (info.currentlyPresent) {
                    list.add(info.info);
                }
            }
            return list;
        }


        public List<FrontendWrapperInfo> getSelected() {
            List<FrontendWrapperInfo> list = new LinkedList<FrontendWrapperInfo>();
            for (LocalInfo info : allWrappersSelections.values()) {
                if (info.selected) {
                    list.add(info.info);
                }
            }
            return list;
        }


        public List<FrontendWrapperInfo> getUnselected() {
            List<FrontendWrapperInfo> list = new LinkedList<FrontendWrapperInfo>();
            for (LocalInfo info : allWrappersSelections.values()) {
                if (!info.selected) {
                    list.add(info.info);
                }
            }
            return list;
        }
    }


    private WrapperSelections selections;


    public WrapperChoiceView(Tool parentTool, int index) {
        super(parentTool, index);
        setName(I18nSupport.getInstance().getLocString(SearchTool.I18N_PREFIX + "wrapper"));
        init();
    }


    public void init() {
        categoryLabelAll = I18nSupport.getInstance().getLocString(SearchTool.I18N_PREFIX + "wrapper.categoryAll");

        setLayout(new BorderLayout());
        Dispatcher.registerInterest(this, GFrameEvent.class);

        refreshButton = new JButton();
        refreshButton.setAction(Actions.WRAPPER_UPDATE_ACTION);

        JScrollPane scrollPane = new JScrollPane();

        list = new CheckBoxJList();
        FilterTextField filterTextField = new FilterTextField(list);
        list.setCellRenderer(new CheckBoxListCellRenderer(filterTextField));
        model = new CheckBoxListModel();
        list.setModel(model);

        TextComponentPopupMenu.addPopupMenu(filterTextField);
        filterTextField.setColumns(12);

        scrollPane.getViewport().add(list);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setPreferredSize(new Dimension(300, 40));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));

        initSelectButtons(buttonPanel);

        selections = new WrapperSelections();
        wrappersFromLastSession = wrappersFromLastSession();

        wrapperCategoryCombobox = new JComboBoxVAlign(refreshButton);
        wrapperCategoryCombobox.setAction(Actions.WRAPPER_CATEGORY_ACTION);

        filterPanel = new JPanel(new GridBagLayout());
        filterPanel.setPreferredSize(new Dimension(300, 40));
        filterPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0;
        c.weighty = 0;
        c.anchor = GridBagConstraints.LINE_START;
        filterPanel.add(refreshButton, c);
        c.gridx = 1;
        c.gridy = 0;
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0;
        c.weighty = 0;
        c.anchor = GridBagConstraints.LINE_START;
        filterPanel.add(wrapperCategoryCombobox, c);
        c.gridx = 2;
        c.gridy = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.weighty = 0;
        c.anchor = GridBagConstraints.LINE_START;
        filterPanel.add(filterTextField, c);

        add(scrollPane, BorderLayout.CENTER);
        add(filterPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.SOUTH);
    }


    private void initSelectButtons(JPanel buttonPanel) {
        JButton selectAll = new JButton(new SelectAction(this, true));
        buttonPanel.add(selectAll);

        JButton selectNone = new JButton(new SelectAction(this, false));
        buttonPanel.add(selectNone);
    }


    private Set<String> wrappersFromLastSession() {
        Set<String> propertyWrapper = new TreeSet<String>();

        Set<Entry<Object, Object>> wrapperProperties = Config.getInstance().refreshProperties().entrySet();
        for (Entry<Object, Object> wrapperProperty : wrapperProperties) {
            String propertyKey = wrapperProperty.getKey().toString();
            if (propertyKey.startsWith(WRAPPER_PROPERTY_PREFIX)) {
                propertyWrapper.add(wrapperProperty.getValue().toString());
            }
        }

        return propertyWrapper;
    }


    private boolean useInitialDefaultWrappers() {
        String listEmptyProp = Config.getInstance().getUserProperty(WRAPPER_PROPERTY_PREFIX + EMPTY_WRAPPER_LIST);
        boolean wrapperListIntentionallyCleared = (listEmptyProp != null);
        boolean setDefaultWrappers = (wrappersFromLastSession.isEmpty() && !wrapperListIntentionallyCleared);
        return setDefaultWrappers;
    }


    private Set<String> getInitialDefaultWrappers(List<FrontendWrapperInfo> wrapperList) {
        Set<String> defaultWrappers = new HashSet<String>();
        for (FrontendWrapperInfo wrapper : wrapperList) {
            if (wrapper.getProposedTimeoutSec() <= MAX_TIMEOUT_FOR_DEFAULT_WRAPPERS_S) {
                defaultWrappers.add(wrapper.getId());
            }
        }

        return defaultWrappers;
    }


    private String getSelectedCategory() {
        String selectedCategory = null;
        if (wrapperCategoryCombobox != null) {
            selectedCategory = (String) wrapperCategoryCombobox.getSelectedItem();
            if (selectedCategory == null) {
                selectedCategory = (String) wrapperCategoryCombobox.getItemAt(0);
            }
        }
        return selectedCategory;
    }


    @Override
    public String getToolViewName() {
        return I18nSupport.getInstance().getLocString(SearchTool.I18N_PREFIX + "wrapper.name");
    }


    /**
     * Updates the view after a mere change of category.
     * <p>
     * No wrapper information is reloaded, no message sent.
     * 
     * @param wrapperCategory
     *            the new category.
     */
    public void update(String wrapperCategory) {
        updateCheckedWrappers();
        model.setItems(buildWrapperCheckboxList());
        repaint();
    }


    /**
     * Updates the view after new wrapper information has been delivered by the
     * backend.
     * 
     * @param wrapperList
     *            the list of wrapper information objects
     */
    public void update(List<FrontendWrapperInfo> wrapperList) {
        final Set<String> selectIfNew = calcPreselections(wrapperList);
        selections.update(wrapperList, selectIfNew);
        updateCheckedWrappers();

        updateCategoryComboBox(wrapperList);

        model.setItems(buildWrapperCheckboxList());
        repaint();

        if ((wrapperList != null) && !wrapperList.isEmpty()) {
            setPersistentWrapperlistIntentionallyEmpty();
        }
    }


    private List<CheckBoxListItem> buildWrapperCheckboxList() {
        List<CheckBoxListItem> wrapperCheckBoxListItems = new ArrayList<CheckBoxListItem>();

        final String category = getSelectedCategory();
        final boolean categoryAll = category.equals(categoryLabelAll);
        wrapperCheckBoxListItems.clear();

        List<FrontendWrapperInfo> allWrappers = selections.getActive();
        Collections.sort(allWrappers, new FrontendWrapperInfoComparator());

        for (FrontendWrapperInfo wrapper : allWrappers) {
            final boolean categoryFits = wrapper.getCategory().equals(category);
            if (categoryFits || categoryAll) {

                ImageIcon icon = (ImageIcon) wrapper.getLargeIcon();

                final CheckBoxListItem checkbox = new CheckBoxListItem(wrapper.getRemoteName(), wrapper.getId(),
                                wrapper.getDescription(), icon);

                checkbox.setSelected(selections.isSelected(wrapper));
                wrapperCheckBoxListItems.add(checkbox);
            }
        }
        return wrapperCheckBoxListItems;
    }


    private void updateCategoryComboBox(List<FrontendWrapperInfo> wrapperList) {
        String[] categories = initCategories(wrapperList);
        wrapperCategoryCombobox.setModel(new DefaultComboBoxModel(categories));
        String category = getSelectedCategory();
        wrapperCategoryCombobox.setSelectedItem(category);
    }


    private String[] initCategories(List<FrontendWrapperInfo> wrapperList) {
        List<String> wrapperCategories = new ArrayList<String>();

        for (FrontendWrapperInfo wrapper : wrapperList) {
            if (!wrapperCategories.contains(wrapper.getCategory())) {
                wrapperCategories.add(wrapper.getCategory());
            }
        }

        if (wrapperCategories.isEmpty()) {
            wrapperCategories.add(I18nSupport.getInstance().getLocString(SearchTool.I18N_PREFIX + "wrapper.empty"));
        }
        else if (wrapperCategories.size() > 1) {
            wrapperCategories.add(0, categoryLabelAll);
        }

        return wrapperCategories.toArray(new String[wrapperCategories.size()]);
    }


    private Set<String> calcPreselections(List<FrontendWrapperInfo> wrapperList) {
        Set<String> selectIfNew = new TreeSet<String>();
        selectIfNew.addAll(wrappersFromLastSession);
        if (useInitialDefaultWrappers()) {
            Set<String> defaultWrappers = getInitialDefaultWrappers(wrapperList);
            selectIfNew.addAll(defaultWrappers);
        }
        return selectIfNew;
    }


    private CheckBoxListModel getModel() {
        return (CheckBoxListModel) list.getModel();
    }


    private void updateCheckedWrappers() {
        List<CheckBoxListItem> items = getModel().getItems();
        if (items != null) {
            for (CheckBoxListItem item : items) {
                selections.setSelected(item.getValue(), item.isSelected());
            }
        }
    }


    /**
     * Returns the IDs of the checked wrappers.
     * 
     * @return the IDs of the checked wrappers
     */
    public List<String> getCheckedWrappers() {
        List<String> result = new ArrayList<String>();
        List<FrontendWrapperInfo> selected = getCheckedWrapperInfo();
        for (FrontendWrapperInfo item : selected) {
            result.add(item.getId());
        }
        return result;
    }


    /**
     * Returns the full information objects on the checked wrappers.
     * 
     * @return the full information on the checked wrappers
     */
    public List<FrontendWrapperInfo> getCheckedWrapperInfo() {
        updateCheckedWrappers();
        return selections.getSelected();
    }


    /**
     * Check the wrappers with the given IDs.
     * 
     * @param checkedDLs
     *            the IDs of the wrappers to check
     */
    public void setCheckedWrappers(List<String> checkedDLs) {
        for (int i = 0; i < getModel().getSize(); i++) {
            CheckBoxListItem c = (CheckBoxListItem) getModel().getElementAt(i);
            c.setSelected(checkedDLs.contains(c.getValue()));
        }
        list.repaint();
    }


    @Override
    public boolean handleEzEvent(EventObject ev) {
        if (ev instanceof ExitEvent) {

            updateCheckedWrappers();

            final List<FrontendWrapperInfo> selected = selections.getSelected();
            for (FrontendWrapperInfo wrapper : selected) {
                final String checkedWrapper = wrapper.getId();
                Config.getInstance().setUserProperty(WRAPPER_PROPERTY_PREFIX + checkedWrapper, checkedWrapper);
            }

            final List<FrontendWrapperInfo> unselected = selections.getUnselected();
            for (FrontendWrapperInfo wrapper : unselected) {
                final String checkedWrapper = wrapper.getId();
                Config.getInstance().removeUserProperty(WRAPPER_PROPERTY_PREFIX + checkedWrapper);
            }

            if (selected.isEmpty()) {
                setPersistentWrapperlistIntentionallyEmpty();
            }
            else {
                clearPersistentWrapperlistIntentionallyEmpty();
            }

            Config.getInstance().writeUserPreferences();
        }
        return true;
    }


    private void setPersistentWrapperlistIntentionallyEmpty() {
        Config.getInstance().setUserProperty(WRAPPER_PROPERTY_PREFIX + EMPTY_WRAPPER_LIST, "true");
    }


    private void clearPersistentWrapperlistIntentionallyEmpty() {
        Config.getInstance().removeUserProperty(WRAPPER_PROPERTY_PREFIX + EMPTY_WRAPPER_LIST);
    }


    private void checkItems(boolean checked) {
        for (CheckBoxListItem item : model.getItems()) {
            item.setSelected(checked);
        }

        repaint();
    }


    /**
     * This action performs the selection of all/none of the DLs offered. It
     * deactivates itself if no wrappers are available.
     * 
     * @author mjordan
     */
    private class SelectAction extends AbstractContentActivatedAction {

        private static final long serialVersionUID = 1L;
        private boolean checked;


        public SelectAction(ContentGetter client, boolean checked) {
            super(client);
            final String key;
            if (checked) {
                key = "selectall";
            }
            else {
                key = "selectnone";
            }
            String name = I18nSupport.getInstance().getLocString(SearchTool.I18N_PREFIX + "wrapper." + key);

            putValue(Action.NAME, name);

            this.checked = checked;
        }


        @Override
        public void actionPerformed(ActionEvent e) {
            checkItems(checked);
        }

    }


    @Override
    public void addListDataListener(ListDataListener listener) {
        list.getModel().addListDataListener(listener);
    }


    @Override
    public List<?> getContentObjects() {
        return model.getItems();
    }

}
