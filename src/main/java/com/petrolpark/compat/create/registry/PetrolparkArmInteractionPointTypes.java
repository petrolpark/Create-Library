package com.petrolpark.compat.create.registry;

import com.petrolpark.Petrolpark;
import com.petrolpark.compat.create.core.world.block.chainConveyor.ChainConveyorArmInteractionPoint;
import com.simibubi.create.api.registry.CreateRegistries;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPointType;
import com.tterrag.registrate.util.entry.RegistryEntry;

public class PetrolparkArmInteractionPointTypes {

    public static final RegistryEntry<ArmInteractionPointType, ChainConveyorArmInteractionPoint.Type> CHAIN_CONVEYOR = Petrolpark.REGISTRATE.simple("chain_conveyor", CreateRegistries.ARM_INTERACTION_POINT_TYPE, () -> new ChainConveyorArmInteractionPoint.Type());
    
    public static final void register() {};
};
