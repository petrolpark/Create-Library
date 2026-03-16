package com.petrolpark.core.item.wooden;

import javax.annotation.Nonnull;

import com.petrolpark.PetrolparkDataComponentTypes;
import com.petrolpark.util.WoodHelper;
import com.petrolpark.util.WoodHelper.Wood;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class WoodenItem extends Item {

    public WoodenItem(Item.Properties properties) {
        super(properties.component(PetrolparkDataComponentTypes.WOOD, WoodHelper.OAK));
    };

    public ItemStack of(Wood wood) {
        final ItemStack stack = new ItemStack(this);
        stack.set(PetrolparkDataComponentTypes.WOOD, wood);
        return stack;
    };

    @Override
    public Component getName(@Nonnull ItemStack stack) {
        return Component.translatable(getDescriptionId(stack), WoodHelper.getName(stack.get(PetrolparkDataComponentTypes.WOOD)));
    };
    
};
