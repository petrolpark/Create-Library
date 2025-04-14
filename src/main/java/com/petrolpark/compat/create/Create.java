package com.petrolpark.compat.create;

import com.petrolpark.RequiresCreate;
import com.petrolpark.compat.create.core.block.entity.behaviour.AbstractRememberPlacerBehaviour;
import com.petrolpark.compat.create.core.loot.CreateGlobalLootModifierSerializers;
import com.petrolpark.config.PetrolparkStressConfig;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.RegisterEvent;

@RequiresCreate
public class Create {

    static {
        
    };
  
    public static void ctor(IEventBus modEventBus, IEventBus mainEventBus) {

        // Registrations
        CreatePackets.register();
        CreateBlockEntityTypes.register();
        CreateBlocks.register();
        CreateGlobalLootModifierSerializers.register();

        PetrolparkMovementChecks.register();
        PetrolparkMandrelAnimationTypes.register();

        // Event Bus Subscribers
        modEventBus.addListener(Create::onRegister);
        mainEventBus.register(PetrolparkStressConfig.class);
        mainEventBus.register(AbstractRememberPlacerBehaviour.class);
    };

    private static final void onRegister(final RegisterEvent event) {
        PetrolparkItemAttributeTypes.init();
    };
};
