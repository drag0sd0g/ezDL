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

import javax.swing.Icon;

import de.unidue.inf.is.ezdl.dlcore.data.dldata.DLObject;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.Person;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.Term;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.Year;
import de.unidue.inf.is.ezdl.dlcore.data.extractor.AuthorExtractor;
import de.unidue.inf.is.ezdl.dlcore.data.extractor.ExtractorService;
import de.unidue.inf.is.ezdl.dlcore.data.extractor.LibraryExtractor;
import de.unidue.inf.is.ezdl.dlcore.data.extractor.TermExtractor;
import de.unidue.inf.is.ezdl.dlcore.data.extractor.YearExtractor;
import de.unidue.inf.is.ezdl.dlfrontend.i18n.I18nSupport;
import de.unidue.inf.is.ezdl.gframedl.Icons;
import de.unidue.inf.is.ezdl.gframedl.transfer.DLObjectTransferable;



/**
 * ExtractionType defines the types of extractions known in ezDL.
 * <p>
 * An "extraction" in this context is the act of getting frequency statistics
 * out of a collection of data - e.g. determining the term distribution from a
 * result set. This is done by implementors of {@link ExtractorService}. The
 * connections between these services, the eligible data classes and fitting
 * icons and label names is what is done in this class.
 * 
 * @author mjordan
 */
public enum ExtractionType {

    TAGS("tags", Icons.MEDIA_TAG.get16x16(), Term.class, TermExtractor.class), //
    AUTHORS("author", Icons.MEDIA_AUTHOR.get16x16(), Person.class, AuthorExtractor.class), //
    YEARS("years", Icons.MEDIA_YEAR.get16x16(), Year.class, YearExtractor.class), //
    LIBRARIES("libraries", Icons.MEDIA_DIGITALLIBRARY.get16x16(), Term.class, LibraryExtractor.class);

    private static final String LOC_PREFIX = "ezdl.controls.resultlistpanel.label.extract.";

    private Class<? extends DLObject> transferable;
    private Class<? extends ExtractorService> extractor;
    private String name;
    private Icon icon;


    /**
     * Constructor.
     * 
     * @param name
     *            the name suffix of the i18n key for this extraction
     * @param icon
     *            the icon
     * @param transferable
     *            the class used in the {@link DLObjectTransferable}
     * @param extractor
     *            the {@link ExtractorService} to use for this type
     */
    ExtractionType(String name, Icon icon, Class<? extends DLObject> transferable,
                    Class<? extends ExtractorService> extractor) {
        this.name = I18nSupport.getInstance().getLocString(LOC_PREFIX + name);
        this.icon = icon;
        this.transferable = transferable;
        this.extractor = extractor;
    }


    /**
     * Returns a transfer object for the given content.
     * 
     * @param s
     *            the content to encapsulate
     * @return the transfer object
     */
    public DLObjectTransferable getTrans(String s) {
        DLObject newInstance;
        try {
            newInstance = transferable.getConstructor(String.class).newInstance(s);
        }
        catch (Exception e) {
            return null;
        }
        return new DLObjectTransferable(newInstance);
    }


    /**
     * Returns the i18n key name for this type.
     * 
     * @return the i18n key name for this type
     */
    public String getName() {
        return name;
    }


    /**
     * Returns the icon.
     * 
     * @return the icon
     */
    public Icon getIcon() {
        return icon;
    }


    /**
     * Returns an instance of the extractor for the given type.
     * 
     * @return an extractor instance
     */
    public ExtractorService getExtractor() {
        try {
            return extractor.newInstance();
        }
        catch (InstantiationException e) {
            return null;
        }
        catch (IllegalAccessException e) {
            return null;
        }
    }
}
