package com.petrolpark.data.loot.numberprovider.itemstack;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.petrolpark.data.loot.PetrolparkLootItemStackNumberProviderTypes;

import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.LootContext;

public record EnchantmentLevelNumberProvider(Holder<Enchantment> enchantment) implements ItemStackNumberProvider {

    public static final MapCodec<EnchantmentLevelNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Enchantment.CODEC.fieldOf("enchantment").forGetter(EnchantmentLevelNumberProvider::enchantment)
    ).apply(instance, EnchantmentLevelNumberProvider::new));

    @Override
    public float getFloat(ItemStack stack, LootContext lootContext) {
        return stack.getEnchantmentLevel(enchantment);
    };

    @Override
    public LootItemStackNumberProviderType getType() {
        return PetrolparkLootItemStackNumberProviderTypes.ENCHANTMENT_LEVEL.get();
    };
    
};
