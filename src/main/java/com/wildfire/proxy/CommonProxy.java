/*
 * Wildfire's Female Gender Mod is a female gender mod created for Minecraft.
 * Copyright (C) 2023-present WildfireRomeo
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 */

package com.wildfire.proxy;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy {

    public void preInit(FMLPreInitializationEvent event) {
        // Load mod's own translation map from classpath
        com.wildfire.main.text.ModTranslations.load();

        // Also inject into server-side LanguageMap for TextComponentTranslation fallback
        try (java.io.InputStream is = CommonProxy.class.getResourceAsStream("/assets/wildfire_gender/lang/en_us.lang")) {
            if (is != null) {
                net.minecraft.util.text.translation.LanguageMap.inject(is);
            }
        } catch (Exception ignored) {
        }

        if (net.minecraftforge.fml.common.Loader.isModLoaded("metamorph")) {
            com.wildfire.compat.blockbuster.BlockbusterCompat.preInit();
        }
    }

    public void init(FMLInitializationEvent event) {
        if (net.minecraftforge.fml.common.Loader.isModLoaded("metamorph")) {
            com.wildfire.compat.blockbuster.BlockbusterCompat.init();
        }
    }

    public void postInit(FMLPostInitializationEvent event) {
    }
}
