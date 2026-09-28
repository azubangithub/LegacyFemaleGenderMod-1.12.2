/*
 * Wildfire's Female Gender Mod is a female gender mod created for Minecraft.
 * Copyright (C) 2023-present WildfireRomeo
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 */

package com.wildfire.main.entitydata;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.wildfire.main.Gender;
import com.wildfire.main.WildfireGender;
import com.wildfire.main.config.ClientConfiguration;
import com.wildfire.main.config.ConfigKey;
import com.wildfire.main.config.Configuration;
import net.minecraft.item.ItemStack;

import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * A version of {@link EntityConfig} backed by a {@link Configuration} for use with players
 */
@SuppressWarnings("UnusedReturnValue")
public class PlayerConfig extends EntityConfig {

    private final ClientConfiguration cfg;
    public SyncStatus syncStatus = SyncStatus.UNKNOWN;
    public boolean needsSync;

    private boolean hurtSounds = ClientConfiguration.HURT_SOUNDS.getDefault();
    protected boolean holidayThemes = ClientConfiguration.HOLIDAY_THEMES.getDefault();
    protected boolean showBreastsInArmor = ClientConfiguration.SHOW_IN_ARMOR.getDefault();
    private boolean armorPhysOverride = ClientConfiguration.ARMOR_PHYSICS_OVERRIDE.getDefault();

    public PlayerConfig(UUID uuid) {
        this(uuid, ClientConfiguration.GENDER.getDefault());
    }

    public PlayerConfig(UUID uuid, Gender gender) {
        super(uuid);
        this.gender = gender;
        this.cfg = new ClientConfiguration(this.uuid.toString());
        this.cfg.set(ClientConfiguration.USERNAME, this.uuid);
        this.cfg.setDefaults(
              ClientConfiguration.GENDER,
              ClientConfiguration.BUST_SIZE,
              ClientConfiguration.HURT_SOUNDS,

              ClientConfiguration.BREASTS_OFFSET_X,
              ClientConfiguration.BREASTS_OFFSET_Y,
              ClientConfiguration.BREASTS_OFFSET_Z,
              ClientConfiguration.BREASTS_UNIBOOB,
              ClientConfiguration.BREASTS_CLEAVAGE,

              ClientConfiguration.BREAST_PHYSICS,
              ClientConfiguration.ARMOR_PHYSICS_OVERRIDE,
              ClientConfiguration.SHOW_IN_ARMOR,
              ClientConfiguration.BOUNCE_MULTIPLIER,
              ClientConfiguration.FLOPPY_MULTIPLIER,

              ClientConfiguration.VOICE_PITCH,
              ClientConfiguration.HOLIDAY_THEMES
        );
    }

    @Override
    public void readFromStack(ItemStack chest) {
        // No-op on players
    }

    public ClientConfiguration getConfig() {
        return cfg;
    }

    private <VALUE> boolean updateValue(ConfigKey<VALUE> key, VALUE value, Consumer<VALUE> setter) {
        if (key.validate(value)) {
            setter.accept(value);
            return true;
        }
        return false;
    }

    public <VALUE> boolean updateFrom(ConfigKey<VALUE> key, Configuration copyFrom, Consumer<VALUE> setter) {
        VALUE value = copyFrom.get(key);
        if (value == null) {
            return false;
        }
        return updateValue(key, value, setter);
    }

    public boolean updateGender(Gender value) {
        return updateValue(ClientConfiguration.GENDER, value, v -> {
            this.gender = v;
            if (v.canHaveBreasts()) {
                lBreastPhysics.setBreastSize(pBustSize);
                rBreastPhysics.setBreastSize(pBustSize);
            } else {
                lBreastPhysics.setBreastSize(0);
                rBreastPhysics.setBreastSize(0);
            }
        });
    }

    public boolean updateBustSize(float value) {
        return updateValue(ClientConfiguration.BUST_SIZE, value, v -> {
            this.pBustSize = v;
            if (gender.canHaveBreasts()) {
                lBreastPhysics.setBreastSize(v);
                rBreastPhysics.setBreastSize(v);
            }
        });
    }

    public boolean updateBustSize(Configuration copyFrom) {
        return updateFrom(ClientConfiguration.BUST_SIZE, copyFrom, v -> {
            this.pBustSize = v;
            if (gender.canHaveBreasts()) {
                lBreastPhysics.setBreastSize(v);
                rBreastPhysics.setBreastSize(v);
            }
        });
    }

    public boolean hasHolidayThemes() {
        return holidayThemes;
    }

    public boolean updateHolidayThemes(boolean value) {
        return updateValue(ClientConfiguration.HOLIDAY_THEMES, value, v -> this.holidayThemes = v);
    }

    public boolean updateVoicePitch(float value) {
        return updateValue(ClientConfiguration.VOICE_PITCH, value, v -> this.voicePitch = v);
    }

    public boolean hasHurtSounds() {
        return hurtSounds;
    }

    public boolean updateHurtSounds(boolean value) {
        return updateValue(ClientConfiguration.HURT_SOUNDS, value, v -> this.hurtSounds = v);
    }

    public boolean updateBreastPhysics(boolean value) {
        return updateValue(ClientConfiguration.BREAST_PHYSICS, value, v -> this.breastPhysics = v);
    }

