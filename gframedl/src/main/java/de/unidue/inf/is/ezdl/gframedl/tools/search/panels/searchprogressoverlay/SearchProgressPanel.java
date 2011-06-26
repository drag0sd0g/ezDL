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
import java.awt.GridLayout;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import de.unidue.inf.is.ezdl.dlfrontend.i18n.I18nSupport;



/**
 * A panel which shows the busy informations of a wrapper like spinner, wrapper
 * name etc. This panel will be shown in the {@link SearchProgressOverlay}.
 * 
 * @author markus
 */
class SearchProgressPanel extends JPanel implements Observer {

    private static final long serialVersionUID = 1931294308910346475L;

    private JPanel wrapperSpinnerPanel = null;
    private JPanel nameIconPanel = null;
    private JLabel wrapperNameLabel = null;
    private JLabel resultLabel = null;
    private SearchProgressSpinnerItem item = null;

    public static final String I18N_PREFIX = "ezdl.tools.search.busyOverlay.";


    /**
     * Constructor to generate the panel.
     * 
     * @param item
     *            The item which encapsulates the wrapper information.
     * @param position
     *            The position of the item in model.
     */
    public SearchProgressPanel(SearchProgressSpinnerItem item, Integer position) {

        item.addObserver(this);

        this.item = item;

        final Color backColor = selectColor(position);
        this.setBackground(backColor);

        wrapperNameLabel = new JLabel();
        wrapperNameLabel.setOpaque(false);
        wrapperNameLabel.setHorizontalAlignment(SwingConstants.LEFT);

        nameIconPanel = new JPanel();
        GridLayout layout = new GridLayout(0, 2);
        layout.setHgap(7);
        nameIconPanel.setLayout(layout);
        nameIconPanel.setBackground(backColor);

        wrapperSpinnerPanel = new JPanel();
        wrapperSpinnerPanel.setLayout(new BorderLayout());
        wrapperSpinnerPanel.setBackground(backColor);
        wrapperSpinnerPanel.add(item.getSpinner(), BorderLayout.CENTER);

        this.add(nameIconPanel);

        if (item.isTotalState()) {
            createsStatePanel();
        }

        else {
            createsWrapperPanel();
        }
        nameIconPanel.add(wrapperNameLabel);
        this.add(wrapperSpinnerPanel);

    }


    /**
     * Set Layout for a panel which shows the busy informations of a wrapper.
     */
    private void createsWrapperPanel() {
        this.setLayout(new GridLayout(0, 3));

        JLabel iconLabel = new JLabel(item.getIcon());
        iconLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        nameIconPanel.add(iconLabel);
        wrapperNameLabel.setText(item.getWrapperName());
        resultLabel = new JLabel();
        resultLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        this.add(resultLabel);

    }


    /**
     * Set Layout for a panel which shows found documents.
     */
    private void createsStatePanel() {
        this.setLayout(new GridLayout(0, 2));
        nameIconPanel.setLayout(new GridLayout(0, 1));
        wrapperNameLabel.setHorizontalAlignment(SwingConstants.LEFT);
        wrapperNameLabel.setText(item.getWrapperID() + "  " + item.getResultCount());
    }


    @Override
    public void update(Observable arg0, Object arg1) {
        if (item.isTotalState()) {
            wrapperNameLabel.setText(item.getWrapperID() + " : " + item.getResultCount());
        }
        else {
            wrapperSpinnerPanel.add(new JLabel(I18nSupport.getInstance().getLocString(I18N_PREFIX + "results")),
                            BorderLayout.CENTER);
            resultLabel.setText(item.getResultCount() + " ");
        }
    }


    /**
     * Every even item has a blue color.
     * 
     * @param position
     *            Position of the item in the model.
     * @return Returns the background color.
     */
    private Color selectColor(int position) {
        switch (position % 2) {
            case 0:
                return new Color(237, 243, 254);

            default:
                return Color.WHITE;
        }
    }

}
