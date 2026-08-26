package de.mb.heldenbogen.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class KampfSet {
    public boolean trefferzonenModell = true;
    public boolean inBenutzung = false;
    public Map<String, String> raufen = new HashMap<>();
    public Map<String, String> ringen = new HashMap<>();
    public Map<String, String> ruestungsZonen = new HashMap<>();
    public ArrayList<NahkampfWaffe> nahkampfWaffen = new ArrayList<>();
    public ArrayList<FernkampfWaffe> fernkampfWaffen = new ArrayList<>();
    public ArrayList<Schild> schilder = new ArrayList<>();
    public int ini = 0;
    public int ausweichen = 0;
    public int geschwindigkeitinklbe = 0;

    public boolean ruestungIsZero() {
        for (String z : this.ruestungsZonen.values()) {
            if (!z.equals("0")) {
                return false;
            }
        }
        return true;
    }
}
