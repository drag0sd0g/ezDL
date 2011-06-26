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

package de.unidue.inf.is.ezdl.gframedl.components.checkboxlist;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.text.BadLocationException;
import javax.swing.text.Utilities;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.unidue.inf.is.ezdl.gframedl.components.DefaultHighlightingListCellRenderer;
import de.unidue.inf.is.ezdl.gframedl.components.FilterTextField;



/**
 * Custom cell renderer for the {@link CheckBoxJList}.
 * 
 * @author tbeckers
 * @author tacke
 */
public class CheckBoxListCellRenderer extends DefaultHighlightingListCellRenderer implements ListCellRenderer {

    private static final long serialVersionUID = -2433963163731618219L;

    private static final Logger logger = Logger.getLogger(CheckBoxListCellRenderer.class);

    private static final int EVEN = 0;
    private static final int ODD = 1;
    private static final int ODD_SELECTED = 2;
    private static final int EVEN_SELECTED = 3;

    private static final Color HIGHLIGHT = new Color(237, 243, 254);
    private static final Color SELECTION;

    static {
        if ("Nimbus".equals(UIManager.getLookAndFeel().getID())) {
            SELECTION = UIManager.getColor("List[Selected].textBackground");
        }
        else {
            SELECTION = new Color(0, 94, 207);
        }
    }
    private JPanel panel;

    private JCheckBox choice;
    private JTextArea description;
    private JLabel iconLabel;
    private JPanel choicePanel;


    public CheckBoxListCellRenderer(FilterTextField filterTextField) {
        super(filterTextField);
        initialize();
    }


    private void initialize() {
        panel = new JPanel();
        panel.setLayout(new BorderLayout());

        choicePanel = new JPanel(new BorderLayout());

        choice = new JCheckBox();

        choicePanel.add(choice, BorderLayout.CENTER);
        choicePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        choicePanel.setPreferredSize(new Dimension(150, 48));

        description = new JTextArea();
        description.setOpaque(true);
        description.setEditable(false);
        description.setLineWrap(true);
        description.setWrapStyleWord(true);

        description.setSize(400, 30);
        description.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        iconLabel = new JLabel();
        iconLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        choicePanel.add(iconLabel, BorderLayout.EAST);

        panel.add(choicePanel, BorderLayout.WEST);
        panel.add(description, BorderLayout.CENTER);

        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        panel.revalidate();
        panel.repaint();
    }


    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
                    boolean hasFocus) {

        choice.setEnabled(list.isEnabled());
        choice.setSelected(((CheckBoxListItem) value).isSelected());

        choice.setFont(list.getFont());
        choice.setText("<html>" + highlight(value.toString(), false) + "</html>");

        description.setFont(list.getFont());
        String text = ((CheckBoxListItem) value).getDescription();

        ImageIcon icon = ((CheckBoxListItem) value).getIcon();
        this.iconLabel.setIcon(icon);

        description.setText(text);
        shortenDescription();

        int type = index % 2;
        if (isSelected && type == 0) {
            type = EVEN_SELECTED;
        }

        if (isSelected && type == 1) {
            type = ODD_SELECTED;
        }

        switch (type) {
            case ODD_SELECTED: {
                paintCell(Color.WHITE, SELECTION);
                break;
            }
            case EVEN_SELECTED: {
                paintCell(Color.WHITE, SELECTION);
                break;
            }
            case ODD: {
                paintCell(Color.BLACK, HIGHLIGHT);
                break;
            }
            case EVEN: {
                paintCell(Color.BLACK, Color.WHITE);
                break;
            }
        }

        return panel;
    }


    private void paintCell(Color foreground, Color background) {
        panel.setBackground(background);
        panel.setForeground(foreground);
        choicePanel.setBackground(background);
        choicePanel.setForeground(foreground);
        choice.setForeground(foreground);
        description.setBackground(background);
        description.setForeground(foreground);
    }


    private void shortenDescription() {
        List<String> lines = new ArrayList<String>();

        int length = description.getDocument().getLength();
        int offset = 0;

        try {
            while (offset < length) {
                int end = Utilities.getRowEnd(description, offset);

                if (end < 0) {
                    break;
                }

                // Include the last character on the line
                end = Math.min(end + 1, length);

                String line = description.getDocument().getText(offset, end - offset);

                // Remove the line break character
                if (line.endsWith("\n")) {
                    line = line.substring(0, line.length() - 1);
                }

                lines.add(line);

                offset = end;
            }
        }
        catch (BadLocationException e) {
            logger.error(e.getMessage(), e);
        }

        makeDescriptionText(lines);

        makeTooltipText(lines);
    }


    private void makeDescriptionText(List<String> lines) {
        StringBuilder textBuilder = new StringBuilder();
        if (lines.size() > 1) {
            String lastLine = lines.get(1);

            if (lines.size() > 2) {
                lastLine = StringUtils.abbreviate(lastLine, lastLine.length() - 1);
            }

            textBuilder.append(lines.get(0)).append("\n").append(lastLine);
            description.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        }
        else if (lines.size() > 0) {
            textBuilder.append(lines.get(0));
            description.setBorder(BorderFactory.createEmptyBorder(16, 10, 10, 10));
        }

        description.setText(textBuilder.toString());
    }


    private void makeTooltipText(List<String> lines) {
        StringBuilder textBuilder = new StringBuilder();
        textBuilder.append("<html>");

        for (String line : lines) {
            textBuilder.append(line).append("<br>");
        }
        textBuilder.append("</html>");

        panel.setToolTipText(textBuilder.toString());
    }
}
