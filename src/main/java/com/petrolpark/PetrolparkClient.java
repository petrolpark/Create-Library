package com.petrolpark;

import com.petrolpark.item.decay.DecayingItemHandler.ClientDecayingItemHandler;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = Petrolpark.MOD_ID, dist = Dist.CLIENT)
public class PetrolparkClient {

    public PetrolparkClient(IEventBus modEventBus) {
		clientCtor(modEventBus, NeoForge.EVENT_BUS);
	};

    public void clientCtor(IEventBus modEventBus, IEventBus neoEventBus) {
        neoEventBus.addListener(PetrolparkClient::clientInit);
    };
    
    public static void clientInit(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> { // Work which must be done on main thread
            Petrolpark.DECAYING_ITEM_HANDLER.set(new ClientDecayingItemHandler());
        });
    };
};
