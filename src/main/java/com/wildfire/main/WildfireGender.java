/*
 * Wildfire's Female Gender Mod is a female gender mod created for Minecraft.
 * Copyright (C) 2023-present WildfireRomeo
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 */

package com.wildfire.main;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.wildfire.api.IGenderArmor;
import com.wildfire.api.WildfireAPI;
import com.wildfire.main.entitydata.BreastDataNBT;
import com.wildfire.main.entitydata.PlayerConfig;
import com.wildfire.main.networking.ClientboundSyncPacket;
import com.wildfire.main.networking.PacketHandler;
import com.wildfire.proxy.CommonProxy;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Mod(modid = WildfireGender.MODID, name = WildfireGender.NAME, version = WildfireGender.VERSION, acceptedMinecraftVersions = "[1.12.2]", dependencies = "after:metamorph;after:blockbuster")
public class WildfireGender {

    public static final String MODID = WildfireAPI.MODID;
    public static final String NAME = "LFGM (Legacy Female Gender Mod)";
    public static final String VERSION = "1.12.2-4.3.0";

    public static final Logger LOGGER = LogManager.getLogger(MODID);

    public static final UUID CREATOR_UUID = UUID.fromString("33c937ae-6bfc-423e-a38e-3a613e7c1256");
    public static final List<UUID> CONTRIBUTOR_UUIDS = Arrays.asList(
          UUID.fromString("70336328-0de7-430e-8cba-2779e2a05ab5"), // celeste
          UUID.fromString("64e57307-72e5-4f43-be9c-181e8e35cc9b"), // pupnewfster
          UUID.fromString("618a8390-51b1-43b2-a53a-ab72c1bbd8bd"), // Kichura
          UUID.fromString("33feda66-c706-4725-8983-f62e5e6cbee7"), // BlueLight
          UUID.fromString("ad8ee68c-0aa1-47f9-b29f-f92fa1ef66dc"), // Diademiemi
          UUID.fromString("8fb5e95d-7f41-4b4c-b8c5-4f15ea3fa2c1"), // Arcti.cc
          UUID.fromString("3f36f7e9-7459-43fe-87ce-4e8a5d47da80"), // IzzyBizzy45
          UUID.fromString("525b0455-15e9-49b7-b61d-f291e8ee6c5b"), // Powerless001
          UUID.fromString("6e0e0db3-19e9-4fa7-af76-a6d3651c57b9")  // A2 76
    );

    public static final LoadingCache<UUID, PlayerConfig> CACHE;

    static {
        CacheBuilder<Object, Object> builder = CacheBuilder.newBuilder();
        if (FMLCommonHandler.instance().getSide().isClient()) {
            builder.expireAfterAccess(15, TimeUnit.MINUTES);
        }
        CACHE = builder.build(new CacheLoader<UUID, PlayerConfig>() {
            @Override
            public PlayerConfig load(UUID key) {
                PlayerConfig config = new PlayerConfig(key);
                if (FMLCommonHandler.instance().getSide().isClient()) {
                    if (config.hasLocalConfig()) {
                        config.loadFromDisk(true);
                    }
                }
                return config;
            }
        });
    }

    @Mod.Instance(MODID)
    public static WildfireGender INSTANCE;

    @SidedProxy(clientSide = "com.wildfire.proxy.ClientProxy", serverSide = "com.wildfire.proxy.CommonProxy")
    public static CommonProxy proxy;

    private final Map<UUID, Set<EntityPlayerMP>> trackedPlayers = new HashMap<>();

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        INSTANCE = this;
        PacketHandler.init();
        MinecraftForge.EVENT_BUS.register(this);
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

    @Mod.EventHandler
    public void serverStopping(FMLServerStoppingEvent event) {
        trackedPlayers.clear();
        CACHE.invalidateAll();
    }

    public static ResourceLocation rl(String path) {
        return new ResourceLocation(MODID, path);
    }

