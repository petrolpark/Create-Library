package com.petrolpark.data.loot.function;

import com.petrolpark.PetrolparkDataComponents;
import com.petrolpark.badge.BadgeItem;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;

public class BadgeAwardLootItemFunction implements LootItemFunction {

    @Override
    public ItemStack apply(ItemStack stack, LootContext context) {
        if (stack.getItem() instanceof BadgeItem) stack.set(PetrolparkDataComponents.BADGE_AWARD, null); //TODO
        return stack;
    };

    @Override
    public LootItemFunctionType<? extends LootItemFunction> getType() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getType'");
    };
    
};
