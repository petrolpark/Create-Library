package com.petrolpark.compat.create;

import com.petrolpark.compat.SharedFeatureFlag;
import com.petrolpark.compat.create.common.processing.basinlid.BasinLidScenes;
import com.petrolpark.compat.create.common.processing.blender.BlenderScenes;
import com.petrolpark.compat.create.common.processing.centrifuge.CentrifugeScenes;
import com.petrolpark.compat.create.common.processing.crushingWheel.CrushingWheelScenes;
import com.petrolpark.compat.create.common.processing.extrusion.ExtrusionScenes;
import com.petrolpark.compat.create.common.processing.meshbasin.MeshBasinScenes;
import com.petrolpark.compat.create.common.redstone.programmer.RedstoneProgrammerPonderScenes;
import com.petrolpark.compat.create.core.chainconveyor.ChainConveyorScenes;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.Create;
import com.simibubi.create.infrastructure.ponder.AllCreatePonderTags;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;

import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.minecraft.resources.ResourceLocation;

public class PetrolparkCreatePonderScenes {
    
    public static void register(PonderSceneRegistrationHelper<ResourceLocation> helper) {
		final PonderSceneRegistrationHelper<ItemProviderEntry<?, ?>> itemProviderHelper = helper.withKeyFunction(RegistryEntry::getId);

		if (SharedFeatureFlag.ARMS_TARGET_CHAIN_CONVEYORS.enabled()) {
			itemProviderHelper.addStoryBoard(AllBlocks.MECHANICAL_ARM, "arm_chain_conveyor", ChainConveyorScenes::mechanicalArmChainConveyor, AllCreatePonderTags.HIGH_LOGISTICS)
				.orderAfter(Create.ID, "mechanical_arm/redstone");
			itemProviderHelper.addStoryBoard(AllBlocks.CHAIN_CONVEYOR, "arm_chain_conveyor", ChainConveyorScenes::mechanicalArmChainConveyor)
				.orderAfter(Create.ID, "high_logistics/chain_conveyor");
			if (SharedFeatureFlag.DRYING_RACK.enabled()) itemProviderHelper.addStoryBoard(AllBlocks.CHAIN_CONVEYOR, "processing/drying/chain_conveyor", ChainConveyorScenes::drying)
				.orderAfter("processing/drying/rack");
		};

		if (SharedFeatureFlag.BASIN_LID.enabled()) itemProviderHelper.forComponents(PetrolparkCreateBlocks.BASIN_LID)
			.addStoryBoard("processing/basin_lid", BasinLidScenes::basinLid);

		if (SharedFeatureFlag.BLENDER.enabled()) {
			itemProviderHelper.addStoryBoard(PetrolparkCreateBlocks.BLENDER, "processing/blender/blending", BlenderScenes::blending);
			if (SharedFeatureFlag.BLOOD.enabled()) itemProviderHelper.addStoryBoard(PetrolparkCreateBlocks.BLENDER, "processing/blender/blood", BlenderScenes::blood);
		};

		if (SharedFeatureFlag.CENTRIFUGE.enabled()) itemProviderHelper.forComponents(PetrolparkCreateBlocks.CENTRIFUGE)
			.addStoryBoard("processing/centrifuge", CentrifugeScenes::centrifuge);

		itemProviderHelper.forComponents(AllBlocks.CRUSHING_WHEEL)
			.addStoryBoard("processing/crushing_wheel_filtering", CrushingWheelScenes::filtering);
			
		if (SharedFeatureFlag.EXTRUSION.enabled()) itemProviderHelper.forComponents(PetrolparkCreateBlocks.EXTRUSION_DIE)
			.addStoryBoard("processing/extrusion", ExtrusionScenes::extrusionDie, AllCreatePonderTags.CONTRAPTION_ACTOR);
	
		if (SharedFeatureFlag.MESH_BASIN.enabled()) itemProviderHelper.forComponents(PetrolparkCreateBlocks.MESH_BASIN)
			.addStoryBoard("processing/mesh_basin/juicing", MeshBasinScenes::juicing)
			.addStoryBoard("processing/mesh_basin/deep_frying", MeshBasinScenes::deepFrying);

		if (SharedFeatureFlag.REDSTONE_PROGRAMMER.enabled()) itemProviderHelper.forComponents(PetrolparkCreateBlocks.REDSTONE_PROGRAMMER)
			.addStoryBoard("redstone/programmer", RedstoneProgrammerPonderScenes::redstoneProgrammer, AllCreatePonderTags.REDSTONE);
	}
};
