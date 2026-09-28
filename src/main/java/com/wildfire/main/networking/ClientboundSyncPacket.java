/*
 * Wildfire's Female Gender Mod is a female gender mod created for Minecraft.
 * Copyright (C) 2023-present WildfireRomeo
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 */

package com.wildfire.main.networking;

import com.wildfire.main.WildfireGender;
import com.wildfire.main.entitydata.PlayerConfig;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ClientboundSyncPacket extends AbstractSyncPacket {

    public ClientboundSyncPacket() {
        super();
    }

    public ClientboundSyncPacket(PlayerConfig plr) {
        super(plr);
    }

    public static class Handler implements IMessageHandler<ClientboundSyncPacket, IMessage> {
        @Override
        public IMessage onMessage(ClientboundSyncPacket message, MessageContext ctx) {
            Minecraft mc = Minecraft.getMinecraft();
            mc.addScheduledTask(() -> {
                if (mc.player == null || !mc.player.getUniqueID().equals(message.uuid)) {
                    PlayerConfig plr = WildfireGender.getOrAddPlayerById(message.uuid);
                    message.updatePlayer(plr);
                    plr.syncStatus = PlayerConfig.SyncStatus.SYNCED;
                }
            });
            return null;
        }
    }
}
