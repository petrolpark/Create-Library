package com.petrolpark.compat.create;

import com.petrolpark.compat.SharedFeatureFlag;
import com.petrolpark.compat.create.common.processing.extrusion.ExtrusionScenes;
import com.petrolpark.compat.create.common.redstone.programmer.RedstoneProgrammerPonderScenes;
import com.simibubi.create.infrastructure.ponder.AllCreatePonderTags;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;

import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.minecraft.resources.ResourceLocation;

public class CreatePonderScenes {
    
    public static void register(PonderSceneRegistrationHelper<ResourceLocation> helper) {
		final PonderSceneRegistrationHelper<ItemProviderEntry<?, ?>> HELPER = helper.withKeyFunction(RegistryEntry::getId);

		if (SharedFeatureFlag.EXTRUSION.enabled()) HELPER.forComponents(CreateBlocks.EXTRUSION_DIE)
			.addStoryBoard("processing/extrusion", ExtrusionScenes::extrusionDie, AllCreatePonderTags.CONTRAPTION_ACTOR);

		if (SharedFeatureFlag.REDSTONE_PROGRAMMER.enabled()) HELPER.forComponents(CreateBlocks.REDSTONE_PROGRAMMER)
			.addStoryBoard("redstone/programmer", RedstoneProgrammerPonderScenes::redstoneProgrammer, AllCreatePonderTags.REDSTONE);
	}
};
