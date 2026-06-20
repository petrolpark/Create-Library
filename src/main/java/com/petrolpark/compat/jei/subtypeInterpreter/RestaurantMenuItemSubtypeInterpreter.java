package com.petrolpark.compat.jei.subtypeInterpreter;

import javax.annotation.ParametersAreNonnullByDefault;

import org.jetbrains.annotations.Nullable;

import com.petrolpark.core.world.item.restaurant.Restaurant;
import com.petrolpark.registry.PetrolparkDataComponentTypes;

import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;

@ParametersAreNonnullByDefault
public class RestaurantMenuItemSubtypeInterpreter implements ISubtypeInterpreter<ItemStack> {

    public static final RestaurantMenuItemSubtypeInterpreter INSTANCE = new RestaurantMenuItemSubtypeInterpreter();

    @Override
    public @Nullable Holder<Restaurant> getSubtypeData(ItemStack ingredient, UidContext context) {
        return ingredient.get(PetrolparkDataComponentTypes.RESTAURANT);
    };

    @Override
    public String getLegacyStringSubtypeInfo(ItemStack ingredient, UidContext context) {
        return "";
    };
    
};
