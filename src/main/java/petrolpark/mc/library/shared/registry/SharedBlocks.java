package petrolpark.mc.library.shared.registry;

import static petrolpark.mc.library.Petrolpark.REGISTRATE;
import static petrolpark.mc.library.PetrolparkTags.commonBlockTag;
import static petrolpark.mc.library.PetrolparkTags.commonItemTag;

import petrolpark.mc.library.core.registrate.builder.PetrolparkBlockBuilder;
import petrolpark.mc.library.core.scratch.world.block.ProgrammingBlock;
import petrolpark.mc.library.core.world.block.SharedBlock;
import petrolpark.mc.library.core.world.block.SharedRotatedPillarBlock;
import petrolpark.mc.library.core.world.item.wooden.WoodenBlockItem;
import petrolpark.mc.library.registry.PetrolparkDataComponentTypes;
import petrolpark.mc.library.shared.SharedFeatureFlag;
import petrolpark.mc.library.shared.world.item.crafting.drying.rack.DryingRackBlock;
import com.tterrag.registrate.util.entry.BlockEntry;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.neoforged.neoforge.common.Tags;

public class SharedBlocks {
  
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
        .loot((lt, b) -> lt.add(b, LootTable.lootTable()
            .withPool(
                lt.applyExplosionCondition(b, LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1f))
                    .add(LootItem.lootTableItem(b).apply(CopyComponentsFunction.copyComponents(CopyComponentsFunction.Source.BLOCK_ENTITY)
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
            LootItem.lootTableItem(SharedItems.RAW_FRIES)
                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(5f)))
        ))).blockstate((ctx, prov) -> prov.axisBlock(ctx.get()))
        .tag(BlockTags.MINEABLE_WITH_SHOVEL, BlockTags.MINEABLE_WITH_HOE)
        .transform(PetrolparkBlockBuilder::defaultBlockItem)
        .build()
        .register();

    public static final void register() {};
};
