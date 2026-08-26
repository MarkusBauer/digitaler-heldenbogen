package de.mb.autoexport;

import de.mb.config.AutoBogenConfig;
import de.mb.heldenbogen.ExportRunningWindow;
import de.mb.heldenbogen.PdfCreator;
import de.mb.heldenbogen.Renderer;
import de.mb.reflection.HeldReflector;
import helden.plugin.datenplugin.DatenPluginHeldenWerkzeug;

import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;

import static de.mb.heldenbogen.HeldenbogenPlugin.getHeldFromPluginApi;

/**
 * Run auto-export tasks, bulked if possible. Uses SwingWorker/Background Threads to do so, and tracks them properly.
 * On termination, started and scheduled tasks are allowed to complete.
 */
public class Exporter {
    private static Exporter instance = null;

    public static Exporter getInstance() {
        if (instance == null) instance = new Exporter();
        return instance;
    }

    private Exporter() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            ExportWorker worker = getUnfinishedWorker();
            while (worker != null) {
                try {
                    worker.join(20000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                worker = getUnfinishedWorker();
            }
        }));
    }

    private HashSet<ExportWorker> workers = new HashSet<>();

    protected synchronized void addWorker(ExportWorker worker) {
        workers.add(worker);
    }

    protected synchronized void removeWorker(ExportWorker worker) {
        workers.remove(worker);
    }

    protected synchronized ExportWorker getUnfinishedWorker() {
        for (ExportWorker worker : workers) {
            if (worker.isAlive()) {
                return worker;
            }
        }
        return null;
    }

    public void exportOne(AutoBogenConfig.AutoExport export, Runnable onSuccess) {
        ArrayList<AutoBogenConfig.AutoExport> list = new ArrayList<>();
        list.add(export);
        exportMany(list, onSuccess);
    }

    public void exportMany(Collection<AutoBogenConfig.AutoExport> exports, Runnable onSuccess) {
        exports = exports.stream().filter(e -> e.isWriteable()).collect(Collectors.toList());
        if (!exports.isEmpty()) {
            new ExportWorker(exports, onSuccess).execute();
        }
    }

    public class ExportWorker extends Thread {
        private final Collection<AutoBogenConfig.AutoExport> exports;
        private final Runnable onSuccess;
        private int success = 0;
        private PdfCreator creator = null;

        public ExportWorker(Collection<AutoBogenConfig.AutoExport> exports, Runnable onSuccess) {
            super("ExportWorker");
            this.exports = exports;
            this.onSuccess = onSuccess;
        }

        public void execute() {
            addWorker(this);
            ExportRunningWindow.start();
            start();
        }

        public void initPdfCreator() {
            if (creator == null) {
                creator = new PdfCreator();
            }
        }

        public void freePdfCreator() {
            try {
                if (creator != null) {
                    creator.close();
                    creator = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public boolean needsPdfCreator() {
            for (AutoBogenConfig.AutoExport export : exports) {
                if ("new-pdf".equals(export.type)) return true;
            }
            return false;
        }

        @Override
        public void run() {
            try {
                if (needsPdfCreator()) initPdfCreator();
                for (AutoBogenConfig.AutoExport export : exports) {
                    try {
                        Object held = HeldReflector.getInstance().getHeldByID(export.heldId);
                        if (held == null) continue;
                        DatenPluginHeldenWerkzeug werkzeug = HeldReflector.getInstance().getWerkzeug(held);
                        File output = new File(export.path);

                        switch (export.type) {
                            case "new-html":
                                exportNewHtml(werkzeug, output);
                                break;
                            case "new-pdf":
                                exportNewPDF(werkzeug, output);
                                break;
                            case "old-pdf":
                                exportOldPDF(werkzeug, output);
                                break;
                        }

                        success++;

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            } finally {
                removeWorker(this);
                freePdfCreator();
                System.out.println("Exporter: Created " + exports.size() + " files.");
                SwingUtilities.invokeLater(() -> {
                    ExportRunningWindow.stop();
                    if (onSuccess != null && success == exports.size())
                        onSuccess.run();
                });
            }
        }

        private void exportNewHtml(DatenPluginHeldenWerkzeug werkzeug, File output) throws IOException {
            String html = new Renderer(getHeldFromPluginApi(werkzeug), "alle_bogen", false).render();
            try (FileWriter writer = new FileWriter(output)) {
                writer.write(html);
            }
        }

        private void exportNewPDF(DatenPluginHeldenWerkzeug werkzeug, File output) {
            String html = new Renderer(getHeldFromPluginApi(werkzeug), "alle_bogen", false).render();
            initPdfCreator();
            creator.create(html, output);
        }

        private void exportOldPDF(DatenPluginHeldenWerkzeug werkzeug, File output) throws IOException {
            OldHeldenbogenWriter.heldToPdf(werkzeug.getSelectesHeld().getHeldObject(), output);
        }
    }
}
