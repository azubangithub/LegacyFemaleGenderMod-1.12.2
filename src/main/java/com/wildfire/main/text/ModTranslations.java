/*
 * Wildfire's Female Gender Mod is a female gender mod created for Minecraft.
 * Copyright (C) 2023-present WildfireRomeo
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 */

package com.wildfire.main.text;

import com.wildfire.main.WildfireGender;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.IllegalFormatException;
import java.util.Map;

/**
 * Self-contained translation system for the mod.
 * Loads translations directly from the classpath to ensure reliability across all environments.
 * Supports en_us fallback and current client language loading (e.g. ru_ru).
 */
public class ModTranslations {

    private static final Map<String, String> TRANSLATIONS = new HashMap<>();
    private static boolean loaded = false;
    private static String lastLoadedLang = null;

    /**
     * Load translations. Called during mod preInit and lazily if needed.
     */
    public static synchronized void load() {
        // Always load en_us as the base / fallback
        TRANSLATIONS.clear();
        loadLangFile("/assets/wildfire_gender/lang/en_us.lang");

        // Try to load current Minecraft language if on client
        try {
            if (net.minecraftforge.fml.common.FMLCommonHandler.instance().getSide().isClient()) {
                net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getMinecraft();
                if (mc != null && mc.getLanguageManager() != null && mc.getLanguageManager().getCurrentLanguage() != null) {
                    String code = mc.getLanguageManager().getCurrentLanguage().getLanguageCode().toLowerCase();
                    lastLoadedLang = code;
                    if (!code.equals("en_us")) {
                        loadLangFile("/assets/wildfire_gender/lang/" + code + ".lang");
                    }
                }
            }
        } catch (Throwable ignored) {
        }

        loaded = true;
        WildfireGender.LOGGER.info("Loaded {} mod translations (active lang: {})", TRANSLATIONS.size(), lastLoadedLang != null ? lastLoadedLang : "en_us");
    }

    private static void loadLangFile(String path) {
        try (InputStream is = ModTranslations.class.getResourceAsStream(path)) {
            if (is == null) {
                return;
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#") || line.startsWith("//")) {
                    continue;
                }
                int eq = line.indexOf('=');
                if (eq > 0) {
                    String key = line.substring(0, eq).trim();
                    String value = line.substring(eq + 1);
                    TRANSLATIONS.put(key, value);
                }
            }
        } catch (Exception e) {
            WildfireGender.LOGGER.warn("Failed to load lang file: " + path, e);
        }
    }

    /**
     * Translate a key using the mod's own translation map.
     * Falls back to Minecraft's I18n, and then to the raw key.
     *
     * @param key  the translation key
     * @param args format arguments for %s, %d, etc.
     * @return the translated and formatted string
     */
    public static String format(String key, Object... args) {
        if (!loaded) {
            load();
        } else {
            // Check if language was switched in client options
            try {
                if (net.minecraftforge.fml.common.FMLCommonHandler.instance().getSide().isClient()) {
                    net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getMinecraft();
                    if (mc != null && mc.getLanguageManager() != null && mc.getLanguageManager().getCurrentLanguage() != null) {
                        String code = mc.getLanguageManager().getCurrentLanguage().getLanguageCode().toLowerCase();
                        if (lastLoadedLang != null && !lastLoadedLang.equals(code)) {
                            load();
                        }
                    }
                }
            } catch (Throwable ignored) {
            }
        }

        String pattern = TRANSLATIONS.get(key);
        if (pattern == null) {
            // Fallback: try Minecraft's I18n as a secondary source
            try {
                String i18nResult = net.minecraft.client.resources.I18n.format(key, args);
                if (!i18nResult.equals(key)) {
                    return i18nResult;
                }
            } catch (Exception ignored) {
            }
            return key;
        }
        if (args.length == 0) {
            return pattern;
        }
        try {
            return String.format(pattern, args);
        } catch (IllegalFormatException e) {
            return pattern;
        }
    }

    /**
     * Check if a translation exists for the given key.
     */
    public static boolean hasKey(String key) {
        if (!loaded) {
            load();
        }
        return TRANSLATIONS.containsKey(key);
    }
}
