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
package de.unidue.inf.is.ezdl.examples.tool;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;

import de.unidue.inf.is.ezdl.dlbackend.agent.RequestIDFactory;
import de.unidue.inf.is.ezdl.dlfrontend.comm.BackendEvent;
import de.unidue.inf.is.ezdl.dlfrontend.dispatcher.Dispatcher;
import de.unidue.inf.is.ezdl.examples.agent.DummyAsk;
import de.unidue.inf.is.ezdl.gframedl.tools.AbstractToolView;
import de.unidue.inf.is.ezdl.gframedl.tools.Tool;



/* <class> */
public class SendingToolView extends AbstractToolView {

    private static final long serialVersionUID = 1L;


    public SendingToolView(Tool parentTool) {
        super(parentTool);
        add(new JButton(new DummyAction()));
    }


    private class DummyAction extends AbstractAction {

        private static final long serialVersionUID = 1L;


        @Override
        public void actionPerformed(ActionEvent e) {
            String id = RequestIDFactory.getInstance().getNextRequestID();
            DummyAsk content = new DummyAsk();
            BackendEvent be = new BackendEvent(this);
            be.setContent(content);
            be.setRequestId(id);
            Dispatcher.postEvent(be);
        }

    }

}
/* </class> */
