package com.petrolpark;

import static com.petrolpark.Petrolpark.REGISTRATE;

import com.petrolpark.core.recipe.ingredient.modifier.CompoundIngredientModifier;
import com.petrolpark.core.recipe.ingredient.modifier.ContaminatedIngredientModifier;
import com.petrolpark.core.recipe.ingredient.modifier.EnchantmentItemIngredientModifier;
import com.petrolpark.core.recipe.ingredient.modifier.GenericIngredientModifierType;
import com.petrolpark.core.recipe.ingredient.modifier.IIngredientModifierType;
import com.petrolpark.core.recipe.ingredient.modifier.INamedIngredientModifierType;
import com.petrolpark.core.recipe.ingredient.modifier.ItemItemIngredientModifier;
import com.petrolpark.core.recipe.ingredient.modifier.NotIngredientModifier;
import com.petrolpark.core.recipe.ingredient.modifier.PassIngredientModifier;
import com.petrolpark.core.recipe.ingredient.modifier.TagItemIngredientModifier;
import com.tterrag.registrate.util.entry.RegistryEntry;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

public class PetrolparkIngredientModifierTypes {


    // Items
    public static final RegistryEntry<IIngredientModifierType<? super ItemStack>, ? extends IIngredientModifierType<? super ItemStack>>
    
    ITEM_PASS = REGISTRATE.itemIngredientModifierType("pass", PassIngredientModifier.TYPE),
    ITEM_CONTAMINATED = REGISTRATE.itemIngredientModifierType("contaminated", ContaminatedIngredientModifier.TYPE);
    
    public static final RegistryEntry<IIngredientModifierType<? super ItemStack>, INamedIngredientModifierType<ItemStack>> 
    
    ITEM = REGISTRATE.itemIngredientModifierType("item", ItemItemIngredientModifier.Type::new),
    ITEM_TAG = REGISTRATE.itemIngredientModifierType("tag", TagItemIngredientModifier.Type::new),
    ITEM_ENCHANTMENTS = REGISTRATE.itemIngredientModifierType("enchantments", EnchantmentItemIngredientModifier.Type::new);
    
    public static final RegistryEntry<IIngredientModifierType<? super ItemStack>, GenericIngredientModifierType<ItemStack, NotIngredientModifier<ItemStack>>> ITEM_NOT = REGISTRATE.itemIngredientModifierType("not", NotIngredientModifier::codec, NotIngredientModifier::streamCodec);
    public static final RegistryEntry<IIngredientModifierType<? super ItemStack>, GenericIngredientModifierType<ItemStack, CompoundIngredientModifier<ItemStack>>> ITEM_COMPOUND = REGISTRATE.itemIngredientModifierType("compound", CompoundIngredientModifier::codec, CompoundIngredientModifier::streamCodec);

    // Fluids
    public static final RegistryEntry<IIngredientModifierType<? super FluidStack>, ? extends IIngredientModifierType<? super FluidStack>>

    FLUID_PASS = REGISTRATE.fluidIngredientModifierType("pass", PassIngredientModifier.TYPE),
    FLUID_NOT = REGISTRATE.fluidIngredientModifierType("not", NotIngredientModifier::codec, NotIngredientModifier::streamCodec),
    FLUID_CONTAMINATED = REGISTRATE.fluidIngredientModifierType("contaminated", ContaminatedIngredientModifier.TYPE);
    
    public static final void register() {};
};
