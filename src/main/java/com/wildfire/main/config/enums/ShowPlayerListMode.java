/*
 * Wildfire's Female Gender Mod is a female gender mod created for Minecraft.
 * Copyright (C) 2023-present WildfireRomeo
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 */

package com.wildfire.main.config.enums;

import com.wildfire.main.WildfireLang;
import com.wildfire.main.text.IHasTextComponent.IHasEnumNameTextComponent;
import com.wildfire.main.text.ILangEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;

public enum ShowPlayerListMode implements IHasEnumNameTextComponent {
    MOD_UI_ONLY(WildfireLang.PLAYER_LIST_MODE_MOD_UI, WildfireLang.PLAYER_LIST_MODE_MOD_UI_TOOLTIP),
    TAB_LIST_OPEN(WildfireLang.PLAYER_LIST_MODE_TAB_LIST, WildfireLang.PLAYER_LIST_MODE_TAB_LIST_TOOLTIP),
    ALWAYS(WildfireLang.PLAYER_LIST_MODE_ALWAYS, WildfireLang.PLAYER_LIST_MODE_ALWAYS_TOOLTIP);

    private final ILangEntry name;
    private final ILangEntry tooltip;

    ShowPlayerListMode(ILangEntry name, ILangEntry tooltip) {
        this.name = name;
        this.tooltip = tooltip;
    }

    public ShowPlayerListMode next() {
        return values()[(this.ordinal() + 1) % values().length];
    }

    public String getTooltipText() {
        if (this == TAB_LIST_OPEN) {
            String keyName = Minecraft.getMinecraft().gameSettings.keyBindPlayerList.getDisplayName();
            return tooltip.asString(keyName);
        }
        return tooltip.asString();
    }

    public String tooltip() {
        return getTooltipText();
    }

    @Override
    public ITextComponent getTextComponent() {
        return name.translate();
    }
}
