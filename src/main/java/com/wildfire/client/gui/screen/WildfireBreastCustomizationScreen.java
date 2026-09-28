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
import com.wildfire.main.config.ClientConfiguration;
import com.wildfire.main.entitydata.Breasts;
import com.wildfire.main.entitydata.PlayerConfig;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;

import java.util.UUID;
import java.util.function.Consumer;

public class WildfireBreastCustomizationScreen extends BaseWildfireScreen {

    private WildfireSlider breastSlider;
    private WildfireSlider xOffsetBoobSlider;
    private WildfireSlider yOffsetBoobSlider;
    private WildfireSlider zOffsetBoobSlider;
    private WildfireSlider cleavageSlider;
    private WildfireButton btnDualPhysics;
    private WildfireButton btnPresets;
    private WildfireButton btnCustomization;
    private WildfireButton btnAddPreset;
    private WildfireButton btnDeletePreset;
    private Tab currentTab = Tab.CUSTOMIZATION;

    public WildfireBreastCustomizationScreen(GuiScreen parent, UUID uuid) {
        super(I18n.format("wildfire_gender.appearance_settings.title"), parent, uuid);
    }

    private String getDualPhysicsLabel(boolean uniboob) {
        String val = uniboob ? I18n.format("wildfire_gender.label.no") : I18n.format("wildfire_gender.label.yes");
        return I18n.format("wildfire_gender.breast_customization.dual_physics", val);
    }

    @Override
    public void initGui() {
        this.buttonList.clear();
        if (this.mc == null) {
            return;
        }

        int j = this.height / 2 - 11;
        PlayerConfig plr = getPlayer();
        if (plr == null) {
            return;
        }
        Breasts breasts = plr.getBreasts();
        Consumer<Float> onSave = value -> PlayerConfig.saveGenderInfo(plr);

        // Close button 'X' at top right: width / 2 + 178, j - 72, 9, 9
        this.buttonList.add(new WildfireButton(this.width / 2 + 178, j - 72, 9, 9, "X",
                button -> this.mc.displayGuiScreen(parent)));

        // Tab buttons: width / 2 + 30, j - 60 (width 78, height 10)
        this.btnCustomization = new WildfireButton(this.width / 2 + 30, j - 60, 78, 10,
                I18n.format("wildfire_gender.breast_customization.tab_customization"), button -> {
            this.currentTab = Tab.CUSTOMIZATION;
            this.updatePresetTab();
        });
        this.buttonList.add(this.btnCustomization);

        this.btnPresets = new WildfireButton(this.width / 2 + 31 + 79, j - 60, 78, 10,
                I18n.format("wildfire_gender.breast_customization.tab_presets"), button -> {
            this.currentTab = Tab.PRESETS;
            this.updatePresetTab();
        });
        this.buttonList.add(this.btnPresets);

        // Sliders (exact original config bounds and offsets):
        // 1. Breast Size slider (0.0 to 0.8 -> 0% to 100%)
        this.breastSlider = new WildfireSlider(this.width / 2 + 30, j - 48, 158, 20, ClientConfiguration.BUST_SIZE, plr.getBustSize(),
                plr::updateBustSize,
                val -> I18n.format("wildfire_gender.wardrobe.slider.breast_size", Math.round(val * 125.0f)),
                onSave::accept);
        this.buttonList.add(this.breastSlider);

        // 2. Separation slider (-1.0 to 1.0 -> -10 to 10)
        this.xOffsetBoobSlider = new WildfireSlider(this.width / 2 + 30, j - 27, 158, 20, ClientConfiguration.BREASTS_OFFSET_X, breasts.getXOffset(),
                breasts::updateXOffset,
                val -> I18n.format("wildfire_gender.wardrobe.slider.separation", Math.round((float) Math.round(val * 100.0f) / 100.0f * 10.0f)),
                onSave::accept);
        this.buttonList.add(this.xOffsetBoobSlider);

        // 3. Height slider (-1.0 to 1.0 -> -10 to 10)
        this.yOffsetBoobSlider = new WildfireSlider(this.width / 2 + 30, j - 6, 158, 20, ClientConfiguration.BREASTS_OFFSET_Y, breasts.getYOffset(),
                breasts::updateYOffset,
                val -> I18n.format("wildfire_gender.wardrobe.slider.height", Math.round((float) Math.round(val * 100.0f) / 100.0f * 10.0f)),
                onSave::accept);
        this.buttonList.add(this.yOffsetBoobSlider);

        // 4. Depth slider (-1.0 to 0.0 -> -10 to 0)
        this.zOffsetBoobSlider = new WildfireSlider(this.width / 2 + 30, j + 15, 158, 20, ClientConfiguration.BREASTS_OFFSET_Z, breasts.getZOffset(),
                breasts::updateZOffset,
                val -> I18n.format("wildfire_gender.wardrobe.slider.depth", Math.round((float) Math.round(val * 100.0f) / 100.0f * 10.0f)),
                onSave::accept);
        this.buttonList.add(this.zOffsetBoobSlider);

        // 5. Cleavage / Rotation slider (0.0 to 0.1 -> 0 to 10)
        this.cleavageSlider = new WildfireSlider(this.width / 2 + 30, j + 36, 158, 20, ClientConfiguration.BREASTS_CLEAVAGE, breasts.getCleavage(),
                breasts::updateCleavage,
                val -> I18n.format("wildfire_gender.wardrobe.slider.rotation", Math.round((float) Math.round(val * 100.0f) / 100.0f * 100.0f)),
                onSave::accept);
        this.buttonList.add(this.cleavageSlider);

        // 6. Dual-Physics toggle
        this.btnDualPhysics = new WildfireButton(this.width / 2 + 30, j + 57, 158, 20,
                getDualPhysicsLabel(breasts.isUniboob()), button -> {
            boolean isUniboob = !breasts.isUniboob();
            if (breasts.updateUniboob(isUniboob)) {
                button.setMessage(getDualPhysicsLabel(isUniboob));
                PlayerConfig.saveGenderInfo(plr);
            }
        });
        this.buttonList.add(this.btnDualPhysics);

        // Presets Tab buttons
        this.btnAddPreset = new WildfireButton(this.width / 2 + 31 + 79, j + 80, 78, 12,
                I18n.format("wildfire_gender.breast_customization.presets.add_new"), button -> {
            plr.updateBustSize(0.75f);
            breasts.updateXOffset(0.0f);
            breasts.updateYOffset(0.0f);
            breasts.updateZOffset(0.0f);
            breasts.updateCleavage(0.0f);
            PlayerConfig.saveGenderInfo(plr);
            this.initGui();
        });
        this.buttonList.add(this.btnAddPreset);

        this.btnDeletePreset = new WildfireButton(this.width / 2 + 30, j + 80, 78, 12,
                I18n.format("wildfire_gender.breast_customization.presets.delete"), button -> {});
        this.btnDeletePreset.enabled = false;
        this.buttonList.add(this.btnDeletePreset);

        this.currentTab = Tab.CUSTOMIZATION;
        this.updatePresetTab();
    }

