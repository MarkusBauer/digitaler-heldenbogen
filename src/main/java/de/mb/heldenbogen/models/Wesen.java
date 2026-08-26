package de.mb.heldenbogen.models;

import java.util.ArrayList;
import java.util.HashMap;

public class Wesen {
    public String name;
    public String grundlage;
    public float gewicht;
    public String quelle;
    public HashMap<String, Integer> eigenschaften = new HashMap<>();
    public ArrayList<VorNachteil> vorteile = new ArrayList<>();
    public ArrayList<Sonderfertigkeit> sonderfertigkeiten = new ArrayList<>();
    public ArrayList<WesenTalent> talente = new ArrayList<>();
    public String gattung;
    public String familie;
    public int groesse;
    public String ini;
    public ArrayList<HashMap<String, String>> angriffe = new ArrayList<>();


    public String getEigenschaft(String eigenschaft) {
        eigenschaft = eigenschaft.toLowerCase();
        eigenschaft = eigenschaft.replace("ä", "ae").replace("ö", "oe");
        if (eigenschaft.equals("initiative"))
            return ini;
        if (eigenschaften.containsKey(eigenschaft)) {
            String suffix = "";
            if (eigenschaft.equals("magieresistenz") || eigenschaft.equals("geschwindigkeit")) {
                int i = 2;
                Integer last = eigenschaften.get(eigenschaft);
                while (eigenschaften.containsKey(eigenschaft + i)) {
                    if (!last.equals(eigenschaften.get(eigenschaft + i))) {
                        last = eigenschaften.get(eigenschaft + i);
                        suffix += " / " + last;
                    }
                    i++;
                }
            }

            return eigenschaften.get(eigenschaft).toString() + suffix;
        } else {
            return null;
        }
    }

    public String getGewichtInStein() {
        float f = gewicht / 40.0f;
        if (f < 9.9) return String.format("%.1f", f);
        return Integer.toString(Math.round(f));
    }

    public static class WesenTalent {
        public String name;
        public boolean basis;
        public String probe;
        public int wert;
    }
}