    public static Set<EntityPlayerMP> getTrackers(EntityPlayer target) {
        return INSTANCE.trackedPlayers.getOrDefault(target.getUniqueID(), Collections.emptySet());
    }

    public static PlayerConfig getPlayerById(UUID id) {
        if (id == null) {
            return null;
        }
        try {
            return CACHE.getUnchecked(id);
        } catch (Exception e) {
            LOGGER.error("Failed to load player config for " + id, e);
            return null;
        }
    }

    public static PlayerConfig getOrAddPlayerById(UUID id) {
        if (id == null) {
            return null;
        }
        return CACHE.getUnchecked(id);
    }

    @SubscribeEvent
    public void onStartTracking(PlayerEvent.StartTracking evt) {
        if (evt.getTarget() instanceof EntityPlayer && evt.getEntityPlayer() instanceof EntityPlayerMP) {
            EntityPlayer toSync = (EntityPlayer) evt.getTarget();
            EntityPlayerMP sendTo = (EntityPlayerMP) evt.getEntityPlayer();

            trackedPlayers.computeIfAbsent(toSync.getUniqueID(), uuid -> new HashSet<>()).add(sendTo);

            PlayerConfig genderToSync = getPlayerById(toSync.getUniqueID());
            if (genderToSync != null) {
                PacketHandler.INSTANCE.sendTo(new ClientboundSyncPacket(genderToSync), sendTo);
            }
        }
    }

    @SubscribeEvent
    public void onStopTracking(PlayerEvent.StopTracking evt) {
        if (evt.getTarget() instanceof EntityPlayer && evt.getEntityPlayer() instanceof EntityPlayerMP) {
            UUID uuid = evt.getTarget().getUniqueID();
            Set<EntityPlayerMP> trackers = trackedPlayers.get(uuid);
            if (trackers != null && trackers.remove((EntityPlayerMP) evt.getEntityPlayer()) && trackers.isEmpty()) {
                trackedPlayers.remove(uuid);
            }
        }
    }

    @SubscribeEvent
    public void onPlayerLoggedOut(PlayerLoggedOutEvent event) {
        if (event.player != null) {
            trackedPlayers.remove(event.player.getUniqueID());
            CACHE.invalidate(event.player.getUniqueID());
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onEntitySpawn(EntityJoinWorldEvent event) {
        if (!event.getWorld().isRemote && event.getEntity() instanceof EntityItem) {
            EntityItem itemEntity = (EntityItem) event.getEntity();
            ItemStack stack = itemEntity.getItem();
            if (stack.getItem() instanceof ItemArmor && ((ItemArmor) stack.getItem()).armorType == EntityEquipmentSlot.CHEST) {
                if (BreastDataNBT.removeFromStack(stack)) {
                    itemEntity.setItem(stack);
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onRightClickArmorStand(PlayerInteractEvent.EntityInteractSpecific event) {
        EntityPlayer player = event.getEntityPlayer();
        if (!player.world.isRemote && event.getTarget() instanceof EntityArmorStand && !player.isSpectator()) {
            EntityArmorStand armorStand = (EntityArmorStand) event.getTarget();
            ItemStack heldStack = player.getHeldItem(event.getHand());

            if (heldStack.getItem() instanceof ItemArmor && ((ItemArmor) heldStack.getItem()).armorType == EntityEquipmentSlot.CHEST) {
                IGenderArmor armorConfig = WildfireHelper.getArmorConfig(heldStack);
                if (armorConfig.armorStandsCopySettings()) {
                    PlayerConfig playerConfig = getPlayerById(player.getUniqueID());
                    if (playerConfig != null) {
                        BreastDataNBT nbt = BreastDataNBT.fromPlayer(player, playerConfig);
                        if (nbt != null) {
                            nbt.writeToStack(heldStack);
                        }
                    }
                }
            }
        }
    }
}