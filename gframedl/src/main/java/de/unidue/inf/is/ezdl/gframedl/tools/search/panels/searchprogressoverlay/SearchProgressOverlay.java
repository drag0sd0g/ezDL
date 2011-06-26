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

package de.unidue.inf.is.ezdl.gframedl.tools.search.panels.searchprogressoverlay;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EventObject;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.Timer;
import javax.swing.border.Border;

import org.jdesktop.jxlayer.JXLayer;
import org.jdesktop.jxlayer.plaf.ext.LockableUI;

import de.unidue.inf.is.ezdl.dlcore.data.wrappers.FrontendWrapperInfo;
import de.unidue.inf.is.ezdl.dlcore.message.content.DocumentQueryInfoNotify;
import de.unidue.inf.is.ezdl.dlcore.message.content.MessageContent;
import de.unidue.inf.is.ezdl.dlfrontend.comm.BackendEvent;
import de.unidue.inf.is.ezdl.dlfrontend.dispatcher.Dispatcher;
import de.unidue.inf.is.ezdl.dlfrontend.dispatcher.EventReceiver;
import de.unidue.inf.is.ezdl.dlfrontend.helper.FieldRegistry;
import de.unidue.inf.is.ezdl.dlfrontend.helper.FrontendWrapperInfoComparator;
import de.unidue.inf.is.ezdl.dlfrontend.i18n.I18nSupport;
import de.unidue.inf.is.ezdl.dlfrontend.query.QueryFactory;
import de.unidue.inf.is.ezdl.dlfrontend.query.parsers.simple.WebLikeFactory;
import de.unidue.inf.is.ezdl.gframedl.ToolController;
import de.unidue.inf.is.ezdl.gframedl.components.SpinnerPainter;
import de.unidue.inf.is.ezdl.gframedl.tools.search.SearchTool;
import de.unidue.inf.is.ezdl.gframedl.tools.search.SearchTool.SearchContext;
import de.unidue.inf.is.ezdl.gframedl.tools.search.actions.Actions;



/**
 * Subclass of the {@link LockableUI} which uses the
 * {@link SearchProgressOverlay} from the SwingX project to implement the
 * "busy effect" when {@link JXLayer} is locked.
 */
public class SearchProgressOverlay extends LockableUI implements ActionListener, EventReceiver {

    private static final long serialVersionUID = 4002461573790477851L;

    public static final String I18N_PREFIX = "ezdl.tools.search.busyOverlay.";

    private static final Border OUTER_BORDER = BorderFactory.createEmptyBorder(5, 15, 5, 15);

    private SpinnerPainter busyPainter;
    private Timer timer;
    private int frameNumber;

    private JPanel mainPanel;
    private JPanel queryPanel;
    private JPanel wrapperPanel;
    private JPanel statePanel;
    private JPanel buttonPanel;
    private JPanel wrapperStatePanel;

    private JTextArea queryLabel;
    private SearchProgressPanelCreator panelCreator;
    private JScrollPane scrollPane;

    private SearchProgressOverlayModel model;
    private SearchProgressSpinnerItem allState;
    private boolean printLock;

    private QueryFactory queryFactory = new WebLikeFactory(FieldRegistry.getInstance());


    public SearchProgressOverlay() {
        init();
        printLock = true;
        busyPainter = new SpinnerPainter();
        busyPainter.setPointShape(new Ellipse2D.Double(0, 0, 5, 5));
        busyPainter.setTrajectory(new Ellipse2D.Double(0, 0, 25, 25));
        timer = new Timer(100, this);

        Dispatcher.registerInterest(this, BackendEvent.class);
    }


    /**
     * Initialize the overlay components.
     */
    private void init() {

        JPanel spacePanel = new JPanel();

        mainPanel = new JPanel();
        queryPanel = new JPanel();
        wrapperPanel = new JPanel();
        statePanel = new JPanel();
        buttonPanel = new JPanel();
        wrapperStatePanel = new JPanel();

        scrollPane = new JScrollPane();
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        scrollPane.getVerticalScrollBar().setBlockIncrement(50);

        queryLabel = new JTextArea();
        queryLabel.setOpaque(false);
        queryLabel.setEditable(false);
        queryLabel.setLineWrap(true);
        queryLabel.setWrapStyleWord(true);
        queryLabel.setBorder(BorderFactory.createEmptyBorder());

        wrapperStatePanel.setBackground(Color.WHITE);
        mainPanel.setBackground(Color.WHITE);
        queryPanel.setBackground(Color.WHITE);
        wrapperPanel.setBackground(Color.WHITE);
        statePanel.setBackground(Color.WHITE);
        buttonPanel.setBackground(Color.WHITE);
        spacePanel.setBackground(Color.WHITE);

        wrapperPanel.setBorder(BorderFactory.createTitledBorder(I18nSupport.getInstance().getLocString(
                        I18N_PREFIX + "libraries")));
        statePanel.setBorder(BorderFactory.createTitledBorder(I18nSupport.getInstance().getLocString(
                        I18N_PREFIX + "totalState")));
        queryPanel.setBorder(BorderFactory.createTitledBorder(I18nSupport.getInstance().getLocString(
                        I18N_PREFIX + "query")));
        spacePanel.setBorder(OUTER_BORDER);

        wrapperStatePanel.setLayout(new BorderLayout());
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        wrapperPanel.setLayout(new BorderLayout());
        mainPanel.setLayout(new BorderLayout(10, 10));
        queryPanel.setLayout(new BorderLayout());
        statePanel.setLayout(new BorderLayout());

        spacePanel.setLayout(new BorderLayout());

        Actions.SHOW_NOW_ACTION.setEnabled(true);
        JButton showButton = new JButton(Actions.SHOW_NOW_ACTION);
        JButton cancelButton = new JButton(Actions.SEARCH_ACTION);

        buttonPanel.add(cancelButton);
        buttonPanel.add(showButton);

        spacePanel.add(buttonPanel, BorderLayout.SOUTH);
        spacePanel.add(queryPanel, BorderLayout.NORTH);
        spacePanel.add(wrapperStatePanel, BorderLayout.CENTER);

        wrapperStatePanel.add(wrapperPanel, BorderLayout.CENTER);
        wrapperStatePanel.add(statePanel, BorderLayout.SOUTH);
        queryPanel.add(queryLabel, BorderLayout.CENTER);

        mainPanel.add(spacePanel, BorderLayout.CENTER);

    }


