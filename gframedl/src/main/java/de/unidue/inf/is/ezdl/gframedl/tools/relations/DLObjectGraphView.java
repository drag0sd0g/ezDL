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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JPanel;

import org.apache.commons.collections15.Transformer;

import de.unidue.inf.is.ezdl.dlcore.data.dldata.DLObject;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.Person;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.Term;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.TextDocument;
import de.unidue.inf.is.ezdl.gframedl.Icons;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.DefaultVisualizationModel;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.VisualizationModel;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.decorators.EllipseVertexShapeTransformer;
import edu.uci.ics.jung.visualization.decorators.PickableEdgePaintTransformer;
import edu.uci.ics.jung.visualization.renderers.DefaultEdgeLabelRenderer;
import edu.uci.ics.jung.visualization.renderers.DefaultVertexLabelRenderer;



/**
 * The component displays a DLObject graph.
 * 
 * @author RT
 */
public class DLObjectGraphView extends JPanel {

    private static final long serialVersionUID = 1L;

    private Graph<DLObject, Number> graph;

    private VisualizationViewer<DLObject, Number> vv;
    private Layout<DLObject, Number> layout;
    private GraphZoomScrollPane gzsp;
    private final ScalingControl scaler = new CrossoverScalingControl();
    private DefaultModalGraphMouse<String, Number> graphMouse;
    private Dimension graphSize;
    private RelationGraphType rgt;


    /**
     * Constructor.
     */
    public DLObjectGraphView(List<DLObject> objects, RelationGraphType rgt) {
        this.rgt = rgt;
        setLayout(new BorderLayout());

        graph = rgt.getGraph(objects);
        layout = rgt.getLayout(graph);
        graphSize = rgt.getGraphSize(graph);

        final VisualizationModel<DLObject, Number> visualizationModel = new DefaultVisualizationModel<DLObject, Number>(
                        layout, graphSize);
        vv = new VisualizationViewer<DLObject, Number>(visualizationModel, graphSize);

        setRenderers();

        graphMouse = new DefaultModalGraphMouse<String, Number>();
        vv.setGraphMouse(graphMouse);
        vv.addKeyListener(graphMouse.getModeKeyListener());

        gzsp = new GraphZoomScrollPane(vv);

        add(gzsp, BorderLayout.CENTER);
    }


    /**
     * Change the mouse mode.
     */
    public void setMouseModeToPicking() {
        graphMouse.setMode(ModalGraphMouse.Mode.PICKING);
    }


    /**
     * Change the mouse mode.
     */
    public void setMouseModeToTransforming() {
        graphMouse.setMode(ModalGraphMouse.Mode.TRANSFORMING);
    }


    /**
     * Zoom in.
     */
    public void zoomIn() {
        scaler.scale(vv, 1.1f, vv.getCenter());
    }


    /**
     * Zoom out.
     */
    public void zoomOut() {
        scaler.scale(vv, 1 / 1.1f, vv.getCenter());
    }


    /**
     * Zoom the graph to the component size.
     */
    public void zoomBestFit() {
        int wh = graphSize.width / 2;
        int hh = graphSize.height / 2;
        vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT).setToIdentity();
        vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.VIEW).setToIdentity();
        if ((graphSize.width > vv.getWidth()) || (graphSize.height > vv.getHeight())) {
            vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.VIEW)
                            .translate(-(wh - getWidth() / 2), -(hh - getHeight() / 2));

            int minSide = Math.min(vv.getWidth(), vv.getHeight());
            double scale = 1.0d / (((double) hh * 2) / minSide);

            int x = vv.getWidth() / 2 - (int) Math.round(wh * scale);
            int y = vv.getHeight() / 2 - (int) Math.round(hh * scale);

            vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.VIEW)
                            .setScale(scale, scale, new Point(x, y));
        }
    }


    /**
     * Returns the layout class.
     * 
     * @return
     */
    @SuppressWarnings("rawtypes")
    public Class<? extends Layout> getLayoutClass() {
        return layout.getClass();
    }


    /**
     * Returns the graph.
     * 
     * @return
     */
    public Graph<DLObject, Number> getGraph() {
        return graph;
    }


    /**
     * Returns the size of the graph.
     * 
     * @return
     */
    public Dimension getGraphSize() {
        return graphSize;
    }


    /**
     * Returns the visualization viewer component.
     * 
     * @return
     */
    public VisualizationViewer<DLObject, Number> getVisualizationViewer() {
        return vv;
    }


    private int getVertexSize(DLObject v) {
        return rgt.getVertexRadius(v, graph);
    }


    private void setRenderers() {
        vv.getRenderContext().setVertexLabelTransformer(new Transformer<DLObject, String>() {

            @Override
            public String transform(DLObject v) {
                String result = "";
                if (v instanceof Person) {
                    result = ((Person) v).getFirstName() + " " + ((Person) v).getLastName();
                }
                if (v instanceof TextDocument) {
                    result = ((TextDocument) v).getTitle();
                }
                return result;
            }
        });
        vv.getRenderContext().setVertexLabelRenderer(new DefaultVertexLabelRenderer(Color.red));
        vv.getRenderContext().setEdgeLabelRenderer(new DefaultEdgeLabelRenderer(Color.cyan));
        vv.getRenderContext().setVertexShapeTransformer(new VertexShapeFunction<DLObject>());

        vv.getRenderContext().setVertexIconTransformer(new Transformer<DLObject, Icon>() {

            @Override
            public Icon transform(final DLObject v) {
                final int size = getVertexSize(v);
                return new Icon() {

                    @Override
                    public int getIconHeight() {
                        return size;
                    }


                    @Override
                    public int getIconWidth() {
                        return size;
                    }


                    @Override
                    public void paintIcon(Component c, Graphics g, int x, int y) {
                        int halfSize = size / 2;
                        if (vv.getPickedVertexState().isPicked(v)) {
                            g.setColor(Color.yellow);
                        }
                        else {
                            g.setColor(Color.lightGray);
                        }
                        g.fillOval(x, y, size, size);

                        if (vv.getPickedVertexState().isPicked(v)) {
                            g.setColor(Color.red);
                        }
                        else {
                            g.setColor(Color.black);
                        }
                        g.drawOval(x, y, size, size);

                        int iconX = x + halfSize - 8;
                        int iconY = y + halfSize - 8;

                        if (v instanceof Person) {
                            Icons.MEDIA_AUTHOR.get16x16().paintIcon(c, g, iconX, iconY);
                        }
                        if (v instanceof TextDocument) {
                            Icons.MEDIA_TEXT.get16x16().paintIcon(c, g, iconX, iconY);
                        }
                        if (v instanceof Term) {
                            Icons.MEDIA_TERM.get16x16().paintIcon(c, g, iconX, iconY);
                        }
                    }
                };
            }
        });

        vv.getRenderContext().setEdgeDrawPaintTransformer(
                        new PickableEdgePaintTransformer<Number>(vv.getPickedEdgeState(), Color.black, Color.black));
        vv.setBackground(Color.white);
    }


    private class VertexShapeFunction<V> extends EllipseVertexShapeTransformer<V> {

        @Override
        public Shape transform(V v) {
            int size = getVertexSize((DLObject) v);
            int halfSize = -(size / 2);
            return new Rectangle(halfSize, halfSize, size, size);
        }
    }
}
