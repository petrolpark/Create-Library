package com.petrolpark.compat.create;

import static com.petrolpark.Petrolpark.REGISTRATE;

import com.petrolpark.core.recipe.ingredient.advanced.CreateItemAttributeAdvancedIngredient;
import com.petrolpark.core.recipe.ingredient.advanced.IAdvancedIngredientType;
import com.petrolpark.core.recipe.ingredient.advanced.INamedAdvancedIngredientType;
import com.tterrag.registrate.util.entry.RegistryEntry;

import net.minecraft.world.item.ItemStack;

public class PetrolparkCreateAdvancedIngredientTypes {

    public static final RegistryEntry<IAdvancedIngredientType<? super ItemStack>, INamedAdvancedIngredientType<ItemStack>> 

    ITEM_ATTRIBUTE = REGISTRATE.itemAdvancedIngredientType("create_item_attribute", CreateItemAttributeAdvancedIngredient.Type::new);

    public static final void register() {};
};
