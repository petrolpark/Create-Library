package com.petrolpark.compat.create.core.world.dough;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.createmod.catnip.platform.CatnipServices;
import net.createmod.catnip.render.SuperBufferFactory;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class DoughRenderer {

    public static final float WIDTH_UNIT = 4 / 16f;
    public static final float THICKNESS_UNIT = 1 / 16f;

    public DoughData data = null;
  
    // If in Rolling Stage
    public byte oldWidth = 0;
    public byte oldLength = 0;
    public float oldThickness = 0f;
    public float rollingProgress = 1f;
    public float oldRollingProgress = 1f;

    @OnlyIn(Dist.CLIENT)
    protected SuperByteBuffer cachedBuffer = null;

    public void update(DoughData data) {
        this.data = data;
        CatnipServices.PLATFORM.executeOnClientOnly(() -> this::refreshBuffer);
    };

    public DoughRenderer setFrom(DoughData data) {
        this.data = data;
        if (data == null) return this;
        oldWidth = data.width();
        oldLength = data.length();
        oldThickness = data.thickness();
        CatnipServices.PLATFORM.executeOnClientOnly(() -> this::refreshBuffer);
        return this;
    };

    @OnlyIn(Dist.CLIENT)
    protected void refreshBuffer() {
        cachedBuffer = null;
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

    public AABB getAABB(float partialTicks) {
        final float width = getWidth(partialTicks), length = getLength(partialTicks);
        return new AABB((1f - width) / 2f, 0f, (1f - length) / 2f, (1f + width) / 2f, getThickness(partialTicks), (1f + length) / 2f);
    };

    @OnlyIn(Dist.CLIENT)
    public void render(BlockState baseState, float partialTicks, PoseStack ms, VertexConsumer builder, int light) {
        if (data == null) return;
        final Minecraft mc = Minecraft.getInstance();
        final ClientLevel level = mc.level;
        if (level == null || !(mc.getBlockRenderer().getBlockModel(baseState) instanceof DoughModel baseModel)) return;

        final TextureAtlasSprite sprite = mc.getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(data.dough().textureLocation());
        final int color = data.dough().tint();

        if (data.decoration().isEmpty()) {

            if (cachedBuffer == null || rollingProgress < 1f) {

                final BakedModel scaledModel = baseModel.getShaped(getAABB(partialTicks), sprite, level.getRandom());

                if (rollingProgress < 1f) {
                    SuperBufferFactory.getInstance().createForBlock(scaledModel, Blocks.AIR.defaultBlockState())
                        .light(light)
                        .color(color)
                        .renderInto(ms, builder);
                    return;
                } else {
                    cachedBuffer = SuperBufferFactory.getInstance().createForBlock(scaledModel, Blocks.AIR.defaultBlockState());
                }
            };

            cachedBuffer
                .light(light)
                .color(color)
                .renderInto(ms, builder);
        };
        
    };
    
};