    private void updatePresetTab() {
        boolean displayBreastSettings = this.currentTab == Tab.CUSTOMIZATION;
        this.breastSlider.visible = displayBreastSettings;
        this.xOffsetBoobSlider.visible = displayBreastSettings;
        this.yOffsetBoobSlider.visible = displayBreastSettings;
        this.zOffsetBoobSlider.visible = displayBreastSettings;
        this.cleavageSlider.visible = displayBreastSettings;
        this.btnDualPhysics.visible = displayBreastSettings;
        this.btnCustomization.enabled = this.currentTab != Tab.CUSTOMIZATION;
        this.btnPresets.enabled = this.currentTab != Tab.PRESETS;
        this.btnAddPreset.visible = this.currentTab == Tab.PRESETS;
        this.btnDeletePreset.visible = this.currentTab == Tab.PRESETS;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        int x = this.width / 2;
        int y = this.height / 2;

        // Exact original panel fills:
        drawRect(x + 28, y - 64 - 21, x + 190, y + 68, 0x55000000);
        drawRect(x + 29, y - 63 - 21, x + 189, y - 60, 0x55000000);
        this.fontRenderer.drawString(this.title, x + 32, y - 60 - 21, 0xFFFFFF);

        // Player preview using exact original positioning (x - 102, y + 75, scale 200) and angles:
        if (this.mc != null && this.mc.world != null) {
            EntityPlayer playerEnt = this.mc.world.getPlayerEntityByUUID(this.playerUUID);
            if (playerEnt == null && this.mc.player != null && this.mc.player.getUniqueID().equals(this.playerUUID)) {
                playerEnt = this.mc.player;
            }
            if (playerEnt != null) {
                int xP = x - 102;
                int yP = y + 75;
                drawExactPreview(xP, yP, 200, playerEnt);
            }
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        saveAllSliders();
    }

    private void saveAllSliders() {
        if (breastSlider != null) breastSlider.save();
        if (xOffsetBoobSlider != null) xOffsetBoobSlider.save();
        if (yOffsetBoobSlider != null) yOffsetBoobSlider.save();
        if (zOffsetBoobSlider != null) zOffsetBoobSlider.save();
        if (cleavageSlider != null) cleavageSlider.save();
    }

    @Override
    public void onGuiClosed() {
        saveAllSliders();
        PlayerConfig plr = getPlayer();
        if (plr != null) {
            PlayerConfig.saveGenderInfo(plr);
        }
    }

    private enum Tab {
        CUSTOMIZATION,
        PRESETS
    }
}
