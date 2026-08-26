package de.mb.heldenbogen;

import de.mb.heldensoftware.customentries.CustomEntryLoader;
import de.mb.heldensoftware.customentries.EntryCreator;
import de.mb.heldensoftware.customentries.ErrorHandler;
import de.mb.heldensoftware.customentries.ModsDatenParserBugPatcher;
import helden.framework.Einstellungen;
import helden.framework.held.persistenz.XMLPersistierer;
import helden.plugin.datenplugin.impl.DatenPluginHeldenWerkzeugImpl;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.io.InputStream;
import java.util.Enumeration;

import static de.mb.heldenbogen.HeldenbogenPlugin.getAdditionalInfos;
import static de.mb.heldenbogen.HeldenbogenPlugin.getHeldFromPluginApi;

public class HeldenbogenManualExporter {
    public DatenPluginHeldenWerkzeugImpl loadHeldForPlugin(File input) {
        try {
            Object held = new XMLPersistierer().ladeHelden(input).get(0);
            return (DatenPluginHeldenWerkzeugImpl) DatenPluginHeldenWerkzeugImpl.class.getConstructors()[0].newInstance(held);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public DatenPluginHeldenWerkzeugImpl loadHeldForPlugin(InputStream input) {
        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(input);
            Object held = new XMLPersistierer().ladeHelden(doc).get(0);
            return (DatenPluginHeldenWerkzeugImpl) DatenPluginHeldenWerkzeugImpl.class.getConstructors()[0].newInstance(held);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void exportDemo() {
        try (PdfCreator creator = new PdfCreator()) {
            // export(new File("demo/IsidavonLowangen.xml"), new File("demo/IsidavonLowangen.html"), creator);
            for (File xml: new File("demo/").listFiles(f -> f.getName().endsWith(".xml") && !f.getName().contains(".plugin."))) {
                export(xml, new File("demo/" + xml.getName().replace(".xml", ".html")), creator);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void exportAll() {
        String filename = Einstellungen.getInstance().getPfade().getPfad("heldenPfad");
        try (ZipFile zipFile = new ZipFile(filename)) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (entry.getName().toLowerCase().endsWith(".xml")) {
                    try (InputStream is = zipFile.getInputStream(entry)) {
                        String outputName = entry.getName().replaceAll(".xml$", ".html");
                        System.err.println("Working on " + outputName + " ...");
                        export(is, new File("demo/all/" + outputName), null);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void export(File input, File output, PdfCreator creator) throws IOException {
        DatenPluginHeldenWerkzeugImpl werkzeug = loadHeldForPlugin(input);
        exportWerkzeug(werkzeug, output, creator);
    }

    public void export(InputStream input, File output, PdfCreator creator) throws IOException {
        DatenPluginHeldenWerkzeugImpl werkzeug = loadHeldForPlugin(input);
        exportWerkzeug(werkzeug, output, creator);
    }

    public void exportWerkzeug(DatenPluginHeldenWerkzeugImpl werkzeug, File output, PdfCreator creator) throws IOException {
        werkzeug.setAktivenHeld(werkzeug.getSelectesHeld());

        String bogen = "alle_bogen";

        if (output.getName().endsWith(".html")) {
            HeldExtractor.dumpDocument(getAdditionalInfos(werkzeug), new File(output.getParentFile(), output.getName().replace(".html", ".plugin.xml")));
            String html = new Renderer(getHeldFromPluginApi(werkzeug), bogen, true).render();
            Files.write(output.toPath(), html.getBytes());
            html = new Renderer(getHeldFromPluginApi(werkzeug), bogen, false).render();
            Files.write(new File(output.getParentFile(), output.getName().replace(".html", ".full.html")).toPath(), html.getBytes());
        }

        if (creator != null) {
            output = new File(output.getParentFile(), output.getName().replace(".html", ".pdf"));
            String html = new Renderer(getHeldFromPluginApi(werkzeug), bogen, false).render();
            creator.create(html, output);
        }
    }

    public static void main(String[] args) {
        Einstellungen.getInstance().setArgs(new String[]{});

        // customentryloader hacks
        try {
            ErrorHandler.patchHeldenErrorHandler();
            ModsDatenParserBugPatcher.patchModsDatenParser();
            EntryCreator.getInstance();
            CustomEntryLoader.loadFiles();
        } catch (Exception e) {
            // e.printStackTrace();
            System.err.println("Cannot load CustomEntryLoader");
        }

        HeldenbogenManualExporter runner = new HeldenbogenManualExporter();
        runner.exportDemo();
        // runner.exportAll();
    }
}
