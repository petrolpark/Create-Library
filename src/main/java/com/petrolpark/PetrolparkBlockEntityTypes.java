package com.petrolpark;

import com.petrolpark.compat.SharedFeatureFlag;
import com.petrolpark.core.scratch.world.block.ProgrammingBlockEntity;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

public class PetrolparkBlockEntityTypes {
  
    public static final BlockEntityEntry<ProgrammingBlockEntity> PROGRAMMING_BLOCK = Petrolpark.REGISTRATE.sharedBlockEntity(SharedFeatureFlag.PROGRAMMING_BLOCK, "programming_block", ProgrammingBlockEntity::new)
        .validBlock(PetrolparkBlocks.PROGRAMMING_BLOCK)
        .register();
    
    public static final void register() {};
};