    @Override
    public boolean getArmorPhysicsOverride() {
        return armorPhysOverride;
    }

    public boolean hasArmorBreastPhysics() {
        return getArmorPhysicsOverride();
    }

    public boolean updateArmorBreastPhysics(boolean value) {
        return updateArmorPhysicsOverride(value);
    }

    public float getBounceMultiplierRaw() {
        return getBounceMultiplier();
    }

    @Override
    public boolean canBreathe() {
        return true;
    }

    public boolean updateArmorPhysicsOverride(boolean value) {
        return updateValue(ClientConfiguration.ARMOR_PHYSICS_OVERRIDE, value, v -> this.armorPhysOverride = v);
    }

    @Override
    public boolean showBreastsInArmor() {
        return showBreastsInArmor;
    }

    public boolean updateShowBreastsInArmor(boolean value) {
        return updateValue(ClientConfiguration.SHOW_IN_ARMOR, value, v -> this.showBreastsInArmor = v);
    }

    public boolean updateBounceMultiplier(float value) {
        return updateValue(ClientConfiguration.BOUNCE_MULTIPLIER, value, v -> this.bounceMultiplier = v);
    }

    public boolean updateFloppiness(float value) {
        return updateValue(ClientConfiguration.FLOPPY_MULTIPLIER, value, v -> this.floppyMultiplier = v);
    }

    public SyncStatus getSyncStatus() {
        return this.syncStatus;
    }

    public JsonObject toJson() {
        JsonObject copy = new JsonObject();
        for (Map.Entry<String, JsonElement> entry : cfg.SAVE_VALUES.entrySet()) {
            copy.add(entry.getKey(), entry.getValue());
        }
        return copy;
    }

    public boolean hasLocalConfig() {
        return cfg.exists();
    }

    public void loadFromDisk(boolean markForSync) {
        this.syncStatus = SyncStatus.CACHED;
        cfg.load();
        loadFromConfig(markForSync);
    }

    public void loadFromConfig(boolean markForSync) {
        updateGender(cfg.get(ClientConfiguration.GENDER));
        updateBustSize(cfg.get(ClientConfiguration.BUST_SIZE));
        updateHurtSounds(cfg.get(ClientConfiguration.HURT_SOUNDS));
        updateVoicePitch(cfg.get(ClientConfiguration.VOICE_PITCH));
        updateHolidayThemes(cfg.get(ClientConfiguration.HOLIDAY_THEMES));

        // physics
        updateBreastPhysics(cfg.get(ClientConfiguration.BREAST_PHYSICS));
        updateShowBreastsInArmor(cfg.get(ClientConfiguration.SHOW_IN_ARMOR));
        updateArmorPhysicsOverride(cfg.get(ClientConfiguration.ARMOR_PHYSICS_OVERRIDE));
        updateBounceMultiplier(cfg.get(ClientConfiguration.BOUNCE_MULTIPLIER));
        updateFloppiness(cfg.get(ClientConfiguration.FLOPPY_MULTIPLIER));

        getBreasts().copyFrom(cfg);

        if (markForSync) {
            this.needsSync = true;
        }
    }

    public static void saveGenderInfo(PlayerConfig plr) {
        ClientConfiguration config = plr.getConfig();
        config.set(ClientConfiguration.USERNAME, plr.uuid);
        config.set(ClientConfiguration.GENDER, plr.getGender());
        config.set(ClientConfiguration.BUST_SIZE, plr.getBustSize());
        config.set(ClientConfiguration.HURT_SOUNDS, plr.hasHurtSounds());
        config.set(ClientConfiguration.VOICE_PITCH, plr.getVoicePitch());
        config.set(ClientConfiguration.HOLIDAY_THEMES, plr.hasHolidayThemes());

        // physics
        config.set(ClientConfiguration.BREAST_PHYSICS, plr.hasBreastPhysics());
        config.set(ClientConfiguration.SHOW_IN_ARMOR, plr.showBreastsInArmor());
        config.set(ClientConfiguration.ARMOR_PHYSICS_OVERRIDE, plr.getArmorPhysicsOverride());
        config.set(ClientConfiguration.BOUNCE_MULTIPLIER, plr.getBounceMultiplier());
        config.set(ClientConfiguration.FLOPPY_MULTIPLIER, plr.getFloppiness());

        config.set(ClientConfiguration.BREASTS_OFFSET_X, plr.getBreasts().getXOffset());
        config.set(ClientConfiguration.BREASTS_OFFSET_Y, plr.getBreasts().getYOffset());
        config.set(ClientConfiguration.BREASTS_OFFSET_Z, plr.getBreasts().getZOffset());
        config.set(ClientConfiguration.BREASTS_UNIBOOB, plr.getBreasts().isUniboob());
        config.set(ClientConfiguration.BREASTS_CLEAVAGE, plr.getBreasts().getCleavage());

        config.save();
        plr.needsSync = true;
    }

    @Override
    public boolean hasJacketLayer() {
        return true;
    }

    public void updateFromJson(JsonObject json) {
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            this.cfg.SAVE_VALUES.add(entry.getKey(), entry.getValue());
        }
        loadFromConfig(false);
        this.syncStatus = SyncStatus.SYNCED;
    }

    public enum SyncStatus {
        CACHED,
        SYNCED,
        UNKNOWN
    }
}
