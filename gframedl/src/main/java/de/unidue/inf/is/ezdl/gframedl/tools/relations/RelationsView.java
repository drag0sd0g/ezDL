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

package de.unidue.inf.is.ezdl.gframedl.tools.relations;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;

import org.apache.log4j.Logger;

import de.unidue.inf.is.ezdl.dlcore.data.dldata.DLObject;
import de.unidue.inf.is.ezdl.dlfrontend.i18n.I18nSupport;
import de.unidue.inf.is.ezdl.gframedl.Icons;
import de.unidue.inf.is.ezdl.gframedl.components.grouping.components.GroupSeparatorBorder;
import de.unidue.inf.is.ezdl.gframedl.components.grouping.components.TitleBtn;
import de.unidue.inf.is.ezdl.gframedl.tools.AbstractToolView;
import de.unidue.inf.is.ezdl.gframedl.tools.Tool;
import de.unidue.inf.is.ezdl.gframedl.tools.relations.types.AuthorDocumentGraph;
import de.unidue.inf.is.ezdl.gframedl.tools.relations.types.CoAuthorsGraph;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.layout.LayoutTransition;
import edu.uci.ics.jung.visualization.util.Animator;



/**
 * The view of the RelationsTool.
 * 
 * @author RT
 */
public final class RelationsView extends AbstractToolView {

    private static final long serialVersionUID = 7044568164115108366L;

    private static Logger logger = Logger.getLogger(RelationsView.class);

    private DLObjectGraphView graphView;
    private List<DLObject> objects;
    private JComboBox graphKindCombo;
    private JComboBox graphLayoutCombo;


    public RelationsView(Tool parentTool) {
        super(parentTool);
        createContent();
        initGraphTypes();
    }


    private void initGraphTypes() {
        graphKindCombo.addItem(new CoAuthorsGraph());
        graphKindCombo.addItem(new AuthorDocumentGraph());
    }


