package com.petrolpark.compat.create.common.kinetics;

import java.util.function.Consumer;

import org.joml.Quaternionf;

import com.mojang.math.Axis;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.contraptions.bearing.IBearingBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.OrientedRotatingVisual;

import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visual.DynamicVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.OrientedInstance;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;
import net.createmod.catnip.math.AngleHelper;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class VerticalBearingVisual<B extends KineticBlockEntity & IBearingBlockEntity> extends OrientedRotatingVisual<B> implements SimpleDynamicVisual {

    final OrientedInstance topInstance;
	final Quaternionf blockOrientation;

    public VerticalBearingVisual(VisualizationContext context, B blockEntity, float partialTick) {
        super(context, blockEntity, partialTick, Direction.SOUTH, blockEntity.getBlockState().getValue(BlockStateProperties.VERTICAL_DIRECTION).getOpposite(), Models.partial(AllPartialModels.SHAFT_HALF));

		blockOrientation = new Quaternionf();
        blockOrientation.mul(Axis.XP.rotationDegrees(-90 - AngleHelper.verticalAngle(blockEntity.getBlockState().getValue(BlockStateProperties.VERTICAL_DIRECTION))));

		final PartialModel top = blockEntity.isWoodenTop() ? AllPartialModels.BEARING_TOP_WOODEN : AllPartialModels.BEARING_TOP;

		topInstance = instancerProvider().instancer(InstanceTypes.ORIENTED, Models.partial(top))
				.createInstance();

		topInstance.position(getVisualPosition())
				.rotation(blockOrientation)
				.setChanged();
    };

    @Override
	public void beginFrame(DynamicVisual.Context ctx) {
		float interpolatedAngle = blockEntity.getInterpolatedAngle(ctx.partialTick() - 1);
		Quaternionf rot = Axis.YN.rotationDegrees(interpolatedAngle);

		rot.mul(blockOrientation);

		topInstance.rotation(rot)
			.setChanged();
	};

	@Override
	public void updateLight(float partialTick) {
		super.updateLight(partialTick);
		relight(topInstance);
	};

	@Override
	protected void _delete() {
		super._delete();
		topInstance.delete();
	};

	@Override
	public void collectCrumblingInstances(Consumer<Instance> consumer) {
		super.collectCrumblingInstances(consumer);
		consumer.accept(topInstance);
	};

    
};
