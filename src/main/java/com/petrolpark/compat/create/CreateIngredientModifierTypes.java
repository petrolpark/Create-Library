package com.petrolpark.compat.create;

import static com.petrolpark.Petrolpark.REGISTRATE;

import com.petrolpark.core.recipe.ingredient.modifier.CreateItemAttributeIngredientModifier;
import com.petrolpark.core.recipe.ingredient.modifier.IIngredientModifierType;
import com.petrolpark.core.recipe.ingredient.modifier.INamedIngredientModifierType;
import com.tterrag.registrate.util.entry.RegistryEntry;

import net.minecraft.world.item.ItemStack;

public class CreateIngredientModifierTypes {

    public static final RegistryEntry<IIngredientModifierType<? super ItemStack>, INamedIngredientModifierType<ItemStack>> 

    ITEM_ATTRIBUTE = REGISTRATE.itemIngredientModifierType("create_item_attribute", CreateItemAttributeIngredientModifier.Type::new);

    public static final void register() {};
};
