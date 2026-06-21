package com.petrolpark.compat.create.shared;

import com.petrolpark.compat.create.shared.registry.SharedContraptionTypes;
import com.petrolpark.compat.create.shared.registry.SharedCreateBlockEntityTypes;
import com.petrolpark.compat.create.shared.registry.SharedCreateBlocks;
import com.petrolpark.compat.create.shared.registry.SharedCreateCriterionTriggers;
import com.petrolpark.compat.create.shared.registry.SharedCreateEntityTypes;
import com.petrolpark.compat.create.shared.registry.SharedCreateFluids;
import com.petrolpark.compat.create.shared.registry.SharedCreateItems;
import com.petrolpark.compat.create.shared.registry.SharedCreateMenuTypes;
import com.petrolpark.compat.create.shared.registry.SharedCreatePackets;

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
