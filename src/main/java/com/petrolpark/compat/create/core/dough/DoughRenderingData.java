package com.petrolpark.compat.create.core.dough;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.render.FluidRenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class DoughRenderingData {

    public static final float WIDTH_UNIT = 4 / 16f;
    public static final float THICKNESS_UNIT = 1 / 16f;

    public DoughData data = null;
  
    // If in Rolling Stage
    public byte oldWidth = 0;
    public byte oldLength = 0;
    public float oldThickness = 0f;
    public float rollingProgress = 1f;
    public float oldRollingProgress = 1f;

    public void update(DoughData data) {
        this.data = data;
    };

    public DoughRenderingData setFrom(DoughData data) {
        this.data = data;
        if (data == null) return this;
        oldWidth = data.width();
        oldLength = data.length();
        oldThickness = data.thickness();
        return this;
    };

    public void tick(Level level) {
        if (level.isClientSide()) {
            oldRollingProgress = rollingProgress;
            if (oldRollingProgress >= 1f && data != null) {
                oldWidth = data.width();
                oldLength = data.length();
                oldThickness = data.thickness();
            };
            rollingProgress += 0.5f;
            if (rollingProgress > 1f) rollingProgress = 1f;
        };
    };

    public float getRollingProgress(float partialTicks) {
        if (partialTicks > 1f) partialTicks = 1f;
        return Mth.lerp(partialTicks, oldRollingProgress, rollingProgress);
    };

    public float getWidth(float partialTicks) {
        return Mth.lerp(getRollingProgress(partialTicks), (float)oldWidth, (float)data.width()) * WIDTH_UNIT;
    };

    public float getLength(float partialTicks) {
        return Mth.lerp(getRollingProgress(partialTicks), (float)oldLength, (float)data.length()) * WIDTH_UNIT;
    };

    public float getThickness(float partialTicks) {
        return Mth.lerp(getRollingProgress(partialTicks), (float)oldThickness, (float)data.thickness()) * THICKNESS_UNIT;
    };

    @OnlyIn(Dist.CLIENT)
    public void render(float partialTicks, PoseStack ms, VertexConsumer builder, int light) {
        if (data == null) return;

        final TextureAtlasSprite doughTex = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(data.dough().textureLocation());
        final int color = data.dough().tint();

        final float
        width = getWidth(partialTicks),
        length = getLength(partialTicks),
        xMin = (1f - width) / 2f,
        yMin = 0f,
        zMin = (1f - length) / 2f,
        xMax = xMin + width,
        yMax = getThickness(partialTicks),
        zMax = zMin + length;

        if (data.decoration().isEmpty()) {

            light = (light & 0xF00000);

            ms.pushPose();

            for (final Direction side : Iterate.directions) {

                boolean positive = side.getAxisDirection() == Direction.AxisDirection.POSITIVE;
                if (side.getAxis().isHorizontal()) {
                    if (side.getAxis() == Direction.Axis.X) {
                        FluidRenderHelper.renderStillTiledFace(side, zMin, yMin, zMax, yMax, positive ? xMax : xMin, builder, ms, light, color, doughTex);
                    } else {
                        FluidRenderHelper.renderStillTiledFace(side, xMin, yMin, xMax, yMax, positive ? zMax : zMin, builder, ms, light, color, doughTex);
                    }
                } else {
                    FluidRenderHelper.renderStillTiledFace(side, xMin, zMin, xMax, zMax, positive ? yMax : yMin, builder, ms, light, color, doughTex);
                };
            }

            ms.popPose();
        };
        
    };
    
};
