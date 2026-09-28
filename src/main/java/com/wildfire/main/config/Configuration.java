/*
 * Wildfire's Female Gender Mod is a female gender mod created for Minecraft.
 * Copyright (C) 2023-present WildfireRomeo
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 */

package com.wildfire.main.config;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonWriter;
import com.wildfire.main.WildfireGender;
import net.minecraftforge.fml.common.Loader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class Configuration {

    private static final TypeAdapter<JsonObject> ADAPTER = new Gson().getAdapter(JsonObject.class);

    private final File CFG_FILE;
    public JsonObject SAVE_VALUES = new JsonObject();

    public Configuration(String saveLoc, String cfgName) {
        File configDir = Loader.instance() != null && Loader.instance().getConfigDir() != null
                ? Loader.instance().getConfigDir()
                : new File("config");
        File saveDir = new File(configDir, saveLoc);
        if (!saveDir.exists()) {
            saveDir.mkdirs();
        }
        CFG_FILE = new File(saveDir, cfgName + ".json");
    }

    public <TYPE> void set(ConfigKey<TYPE> key, TYPE value) {
        key.save(SAVE_VALUES, value);
    }

    public void setDefaults(ConfigKey<?>... keys) {
        for (ConfigKey<?> key : keys) {
            setDefault(key);
        }
    }

    public <TYPE> void setDefault(ConfigKey<TYPE> key) {
        if (!SAVE_VALUES.has(key.key)) {
            set(key, key.defaultValue);
        }
    }

    public <TYPE> TYPE get(ConfigKey<TYPE> key) {
        return key.read(SAVE_VALUES);
    }

    public void removeParameter(ConfigKey<?> key) {
        removeParameter(key.key);
    }

    public void removeParameter(String key) {
        SAVE_VALUES.remove(key);
    }

    public boolean exists() {
        return CFG_FILE.exists();
    }

    public void save() {
        try (FileWriter writer = new FileWriter(CFG_FILE);
             JsonWriter jsonWriter = new JsonWriter(writer)) {
            jsonWriter.setIndent("\t");
            ADAPTER.write(jsonWriter, SAVE_VALUES);
        } catch (IOException e) {
            WildfireGender.LOGGER.error("Failed to save Configuration", e);
        }
    }

    public void load() {
        if (!CFG_FILE.exists()) {
            return;
        }
        try (FileReader configurationFile = new FileReader(CFG_FILE)) {
            JsonObject obj = new JsonParser().parse(configurationFile).getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
                SAVE_VALUES.add(entry.getKey(), entry.getValue());
            }
        } catch (Exception e) {
            WildfireGender.LOGGER.error("Failed to load Configuration", e);
        }
    }
}
