package com.wildfire.compat.blockbuster;

import mchorse.blockbuster_pack.BlockbusterSection;
import mchorse.metamorph.api.IMorphFactory;
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.creative.categories.MorphCategory;
import mchorse.metamorph.api.creative.sections.MorphSection;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.client.gui.editor.GuiAbstractMorph;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class BreastMorphFactory implements IMorphFactory {

    public static final BreastMorphFactory INSTANCE = new BreastMorphFactory();

    public MorphSection section;
    private boolean registered = false;

    @Override
    public void register(MorphManager manager) {
        if (!registered) {
            this.section = new MorphSection("lfgm");
            MorphCategory breasts = new MorphCategory(this.section, "breasts");
            breasts.add(new BreastMorph());
            this.section.categories.add(breasts);
            manager.list.register(this.section);
            registered = true;
        }

        if (Loader.isModLoaded("blockbuster")) {
            try {
                BlockbusterSection bb = manager.list.getSection(BlockbusterSection.class);
                if (bb != null && bb.extra != null) {
                    boolean exists = false;
                    for (AbstractMorph m : bb.extra.getMorphs()) {
                        if (m instanceof BreastMorph) {
                            exists = true;
                            break;
                        }
                    }
                    if (!exists) {
                        bb.extra.add(new BreastMorph());
                    }
                }
            } catch (Throwable ignored) {
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerMorphEditors(Minecraft mc, List<GuiAbstractMorph> editors) {
        editors.add(new GuiBreastMorph(mc));
    }

    @Override
    public AbstractMorph getMorphFromNBT(NBTTagCompound tag) {
        String name = tag.getString("Name");
        if (this.hasMorph(name)) {
            BreastMorph morph = new BreastMorph();
            morph.fromNBT(tag);
            return morph;
        }
        return null;
    }

    @Override
    public boolean hasMorph(String morph) {
        return morph.equals("lfgm.breasts") || morph.equals("blockbuster.breasts") || morph.equals("breasts");
    }
}
