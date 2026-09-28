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

import com.wildfire.api.IBreastArmorTexture;
import com.wildfire.api.IGenderArmor;
import com.wildfire.api.Vec2i;
import com.wildfire.api.impl.BreastArmorTexture;
import com.wildfire.client.render.WildfireModelRenderer.BreastModelBox;
import com.wildfire.client.render.WildfireModelRenderer.OverlayModelBox;
import com.wildfire.main.WildfireGender;
import com.wildfire.main.WildfireHelper;
import com.wildfire.main.config.GeneralClientConfig;
import com.wildfire.main.entitydata.Breasts;
import com.wildfire.main.entitydata.EntityConfig;
import com.wildfire.physics.BreastPhysics;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.ForgeHooksClient;
import org.lwjgl.opengl.GL11;

import java.util.Objects;

public class GenderLayer implements LayerRenderer<EntityLivingBase> {

    private static final OverlayModelBox lBreastWear = new OverlayModelBox(true, 64, 64, 17, 34, -4F, 0.0F, 0F, 4, 5, 3, 0.0F, false);
    private static final OverlayModelBox rBreastWear = new OverlayModelBox(false, 64, 64, 21, 34, 0, 0.0F, 0F, 4, 5, 3, 0.0F, false);
    private static boolean loggedFirstRender = false;

    private final RenderLivingBase<?> renderer;

    private BreastModelBox lBreast, rBreast;
    private BreastModelBox lBoobArmor, rBoobArmor;
    private float preBreastSize, preBreastOffsetZ;
    private IBreastArmorTexture textureData = BreastArmorTexture.DEFAULT;

    public GenderLayer(RenderLivingBase<?> renderer) {
        this.renderer = renderer;

        lBreast = new BreastModelBox(64, 64, 16, 17, -4F, 0.0F, 0F, 4, 5, 4, 0.0F, false);
        rBreast = new BreastModelBox(64, 64, 20, 17, 0, 0.0F, 0F, 4, 5, 4, 0.0F, false);

        lBoobArmor = new BreastModelBox(64, 32, 16, 17, -4F, 0.0F, 0F, 4, 5, 3, 0.0F, false);
        rBoobArmor = new BreastModelBox(64, 32, 20, 17, 0, 0.0F, 0F, 4, 5, 3, 0.0F, false);
    }

