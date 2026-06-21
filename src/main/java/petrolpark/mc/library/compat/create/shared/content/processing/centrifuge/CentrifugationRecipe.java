package petrolpark.mc.library.compat.create.shared.content.processing.centrifuge;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.annotation.Nonnull;

import petrolpark.mc.library.compat.create.core.data.recipe.AdvancedProcessingRecipe;
import petrolpark.mc.library.compat.create.core.data.recipe.AdvancedProcessingRecipeParams;
import petrolpark.mc.library.compat.create.shared.registry.SharedCreateRecipeTypes;
import com.simibubi.create.api.data.recipe.ProcessingRecipeGen;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.NonNullList;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;

public class CentrifugationRecipe extends AdvancedProcessingRecipe<RecipeInput> implements ICentrifugationRecipe {

    public CentrifugationRecipe(AdvancedProcessingRecipeParams params) {
        super(SharedCreateRecipeTypes.CENTRIFUGATION, params);
    };

    @Override
    public boolean matches(@Nonnull RecipeInput input, @Nonnull Level level) {
        return false;
    };

    @Override
    protected boolean canSpecifyDuration() {
        return true;
    };

    @Override
    protected int getMaxInputCount() {
        return 64;
    };

    @Override
    protected int getMaxFluidInputCount() {
        return 1;
    };

    @Override
    protected int getMaxOutputCount() {
        return 4;
    };

    @Override
    protected int getMaxFluidOutputCount() {
        return 2;
    };

    @Override
    public NonNullList<ItemStack> getRemainingItems(@Nonnull RecipeInput input) {
        return super.getRemainingItems(input);
    };

    @Override
    public NonNullList<Ingredient> getCentrifugationIngredients() {
        return getIngredients();
    };

    @Override
    public List<ItemStack> rollLuckyResults(SmartBlockEntity blockEntity, RandomSource random) {
        return super.rollLuckyResults(blockEntity, random);
    };

    @Override
    public FluidStack getDenseOutputFluid() {
        return getFluidResults().size() >= 1 ? getFluidResults().get(0) : FluidStack.EMPTY;
    };

    @Override
    public FluidStack getLightOutputFluid() {
        return getFluidResults().size() >= 2 ? getFluidResults().get(1) : FluidStack.EMPTY;
    };

    public static final AdvancedProcessingRecipe.Builder<CentrifugationRecipe> builder(ResourceLocation id) {
        return new AdvancedProcessingRecipe.Builder<>(CentrifugationRecipe::new, id);
    };

    /**
     * The base class for Centrifugation recipe generation.
     * Addons should extend this and use the {@link ProcessingRecipeGen#create} methods to make recipes.
     */
    public static abstract class Gen extends AdvancedProcessingRecipe.Gen<CentrifugationRecipe> {
      
        public Gen(PackOutput output, CompletableFuture<Provider> registries, String defaultNamespace) {
            super(output, registries, defaultNamespace);
        };

        @Override
        protected IRecipeTypeInfo getRecipeType() {
            return SharedCreateRecipeTypes.CENTRIFUGATION;
        };

        @Override
        protected Builder<CentrifugationRecipe> getBuilder(ResourceLocation id) {
            return builder(id);
        };
    };
    
};
