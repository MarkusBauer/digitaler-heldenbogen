package de.mb.heldenbogen.models;

import de.mb.heldenbogen.Shortener;

import java.util.ArrayList;
import java.util.HashMap;

public class Sonderfertigkeit {
    public String name;
    private String lastName; // after joins: right-most name
    public boolean hidden = false;

    public String nameAusfuehrlich;
    public String bezeichner;
    public String wirkung;
    public String dauer;
    public String kosten;
    public String probe;
    public String kommentar = "";
    public String nameMitKommentar;
    public String[] bereich;
    public ArrayList<HashMap<String, String>> auswahlen = new ArrayList<>();

    public String custom;

    public void complete() {
        this.name = Shortener.getInstance().sf(nameAusfuehrlich);
        this.lastName = name;

        if (wirkung.equals("-----")) wirkung = "";
        if (dauer.equals("-----")) dauer = "";
        if (kosten.equals("-----")) kosten = "";
        if (probe.equals("-----")) probe = "";

        if (istBereich("Liturgie") && probe.startsWith("Grad") && probe.contains(",")) {
            custom = probe.substring(probe.indexOf(',') + 1).trim();
            probe = probe.substring(0, probe.indexOf(',')).trim();
        }
    }

    public boolean istBereich(String bereich) {
        for (String b : this.bereich) {
            if (b.equals(bereich)) {
                return true;
            }
        }
        return false;
    }

    public boolean canJoin(Sonderfertigkeit sf2) {
        return sf2.name.equals(lastName + "I") ||
            (name.startsWith("Kampfstil:") && sf2.name.startsWith("Kampfstil:")) ||
            (name.startsWith("Merkmalskenntnis:") && sf2.name.startsWith("Merkmalskenntnis:")) ||
            (name.startsWith("Repräsentation:") && sf2.name.startsWith("Repräsentation:")) ||
            (name.startsWith("Wahrer Name:") && sf2.name.startsWith("Wahrer Name:"));
    }

    public void join(Sonderfertigkeit sf2) {
        if (name.startsWith("Kampfstil:") || name.startsWith("Merkmalskenntnis:") || name.startsWith("Repräsentation:") || name.startsWith("Wahrer Name:")) {
            name += ", " + sf2.name.substring(sf2.name.indexOf(':') + 2);
        } else {
            name += " + " + sf2.name.substring(sf2.name.lastIndexOf(' ') + 1);
        }
        if (!sf2.kommentar.isEmpty()) {
            kommentar += (kommentar.isEmpty() ? "" : ";\n") + sf2.kommentar;
        }
        sf2.hidden = true;
        lastName = sf2.name;
    }

    public boolean istTalentspezialisierung() {
        return istBereich("Talentspezialisierung");
    }

    public String getTSTalent() {
        if (!bezeichner.startsWith("Talentspezialisierung")) {
            return null;
        }
        // "Talentspezialisierung Kochen (Tränke)"
        String s = bezeichner.substring(22);
        return s.substring(0, s.indexOf(" (")).trim();
    }

    public String getTSSpezialisierung() {
        if (!bezeichner.startsWith("Talentspezialisierung")) {
            return null;
        }
        // "Talentspezialisierung Kochen (Tränke)"
        String s = bezeichner.substring(22);
        return s.substring(s.indexOf(" (") + 2, s.length() - 1).trim();
    }

    public boolean istRitual() {
        return istBereich("Ritual");
    }

    public boolean istLiturgie() {
        return istBereich("Liturgie");
    }

    public boolean istLiturgiekenntnis() {
        return istBereich("Liturgiekenntnis");
    }
}
