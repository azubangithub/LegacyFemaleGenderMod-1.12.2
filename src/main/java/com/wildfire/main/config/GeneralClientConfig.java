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

import com.wildfire.main.config.enums.ShowPlayerListMode;
import net.minecraftforge.common.config.Configuration;

import java.io.File;

public class GeneralClientConfig {

    public static final GeneralClientConfig INSTANCE = new GeneralClientConfig();

    private Configuration config;

    public ConfigValue<Boolean> disableRendering = new ConfigValue<>(false);
    public ConfigValue<Boolean> disableSoundReplacement = new ConfigValue<>(false);
    public ConfigValue<Boolean> armorStat = new ConfigValue<>(true);
    public ConfigValue<ShowPlayerListMode> alwaysShowList = new ConfigValue<>(ShowPlayerListMode.MOD_UI_ONLY);
    public ConfigValue<Boolean> hideOwnContributorTag = new ConfigValue<>(false);

    // Disabled cloud compatibility properties (always disabled / offline for privacy)
    public final ConfigValue<Boolean> cloudSync = new ConfigValue<>(false);
    public final ConfigValue<Boolean> syncPlayerData = new ConfigValue<>(false);
    public final ConfigValue<String> cloudServer = new ConfigValue<>("");
    public final ConfigValue<Boolean> firstTimeLoad = new ConfigValue<>(false);

    private GeneralClientConfig() {
    }

    public void init(File configFile) {
        if (config == null) {
            config = new Configuration(configFile);
            load();
        }
    }

    public void load() {
        if (config == null) return;
        try {
            config.load();
            disableRendering.set(config.getBoolean("disableRendering", "client", false, "Global override to disable all rendering related to the mod"));
            disableSoundReplacement.set(config.getBoolean("disableSoundReplacement", "client", false, "Global override to disable replacing sounds of players with female variants"));
            armorStat.set(config.getBoolean("armorStat", "client", true, "Show breast physics resistance on armor tooltips"));
            hideOwnContributorTag.set(config.getBoolean("hideOwnContributorTag", "client", false, "Hide your own contributor tag"));

            String listModeStr = config.getString("alwaysShowList", "client", ShowPlayerListMode.MOD_UI_ONLY.name(), "Player list mode");
            try {
                alwaysShowList.set(ShowPlayerListMode.valueOf(listModeStr));
            } catch (Exception e) {
                alwaysShowList.set(ShowPlayerListMode.MOD_UI_ONLY);
            }
        } finally {
            if (config.hasChanged()) {
                config.save();
            }
        }
    }

    public void save() {
        if (config == null) return;
        config.get("client", "disableRendering", false).set(disableRendering.get());
        config.get("client", "disableSoundReplacement", false).set(disableSoundReplacement.get());
        config.get("client", "armorStat", true).set(armorStat.get());
        config.get("client", "hideOwnContributorTag", false).set(hideOwnContributorTag.get());
        config.get("client", "alwaysShowList", ShowPlayerListMode.MOD_UI_ONLY.name()).set(alwaysShowList.get().name());
        config.save();
    }

    public static class ConfigValue<T> {
        private T val;

        public ConfigValue(T val) {
            this.val = val;
        }

        public T get() {
            return val;
        }

        public void set(T val) {
            this.val = val;
        }
    }
}