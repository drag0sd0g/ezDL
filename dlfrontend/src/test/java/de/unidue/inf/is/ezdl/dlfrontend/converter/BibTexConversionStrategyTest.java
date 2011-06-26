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

package de.unidue.inf.is.ezdl.dlfrontend.converter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import de.unidue.inf.is.ezdl.dlcore.AbstractTestBase;
import de.unidue.inf.is.ezdl.dlcore.DocumentFactory;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.PersonList;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.TextDocument;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.URLList;
import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;
import de.unidue.inf.is.ezdl.dlcore.utils.IOUtils;



/**
 * @author mj
 */
public class BibTexConversionStrategyTest extends AbstractTestBase {

    private static final String LATEX_NAME = "latex";
    private static final String BIBTEX_NAME = "bibtex";
    private static final String LATEX_BATCH = " -interaction batchmode ";

    protected static final long SLEEP_TIME_MS = 2000;

    private BibTexConversionStrategy strategy;

    private String BIB_OKAY = "@article{citationkey,"
                    + "author = {Dalrymple, Prudence W.},"
                    + "title = {Retrieval by Reformulation in Two Library Catalogs: Toward a Cognitive Model of Searching Behavior},"
                    + "journal = {JASIS}," + "year = {1990}" + "}";

    private String BIB_FAULTY = "@article{citationkey,"
                    + "author = {Dalrymple, Prudence W.}"
                    + "title = {Retrieval by Reformulation in Two Library Catalogs: Toward a Cognitive Model of Searching Behavior},"
                    + "journal = {JASIS}," + "year = {1990}" + "}";


    private class TestableBibTexConversionStrategy extends BibTexConversionStrategy {

        @Override
        protected String getUUID() {
            return "citationkey";
        }
    }


    private static boolean isLatexAvailable() {
        try {
            ProcessBuilder pb = new ProcessBuilder("latex -version");
            Process p = pb.start();
            p.waitFor();
        }
        catch (IOException e) {
            return false;
        }
        catch (InterruptedException e) {
            return false;
        }
        return true;
    }


    @Before
    public void setup() {
        strategy = new TestableBibTexConversionStrategy();
        setSkip(!isLatexAvailable());
    }


    @Test
    public void selfTest1() {
        if (!skip()) {
            checkIfBibTexCompiles(BIB_FAULTY, 2);
        }
    }


    @Test
    public void selfTest2() {
        if (!skip()) {
            checkIfBibTexCompiles(BIB_OKAY, 0);
        }
    }


    @Test
    public void testEmptyDocument() {
        if (!skip()) {
            TextDocument doc = new TextDocument();
            String bibTexStr = check(doc);
            checkAuthors(bibTexStr, doc.getAuthorList());
        }
    }


    @Test
    public void testOneAuthor() {
        if (!skip()) {
            TextDocument doc = DocumentFactory.createDocument("De nova stella", 1573, "Tycho Brahe");
            check(doc);
            String bibTexStr = check(doc);
            checkAuthors(bibTexStr, doc.getAuthorList());
        }
    }


    @Test
    public void testTwoAuthors() {
        if (!skip()) {
            TextDocument doc = DocumentFactory.createDocument("title", 2000, "John Doe", "Jane Doe");
            String bibTexStr = check(doc);
            checkAuthors(bibTexStr, doc.getAuthorList());
        }
    }


    @Test
    public void testMuchstuff() {
        if (!skip()) {
            TextDocument doc = DocumentFactory.createDocument("title", 2001, "John Doe", "Jane Doe");
            doc.setAbstract("This is an abstract that is totally boring.");
            URLList ul = new URLList();
            try {
                ul.add(new URL("http://www.acm.org"));
            }
            catch (MalformedURLException e) {
                getLogger().error(e);
            }
            doc.setDetailURLs(ul);
            doc.setFieldValue(Field.JOURNAL, "Journal of Boring Stuff");
            doc.setFieldValue(Field.DOI, "bogus.doi.12345");
            doc.setFieldValue(Field.ISBN, "13423234");
            doc.setFieldValue(Field.PAGES, "2-4");
            String bibTexStr = check(doc);
            checkAuthors(bibTexStr, doc.getAuthorList());
        }

    }


    private String check(TextDocument document) {
        ExportResult bib = strategy.print(document);
        Assert.assertFalse("BibTex is not binary", bib.isBinary());
        String bibTexStr = bib.asString();
        getLogger().debug("BibTeX: " + bibTexStr);
        checkIfBibTexCompiles(bibTexStr, 0);
        return bibTexStr;
    }


