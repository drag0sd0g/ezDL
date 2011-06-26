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

import java.io.FileNotFoundException;
import java.io.IOError;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import de.unidue.inf.is.ezdl.dlcore.data.dldata.Document;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.TextDocument;



/**
 * Icons for many places in ezDL, be it button icons or for tool views, you name
 * it.
 */
public enum Icons {

    OK_ACTION("actions/ok_16.png", null), //
    CANCEL_ACTION("actions/cancel_16.png", null), //
    SEARCH("actions/search_16.png", "actions/search_22.png"), //
    CANCEL("actions/cancel_16.png", "actions/cancel_22.png"), //
    CLEAR_ACTION("actions/clear_16.png", "actions/clear_16.png"), //
    COMPACT_VIEW_ACTION("actions/compact_view_16.png", null), //
    SORT_BY_RELEVANCE_ACTION("actions/sort_by_relevance_16.png", null), //
    SORT_BY_TITLE_ACTION("actions/sort_by_title_16.png", null), //
    SORT_BY_YEAR_ACTION("actions/sort_by_date_16.png", null), //
    UP_ACTION("actions/up_16.png", null), //
    DOWN_ACTION("actions/down_16.png", null), //
    DETAILED_VIEW_ACTION("actions/detailed_view_16.png", null), //
    EXTRACT_ACTION("actions/extract_16.png", "actions/extract_22.png"), //
    REFRESH_ACTION("actions/reload_16.png", "actions/reload_22.png"), //
    CLOSE_ALL("actions/closeAll_16.png", null), //
    EXPORT_ACTION("actions/export_16.png", "actions/export_22.png"), //
    PRINT("actions/print_16.png", "actions/print_22.png"), //
    SAVE("actions/save_16.png", "actions/save_22.png"), //
    COPY("actions/copy_16.png", "actions/copy_22.png"), //
    PASTE("actions/paste_16.png", "actions/paste_22.png"), //
    DELETE("actions/delete_16.png", "actions/delete_22.png"), //
    CUT("actions/cut_16.png", "actions/cut_22.png"), //
    COPY_TO_CLIPBOARD("actions/copyClipBrd_16.png", "actions/copyClipBrd_22.png"), //
    COPY_TO_LIBRARY("actions/add_library_16.png", "actions/add_library_22.png"), //
    TURN_OVER_ACTION_RIGHT("actions/arrow_right_16.png", "actions/arrow_right_22.png"), //
    TURN_OVER_ACTION_LEFT("actions/arrow_left_16.png", "actions/arrow_left_22.png"), //
    ADD_NEW("actions/new_16.png", "actions/new_22.png"), //
    ADD_GROUP("actions/new_folder_16.png", "actions/new_folder_22.png"), //
    EDIT_GROUP("actions/edit_group_16.png", "actions/edit_group_22.png"), //
    EDIT("actions/edit_16.png", "actions/edit_22.png"), //
    DELETE_GROUP("actions/delete_group_16.png", "actions/delete_group_22.png"), //
    RESYNCH("actions/synchronize_16.png", "actions/synchronize_22.png"), //
    ZOOM_BEST_FIT("actions/zoom_best_fit_16.png", null), //
    ZOOM_IN("actions/zoom_in_16.png", null), //
    ZOOM_OUT("actions/zoom_out_16.png", null), //
    DRAG("actions/drag_16.png", null), //
    CURSOR("actions/cursor_16.png", null), //
    MEDIA_DOCUMENT("mime/document_16.png", "mime/document_22.png"), //
    MEDIA_MUSIC("mime/album_16.png", null), //
    MEDIA_TEXT("mime/document_16.png", null), //
    MEDIA_IMAGE("mime/picture_16.png", null), //
    MEDIA_VIDEO("mime/video_16.png", null), //
    MEDIA_HTML("mime/document_16.png", null), //
    MEDIA_AUTHOR("mime/author_16.png", "mime/author_22.png"), //
    MEDIA_TAG("mime/tag_16.png", "mime/tag_22.png"), //
    MEDIA_YEAR("mime/year_16.png", "mime/year_22.png"), //
    MEDIA_QUERY("mime/query_16.png", "mime/query_22.png"), //
    MEDIA_DIGITALLIBRARY("mime/digitallibrary_16.png", "mime/digitallibrary_22.png"), //
    MEDIA_TERM("mime/term_16.png", "mime/term_22.png"), //
    MEDIA_GROUP("mime/group_16.png", "mime/group_22.png"), //
    MEDIA_GROUP_ONLINE("mime/group_online_16.png", "mime/group_online_22.png"), //
    MEDIA_URL("mime/url_16.png", "mime/url_22.png"), //
    MEDIA_OFFLINEURL("mime/url_offline_16.png", "mime/url_offline_22.png"), //
    MEDIA_DOCUMENTONLINE("mime/document_online_16.png", "mime/document_online_22.png"), //
    DETAIL_TOOL("tools/detail_16.png", "tools/detail_22.png"), //
    EXTRACTION_TOOL("tools/extraction_16.png", "tools/extraction_22.png"), //
    CLIPBOARD_TOOL("tools/clipboard_16.png", "tools/clipboard_22.png"), //
    SEARCH_TOOL("tools/search_16.png", "tools/search_22.png"), //
    RELATED_TOOL("tools/related_16.png", "tools/related_22.png"), //
    QUERY_HISTORY_TOOL("tools/history_16.png", "tools/history_22.png"), //
    LIBRARY_TOOL("tools/library_16.png", "tools/library_22.png"), //
    RELATION_TOOL("tools/relations_16.png", null), //

