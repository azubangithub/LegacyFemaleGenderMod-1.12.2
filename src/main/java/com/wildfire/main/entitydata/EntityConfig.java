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

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.wildfire.api.IGenderArmor;
import com.wildfire.main.Gender;
import com.wildfire.main.WildfireGender;
import com.wildfire.main.WildfireHelper;
import com.wildfire.main.config.ClientConfiguration;
import com.wildfire.physics.BreastPhysics;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class EntityConfig {

    public static final LoadingCache<UUID, EntityConfig> CACHE = CacheBuilder.newBuilder()
          .expireAfterAccess(5, TimeUnit.MINUTES)
          .build(new CacheLoader<UUID, EntityConfig>() {
              @Override
              public EntityConfig load(UUID key) {
                  return new EntityConfig(key);
              }
          });

    public final UUID uuid;
    protected Gender gender = ClientConfiguration.GENDER.getDefault();
    protected float pBustSize = ClientConfiguration.BUST_SIZE.getDefault();
    protected boolean breastPhysics = ClientConfiguration.BREAST_PHYSICS.getDefault();
    protected float bounceMultiplier = ClientConfiguration.BOUNCE_MULTIPLIER.getDefault();
    protected float floppyMultiplier = ClientConfiguration.FLOPPY_MULTIPLIER.getDefault();

    protected float voicePitch = ClientConfiguration.VOICE_PITCH.getDefault();
    protected final BreastPhysics lBreastPhysics, rBreastPhysics;
    protected final Breasts breasts;
    protected boolean jacketLayer = true;
    protected BreastDataNBT fromNbt;

    public EntityConfig(UUID uuid) {
        this.uuid = uuid;
        this.breasts = new Breasts();
        this.lBreastPhysics = new BreastPhysics(this);
        this.rBreastPhysics = new BreastPhysics(this);
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public void setBustSize(float bustSize) {
        this.pBustSize = bustSize;
    }

    public void setBreastPhysics(boolean breastPhysics) {
        this.breastPhysics = breastPhysics;
    }

    public void setBounceMultiplier(float bounceMultiplier) {
        this.bounceMultiplier = bounceMultiplier;
    }

    public void setFloppyMultiplier(float floppyMultiplier) {
        this.floppyMultiplier = floppyMultiplier;
    }

    public void setJacketLayer(boolean jacketLayer) {
        this.jacketLayer = jacketLayer;
    }

    public void readFromStack(ItemStack chestplate) {
        if (chestplate == null || chestplate.isEmpty() || !chestplate.hasTagCompound()) {
            this.fromNbt = null;
            this.gender = Gender.MALE;
            return;
        }
        BreastDataNBT data = BreastDataNBT.fromStack(chestplate);
        if (data == null) {
            this.fromNbt = null;
            this.gender = Gender.MALE;
            return;
        }
        this.fromNbt = data;
        data.apply(this);
    }

    public static boolean isSupportedEntity(EntityLivingBase living) {
        return living instanceof EntityPlayer || living instanceof EntityArmorStand;
    }

    public static EntityConfig getEntity(EntityLivingBase entity) {
        if (entity instanceof EntityPlayer) {
            return WildfireGender.getPlayerById(entity.getUniqueID());
        } else if (entity instanceof EntityArmorStand) {
            return CACHE.getUnchecked(entity.getUniqueID());
        }
        return null;
    }

    public void tickBreastPhysics(EntityLivingBase living) {
        if (gender.canHaveBreasts() && (pBustSize > 0 || hasBreastPhysics())) {
            ItemStack chestplate = living.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
            IGenderArmor armorConfig = WildfireHelper.getArmorConfig(chestplate);
            lBreastPhysics.update(living, armorConfig);
            if (!breasts.isUniboob()) {
                rBreastPhysics.update(living, armorConfig);
            }
        }
    }

    public Gender getGender() {
        return gender;
    }

    public float getBustSize() {
        return pBustSize;
    }

    public boolean hasBreastPhysics() {
        return breastPhysics;
    }

    public float getBounceMultiplier() {
        return bounceMultiplier;
    }

    public float getFloppiness() {
        return floppyMultiplier;
    }

    public float getVoicePitch() {
        return voicePitch;
    }

    public boolean getArmorPhysicsOverride() {
        return false;
    }

    public boolean showBreastsInArmor() {
        return true;
    }

    public boolean canBreathe() {
        return false;
    }

    public BreastPhysics getLeftBreastPhysics() {
        return lBreastPhysics;
    }

    public BreastPhysics getRightBreastPhysics() {
        return rBreastPhysics;
    }

    public Breasts getBreasts() {
        return breasts;
    }

    public boolean hasJacketLayer() {
        return jacketLayer;
    }
}
