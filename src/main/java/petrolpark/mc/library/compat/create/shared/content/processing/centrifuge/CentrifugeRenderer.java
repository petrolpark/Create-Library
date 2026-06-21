package petrolpark.mc.library.compat.create.shared.content.processing.centrifuge;

import com.mojang.blaze3d.vertex.PoseStack;
import petrolpark.mc.library.compat.create.shared.registry.SharedPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringRenderer;

import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;

public class CentrifugeRenderer extends KineticBlockEntityRenderer<CentrifugeBlockEntity> {
    
    public CentrifugeRenderer(BlockEntityRendererProvider.Context context) {
		super(context);
	};

	@Override
	protected void renderSafe(CentrifugeBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
		FilteringRenderer.renderOnBlockEntity(be, partialTicks, ms, buffer, light, overlay);
		super.renderSafe(be, partialTicks, ms, buffer, light, overlay);
	};

	@Override
	protected SuperByteBuffer getRotatedModel(CentrifugeBlockEntity be, BlockState state) {
		return CachedBuffers.partial(SharedPartialModels.CENTRIFUGE_COG, state);
	};

};
