package com.petrolpark.compat.create.common.kinetics.horseMill.ponder;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.petrolpark.compat.create.PetrolparkPartialModels;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;

import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.render.CachedBuffers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;

public class HarnessWithCowDummyRenderer extends SafeBlockEntityRenderer<HarnessWithCowDummyBlockEntity> {

    public HarnessWithCowDummyRenderer(BlockEntityRendererProvider.Context context) {

    };

    @Override
    @SuppressWarnings("null")
    protected void renderSafe(HarnessWithCowDummyBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource bufferSource, int light, int overlay) {
        final BlockState state = be.getBlockState();
        final VertexConsumer vc = bufferSource.getBuffer(RenderType.solid());

        final float angle = be.walking ? Mth.sin(AnimationTickHolder.getRenderTime(be.getLevel()) / 8f) * 15 : 0;

        for (boolean left : Iterate.trueAndFalse) {
            for (boolean front : Iterate.trueAndFalse) {
                CachedBuffers.partial(PetrolparkPartialModels.COW_DUMMY_LEG, state)
                .center()
                    .rotateTo(Direction.NORTH, state.getValue(HarnessWithCowDummyBlock.FACING))
                    .uncenter()
                    .translate(left ? 12 / 16f : 4 / 16f, 12 / 16f, front ? -8 / 16f : 7 / 16f)
                    
                    .rotateXDegrees(left == front ? angle : - angle)
                    
                    .light(light)
                    .renderInto(ms, vc);;
            }; 
        };
    };
    
};
