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

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JPanel;

import de.unidue.inf.is.ezdl.gframedl.components.grouping.list.GroupedList;



/**
 * Test actions for the grouped list test.
 * 
 * @author RB1
 */
public class GroupedListTestActions extends JPanel {

    private static final long serialVersionUID = 1L;
    private GroupedList list;


    public GroupedListTestActions(GroupedList l) {
        super();
        list = l;
        setLayout(new FlowLayout());

        JButton b;

        b = new JButton("getSelectedValue");
        b.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                Object o = list.getSelectedValue();
                System.out.println("getSelectedValue :");
                System.out.println(o);
            }
        });
        add(b);

        b = new JButton("getSelectedValues");
        b.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                List<Object> o = list.getSelectedValues();
                System.out.println("getSelectedValues :");
                System.out.println(o);
            }
        });
        add(b);

        b = new JButton("getSelectedIndex");
        b.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                int i = list.getSelectedIndex();
                System.out.println("getSelectedIndex :");
                System.out.println(i);
                System.out.println(list.getElementAt(i));
            }
        });
        add(b);

        b = new JButton("getSelectedIndices");
        b.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                List<Integer> l = list.getSelectedIndices();
                System.out.println("getSelectedIndices :");
                System.out.println(l);
                for (Integer i : l) {
                    System.out.println(list.getElementAt(i));
                }
            }
        });
        add(b);

        b = new JButton("addElement");
        b.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                Random rnd = new Random();
                int n = rnd.nextInt(5);
                System.out.println("AddElement : " + n);
                list.addElement(new Item(n));
            }
        });
        add(b);

        b = new JButton("removeElement");
        b.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                /*
                 * Object o = list.getSelectedValue(); if (o != null){
                 * list.removeElement(o); }/*
                 */

                List<Object> ol = list.getSelectedValues();
                // DefaultListModel dlm = (DefaultListModel)list.getModel();
                for (Object o : ol) {
                    // dlm.removeElement(o);
                    list.removeElement(o);
                }
            }
        });
        add(b);

        b = new JButton("clearSelection");
        b.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                list.clearSelection();
            }
        });
        add(b);
    }
}
