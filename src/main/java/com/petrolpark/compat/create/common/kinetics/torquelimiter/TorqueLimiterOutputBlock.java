package com.petrolpark.compat.create.common.kinetics.torquelimiter;

import java.util.Optional;

import com.petrolpark.compat.create.CreateBlockEntityTypes;
import com.simibubi.create.api.contraption.BlockMovementChecks;
import com.simibubi.create.api.contraption.BlockMovementChecks.CheckResult;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.foundation.block.IBE;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class TorqueLimiterOutputBlock extends DirectionalKineticBlock implements IBE<TorqueLimiterOutputBlockEntity> {

    static {
        BlockMovementChecks.registerAttachedCheck(TorqueLimiterOutputBlock::isInputAttached);
    };

    public TorqueLimiterOutputBlock(Properties properties) {
        super(properties);
    };

    public static final Optional<TorqueLimiterInputBlockEntity> getInput(Level level, BlockPos pos, BlockState state) {
        return level.getBlockEntity(pos.relative(state.getValue(FACING).getOpposite()), CreateBlockEntityTypes.TORQUE_LIMITER_INPUT.get());
    };

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        return InteractionResult.PASS;
    };

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face == state.getValue(FACING);
    };
    
    @Override
    public Axis getRotationAxis(BlockState state) {
        return state.getValue(FACING).getAxis();
    };

    @Override
    public Class<TorqueLimiterOutputBlockEntity> getBlockEntityClass() {
        return TorqueLimiterOutputBlockEntity.class;
    };

    @Override
    public BlockEntityType<? extends TorqueLimiterOutputBlockEntity> getBlockEntityType() {
        return CreateBlockEntityTypes.TORQUE_LIMITER_OUTPUT.get();
    };

    public static CheckResult isInputAttached(BlockState state, Level world, BlockPos pos, Direction direction) {
        if (state.getBlock() instanceof TorqueLimiterOutputBlock && state.getValue(FACING).getOpposite() == direction && getInput(world, pos, state).isPresent()) return CheckResult.SUCCESS;
        return CheckResult.PASS;
    };
    
};
