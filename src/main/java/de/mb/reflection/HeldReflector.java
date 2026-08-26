package de.mb.reflection;

import helden.cloudinterface.HeldenContainerImpl;
import helden.framework.held.persistenz.XMLParserKonverter;
import helden.framework.sonderfertigkeit.SFInfos;
import helden.framework.zauber.KonkreterZauber;
import helden.framework.zauber.ZauberInfos;
import helden.gui.erschaffung.werkzeug.HEW2Zauber;
import helden.model.DDZprofessionen.Bettler;
import helden.plugin.datenplugin.DatenPluginHeldenWerkzeug;
import helden.plugin.datenplugin.impl.DatenPluginHeldenWerkzeugImpl;

import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class HeldReflector {
    private static HeldReflector instance;
    private final EntryCreatorLite entryCreator = EntryCreatorLite.getInstance();

    public final Class<?> mainWindowClass;
    private final Method getMainWindowInstance;
    private final Method getCurrentHeld;
    private final Method getAlleHelden;
    private final Method getHeldByID;

    public final Class<?> heldInterfaceClass;
    public final Class<?> heldClass;
    public final DataClassWrapper heldFields;
    public final Class<?> dingMitZauberInfosClass;
    private final Method getDingMitZauberInfos;
    private final Method getKonkreteZauber;
    private final Method getZauberInfos;
    public final Class<?> heldChangesHandlerClass;
    private final Method heldChangesHandlerGetter;
    private final Method heldIsChanged;

    private final Method getSonderfertigkeitInfos;
    public final Class<?> sonderfertigkeitInfosClass;
    private final Method getSfList;
    private final Method getSfsFromList;
    private final Method liturgieSFGetGrad;

    public final Class<?> vorteilList;
    private final Method getVorteilList;
    private final Method getVorteilIteratorFromList;

    private HeldReflector() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, ParserConfigurationException {
        heldInterfaceClass = DatenPluginHeldenWerkzeugImpl.class.getConstructors()[0].getParameterTypes()[0];
        heldClass = HeldenContainerImpl.class.getMethod("getHeld").getReturnType();
        mainWindowClass = XMLParserKonverter.class.getConstructors()[0].getParameterTypes()[0];
        getMainWindowInstance = Helpers.findGetter(mainWindowClass, mainWindowClass, 0);
        getCurrentHeld = Helpers.findGetter(mainWindowClass, heldInterfaceClass, 0);
        getAlleHelden = Helpers.findGetter(mainWindowClass, ArrayList.class, 0);
        getHeldByID = Helpers.findGetter(mainWindowClass, heldInterfaceClass, 1);
        dingMitZauberInfosClass = Helpers.findDeclaredMethod(HEW2Zauber.class, HashMap.class, 1).getParameterTypes()[0];
        getDingMitZauberInfos = Helpers.findGetter(heldInterfaceClass, dingMitZauberInfosClass, 0);
        getKonkreteZauber = Helpers.findGetter(dingMitZauberInfosClass, List.class, 0);
        getZauberInfos = Helpers.findDeclaredMethod(dingMitZauberInfosClass, ZauberInfos.class, 1);
        heldFields = new DataClassWrapper(heldGetterSetter(heldClass));

        getSonderfertigkeitInfos = Arrays.stream(heldInterfaceClass.getMethods())
            .filter(m ->
                m.getParameterCount() == 1 && m.getParameterTypes()[0].equals(entryCreator.sonderfertigkeitType) &&
                    !m.getReturnType().isPrimitive() && !m.getReturnType().equals(Void.class))
            .findFirst().get();
        sonderfertigkeitInfosClass = getSonderfertigkeitInfos.getReturnType();
        getSfList = Helpers.findGetter(heldInterfaceClass, entryCreator.sonderfertigkeitListType, 0);
        getSfsFromList = Helpers.findDeclaredMethod(entryCreator.sonderfertigkeitListType, Collection.class, 0);
        liturgieSFGetGrad = Helpers.findDeclaredMethod(entryCreator.sonderfertigkeitLiturgieType.getSuperclass(), int.class, 0);

        vorteilList = Bettler.class.getMethod("getVorteile").getReturnType();
        getVorteilList = Helpers.findGetter(heldInterfaceClass, vorteilList, 0);
        getVorteilIteratorFromList = Helpers.findGetter(vorteilList, Iterator.class, 0);

        heldChangesHandlerClass = findHeldenChangesHandler(heldInterfaceClass);
        heldChangesHandlerGetter = Helpers.findGetter(heldInterfaceClass, heldChangesHandlerClass, 0);
        heldIsChanged = Helpers.findGetter(heldChangesHandlerClass, boolean.class, 0);
    }

    private HashMap<String, GetterSetter> heldGetterSetter(Class<?> heldClass) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        ArrayList<GetterSetter> gs = GetterSetter.fromClass(heldClass);
        Object held = heldClass.getConstructor().newInstance();
        for (int i = 0; i < gs.size(); i++) {
            gs.get(i).setter.invoke(held, "" + i);
        }
        DatenPluginHeldenWerkzeug werkzeug = getWerkzeug(held);

        HashMap<String, GetterSetter> getterSetterMap = new HashMap<>();
        getterSetterMap.put("pfadZumPortrait", gs.get(Integer.parseInt(werkzeug.getPfadZumPortrait())));
        return getterSetterMap;
    }

    private Class<?> findHeldenChangesHandler(Class<?> HeldInterface) {
        return Arrays.stream(heldInterfaceClass.getMethods())
            .filter(m ->
                m.getReturnType().getPackage() != null && m.getReturnType().getPackage().getName().equals("helden.framework.held")
            )
            .map(Method::getReturnType)
            .filter(c ->
                c.getConstructors().length == 1 &&
                    c.getConstructors()[0].getParameterCount() == 1 &&
                    c.getConstructors()[0].getParameterTypes()[0].equals(PropertyChangeSupport.class)
            )
            .findFirst().get();
    }

    public static HeldReflector getInstance() {
        if (instance == null) {
            try {
                instance = new HeldReflector();
            } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException |
                     ParserConfigurationException e) {
                throw new RuntimeException(e);
            }
        }
        return instance;
    }

    public JFrame getMainWindow() {
        try {
            return (JFrame) getMainWindowInstance.invoke(null);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public Object getCurrentHeld() {
        try {
            Object mainWindow = getMainWindowInstance.invoke(null);
            return getCurrentHeld.invoke(mainWindow);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public DatenPluginHeldenWerkzeugImpl getCurrentHeldWerkzeug() {
        try {
            return (DatenPluginHeldenWerkzeugImpl) DatenPluginHeldenWerkzeugImpl.class.getConstructors()[0].newInstance(getCurrentHeld());
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public DatenPluginHeldenWerkzeug getWerkzeug(Object held) {
        try {
            return (DatenPluginHeldenWerkzeugImpl) DatenPluginHeldenWerkzeugImpl.class.getConstructors()[0].newInstance(held);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<Object> getAlleHelden() {
        try {
            return (ArrayList<Object>) getAlleHelden.invoke(getMainWindowInstance.invoke(null));
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public Object getHeldByID(String id) {
        try {
            return getHeldByID.invoke(getMainWindowInstance.invoke(null), id);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public List<KonkreterZauber> getKonkreteZauber(Object held) {
        try {
            return (List<KonkreterZauber>) getKonkreteZauber.invoke(getDingMitZauberInfos.invoke(held));
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public ZauberInfos getZauberInfos(Object held, KonkreterZauber zauber) {
        try {
            return (ZauberInfos) getZauberInfos.invoke(getDingMitZauberInfos.invoke(held), zauber);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public SFInfos getSonderfertigkeitInfos(Object held, Object sonderfertigkeit) {
        try {
            return (SFInfos) getSonderfertigkeitInfos.invoke(held, sonderfertigkeit);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public Collection<Object> getSfsFromList(Object held) {
        try {
            return (Collection<Object>) getSfsFromList.invoke(getSfList.invoke(held));
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<Object> getVorteile(Object held) {
        try {
            ArrayList<Object> result = new ArrayList<>();
            Iterator<?> it = (Iterator<?>) getVorteilIteratorFromList.invoke(getVorteilList.invoke(held));
            while (it.hasNext()) {
                result.add(it.next());
            }
            return result;

        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public int getLiturgieSFGrad(Object sf) {
        try {
            return (int) liturgieSFGetGrad.invoke(sf);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isHeldChanged(Object held) {
        try {
            return (boolean) heldIsChanged.invoke(heldChangesHandlerGetter.invoke(held));
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