    COLLAPSE("grouping/collapse_16.png", null), COLLAPSE_ALL("grouping/collapse_all_16.png", null), EXPAND(
                    "grouping/expand_16.png", null), EXPAND_ALL("grouping/expand_all_16.png", null), SELECT_GROUP(
                    "grouping/group_select_all_16.png", null), DESELECT_GROUP("grouping/group_deselect_all_16.png",
                    null), ONE("grouping/one_16.png", null), NULL("grouping/null_16.png", null),

    DEFAULT("default_16.png", "default_22.png");

    private static final String BASE_16 = "/icons/16x16/";
    private static final String BASE_22 = "/icons/22x22/";

    private String fileName16x16;
    private String fileName22x22;

    private Icon icon16x16;
    private Icon icon22x22;


    private Icons(String fileName16x16, String fileName22x22) {
        this.fileName16x16 = fileName16x16;
        this.fileName22x22 = fileName22x22;
    }


    /**
     * Returns the icon for the given type in 16x16 pixels.
     * 
     * @return the 16x16 icon
     */
    public Icon get16x16() {
        if (icon16x16 == null) {
            if (fileName16x16 == null) {
                throw new IOError(new FileNotFoundException());
            }
            synchronized (fileName16x16) {
                icon16x16 = getIcon(getClass().getResource(BASE_16 + fileName16x16));
            }
        }
        return icon16x16;
    }


    /**
     * Returns the icon for the given type in 22x22 pixels.
     * 
     * @return the 22x22 icon
     */
    public Icon get22x22() {
        if (icon22x22 == null) {
            if (fileName22x22 == null) {
                throw new IOError(new FileNotFoundException());
            }
            synchronized (fileName22x22) {
                icon22x22 = getIcon(getClass().getResource(BASE_22 + fileName22x22));
            }
        }
        return icon22x22;
    }


    /**
     * Returns the icons as {@link IconsTuple}
     * 
     * @return
     */
    public IconsTuple toIconsTuple() {
        return new IconsTuple(get22x22(), get16x16());
    }


    /**
     * Returns the icon if the URL is not null, otherwise returns a default icon
     * from {@link #DEFAULT}.
     * 
     * @param url
     *            the URL of the icon
     * @return the correct icon object or a default object if the given URL is
     *         null
     */
    private ImageIcon getIcon(URL url) {
        ImageIcon icon = null;
        if (url != null) {
            icon = new ImageIcon(url);
        }
        else {
            icon = new ImageIcon(getClass().getResource(BASE_16 + DEFAULT.fileName16x16));
        }
        return icon;
    }


    /**
     * Returns icon for a document.
     * 
     * @param document
     *            the document
     * @return the icon
     */
    public static Icon getIconForDocument(Document document) {
        if (document.getClass() == TextDocument.class) {
            return Icons.MEDIA_TEXT.get16x16();
        }
        else {
            throw new IllegalArgumentException("unknown document type");
        }
    }

}