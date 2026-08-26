package de.mb.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import helden.framework.Einstellungen;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CustomGlobalConfig {

    private static CustomGlobalConfig instance;

    private Map<String, JsonNode> map = new HashMap<>();
    private final File file;

    private CustomGlobalConfig() {
        file = new File(Einstellungen.getInstance().getPfade().getPfad("einstellungsPfad").replace(".xml", ".json"));
        System.err.println("[Config] Loading config file: " + file.getAbsolutePath());
        try {
            if (file.exists()) {
                ObjectMapper mapper = new ObjectMapper();
                map = mapper.readValue(file, new TypeReference<HashMap<String, JsonNode>>() {
                });
            }
        } catch (IOException e) {
            System.err.println("[Config] Error reading config file: " + e.getMessage());
        }
    }

    public static CustomGlobalConfig getInstance() {
        if (instance == null) {
            instance = new CustomGlobalConfig();
        }
        return instance;
    }

    protected JsonNode getKey(String key) {
        return map.get(key);
    }

    protected void setKey(String key, JsonNode value) {
        map.put(key, value);

        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(file, map);
        } catch (IOException e) {
            System.err.println("[Config] Error writing config file: " + e.getMessage());
        }
    }

    public static <T> T get(T defaultValue) {
        JsonNode j = getInstance().getKey(defaultValue.getClass().getSimpleName());
        if (j == null)
            return defaultValue;

        try {
            ObjectMapper mapper = new ObjectMapper();
            //noinspection unchecked
            return mapper.treeToValue(j, (Class<T>) defaultValue.getClass());
        } catch (IOException e) {
            System.err.println("[Config] Error converting value: " + e.getMessage());
            return defaultValue;
        }
    }

    public static <T> void set(T value) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode j = mapper.valueToTree(value);
        getInstance().setKey(value.getClass().getSimpleName(), j);
    }
}
