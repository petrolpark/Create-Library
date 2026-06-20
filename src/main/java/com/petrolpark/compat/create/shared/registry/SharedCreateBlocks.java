package com.petrolpark.compat.create.shared.registry;

import static com.petrolpark.Petrolpark.REGISTRATE;
import static com.petrolpark.core.registrate.PetrolparkTagGen.axeOrPickaxe;
import static com.petrolpark.core.registrate.PetrolparkTagGen.pickaxeOnly;
import static com.simibubi.create.api.behaviour.movement.MovementBehaviour.movementBehaviour;

import com.petrolpark.PetrolparkTags;
import com.petrolpark.compat.create.PetrolparkCreate;
import com.petrolpark.compat.create.core.world.block.crushingWheel.EncasedCrushingWheelControllerBlock;
import com.petrolpark.compat.create.core.world.block.tube.TubeStructuralBlock;
import com.petrolpark.compat.create.core.world.dough.DoughBlock;
import com.petrolpark.compat.create.core.world.dough.DoughItem;
import com.petrolpark.compat.create.registry.PetrolparkCreateDataComponentTypes;
import com.petrolpark.compat.create.shared.content.kinetics.horseMill.HarnessBlock;
import com.petrolpark.compat.create.shared.content.kinetics.horseMill.HarnessMovementBehaviour;
import com.petrolpark.compat.create.shared.content.kinetics.horseMill.HorseMillBearingBlock;
import com.petrolpark.compat.create.shared.content.kinetics.horseMill.ponder.HarnessWithCowDummyBlock;
import com.petrolpark.compat.create.shared.content.processing.basinLid.BasinLidBlock;
import com.petrolpark.compat.create.shared.content.processing.blender.BlenderBlock;
import com.petrolpark.compat.create.shared.content.processing.centrifuge.CentrifugeBlock;
import com.petrolpark.compat.create.shared.content.processing.extrusion.ExtrusionDieBlock;
import com.petrolpark.compat.create.shared.content.processing.mandrel.MandrelBlock;
import com.petrolpark.compat.create.shared.content.processing.meshBasin.MeshBasinBlock;
import com.petrolpark.compat.create.shared.content.redstone.programmer.RedstoneProgrammerBlock;
import com.petrolpark.compat.create.shared.content.redstone.programmer.RedstoneProgrammerBlockItem;
import com.petrolpark.config.PetrolparkStressConfig;
import com.petrolpark.shared.SharedFeatureFlag;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllTags.AllBlockTags;
import com.simibubi.create.api.stress.BlockStressValues;
import com.simibubi.create.content.processing.basin.BasinMovementBehaviour;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.BlockStateGen;
import com.simibubi.create.foundation.data.ModelGen;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;

import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

public class SharedCreateBlocks {

    public static final BlockEntry<BasinLidBlock> BASIN_LID = REGISTRATE.sharedBlock(SharedFeatureFlag.BASIN_LID, "basin_lid", BasinLidBlock::new)
        .initialProperties(SharedProperties::copperMetal)
        .properties(BlockBehaviour.Properties::noOcclusion)
        .blockstate(BlockStateGen.horizontalBlockProvider(false))
        .transform(pickaxeOnly())
        .item()
        .build()
        .register();

    public static final BlockEntry<BlenderBlock> BLENDER = REGISTRATE.sharedBlock(SharedFeatureFlag.BLENDER, "blender", BlenderBlock::new)
        .initialProperties(AllBlocks.BASIN)
        .properties(p -> p
            .noOcclusion()
        ).blockstate((c, p) -> p.simpleBlock(c.getEntry(), AssetLookup.partialBaseModel(c, p)))
        .transform(axeOrPickaxe())
        .transform(PetrolparkStressConfig.setImpact(2f))
        .item()
        .tag(PetrolparkTags.Items.FLAGGABLE.tag)
        .onRegister(PetrolparkCreate::registerTooltip)
        .build()
        .register();

    public static final BlockEntry<CentrifugeBlock> CENTRIFUGE = REGISTRATE.sharedBlock(SharedFeatureFlag.CENTRIFUGE, "centrifuge", CentrifugeBlock::new)
        .initialProperties(SharedProperties::copperMetal)
        .properties(BlockBehaviour.Properties::noOcclusion)
        .transform(pickaxeOnly())
        .transform(PetrolparkStressConfig.setImpact(2f))
        .item()
        .tag(PetrolparkTags.Items.FLAGGABLE.tag)
        .onRegister(PetrolparkCreate::registerTooltip)
        .build()
        .register();

