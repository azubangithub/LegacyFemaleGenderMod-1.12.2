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
import com.wildfire.main.Gender;
import com.wildfire.main.WildfireGender;
import com.wildfire.main.entitydata.PlayerConfig;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

import java.util.UUID;

public class WardrobeBrowserScreen extends BaseWildfireScreen {

    private static final ResourceLocation BACKGROUND_FEMALE = WildfireGender.rl("textures/gui/wardrobe_bg2.png");
    private static final ResourceLocation BACKGROUND = WildfireGender.rl("textures/gui/wardrobe_bg3.png");

    public WardrobeBrowserScreen(GuiScreen parent, UUID uuid) {
        super(I18n.format("wildfire_gender.name"), parent, uuid);
    }

    private String getGenderLabel(Gender gender) {
        String prefix = I18n.format("wildfire_gender.label.gender") + " - ";
        switch (gender) {
            case MALE:
                return prefix + TextFormatting.BLUE + I18n.format("wildfire_gender.label.male");
            case FEMALE:
                return prefix + TextFormatting.LIGHT_PURPLE + I18n.format("wildfire_gender.label.female");
            case OTHER:
            default:
                return prefix + TextFormatting.GREEN + I18n.format("wildfire_gender.label.other");
        }
    }

    @Override
    public void initGui() {
        this.buttonList.clear();
        if (this.mc == null) {
            return;
        }

        PlayerConfig plr = getPlayer();
        if (plr == null) {
            return;
        }

        int y = this.height / 2;
        int buttonX = this.width / 2 - 42;

        // Gender toggle button
        this.buttonList.add(new WildfireButton(buttonX, y - 52, 158, 20, getGenderLabel(plr.getGender()), button -> {
            Gender nextGender;
            switch (plr.getGender()) {
                case MALE:
                    nextGender = Gender.FEMALE;
                    break;
                case FEMALE:
                    nextGender = Gender.OTHER;
                    break;
                case OTHER:
                default:
                    nextGender = Gender.MALE;
                    break;
            }
            if (plr.updateGender(nextGender)) {
                PlayerConfig.saveGenderInfo(plr);
                this.initGui();
            }
        }));

        int yOffset = 32;
        if (plr.getGender().canHaveBreasts()) {
            this.buttonList.add(new WildfireButton(buttonX, y - yOffset, 158, 20,
                    I18n.format("wildfire_gender.appearance_settings.title") + "...",
                    button -> this.mc.displayGuiScreen(new WildfireBreastCustomizationScreen(this, this.playerUUID))));
            yOffset -= 20;
        }
        this.buttonList.add(new WildfireButton(buttonX, y - yOffset, 158, 20,
                I18n.format("wildfire_gender.char_settings.title") + "...",
                button -> this.mc.displayGuiScreen(new WildfireCharacterSettingsScreen(this, this.playerUUID))));

        // Close button 'X' at top-right
        this.buttonList.add(new WildfireButton(this.width / 2 + 111, y - 63, 9, 9, "X",
                button -> this.mc.displayGuiScreen(parent)));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        PlayerConfig plr = getPlayer();
        ResourceLocation backgroundTexture = (plr != null && plr.getGender().canHaveBreasts()) ? BACKGROUND_FEMALE : BACKGROUND;

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(backgroundTexture);
        drawTexturedModalRect((this.width - 248) / 2, (this.height - 134) / 2, 0, 0, 248, 156);

        int x = this.width / 2;
        int y = this.height / 2;
        this.fontRenderer.drawString(this.title, x - 118, y - 62, 0x444444);

        // Draw Player preview in left box (exact original position & scale)
        if (this.mc != null && this.mc.world != null) {
            EntityPlayer playerEnt = this.mc.world.getPlayerEntityByUUID(this.playerUUID);
            if (playerEnt == null && this.mc.player != null && this.mc.player.getUniqueID().equals(this.playerUUID)) {
                playerEnt = this.mc.player;
            }
            if (playerEnt != null) {
                int xP = x - 83;
                int yP = y + 42;
                drawPlayerPreview(xP, yP, 45, (float) (xP - mouseX), (float) (y - 25 - mouseY), playerEnt);
            }
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}