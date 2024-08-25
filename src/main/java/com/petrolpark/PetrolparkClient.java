package com.petrolpark;

import com.petrolpark.itemdecay.DecayingItemHandler.ClientDecayingItemHandler;

import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class PetrolparkClient {
    
    public static void clientInit(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> { // Work which must be done on main thread
            Petrolpark.DECAYING_ITEM_HANDLER.set(new ClientDecayingItemHandler());
        });
    };
};
