package com.petrolpark.data.loot.numberprovider.itemstack;

import com.mojang.serialization.Codec;
import com.petrolpark.PetrolparkRegistries;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootContextUser;

public interface ItemStackNumberProvider extends LootContextUser {

    public static final Codec<ItemStackNumberProvider> CODEC = Codec.lazyInitialized(
        () -> Codec.withAlternative(TypedCodec.TYPED_CODEC, Codec.unit(CountItemStackNumberProvider::new))
    );
    
    public float getFloat(ItemStack stack, LootContext lootContext);

    public LootItemStackNumberProviderType getType();

    static class TypedCodec {
        private static final Codec<ItemStackNumberProvider> TYPED_CODEC = PetrolparkRegistries.LOOT_ITEM_STACK_NUMBER_PROVIDER_TYPES
            .byNameCodec()
            .dispatch(ItemStackNumberProvider::getType, LootItemStackNumberProviderType::codec);
    };
};
