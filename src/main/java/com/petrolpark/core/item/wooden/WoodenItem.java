package com.petrolpark.core.item.wooden;

import javax.annotation.Nonnull;

import com.petrolpark.PetrolparkDataComponents;
import com.petrolpark.util.WoodHelper;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class WoodenItem extends Item {

    public WoodenItem(Item.Properties properties) {
        super(properties.component(PetrolparkDataComponents.WOOD, WoodHelper.OAK));
    };

    @Override
    public Component getName(@Nonnull ItemStack stack) {
        return Component.translatable(getDescriptionId(stack), WoodHelper.getName(stack.get(PetrolparkDataComponents.WOOD)));
    };
    
};
