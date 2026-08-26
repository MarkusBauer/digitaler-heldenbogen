package de.mb.heldenbogen;

import de.mb.heldenbogen.models.*;
import helden.framework.held.persistenz.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HeldExtractor {
    private static Element getFirstElementByTagName(Element element, String tagName) {
        NodeList nodes = element.getElementsByTagName(tagName);
        return nodes.getLength() > 0 ? (Element) nodes.item(0) : null;
    }

    private static String getTextContentByTagName(Element element, String tagName) {
        Element e = getFirstElementByTagName(element, tagName);
        return e != null ? e.getTextContent() : "";
    }

    private static String getTextContentByTagName(Element element, String tagName, String defaultValue) {
        String x = getTextContentByTagName(element, tagName);
        return x.isEmpty() ? defaultValue : x;
    }

    private static String[] getTextArrayFromMultiline(Element element) {
        ArrayList<String> tmp = new ArrayList<>();
        NodeList nodes = element.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            if (!(nodes.item(i) instanceof Element)) continue;
            Element e = (Element) nodes.item(i);
            if (!e.getTagName().equals("text")) {
                tmp.add(e.getTextContent());
            }
        }
        return tmp.toArray(new String[0]);
    }

    private HeldExtractor() {
    }

    public static Held parseHeldDaten(Document xml) {
        Held held = new Held();
        Element angaben = getFirstElementByTagName(xml.getDocumentElement(), "angaben");
        held.angaben = parseAngaben(angaben);
        Element apElement = getFirstElementByTagName(angaben, "ap");
        held.ap.put("gesamt", Integer.parseInt(getTextContentByTagName(apElement, "gesamt")));
        held.ap.put("frei", Integer.parseInt(getTextContentByTagName(apElement, "frei")));
        held.ap.put("genutzt", Integer.parseInt(getTextContentByTagName(apElement, "genutzt")));
        Element gpElement = getFirstElementByTagName(angaben, "gp");
        held.gp.put("start", Integer.parseInt(getTextContentByTagName(gpElement, "start")));
        held.gp.put("rest", Integer.parseInt(getTextContentByTagName(gpElement, "rest")));
        held.aspregeneration = getTextContentByTagName(angaben, "aspregeneration");
        held.leregeneration = getTextContentByTagName(angaben, "leregeneration");
        held.wundschwelle = Integer.parseInt(getTextContentByTagName(angaben, "wundschwelle"));
        NodeList kampfSetNodes = xml.getDocumentElement().getElementsByTagName("kampfset");
        for (int i = 0; i < kampfSetNodes.getLength(); i++) {
            held.kampfSets.add(parseKampfSet((Element) kampfSetNodes.item(i)));
        }
        held.kampfset = held.kampfSets.stream().filter(ks -> ks.inBenutzung && ks.trefferzonenModell).findFirst()
            .orElse(held.kampfSets.stream().filter(ks -> ks.trefferzonenModell).findFirst().orElse(new KampfSet()));
        NodeList ereignisse = getFirstElementByTagName(xml.getDocumentElement(), "ereignisse").getElementsByTagName("date");
        held.lastEreignisDate = ereignisse.item(ereignisse.getLength() - 1).getTextContent();

        NodeList talentNodes = getFirstElementByTagName(xml.getDocumentElement(), "talentliste").getElementsByTagName("talent");
        for (int i = 0; i < talentNodes.getLength(); i++) {
            held.addTalent(parseTalent((Element) talentNodes.item(i)));
        }

        NodeList vorteilNodes = getFirstElementByTagName(xml.getDocumentElement(), "vorteile").getElementsByTagName("vorteil");
        for (int i = 0; i < vorteilNodes.getLength(); i++) {
            held.addVorNachteil(parseVorNachteil((Element) vorteilNodes.item(i)));
        }

        NodeList zauberNodes = getFirstElementByTagName(xml.getDocumentElement(), "zauberliste").getElementsByTagName("zauber");
        for (int i = 0; i < zauberNodes.getLength(); i++) {
            held.zauber.add(parseZauber((Element) zauberNodes.item(i)));
        }

        NodeList sfNodes = getFirstElementByTagName(xml.getDocumentElement(), "sonderfertigkeiten").getElementsByTagName("sonderfertigkeit");
        for (int i = 0; i < sfNodes.getLength(); i++) {
            held.sonderfertigkeiten.add(parseSonderfertigkeit((Element) sfNodes.item(i)));
        }

        NodeList gegenstaende = xml.getDocumentElement().getElementsByTagName("gegenstand");
        for (int i = 0; i < gegenstaende.getLength(); i++) {
            Element g = (Element) gegenstaende.item(i);
            if (getTextContentByTagName(g, "arten", "").equals("Wesen")) {
                held.wesen.add(parseWesen(g));
            }
        }

        parseEigenschaften(held.eigenschaften, getFirstElementByTagName(xml.getDocumentElement(), "eigenschaften"));

        held.complete();
        return held;
    }

    public static void dumpDocument(Document doc, File output) {
        try {
            Transformer transformer = XMLUtils.createSecureTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(output);
            transformer.transform(source, result);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected static void dumpElement(Element elem) {
        try {
            Transformer transformer = XMLUtils.createSecureTransformer();
            DOMSource source = new DOMSource(elem);
            StreamResult result = new StreamResult(System.out);
            transformer.transform(source, result);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Angaben parseAngaben(Element element) {
        /*
        <angaben>
            <augenfarbe>bernsteinfarben</augenfarbe>
            <geburtstag>18. Peraine 993 BF</geburtstag>
            <alter>38</alter>
            <geschlecht>weiblich</geschlecht>
            <groesse>190</groesse>
            <gewicht>70</gewicht>
            <haarfarbe>rotblond</haarfarbe>
            <stand/>
            <titel/>
            <name>...</name>
         */
        Angaben angaben = new Angaben();
        angaben.augenfarbe = getTextContentByTagName(element, "augenfarbe");
        angaben.geburtstag = getTextContentByTagName(element, "geburtstag");
        angaben.alter = Integer.parseInt(getTextContentByTagName(element, "alter", "0"));
        angaben.geschlecht = getTextContentByTagName(element, "geschlecht");
        angaben.groesse = Integer.parseInt(getTextContentByTagName(element, "groesse", "0"));
        angaben.gewicht = Integer.parseInt(getTextContentByTagName(element, "gewicht", "0"));
        angaben.haarfarbe = getTextContentByTagName(element, "haarfarbe");
        angaben.stand = getTextContentByTagName(element, "stand");
        angaben.titel = getTextContentByTagName(element, "titel");
        angaben.name = getTextContentByTagName(element, "name");
        angaben.bildPfad = getTextContentByTagName(element, "bildPfad");
        angaben.gilde = getTextContentByTagName(element, "gilde");
        angaben.rasse = getTextContentByTagName(element, "rasse");
        angaben.kultur = getTextContentByTagName(element, "kultur");
        Element professionElement = getFirstElementByTagName(element, "profession");
        angaben.profession = new Profession(
            getTextContentByTagName(professionElement, "text"),
            getTextContentByTagName(professionElement, "textkurz"),
            getTextContentByTagName(professionElement, "tarnidentitaet")
        );
        angaben.aussehen = getTextArrayFromMultiline(getFirstElementByTagName(element, "aussehen"));
        angaben.familie = getTextArrayFromMultiline(getFirstElementByTagName(element, "familie"));
        angaben.notizen = getTextArrayFromMultiline(getFirstElementByTagName(element, "notizen"));

        return angaben;
    }


    public static void parseEigenschaften(Map<String, Integer> eigenschaften, Element element) {
        /*
        <geschwindigkeit>
          <akt>9</akt>
          <start>9</start>
          <modi>0</modi>
          <name>Geschwindigkeit</name>
          <bereich>sonst</bereich>
        </geschwindigkeit>
         */
        NodeList nodes = element.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            if (nodes.item(i) instanceof Element) {
                Element e = (Element) nodes.item(i);
                int value = Integer.parseInt(getTextContentByTagName(e, "akt", "0"));
                eigenschaften.put(e.getTagName(), value);
                eigenschaften.put(getTextContentByTagName(e, "name"), value);
            }
        }
    }

    public static KampfSet parseKampfSet(Element element) {
        KampfSet ks = new KampfSet();
        ks.trefferzonenModell = Boolean.parseBoolean(element.getAttribute("tzm"));
        ks.inBenutzung = Boolean.parseBoolean(element.getAttribute("inbenutzung"));
        ks.ini = Integer.parseInt(getTextContentByTagName(element, "ini"));
        ks.ausweichen = Integer.parseInt(getTextContentByTagName(element, "ausweichen"));
        ks.geschwindigkeitinklbe = Integer.parseInt(getTextContentByTagName(element, "geschwindigkeitinklbe"));

        ks.raufen = new HashMap<>();
        ks.ringen = new HashMap<>();
        ks.ruestungsZonen = new HashMap<>();

        Element raufenElement = getFirstElementByTagName(element, "raufen");
        NodeList raufenNodes = raufenElement.getChildNodes();
        for (int i = 0; i < raufenNodes.getLength(); i++) {
            if (raufenNodes.item(i) instanceof Element) {
                Element e = (Element) raufenNodes.item(i);
                ks.raufen.put(e.getTagName(), e.getTextContent());
            }
        }

        Element ringenElement = getFirstElementByTagName(element, "ringen");
        NodeList ringenNodes = ringenElement.getChildNodes();
        for (int i = 0; i < ringenNodes.getLength(); i++) {
            if (ringenNodes.item(i) instanceof Element) {
                Element e = (Element) ringenNodes.item(i);
                ks.ringen.put(e.getTagName(), e.getTextContent());
            }
        }

        Element ruestungsZonenElement = getFirstElementByTagName(element, "ruestungzonen");
        if (ruestungsZonenElement != null) {
            NodeList zonenNodes = ruestungsZonenElement.getChildNodes();
            for (int i = 0; i < zonenNodes.getLength(); i++) {
                if (zonenNodes.item(i) instanceof Element) {
                    Element e = (Element) zonenNodes.item(i);
                    ks.ruestungsZonen.put(e.getTagName(), e.getTextContent());
                }
            }
        }

        ks.nahkampfWaffen = new ArrayList<>();
        NodeList nahkampfNodes = element.getElementsByTagName("nahkampfwaffe");
        for (int i = 0; i < nahkampfNodes.getLength(); i++) {
            ks.nahkampfWaffen.add(parseNahkampfWaffe((Element) nahkampfNodes.item(i)));
        }

        ks.fernkampfWaffen = new ArrayList<>();
        NodeList fernkampfNodes = element.getElementsByTagName("fernkampfwaffe");
        for (int i = 0; i < fernkampfNodes.getLength(); i++) {
            ks.fernkampfWaffen.add(parseFernkampfWaffe((Element) fernkampfNodes.item(i)));
        }

        ks.schilder = new ArrayList<>();
        NodeList schildNodes = element.getElementsByTagName("schild");
        for (int i = 0; i < schildNodes.getLength(); i++) {
            ks.schilder.add(parseSchild((Element) schildNodes.item(i)));
        }

        return ks;
    }


    public static NahkampfWaffe parseNahkampfWaffe(Element element) {
            /*
            <nahkampfwaffe>
                    <nummer>1</nummer>
                    <möglich>true</möglich>
                    <bh>false</bh>
                    <name>Streitaxt</name>
                    <spalte2>Hi/BE-4</spalte2>
                    <dk>N</dk>
                    <tp>1W+4</tp>
                    <tpkk schrittweite="2" schwelle="13">13 / 2</tpkk>
                    <ini>0</ini>
                    <wm>0 /-1</wm>
                    <at>12</at>
                    <pa>10</pa>
                    <tpinkl>1W+4</tpinkl>
                    <bereich>nahkampf</bereich>
                    <bfmin>2</bfmin>
                    <bfakt>2</bfakt>
                    <waffentalentkurz>Hi</waffentalentkurz>
                    <waffentalent>Hiebwaffen</waffentalent>
                    <be>BE-4</be>
                </nahkampfwaffe>
             */
        NahkampfWaffe nkw = new NahkampfWaffe();
        nkw.moeglich = Boolean.parseBoolean(getTextContentByTagName(element, "möglich"));
        nkw.schildnummer = Integer.parseInt(getTextContentByTagName(element, "schildnummer", "0")) - 1;
        nkw.bh = Boolean.parseBoolean(getTextContentByTagName(element, "bh"));
        nkw.name = getTextContentByTagName(element, "name");
        nkw.spalte2 = getTextContentByTagName(element, "spalte2");
        nkw.dk = getTextContentByTagName(element, "dk");
        nkw.tp = getTextContentByTagName(element, "tp");
        nkw.tpkk = getTextContentByTagName(element, "tpkk");
        nkw.tpinkl = getTextContentByTagName(element, "tpinkl");
        nkw.ini = getTextContentByTagName(element, "ini");
        nkw.wm = getTextContentByTagName(element, "wm");
        nkw.at = Integer.parseInt(getTextContentByTagName(element, "at", "-9"));
        nkw.pa = Integer.parseInt(getTextContentByTagName(element, "pa", "-9"));
        nkw.bfmin = Integer.parseInt(getTextContentByTagName(element, "bfmin"));
        nkw.bfakt = Integer.parseInt(getTextContentByTagName(element, "bfakt"));
        nkw.waffentalent = getTextContentByTagName(element, "waffentalent");
        nkw.waffentalentKurz = getTextContentByTagName(element, "waffentalentkurz");
        nkw.be = getTextContentByTagName(element, "be");
        return nkw;
    }

    public static FernkampfWaffe parseFernkampfWaffe(Element element) {
        FernkampfWaffe fkw = new FernkampfWaffe();
        fkw.name = getTextContentByTagName(element, "name");
        fkw.spalte2 = getTextContentByTagName(element, "spalte2");
        fkw.reichweite = getTextContentByTagName(element, "reichweite");
        fkw.tp = getTextContentByTagName(element, "tp");
        fkw.tpmod = getTextContentByTagName(element, "tpmod");
        fkw.at = Integer.parseInt(getTextContentByTagName(element, "at"));
        fkw.ladezeit = Integer.parseInt(getTextContentByTagName(element, "ladezeit"));
        fkw.kampftalent = getTextContentByTagName(element, "kampftalent");
        return fkw;
    }

    public static Schild parseSchild(Element element) {
            /*
            <schild>
                <nummer>1</nummer>
                <name>Lederschild (groß)</name>
                <ini>-1</ini>
                <mod>-1/ 4</mod>
                <at>0</at>
                <pa>18</pa>
                <bfmin>6</bfmin>
                <bfakt>6</bfakt>
                <bf>6/6</bf>
                <typ>Schild</typ>
                </schild>
            </schild>
             */
        Schild schild = new Schild();
        schild.name = getTextContentByTagName(element, "name");
        schild.ini = getTextContentByTagName(element, "ini");
        schild.mod = getTextContentByTagName(element, "mod");
        schild.pa = Integer.parseInt(getTextContentByTagName(element, "pa"));
        schild.bfmin = Integer.parseInt(getTextContentByTagName(element, "bfmin"));
        schild.bfakt = Integer.parseInt(getTextContentByTagName(element, "bfakt"));
        schild.typ = getTextContentByTagName(element, "typ");
        return schild;
    }

    private static Talent parseTalent(Element element) {
        Talent talent = new Talent();
        talent.name = getTextContentByTagName(element, "name");
        talent.meisterhandwerk = Boolean.parseBoolean(getTextContentByTagName(element, "meisterhandwerk"));
        talent.leittalent = Boolean.parseBoolean(getTextContentByTagName(element, "leittalent"));
        talent.basis = Boolean.parseBoolean(getTextContentByTagName(element, "basis"));
        talent.nameausfuehrlich = getTextContentByTagName(element, "nameausfuehrlich");
        talent.wert = Integer.parseInt(getTextContentByTagName(element, "wert"));
        talent.probe = getTextContentByTagName(element, "probe");
        talent.probenwerte = getTextContentByTagName(element, "probenwerte");
        talent.nameausfuehrlichmitprobe = getTextContentByTagName(element, "nameausfuehrlichmitprobe");
        talent.behinderung = getTextContentByTagName(element, "behinderung");
        talent.mirakelplus = Boolean.parseBoolean(getTextContentByTagName(element, "mirakelplus"));
        talent.mirakelminus = Boolean.parseBoolean(getTextContentByTagName(element, "mirakelminus"));
        talent.metatalent = Boolean.parseBoolean(getTextContentByTagName(element, "metatalent"));
        talent.bereich = getTextContentByTagName(element, "bereich");
        talent.komplexitaet = getTextContentByTagName(element, "komplexität");
        talent.lernkomplexitaet = getTextContentByTagName(element, "lernkomplexität");
        talent.spezialisierungen = getTextContentByTagName(element, "spezialisierungen");
        talent.sprachkomplexitaet = getTextContentByTagName(element, "sprachkomplexität");
        talent.muttersprache = Boolean.parseBoolean(getTextContentByTagName(element, "muttersprache", "false"));
        talent.schriftmuttersprache = Boolean.parseBoolean(getTextContentByTagName(element, "schriftmuttersprache", "false"));
        talent.at = Integer.parseInt(getTextContentByTagName(element, "at", "-9"));
        talent.pa = Integer.parseInt(getTextContentByTagName(element, "pa", "-9"));
        return talent;
    }


    public static VorNachteil parseVorNachteil(Element element) {
        /*
        <vorteil>
            <bezeichner>Astrale Regeneration</bezeichner>
            <name>Astrale Regeneration: 1</name>
            <kommentar>AsP-Regeneration +1, IN-Probe -1</kommentar>
            <namemitkommentar>Astrale Regeneration: 1 (AsP-Regeneration +1, IN-Probe -1)</namemitkommentar>
            <istvorteil>true</istvorteil>
            <istnachteil>false</istnachteil>
            <wert>1</wert>
            <istschlechteeigenschaft>false</istschlechteeigenschaft>
            <bereich>Magisch</bereich>
        </vorteil>
         */

        VorNachteil v = new VorNachteil();

        v.bezeichner = getTextContentByTagName(element, "bezeichner");
        v.name = getTextContentByTagName(element, "name");
        v.kommentar = getTextContentByTagName(element, "kommentar");
        v.nameMitKommentar = getTextContentByTagName(element, "namemitkommentar");
        v.istVorteil = Boolean.parseBoolean(getTextContentByTagName(element, "istvorteil"));
        v.istNachteil = Boolean.parseBoolean(getTextContentByTagName(element, "istnachteil"));
        v.wert = Integer.parseInt(getTextContentByTagName(element, "wert", "0"));
        v.istSchlechteEigenschaft = Boolean.parseBoolean(getTextContentByTagName(element, "istschlechteeigenschaft", "false"));
        v.bereich = getTextContentByTagName(element, "bereich");

        v.complete();
        return v;
    }

    public static Zauber parseZauber(Element element) {
        /*
        <zauber>
            <name>Destructibo Arcanitas</name>
            <variante/>
            <namemitvariante>Destructibo Arcanitas</namemitvariante>
            <nameausfuehrlich>Destructibo Arcanitas</nameausfuehrlich>
            <wert>5</wert>
            <spezialisierungen/>
            <probe>KL/KL/FF</probe>
            <probenwerte>19/19/13</probenwerte>
            <bereich>Zauber</bereich>
            <komplexität>E</komplexität>
            <lernkomplexität>B</lernkomplexität>
            <hauszauber>true</hauszauber>
            <hauszauberformatiert>X</hauszauberformatiert>
            <repräsentation>Magier</repräsentation>
            <merkmale>Anti, Krft, Meta</merkmale>
            <zauberdauer>1+ min</zauberdauer>
            <kosten>speziell</kosten>
            <reichweite>B</reichweite>
            <wirkungsdauer>augenblicklich</wirkungsdauer>
            <anmerkung>Entzaubert magische Artefakte</anmerkung>
            <quelle buch="LCD" seite="66">LCD: 66</quelle>
            <kontrollwert/>
            <mr>+Mod</mr>
            <leittalent>false</leittalent>
        </zauber>
         */

        Zauber z = new Zauber(getTextContentByTagName(element, "namemitvariante"));
        z.name = getTextContentByTagName(element, "name");
        z.variante = getTextContentByTagName(element, "variante");
        z.nameAusfuehrlich = getTextContentByTagName(element, "nameausfuehrlich");
        z.wert = Integer.parseInt(getTextContentByTagName(element, "wert"));
        z.spezialisierungen = getTextContentByTagName(element, "spezialisierungen");
        z.probe = getTextContentByTagName(element, "probe");
        z.probenwerte = getTextContentByTagName(element, "probenwerte");
        z.bereich = getTextContentByTagName(element, "bereich");
        z.komplexitaet = getTextContentByTagName(element, "komplexität");
        z.lernKomplexitaet = getTextContentByTagName(element, "lernkomplexität");
        z.hauszauber = Boolean.parseBoolean(getTextContentByTagName(element, "hauszauber"));
        z.repraesentation = getTextContentByTagName(element, "repräsentation");
        z.merkmale = getTextContentByTagName(element, "merkmale");
        z.zauberdauer = getTextContentByTagName(element, "zauberdauer");
        z.kosten = getTextContentByTagName(element, "kosten");
        z.reichweite = getTextContentByTagName(element, "reichweite");
        z.wirkungsdauer = getTextContentByTagName(element, "wirkungsdauer");
        z.anmerkung = getTextContentByTagName(element, "anmerkung");
        z.quelle = getTextContentByTagName(element, "quelle");
        z.kontrollwert = getTextContentByTagName(element, "kontrollwert");
        z.mr = getTextContentByTagName(element, "mr");
        z.leittalent = Boolean.parseBoolean(getTextContentByTagName(element, "leittalent"));
        return z;
    }

    public static Sonderfertigkeit parseSonderfertigkeit(Element element) {
        /*
        <sonderfertigkeit>
            <nameausfuehrlich>Stabzauber: Zauberspeicher</nameausfuehrlich>
            <name>Stabzauber: Zauberspeicher</name>
            <bezeichner>Stabzauber: Zauberspeicher</bezeichner>
            <wirkung>Zauber auslösen</wirkung>
            <dauer>1 Akt</dauer>
            <kosten/>
            <probe>(MU/IN/KL)</probe>
            <kommentar/>
            <namemitkommentar>Stabzauber: Zauberspeicher</namemitkommentar>
            <bereich>Magisch</bereich>
            <bereich>Ritual</bereich>
        </sonderfertigkeit>
         */

        Sonderfertigkeit sf = new Sonderfertigkeit();
        sf.nameAusfuehrlich = getTextContentByTagName(element, "nameausfuehrlich");
        sf.name = getTextContentByTagName(element, "name");
        sf.bezeichner = getTextContentByTagName(element, "bezeichner");
        sf.wirkung = getTextContentByTagName(element, "wirkung");
        sf.dauer = getTextContentByTagName(element, "dauer");
        sf.kosten = getTextContentByTagName(element, "kosten");
        sf.probe = getTextContentByTagName(element, "probe");
        sf.kommentar = getTextContentByTagName(element, "kommentar");
        sf.nameMitKommentar = getTextContentByTagName(element, "namemitkommentar");
        NodeList bereichNodes = element.getElementsByTagName("bereich");
        sf.bereich = new String[bereichNodes.getLength()];
        for (int i = 0; i < bereichNodes.getLength(); i++) {
            sf.bereich[i] = bereichNodes.item(i).getTextContent();
        }

        NodeList auswahlenNodes = element.getElementsByTagName("auswahl");
        for (int i = 0; i < auswahlenNodes.getLength(); i++) {
            sf.auswahlen.add(parseSfAuswahl((Element) auswahlenNodes.item(i)));
        }

        sf.complete();
        return sf;
    }

    private static HashMap<String, String> parseSfAuswahl(Element element) {
        HashMap<String, String> result = new HashMap<>();
        NodeList nodes = element.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            if (nodes.item(i) instanceof Element) {
                Element e = (Element) nodes.item(i);
                if (e.getTagName().equals("feld")) {
                    result.put(e.getAttribute("feldname"), e.getTextContent());
                } else {
                    result.put(e.getTagName(), e.getTextContent());
                }
            }
        }
        return result;
    }


    public static Wesen parseWesen(Element element) {
        Wesen w = new Wesen();
        w.name = getTextContentByTagName(element, "name");
        w.grundlage = getTextContentByTagName(element, "grundlage");
        w.gewicht = Float.parseFloat(getTextContentByTagName(element, "gewicht", "0.0"));
        w.quelle = getTextContentByTagName(element, "quelle");

        Element details = getFirstElementByTagName(element, "details");
        Element wesen = getFirstElementByTagName(details, "wesen");

        w.gattung = getTextContentByTagName(wesen, "gattung");
        w.familie = getTextContentByTagName(wesen, "familie");
        w.groesse = Integer.parseInt(getTextContentByTagName(wesen, "groesse", "0"));

        Element eigenschaftenElement = getFirstElementByTagName(wesen, "eigenschaften");
        if (eigenschaftenElement != null) {
            NodeList eigenschaftNodes = eigenschaftenElement.getChildNodes();
            for (int i = 0; i < eigenschaftNodes.getLength(); i++) {
                if (eigenschaftNodes.item(i) instanceof Element) {
                    Element e = (Element) eigenschaftNodes.item(i);
                    w.eigenschaften.put(e.getTagName(), Integer.parseInt(e.getTextContent().split("\\.")[0]));
                }
            }
        }

        Element vorteileElement = getFirstElementByTagName(wesen, "vorteile");
        if (vorteileElement != null) {
            NodeList vorteilNodes = vorteileElement.getElementsByTagName("vorteil");
            for (int i = 0; i < vorteilNodes.getLength(); i++) {
                w.vorteile.add(parseVorNachteil((Element) vorteilNodes.item(i)));
            }
        }

        Element sonderfertigkeitenElement = getFirstElementByTagName(wesen, "sonderfertigkeiten");
        if (sonderfertigkeitenElement != null) {
            NodeList sfNodes = sonderfertigkeitenElement.getElementsByTagName("sonderfertigkeit");
            for (int i = 0; i < sfNodes.getLength(); i++) {
                w.sonderfertigkeiten.add(parseSonderfertigkeit((Element) sfNodes.item(i)));
            }
        }

        Element talenteElement = getFirstElementByTagName(wesen, "talente");
        if (talenteElement != null) {
            NodeList talentNodes = talenteElement.getElementsByTagName("talent");
            for (int i = 0; i < talentNodes.getLength(); i++) {
                w.talente.add(parseWesenTalent((Element) talentNodes.item(i)));
            }
        }

        Element iniElement = getFirstElementByTagName(wesen, "ini");
        if (iniElement != null) {
            String mul = iniElement.getAttribute("mul");
            String sum = iniElement.getAttribute("sum");
            String wuerfel = iniElement.getAttribute("w");
            w.ini = mul + "W" + wuerfel + (sum.equals("0") ? "" : ("+" + sum));
        } else {
            w.ini = "";
        }

        Element angriffeElement = getFirstElementByTagName(wesen, "angriffe");
        if (angriffeElement != null) {
            NodeList angriffNodes = angriffeElement.getElementsByTagName("angriff");
            for (int i = 0; i < angriffNodes.getLength(); i++) {
                Element angriff = (Element) angriffNodes.item(i);
                HashMap<String, String> angriffMap = new HashMap<>();
                angriffMap.put("name", angriff.getAttribute("name"));
                angriffMap.put("at", angriff.getAttribute("at"));
                angriffMap.put("pa", angriff.getAttribute("pa"));
                angriffMap.put("tp", angriff.getAttribute("tp"));
                angriffMap.put("dk", angriff.getAttribute("dk"));
                w.angriffe.add(angriffMap);
            }
        }

        return w;
    }


    public static Wesen.WesenTalent parseWesenTalent(Element element) {
        Wesen.WesenTalent wt = new Wesen.WesenTalent();
        wt.name = getTextContentByTagName(element, "name");
        wt.basis = Boolean.parseBoolean(getTextContentByTagName(element, "basis"));
        wt.probe = getTextContentByTagName(element, "probe");
        wt.wert = Integer.parseInt(getTextContentByTagName(element, "wert"));
        return wt;
    }
}
