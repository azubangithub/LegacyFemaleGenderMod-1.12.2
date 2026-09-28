package com.wildfire.compat.blockbuster;

import com.wildfire.client.render.WildfireModelRenderer;
import com.wildfire.main.Gender;
import com.wildfire.main.entitydata.Breasts;
import com.wildfire.main.entitydata.EntityConfig;
import com.wildfire.physics.BreastPhysics;
import mchorse.blockbuster.client.textures.GifTexture;
import mchorse.mclib.utils.resources.RLUtils;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.UUID;

public class BreastMorph extends AbstractMorph {

    public float size = 0.6F;
    public float offsetX = 0.0F;
    public float offsetY = 0.0F;
    public float offsetZ = 0.0F;
    public float cleavage = 0.0F;
    public boolean uniboob = false;
    public boolean jacket = true;
    public boolean physics = false;
    public float bounceMultiplier = 1.0F;
    public ResourceLocation customTexture = null;

    private EntityConfig entityConfig;
    private WeakReference<EntityLivingBase> ownerEntity;

    @SideOnly(Side.CLIENT)
    private WildfireModelRenderer.BreastModelBox lBreast;
    @SideOnly(Side.CLIENT)
    private WildfireModelRenderer.BreastModelBox rBreast;
    @SideOnly(Side.CLIENT)
    private static final WildfireModelRenderer.OverlayModelBox lBreastWear =
            new WildfireModelRenderer.OverlayModelBox(true, 64, 64, 17, 34, -4F, 0.0F, 0F, 4, 5, 3, 0.0F, false);
    @SideOnly(Side.CLIENT)
    private static final WildfireModelRenderer.OverlayModelBox rBreastWear =
            new WildfireModelRenderer.OverlayModelBox(false, 64, 64, 21, 34, 0, 0.0F, 0F, 4, 5, 3, 0.0F, false);

    private float preBreastSize = -1;
    private float preBreastOffsetZ = -999;

    public BreastMorph() {
        super();
        this.name = "lfgm.breasts";
    }

    @Override
    @SideOnly(Side.CLIENT)
    protected String getSubclassDisplayName() {
        return I18n.format("morph.lfgm.breasts");
    }

    @Override
    public float getWidth(EntityLivingBase entity) {
        return 0.5F;
    }

    @Override
    public float getHeight(EntityLivingBase entity) {
        return 0.5F;
    }

    public static boolean isDummy(EntityLivingBase entity) {
        if (entity == null) return true;
        String name = entity.getClass().getSimpleName();
        return name.contains("Dummy");
    }

    @SideOnly(Side.CLIENT)
    private EntityLivingBase resolveRealEntity(EntityLivingBase target) {
        if (target != null && !isDummy(target)) {
            return target;
        }
        EntityLivingBase bound = this.ownerEntity != null ? this.ownerEntity.get() : null;
        if (bound != null && !isDummy(bound)) {
            return bound;
        }
        EntityLivingBase curRender = BlockbusterClientHandler.getCurrentRenderEntity();
        if (curRender != null && !isDummy(curRender)) {
            return curRender;
        }
        EntityPlayer player = Minecraft.getMinecraft().player;
        if (player != null) {
            return player;
        }
        return target;
    }

    @SideOnly(Side.CLIENT)
    private void tickPhysicsClient(EntityLivingBase realEntity) {
        float yawDelta = BlockbusterClientHandler.getYawDelta(realEntity);

        float savedYaw = realEntity.renderYawOffset;
        float savedPrevYaw = realEntity.prevRenderYawOffset;
        realEntity.prevRenderYawOffset = 0.0f;
        realEntity.renderYawOffset = yawDelta;

        try {
            this.entityConfig.tickBreastPhysics(realEntity);
        } finally {
            realEntity.renderYawOffset = savedYaw;
            realEntity.prevRenderYawOffset = savedPrevYaw;
        }
    }

    @Override
    public void update(EntityLivingBase target) {
        super.update(target);
        if (this.physics) {
            syncEntityConfig();
            if (FMLCommonHandler.instance().getSide().isClient()) {
                EntityLivingBase realEntity = resolveRealEntity(target);
                if (realEntity != null) {
                    tickPhysicsClient(realEntity);
                }
            } else if (target != null) {
                this.entityConfig.tickBreastPhysics(target);
            }
        }
    }

