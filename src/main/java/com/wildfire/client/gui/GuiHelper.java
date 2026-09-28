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

import com.wildfire.main.WildfireGender;
import com.wildfire.main.WildfireLang;
import com.wildfire.main.entitydata.PlayerConfig;
import com.wildfire.main.text.TextComponentUtil;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;

import java.util.List;

public class GuiHelper {

    private GuiHelper() {
    }

    public static void drawSyncedPlayers(FontRenderer font, List<NetworkPlayerInfo> syncedPlayers) {
        if (syncedPlayers.isEmpty()) {
            return;
        }
        font.drawStringWithShadow(WildfireLang.WARDROBE_PLAYERS_USING.translateColored(TextFormatting.AQUA).getFormattedText(), 5, 5, 0xFFFFFF);

        int yPos = 18;
        for (NetworkPlayerInfo entry : syncedPlayers) {
            PlayerConfig cfg = WildfireGender.getPlayerById(entry.getGameProfile().getId());
            if (cfg != null) {
                ITextComponent text = TextComponentUtil.build(entry.getGameProfile(), " - ", cfg.getGender());
                font.drawStringWithShadow(text.getFormattedText(), 10, yPos, 0xFFFFFF);
                yPos += 10;
            }
        }
    }
}