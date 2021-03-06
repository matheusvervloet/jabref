package net.sf.jabref.es2test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import net.sf.jabref.Globals;
import net.sf.jabref.JabRefPreferences;
import net.sf.jabref.importer.OutputPrinterToNull;
import net.sf.jabref.importer.fileformat.BibtexImporter;
import net.sf.jabref.model.entry.BibEntry;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ImportItemTest {

    private BibtexImporter importer;

    @Before
    public void setUp() {
        Globals.prefs = JabRefPreferences.getInstance();
        importer = new BibtexImporter();
    }

    @Test
    public void basicTest() throws IOException {
        try (InputStream stream = ImportItemTest.class.getResourceAsStream("es2bib.bib")) {
            List<BibEntry> entries = importer.importEntries(stream, new OutputPrinterToNull());

            assertEquals(2, entries.size());

            for (BibEntry entry : entries) {

                if (entry.getCiteKey().equals("small")) {
                    assertEquals("Freely, I.P.", entry.getField("author"));
                    assertEquals("A small paper", entry.getField("title"));
                    assertEquals("The journal of small papers", entry.getField("journal"));
                    assertEquals("1997", entry.getField("year"));
                    assertEquals("-1", entry.getField("volume"));
                    assertEquals("to appear", entry.getField("note"));
                } else if (entry.getCiteKey().equals("big")) {
                    assertEquals("Jass, Hugh", entry.getField("author"));
                    assertEquals("A big paper", entry.getField("title"));
                    assertEquals("The journal of big papers", entry.getField("journal"));
                    assertEquals("7991", entry.getField("year"));
                    assertEquals("MCMXCVII", entry.getField("volume"));
                }

            }
        }
    }

    @Test
    public void basicTestEmpty() throws IOException {
        try (InputStream stream = ImportItemTest.class.getResourceAsStream("empty.bib")) {
            List<BibEntry> entries = importer.importEntries(stream, new OutputPrinterToNull());

            assertEquals(2, entries.size());

            for (BibEntry entry : entries) {

                if (entry.getType().equals("article")) {
                    assertEquals(null, entry.getField("author"));
                    assertEquals(null, entry.getField("title"));
                    assertEquals(null, entry.getField("journal"));
                    assertEquals(null, entry.getField("year"));
                    assertEquals(null, entry.getField("volume"));
                    assertEquals(null, entry.getField("note"));
                    assertEquals(null, entry.getCiteKey());
                } else if (entry.getType().equals("book")) {
                    assertEquals(null, entry.getField("author"));
                    assertEquals(null, entry.getField("title"));
                    assertEquals(null, entry.getField("publisher"));
                    assertEquals(null, entry.getField("year"));
                    assertEquals(null, entry.getField("editor"));
                    assertEquals(null, entry.getCiteKey());
                }

            }
        }
    }

}

