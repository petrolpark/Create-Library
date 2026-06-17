package com.petrolpark.compat.create;

import com.petrolpark.PetrolparkBlocks;
import com.petrolpark.compat.SharedFeatureFlag;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.infrastructure.ponder.AllCreatePonderTags;
import com.tterrag.registrate.util.entry.RegistryEntry;

import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.resources.ResourceLocation;

public class PetrolparkCreatePonderTags {
    
    public static final void register(PonderTagRegistrationHelper<ResourceLocation> helper) {

        final PonderTagRegistrationHelper<RegistryEntry<?, ?>> registrateHelper = helper.withKeyFunction(RegistryEntry::getId);

        registrateHelper.addToTag(AllCreatePonderTags.THRESHOLD_SWITCH_TARGETS)
            .add(AllBlocks.STRESSOMETER);

        if (SharedFeatureFlag.ARMS_TARGET_CHAIN_CONVEYORS.enabled()) registrateHelper.addToTag(AllCreatePonderTags.ARM_TARGETS)
            .add(AllBlocks.CHAIN_CONVEYOR);

        if (SharedFeatureFlag.BLENDER.enabled()) registrateHelper.addToTag(AllCreatePonderTags.KINETIC_APPLIANCES)
            .add(PetrolparkCreateBlocks.BLENDER);

        if (SharedFeatureFlag.DRYING_RACK.enabled()) registrateHelper.addToTag(AllCreatePonderTags.ARM_TARGETS)
            .add(PetrolparkBlocks.DRYING_RACK);
        
        if (SharedFeatureFlag.HORSE_MILL.enabled()) {
            registrateHelper.addToTag(AllCreatePonderTags.KINETIC_SOURCES)
                .add(PetrolparkCreateBlocks.HORSE_MILL_BEARING)
                .add(PetrolparkCreateBlocks.HARNESS);
            registrateHelper.addToTag(AllCreatePonderTags.MOVEMENT_ANCHOR)
                .add(PetrolparkCreateBlocks.HORSE_MILL_BEARING);
            registrateHelper.addToTag(AllCreatePonderTags.CONTRAPTION_ACTOR)
                .add(PetrolparkCreateBlocks.HARNESS);
        };

        if (SharedFeatureFlag.MESH_BASIN.enabled()) registrateHelper.addToTag(AllCreatePonderTags.ARM_TARGETS)
            .add(PetrolparkCreateBlocks.MESH_BASIN);
    };
};
