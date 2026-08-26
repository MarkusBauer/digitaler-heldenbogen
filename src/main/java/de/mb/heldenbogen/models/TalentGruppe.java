package de.mb.heldenbogen.models;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class TalentGruppe {
    public final String name;
    public final ArrayList<Talent> talente = new ArrayList<>();

    public TalentGruppe(String name) {
        this.name = name;
    }

    public String getIdent() {
        // css classes etc
        return name.toLowerCase().replace(" ", "-").replace("ö", "oe");
    }

    public boolean isVisible() {
        return !this.talente.isEmpty() || !this.name.equals("Gaben");
    }

    public boolean hasAtPa() {
        return name.equals("Kampf");
    }

    public boolean hasK() {
        return name.equals("Sprachen") || name.equals("Schriften");
    }

    public String getProbe() {
        if (name.equals("Sprachen")) return "KL/IN/CH";
        if (name.equals("Schriften")) return "KL/FF/FF";
        return null;
    }

    public static ArrayList<TalentGruppe> getInGruppen(ArrayList<Talent> talente) {
        Map<String, TalentGruppe> gruppeByName = new LinkedHashMap<>();
        gruppeByName.put("Gaben", new TalentGruppe("Gaben"));
        gruppeByName.put("Kampf", new TalentGruppe("Kampf"));
        gruppeByName.put("Körperlich", new TalentGruppe("Körperlich"));
        gruppeByName.put("Gesellschaft", new TalentGruppe("Gesellschaft"));
        gruppeByName.put("Natur", new TalentGruppe("Natur"));
        gruppeByName.put("Wissen", new TalentGruppe("Wissen"));
        gruppeByName.put("Sprachen", new TalentGruppe("Sprachen"));
        gruppeByName.put("Schriften", new TalentGruppe("Schriften"));
        gruppeByName.put("Handwerk", new TalentGruppe("Handwerk"));

        for (Talent t : talente) {
            if (!t.metatalent && t.wert > -9) {
                gruppeByName.computeIfAbsent(t.bereich, TalentGruppe::new).talente.add(t);
            }
        }
        return new ArrayList<>(gruppeByName.values());
    }


    public static TalentGruppe getRitualKenntnisse(ArrayList<TalentGruppe> talente) {
        TalentGruppe result = new TalentGruppe("");
        for (TalentGruppe tg : talente) {
            if (tg.name.equals("Ritualkenntnis") || tg.name.equals("Liturgiekenntnis") || tg.name.equals("Ritualfertigkeit")) {
                result.talente.addAll(tg.talente);
            }
        }
        return result;
    }
}