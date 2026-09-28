/*
 * Wildfire's Female Gender Mod is a female gender mod created for Minecraft.
 * Copyright (C) 2023-present WildfireRomeo
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 */

package com.wildfire.physics;

import com.wildfire.api.IGenderArmor;
import com.wildfire.main.entitydata.EntityConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.Random;

public class BreastPhysics {

    private final Random random = new Random();
    private final EntityConfig entityConfig;

    // X-Axis
    private float bounceVelX = 0, targetBounceX = 0, velocityX = 0, positionX, prePositionX;
    // Y-Axis
    private float bounceVel = 0, targetBounceY = 0, velocity = 0, positionY, prePositionY;
    // Rotation
    private float bounceRotVel = 0, targetRotVel = 0, rotVelocity = 0, wfg_bounceRotation, wfg_preBounceRotation;

    private float breastSize = 0, preBreastSize = 0;

    private boolean wasSneaking = false;
    private boolean wasSleeping = false;
    private int lastSwingDuration = 6, lastSwingTick = 0;
    private Vec3d prePos;
    private int randomB = 1;
    private double lastVerticalMoveVelocity;

    public BreastPhysics(EntityConfig entityConfig) {
        this.entityConfig = entityConfig;
        if (entityConfig.getGender().canHaveBreasts()) {
            this.breastSize = entityConfig.getBustSize();
            this.preBreastSize = entityConfig.getBustSize();
        }
    }

    public void setBreastSize(float size) {
        this.breastSize = size;
        this.preBreastSize = size;
    }

