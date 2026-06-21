package petrolpark.mc.library.compat.create.registry;

import static petrolpark.mc.library.compat.create.PetrolparkCreate.REGISTRATE;

import petrolpark.mc.library.compat.create.core.world.dough.DoughData;
import petrolpark.mc.library.compat.create.core.world.dough.ingredient.DoughItemAdvancedIngredient;
import petrolpark.mc.library.compat.create.core.world.dough.ingredient.DoughTypeIngredient;
import petrolpark.mc.library.compat.create.core.world.dough.ingredient.ThicknessDoughIngredient;
import petrolpark.mc.library.compat.create.core.world.dough.ingredient.ToppingDoughIngredient;
import petrolpark.mc.library.core.data.recipe.ingredient.advanced.CompoundAdvancedIngredient;
import petrolpark.mc.library.core.data.recipe.ingredient.advanced.IAdvancedIngredientType;
import petrolpark.mc.library.core.data.recipe.ingredient.advanced.NamedAdvancedIngredientType;
import petrolpark.mc.library.core.data.recipe.ingredient.advanced.NotAdvancedIngredient;
import petrolpark.mc.library.core.data.recipe.ingredient.advanced.PassAdvancedIngredient;
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
