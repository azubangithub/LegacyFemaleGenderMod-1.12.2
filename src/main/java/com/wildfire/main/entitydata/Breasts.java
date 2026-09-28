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

import com.wildfire.main.config.ClientConfiguration;
import com.wildfire.main.config.ConfigKey;
import com.wildfire.main.config.Configuration;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.Vec3d;

import java.util.function.Consumer;

/**
 * Data class representing an entity's breast appearance settings
 */
@SuppressWarnings("UnusedReturnValue")
public final class Breasts {

    private float xOffset = ClientConfiguration.BREASTS_OFFSET_X.getDefault(),
          yOffset = ClientConfiguration.BREASTS_OFFSET_Y.getDefault(),
          zOffset = ClientConfiguration.BREASTS_OFFSET_Z.getDefault();
    private float cleavage = ClientConfiguration.BREASTS_CLEAVAGE.getDefault();
    private boolean uniboob = ClientConfiguration.BREASTS_UNIBOOB.getDefault();

    public void toBytes(ByteBuf buf) {
        buf.writeFloat(xOffset);
        buf.writeFloat(yOffset);
        buf.writeFloat(zOffset);
        buf.writeBoolean(uniboob);
        buf.writeFloat(cleavage);
    }

    public static Breasts fromBytes(ByteBuf buf) {
        Breasts breasts = new Breasts();
        breasts.xOffset = buf.readFloat();
        breasts.yOffset = buf.readFloat();
        breasts.zOffset = buf.readFloat();
        breasts.uniboob = buf.readBoolean();
        breasts.cleavage = buf.readFloat();
        return breasts;
    }

    private <VALUE> boolean updateValue(ConfigKey<VALUE> key, VALUE value, Consumer<VALUE> setter) {
        if (key.validate(value)) {
            setter.accept(value);
            return true;
        }
        return false;
    }

    private <VALUE> boolean updateFrom(ConfigKey<VALUE> key, Configuration copyFrom, Consumer<VALUE> setter) {
        VALUE value = copyFrom.get(key);
        if (value == null) {
            return false;
        }
        return updateValue(key, value, setter);
    }

    public void updateFrom(Breasts other) {
        updateXOffset(other.getXOffset());
        updateYOffset(other.getYOffset());
        updateZOffset(other.getZOffset());
        updateUniboob(other.isUniboob());
        updateCleavage(other.getCleavage());
    }

    public Vec3d getOffsets() {
        return new Vec3d(xOffset, yOffset, zOffset);
    }

    public void updateOffsets(Vec3d offsets) {
        updateXOffset((float) offsets.x);
        updateYOffset((float) offsets.y);
        updateZOffset((float) offsets.z);
    }

    public float getXOffset() {
        return xOffset;
    }

    public void setXOffset(float value) {
        this.xOffset = value;
    }

    public void setYOffset(float value) {
        this.yOffset = value;
    }

    public void setZOffset(float value) {
        this.zOffset = value;
    }

    public void setCleavage(float value) {
        this.cleavage = value;
    }

    public void setUniboob(boolean value) {
        this.uniboob = value;
    }

    public boolean updateXOffset(float value) {
        return updateValue(ClientConfiguration.BREASTS_OFFSET_X, value, v -> this.xOffset = v);
    }

    public float getYOffset() {
        return yOffset;
    }

    public boolean updateYOffset(float value) {
        return updateValue(ClientConfiguration.BREASTS_OFFSET_Y, value, v -> this.yOffset = v);
    }

    public float getZOffset() {
        return zOffset;
    }

    public boolean updateZOffset(float value) {
        return updateValue(ClientConfiguration.BREASTS_OFFSET_Z, value, v -> this.zOffset = v);
    }

    public float getCleavage() {
        return cleavage;
    }

    public boolean updateCleavage(float value) {
        return updateValue(ClientConfiguration.BREASTS_CLEAVAGE, value, v -> this.cleavage = v);
    }

    public boolean isUniboob() {
        return uniboob;
    }

    public boolean updateUniboob(boolean value) {
        return updateValue(ClientConfiguration.BREASTS_UNIBOOB, value, v -> this.uniboob = v);
    }

    public boolean copyFrom(Configuration copyFrom) {
        return updateFrom(ClientConfiguration.BREASTS_OFFSET_X, copyFrom, v -> this.xOffset = v) |
               updateFrom(ClientConfiguration.BREASTS_OFFSET_Y, copyFrom, v -> this.yOffset = v) |
               updateFrom(ClientConfiguration.BREASTS_OFFSET_Z, copyFrom, v -> this.zOffset = v) |
               updateFrom(ClientConfiguration.BREASTS_CLEAVAGE, copyFrom, v -> this.cleavage = v) |
               updateFrom(ClientConfiguration.BREASTS_UNIBOOB, copyFrom, v -> this.uniboob = v);
    }
}
