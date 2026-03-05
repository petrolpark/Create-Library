package com.petrolpark.compat.create;

import static com.petrolpark.Petrolpark.REGISTRATE;

import com.petrolpark.PetrolparkRegistrate;
import com.petrolpark.RequiresCreate;
import com.petrolpark.compat.SharedFeatureFlag;
import com.petrolpark.compat.create.common.kinetics.torquelimiter.TorqueLimiterInputBlockEntity;
import com.petrolpark.compat.create.common.kinetics.torquelimiter.TorqueLimiterOutputBlockEntity;
import com.petrolpark.compat.create.common.processing.basinlid.BasinLidBlockEntity;
import com.petrolpark.compat.create.common.processing.blender.BlenderBlockEntity;
import com.petrolpark.compat.create.common.processing.blender.BlenderRenderer;
import com.petrolpark.compat.create.common.processing.blender.BlenderVisual;
import com.petrolpark.compat.create.common.processing.centrifuge.CentrifugeBlockEntity;
import com.petrolpark.compat.create.common.processing.centrifuge.CentrifugeRenderer;
import com.petrolpark.compat.create.common.processing.extrusion.ExtrusionDieBlockEntity;
import com.petrolpark.compat.create.common.processing.mandrel.MandrelBlockEntity;
import com.petrolpark.compat.create.common.processing.mandrel.MandrelRenderer;
import com.petrolpark.compat.create.common.processing.meshbasin.MeshBasinBlockEntity;
import com.petrolpark.compat.create.common.processing.meshbasin.MeshBasinRenderer;
import com.petrolpark.compat.create.common.redstone.programmer.RedstoneProgrammerBlockEntity;
import com.petrolpark.compat.create.common.redstone.programmer.RedstoneProgrammerBlockEntityRenderer;
import com.petrolpark.compat.create.core.tube.TubeStructuralBlockEntity;
import com.petrolpark.core.registrate.SharedCreateBlockEntityBuilder;
import com.simibubi.create.content.kinetics.base.SingleAxisRotatingVisual;
import com.tterrag.registrate.builders.BlockEntityBuilder.BlockEntityFactory;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

import net.minecraft.world.level.block.entity.BlockEntity;

@RequiresCreate
public class PetrolparkCreateBlockEntityTypes {

    public static final BlockEntityEntry<BasinLidBlockEntity> BASIN_LID = sharedBlockEntity(SharedFeatureFlag.BASIN_LID, "basin_lid", BasinLidBlockEntity::new)
        .validBlock(PetrolparkCreateBlocks.BASIN_LID)
        .register();

    public static final BlockEntityEntry<BlenderBlockEntity> BLENDER = sharedBlockEntity(SharedFeatureFlag.BLENDER, "blender", BlenderBlockEntity::new)
        .visual(() -> BlenderVisual::new)
        .validBlock(PetrolparkCreateBlocks.BLENDER)
        .renderer(() -> BlenderRenderer::new)
        .register();

    public static final BlockEntityEntry<CentrifugeBlockEntity> CENTRIFUGE = sharedBlockEntity(SharedFeatureFlag.CENTRIFUGE, "centrifuge", CentrifugeBlockEntity::new)
        .visual(() -> SingleAxisRotatingVisual.of(PetrolparkPartialModels.CENTRIFUGE_COG), true)
        .validBlock(PetrolparkCreateBlocks.CENTRIFUGE)
        .renderer(() -> CentrifugeRenderer::new)
        .register();

    public static final BlockEntityEntry<ExtrusionDieBlockEntity> EXTRUSION_DIE = sharedBlockEntity(SharedFeatureFlag.EXTRUSION, "extrusion_die", ExtrusionDieBlockEntity::new)
        .validBlock(PetrolparkCreateBlocks.EXTRUSION_DIE)
        .register();

    public static final BlockEntityEntry<MandrelBlockEntity> MANDREL = sharedBlockEntity(SharedFeatureFlag.MANDREL, "mandrel", MandrelBlockEntity::new)
        .validBlock(PetrolparkCreateBlocks.MANDREL)
        .renderer(() -> MandrelRenderer::new)
        .register();

    public static final BlockEntityEntry<MeshBasinBlockEntity> MESH_BASIN = sharedBlockEntity(SharedFeatureFlag.MESH_BASIN, "mesh_basin", MeshBasinBlockEntity::new)
        .validBlock(PetrolparkCreateBlocks.MESH_BASIN)
        .renderer(() -> MeshBasinRenderer::new)
        .register();

    public static final BlockEntityEntry<RedstoneProgrammerBlockEntity> REDSTONE_PROGRAMMER = sharedBlockEntity(SharedFeatureFlag.REDSTONE_PROGRAMMER, "redstone_programmer", RedstoneProgrammerBlockEntity::new)
        .validBlock(PetrolparkCreateBlocks.REDSTONE_PROGRAMMER)
        .renderer(() -> RedstoneProgrammerBlockEntityRenderer::new)
        .register();

    public static final BlockEntityEntry<TubeStructuralBlockEntity> TUBE_STRUCTURE = REGISTRATE.blockEntity("tube_structure", TubeStructuralBlockEntity::new)
        .validBlock(PetrolparkCreateBlocks.TUBE_STRUCTURE)
        .register();

    @Deprecated
    public static final BlockEntityEntry<TorqueLimiterInputBlockEntity> TORQUE_LIMITER_INPUT = sharedBlockEntity(SharedFeatureFlag.TORQUE_LIMITER, "torque_limiter_input", TorqueLimiterInputBlockEntity::new)
        .validBlock(PetrolparkCreateBlocks.TORQUE_LIMITER_INPUT)
        .register();

    @Deprecated
    public static final BlockEntityEntry<TorqueLimiterOutputBlockEntity> TORQUE_LIMITER_OUTPUT = sharedBlockEntity(SharedFeatureFlag.TORQUE_LIMITER, "torque_limiter_output", TorqueLimiterOutputBlockEntity::new)
        .validBlock(PetrolparkCreateBlocks.TORQUE_LIMITER_OUTPUT)
        .register();

    public static final void register() {};

    private static final <T extends BlockEntity> SharedCreateBlockEntityBuilder<T, PetrolparkRegistrate> sharedBlockEntity(SharedFeatureFlag featureFlag, String name, BlockEntityFactory<T> factory) {
        return (SharedCreateBlockEntityBuilder<T, PetrolparkRegistrate>)REGISTRATE.sharedEntry(featureFlag, name, callback -> SharedCreateBlockEntityBuilder.create(REGISTRATE, REGISTRATE, featureFlag, name, callback, factory));
    };
};
