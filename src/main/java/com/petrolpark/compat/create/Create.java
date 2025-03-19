package com.petrolpark.compat.create;

import com.petrolpark.RequiresCreate;
import com.petrolpark.compat.create.block.entity.behaviour.AbstractRememberPlacerBehaviour;
import com.petrolpark.compat.create.loot.CreateGlobalLootModifierSerializers;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.RegisterEvent;

@RequiresCreate
public class Create {
  
    public static void ctor(IEventBus modEventBus, IEventBus forgeEventBus) {

        // Registrations
        CreatePackets.register();
        CreateBlockEntityTypes.register();
        CreateBlocks.register();
        CreateGlobalLootModifierSerializers.register();

        // Event Bus Subscribers
        modEventBus.addListener(Create::onRegister);
        forgeEventBus.register(AbstractRememberPlacerBehaviour.class);
    };

    private static final void onRegister(final RegisterEvent event) {
        PetrolparkItemAttributeTypes.init();
    };
};
