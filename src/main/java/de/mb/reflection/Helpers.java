package de.mb.reflection;

import java.lang.reflect.Method;
import java.util.Arrays;

public class Helpers {
    public static Method findGetter(Class<?> cls, Class<?> returnType, int argumentCount) {
        for (Method method : cls.getMethods()) {
            if (method.getParameterCount() == argumentCount && method.getReturnType().equals(returnType)) {
                return method;
            }
        }
        throw new RuntimeException("No getter found for " + cls.getSimpleName() + " that returns " + returnType + " with " + argumentCount + " arguments.");
    }

    public static Method findDeclaredMethod(Class<?> cls, Class<?> returnType, int argumentCount) {
        for (Method method : cls.getDeclaredMethods()) {
            if (method.getParameterCount() == argumentCount && method.getReturnType().equals(returnType)) {
                return method;
            }
        }
        throw new RuntimeException("No getter found for " + cls.getSimpleName() + " that returns " + returnType + " with " + argumentCount + " arguments.");
    }

    public static Method findMethodByParameterTypes(Class type, Class... params) {
		for (Method m : type.getDeclaredMethods()) {
			if (Arrays.equals(m.getParameterTypes(), params))
				return m;
		}
		return null;
	}

    public static Method getMethodByName(Class type, String name) {
		for (Method m : type.getDeclaredMethods()) {
			if (m.getName().equals(name))
				return m;
		}
		return null;
	}
}
