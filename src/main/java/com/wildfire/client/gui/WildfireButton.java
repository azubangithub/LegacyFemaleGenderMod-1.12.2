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

package com.wildfire.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.text.ITextComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class WildfireButton extends GuiButton {

    private Consumer<WildfireButton> onPress;
    private List<String> tooltipLines = new ArrayList<>();

    public WildfireButton(int id, int x, int y, int width, int height, String text, Consumer<WildfireButton> onPress) {
        super(id, x, y, width, height, text);
        this.onPress = onPress;
    }

    public WildfireButton(int x, int y, int width, int height, String text, Consumer<WildfireButton> onPress) {
        this(0, x, y, width, height, text, onPress);
    }

    public WildfireButton(int x, int y, int width, int height, ITextComponent text, Consumer<WildfireButton> onPress) {
        this(0, x, y, width, height, text.getFormattedText(), onPress);
    }

    public WildfireButton(int x, int y, int width, int height, ITextComponent text, Consumer<WildfireButton> onPress, ITextComponent tooltip) {
        this(0, x, y, width, height, text.getFormattedText(), onPress);
        if (tooltip != null) {
            setTooltip(tooltip);
        }
    }

    public WildfireButton(int x, int y, int width, int height, String text, Consumer<WildfireButton> onPress, ITextComponent tooltip) {
        this(0, x, y, width, height, text, onPress);
        if (tooltip != null) {
            setTooltip(tooltip);
        }
    }

    public WildfireButton(int x, int y, int width, int height, String text, Consumer<WildfireButton> onPress, String tooltip) {
        this(0, x, y, width, height, text, onPress);
        if (tooltip != null) {
            setTooltip(tooltip);
        }
    }

    public void setTooltip(ITextComponent tooltip) {
        this.tooltipLines.clear();
        if (tooltip != null) {
            String[] split = tooltip.getFormattedText().split("\n");
            for (String s : split) {
                this.tooltipLines.add(s);
            }
        }
    }

    public void setTooltip(String tooltip) {
        this.tooltipLines.clear();
        if (tooltip != null) {
            String[] split = tooltip.split("\n");
            for (String s : split) {
                this.tooltipLines.add(s);
            }
        }
    }

    public List<String> getTooltipLines() {
        return tooltipLines;
    }

    public void setMessage(ITextComponent text) {
        this.displayString = text.getFormattedText();
    }

    public void setMessage(String text) {
        this.displayString = text;
    }

    private boolean transparent = false;

    public WildfireButton setTransparent(boolean transparent) {
        this.transparent = transparent;
        return this;
    }

    public boolean isTransparent() {
        return transparent;
    }

    public void onClick() {
        if (this.onPress != null) {
            this.onPress.accept(this);
        }
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if (!this.visible) {
            return;
        }
        this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;

        if (!this.transparent) {
            int clr = 0x54444444;
            if (!this.enabled) {
                clr = 0x54222222;
            } else if (this.hovered) {
                clr = 0x54666666;
            }
            drawRect(this.x, this.y, this.x + this.width, this.y + this.height, clr);
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        renderContents(mc, mouseX, mouseY, partialTicks);
    }

    protected void renderContents(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        int color = this.enabled ? (this.hovered ? 0xFFFF55 : 0xFFFFFF) : 0x666666;
        this.drawCenteredString(mc.fontRenderer, this.displayString, this.x + this.width / 2, this.y + (this.height - 8) / 2, color);
    }
}