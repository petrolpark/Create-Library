package com.petrolpark.compat.jei.category;

import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public interface ITickableCategory {
    
    public static final List<ITickableCategory> TICKING_CATEGORIES = new ArrayList<>();

    public static class ClientEvents {
        @SubscribeEvent
        public static void tickAll(ClientTickEvent event) {
            if (event.phase != ClientTickEvent.Phase.START) return;
            TICKING_CATEGORIES.forEach(ITickableCategory::tick);
        };
    };

    void tick();
};
