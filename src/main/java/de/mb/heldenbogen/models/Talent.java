package de.mb.heldenbogen.models;

import de.mb.heldenbogen.Renderer;
import de.mb.heldenbogen.Shortener;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class Talent {
    public String name;
    public boolean meisterhandwerk;
    public boolean leittalent;
    public boolean basis;
    public String nameausfuehrlich;
    public int wert;
    public String probe;
    public String probenwerte;
    public String nameausfuehrlichmitprobe;
    public String behinderung;
    public boolean mirakelplus;
    public boolean mirakelminus;
    public boolean metatalent;
    public String bereich;
    public String komplexitaet;
    public String lernkomplexitaet;
    public String spezialisierungen;
    public String sprachkomplexitaet;
    public boolean muttersprache;
    public boolean schriftmuttersprache;
    public int at;
    public int pa;

    public final ArrayList<Sonderfertigkeit> ts = new ArrayList<>();

    public String getName() {
        String name = this.name;
        if (bereich.equals("Ritualkenntnis")) name = "RK " + name;
        if (bereich.equals("Liturgiekenntnis")) name = "LK " + name;
        return Shortener.getInstance().talent(name);
    }

    public String getShortName() {
        String name = this.name;
        if (bereich.equals("Ritualkenntnis")) name = "RK " + name;
        if (bereich.equals("Liturgiekenntnis")) name = "LK " + name;
        return Shortener.getInstance().talentStrong(name);
    }

    public String getTooltip() {
        String name = this.name;
        if (bereich.equals("Ritualkenntnis") || bereich.equals("Liturgiekenntnis"))
            name = bereich + " " + name;
        name = name.replace("L/S ", "Lesen/Schreiben ").trim();
        if (meisterhandwerk) {
            name += " (Meisterhandwerk)";
        }
        if (mirakelplus) {
            name += " (Mirakel +)";
        }
        if (mirakelminus) {
            name += " (Mirakel -)";
        }
        return name;
    }

    public String getProbe() {
        String art = bereich;
        if (art.equals("Kampf") || art.equals("Sprachen") || art.equals("Schriften"))
            return null;

        return Renderer.formatProbe(probe.split("/"));
    }

    public String getBehinderung() {
        if (bereich.equals("Kampf") || bereich.equals("Körperlich")) {
            return behinderung;
        }
        return null;
    }

    public String getTS() {
        return ts.stream().map(Sonderfertigkeit::getTSSpezialisierung).collect(Collectors.joining(", "));
    }

    public String getClasses() {
        return ts.isEmpty() ? "" : "talent-hat-ts";
    }
}