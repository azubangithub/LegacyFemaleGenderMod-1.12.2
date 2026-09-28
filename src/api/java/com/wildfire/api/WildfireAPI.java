/*
 * Wildfire's Female Gender Mod is a female gender mod created for Minecraft.
 * Copyright (C) 2023-present WildfireRomeo
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 */

package com.wildfire.api;

import net.minecraft.item.Item;

import java.util.HashMap;
import java.util.Map;

public final class WildfireAPI {

    public static final String MODID = "wildfire_gender";

    private static final Map<Item, IGenderArmor> ARMOR_REGISTRY = new HashMap<>();

    private WildfireAPI() {
    }

    /**
     * Register a custom {@link IGenderArmor} configuration for an armor item
     *
     * @param item  The armor item
     * @param armor The armor config
     */
    public static void registerGenderArmor(Item item, IGenderArmor armor) {
        if (item != null && armor != null) {
            ARMOR_REGISTRY.put(item, armor);
        }
    }

    /**
     * Get the registered {@link IGenderArmor} for an item, or null if none
     */
    public static IGenderArmor getGenderArmor(Item item) {
        return ARMOR_REGISTRY.get(item);
    }
}