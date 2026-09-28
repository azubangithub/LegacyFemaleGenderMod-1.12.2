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
 * Defines the texture data for a given armor piece when covering an entity's breasts
 */
public interface IBreastArmorTexture {

    Vec2i DEFAULT_TEXTURE_SIZE = new Vec2i(64, 32);
    Vec2i DEFAULT_DIMENSIONS = new Vec2i(4, 5);
    Vec2i DEFAULT_LEFT_UV = new Vec2i(16, 17);
    Vec2i DEFAULT_RIGHT_UV = DEFAULT_LEFT_UV.add(DEFAULT_DIMENSIONS.x(), 0);

    IBreastArmorTexture DEFAULT = new IBreastArmorTexture() {
    };

    /**
     * The size of the armor sprite in pixels
     *
     * @return A {@link Vec2i} indicating how large the texture file is
     */
    default Vec2i textureSize() {
        return DEFAULT_TEXTURE_SIZE;
    }

    /**
     * How large of an area from the sprite should be used for each breast
     *
     * @return A {@link Vec2i} indicating how large of an area should be grabbed from the texture sprite to display over the wearer's breasts
     */
    default Vec2i dimensions() {
        return DEFAULT_DIMENSIONS;
    }

    /**
     * Where the left breast should grab the texture from on the sprite
     *
     * @return A {@link Vec2i} indicating the UV to use for the left breast
     */
    default Vec2i leftUv() {
        return DEFAULT_LEFT_UV;
    }

    /**
     * Where the right breast should grab the texture from on the sprite
     *
     * @return A {@link Vec2i} indicating the UV to use for the right breast
     */
    default Vec2i rightUv() {
        return DEFAULT_RIGHT_UV;
    }
}