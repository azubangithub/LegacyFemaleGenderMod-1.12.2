/*
 * Wildfire's Female Gender Mod is a female gender mod created for Minecraft.
 * Copyright (C) 2023-present WildfireRomeo
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 */

package com.wildfire.api.impl;

import com.wildfire.api.IBreastArmorTexture;
import com.wildfire.api.IGenderArmor;

/**
 * Standard implementation of {@link IGenderArmor}
 */
public class GenderArmor implements IGenderArmor {

    public static final IGenderArmor DEFAULT = new Default();
    public static final IGenderArmor EMPTY = new GenderArmor(0F, 0F, false, false, false, BreastArmorTexture.DEFAULT);

    private final float physicsResistance;
    private final float tightness;
    private final boolean coversBreasts;
    private final boolean alwaysHidesBreasts;
    private final boolean armorStandsCopySettings;
    private final IBreastArmorTexture texture;

    public GenderArmor(float physicsResistance, float tightness, boolean coversBreasts, boolean alwaysHidesBreasts,
                       boolean armorStandsCopySettings, IBreastArmorTexture texture) {
        this.physicsResistance = physicsResistance;
        this.tightness = tightness;
        this.coversBreasts = coversBreasts;
        this.alwaysHidesBreasts = alwaysHidesBreasts;
        this.armorStandsCopySettings = armorStandsCopySettings;
        this.texture = texture != null ? texture : BreastArmorTexture.DEFAULT;
    }

    public GenderArmor(float physicsResistance, float tightness, boolean armorStandsCopySettings) {
        this(physicsResistance, tightness, true, false, armorStandsCopySettings, BreastArmorTexture.DEFAULT);
    }

    @Override
    public float physicsResistance() {
        return physicsResistance;
    }

    @Override
    public float tightness() {
        return tightness;
    }

    @Override
    public boolean coversBreasts() {
        return coversBreasts;
    }

    @Override
    public boolean alwaysHidesBreasts() {
        return alwaysHidesBreasts;
    }

    @Override
    public boolean armorStandsCopySettings() {
        return armorStandsCopySettings;
    }

    @Override
    public IBreastArmorTexture texture() {
        return texture;
    }

    public static final class Default implements IGenderArmor {
        private Default() {}
    }
}