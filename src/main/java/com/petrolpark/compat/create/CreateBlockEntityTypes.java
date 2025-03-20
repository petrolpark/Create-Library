package com.petrolpark.compat.create;

import static com.petrolpark.Petrolpark.REGISTRATE;

import com.petrolpark.RequiresCreate;
import com.petrolpark.compat.create.common.kinetics.torquelimiter.TorqueLimiterInputBlockEntity;
import com.petrolpark.compat.create.core.tube.TubeStructuralBlockEntity;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

@RequiresCreate
public class CreateBlockEntityTypes {

    public static final BlockEntityEntry<TubeStructuralBlockEntity> TUBE_STRUCTURE = REGISTRATE.blockEntity("tube_structure", TubeStructuralBlockEntity::new)
        .validBlock(CreateBlocks.TUBE_STRUCTURE)
        .register();

    public static final BlockEntityEntry<TorqueLimiterInputBlockEntity> TORQUE_LIMITER_INPUT = REGISTRATE.blockEntity("torque_limiter_input", TorqueLimiterInputBlockEntity::new)
        .register();

    public static final void register() {};
};
