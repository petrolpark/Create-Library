package com.petrolpark.compat.create;

import com.petrolpark.client.outline.Outliner;
import com.petrolpark.compat.create.core.tube.ClientTubePlacementHandler;
import com.petrolpark.compat.create.event.CreateClientEvents;
import com.petrolpark.compat.create.event.CreateClientModEvents;

import net.createmod.ponder.foundation.PonderIndex;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

public class CreateClient {

    public static final Outliner OUTLINER = new Outliner();
    
    public static final void clientCtor(IEventBus modEventBus, IEventBus forgeEventBus) {

        // Event Bus Subscribers
        modEventBus.register(CreateClientModEvents.class);
        forgeEventBus.register(CreateClientEvents.class);
        forgeEventBus.register(ClientTubePlacementHandler.class);
        modEventBus.addListener(CreateClient::clientInit);

        PetrolparkPartialModels.register();
    };

    public static final void clientInit(final FMLClientSetupEvent event) {
        PonderIndex.addPlugin(new PetrolparkPonderPlugin());
    };
};
