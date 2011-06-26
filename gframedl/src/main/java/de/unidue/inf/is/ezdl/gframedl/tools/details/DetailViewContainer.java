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

package de.unidue.inf.is.ezdl.gframedl.tools.details;

import java.awt.Component;
import java.util.List;

import javax.swing.Action;
import javax.swing.JLayeredPane;

import de.unidue.inf.is.ezdl.dlcore.data.dldata.DLObject;
import de.unidue.inf.is.ezdl.gframedl.tools.details.components.OverlayBusyPanel;
import de.unidue.inf.is.ezdl.gframedl.tools.details.components.OverlayButton;
import de.unidue.inf.is.ezdl.gframedl.tools.details.components.OverlayButtonPanel;



/**
 * DetailViews are shown within this container. This class is used to query a
 * DetailView for its possible actions (e.g. PrintAction). To assembly/display
 * OverlayButtonPanel for this actions.
 */
public class DetailViewContainer extends JLayeredPane {

    private static final long serialVersionUID = 5796161335081002427L;

    private int layerCount;
    private Component detailView;
    private OverlayButtonPanel overlayButtonPanel;
    private OverlayBusyPanel overlayBusyPanel;


    /**
     * Constructor, a DetailView-Component is expected.
     */
    public DetailViewContainer(Component dv) {
        super();
        setLayout(null);
        setDoubleBuffered(true);
        detailView = dv;

        layerCount = JLayeredPane.DEFAULT_LAYER.intValue();
        add(dv, JLayeredPane.DEFAULT_LAYER);

        overlayButtonPanel = queryViewsPossibleActions();
        if (overlayButtonPanel != null) {
            addNewLayer(overlayButtonPanel);
        }
        overlayBusyPanel = new OverlayBusyPanel();
        addNewLayer(overlayBusyPanel);
        setBusy(false);
    }


    /**
     * Sets the state of the integrated OverlayBusyPanel.
     */
    public void setBusy(boolean b) {
        overlayBusyPanel.setBusy(b);
    }


    /**
     * Gets the state of the integrated OverlayBusyPanel.
     */
    public boolean isBusy() {
        return overlayBusyPanel.isBusy();
    }


    /**
     * Returns the contained DetailView.
     */
    public DetailView getDetailView() {
        return (DetailView) detailView;
    }


    /**
     * Returns the contained DLObject.
     */
    public DLObject getDLObject() {
        return getDetailView().getObject();
    }


    /**
     * Determines if the DLObject "o" is contained. Uses
     * DLObject.getOid().equals(), this method is not used at present [see
     * DetailToolView().getDetailViewIndex()].
     */
    public boolean containsObject(DLObject o) {
        DLObject ref = ((DetailView) detailView).getObject();
        if ((ref != null) && (o != null)) {
            return ref.getOid().equals(o.getOid());
        }
        else {
            return false;
        }
    }


    private void addNewLayer(Component c) {
        layerCount++;
        // do not replace new Integer() with Interger.valueOf()
        add(c, new Integer(JLayeredPane.DEFAULT_LAYER.intValue() + layerCount));
    }


    private OverlayButtonPanel queryViewsPossibleActions() {
        OverlayButtonPanel result = new OverlayButtonPanel();

        List<Action> actions = getDetailView().getPossibleActions();
        if (actions != null) {
            for (Action a : actions) {
                result.addOverlayButton(new OverlayButton(a));
            }
        }

        if (result.getComponentCount() > 0) {
            return result;
        }
        else {
            return null;
        }
    }


    @Override
    public void repaint() {
        detailView.setBounds(0, 0, this.getWidth(), this.getHeight());
        if (overlayButtonPanel != null) {
            overlayButtonPanel.setBounds(0, 0, this.getWidth(), this.getHeight());
        }
        if (!overlayBusyPanel.getSize().equals(getSize())) {
            overlayBusyPanel.setBounds(0, 0, this.getWidth(), this.getHeight());
            overlayBusyPanel.resetPainter();
        }
        super.repaint();
    }
}
