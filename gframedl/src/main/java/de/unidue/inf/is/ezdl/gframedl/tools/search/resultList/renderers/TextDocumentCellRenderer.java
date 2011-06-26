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

package de.unidue.inf.is.ezdl.gframedl.tools.search.resultList.renderers;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import de.unidue.inf.is.ezdl.dlcore.utils.StringUtils;
import de.unidue.inf.is.ezdl.gframedl.Colors;
import de.unidue.inf.is.ezdl.gframedl.tools.search.ResultItem;
import de.unidue.inf.is.ezdl.gframedl.tools.search.ResultListModel;
import de.unidue.inf.is.ezdl.gframedl.tools.search.resultList.ResultList;
import de.unidue.inf.is.ezdl.gframedl.utils.HighlightingUtils;



/**
 * Component for rendering a TextDocument in result list.
 * 
 * @author tipografov, tbeckers
 */
public final class TextDocumentCellRenderer extends JComponent implements ListCellRenderer {

    private static final long serialVersionUID = 7436107310380473511L;

    private static final int RSV_BAR_WIDTH = 20;
    private static final int RSV_BAR_HEIGHT = 5;
    private static final int TOP_OFFSET = 20;
    private static final int LEFT_OFFSET = 10;
    private static final int RIGHT_OFFSET = 10;
    private static final int TEXT_START_OFFSET = 20;

    private static final LinearGradientPaint PAINT = new LinearGradientPaint(10, 25, 10 + RSV_BAR_WIDTH, 25,
                    new float[] {
                                    .05f, .7f
                    }, new Color[] {
                                    new Color(255, 99, 071), new Color(50, 205, 50)
                    });

    private ResultItem resultItem;
    private ResultList list;
    private ResultListModel resultListModel;
    private boolean isSelected;
    private boolean cellHasFocus;
    private int index;

    private List<TextLayout> textLines;
    private FontRenderContext frc;


