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
import com.wildfire.api.Vec2i;

import java.util.Objects;

/**
 * Standard implementation of {@link IBreastArmorTexture}
 */
public class BreastArmorTexture implements IBreastArmorTexture {

    private final Vec2i textureSize;
    private final Vec2i leftUv;
    private final Vec2i rightUv;
    private final Vec2i dimensions;

    public BreastArmorTexture(Vec2i textureSize, Vec2i leftUv, Vec2i rightUv, Vec2i dimensions) {
        this.textureSize = textureSize != null ? textureSize : DEFAULT_TEXTURE_SIZE;
        this.leftUv = leftUv != null ? leftUv : DEFAULT_LEFT_UV;
        this.rightUv = rightUv != null ? rightUv : DEFAULT_RIGHT_UV;
        this.dimensions = dimensions != null ? dimensions : DEFAULT_DIMENSIONS;
    }

    @Override
    public Vec2i textureSize() {
        return textureSize;
    }

    @Override
    public Vec2i leftUv() {
        return leftUv;
    }

    @Override
    public Vec2i rightUv() {
        return rightUv;
    }

    @Override
    public Vec2i dimensions() {
        return dimensions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BreastArmorTexture that = (BreastArmorTexture) o;
        return Objects.equals(textureSize, that.textureSize) &&
                Objects.equals(leftUv, that.leftUv) &&
                Objects.equals(rightUv, that.rightUv) &&
                Objects.equals(dimensions, that.dimensions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(textureSize, leftUv, rightUv, dimensions);
    }
}