    public void update(EntityLivingBase entity, IGenderArmor armor) {
        if (entity instanceof EntityArmorStand) {
            if (entityConfig.getGender().canHaveBreasts()) {
                this.breastSize = entityConfig.getBustSize();
                if (!entityConfig.getArmorPhysicsOverride() && armor.coversBreasts()) {
                    float tightness = MathHelper.clamp(armor.tightness(), 0, 1);
                    this.breastSize *= 1 - 0.15F * tightness;
                }
                this.preBreastSize = this.breastSize;
            } else {
                this.preBreastSize = this.breastSize = 0f;
            }
            return;
        }

        this.prePositionY = this.positionY;
        this.prePositionX = this.positionX;
        this.wfg_preBounceRotation = this.wfg_bounceRotation;
        this.preBreastSize = this.breastSize;

        Vec3d currentPos = new Vec3d(entity.posX, entity.posY, entity.posZ);
        if (this.prePos == null) {
            this.prePos = currentPos;
            if (entityConfig.getGender().canHaveBreasts() && this.breastSize <= 0.01F) {
                this.breastSize = entityConfig.getBustSize();
                this.preBreastSize = entityConfig.getBustSize();
            }
            return;
        }

        float breastWeight = entityConfig.getBustSize() * 1.25f;
        float targetBreastSize = entityConfig.getBustSize();

        if (!entityConfig.getGender().canHaveBreasts()) {
            targetBreastSize = 0;
        } else if (!entityConfig.getArmorPhysicsOverride() && armor.coversBreasts()) {
            float tightness = MathHelper.clamp(armor.tightness(), 0, 1);
            targetBreastSize *= 1 - 0.15F * tightness;
        }

        breastSize += (breastSize < targetBreastSize) ? Math.abs(breastSize - targetBreastSize) / 2f : -Math.abs(breastSize - targetBreastSize) / 2f;

        Vec3d motion = currentPos.subtract(this.prePos);
        this.prePos = currentPos;

        float bounceIntensity = (targetBreastSize * 3f) * Math.round((entityConfig.getBounceMultiplier() * 3) * 100) / 100f;
        if (!entityConfig.getArmorPhysicsOverride() && armor.coversBreasts()) {
            float resistance = MathHelper.clamp(armor.physicsResistance(), 0, 1);
            bounceIntensity *= 1 - resistance;
        }

        if (!entityConfig.getBreasts().isUniboob()) {
            bounceIntensity = bounceIntensity * (0.5f + random.nextFloat());
        }
        double vertVelocity = entity.motionY;
        if ((lastVerticalMoveVelocity <= 0 && vertVelocity > 0) || (lastVerticalMoveVelocity < 0 && vertVelocity == 0)) {
            randomB = random.nextBoolean() ? -1 : 1;
        }
        lastVerticalMoveVelocity = vertVelocity;

        this.targetBounceY = (float) motion.y * bounceIntensity;
        this.targetBounceY += breastWeight;

        this.targetRotVel = calcRotation(entity, bounceIntensity);
        this.targetRotVel += (float) motion.y * bounceIntensity * randomB;

        this.targetBounceX = -calcRotation(entity, bounceIntensity) / 10f;

        float speedSqr = (float) (entity.motionX * entity.motionX + entity.motionY * entity.motionY + entity.motionZ * entity.motionZ);
        float f = speedSqr / 0.2F;
        f = f * f * f;
        if (f < 1.0F) {
            f = 1.0F;
        }

        this.targetBounceY += MathHelper.cos(entity.limbSwing * 0.6662F + (float) Math.PI) * 0.5F * entity.limbSwingAmount * 0.5F / f;

        boolean isSneaking = entity.isSneaking();
        if (isSneaking != wasSneaking) {
            if (isSneaking || wasSneaking) {
                this.targetBounceY += bounceIntensity;
            }
            wasSneaking = isSneaking;
        }

        boolean isSleeping = entity.isPlayerSleeping();
        if (isSleeping != wasSleeping) {
            if (isSleeping || wasSleeping) {
                this.targetBounceY = bounceIntensity;
            }
            wasSleeping = isSleeping;
        }

        Entity vehicle = entity.getRidingEntity();
        if (vehicle instanceof EntityBoat) {
            this.targetBounceY = bounceIntensity / 3.25f;
        } else if (vehicle instanceof EntityMinecart) {
            float cartSpeed = (float) (vehicle.motionX * vehicle.motionX + vehicle.motionZ * vehicle.motionZ);
            if (random.nextDouble() * cartSpeed < 0.5f && cartSpeed > 0.2f) {
                this.targetBounceY = bounceIntensity / 6f;
                if (random.nextBoolean()) {
                    this.targetBounceY = -this.targetBounceY;
                }
                this.targetBounceY += breastWeight;
            }
        } else if (vehicle instanceof AbstractHorse) {
            float horseSpeed = (float) Math.sqrt(vehicle.motionX * vehicle.motionX + vehicle.motionZ * vehicle.motionZ);
            if (vehicle.ticksExisted % clampMovement(horseSpeed) == 5 && horseSpeed > 0.05f) {
                this.targetBounceY = bounceIntensity / 4f;
                this.targetBounceY += breastWeight;
            }
        } else if (vehicle instanceof EntityPig) {
            float pigSpeed = (float) Math.sqrt(vehicle.motionX * vehicle.motionX + vehicle.motionZ * vehicle.motionZ);
            if (vehicle.ticksExisted % clampMovement(pigSpeed) == 5 && pigSpeed > 0.002f) {
                this.targetBounceY = (bounceIntensity * MathHelper.clamp(pigSpeed * 75, 0.1f, 1f)) / 4f;
                this.targetBounceY += breastWeight;
            }
        }

        int swingDuration = 6;
        if (entity.isSwingInProgress && !isSleeping) {
            float rawAmplifier = 0f;
            float amplifier = MathHelper.clamp(1 + rawAmplifier, 0.6f, 1.3f);
            EnumHandSide swingingArm = entity.swingingHand == EnumHand.MAIN_HAND ? entity.getPrimaryHand() : (entity.getPrimaryHand() == EnumHandSide.RIGHT ? EnumHandSide.LEFT : EnumHandSide.RIGHT);
            int swingTickDelta = entity.swingProgressInt - lastSwingTick;

            if (entity.ticksExisted % 3 == 0) {
                float amplifiedBounce = 0.25f * amplifier * bounceIntensity;
                if (random.nextBoolean()) {
                    this.targetBounceY -= amplifiedBounce;
                } else {
                    this.targetBounceY += amplifiedBounce;
                }
                float xAmp = MathHelper.clamp(1 + (rawAmplifier * (rawAmplifier < 0 ? 1.625f : 0.8f)), 0.25f, 1.225f);
                this.targetBounceX = (0.325f * xAmp * bounceIntensity) * (swingingArm == EnumHandSide.RIGHT ? -1f : 1f);
            }
            lastSwingTick = entity.swingProgressInt;
        } else {
            lastSwingTick = 0;
        }

        float percent = entityConfig.getFloppiness();
        float bounceAmount = 0.45f * (1f - percent) + 0.15f;
        bounceAmount = MathHelper.clamp(bounceAmount, 0.15f, 0.6f);
        float delta = 2.25f - bounceAmount;

        float distanceFromMin = Math.abs(bounceVel + 0.5f) * 0.5f;
        float distanceFromMax = Math.abs(bounceVel - 2.65f) * 0.5f;

        if (bounceVel < -0.5f) {
            targetBounceY += distanceFromMin;
        }
        if (bounceVel > 2.5f) {
            targetBounceY -= distanceFromMax;
        }
        targetBounceY = MathHelper.clamp(targetBounceY, -1.5f, 2.5f);
        targetRotVel = MathHelper.clamp(targetRotVel, -25f, 25f);

        this.velocity = lerp(bounceAmount, this.velocity, (this.targetBounceY - this.bounceVel) * delta);
        this.bounceVel += this.velocity * percent * 1.1625f;

        // X
        this.velocityX = lerp(bounceAmount, this.velocityX, (this.targetBounceX - this.bounceVelX) * delta);
        this.bounceVelX += this.velocityX * percent;

        this.rotVelocity = lerp(bounceAmount, this.rotVelocity, (this.targetRotVel - this.bounceRotVel) * delta);
        this.bounceRotVel += this.rotVelocity * percent;

        this.wfg_bounceRotation = this.bounceRotVel;
        this.positionX = this.bounceVelX;
        this.positionY = this.bounceVel;

        if (this.positionY < -0.5f) {
            this.positionY = -0.5f;
        }
        if (this.positionY > 1.5f) {
            this.positionY = 1.5f;
            this.velocity = 0;
        }
    }

