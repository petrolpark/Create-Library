package com.petrolpark.compat.create;

import static com.petrolpark.Petrolpark.REGISTRATE;

import com.petrolpark.RequiresCreate;
import com.petrolpark.compat.SharedFeatureFlag;
import com.petrolpark.compat.create.common.kinetics.torquelimiter.TorqueLimiterInputBlockEntity;
import com.petrolpark.compat.create.common.kinetics.torquelimiter.TorqueLimiterOutputBlockEntity;
import com.petrolpark.compat.create.common.processing.basinlid.BasinLidBlockEntity;
import com.petrolpark.compat.create.common.processing.extrusion.ExtrusionDieBlockEntity;
import com.petrolpark.compat.create.common.processing.mandrel.MandrelBlockEntity;
import com.petrolpark.compat.create.common.processing.mandrel.MandrelRenderer;
import com.petrolpark.compat.create.common.redstone.programmer.RedstoneProgrammerBlockEntity;
import com.petrolpark.compat.create.common.redstone.programmer.RedstoneProgrammerBlockEntityRenderer;
import com.petrolpark.compat.create.core.tube.TubeStructuralBlockEntity;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

@RequiresCreate
public class CreateBlockEntityTypes {

    public static final BlockEntityEntry<BasinLidBlockEntity> BASIN_LID = REGISTRATE.sharedBlockEntity(SharedFeatureFlag.BASIN_LID, "basin_lid", BasinLidBlockEntity::new)
        .validBlock(CreateBlocks.BASIN_LID)
        .register();

    public static final BlockEntityEntry<ExtrusionDieBlockEntity> EXTRUSION_DIE = REGISTRATE.sharedBlockEntity(SharedFeatureFlag.EXTRUSION, "extrusion_die", ExtrusionDieBlockEntity::new)
        .validBlock(CreateBlocks.EXTRUSION_DIE)
        .register();

    public static final BlockEntityEntry<MandrelBlockEntity> MANDREL = REGISTRATE.sharedBlockEntity(SharedFeatureFlag.MANDREL, "mandrel", MandrelBlockEntity::new)
        .validBlock(CreateBlocks.MANDREL)
        .renderer(() -> MandrelRenderer::new)
        .register();

    public static final BlockEntityEntry<RedstoneProgrammerBlockEntity> REDSTONE_PROGRAMMER = REGISTRATE.sharedBlockEntity(SharedFeatureFlag.REDSTONE_PROGRAMMER, "redstone_programmer", RedstoneProgrammerBlockEntity::new)
        .validBlock(CreateBlocks.REDSTONE_PROGRAMMER)
        .renderer(() -> RedstoneProgrammerBlockEntityRenderer::new)
        .register();

    public static final BlockEntityEntry<TubeStructuralBlockEntity> TUBE_STRUCTURE = REGISTRATE.blockEntity("tube_structure", TubeStructuralBlockEntity::new)
        .validBlock(CreateBlocks.TUBE_STRUCTURE)
        .register();

    @Deprecated
    public static final BlockEntityEntry<TorqueLimiterInputBlockEntity> TORQUE_LIMITER_INPUT = REGISTRATE.sharedBlockEntity(SharedFeatureFlag.TORQUE_LIMITER, "torque_limiter_input", TorqueLimiterInputBlockEntity::new)
        .validBlock(CreateBlocks.TORQUE_LIMITER_INPUT)
        .register();

    @Deprecated
    public static final BlockEntityEntry<TorqueLimiterOutputBlockEntity> TORQUE_LIMITER_OUTPUT = REGISTRATE.sharedBlockEntity(SharedFeatureFlag.TORQUE_LIMITER, "torque_limiter_output", TorqueLimiterOutputBlockEntity::new)
        .validBlock(CreateBlocks.TORQUE_LIMITER_OUTPUT)
        .register();

    public static final void register() {};
};
