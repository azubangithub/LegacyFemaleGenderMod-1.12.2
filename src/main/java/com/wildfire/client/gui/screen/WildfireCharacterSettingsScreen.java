/*
 * Wildfire's Female Gender Mod is a female gender mod created for Minecraft.
 * Copyright (C) 2023-present WildfireRomeo
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 */

package com.wildfire.client.gui.screen;

import com.wildfire.client.gui.WildfireButton;
import com.wildfire.client.gui.WildfireSlider;
import com.wildfire.main.WildfireGender;
import com.wildfire.main.config.ClientConfiguration;
import com.wildfire.main.entitydata.PlayerConfig;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

import java.util.UUID;

public class WildfireCharacterSettingsScreen extends BaseWildfireScreen {

    private static final ResourceLocation BACKGROUND = WildfireGender.rl("textures/gui/settings_bg.png");

    private WildfireSlider bounceSlider;
    private WildfireSlider floppySlider;
    private boolean bounceWarning = false;

    public WildfireCharacterSettingsScreen(GuiScreen parent, UUID uuid) {
        super(I18n.format("wildfire_gender.char_settings.title"), parent, uuid);
    }

    private String getPhysicsLabel(boolean enabled) {
        String status = enabled ? (TextFormatting.GREEN + I18n.format("wildfire_gender.label.enabled"))
                : (TextFormatting.RED + I18n.format("wildfire_gender.label.disabled"));
        return I18n.format("wildfire_gender.char_settings.physics", status);
    }

    private String getHideInArmorLabel(boolean showBreastsInArmor) {
        boolean hideInArmor = !showBreastsInArmor;
        String status = hideInArmor ? (TextFormatting.GREEN + I18n.format("wildfire_gender.label.enabled"))
                : (TextFormatting.RED + I18n.format("wildfire_gender.label.disabled"));
        return I18n.format("wildfire_gender.char_settings.hide_in_armor", status);
    }

    private String getArmorPhysicsLabel(boolean enabled) {
        String status = enabled ? (TextFormatting.GREEN + I18n.format("wildfire_gender.label.enabled"))
                : (TextFormatting.RED + I18n.format("wildfire_gender.label.disabled"));
        return I18n.format("wildfire_gender.char_settings.override_armor_physics", status);
    }

    private String getHurtSoundsLabel(boolean enabled) {
        String status = enabled ? (TextFormatting.GREEN + I18n.format("wildfire_gender.label.enabled"))
                : (TextFormatting.RED + I18n.format("wildfire_gender.label.disabled"));
        return I18n.format("wildfire_gender.char_settings.hurt_sounds", status);
    }