    private static float lerp(float pct, float start, float end) {
        return start + pct * (end - start);
    }

    public float getBreastSize(float partialTicks) {
        if (!entityConfig.getGender().canHaveBreasts()) {
            return 0F;
        }
        if (!entityConfig.hasBreastPhysics()) {
            return entityConfig.getBustSize();
        }
        float size = lerp(partialTicks, preBreastSize, breastSize);
        if (size < 0.02F && entityConfig.getBustSize() > 0) {
            return entityConfig.getBustSize();
        }
        return size;
    }

    public float getPrePositionY() {
        return this.prePositionY;
    }

    public float getPositionY() {
        return this.positionY;
    }

    public float getPrePositionX() {
        return this.prePositionX;
    }

    public float getPositionX() {
        return this.positionX;
    }

    public float getBounceRotation() {
        return this.wfg_bounceRotation;
    }

    public float getPreBounceRotation() {
        return this.wfg_preBounceRotation;
    }

    private int clampMovement(float movement) {
        return Math.max((int) (10 - 2 * movement), 1);
    }

    private float calcRotation(EntityLivingBase entity, float bounceIntensity) {
        Entity vehicle = entity.getRidingEntity();
        if (vehicle != null) {
            float prevYaw = vehicle instanceof EntityLivingBase ? ((EntityLivingBase) vehicle).prevRenderYawOffset : vehicle.prevRotationYaw;
            float curYaw = vehicle instanceof EntityLivingBase ? ((EntityLivingBase) vehicle).renderYawOffset : vehicle.rotationYaw;
            return -((curYaw - prevYaw) / 15f) * bounceIntensity;
        }
        return -((entity.renderYawOffset - entity.prevRenderYawOffset) / 15f) * bounceIntensity;
    }
}