    public static final BlockEntry<DoughBlock> DOUGH = REGISTRATE.block("dough", DoughBlock::new)
        .properties(p -> p
            .noOcclusion()
            .instabreak()
        ).color(() -> () -> DoughBlock::getColor) // For particles
        .item(DoughItem::new)
        .tag(PetrolparkTags.Items.FLAGGABLE.tag)
        .properties(p -> p
            .stacksTo(1)
        ).build()
        .register();

    public static final BlockEntry<EncasedCrushingWheelControllerBlock> ENCASED_CRUSHING_WHEEL_CONTROLLER = REGISTRATE.block("encased_crushing_wheel_controller", EncasedCrushingWheelControllerBlock::new)
        .initialProperties(AllBlocks.CRUSHING_WHEEL_CONTROLLER)
        .register();

    public static final BlockEntry<ExtrusionDieBlock> EXTRUSION_DIE = REGISTRATE.sharedBlock(SharedFeatureFlag.EXTRUSION, "extrusion_die", ExtrusionDieBlock::new)
        .initialProperties(SharedProperties::softMetal)
        .properties(BlockBehaviour.Properties::noCollission)
        .transform(pickaxeOnly())
        .item()
        .tag(PetrolparkTags.Items.FLAGGABLE.tag)
        .build()
        .register();

    public static final BlockEntry<HarnessBlock> HARNESS = REGISTRATE.sharedBlock(SharedFeatureFlag.HORSE_MILL, "harness", HarnessBlock::new)
        .onRegister(movementBehaviour(new HarnessMovementBehaviour()))
        .item()
        .build()
        .register();
    
    public static final BlockEntry<HorseMillBearingBlock> HORSE_MILL_BEARING = REGISTRATE.sharedBlock(SharedFeatureFlag.HORSE_MILL, "horse_mill_bearing", HorseMillBearingBlock::new)
        .transform(axeOrPickaxe())
        .properties(p -> p
            .noOcclusion()
            .mapColor(MapColor.PODZOL)
        ).onRegister(BlockStressValues.setGeneratorSpeed(16, true))
        .tag(AllBlockTags.SAFE_NBT.tag)
        .item()
        .tag(PetrolparkTags.Items.FLAGGABLE.tag)
        .build()
        .register();

    public static final BlockEntry<HarnessWithCowDummyBlock> HARNESS_WITH_COW_DUMMY = REGISTRATE.sharedBlock(SharedFeatureFlag.HORSE_MILL, "harness_with_cow_dummy", HarnessWithCowDummyBlock::new)
        .properties(p -> p
            .noOcclusion()
            .noLootTable()
        ).register();

    public static final BlockEntry<MandrelBlock> MANDREL = REGISTRATE.sharedBlock(SharedFeatureFlag.MANDREL, "mandrel", MandrelBlock::new)
        .initialProperties(SharedProperties::stone)
        .blockstate(BlockStateGen.horizontalBlockProvider(true))
        .transform(axeOrPickaxe())
        .item()
        .tag(PetrolparkTags.Items.FLAGGABLE.tag)
        .transform(ModelGen.customItemModel())
        .register();

    public static final BlockEntry<MeshBasinBlock> MESH_BASIN = REGISTRATE.sharedBlock(SharedFeatureFlag.MESH_BASIN, "mesh_basin", MeshBasinBlock::new)
        .initialProperties(SharedProperties::copperMetal)
        .transform(pickaxeOnly())
        .onRegister(movementBehaviour(new BasinMovementBehaviour()))
        .item()
        .build()
        .register();
    
    public static final BlockEntry<TubeStructuralBlock> TUBE_STRUCTURE = REGISTRATE.block("tube", TubeStructuralBlock::new)
        .properties(p -> p
            .noCollission()
            .noLootTable()
            .pushReaction(PushReaction.DESTROY)
        ).blockstate((c, p) -> {})
        .register();

    public static final BlockEntry<RedstoneProgrammerBlock> REDSTONE_PROGRAMMER = REGISTRATE.sharedBlock(SharedFeatureFlag.REDSTONE_PROGRAMMER, "redstone_programmer", RedstoneProgrammerBlock::new)
        .initialProperties(SharedProperties::wooden)
        .properties(p -> p
            .noOcclusion()
        ).loot((lt, b) -> lt.add(b, LootTable.lootTable()
            .withPool(
                lt.applyExplosionCondition(b, LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1f))
                    .add(LootItem.lootTableItem(b).apply(CopyComponentsFunction.copyComponents(CopyComponentsFunction.Source.BLOCK_ENTITY)
                        .include(PetrolparkCreateDataComponentTypes.REDSTONE_PROGRAM)
                    ))
                )
            ))
        ).transform(axeOrPickaxe())
        .item(RedstoneProgrammerBlockItem::new)
        .build()
        .register();

    public static final void register() {};
};
