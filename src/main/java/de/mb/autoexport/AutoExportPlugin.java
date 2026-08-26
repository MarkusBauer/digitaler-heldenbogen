package de.mb.autoexport;

import de.mb.BasicXMLPlugin;
import de.mb.config.AutoBogenConfig;
import de.mb.heldensoftware.HeldChangeListeners;
import de.mb.reflection.HeldReflector;
import helden.plugin.HeldenXMLDatenPlugin3;
import helden.plugin.datenxmlplugin.DatenAustausch3Interface;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;

public class AutoExportPlugin extends BasicXMLPlugin implements HeldenXMLDatenPlugin3 {

    private ArrayList<Object> changedHelden;

    private void onBeforeSave() {
        changedHelden = new ArrayList<>();
        for (Object held : HeldReflector.getInstance().getAlleHelden()) {
            if (HeldReflector.getInstance().isHeldChanged(held))
                changedHelden.add(held);
        }
    }

    private void onAfterSave() {
        if (changedHelden == null) return;
        AutoBogenConfig config = AutoBogenDialog.getInstance(jFrame).getConfig();
        ArrayList<AutoBogenConfig.AutoExport> queue = new ArrayList<>();
        for (Object held : changedHelden) {
            if (!HeldReflector.getInstance().isHeldChanged(held)) {
                String heldId = HeldReflector.getInstance().getWerkzeug(held).getHeldenID();
                for (AutoBogenConfig.AutoExport entry : config.exports) {
                    if (entry.heldId.equals(heldId)) {
                        queue.add(entry);
                    }
                }
            }
        }
        if (!queue.isEmpty()) {
            Exporter.getInstance().exportMany(queue, null);
        }
        changedHelden = null;
    }

    @Override
    public void init(DatenAustausch3Interface datenAustausch3Interface, JFrame jFrame) {
        super.init(datenAustausch3Interface, jFrame);
        HeldChangeListeners.bindToInterface(datenAustausch3Interface);
        HeldChangeListeners.addListener(HeldChangeListeners.BEFORE_SAVE, this::onBeforeSave);
        HeldChangeListeners.addListener(HeldChangeListeners.AFTER_SAVE, this::onAfterSave);

        // Create missing exports after everything is loaded
        Timer timer = new Timer(1500, e -> {
            ArrayList<AutoBogenConfig.AutoExport> queue = new ArrayList<>();
            for (AutoBogenConfig.AutoExport entry : AutoBogenDialog.getInstance(jFrame).getConfig().exports) {
                if (!entry.exists() && entry.isWriteable() && HeldReflector.getInstance().getHeldByID(entry.heldId) != null) {
                    queue.add(entry);
                }
            }
            if (!queue.isEmpty()) {
                Exporter.getInstance().exportMany(queue, null);
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    @Override
    public boolean hatMenu() {
        return true;
    }

    @Override
    public String getMenuName() {
        return "Auto-Export für Heldenbogen";
    }

    @Override
    public void click() {
        AutoBogenDialog.getInstance(jFrame).setVisible(true);
    }
}
