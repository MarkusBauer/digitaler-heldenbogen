package de.mb.heldenbogen;

import de.mb.heldenbogen.models.*;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.*;
import java.util.*;

public class Renderer {
    private final Held held;
    private final String baseTemplate;
    private final boolean dev;
    private final PictureScaler pics = new PictureScaler();

    public static final Map<String, String> talentIcon = new HashMap<>();
    public static final Map<String, String> tooltips = new HashMap<>();


    static {
        tooltips.put("bauch", "Zone Bauch");
        tooltips.put("behinderung", "Rüstungs-Behinderung");
        tooltips.put("brust", "Zone Brust");
        tooltips.put("gesamt", "Rüstung Gesamt");
        tooltips.put("gesamtschutz", "Rüstung Gesamt");
        tooltips.put("gesamtzonenschutz", "Rüstung Gesamt");
        tooltips.put("kopf", "Zone Kopf");
        tooltips.put("linkerarm", "Zone linker Arm");
        tooltips.put("linkesbein", "Zone linkes Bein");
        tooltips.put("rechterarm", "Zone rechter Arm");
        tooltips.put("rechtesbein", "Zone rechtes Bein");
        tooltips.put("ruecken", "Zone Rücken");

        tooltips.put("spezialisierungen", "Zauber-Spezialisierungen");
        tooltips.put("zauberdauer", "Zauberdauer");
        tooltips.put("wirkungsdauer", "Wirkungsdauer");
        tooltips.put("reichweite", "Reichweite");
        tooltips.put("kosten", "Asp-Kosten");
        tooltips.put("anmerkung", "");
        tooltips.put("kontrollwert", "Kontrollwert");
        tooltips.put("dauer", "Ritual-Dauer");
        tooltips.put("wirkung", "");
        tooltips.put("kommentar", "");
        tooltips.put("custom", "");

        talentIcon.put("Anderthalbhänder", "sword2");
        talentIcon.put("Armbrust", "crossbow");
        talentIcon.put("Bogen", "bow");
        talentIcon.put("Dolche", "dagger");
        talentIcon.put("Fechtwaffen", "rapier");
        talentIcon.put("Hiebwaffen", "handaxe");
        talentIcon.put("Infanteriewaffen", "halbert");
        talentIcon.put("Kettenstäbe", "chain");
        talentIcon.put("Kettenwaffen", "chain");
        talentIcon.put("Paradewaffe", "parierwaffen");
        talentIcon.put("Peitsche", "whip");
        talentIcon.put("Peitschen", "whip");
        talentIcon.put("Raufen", "fist");
        talentIcon.put("Ringen", "fist");
        talentIcon.put("Rüstung", "armor");
        talentIcon.put("Schild", "shield");
        talentIcon.put("Schleuder", "sling");
        talentIcon.put("Schwerter", "sword");
        talentIcon.put("Speere", "spear");
        talentIcon.put("Stäbe", "wand");
        talentIcon.put("Säbel", "saber");
        talentIcon.put("Wurfbeile", "thrown-axe");
        talentIcon.put("Wurfmesser", "thrown-knife");
        talentIcon.put("Wurfspeere", "thrown-knife");
        talentIcon.put("Zweihandhiebwaffen", "battleaxe");
        talentIcon.put("Zweihandschwerter/-säbel", "sword2");
    }

    public Renderer(Held held) {
        this(held, "kompaktbogen", false);
    }

    public Renderer(Held held, String baseTemplate, boolean dev) {
        this.held = held;
        this.baseTemplate = baseTemplate;
        this.dev = dev;
    }

