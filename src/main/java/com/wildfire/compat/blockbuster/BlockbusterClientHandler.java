package com.wildfire.compat.blockbuster;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Map;
import java.util.WeakHashMap;

@SideOnly(Side.CLIENT)
public class BlockbusterClientHandler {

    public static final Map<EntityLivingBase, Float> ENTITY_YAW_MAP = new WeakHashMap<>();
    public static final Map<EntityLivingBase, Float> ENTITY_PREV_YAW_MAP = new WeakHashMap<>();
    public static EntityLivingBase CURRENT_RENDER_ENTITY = null;

    public static void init() {
        MinecraftForge.EVENT_BUS.register(new BlockbusterClientHandler());
    }

    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        if (entity != null && entity.world != null && entity.world.isRemote && !BreastMorph.isDummy(entity)) {
            ENTITY_YAW_MAP.put(entity, entity.renderYawOffset);
            ENTITY_PREV_YAW_MAP.put(entity, entity.prevRenderYawOffset);
        }
    }

    @SubscribeEvent
    public void onRenderLivingPre(RenderLivingEvent.Pre<?> event) {
        EntityLivingBase entity = event.getEntity();
        if (entity != null && !BreastMorph.isDummy(entity)) {
            CURRENT_RENDER_ENTITY = entity;
        }
    }

    @SubscribeEvent
    public void onRenderLivingPost(RenderLivingEvent.Post<?> event) {
        if (CURRENT_RENDER_ENTITY == event.getEntity()) {
            CURRENT_RENDER_ENTITY = null;
        }
    }

    @SubscribeEvent
    public void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        ENTITY_YAW_MAP.clear();
        ENTITY_PREV_YAW_MAP.clear();
        CURRENT_RENDER_ENTITY = null;
    }

    public static float getYawDelta(EntityLivingBase entity) {
        if (entity == null) return 0f;
        Float trueCur = ENTITY_YAW_MAP.get(entity);
        Float truePrev = ENTITY_PREV_YAW_MAP.get(entity);
        if (trueCur != null && truePrev != null) {
            return MathHelper.wrapDegrees(trueCur - truePrev);
        }
        return MathHelper.wrapDegrees(entity.rotationYaw - entity.prevRotationYaw);
    }

    public static EntityLivingBase getCurrentRenderEntity() {
        return CURRENT_RENDER_ENTITY;
    }
}
