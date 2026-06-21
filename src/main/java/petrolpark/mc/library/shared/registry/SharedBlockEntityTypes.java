package petrolpark.mc.library.shared.registry;

import static petrolpark.mc.library.Petrolpark.REGISTRATE;

import petrolpark.mc.library.core.scratch.world.block.ProgrammingBlockEntity;
import petrolpark.mc.library.core.world.block.soil.SoilBlockEntity;
import petrolpark.mc.library.shared.SharedFeatureFlag;
import petrolpark.mc.library.shared.world.item.crafting.drying.rack.DryingRackBlockEntity;
import petrolpark.mc.library.shared.world.item.crafting.drying.rack.DryingRackBlockEntityRenderer;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

public class SharedBlockEntityTypes {

    public static final BlockEntityEntry<DryingRackBlockEntity> DRYING_RACK = REGISTRATE.sharedBlockEntity(SharedFeatureFlag.DRYING_RACK, "drying_rack", DryingRackBlockEntity::new)
        .registerItemCapability(DryingRackBlockEntity::getItemHandler)
        .validBlock(SharedBlocks.DRYING_RACK)
        .renderer(() -> DryingRackBlockEntityRenderer::new)
        .register();

    public static final BlockEntityEntry<SoilBlockEntity> SOIL = REGISTRATE.blockEntity("soil", SoilBlockEntity::new)
        .register();
  
    public static final BlockEntityEntry<ProgrammingBlockEntity> PROGRAMMING_BLOCK = REGISTRATE.sharedBlockEntity(SharedFeatureFlag.PROGRAMMING_BLOCK, "programming_block", ProgrammingBlockEntity::new)
        .validBlock(SharedBlocks.PROGRAMMING_BLOCK)
        .register();
    
    public static final void register() {};
};