    @Override
    public void doRenderLayer(EntityLivingBase entity, float limbSwing, float limbSwingAmount, float partialTicks,
                              float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (GeneralClientConfig.INSTANCE.disableRendering.get()
                || (com.wildfire.client.WildfireGenderClient.INSTANCE != null && !com.wildfire.client.WildfireGenderClient.INSTANCE.renderBreasts)
                || (entity instanceof EntityPlayer && ((EntityPlayer) entity).isSpectator())) {
            return;
        }

        try {
            EntityConfig entityConfig = EntityConfig.getEntity(entity);
            if (entityConfig == null || !entityConfig.getGender().canHaveBreasts()) {
                return;
            }

            if (!loggedFirstRender) {
                loggedFirstRender = true;
                WildfireGender.LOGGER.info("GenderLayer active and rendering for: {} (gender: {})", entity.getName(), entityConfig.getGender());
            }

            ItemStack armorStack = entity.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
            IGenderArmor genderArmor = WildfireHelper.getArmorConfig(armorStack);
            final boolean isChestplateOccupied = genderArmor.coversBreasts();

            if (genderArmor.alwaysHidesBreasts() || (!entityConfig.showBreastsInArmor() && isChestplateOccupied)) {
                return;
            }

            ResourceLocation entityTexture = getBreastTexture(entity);
            if (entityTexture == null && !isChestplateOccupied) {
                return;
            }

            Breasts breasts = entityConfig.getBreasts();
            float breastOffsetX = Math.round((Math.round(breasts.getXOffset() * 100f) / 100f) * 10) / 10f;
            float breastOffsetY = -Math.round((Math.round(breasts.getYOffset() * 100f) / 100f) * 10) / 10f;
            float breastOffsetZ = -Math.round((Math.round(breasts.getZOffset() * 100f) / 100f) * 10) / 10f;

            BreastPhysics leftBreastPhysics = entityConfig.getLeftBreastPhysics();
            float bSize = leftBreastPhysics.getBreastSize(partialTicks);
            if (bSize < 0.02f && entityConfig.getGender().canHaveBreasts()) {
                bSize = entityConfig.getBustSize();
            }
            float outwardAngle = (Math.round(breasts.getCleavage() * 100f) / 100f) * 100f;
            outwardAngle = Math.min(outwardAngle, 10);

            resizeBox(genderArmor, bSize, breastOffsetZ);

            float lPhysPositionY = lerp(partialTicks, leftBreastPhysics.getPrePositionY(), leftBreastPhysics.getPositionY());
            float lPhysPositionX = lerp(partialTicks, leftBreastPhysics.getPrePositionX(), leftBreastPhysics.getPositionX());
            float leftBounceRotation = lerp(partialTicks, leftBreastPhysics.getPreBounceRotation(), leftBreastPhysics.getBounceRotation());

            float rPhysPositionY;
            float rPhysPositionX;
            float rightBounceRotation;
            if (breasts.isUniboob()) {
                rPhysPositionY = lPhysPositionY;
                rPhysPositionX = lPhysPositionX;
                rightBounceRotation = leftBounceRotation;
            } else {
                BreastPhysics rightBreastPhysics = entityConfig.getRightBreastPhysics();
                rPhysPositionY = lerp(partialTicks, rightBreastPhysics.getPrePositionY(), rightBreastPhysics.getPositionY());
                rPhysPositionX = lerp(partialTicks, rightBreastPhysics.getPrePositionX(), rightBreastPhysics.getPositionX());
                rightBounceRotation = lerp(partialTicks, rightBreastPhysics.getPreBounceRotation(), rightBreastPhysics.getBounceRotation());
            }

            float breastSize = bSize * 1.5f;
            if (breastSize > 0.7f) breastSize = 0.7f;
            if (bSize > 0.7f) breastSize = bSize;

            if (breastSize < 0.02f) return;

            float zOff = 0.0625f - (bSize * 0.0625f);
            breastSize = bSize + 0.5f * Math.abs(bSize - 0.7f) * 2f;

            float resistance = entityConfig.getArmorPhysicsOverride() || !isChestplateOccupied ? 0 : MathHelper.clamp(genderArmor.physicsResistance(), 0, 1);
            boolean breathingAnimation = entityConfig.canBreathe() && resistance <= 0.5F &&
                    (!entity.isInWater() || entity.isPotionActive(MobEffects.WATER_BREATHING));
            boolean bounceEnabled = entityConfig.hasBreastPhysics() && resistance < 1;

            boolean hasJacketLayer = entity instanceof EntityPlayer ? ((EntityPlayer) entity).isWearing(EnumPlayerModelParts.JACKET) : entityConfig.hasJacketLayer();

            // Ensure GL state is correct for raw quad rendering
            GlStateManager.enableTexture2D();
            GlStateManager.enableLighting();
            GlStateManager.enableRescaleNormal();

            renderBreastWithTransforms(entity, armorStack, scale, entityTexture, bounceEnabled,
                    lPhysPositionX, lPhysPositionY, leftBounceRotation, breastSize, breastOffsetX, breastOffsetY, breastOffsetZ, zOff, outwardAngle, breasts.isUniboob(),
                    isChestplateOccupied, breathingAnimation, true, hasJacketLayer);

            renderBreastWithTransforms(entity, armorStack, scale, entityTexture, bounceEnabled,
                    rPhysPositionX, rPhysPositionY, rightBounceRotation, breastSize, -breastOffsetX, breastOffsetY, breastOffsetZ, zOff, outwardAngle, breasts.isUniboob(),
                    isChestplateOccupied, breathingAnimation, false, hasJacketLayer);

            GlStateManager.color(1F, 1F, 1F, 1F);
        } catch (Exception e) {
            WildfireGender.LOGGER.error("Failed to render gender layer", e);
        }
    }

    private static float lerp(float pct, float start, float end) {
        return start + pct * (end - start);
    }

