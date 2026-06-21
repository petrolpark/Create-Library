package petrolpark.mc.library.compat.create.registry;

import static petrolpark.mc.library.compat.create.PetrolparkCreate.REGISTRATE;

import petrolpark.mc.library.compat.create.RequiresCreate;
import petrolpark.mc.library.compat.create.core.world.block.crushingWheel.EncasedCrushingWheelControllerBlockEntity;
import petrolpark.mc.library.compat.create.core.world.block.tube.TubeStructuralBlockEntity;
import petrolpark.mc.library.compat.create.core.world.dough.DoughBlockEntity;
import petrolpark.mc.library.compat.create.core.world.dough.DoughBlockEntityRenderer;
import petrolpark.mc.library.compat.create.shared.registry.SharedCreateBlocks;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

@RequiresCreate
public class PetrolparkCreateBlockEntityTypes {

    public static final BlockEntityEntry<DoughBlockEntity> DOUGH = REGISTRATE.blockEntity("dough", DoughBlockEntity::new)
        .validBlock(SharedCreateBlocks.DOUGH)
        .renderer(() -> DoughBlockEntityRenderer::new)
        .register();

    public static final BlockEntityEntry<EncasedCrushingWheelControllerBlockEntity> ENCASED_CRUSHING_WHEEL_CONTROLLER = REGISTRATE.blockEntity("encased_crushing_wheel_controller", EncasedCrushingWheelControllerBlockEntity::new)
        .validBlock(SharedCreateBlocks.ENCASED_CRUSHING_WHEEL_CONTROLLER)
        .renderer(() -> SmartBlockEntityRenderer::new)
        .register();

    public static final BlockEntityEntry<TubeStructuralBlockEntity> TUBE_STRUCTURE = REGISTRATE.blockEntity("tube_structure", TubeStructuralBlockEntity::new)
        .validBlock(SharedCreateBlocks.TUBE_STRUCTURE)
        .register();

    public static final void register() {};

    
};
