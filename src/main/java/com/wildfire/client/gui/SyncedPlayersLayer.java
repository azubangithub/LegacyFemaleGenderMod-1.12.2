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

import com.wildfire.client.WildfireGenderClient;
import com.wildfire.client.gui.screen.WardrobeBrowserScreen;
import com.wildfire.main.config.GeneralClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SyncedPlayersLayer {

    public static final SyncedPlayersLayer INSTANCE = new SyncedPlayersLayer();

    private SyncedPlayersLayer() {
    }

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.currentScreen instanceof WardrobeBrowserScreen) {
            return;
        }

        boolean shouldShow;
        switch (GeneralClientConfig.INSTANCE.alwaysShowList.get()) {
            case MOD_UI_ONLY:
                shouldShow = false;
                break;
            case TAB_LIST_OPEN:
                shouldShow = mc.gameSettings.keyBindPlayerList.isKeyDown();
                break;
            case ALWAYS:
            default:
                shouldShow = true;
                break;
        }

        if (shouldShow) {
            FontRenderer font = mc.fontRenderer;
            GuiHelper.drawSyncedPlayers(font, WildfireGenderClient.collectPlayerEntries());
        }
    }
}