package de.mb.heldensoftware;

import helden.plugin.datenxmlplugin.DatenAustausch3Interface;

import java.util.ArrayList;
import java.util.HashMap;

public class HeldChangeListeners {
    private static boolean isBoundToInterface = false;

    public static final HashMap<String, ArrayList<Runnable>> listeners = new HashMap<>();

    public static final String CHANGE = "Änderung";
    public static final String HELD_SELECTION_CHANGED = "neuer Held";
    public static final String BEFORE_SAVE_WITH_DATA_LOSS = "vor speichern mit verlust";
    public static final String BEFORE_SAVE = "Speichern Start";
    public static final String AFTER_SAVE = "Speichern Ende";

    public static void addListener(String key, Runnable r) {
        listeners.computeIfAbsent(key, k -> new ArrayList<>()).add(r);
    }

    public static void fire(String key) {
        listeners.getOrDefault(key, new ArrayList<>()).forEach(Runnable::run);
    }

    public static void bindToInterface(DatenAustausch3Interface iface) {
        if (!isBoundToInterface) {
            iface.addChangeListener(e -> fire(e.getSource().toString()));
            isBoundToInterface = true;
        }
    }
}