    private void setGraphType(RelationGraphType rgt) {
        if ((objects != null)) {
            if (graphView != null) {
                remove(graphView);
            }
            GridBagConstraints c = new GridBagConstraints();
            c = new GridBagConstraints();
            c.gridy = 1;
            c.weightx = 1.0f;
            c.weighty = 1.0f;
            c.fill = GridBagConstraints.BOTH;

            graphView = new DLObjectGraphView(objects, rgt);
            add(graphView, c);
            setLayoutComboValue(graphView.getLayoutClass());

            toFront();

            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    graphView.zoomBestFit();
                }
            });
        }
    }


    /**
     * Show the relations of the objects.
     * 
     * @param objects
     */
    public void showRelations(List<DLObject> objects) {
        this.objects = objects;
        RelationGraphType rgt = (RelationGraphType) graphKindCombo.getItemAt(0);
        setGraphType(rgt);
    }


    private void createContent() {
        setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 1.0f;
        c.fill = GridBagConstraints.HORIZONTAL;
        add(getTitlePanel(), c);
        setBorder(new LineBorder(SystemColor.controlShadow, 1));
    }


    private List<Action> getActions() {
        List<Action> actions = new ArrayList<Action>();

        Action zoomIn = new AbstractAction() {

            private static final long serialVersionUID = 1L;


            @Override
            public void actionPerformed(ActionEvent e) {
                graphView.zoomIn();
            }
        };
        zoomIn.putValue(Action.SMALL_ICON, Icons.ZOOM_IN.get16x16());
        zoomIn.putValue(Action.NAME, I18nSupport.getInstance().getLocString("ezdl.relations.zoom.in"));

        Action zoomOut = new AbstractAction() {

            private static final long serialVersionUID = 1L;


            @Override
            public void actionPerformed(ActionEvent e) {
                graphView.zoomOut();
            }
        };
        zoomOut.putValue(Action.SMALL_ICON, Icons.ZOOM_OUT.get16x16());
        zoomOut.putValue(Action.NAME, I18nSupport.getInstance().getLocString("ezdl.relations.zoom.out"));

        Action zoomBestFit = new AbstractAction() {

            private static final long serialVersionUID = 1L;


            @Override
            public void actionPerformed(ActionEvent e) {
                graphView.zoomBestFit();
            }
        };
        zoomBestFit.putValue(Action.SMALL_ICON, Icons.ZOOM_BEST_FIT.get16x16());
        zoomBestFit.putValue(Action.NAME, I18nSupport.getInstance().getLocString("ezdl.relations.zoom.bestFit"));

        Action transform = new AbstractAction() {

            private static final long serialVersionUID = 1L;


            @Override
            public void actionPerformed(ActionEvent e) {
                graphView.setMouseModeToTransforming();
            }
        };
        transform.putValue(Action.SMALL_ICON, Icons.DRAG.get16x16());
        transform.putValue(Action.NAME, I18nSupport.getInstance().getLocString("ezdl.relations.mouse.transforming"));

        Action pick = new AbstractAction() {

            private static final long serialVersionUID = 1L;


            @Override
            public void actionPerformed(ActionEvent e) {
                graphView.setMouseModeToPicking();
            }
        };
        pick.putValue(Action.SMALL_ICON, Icons.CURSOR.get16x16());
        pick.putValue(Action.NAME, I18nSupport.getInstance().getLocString("ezdl.relations.mouse.picking"));

        actions.add(transform);
        actions.add(pick);
        actions.add(zoomIn);
        actions.add(zoomOut);
        actions.add(zoomBestFit);
        return actions;
    }


    @SuppressWarnings("unchecked")
    private void setLayout(Class<? extends Layout<DLObject, Number>> layoutC, VisualizationViewer<DLObject, Number> vv) {
        Object[] constructorArgs = {
            graphView.getGraph()
        };

        try {
            Constructor<? extends Layout<DLObject, Number>> constructor = layoutC.getConstructor(new Class[] {
                Graph.class
            });
            Object o = constructor.newInstance(constructorArgs);
            Layout<DLObject, Number> l = (Layout<DLObject, Number>) o;
            l.setInitializer(vv.getGraphLayout());
            l.setSize(graphView.getGraphSize());

            LayoutTransition<DLObject, Number> lt = new LayoutTransition<DLObject, Number>(vv, vv.getGraphLayout(), l);
            Animator animator = new Animator(lt);
            animator.start();
            vv.repaint();
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }


    private JPanel getTitlePanel() {
        JPanel result = new JPanel();
        result.setLayout(new GridBagLayout());

        JLabel titleLabel = new JLabel(I18nSupport.getInstance().getLocString("ezdl.relations.relationKind") + " :");
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.LINE_START;
        c.insets = new Insets(0, 5, 0, 0);
        result.add(titleLabel, c);

        c = new GridBagConstraints();
        graphKindCombo = new JComboBox();
        graphKindCombo.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent evt) {
                JComboBox cb = (JComboBox) evt.getSource();
                Object o = cb.getSelectedItem();
                if (o != null) {
                    setGraphType((RelationGraphType) o);
                }
            }
        });
        c.gridx = 1;
        c.gridy = 0;
        c.anchor = GridBagConstraints.LINE_START;
        result.add(graphKindCombo, c);

        JLabel layoutLabel = new JLabel(I18nSupport.getInstance().getLocString("ezdl.relations.layout") + " :");
        c = new GridBagConstraints();
        c.gridx = 2;
        c.gridy = 0;
        c.anchor = GridBagConstraints.LINE_START;
        c.insets = new Insets(0, 5, 0, 0);
        result.add(layoutLabel, c);

        c = new GridBagConstraints();
        graphLayoutCombo = new JComboBox(getLayouts());
        graphLayoutCombo.addActionListener(new ActionListener() {

            private static final long serialVersionUID = 1L;


            @SuppressWarnings("unchecked")
            @Override
            public void actionPerformed(ActionEvent evt) {
                JComboBox cb = (JComboBox) evt.getSource();
                Object o = cb.getSelectedItem();
                if (o != null) {
                    setLayout((Class<? extends Layout<DLObject, Number>>) o, graphView.getVisualizationViewer());
                }
            }
        });
        graphLayoutCombo.setRenderer(new DefaultListCellRenderer() {

            private static final long serialVersionUID = 1L;


            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
                            boolean cellHasFocus) {
                String valueString = value.toString();
                valueString = valueString.substring(valueString.lastIndexOf('.') + 1);
                return super.getListCellRendererComponent(list, valueString, index, isSelected, cellHasFocus);
            }
        });
        c.gridx = 3;
        c.gridy = 0;
        c.anchor = GridBagConstraints.LINE_START;
        result.add(graphLayoutCombo, c);

        c = new GridBagConstraints();
        c.gridx = 4;
        c.weightx = 1.0f;
        c.fill = GridBagConstraints.HORIZONTAL;
        result.add(new JPanel(), c);

        int col = 5;
        List<Action> actions = getActions();
        for (Action a : actions) {
            c = new GridBagConstraints();
            c.gridx = col;
            c.gridy = 0;
            c.anchor = GridBagConstraints.LINE_END;
            TitleBtn tb = new TitleBtn(a);
            Dimension d = new Dimension(24, 24);
            tb.setSize(24, 24);
            tb.setMinimumSize(d);
            tb.setMaximumSize(d);
            tb.setPreferredSize(d);
            result.add(tb, c);
            col++;
        }

        result.setBorder(GroupSeparatorBorder.DEFAULT_GROUPS_CONTAINER_TITLE_BORDER);
        return result;
    }


    @SuppressWarnings("rawtypes")
    private void setLayoutComboValue(Class<? extends Layout> c) {
        graphLayoutCombo.setSelectedItem(c);
    }


    @SuppressWarnings({
                    "rawtypes", "unchecked"
    })
    private static Class<? extends Layout>[] getLayouts() {
        List<Class<? extends Layout>> layouts = new ArrayList<Class<? extends Layout>>();
        layouts.add(ISOMLayout.class);
        layouts.add(FRLayout.class);
        layouts.add(KKLayout.class);
        layouts.add(CircleLayout.class);
        return layouts.toArray(new Class[0]);
    }
}
