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

        if (SharedFeatureFlag.ARMS_TARGET_CHAIN_CONVEYORS.enabled()) registrateHelper.addToTag(AllCreatePonderTags.ARM_TARGETS)
            .add(AllBlocks.CHAIN_CONVEYOR);

        if (SharedFeatureFlag.BLENDER.enabled()) registrateHelper.addToTag(AllCreatePonderTags.KINETIC_APPLIANCES)
            .add(CreateBlocks.BLENDER);

        if (SharedFeatureFlag.DRYING_RACK.enabled()) registrateHelper.addToTag(AllCreatePonderTags.ARM_TARGETS)
            .add(PetrolparkBlocks.DRYING_RACK);

        if (SharedFeatureFlag.MESH_BASIN.enabled()) registrateHelper.addToTag(AllCreatePonderTags.ARM_TARGETS)
            .add(CreateBlocks.MESH_BASIN);
    };
};
