package petrolpark.mc.library.compat.create.core.client.ponder;

import petrolpark.mc.library.compat.create.shared.registry.SharedCreateBlocks;
import petrolpark.mc.library.shared.SharedFeatureFlag;
import petrolpark.mc.library.shared.registry.SharedBlocks;
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
            .add(SharedCreateBlocks.BLENDER);

        if (SharedFeatureFlag.DRYING_RACK.enabled()) registrateHelper.addToTag(AllCreatePonderTags.ARM_TARGETS)
            .add(SharedBlocks.DRYING_RACK);
        
        if (SharedFeatureFlag.HORSE_MILL.enabled()) {
            registrateHelper.addToTag(AllCreatePonderTags.KINETIC_SOURCES)
                .add(SharedCreateBlocks.HORSE_MILL_BEARING)
                .add(SharedCreateBlocks.HARNESS);
            registrateHelper.addToTag(AllCreatePonderTags.MOVEMENT_ANCHOR)
                .add(SharedCreateBlocks.HORSE_MILL_BEARING);
            registrateHelper.addToTag(AllCreatePonderTags.CONTRAPTION_ACTOR)
                .add(SharedCreateBlocks.HARNESS);
        };

        if (SharedFeatureFlag.MESH_BASIN.enabled()) registrateHelper.addToTag(AllCreatePonderTags.ARM_TARGETS)
            .add(SharedCreateBlocks.MESH_BASIN);
    };
};