    /**
     * Constructor, expects a JList-owner.
     */
    public TextDocumentCellRenderer() {
        super();
        this.setDoubleBuffered(true);
        textLines = new ArrayList<TextLayout>();
    }


    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D graphics2d = (Graphics2D) g;
        graphics2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        paintBackground(graphics2d);
        paintResultItem(g);
        paintRelevanceBar(graphics2d);
        paintAnnotationIcons(graphics2d);
    }


    private void paintBackground(Graphics2D g) {
        if (cellHasFocus || isSelected) {
            g.setColor(list.getSelectionBackground());
            g.fillRect(0, 0, this.getWidth(), this.getHeight());
        }
        else {
            g.setColor(list.getBackground());
            g.fillRect(0, 0, this.getWidth(), this.getHeight());
        }
    }


    private void paintRelevanceBar(Graphics2D g) {
        int relevance = Math.round((float) resultItem.getRsv() * RSV_BAR_WIDTH);
        g.setPaint(PAINT);
        g.fillRect(LEFT_OFFSET, TOP_OFFSET + 7, relevance, RSV_BAR_HEIGHT);
        g.setColor(Color.LIGHT_GRAY);
        g.drawRect(LEFT_OFFSET, TOP_OFFSET + 7, RSV_BAR_WIDTH, RSV_BAR_HEIGHT);
    }


    private void paintAnnotationIcons(Graphics2D g) {
    }


    private void paintResultItem(Graphics g) {
        FontMetrics fm = g.getFontMetrics();
        if (resultItem != null) {
            resultItem.getIcon().paintIcon(this, g, 10, 10 + (fm.getHeight()) * 2);

            if (cellHasFocus || isSelected) {
                g.setColor(list.getBackground());
            }
            else {
                g.setColor(list.getForeground());
            }

            // int positionInModel =
            // list.getResultPanel().getPositionInModel(resultItem);
            int positionInModel = index;
            g.drawString(String.valueOf(positionInModel + 1) + '.', LEFT_OFFSET, TOP_OFFSET);

            textLines(((Graphics2D) g).getFontRenderContext());
            int textBaseline = 5 + fm.getHeight();
            for (TextLayout line : textLines) {

                line.draw((Graphics2D) g, LEFT_ALIGNMENT + getTextPositionOffset(fm), textBaseline);
                textBaseline += fm.getHeight();
            }

        }
    }


    private void textLines(FontRenderContext frc) {
        List<String> queryTerms = list.getResultPanel().getQueryTermsForHighlighting();
        AttributedString hTitle = getHighlightedString(resultItem.getTitle(), queryTerms, true, false, true);
        AttributedString hAuthors = getHighlightedString(resultItem.getAuthors(), queryTerms, false, true, false);
        AttributedString hYearAndSource = getHighlightedString(resultItem.getYear() + " (" + resultItem.getSourceDLs()
                        + ")", queryTerms, false, false, false);

        textLines.clear();
        makeTextLines(frc, hTitle);
        makeTextLines(frc, hAuthors);
        makeTextLines(frc, hYearAndSource);
    }


    private void makeTextLines(FontRenderContext frc, AttributedString attributedString) {
        if (attributedString != null) {
            TextLayout layout = new TextLayout(attributedString.getIterator(), frc);
            textLines.add(layout);
        }
    }


    private AttributedString getHighlightedString(String string, List<String> termsToHighlight, boolean bold,
                    boolean italic, boolean big) {
        AttributedString result = null;

        if (!StringUtils.isEmpty(string)) {
            result = new AttributedString(string);

            applyFontAttributes(bold, italic, big, result);

            string = string.toLowerCase();
            highlightQueryTerms(string, termsToHighlight, result);
            highlightFilterTerms(string, result);
        }
        return result;
    }


    private void highlightFilterTerms(String string, AttributedString result) {
        String filterString = list.getResultPanel().getFilter();
        boolean filterStringNormalization = !StringUtils.containsNonAsciiCharacters(filterString);
        if (!StringUtils.isEmpty(filterString)) {
            String filterStringForHighlighting = filterStringNormalization ? StringUtils.normalize(filterString)
                            : filterString;
            String stringForHighlighting = filterStringNormalization ? StringUtils.normalize(string) : string;
            if (filterStringForHighlighting.length() == filterString.length()
                            && stringForHighlighting.length() == string.length()) {
                highlightInString(stringForHighlighting, filterStringForHighlighting, result,
                                Colors.FILTER_HIGHLIGHT_FOREGROUND_COLOR, Colors.FILTER_HIGHLIGHT_BACKGROUND_COLOR);
            }
        }
    }


    private void highlightQueryTerms(String string, List<String> termsToHighlight, AttributedString result) {
        if (termsToHighlight != null) {
            Color color = isSelected ? Colors.SELECTED_HIGHLIGHT_FOREGROUND_COLOR : Colors.HIGHLIGHT_FOREGROUND_COLOR;
            highlightTermInString(string, termsToHighlight, result, color, null);
        }
    }


    private void applyFontAttributes(boolean bold, boolean italic, boolean big, AttributedString result) {
        Font font = getFont();
        if (bold) {
            font = font.deriveFont(Font.BOLD);
            result.addAttribute(TextAttribute.FONT, font);
        }
        if (italic) {
            font = font.deriveFont(Font.ITALIC);
            result.addAttribute(TextAttribute.FONT, font);
        }
        if (big) {
            font = font.deriveFont((float) font.getSize() + 2);
            result.addAttribute(TextAttribute.FONT, font);
        }
    }


    /**
     * highlights only terms
     */
    private static void highlightTermInString(String string, List<String> terms, AttributedString result,
                    Color foreground, Color background) {
        if (terms.size() > 0) {
            Matcher matcher = HighlightingUtils.matcher(terms, false, string, true);
            while (matcher.find()) {
                int start = matcher.start(1);
                int end = matcher.end(1);

                if (foreground != null) {
                    result.addAttribute(TextAttribute.FOREGROUND, foreground, start, end);
                }
                if (background != null) {
                    result.addAttribute(TextAttribute.BACKGROUND, background, start, end);
                }
            }
        }
    }


    /**
     * highlights also parts of terms
     */
    private static void highlightInString(String string, String term, AttributedString result, Color foreground,
                    Color background) {
        int indexOfSubstring = 0;
        while ((indexOfSubstring = string.indexOf(term, indexOfSubstring)) != -1) {
            if (foreground != null) {
                result.addAttribute(TextAttribute.FOREGROUND, foreground, indexOfSubstring,
                                indexOfSubstring + term.length());
            }
            if (background != null) {
                result.addAttribute(TextAttribute.BACKGROUND, background, indexOfSubstring,
                                indexOfSubstring + term.length());
                indexOfSubstring += term.length();
            }
            indexOfSubstring += term.length();
        }
    }


    @Override
    public Dimension getPreferredSize() {
        FontMetrics fm = getFontMetrics(getFont());

        try {
            textLines(frc);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        long max = maxWidth();
        return new Dimension((int) max + LEFT_OFFSET + getTextPositionOffset(fm) + RIGHT_OFFSET, TOP_OFFSET
                        + textLines.size() * fm.getHeight());
    }


    private long maxWidth() {
        long max = 0;
        for (TextLayout layout : textLines) {
            long w = Math.round(layout.getBounds().getWidth());
            if (w > max) {
                max = w;
            }
        }
        return max;
    }


    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
        this.list = (ResultList) list;
        frc = ((Graphics2D) list.getGraphics()).getFontRenderContext();

        if (!list.isDoubleBuffered()) {
            list.setDoubleBuffered(true);
        }
        this.resultListModel = ((ResultListModel) list.getModel());
        if (value instanceof ResultItem) {
            this.resultItem = (ResultItem) value;
        }
        else {
            this.resultItem = null;
        }
        this.isSelected = isSelected;
        this.cellHasFocus = cellHasFocus;
        this.index = index;

        return this;
    }


    /**
     * Calculate the offset of a result cell.
     * 
     * @param fm
     *            The current {@link FontMetrics} of the renderer.
     * @return Returns the offset value.
     */
    private int getTextPositionOffset(FontMetrics fm) {

        int resultWith = fm.stringWidth(String.valueOf(resultListModel.size())) + TEXT_START_OFFSET;
        int rsvOffset = LEFT_OFFSET + RSV_BAR_WIDTH + 7;

        if (rsvOffset < resultWith) {
            return resultWith;
        }

        return rsvOffset;
    }
}
