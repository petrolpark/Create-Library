package com.petrolpark.compat.create;

import com.petrolpark.client.outline.Outliner;
import com.petrolpark.compat.create.core.tube.ClientTubePlacementHandler;
import com.petrolpark.compat.create.event.CreateClientEvents;
import com.petrolpark.compat.create.event.CreateClientModEvents;

import net.minecraftforge.eventbus.api.IEventBus;

public class CreateClient {

    public static final Outliner OUTLINER = new Outliner();
    
    public static void clientCtor(IEventBus modEventBus, IEventBus forgeEventBus) {

        // Event Bus Subscribers
        modEventBus.register(CreateClientModEvents.class);
        forgeEventBus.register(CreateClientEvents.class);
        forgeEventBus.register(ClientTubePlacementHandler.class);
    };
};
