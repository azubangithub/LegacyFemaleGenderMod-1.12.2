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
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandler {

    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(WildfireGender.MODID);
    private static int packetId = 0;

    public static void init() {
        INSTANCE.registerMessage(ServerboundSyncPacket.Handler.class, ServerboundSyncPacket.class, packetId++, Side.SERVER);
        INSTANCE.registerMessage(ClientboundSyncPacket.Handler.class, ClientboundSyncPacket.class, packetId++, Side.CLIENT);
    }

    public static void sendToServer(net.minecraftforge.fml.common.network.simpleimpl.IMessage message) {
        INSTANCE.sendToServer(message);
    }

    public static void sendTo(net.minecraftforge.fml.common.network.simpleimpl.IMessage message, net.minecraft.entity.player.EntityPlayerMP player) {
        INSTANCE.sendTo(message, player);
    }
}
