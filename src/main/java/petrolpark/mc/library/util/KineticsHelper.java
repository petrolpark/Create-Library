package petrolpark.mc.library.util;

import java.util.List;

import javax.annotation.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;

import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.world.level.block.entity.BlockEntity;
import petrolpark.mc.library.compat.create.RequiresCreate;
import petrolpark.mc.library.compat.create.core.world.block.composite.CompositeKineticBlockEntity;
import petrolpark.mc.library.compat.create.core.world.block.entity.IKineticBlockEntityDuck;

@RequiresCreate
public class KineticsHelper {

	@Nullable
	@SuppressWarnings("null")
	public static KineticBlockEntity getSource(KineticBlockEntity kbe) {
		if (!kbe.hasSource()) return null;
		final BlockEntity candidate = kbe.getLevel().getBlockEntity(kbe.source);
		if (candidate == null) return null;
		if (candidate instanceof KineticBlockEntity source) return source;
		if (candidate instanceof CompositeKineticBlockEntity ckbe) {
			final Integer sourceIndex = ((IKineticBlockEntityDuck)kbe).getSourceIndex();
			if (sourceIndex != null && sourceIndex >= 0 && sourceIndex < ckbe.getParts().size()) return ckbe.getParts().get(sourceIndex);
		};
		return null;
	};

	public static boolean isOrCanBePoweredBy(KineticBlockEntity kbe, KineticBlockEntity source) {
		final KineticBlockEntity existingSource = getSource(kbe);
		return existingSource == null || existingSource == source;
	};
    
	public static void addLargeCogwheelPropagationLocations(BlockPos pos, List<BlockPos> neighbours) {
		BlockPos.betweenClosedStream(new BlockPos(-1, -1, -1), new BlockPos(1, 1, 1))
			.forEach(offset -> {
				if (offset.distSqr(BlockPos.ZERO) == 2)
					neighbours.add(pos.offset(offset));
			});
	};

    public static Direction directionBetween(BlockPos posFrom, BlockPos posTo) {
        for (Direction direction : Direction.values()) {
            if (posFrom.relative(direction).equals(posTo)) return direction;
        };
        return null;
    };

	public static PoseStack rotateToFace(Direction facing) {
		PoseStack poseStack = new PoseStack();
		TransformStack.of(poseStack)
				.center()
				.rotateToFace(facing)
				.rotate(com.mojang.math.Axis.XN.rotationDegrees(-90))
				.uncenter();
		return poseStack;
	};

	public static PoseStack rotateToAxis(Axis axis) {
		Direction facing = Direction.fromAxisAndDirection(axis, AxisDirection.POSITIVE);
		PoseStack poseStack = new PoseStack();
		TransformStack.of(poseStack)
				.center()
				.rotateToFace(facing)
				.rotate(com.mojang.math.Axis.XN.rotationDegrees(-90))
				.uncenter();
		return poseStack;
	};
};