    public String render() {
        testrender();

        try {
            Configuration cfg = new Configuration(Configuration.VERSION_2_3_34);
            cfg.setClassLoaderForTemplateLoading(getClass().getClassLoader(), "/");
            DefaultObjectWrapper wrapper = new DefaultObjectWrapper(Configuration.VERSION_2_3_34);
            wrapper.setExposeFields(true);
            cfg.setObjectWrapper(wrapper);

            Template template = cfg.getTemplate("templates/" + baseTemplate + ".html.ftlh");

            addEmptyWeaponSlots();
            Map<String, Object> model = getModel();

            StringWriter writer = new StringWriter();
            template.process(model, writer);
            return writer.toString();
        } catch (IOException | TemplateException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, Object> getModel() throws IOException {
        Shortener shortener = Shortener.getInstance();
        Map<String, Object> model = new HashMap<>();
        model.put("renderer", this);
        model.put("short", shortener);
        model.put("resources", getResources());
        model.put("held", held);
        model.put("tooltips", tooltips);
        model.put("talentIcon", talentIcon);
        PictureScaler.Image portrait = pics.loadPictureWithDimensions(held.angaben.bildPfad);
        if (portrait != null)
            model.put("portrait", portrait);
        model.put("rasse", shortener.rasse(held.angaben.rasse));
        model.put("kultur", shortener.kultur(held.angaben.kultur));
        model.put("profession", getProfession());
        ArrayList<TalentGruppe> talente = TalentGruppe.getInGruppen(held.talente);
        model.put("talente", talente);
        model.put("ritualkenntnis", TalentGruppe.getRitualKenntnisse(talente));
        List<Zauber> zauber = getZauber();
        model.put("zauber", zauber);
        model.put("defaultRepraesentation", getDefaultRepraesentation(zauber));
        model.put("sonderfertigkeiten", groupSF(getSF()));
        model.put("rituale", getRituale());
        VorteileGroup vorteile = getVorteile();
        model.put("vorteile", vorteile.vorteile);
        model.put("nachteile", vorteile.nachteile);
        model.put("date", held.lastEreignisDate);
        model.put("wesen", held.wesen);
        return model;
    }

    protected Map<String, String> getResources() {
        Map<String, String> resources = new HashMap<>();
        if (dev) {
            resources.put("css", "<link href=\"../build/resources/main/heldenbogen/main.css\" rel=\"stylesheet\">");
            resources.put("js", "<script src=\"../build/resources/main/heldenbogen/main.js\"></script>");
        } else {
            try {
                resources.put("css", "<style>" + readResource("/heldenbogen/main.css") + "</style>");
                resources.put("js", "<script>" + readResource("/heldenbogen/main.js") + "</script>");
            } catch (IOException e) {
                throw new RuntimeException("Failed to load resources", e);
            }
        }
        return resources;
    }

    private String readResource(String path) throws IOException {
        StringBuilder builder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream(path))))) {
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
            }
        }
        return builder.toString();
    }

    public boolean showEigenschaft(String eigenschaft) {
        if (eigenschaft.equals("Astralenergie") || eigenschaft.equals("Karmaenergie"))
            return held.getEigenschaft(eigenschaft) > 0;
        if (eigenschaft.equals("Astralenergie-Regeneration"))
            return !held.aspregeneration.isEmpty();
        return true;
    }

    private String getProfession() {
        if (held.angaben.profession.tarnidentitaet != null && !held.angaben.profession.tarnidentitaet.isEmpty()) {
            return held.angaben.profession.tarnidentitaet;
        }
        return Shortener.getInstance().profession(held.angaben.profession.text);
    }


    private List<Zauber> getZauber() {
        return held.zauber;
    }

    private String getDefaultRepraesentation(List<Zauber> zauber) {
        Map<String, Integer> counts = new HashMap<>();
        for (Zauber z : zauber) {
            counts.merge(z.repraesentation, 1, Integer::sum);
        }
        return counts.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("");
    }

    private static class VorteileGroup {
        public final ArrayList<VorNachteil> vorteile = new ArrayList<>();
        public final ArrayList<VorNachteil> nachteile = new ArrayList<>();
    }

    private VorteileGroup getVorteile() {
        VorteileGroup result = new VorteileGroup();
        VorNachteil last = null;
        for (VorNachteil v : held.vorNachteile) {
            if (last != null && last.canJoin(v)) {
                last.join(v);
            } else if (v.isVisible()) {
                (v.istNachteil ? result.nachteile : result.vorteile).add(v);
                last = v;
            }
        }
        return result;
    }

    private ArrayList<Sonderfertigkeit> getSF() {
        ArrayList<Sonderfertigkeit> sfs = new ArrayList<>();
        Sonderfertigkeit last = null;
        for (Sonderfertigkeit sf : held.sonderfertigkeiten) {
            // Talentspezialisierung bei das passende Talent schreiben
            if (sf.istTalentspezialisierung()) {
                Talent t = held.talentByName.get(sf.getTSTalent());
                if (t != null) {
                    t.ts.add(sf);
                }
                continue;
            }
            // Stufen-SF zusammenfassen
            if (last != null && last.canJoin(sf)) {
                last.join(sf);
            } else {
                last = sf;
            }
            sfs.add(sf);
        }
        return sfs;
    }

    private ArrayList<SonderfertigkeitGruppe> groupSF(ArrayList<Sonderfertigkeit> sfs) {
        ArrayList<SonderfertigkeitGruppe> gruppen = new ArrayList<>();
        gruppen.add(new SonderfertigkeitGruppe(""));
        gruppen.add(new SonderfertigkeitGruppe("Magisch"));
        gruppen.add(new SonderfertigkeitGruppe("Karmal"));
        gruppen.add(new SonderfertigkeitGruppe("Manöver"));
        gruppen.add(new SonderfertigkeitGruppe("Waffenlos"));

        for (Sonderfertigkeit sf : sfs) {
            if (sf.istBereich("Magisch")) {
                gruppen.get(1).sfs.add(sf);
            } else if (sf.istBereich("Geweiht")) {
                gruppen.get(2).sfs.add(sf);
            } else if (sf.istBereich("Manöver") || sf.name.contains("Kampfstil:")) {
                gruppen.get(4).sfs.add(sf);
            } else if (sf.istBereich("Nahkampf") || sf.istBereich("Fernkampf")) {
                gruppen.get(3).sfs.add(sf);
            } else {
                gruppen.get(0).sfs.add(sf);
            }
        }
        return gruppen;
    }

    private ArrayList<Sonderfertigkeit> getRituale() {
        ArrayList<Sonderfertigkeit> rituale = new ArrayList<>();
        for (Sonderfertigkeit sf : held.sonderfertigkeiten) {
            if (sf.name.equals("Zauberzeichen")) continue;
            if (sf.istBereich("Ritual") || sf.istBereich("Liturgie"))
                rituale.add(sf);
            if (sf.bezeichner.equals("Formel")) {
                for (HashMap<String, String> auswahl : sf.auswahlen) {
                    Sonderfertigkeit sf2 = new Sonderfertigkeit();
                    sf2.name = auswahl.get("name");
                    sf2.nameAusfuehrlich = sf2.name;
                    sf2.bezeichner = "Formel";
                    sf2.kommentar = auswahl.getOrDefault("kommentar", "");
                    sf2.nameMitKommentar = sf2.name + " " + sf2.kommentar;
                    sf2.bereich = new String[]{"Rituale"};
                    sf2.wirkung = auswahl.getOrDefault("wirkung", "");
                    sf2.dauer = auswahl.getOrDefault("dauer", "");
                    sf2.kosten = auswahl.getOrDefault("kosten", "");
                    sf2.probe = auswahl.getOrDefault("probe", "");
                    rituale.add(sf2);
                }
            }
        }
        rituale.sort(Comparator.comparing(a -> a.name));
        return rituale;
    }

    public void addEmptyWeaponSlots() {
        if (held.kampfset.nahkampfWaffen.isEmpty()) {
            held.kampfset.nahkampfWaffen.add(new NahkampfWaffe());
        }
    }

    public String waffenName1(String name) {
        int p = name.indexOf('(');
        if (p < 0) return name;
        return name.substring(0, p).trim();
    }

    public String waffenName2(String name) {
        int p = name.indexOf('(');
        if (p < 0) return "";
        return name.substring(p).replace("(einhändig)", "").trim();
    }

    public String waffeTP(String tp) {
        return tp.replace("+0", "");
    }

    public boolean isSfGroupMagisch(SonderfertigkeitGruppe grp) {
        return grp.name.equals("Magisch") || grp.name.equals("Karmal");
    }

    public boolean hasAnySfGroupMagisch(List<SonderfertigkeitGruppe> groups) {
        for (SonderfertigkeitGruppe grp : groups) {
            if (isSfGroupMagisch(grp)) {
                for (Sonderfertigkeit sf : grp.sfs) {
                    if (sfVisibleRitualPage(sf))
                        return true;
                }
            }
        }
        return false;
    }

    public boolean sfVisibleFirstPage(Sonderfertigkeit sf) {
        return !sf.hidden && !sf.name.contains("Merkmalskenntnis") && !sf.name.startsWith("Ritualkenntnis") &&
            !sf.istRitual() && !sf.istLiturgie() && !sf.istLiturgiekenntnis() &&
            !sf.name.equals("Astrale Meditation") && !sf.name.equals("Große Meditation") && !sf.name.equals("Karmalqueste") &&
            !sf.name.startsWith("Repräsentation") && !sf.name.startsWith("Wahrer Name") && !sf.name.startsWith("Formel ");
    }

    public boolean sfVisibleRitualPage(Sonderfertigkeit sf) {
        if (sf.name.equals("Zauberzeichen")) return true;  // to me this is not a ritual
        if (sf.name.startsWith("Formel ")) return false;  // converted to Ritual later
        return !sf.hidden && !sf.name.startsWith("Ritualkenntnis") &&
            !sf.istRitual() && !sf.istLiturgie() && !sf.istLiturgiekenntnis();
    }

    public boolean isVorteilMagisch(VorNachteil vnt) {
        return vnt.bereich.equals("Magisch") || vnt.bereich.equals("Geweiht");
    }

    public String[] split(String s) {
        return s.split(" / ");
    }

    public boolean arrayHasContent(String[] strings) {
        for (String s : strings) {
            if (!s.isEmpty()) return true;
        }
        return false;
    }

    public String join(String[] strings) {
        StringBuilder result = new StringBuilder();
        boolean needSep = false;
        for (String item : strings) {
            if (item.isEmpty()) continue;
            if (needSep) result.append("; ");
            result.append(item);
            String lastChar = item.trim();
            lastChar = lastChar.substring(lastChar.length() - 1);
            needSep = (!lastChar.equals(".") && !lastChar.equals(",") && !lastChar.equals("!") && !lastChar.equals("/"));
        }
        return result.toString();
    }

    public boolean filterTrue(Object _o) {
        return true;
    }

    public void testrender() {
    }


    public static String formatProbe(String[] probe) {
        for (int i = 0; i < probe.length; i++) {
            probe[i] = Shortener.getInstance().eigenschaft(probe[i]);
        }
        return String.join("/", probe);
    }

}
