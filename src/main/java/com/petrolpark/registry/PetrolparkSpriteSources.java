package com.petrolpark.registry;

import org.jetbrains.annotations.ApiStatus;

import com.petrolpark.Petrolpark;
import com.petrolpark.core.client.spriteSource.MeanShiftClusterSpriteSource;
import com.petrolpark.core.client.spriteSource.SmallBannerSpriteSource;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterSpriteSourceTypesEvent;

@ApiStatus.Experimental
@EventBusSubscriber(Dist.CLIENT)
public class PetrolparkSpriteSources {
    
    @SubscribeEvent
    public static final void onRegisterSpriteSourceTypes(RegisterSpriteSourceTypesEvent event) {
        event.register(Petrolpark.asResource("mean_shift_cluster"), MeanShiftClusterSpriteSource.TYPE);
        event.register(Petrolpark.asResource("small_banner"), SmallBannerSpriteSource.TYPE);
    };
};
