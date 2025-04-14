package com.petrolpark.compat.create;

import com.petrolpark.Petrolpark;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;

public class PetrolparkPartialModels {
    
    public static final PartialModel

    MANDREL_SHAFT = block("mandrel/shaft");

    private static PartialModel block(String path) {
        return PartialModel.of(Petrolpark.asResource("block/"+path));
    };

    public static final void register() {};
};
