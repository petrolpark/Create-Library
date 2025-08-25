package com.petrolpark;

import org.jetbrains.annotations.ApiStatus;

import com.petrolpark.client.sprite.MeanShiftClusterSpriteSource;
import com.petrolpark.client.sprite.SmallBannerSpriteSource;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterSpriteSourceTypesEvent;

@ApiStatus.Experimental
@EventBusSubscriber(value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class PetrolparkSpriteSources {
    
    @SubscribeEvent
    public static final void onRegisterSpriteSourceTypes(RegisterSpriteSourceTypesEvent event) {
        event.register(Petrolpark.asResource("mean_shift_cluster"), MeanShiftClusterSpriteSource.TYPE);
        event.register(Petrolpark.asResource("small_banner"), SmallBannerSpriteSource.TYPE);
    };
};