    private void syncEntityConfig() {
        if (this.entityConfig == null) {
            this.entityConfig = new EntityConfig(UUID.randomUUID());
        }
        this.entityConfig.setGender(Gender.FEMALE);
        this.entityConfig.setBustSize(this.size);
        this.entityConfig.setBreastPhysics(this.physics);
        this.entityConfig.setBounceMultiplier(this.bounceMultiplier);
        this.entityConfig.setJacketLayer(this.jacket);

        Breasts b = this.entityConfig.getBreasts();
        b.setXOffset(this.offsetX);
        b.setYOffset(this.offsetY);
        b.setZOffset(this.offsetZ);
        b.setCleavage(this.cleavage / 100f);
        b.setUniboob(this.uniboob);
    }

    @SideOnly(Side.CLIENT)
    private void updateBoxes(float bSize, float bOffsetZ) {
        float reducer = -1;
        if (bSize < 0.84F) reducer++;
        if (bSize < 0.72F) reducer++;

        if (preBreastSize != bSize || preBreastOffsetZ != bOffsetZ || lBreast == null) {
            int depth = Math.max(1, (int) (4 - bOffsetZ - reducer));
            lBreast = new WildfireModelRenderer.BreastModelBox(64, 64, 16, 17, -4F, 0.0F, 0F, 4, 5, depth, 0.0F, false);
            rBreast = new WildfireModelRenderer.BreastModelBox(64, 64, 20, 17, 0, 0.0F, 0F, 4, 5, depth, 0.0F, false);
            preBreastSize = bSize;
            preBreastOffsetZ = bOffsetZ;
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void render(EntityLivingBase entity, double x, double y, double z, float entityYaw, float partialTicks) {
        if (this.size < 0.02F) return;

        EntityLivingBase realEntity = resolveRealEntity(entity);
        if (realEntity != null && !isDummy(realEntity)) {
            this.ownerEntity = new WeakReference<>(realEntity);
        }

        ResourceLocation texture = this.customTexture;
        if (texture == null) {
            if (realEntity instanceof AbstractClientPlayer) {
                texture = ((AbstractClientPlayer) realEntity).getLocationSkin();
            } else if (entity instanceof AbstractClientPlayer) {
                texture = ((AbstractClientPlayer) entity).getLocationSkin();
            } else {
                texture = DefaultPlayerSkin.getDefaultSkinLegacy();
            }
        }
        if (texture != null) {
            int ticks = entity != null ? entity.ticksExisted : (realEntity != null ? realEntity.ticksExisted : 0);
            GifTexture.bindTexture(texture, ticks, partialTicks);
        }

        syncEntityConfig();
        updateBoxes(this.size, this.offsetZ);

        GlStateManager.pushMatrix();
        if (x != 0 || y != 0 || z != 0) {
            GlStateManager.translate(x, y, z);
            GlStateManager.rotate(180.0F - entityYaw, 0.0F, 1.0F, 0.0F);
            GlStateManager.scale(1.0F, -1.0F, -1.0F);
            GlStateManager.translate(0, -1.375F, 0);
        } else {
            GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
        }

        renderBreasts(realEntity != null ? realEntity : entity, partialTicks);
        GlStateManager.popMatrix();
    }

    @SideOnly(Side.CLIENT)
    private void renderBreasts(EntityLivingBase entity, float partialTicks) {
        float breastOffsetX = Math.round((Math.round(this.offsetX * 100f) / 100f) * 10) / 10f;
        float breastOffsetY = -Math.round((Math.round(this.offsetY * 100f) / 100f) * 10) / 10f;
        float breastOffsetZ = -Math.round((Math.round(this.offsetZ * 100f) / 100f) * 10) / 10f;

        float outwardAngle = MathHelper.clamp(this.cleavage, 0F, 10F);

        float breastSize = this.size * 1.5f;
        if (breastSize > 0.7f) breastSize = 0.7f;
        if (this.size > 0.7f) breastSize = this.size;

        float zOff = 0.0625f - (this.size * 0.0625f);
        breastSize = this.size + 0.5f * Math.abs(this.size - 0.7f) * 2f;

        float lPhysPositionY = 0, lPhysPositionX = 0, leftBounceRotation = 0;
        float rPhysPositionY = 0, rPhysPositionX = 0, rightBounceRotation = 0;

        if (this.physics && this.entityConfig != null) {
            BreastPhysics lp = this.entityConfig.getLeftBreastPhysics();
            lPhysPositionY = lerp(partialTicks, lp.getPrePositionY(), lp.getPositionY());
            lPhysPositionX = lerp(partialTicks, lp.getPrePositionX(), lp.getPositionX());
            leftBounceRotation = lerp(partialTicks, lp.getPreBounceRotation(), lp.getBounceRotation());

            if (this.uniboob) {
                rPhysPositionY = lPhysPositionY;
                rPhysPositionX = lPhysPositionX;
                rightBounceRotation = leftBounceRotation;
            } else {
                BreastPhysics rp = this.entityConfig.getRightBreastPhysics();
                rPhysPositionY = lerp(partialTicks, rp.getPrePositionY(), rp.getPositionY());
                rPhysPositionX = lerp(partialTicks, rp.getPrePositionX(), rp.getPositionX());
                rightBounceRotation = lerp(partialTicks, rp.getPreBounceRotation(), rp.getBounceRotation());
            }
        }

        float alpha = entity != null && entity.isInvisible() ? 0.15F : 1F;
        GlStateManager.color(1F, 1F, 1F, alpha);
        GlStateManager.enableTexture2D();
        GlStateManager.enableLighting();
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ZERO);
        GlStateManager.disableCull();
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();

        // Left breast
        renderHalf(entity, true, lPhysPositionX, lPhysPositionY, leftBounceRotation, breastSize, breastOffsetX, breastOffsetY, breastOffsetZ, zOff, outwardAngle);

        // Right breast
        renderHalf(entity, false, rPhysPositionX, rPhysPositionY, rightBounceRotation, breastSize, -breastOffsetX, breastOffsetY, breastOffsetZ, zOff, outwardAngle);

        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.color(1F, 1F, 1F, 1F);
    }

    private static void shiftForJacket() {
        GlStateManager.translate(0, 0, -0.015f);
        GlStateManager.scale(1.05f, 1.05f, 1.05f);
    }

    @SideOnly(Side.CLIENT)
    private void renderHalf(EntityLivingBase entity, boolean left, float physX, float physY, float bounceRot,
                            float breastSize, float offX, float offY, float offZ, float zOff, float outwardAngle) {
        GlStateManager.pushMatrix();

        if (this.physics && (physX != 0 || physY != 0)) {
            GlStateManager.translate(physX / 32f, physY / 32f, 0);
        }

        GlStateManager.translate(offX * 0.0625f, 0.05625f + (offY * 0.0625f), zOff - 0.125f + (offZ * 0.0625f));

        if (!this.uniboob) {
            GlStateManager.translate(-0.0625f * 2 * (left ? 1 : -1), 0, 0);
        }
        if (this.physics && bounceRot != 0) {
            GlStateManager.rotate(bounceRot, 0, 1, 0);
        }
        if (!this.uniboob) {
            GlStateManager.translate(0.0625f * 2 * (left ? 1 : -1), 0, 0);
        }

        float rotation = breastSize;
        if (this.physics) {
            GlStateManager.translate(0, -0.035f * breastSize, 0);
            rotation -= physY / 12f;
        }
        rotation = Math.max(0.0f, Math.min(rotation, breastSize + 0.2f));
        rotation = Math.min(rotation, 1);

        GlStateManager.rotate(left ? outwardAngle : -outwardAngle, 0, 1, 0);
        GlStateManager.rotate(-35F * rotation, 1, 0, 0);

        if (entity != null) {
            float f5 = -MathHelper.cos(entity.ticksExisted * 0.09F) * 0.45F + 0.45F;
            GlStateManager.rotate(f5, 1, 0, 0);
        }

        GlStateManager.scale(0.9995f, 1f, 1f);

        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();
        float scale = 0.0625f;

        WildfireModelRenderer.BreastModelBox box = left ? lBreast : rBreast;
        if (box != null) {
            buf.begin(GL11.GL_QUADS, DefaultVertexFormats.OLDMODEL_POSITION_TEX_NORMAL);
            box.render(buf, scale);
            tess.draw();
        }

        if (this.jacket) {
            GlStateManager.pushMatrix();
            shiftForJacket();
            buf.begin(GL11.GL_QUADS, DefaultVertexFormats.OLDMODEL_POSITION_TEX_NORMAL);
            (left ? lBreastWear : rBreastWear).render(buf, scale);
            tess.draw();
            GlStateManager.popMatrix();
        }

        GlStateManager.popMatrix();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderOnScreen(EntityPlayer player, int x, int y, float scale, float alpha) {
        ResourceLocation texture = this.customTexture;
        if (texture == null) {
            if (player instanceof AbstractClientPlayer) {
                texture = ((AbstractClientPlayer) player).getLocationSkin();
            } else {
                texture = DefaultPlayerSkin.getDefaultSkinLegacy();
            }
        }
        if (texture != null) {
            GifTexture.bindTexture(texture, player != null ? player.ticksExisted : 0, 0F);
        }

        updateBoxes(this.size, this.offsetZ);

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 50.0F);
        GlStateManager.scale(-scale * 2.5F, scale * 2.5F, scale * 2.5F);
        GlStateManager.rotate(20.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(35.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.translate(0, -0.2F, 0);

        GlStateManager.enableRescaleNormal();
        GlStateManager.enableDepth();
        RenderHelper.enableStandardItemLighting();

        renderBreasts(player, 0F);

        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableDepth();
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
    }

    private static float lerp(float pct, float start, float end) {
        return start + pct * (end - start);
    }

    @Override
    public void toNBT(NBTTagCompound tag) {
        super.toNBT(tag);
        tag.setFloat("Size", this.size);
        tag.setFloat("OffsetX", this.offsetX);
        tag.setFloat("OffsetY", this.offsetY);
        tag.setFloat("OffsetZ", this.offsetZ);
        tag.setFloat("Cleavage", this.cleavage);
        tag.setBoolean("Uniboob", this.uniboob);
        tag.setBoolean("Jacket", this.jacket);
        tag.setBoolean("Physics", this.physics);
        tag.setFloat("BounceMultiplier", this.bounceMultiplier);
        if (this.customTexture != null) {
            tag.setTag("Texture", RLUtils.writeNbt(this.customTexture));
        }
    }

    @Override
    public void fromNBT(NBTTagCompound tag) {
        super.fromNBT(tag);
        if (tag.hasKey("Size")) this.size = tag.getFloat("Size");
        if (tag.hasKey("OffsetX")) this.offsetX = tag.getFloat("OffsetX");
        if (tag.hasKey("OffsetY")) this.offsetY = tag.getFloat("OffsetY");
        if (tag.hasKey("OffsetZ")) this.offsetZ = tag.getFloat("OffsetZ");
        if (tag.hasKey("Cleavage")) this.cleavage = tag.getFloat("Cleavage");
        if (tag.hasKey("Uniboob")) this.uniboob = tag.getBoolean("Uniboob");
        if (tag.hasKey("Jacket")) this.jacket = tag.getBoolean("Jacket");
        if (tag.hasKey("Physics")) this.physics = tag.getBoolean("Physics");
        if (tag.hasKey("BounceMultiplier")) this.bounceMultiplier = tag.getFloat("BounceMultiplier");
        if (tag.hasKey("Texture")) {
            this.customTexture = RLUtils.create(tag.getTag("Texture"));
        } else {
            this.customTexture = null;
        }
    }

    @Override
    public AbstractMorph create() {
        return new BreastMorph();
    }

    @Override
    public void copy(AbstractMorph from) {
        super.copy(from);
        if (from instanceof BreastMorph) {
            BreastMorph morph = (BreastMorph) from;
            this.size = morph.size;
            this.offsetX = morph.offsetX;
            this.offsetY = morph.offsetY;
            this.offsetZ = morph.offsetZ;
            this.cleavage = morph.cleavage;
            this.uniboob = morph.uniboob;
            this.jacket = morph.jacket;
            this.physics = morph.physics;
            this.bounceMultiplier = morph.bounceMultiplier;
            this.customTexture = morph.customTexture;
        }
    }

    @Override
    public boolean canMerge(AbstractMorph morph) {
        if (morph instanceof BreastMorph) {
            BreastMorph other = (BreastMorph) morph;
            return Float.compare(this.size, other.size) == 0
                    && Float.compare(this.offsetX, other.offsetX) == 0
                    && Float.compare(this.offsetY, other.offsetY) == 0
                    && Float.compare(this.offsetZ, other.offsetZ) == 0
                    && Float.compare(this.cleavage, other.cleavage) == 0
                    && this.uniboob == other.uniboob
                    && this.jacket == other.jacket
                    && this.physics == other.physics
                    && Float.compare(this.bounceMultiplier, other.bounceMultiplier) == 0
                    && Objects.equals(this.customTexture, other.customTexture);
        }
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BreastMorph) {
            BreastMorph other = (BreastMorph) obj;
            return super.equals(obj)
                    && Float.compare(this.size, other.size) == 0
                    && Float.compare(this.offsetX, other.offsetX) == 0
                    && Float.compare(this.offsetY, other.offsetY) == 0
                    && Float.compare(this.offsetZ, other.offsetZ) == 0
                    && Float.compare(this.cleavage, other.cleavage) == 0
                    && this.uniboob == other.uniboob
                    && this.jacket == other.jacket
                    && this.physics == other.physics
                    && Float.compare(this.bounceMultiplier, other.bounceMultiplier) == 0
                    && Objects.equals(this.customTexture, other.customTexture);
        }
        return super.equals(obj);
    }

    @Override
    public boolean useTargetDefault() {
        return true;
    }
}

