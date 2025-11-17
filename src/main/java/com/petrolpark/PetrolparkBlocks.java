package com.petrolpark;

import static com.petrolpark.Petrolpark.REGISTRATE;

import com.petrolpark.compat.SharedFeatureFlag;
import com.petrolpark.core.scratch.world.block.ProgrammingBlock;
import com.tterrag.registrate.util.entry.BlockEntry;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;

public class PetrolparkBlocks {
  
    public static final BlockEntry<Block> MASHED_POTATO_BLOCK = REGISTRATE.sharedBlock(SharedFeatureFlag.POTATO_PRODUCTS, "mashed_potato_block", Block::new)
        .initialProperties(() -> Blocks.CLAY)
        .properties(p -> p
            .mapColor(MapColor.COLOR_YELLOW)
            .sound(SoundType.SLIME_BLOCK)
            .strength(0.2f)
        ).item()
        .build()
        .register();

    public static final BlockEntry<ProgrammingBlock> PROGRAMMING_BLOCK = REGISTRATE.sharedBlock(SharedFeatureFlag.PROGRAMMING_BLOCK, "programming_block", ProgrammingBlock::new)
        .item()
        .build()
        .register();

    public static final void register() {};
};
