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

import com.wildfire.main.Gender;
import com.wildfire.main.entitydata.Breasts;
import com.wildfire.main.entitydata.PlayerConfig;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.util.UUID;

public abstract class AbstractSyncPacket implements IMessage {

    public UUID uuid;
    public Gender gender;
    public float bustSize;
    public boolean hurtSounds;
    public float voicePitch;
    public boolean physics;
    public boolean showInArmor;
    public float bounceMultiplier;
    public float floppyMultiplier;
    public Breasts breasts;

    public AbstractSyncPacket() {
        this.breasts = new Breasts();
    }

    public AbstractSyncPacket(PlayerConfig plr) {
        this.uuid = plr.uuid;
        this.gender = plr.getGender();
        this.bustSize = plr.getBustSize();
        this.hurtSounds = plr.hasHurtSounds();
        this.voicePitch = plr.getVoicePitch();
        this.physics = plr.hasBreastPhysics();
        this.showInArmor = plr.showBreastsInArmor();
        this.bounceMultiplier = plr.getBounceMultiplier();
        this.floppyMultiplier = plr.getFloppiness();
        this.breasts = new Breasts();
        this.breasts.updateFrom(plr.getBreasts());
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(uuid.getMostSignificantBits());
        buf.writeLong(uuid.getLeastSignificantBits());
        buf.writeByte(gender.ordinal());
        buf.writeFloat(bustSize);
        buf.writeBoolean(hurtSounds);
        buf.writeFloat(voicePitch);
        buf.writeBoolean(physics);
        buf.writeBoolean(showInArmor);
        buf.writeFloat(bounceMultiplier);
        buf.writeFloat(floppyMultiplier);
        breasts.toBytes(buf);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.uuid = new UUID(buf.readLong(), buf.readLong());
        this.gender = Gender.byId(buf.readByte());
        this.bustSize = buf.readFloat();
        this.hurtSounds = buf.readBoolean();
        this.voicePitch = buf.readFloat();
        this.physics = buf.readBoolean();
        this.showInArmor = buf.readBoolean();
        this.bounceMultiplier = buf.readFloat();
        this.floppyMultiplier = buf.readFloat();
        this.breasts = Breasts.fromBytes(buf);
    }

    public void updatePlayer(PlayerConfig plr) {
        plr.updateGender(gender);
        plr.updateBustSize(bustSize);
        plr.updateHurtSounds(hurtSounds);
        plr.updateVoicePitch(voicePitch);
        plr.updateBreastPhysics(physics);
        plr.updateShowBreastsInArmor(showInArmor);
        plr.updateBounceMultiplier(bounceMultiplier);
        plr.updateFloppiness(floppyMultiplier);
        plr.getBreasts().updateFrom(breasts);
    }
}
