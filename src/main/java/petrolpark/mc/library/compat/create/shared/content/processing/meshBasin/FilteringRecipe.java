package petrolpark.mc.library.compat.create.shared.content.processing.meshBasin;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.api.data.recipe.ProcessingRecipeGen;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.data.PackOutput;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import petrolpark.mc.library.compat.create.core.data.recipe.AdvancedBasinRecipe;
import petrolpark.mc.library.compat.create.core.data.recipe.AdvancedProcessingRecipe;
import petrolpark.mc.library.compat.create.core.data.recipe.AdvancedProcessingRecipeParams;
import petrolpark.mc.library.compat.create.shared.registry.SharedCreateRecipeTypes;

public class FilteringRecipe extends AdvancedBasinRecipe {
    
    public static final MapCodec<FilteringRecipe> CODEC = ProcessingRecipe.codec(FilteringRecipe::create, AdvancedProcessingRecipeParams.UNADVANCED_CODEC);
    public static final StreamCodec<RegistryFriendlyByteBuf, FilteringRecipe> STREAM_CODEC = ProcessingRecipe.streamCodec(FilteringRecipe::create, AdvancedProcessingRecipeParams.UNADVANCED_STREAM_CODEC);

    public static final FilteringRecipe create(ProcessingRecipeParams params) {
        if (!(params instanceof AdvancedProcessingRecipeParams advancedParams)) throw new IllegalArgumentException("Not Advanced Processing Params");
        return new FilteringRecipe(advancedParams);
    };

    protected FilteringRecipe(AdvancedProcessingRecipeParams params) {
        super(SharedCreateRecipeTypes.FILTERING, params);
    };

    @Override
    public boolean isForMeshBasin() {
        return true;
    };

    @Override
    public int getMaxFluidInputCount() {
        return 1;
    };

    /**
     * Don't return the input Fluid here as that's not used in the Basin itself
     */
    @Override
    public NonNullList<SizedFluidIngredient> getFluidIngredients() {
        return NonNullList.create();
    };

    public SizedFluidIngredient getInputFluid() {
        return super.getFluidIngredients().get(0);
    };

    @Override
    public List<String> validate() {
        List<String> errors = super.validate();
        if (fluidIngredients.size() < 1) errors.add("Filtering Recipe has no fluid input");
        return errors;
    };

    public static final AdvancedProcessingRecipe.BasinBuilder<FilteringRecipe> builder(ResourceLocation id) {
        return new AdvancedProcessingRecipe.BasinBuilder<>(FilteringRecipe::create, id);
    };

    public static class Serializer implements RecipeSerializer<FilteringRecipe> {

        @Override
        public MapCodec<FilteringRecipe> codec() {
            return CODEC;
        };

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, FilteringRecipe> streamCodec() {
            return STREAM_CODEC;
        };

    };

    /**
     * The base class for Filtering recipe generation.
     * Addons should extend this and use the {@link ProcessingRecipeGen#create} methods to make recipes.
     */
    public static abstract class Gen extends AdvancedBasinRecipe.Gen<FilteringRecipe> {

        public Gen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, String defaultNamespace) {
            super(output, registries, defaultNamespace);
        };

        @Override
        protected IRecipeTypeInfo getRecipeType() {
            return SharedCreateRecipeTypes.FILTERING;
        };

        @Override
        protected AdvancedProcessingRecipe.BasinBuilder<FilteringRecipe> getBuilder(ResourceLocation id) {
            return builder(id);
        };

    };
};
