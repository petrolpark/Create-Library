package petrolpark.mc.library.compat.create.core.client.ponder;

import petrolpark.mc.library.compat.create.core.world.block.chainConveyor.ChainConveyorScenes;
import petrolpark.mc.library.compat.create.core.world.block.crushingWheel.CrushingWheelScenes;
import petrolpark.mc.library.compat.create.shared.content.kinetics.horseMill.ponder.HorseMillScenes;
import petrolpark.mc.library.compat.create.shared.content.processing.basinLid.BasinLidScenes;
import petrolpark.mc.library.compat.create.shared.content.processing.blender.BlenderScenes;
import petrolpark.mc.library.compat.create.shared.content.processing.centrifuge.CentrifugeScenes;
import petrolpark.mc.library.compat.create.shared.content.processing.extrusion.ExtrusionScenes;
import petrolpark.mc.library.compat.create.shared.content.processing.meshBasin.MeshBasinScenes;
import petrolpark.mc.library.compat.create.shared.content.redstone.programmer.RedstoneProgrammerPonderScenes;
import petrolpark.mc.library.compat.create.shared.registry.SharedCreateBlocks;
import petrolpark.mc.library.shared.SharedFeatureFlag;
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
			itemProviderHelper.addStoryBoard(AllBlocks.MECHANICAL_ARM, "shared/logistics/arm_chain_conveyor", ChainConveyorScenes::mechanicalArmChainConveyor, AllCreatePonderTags.HIGH_LOGISTICS)
				.orderAfter(Create.ID, "mechanical_arm/redstone");
			itemProviderHelper.addStoryBoard(AllBlocks.CHAIN_CONVEYOR, "shared/logistics/arm_chain_conveyor", ChainConveyorScenes::mechanicalArmChainConveyor)
				.orderAfter(Create.ID, "high_logistics/chain_conveyor");
			if (SharedFeatureFlag.DRYING_RACK.enabled()) itemProviderHelper.addStoryBoard(AllBlocks.CHAIN_CONVEYOR, "shared/processing/drying/chain_conveyor", ChainConveyorScenes::drying)
				.orderAfter("shared/processing/drying/rack");
		};

		if (SharedFeatureFlag.BASIN_LID.enabled()) itemProviderHelper.forComponents(SharedCreateBlocks.BASIN_LID)
			.addStoryBoard("shared/processing/basin_lid", BasinLidScenes::basinLid);

		if (SharedFeatureFlag.BLENDER.enabled()) {
			itemProviderHelper.addStoryBoard(SharedCreateBlocks.BLENDER, "shared/processing/blender/blending", BlenderScenes::blending);
			if (SharedFeatureFlag.BLOOD.enabled()) itemProviderHelper.addStoryBoard(SharedCreateBlocks.BLENDER, "shared/processing/blender/blood", BlenderScenes::blood);
		};

		if (SharedFeatureFlag.CENTRIFUGE.enabled()) itemProviderHelper.forComponents(SharedCreateBlocks.CENTRIFUGE)
			.addStoryBoard("shared/processing/centrifuge", CentrifugeScenes::centrifuge);

		itemProviderHelper.forComponents(AllBlocks.CRUSHING_WHEEL)
			.addStoryBoard("shared/processing/crushing_wheel_filtering", CrushingWheelScenes::filtering);
			
		if (SharedFeatureFlag.EXTRUSION.enabled()) itemProviderHelper.forComponents(SharedCreateBlocks.EXTRUSION_DIE)
			.addStoryBoard("shared/processing/extrusion", ExtrusionScenes::extrusionDie, AllCreatePonderTags.CONTRAPTION_ACTOR);

		if (SharedFeatureFlag.HORSE_MILL.enabled()) {
			itemProviderHelper.forComponents(SharedCreateBlocks.HORSE_MILL_BEARING, SharedCreateBlocks.HARNESS)
				.addStoryBoard("shared/kinetics/horse_mill", HorseMillScenes::horseMill);
		};
	
		if (SharedFeatureFlag.MESH_BASIN.enabled()) itemProviderHelper.forComponents(SharedCreateBlocks.MESH_BASIN)
			.addStoryBoard("shared/processing/mesh_basin/juicing", MeshBasinScenes::juicing)
			.addStoryBoard("shared/processing/mesh_basin/boiling", MeshBasinScenes::boiling);

		if (SharedFeatureFlag.REDSTONE_PROGRAMMER.enabled()) itemProviderHelper.forComponents(SharedCreateBlocks.REDSTONE_PROGRAMMER)
			.addStoryBoard("shared/redstone/programmer", RedstoneProgrammerPonderScenes::redstoneProgrammer, AllCreatePonderTags.REDSTONE);
	}
};
