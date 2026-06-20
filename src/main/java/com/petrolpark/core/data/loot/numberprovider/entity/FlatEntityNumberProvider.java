package com.petrolpark.core.data.loot.numberprovider.entity;

import javax.annotation.ParametersAreNonnullByDefault;

import com.mojang.serialization.MapCodec;
import com.petrolpark.core.data.loot.numberprovider.NumberEstimate;
import com.petrolpark.registry.PetrolparkNumberProviderTypes;
import com.petrolpark.util.codec.CodecHelper;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

/**
 * <p>{@code petrolpark:flat}</p>
 * 
 * Get a simple {@link NumberProvider} value, without reference to the Entity.
 * 
 * Arguments:
 * <ul>
 * <li> {@code value} - A {@link NumberProvider}
 * </ul>
 * 
 * Note that this has inline serialization. You can just refer to a {@link NumberProvider} directly when an {@link EntityNumberProvider} is expected and it will be converted.
 * 
 * @author petrolpark
 */
@ParametersAreNonnullByDefault
public record FlatEntityNumberProvider(NumberProvider numberProvider) implements EntityNumberProvider {

    public static final MapCodec<FlatEntityNumberProvider> CODEC = CodecHelper.singleFieldMap(NumberProviders.CODEC, "provider", FlatEntityNumberProvider::numberProvider, FlatEntityNumberProvider::new);

    @Override
    public float getFloat(Entity entity, LootContext lootContext) {
        return numberProvider().getFloat(lootContext);
    };

    @Override
    public NumberEstimate getEstimate() {
        return NumberEstimate.get(numberProvider());
    };

    @Override
    public LootEntityNumberProviderType getEntityNumberProviderType() {
        return PetrolparkNumberProviderTypes.FLAT_ENTITY.get();
    };
    
};
