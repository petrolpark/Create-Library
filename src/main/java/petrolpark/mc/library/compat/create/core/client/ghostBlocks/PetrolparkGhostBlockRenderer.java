package petrolpark.mc.library.compat.create.core.client.ghostBlocks;

import javax.annotation.ParametersAreNonnullByDefault;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.createmod.catnip.ghostblock.GhostBlockParams;
import net.createmod.catnip.ghostblock.GhostBlockRenderer;
import net.createmod.catnip.impl.client.render.ColoringVertexConsumer;
import net.createmod.catnip.placement.PlacementClient;
import net.createmod.catnip.render.SuperRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import petrolpark.mc.library.mixin.compat.create.accessor.GhostBlockParamsAccessor;

@ParametersAreNonnullByDefault
public abstract class PetrolparkGhostBlockRenderer extends GhostBlockRenderer {

    @Override
    public final void render(PoseStack ms, SuperRenderTypeBuffer buffer, Vec3 camera, GhostBlockParams params) {
        final BlockState state = ((GhostBlockParamsAccessor)params).getState();
        final BlockPos pos = ((GhostBlockParamsAccessor)params).getPos();
        final float alpha = ((GhostBlockParamsAccessor)params).getAlphaSupplier().get() * 0.75f * PlacementClient.getCurrentAlpha();
        final VertexConsumer vb = new ColoringVertexConsumer(buffer.getEarlyBuffer(RenderType.translucent()), 1, 1, 1, alpha);

        ms.pushPose();
        ms.translate(pos.getX() - camera.x(), pos.getY() - camera.y(), pos.getZ() - camera.z());
        ms.pushPose();
        render(state, pos, ms, buffer, vb, camera);
        ms.popPose();
        ms.popPose();
    };

    public abstract void render(BlockState state, BlockPos pos, PoseStack ms, SuperRenderTypeBuffer buffer, VertexConsumer vertexConsumer, Vec3 camera);
    
};
