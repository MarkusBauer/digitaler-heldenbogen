package de.mb.heldenbogen;

import de.mb.heldenbogen.models.Held;
import helden.framework.Einstellungen;
import helden.plugin.HeldenDatenPlugin;
import helden.plugin.datenplugin.DatenPluginHeldenWerkzeug;
import helden.plugin.datenxmlplugin.XMLDatenGenerator;
import helden.plugin.werteplugin2.PluginHeld2;
import org.w3c.dom.Document;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

public class HeldenbogenPlugin implements HeldenDatenPlugin {
    @Override
    public void doWork(JFrame jFrame, PluginHeld2[] helden, DatenPluginHeldenWerkzeug werkzeug) {
        checkForInfos(jFrame);
        File output = fileSaveDialog(jFrame);
        if (output == null) return;
        File outputHtml = null;
        File outputPdf = null;
        if (output.getName().toLowerCase().endsWith(".html")) {
            outputHtml = output;
        } else if (output.getName().toLowerCase().endsWith(".pdf")) {
            outputPdf = output;
        } else {
            outputHtml = new File(output.getParentFile(), output.getName() + ".html");
            outputPdf = new File(output.getParentFile(), output.getName() + ".pdf");
        }

        ExportRunningWindow.start();
        new ExportWorker(jFrame, werkzeug, outputHtml, outputPdf).execute();
    }

    public static class ExportWorker extends SwingWorker<Void, Void> {
        private final JFrame jFrame;
        private final DatenPluginHeldenWerkzeug werkzeug;
        private final File outputHtml;
        private final File outputPdf;

        public ExportWorker(JFrame frame, DatenPluginHeldenWerkzeug werkzeug, File outputHtml, File outputPdf) {
            this.jFrame = frame;
            this.werkzeug = werkzeug;
            this.outputHtml = outputHtml;
            this.outputPdf = outputPdf;
        }

        @Override
        protected Void doInBackground() throws IOException {
            String html = new Renderer(getHeldFromPluginApi(werkzeug), "alle_bogen", false).render();

            if (outputHtml != null) {
                try (FileWriter writer = new FileWriter(outputHtml)) {
                    writer.write(html);
                }
            }
            if (outputPdf != null) {
                PdfCreator.createSingle(html, outputPdf);
            }

            try {
                if (outputHtml != null && outputPdf == null) {
                    Desktop.getDesktop().open(outputHtml);
                } else if (outputHtml == null && outputPdf != null) {
                    Desktop.getDesktop().open(outputPdf);
                }
            } catch (IOException ignored) {
            }
            return null;
        }

        @Override
        protected void done() {
            ExportRunningWindow.stop();
            try {
                get();
                JOptionPane.showMessageDialog(jFrame, "Heldenbogen wurde erfolgreich geschrieben.", "Information", JOptionPane.INFORMATION_MESSAGE);
            } catch (ExecutionException e) {
                e.getCause().printStackTrace();
                JOptionPane.showMessageDialog(jFrame, "Datei kann nicht geschrieben werden: " + e.getCause().getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
            } catch (InterruptedException e) {
                JOptionPane.showMessageDialog(jFrame, "Datei kann nicht geschrieben werden: " + e.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    protected void checkForInfos(JFrame jFrame) {
        // not implemented here, because not available in standalone
    }

    private File fileSaveDialog(JFrame jFrame) {
        JFileChooser fileChooser = new JFileChooser(Einstellungen.getInstance().getLetzterPfad());
        fileChooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".html") || f.getName().toLowerCase().endsWith(".pdf");
            }

            @Override
            public String getDescription() {
                return "HTML / PDF Dokumente (*.html, *.pdf)";
            }
        });

        if (fileChooser.showSaveDialog(jFrame) == JFileChooser.APPROVE_OPTION) {
            Einstellungen.getInstance().setLetzterPfad(fileChooser.getCurrentDirectory().getAbsolutePath());
            return fileChooser.getSelectedFile();
        }
        return null;
    }

    @Override
    public void initTab(DatenPluginHeldenWerkzeug werkzeug) {
    }

    @Override
    public int compareVersion(String s) {
        return 0;
    }

    @Override
    public void doWork(JFrame jFrame) {
    }

    @Override
    public ImageIcon getIcon() {
        return null;
    }

    @Override
    public String getMenuName() {
        return "Heldenbogen speichern...";
    }

    @Override
    public String getToolTipText() {
        return null;
    }

    @Override
    public String getType() {
        return HELDENDATEN;
    }

    @Override
    public String getVersion() {
        return "0.1a";
    }

    public static Document getAdditionalInfos(DatenPluginHeldenWerkzeug werkzeug) {
        try {
            Method genDaten = Arrays.stream(XMLDatenGenerator.class.getMethods()).filter(m -> m.getName().equals("genDaten") && m.getParameterTypes()[1].equals(int.class)).findFirst().get();
            return (Document) genDaten.invoke(null, werkzeug.getSelectesHeld().getHeldObject(), 3);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Held getHeldFromPluginApi(DatenPluginHeldenWerkzeug werkzeug) {
        return HeldExtractor.parseHeldDaten(getAdditionalInfos(werkzeug));
    }
}
