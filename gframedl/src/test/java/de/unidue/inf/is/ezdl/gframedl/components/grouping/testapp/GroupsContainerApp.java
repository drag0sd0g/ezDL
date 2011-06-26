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

package de.unidue.inf.is.ezdl.gframedl.components.grouping.testapp;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import de.unidue.inf.is.ezdl.gframedl.components.grouping.GroupContainer;
import de.unidue.inf.is.ezdl.gframedl.components.grouping.GroupsContainer;



/**
 * Testclass for the GroupedContainer.
 */
public class GroupsContainerApp {

    /**
     * @param args
     */
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
                new GroupsContainerApp();
            }

        });
    }


    /**
     * Testclass for the GroupedContainer.
     */
    public GroupsContainerApp() {
        JFrame frame = new JFrame("GroupsContainerTest");

        frame.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0f;
        c.weighty = 1.0f;
        c.fill = GridBagConstraints.BOTH;

        // Test with no main title
        // GroupsContainer g = new GroupsContainer();
        GroupsContainer g = new GroupsContainer("MyGroups", null);
        GroupContainer group2 = null;
        for (int i = 0; i < 10; i++) {
            GroupContainer gc = new GroupContainer("Group" + i, null, GroupsContainerApp.getTestComponent(), null);
            if (i == 2) {
                group2 = gc;
            }
            g.addGroupContainer(gc);
        }

        // Test remove
        g.removeGroupContainer(group2);

        frame.add(g, c);

        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }


    /**
     * A component for the test.
     * 
     * @return
     */
    public static JComponent getTestComponent() {
        JList result = new JList();
        result.setModel(new DefaultListModel());
        DefaultListModel dlm = (DefaultListModel) result.getModel();
        for (int i = 0; i < 10; i++) {
            dlm.addElement("TestItem ................................................................................. ed"
                            + i);
        }
        return result;
    }
}
