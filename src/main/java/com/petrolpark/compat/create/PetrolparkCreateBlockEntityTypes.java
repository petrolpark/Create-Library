package com.petrolpark.compat.create;

import static com.petrolpark.compat.create.PetrolparkCreate.REGISTRATE;

import com.petrolpark.RequiresCreate;
import com.petrolpark.compat.SharedFeatureFlag;
import com.petrolpark.compat.create.common.kinetics.VerticalBearingRenderer;
import com.petrolpark.compat.create.common.kinetics.VerticalBearingVisual;
import com.petrolpark.compat.create.common.kinetics.horseMill.HorseMillBearingBlockEntity;
import com.petrolpark.compat.create.common.kinetics.torquelimiter.TorqueLimiterInputBlockEntity;
import com.petrolpark.compat.create.common.kinetics.torquelimiter.TorqueLimiterOutputBlockEntity;
import com.petrolpark.compat.create.common.processing.basinlid.BasinLidBlockEntity;
import com.petrolpark.compat.create.common.processing.blender.BlenderBlockEntity;
import com.petrolpark.compat.create.common.processing.blender.BlenderRenderer;
import com.petrolpark.compat.create.common.processing.blender.BlenderVisual;
import com.petrolpark.compat.create.common.processing.centrifuge.CentrifugeBlockEntity;
import com.petrolpark.compat.create.common.processing.centrifuge.CentrifugeRenderer;
import com.petrolpark.compat.create.common.processing.crushingWheel.EncasedCrushingWheelControllerBlockEntity;
import com.petrolpark.compat.create.common.processing.extrusion.ExtrusionDieBlockEntity;
import com.petrolpark.compat.create.common.processing.mandrel.MandrelBlockEntity;
import com.petrolpark.compat.create.common.processing.mandrel.MandrelRenderer;
import com.petrolpark.compat.create.common.processing.meshbasin.MeshBasinBlockEntity;
import com.petrolpark.compat.create.common.processing.meshbasin.MeshBasinRenderer;
import com.petrolpark.compat.create.common.redstone.programmer.RedstoneProgrammerBlockEntity;
import com.petrolpark.compat.create.common.redstone.programmer.RedstoneProgrammerBlockEntityRenderer;
import com.petrolpark.compat.create.core.dough.DoughBlockEntity;
import com.petrolpark.compat.create.core.dough.DoughBlockEntityRenderer;
import com.petrolpark.compat.create.core.tube.TubeStructuralBlockEntity;
import com.simibubi.create.content.kinetics.base.SingleAxisRotatingVisual;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

@RequiresCreate
public class PetrolparkCreateBlockEntityTypes {

    public static final BlockEntityEntry<BasinLidBlockEntity> BASIN_LID = REGISTRATE.sharedCreateBlockEntity(SharedFeatureFlag.BASIN_LID, "basin_lid", BasinLidBlockEntity::new)
        .validBlock(PetrolparkCreateBlocks.BASIN_LID)
        .register();

    public static final BlockEntityEntry<BlenderBlockEntity> BLENDER = REGISTRATE.sharedCreateBlockEntity(SharedFeatureFlag.BLENDER, "blender", BlenderBlockEntity::new)
        .visual(() -> BlenderVisual::new)
        .validBlock(PetrolparkCreateBlocks.BLENDER)
        .renderer(() -> BlenderRenderer::new)
        .register();

    public static final BlockEntityEntry<CentrifugeBlockEntity> CENTRIFUGE = REGISTRATE.sharedCreateBlockEntity(SharedFeatureFlag.CENTRIFUGE, "centrifuge", CentrifugeBlockEntity::new)
        .registerItemCapability(CentrifugeBlockEntity::getItemHandler)
        .registerFluidCapability(CentrifugeBlockEntity::getFluidHandler)
        .visual(() -> SingleAxisRotatingVisual.of(PetrolparkPartialModels.CENTRIFUGE_COG), true)
        .validBlock(PetrolparkCreateBlocks.CENTRIFUGE)
        .renderer(() -> CentrifugeRenderer::new)
        .register();

