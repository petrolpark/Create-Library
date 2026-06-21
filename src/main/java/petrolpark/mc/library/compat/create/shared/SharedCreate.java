package petrolpark.mc.library.compat.create.shared;

import petrolpark.mc.library.compat.create.shared.registry.SharedContraptionTypes;
import petrolpark.mc.library.compat.create.shared.registry.SharedCreateBlockEntityTypes;
import petrolpark.mc.library.compat.create.shared.registry.SharedCreateBlocks;
import petrolpark.mc.library.compat.create.shared.registry.SharedCreateCriterionTriggers;
import petrolpark.mc.library.compat.create.shared.registry.SharedCreateEntityTypes;
import petrolpark.mc.library.compat.create.shared.registry.SharedCreateFluids;
import petrolpark.mc.library.compat.create.shared.registry.SharedCreateItems;
import petrolpark.mc.library.compat.create.shared.registry.SharedCreateMenuTypes;
import petrolpark.mc.library.compat.create.shared.registry.SharedCreatePackets;

import net.neoforged.bus.api.IEventBus;

public class SharedCreate {
    
    public static final void ctor(IEventBus modEventBus, IEventBus mainEventBus) {
        SharedContraptionTypes.register();
        SharedCreateBlockEntityTypes.register();
        SharedCreateBlocks.register();
        SharedCreateCriterionTriggers.register();
        SharedCreateEntityTypes.register();
        SharedCreateFluids.register();
        SharedCreateItems.register();
        SharedCreateMenuTypes.register();
        SharedCreatePackets.register();
    };
};
