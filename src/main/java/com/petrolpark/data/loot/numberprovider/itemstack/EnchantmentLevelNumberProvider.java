package com.petrolpark.data.loot.numberprovider.itemstack;

import com.mojang.serialization.MapCodec;
import com.petrolpark.PetrolparkNumberProviderTypes;
import com.petrolpark.util.NetworkHelper;

import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.LootContext;

public record EnchantmentLevelNumberProvider(Holder<Enchantment> enchantment) implements ItemStackNumberProvider {

    public static final MapCodec<EnchantmentLevelNumberProvider> CODEC = NetworkHelper.singleFieldMapCodec(Enchantment.CODEC, "enchantment", EnchantmentLevelNumberProvider::enchantment, EnchantmentLevelNumberProvider::new);

    @Override
    public float getFloat(ItemStack stack, LootContext lootContext) {
        return stack.getEnchantmentLevel(enchantment);
    };

    @Override
    public LootItemStackNumberProviderType getType() {
        return PetrolparkNumberProviderTypes.ENCHANTMENT_LEVEL.get();
    };
    
};
