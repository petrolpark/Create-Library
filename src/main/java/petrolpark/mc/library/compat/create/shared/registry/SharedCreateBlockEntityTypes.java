package petrolpark.mc.library.compat.create.shared.registry;

import static petrolpark.mc.library.compat.create.PetrolparkCreate.REGISTRATE;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.api.behaviour.spouting.BlockSpoutingBehaviour;
import com.simibubi.create.content.kinetics.base.SingleAxisRotatingVisual;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

import petrolpark.mc.library.compat.create.RequiresCreate;
import petrolpark.mc.library.compat.create.core.world.dough.rollingPin.holder.RollingPinHolderBlockEntity;
import petrolpark.mc.library.compat.create.core.world.dough.rollingPin.holder.RollingPinHolderRenderer;
import petrolpark.mc.library.compat.create.shared.content.kinetics.VerticalBearingRenderer;
import petrolpark.mc.library.compat.create.shared.content.kinetics.VerticalBearingVisual;
import petrolpark.mc.library.compat.create.shared.content.kinetics.horseMill.HorseMillBearingBlockEntity;
import petrolpark.mc.library.compat.create.shared.content.kinetics.horseMill.ponder.HarnessWithCowDummyBlockEntity;
import petrolpark.mc.library.compat.create.shared.content.kinetics.horseMill.ponder.HarnessWithCowDummyRenderer;
import petrolpark.mc.library.compat.create.shared.content.processing.basinLid.BasinLidBlockEntity;
import petrolpark.mc.library.compat.create.shared.content.processing.blender.BlenderBlockEntity;
import petrolpark.mc.library.compat.create.shared.content.processing.blender.BlenderRenderer;
import petrolpark.mc.library.compat.create.shared.content.processing.blender.BlenderVisual;
import petrolpark.mc.library.compat.create.shared.content.processing.centrifuge.CentrifugeBlockEntity;
import petrolpark.mc.library.compat.create.shared.content.processing.centrifuge.CentrifugeRenderer;
import petrolpark.mc.library.compat.create.shared.content.processing.extrusion.ExtrusionDieBlockEntity;
import petrolpark.mc.library.compat.create.shared.content.processing.mandrel.MandrelBlockEntity;
import petrolpark.mc.library.compat.create.shared.content.processing.mandrel.MandrelRenderer;
import petrolpark.mc.library.compat.create.shared.content.processing.meshBasin.MeshBasinBlockEntity;
import petrolpark.mc.library.compat.create.shared.content.processing.meshBasin.MeshBasinRenderer;
import petrolpark.mc.library.compat.create.shared.content.processing.meshBasin.MeshBasinSpoutingBehaviour;
import petrolpark.mc.library.compat.create.shared.content.redstone.programmer.RedstoneProgrammerBlockEntity;
import petrolpark.mc.library.compat.create.shared.content.redstone.programmer.RedstoneProgrammerBlockEntityRenderer;
import petrolpark.mc.library.shared.SharedFeatureFlag;

@RequiresCreate
public class SharedCreateBlockEntityTypes {

    public static final BlockEntityEntry<BasinLidBlockEntity> BASIN_LID = REGISTRATE.sharedCreateBlockEntity(SharedFeatureFlag.BASIN_LID, "basin_lid", BasinLidBlockEntity::new)
        .validBlock(SharedCreateBlocks.BASIN_LID)
        .register();

    public static final BlockEntityEntry<BlenderBlockEntity> BLENDER = REGISTRATE.sharedCreateBlockEntity(SharedFeatureFlag.BLENDER, "blender", BlenderBlockEntity::new)
        .visual(() -> BlenderVisual::new)
        .validBlock(SharedCreateBlocks.BLENDER)
        .renderer(() -> BlenderRenderer::new)
        .register();

