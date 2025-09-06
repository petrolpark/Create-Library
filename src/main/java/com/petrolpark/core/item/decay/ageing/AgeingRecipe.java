package com.petrolpark.core.item.decay.ageing;

import javax.annotation.Nonnull;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.petrolpark.PetrolparkDataComponents;
import com.petrolpark.PetrolparkRecipeSerializers;
import com.petrolpark.PetrolparkRecipeTypes;
import com.petrolpark.core.item.decay.DecayTime;
import com.petrolpark.core.item.decay.ItemDecay;
import com.petrolpark.core.item.decay.product.IDecayProduct;

import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

public record AgeingRecipe(Ingredient ingredient, IDecayProduct product, DecayTime decayTime) implements Recipe<SingleRecipeInput> {

    public static final MapCodec<AgeingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(AgeingRecipe::ingredient),
        IDecayProduct.CODEC.fieldOf("product").forGetter(AgeingRecipe::product),
        Codec.withAlternative(DecayTime.CODEC, DecayTime.INLINE_CODEC).fieldOf("time").forGetter(AgeingRecipe::decayTime)
    ).apply(instance, AgeingRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, AgeingRecipe> STREAM_CODEC = StreamCodec.composite(
        Ingredient.CONTENTS_STREAM_CODEC, AgeingRecipe::ingredient,
        IDecayProduct.STREAM_CODEC, AgeingRecipe::product,
        DecayTime.STREAM_CODEC, AgeingRecipe::decayTime,
        AgeingRecipe::new
    );

    /**
     * Must be passed the {@link ItemDecay#checkDecay(ItemStack) true} ItemStack.
     */
    @Override
    public boolean matches(@Nonnull SingleRecipeInput input, @Nonnull Level level) {
        IDecayProduct product = input.item().get(PetrolparkDataComponents.DECAY_PRODUCT);
        return ingredient().test(input.item()) && (product == null || product.equals(product())); // Applicable to Items which aren't currently decaying (to check when putting in the Barrel) or which have this recipe's decay (to check when taking out of the Barrel)
    };

    public ItemStack setDecayProductAndTime(ItemStack stack) {
        stack.set(PetrolparkDataComponents.DECAY_PRODUCT, product);
        stack.set(PetrolparkDataComponents.DECAY_TIME, decayTime);
        return stack;
    };

    @Override
    public ItemStack assemble(@Nonnull SingleRecipeInput input, @Nonnull HolderLookup.Provider registries) {
        return assemble(input, true);
    };

    public ItemStack assemble(@Nonnull SingleRecipeInput input, boolean startDecay) {
        ItemStack result = input.item().copy();
        setDecayProductAndTime(result);
        if (startDecay) ItemDecay.startDecay(result);
        return result;
    };

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    };

    @Override
    public ItemStack getResultItem(@Nonnull HolderLookup.Provider registries) {
        return ItemStack.EMPTY; // Depends on input
    };

    @Override
    public RecipeSerializer<AgeingRecipe> getSerializer() {
        return PetrolparkRecipeSerializers.AGEING.get();
    };

    @Override
    public RecipeType<AgeingRecipe> getType() {
        return PetrolparkRecipeTypes.AGEING.get();
    };

    public static final AgeingRecipe cast(Recipe<SingleRecipeInput> recipe) {
        if (recipe instanceof AgeingRecipe ageingRecipe) return ageingRecipe;
        return null;
    };
};
