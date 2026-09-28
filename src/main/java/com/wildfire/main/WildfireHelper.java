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

import com.wildfire.api.IGenderArmor;
import com.wildfire.api.WildfireAPI;
import com.wildfire.api.impl.GenderArmor;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

import java.util.function.Consumer;

public class WildfireHelper {

    public static IGenderArmor getArmorConfig(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return GenderArmor.EMPTY;
        }
        IGenderArmor apiArmor = WildfireAPI.getGenderArmor(stack.getItem());
        if (apiArmor != null) {
            return apiArmor;
        }
        Item item = stack.getItem();
        if (item instanceof ItemArmor) {
            ItemArmor armor = (ItemArmor) item;
            if (armor.armorType == EntityEquipmentSlot.CHEST) {
                if (item == Items.LEATHER_CHESTPLATE) {
                    return new GenderArmor(0.4F, 0.05F, false);
                } else if (item == Items.CHAINMAIL_CHESTPLATE) {
                    return new GenderArmor(0.6F, 0.1F, false);
                } else if (item == Items.IRON_CHESTPLATE || item == Items.GOLDEN_CHESTPLATE || item == Items.DIAMOND_CHESTPLATE) {
                    return new GenderArmor(1.0F, 0.15F, true);
                }
                return GenderArmor.DEFAULT;
            }
        }
        return GenderArmor.EMPTY;
    }

    public static <ENTITY extends EntityLivingBase> void withEntityAngles(ENTITY entity, float yBodyRot, float yRot, float xRot, Consumer<ENTITY> runnable) {
        float oldYBodyRot = entity.renderYawOffset;
        float oldYRot = entity.rotationYaw;
        float oldXRot = entity.rotationPitch;
        float oldYHeadRot0 = entity.prevRotationYawHead;
        float oldYHeadRot = entity.rotationYawHead;
        float oldPrevYBodyRot = entity.prevRenderYawOffset;
        float oldPrevYRot = entity.prevRotationYaw;
        float oldPrevXRot = entity.prevRotationPitch;

        entity.prevRenderYawOffset = yBodyRot;
        entity.renderYawOffset = yBodyRot;
        entity.prevRotationYaw = yRot;
        entity.rotationYaw = yRot;
        entity.prevRotationPitch = xRot;
        entity.rotationPitch = xRot;
        entity.rotationYawHead = yRot;
        entity.prevRotationYawHead = yRot;

        runnable.accept(entity);

        entity.renderYawOffset = oldYBodyRot;
        entity.rotationYaw = oldYRot;
        entity.rotationPitch = oldXRot;
        entity.prevRotationYawHead = oldYHeadRot0;
        entity.rotationYawHead = oldYHeadRot;
        entity.prevRenderYawOffset = oldPrevYBodyRot;
        entity.prevRotationYaw = oldPrevYRot;
        entity.prevRotationPitch = oldPrevXRot;
    }
}
