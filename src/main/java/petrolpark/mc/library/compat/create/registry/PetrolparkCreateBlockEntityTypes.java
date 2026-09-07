package petrolpark.mc.library.compat.create.registry;

import static petrolpark.mc.library.compat.create.PetrolparkCreate.REGISTRATE;

import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

import petrolpark.mc.library.compat.create.RequiresCreate;
import petrolpark.mc.library.compat.create.core.world.block.crushingWheel.EncasedCrushingWheelControllerBlockEntity;
import petrolpark.mc.library.compat.create.core.world.block.tube.TubeStructuralBlockEntity;

@RequiresCreate
public class PetrolparkCreateBlockEntityTypes {

    public static final BlockEntityEntry<EncasedCrushingWheelControllerBlockEntity> ENCASED_CRUSHING_WHEEL_CONTROLLER = REGISTRATE.blockEntity("encased_crushing_wheel_controller", EncasedCrushingWheelControllerBlockEntity::new)
        .validBlock(PetrolparkCreateBlocks.ENCASED_CRUSHING_WHEEL_CONTROLLER)
        .renderer(() -> SmartBlockEntityRenderer::new)
        .register();

    public static final BlockEntityEntry<TubeStructuralBlockEntity> TUBE_STRUCTURE = REGISTRATE.blockEntity("tube_structure", TubeStructuralBlockEntity::new)
        .validBlock(PetrolparkCreateBlocks.TUBE_STRUCTURE)
        .register();

    public static final void register() {};

    
};
