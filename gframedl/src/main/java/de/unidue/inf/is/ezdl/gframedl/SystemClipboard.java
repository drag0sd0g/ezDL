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

package de.unidue.inf.is.ezdl.gframedl;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;



public final class SystemClipboard implements ClipboardOwner {

    private static SystemClipboard instance = new SystemClipboard();


    private SystemClipboard() {
    }


    /**
     * Puts text on clipboard.
     * 
     * @param sText
     *            the s text
     */
    public static void copyToClipboard(String sText) {
        Clipboard objClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection objStringSelection = new StringSelection(sText);
        objClipboard.setContents(objStringSelection, instance);
    }


    /**
     * Gets text from clipboard
     * 
     * @return Text from clipboard
     */
    public static String getClipboardContent() {
        Clipboard objClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        String content = "";
        try {
            Transferable transferable = objClipboard.getContents(null);
            boolean hasTransferableText = transferable != null
                            && transferable.isDataFlavorSupported(DataFlavor.stringFlavor);
            if (hasTransferableText) {
                content = (String) transferable.getTransferData(DataFlavor.stringFlavor);
            }
        }
        catch (Exception e) {
        }
        return content;
    }


    /**
     * Gets if clipboard contains text
     * 
     * @return <code>true</code> if clipboard contains text
     */
    public static boolean clipboardContainsText() {
        return !getClipboardContent().equals("");
    }


    @Override
    public void lostOwnership(Clipboard arg0, Transferable arg1) {
    }

}
