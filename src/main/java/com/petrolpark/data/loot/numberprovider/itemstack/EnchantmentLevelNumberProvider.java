package com.petrolpark.data.loot.numberprovider.itemstack;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import com.petrolpark.data.loot.PetrolparkLootItemStackNumberProviderTypes;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraftforge.registries.ForgeRegistries;

public class EnchantmentLevelNumberProvider implements ItemStackNumberProvider {

    public final Enchantment enchantment;

    public EnchantmentLevelNumberProvider(Enchantment enchantment) {
        this.enchantment = enchantment;
    };

    @Override
    public float getFloat(ItemStack stack, LootContext lootContext) {
        return stack.getEnchantmentLevel(enchantment);
    };

    @Override
    public LootItemStackNumberProviderType getType() {
        return PetrolparkLootItemStackNumberProviderTypes.ENCHANTMENT_LEVEL.get();
    };

    public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<EnchantmentLevelNumberProvider> {

        @Override
        public void serialize(JsonObject json, EnchantmentLevelNumberProvider value, JsonSerializationContext serializationContext) {
            json.addProperty("enchantment", ForgeRegistries.ENCHANTMENTS.getKey(value.enchantment).toString());
        };

        @Override
        public EnchantmentLevelNumberProvider deserialize(JsonObject json, JsonDeserializationContext serializationContext) {
            ResourceLocation resourceLocation = ResourceLocation.fromNamespaceAndPath(GsonHelper.getAsString(json, "enchantment"));
            return new EnchantmentLevelNumberProvider(ForgeRegistries.ENCHANTMENTS.getDelegate(resourceLocation).orElseThrow(() -> new JsonSyntaxException("Unknown enchantment '" + resourceLocation + "'")).get());
        };

    };
    
};
