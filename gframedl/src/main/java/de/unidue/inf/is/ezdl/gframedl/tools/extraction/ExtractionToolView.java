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

package de.unidue.inf.is.ezdl.gframedl.tools.extraction;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import de.unidue.inf.is.ezdl.dlcore.data.extractor.ExtractionResult;
import de.unidue.inf.is.ezdl.gframedl.events.ExtractionEvent;
import de.unidue.inf.is.ezdl.gframedl.tools.AbstractToolView;
import de.unidue.inf.is.ezdl.gframedl.tools.Tool;
import de.unidue.inf.is.ezdl.gframedl.tools.extraction.views.AbstractExtractionView;
import de.unidue.inf.is.ezdl.gframedl.tools.extraction.views.TableView;
import de.unidue.inf.is.ezdl.gframedl.tools.extraction.views.TagcloudView;



/**
 * The {@link ExtractionToolView} contains the views to display an
 * {@link ExtractionResult}. Those views are created here and registered in the
 * views list to be automatically refreshed when the {@link ExtractionTool}
 * receives an {@link ExtractionEvent}
 * 
 * @author tacke
 */
public class ExtractionToolView extends AbstractToolView implements ActionListener {

    private static final long serialVersionUID = 8600539361437000789L;

    private JPanel toolBar = null;
    private JComboBox chooser = null;

    private List<AbstractExtractionView> views;
    private AbstractExtractionView currentView;


    public ExtractionToolView(Tool parentTool) {
        super(parentTool);

        initialize();
    }


    private void initialize() {
        setLayout(new BorderLayout());

        views = new LinkedList<AbstractExtractionView>();
        views.add(new TagcloudView());
        views.add(new TableView());

        add(getToolBar(), BorderLayout.NORTH);
    }


    private JPanel getToolBar() {
        if (toolBar == null) {
            toolBar = new JPanel();
            toolBar.setPreferredSize(new Dimension(300, 40));
            toolBar.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 0));

            SpringLayout layout = new SpringLayout();

            toolBar.setLayout(layout);
            toolBar.add(getChooser());

            layout.putConstraint(SpringLayout.NORTH, chooser, 5, SpringLayout.NORTH, toolBar);
            layout.putConstraint(SpringLayout.WEST, chooser, 5, SpringLayout.WEST, toolBar);

            toolBar.setOpaque(false);
        }
        return toolBar;
    }


    private JComboBox getChooser() {
        if (chooser == null) {
            chooser = new JComboBox();

            for (AbstractExtractionView view : views) {
                chooser.addItem(view);
            }

            currentView = (AbstractExtractionView) chooser.getSelectedItem();
            add(currentView, BorderLayout.CENTER);

            chooser.addActionListener(this);
        }
        return chooser;
    }


    /**
     * Triggers all installed views to display the supplied
     * {@link ExtractionResult}
     * 
     * @param result
     */
    protected void updateViews(ExtractionResult result) {
        for (AbstractExtractionView ex : views) {
            ex.displayData(result);
        }
    }


    private void switchView() {
        remove(currentView);
        AbstractExtractionView newView = (AbstractExtractionView) chooser.getSelectedItem();
        currentView = newView;
        add(newView);
        revalidate();
        repaint();
    }


    protected AbstractExtractionView getCurrentView() {
        return currentView;
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        Object trigger = e.getSource();
        if (trigger.equals(chooser)) {
            switchView();
        }
    }
}
