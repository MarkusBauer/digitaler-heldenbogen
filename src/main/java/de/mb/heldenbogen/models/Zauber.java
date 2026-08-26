package de.mb.heldenbogen.models;

import de.mb.heldenbogen.Shortener;

import static de.mb.heldenbogen.Renderer.formatProbe;

public class Zauber {
    public String name;
    public final String nameMitVariante;
    public final String displayName;
    public String variante;
    public String nameAusfuehrlich;
    public int wert;
    public String spezialisierungen;
    public String probe;
    public String probenwerte;
    public String bereich;
    public String komplexitaet;
    public String lernKomplexitaet;
    public boolean hauszauber;
    public String repraesentation;
    public String merkmale;
    public String zauberdauer;
    public String kosten;
    public String reichweite;
    public String wirkungsdauer;
    public String anmerkung;
    public String quelle;
    public String kontrollwert;
    public String mr;
    public boolean leittalent;

    public Zauber(String nameMitVariante) {
        this.nameMitVariante = nameMitVariante;
        this.displayName = Shortener.getInstance().zauber(nameMitVariante);
    }

    public String getProbe() {
        return "(" + formatProbe(probe.split("/")) + ")" + mr.replace("+Mod", "+x");
    }

    public String getTooltip() {
        return "Merkmale: " + merkmale +
            "\nKomplexität: " + komplexitaet + "/" + lernKomplexitaet +
            "\nQuelle: " + quelle;
    }
}
