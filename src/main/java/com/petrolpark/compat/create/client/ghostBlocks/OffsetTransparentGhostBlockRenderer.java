package com.petrolpark.compat.create.client.ghostBlocks;

import java.util.function.Function;

import javax.annotation.ParametersAreNonnullByDefault;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.petrolpark.mixin.compat.create.accessor.GhostBlockParamsAccessor;

import dev.engine_room.flywheel.lib.model.baked.EmptyVirtualBlockGetter;
import net.createmod.catnip.client.render.model.BakedModelBufferer;
import net.createmod.catnip.ghostblock.GhostBlockParams;
import net.createmod.catnip.ghostblock.GhostBlockRenderer;
import net.createmod.catnip.ghostblock.GhostBlocks;
import net.createmod.catnip.impl.client.render.ColoringVertexConsumer;
import net.createmod.catnip.placement.PlacementClient;
import net.createmod.catnip.render.SuperRenderTypeBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/**
 * Copied from {@link GhostBlockRenderer#transparent()} Create source code
 */
@ParametersAreNonnullByDefault
public class OffsetTransparentGhostBlockRenderer extends GhostBlockRenderer {

    public static final GhostBlockParams show(Object slot, BlockState state, Vec3 offset) {
		return show(slot, state, offset, 1);
	};

	public static final GhostBlockParams show(Object slot, BlockState state, Vec3 offset, int ttl) {
		return GhostBlocks.getInstance().showGhost(slot, new OffsetTransparentGhostBlockRenderer(offset), GhostBlockParams.of(state), ttl);
	};

    public static final GhostBlockParams show(Object slot, BlockState state, Vec3 offset, Function<BlockState, BakedModel> modelGetter, int ttl) {
		return GhostBlocks.getInstance().showGhost(slot, new OffsetTransparentGhostBlockRenderer(offset), GhostBlockParams.of(state), ttl);
	};

    protected final Function<BlockState, BakedModel> modelGetter;
    protected final Vec3 offset;
    
    protected OffsetTransparentGhostBlockRenderer(Vec3 offset) {
        this(offset, state -> Minecraft.getInstance().getBlockRenderer().getBlockModel(state));
    };

    protected OffsetTransparentGhostBlockRenderer(Vec3 offset, Function<BlockState, BakedModel> modelGetter) {
        this.offset = offset;
        this.modelGetter = modelGetter;
    };

    @Override
    public void render(PoseStack ms, SuperRenderTypeBuffer buffer, Vec3 camera, GhostBlockParams params) {
        final BlockState state = ((GhostBlockParamsAccessor)params).getState();
        final BakedModel model = modelGetter.apply(state);
        final BlockPos pos = ((GhostBlockParamsAccessor)params).getPos();
        final float alpha = ((GhostBlockParamsAccessor)params).getAlphaSupplier().get() * 0.75f * PlacementClient.getCurrentAlpha();
        final VertexConsumer vb = new ColoringVertexConsumer(buffer.getEarlyBuffer(RenderType.translucent()), 1, 1, 1, alpha);

        ms.pushPose();
        ms.translate(pos.getX() + offset.x() - camera.x(), pos.getY() + offset.y() - camera.y(), pos.getZ() + offset.y() - camera.z());
        ms.translate(0.5d, 0.5d, 0.5d);
        ms.scale(0.85f, 0.85f, 0.85f);
        ms.translate(-0.5d, -0.5d, -0.5d);
        BakedModelBufferer.bufferModel(model, pos, EmptyVirtualBlockGetter.FULL_BRIGHT, state, ms, (layer, shade) -> vb);
        ms.popPose();
    };
};
