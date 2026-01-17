package com.petrolpark.core.item.decay;

import javax.annotation.Nonnull;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.petrolpark.PetrolparkDataComponents;
import com.petrolpark.core.item.decay.product.IDecayProduct;

import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

public interface IApplyDecayRecipe extends Recipe<SingleRecipeInput> {
    
    public Ingredient ingredient();

    public IDecayProduct decayProduct();

    public DecayTime decayTime();

    public static <R extends IApplyDecayRecipe> ItemStack withAppliedDecayRemoved(Level level, RecipeType<R> recipeType, ItemStack stack) {
        final ItemStack trueStack = ItemDecay.checkDecay(stack);
        level.getRecipeManager().getRecipesFor(recipeType, new SingleRecipeInput(trueStack), level).stream().findAny()
            .ifPresent(rh -> ItemDecay.removeAppliedDecay(trueStack));
        return checkDecay(level, recipeType, trueStack);
    };

    public static <R extends IApplyDecayRecipe> ItemStack withAppliedDecay(Level level, RecipeType<R> recipeType, ItemStack stack, boolean startDecay) {
        final SingleRecipeInput input = new SingleRecipeInput(stack);
        return checkDecay(level, recipeType, level.getRecipeManager().getRecipesFor(recipeType, input, level).stream().findAny()
            .map(RecipeHolder::value)
            .map(IApplyDecayRecipe::cast)
            .map(recipe -> recipe.assemble(input, startDecay))
            .orElse(stack));
    };

    public static <R extends IApplyDecayRecipe> ItemStack checkDecay(Level level, RecipeType<R> recipeType, ItemStack stack) {
        return ItemDecay.checkDecay(stack, s -> withAppliedDecay(level, recipeType, s, false));
    };

    public static <R extends IApplyDecayRecipe> MapCodec<R> codec(Factory<R> factory) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(IApplyDecayRecipe::ingredient),
            IDecayProduct.CODEC.fieldOf("product").forGetter(IApplyDecayRecipe::decayProduct),
            Codec.withAlternative(DecayTime.CODEC, DecayTime.INLINE_CODEC).fieldOf("time").forGetter(IApplyDecayRecipe::decayTime)
        ).apply(instance, factory::create));
    };

    public static <R extends IApplyDecayRecipe> StreamCodec<RegistryFriendlyByteBuf, R> streamCodec(Factory<R> factory) {
        return StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC, IApplyDecayRecipe::ingredient,
            IDecayProduct.STREAM_CODEC, IApplyDecayRecipe::decayProduct,
            DecayTime.STREAM_CODEC, IApplyDecayRecipe::decayTime,
            factory::create
        );
    };

    /**
     * Must be passed the {@link ItemDecay#checkDecay(ItemStack) true} ItemStack.
     */
    @Override
    public default boolean matches(@Nonnull SingleRecipeInput input, @Nonnull Level level) {
        IDecayProduct product = input.item().get(PetrolparkDataComponents.DECAY_PRODUCT);
        return ingredient().test(input.item()) && (product == null || product.equals(decayProduct())); // Applicable to Items which aren't currently decaying (to check when putting in the Barrel) or which have this recipe's decay (to check when taking out of the Barrel)
    };

    public default ItemStack setDecayProductAndTime(ItemStack stack) {
        stack.set(PetrolparkDataComponents.DECAY_PRODUCT, decayProduct());
        stack.set(PetrolparkDataComponents.DECAY_TIME, decayTime());
        return stack;
    };

    @Override
    public default ItemStack assemble(@Nonnull SingleRecipeInput input, @Nonnull HolderLookup.Provider registries) {
        return assemble(input, true);
    };

    public default ItemStack assemble(@Nonnull SingleRecipeInput input, boolean startDecay) {
        ItemStack result = input.item().copy();
        setDecayProductAndTime(result);
        if (startDecay) ItemDecay.startDecay(result);
        return result;
    };

    @Override
    public default boolean canCraftInDimensions(int width, int height) {
        return false;
    };

    @Override
    public default ItemStack getResultItem(@Nonnull HolderLookup.Provider registries) {
        return ItemStack.EMPTY; // Depends on input
    };

    public static IApplyDecayRecipe cast(Recipe<SingleRecipeInput> recipe) {
        if (recipe instanceof IApplyDecayRecipe ageingRecipe) return ageingRecipe;
        return null;
    };

    @FunctionalInterface
    public interface Factory<R extends IApplyDecayRecipe> {

        public R create(Ingredient ingredient, IDecayProduct decayProduct, DecayTime decayTime);
    };
};