    protected void resizeBox(IGenderArmor genderArmor, float breastSize, float breastOffsetZ) {
        float reducer = -1;
        if (breastSize < 0.84f) reducer++;
        if (breastSize < 0.72f) reducer++;

        if (preBreastSize != breastSize || preBreastOffsetZ != breastOffsetZ) {
            lBreast = new BreastModelBox(64, 64, 16, 17, -4F, 0.0F, 0F, 4, 5, (int) (4 - breastOffsetZ - reducer), 0.0F, false);
            rBreast = new BreastModelBox(64, 64, 20, 17, 0, 0.0F, 0F, 4, 5, (int) (4 - breastOffsetZ - reducer), 0.0F, false);
            preBreastSize = breastSize;
            preBreastOffsetZ = breastOffsetZ;
        }

        if (genderArmor.coversBreasts() && !Objects.equals(textureData, genderArmor.texture())) {
            textureData = genderArmor.texture();
            Vec2i texSize = textureData.textureSize();
            Vec2i lUV = textureData.leftUv();
            Vec2i dim = textureData.dimensions();
            lBoobArmor = new BreastModelBox(texSize.x(), texSize.y(), lUV.x(), lUV.y(), -4F, 0.0F, 0F, dim.x(), dim.y(), 3, 0.0F, false);
            Vec2i rUV = textureData.rightUv();
            rBoobArmor = new BreastModelBox(texSize.x(), texSize.y(), rUV.x(), rUV.y(), 0, 0.0F, 0F, dim.x(), dim.y(), 3, 0.0F, false);
        }
    }

    private void renderBreastWithTransforms(EntityLivingBase entity, ItemStack armorStack, float scale,
                                            ResourceLocation entityTexture, boolean bounceEnabled, float physPositionX, float physPositionY, float bounceRotation,
                                            float breastSize, float breastOffsetX, float breastOffsetY, float breastOffsetZ, float zOff, float outwardAngle,
                                            boolean uniboob, boolean isChestplateOccupied, boolean breathingAnimation, boolean left, boolean hasJacketLayer) {
        GlStateManager.pushMatrix();
        try {
            if (entity.isChild()) {
                float ageScale = 0.5F;
                GlStateManager.scale(ageScale, ageScale, ageScale);
                GlStateManager.translate(0.0F, 24.0F * scale, 0.0F);
            } else if (entity.isSneaking()) {
                GlStateManager.translate(0.0F, 0.2F, 0.0F);
            }

            if (renderer.getMainModel() instanceof ModelBiped) {
                ModelBiped model = (ModelBiped) renderer.getMainModel();
                model.bipedBody.postRender(scale);
            }

            if (bounceEnabled) {
                GlStateManager.translate(physPositionX / 32f, physPositionY / 32f, 0);
            }

            GlStateManager.translate(breastOffsetX * 0.0625f, 0.05625f + (breastOffsetY * 0.0625f), zOff - 0.125f + (breastOffsetZ * 0.0625f));

            if (!uniboob) {
                GlStateManager.translate(-0.0625f * 2 * (left ? 1 : -1), 0, 0);
            }
            if (bounceEnabled) {
                GlStateManager.rotate(bounceRotation, 0, 1, 0);
            }
            if (!uniboob) {
                GlStateManager.translate(0.0625f * 2 * (left ? 1 : -1), 0, 0);
            }

            float rotation = breastSize;
            if (bounceEnabled) {
                GlStateManager.translate(0, -0.035f * breastSize, 0);
                rotation -= physPositionY / 12f;
            }
            rotation = Math.max(0.0f, Math.min(rotation, breastSize + 0.2f));
            rotation = Math.min(rotation, 1);

            if (isChestplateOccupied) {
                GlStateManager.translate(0, 0, 0.01f);
            }

            GlStateManager.rotate(left ? outwardAngle : -outwardAngle, 0, 1, 0);
            GlStateManager.rotate(-35F * rotation, 1, 0, 0);

            if (breathingAnimation) {
                float f5 = -MathHelper.cos(entity.ticksExisted * 0.09F) * 0.45F + 0.45F;
                GlStateManager.rotate(f5, 1, 0, 0);
            }

            GlStateManager.scale(0.9995f, 1f, 1f);

            renderBreast(entity, armorStack, scale, entityTexture, left, hasJacketLayer);
        } finally {
            GlStateManager.popMatrix();
        }
    }

    private ResourceLocation getBreastTexture(EntityLivingBase entity) {
        if (entity instanceof AbstractClientPlayer) {
            return ((AbstractClientPlayer) entity).getLocationSkin();
        }
        return null;
    }

    private void shiftForJacket() {
        GlStateManager.translate(0, 0, -0.015f);
        GlStateManager.scale(1.05f, 1.05f, 1.05f);
    }

