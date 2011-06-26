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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.apache.log4j.Logger;

import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;
import de.unidue.inf.is.ezdl.dlcore.data.query.DefaultQuery;
import de.unidue.inf.is.ezdl.dlcore.data.query.Query;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNode;
import de.unidue.inf.is.ezdl.dlfrontend.helper.FieldRegistry;
import de.unidue.inf.is.ezdl.dlfrontend.i18n.I18nSupport;
import de.unidue.inf.is.ezdl.dlfrontend.query.NoSuchFieldCodeException;
import de.unidue.inf.is.ezdl.dlfrontend.query.QueryFactory;
import de.unidue.inf.is.ezdl.dlfrontend.query.parsers.simple.WebLikeFactory;
import de.unidue.inf.is.ezdl.gframedl.Icons;
import de.unidue.inf.is.ezdl.gframedl.components.TextComponentPopupMenu;
import de.unidue.inf.is.ezdl.gframedl.components.TextPrompt;
import de.unidue.inf.is.ezdl.gframedl.tools.search.actions.Actions;
import de.unidue.inf.is.ezdl.gframedl.tools.search.panels.AbstractQueryPanel;
import de.unidue.inf.is.ezdl.gframedl.tools.search.panels.queryviews.AdvancedFormUpdateStrategy.QueryField;
import de.unidue.inf.is.ezdl.gframedl.transfer.QueryFieldTransferHandler;



/**
 * Implements the four-field search form consisting of fields for author, title,
 * year and free-text.
 */
public final class AdvancedFormQueryView extends JPanel implements QueryView {

    private static final long serialVersionUID = 4371421212941606137L;

    private static Logger logger = Logger.getLogger(AdvancedFormQueryView.class);

    /**
     * The space around form fields and their labels.
     */
    private static final Insets insets = new Insets(2, 5, 2, 5);

    /**
     * Information about the fields.
     */
    private Map<Field, FieldInfo> fields = new EnumMap<Field, FieldInfo>(Field.class);
    /**
     * Reference to the i18n stuff.
     */
    private I18nSupport i18n = I18nSupport.getInstance();
    /**
     * The factory that takes care of the input of this form.
     */
    private QueryFactory queryFactory = new WebLikeFactory(FieldRegistry.getInstance());
    /**
     * Reference to the panel.
     */
    private AbstractQueryPanel queryPanel;
    /**
     * The class that contains the update logic of this view.
     */
    private AdvancedFormUpdateStrategy updateStrategy;
    /**
     * True if it is impossible to map the query to the form.
     */
    private boolean errorInFormMapping;
    /**
     * Field with focus; required for restoring focus state after disabling text
     * fields.
     */
    private FieldInfo focusField;


    /**
     * Constructor.
     * 
     * @param qp
     *            reference to the QueryPanel.
     */
    public AdvancedFormQueryView(AbstractQueryPanel qp) {
        super(new GridBagLayout());
        queryPanel = qp;
        updateStrategy = new AdvancedFormUpdateStrategy(fields, queryFactory);
        init();

        /*
         * TabbedPane is recessed and therefore has a slightly darker background
         * this property allows it to show through for a more consistent look
         */
        setOpaque(false);
    }


    /**
     * Initializes the view.
     */
    private void init() {
        List<FieldInfo> fields = new ArrayList<FieldInfo>();
        try {
            // The order determines the order of the text field in the form
            fields.add(getTextFieldInfo());
            fields.add(getTitleFieldInfo());
            fields.add(getAuthorFieldInfo());
            fields.add(getYearFieldInfo());
        }
        catch (NoSuchFieldCodeException e) {
            logger.error(e.getMessage(), e);
        }

        GridBagConstraints c = new GridBagConstraints();
        c.insets = insets;

        int y = 0;
        for (FieldInfo field : fields) {
            addToMap(field);

            field.labelConstraints.gridx = 0;
            field.labelConstraints.gridy = y;
            field.label.setText(i18n.getLocString(field.labelLocString) + ":");
            field.label.setHorizontalAlignment(SwingConstants.RIGHT);
            add(field.label, field.labelConstraints);

            field.fieldConstraints.gridx = 1;
            field.fieldConstraints.gridy = y;
            TextPrompt.addDefaultPrompt(i18n.getLocString(field.fieldLocString), field.textField);
            add(field.textField, field.fieldConstraints);

            y++;
        }
    }


    /**
     * Adds a FieldInfo object to the info map.
     * 
     * @param info
     *            the info object
     */
    private void addToMap(FieldInfo info) {
        fields.put(info.fieldCode, info);
    }


    private FieldInfo getAuthorFieldInfo() throws NoSuchFieldCodeException {
        return getFieldInfo(Field.AUTHOR, "ezdl.queryform.prompt.author", Icons.MEDIA_AUTHOR.get16x16());
    }


    private FieldInfo getTitleFieldInfo() throws NoSuchFieldCodeException {
        return getFieldInfo(Field.TITLE, "ezdl.queryform.prompt.title", Icons.MEDIA_TERM.get16x16());
    }


    private FieldInfo getYearFieldInfo() throws NoSuchFieldCodeException {
        return getFieldInfo(Field.YEAR, "ezdl.queryform.prompt.year", Icons.MEDIA_YEAR.get16x16());
    }


    private FieldInfo getTextFieldInfo() throws NoSuchFieldCodeException {
        return getFieldInfo(Field.TEXT, "ezdl.queryform.prompt.text", Icons.MEDIA_TEXT.get16x16());
    }


