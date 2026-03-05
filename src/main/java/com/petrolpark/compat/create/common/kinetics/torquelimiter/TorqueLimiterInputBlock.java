package com.petrolpark.compat.create.common.kinetics.torquelimiter;

import java.util.Optional;

import com.petrolpark.compat.create.PetrolparkCreateBlockEntityTypes;
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

public class TorqueLimiterInputBlock extends DirectionalKineticBlock implements IBE<TorqueLimiterInputBlockEntity> {

    static {
        BlockMovementChecks.registerAttachedCheck(TorqueLimiterInputBlock::isOutputAttached);
    };
    
    public TorqueLimiterInputBlock(Properties properties) {
        super(properties);
    };

    public static final Optional<TorqueLimiterOutputBlockEntity> getOutput(LevelReader level, BlockPos pos, BlockState state) {
        return level.getBlockEntity(pos.relative(state.getValue(FACING)), PetrolparkCreateBlockEntityTypes.TORQUE_LIMITER_OUTPUT.get());
    };

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        return InteractionResult.PASS;
    };

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face == state.getValue(FACING).getOpposite();
    };
    
    @Override
    public Axis getRotationAxis(BlockState state) {
        return state.getValue(FACING).getAxis();
    };

    @Override
    public Class<TorqueLimiterInputBlockEntity> getBlockEntityClass() {
        return TorqueLimiterInputBlockEntity.class;
    };

    @Override
    public BlockEntityType<? extends TorqueLimiterInputBlockEntity> getBlockEntityType() {
        return PetrolparkCreateBlockEntityTypes.TORQUE_LIMITER_INPUT.get();
    };

    public static CheckResult isOutputAttached(BlockState state, Level world, BlockPos pos, Direction direction) {
        if (state.getBlock() instanceof TorqueLimiterInputBlock && state.getValue(FACING) == direction && getOutput(world, pos, state).isPresent()) return CheckResult.SUCCESS;
        return CheckResult.PASS; 
    };
    
};
