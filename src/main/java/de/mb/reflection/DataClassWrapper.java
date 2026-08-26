package de.mb.reflection;

import java.util.HashMap;
import java.util.Map;

public class DataClassWrapper {
    private final Map<String, GetterSetter> gettersAndSetters = new HashMap<>();

    public DataClassWrapper(Map<String, GetterSetter> gettersAndSetters) {
        this.gettersAndSetters.putAll(gettersAndSetters);
    }

    public Object get(Object instance, String what) {
        try {
            return gettersAndSetters.get(what).getter.invoke(instance);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void set(Object instance, String what, Object value) {
        try {
            gettersAndSetters.get(what).setter.invoke(instance, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
