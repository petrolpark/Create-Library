package com.petrolpark.core.data.loot.numberprovider.itemstack;

import com.petrolpark.PetrolparkNumberProviderTypes;
import com.petrolpark.core.data.loot.numberprovider.NumberEstimate;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;

/**
 * <p>{@code petrolpark:count}</p>
 * 
 * {@link ItemStack#getCount Get the count of the Item Stack.} No arguments.
 * 
 * @author petrolpark
 */
public class CountItemStackNumberProvider implements ItemStackNumberProvider {

    @Override
    public float getFloat(ItemStack stack, LootContext lootContext) {
        return stack.getCount();
    };

    @Override
    public NumberEstimate getEstimate() {
        return NumberEstimate.ranged(0f, 99f); // 99 is global max stack size
    };

    @Override
    public float getMaxFloat(ItemStack stack, LootContext lootContext) {
        return getFloat(stack, lootContext); 
    };

    @Override
    public LootItemStackNumberProviderType getItemStackNumberProviderType() {
        return PetrolparkNumberProviderTypes.COUNT.get();
    };
    
};
