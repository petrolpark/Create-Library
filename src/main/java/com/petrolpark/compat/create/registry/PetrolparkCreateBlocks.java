package com.petrolpark.compat.create.registry;

import static com.petrolpark.Petrolpark.REGISTRATE;

import com.petrolpark.PetrolparkTags;
import com.petrolpark.compat.create.core.world.block.crushingWheel.EncasedCrushingWheelControllerBlock;
import com.petrolpark.compat.create.core.world.block.tube.TubeStructuralBlock;
import com.petrolpark.compat.create.core.world.dough.DoughBlock;
import com.petrolpark.compat.create.core.world.dough.DoughItem;
import com.simibubi.create.AllBlocks;
import com.tterrag.registrate.util.entry.BlockEntry;

import net.minecraft.world.level.material.PushReaction;

public class PetrolparkCreateBlocks {

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
    
    public static final BlockEntry<TubeStructuralBlock> TUBE_STRUCTURE = REGISTRATE.block("tube", TubeStructuralBlock::new)
        .properties(p -> p
            .noCollission()
            .noLootTable()
            .pushReaction(PushReaction.DESTROY)
        ).blockstate((c, p) -> {})
        .register();

    public static final void register() {};
};
