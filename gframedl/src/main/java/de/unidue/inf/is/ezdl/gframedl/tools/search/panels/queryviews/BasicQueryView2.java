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

package de.unidue.inf.is.ezdl.gframedl.tools.search.panels.queryviews;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.EnumSet;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

import org.antlr.runtime.RecognitionException;

import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;
import de.unidue.inf.is.ezdl.dlcore.data.query.Query;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNode;
import de.unidue.inf.is.ezdl.dlfrontend.helper.FieldRegistry;
import de.unidue.inf.is.ezdl.dlfrontend.i18n.I18nSupport;
import de.unidue.inf.is.ezdl.dlfrontend.query.NoSuchFieldCodeException;
import de.unidue.inf.is.ezdl.dlfrontend.query.QueryFactory;
import de.unidue.inf.is.ezdl.dlfrontend.query.parsers.simple.WebLikeFactory;
import de.unidue.inf.is.ezdl.gframedl.components.TextComponentPopupMenu;
import de.unidue.inf.is.ezdl.gframedl.components.TextPrompt;
import de.unidue.inf.is.ezdl.gframedl.tools.search.actions.Actions;
import de.unidue.inf.is.ezdl.gframedl.tools.search.panels.AbstractQueryPanel;
import de.unidue.inf.is.ezdl.gframedl.transfer.QueryFieldTransferHandler;



/**
 * A single-line query form.
 */
public final class BasicQueryView2 extends JPanel implements QueryView {

    /**
     * The default field determines which field code is assumed for terms that
     * don't have an explicit field prefix. I.e. "term" as opposed to
     * "author:term".
     */
    private static final Field DEFAULT = Field.TEXT;
    /**
     * The ID.
     */
    private static final long serialVersionUID = 4371421212941606137L;
    /**
     * The text area of this form.
     */
    private JTextArea queryArea;
    /**
     * Reference to the i18n stuff.
     */
    private I18nSupport i18n = I18nSupport.getInstance();
    /**
     * The factory that takes care of the input of this form.
     */
    private QueryFactory queryFactory = new WebLikeFactory(FieldRegistry.getInstance(), DEFAULT);
    /**
     * Reference to the panel.
     */
    private AbstractQueryPanel queryPanel;


    /**
     * Constructor.
     * 
     * @param qp
     *            reference to the QueryPanel.
     */
    public BasicQueryView2(AbstractQueryPanel qp) {
        super(new GridBagLayout());
        queryPanel = qp;
        init();
        setOpaque(false);
    }


    /**
     * Initializes the query area.
     * 
     * @return the query text area
     */
    private JTextArea getQueryArea() {
        if (queryArea == null) {
            queryArea = new JTextArea();
            queryArea.setDocument(new LimitedPlainDocument(MAX_FIELD_INPUT_LENGTH));
            TextPrompt.addDefaultPrompt(i18n.getLocString("field.query"), queryArea);
            queryArea.setLineWrap(true);
            queryArea.setWrapStyleWord(true);
            queryArea.getActionMap().put("search", Actions.SEARCH_ACTION);
            queryArea.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.CTRL_DOWN_MASK), "search");
            queryArea.getInputMap().put(KeyStroke.getKeyStroke((char) KeyEvent.VK_ENTER), null);
            queryArea.addKeyListener(new KeyListener() {

                @Override
                public void keyPressed(KeyEvent e) {
                }


                @Override
                public void keyReleased(KeyEvent e) {
                    handleKeyPress(e);
                }


                @Override
                public void keyTyped(KeyEvent e) {
                }

            });
            queryArea.setTransferHandler(new QueryFieldTransferHandler(queryArea, Field.TEXT, queryPanel, this));
            TextComponentPopupMenu.addPopupMenu(queryArea);
        }
        return queryArea;
    }


    /**
     * Initializes the view.
     */
    private void init() {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        JTextArea qA = getQueryArea();
        JScrollPane jsp = new JScrollPane(qA);
        add(jsp, c);
    }


    /**
     * Handles key presses. Each key press makes the whole query to be generated
     * again.
     * 
     * @param e
     *            the key event
     */
    private void handleKeyPress(KeyEvent e) {
        Object comp = e.getSource();
        if (comp == queryArea) {
            updateQuery();
        }
    }


    @Override
    public void updateQuery() {
        boolean illformedQuery = false;
        String queryString = queryArea.getText().trim();
        Query query;
        try {
            query = queryFactory.parse(queryString);
            queryPanel.setQueryInBackground(query);
        }
        catch (RecognitionException e1) {
            illformedQuery = true;
        }
        catch (NoSuchFieldCodeException e1) {
            illformedQuery = true;
        }
        finally {
            queryArea.setForeground(illformedQuery ? Color.RED : Color.BLACK);
        }
    }


    @Override
    public void updateView() {
        Query query = queryPanel.getQuery();
        String queryString = queryFactory.getTextForQueryNode(query.getTree(), DEFAULT);
        queryArea.setText(queryString);
    }


    @Override
    public void resetView() {
        queryArea.setText("");
    }


    @Override
    public boolean isErroneous() {
        // This form is supposed to not contain severe errors that would make
        // the form itself unusable
        return false;
    }


    @Override
    public void setUsable(UsabilityState state) {
        if (EnumSet.of(UsabilityState.USABLE).contains(state)) {
            queryArea.setEnabled(true);
        }
        else if (EnumSet.of(UsabilityState.UNUSABLE, UsabilityState.UNUSABLE_BECAUSE_OF_QUERY).contains(state)) {
            queryArea.setEnabled(false);
        }
        else {
            throw new IllegalArgumentException();
        }
    }


    @Override
    public void updateWithHistoricQuery(QueryNode andNode) {
        String queryString = queryFactory.getTextForQueryNode(andNode, DEFAULT);
        queryArea.setText(queryString);

    }

}
