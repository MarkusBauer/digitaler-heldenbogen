package de.mb.autoexport;

import de.mb.config.AutoBogenConfig;
import de.mb.config.CustomGlobalConfig;
import de.mb.reflection.HeldReflector;
import helden.framework.Einstellungen;
import helden.plugin.datenplugin.impl.DatenPluginHeldenWerkzeugImpl;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

public class AutoBogenDialog extends JDialog {
    private static AutoBogenDialog instance = null;

    public static AutoBogenDialog getInstance(JFrame parent) {
        if (instance == null) instance = new AutoBogenDialog(parent);
        return instance;
    }

    private final AutoBogenConfig config = CustomGlobalConfig.get(new AutoBogenConfig());

    DefaultTableModel model = new DefaultTableModel(new Object[]{"Held", "Typ", "Datei", ""}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return column == 3;
        }
    };

    public AutoBogenDialog(JFrame parent) {
        super(parent, "Auto-Export für Heldenbogen", true);

        setLayout(new BorderLayout());

        config.exports.forEach(this::addToModel);

        JLabel helpLabel = new JLabel(
            "<html>Sie können Heldenbögen exportieren, die automatisch aktuell gehalten werden. " +
                "Nach jeder Steigerung wird die Software automatisch die generierte Datei aktualisieren. "+
            "In Kombination mit bspw. Dropbox haben Sie so ihre aktuellen Helden immer dabei."
        );
        helpLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        add(helpLabel, BorderLayout.NORTH);

        JTable table = new JTable(model);
        new ButtonColumn(table, new DeleteEntryAction(), 3);
        table.getColumnModel().getColumn(3).setPreferredWidth(50);
        table.getColumnModel().getColumn(3).setWidth(50);
        table.getColumnModel().getColumn(3).setMaxWidth(50);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel panel = new JPanel();
        JButton btn = new JButton(new AddEntryAction("new-html"));
        btn.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(btn);
        btn = new JButton(new AddEntryAction("new-pdf"));
        btn.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(btn);
        btn = new JButton(new AddEntryAction("old-pdf"));
        btn.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(btn);
        add(panel, BorderLayout.SOUTH);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
    }

    private void addToModel(AutoBogenConfig.AutoExport entry) {
        Object held = HeldReflector.getInstance().getHeldByID(entry.heldId);
        model.addRow(new Object[]{
            held != null ? held.toString() : "#" + entry.heldId + " (nicht verfügbar)",
            typeToDisplay(entry.type),
            entry.path,
            "-"
        });
    }

    public AutoBogenConfig getConfig() {
        return config;
    }

    public class AddEntryAction extends AbstractAction {
        private final String type;

        public AddEntryAction(String type) {
            super("<html><center>Aktuellen Held<br>als " + typeToDisplayGenitiv(type) + "<br>automatisch exportieren...");
            this.type = type;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            DatenPluginHeldenWerkzeugImpl werkzeug = HeldReflector.getInstance().getCurrentHeldWerkzeug();

            String extension = type.split("-")[1];
            JFileChooser chooser = new JFileChooser(Einstellungen.getInstance().getLetzterPfad());
            chooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
                public boolean accept(java.io.File f) {
                    return f.isDirectory() || f.getName().toLowerCase().endsWith("." + extension);
                }

                public String getDescription() {
                    return "*." + extension;
                }
            });
            chooser.setSelectedFile(new File(werkzeug.getSelectesHeld().getHeldObject().toString() + "." + extension));

            if (chooser.showSaveDialog(AutoBogenDialog.this) == JFileChooser.APPROVE_OPTION) {
                Einstellungen.getInstance().setLetzterPfad(chooser.getSelectedFile().getParent());

                String path = chooser.getSelectedFile().getAbsolutePath();
                final String fname = chooser.getSelectedFile().getName();
                if (!path.toLowerCase().endsWith("." + extension)) {
                    path += "." + extension;
                }
                AutoBogenConfig.AutoExport entry = new AutoBogenConfig.AutoExport(werkzeug.getHeldenID(), path, type);
                config.exports.add(entry);
                addToModel(entry);
                CustomGlobalConfig.set(config);

                Exporter.getInstance().exportOne(entry, () ->
                    JOptionPane.showMessageDialog(AutoBogenDialog.this,
                        "Datei " + fname + " wurde erfolgreich erstellt, und wird ab jetzt automatisch aktualisiert.",
                        "Export erfolgreich", JOptionPane.INFORMATION_MESSAGE
                    )
                );
            }
        }
    }

    public class DeleteEntryAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            JTable table = (JTable) e.getSource();
            int modelRow = Integer.parseInt(e.getActionCommand());
            String heldName = table.getModel().getValueAt(modelRow, 0).toString();

            if (JOptionPane.showConfirmDialog(AutoBogenDialog.this, "Automatischen Export für " + heldName + " wirklich löschen?", "Export löschen", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                ((DefaultTableModel) table.getModel()).removeRow(modelRow);
                config.exports.remove(modelRow);
                CustomGlobalConfig.set(config);
            }
        }
    }

    public static String typeToDisplay(String type) {
        switch (type) {
            case "new-html":
                return "neuer Heldenbogen (HTML)";
            case "new-pdf":
                return "neuer Heldenbogen (PDF)";
            case "old-pdf":
                return "klassischer Heldenbogen (PDF)";
            default:
                return type;
        }
    }

    public static String typeToDisplayGenitiv(String type) {
        switch (type) {
            case "new-html":
                return "neuen Heldenbogen (HTML)";
            case "new-pdf":
                return "neuen Heldenbogen (PDF)";
            case "old-pdf":
                return "klassischen Heldenbogen (PDF)";
            default:
                return type;
        }
    }

}