    public static final BlockEntityEntry<DoughBlockEntity> DOUGH = REGISTRATE.blockEntity("dough", DoughBlockEntity::new)
        .validBlock(PetrolparkCreateBlocks.DOUGH)
        .renderer(() -> DoughBlockEntityRenderer::new)
        .register();

    public static final BlockEntityEntry<EncasedCrushingWheelControllerBlockEntity> ENCASED_CRUSHING_WHEEL_CONTROLLER = REGISTRATE.blockEntity("encased_crushing_wheel_controller", EncasedCrushingWheelControllerBlockEntity::new)
        .validBlock(PetrolparkCreateBlocks.ENCASED_CRUSHING_WHEEL_CONTROLLER)
        .renderer(() -> SmartBlockEntityRenderer::new)
        .register();

    public static final BlockEntityEntry<ExtrusionDieBlockEntity> EXTRUSION_DIE = REGISTRATE.sharedCreateBlockEntity(SharedFeatureFlag.EXTRUSION, "extrusion_die", ExtrusionDieBlockEntity::new)
        .validBlock(PetrolparkCreateBlocks.EXTRUSION_DIE)
        .register();

    public static final BlockEntityEntry<HorseMillBearingBlockEntity> HORSE_MILL_BEARING = REGISTRATE.sharedCreateBlockEntity(SharedFeatureFlag.HORSE_MILL, "horse_mill_bearing", HorseMillBearingBlockEntity::new)
        .visual(() -> VerticalBearingVisual::new)
        .validBlock(PetrolparkCreateBlocks.HORSE_MILL_BEARING)
        .renderer(() -> VerticalBearingRenderer::new)
        .register();

    public static final BlockEntityEntry<MandrelBlockEntity> MANDREL = REGISTRATE.sharedCreateBlockEntity(SharedFeatureFlag.MANDREL, "mandrel", MandrelBlockEntity::new)
        .validBlock(PetrolparkCreateBlocks.MANDREL)
        .renderer(() -> MandrelRenderer::new)
        .register();

    public static final BlockEntityEntry<MeshBasinBlockEntity> MESH_BASIN = REGISTRATE.sharedCreateBlockEntity(SharedFeatureFlag.MESH_BASIN, "mesh_basin", MeshBasinBlockEntity::new)
        .registerItemCapability(MeshBasinBlockEntity::getItemHandler)
        .registerFluidCapability(MeshBasinBlockEntity::getFluidHandler)
        .validBlock(PetrolparkCreateBlocks.MESH_BASIN)
        .renderer(() -> MeshBasinRenderer::new)
        .register();

    public static final BlockEntityEntry<RedstoneProgrammerBlockEntity> REDSTONE_PROGRAMMER = REGISTRATE.sharedCreateBlockEntity(SharedFeatureFlag.REDSTONE_PROGRAMMER, "redstone_programmer", RedstoneProgrammerBlockEntity::new)
        .validBlock(PetrolparkCreateBlocks.REDSTONE_PROGRAMMER)
        .renderer(() -> RedstoneProgrammerBlockEntityRenderer::new)
        .register();

    public static final BlockEntityEntry<TubeStructuralBlockEntity> TUBE_STRUCTURE = REGISTRATE.blockEntity("tube_structure", TubeStructuralBlockEntity::new)
        .validBlock(PetrolparkCreateBlocks.TUBE_STRUCTURE)
        .register();

    @Deprecated
    public static final BlockEntityEntry<TorqueLimiterInputBlockEntity> TORQUE_LIMITER_INPUT = REGISTRATE.sharedCreateBlockEntity(SharedFeatureFlag.TORQUE_LIMITER, "torque_limiter_input", TorqueLimiterInputBlockEntity::new)
        .validBlock(PetrolparkCreateBlocks.TORQUE_LIMITER_INPUT)
        .register();

    @Deprecated
    public static final BlockEntityEntry<TorqueLimiterOutputBlockEntity> TORQUE_LIMITER_OUTPUT = REGISTRATE.sharedCreateBlockEntity(SharedFeatureFlag.TORQUE_LIMITER, "torque_limiter_output", TorqueLimiterOutputBlockEntity::new)
        .validBlock(PetrolparkCreateBlocks.TORQUE_LIMITER_OUTPUT)
        .register();

    public static final void register() {};

    
};