    public static final BlockEntityEntry<CentrifugeBlockEntity> CENTRIFUGE = REGISTRATE.sharedCreateBlockEntity(SharedFeatureFlag.CENTRIFUGE, "centrifuge", CentrifugeBlockEntity::new)
        .registerItemCapability(CentrifugeBlockEntity::getItemHandler)
        .registerFluidCapability(CentrifugeBlockEntity::getFluidHandler)
        .visual(() -> SingleAxisRotatingVisual.of(SharedPartialModels.CENTRIFUGE_COG), true)
        .validBlock(SharedCreateBlocks.CENTRIFUGE)
        .renderer(() -> CentrifugeRenderer::new)
        .register();

    public static final BlockEntityEntry<ExtrusionDieBlockEntity> EXTRUSION_DIE = REGISTRATE.sharedCreateBlockEntity(SharedFeatureFlag.EXTRUSION, "extrusion_die", ExtrusionDieBlockEntity::new)
        .validBlock(SharedCreateBlocks.EXTRUSION_DIE)
        .register();

    public static final BlockEntityEntry<HorseMillBearingBlockEntity> HORSE_MILL_BEARING = REGISTRATE.sharedCreateBlockEntity(SharedFeatureFlag.HORSE_MILL, "horse_mill_bearing", HorseMillBearingBlockEntity::new)
        .visual(() -> VerticalBearingVisual::new)
        .validBlock(SharedCreateBlocks.HORSE_MILL_BEARING)
        .renderer(() -> VerticalBearingRenderer::new)
        .register();

    // Only exists to render in Ponder
    public static final BlockEntityEntry<HarnessWithCowDummyBlockEntity> HARNESS_WITH_COW_DUMMY = REGISTRATE.sharedCreateBlockEntity(SharedFeatureFlag.HORSE_MILL, "harness_with_cow_dummy", HarnessWithCowDummyBlockEntity::new)
        .validBlock(SharedCreateBlocks.HARNESS_WITH_COW_DUMMY)
        .renderer(() -> HarnessWithCowDummyRenderer::new)
        .register();

    public static final BlockEntityEntry<MandrelBlockEntity> MANDREL = REGISTRATE.sharedCreateBlockEntity(SharedFeatureFlag.MANDREL, "mandrel", MandrelBlockEntity::new)
        .validBlock(SharedCreateBlocks.MANDREL)
        .renderer(() -> MandrelRenderer::new)
        .register();

    public static final BlockEntityEntry<MeshBasinBlockEntity> MESH_BASIN = REGISTRATE.sharedCreateBlockEntity(SharedFeatureFlag.MESH_BASIN, "mesh_basin", MeshBasinBlockEntity::new)
        .registerItemCapability(MeshBasinBlockEntity::getItemHandler)
        .registerFluidCapability(MeshBasinBlockEntity::getFluidHandler)
        .validBlock(SharedCreateBlocks.MESH_BASIN)
        .renderer(() -> MeshBasinRenderer::new)
        .onRegister(type -> BlockSpoutingBehaviour.BY_BLOCK_ENTITY.register(type, MeshBasinSpoutingBehaviour.INSTANCE))
        .register();

    public static final BlockEntityEntry<RedstoneProgrammerBlockEntity> REDSTONE_PROGRAMMER = REGISTRATE.sharedCreateBlockEntity(SharedFeatureFlag.REDSTONE_PROGRAMMER, "redstone_programmer", RedstoneProgrammerBlockEntity::new)
        .validBlock(SharedCreateBlocks.REDSTONE_PROGRAMMER)
        .renderer(() -> RedstoneProgrammerBlockEntityRenderer::new)
        .register();

    public static final BlockEntityEntry<RollingPinHolderBlockEntity> ROLLING_PIN_HOLDER = REGISTRATE.sharedCreateBlockEntity(SharedFeatureFlag.ROLLING_PIN, "rolling_pin_holder", RollingPinHolderBlockEntity::new)
        .visual(() -> SingleAxisRotatingVisual.of(AllPartialModels.SHAFTLESS_COGWHEEL), true)
        .validBlock(SharedCreateBlocks.ROLLING_PIN_HOLDER)
        .renderer(() -> RollingPinHolderRenderer::new)
        .register();

    public static final void register() {};

    
};
