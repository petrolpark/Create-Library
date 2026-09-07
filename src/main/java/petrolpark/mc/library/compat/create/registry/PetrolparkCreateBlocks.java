package petrolpark.mc.library.compat.create.registry;

import static petrolpark.mc.library.Petrolpark.REGISTRATE;

import com.simibubi.create.AllBlocks;
import com.tterrag.registrate.util.entry.BlockEntry;

import net.minecraft.world.level.material.PushReaction;
import petrolpark.mc.library.compat.create.core.world.block.crushingWheel.EncasedCrushingWheelControllerBlock;
import petrolpark.mc.library.compat.create.core.world.block.tube.TubeStructuralBlock;

public class PetrolparkCreateBlocks {

    public static final BlockEntry<EncasedCrushingWheelControllerBlock> ENCASED_CRUSHING_WHEEL_CONTROLLER = REGISTRATE.block("encased_crushing_wheel_controller", EncasedCrushingWheelControllerBlock::new)
        .initialProperties(AllBlocks.CRUSHING_WHEEL_CONTROLLER)
        .register();
    
    public static final BlockEntry<TubeStructuralBlock> TUBE_STRUCTURE = REGISTRATE.block("tube", TubeStructuralBlock::new)
        .properties(p -> p
            .noCollission()
            .noLootTable()
            .pushReaction(PushReaction.DESTROY)
        ).blockstate((c, p) -> {})
        .register();

    public static final void register() {};
};
