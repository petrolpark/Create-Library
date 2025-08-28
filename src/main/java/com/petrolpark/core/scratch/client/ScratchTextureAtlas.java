package com.petrolpark.core.scratch.client;

import com.petrolpark.Petrolpark;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMaterialAtlasesEvent;

@EventBusSubscriber(value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class ScratchTextureAtlas {

    public static final ResourceLocation LOCATION = Petrolpark.asResource("scratch");

    //public static final ResourceLocation MSC_TEST_LOCATION = Petrolpark.asResource("msc_test");
    
    @SubscribeEvent
    public static final void registerAtlases(RegisterMaterialAtlasesEvent event) {
        event.register(LOCATION, LOCATION);
        //event.register(MSC_TEST_LOCATION, MSC_TEST_LOCATION);
    };
};
