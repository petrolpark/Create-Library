package com.petrolpark;

import com.petrolpark.item.decay.DecayingItemHandler.ClientDecayingItemHandler;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class PetrolparkClient {

    public static void clientCtor(IEventBus modEventBus, IEventBus forgeEventBus) {
        modEventBus.addListener(PetrolparkClient::clientInit);
    };
    
    public static void clientInit(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> { // Work which must be done on main thread
            Petrolpark.DECAYING_ITEM_HANDLER.set(new ClientDecayingItemHandler());
        });
    };
};
