package com.petrolpark.compat.create.shared.content.processing.blender;

import java.util.function.Consumer;

import com.petrolpark.compat.create.shared.registry.SharedPartialModels;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.base.RotatingInstance;
import com.simibubi.create.content.kinetics.base.SingleAxisRotatingVisual;
import com.simibubi.create.foundation.render.AllInstanceTypes;

import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.model.Models;
import net.minecraft.world.level.block.state.BlockState;

public class BlenderVisual extends SingleAxisRotatingVisual<BlenderBlockEntity> {
    
    protected final RotatingInstance blades;

    public BlenderVisual(VisualizationContext context, BlenderBlockEntity blockEntity, float partialTick) {
        super(context, blockEntity, partialTick, Models.partial(SharedPartialModels.BLENDER_COG));

        blades = instancerProvider().instancer(AllInstanceTypes.ROTATING, Models.partial(SharedPartialModels.BLENDER_BLADES))
			.createInstance();

        final BlockState state = level.getBlockState(blockEntity.getBlockPos().above());

        blades.setVisible(state.isAir() || AllBlocks.BASIN.has(state));
		blades.setup(blockEntity)
            .setPosition(getVisualPosition())
            .nudge(0f, state.isAir() ? 0f : 2 / 16f, 0f)
            .setChanged();
    };

    @Override
    public void update(float pt) {
        super.update(pt);

        final BlockState state = level.getBlockState(blockEntity.getBlockPos().above());

        blades.setVisible(state.isAir() || AllBlocks.BASIN.has(state));
        blades.setup(blockEntity)
            .setPosition(getVisualPosition())
            .nudge(0f, state.isAir() ? 0f : 2 / 16f, 0f)
            .setChanged();
    };

    @Override
    public void updateLight(float partialTick) {
        super.updateLight(partialTick);
        relight(blades);
    };

    @Override
    protected void _delete() {
        super._delete();
        blades.delete();
    };

    @Override
    public void collectCrumblingInstances(Consumer<Instance> consumer) {
        super.collectCrumblingInstances(consumer);
        consumer.accept(blades);
    };
};
