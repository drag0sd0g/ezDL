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

package de.unidue.inf.is.ezdl.gframedl.tools.library;

import java.awt.Color;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EventObject;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.RowFilter;
import javax.swing.SortOrder;
import javax.swing.TransferHandler;
import javax.swing.TransferHandler.TransferSupport;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.apache.log4j.Logger;

import de.unidue.inf.is.ezdl.dlcore.data.dldata.DLObject;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.Document;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.TermWithWeightList;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.TextDocument;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.library.EditDocument;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.library.Group;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.library.GroupList;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.library.ReferenceSystem;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.library.ReferenceSystemList;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.library.ReferenceSystemMessage;
import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;
import de.unidue.inf.is.ezdl.dlcore.data.query.ResultDocument;
import de.unidue.inf.is.ezdl.dlcore.message.content.DocumentDetailsTell;
import de.unidue.inf.is.ezdl.dlcore.message.content.MessageContent;
import de.unidue.inf.is.ezdl.dlcore.message.content.library.AddGroupNotify;
import de.unidue.inf.is.ezdl.dlcore.message.content.library.AddToLibraryNotify;
import de.unidue.inf.is.ezdl.dlcore.message.content.library.DeleteGroupNotify;
import de.unidue.inf.is.ezdl.dlcore.message.content.library.DeleteLibraryNotify;
import de.unidue.inf.is.ezdl.dlcore.message.content.library.GroupsTell;
import de.unidue.inf.is.ezdl.dlcore.message.content.library.InitializeLibraryAsk;
import de.unidue.inf.is.ezdl.dlcore.message.content.library.LibraryTell;
import de.unidue.inf.is.ezdl.dlcore.message.content.library.ReferenceSystemMessageTell;
import de.unidue.inf.is.ezdl.dlcore.message.content.library.ReferenceSystemTell;
import de.unidue.inf.is.ezdl.dlcore.message.content.library.ReferenceSystemsAsk;
import de.unidue.inf.is.ezdl.dlcore.message.content.library.ReferenceSystemsTell;
import de.unidue.inf.is.ezdl.dlfrontend.comm.BackendEvent;
import de.unidue.inf.is.ezdl.dlfrontend.dispatcher.Dispatcher;
import de.unidue.inf.is.ezdl.dlfrontend.i18n.I18nSupport;
import de.unidue.inf.is.ezdl.gframedl.Config;
import de.unidue.inf.is.ezdl.gframedl.Icons;
import de.unidue.inf.is.ezdl.gframedl.IconsTuple;
import de.unidue.inf.is.ezdl.gframedl.ToolController;
import de.unidue.inf.is.ezdl.gframedl.events.AddToLibraryEvent;
import de.unidue.inf.is.ezdl.gframedl.events.DetailEvent;
import de.unidue.inf.is.ezdl.gframedl.events.LibraryEvent;
import de.unidue.inf.is.ezdl.gframedl.tools.AbstractTool;
import de.unidue.inf.is.ezdl.gframedl.tools.ToolView;
import de.unidue.inf.is.ezdl.gframedl.transfer.DLObjectTransferable;
import de.unidue.inf.is.ezdl.gframedl.transfer.DataFlavors;



/** Implements the GUI functionality of the library */
public final class LibraryTool extends AbstractTool {

    public static final String I18N_PREFIX = "ezdl.tools.library.";
    public static final String REFERENCESYSTEM_KEY = "referencesystem";
    public static final String WORK_OFFLINE_KEY = "referencesystem.workoffline";

    private Config conf = Config.getInstance();
    private LibraryListModel libraryModel;
    private GroupLabel selectedGroup = null;
    private LibraryRowFilter rowFilter;
    private ReferenceSystem referenceSystem;
    private Logger logger = Logger.getLogger(LibraryTool.class);
    private I18nSupport i18n = I18nSupport.getInstance();


    // event handler for buttons
    private class ButtonEventHandler extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {

            if (e.getComponent() == null) {
                return;
            }

            if (e.getComponent().equals(((LibraryToolView) getDefaultView()).getToolbarAddGroupButton())) {
                handleAddNewGroup();
            }

            if (e.getComponent().equals(((LibraryToolView) getDefaultView()).getToolbarEditGroupButton())) {
                handleEditGroup();
            }

            if (e.getComponent().equals(((LibraryToolView) getDefaultView()).getToolbarDeleteGroupButton())) {
                handleDeleteGroup(selectedGroup.getGroup());
            }

            if (e.getComponent().equals(((LibraryToolView) getDefaultView()).getToolbarDeleteButton())) {
                handleRemoveObject();
            }

            if (e.getComponent().equals(((LibraryToolView) getDefaultView()).getToolbarEditButton())) {
                handleEditObject();
            }

            if (e.getComponent().equals(((LibraryToolView) getDefaultView()).getToolbarReferenceSystemButton())) {
                handleEditReferenceSystem();
            }

            if (e.getComponent().equals(((LibraryToolView) getDefaultView()).getToolbarNewButton())) {
                handleInsertNewDocument();
            }

