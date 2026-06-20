package com.petrolpark;

import static com.petrolpark.Petrolpark.REGISTRATE;

import com.petrolpark.core.recipe.ingredient.advanced.CompoundAdvancedIngredient;
import com.petrolpark.core.recipe.ingredient.advanced.EnchantmentItemAdvancedIngredient;
import com.petrolpark.core.recipe.ingredient.advanced.FlaggedAdvancedIngredient;
import com.petrolpark.core.recipe.ingredient.advanced.GenericAdvancedIngredientType;
import com.petrolpark.core.recipe.ingredient.advanced.HolderSetFluidAdvancedIngredient;
import com.petrolpark.core.recipe.ingredient.advanced.HolderSetItemAdvancedIngredient;
import com.petrolpark.core.recipe.ingredient.advanced.IAdvancedIngredientType;
import com.petrolpark.core.recipe.ingredient.advanced.INamedAdvancedIngredientType;
import com.petrolpark.core.recipe.ingredient.advanced.ItemIDRegExAdvancedIngredient;
import com.petrolpark.core.recipe.ingredient.advanced.ItemItemAdvancedIngredient;
import com.petrolpark.core.recipe.ingredient.advanced.NotAdvancedIngredient;
import com.petrolpark.core.recipe.ingredient.advanced.PassAdvancedIngredient;
import com.petrolpark.core.recipe.ingredient.advanced.TagItemAdvancedIngredient;
import com.tterrag.registrate.util.entry.RegistryEntry;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

public class PetrolparkAdvancedIngredientTypes {


    // Items
    public static final RegistryEntry<IAdvancedIngredientType<? super ItemStack>, ? extends IAdvancedIngredientType<? super ItemStack>>
    
    ITEM_PASS = REGISTRATE.itemAdvancedIngredientType("pass", PassAdvancedIngredient.TYPE),
    ITEM_FLAGGED = REGISTRATE.itemAdvancedIngredientType("flagged", FlaggedAdvancedIngredient.TYPE);
    
    public static final RegistryEntry<IAdvancedIngredientType<? super ItemStack>, ? extends INamedAdvancedIngredientType<ItemStack>> 
    
    ITEM = REGISTRATE.itemAdvancedIngredientType("item", ItemItemAdvancedIngredient.Type::new),
    ITEM_ID_REGEX = REGISTRATE.itemAdvancedIngredientType("id_regex", ItemIDRegExAdvancedIngredient.CODEC, ItemIDRegExAdvancedIngredient.STREAM_CODEC),
    ITEM_HOLDER_SET = REGISTRATE.itemAdvancedIngredientType("set", HolderSetItemAdvancedIngredient.CODEC, HolderSetItemAdvancedIngredient.STREAM_CODEC),
    ITEM_TAG = REGISTRATE.itemAdvancedIngredientType("tag", TagItemAdvancedIngredient.Type::new),
    ITEM_ENCHANTMENTS = REGISTRATE.itemAdvancedIngredientType("enchantments", EnchantmentItemAdvancedIngredient.Type::new);
    
    public static final RegistryEntry<IAdvancedIngredientType<? super ItemStack>, GenericAdvancedIngredientType<ItemStack, NotAdvancedIngredient<ItemStack>>> ITEM_NOT = REGISTRATE.itemAdvancedIngredientType("not", NotAdvancedIngredient::codec, NotAdvancedIngredient::streamCodec);
    public static final RegistryEntry<IAdvancedIngredientType<? super ItemStack>, GenericAdvancedIngredientType<ItemStack, CompoundAdvancedIngredient<ItemStack>>> ITEM_COMPOUND = REGISTRATE.itemAdvancedIngredientType("compound", CompoundAdvancedIngredient::codec, CompoundAdvancedIngredient::streamCodec);

    // Fluids
    public static final RegistryEntry<IAdvancedIngredientType<? super FluidStack>, ? extends IAdvancedIngredientType<? super FluidStack>>

    FLUID_PASS = REGISTRATE.fluidAdvancedIngredientType("pass", PassAdvancedIngredient.TYPE),
    FLUID_NOT = REGISTRATE.fluidAdvancedIngredientType("not", NotAdvancedIngredient::codec, NotAdvancedIngredient::streamCodec),
    FLUID_FLAGGED = REGISTRATE.fluidAdvancedIngredientType("flagged", FlaggedAdvancedIngredient.TYPE);

    public static final RegistryEntry<IAdvancedIngredientType<? super FluidStack>, ? extends INamedAdvancedIngredientType<FluidStack>> 

    FLUID_HOLDER_SET = REGISTRATE.fluidAdvancedIngredientType("set", HolderSetFluidAdvancedIngredient.CODEC, HolderSetFluidAdvancedIngredient.STREAM_CODEC);
    
    public static final void register() {};
};
