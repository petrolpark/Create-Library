package petrolpark.mc.library.core.world.item.restaurant.offer.order;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

public record RestaurantOrderModifierEntry(RestaurantOrderModifier orderModifier, NumberProvider chance, boolean hidden) {

    public static final Codec<RestaurantOrderModifierEntry> CODEC = Codec.lazyInitialized(() -> RecordCodecBuilder.create(instance -> 
        instance.group(
            RestaurantOrderModifier.CODEC.fieldOf("modifier").forGetter(RestaurantOrderModifierEntry::orderModifier),
            NumberProviders.CODEC.optionalFieldOf("chance", ConstantValue.exactly(1f)).forGetter(RestaurantOrderModifierEntry::chance),
            Codec.BOOL.optionalFieldOf("hidden", false).forGetter(RestaurantOrderModifierEntry::hidden)
        ).apply(instance, RestaurantOrderModifierEntry::new)
    ));
};
