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

package de.unidue.inf.is.ezdl.gframedl.transfer;

import java.awt.Component;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;
import javax.swing.TransferHandler;
import javax.swing.text.JTextComponent;

import org.apache.log4j.Logger;

import de.unidue.inf.is.ezdl.dlcore.data.dldata.Person;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.Term;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.Year;
import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;
import de.unidue.inf.is.ezdl.dlcore.data.query.DefaultQuery;
import de.unidue.inf.is.ezdl.dlcore.data.query.HistoricQuery;
import de.unidue.inf.is.ezdl.dlcore.data.query.Query;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNode;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeBool;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeBool.NodeType;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeCompare;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.transformation.flatten.FlattenerTransformer;
import de.unidue.inf.is.ezdl.dlfrontend.i18n.I18nSupport;
import de.unidue.inf.is.ezdl.dlfrontend.query.QueryContainer;
import de.unidue.inf.is.ezdl.gframedl.tools.search.SearchTool;
import de.unidue.inf.is.ezdl.gframedl.tools.search.panels.queryviews.QueryView;



/**
 * Handles dropping objects into a query form field.
 * <p>
 * A popup menu is shown with a few options:
 * <ul>
 * <li>AND</li>
 * <li>OR</li>
 * <li>AND NOT</li>
 * <li>verbatim</li>
 * <li>abort</li>
 * </ul>
 * If the user presses CTRL when choosing the option, the query is also
 * simplified using the {@link FlattenerTransformer}.
 * 
 * @author mjordan
 * @author franitza
 */
public class QueryFieldTransferHandler extends TransferHandler {

    private static final long serialVersionUID = 927873729411144850L;
    private static final Logger logger = Logger.getLogger(QueryFieldTransferHandler.class.getName());

    /**
     * The i18n property key prefix.
     */
    private static final String PREFIX = "querycombine.";
    /**
     * The i18n key suffix for the AND option.
     */
    private static final String MENU_ITEM_AND = "and";
    /**
     * The i18n key suffix for the OR option.
     */
    private static final String MENU_ITEM_OR = "or";
    /**
     * The i18n key suffix for the AND NOT option.
     */
    private static final String MENU_ITEM_ANDNOT = "andnot";
    /**
     * The i18n key suffix for the VERBATIM option.
     */
    private static final String MENU_ITEM_VERBATIM = "verbatim";
    /**
     * The i18n key suffix for the ABORT option.
     */
    private static final String MENU_ITEM_ABORT = "abort";


    /**
     * Keeps information about the dropped objects and the query in the search
     * tool at that very moment.
     * 
     * @author mjordan
     */
    private class DropSituation {

        /**
         * The query in the search tool.
         */
        public Query searchToolQuery;
        /**
         * The dropped queries.
         */
        public List<Query> droppedQueries;


        /**
         * Returns a list starting with the search tool query and ending with
         * all the queries that were dropped into the query field.
         * 
         * @return the full query list
         */
        public List<Query> getFullList() {
            List<Query> full = new LinkedList<Query>();
            if (!isQueryEmpty(searchToolQuery)) {
                full.add(searchToolQuery);
            }
            full.addAll(droppedQueries);
            return full;
        }
    }


    /**
     * The view to update after a successful transfer.
     */
    private QueryView view;
    /**
     * The default field of the query field this handler is working for.
     */
    private Field field;
    /**
     * Reference to the object that contains the query to be modified.
     */
    private QueryContainer queryContainer;


    /**
     * Creates a new handler with the given parameters.
     * 
     * @param textComponent
     *            the query field the handler is working for
     * @param field
     *            the default field code of that query field
     * @param queryContainer
     *            the object that holds the query that is to be modified
     * @param view
     *            the view that should handle the popup
     */
    public QueryFieldTransferHandler(JTextComponent textComponent, Field field, QueryContainer queryContainer,
                    QueryView view) {
        this.view = view;
        this.field = field;
        this.queryContainer = queryContainer;
    }


