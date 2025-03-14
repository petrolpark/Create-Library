package com.petrolpark.data.loot.numberprovider.itemstack;

import com.petrolpark.PetrolparkNumberProviderTypes;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;

public class CountItemStackNumberProvider implements ItemStackNumberProvider {

    @Override
    public float getFloat(ItemStack stack, LootContext lootContext) {
        return stack.getCount();
    };

    @Override
    public LootItemStackNumberProviderType getType() {
        return PetrolparkNumberProviderTypes.COUNT.get();
    };
    
};
