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

package com.wildfire.client;

import com.wildfire.api.IGenderArmor;
import com.wildfire.client.gui.SyncedPlayersLayer;
import com.wildfire.client.gui.screen.WardrobeBrowserScreen;
import com.wildfire.client.render.GenderLayer;
import com.wildfire.client.render.HolidayFeaturesRenderer;
import com.wildfire.main.WildfireGender;
import com.wildfire.main.WildfireHelper;
import com.wildfire.main.WildfireLang;
import com.wildfire.main.config.GeneralClientConfig;
import com.wildfire.main.entitydata.EntityConfig;
import com.wildfire.main.entitydata.PlayerConfig;
import com.wildfire.main.networking.PacketHandler;
import com.wildfire.main.networking.ServerboundSyncPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.lwjgl.input.Keyboard;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class WildfireGenderClient {

    public static WildfireGenderClient INSTANCE;

    public final KeyBinding configKey = new KeyBinding(
            WildfireLang.KEY_CONFIG.getTranslationKey(),
            KeyConflictContext.UNIVERSAL,
            Keyboard.KEY_G,
            WildfireLang.KEY_CATEGORY.getTranslationKey()
    );

    public final KeyBinding toggleKey = new KeyBinding(
            WildfireLang.KEY_TOGGLE.getTranslationKey(),
            KeyConflictContext.IN_GAME,
            Keyboard.KEY_NONE,
            WildfireLang.KEY_CATEGORY.getTranslationKey()
    );

    public boolean renderBreasts = true;
    private int timer = 0;

    public static void preInit(FMLPreInitializationEvent event) {
        INSTANCE = new WildfireGenderClient();
        ClientRegistry.registerKeyBinding(INSTANCE.configKey);
        ClientRegistry.registerKeyBinding(INSTANCE.toggleKey);

        MinecraftForge.EVENT_BUS.register(INSTANCE);
        MinecraftForge.EVENT_BUS.register(SyncedPlayersLayer.INSTANCE);
    }

    public static void init(FMLInitializationEvent event) {
    }

    public static void postInit(FMLPostInitializationEvent event) {
        RenderManager rm = Minecraft.getMinecraft().getRenderManager();
        Map<String, RenderPlayer> skinMap = rm.getSkinMap();
        for (RenderPlayer renderer : skinMap.values()) {
            renderer.addLayer(new GenderLayer(renderer));
            renderer.addLayer(new HolidayFeaturesRenderer(renderer));
        }
        Render<?> asRender = rm.getEntityClassRenderObject(EntityArmorStand.class);
        if (asRender instanceof RenderLivingBase) {
            ((RenderLivingBase<?>) asRender).addLayer(new GenderLayer((RenderLivingBase<?>) asRender));
        }
        WildfireGender.LOGGER.info("Registered GenderLayer and HolidayFeaturesRenderer to {} player skin renderers", skinMap.size());
    }

    public static CompletableFuture<PlayerConfig> loadGenderInfo(UUID uuid, boolean markForSync, boolean bypassQueue) {
        PlayerConfig cache = WildfireGender.getPlayerById(uuid);
        if (cache == null) {
            return CompletableFuture.completedFuture(null);
        }
        return loadGenderInfo(cache, markForSync, bypassQueue);
    }

    public static CompletableFuture<PlayerConfig> loadGenderInfo(PlayerConfig player, boolean markForSync, boolean bypassQueue) {
        if (player.hasLocalConfig()) {
            player.loadFromDisk(markForSync);
        }
        return CompletableFuture.completedFuture(player);
    }

    public static List<NetworkPlayerInfo> collectPlayerEntries() {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if (player == null || player.connection == null) {
            return Collections.emptyList();
        }
        return player.connection.getPlayerInfoMap().stream()
                .filter(entry -> !entry.getGameProfile().getId().equals(player.getUniqueID()))
                .filter(entry -> {
                    PlayerConfig cfg = WildfireGender.getPlayerById(entry.getGameProfile().getId());
                    return cfg != null && cfg.getSyncStatus() != PlayerConfig.SyncStatus.UNKNOWN;
                })
                .limit(40L)
                .collect(Collectors.toList());
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (configKey.isPressed()) {
            if (mc.currentScreen == null && mc.player != null) {
                mc.displayGuiScreen(new WardrobeBrowserScreen(null, mc.player.getUniqueID()));
            }
        }
        if (toggleKey.isPressed()) {
            renderBreasts ^= true;
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.world == null || mc.player == null) {
            return;
        }
        PlayerConfig clientConfig = WildfireGender.getPlayerById(mc.player.getUniqueID());
        timer++;

        // 20 ticks per second / 5 = 4 times per second
        if (clientConfig != null && clientConfig.needsSync && timer % 5 == 0) {
            PacketHandler.sendToServer(new ServerboundSyncPacket(clientConfig));
            clientConfig.needsSync = false;
        }
    }

    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        if (entity.world.isRemote && EntityConfig.isSupportedEntity(entity)) {
            EntityConfig cfg = EntityConfig.getEntity(entity);
            if (cfg != null) {
                if (entity instanceof EntityArmorStand) {
                    cfg.readFromStack(entity.getItemStackFromSlot(EntityEquipmentSlot.CHEST));
                }
                cfg.tickBreastPhysics(entity);
            }
        }
    }

    @SubscribeEvent
    public void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        WildfireGender.CACHE.invalidateAll();
        EntityConfig.CACHE.invalidateAll();
    }

    @SubscribeEvent
    public void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (stack.getItem() instanceof ItemArmor) {
            ItemArmor armor = (ItemArmor) stack.getItem();
            if (armor.armorType == EntityEquipmentSlot.CHEST) {
                EntityPlayer player = event.getEntityPlayer();
                if (player != null && GeneralClientConfig.INSTANCE.armorStat.get()) {
                    IGenderArmor armorConfig = WildfireHelper.getArmorConfig(stack);
                    if (armorConfig.coversBreasts() || armorConfig.physicsResistance() == 0) {
                        PlayerConfig playerConfig = WildfireGender.getPlayerById(player.getUniqueID());
                        if (playerConfig != null && playerConfig.getGender().canHaveBreasts()) {
                            float physResistance = armorConfig.physicsResistance();
                            event.getToolTip().add(WildfireLang.ARMOR_TOOLTIP.translateColored(TextFormatting.LIGHT_PURPLE,
                                    String.format("%.1f", physResistance)).getFormattedText());
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPlaySoundAtEntity(PlaySoundAtEntityEvent event) {
        if (GeneralClientConfig.INSTANCE.disableSoundReplacement.get()) {
            return;
        }
        if (event.getEntity() instanceof EntityPlayer) {
            EntityPlayer p = (EntityPlayer) event.getEntity();
            SoundEvent soundEvent = event.getSound();
            if (soundEvent != null && isPlayerHurtSound(soundEvent)) {
                PlayerConfig plr = WildfireGender.getPlayerById(p.getUniqueID());
                if (plr != null && plr.hasHurtSounds()) {
                    SoundEvent soundOverride = plr.getGender().getHurtSound();
                    if (soundOverride != null) {
                        event.setSound(soundOverride);
                        float pitchMult = plr.getVoicePitch() > 0 ? plr.getVoicePitch() : 1.0F;
                        event.setPitch(event.getPitch() * pitchMult);
                    }
                }
            }
        }
    }

    private boolean isPlayerHurtSound(SoundEvent sound) {
        return sound == SoundEvents.ENTITY_PLAYER_HURT
                || sound == SoundEvents.ENTITY_PLAYER_HURT_DROWN
                || sound == SoundEvents.ENTITY_PLAYER_HURT_ON_FIRE;
    }

    @SubscribeEvent
    public void onRenderNameTag(RenderLivingEvent.Specials.Pre<EntityLivingBase> event) {
        if (!(event.getEntity() instanceof EntityPlayer)) {
            return;
        }
        EntityPlayer player = (EntityPlayer) event.getEntity();
        ITextComponent nametag = getNametag(player.getUniqueID());
        if (nametag != null) {
            // Player nametag customization if needed
        }
    }

    private ITextComponent getNametag(UUID uuid) {
        EntityPlayerSP clientPlayer = Minecraft.getMinecraft().player;
        if (GeneralClientConfig.INSTANCE.hideOwnContributorTag.get() && clientPlayer != null && uuid.equals(clientPlayer.getUniqueID())) {
            return null;
        }
        if (WildfireGender.CREATOR_UUID.equals(uuid)) {
            return WildfireLang.NAME_TAG_CREATOR.translateColored(TextFormatting.LIGHT_PURPLE);
        } else if (WildfireGender.CONTRIBUTOR_UUIDS.contains(uuid)) {
            return WildfireLang.NAME_TAG_CONTRIBUTOR.translateColored(TextFormatting.GOLD);
        }
        return null;
    }
}