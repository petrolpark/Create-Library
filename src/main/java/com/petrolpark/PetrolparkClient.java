package com.petrolpark;

import com.petrolpark.item.decay.DecayingItemHandler.ClientDecayingItemHandler;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

public class PetrolparkClient {

    public static void clientCtor(IEventBus modEventBus, IEventBus neoEventBus) {
        neoEventBus.addListener(PetrolparkClient::clientInit);
    };
    
    public static void clientInit(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> { // Work which must be done on main thread
            Petrolpark.DECAYING_ITEM_HANDLER.set(new ClientDecayingItemHandler());
        });
    };
};
