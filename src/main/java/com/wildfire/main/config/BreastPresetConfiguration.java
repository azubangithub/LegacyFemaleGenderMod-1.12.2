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

import net.minecraftforge.fml.common.Loader;

import java.io.File;
import java.util.Arrays;

public class BreastPresetConfiguration extends Configuration {

    public static final StringConfigKey PRESET_NAME = new StringConfigKey("preset_name", "");
    public static final FloatConfigKey BUST_SIZE = ClientConfiguration.BUST_SIZE;

    public static final FloatConfigKey BREASTS_OFFSET_X = ClientConfiguration.BREASTS_OFFSET_X;
    public static final FloatConfigKey BREASTS_OFFSET_Y = ClientConfiguration.BREASTS_OFFSET_Y;
    public static final FloatConfigKey BREASTS_OFFSET_Z = ClientConfiguration.BREASTS_OFFSET_Z;
    public static final BooleanConfigKey BREASTS_UNIBOOB = ClientConfiguration.BREASTS_UNIBOOB;
    public static final FloatConfigKey BREASTS_CLEAVAGE = ClientConfiguration.BREASTS_CLEAVAGE;

    public BreastPresetConfiguration(String cfgName) {
        super("wildfire_gender/presets", cfgName);
    }

    public static File getPresetDir() {
        File configDir = Loader.instance() != null && Loader.instance().getConfigDir() != null
                ? Loader.instance().getConfigDir()
                : new File("config");
        File dir = new File(configDir, "wildfire_gender/presets");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    public static BreastPresetConfiguration[] getBreastPresetConfigurationFiles() {
        File[] presetFiles = getPresetDir().listFiles((dir, name) -> name.endsWith(".json"));
        if (presetFiles != null) {
            return Arrays.stream(presetFiles).map(file -> {
                BreastPresetConfiguration cfg = new BreastPresetConfiguration(file.getName().replace(".json", ""));
                cfg.load();
                return cfg;
            }).toArray(BreastPresetConfiguration[]::new);
        }
        return new BreastPresetConfiguration[]{};
    }
}