package petrolpark.mc.library.registry;

import static petrolpark.mc.library.Petrolpark.REGISTRATE;

import petrolpark.mc.library.core.data.recipe.ingredient.AdvancedFluidIngredient;
import petrolpark.mc.library.core.data.recipe.ingredient.AdvancedItemIngredient;
import petrolpark.mc.library.core.data.recipe.ingredient.BlockHolderSetIngredient;
import com.tterrag.registrate.util.entry.RegistryEntry;

import net.neoforged.neoforge.common.crafting.IngredientType;
import net.neoforged.neoforge.fluids.crafting.FluidIngredientType;

public class PetrolparkNeoForgeIngredientTypes {

    public static final RegistryEntry<IngredientType<?>, IngredientType<AdvancedItemIngredient>> ADVANCED = REGISTRATE.ingredientType("advanced", AdvancedItemIngredient.CODEC, AdvancedItemIngredient.STREAM_CODEC);
    public static final RegistryEntry<IngredientType<?>, IngredientType<BlockHolderSetIngredient>> BLOCK_SET = REGISTRATE.ingredientType("block_set", BlockHolderSetIngredient.CODEC, BlockHolderSetIngredient.STREAM_CODEC);

    public static final RegistryEntry<FluidIngredientType<?>, FluidIngredientType<AdvancedFluidIngredient>> FLUID_ADVANCED = REGISTRATE.fluidIngredientType("advanced", AdvancedFluidIngredient.CODEC, AdvancedFluidIngredient.STREAM_CODEC);
    
    public static final void register() {};
};
