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

package de.unidue.inf.is.ezdl.gframedl.tools.details.views;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Font;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.apache.log4j.Logger;

import de.unidue.inf.is.ezdl.dlcore.data.dldata.DLObject;
import de.unidue.inf.is.ezdl.dlcore.utils.HtmlUtils;
import de.unidue.inf.is.ezdl.dlcore.utils.SystemUtils;
import de.unidue.inf.is.ezdl.dlcore.utils.SystemUtils.OperatingSystem;
import de.unidue.inf.is.ezdl.dlfrontend.i18n.I18nSupport;
import de.unidue.inf.is.ezdl.gframedl.Icons;
import de.unidue.inf.is.ezdl.gframedl.ToolController;
import de.unidue.inf.is.ezdl.gframedl.helper.EzDLProtocolHandler;
import de.unidue.inf.is.ezdl.gframedl.helper.InternalProtocolFactory;
import de.unidue.inf.is.ezdl.gframedl.tools.Tool;
import de.unidue.inf.is.ezdl.gframedl.tools.details.CopyDLObjectToClipboardAction;
import de.unidue.inf.is.ezdl.gframedl.tools.details.DetailTool;
import de.unidue.inf.is.ezdl.gframedl.tools.details.DetailView;
import de.unidue.inf.is.ezdl.gframedl.tools.details.DetailViewContainer;
import de.unidue.inf.is.ezdl.gframedl.tools.details.actions.CopyAction;
import de.unidue.inf.is.ezdl.gframedl.tools.details.actions.CopyToLibraryAction;



/**
 * DefaultDetailView is the DetailView, which is used if there is no specific
 * DetailView for a class defined. It calls the object.toString() method. The
 * default content type is html.
 */
public class DefaultDetailView extends JComponent implements DetailView {

    private static final long serialVersionUID = -9144823984640187939L;

    private final String EZDL_PROTOCOL = "ezdl";

    private static Logger logger = Logger.getLogger(DefaultDetailView.class);

    protected JEditorPane editorPane = new JEditorPane();
    protected DLObject object;
    protected Tool parentTool;
    protected JScrollPane scrollPane = new JScrollPane();

    protected InternalProtocolFactory protocolFactory = new InternalProtocolFactory();
    protected EzDLProtocolHandler protocolHandler = (EzDLProtocolHandler) protocolFactory
                    .createURLStreamHandler(EZDL_PROTOCOL);


    /**
     * Constructor.
     */
    public DefaultDetailView() {
        super();
        setLayout(new BorderLayout());

        scrollPane.setViewportView(editorPane);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        add(scrollPane, BorderLayout.CENTER);

        /**
         * FIXME The following code row has no effect (scrolling with keyboard
         * don't work properly). Reason : The editor pane is not editable, but
         * internally there is keyboard-cursor (the thing which is blinking at
         * the text position you are editing). If you use arrow-key's - e.g.
         * down for scrolling, nothing happens until the keyboard cursor (which
         * you don't see) arrives at the last visible row, from this point the
         * scrolling is normal.
         */
        scrollPane.getVerticalScrollBar().setUnitIncrement(40);
        scrollPane.getVerticalScrollBar().setBlockIncrement(100);

        if (SystemUtils.OS == OperatingSystem.MAC_OS) {
            editorPane.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
            editorPane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
            editorPane.setFont(new Font("sansserif", Font.PLAIN, 12));
        }

        editorPane.setContentType("text/html");
        editorPane.setEditable(false);
        HyperlinkListener hll = new HyperlinkListener() {

            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (e.getEventType() == HyperlinkEvent.EventType.ENTERED) {
                    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                }
                else if (e.getEventType() == HyperlinkEvent.EventType.EXITED) {
                    setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                }
                else if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    URL url = e.getURL();
                    String protocol = url.getProtocol();
                    if (EZDL_PROTOCOL.equals(protocol)) {
                        protocolHandler.handleLink(url);
                    }
                    else {
                        try {
                            Desktop.getDesktop().browse(e.getURL().toURI());
                        }
                        catch (IOException ex) {
                            logger.error(ex.getMessage(), ex);
                        }
                        catch (URISyntaxException ex) {
                            logger.error(ex.getMessage(), ex);
                        }
                    }
                }
            }
        };
        editorPane.addHyperlinkListener(hll);
    }


    @Override
    public void setCursor(Cursor cursor) {
        Component c = getParent();
        if (c instanceof DetailViewContainer) {
            ((DetailViewContainer) c).setCursor(cursor);
        }
    }


    protected void setBusy(boolean b) {
        Component c = getParent();
        if (c instanceof DetailViewContainer) {
            ((DetailViewContainer) c).setBusy(b);
        }
    }


    protected boolean isBusy() {
        Component c = getParent();
        if (c instanceof DetailViewContainer) {
            return ((DetailViewContainer) c).isBusy();
        }
        else {
            return false;
        }
    }


    @Override
    public void setObject(DLObject o, List<String> highlightStrings) {
        object = o;
        if (o != null) {
            editorPane.setText(text());
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    editorPane.setCaretPosition(0);
                }
            });
        }
    }


    @Override
    public DLObject getObject() {
        return object;
    }


    @Override
    public String getTabName() {
        if (object != null) {
            return I18nSupport.getInstance().getLocString("ezdl.tools.detail.name");
        }
        else {
            return null;
        }
    }


    @Override
    public Icon getIcon() {
        return Icons.DETAIL_TOOL.get16x16();
    }


    private String text() {
        StringBuffer result = new StringBuffer(HtmlUtils.getHTMLHeader());
        result.append("\n<b>").append(object.getClass().getName()).append("</b><br>");
        result.append("\n<b>").append(object.toString()).append("</b><br>");
        result.append(HtmlUtils.getHTMLFooter());
        return result.toString();
    }


    /**
     * The Default-Actions are
     * <ul>
     * <li>Copy to EzDL-clipboard (file tray).</li>
     * <li>Copy selected text to system-clipboard.</li>
     * </ul>
     */
    @Override
    public List<Action> getPossibleActions() {
        List<Action> result = new ArrayList<Action>();

        ToolController tc = ToolController.getInstance();
        DetailTool detailTool = tc.getTool(DetailTool.class);
        result.add(new CopyDLObjectToClipboardAction(detailTool, this));
        result.add(new CopyToLibraryAction(detailTool, this));
        result.add(new CopyAction(this.editorPane, this));

        return result;
    }
}
