package com.petrolpark.data.loot.numberprovider.itemstack;

import com.mojang.serialization.MapCodec;
import com.petrolpark.PetrolparkNumberProviderTypes;
import com.petrolpark.util.CodecHelper;

import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.LootContext;

public record EnchantmentLevelItemStackNumberProvider(Holder<Enchantment> enchantment) implements ItemStackNumberProvider {

    public static final MapCodec<EnchantmentLevelItemStackNumberProvider> CODEC = CodecHelper.singleFieldMap(Enchantment.CODEC, "enchantment", EnchantmentLevelItemStackNumberProvider::enchantment, EnchantmentLevelItemStackNumberProvider::new);

    @Override
    public float getFloat(ItemStack stack, LootContext lootContext) {
        return stack.getEnchantmentLevel(enchantment);
    };

    @Override
    public LootItemStackNumberProviderType getType() {
        return PetrolparkNumberProviderTypes.ENCHANTMENT_LEVEL.get();
    };
    
};
