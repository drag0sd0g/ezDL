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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;



/**
 * The TextPrompt class will display a prompt over top of a text component when
 * the Document of the text field is empty. The Show property is used to
 * determine the visibility of the prompt. The Font and foreground Color of the
 * prompt will default to those properties of the parent text component. You are
 * free to change the properties after class construction.
 */
public final class TextPrompt extends JLabel implements FocusListener, DocumentListener, PropertyChangeListener {

    private static final long serialVersionUID = -3144193447745261408L;


    public enum Show {
        ALWAYS, FOCUS_GAINED, FOCUS_LOST;
    }


    private JTextComponent component;
    private Document document;

    private Show show;
    private boolean showPromptOnce;
    private int focusLost;


    private TextPrompt(String text, JTextComponent component) {
        this(text, component, Show.ALWAYS);
    }


    private TextPrompt(String text, JTextComponent component, Show show) {
        this.component = component;
        setShow(show);
        document = component.getDocument();

        setText(text);
        setFont(component.getFont());
        setForeground(component.getForeground());
        setBorder(new EmptyBorder(component.getInsets()));
        setHorizontalAlignment(SwingConstants.LEADING);

        component.addFocusListener(this);
        component.addPropertyChangeListener("enabled", this);
        document.addDocumentListener(this);

        component.setLayout(new BorderLayout());
        component.add(this);
        checkForPrompt();
    }


    /**
     * Convenience method to change the alpha value of the current foreground
     * Color to the specifice value.
     * 
     * @param alpha
     *            value in the range of 0 - 1.0.
     */
    public void changeAlpha(float alpha) {
        changeAlpha((int) (alpha * 255));
    }


    /**
     * Convenience method to change the alpha value of the current foreground
     * Color to the specifice value.
     * 
     * @param alpha
     *            value in the range of 0 - 255.
     */
    public void changeAlpha(int alpha) {
        alpha = alpha > 255 ? 255 : alpha < 0 ? 0 : alpha;

        Color foreground = getForeground();
        int red = foreground.getRed();
        int green = foreground.getGreen();
        int blue = foreground.getBlue();

        Color withAlpha = new Color(red, green, blue, alpha);
        super.setForeground(withAlpha);
    }


    /**
     * Convenience method to change the style of the current Font. The style
     * values are found in the Font class. Common values might be: Font.BOLD,
     * Font.ITALIC and Font.BOLD + Font.ITALIC.
     * 
     * @param style
     *            value representing the the new style of the Font.
     */
    public void changeStyle(int style) {
        setFont(getFont().deriveFont(style));
    }


    /**
     * Get the Show property
     * 
     * @return the Show property.
     */
    public Show getShow() {
        return show;
    }


    /**
     * Set the prompt Show property to control when the promt is shown. Valid
     * values are: Show.AWLAYS (default) - always show the prompt
     * Show.Focus_GAINED - show the prompt when the component gains focus (and
     * hide the prompt when focus is lost) Show.Focus_LOST - show the prompt
     * when the component loses focus (and hide the prompt when focus is gained)
     * 
     * @param show
     *            a valid Show enum
     */
    public void setShow(Show show) {
        this.show = show;
    }


    /**
     * Get the showPromptOnce property
     * 
     * @return the showPromptOnce property.
     */
    public boolean getShowPromptOnce() {
        return showPromptOnce;
    }


    /**
     * Show the prompt once. Once the component has gained/lost focus once, the
     * prompt will not be shown again.
     * 
     * @param showPromptOnce
     *            when true the prompt will only be shown once, otherwise it
     *            will be shown repeatedly.
     */
    public void setShowPromptOnce(boolean showPromptOnce) {
        this.showPromptOnce = showPromptOnce;
    }


    /**
     * Check whether the prompt should be visible or not. The visibility will
     * change on updates to the Document and on focus changes.
     */
    private void checkForPrompt() {

        // Show prompt only is text component is enabled
        if (!component.isEnabled()) {
            setVisible(false);
            return;
        }

        // Text has been entered, remove the prompt

        if (document.getLength() > 0) {
            setVisible(false);
            return;
        }

        // Prompt has already been shown once, remove it

        if (showPromptOnce && focusLost > 0) {
            setVisible(false);
            return;
        }

        // Check the Show property and component focus to determine if the
        // prompt should be displayed.

        if (component.hasFocus()) {
            if (show == Show.ALWAYS || show == Show.FOCUS_GAINED) {
                setVisible(true);
            }
            else {
                setVisible(false);
            }
        }
        else {
            if (show == Show.ALWAYS || show == Show.FOCUS_LOST) {
                setVisible(true);
            }
            else {
                setVisible(false);
            }
        }
    }


    @Override
    public void focusGained(FocusEvent e) {
        checkForPrompt();
    }


    @Override
    public void focusLost(FocusEvent e) {
        focusLost++;
        checkForPrompt();
    }


    @Override
    public void insertUpdate(DocumentEvent e) {
        checkForPrompt();
    }


    @Override
    public void removeUpdate(DocumentEvent e) {
        checkForPrompt();
    }


    @Override
    public void changedUpdate(DocumentEvent e) {
    }


    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        checkForPrompt();
    }


    /**
     * Creates default prompt for text component.
     * 
     * @param prompt
     *            the prompt
     * @param textComponent
     *            the text component
     * @param icon
     *            the icon
     */
    public static void addDefaultPrompt(String prompt, JTextComponent textComponent, Icon icon) {
        TextPrompt tp = new TextPrompt(prompt, textComponent);
        tp.changeAlpha(100);
        tp.setShow(Show.FOCUS_LOST);
        tp.changeStyle(Font.ITALIC);
        tp.setIcon(icon);
    }


    /**
     * Creates default prompt for text component.
     * 
     * @param prompt
     *            the prompt
     * @param textComponent
     *            the text component
     */
    public static void addDefaultPrompt(String prompt, JTextComponent textComponent) {
        addDefaultPrompt(prompt, textComponent, null);
    }
}
