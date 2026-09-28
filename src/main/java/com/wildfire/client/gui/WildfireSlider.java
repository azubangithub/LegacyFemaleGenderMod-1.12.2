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

import com.wildfire.main.config.FloatConfigKey;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import org.lwjgl.input.Keyboard;

import java.util.function.Consumer;
import java.util.function.Function;

public class WildfireSlider extends GuiButton {

    private final double minValue;
    private final double maxValue;
    private final Consumer<Float> valueUpdate;
    private final Function<Float, String> messageUpdate;
    private final Consumer<Float> onSave;

    private float sliderValue;
    private float lastValue;
    private boolean changed;
    private boolean dragging;
    private double arrowKeyStep = 0.05;

    public WildfireSlider(int xPos, int yPos, int width, int height, FloatConfigKey config, double currentVal,
                          Consumer<Float> valueUpdate, Function<Float, String> messageUpdate, Consumer<Float> onSave) {
        this(xPos, yPos, width, height, config.getMinInclusive(), config.getMaxInclusive(), currentVal, valueUpdate, messageUpdate, onSave);
    }

    public WildfireSlider(int xPos, int yPos, int width, int height, double minVal, double maxVal, double currentVal,
                          Consumer<Float> valueUpdate, Function<Float, String> messageUpdate, Consumer<Float> onSave) {
        super(0, xPos, yPos, width, height, "");
        this.minValue = minVal;
        this.maxValue = maxVal;
        this.valueUpdate = valueUpdate;
        this.messageUpdate = messageUpdate;
        this.onSave = onSave;
        setValueInternal(currentVal);
    }

    public void setArrowKeyStep(double arrowKeyStep) {
        this.arrowKeyStep = arrowKeyStep;
    }

    protected void updateMessage() {
        if (this.messageUpdate != null) {
            this.displayString = this.messageUpdate.apply(lastValue);
        }
    }

    protected void applyValue() {
        float newValue = getFloatValue();
        if (lastValue != newValue) {
            if (valueUpdate != null) {
                valueUpdate.accept(newValue);
            }
            lastValue = newValue;
            changed = true;
        }
    }

    public void save() {
        if (changed) {
            if (onSave != null) {
                onSave.accept(lastValue);
            }
            changed = false;
        }
    }

    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        if (super.mousePressed(mc, mouseX, mouseY)) {
            this.sliderValue = (float) (mouseX - (this.x + 2)) / (float) (this.width - 4);
            this.sliderValue = MathHelper.clamp(this.sliderValue, 0.0F, 1.0F);
            this.dragging = true;
            applyValue();
            updateMessage();
            return true;
        }
        return false;
    }

    @Override
    protected void mouseDragged(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible && this.dragging) {
            this.sliderValue = (float) (mouseX - (this.x + 2)) / (float) (this.width - 4);
            this.sliderValue = MathHelper.clamp(this.sliderValue, 0.0F, 1.0F);
            applyValue();
            updateMessage();
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY) {
        if (this.dragging) {
            this.dragging = false;
            save();
        }
    }

    public boolean handleKeyInput(char typedChar, int keyCode) {
        if (!this.enabled || !this.visible) {
            return false;
        }
        if (keyCode == Keyboard.KEY_LEFT || keyCode == Keyboard.KEY_RIGHT) {
            this.sliderValue += (keyCode == Keyboard.KEY_LEFT ? -arrowKeyStep : arrowKeyStep);
            this.sliderValue = MathHelper.clamp(this.sliderValue, 0.0F, 1.0F);
            applyValue();
            updateMessage();
            save();
            return true;
        }
        return false;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if (!this.visible) {
            return;
        }
        this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;

        // Outer border
        drawRect(this.x, this.y, this.x + this.width, this.y + this.height, 84 << 24);

        // Background
        drawRect(this.x + 1, this.y + 1, this.x + this.width - 1, this.y + this.height - 1, 0x80222222);

        // Inner blue filler
        int xPos = this.x + 2 + (int) (this.sliderValue * (this.width - 3));
        drawRect(this.x + 1, this.y + 1, xPos - 1, this.y + this.height - 1, this.enabled ? 0xB4222266 : 0xB4111133);

        if (this.enabled) {
            int xPos2 = this.x + 3 + (int) (this.sliderValue * (this.width - 4));
            drawRect(xPos2 - 2, this.y + 1, xPos2, this.y + this.height - 1, 0x78FFFFFF);
        }

        if (this.hovered && this.enabled) {
            drawRect(this.x, this.y, this.x + this.width, this.y + 1, 0x88FFFFFF);
            drawRect(this.x, this.y + this.height - 1, this.x + this.width, this.y + this.height, 0x88FFFFFF);
            drawRect(this.x, this.y, this.x + 1, this.y + this.height, 0x88FFFFFF);
            drawRect(this.x + this.width - 1, this.y, this.x + this.width, this.y + this.height, 0x88FFFFFF);
        }

        mouseDragged(mc, mouseX, mouseY);

        int textColor = (this.hovered && this.enabled) || changed ? 0xFFFF55 : 0xFFFFFF;
        if (!this.enabled) {
            textColor = 0x666666;
        }
        this.drawCenteredString(mc.fontRenderer, this.displayString, this.x + this.width / 2, this.y + (this.height - 8) / 2, textColor);
    }

    public float getFloatValue() {
        return (float) MathHelper.clamp(getValue(), this.minValue, this.maxValue);
    }

    public double getValue() {
        return this.sliderValue * (maxValue - minValue) + minValue;
    }

    private void setValueInternal(double value) {
        double clamped = MathHelper.clamp(value, this.minValue, this.maxValue);
        this.sliderValue = (float) MathHelper.clamp((clamped - this.minValue) / (this.maxValue - this.minValue), 0.0, 1.0);
        this.lastValue = (float) clamped;
        updateMessage();
    }
}
