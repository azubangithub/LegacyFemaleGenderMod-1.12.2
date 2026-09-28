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
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ServerboundSyncPacket extends AbstractSyncPacket {

    public ServerboundSyncPacket() {
        super();
    }

    public ServerboundSyncPacket(PlayerConfig plr) {
        super(plr);
    }

    public static class Handler implements IMessageHandler<ServerboundSyncPacket, IMessage> {
        @Override
        public IMessage onMessage(ServerboundSyncPacket message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            if (player != null && player.getUniqueID().equals(message.uuid)) {
                player.getServerWorld().addScheduledTask(() -> {
                    PlayerConfig plr = WildfireGender.getOrAddPlayerById(message.uuid);
                    message.updatePlayer(plr);
                    for (EntityPlayerMP tracker : WildfireGender.getTrackers(player)) {
                        PacketHandler.INSTANCE.sendTo(new ClientboundSyncPacket(plr), tracker);
                    }
                });
            }
            return null;
        }
    }
}
