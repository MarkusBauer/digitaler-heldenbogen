package de.mb.heldenbogen.models;

import de.mb.heldenbogen.Shortener;

public class VorNachteil {
    public String name;
    public String displayName;
    public String kommentar;
    public String bezeichner;
    public String nameMitKommentar;
    public boolean istVorteil;
    public boolean istNachteil;
    public int wert;
    public boolean istSchlechteEigenschaft;
    public String bereich;
    protected boolean hidden = false;


    public void complete() {
        this.displayName = Shortener.getInstance().vorteil(name);
    }

    public boolean isVisible() {
        return !hidden && !name.contains("Meisterhandwerk") && !name.contains("Übernatürliche Begabung");
    }

    public String getTooltip() {
        return kommentar;
    }

    public boolean canJoin(VorNachteil vnt2) {
        return (name.startsWith("Begabung für ") && vnt2.name.startsWith("Begabung für ")) ||
            (name.startsWith("Unfähigkeit für ") && vnt2.name.startsWith("Unfähigkeit für ")) ||
            (name.startsWith("Angst vor ") && vnt2.name.startsWith("Angst vor "));
    }

    public void join(VorNachteil vnt2) {
        name += ", " + vnt2.name.substring(vnt2.name.indexOf(' ', vnt2.name.indexOf(' ') + 1) + 1);
        displayName += ", " + vnt2.displayName.substring(vnt2.displayName.indexOf(' ', vnt2.displayName.indexOf(' ') + 1) + 1);
        if (vnt2.kommentar != null && !vnt2.kommentar.isEmpty()) {
            if (kommentar == null) kommentar = "";
            kommentar += (kommentar.isEmpty() ? "" : ";\n") + vnt2.kommentar;
        }
        vnt2.hidden = true;
    }

}
