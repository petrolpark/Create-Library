package com.petrolpark.compat.create.common.kinetics.torquelimiter;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;

import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class TorqueLimiterInputBlockEntity extends KineticBlockEntity {

    protected ScrollValueBehaviour stress;

    public TorqueLimiterInputBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    };

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
    };

    @Override
    public float calculateStressApplied() {
        // TODO Auto-generated method stub
        return super.calculateStressApplied();
    };

    class TorqueLimiterValueBox extends ValueBoxTransform.Sided {

        @Override
		protected Vec3 getSouthLocation() {
			return VecHelper.voxelSpace(8d, 8d, 12.5d);
		};

		@Override
		public Vec3 getLocalOffset(LevelAccessor level, BlockPos pos, BlockState state) {
			return super.getLocalOffset(level, pos, state).add(Vec3.atLowerCornerOf(state.getValue(TorqueLimiterInputBlock.FACING).getNormal()).scale(-1 / 16f));
		};

		@Override
		public void rotate(LevelAccessor level, BlockPos pos, BlockState state, PoseStack ms) {
			super.rotate(level, pos, state, ms);
			Direction facing = state.getValue(TorqueLimiterInputBlock.FACING);
			if (facing.getAxis() == Axis.Y) return;
			if (getSide() != Direction.UP) return;
			TransformStack.of(ms).rotateZDegrees(-AngleHelper.horizontalAngle(facing) + 180);
		};

		@Override
		protected boolean isSideActive(BlockState state, Direction direction) {
			Direction facing = state.getValue(TorqueLimiterInputBlock.FACING);
			if (facing.getAxis() != Axis.Y && direction == Direction.DOWN) return false;
			return direction.getAxis() != facing.getAxis();
		};

    };
    
};