    @Override
    public boolean canImport(TransferSupport support) {
        return DataFlavors.canImport(support, DataFlavor.stringFlavor, DataFlavors.TERM, DataFlavors.AUTHOR,
                        DataFlavors.YEAR, DataFlavors.DL_OBJECT);
    }


    @Override
    public boolean importData(TransferSupport support) {
        final Transferable transferable = support.getTransferable();
        final DropSituation ds = calcQueryList(transferable);

        if (ds.droppedQueries.size() == 0) {
            // don't do jack.
        }
        if (isQueryEmpty(ds.searchToolQuery) && (ds.droppedQueries.size() == 1)) {
            queryContainer.setQuery(ds.droppedQueries.get(0));
        }
        else {
            showPopupMenu(support, ds);
        }
        return true;
    }


    private boolean isQueryEmpty(Query query) {
        return (query == null) || (query.getTree() == null);
    }


    /**
     * Calculates the list of queries that we're working with: the list of query
     * objects inside the {@link Transferable} and the query in the
     * {@link SearchTool}, if it exists.
     * 
     * @param transferable
     *            the objects dropped
     * @return
     */
    @SuppressWarnings("unchecked")
    private DropSituation calcQueryList(Transferable transferable) {
        final DropSituation ds = new DropSituation();
        ds.searchToolQuery = queryContainer.getQuery();
        ds.droppedQueries = new LinkedList<Query>();

        try {
            if (transferable.isDataFlavorSupported(DataFlavors.DL_OBJECT)) {
                Object td = transferable.getTransferData(DataFlavors.DL_OBJECT);
                if (td instanceof List<?>) {
                    List<Object> list = (List<Object>) td;
                    for (Object object : list) {
                        if (object instanceof HistoricQuery) {
                            final HistoricQuery historicQuery = (HistoricQuery) object;
                            final Query innerQuery = historicQuery.getQuery();
                            addQuery(ds.droppedQueries, innerQuery);
                        }
                        else if (object instanceof Term) {
                            final Term term = (Term) object;
                            addStringQuery(ds.droppedQueries, term.asString());
                        }
                        else if (object instanceof Person) {
                            final Person term = (Person) object;
                            addStringQuery(ds.droppedQueries, term.asString());
                        }
                        else if (object instanceof Year) {
                            final Year term = (Year) object;
                            addStringQuery(ds.droppedQueries, term.asString());
                        }
                    }
                }
            }
            else if (transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                final String str = (String) transferable.getTransferData(DataFlavor.stringFlavor);
                addStringQuery(ds.droppedQueries, str);
            }
        }
        catch (UnsupportedFlavorException e) {
            logger.error(e.getMessage(), e);
        }
        catch (IOException e) {
        }

        return ds;
    }


    private void addStringQuery(final List<Query> queryList, final String str) {
        final QueryNodeCompare comp = new QueryNodeCompare(field, str);
        final Query query = new DefaultQuery(comp);
        addQuery(queryList, query);
    }


    private void addQuery(final List<Query> queryList, final Query query) {
        if ((query != null) && (query.getTree() != null)) {
            queryList.add(query);
        }
    }


    @SuppressWarnings("unused")
    private void addQueryAlways(final List<Query> queryList, final Query query) {
        if ((query != null) && (query.getTree() != null)) {
            queryList.add(query);
        }
        else {
            final Query newQuery = new DefaultQuery();
            queryList.add(newQuery);
        }
    }


