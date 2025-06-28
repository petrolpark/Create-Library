package com.petrolpark.core.data.loot.numberprovider.itemstack;

import com.mojang.serialization.Codec;
import com.petrolpark.PetrolparkRegistries;
import com.petrolpark.core.data.loot.numberprovider.NumberEstimate;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootContextUser;

public interface ItemStackNumberProvider extends LootContextUser {

    /**
     * Use {@link ItemStackNumberProvider#CODEC} instead.
     */
    static final Codec<ItemStackNumberProvider> TYPED_CODEC = PetrolparkRegistries.LOOT_ITEM_STACK_NUMBER_PROVIDER_TYPES
        .byNameCodec()
        .dispatch(ItemStackNumberProvider::getType, LootItemStackNumberProviderType::codec);

    public static final Codec<ItemStackNumberProvider> CODEC = Codec.lazyInitialized(() -> Codec.withAlternative(TYPED_CODEC, Codec.unit(CountItemStackNumberProvider::new)));
    
    public float getFloat(ItemStack stack, LootContext lootContext);

    public default float getMaxFloat(ItemStack stack, LootContext lootContext) {
        return getFloat(stack, lootContext);
    };

    public NumberEstimate getEstimate();

    public LootItemStackNumberProviderType getType();
};
