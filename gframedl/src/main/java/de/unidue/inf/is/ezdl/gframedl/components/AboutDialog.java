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

package de.unidue.inf.is.ezdl.gframedl.components;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.lang.management.ClassLoadingMXBean;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;
import java.nio.channels.IllegalSelectorException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.Timer;
import javax.swing.table.AbstractTableModel;

import org.apache.commons.io.IOUtils;

import de.unidue.inf.is.ezdl.dlcore.utils.StringUtils;
import de.unidue.inf.is.ezdl.dlfrontend.i18n.I18nSupport;
import de.unidue.inf.is.ezdl.gframedl.Images;



/**
 * This dialog shows information about ezDL.
 */
public final class AboutDialog extends JDialog {

    /*
     * Static attributes with inmutable data to be shown in properties table
     */
    static final String[] VERSION = new String[] {
                    "Version", "1.4.0"
    };
    static final String[] JAVA_VERSION = new String[] {
                    "Java Runtime Enviroment", System.getProperty("java.version")
    };
    static final String[] OS_NAME = new String[] {
                    "OS", System.getProperty("os.name")
    };


    private static class AboutDialogTableModel extends AbstractTableModel {

        private static final long serialVersionUID = 1786557125033788184L;

        private List<String[]> valuesToShow;


        AboutDialogTableModel() {
            refreshData();
        }


        @Override
        public int getColumnCount() {
            return 2;
        }


        @Override
        public String getColumnName(int column) {
            return column == 0 ? "Property" : "Value";
        }


        private List<String[]> getData() {
            MemoryUsage heapUsage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
            MemoryUsage nonHeapUsage = ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage();
            RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
            ClassLoadingMXBean classLoadingMXBean = ManagementFactory.getClassLoadingMXBean();
            ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
            List<GarbageCollectorMXBean> garbageCollectionMXBeans = ManagementFactory.getGarbageCollectorMXBeans();

            List<String[]> data = new ArrayList<String[]>();
            data.add(VERSION);
            data.add(JAVA_VERSION);
            data.add(OS_NAME);
            data.add(new String[] {
                            "Uptime", String.valueOf(runtimeMXBean.getUptime())
            });
            data.add(new String[] {
                            "Used Heap Space", StringUtils.fromByteToMegaOrGiga(heapUsage.getUsed())
            });
            data.add(new String[] {
                            "Max Heap Space", StringUtils.fromByteToMegaOrGiga(heapUsage.getMax())
            });
            data.add(new String[] {
                            "Initial Heap Space", StringUtils.fromByteToMegaOrGiga(heapUsage.getInit())
            });
            data.add(new String[] {
                            "Committed Heap Space", StringUtils.fromByteToMegaOrGiga(heapUsage.getCommitted())
            });
            data.add(new String[] {
                            "Used Non Heap Space", StringUtils.fromByteToMegaOrGiga(nonHeapUsage.getUsed())
            });
            data.add(new String[] {
                            "Max Non Heap Space", StringUtils.fromByteToMegaOrGiga(nonHeapUsage.getMax())
            });
            data.add(new String[] {
                            "Initial Non Heap Space", StringUtils.fromByteToMegaOrGiga(nonHeapUsage.getInit())
            });
            data.add(new String[] {
                            "Committed Non Heap Space", StringUtils.fromByteToMegaOrGiga(nonHeapUsage.getCommitted())
            });
            data.add(new String[] {
                            "Total Loaded Classes Count", String.valueOf(classLoadingMXBean.getTotalLoadedClassCount())
            });
            data.add(new String[] {
                            "Loaded Classes Count", String.valueOf(classLoadingMXBean.getLoadedClassCount())
            });
            data.add(new String[] {
                            "Unloaded Classes Count", String.valueOf(classLoadingMXBean.getUnloadedClassCount())
            });
            data.add(new String[] {
                            "Thread Count", String.valueOf(threadMXBean.getThreadCount())
            });
            long collectionCount = 0;
            for (GarbageCollectorMXBean garbageCollectorMXBean : garbageCollectionMXBeans) {
                collectionCount += Math.max(0, garbageCollectorMXBean.getCollectionCount());
            }
            data.add(new String[] {
                            "Garbage Collection Count", String.valueOf(collectionCount)
            });

            return data;
        }


        @Override
        public int getRowCount() {
            return valuesToShow.size();
        }


        @Override
        public String getValueAt(int rowIndex, int columnIndex) {
            return valuesToShow.get(rowIndex)[columnIndex];
        }


        /**
         * Refresh data.
         */
        public void refreshData() {
            valuesToShow = getData();
        }
    }


    private static final long serialVersionUID = 8666235475424750562L;

    private AboutDialogTableModel tableModel = new AboutDialogTableModel();
    private String licenseText = getLicenseText();
    private Timer timer = new Timer(1000, new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            tableModel.refreshData();
            tableModel.fireTableDataChanged();
        }

    });


    public AboutDialog(Window owner) {
        super(owner, ModalityType.APPLICATION_MODAL);
        setSize(600, 650);
        setTitle("ezDL");
        setLocationRelativeTo(null);
        setResizable(false);
        add(getContent());
    }


    private JPanel getContent() {
        JPanel panel = new JPanel(new GridBagLayout());

        JLabel iconLabel = new JLabel(new ImageIcon(Images.LOGO_EZDL_LARGE_SINGLE.getImage()));

        JTextArea licenseTextArea = new JTextArea(licenseText);
        licenseTextArea.setEditable(false);
        licenseTextArea.setLineWrap(true);
        licenseTextArea.setWrapStyleWord(true);
        licenseTextArea.setOpaque(false);
        licenseTextArea.setBorder(BorderFactory.createEmptyBorder());
        JScrollPane licenseScrollPane = new JScrollPane(licenseTextArea);

        JTable propertiesTable = new JTable(tableModel);
        propertiesTable.setBackground(Color.WHITE);
        propertiesTable.setShowGrid(false);
        JScrollPane propertiesScrollPane = new JScrollPane(propertiesTable);
        propertiesScrollPane.setBackground(Color.WHITE);
        propertiesScrollPane.getViewport().setBackground(Color.WHITE);

        JButton closeButton = new JButton(I18nSupport.getInstance().getLocString("ezdl.controls.close"));
        closeButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab(I18nSupport.getInstance().getLocString("ezdl.licence"), licenseScrollPane);
        tabbedPane.addTab(I18nSupport.getInstance().getLocString("ezdl.properties"), propertiesScrollPane);
        tabbedPane.setBackground(Color.WHITE);

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(0, 0, 0, 0);
        c.anchor = GridBagConstraints.CENTER;
        panel.add(iconLabel, c);

        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 1;
        c.weighty = 1;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(10, 20, 10, 20);
        panel.add(tabbedPane, c);

        c.gridy = 2;
        c.fill = GridBagConstraints.NONE;
        c.weighty = 0;
        c.insets = new Insets(0, 20, 10, 20);
        panel.add(closeButton, c);

        panel.setBackground(Color.WHITE);

        return panel;
    }


    private String getLicenseText() {
        try {
            return IOUtils.toString(AboutDialog.class.getResourceAsStream("/license.txt"), "UTF-8");
        }
        catch (IOException e) {
            throw new IllegalSelectorException();
        }
    }


    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            timer.start();
        }
        else {
            timer.stop();
        }
        super.setVisible(visible);
    }

}
