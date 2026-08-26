package de.mb.reflection;

import helden.framework.held.persistenz.BasisXMLParser;
import helden.model.profession.EigeneProfession;

import java.lang.reflect.Method;

public class EntryCreatorLite {
    private static EntryCreatorLite instance;

    public static EntryCreatorLite getInstance() {
        if (instance == null) instance = new EntryCreatorLite();
        return instance;
    }

    public Class sonderfertigkeitType;
    public Class sonderfertigkeitLiturgieType;
    public Class sonderfertigkeitListType;
    public Method sonderfertigkeitListAdd;
    public Method sonderfertigkeitListGet;

    private EntryCreatorLite() {
        sonderfertigkeitType = Helpers.getMethodByName(BasisXMLParser.class, "getSonderfertigkeit").getReturnType();
        assert sonderfertigkeitType != null;

        sonderfertigkeitListType = Helpers.getMethodByName(EigeneProfession.class, "getSonderfertigkeiten").getReturnType();

        // for reasons I don't know, the add method has a type parameter. add should be o00000
        for (Method m : sonderfertigkeitListType.getDeclaredMethods()) {
            if (m.getReturnType().equals(void.class)
                && m.getParameterTypes().length == 1 && m.getParameterTypes()[0].equals(sonderfertigkeitType)
                && m.getTypeParameters().length > 0) {
                sonderfertigkeitListAdd = m;
            } else if (m.getReturnType().equals(sonderfertigkeitType)
                && m.getParameterTypes().length == 1 && m.getParameterTypes()[0].equals(String.class)) {
                sonderfertigkeitListGet = m;
            }
        }
        assert sonderfertigkeitListAdd != null;
        assert sonderfertigkeitListGet != null;

        // Liturgie is a special subclass
        sonderfertigkeitLiturgieType = Helpers.findMethodByParameterTypes(sonderfertigkeitType, String.class, int.class, boolean.class).getReturnType();
        assert sonderfertigkeitLiturgieType != null;
    }
}
