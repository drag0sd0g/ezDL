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

package de.unidue.inf.is.ezdl.gframedl.tools.extraction;

import java.util.Arrays;
import java.util.EventObject;
import java.util.List;

import de.unidue.inf.is.ezdl.dlcore.data.extractor.ExtractionResult;
import de.unidue.inf.is.ezdl.dlcore.data.extractor.ExtractorService;
import de.unidue.inf.is.ezdl.dlcore.message.content.UserLogNotify;
import de.unidue.inf.is.ezdl.dlfrontend.comm.BackendEvent;
import de.unidue.inf.is.ezdl.dlfrontend.dispatcher.Dispatcher;
import de.unidue.inf.is.ezdl.gframedl.Icons;
import de.unidue.inf.is.ezdl.gframedl.IconsTuple;
import de.unidue.inf.is.ezdl.gframedl.ToolController;
import de.unidue.inf.is.ezdl.gframedl.events.ExtractionEvent;
import de.unidue.inf.is.ezdl.gframedl.tools.AbstractTool;
import de.unidue.inf.is.ezdl.gframedl.tools.ToolView;
import de.unidue.inf.is.ezdl.gframedl.tools.extraction.views.AbstractExtractionView;
import de.unidue.inf.is.ezdl.gframedl.tools.search.actions.ExtractionAction;



/**
 * The ExtractionTool can display an {@link ExtractionResult} contained in an
 * {@link ExtractionEvent} and uses different views to do so. Those views have
 * to subclass {@link AbstractExtractionView} and are maintained by the
 * {@link ExtractionToolView}
 * 
 * @author tacke
 */
public class ExtractionTool extends AbstractTool {

    public static final String I18N_PREFIX = "ezdl.tools.extraction.";

    private ExtractionToolView view;
    private ExtractionResult resultList;

    private ExtractionType currentType;


    public ExtractionTool() {
        initialize();
    }


    private void initialize() {
        Dispatcher.registerInterest(this, ExtractionEvent.class);
    }


    @Override
    public List<ToolView> createViews() {
        view = new ExtractionToolView(this);
        return Arrays.<ToolView> asList(view);
    }


    @Override
    protected IconsTuple getIcon() {
        return Icons.EXTRACTION_TOOL.toIconsTuple();
    }


    @Override
    protected String getI18nPrefix() {
        return I18N_PREFIX;
    }


    /**
     * Type of the current {@link ExtractionAction}
     * 
     * @return type
     */
    public ExtractionType getCurrentType() {
        return currentType;
    }


    @Override
    public boolean handleEzEvent(EventObject ev) {
        if (ev instanceof ExtractionEvent) {
            ExtractionEvent event = (ExtractionEvent) ev;
            List<Object> documentList = event.getContent();

            ExtractionType type = event.getType();
            ExtractorService service = type.getExtractor();

            UserLogNotify userLogNotify = new UserLogNotify(ToolController.getInstance().getSessionId(), "extract");
            userLogNotify.addParameter("type", type.toString().toLowerCase());
            Dispatcher.postEvent(new BackendEvent(this, userLogNotify));

            currentType = type;

            if (service != null) {
                resultList = service.extract(documentList);

                view.updateViews(resultList);

                open();
                view.toFront();

                return true;
            }
        }
        return false;
    }

}
