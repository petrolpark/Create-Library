package com.petrolpark;

import static com.petrolpark.Petrolpark.REGISTRATE;
import static com.petrolpark.PetrolparkTags.commonBlockTag;
import static com.petrolpark.PetrolparkTags.commonItemTag;
import static net.minecraft.world.level.storage.loot.LootPool.lootPool;
import static net.minecraft.world.level.storage.loot.LootTable.lootTable;
import static net.minecraft.world.level.storage.loot.entries.LootItem.lootTableItem;

import com.petrolpark.compat.SharedFeatureFlag;
import com.petrolpark.core.item.decay.drying.rack.DryingRackBlock;
import com.petrolpark.core.item.wooden.WoodenBlockItem;
import com.petrolpark.core.registrate.builder.PetrolparkBlockBuilder;
import com.petrolpark.core.scratch.world.block.ProgrammingBlock;
import com.petrolpark.core.world.block.SharedBlock;
import com.petrolpark.core.world.block.SharedRotatedPillarBlock;
import com.tterrag.registrate.util.entry.BlockEntry;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.neoforged.neoforge.common.Tags;

public class PetrolparkBlocks {
  
    public static final BlockEntry<SharedBlock> MASHED_POTATO_BLOCK = REGISTRATE.sharedBlock(SharedFeatureFlag.POTATO_PRODUCTS, "mashed_potato_block", SharedBlock::new)
        .initialProperties(() -> Blocks.CLAY)
        .properties(p -> p
            .mapColor(MapColor.COLOR_YELLOW)
            .sound(SoundType.SLIME_BLOCK)
            .strength(0.2f)
        ).loot((lt, b) -> lt.dropSelf(b))
        .defaultBlockstate()
        .tag(BlockTags.MINEABLE_WITH_SHOVEL, BlockTags.MINEABLE_WITH_HOE, Tags.Blocks.STORAGE_BLOCKS, commonBlockTag("storage_blocks/mashed_potato"))
        .transform(PetrolparkBlockBuilder::defaultBlockItem)
        .tag(Tags.Items.STORAGE_BLOCKS, commonItemTag("storage_blocks/mashed_potato"))
        .build()
        .register();

    public static final BlockEntry<DryingRackBlock> DRYING_RACK = REGISTRATE.sharedBlock(SharedFeatureFlag.DRYING_RACK, "drying_rack", DryingRackBlock::new)
        .initialProperties(() -> Blocks.OAK_FENCE)
        .loot((lt, b) -> lt.add(b, lootTable()
            .withPool(
                lt.applyExplosionCondition(b, lootPool()
                    .setRolls(ConstantValue.exactly(1f))
                    .add(lootTableItem(b).apply(CopyComponentsFunction.copyComponents(CopyComponentsFunction.Source.BLOCK_ENTITY)
                        .include(PetrolparkDataComponentTypes.WOOD)
                    ))
                )
            ))
        ).tag(BlockTags.MINEABLE_WITH_AXE)
        .item(WoodenBlockItem::new)
        .build()
        .register();

    public static final BlockEntry<ProgrammingBlock> PROGRAMMING_BLOCK = REGISTRATE.sharedBlock(SharedFeatureFlag.PROGRAMMING_BLOCK, "programming_block", ProgrammingBlock::new)
        .item()
        .build()
        .register();

    public static final BlockEntry<SharedRotatedPillarBlock> RAW_FRIES_BLOCK = REGISTRATE.sharedBlock(SharedFeatureFlag.FRIES, "raw_fries_block", SharedRotatedPillarBlock::new)
        .initialProperties(() -> Blocks.CLAY)
        .properties(p -> p
            .mapColor(MapColor.COLOR_YELLOW)
            .sound(SoundType.SLIME_BLOCK)
            .strength(0.2f)
        ).loot((lt, b) -> lt.add(b, lt.createSilkTouchDispatchTable(b,
            LootItem.lootTableItem(PetrolparkItems.RAW_FRIES)
                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(5f)))
        ))).blockstate((ctx, prov) -> prov.axisBlock(ctx.get()))
        .tag(BlockTags.MINEABLE_WITH_SHOVEL, BlockTags.MINEABLE_WITH_HOE)
        .transform(PetrolparkBlockBuilder::defaultBlockItem)
        .build()
        .register();

    public static final void register() {};
};
