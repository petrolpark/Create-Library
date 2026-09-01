package petrolpark.mc.library.compat.create.core.data.recipe;

import com.mojang.serialization.Decoder;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

/**
 * Publicly readable version of {@link ProcessingRecipeParams}
 */
public class RecordProcessingRecipeParams extends ProcessingRecipeParams {

    public static final Decoder<RecordProcessingRecipeParams> DECODER = ProcessingRecipeParams.codec(RecordProcessingRecipeParams::new).decoder();
    
    public RecordProcessingRecipeParams() {
        super();
    };

    public NonNullList<Ingredient> itemIngredients() {
        return ingredients;
    };

	public NonNullList<ProcessingOutput> itemResults() {
        return results;
    };

	public NonNullList<SizedFluidIngredient> fluidIngredients() {
        return fluidIngredients;
    };

	public NonNullList<FluidStack> fluidResults() {
        return fluidResults;
    };

	public int processingTime() {
        return processingDuration;
    };

	public HeatCondition heatRequirement() {
        return requiredHeat;
    };

    public <B extends ProcessingRecipeBuilder<?, ?, B>> B addToBuilder(B builder) {
        itemIngredients().forEach(builder::require);
        fluidIngredients().forEach(builder::require);
        itemResults().forEach(builder::output);
        fluidResults().forEach(builder::output);
        return builder
            .duration(processingTime())
            .requiresHeat(heatRequirement());
    };

};
