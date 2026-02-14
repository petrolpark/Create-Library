package com.petrolpark.compat.create.common.processing.centrifuge;

import com.petrolpark.compat.create.PetrolparkPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;

import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;

public class CentrifugeRenderer extends KineticBlockEntityRenderer<CentrifugeBlockEntity> {
    
    public CentrifugeRenderer(BlockEntityRendererProvider.Context context) {
		super(context);
	};

	@Override
	protected SuperByteBuffer getRotatedModel(CentrifugeBlockEntity be, BlockState state) {
		return CachedBuffers.partial(PetrolparkPartialModels.CENTRIFUGE_COG, state);
	};
};
