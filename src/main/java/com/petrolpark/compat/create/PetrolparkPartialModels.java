package com.petrolpark.compat.create;

import com.petrolpark.Petrolpark;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;

public class PetrolparkPartialModels {
    
    public static final PartialModel

    MANDREL_SHAFT = block("mandrel/shaft"),
    CENTRIFUGE_COG = block("centrifuge/cog"),

    // Deployer
    DEPLOYER_HAND_PAPER = block("deployer/hand_paper"),
    DEPLOYER_HAND_SCISSORS = block("deployer/hand_scissors"),
    DEPLOYER_HAND_SWEARING = block("deployer/hand_swearing"),

    // Redstone Programmer
    REDSTONE_PROGRAMMER_CYLINDER = block("redstone_programmer/cylinder"),
    REDSTONE_PROGRAMMER_NEEDLE = block("redstone_programmer/needle"),
    REDSTONE_PROGRAMMER_TRANSMITTER = block("redstone_programmer/transmitter"),
    REDSTONE_PROGRAMMER_TRANSMITTER_POWERED = block("redstone_programmer/transmitter_powered"),

    // Chain Conveyor
    CHAIN_CONVEYOR_HOOK = block("chain_conveyor_hook"),

    // Blender
    BLENDER_COG = block("blender/inner"),
    BLENDER_BLADES = block("blender/blades"),

    // Harness
    COW_HARNESS = block("harness/cow")
    ;

    private static PartialModel block(String path) {
        return PartialModel.of(Petrolpark.asResource("block/"+path));
    };

    public static final void register() {};
};
