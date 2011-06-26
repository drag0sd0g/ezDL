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

package de.unidue.inf.is.ezdl.gframedl.components.grouping.list.testapp;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jdesktop.swingx.JXList;

import de.unidue.inf.is.ezdl.gframedl.components.grouping.list.GroupByRelation;
import de.unidue.inf.is.ezdl.gframedl.components.grouping.list.GroupedList;
import de.unidue.inf.is.ezdl.gframedl.components.grouping.list.GroupedListConfig;



/**
 * Test application and usage example for the GroupedList component.
 * 
 * @author RB1
 */
public class GroupedListApp implements ListSelectionListener {

    public GroupedList list;


    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                try {
                    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                        if ("Nimbus".equals(info.getName())) {
                            UIManager.setLookAndFeel(info.getClassName());
                            break;
                        }
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                new GroupedListApp();
            }

        });
    }


    public GroupedListApp() {
        JFrame frame = new JFrame("GroupedListTest");

        frame.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0f;
        c.weighty = 1.0f;
        c.fill = GridBagConstraints.BOTH;

        /**
         * Create list instance.
         */
        list = new GroupedList();

        /**
         * Create a list config.
         */
        list.setConfig(new GroupedListConfig() {

            /**
             * Possible grouping relations.
             */
            @Override
            public List<GroupByRelation> getRelations() {
                List<GroupByRelation> result = new ArrayList<GroupByRelation>();

                /**
                 * Allow the no-grouping option.
                 */
                result.add(new GroupByRelation());

                result.add(new GroupByMod2());
                result.add(new GroupByMod5());
                return result;
            }


            /**
             * Provide getter for the initialized list instance.
             */
            @Override
            public JXList getEmptyListInstance() {
                JXList l = new JXList();

                /**
                 * Here we set the cellrenderer, mouselisteners, context menus
                 * etc.
                 */

                // l.setRolloverEnabled(true);
                // l.setCellRenderer(new DefaultListRenderer());
                // l.addHighlighter(new
                // ColorHighlighter(HighlightPredicate.ROLLOVER_ROW,
                // null, Color.RED));

                return l;
            }

        });

        // Test SelectionListener
        list.addListSelectionListener(this);

        /**
         * Generate test docs.
         */
        Random rnd = new Random();
        for (int i = 0; i < 20; i++) {
            list.addElement(new Item(rnd.nextInt(10)));
        }

        frame.add(list, c);

        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 1;
        frame.add(new GroupedListTestActions(list), c);

        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }


    @Override
    public void valueChanged(ListSelectionEvent arg0) {
        System.out.println("Selection event fired.");
        System.out.println("Firstindex " + arg0.getFirstIndex());
    }
}
