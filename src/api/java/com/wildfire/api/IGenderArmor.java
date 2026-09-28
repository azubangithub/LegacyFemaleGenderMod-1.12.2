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

/**
 * Configure how an armor item interacts with breast rendering.
 */
public interface IGenderArmor {

    /**
     * Determines whether this {@link IGenderArmor} "covers" the breasts or if it has an open front (false) like the elytra.
     *
     * @return true if breasts are covered.
     */
    default boolean coversBreasts() {
        return true;
    }

    /**
     * Determines if this {@link IGenderArmor} should always hide the wearer's breasts when worn.
     *
     * @return true to hide breasts regardless of user settings.
     */
    default boolean alwaysHidesBreasts() {
        return false;
    }

    /**
     * The percentage of physics resistance this armor provides (0 = full bounce, 1 = no bounce).
     *
     * @return value between 0 and 1.
     */
    default float physicsResistance() {
        return 0.5F;
    }

    /**
     * Value representing how "tight" this armor is (0 = normal, 1 = maximum compression).
     *
     * @return value between 0 and 1.
     */
    default float tightness() {
        return 0;
    }

    /**
     * Determines whether armor stands should copy the breast settings of the player equipping this chestplate.
     *
     * @return true to copy settings onto armor stands.
     */
    default boolean armorStandsCopySettings() {
        return !alwaysHidesBreasts() && coversBreasts() && physicsResistance() == 1F;
    }

    /**
     * Texture override details for this armor piece.
     *
     * @return {@link IBreastArmorTexture}
     */
    default IBreastArmorTexture texture() {
        return IBreastArmorTexture.DEFAULT;
    }
}