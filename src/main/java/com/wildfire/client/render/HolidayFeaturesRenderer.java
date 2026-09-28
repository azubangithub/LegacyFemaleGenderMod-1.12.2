/*
 * Wildfire's Female Gender Mod is a female gender mod created for Minecraft.
 * Copyright (C) 2023-present WildfireRomeo
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 */

package com.wildfire.client.render;

import com.wildfire.main.WildfireGender;
import com.wildfire.main.entitydata.PlayerConfig;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;

import java.util.Calendar;

public class HolidayFeaturesRenderer implements LayerRenderer<AbstractClientPlayer> {

    private final RenderPlayer renderer;
    private final ModelRenderer santaHat;
    private static final ResourceLocation SANTA_HAT = WildfireGender.rl("textures/santa_hat.png");
    private final boolean christmas = isAroundChristmas();

    public HolidayFeaturesRenderer(RenderPlayer renderer) {
        this.renderer = renderer;
        this.santaHat = new ModelRenderer(renderer.getMainModel(), 0, 0);
        this.santaHat.setTextureSize(32, 32);
        this.santaHat.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.75F);
    }

    @Override
    public void doRenderLayer(AbstractClientPlayer player, float limbSwing, float limbSwingAmount,
                              float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (!christmas) return;

        PlayerConfig config = WildfireGender.getPlayerById(player.getUniqueID());
        if (config == null || !config.hasHolidayThemes()) {
            return;
        }

        GlStateManager.pushMatrix();
        try {
            if (player.isChild()) {
                float ageScale = 0.5F;
                GlStateManager.scale(ageScale, ageScale, ageScale);
                GlStateManager.translate(0.0F, 24.0F * scale, 0.0F);
            } else if (player.isSneaking()) {
                GlStateManager.translate(0.0F, 0.2F, 0.0F);
            }

            if (renderer.getMainModel() instanceof ModelBiped) {
                ModelBiped model = (ModelBiped) renderer.getMainModel();
                model.bipedHead.postRender(scale);
            }

            renderer.bindTexture(SANTA_HAT);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            santaHat.render(scale);
        } catch (Exception e) {
            WildfireGender.LOGGER.error("Failed to render santa hat", e);
        } finally {
            GlStateManager.popMatrix();
        }
    }

    public static boolean isAroundChristmas() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.MONTH) == Calendar.DECEMBER && calendar.get(Calendar.DATE) >= 24 && calendar.get(Calendar.DATE) <= 26;
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
}
