package com.petrolpark.core.data.loot;

import java.util.function.Consumer;

import com.petrolpark.core.recipe.ingredient.modifier.IIngredientModifier;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;

public class LootForcing {
  
    /**
     * 
     * @param requirement
     * @param lootTable
     * @return
     */
    public static boolean forceLoot(IIngredientModifier<? super ItemStack> requirement, LootTable lootTable, LootContext context, Consumer<ItemStack> stackConsumer) {
        return false;
        //TODO
    };
};
