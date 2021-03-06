package net.sf.jabref.exporter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.sf.jabref.BibDatabaseContext;
import net.sf.jabref.Globals;
import net.sf.jabref.JabRefPreferences;
import net.sf.jabref.MetaData;
import net.sf.jabref.importer.OutputPrinterToNull;
import net.sf.jabref.importer.fileformat.BibtexImporter;
import net.sf.jabref.model.database.BibDatabase;
import net.sf.jabref.model.entry.BibEntry;

import com.google.common.base.Charsets;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(Parameterized.class)
public class MSBibExportFormatTestFiles {

    public BibDatabaseContext databaseContext;
    public Charset charset;
    public File tempFile;
    public MSBibExportFormat msBibExportFormat;
    public BibtexImporter testImporter;

    public static final String PATH_TO_FILE = "src/test/resources/net/sf/jabref/exporter/";

    @Parameter
    public String filename;

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();


    @Parameters(name = "{0}")
    public static Collection<String> fileNames() throws IOException {
        try (Stream<Path> stream = Files.list(Paths.get(PATH_TO_FILE))) {
            return stream.map(n -> n.getFileName().toString()).filter(n -> n.endsWith(".bib"))
                    .filter(n -> n.startsWith("MsBibExportFormat")).collect(Collectors.toList());
        }
    }

    @Before
    public void setUp() throws Exception {
        Globals.prefs = JabRefPreferences.getInstance();
        databaseContext = new BibDatabaseContext(new BibDatabase(), new MetaData());
        charset = Charsets.UTF_8;
        msBibExportFormat = new MSBibExportFormat();
        tempFile = testFolder.newFile();
        testImporter = new BibtexImporter();
    }

    @Test
    public final void testPerformExport() throws IOException {
        String xmlFileName = filename.replace(".bib", ".xml");
        String tempFilename = tempFile.getCanonicalPath();
        try (InputStream bibIn = MSBibExportFormat.class.getResourceAsStream(filename)) {
            List<BibEntry> entries = testImporter.importEntries(bibIn, new OutputPrinterToNull());
            assertNotNull(entries);
            msBibExportFormat.performExport(databaseContext, tempFile.getPath(), charset, entries);
        }
        List<String> expected = Files.readAllLines(Paths.get(PATH_TO_FILE + xmlFileName));
        List<String> exported = Files.readAllLines(Paths.get(tempFilename));
        assertEquals(expected, exported);
    }
}
