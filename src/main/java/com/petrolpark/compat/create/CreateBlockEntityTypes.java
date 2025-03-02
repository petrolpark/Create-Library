package com.petrolpark.compat.create;

import static com.petrolpark.Petrolpark.REGISTRATE;

import com.petrolpark.RequiresCreate;
import com.petrolpark.tube.TubeStructuralBlockEntity;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

@RequiresCreate
public class CreateBlockEntityTypes {

    public static final BlockEntityEntry<TubeStructuralBlockEntity> TUBE_STRUCTURE = REGISTRATE.get().blockEntity("tube_structure", TubeStructuralBlockEntity::new)
        .validBlock(CreateBlocks.TUBE_STRUCTURE)
        .register();

    public static final void register() {};
};
