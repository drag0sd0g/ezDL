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

package de.unidue.inf.is.ezdl.gframedl.tools.search.renderers;

import java.awt.HeadlessException;

import javax.swing.JList;

import junit.framework.Assert;

import org.junit.Test;

import de.unidue.inf.is.ezdl.dlcore.AbstractTestBase;
import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;
import de.unidue.inf.is.ezdl.dlfrontend.helper.FieldRegistry;
import de.unidue.inf.is.ezdl.gframedl.tools.search.SearchTool;
import de.unidue.inf.is.ezdl.gframedl.tools.search.SearchTool.SearchContext;



/**
 * Tests if all fields that are defined in the SearchTool are also implemented
 * in {@link FieldRegistry}.
 * 
 * @author mjordan
 */
public class SortingListCellRendererTest extends AbstractTestBase {

    @Test
    public void test() {
        try {
            SortingListCellRenderer renderer = new SortingListCellRenderer();
            SearchTool st = new SearchTool();
            JList list = new JList();
            st.startNewSearch();
            SearchContext c = st.getCurrentSearch();
            for (Field field : c.resultConfig.getFields()) {
                try {
                    renderer.getListCellRendererComponent(list, field, 0, false, false);
                }
                catch (RuntimeException e) {
                    Assert.fail("failed because of " + e.getMessage());
                }
            }
        }
        catch (HeadlessException e) {
            getLogger().info("Test skipped because we're running headless.");
        }
    }
}
