package com.wildfire.compat.blockbuster;

import com.wildfire.main.WildfireGender;
import mchorse.metamorph.api.MorphManager;
import net.minecraftforge.fml.common.Loader;

public class BlockbusterCompat {

    private static boolean initialized = false;

    public static void preInit() {
        if (Loader.isModLoaded("metamorph")) {
            try {
                MorphManager.INSTANCE.factories.add(BreastMorphFactory.INSTANCE);
                WildfireGender.LOGGER.info("Registered BreastMorphFactory to Metamorph");
            } catch (Throwable t) {
                WildfireGender.LOGGER.error("Failed to register BreastMorphFactory in preInit", t);
            }
        }
    }

    public static void init() {
        if (Loader.isModLoaded("metamorph")) {
            try {
                if (!initialized) {
                    BreastMorphFactory.INSTANCE.register(MorphManager.INSTANCE);
                    initialized = true;
                    if (net.minecraftforge.fml.common.FMLCommonHandler.instance().getSide().isClient()) {
                        BlockbusterClientHandler.init();
                    }
                    WildfireGender.LOGGER.info("Initialized LFGM Blockbuster & Metamorph compatibility");
                }
            } catch (Throwable t) {
                WildfireGender.LOGGER.error("Failed to initialize Metamorph compatibility in init", t);
            }
        }
    }
}
