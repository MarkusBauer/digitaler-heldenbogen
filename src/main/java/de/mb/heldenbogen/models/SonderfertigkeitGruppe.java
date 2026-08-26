package de.mb.heldenbogen.models;

import java.util.ArrayList;

public class SonderfertigkeitGruppe {
    public final String name;
    public final ArrayList<Sonderfertigkeit> sfs = new ArrayList<>();

    public SonderfertigkeitGruppe(String name) {
        this.name = name;
    }
}
