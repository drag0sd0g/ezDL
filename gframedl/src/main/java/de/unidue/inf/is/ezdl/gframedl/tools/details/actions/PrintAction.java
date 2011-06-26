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

package de.unidue.inf.is.ezdl.gframedl.tools.details.actions;

import java.awt.event.ActionEvent;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

import javax.swing.AbstractAction;
import javax.swing.Action;

import org.apache.log4j.Logger;

import de.unidue.inf.is.ezdl.dlcore.data.dldata.DLObject;
import de.unidue.inf.is.ezdl.dlcore.message.content.UserLogNotify;
import de.unidue.inf.is.ezdl.dlfrontend.comm.BackendEvent;
import de.unidue.inf.is.ezdl.dlfrontend.dispatcher.Dispatcher;
import de.unidue.inf.is.ezdl.dlfrontend.i18n.I18nSupport;
import de.unidue.inf.is.ezdl.gframedl.Icons;
import de.unidue.inf.is.ezdl.gframedl.ToolController;
import de.unidue.inf.is.ezdl.gframedl.tools.details.DetailView;



/**
 * Prints an instance of Printable. A standard print-dialog is displayed.
 */
public class PrintAction extends AbstractAction {

    private static final long serialVersionUID = 5979244491793449467L;

    private static Logger logger = Logger.getLogger(PrintAction.class);

    private Printable printable;
    private DetailView detailView;


    /**
     * Constructor.
     */
    public PrintAction(Printable printable, DetailView detailView) {
        super(I18nSupport.getInstance().getLocString("ezdl.actions.print"));
        this.printable = printable;
        this.detailView = detailView;
        putValue(Action.SMALL_ICON, Icons.PRINT.get16x16());
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (printable != null) {
            PrinterJob printJob = PrinterJob.getPrinterJob();
            printJob.setPrintable(printable);
            if (printJob.printDialog()) {
                try {
                    printJob.print();

                    UserLogNotify userLogNotify = new UserLogNotify(ToolController.getInstance().getSessionId(),
                                    "printdetails");
                    DLObject object = detailView.getObject();
                    if (object != null) {
                        userLogNotify.addParameter("oid", object.getOid());
                    }
                    Dispatcher.postEvent(new BackendEvent(this, userLogNotify));
                }
                catch (PrinterException pex) {
                    logger.error(pex.getMessage(), pex);
                }
            }
        }
    }
}
