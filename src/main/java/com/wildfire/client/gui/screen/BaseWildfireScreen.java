/*
 * Wildfire's Female Gender Mod is a female gender mod created for Minecraft.
 * Copyright (C) 2023-present WildfireRomeo
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.wildfire.client.gui.screen;

import com.wildfire.client.gui.WildfireButton;
import com.wildfire.main.WildfireGender;
import com.wildfire.main.entitydata.PlayerConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;
import java.util.UUID;

public abstract class BaseWildfireScreen extends GuiScreen {

    public static void drawPlayerPreview(int posX, int posY, int scale, float mouseX, float mouseY, EntityLivingBase ent) {
        if (ent == null) {
            return;
        }
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) posX, (float) posY, 50.0F);
        GlStateManager.scale((float) (-scale), (float) scale, (float) scale);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);

        float prevRenderYawOffset = ent.renderYawOffset;
        float prevRotationYaw = ent.rotationYaw;
        float prevRotationPitch = ent.rotationPitch;
        float prevPrevRotationYawHead = ent.prevRotationYawHead;
        float prevRotationYawHead = ent.rotationYawHead;

        GlStateManager.rotate(135.0F, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);

        float pitch = -((float) Math.atan((double) (mouseY / 40.0F))) * 20.0F;
        float yaw = (float) Math.atan((double) (mouseX / 40.0F)) * 40.0F;
        GlStateManager.rotate(pitch, 1.0F, 0.0F, 0.0F);

        ent.renderYawOffset = (float) Math.atan((double) (mouseX / 40.0F)) * 20.0F;
        ent.rotationYaw = yaw;
        ent.rotationPitch = pitch;
        ent.rotationYawHead = ent.rotationYaw;
        ent.prevRotationYawHead = ent.rotationYaw;

        GlStateManager.translate(0.0F, 0.0F, 0.0F);

        // 1.12.2 Fix: Reset color, normal rescaling, and set full lightmap brightness so skin and head are not shaded black
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableRescaleNormal();
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);

        RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        rendermanager.setPlayerViewY(180.0F);
        rendermanager.setRenderShadow(false);
        rendermanager.renderEntity(ent, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);
        rendermanager.setRenderShadow(true);

        ent.renderYawOffset = prevRenderYawOffset;
        ent.rotationYaw = prevRotationYaw;
        ent.rotationPitch = prevRotationPitch;
        ent.prevRotationYawHead = prevPrevRotationYawHead;
        ent.rotationYawHead = prevRotationYawHead;

        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public static void drawExactPreview(int posX, int posY, int scale, EntityLivingBase ent) {
        if (ent == null) return;
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();

        // 1. Move to screen position
        GlStateManager.translate((float) posX, (float) posY, 50.0F);
        // 2. Scale
        GlStateManager.scale((float) (-scale), (float) scale, (float) scale);
        // 3. Flip Y for OpenGL GUI
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);

        // 4. Translate by half bounding box height (0.9F blocks) so waist/chest is centered at (posX, posY) exactly as in original
        GlStateManager.translate(0.0F, -ent.height / 2.0F, 0.0F);

        float f = ent.renderYawOffset;
        float f1 = ent.rotationYaw;
        float f2 = ent.rotationPitch;
        float f3 = ent.prevRotationYawHead;
        float f4 = ent.rotationYawHead;

        // Setup standard item lighting
        GlStateManager.rotate(135.0F, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);

        // Exact angles from original mod:
        // ANGLE = atan(-0.5)
        // PREVIEW_Y_BODY_ROT = 180.0f + ANGLE * 20.0f (local bodyYaw: ANGLE * 20.0f)
        // PREVIEW_Y_ROT = 180.0f + ANGLE * 40.0f (local headYaw: ANGLE * 40.0f)
        // PREVIEW_X_ROT = -ANGLE * 20.0f (pitch)
        float angle = (float) Math.atan(-0.5);
        float pitch = -angle * 20.0F;
        float bodyYaw = angle * 20.0F;
        float headYaw = angle * 40.0F;

        GlStateManager.rotate(-pitch, 1.0F, 0.0F, 0.0F);

        ent.renderYawOffset = bodyYaw;
        ent.rotationYaw = headYaw;
        ent.rotationPitch = pitch;
        ent.rotationYawHead = headYaw;
        ent.prevRotationYawHead = headYaw;

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableRescaleNormal();
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);

        RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        rendermanager.setPlayerViewY(180.0F);
        rendermanager.setRenderShadow(false);
        rendermanager.renderEntity(ent, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);
        rendermanager.setRenderShadow(true);

        ent.renderYawOffset = f;
        ent.rotationYaw = f1;
        ent.rotationPitch = f2;
        ent.prevRotationYawHead = f3;
        ent.rotationYawHead = f4;

        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    // Keira Emberlyn - The Mod's New Mascot
    protected static final ResourceLocation KEIRA_LOOK = WildfireGender.rl("textures/gui/mascot/keira_look.png");
    protected static final ResourceLocation KEIRA_WAVE = WildfireGender.rl("textures/gui/mascot/keira_wave.png");
    protected static final ResourceLocation KEIRA_LEATHER = WildfireGender.rl("textures/gui/mascot/keira_leather.png");
    protected static final int KEIRA_WIDTH = 610;
    protected static final int KEIRA_HEIGHT = 736;

    protected final UUID playerUUID;
    protected final GuiScreen parent;
    protected final String title;

    protected BaseWildfireScreen(String title, GuiScreen parent, UUID uuid) {
        this.title = title;
        this.parent = parent;
        this.playerUUID = uuid;
    }

    public PlayerConfig getPlayer() {
        return WildfireGender.getOrAddPlayerById(this.playerUUID);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        if (button instanceof WildfireButton) {
            ((WildfireButton) button).onClick();
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 1) { // ESC
            this.mc.displayGuiScreen(this.parent);
            if (this.mc.currentScreen == null) {
                this.mc.setIngameFocus();
            }
            return;
        }
        super.keyTyped(typedChar, keyCode);
    }

    public void drawTooltips(int mouseX, int mouseY) {
        for (GuiButton btn : this.buttonList) {
            if (btn instanceof WildfireButton && btn.isMouseOver()) {
                WildfireButton wBtn = (WildfireButton) btn;
                if (!wBtn.getTooltipLines().isEmpty()) {
                    drawHoveringText(wBtn.getTooltipLines(), mouseX, mouseY);
                    break;
                }
            }
        }
    }
}