    private FieldInfo getFieldInfo(Field fieldType, String label, Icon icon) throws NoSuchFieldCodeException {
        FieldInfo field = new FieldInfo();
        field.fieldCode = fieldType;
        field.labelLocString = FieldRegistry.getInstance().getFieldName(fieldType);
        field.fieldLocString = label;
        field.labelConstraints = getLabelConstraints();
        field.fieldConstraints = getFieldConstraints();
        field.label = new JLabel();
        field.textField = newSearchTextField(fieldType);

        field.icon = icon;

        return field;
    }


    private JTextField newSearchTextField(Field field) {
        final JTextField textField = new JTextField();
        textField.setTransferHandler(new QueryFieldTransferHandler(textField, field, queryPanel, this));
        textField.setDocument(new LimitedPlainDocument(MAX_FIELD_INPUT_LENGTH));
        textField.addKeyListener(new AdvancedFormKeyListener());
        textField.addMouseListener(new AdvancedFormMouseListener());
        TextComponentPopupMenu.addPopupMenu(textField);
        return textField;
    }


    private GridBagConstraints getLabelConstraints() {
        GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.insets = insets;
        labelConstraints.weightx = 0;
        labelConstraints.anchor = GridBagConstraints.LINE_END;
        labelConstraints.fill = GridBagConstraints.NONE;
        return labelConstraints;
    }


    private GridBagConstraints getFieldConstraints() {
        GridBagConstraints fieldConstraints = new GridBagConstraints();
        fieldConstraints.insets = insets;
        fieldConstraints.weightx = 3;
        fieldConstraints.fill = GridBagConstraints.HORIZONTAL;
        return fieldConstraints;
    }


    private class AdvancedFormKeyListener extends KeyAdapter {

        @Override
        public void keyReleased(KeyEvent e) {
            handleKeyPress(e);
        }

    }


    private class AdvancedFormMouseListener extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON2) {
                updateQuery();
            }
        }
    }


    /**
     * Handler for key presses that makes the query get parsed again.
     * 
     * @param e
     *            the event
     */
    private void handleKeyPress(KeyEvent e) {
        Object comp = e.getSource();
        if (comp instanceof JTextField) {

            updateQuery();

            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                for (FieldInfo field : fields.values()) {
                    if (field.textField.isFocusOwner()) {
                        focusField = field;
                    }
                }

                Actions.SEARCH_ACTION.actionPerformed(null);
            }
        }
    }


    @Override
    public void updateQuery() {
        Query query = updateStrategy.getQueryFromFields(fields.values());
        queryPanel.setQueryInBackground(query);
    }


    /**
     * Updates the form using the query. No attempt is made to rearrange the
     * query to fit into the form. We consider the form to consist of ANDed
     * fields. Even if the query is something like "NOT(A OR B)" (equivalent to
     * (NOT A AND NOT B), which would be possible to map into the fiels), this
     * is not done.
     * <p>
     * This is because we decided not to change the query too much in order to
     * prevent the user from being totally confused.
     */
    @Override
    public void updateView() {
        boolean updateHadError = false;

        Query query = queryPanel.getQuery();
        QueryNode treeRoot = query.getTree();

        resetFields();

        try {
            List<QueryField> fieldStrings = updateStrategy.getFieldStrings(treeRoot);

            for (QueryField field : fieldStrings) {
                FieldInfo textField = fields.get(field.fieldCode);
                textField.textField.setText(field.text);
            }
        }
        catch (QueryViewUpdateException e) {
            // The attempt to map the query into the form fields has failed.
            resetFields();
            updateHadError = true;
        }

        setErroneous(updateHadError);
    }


    /**
     * Clears all text in the text fields.
     */
    private void resetFields() {
        for (FieldInfo field : fields.values()) {
            field.textField.setText("");
        }
    }


    /**
     * Sets all form fields to the passed value
     * 
     * @param enabled
     */
    private void setFieldsEnabled(boolean enabled) {
        for (FieldInfo field : fields.values()) {
            field.textField.setEnabled(enabled);
            if (field == focusField) {
                field.textField.requestFocusInWindow();
            }
        }
    }


    @Override
    public void resetView() {
        resetFields();
        setErroneous(false);
    }


    private void setErroneous(boolean error) {
        errorInFormMapping = error;
        FormMediator.instance().tellChanged(this);
    }


    @Override
    public boolean isErroneous() {
        return errorInFormMapping;
    }


    @Override
    public void setUsable(UsabilityState state) {
        switch (state) {
            case UNUSABLE: {
                setFieldsEnabled(false);
                break;
            }
            case UNUSABLE_BECAUSE_OF_QUERY: {
                // TODO
                break;
            }
            case USABLE: {
                setFieldsEnabled(true);
                break;
            }
            default: {
                throw new IllegalArgumentException();
            }
        }
    }


    @Override
    public void updateWithHistoricQuery(QueryNode andNode) {

        try {
            resetFields();
            final QueryNode q = DefaultQuery.and(andNode, queryPanel.getQuery().getTree());

            List<QueryField> fieldStrings;
            fieldStrings = updateStrategy.getFieldStrings(q);

            for (QueryField field : fieldStrings) {
                FieldInfo textField = fields.get(field.fieldCode);
                textField.textField.setText(field.text);
            }

            updateQuery();
            // queryPanel.selectFreeText();
        }
        catch (QueryViewUpdateException e) {
            logger.error(e.getMessage(), e);
        }

    }

}
