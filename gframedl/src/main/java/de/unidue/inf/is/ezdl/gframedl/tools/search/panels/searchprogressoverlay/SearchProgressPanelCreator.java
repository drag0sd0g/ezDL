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

import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;



/**
 * A creator class which creates for a model of
 * {@link SearchProgressSpinnerItem} a list of panels These panels will be shown
 * by {@link SearchProgressOverlay}.
 * 
 * @author markus
 */
class SearchProgressPanelCreator {

    private SearchProgressOverlayModel model;


    public SearchProgressPanelCreator(SearchProgressOverlayModel model) {
        this.model = model;
    }


    /**
     * Create a list of {@link SearchProgressPanel} for each entry in the model.
     * Each panel show a busy effect for a wrapper.
     * 
     * @return The list of panels.
     */
    public List<JPanel> constuctBusyLabelList() {
        List<JPanel> result = new ArrayList<JPanel>();
        int position = -1;
        for (SearchProgressSpinnerItem item : model.getItems()) {
            if (!item.isTotalState()) {
                position++;
                result.add(constructBusyLabel(item, position));
            }
        }
        return result;
    }


    /**
     * Create the total result panel.
     * 
     * @return The total result panel.
     */
    public JPanel constructBusyStatePanel() {
        JPanel tempPanel = new JPanel();
        int position = 0;
        for (SearchProgressSpinnerItem item : model.getItems()) {
            if (item.isTotalState()) {
                position++;
                return constructBusyLabel(item, position);

            }
        }
        return tempPanel;
    }


    /**
     * Create a panel for a model entry.
     * 
     * @param item
     *            The model item.
     * @param position
     *            The position of the item in the model to define the background
     *            color of the panel.
     * @return A panel for the item.
     */
    private JPanel constructBusyLabel(SearchProgressSpinnerItem item, int position) {
        return new SearchProgressPanel(item, position);

    }

}
