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

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = WildfireGender.MODID)
public final class WildfireSounds {

	private WildfireSounds() {
	}

	public static final SoundEvent FEMALE_HURT = new SoundEvent(new ResourceLocation(WildfireGender.MODID, "female_hurt"))
			.setRegistryName(new ResourceLocation(WildfireGender.MODID, "female_hurt"));

	@SubscribeEvent
	public static void registerSounds(RegistryEvent.Register<SoundEvent> event) {
		event.getRegistry().register(FEMALE_HURT);
	}
}
