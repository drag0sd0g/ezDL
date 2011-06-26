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

package de.unidue.inf.is.ezdl.gframedl.tools.library;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.Comparator;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.jdesktop.swingx.JXList;
import org.jdesktop.swingx.decorator.HighlighterFactory;

import de.unidue.inf.is.ezdl.dlcore.data.dldata.Document;
import de.unidue.inf.is.ezdl.dlfrontend.i18n.I18nSupport;
import de.unidue.inf.is.ezdl.gframedl.components.TextComponentPopupMenu;



/** Panel which contains the library List */
public final class LibraryListPanel extends JPanel {

    private static final long serialVersionUID = -3746888688432943810L;

    private JXList referenceList;
    private JScrollPane referenceListScrollPane;
    private JPanel searchPanel;
    private JTextField searchTextField;

    I18nSupport i18n = I18nSupport.getInstance();


    public LibraryListPanel() {
        initialize();
    }


    private void initialize() {
        setLayout(new BorderLayout());
        referenceListScrollPane = new JScrollPane();
        referenceListScrollPane.setViewportView(getList());
        add(getSearchPanel(), BorderLayout.NORTH);
        add(referenceListScrollPane, BorderLayout.CENTER);
    }


    public JXList getList() {
        if (referenceList == null) {
            referenceList = new JXList();
            referenceList.setAutoCreateRowSorter(true);
            referenceList.setCellRenderer(new LibraryListRenderer());
            referenceList.setHighlighters(HighlighterFactory.createSimpleStriping(Color.decode("#ECEDF0")));
            referenceList.setComparator(new Comparator<Document>() {

                @Override
                public int compare(Document o1, Document o2) {
                    return o1.getTitle().compareTo(o2.getTitle());
                }
            });

        }
        return referenceList;
    }


    private JPanel getSearchPanel() {
        if (searchPanel == null) {
            searchPanel = new JPanel(new BorderLayout());
            searchPanel.add(new JLabel(i18n.getLocString("ezdl.tools.library.search")), BorderLayout.WEST);
            searchPanel.add(getSearchTextField(), BorderLayout.CENTER);
        }
        return searchPanel;
    }


    public JTextField getSearchTextField() {
        if (searchTextField == null) {
            searchTextField = new JTextField();
            TextComponentPopupMenu.addPopupMenu(searchTextField);
        }
        return searchTextField;
    }
}
