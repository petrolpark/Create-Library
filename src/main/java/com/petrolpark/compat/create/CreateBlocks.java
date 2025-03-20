package com.petrolpark.compat.create;

import static com.petrolpark.Petrolpark.REGISTRATE;

import com.petrolpark.compat.SharedFeatures;
import com.petrolpark.compat.create.common.kinetics.torquelimiter.TorqueLimiterInputBlock;
import com.petrolpark.compat.create.common.kinetics.torquelimiter.TorqueLimiterOutputBlock;
import com.petrolpark.compat.create.core.tube.TubeStructuralBlock;
import com.tterrag.registrate.util.entry.BlockEntry;

import net.minecraft.world.level.material.PushReaction;

public class CreateBlocks {
    
    public static final BlockEntry<TubeStructuralBlock> TUBE_STRUCTURE = REGISTRATE.block("tube", TubeStructuralBlock::new)
        .properties(p -> p
            .noCollission()
            .pushReaction(PushReaction.DESTROY)
        ).lang("Tube Segment")
        .blockstate((c, p) -> {})
        .register();

    public static final BlockEntry<TorqueLimiterInputBlock> TORQUE_LIMITER_INPUT = REGISTRATE.sharedBlock(SharedFeatures.TORQUE_LIMITER, "torque_limiter_input", TorqueLimiterInputBlock::new)
        .lang("Torque Limiter")
        .blockstate((c, p) -> {})
        .register();

    public static final BlockEntry<TorqueLimiterOutputBlock> TORQUE_LIMITER_OUTPUT = REGISTRATE.sharedBlock(SharedFeatures.TORQUE_LIMITER, "torque_limiter_output", TorqueLimiterOutputBlock::new)
        .lang("Torque Limiter")
        .blockstate((c, p) -> {})
        .register();

    public static final void register() {};
};
