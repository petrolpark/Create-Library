package com.petrolpark.core.data.loot.numberprovider.itemstack;

import org.jetbrains.annotations.ApiStatus;

import com.mojang.serialization.Codec;
import com.petrolpark.core.data.loot.numberprovider.ContextToolNumberProvider;
import com.petrolpark.core.data.loot.numberprovider.NumberEstimate;
import com.petrolpark.core.data.loot.numberprovider.entity.EntityNumberProvider;
import com.petrolpark.core.data.loot.numberprovider.team.TeamNumberProvider;
import com.petrolpark.registry.PetrolparkRegistries;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootContextUser;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

/**
 * Item Stack-specific version of {@link NumberProvider}.
 * 
 * @see ContextToolNumberProvider Get a value from the tool Item Stack in the LootContext
 * @see EntityNumberProvider Entity equivalent
 * @see TeamNumberProvider Team equivalent
 */
public interface ItemStackNumberProvider extends LootContextUser {

    /**
     * Use {@link ItemStackNumberProvider#CODEC} instead.
     */
    @ApiStatus.Internal
    static final Codec<ItemStackNumberProvider> TYPED_CODEC = PetrolparkRegistries.LOOT_ITEM_STACK_NUMBER_PROVIDER_TYPES
        .byNameCodec()
        .dispatch(ItemStackNumberProvider::getItemStackNumberProviderType, LootItemStackNumberProviderType::codec);

    public static final Codec<ItemStackNumberProvider> CODEC = Codec.lazyInitialized(() -> Codec.withAlternative(TYPED_CODEC, NumberProviders.CODEC.xmap(FlatItemStackNumberProvider::new, FlatItemStackNumberProvider::numberProvider)));
    
    public float getFloat(ItemStack stack, LootContext lootContext);

    public default float getMaxFloat(ItemStack stack, LootContext lootContext) {
        return getFloat(stack, lootContext);
    };

    /**
     * Get the approximate bounds for the {@link ItemStackNumberProvider#getFloat(ItemStack, LootContext) output} of this {@link ItemStackNumberProvider} on a best-effort basis.
     * @see NumberEstimate#unknown() 
     */
    public NumberEstimate getEstimate();

    public LootItemStackNumberProviderType getItemStackNumberProviderType();
};
