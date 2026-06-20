package com.petrolpark.compat.jei.subtypeInterpreter;

import javax.annotation.ParametersAreNonnullByDefault;

import org.jetbrains.annotations.Nullable;

import com.petrolpark.core.world.item.crafting.recipeBook.RecipeReferenceDataComponent;
import com.petrolpark.registry.PetrolparkDataComponentTypes;

import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

@ParametersAreNonnullByDefault
public class RecipeBookItemSubtypeInterpreter implements ISubtypeInterpreter<ItemStack> {

    public static final RecipeBookItemSubtypeInterpreter INSTANCE = new RecipeBookItemSubtypeInterpreter();

    @Override
    public @Nullable ResourceLocation getSubtypeData(ItemStack ingredient, UidContext context) {
        final RecipeReferenceDataComponent ref = ingredient.get(PetrolparkDataComponentTypes.RECIPE_REFERENCE);
        return ref == null ? null : ref.recipeId();
    };

    @Override
    public String getLegacyStringSubtypeInfo(ItemStack ingredient, UidContext context) {
        return "";
    };
    
};
