package de.mb.heldenbogen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Shortener {
    private static Shortener instance;

    public static Shortener getInstance() {
        if (instance == null) {
            instance = new Shortener();
        }
        return instance;
    }

    private static final Map<String, String> shortNames = new HashMap<>();
    private static final Map<String, String> talentAbkuerzungen = new HashMap<>();
    private static final Map<String, String> talentAbkuerzungen2 = new HashMap<>();

    static {
        shortNames.put("Mut", "MU");
        shortNames.put("Klugheit", "KL");
        shortNames.put("Intuition", "IN");
        shortNames.put("Charisma", "CH");
        shortNames.put("Fingerfertigkeit", "FF");
        shortNames.put("Gewandtheit", "GE");
        shortNames.put("Konstitution", "KO");
        shortNames.put("Körperkraft", "KK");
        shortNames.put("Eigenschaft", "**");
        shortNames.put("Sozialstatus", "SO");
        shortNames.put("Magieresistenz", "MR");
        shortNames.put("Geschwindigkeit", "GS");
        shortNames.put("Initiative", "INI");
        shortNames.put("Attacke", "AT");
        shortNames.put("Parade", "PA");
        shortNames.put("Fernkampf-Basis", "FK");
        shortNames.put("Loyalität", "LO");
        shortNames.put("Gefahrenwert", "GW");

        shortNames.put("Lebensenergie", "LeP");
        shortNames.put("Ausdauer", "AuP");
        shortNames.put("Astralenergie", "AsP");
        shortNames.put("Astralenergie-Regeneration", "Reg");
        shortNames.put("Karmaenergie", "KaP");

        shortNames.put("bauch", "Ba");
        shortNames.put("behinderung", "BE");
        shortNames.put("brust", "Br");
        shortNames.put("gesamt", "Ges");
        shortNames.put("gesamtschutz", "gRS");
        shortNames.put("gesamtzonenschutz", "gzRS");
        shortNames.put("kopf", "Ko");
        shortNames.put("linkerarm", "LA");
        shortNames.put("linkesbein", "LB");
        shortNames.put("rechterarm", "RA");
        shortNames.put("rechtesbein", "RB");
        shortNames.put("ruecken", "Rü");

        shortNames.put("spezialisierungen", "ZS");
        shortNames.put("zauberdauer", "ZD");
        shortNames.put("wirkungsdauer", "WD");
        shortNames.put("reichweite", "RW");
        shortNames.put("kosten", "Asp");
        shortNames.put("anmerkung", "");
        shortNames.put("kontrollwert", "K");
        shortNames.put("dauer", "RD");
        shortNames.put("wirkung", "");
        shortNames.put("kommentar", "");
        shortNames.put("custom", "");

        shortNames.put("Achaz", "Ach");
        shortNames.put("Borbaradianer", "Bor");
        shortNames.put("Druide", "Dru");
        shortNames.put("Elf", "Elf");
        shortNames.put("Geode", "Geo");
        shortNames.put("Hexe", "Hex");
        shortNames.put("Magier", "Mag");
        shortNames.put("Scharlatan", "Srl");
        shortNames.put("Schelm", "Sch");
        shortNames.put("Magiedilletant", "Dil");
        shortNames.put("Alhanisch", "Alh");
        shortNames.put("Druidisch-Geodisch", "D/G");
        shortNames.put("Güldenländisch", "Gül");
        shortNames.put("Grolmisch", "Gro");
        shortNames.put("Kophtanisch", "Kop");
        shortNames.put("Mudramulisch", "Mud");
        shortNames.put("Satuarisch", "Sat");
        shortNames.put("Optimatik", "Opt");
        shortNames.put("Aygon", "Ayg");
        shortNames.put("BaLoa", "BaL");
        shortNames.put("Bashide", "Bas");
        shortNames.put("Geisterkalb", "Gek");
        shortNames.put("Geisterwerker", "Gew");
        shortNames.put("G'Rolmur", "G'R");
        shortNames.put("Hornherren", "Hor");
        shortNames.put("Icemna", "Icm");
        shortNames.put("Kentori", "Ken");
        shortNames.put("Leonir", "Leo");
        shortNames.put("Maritim", "Mar");
        shortNames.put("Medizinleute", "Med");
        shortNames.put("Monddeuter", "Mon");
        shortNames.put("natürlich", "nat");
        shortNames.put("Neristu", "Ner");
        shortNames.put("Saithakenner", "Sai");
        shortNames.put("Satudur", "Sad");
        shortNames.put("Shanwada", "Sha");
        shortNames.put("Sherkumar", "She");
        shortNames.put("Shindramatha", "Shi");
        shortNames.put("Shurhokach", "Shu");
        shortNames.put("Sibyllen", "Sib");
        shortNames.put("Sternensänger", "Ssg");
        shortNames.put("Weihertänzer", "Wei");
        shortNames.put("Windflüsterer", "Win");
        shortNames.put("Wolfalben", "Wol");
        shortNames.put("Yachzuq", "Yac");
        shortNames.put("Zaubertänzer", "Zau");
        shortNames.put("Zharzuri", "Zha");
        shortNames.put("Runenmagie", "Run");

        talentAbkuerzungen2.put("Anderthalbhänder", "1 1/2 Händer");
        talentAbkuerzungen2.put("Zweihandflegel", "2H Flegel");
        talentAbkuerzungen2.put("Zweihandhiebwaffen", "2H Hiebwaffen");
        talentAbkuerzungen2.put("Zweihandschwerter/-säbel", "2H-Schwert/-Säbel");
        talentAbkuerzungen2.put("Belagerungswaffen", "Belagerungsw.");
        talentAbkuerzungen2.put("Fesseln/Entfesseln", "Fesseln/Entf.");
        talentAbkuerzungen2.put("Feuersteinbearbeitung", "Feuersteinbearb.");
        talentAbkuerzungen2.put("Heilkunde: Gift", "Heilk.: Gift");
        talentAbkuerzungen2.put("Heilkunde: Krankheiten", "Heilk.: Krankheiten");
        talentAbkuerzungen2.put("Heilkunde: Seele", "Heilk.: Seele");
        talentAbkuerzungen2.put("Heilkunde: Wunden", "Heilk.: Wunden");
        talentAbkuerzungen2.put("Hundeschlitten fahren", "Hundeschl. fahren");
        talentAbkuerzungen2.put("Körperbeherrschung", "Körperbeherr.");
        talentAbkuerzungen2.put("Selbstbeherrschung", "Selbstbeherr.");
        talentAbkuerzungen2.put("Menschenkenntnis", "Menschenk.");
        talentAbkuerzungen.put("Liturgiekenntnis (Angrosch)", "LK (Angrosch)");
        talentAbkuerzungen.put("Liturgiekenntnis (Aves)", "LK (Aves)");
        talentAbkuerzungen.put("Liturgiekenntnis (Boron)", "LK (Boron)");
        talentAbkuerzungen.put("Liturgiekenntnis (Efferd)", "LK (Efferd)");
        talentAbkuerzungen.put("Liturgiekenntnis (Firun)", "LK (Firun)");
        talentAbkuerzungen.put("Liturgiekenntnis (Gravesh)", "LK (Gravesh)");
        talentAbkuerzungen.put("Liturgiekenntnis (H'Ranga)", "LK (H'Ranga)");
        talentAbkuerzungen.put("Liturgiekenntnis (Hesinde)", "LK (Hesinde)");
        talentAbkuerzungen.put("Liturgiekenntnis (Ifirn)", "LK (Ifirn)");
        talentAbkuerzungen.put("Liturgiekenntnis (Ingerimm)", "LK (Ingerimm)");
        talentAbkuerzungen.put("Liturgiekenntnis (Nandus)", "LK (Nandus)");
        talentAbkuerzungen.put("Liturgiekenntnis (Peraine)", "LK (Peraine)");
        talentAbkuerzungen.put("Liturgiekenntnis (Phex)", "LK (Phex)");
        talentAbkuerzungen.put("Liturgiekenntnis (Praios)", "LK (Praios)");
        talentAbkuerzungen.put("Liturgiekenntnis (Rahja)", "LK (Rahja)");
        talentAbkuerzungen.put("Liturgiekenntnis (Rondra)", "LK (Rondra)");
        talentAbkuerzungen.put("Liturgiekenntnis (Swafnir)", "LK (Swafnir)");
        talentAbkuerzungen.put("Liturgiekenntnis (Travia)", "LK (Travia)");
        talentAbkuerzungen.put("Liturgiekenntnis (Tsa)", "LK (Tsa)");
        talentAbkuerzungen.put("Ritualfertigkeit Geister aufnehmen", "RF Geister aufnehmen");
        talentAbkuerzungen.put("Ritualfertigkeit Geister bannen", "RF Geister bannen");
        talentAbkuerzungen.put("Ritualfertigkeit Geister binden", "RF Geister binden");
        talentAbkuerzungen.put("Ritualfertigkeit Geister rufen", "RF Geister rufen");
        talentAbkuerzungen.put("Ritualkenntnis: Alchimist", "RK: Alchimist");
        talentAbkuerzungen.put("Ritualkenntnis: Derwisch", "RK: Derwisch");
        talentAbkuerzungen.put("Ritualkenntnis: Druide", "RK: Druide");
        talentAbkuerzungen.put("Ritualkenntnis: Durro-Dûn", "RK: Durro-Dûn");
        talentAbkuerzungen.put("Ritualkenntnis: Geode", "RK: Geode");
        talentAbkuerzungen.put("Ritualkenntnis: Gildenmagie", "RK: Gildenmagie");
        talentAbkuerzungen.put("Ritualkenntnis: Hexe", "RK: Hexe");
        talentAbkuerzungen.put("Ritualkenntnis: Kristallomantie", "RK: Kristallomantie");
        talentAbkuerzungen.put("Ritualkenntnis: Scharlatan", "RK: Scharlatan");
        talentAbkuerzungen.put("Ritualkenntnis: Zaubertänzer", "RK: Zaubertänzer");
        talentAbkuerzungen.put("Ritualkenntnis: Zibilja", "RK: Zibilja");
        talentAbkuerzungen2.put("Sagen und Legenden", "Sagen/Legenden");
        talentAbkuerzungen2.put("Schriftlicher Ausdruck", "Schrift. Ausdruck");
        talentAbkuerzungen2.put("Steinschneider/Juwelier", "Steinschneider");
        talentAbkuerzungen.put("Memorans Gedächtniskraft", "Memorans");
        talentAbkuerzungen.put("Menetekel Flammenschrift", "Menetekel");
        talentAbkuerzungen.put("Metamagie neutralisieren", "Metamagie neutral.");
        talentAbkuerzungen.put("Metamorpho Gletscherform", "Metamorpho");
        talentAbkuerzungen.put("Metamorpho Felsenform", "Metamorpho Fels");
        talentAbkuerzungen.put("Movimento Dauerlauf", "Movimento");
        talentAbkuerzungen.put("Nebelwand und Morgendunst", "Nebelwand");
        talentAbkuerzungen.put("Nekropathia Seelenreise", "Nekropathia");
        talentAbkuerzungen.put("Nihilogravo Schwerelos", "Nihilogravo");
        talentAbkuerzungen.put("Nuntiovolo Botenvogel", "Nuntiovolo");
        talentAbkuerzungen.put("Orcanofaxius Luftstrahl", "Orkanofaxius");
        talentAbkuerzungen.put("Orkanosphaero Orkanball", "Orkanosphaero");
        talentAbkuerzungen.put("Panik überkomme euch!", "Panik überkomme..");
        talentAbkuerzungen.put("Paralysis starr wie Stein", "Paralysis");
        talentAbkuerzungen.put("Pectetondo Zauberhaar", "Pectetondo");
        talentAbkuerzungen.put("Penetrizzel Tiefenblick", "Penetrizzel");
        talentAbkuerzungen.put("Pentagramma Sphärenbann", "Pentagramma");
        talentAbkuerzungen.put("Planastrale Anderswelt", "Planastrale");
        talentAbkuerzungen.put("Plumbumbarum schwerer Arm", "Plumbumbarum");
        talentAbkuerzungen.put("Projektimago Ebenbild", "Projektimago");
        talentAbkuerzungen.put("Protectionis Kontrabann", "Protectionis");
        talentAbkuerzungen.put("Reflectimago Spiegelschein", "Reflectimago");
        talentAbkuerzungen.put("Reptilea Natternest", "Reptilea");
        talentAbkuerzungen.put("Reversalis Revidum", "Reversalis");
        talentAbkuerzungen.put("Ruhe Körper, ruhe Geist", "Ruhe Körper");
        talentAbkuerzungen.put("Salander Mutander", "Salander");
        talentAbkuerzungen.put("Sapefacta Zauberschwamm", "Sapefacta");
        talentAbkuerzungen.put("Schadenszauber bannen", "Schadensz. bann.");
        talentAbkuerzungen.put("Schleier der Unwissenheit", "Schleier");
        talentAbkuerzungen.put("Seidenweich Schuppengleich", "Seidenweich");
        talentAbkuerzungen.put("Seidenzunge Elfenwort", "Seidenzunge");
        talentAbkuerzungen.put("Sensattacco Meisterstreich", "Sensattacco");
        talentAbkuerzungen.put("Sensibar Empathicus", "Sensibar");
        talentAbkuerzungen.put("Serpentialis Schlangenleib", "Serpentialis");
        talentAbkuerzungen.put("Sinesigil unerkannt", "Sinesigil");
        talentAbkuerzungen.put("Solidirid Weg aus Licht", "Solidirid");
        talentAbkuerzungen.put("Somnigravis tiefer Schlaf", "Somnigravis");
        talentAbkuerzungen.put("Standfest Katzengleich", "Standfest");
        talentAbkuerzungen.put("Tlalucs Odem Pestgestank", "Tlalucs Odem");
        talentAbkuerzungen.put("Transformatio Formgestalt", "Transformatio");
        talentAbkuerzungen.put("Transmutare Körperform", "Transmutare");
        talentAbkuerzungen.put("Transversalis Teleport", "Transversalis");
        talentAbkuerzungen.put("Unberührt von Satinav", "Unberührt");
        talentAbkuerzungen.put("Unitatio Geistesbund", "Unitatio");
        talentAbkuerzungen.put("Veränderung aufheben", "Veränd. aufheben");
        talentAbkuerzungen.put("Verständigung stören", "Verständ. stören");
        talentAbkuerzungen.put("Verwandlung beenden", "Verwand. beenden");
        talentAbkuerzungen.put("Vocolimbo hohler Klang", "Vocolimbo");
        talentAbkuerzungen.put("Vogelzwitschern Glockenspiel", "Vogelzwitschern");
        talentAbkuerzungen.put("Weihrauchwolke Wohlgeruch", "Weihrauchwolke");
        talentAbkuerzungen.put("Weiße Mähn und goldener Huf", "Weiße Mähn");
        talentAbkuerzungen.put("Widerwille Ungemach", "Widerwille");
        talentAbkuerzungen.put("Xenographus Schriftenkunde", "Xenographus");
        talentAbkuerzungen.put("Zauberklinge Geisterspeer", "Zauberklinge");
        talentAbkuerzungen.put("Zaubernahrung Hungerbann", "Zaubernahrung");
        talentAbkuerzungen.put("Zauberwesen der Natur", "Zauberwesen");
    }

    public String rasse(String rasse) {
        rasse = rasse
            .replace("Halbelfe: Halbelfe, Halbelfe", "Halbelfe")
            .replace("Halbelf: Halbelf, Halbelf", "Halbelf")
            .replace("Halbelfe: Halbelfe", "Halbelfe")
            .replace("Halbelf: Halbelf", "Halbelf")
            .replaceFirst("(Halbelfe? .*?) Abstammung, Halbelfe? (.*? Abstammung)", "$1/$2")
            .replace(", in menschlicher Kultur aufgewachsen", "")
            .replace(", in elfischer Kultur aufgewachsen", "")
            .replace("keine Variante", "");
        if (rasse.startsWith("Achaz/") && rasse.endsWith("Achaz")) {
            rasse = rasse.substring(6);
        }
        return rasse;
    }

    public String kultur(String kultur) {
        kultur = kultur
            .replace(": keine Spezialisierung", "")
            .replace("Städte/Hafenstädte und Städte an großen Flüssen", "Hafen- und Fluss-Städte")
            .replace("Flüchtlinge aus borbaradianisch besetzten Städten", "Flüchtling")
            .replace("Städte mit wichtigem Tempel/Pilgerstätte", "Pilgerstätte")
            .replace("Maraskan/Maraskanische Städte", "Maraskan/Städte")
            .replace("Küstengebiete oder an großen Flüssen", "Küsten/Flussgebiete")
            .replace("An einer wichtigen Handelsroute/Reichsstraße", "Handelsroute/Reichsstraße");
        if (kultur.endsWith(")") && kultur.contains("(")) {
            kultur = kultur.substring(0, kultur.indexOf("(")).trim();
        }
        return kultur;
    }

    public String profession(String profession) {
        profession = profession.replace("Magische Weiterbildung: ", "");
        profession = profession.replace("Akademie für Beschwörung und gem. Magie im Heer zu Mendena", "Akademie für Beschwörung zu Mendena");
        profession = profession.replace("Die Rondragefällige und Theaterritterliche Kriegerschule der Bornländischen Lande zu Neersand", "Kriegerschule zu Neersand");
        profession = profession.replace("Fakultät der Alchimie der Herzog-Eolan-Universität zu Methumis", "Fakultät der Alchimie zu Methumis");

        // "Taugenichts/Taugenichts"
        String[] parts = profession.split("/", 2);
        if (parts.length > 1 && parts[0].equals(parts[1])) {
            profession = parts[0];
        }

        if (profession.length() > 75) {
            profession = profession2(profession);
        }
        return profession;
    }

    private String profession2(String profession) {
        profession = profession.replace("Akademie der geistigen Kraft zu Fasar", "Akademie zu Fasar");
        profession = profession.replace("Akademie für Beschwörung zu Mendena", "Akademie zu Mendena");
        profession = profession.replace("Alchimistische Fakultät der Universität von Al'Anfa", "Alchimistische Fakultät von Al'Anfa");
        profession = profession.replace("Arcanes Institut Punin", "Punin");
        profession = profession.replace("Chamib al'Chimie der Drachenei-Akademie zu Khunchom", "Chamib al'Chimie zu Khunchom");
        profession = profession.replace("Institut der Arkanen Analysen zu Kuslik", "IAA Kuslik");
        profession = profession.replace("Jünger der ", "").replace("Jünger des ", "");
        profession = profession.replace("Konzil der Elemente zu Drakonia", "Drakonia");
        profession = profession.replace("Orden des schwarzen Raben (Rabengarde)", "Rabengarde");
        profession = profession.replace("Pentagramm-Akademie zu Rashdul", "Rashdul");
        profession = profession.replace("Zinnober-Laboratorien der Halle des Quecksilbers zu Festum", "Zinnober-Laboratorien zu Festum");
        return profession;
    }

    public String vorteil(String name) {
        name = name
            .replace("[Talent]", "")
            .replace("[Talentgruppe]", "")
            .replace("[Zauber]", "")
            .replace("[Merkmal]", "")
            .replace("[Element]", "")
            .replace("[Dienst]", "")
            .replace("[Quelle/Kategorie/Sphäre]", "")
            .replaceAll("\\d GP/Stufe; ", "");
        return name;
    }

    public String sf(String name) {
        name = name
            .replace("zauber:", ":")
            .replace("Zauberzeichen:", "Zeichen:")
            .replace("Hexenfluch:", "Fluch:")
            .replace("Elfenlied:", "Lied:")
            .replace("Druidisches Dolchritual:", "Dolch:")
            .replace("Druidisches Herrschaftsritual:", "Herrschaft:")
            .replace("Zeichen: Bann- und Schutzkreis gegen", "Bann/Schutzkreis")
            .replace("Zeichen: Bannkreis gegen", "Bannkreis")
            .replace("Zeichen: Schutzkreis gegen", "Schutzkreis")
            .replace("Waffenloser Kampfstil", "Kampfstil");
        if (name.startsWith("Waffenmeister")) {
            name = waffenmeister(name);
        }
        return name;
    }

    private String waffenmeister(String name) {
        // "Waffenmeister (Schlagring; Raufen; Talent: C; Erlaubtes Manöver: Gegenhalten; Probenerleichterung: Gegenhalten; -1; AT: 1)"
        name = name
            .replace("Erlaubtes Manöver", "Erlaubt")
            .replace("Probenerleichterung", "Erleichterung");

        ArrayList<String> newParts = new ArrayList<>();
        String[] parts = name.split("; ");
        for (int i = 0; i < parts.length; i++) {
            if (i == 1) continue; // "Raufen"
            if (parts[i].startsWith("Talent:")) continue;
            newParts.add(parts[i]);
        }
        return String.join("; ", newParts);
    }

    public String eigenschaft(String eigenschaft) {
        String s = shortNames.get(eigenschaft);
        if (s == null) {
            if (eigenschaft.length() > 2)
                System.out.println("[WARN] Eigenschaft '" + eigenschaft + "' is unknown");
            return eigenschaft;
        }
        return s;
    }

    public String wert(String wert) {
        String s = shortNames.get(wert);
        if (s == null) {
            System.out.println("[WARN] Wert '" + wert + "' is unknown");
            return wert;
        }
        return s;
    }

    public String repraesentation(String repr) {
        return shortNames.getOrDefault(repr, repr);
    }

    public String talent(String talent) {
        talent = talent.replace("Sprachen kennen ", "")
            .replace("Lesen/Schreiben ", "")
            .replace("L/S ", "")
            .replace("Schwarze Gabe: ", "")
            .trim();
        return talentAbkuerzungen.getOrDefault(talent, talent);
    }

    public String talentStrong(String talent) {
        talent = talent.replace("Sprachen kennen ", "")
            .replace("Lesen/Schreiben ", "")
            .replace("L/S ", "")
            .replace("Schwarze Gabe: ", "")
            .trim();
        return talentAbkuerzungen2.getOrDefault(talent, talentAbkuerzungen.getOrDefault(talent, talent));
    }

    public String zauber(String zauber) {
        zauber = zauber.replace("[Semipermanenz]", "[Semi]");
        zauber = zauber.replace("Wolfsgestalt [", "[");
        return zauber;
    }

    public String zauber2(String z) {
        z = zauber(z);
        return talentAbkuerzungen.getOrDefault(z, z);
    }
}
