package com.petrolpark.compat.create;

import static com.petrolpark.Petrolpark.REGISTRATE;

import com.petrolpark.RequiresCreate;
import com.petrolpark.compat.SharedFeatures;
import com.petrolpark.compat.create.common.kinetics.torquelimiter.TorqueLimiterInputBlockEntity;
import com.petrolpark.compat.create.common.kinetics.torquelimiter.TorqueLimiterOutputBlockEntity;
import com.petrolpark.compat.create.common.processing.basinlid.BasinLidBlockEntity;
import com.petrolpark.compat.create.core.tube.TubeStructuralBlockEntity;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

@RequiresCreate
public class CreateBlockEntityTypes {

    public static final BlockEntityEntry<BasinLidBlockEntity> BASIN_LID = REGISTRATE.sharedBlockEntity(SharedFeatures.BASIN_LID, "basin_lid", BasinLidBlockEntity::new)
        .validBlock(CreateBlocks.BASIN_LID)
        .register();

    public static final BlockEntityEntry<TubeStructuralBlockEntity> TUBE_STRUCTURE = REGISTRATE.blockEntity("tube_structure", TubeStructuralBlockEntity::new)
        .validBlock(CreateBlocks.TUBE_STRUCTURE)
        .register();

    public static final BlockEntityEntry<TorqueLimiterInputBlockEntity> TORQUE_LIMITER_INPUT = REGISTRATE.sharedBlockEntity(SharedFeatures.TORQUE_LIMITER, "torque_limiter_input", TorqueLimiterInputBlockEntity::new)
        .validBlock(CreateBlocks.TORQUE_LIMITER_INPUT)
        .register();

    public static final BlockEntityEntry<TorqueLimiterOutputBlockEntity> TORQUE_LIMITER_OUTPUT = REGISTRATE.sharedBlockEntity(SharedFeatures.TORQUE_LIMITER, "torque_limiter_output", TorqueLimiterOutputBlockEntity::new)
        .validBlock(CreateBlocks.TORQUE_LIMITER_OUTPUT)
        .register();

    public static final void register() {};
};
