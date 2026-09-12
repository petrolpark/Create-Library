package petrolpark.mc.library.shared.registry;

import com.mojang.serialization.Codec;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.datamaps.AdvancedDataMapType;
import net.neoforged.neoforge.registries.datamaps.DataMapValueRemover;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;
import petrolpark.mc.library.Petrolpark;
import petrolpark.mc.library.shared.SharedFeatureFlag;

@EventBusSubscriber
public class SharedDataMapTypes {
    
    public static final AdvancedDataMapType<EntityType<?>, Integer, DataMapValueRemover.Default<Integer, EntityType<?>>> RESTAURANT_EAT_BEHAVIOR_PRIORITY = AdvancedDataMapType
        .builder(
            Petrolpark.asResource("restaurant_eat_behavior_priority"),
            Registries.ENTITY_TYPE,
            Codec.INT
        ).remover(DataMapValueRemover.Default.codec())
        .merger((registry, first, firstValue, second, secondValue) -> Math.min(firstValue, secondValue))
        .build();

    @SubscribeEvent
    public static final void onRegisterDataMapTypes(RegisterDataMapTypesEvent event) {
        if (SharedFeatureFlag.RESTAURANT_SEATING.enabled())
            event.register(RESTAURANT_EAT_BEHAVIOR_PRIORITY);
    };
};
