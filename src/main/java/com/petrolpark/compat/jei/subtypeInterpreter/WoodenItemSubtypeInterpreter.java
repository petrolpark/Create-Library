package com.petrolpark.compat.jei.subtypeInterpreter;

import javax.annotation.ParametersAreNonnullByDefault;

import org.jetbrains.annotations.Nullable;

import com.petrolpark.registry.PetrolparkDataComponentTypes;
import com.petrolpark.util.WoodHelper.Wood;

import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.world.item.ItemStack;

@ParametersAreNonnullByDefault
public class WoodenItemSubtypeInterpreter implements ISubtypeInterpreter<ItemStack> {

    public static final WoodenItemSubtypeInterpreter INSTANCE = new WoodenItemSubtypeInterpreter();

    @Override
    public @Nullable Wood getSubtypeData(ItemStack ingredient, UidContext context) {
        return ingredient.get(PetrolparkDataComponentTypes.WOOD);
    };

    @Override
    public String getLegacyStringSubtypeInfo(ItemStack ingredient, UidContext context) {
        return "";
    };
    
};
