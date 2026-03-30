package com.petrolpark.compat.create;

import static com.petrolpark.Petrolpark.REGISTRATE;
import static com.petrolpark.core.registrate.PetrolparkTagGen.axeOrPickaxe;
import static com.petrolpark.core.registrate.PetrolparkTagGen.pickaxeOnly;
import static com.simibubi.create.api.behaviour.movement.MovementBehaviour.movementBehaviour;
import static net.minecraft.world.level.storage.loot.LootPool.lootPool;
import static net.minecraft.world.level.storage.loot.LootTable.lootTable;
import static net.minecraft.world.level.storage.loot.entries.LootItem.lootTableItem;

import com.petrolpark.compat.SharedFeatureFlag;
import com.petrolpark.compat.create.common.kinetics.horseMill.HarnessBlock;
import com.petrolpark.compat.create.common.kinetics.horseMill.HorseMillBearingBlock;
import com.petrolpark.compat.create.common.kinetics.torquelimiter.TorqueLimiterInputBlock;
import com.petrolpark.compat.create.common.kinetics.torquelimiter.TorqueLimiterOutputBlock;
import com.petrolpark.compat.create.common.processing.basinlid.BasinLidBlock;
import com.petrolpark.compat.create.common.processing.blender.BlenderBlock;
import com.petrolpark.compat.create.common.processing.centrifuge.CentrifugeBlock;
import com.petrolpark.compat.create.common.processing.crushingWheel.EncasedCrushingWheelControllerBlock;
import com.petrolpark.compat.create.common.processing.extrusion.ExtrusionDieBlock;
import com.petrolpark.compat.create.common.processing.mandrel.MandrelBlock;
import com.petrolpark.compat.create.common.processing.meshbasin.MeshBasinBlock;
import com.petrolpark.compat.create.common.redstone.programmer.RedstoneProgrammerBlock;
import com.petrolpark.compat.create.common.redstone.programmer.RedstoneProgrammerBlockItem;
import com.petrolpark.compat.create.core.dough.DoughBlock;
import com.petrolpark.compat.create.core.dough.DoughItem;
import com.petrolpark.compat.create.core.tube.TubeStructuralBlock;
import com.petrolpark.config.PetrolparkStressConfig;
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
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

public class PetrolparkCreateBlocks {

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
        .onRegister(PetrolparkCreate::registerTooltip)
        .build()
        .register();

    public static final BlockEntry<CentrifugeBlock> CENTRIFUGE = REGISTRATE.sharedBlock(SharedFeatureFlag.CENTRIFUGE, "centrifuge", CentrifugeBlock::new)
        .initialProperties(SharedProperties::copperMetal)
        .properties(BlockBehaviour.Properties::noOcclusion)
        .transform(pickaxeOnly())
        .transform(PetrolparkStressConfig.setImpact(2f))
        .item()
        .onRegister(PetrolparkCreate::registerTooltip)
        .build()
        .register();

    public static final BlockEntry<DoughBlock> DOUGH = REGISTRATE.block("dough", DoughBlock::new)
        .properties(p -> p
            .noOcclusion()
            .instabreak()
        ).color(() -> () -> DoughBlock::getColor) // For particles
        .item(DoughItem::new)
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
        .build()
        .register();

    public static final BlockEntry<HarnessBlock> HARNESS = REGISTRATE.sharedBlock(SharedFeatureFlag.HORSE_MILL, "harness", HarnessBlock::new)
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
        .build()
        .register();

    public static final BlockEntry<MandrelBlock> MANDREL = REGISTRATE.sharedBlock(SharedFeatureFlag.MANDREL, "mandrel", MandrelBlock::new)
        .initialProperties(SharedProperties::stone)
        .blockstate(BlockStateGen.horizontalBlockProvider(true))
        .transform(axeOrPickaxe())
        .item()
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
        ).loot((lt, b) -> lt.add(b, lootTable()
            .withPool(
                lt.applyExplosionCondition(b, lootPool()
                    .setRolls(ConstantValue.exactly(1f))
                    .add(lootTableItem(b).apply(CopyComponentsFunction.copyComponents(CopyComponentsFunction.Source.BLOCK_ENTITY)
                        .include(PetrolparkCreateDataComponentTypes.REDSTONE_PROGRAM)
                    ))
                )
            ))
        ).transform(axeOrPickaxe())
        .item(RedstoneProgrammerBlockItem::new)
        .build()
        .register();

    @Deprecated
    public static final BlockEntry<TorqueLimiterInputBlock> TORQUE_LIMITER_INPUT = REGISTRATE.sharedBlock(SharedFeatureFlag.TORQUE_LIMITER, "torque_limiter_input", TorqueLimiterInputBlock::new)
        .blockstate((c, p) -> {})
        .register();

    @Deprecated
    public static final BlockEntry<TorqueLimiterOutputBlock> TORQUE_LIMITER_OUTPUT = REGISTRATE.sharedBlock(SharedFeatureFlag.TORQUE_LIMITER, "torque_limiter_output", TorqueLimiterOutputBlock::new)
        .blockstate((c, p) -> {})
        .register();

    public static final void register() {};
};
