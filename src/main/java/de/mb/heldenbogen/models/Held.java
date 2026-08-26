package de.mb.heldenbogen.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Held {
    public Angaben angaben;

    public Map<String, Integer> ap = new HashMap<>();
    public Map<String, Integer> gp = new HashMap<>();
    public String aspregeneration;
    public String leregeneration;
    public int wundschwelle;

    public String lastEreignisDate;

    public final Map<String, Integer> eigenschaften = new HashMap<>();


    public final ArrayList<VorNachteil> vorNachteile = new ArrayList<>();
    public final Map<String, VorNachteil> vorNachteilByName = new HashMap<>();

    public final ArrayList<Sonderfertigkeit> sonderfertigkeiten = new ArrayList<>();
    public final ArrayList<Talent> talente = new ArrayList<>();
    public final HashMap<String, Talent> talentByName = new HashMap<>();
    public final ArrayList<Zauber> zauber = new ArrayList<>();

    public final ArrayList<KampfSet> kampfSets = new ArrayList<>();
    public KampfSet kampfset;

    public final ArrayList<Wesen> wesen = new ArrayList<>();

    public void addTalent(Talent talent) {
        talente.add(talent);
        talentByName.put(talent.name, talent);
    }

    public void addVorNachteil(VorNachteil vn) {
        vorNachteile.add(vn);
        vorNachteilByName.put(vn.name, vn);
        vorNachteilByName.put(vn.bezeichner, vn);
        vorNachteilByName.put(vn.nameMitKommentar, vn);
        vorNachteilByName.put(vn.name.replaceAll(": [0-9]+$", ""), vn);
    }

    public void complete() {
        eigenschaften.put("MU", eigenschaften.get("Mut"));
        eigenschaften.put("KL", eigenschaften.get("Klugheit"));
        eigenschaften.put("IN", eigenschaften.get("Initiative"));
        eigenschaften.put("CH", eigenschaften.get("Charisma"));
        eigenschaften.put("FF", eigenschaften.get("Fingerfertigkeit"));
        eigenschaften.put("GE", eigenschaften.get("Gewandtheit"));
        eigenschaften.put("KO", eigenschaften.get("Konstitution"));
        eigenschaften.put("KK", eigenschaften.get("Körperkraft"));
        eigenschaften.put("MR", eigenschaften.get("Magieresistenz"));
        eigenschaften.put("Lep", eigenschaften.get("Lebensenergie"));
        eigenschaften.put("Asp", eigenschaften.get("Astralenergie"));
        eigenschaften.put("Kap", eigenschaften.get("Karmaenergie"));
    }

    public int getEigenschaft(String eigenschaft) {
        if (eigenschaft.equals("Ausweichen")) {
            return kampfset.ausweichen;
        }
        return eigenschaften.get(eigenschaft);
    }
}