    private void renderBreast(EntityLivingBase entity, ItemStack armorStack, float scale,
                              ResourceLocation entityTexture, boolean left, boolean hasJacketLayer) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        ItemStack chestStack = entity.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
        IGenderArmor genderArmor = WildfireHelper.getArmorConfig(chestStack);
        boolean isChestplateOccupied = genderArmor.coversBreasts();

        // Only render naked/jacket breast layer if the chest is NOT covered by an armor item
        if (entityTexture != null && !isChestplateOccupied) {
            this.renderer.bindTexture(entityTexture);

            float alpha = entity.isInvisible() ? 0.15F : 1F;
            GlStateManager.color(1F, 1F, 1F, alpha);
            GlStateManager.enableTexture2D();
            GlStateManager.enableLighting();
            GlStateManager.enableRescaleNormal();
            GlStateManager.enableAlpha();
            GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(
                    GlStateManager.SourceFactor.SRC_ALPHA,
                    GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                    GlStateManager.SourceFactor.ONE,
                    GlStateManager.DestFactor.ZERO);
            GlStateManager.disableCull();
            GlStateManager.depthMask(true);
            GlStateManager.enableDepth();

            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.OLDMODEL_POSITION_TEX_NORMAL);
            (left ? lBreast : rBreast).render(buffer, scale);
            tessellator.draw();

            if (hasJacketLayer) {
                GlStateManager.pushMatrix();
                shiftForJacket();
                buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.OLDMODEL_POSITION_TEX_NORMAL);
                (left ? lBreastWear : rBreastWear).render(buffer, scale);
                tessellator.draw();
                GlStateManager.popMatrix();
            }

            GlStateManager.enableCull();
            GlStateManager.disableBlend();
        }

        // Render Breast Armor
        if (!armorStack.isEmpty() && armorStack.getItem() instanceof ItemArmor) {
            ItemArmor armorItem = (ItemArmor) armorStack.getItem();
            GlStateManager.pushMatrix();
            GlStateManager.translate(left ? 0.001f : -0.001f, 0.015f, -0.035f);
            GlStateManager.scale(1.08f, 1.05f, 1.05f);

            BreastModelBox armorBox = left ? lBoobArmor : rBoobArmor;
            String materialName = armorItem.getArmorMaterial().getName();
            String defaultTexPath = String.format("textures/models/armor/%s_layer_1.png", materialName);
            String armorTexPath = ForgeHooksClient.getArmorTexture(entity, armorStack, defaultTexPath, EntityEquipmentSlot.CHEST, null);
            ResourceLocation armorTexture = armorTexPath != null ? new ResourceLocation(armorTexPath) : null;
            if (armorTexture != null) {
                this.renderer.bindTexture(armorTexture);

                GlStateManager.enableTexture2D();
                GlStateManager.enableLighting();
                GlStateManager.disableCull();
                GlStateManager.depthMask(true);
                GlStateManager.enableDepth();
                GlStateManager.enablePolygonOffset();
                GlStateManager.doPolygonOffset(-1.0F, -2.0F);

                if (armorItem.hasColor(armorStack)) {
                    int color = armorItem.getColor(armorStack);
                    float r = (float) (color >> 16 & 255) / 255.0F;
                    float g = (float) (color >> 8 & 255) / 255.0F;
                    float b = (float) (color & 255) / 255.0F;
                    GlStateManager.color(r, g, b, 1.0F);
                } else {
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                }

                buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.OLDMODEL_POSITION_TEX_NORMAL);
                armorBox.render(buffer, scale);
                tessellator.draw();

                // Leather overlay layer
                if (armorItem.hasOverlay(armorStack)) {
                    String defaultOverlayPath = String.format("textures/models/armor/%s_layer_1_overlay.png", materialName);
                    String overlayTexPath = ForgeHooksClient.getArmorTexture(entity, armorStack, defaultOverlayPath, EntityEquipmentSlot.CHEST, "overlay");
                    ResourceLocation overlayTexture = overlayTexPath != null ? new ResourceLocation(overlayTexPath) : null;
                    if (overlayTexture != null) {
                        this.renderer.bindTexture(overlayTexture);
                        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.OLDMODEL_POSITION_TEX_NORMAL);
                        armorBox.render(buffer, scale);
                        tessellator.draw();
                    }
                }

                GlStateManager.disablePolygonOffset();
                GlStateManager.enableCull();
            }

            GlStateManager.popMatrix();
        }
    }

    @Override
    public boolean shouldCombineTextures() {
        return true;
    }
}