    private void showPopupMenu(TransferSupport support, DropSituation ds) {
        JPopupMenu popup;
        MenuListener menuListener;

        popup = new JPopupMenu();
        menuListener = new MenuListener(support, ds);

        JMenuItem item;
        popup.add(item = new JMenuItem(i18nName(MENU_ITEM_AND)));
        item.setHorizontalTextPosition(SwingConstants.RIGHT);
        item.addActionListener(menuListener);
        popup.add(item = new JMenuItem(i18nName(MENU_ITEM_OR)));
        item.setHorizontalTextPosition(SwingConstants.RIGHT);
        item.addActionListener(menuListener);
        popup.add(item = new JMenuItem(i18nName(MENU_ITEM_ANDNOT)));
        item.setHorizontalTextPosition(SwingConstants.RIGHT);
        item.addActionListener(menuListener);
        if (support.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            popup.add(item = new JMenuItem(i18nName(MENU_ITEM_VERBATIM)));
            item.setHorizontalTextPosition(SwingConstants.RIGHT);
            item.addActionListener(menuListener);
        }

        popup.addSeparator();
        popup.add(item = new JMenuItem(i18nName(MENU_ITEM_ABORT)));
        item.addActionListener(menuListener);

        final Component component = support.getComponent();
        popup.show((JComponent) view, component.getX(), component.getY() + component.getHeight() + 1);
    }


    private String i18nName(String name) {
        return I18nSupport.getInstance().getLocString(PREFIX + name);
    }


    /**
     * Handles the menu choice.
     * 
     * @author mjordan
     */
    class MenuListener implements ActionListener {

        private DropSituation ds;
        private TransferSupport support;


        /**
         * Creates the listener with the given transfer support object for the
         * also given drop situation.
         * 
         * @param support
         *            the transfer support object for the drop
         * @param ds
         *            the object with information about the dropped objects and
         *            the query
         */
        public MenuListener(TransferSupport support, DropSituation ds) {
            this.ds = ds;
            this.support = support;
        }


        private void update(QueryNode parentNode, QueryNodeBool targetNode, List<Query> toAdd, boolean flatten) {
            for (Query object : toAdd) {
                if (object instanceof Query) {
                    final Query query = object;
                    targetNode.addChild(query.getTree());
                }
            }
            if (flatten) {
                final FlattenerTransformer flattener = new FlattenerTransformer();
                final QueryNode newRoot = flattener.transform(parentNode);
                parentNode = newRoot;
            }
            final Query query = new DefaultQuery(parentNode);
            queryContainer.setQuery(query);
        }


        @Override
        public void actionPerformed(ActionEvent e) {

            if (e.getActionCommand().equals(i18nName(MENU_ITEM_AND))) {
                final QueryNodeBool andNode = new QueryNodeBool(NodeType.AND);
                final boolean flatten = flattenQuery(e);
                update(andNode, andNode, ds.getFullList(), flatten);
            }
            else if (e.getActionCommand().equals(i18nName(MENU_ITEM_OR))) {
                final QueryNodeBool orNode = new QueryNodeBool(NodeType.OR);
                final boolean flatten = flattenQuery(e);
                update(orNode, orNode, ds.getFullList(), flatten);
            }
            else if (e.getActionCommand().equals(i18nName(MENU_ITEM_ANDNOT))) {
                QueryNode root = null;
                final QueryNodeBool not = new QueryNodeBool(NodeType.AND);
                not.setNegated(true);
                root = not;
                if (!isQueryEmpty(ds.searchToolQuery)) {
                    final QueryNodeBool and = new QueryNodeBool(NodeType.AND);
                    and.addChild(ds.searchToolQuery.getTree());
                    and.addChild(not);
                    root = and;
                }
                final boolean flatten = flattenQuery(e);
                update(root, not, ds.droppedQueries, flatten);
            }
            else if (e.getActionCommand().equals(i18nName(MENU_ITEM_VERBATIM))) {
                DefaultTextComponentTransferHandler delegate = new DefaultTextComponentTransferHandler(
                                (JTextComponent) support.getComponent());
                if (delegate.canImport(support)) {
                    delegate.importData(support);
                }
            }

        }


        /**
         * Determines if the resulting query should be simplified using
         * {@link FlattenerTransformer}.
         * 
         * @param e
         *            the event to get the information from
         * @return true if flattening requested. Else false.
         */
        private boolean flattenQuery(ActionEvent e) {
            return (e.getModifiers() & ActionEvent.CTRL_MASK) != 0;
        }

    }

}
