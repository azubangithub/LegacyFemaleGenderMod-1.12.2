/*
 * Wildfire's Female Gender Mod is a female gender mod created for Minecraft.
 * Copyright (C) 2023-present WildfireRomeo
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 */

package com.wildfire.main;

import com.wildfire.main.text.IHasTextComponent.IHasEnumNameTextComponent;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;

public enum Gender implements IHasEnumNameTextComponent {
    FEMALE(true, WildfireSounds.FEMALE_HURT),
    MALE(false, null),
    OTHER(true, null);

    private final SoundEvent hurtSound;
    private final boolean canHaveBreasts;

    Gender(boolean canHaveBreasts, SoundEvent hurtSound) {
        this.canHaveBreasts = canHaveBreasts;
        this.hurtSound = hurtSound;
    }

    public SoundEvent getHurtSound() {
        return hurtSound;
    }

    public boolean canHaveBreasts() {
        return canHaveBreasts;
    }

    @Override
    public ITextComponent getTextComponent() {
        switch (this) {
            case FEMALE:
                return WildfireLang.FEMALE.translateColored(TextFormatting.LIGHT_PURPLE);
            case MALE:
                return WildfireLang.MALE.translateColored(TextFormatting.BLUE);
            case OTHER:
            default:
                return WildfireLang.OTHER.translateColored(TextFormatting.GREEN);
        }
    }

    public static Gender byId(int id) {
        if (id < 0 || id >= values().length) {
            return FEMALE;
        }
        return values()[id];
    }
}