/*
 * Wildfire's Female Gender Mod is a female gender mod created for Minecraft.
 * Copyright (C) 2023-present WildfireRomeo
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 */

package com.wildfire.main.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.minecraft.util.math.MathHelper;

public class FloatConfigKey extends NumberConfigKey<Float> {

    public FloatConfigKey(String key, Float defaultValue) {
        super(key, defaultValue);
    }

    public FloatConfigKey(String key, float defaultValue, float minInclusive, float maxInclusive) {
        super(key, defaultValue, minInclusive, maxInclusive);
    }

    @Override
    protected Float read(JsonElement element) {
        return MathHelper.clamp(super.read(element), getMinInclusive(), getMaxInclusive());
    }

    @Override
    protected Float fromPrimitive(JsonPrimitive primitive) {
        return primitive.getAsFloat();
    }

    public float getMinInclusive() {
        return minInclusive == null ? -Float.MAX_VALUE : minInclusive;
    }

    public float getMaxInclusive() {
        return maxInclusive == null ? Float.MAX_VALUE : maxInclusive;
    }
}