    @Override
    protected void paintLayer(Graphics2D g2, JXLayer<? extends JComponent> l) {
        super.paintLayer(g2, l);
        JPanel glassPane = l.getGlassPane();
        if (isLocked()) {
            SearchTool searchTool = ToolController.getInstance().getTool(SearchTool.class);
            SearchContext currentSearch = searchTool.getCurrentSearch();

            if (printLock) {
                init();
                glassPane.setLayout(new BorderLayout());
                queryLabel.setText(queryFactory.getTextForQueryNode(currentSearch.query.getTree()));

                List<SearchProgressSpinnerItem> wrapperSpinnerList = new ArrayList<SearchProgressSpinnerItem>();
                allState = new SearchProgressSpinnerItem(I18nSupport.getInstance().getLocString(
                                I18N_PREFIX + "totalDocuments"), busyPainter);
                wrapperSpinnerList.add(allState);

                Collections.sort(currentSearch.usedWrappers, new FrontendWrapperInfoComparator());
                for (FrontendWrapperInfo info : currentSearch.usedWrappers) {
                    wrapperSpinnerList.add(new SearchProgressSpinnerItem(info.getId(), busyPainter, info
                                    .getRemoteName(), info.getLargeIcon()));
                }

                JPanel temp = new JPanel();
                temp.setLayout(new GridLayout(0, 1));

                model = new SearchProgressOverlayModel(wrapperSpinnerList);
                panelCreator = new SearchProgressPanelCreator(model);

                List<JPanel> tempPanelList = panelCreator.constuctBusyLabelList();

                for (JPanel p : tempPanelList) {
                    temp.add(p);
                }
                scrollPane.getViewport().add(temp);
                wrapperPanel.add(scrollPane, BorderLayout.CENTER);
                statePanel.add(panelCreator.constructBusyStatePanel(), BorderLayout.CENTER);
                printLock = false;
            }
            glassPane.setBorder(BorderFactory.createEmptyBorder(35, 100, 10, 100));
            glassPane.add(mainPanel, BorderLayout.CENTER);
        }
        else {
            glassPane.removeAll();
        }
    }


    @Override
    public void setLocked(boolean isLocked) {
        super.setLocked(isLocked);
        if (isLocked) {
            timer.start();
            printLock = true;
        }
        else {
            timer.stop();
        }
    }


    // Change the frame for the busyPainter
    // and mark BusyPainterUI as dirty
    @Override
    public void actionPerformed(ActionEvent e) {
        frameNumber = (frameNumber + 1) % 8;
        busyPainter.setFrame(frameNumber);
        // this will repaint the layer
        setDirty(true);
    }


    private void handleIncomingBackendEvent(MessageContent messageContent) {
        if (messageContent instanceof DocumentQueryInfoNotify) {
            handleDocumentQueryInfoMessage((DocumentQueryInfoNotify) messageContent);
        }
    }


    private void handleDocumentQueryInfoMessage(DocumentQueryInfoNotify messageContent) {
        String name = messageContent.getWrapperName();
        final int count = messageContent.getWrapperResultCount();
        if (name != null) {
            name = name.toLowerCase();
            for (SearchProgressSpinnerItem item : model.getItems()) {
                if (name.equals(item.getWrapperID())) {
                    item.setResultCount(count);
                    item.getSpinner().setVisible(false);
                    allState.setResultCount(messageContent.getCount());
                }
            }
            wrapperStatePanel.revalidate();
            wrapperStatePanel.repaint();
        }

    }


    @Override
    public boolean handleEzEvent(EventObject ev) {
        if (ev.getSource() == this) {
            return false;
        }
        else if (ev instanceof BackendEvent) {
            MessageContent messageContent = ((BackendEvent) ev).getContent();
            handleIncomingBackendEvent(messageContent);
        }
        return true;
    }

}