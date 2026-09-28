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

import com.wildfire.main.Gender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class BreastDataNBT {

    public static final String NBT_TAG = "wildfire_gender";

    private final Gender gender;
    private final float size;
    private final float xOffset;
    private final float yOffset;
    private final float zOffset;
    private final boolean uniboob;
    private final float cleavage;

    public BreastDataNBT(Gender gender, float size, float xOffset, float yOffset, float zOffset, boolean uniboob, float cleavage) {
        this.gender = gender;
        this.size = size;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.zOffset = zOffset;
        this.uniboob = uniboob;
        this.cleavage = cleavage;
    }

    public static BreastDataNBT fromPlayer(EntityPlayer player, PlayerConfig config) {
        if (config == null || !config.getGender().canHaveBreasts()) {
            return null;
        }
        Breasts breasts = config.getBreasts();
        return new BreastDataNBT(
                config.getGender(),
                config.getBustSize(),
                breasts.getXOffset(),
                breasts.getYOffset(),
                breasts.getZOffset(),
                breasts.isUniboob(),
                breasts.getCleavage()
        );
    }

    public static BreastDataNBT fromStack(ItemStack stack) {
        if (stack == null || stack.isEmpty() || !stack.hasTagCompound()) {
            return null;
        }
        NBTTagCompound tag = stack.getTagCompound();
        if (!tag.hasKey(NBT_TAG, 10)) {
            return null;
        }
        NBTTagCompound breastTag = tag.getCompoundTag(NBT_TAG);
        Gender gender = Gender.byId(breastTag.getInteger("gender"));
        float size = breastTag.getFloat("size");
        float x = breastTag.getFloat("x");
        float y = breastTag.getFloat("y");
        float z = breastTag.getFloat("z");
        boolean uniboob = breastTag.getBoolean("uniboob");
        float cleavage = breastTag.getFloat("cleavage");
        return new BreastDataNBT(gender, size, x, y, z, uniboob, cleavage);
    }

    public void writeToStack(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return;
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null) {
            tag = new NBTTagCompound();
            stack.setTagCompound(tag);
        }
        NBTTagCompound breastTag = new NBTTagCompound();
        breastTag.setInteger("gender", gender.ordinal());
        breastTag.setFloat("size", size);
        breastTag.setFloat("x", xOffset);
        breastTag.setFloat("y", yOffset);
        breastTag.setFloat("z", zOffset);
        breastTag.setBoolean("uniboob", uniboob);
        breastTag.setFloat("cleavage", cleavage);
        tag.setTag(NBT_TAG, breastTag);
    }

    public static boolean removeFromStack(ItemStack stack) {
        if (stack == null || stack.isEmpty() || !stack.hasTagCompound()) {
            return false;
        }
        NBTTagCompound tag = stack.getTagCompound();
        if (tag.hasKey(NBT_TAG)) {
            tag.removeTag(NBT_TAG);
            if (tag.isEmpty()) {
                stack.setTagCompound(null);
            }
            return true;
        }
        return false;
    }

    public void apply(EntityConfig config) {
        config.gender = this.gender;
        config.pBustSize = this.size;
        config.breasts.updateXOffset(this.xOffset);
        config.breasts.updateYOffset(this.yOffset);
        config.breasts.updateZOffset(this.zOffset);
        config.breasts.updateUniboob(this.uniboob);
        config.breasts.updateCleavage(this.cleavage);
    }

    public Gender getGender() { return gender; }
    public float getSize() { return size; }
    public float getXOffset() { return xOffset; }
    public float getYOffset() { return yOffset; }
    public float getZOffset() { return zOffset; }
    public boolean isUniboob() { return uniboob; }
    public float getCleavage() { return cleavage; }
}