    @Override
    public void initGui() {
        this.buttonList.clear();
        if (this.mc == null) {
            return;
        }

        PlayerConfig aPlr = getPlayer();
        if (aPlr == null) {
            return;
        }

        int x = this.width / 2;
        int y = this.height / 2;
        int yPos = y - 47;
        int xPos = x - 79;

        // Close button 'X' at top-right
        this.buttonList.add(new WildfireButton(this.width / 2 + 73, yPos - 11, 9, 9, "X",
                button -> this.mc.displayGuiScreen(parent)));

        // Row 1: Breast Physics
        this.buttonList.add(new WildfireButton(xPos, yPos, 157, 20, getPhysicsLabel(aPlr.hasBreastPhysics()), button -> {
            boolean enablePhysics = !aPlr.hasBreastPhysics();
            if (aPlr.updateBreastPhysics(enablePhysics)) {
                button.setMessage(getPhysicsLabel(enablePhysics));
                PlayerConfig.saveGenderInfo(aPlr);
            }
        }));

        // Row 2: Hide In Armor
        this.buttonList.add(new WildfireButton(xPos, yPos + 20, 157, 20, getHideInArmorLabel(aPlr.showBreastsInArmor()), button -> {
            boolean enableShowInArmor = !aPlr.showBreastsInArmor();
            if (aPlr.updateShowBreastsInArmor(enableShowInArmor)) {
                button.setMessage(getHideInArmorLabel(enableShowInArmor));
                PlayerConfig.saveGenderInfo(aPlr);
            }
        }));

        // Row 3: Armor Physics
        this.buttonList.add(new WildfireButton(xPos, yPos + 40, 157, 20, getArmorPhysicsLabel(aPlr.getArmorPhysicsOverride()), button -> {
            boolean enableArmorPhysics = !aPlr.getArmorPhysicsOverride();
            if (aPlr.updateArmorPhysicsOverride(enableArmorPhysics)) {
                button.setMessage(getArmorPhysicsLabel(enableArmorPhysics));
                PlayerConfig.saveGenderInfo(aPlr);
            }
        }));

        // Row 4: Bounce Intensity slider (0.0 to 0.5f, 0.333f = 100%)
        this.bounceSlider = new WildfireSlider(xPos, yPos + 60, 157, 20, ClientConfiguration.BOUNCE_MULTIPLIER, aPlr.getBounceMultiplier(),
                value -> {},
                value -> {
                    int v = Math.round(value * 300.0f);
                    this.bounceWarning = v > 100;
                    return I18n.format("wildfire_gender.slider.bounce", v);
                },
                value -> {
                    if (aPlr.updateBounceMultiplier(value)) {
                        PlayerConfig.saveGenderInfo(aPlr);
                    }
                });
        this.buttonList.add(this.bounceSlider);

        // Row 5: Breast Momentum slider (0.25f to 1.0f, default 0.75f = 75%)
        this.floppySlider = new WildfireSlider(xPos, yPos + 80, 157, 20, ClientConfiguration.FLOPPY_MULTIPLIER, aPlr.getFloppiness(),
                value -> {},
                value -> I18n.format("wildfire_gender.slider.floppy", Math.round(value * 100.0f)),
                value -> {
                    if (aPlr.updateFloppiness(value)) {
                        PlayerConfig.saveGenderInfo(aPlr);
                    }
                });
        this.buttonList.add(this.floppySlider);

        // Row 6: Female Hurt Sounds
        this.buttonList.add(new WildfireButton(xPos, yPos + 100, 157, 20, getHurtSoundsLabel(aPlr.hasHurtSounds()), button -> {
            boolean enableHurtSounds = !aPlr.hasHurtSounds();
            if (aPlr.updateHurtSounds(enableHurtSounds)) {
                button.setMessage(getHurtSoundsLabel(enableHurtSounds));
                PlayerConfig.saveGenderInfo(aPlr);
            }
        }));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(BACKGROUND);
        drawTexturedModalRect((this.width - 172) / 2, (this.height - 124) / 2, 0, 0, 172, 144);

        int x = this.width / 2;
        int y = this.height / 2;
        int yPos = y - 47;

        this.fontRenderer.drawString(this.title, x - 79, yPos - 10, 0x444444);

        String playerName = this.mc.player != null ? this.mc.player.getName() : "";
        this.drawCenteredString(this.fontRenderer, playerName, x, yPos - 30, 0xFFFFFF);

        super.drawScreen(mouseX, mouseY, partialTicks);

        if (this.bounceWarning) {
            this.drawCenteredString(this.fontRenderer, TextFormatting.ITALIC + I18n.format("wildfire_gender.tooltip.bounce_warning"),
                    x, y + 90, 0xFF6666);
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        if (bounceSlider != null) bounceSlider.save();
        if (floppySlider != null) floppySlider.save();
    }

    @Override
    public void onGuiClosed() {
        if (bounceSlider != null) bounceSlider.save();
        if (floppySlider != null) floppySlider.save();
        PlayerConfig plr = getPlayer();
        if (plr != null) {
            PlayerConfig.saveGenderInfo(plr);
        }
    }
}