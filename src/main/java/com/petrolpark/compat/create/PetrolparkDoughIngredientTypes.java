package com.petrolpark.compat.create;

import static com.petrolpark.compat.create.PetrolparkCreate.REGISTRATE;

import com.petrolpark.compat.create.core.dough.DoughData;
import com.petrolpark.compat.create.core.dough.ingredient.DoughItemAdvancedIngredient;
import com.petrolpark.compat.create.core.dough.ingredient.DoughTypeIngredient;
import com.petrolpark.compat.create.core.dough.ingredient.ThicknessDoughIngredient;
import com.petrolpark.compat.create.core.dough.ingredient.ToppingDoughIngredient;
import com.petrolpark.core.recipe.ingredient.advanced.CompoundAdvancedIngredient;
import com.petrolpark.core.recipe.ingredient.advanced.IAdvancedIngredientType;
import com.petrolpark.core.recipe.ingredient.advanced.NamedAdvancedIngredientType;
import com.petrolpark.core.recipe.ingredient.advanced.NotAdvancedIngredient;
import com.petrolpark.core.recipe.ingredient.advanced.PassAdvancedIngredient;
import com.tterrag.registrate.util.entry.RegistryEntry;

import net.minecraft.world.item.ItemStack;

public class PetrolparkDoughIngredientTypes {

    public static final RegistryEntry<IAdvancedIngredientType<? super ItemStack>, NamedAdvancedIngredientType<ItemStack>> ITEM = REGISTRATE.itemAdvancedIngredientType("dough", DoughItemAdvancedIngredient.CODEC, DoughItemAdvancedIngredient.STREAM_CODEC);

    public static final RegistryEntry<IAdvancedIngredientType<? super DoughData>, ? extends IAdvancedIngredientType<? super DoughData>>

    PASS = REGISTRATE.doughIngredientType("pass", PassAdvancedIngredient.TYPE),
    NOT = REGISTRATE.doughIngredientType("not", NotAdvancedIngredient::codec, NotAdvancedIngredient::streamCodec),
    COMPOUND = REGISTRATE.doughIngredientType("compound", CompoundAdvancedIngredient::codec, CompoundAdvancedIngredient::streamCodec);

    public static final RegistryEntry<IAdvancedIngredientType<? super DoughData>, NamedAdvancedIngredientType<DoughData>>

    THICKNESS = REGISTRATE.doughIngredientType("thickness", ThicknessDoughIngredient.CODEC, ThicknessDoughIngredient.STREAM_CODEC),
    TOPPING = REGISTRATE.doughIngredientType("topping", ToppingDoughIngredient.CODEC, ToppingDoughIngredient.STREAM_CODEC),
    TYPE = REGISTRATE.doughIngredientType("type", DoughTypeIngredient.CODEC, DoughTypeIngredient.STREAM_CODEC);

    public static final void register() {};
};
