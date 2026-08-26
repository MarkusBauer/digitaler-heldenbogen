package de.mb.reflection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class GetterSetter {
    public final Method getter;
    public final Method setter;

    public GetterSetter(Method getter, Method setter) {
        this.getter = getter;
        this.setter = setter;
    }

    public static ArrayList<GetterSetter> fromClass(Class<?> cls) {
        try {
            Object instance = cls.getConstructor().newInstance();
            ArrayList<Method> setters = new ArrayList<>();
            for (Method m: cls.getMethods()) {
                if (m.getReturnType().equals(Void.TYPE) && m.getParameterCount() == 1 && m.getParameterTypes()[0].equals(String.class)) {
                    m.invoke(instance, setters.size() + "");
                    setters.add(m);
                }
            }

            ArrayList<GetterSetter> result = new ArrayList<>();
            for (Method m: cls.getMethods()) {
                if (m.getReturnType().equals(String.class) && m.getParameterCount() == 0) {
                    String s = m.invoke(instance).toString();
                    try {
                        int i = Integer.parseInt(s);
                        result.add(new GetterSetter(m, setters.get(i)));
                    } catch (NumberFormatException ignored) {}
                }
            }

            return result;

        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
