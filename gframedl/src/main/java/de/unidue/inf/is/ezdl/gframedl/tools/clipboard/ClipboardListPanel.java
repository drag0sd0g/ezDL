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

package de.unidue.inf.is.ezdl.gframedl.tools.clipboard;

import java.awt.datatransfer.Transferable;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.TransferHandler;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionListener;

import de.unidue.inf.is.ezdl.dlcore.data.dldata.DLObject;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.Term;
import de.unidue.inf.is.ezdl.dlcore.message.content.UserLogNotify;
import de.unidue.inf.is.ezdl.dlfrontend.comm.BackendEvent;
import de.unidue.inf.is.ezdl.dlfrontend.dispatcher.Dispatcher;
import de.unidue.inf.is.ezdl.gframedl.ToolController;
import de.unidue.inf.is.ezdl.gframedl.components.actions.ContentGetter;
import de.unidue.inf.is.ezdl.gframedl.components.actions.SelectionGetter;
import de.unidue.inf.is.ezdl.gframedl.events.DetailEvent;
import de.unidue.inf.is.ezdl.gframedl.transfer.DLObjectTransferable;
import de.unidue.inf.is.ezdl.gframedl.transfer.DataFlavors;



public class ClipboardListPanel extends JPanel implements SelectionGetter, ContentGetter {

    private static final long serialVersionUID = -6085856911827767714L;

    private JList attributeList;
    private JLabel attributeListInfo = null;
    private JScrollPane attributeListScrollPane;
    private ClipboardToolView toolView;
    private DefaultListModel clipboardModel;


    private class ClipboardItemTransferHandler extends TransferHandler {

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
            return toolView.getParentTool().importDataFromButtonDrop(support);
        }

    }


    private class ListEventHandler extends MouseAdapter implements KeyListener {

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_DELETE) {

                if (getSelectedItem() == null) {
                    return;
                }

                JList olist = getList();
                if (olist.getModel() != null && olist.getModel().getSize() > 0) {
                    DLObject selected = (DLObject) olist.getSelectedValue();
                    handleRemoveObject(selected);
                }
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


    public ClipboardListPanel(ClipboardToolView toolView) {
        this.toolView = toolView;
        initialize();
    }


    public JLabel getAttributeListInfo() {
        if (attributeListInfo == null) {
            attributeListInfo = new JLabel();
            attributeListInfo.setLabelFor(attributeList);
            // attributeListInfo.setText("");
        }
        return attributeListInfo;
    }


    public JList getList() {
        if (attributeList == null) {
            attributeList = new JList();
            attributeList.setCellRenderer(new ClipboardListRenderer());
        }
        return attributeList;
    }


    private void initialize() {
        clipboardModel = new DefaultListModel();

        setLayout(new java.awt.BorderLayout());
        attributeListScrollPane = new JScrollPane();
        attributeListScrollPane.setViewportView(getList());
        add(attributeListScrollPane, "Center");
        add(getAttributeListInfo(), "North");

        ListEventHandler leh = new ListEventHandler();
        getList().addMouseListener(leh);
        getList().addKeyListener(leh);

        getList().setTransferHandler(new ClipboardItemTransferHandler());
        getList().setDragEnabled(true);
        getList().setModel(clipboardModel);
    }


    public void setListInfo(String info) {
        attributeListInfo.setText(info);
    }


    @Override
    public List<Object> getSelectedObjects() {
        LinkedList<Object> out = new LinkedList<Object>();
        Object[] sel = attributeList.getSelectedValues();
        for (Object o : sel) {
            out.add(o);
        }
        return out;
    }


    @Override
    public void addListSelectionListener(ListSelectionListener listener) {
        attributeList.addListSelectionListener(listener);
    }


    /**
     * retrieve the currently selected item from clipboard
     * 
     * @return
     */
    protected DLObject getSelectedItem() {
        JList olist = getList();
        if (olist.getModel() != null && olist.getModel().getSize() > 0) {
            Object selected = olist.getSelectedValue();
            if (selected instanceof DLObject) {
                return (DLObject) selected;
            }
        }
        return null;
    }


    /**
     * remove single item from listmodel
     * 
     * @param d
     * @return
     */
    private boolean handleRemoveObject(DLObject d) {
        if (clipboardModel.contains(d)) {
            clipboardModel.removeElement(d);
            getList().setModel(clipboardModel);

            UserLogNotify userLogNotify = new UserLogNotify(ToolController.getInstance().getSessionId(),
                            "removedfromclipboard");
            userLogNotify.addParameter("oid", d.getOid());
            Dispatcher.postEvent(new BackendEvent(this, userLogNotify));

            return true;
        }
        return false;
    }


    /**
     * check if d is contained in this tool's listModel
     * 
     * @param d
     * @return
     */
    private boolean containsObject(DLObject d) {
        final DefaultListModel model = (DefaultListModel) getList().getModel();
        if (d instanceof Term) {
            for (int i = 0; i < model.getSize(); i++) {
                DLObject litem = (DLObject) model.get(i);
                if (litem instanceof Term && ((Term) litem).getTerm().equals(((Term) d).getTerm())) {
                    return true;
                }
            }
        }
        if (model.contains(d)) {
            return true;
        }
        return false;
    }


    /**
     * remove all items from listmodel
     */
    public void handleClearClipboard() {
        if (clipboardModel.getSize() > 0) {
            clipboardModel.removeAllElements();
            getList().setModel(clipboardModel);
        }
        UserLogNotify userLogNotify = new UserLogNotify(ToolController.getInstance().getSessionId(), "clearedclipboard");
        Dispatcher.postEvent(new BackendEvent(this, userLogNotify));
    }


    /**
     * add new object(s) to tool's listmodel if not already in there
     * 
     * @param objects
     * @return
     */
    boolean handleAddObjects(List<DLObject> objects) {
        UserLogNotify userLogNotify = new UserLogNotify(ToolController.getInstance().getSessionId(), "addedtoclipboard");
        for (DLObject d : objects) {
            if (!containsObject(d)) {
                clipboardModel.addElement(d);
            }
            userLogNotify.addParameter("oid", d.getOid());
        }
        Dispatcher.postEvent(new BackendEvent(this, userLogNotify));
        getList().setModel(clipboardModel);
        return true;
    }


    @Override
    public void addListDataListener(ListDataListener listener) {
        clipboardModel.addListDataListener(listener);
    }


    @Override
    public List<Object> getContentObjects() {
        LinkedList<Object> out = new LinkedList<Object>();
        final int size = clipboardModel.getSize();
        for (int i = 0; (i < size); i++) {
            Object o = clipboardModel.getElementAt(i);
            out.add(o);
        }
        return out;
    }

}