            if (e.getComponent().equals(((LibraryToolView) getDefaultView()).getToolbarReSynchronizeButton())) {
                handleReSychronization();
            }
        }
    }


    /** Eventhandler for groups */
    private class GroupsEventHandler extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {

            if (e.getClickCount() == 1) {
                if (e.getSource() instanceof GroupLabel) {
                    handleGroupClick((GroupLabel) e.getSource());
                }
            }
        }
    }


    /** Searcheventhandler for search in the reference list */
    private class SearchEventHandler implements KeyListener {

        @Override
        public void keyPressed(KeyEvent e) {
        }


        @Override
        public void keyReleased(KeyEvent e) {
            handleSearchEvent(((JTextField) e.getSource()).getText());
        }


        @Override
        public void keyTyped(KeyEvent e) {
        }
    }


    /** Needed for sorting the referencelist */
    private class LibraryListDataListener implements ListDataListener {

        @Override
        public void contentsChanged(ListDataEvent e) {
            ((LibraryToolView) getDefaultView()).getObjectList().setSortOrder(SortOrder.ASCENDING);
        }


        @Override
        public void intervalRemoved(ListDataEvent e) {
        }


        @Override
        public void intervalAdded(ListDataEvent e) {
        }
    }


    /** Eventhandler for LibraryList */
    private class ListEventHandler extends MouseAdapter implements KeyListener {

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_DELETE) {
                handleRemoveObject();
            }
        }


        @Override
        public void mouseClicked(MouseEvent e) {

            if (e.getClickCount() == 1) {
                DetailEvent.fireDetailEvent(this, getSelectedItem(), DetailEvent.OpenMode.DEFAULT);
            }

            if (e.getClickCount() == 2) {
                // on double click do default action
            }
        }


        @Override
        public void keyReleased(KeyEvent e) {
        }


        @Override
        public void keyTyped(KeyEvent e) {
        }

    }


    /** Transferhandler for Drag and Drop */
    private class LibraryItemTransferHandler extends TransferHandler {

        private static final long serialVersionUID = -2586729970294673875L;


        @Override
        protected Transferable createTransferable(JComponent c) {

            JList list = (JList) c;
            Object[] values = list.getSelectedValues();

            return new DLObjectTransferable(((DLObject) values[0]));
        }


        @Override
        public int getSourceActions(JComponent c) {
            return TransferHandler.COPY;
        }


        @Override
        public boolean canImport(TransferSupport support) {
            return DataFlavors.canImport(support, DataFlavors.DL_OBJECT);
        }


        @Override
        public boolean importData(TransferSupport support) {
            return importDataFromButtonDrop(support);
        }

    }


    /** Transferhandler for Drag and Drop to Groups */
    private class GroupTransferHandler extends TransferHandler {

        private static final long serialVersionUID = -7214710605576785896L;


        @Override
        public int getSourceActions(JComponent c) {
            return TransferHandler.COPY;
        }


        @Override
        public boolean canImport(TransferSupport support) {
            return DataFlavors.canImport(support, DataFlavors.DL_OBJECT);
        }


        @Override
        public boolean importData(TransferSupport support) {
            return importDataFromButtonDropGroup(support);
        }

    }


    /* Filter for the reference list */
    private abstract class LibraryRowFilter extends RowFilter<ListModel, Integer> {

        protected String searchString = "";
        protected String groupId = "";
        protected String referencesystemId = "";


        public void setSearchString(String searchString) {
            this.searchString = searchString;
        }


        public void setGroupId(String groupId) {
            this.groupId = groupId;
        }


        public void setReferenceSystemId(String referenceSystemId) {
            this.referencesystemId = referenceSystemId;
        }
    }


    // -----------------------------------------------------------------------------

    /** Constructor */
    public LibraryTool() {
        init();
    }


    private void init() {
        libraryModel = new LibraryListModel();

        // Set the rowfilter for the list
        rowFilter = new LibraryRowFilter() {

            @Override
            public boolean include(Entry<? extends ListModel, ? extends Integer> entry) {
                LibraryListModel libraryModel = (LibraryListModel) entry.getModel();
                Document document = (Document) libraryModel.getElementAt(entry.getIdentifier());

                // No search
                if (searchString.length() == 0 && groupId.length() == 0) {
                    return true;
                }

                // only tags
                if (searchString.length() != 0 && groupId.length() == 0) {
                    return handleTagSearch(document);
                }

                // only groups
                if (searchString.length() == 0 && groupId.length() != 0) {
                    return handleGroupSearch(document);
                }

                // both tags and groups
                if (searchString.length() != 0 && groupId.length() != 0) {
                    return handleGroupSearch(document) && handleTagSearch(document);
                }

                return false;
            }


            private boolean handleTagSearch(Document document) {
                TermWithWeightList tagList = (TermWithWeightList) document.getFieldValue(Field.TAGS);

                if (tagList != null) {
                    StringTokenizer st = new StringTokenizer(searchString, " ");

                    while (st.hasMoreTokens()) {
                        if (!tagList.contains(st.nextToken())) {
                            return false;
                        }
                    }
                    return true;
                }
                return false;
            }


            private boolean handleGroupSearch(Document document) {
                GroupList groupList = (GroupList) document.getFieldValue(Field.GROUPS);
                if (groupList != null) {
                    if (groupList.contains(groupId, referencesystemId)) {
                        return true;
                    }
                    else {
                        return false;
                    }
                }
                return false;
            }
        };

        // Register events
        Dispatcher.registerInterest(this, BackendEvent.class);
        Dispatcher.registerInterest(this, AddToLibraryEvent.class);
        Dispatcher.registerInterest(this, LibraryEvent.class);

        // Retrieve available online referencesystems
        sendReferenceSystemsAsk();

    }


    /** Create the view of the library */
    @Override
    public List<ToolView> createViews() {
        LibraryToolView c = new LibraryToolView(this);

        ButtonEventHandler beh = new ButtonEventHandler();
        c.getToolbarAddGroupButton().addMouseListener(beh);
        c.getToolbarEditGroupButton().addMouseListener(beh);
        c.getToolbarDeleteGroupButton().addMouseListener(beh);
        c.getToolbarDeleteButton().addMouseListener(beh);
        c.getToolbarEditButton().addMouseListener(beh);
        c.getToolbarReferenceSystemButton().addMouseListener(beh);
        c.getToolbarNewButton().addMouseListener(beh);
        c.getToolbarReSynchronizeButton().addMouseListener(beh);

        ListEventHandler leh = new ListEventHandler();
        c.getObjectList().addMouseListener(leh);
        c.getObjectList().addKeyListener(leh);

        SearchEventHandler seh = new SearchEventHandler();
        c.getSearchTextField().addKeyListener(seh);

        libraryModel.addListDataListener(new LibraryListDataListener());
        c.setListModel(libraryModel);

        c.setEnabled(true);

        c.getObjectList().setTransferHandler(new LibraryItemTransferHandler());
        c.getObjectList().setDragEnabled(true);

        c.getObjectList().setRowFilter(rowFilter);
        c.getObjectList().setSortOrder(SortOrder.ASCENDING);

        return Arrays.<ToolView> asList(c);
    }


    @Override
    public boolean handleEzEvent(EventObject ev) {
        if (ev.getSource() == this) {
            return false;
        }
        else if (ev instanceof BackendEvent) {
            handleBackendEvent((BackendEvent) ev);
        }
        else if (ev instanceof AddToLibraryEvent) {
            handleAddObject(((AddToLibraryEvent) ev).getObject());
        }
        else if (ev instanceof LibraryEvent) {
            handleLibraryEvent(((LibraryEvent) ev).getType(), ((LibraryEvent) ev).getObject());
        }

        return true;
    }


    private void handleBackendEvent(BackendEvent ev) {
        MessageContent content = ev.getContent();

        if (content instanceof DocumentDetailsTell) {
            handleObjectsDetail((DocumentDetailsTell) content);
        }
        if (content instanceof LibraryTell) {
            handleLibraryTell((LibraryTell) content);
        }
        if (content instanceof GroupsTell) {
            handleGroupsTell((GroupsTell) content);
        }
        if (content instanceof ReferenceSystemsTell) {
            handleReferenceSystemsTell((ReferenceSystemsTell) content);
        }
        if (content instanceof ReferenceSystemTell) {
            handleReferenceSystemTell((ReferenceSystemTell) content);
        }
        if (content instanceof ReferenceSystemMessageTell) {
            handleReferenceSystemMessageTell((ReferenceSystemMessageTell) content);
        }
    }


    private void handleLibraryEvent(String type, DLObject object) {
        // New Group has been added on the details window. Update Group View and
        // sned to the Backend
        if (type.equals(LibraryEvent.ADD_GROUP)) {
            if (object instanceof Group) {
                // Add to GUI
                handleAddGroup((Group) object);

                Group group = (Group) object;
                List<Document> documentList = null;

                if (!group.onlineGroup() && group.getSaveOnline()) {
                    // a local group should be saved online. documents in this
                    // group have to send
                    // to the backend to update them

                    documentList = new ArrayList<Document>();

                    // Get documents which are member in this group
                    for (Document document : libraryModel.getDocuments()) {
                        GroupList groupList = (GroupList) document.getFieldValue(Field.GROUPS);
                        if (groupList != null) {
                            if (groupList.contains(group.getId(), group.getReferenceSystemId())) {
                                documentList.add(document);
                            }
                        }
                    }
                }

                // Send to Backend
                AddGroupNotify addGroupNotify = new AddGroupNotify(ToolController.getInstance().getSessionId(), group,
                                documentList, referenceSystem);
                Dispatcher.postEvent(new BackendEvent(this, addGroupNotify));
            }
        }

        if (type.equals(LibraryEvent.DELETE_GROUP)) {
            if (object instanceof Group) {
                handleDeleteGroup((Group) object);
            }
        }

        if (type.equals(LibraryEvent.SAVE_DOCUMENT)) {
            if (object instanceof Document) {

                Document d = (Document) object;

                // Set referencesystem id of local model. maybe it was changed
                // in the meantime through an update
                // process
                Document dl = libraryModel.getDocument(d.getOid());
                if (dl != null) {
                    d.setFieldValue(Field.REFERENCESYSTEMID, dl.getFieldValue(Field.REFERENCESYSTEMID));
                }

                // Send updated document to the backend
                sendAddToLibraryNotify((Document) object);
                ((LibraryToolView) getDefaultView()).getObjectList().repaint();
                ((LibraryToolView) getDefaultView()).getObjectList().revalidate();

            }
        }

        if (type.equals(LibraryEvent.CHANGE_REFERENCESYSTEM)) {
            // Back from reference system settings
            referenceSystem.workOffline(false);
            ((LibraryToolView) getDefaultView()).getToolbarReferenceSystemButton().setIcon(Icons.MEDIA_URL.get22x22());
            ((LibraryToolView) getDefaultView()).getToolbarReferenceSystemButton().setToolTipText(
                            i18n.getLocString("ezdl.tools.library.toolbarReferenceSystemButton"));
            ((LibraryToolView) getDefaultView()).revalidate();
            ((LibraryToolView) getDefaultView()).repaint();

            // Save in user.properties
            writeReferenceSystem();
            // Retrieve Library and Groups
            handleInitializeHandler();
        }

        if (type.equals(LibraryEvent.DELETE_REFERENCESYSTEM)) {
            // Back from reference system settings
            // Save in user.properties
            deleteReferenceSystem();
        }

        if (type.equals(LibraryEvent.SEND_VERIFIER)) {
            // Back from reference system message display
            // Send verifier to backend
            handleSendVerifier((ReferenceSystemMessage) object);
        }

        if (type.equals(LibraryEvent.WORK_OFFLINE)) {
            handleWorkOffline();
        }

        if (type.equals(LibraryEvent.CHOOSE_REFERENCESYSTEM)) {
            handleEditReferenceSystem();
        }
    }


    // Document details are received from the backend
    private boolean handleObjectsDetail(DocumentDetailsTell ddt) {
        DLObject d = null;
        boolean saved = true;

        // selected document in the list
        Document selD = (Document) ((LibraryToolView) getDefaultView()).getObjectList().getSelectedValue();

        // There may be a lot of ResultDocuments for one Document.
        // collect them all (merge) and in the end the finished updated Document
        // will send to
        // the backend
        for (ResultDocument rd : ddt.getResults()) {
            if ((d != null && d.getOid().equals(rd.getOid())) || selD.getOid().equals(rd.getOid())) {
                if (d != null && !d.getOid().equals(rd.getOid())) {
                    // Another updateted document. Update changes of actually
                    // document in the Backend
                    sendAddToLibraryNotify((Document) d);
                    saved = true;
                }
                d = libraryModel.getDocument(rd.getOid());
                d.merge(rd.getDocument());
                saved = false;
            }
        }

        if (!saved) {
            sendAddToLibraryNotify((Document) d);
        }

        return true;
    }


    /** Library TELL from backend. update documents */
    @SuppressWarnings("unused")
    private boolean handleLibraryTell(LibraryTell lt) {
        List<Document> documents = lt.getDocuments();
        // libraryModel.removeAllElements();

        for (Document d : documents) {
            if (libraryModel.containsOid(d.getOid())) {
                boolean selected = false;

                // check if actual document is selected in list
                if (getSelectedItem() == libraryModel.getDocument(d.getOid())) {
                    selected = true;
                }

                // get index of document in list
                int index = libraryModel.indexOf(libraryModel.getDocument(d.getOid()));
                libraryModel.set(index, d);
            }
            else {
                libraryModel.addElement(d);
            }
        }
        return true;
    }


    /** Adds object to ListModel */
    private boolean handleAddObjects(List<DLObject> objects) {
        for (DLObject d : objects) {
            handleAddObject(d);
        }

        ((LibraryToolView) getDefaultView()).setListModel(libraryModel);
        return true;
    }


    /** Adds a single DLObject to the Library List */
    private boolean handleAddObject(DLObject d) {
        if (d instanceof Document) {
            if (!libraryModel.containsOid(d.getOid())) {
                libraryModel.addElement(d);
                sendAddToLibraryNotify((Document) d);
            }
        }
        return true;
    }


    /** Sends the document to the backend */
    private void sendAddToLibraryNotify(Document d) {
        ArrayList<Document> documents = new ArrayList<Document>();
        documents.add(d);
        AddToLibraryNotify addToLibraryNotify = new AddToLibraryNotify(ToolController.getInstance().getSessionId(),
                        documents, referenceSystem);
        Dispatcher.postEvent(new BackendEvent(this, addToLibraryNotify));
    }


    /** Sends a list of document to the backend */
    private void sendAddToLibraryNotify(List<Document> documents) {
        AddToLibraryNotify addToLibraryNotify = new AddToLibraryNotify(ToolController.getInstance().getSessionId(),
                        documents, referenceSystem);
        Dispatcher.postEvent(new BackendEvent(this, addToLibraryNotify));
    }


    /** Removes a single DLObject from the Library List */
    private boolean handleRemoveObject() {
        if (getSelectedItem() != null) {
            Document selected = (Document) getSelectedItem();
            libraryModel.removeElement(selected);
            DeleteLibraryNotify deleteLibraryNotify = new DeleteLibraryNotify(ToolController.getInstance()
                            .getSessionId(), selected, referenceSystem);
            Dispatcher.postEvent(new BackendEvent(this, deleteLibraryNotify));
        }
        return true;
    }


    /** Adds the Groups of the user */
    private boolean handleGroupsTell(GroupsTell gt) {

        for (Group g : gt.getGroups()) {
            handleAddGroup(g);
        }

        return true;
    }


    /** Handles the reference Systems tell. Save other */
    private boolean handleReferenceSystemsTell(ReferenceSystemsTell rt) {
        readReferenceSystem(rt.getReferenceSystems());
        referenceSystem.setOtherAvailableReferenceSystems(rt.getReferenceSystems());

        // no referencesystem choosen. Inform User
        if (referenceSystem.getName() == null && !referenceSystem.workOffline()) {
            // first automatically set referenceSystem offline because if user
            // ignores the message
            referenceSystem.workOffline(true);
            ((LibraryToolView) getDefaultView()).getToolbarReferenceSystemButton().setIcon(
                            Icons.MEDIA_OFFLINEURL.get22x22());
            ((LibraryToolView) getDefaultView()).getToolbarReferenceSystemButton().setToolTipText(
                            i18n.getLocString("ezdl.tools.library.toolbarReferenceSystemButton.offline"));
            ((LibraryToolView) getDefaultView()).revalidate();
            ((LibraryToolView) getDefaultView()).repaint();
            ReferenceSystemMessage rm = new ReferenceSystemMessage(ReferenceSystemMessage.NO_REFERENCE_SYSTEM_INFO,
                            null, null, null);
            DetailEvent.fireDetailEvent(this, rm, DetailEvent.OpenMode.DEFAULT);

        }
        else {
            // Now initialize all data
            handleInitializeHandler();
        }

        return true;
    }


    /** Handles the reference System tell. Update local referenceSystem object */
    private boolean handleReferenceSystemTell(ReferenceSystemTell rt) {
        referenceSystem = rt.getReferenceSystem();
        writeReferenceSystem();
        return true;
    }


    /**
     * Handles a referencesystem message tell. This are messages from the online
     * reference system show on detail view
     */
    private boolean handleReferenceSystemMessageTell(ReferenceSystemMessageTell rmt) {
        DetailEvent.fireDetailEvent(this, rmt.getReferenceSystemMessage(), DetailEvent.OpenMode.NEW_TAB);
        return true;
    }


    /** Edit a reference. Fire DetailEvent to DetailView */
    private void handleEditObject() {
        if (getSelectedItem() != null) {
            Document selected = (Document) getSelectedItem();
            DetailEvent.fireDetailEvent(this, new EditDocument(selected, ((LibraryToolView) getDefaultView())
                            .getGroupListPanel().getGroups()), DetailEvent.OpenMode.DEFAULT);
        }
    }


    /** Search Tags */
    private void handleSearchEvent(String text) {
        rowFilter.setSearchString(text);
        ((LibraryToolView) getDefaultView()).getObjectList().setRowFilter(rowFilter);
    }


    /** Edit settings button was pressed in toolbar */
    private void handleEditReferenceSystem() {
        DetailEvent.fireDetailEvent(this, referenceSystem, DetailEvent.OpenMode.DEFAULT);
    }


    // Mouseclick on a Group
    private void handleGroupClick(GroupLabel g) {
        // Highlight selected group
        g.setOpaque(true);
        g.setBackground(Color.LIGHT_GRAY);

        if (selectedGroup != null) {
            selectedGroup.setBackground(null);
        }
        if (selectedGroup != null && selectedGroup == g) {
            // Show all references again. deselect group
            rowFilter.setGroupId("");
            ((LibraryToolView) getDefaultView()).getObjectList().setRowFilter(rowFilter);
            selectedGroup = null;
        }
        else {
            selectedGroup = g;
            // Show references of this group
            rowFilter.setGroupId(g.getGroup().getId());
            rowFilter.setReferenceSystemId(g.getGroup().getReferenceSystemId());
            ((LibraryToolView) getDefaultView()).getObjectList().setRowFilter(rowFilter);
        }

        ((LibraryToolView) getDefaultView()).revalidate();
    }


    /** Adds a document to the given group */
    private void addDocumentToGroup(Document document, Group group) {
        // Save groups
        GroupList groupList = (GroupList) document.getFieldValue(Field.GROUPS);
        if (groupList == null) {
            groupList = new GroupList();
            document.setFieldValue(Field.GROUPS, groupList);
        }

        if (!groupList.contains(group.getId(), group.getReferenceSystemId())) {
            boolean add = true;
            if (group.onlineGroup()) {
                // Online group. Check if document is not member of an other
                // online group
                // it is online possible to be a member in one online group
                for (Group g : groupList) {
                    if (g.onlineGroup()) {
                        add = false;
                        break;
                    }
                }

            }

            if (add) {
                groupList.add(group);
                document.setFieldValue(Field.GROUPS, groupList);
                sendAddToLibraryNotify(document);
            }
        }

    }


    /**
     * Sends a initialize command to the backend. All needed information will be
     * send from the Backend (Library, Groups...)
     */
    private void handleInitializeHandler() {
        InitializeLibraryAsk initializeLibraryAsk = new InitializeLibraryAsk(
                        (ToolController.getInstance().getSessionId()), referenceSystem);
        Dispatcher.postEvent(new BackendEvent(this, initializeLibraryAsk));
    }


    /** Request all available OnlineReferenceSystems */
    private void sendReferenceSystemsAsk() {
        Dispatcher.postEvent(new BackendEvent(this,
                        new ReferenceSystemsAsk(ToolController.getInstance().getSessionId())));
    }


    @Override
    protected String getI18nPrefix() {
        return I18N_PREFIX;
    }


    @Override
    protected IconsTuple getIcon() {
        return Icons.LIBRARY_TOOL.toIconsTuple();
    }


    @SuppressWarnings("unchecked")
    @Override
    public boolean importDataFromButtonDrop(TransferSupport support) {
        try {
            if (support.isDataFlavorSupported(DataFlavors.DL_OBJECT)) {
                List<DLObject> object = (List<DLObject>) support.getTransferable().getTransferData(
                                DataFlavors.DL_OBJECT);
                handleAddObjects(object);
                logger.info(object);
            }
            return true;
        }
        catch (UnsupportedFlavorException e) {
            logger.error(e.getMessage(), e);
        }
        catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return false;
    }


    @SuppressWarnings("unchecked")
    public boolean importDataFromButtonDropGroup(TransferSupport support) {
        try {

            if (support.isDataFlavorSupported(DataFlavors.DL_OBJECT)) {
                List<DLObject> object = (List<DLObject>) support.getTransferable().getTransferData(
                                DataFlavors.DL_OBJECT);

                for (DLObject dl : object) {
                    if (dl instanceof Document) {
                        addDocumentToGroup(libraryModel.getDocument(((Document) dl).getOid()),
                                        ((GroupLabel) support.getComponent()).getGroup());
                    }
                }

            }
            return true;
        }
        catch (UnsupportedFlavorException e) {
            logger.error(e.getMessage(), e);
        }
        catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return false;
    }


    /** Add a group */
    private boolean handleAddGroup(Group g) {

        GroupLabel groupLabel = ((LibraryToolView) getDefaultView()).getGroupListPanel().addGroup(g);

        // Add Mouseeventhandler
        if (groupLabel != null) {
            groupLabel.addMouseListener(new GroupsEventHandler());
            // dragndrop
            groupLabel.setTransferHandler(new GroupTransferHandler());
        }
        ((LibraryToolView) getDefaultView()).updateSplitPaneDivider();
        ((LibraryToolView) getDefaultView()).revalidate();

        // Group could also be updated. Look if documents are member in this
        // groups
        // Maybe the name of the group was changed.
        // Delete group membership from every document
        ArrayList<Document> documents = new ArrayList<Document>();
        for (Document document : libraryModel.getDocuments()) {
            GroupList groupList = (GroupList) document.getFieldValue(Field.GROUPS);
            if (groupList != null) {
                if (groupList.contains(g.getId(), g.getReferenceSystemId())) {
                    // Update name of group
                    if (!groupList.getGroup(g.getId()).getName().equals(g.getName())) {
                        groupList.getGroup(g.getId()).setName(g.getName());

                        documents.add(document);

                    }
                }
            }
        }

        // Update document in the backen
        if (documents.size() > 0) {
            sendAddToLibraryNotify(documents);
        }

        return true;
    }


    /** Add a new Group to the Library */
    private void handleAddNewGroup() {
        DetailEvent.fireDetailEvent(this, new Group(), DetailEvent.OpenMode.DEFAULT);
    }


    /** Edit the selected Group */
    private void handleEditGroup() {
        if (selectedGroup != null) {
            DetailEvent.fireDetailEvent(this, selectedGroup.getGroup(), DetailEvent.OpenMode.DEFAULT);
        }
        else {

            ((LibraryToolView) getDefaultView()).showMessageBox(
                            i18n.getLocString("ezdl.tools.library.group.selectwarning"), i18n.getLocString("error"));
        }
    }


    /** Deletes a group */
    private boolean handleDeleteGroup(Group group) {
        if (group != null) {
            if (selectedGroup.getGroup() == group) {

                selectedGroup = null;
                rowFilter.setGroupId("");
                rowFilter.setReferenceSystemId("");
                ((LibraryToolView) getDefaultView()).getObjectList().setRowFilter(rowFilter);
            }

            // Delete group membership from every document
            ArrayList<Document> documents = new ArrayList<Document>();
            for (Document document : libraryModel.getDocuments()) {
                GroupList groupList = (GroupList) document.getFieldValue(Field.GROUPS);
                if (groupList != null) {
                    if (groupList.contains(group.getId(), group.getReferenceSystemId())) {
                        groupList.remove(group);

                        if (group.getType() != null && !group.getType().equals(Group.TYPE_PRIVATE)) {
                            // Group was an online group.
                            // Documents of this groups are not saved in local
                            // store.
                            // Remove them from the view. No need to send to
                            // backend
                            libraryModel.removeElement(document);

                        }
                        else {
                            // Send updated document to backend
                            documents.add(document);
                        }

                    }
                }
            }
            // Send updated documents to backend
            if (documents.size() > 0) {
                sendAddToLibraryNotify(documents);
            }

            // Delete group from groupList
            ((LibraryToolView) getDefaultView()).getGroupListPanel().deleteGroup(group);
            ((LibraryToolView) getDefaultView()).updateSplitPaneDivider();

            // Send delete group to Backend
            DeleteGroupNotify deleteGroupNotify = new DeleteGroupNotify(ToolController.getInstance().getSessionId(),
                            group, referenceSystem);
            Dispatcher.postEvent(new BackendEvent(this, deleteGroupNotify));

            ((LibraryToolView) getDefaultView()).revalidate();
        }
        else {
            i18n.getLocString("ezdl.tools.library.group.selectwarning");
            ((LibraryToolView) getDefaultView()).showMessageBox(
                            i18n.getLocString("ezdl.tools.library.group.selectwarning"), i18n.getLocString("error"));
        }
        ((LibraryToolView) getDefaultView()).revalidate();
        ((LibraryToolView) getDefaultView()).repaint();
        return true;
    }


    /** Read the used reference system from the user properties file */
    private void readReferenceSystem(ReferenceSystemList rl) {
        referenceSystem = new ReferenceSystem();
        for (ReferenceSystem rs : rl) {
            // User have already choosen this reference system
            // read settings
            if (conf.getUserProperty(REFERENCESYSTEM_KEY) != null
                            && conf.getUserProperty(REFERENCESYSTEM_KEY).equals(rs.getName())) {
                referenceSystem.setName(conf.getUserProperty(REFERENCESYSTEM_KEY));
                referenceSystem.setRequiredParameters(rs.getRequiredParameters());
                referenceSystem.setOtherParameters(rs.getOtherParameters());

                // Fill required Parameters from the property file
                for (java.util.Map.Entry<String, String> e : referenceSystem.getRequiredParameters().entrySet()) {
                    e.setValue(conf.getUserProperty(REFERENCESYSTEM_KEY + "." + e.getKey()));
                }

                // Fill other Parameters from the property file
                for (java.util.Map.Entry<String, String> e : referenceSystem.getOtherParameters().entrySet()) {
                    e.setValue(conf.getUserProperty(REFERENCESYSTEM_KEY + "." + e.getKey()));
                }
            }

            if (conf.getUserProperty(WORK_OFFLINE_KEY, "").length() > 0) {
                referenceSystem.workOffline(conf.getUserPropertyAsBoolean(WORK_OFFLINE_KEY));
            }

            if (referenceSystem.workOffline()) {
                ((LibraryToolView) getDefaultView()).getToolbarReferenceSystemButton().setIcon(
                                Icons.MEDIA_OFFLINEURL.get22x22());
                ((LibraryToolView) getDefaultView()).getToolbarReferenceSystemButton().setToolTipText(
                                i18n.getLocString("ezdl.tools.library.toolbarReferenceSystemButton.offline"));
                ((LibraryToolView) getDefaultView()).revalidate();
                ((LibraryToolView) getDefaultView()).repaint();
            }
        }

    }


    /** Save settings into user properties file */
    private void writeReferenceSystem() {
        if (referenceSystem.getName() != null) {
            conf.setUserProperty(REFERENCESYSTEM_KEY, referenceSystem.getName());
        }

        conf.setUserProperty(WORK_OFFLINE_KEY, referenceSystem.workOffline());

        if (referenceSystem.getRequiredParameters() != null) {
            for (Map.Entry<String, String> e : referenceSystem.getRequiredParameters().entrySet()) {
                conf.setUserProperty(REFERENCESYSTEM_KEY + "." + e.getKey(), e.getValue());
            }
        }

        if (referenceSystem.getOtherParameters() != null) {
            for (Map.Entry<String, String> e : referenceSystem.getOtherParameters().entrySet()) {
                // Parameters start with $ are not saved in user property file.
                // temp
                // parameters
                if (!e.getKey().startsWith("$")) {
                    conf.setUserProperty(REFERENCESYSTEM_KEY + "." + e.getKey(), e.getValue());
                }
            }
        }
    }


    /** Send verifier to backend */
    private void handleSendVerifier(ReferenceSystemMessage rsm) {
        referenceSystem.getOtherParameters().put("$verifier", rsm.getParameters().get("verifier"));

        // Retrieve Library and Groups
        handleInitializeHandler();
    }


    /** work offline without an online reference system */
    private void handleWorkOffline() {
        referenceSystem.workOffline(true);
        ((LibraryToolView) getDefaultView()).getToolbarReferenceSystemButton().setIcon(
                        Icons.MEDIA_OFFLINEURL.get22x22());
        ((LibraryToolView) getDefaultView()).getToolbarReferenceSystemButton().setToolTipText(
                        i18n.getLocString("ezdl.tools.library.toolbarReferenceSystemButton.offline"));
        ((LibraryToolView) getDefaultView()).revalidate();
        ((LibraryToolView) getDefaultView()).repaint();
        conf.setUserProperty(WORK_OFFLINE_KEY, true);
        // Retrieve Library and Groups
        handleInitializeHandler();
    }


    /** Delete setting from properties file */
    private void deleteReferenceSystem() {
        conf.removeUserProperty(REFERENCESYSTEM_KEY);
        for (java.util.Map.Entry<String, String> e : referenceSystem.getRequiredParameters().entrySet()) {
            conf.removeUserProperty(REFERENCESYSTEM_KEY + "." + e.getKey());
        }
        for (java.util.Map.Entry<String, String> e : referenceSystem.getOtherParameters().entrySet()) {
            conf.removeUserProperty(REFERENCESYSTEM_KEY + "." + e.getKey());
        }

        referenceSystem.setName(null);
        referenceSystem.workOffline(true);

        // Delete all online reference settings from each document and group
        for (Document document : libraryModel.getDocuments()) {
            document.setFieldValue(Field.REFERENCESYSTEM, null);
            document.setFieldValue(Field.REFERENCESYSTEMID, null);
        }

        // Groups
        for (Group g : ((LibraryToolView) getDefaultView()).getGroupListPanel().getGroups()) {
            // group is saved in online referencesystem and type is private
            if (g.onlineGroup() && g.getType().equals(Group.TYPE_PRIVATE)) {
                List<Document> documentList = new ArrayList<Document>();

                g.setReferenceSystem(null);
                g.setReferenceSystemId(null);
                AddGroupNotify addGroupNotify = new AddGroupNotify(ToolController.getInstance().getSessionId(), g,
                                documentList, referenceSystem);
                Dispatcher.postEvent(new BackendEvent(this, addGroupNotify));

                // Update view
                ((LibraryToolView) getDefaultView()).getGroupListPanel().addGroup(g);

            }
            else if (g.onlineGroup()) {
                // Group is online and public group, remove from Panel and
                // remove documents which are member in this group

                for (Document document : libraryModel.getDocuments()) {
                    GroupList groupList = (GroupList) document.getFieldValue(Field.GROUPS);
                    if (groupList != null) {
                        if (groupList.contains(g.getId(), g.getReferenceSystemId())) {
                            // documents which are member in an online Group are
                            // not stored in local store
                            libraryModel.removeElement(document);
                        }
                    }
                }

                ((LibraryToolView) getDefaultView()).getGroupListPanel().deleteGroup(g);
            }
        }

        ((LibraryToolView) getDefaultView()).getToolbarReferenceSystemButton().setIcon(
                        Icons.MEDIA_OFFLINEURL.get22x22());
        ((LibraryToolView) getDefaultView()).getToolbarReferenceSystemButton().setToolTipText(
                        i18n.getLocString("ezdl.tools.library.toolbarReferenceSystemButton.offline"));
        ((LibraryToolView) getDefaultView()).getGroupListPanel().revalidate();
        ((LibraryToolView) getDefaultView()).getGroupListPanel().repaint();

        // Send updated documents to Backend
        sendAddToLibraryNotify(libraryModel.getDocuments());
    }


    // Insert a new Document
    private void handleInsertNewDocument() {
        DetailEvent.fireDetailEvent(this, new EditDocument(new TextDocument(), ((LibraryToolView) getDefaultView())
                        .getGroupListPanel().getGroups()), DetailEvent.OpenMode.DEFAULT);
    }


    // Resychronize library with online Library Store
    private void handleReSychronization() {
        if (!referenceSystem.workOffline()) {
            handleInitializeHandler();
        }
    }


    protected DLObject getSelectedItem() {
        LibraryToolView view = (LibraryToolView) getDefaultView();
        JList olist = view.getObjectList();
        if (olist.getModel() != null) {
            if (olist.getModel().getSize() > 0) {
                Object selected = olist.getSelectedValue();
                if (selected instanceof DLObject) {
                    return (DLObject) selected;
                }
            }
        }
        return null;
    }


    @Override
    public boolean canImportButtonDrop(TransferSupport support) {
        return true;
    }

}
