package com.wildfire.compat.blockbuster;

import mchorse.mclib.client.gui.framework.elements.GuiModelRenderer;
import mchorse.mclib.client.gui.framework.elements.GuiScrollElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiToggleElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTexturePicker;
import mchorse.mclib.client.gui.framework.elements.input.GuiTrackpadElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.metamorph.api.MorphUtils;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.client.gui.creative.GuiMorphRenderer;
import mchorse.metamorph.client.gui.editor.GuiAbstractMorph;
import mchorse.metamorph.client.gui.editor.GuiMorphPanel;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiBreastMorph extends GuiAbstractMorph<BreastMorph> {

    public GuiBreastMorphPanel general;

    public GuiBreastMorph(Minecraft mc) {
        super(mc);
        this.defaultPanel = this.general = new GuiBreastMorphPanel(mc, this);
        this.registerPanel(this.general, IKey.lang("lfgm.gui.breasts"), Icons.MATERIAL);
    }

    @Override
    protected GuiModelRenderer createMorphRenderer(Minecraft mc) {
        return new GuiMorphRenderer(mc) {
            @Override
            protected void drawUserModel(GuiContext context) {
                if (this.morph != null) {
                    MorphUtils.render(this.morph, this.entity, 0.0D, 0.0D, 0.0D, this.yaw, context.partialTicks);
                }
            }
        };
    }

    @Override
    public boolean canEdit(AbstractMorph morph) {
        return morph instanceof BreastMorph;
    }

    public static class GuiBreastMorphPanel extends GuiMorphPanel<BreastMorph, GuiBreastMorph> {

        public GuiTrackpadElement size;
        public GuiTrackpadElement offsetX;
        public GuiTrackpadElement offsetY;
        public GuiTrackpadElement offsetZ;
        public GuiTrackpadElement cleavage;
        public GuiToggleElement dualPhysics;
        public GuiToggleElement jacket;
        public GuiToggleElement physics;
        public GuiTrackpadElement bounceMultiplier;

        public GuiButtonElement texture;
        public GuiButtonElement resetTexture;
        public GuiTexturePicker picker;

        public GuiBreastMorphPanel(Minecraft mc, GuiBreastMorph editor) {
            super(mc, editor);

            this.size = new GuiTrackpadElement(mc, v -> this.morph.size = v.floatValue()).limit(0.0, 1.0);
            this.size.tooltip(IKey.lang("wildfire_gender.bust_size"));

            this.offsetX = new GuiTrackpadElement(mc, v -> this.morph.offsetX = v.floatValue()).limit(-1.0, 1.0);
            this.offsetX.tooltip(IKey.lang("wildfire_gender.breast_offset_x"));

            this.offsetY = new GuiTrackpadElement(mc, v -> this.morph.offsetY = v.floatValue()).limit(-1.0, 1.0);
            this.offsetY.tooltip(IKey.lang("wildfire_gender.breast_offset_y"));

            this.offsetZ = new GuiTrackpadElement(mc, v -> this.morph.offsetZ = v.floatValue()).limit(-1.0, 1.0);
            this.offsetZ.tooltip(IKey.lang("wildfire_gender.breast_offset_z"));

            this.cleavage = new GuiTrackpadElement(mc, v -> this.morph.cleavage = v.floatValue()).limit(0.0, 10.0);
            this.cleavage.tooltip(IKey.lang("wildfire_gender.cleavage"));

            this.dualPhysics = new GuiToggleElement(mc, IKey.lang("wildfire_gender.dual_physics"), true, b -> this.morph.uniboob = !b.isToggled());
            this.jacket = new GuiToggleElement(mc, IKey.lang("lfgm.gui.jacket_layer"), true, b -> this.morph.jacket = b.isToggled());
            this.physics = new GuiToggleElement(mc, IKey.lang("wildfire_gender.breast_physics"), false, b -> this.morph.physics = b.isToggled());

            this.bounceMultiplier = new GuiTrackpadElement(mc, v -> this.morph.bounceMultiplier = v.floatValue()).limit(0.0, 3.0);
            this.bounceMultiplier.tooltip(IKey.lang("wildfire_gender.bounce_multiplier"));

            this.picker = new GuiTexturePicker(mc, rl -> this.morph.customTexture = rl);

            this.texture = new GuiButtonElement(mc, IKey.lang("blockbuster.gui.builder.pick_texture"), b -> {
                this.picker.refresh();
                this.picker.fill(this.morph.customTexture);
                this.add(this.picker);
                this.picker.resize();
            });

            this.resetTexture = new GuiButtonElement(mc, IKey.lang("lfgm.gui.reset_skin"), b -> {
                this.morph.customTexture = null;
                this.picker.removeFromParent();
            });

            this.picker.flex().relative(this.area).wh(1F, 1F);

            GuiScrollElement column = new GuiScrollElement(mc);
            column.scroll.opposite = true;
            column.flex().relative(this).w(150).h(1F).column(5).vertical().stretch().scroll().height(20).padding(10);

            column.add(Elements.label(IKey.lang("lfgm.gui.morph_settings")));
            column.add(Elements.label(IKey.lang("wildfire_gender.bust_size")), this.size);
            column.add(Elements.label(IKey.lang("wildfire_gender.breast_offset_x")), this.offsetX);
            column.add(Elements.label(IKey.lang("wildfire_gender.breast_offset_y")), this.offsetY);
            column.add(Elements.label(IKey.lang("wildfire_gender.breast_offset_z")), this.offsetZ);
            column.add(Elements.label(IKey.lang("wildfire_gender.cleavage")), this.cleavage);
            column.add(this.dualPhysics, this.jacket, this.physics);
            column.add(Elements.label(IKey.lang("wildfire_gender.bounce_multiplier")), this.bounceMultiplier);
            column.add(Elements.label(IKey.lang("lfgm.gui.texture")), this.texture, this.resetTexture);

            this.add(column);
        }

        @Override
        public void fillData(BreastMorph morph) {
            super.fillData(morph);
            this.picker.removeFromParent();

            this.size.setValue(morph.size);
            this.offsetX.setValue(morph.offsetX);
            this.offsetY.setValue(morph.offsetY);
            this.offsetZ.setValue(morph.offsetZ);
            this.cleavage.setValue(morph.cleavage);
            this.dualPhysics.toggled(!morph.uniboob);
            this.jacket.toggled(morph.jacket);
            this.physics.toggled(morph.physics);
            this.bounceMultiplier.setValue(morph.bounceMultiplier);
        }

        @Override
        public void finishEditing() {
            this.picker.close();
            super.finishEditing();
        }
    }
}
