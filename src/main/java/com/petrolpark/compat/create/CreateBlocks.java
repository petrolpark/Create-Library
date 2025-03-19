package com.petrolpark.compat.create;

import static com.petrolpark.Petrolpark.REGISTRATE;

import com.petrolpark.tube.TubeStructuralBlock;
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

    public static final void register() {};
};