    /**
     * Takes a supposed bibtex document, writes it to disk and launches latex
     * and bibtex on it, if they are found. If the bibtex process returns with
     * the expected return value, the test ist passed. Else it fails.
     * 
     * @param bibTexStr
     *            the bibtex document to test
     * @param expectedRes
     *            the expected bibtex return value
     */
    private void checkIfBibTexCompiles(String bibTexStr, int expectedRes) {
        if (!isLatexThere() || !isBibtexThrere()) {
            getLogger().warn("Cannot find latex or bibtex. Skipping test.");
            return;
        }
        try {
            final URL testTexUrl = this.getClass().getResource("/bibtexexport/test.tex");
            if (testTexUrl == null) {
                getLogger().warn("Cannot find test .tex file. Skipping test.");
                return;
            }

            final File texFile = getFile(testTexUrl);
            if (texFile.canRead()) {
                final String parent = texFile.getParentFile().getAbsolutePath();
                final File parentDir = new File(parent);

                getLogger().debug("Beginning test");
                saveBibTex(bibTexStr, parentDir);

                String latexCommand = getLatexCommand(texFile, parent);
                runCommand(latexCommand, parentDir);

                String bibtexCommand = getBibtexCommand();
                int bibtexRes = runCommand(bibtexCommand, parentDir);
                Assert.assertEquals("res: " + expectedRes, expectedRes, bibtexRes);
            }
        }
        catch (IOException e) {
            getLogger().error(e);
        }

    }


    private void checkAuthors(String bibTexStr, PersonList authors) {
        bibTexStr = bibTexStr.replace("\n", "");
        bibTexStr = bibTexStr.replaceAll(".*author.*?=.*?\\{", "");
        String authorStr = bibTexStr.replaceAll("\\}.*", "");
        if (authors != null) {
            String[] authorParts = authorStr.split(" and ");
            int size = authors.size();
            int length = authorParts.length;
            Assert.assertEquals("author names seem okay", size, length);
        }
        else {
            Assert.assertEquals("no authors in bibtex", "", authorStr.trim());
        }
    }


    /**
     * Writes a string into the file <code>test.bib</code> in the given parent
     * directory.
     * 
     * @param testBib
     *            the string to write to disk
     * @param parentDir
     *            the directory in which the file is to be created
     */
    private void saveBibTex(String testBib, File parentDir) {
        try {
            FileOutputStream out = new FileOutputStream(new File(parentDir, "test.bib"));
            out.write(testBib.getBytes("utf8"));
            out.close();
        }
        catch (UnsupportedEncodingException e) {
            getLogger().error(e);
        }
        catch (FileNotFoundException e) {
            getLogger().error(e);
        }
        catch (IOException e) {
            getLogger().error(e);
        }
    }


    private String getBibtexCommand() {
        String bibtexCommand = BIBTEX_NAME + " ";
        bibtexCommand += "test";
        return bibtexCommand;
    }


    private boolean isBibtexThrere() {
        return isCommandThere(BIBTEX_NAME);
    }


    private boolean isLatexThere() {
        return isCommandThere(LATEX_NAME + LATEX_BATCH + " xyz");
    }


    private boolean isCommandThere(String command) {
        boolean itIsThere = true;
        try {
            runCommand(command, null);
        }
        catch (IOException e) {
            itIsThere = false;
        }
        return itIsThere;
    }


    private String getLatexCommand(final File texFile, final String path) {
        String testTexFilename;
        testTexFilename = texFile.getAbsolutePath();
        String options = "";
        options = LATEX_BATCH + " -halt-on-error";
        options += " -output-directory " + path;
        String latexCommand = "latex " + options + " " + testTexFilename;
        return latexCommand;
    }


    private File getFile(final URL testTexUrl) {
        URI uri = null;
        try {
            uri = testTexUrl.toURI();
        }
        catch (URISyntaxException e1) {
            e1.printStackTrace();
        }
        File f = new File(uri);
        return f;
    }


    /**
     * Runs the given command, returning its exit value.
     * <p>
     * If the command runs longer than {@link #SLEEP_TIME_MS}, it is killed.
     * 
     * @param command
     *            the command to run
     * @param dir
     *            the working directory
     * @return the return value
     * @throws IOException
     */
    private int runCommand(String command, File dir) throws IOException {
        getLogger().debug("Running: " + command);
        int res = -1;
        Runtime runtime = Runtime.getRuntime();
        final Process p = runtime.exec(command, new String[0], dir);
        new Thread() {

            @Override
            public void run() {
                try {
                    Thread.sleep(SLEEP_TIME_MS);
                }
                catch (InterruptedException e) {
                }
                boolean running = false;
                try {
                    p.exitValue();
                }
                catch (IllegalThreadStateException e) {
                    running = true;
                }
                if (running) {
                    p.destroy();
                    getLogger().debug("process destroyed");
                }
            };
        }.start();
        try {
            res = p.waitFor();
        }
        catch (InterruptedException e) {
            getLogger().error(e);
        }
        String output = IOUtils.readInputStreamAsString(p.getInputStream());
        String err = IOUtils.readInputStreamAsString(p.getErrorStream());
        getLogger().debug("Res: " + res);
        getLogger().debug(output);
        getLogger().debug(err);
        return res;
    }

}
