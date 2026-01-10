package com.petrolpark;

import static com.petrolpark.Petrolpark.REGISTRATE;

import com.petrolpark.compat.SharedFeatureFlag;
import com.petrolpark.core.item.decay.drying.rack.DryingRackBlockEntity;
import com.petrolpark.core.item.decay.drying.rack.DryingRackBlockEntityRenderer;
import com.petrolpark.core.scratch.world.block.ProgrammingBlockEntity;
import com.petrolpark.core.world.block.soil.SoilBlockEntity;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

public class PetrolparkBlockEntityTypes {

    public static final BlockEntityEntry<DryingRackBlockEntity> DRYING_RACK = REGISTRATE.sharedBlockEntity(SharedFeatureFlag.DRYING_RACK, "drying_rack", DryingRackBlockEntity::new)
        .validBlock(PetrolparkBlocks.DRYING_RACK)
        .renderer(() -> DryingRackBlockEntityRenderer::new)
        .register();

    public static final BlockEntityEntry<SoilBlockEntity> SOIL = REGISTRATE.blockEntity("soil", SoilBlockEntity::new)
        .register();
  
    public static final BlockEntityEntry<ProgrammingBlockEntity> PROGRAMMING_BLOCK = REGISTRATE.sharedBlockEntity(SharedFeatureFlag.PROGRAMMING_BLOCK, "programming_block", ProgrammingBlockEntity::new)
        .validBlock(PetrolparkBlocks.PROGRAMMING_BLOCK)
        .register();
    
    public static final void register() {};
};
