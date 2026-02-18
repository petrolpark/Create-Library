package com.petrolpark.compat.create;

import com.petrolpark.compat.SharedFeatureFlag;
import com.petrolpark.compat.create.common.processing.basinlid.BasinLidScenes;
import com.petrolpark.compat.create.common.processing.centrifuge.CentrifugeScenes;
import com.petrolpark.compat.create.common.processing.extrusion.ExtrusionScenes;
import com.petrolpark.compat.create.common.redstone.programmer.RedstoneProgrammerPonderScenes;
import com.petrolpark.compat.create.core.chainconveyor.ChainConveyorScenes;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.infrastructure.ponder.AllCreatePonderTags;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;

import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.minecraft.resources.ResourceLocation;

public class PetrolparkCreatePonderScenes {
    
    public static void register(PonderSceneRegistrationHelper<ResourceLocation> helper) {
		final PonderSceneRegistrationHelper<ItemProviderEntry<?, ?>> itemProviderHelper = helper.withKeyFunction(RegistryEntry::getId);

		if (SharedFeatureFlag.BASIN_LID.enabled()) itemProviderHelper.forComponents(CreateBlocks.BASIN_LID)
			.addStoryBoard("processing/basin_lid", BasinLidScenes::basinLid);

		if (SharedFeatureFlag.CENTRIFUGE.enabled()) itemProviderHelper.forComponents(CreateBlocks.CENTRIFUGE)
			.addStoryBoard("processing/centrifuge", CentrifugeScenes::centrifuge);

		if (SharedFeatureFlag.ARMS_TARGET_CHAIN_CONVEYORS.enabled()) {
			itemProviderHelper.addStoryBoard(AllBlocks.MECHANICAL_ARM, "arm_chain_conveyor", ChainConveyorScenes::mechanicalArmChainConveyor, AllCreatePonderTags.HIGH_LOGISTICS);
			itemProviderHelper.addStoryBoard(AllBlocks.CHAIN_CONVEYOR, "arm_chain_conveyor", ChainConveyorScenes::mechanicalArmChainConveyor);
			if (SharedFeatureFlag.DRYING_RACK.enabled()) itemProviderHelper.addStoryBoard(AllBlocks.CHAIN_CONVEYOR, "processing/drying_chain_conveyor", ChainConveyorScenes::drying);
		};

		if (SharedFeatureFlag.EXTRUSION.enabled()) itemProviderHelper.forComponents(CreateBlocks.EXTRUSION_DIE)
			.addStoryBoard("processing/extrusion", ExtrusionScenes::extrusionDie, AllCreatePonderTags.CONTRAPTION_ACTOR);

		if (SharedFeatureFlag.REDSTONE_PROGRAMMER.enabled()) itemProviderHelper.forComponents(CreateBlocks.REDSTONE_PROGRAMMER)
			.addStoryBoard("redstone/programmer", RedstoneProgrammerPonderScenes::redstoneProgrammer, AllCreatePonderTags.REDSTONE);
	}
};
